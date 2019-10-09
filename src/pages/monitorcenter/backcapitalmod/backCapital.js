import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,TIMEFORMAT } from '../../../conf'
import { Modal,Pagination,message,Button} from 'antd'
import { toThousands,pageLimit,tableScroll } from '../../../utils'
import ModalRatio from './modal/modalRatio'
import ModalUpdate from './modal/modalUpdate'
import GoogleCode from '../../common/modal/googleCode'

export default class BackCapital extends React.Component{
    constructor(props){
        super(props)
        this.state = {
           tableList:[] ,
           visible:false,
           modalHtml:'',
           title:'',
           width:'',
           feeRatio:1,
           id:'',
           bcUserId:'',
           bcFrequency:'',
           feeRatio:'',
           baseBalance:'',
           luckyUserId:'',
           withdrawFrequency:'',
           withdrawAddress:'',
           webUrl:'',
           googleCode:'',
           limitBtn: [],
           check:'',
           googVisibal:false,
           item:'',
           type:'',
           height:0,
            tableScroll:{
                tableId:'BACPTAL',
                x_panelId:'BACPTALX',
                defaultHeight:500,
                height:0,
            }

        }
        this.changeStatus = this.changeStatus.bind(this)
        this.changeRatio = this.changeRatio.bind(this)
        this.modifyConfig = this.modifyConfig.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onChangeRatio = this.onChangeRatio.bind(this)
        this.modalisOkBtn = this.modalisOkBtn.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.inputChange = this.inputChange.bind(this)
        this.modalsaveModify = this.modalsaveModify.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.modalisOk = this.modalisOk.bind(this)
        this.onChangeStatus = this.onChangeStatus.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn:pageLimit('config', this.props.permissList)
        })
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`)
    }
    componentWillUnmount(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight){
        this.setState({
            xheight
        })
    }
     //输入时 input 设置到 satte
     inputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        }, () => this.clickKey(name, value));
    }
    //转出或者回购按钮
    onChangeStatus(status,type){
        let self = this
        let taskStatus = status ==0?1:0
        let url = type==0?'/backcapital/config/withdraw/task/switch':'/backcapital/config/task/switch'
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+url,qs.stringify({
                taskStatus
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    self.requestTable()
                    resolve(result.msg)
                }else{
                    message.warning(result.msg)
                }
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
        console.log('OK');
    }
    //转出或者回购弹窗
    changeStatus(status,type){
        let self = this
        let statusht = status==0?'启动':'停止'
        let typeName = type == 0?'转出':'回购'
        //let taskStatus = status ==0?1:0
        //let url = type==0?'/backcapital/config/withdraw/task/switch':'/backcapital/config/task/switch'
        Modal.confirm({
            title: `确定要`+statusht+typeName+'任务?',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(status,type) 
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //切换回购手续费比例选项弹框
    changeRatio(feeRatio,type){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.modalisOk(feeRatio,type)}>
                保存
            </Button>,
        ]
        console.log(feeRatio)
        this.setState({
            visible:true,
            title:"切换回购手续费比例",
            width:"500px",
            modalHtml:<ModalRatio feeRatio={feeRatio} onChangeRatio ={this.onChangeRatio}/>,
            feeRatio,
        })
    }
    //单选按钮
    onChangeRatio(e){
        console.log(e.target.value)
        this.setState({
            feeRatio:e.target.value,
        },()=>{console.log(this.state.feeRatio)});
    }
    //确认按钮
    modalisOkBtn(type){
        let self = this
        const feeRatio = this.state.feeRatio
        console.log(feeRatio)
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/backcapital/config/switchFeeRatio',qs.stringify({
                feeRatio
            })).then(res => {
                const result = res.data;
                console.log(result);
                if(result.code == 0){
                    message.success(result.msg);
                    self.setState({
                        visible:false
                    })
                    self.requestTable()
                    resolve(result.msg)
                }else{
                    message.warning(result.msg);
                }   
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
        console.log('OK');
    }
    //确定按钮弹框
    modalisOk(feeRatio,type){
        let self = this
        //const feeRatio = this.state.feeRatio
        Modal.confirm({
            title: '确定切换手续费比例？切换后会立刻把回购GBC余额转入到分红地址和抽奖账户中',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(feeRatio,type)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
        
    }
 
    //google 验证弹窗
    modalGoogleCode(item,type){
        console.log(item)
        this.setState({
            googVisibal:true,
            item,
            type,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const {item,type} = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(type == 0||type == 1){
                    this.setState({
                        googVisibal:false
                    },() => this.onChangeStatus(item,type))
                }else if(type == 3){
                    this.setState({
                        googVisibal:false
                    },()=>this.modalisOkBtn(item))
                }else{
                    this.setState({
                        googVisibal:false
                    },()=>this.modalsaveModify(item))
                }
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //修改配置弹窗
    modifyConfig(item, type){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.modalGoogleCode(item, type)}>
                确定
            </Button>,
        ]
        this.setState({
            visible:true,
            title:"修改配置",
            width:"500px",
            modalHtml:<ModalUpdate item={item} handleInputChange ={this.handleInputChange}/>,
            id:item.id,
            bcUserId:item.bcUserId,
            bcFrequency:item.bcFrequency,
            feeRatio:item.feeRatio,
            baseBalance:item.baseBalance,
            luckyUserId:item.luckyUserId,
            withdrawFrequency:item.withdrawFrequency,
            withdrawAddress:item.withdrawAddress,
            webUrl:item.webUrl
        })
    }
    //列表
    requestTable(){
        const {pageIndex,pageSize } = this.state
        axios.get(DOMAIN_VIP+"/backcapital/config/get").then(res => {
            const result = res.data;
            console.log(result)
            if(result.code == 0){
                this.setState({
                    tableList:result.data
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
     //输入时 input 设置到 satte
    handleInputChange(name,value){
        this.setState({
            [name]: value
        });
    }
    
    //保存修改配置按钮
    modalsaveModify(item){
        const {id,bcUserId,bcFrequency,feeRatio,baseBalance,luckyUserId,withdrawFrequency,withdrawAddress,webUrl} = this.state
        axios.post(DOMAIN_VIP+'/backcapital/config/update',qs.stringify({
            id,bcUserId,bcFrequency,feeRatio,baseBalance,luckyUserId,withdrawFrequency,withdrawAddress,webUrl
        })).then(res => {
            const result = res.data;
            console.log(result);
            if(result.code == 0){
                message.success(result.msg);
                this.setState({
                    visible:false
                })
                this.requestTable()
            }else{
                message.warning(result.msg);
            }   
        })
    }
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.modalGoogleCodeBtn(values)
        });
      }
      saveFormRef(formRef){
        this.formRef = formRef;
      }
        //谷歌弹窗关闭
    onhandleCancel(){
        this.setState({
            googVisibal: false 
        })
    }
    render(){
        const {visible,modalHtml,title,width,tableList,limitBtn} = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：监控中心>GBC回购监控>GBC回购管理
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll ">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title min_69px">回购账号id</th>
                                                <th className="column-title">回购执行频率</th>
                                                <th className="column-title min_68px">手续费比例</th>
                                                <th className="column-title">打底金额usdc</th>
                                                <th className="column-title min_68px">抽奖账号id</th>
                                                <th className="column-title">回购转出频率</th>                   
                                                <th className="column-title min_68px">分红地址</th>  
                                                <th className="column-title">查看链接</th>
                                                <th className="column-title">回购任务状态</th>
                                                <th className="column-title">转出任务状态</th>  
                                                <th className="column-title">回购任务切换</th> 
                                                <th className="column-title">转出任务切换</th>
                                                <th className="column-title">操作</th>                                                  
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                               tableList.length>0?
                                               tableList.map((item,index)=>{
                                                   return (
                                                        <tr key={index}>
                                                            <td>{item.bcUserId}</td>
                                                            <td>{item.bcFrequency}</td>
                                                            <td>{item.feeRatio==0?'50%':'100%'}</td>
                                                            <td>{item.baseBalance?toThousands(item.baseBalance):''}</td>
                                                            <td>{item.luckyUserId}</td>
                                                            <td>{item.withdrawFrequency}</td>
                                                            <td>{item.withdrawAddress}</td>
                                                            <td>{item.webUrl}</td> 
                                                            <td>{item.bcTaskStatus==0?'false':'true'}</td> 
                                                            <td>{item.withdrawTaskStatus==0?'false':'true'}</td> 
                                                            <td>{limitBtn.indexOf('switchTask')>-1?<a href="javascript:void(0)" onClick={() => this.changeStatus(item.bcTaskStatus,1)}>{item.bcTaskStatus==0?'启动':'停止'}</a>:''}</td> 
                                                            <td>{limitBtn.indexOf('switchWithdrawTask')>-1?<a href="javascript:void(0)" onClick={() => this.changeStatus(item.withdrawTaskStatus,0)}>{item.withdrawTaskStatus==0?'启动':'停止'}</a>:''}</td>                                                      
                                                            <td>
                                                                {limitBtn.indexOf('switchFeeRatio')>-1?<a href="javascript:void(0)" className="mar20" onClick={() => this.changeRatio(item.feeRatio,3)}>切换手续费比例</a>:''}
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" onClick={() => this.modifyConfig(item,4)}>修改配置</a>:""}                                                                
                                                            </td>
                                                        </tr>
                                                        )
                                                    }):<tr className="no-record"><td colSpan="13">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 60 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}            
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='BC'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }
}