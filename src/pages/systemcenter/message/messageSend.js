import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { getByteLen } from '../../../utils'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT} from '../../../conf'
import { Button,Tabs,Pagination,Input,message,Popconfirm,Modal } from 'antd'
import { pageLimit } from '../../../utils/index'
import GoogleCode from '../../common/modal/googleCode'
const TabPane = Tabs.TabPane;
const { TextArea } = Input;

export default class MessageSend extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            phones:'',
            msg:'',
            msgEmails:'',
            tab:'1',
            emails:'',
            title:'',
            sendLocation:9,
            // phoneSit:'5',
            modalTitle:'',
            visible:false,
            width:'600px',
            modalHtml:'',
            googleCode:'',
            limitBtn: [],
            check:'',
            googVisibal:false,
            item:'',
            type:''
        }
        this.clickHide = this.clickHide.bind(this)
        this.sendPhone = this.sendPhone.bind(this)
        this.sendEmail = this.sendEmail.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.callback = this.callback.bind(this)
        this.InputphoneChange = this.InputphoneChange.bind(this)
        this.phoneJudge = this.phoneJudge.bind(this)
        this.onResetStateNumber = this.onResetStateNumber.bind(this)
        this.onResetStateEmails = this.onResetStateEmails.bind(this)
        this.onResetEmailsContent =this.onResetEmailsContent.bind(this)
        this.onResetPhoneContent = this.onResetPhoneContent.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.checkLength = this.checkLength.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }
    componentDidMount(){
        this.setState({
            limitBtn: pageLimit('messageSend', this.props.permissList)
        })
    }
    componentWillUnmount(){
    }
     //点击收起
     clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    //tabs 
    callback(key) {
        console.log(key);
        this.setState({
            tab:key
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
    InputphoneChange(event){
        const target = event.target;
        console.log(target.value)
        const value = target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
 
    phoneJudge(){
        const {phones,msg,sendLocation} = this.state
        console.log(1)
        let stringBegin = [];
        let phonesStart = phones.slice(0,4);
       const phoneArray = phones.split("\n")
       if(phones.slice(0,1)=='+'){
           
            if(phonesStart == '+86 '){
                    for(let i of phoneArray){
                        console.log("dfd" +  phonesStart)
                        const aphoneStart = i.slice(0,4)
                        if (aphoneStart !== phonesStart) {
                            message.error('请保持手机号码号段一致！');
                            break;
                        }
                    }    
                }else if(phonesStart == '+852'||phonesStart =='+853'||phonesStart =='+886'){
                    console.log("港澳台手机号")
                    for(let i of phoneArray){
                        console.log("dfd" +  phonesStart)
                        const aphoneStart = i.slice(0,4)
                        if (aphoneStart == '+852'||aphoneStart == '+853'||aphoneStart == '+886') {
                            console.log("一致！")
                        }else{
                            console.log("这个"+aphoneStart)
                            message.error('请保持手机号码号段一致！');
                            break;
                        }
                    }
                }
        }else{
            message.error('手机号码必须以“+”开头');
        }
     
    }
    sendPhone(){
       const {phones,msg,sendLocation} = this.state
       if(phones == ''){
            message.warning('请输入要发送的手机号码！');
       }else if(msg == ''){
            message.warning('请输入要发送的内容！');
       }else{
            const phoneArray = phones.split("\n")
            const phonesNum = phoneArray.join(",")
            
                axios.post(DOMAIN_VIP+'/messageSend/phoneSend',qs.stringify({
                    phones:phonesNum,msg,sendLocation,
                })).then(res => {
                    const result = res.data;
                    console.log(result);
                    if(result.code == 0){
                        message.success(result.msg);
                    }else{
                        message.warning(result.msg);
                    }   
        })
        }
        
    }
    sendEmail(){
        const {emails,msgEmails,title,sendLocation} = this.state
        if(emails == ''){
            message.warning('请输入要发送的邮箱！');
        }else if(msgEmails == ''){
            message.warning('请输入要发送的内容！');
        }else{
            const EmailsArray = emails.split("\n")
            const EmailsNum = EmailsArray.join(",")
            axios.post(DOMAIN_VIP+'/messageSend/emailSend',qs.stringify({
                emails:EmailsNum,msg:msgEmails,sendLocation,title,
            })).then(res => {
                const result = res.data;
                console.log(result);
                if(result.code == 0){
                    message.success(result.msg);
                }else{
                    message.warning(result.msg);
                }  
            })
        }
        
    }
   //重置手机号
   onResetStateNumber(){
        this.setState({
            phones:'',
        })
   }
    //重置手机发送内容
    onResetPhoneContent(){
        this.setState({
            msg:'',
        })
    }
    //重置发送邮箱
    onResetStateEmails(){
        this.setState({
            emails:'',
        })
    }
    //重置邮箱发送内容
    onResetEmailsContent(){
        this.setState({
            msgEmails:'',
            title:''
        })
    }
     //关闭弹窗
     handleCancel(){
        this.setState({
            visible: false,
        })
    }
 
    //google 验证弹窗
    modalGoogleCode(type){
        this.setState({
            googVisibal:true,
            type,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { type } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                if(type == "phone"){
                    this.sendPhone()
                }else if(type == "email"){
                    this.sendEmail()
                }
                this.setState({
                    googVisibal: false,
                })
                
            }else{
                message.warning(result.msg)
            }
        })
    }

    //判断字符长度（汉字算两个字符，字母数字算一个）
    checkLength(e) {
        var maxChars = 280;
        var curr = maxChars - getByteLen(e.target.value);
        if (curr < 0) {
            message.warning("不能再输入了！")
        }
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
        const {tab,showHide,emails,phones,msg,msgEmails,title,visible,modalHtml,width,modalTitle,limitBtn } = this.state
        
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 消息管理 > 消息发送
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                        <Tabs activeKey={tab} onChange={this.callback}>
                                <TabPane tab="短信" key="1"></TabPane>
                                <TabPane tab="邮件" key="2"></TabPane>
                            </Tabs>
                            <div className="x_content">
                            {
                                tab == "1" ? 
                                 <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="col-md-6 col-sm-6 col-xs-6">
                                        <div className="x_title">
                                                <h5>输入需要发送的手机号码</h5>
                                        </div>
                                        <TextArea name="phones" rows={16} value={phones} placeholder="请输入手机号码，例如：+86 15269222008，每个手机号一行。&#13;&#10;大陆手机号号段“+86 。&#13;&#10;香港手机号号段“+852 ”，澳门手机号号段“+853 ”，台湾手机号号段“+886”。&#13;&#10;国外手机号码格式为：+66 0622222222。&#13;&#10;注：不允许同时发送大陆，港澳台，国外手机号号段，每次只能发送一类号段。" onChange={this.InputphoneChange}/>
                                    </div>
                                    <div className="col-md-6 col-sm-6 col-xs-6">
                                        <div className="x_title">
                                                <h5>输入需要发送的内容</h5>
                                        </div>
                                        <TextArea name="msg" rows={16} value={msg} maxLength={280} placeholder="短信每67个字符（1个中文为2个字符）计费1条。最多不超过280字符。" onKeyUp={this.checkLength} onChange={this.handleInputChange}/>
                                    </div>
                                    <div className="col-md-6 col-sm-6 col-xs-6 right" style={{marginTop:"30px"}}>
                                        <div className="right">
                                        <Popconfirm placement="top" onConfirm={() => this.modalGoogleCode('phone')} okText="Yes" cancelText="No"
                                         title='请确认输入内容与号段一致'>
                                            {limitBtn.indexOf('phoneSend')>-1?<Button type="more" onClick={this.phoneJudge}>发送</Button>:''}
                                            </Popconfirm>
                                            <Button type="more" onClick={this.onResetStateNumber}>重置手机号</Button>
                                            <Button type="more" onClick={this.onResetPhoneContent}>重置发送内容</Button>
                                        </div>
                                    </div>
                                </div>
                                :
                                <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="col-md-6 col-sm-6 col-xs-6">
                                        <div className="x_title">
                                                <h5>输入需要发送的邮箱</h5>
                                        </div>
                                        <TextArea name="emails" rows={16} value={emails} placeholder="请输入邮箱，例如：363545262@qq.com，每个邮箱一行。" onChange={this.handleInputChange}/>
                                    </div>
                                    <div className="col-md-6 col-sm-6 col-xs-6">
                                        <div className="x_title">
                                                <h5>输入需要发送的内容</h5>
                                        </div>
                                        <TextArea name="msgEmails" rows={16} value={msgEmails} maxLength={292} placeholder="" onChange={this.handleInputChange}/>
                                    </div>
                                    <div className="col-md-6 col-sm-6 col-xs-6" style={{marginTop:"10px"}}>
                                        <div className="col-md-3 col-sm-3 col-xs-3">
                                            <label className=" control-label">邮件标题：</label>
                                        </div>
                                        <div className="col-md-8 col-sm-8 col-xs-8">
                                            <TextArea name="title" rows={2} value={title} maxLength={280} placeholder="" onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                    <div className="col-md-6 col-sm-6 col-xs-6 right" style={{marginTop:"30px"}}>
                                        <div className="right">
                                            <Popconfirm placement="top" onConfirm={() => this.modalGoogleCode('email')} okText="Yes" cancelText="No"
                                         title='请确认输入内容'>
                                            {limitBtn.indexOf('emailSend')>-1?<Button type="more">发送</Button>:''}
                                            </Popconfirm>
                                            <Button type="more" onClick={this.onResetStateEmails}>重置邮箱</Button>
                                            <Button type="more" onClick={this.onResetEmailsContent}>重置发送内容</Button>
                                        </div>
                                    </div>
                                    </div>

                            }                            
                            </div>
                        </div>

                    </div>
                </div>
                <Modal
                        visible={visible}
                        title={modalTitle}
                        width={width}
                        onCancel={this.handleCancel}
                        footer={this.footer}
                        >
                        {modalHtml}            
                    </Modal>
                    <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='MS'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
        
    }
}


































