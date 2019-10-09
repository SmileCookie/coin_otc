import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,TIMEFORMAT,TIMEFORMAT_ss,TIMEFORMAT_DAYS_ss,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button,Pagination,Select,DatePicker,Modal,message } from 'antd'
import GoogleCode from '../../common/modal/googleCode'
import {tableScroll} from '../../../utils'
const Option = Select.Option;
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;

export default class MessageQuery extends React.Component{

    constructor(props) {
        super(props)
        this.state = {
            showHide:true,
            sendTimeBegin:'',
            sendTimeEnd :'',
            sendState:'',
            sendType:'',
            title:'',
            content :'',
            mobile:'',
            userName:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            tableList:[],
            visible:false,
            time:[],
            modalHtml:'',
            mtitle:'',
            googVisibal:false,
            check:'',
            item:{},
            googletype:'',
            height:0,
            tableScroll:{
                tableId:'MESEQY',
                x_panelId:'MESEQYX',
                defaultHeight:500,
            }
        }   
        this.handleInputChange = this.handleInputChange.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changeSendState = this.changeSendState.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.onChange = this.onChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.reSendMsg = this.reSendMsg.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.reSendMsgBtn = this.reSendMsgBtn.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        // tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillUnmount(){
        // tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight){
        this.setState({
            xheight
        })
    }
    //select change
    changeSendState(value) {
        //console.log(`selected ${value}`);
        this.setState({
            sendState:value
        })
    }
    //弹窗隐藏
    handleCancel(){
        //console.log("handleCancel")
        this.setState({ 
            visible: false,
        });
    }
    //select change 短信类型
    handleChangeType(val){
        this.setState({
            sendType:val
        })
    }
    //时间改变时
    onChange(date, dateString) {
        this.setState({
            sendTimeBegin:dateString[0],
            sendTimeEnd:dateString[1],
            time:date
        })
    }
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page,
            pageSize:pageSize
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

    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        let value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        
        this.setState({
            [name]: value.replace(/^\s+|\s+$/g, '')//替换空格
        });
    }
    //google验证弹窗
    modalGoogleCode(item,type){
        this.setState({
            googVisibal:true,
            item,
            googletype:type
        })
    }
    //google 按钮
    modalGoogleCodeBtn(value){
        const { item,googletype } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                },()=>this.reSendMsgBtn(item))
                
            }else{
                message.warning(result.msg)
            }
        })
    }
    //再次发送按钮
    reSendMsgBtn(id){
        let self = this;
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/otcRePush/push', qs.stringify({
                id
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    self.requestTable();
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
    //再次发送弹窗
    reSendMsg(id, type){
        let self = this
        Modal.confirm({
            title: '您确定要重发?',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(id, type)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())        
    }
    //列表详情
    requestTable(currIndex,currSize){
        const { sendTimeBegin,sendTimeEnd,pageIndex,pageSize,sendState,sendType,content,mobile } = this.state
        axios.post(DOMAIN_VIP+'/otcSms/list',qs.stringify({
            sendTimeBegin,sendTimeEnd,sendState,sendType,content,mobile,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
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

    //重置按钮
    onResetState(){
         this.setState({
            sendTimeBegin:'',
            sendTimeEnd:'',
            mobile:'',
            content:'',
            sendType:'',
            sendState:'',
            time:[]
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
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err, valus) => {
            if(err){
                return;
            }
            form.resetFields();
            this.modalGoogleCodeBtn(valus)
        })
    }
    saveFormRef(formRef){
        this.formRef = formRef
    }
    //google弹窗关闭
    onhandleCancel(){
        this.setState({
            googVisibal:false
        })
    }
    render(){
        const { showHide,sendTimeBegin,sendTimeEnd,sendState,sendType,time,mobile,mtitle,content,userName,pageTotal,tableList,pageIndex,pageSize,visible,modalHtml,googVisibal,check } = this.state
        let regExp = new RegExp('<div style="padding: 0px 20px; height: 50px; text-align: right; line-height: 40px; overflow: hidden;"></div>','ig')
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 消息管理 > 消息查询
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div className="x_panel">
                            
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发送状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={sendState} style={{ width: SELECTWIDTH }} onChange={this.changeSendState}>
                                                <Option value="">请选择</Option>
                                                <Option value="0">失败</Option>
                                                <Option value="1">成功</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label" style={{width:'150px'}}>短信类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={sendType} style={{ width: SELECTWIDTH }} onChange={this.handleChangeType}>
                                                <Option value="">请选择</Option>
                                                <Option value="aliyun_sms">阿里大鱼</Option>
                                                <Option value="yunpian_sms">云片</Option>
                                                <Option value="luosimao_sms">螺丝帽</Option>
                                                <Option value="5c_sms">5C短信平台</Option>
                                                <Option value="rush_mail">RUSHMAIL邮件平台</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label" >消息内容：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="content" value={content} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label" style={{width:'150px'}}>接收手机号/邮箱：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="mobile" value={mobile} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发送时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChange} 
                                            showTime={{
                                                defaultValue:[moment('00:00:00', TIMEFORMAT_DAYS_ss), moment('23:59:59', TIMEFORMAT_DAYS_ss)]
                                            }}
                                            value={time} 
                                            format={TIMEFORMAT_ss}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-3 col-sm-3 col-xs-3 right marTop">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button> 
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>  
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive-fixed">
                                    <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title ">序号</th>
                                                <th className="column-title ">用户编号</th>
                                                <th className="column-title ">用户名</th>
                                                <th className="column-title">接收手机号/邮箱</th>
                                                <th className="column-title">发送时间</th>
                                                <th className="column-title">发送状态</th>
                                                <th className="column-title">短信类型</th>
                                                <th className="column-title">短信内容</th>
                                                {/* <th className="column-title">操作</th>*/}
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.userId}</td>
                                                            <td>{item.userName}</td>
                                                            <td>{item.mobile}</td>
                                                            <td>{moment(item.sendtime).format(TIMEFORMAT_ss)}</td>
                                                            <td>{item.sendStateName}</td>
                                                            {/* <td><span dangerouslySetInnerHTML={{__html:item.cont}}></span></td> */}
                                                            <td>{item.sendTypeName}</td>
                                                            <td ><div className='pd0_mg0' dangerouslySetInnerHTML={{__html:item.content.replace(regExp,'').replace(/<br \/>/ig,'')}}></div></td>
                                                            {/* <td><a href="javascript:void(0)" onClick={()=>this.reSendMsg(item.id)}>重发</a></td> */}
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }

                                        </tbody>
                                    </table>
                                </div>
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
                                                showSizeChanger
                                                showQuickJumper
                                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                defaultPageSize={PAGESIZE}
                                                 />
                                }
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={mtitle}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    { modalHtml }
                </Modal> 
                <GoogleCode 
                wrappedComponentRef={this.saveFormRef}
                check={check}
                handleInputChange={this.handleInputChange}
                mid='MQ'
                visible={googVisibal}
                onCancel={this.onhandleCancel}
                onCreate={this.handleCreate}/>
            </div>
        )
    }
}






























































