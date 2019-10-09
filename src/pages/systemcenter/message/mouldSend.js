import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import ModalPlate from './modal/modalPlate'
import ModalMatch from './modal/modalMatch'
import {getByteLen,pageLimit } from '../../../utils'
import { Tabs,Button,Input,Radio,Modal,message,Popconfirm } from 'antd';
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP } from '../../../conf/index';
const TabPane = Tabs.TabPane;
const { TextArea } = Input;
const RadioGroup = Radio.Group;

export default class MouldSend extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            value:2,
            visible:false,
            width:"",
            title:"",
            modalHtml:"",
            sendmodecode:"1",
            id:'',
            templatename:"",
            sendareacode:"",
            templatedesc:"",
            textSend:"",
            emailTitle:"",
            limitBtn: [],
            check:'',
            googVisibal:false,
            item:'',
            type:''
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.callback = this.callback.bind(this)
        this.onChangeRadio = this.onChangeRadio.bind(this)
        this.choosePlate = this.choosePlate.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.chooseMatch = this.chooseMatch.bind(this)
        this.chooseMould = this.chooseMould.bind(this)
        this.chooseMatchBtn = this.chooseMatchBtn.bind(this)
        this.onResetCon = this.onResetCon.bind(this)
        this.onResetMould = this.onResetMould.bind(this)
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

    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
  
    //tabs 
    callback(key) {
        this.setState({
            sendmodecode:key,
            sendareacode:3
        })
    }
    //radio change 时候
    onChangeRadio(e){
        console.log('radio checked', e.target.value);
        this.setState({
            sendareacode: e.target.value,
        });
    }
    //modal ok 按钮
    handleOk(){
        this.setState({ loading: true });
        setTimeout(() => {
          this.setState({ loading: false, visible: false });
        }, 3000);
    }
    //modal 取消 按钮
    handleCancel(){
        this.setState({ visible: false });
    }
    //选择带回
    chooseMould(item){
        this.setState({
            id:item.id,
            templatename:item.templatename,
            sendmodecode:item.sendmodecode,
            sendareacode:item.sendareacode,
            templatedesc:item.templatedesc,
            visible:false
        })
    }

    //模版选择
    choosePlate(){
        const { sendmodecode } = this.state
        this.footer = [
                <Button key="back" onClick={this.handleCancel}>取消</Button>,
                <Button key="submit" type="more" onClick={this.handleCancel}>
                   确定
                </Button>,
              ];
        this.setState({
            visible:true,
            title:"模版选择",
            width:"1200px",
            modalHtml:<ModalPlate sendmodecode={sendmodecode} chooseMould={this.chooseMould} />
        })
    }
    //匹配模版
    chooseMatch(){
        const { sendmodecode,id } = this.state
        let title = sendmodecode==1?"手机匹配预览":"邮箱匹配预览"
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={this.handleCancel}>
               确定
            </Button>,
          ];
        this.setState({
            visible:true,
            title:title,
            width:"800px",
            modalHtml:<ModalMatch  sendmodecode={sendmodecode} id={id} />
        })
    }
 
    //google 验证弹窗
    modalGoogleCode(){
        this.setState({
            googVisibal:true,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { googleCode } = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                },()=>this.chooseMatchBtn())
                
            }else{
                message.warning(result.msg)
            }
        })
    }

    //模版保存按钮
    chooseMatchBtn(){
        const { sendareacode,sendmodecode,id,textSend,emailTitle } = this.state
        let url = sendmodecode==1?"/messageSend/tempPhoneSend":"/messageSend/tempEmailSend";
        let newTitle = sendmodecode==1?"":emailTitle
        if(!textSend){
            message.warning("请输入需要发送的内容！");
            return false;
        }
        axios.post(DOMAIN_VIP+url,qs.stringify({
            id,
            msg:textSend,
            title:newTitle
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    textSend:'',
                    templatedesc:'',
                    id:'',
                    templatename:'',
                    emailTitle:""
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    //重置模版
    onResetMould(){
        this.setState({
            id:"",
            templatename:"",
            templatedesc:"",
            sendareacode:"",
            title:""
        })
    }
    //重置发送内容
    onResetCon(){
        this.setState({
            textSend:""
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
        const { value,sendmodecode,visible,width,title,emailTitle,modalHtml,templatedesc,sendareacode,textSend,limitBtn } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 消息管理 > 模版发送
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <Tabs activeKey={`${sendmodecode}`} onChange={this.callback}>
                                <TabPane tab="短信" key="1"></TabPane>
                                <TabPane tab="邮件" key="2"></TabPane>
                            </Tabs>
                            <div className="x_content">
                                <div className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="x_panel">
                                        <div className="x_title">
                                            <h5>选择需要发送的规则模板</h5>
                                        </div>
                                        <div className="x_content" style={{height:"256px"}}>
                                            <div className="col-md-12 col-sm-12 col-xs-12">
                                                <div className="form-group heiAuto">
                                                    <label className="col-sm-3 control-label">模版选择：</label>
                                                    <div className="col-sm-8">
                                                        <TextArea name="templatedesc" value={templatedesc} onChange={this.handleInputChange}  />
                                                    </div>
                                                    <label className="col-sm-3 control-label" style={{opacity:0}}>模版选择：</label>
                                                    <div className="col-sm-12 martop10">
                                                        <div className="right">
                                                            <Button type="more" onClick={this.choosePlate}>模版选择</Button>
                                                            <Button type="more" onClick={this.chooseMatch}>匹配预览</Button> 
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="col-md-12 col-sm-12 col-xs-12">
                                            <div className="form-group martop10">
                                                <label className="col-sm-3 control-label">发送区域：</label>
                                                <div className="col-sm-8">
                                                    {
                                                        sendmodecode==1? <RadioGroup onChange={this.onChangeRadio} name="emailRadio" value={sendareacode} disabled>
                                                                    <Radio value={1}>国内</Radio>
                                                                    <Radio value={2}>国外</Radio>
                                                                    <Radio value={4}>港澳台</Radio>                                                                                                                            
                                                                </RadioGroup>
                                                                :
                                                                <RadioGroup value={3}>
                                                                    <Radio value={3}>全球</Radio>
                                                                </RadioGroup>
                                                    }
                                                </div>
                                            </div>
                                            </div>
                                            <div className="col-md-12 col-sm-12 col-xs-12">
                                            {sendmodecode==2&&<div className="form-group">
                                                <label className="col-sm-3 control-label">邮件标题：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control"  name="emailTitle" value={emailTitle} onChange={this.handleInputChange} />
                                                </div>
                                            </div>}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="x_panel">
                                        <div className="x_title">
                                            <h5>输入需要发送的内容</h5>
                                        </div>
                                        <div className="x_content" style={{height:"256px"}}>
                                            <TextArea className="textSend" placeholder="短信每67个字符（1个中文为2个字符）计费1条。最多不超过280字符。" name="textSend" value={textSend} onKeyUp={(e) => this.checkLength(e)} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right martop30">
                                     <div className="right">
                                          <Popconfirm placement="top" onConfirm={() => this.modalGoogleCode()} okText="Yes" cancelText="No"
                                           title={(()=>{
                                                switch (sendareacode) {
                                                    case 1:
                                                        return '请输入简体中文'
                                                        break;
                                                    case 2,3:
                                                        return '请输入英文'
                                                        break;
                                                    case 4:
                                                        return '请输入繁体中文'
                                                        break;
                                                    default:
                                                        return '确认发送？'
                                                        break;
                                                }
                                            })()}>
                                            {(limitBtn.indexOf('tempPhoneSend')>-1||limitBtn.indexOf('tempEmailSend')>-1)?<Button type="more" onClick={this.phoneJudge}>发送</Button>:''}
                                          </Popconfirm>
                                          <Button type="more" onClick={this.onResetMould}>重置模版</Button> 
                                          <Button type="more" onClick={this.onResetCon}>重置发送内容</Button>           
                                     </div>               
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
                { modalHtml }
              </Modal>
              <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='MOS'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }
}





















