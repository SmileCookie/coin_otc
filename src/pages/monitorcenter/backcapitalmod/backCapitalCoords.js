import React from 'react'
import axios from '../../../utils/fetch'
import ModalAddcapital from './modal/modalAddcapital'
import qs from 'qs'
import { DOMAIN_VIP,TIMEFORMAT,PAGEINDEX,PAGESIZE,SELECTWIDTH,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Modal,Pagination,message,Button,Select} from 'antd'
import { pageLimit,tableScroll } from '../../../utils'
const Option = Select.Option;
import GoogleCode from '../../common/modal/googleCode'
export default class BackCapitalCoords extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
           tableList:[] ,
           visible:false,
           modalHtml:'',
           title:'',
           width:'',
           x:'',
           y:'',
           status:'0',
           pageIndex:PAGEINDEX,
           pageSize:PAGESIZE,
           PAGRSIZE_OPTIONS20:PAGRSIZE_OPTIONS20,
           pageTotal:0,
           ycoord:'',
           xcoord:'',
           googleCode:'',
           check:'',
           googVisibal:false,
           item:'',
           type:'',
           limitBtn:[],
           loading:false,
           height:0,
            tableScroll:{
                tableId:'BACPTALCS',
                x_panelId:'BACPTALCSX',
                defaultHeight:500,
                height:0,
            }
           
        }
        this.changeStatus = this.changeStatus.bind(this)
        this.changeRatio = this.changeRatio.bind(this)
        this.deleteConfig = this.deleteConfig.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modalisOkBtn = this.modalisOkBtn.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.ChangehandleInput = this.ChangehandleInput.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleStatusChange = this.handleStatusChange.bind(this)
         this.modalGoogleCode = this.modalGoogleCode.bind(this)
         this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.deleteConfigItem = this.deleteConfigItem.bind(this)
        this.changeStatusBtn = this.changeStatusBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        // this.requestTable()
        // this.setState({
        //     limitBtn: pageLimit('coords', this.props.permissList)
        // })
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillReceiveProps(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillUnmount(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight){
        this.setState({
            xheight
        })
    }
    //点击收起
    clickHide() {
        let { showHide,xheight,pageSize } = this.state;
        if(showHide&&pageSize>10){
            this.setState({
                showHide: !showHide,
                height:xheight,
            })
        }else{
            this.setState({
                showHide: !showHide,
                height:0
            })
        }
        // this.setState({
        //     showHide: !showHide,
        // })
    }
  //点击分页
  changPageNum(page,pageSize){
    this.setState({
        pageIndex:page,
        current:page
    })
    this.requestTable(page,pageSize)
}
//分页的 pagesize 改变时
onShowSizeChange(current,size){
    this.requestTable(current,size)
    this.setState({
        pageIndex:current,
        pageSize:size
    })
}
 //查询 按钮
 inquireBtn(){
    this.setState({
        pageIndex:PAGEINDEX,
        current:PAGEINDEX
    },()=>this.requestTable())
}
    changeRatio(){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={this.modalisOkBtn}>
                保存
            </Button>,
        ]
        this.setState({
            visible:true,
            title:"修改坐标",
            width:"500px",
            modalHtml:<ModalAddcapital ChangehandleInput ={this.ChangehandleInput}/>,
        })
    }
    //新增确定按钮
    modalisOkBtn(){
        let self = this
        const{xcoord,ycoord}= this.state
        if(!xcoord || !ycoord){
            message.warning('坐标不能为空')
            return false
        }
        this.setState({
            loading:true
        })
        axios.post(DOMAIN_VIP+'/backcapital/coords/insert',qs.stringify({
            x:xcoord,y:ycoord
        })).then(res => {
            const result = res.data;
            console.log(result);
            if(result.code == 0){
                message.success(result.msg);
                self.setState({
                    visible:false,
                    loading:false
                })
                self.requestTable()
            }else{
                message.warning(result.msg);
                this.setState({
                    loading:false
                })
            }   
        })
    }
    handleCancel(){
        this.setState({
            visible:false,
            loading:false
        })
    }
    //修改状态确认按钮
    changeStatusBtn(status, id){
        const statusChange = status==1?2:1
        //let typeName = status == 1?'关闭':'开启'
        let self = this
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/backcapital/coords/status/update',qs.stringify({
                id,status:statusChange
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
    //修改状态弹框
    changeStatus(status,id){
        //const statusChange = status==1?2:1
        let typeName = status == 1?'关闭':'开启'
        let self = this
        Modal.confirm({
            title:`确定要` + typeName,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(status, id)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //删除Item
    deleteConfigItem(item){
        let self = this;
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/backcapital/coords/delete',qs.stringify({
                id:item.id
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
    //删除弹框
    deleteConfig(item, type){
        let self = this
        Modal.confirm({
            title: '确定删除此条记录？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(item, type);
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //列表
    requestTable(currIndex,currSize){
        const {x,y,status,pageIndex,pageSize} = this.state
        axios.get(DOMAIN_VIP+"/backcapital/coords/list", { params: {
            x,y,status,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize,
        }}).then(res => {
            const result = res.data;
            console.log(result)
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
     //输入时 input 设置到 satte
     ChangehandleInput(name,value){
        this.setState({
            [name]: value
        });
    }
    
    //状态选则
    handleStatusChange(value){
        this.setState({
            status:value
        })
    }

    //google 验证弹窗
    modalGoogleCode(item,type){
        this.setState({
            googVisibal:true,
            item,
            type,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { item,type } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                })
                if(type == 1){
                    this.setState({
                        googVisibal:false
                    },() => this.deleteConfigItem(item))
                }else{
                    this.setState({
                        googVisibal:false
                    },() => this.changeStatusBtn(item,type))
                }
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    modalsaveModify(){
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
    onResetState(){
        this.setState({
            x:'',
            y:'',
            status:'0'
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
            const {showHide,tableList,visible,modalHtml,title,width,x,y,status,pageIndex,pageSize,pageTotal,limitBtn,defaultPageSize} = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：监控中心 > GBC回购监控 > 私钥坐标管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                                <div className="x_content">
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                        <label className="col-sm-3 control-label">x坐标：</label>
                                        <div className="col-sm-9">
                                                <input type="text" className="form-control"  name="x" value={x} onChange={this.handleInputChange} />
                                        </div>
                                        </div>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                    <label className="col-sm-3 control-label">y坐标：</label>
                                    <div className="col-sm-9">
                                             <input type="text" className="form-control"  name="y" value={y} onChange={this.handleInputChange} />
                                    </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">状态：</label>
                                        <div className="col-sm-9">
                                        <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.handleStatusChange} >
                                                <Option  value='0'>请选择</Option>
                                                <Option  value='1'>开启</Option>
                                                <Option  value='2'>关闭</Option>
                                        </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={this.changeRatio}>新增</Button>:''}
                                    </div>
                                </div>
                            </div>
                        </div>
                            }
                        <div className="x_panel">
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">x坐标</th>
                                                <th className="column-title">y坐标</th>
                                                <th className="column-title">操作</th>                                                  
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                               tableList.length>0?
                                               tableList.map((item,index)=>{
                                                   return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.x}</td>
                                                            <td>{item.y}</td>
                                                            {/* <td>{item.status==1?'开启':item.status==2?'关闭':''}</td> */}
                                                            <td> 
                                                                {limitBtn.indexOf('updateStatus')>-1?<a href="javascript:void(0)" className={item.status==0?'mar50':'mar20'} onClick={() => this.changeStatus(item.status,item.id)}>{item.status==1?'关闭':item.status==2?'开启':''}</a>:''}
                                                                {limitBtn.indexOf('delete')>-1?<a href="javascript:void(0)" onClick={() => this.deleteConfig(item, 1)}>删除</a>:''}                                                                
                                                            </td>
                                                        </tr>
                                                        )
                                                    }):<tr className="no-record"><td colSpan="5">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                <div className="pagation-box">
                                {
                                    pageTotal>0 && <Pagination
                                                size="small"
                                                current={pageIndex}
                                                total={pageTotal}
                                                showTotal={total => `总共 ${total} 条`}
                                                onChange={this.changPageNum}
                                                onShowSizeChange={this.onShowSizeChange}
                                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                defaultPageSize={PAGESIZE}
                                                showSizeChanger
                                                showQuickJumper />
                                }
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
                 mid='BCC'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }
}