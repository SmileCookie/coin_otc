import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../conf'
import {message,Input,Modal,Button } from 'antd'
import ModalchangeDetail from './modal/modalchangeDetail'
import {pageLimit,tableScroll} from '../../../utils'

export default class ModalDictionary extends React.Component{
    constructor(props){
        super(props)
        this.state = {
           tableList:[],
           visible:false,
           title:'',
           width:'',
           modalHtml:'',
           status:1,
           key:'',
           remark:'',
           value:'',
           type:'',
           showHide:true,
           reremark:'',
           rekey:'',
           limitBtn:[],
           loading:false,
           height:0,
            tableScroll:{
                tableId:'MDLDCNY',
                x_panelId:'MDLDCNYX',
                defaultHeight:600,
            }
        }
        this.requestTable = this.requestTable.bind(this)
        this.changeDetail = this.changeDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onAuditInfoBtn = this.onAuditInfoBtn.bind(this)
        this.deleteItem = this.deleteItem.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.getPushAddress = this.getPushAddress.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn:pageLimit('otcConfig',this.props.permissList)
        })
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
    requestTable(){
        const {reremark,rekey} = this.state
        axios.post(DOMAIN_VIP+'/otcConfig/query',qs.stringify({
            status:1,remark:reremark,key:rekey
         })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //地址推送
    getPushAddress(){
        axios.get(DOMAIN_VIP + "/common/push").then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
     //输入时 input 设置到 satte
     handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
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
    changeDetail(item){
       let mtitle= item.id?'修改信息':'新增'
       let huitype =item.id?true:false
       console.log(huitype)
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.onAuditInfoBtn(item)}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            title:mtitle,
            width:'600px',
            modalHtml:<ModalchangeDetail item={item} handleInputChange ={this.handleInputChange} huitype={huitype}/>,
            key:item.key||'',
            remark:item.remark||'',
            value:item.value||'',
            type:item.type||'',
            
        })
    }
    //重置
    onResetState(){
        this.setState({
            reremark:'',
            rekey:''
        })
    }
    onAuditInfoBtn(item){
        const {status,key,value,type,remark}=this.state
        this.setState({loading:true})
        item.id?
        axios.post(DOMAIN_VIP+'/otcConfig/update',qs.stringify({
           id:item.id,status,key,value,type,remark
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState(
                    {
                        visible:false,
                        loading:false
                    }
                )
                this.requestTable()
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        }):axios.post(DOMAIN_VIP+'/otcConfig/insert',qs.stringify({
            status,key,value,type,remark
         })).then(res => {
             const result = res.data;
             if(result.code == 0){
                 message.success(result.msg)
                 this.setState(
                     {
                         visible:false,
                         loading:false
                     }
                 )
                 this.requestTable()
             }else{
                 message.warning(result.msg)
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
    //删除
    deleteItem(id){
        let self = this;
        Modal.confirm({
            title: "确定删除本项吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+"/otcConfig/delete",qs.stringify({
                        id
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
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    
    render(){
        const {tableList,visible,title,width,modalHtml,showHide,reremark,rekey,limitBtn} = this.state
        return(
            <div className="right-con">
            <div className="page-title">
                当前位置：系统中心 > 配置中心 > 通用配置
                <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
            </div>
            <div className="clearfix"></div>
            <div className="row">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                        
                        <div className="x_content">
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">字典：</label>
                                    <div className="col-sm-8">
                                        <input type="text" className="form-control"  name="rekey" value={rekey} onChange={this.handleInputChange} />
                                    </div>
                                </div>
                          </div>
                          <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">备注：</label>
                                    <div className="col-sm-8">
                                        <input type="text" className="form-control"  name="reremark" value={reremark} onChange={this.handleInputChange} />
                                    </div>
                                </div>
                          </div>
                        </div>
                        <div className="col-md-6 col-sm-6 col-xs-6 right">
                            <div className="right">
                                <Button type="primary" onClick={this.requestTable}>查询</Button>
                                <Button type="primary" onClick={this.onResetState}>重置</Button>
                                {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={this.changeDetail}>新增</Button>:''}
                                <Button type="primary" onClick={()=>this.getPushAddress()}>地址推送</Button> 
                            </div>
                        </div>
                    <div>
                </div>
             </div>}
            
             
            <div className="x_panel">                 
                <div className="x_content">
                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll table-more">
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">字典</th>
                                    <th className="column-title wid300">字典值</th>
                                    <th className="column-title">备注</th>
                                    <th className="column-title">类型值</th>
                                    {/* <th className="column-title">操作</th>                    */}
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length>0?
                                    tableList.map((item,index) => {
                                        return (
                                            <tr key={index}>
                                                <td>{index+1}</td>
                                                <td>{item.key}</td>
                                                <td>{item.value}</td>
                                                <td>{item.remark}</td>
                                                <td>{item.type}</td>
                                                {/* <td>
                                                    {limitBtn.indexOf('update')>-1? <a className="mar20" onClick ={()=>this.changeDetail(item)}>修改</a>:''}
                                                    {limitBtn.indexOf('delete')>-1?<a onClick ={()=>this.deleteItem(item.id)}>删除</a>:''}
                                                </td> */}
                                            </tr>
                                        )
                                    })
                                    :<tr className="no-record"><td colSpan="20">暂无数据</td></tr>
                                }
                            </tbody>
                        </table>
                    </div>
                   
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    style={{marginTop:'-80px'}}
                    >
                    {modalHtml}            
                </Modal>
            </div>
            </div>
             </div>
            </div>
        )
    }
}