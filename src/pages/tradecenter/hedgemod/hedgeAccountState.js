import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import GoogleCode from '../../common/modal/googleCode'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT} from '../../../conf'
import {Tabs,Pagination,Modal,message ,Button} from 'antd'
import { pageLimit,tableScroll }  from '../../../utils'
const TabPane = Tabs.TabPane;

export default class HedgeAccountState extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            tableList:[],
            visible:false,
            width:'600px',
            title:'',
            modalHtml:'',
            googleCode:'',
            limitBtn: [],
            googVisibal:false,
            item:'',
            type:'',
            check:'',
            height:0,
            tableScroll:{
                tableId:'HDGEAOTSE',
                x_panelId:'HDGEAOTSEX',
                defaultHeight:500,
                height:0,
            }
        }
        this.requestTable = this.requestTable.bind(this)
        this.modifyTrue = this.modifyTrue.bind(this)
        this.modifyFalse = this.modifyFalse.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.modifyTrueBtn = this.modifyTrueBtn.bind(this)
        this.modifyFalseBtn = this.modifyFalseBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('hedge', this.props.permissList)
        })
        tableScroll(`#${this.state.tableScroll.tableId}`,'add')
    }
    componentWillUnmount(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    requestTable(){
        axios.get(DOMAIN_VIP+"/brush/hedge/account/list").then(res => {
            const result = res.data
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
    //启用
    modifyTrueBtn(item){
        let self = this
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/brush/hedge/account/valid/update',qs.stringify({
                platform:item.platform,userName:item.userName,valid:1,
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
    }
    //启用弹窗
    modifyTrue(item,type){
        let self = this;
        Modal.confirm({
            title: '您确定要启用?',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk(){
                self.modalGoogleCode(item, type)
                // return new Promise((resolve, reject) => {
                //     axios.post(DOMAIN_VIP+'/brush/hedge/account/valid/update',qs.stringify({
                //         platform:item.platform,userName:item.userName,valid:1,
                //     })).then(res => {
                //         const result = res.data;
                //         if(result.code == 0){
                //             message.success(result.msg)
                //             self.requestTable()
                //             resolve(result.msg)
                //         }else{
                //             message.warning(result.msg)
                //         }
                //     }).then(error => {
                //         reject(error)
                //     })
                // }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        })
    }
    //禁用
    modifyFalseBtn(item){
        let self = this
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/brush/hedge/account/valid/update',qs.stringify({
                platform:item.platform,userName:item.userName,valid:0,
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
    }
    //禁用弹窗
    modifyFalse(item, type){
        let self = this;
        Modal.confirm({
            title: '您确定要禁用?',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                self.modalGoogleCode(item, type)
                // return new Promise((resolve, reject) => {
                //     axios.post(DOMAIN_VIP+'/brush/hedge/account/valid/update',qs.stringify({
                //         platform:item.platform,userName:item.userName,valid:0,
                //     })).then(res => {
                //         const result = res.data;
                //         if(result.code == 0){
                //             message.success(result.msg)
                //             self.requestTable()
                //             resolve(result.msg)
                //         }else{
                //             message.warning(result.msg)
                //         }
                //     }).then(error => {
                //         reject(error)
                //     })
                // }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        })
    }
    //关闭弹窗
    handleCancel(){
        this.setState({
            visible: false,
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
        //输入时 input 设置到 satte
        handleInputChange(event) {
            const target = event.target;
            const value = target.type === 'checkbox' ? target.checked : target.value;
            const name = target.name;
            this.setState({
                [name]: value
            });
        }  
    //google 按钮
    modalGoogleCodeBtn(value){
        const { item,type } = this.state
        const {googleCode } = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                if(type == "1"){
                    this.modifyTrueBtn(item)
                }else if(type == "2"){
                    this.modifyFalseBtn(item)
                }
                this.setState({
                    googVisibal: false,
                })
                
            }else{
                message.warning(result.msg)
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
        const{tableList,visible,width,title,modalHtml,limitBtn }=this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 保值管理 > 保值账户状态
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">

                        <div className="x_panel">
                                <div className="x_content">
                                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                            <thead>
                                                <tr className="headings">
                                                    <th className="column-title">平台</th>
                                                    <th className="column-title">账号名称</th>
                                                    <th className="column-title">账号状态</th>
                                                    <th className="column-title">操作</th>                  
                                                </tr>
                                              </thead> 
                                            <tbody>
                                            {
                                                tableList.length>0? 
                                                tableList.map((item,index)=>{
                                                    return (
                                                        <tr key={index}>
                                                            <td>{item.platform}</td>
                                                            <td>{item.userName}</td>
                                                            <td>{item.valid == '0'?"禁用":"启用"}</td>
                                                            <td>{
                                                                limitBtn.indexOf('accountValidUpdate')>-1?
                                                                (item.valid == '0'?
                                                                 <a href="javascript:void(0)" onClick={() => this.modifyTrue(item,"1")}>启用</a>:
                                                                 <a href="javascript:void(0)" onClick={() => this.modifyFalse(item,"2")}>禁用</a>)
                                                                 :''
                                                                 }</td>
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
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
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {modalHtml}
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='HAS'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
        
    }
}