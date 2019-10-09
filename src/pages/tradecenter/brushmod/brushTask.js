import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, DEFAULTVALUE, TIMEFORMAT,SELECTWIDTH } from '../../../conf'
import { Button, Pagination, Select, Modal,message } from 'antd'
import GoogleCode from '../../common/modal/googleCode'
import { pageLimit} from '../../../utils'
import moment from 'moment'
const Big = require('big.js')
const Option = Select.Option;

export default class BrushTask extends React.Component {

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
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.startBrushTaskBtn = this.startBrushTaskBtn.bind(this)
        this.stopBrushTaskBtn = this.stopBrushTaskBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('task',this.props.permissList)
        })
    }

    requestTable(){
        axios.get(DOMAIN_VIP+"/brush/task/gbc/list").then(res => {
            const result = res.data
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
                }
            }else{
                message.warning(result.msg)
            }
        })
    }
    //启动
    startBrushTaskBtn(market){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/brush/task/gbc/start',qs.stringify({
                market:market
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    this.requestTable()
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
    //启动弹框
    startBrushTask(market, type){
        let self = this
        Modal.confirm({
            title: '您确定要启动?',
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
    //停止
    stopBrushTaskBtn(market){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/brush/task/gbc/stop',qs.stringify({
                market:market
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    this.requestTable()
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
    //停止弹窗
    stopBrushTask(market,type){
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

    render() {
        const { visible,title,modalHtml,limitBtn } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：交易中心 > GBC刷量管理 > GBC任务管理
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">市场</th>
                                                <th className="column-title">任务类型</th>
                                                <th className="column-title">任务状态</th>
                                                <th className="column-title">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                this.state.tableList.length > 0 ? this.state.tableList.map((item, index) => {

                                                    return (
                                                        <tr key={index}>
                                                            <td>{item.market}</td>
                                                            <td>{item.taskName}</td>
                                                            <td>{`${item.taskStatus}`}</td>
                                                            <td>
                                                                {limitBtn.indexOf('gbcStart')>-1?<a href="javascript:void(0)" className="mar20" onClick={() => this.startBrushTask(item.market,'start')} >启动</a>:''}
                                                                {limitBtn.indexOf('gbcStop')>-1?<a href="javascript:void(0)" onClick={() => this.stopBrushTask(item.market,'stop')} >停止</a> :''}                                                               
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
                 mid='BT'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}





























