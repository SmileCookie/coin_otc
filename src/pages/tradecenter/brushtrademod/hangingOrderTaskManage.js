import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, SELECTWIDTH,  } from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import { Button , Modal, message } from 'antd'
import moment from 'moment'
import { pageLimit} from "../../../utils"

export default class BrushTaskManage extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            tableList: [],
            visible:false,
            title:'',
            modalHtml:'',
            limitBtn: [],
            googVisibal:false,
            market:'',
            type:'',
            check:'',
        }

        this.requestTable = this.requestTable.bind(this)
        this.startBrushTask = this.startBrushTask.bind(this)
        this.stopBrushTask = this.stopBrushTask.bind(this)
        this.switchPlatform = this.switchPlatform.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.switchPlatformBtn = this.switchPlatformBtn.bind(this)
        this.startBrushTaskBtn = this.startBrushTaskBtn.bind(this)
        this.stopBrushTaskBtn = this.stopBrushTaskBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.allStartTask = this.allStartTask.bind(this)
        this.allStopTask = this.allStopTask.bind(this)
        this.allStartTaskBtn = this.allStartTaskBtn.bind(this)
        this.allStopTaskBtn = this.allStopTaskBtn.bind(this)
    }

    componentDidMount() {
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('task',this.props.permissList)
        })
    }
    requestTable(){
        axios.get(DOMAIN_VIP+"/brush/task/cancelList").then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    tableList:result.data
                })
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
 
    //弹窗隐藏
    handleCancel(){
        this.setState({ 
            visible: false,
        });
    }

    //google 验证弹窗
    modalGoogleCode(market,type){
        this.setState({
            googVisibal:true,
            market,
            type,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { market,type } = this.state
        const {googleCode } = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(type == 'start'){
                    this.setState({
                        googVisibal:false
                    },() => this.startBrushTaskBtn(market))
                }else if(type == 'stop'){
                    this.setState({
                        googVisibal:false
                    },()=>this.stopBrushTaskBtn(market))
                }else if(type == 'allStart'){
                    this.setState({
                        googVisibal:false
                    },()=>this.allStartTaskBtn(market))
                }else if(type == 'allStop'){
                    this.setState({
                        googVisibal:false
                    },()=>this.allStopTaskBtn(market))
                }
                // else{
                //     this.setState({
                //         googVisibal:false
                //     },() => this.switchPlatformBtn(market,type))
                // }
            }else{
                message.warning(result.msg)
            }
        })
    }
    //启动
    startBrushTaskBtn(market){
        let self = this
        return new Promise((resolve, reject) => {
            axios.get(DOMAIN_VIP+'/brush/task/cancelStart',{params:{
                market:market
            }}).then(res => {
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
    //启动弹窗
    startBrushTask(market,type){
        let self = this
        Modal.confirm({
            title: '您确定要启动?',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(market,type)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //停止
    stopBrushTaskBtn(market){
        let self = this
        return new Promise((resolve, reject) => {
            axios.get(DOMAIN_VIP+'/brush/task/stopCancel',{
                params:{
                    market:market
                }
            }).then(res => {
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
    //停止弹窗
    stopBrushTask(market, type){
        let self = this
        Modal.confirm({
            title: '您确定要停止?',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(market, type)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //切换平台
    switchPlatformBtn(market,platform){
        let self = this
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/brush/task/switchPlatform',qs.stringify({
                market:market,
                platform:platform
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
    //切换平台弹窗
    switchPlatform(market,platform){
        let self = this
        Modal.confirm({
            title: `确定要把市场【${market}】切换成【${platform}】平台？`,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(market, platform);
            },
            onCancel() {
                console.log('Cancel');
            },
        });
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

    //全部开始
    allStartTask(){
        let self = this
        Modal.confirm({
            title: '您确定要启动?',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode("all","allStart")
            },
            onCancel() {
                console.log('Cancel');
            },
        });
       
    }
    allStartTaskBtn(){
        axios.get(DOMAIN_VIP+"/brush/task/allCancelStart").then((res) => {
            const result = res.data
            if(result.code == 0){
                this.requestTable()
            }
            message.info(result.msg)
        })
    }
    //全部停止
    allStopTask(){
        let self = this
        Modal.confirm({
            title: '您确定要停止?',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode("all","allStop")
            },
            onCancel() {
                console.log('Cancel');
            },
        });
       
    }
    allStopTaskBtn(){
        axios.get(DOMAIN_VIP+"/brush/task/allCancelStop").then((res) => {
            const result = res.data
            if(result.code == 0){
                this.requestTable()
            }
            message.info(result.msg)
        })
    }

    render() {
        const { tableList,visible,title,modalHtml,limitBtn } = this.state 
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 量化交易管理 > 挂撤单任务管理
            </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="col-md-12 col-sm-12 col-xs-12 marbot10">
                                    <div className="right">
                                        <Button type="primary" onClick={this.allStartTask} >全部开始</Button>
                                        <Button type="danger" onClick={this.allStopTask} >全部停止</Button>
                                    </div>
                                </div>
                                <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">市场</th>
                                                <th className="column-title">任务状态</th>
                                                <th className="column-title">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ? tableList.map((item, index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{item.market}</td>
                                                            <td>{item.taskStatus?"开":"关"}</td>
                                                            <td>
                                                                {limitBtn.indexOf('start')>-1?<a href="javascript:void(0)" className="mar20" onClick={() => this.startBrushTask(item.market,'start')}>启动</a>:''}
                                                                {limitBtn.indexOf('stop')>-1?<a href="javascript:void(0)" onClick={() => this.stopBrushTask(item.market,'stop')}>停止</a>:''}                                                              
                                                            </td>                                                            
                                                        </tr>
                                                    )
                                                }) : <tr className="no-record"><td colSpan="10">暂无数据</td></tr>
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
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    { modalHtml }
                </Modal> 
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='BOTM'
                 visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}




























































