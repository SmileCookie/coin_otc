
import React from 'react';
import { connect } from 'react-redux';
import PhoneCode from '../../../components/phonecode'
import { sendModifyCode,setpOne,sendNewCode,ChangeImgCode,fetchMobile,saveModifyMobile } from '../../../redux/modules/userInfo'
import { DISMISS_TIME,COUNTDOWN_INTERVAL,COUNT_DOWN_ONE_MINUTE,MOBILE_IMG_CODE,MOBILE_COUNTRY_CODE, MOBILE_MODIFY_CODETYPE, REDIRECT } from '../../../conf'
import { Link,browserHistory } from 'react-router'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import { sendSafePwdCode } from '../../../redux/modules/userInfo'
import { FormattedMessage, injectIntl } from 'react-intl';
const { notifSend } = notifActions;
const BigNumber = require('big.js')


class ModifyMobile extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            mobileCode:"",
            mobile:"",
            mCode:MOBILE_COUNTRY_CODE,
            code:"",
            newMobileCode:"",
            safePwd:"",
            googleCode:"",
            step:1,
            smsCodeCount:"",
            oneMinute:60,
            sendBtn:true,
            steps:0
        }
        this.intl = props.intl
        this.handleInputChange = this.handleInputChange.bind(this)
        this.sendSmsCode = this.sendSmsCode.bind(this)
        this.sendNewCode = this.sendNewCode.bind(this)
        this.doStepOne = this.doStepOne.bind(this)
        this.handleModifySubmit = this.handleModifySubmit.bind(this)
        this.setSmsInterval = this.setSmsInterval.bind(this)
        this.changeCountryCode = this.changeCountryCode.bind(this)
        this.changeMobileImg = this.changeMobileImg.bind(this)
        this.settime = this.settime.bind(this)
        this.skipStepOne = this.skipStepOne.bind(this)
    }

    skipStepOne(){
        this.setState({
            steps:2
        })
    }

    settime(){
        this.props.sendSafePwdCode((res) => {
            console.log(res)
            let result = res.data;
            this.props.notifSend({
                message: result.des,
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });

            if(result.isSuc){
                if(this.state.sendBtn){
                    this.setState({sendBtn:false})
                    let timer = setInterval(() => {
                        this.setState((prevState) => ({
                            oneMinute: prevState.oneMinute - 1
                          }))
                        if(this.state.oneMinute == 0){
                            clearInterval(timer);
                            this.setState({sendBtn : true,oneMinute : 60 });
                        }
                    },1000)
                }
                
            } 
        });
    }

    componentDidMount(){
        this.props.fetchMobile()
    }

    componentWillUnmount(){
        clearInterval(this.smsInterval)
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
    //获取手机号 国家号
    changeCountryCode(code){
        this.setState({
            mCode:code
        })
    }
    //定时器
    setSmsInterval(bool){
        if(bool){
            BigNumber.RM=2;      
            let clickTime = new Date().getTime();
            console.log(1)
            this.smsInterval = setInterval(()=>{
                console.log((new Date().getTime() - clickTime))
                let diffTime =  new BigNumber((new Date().getTime() - clickTime)/1000).toFixed(0)
                console.log(diffTime)
                if(COUNT_DOWN_ONE_MINUTE - diffTime > 0){
                    this.setState({
                        smsCodeCount:COUNT_DOWN_ONE_MINUTE-diffTime
                    })
                }else{
                    this.setState({
                        smsCodeCount:0
                    })
                    clearInterval(this.smsInterval)
                }
            },COUNTDOWN_INTERVAL)
        }  
    }


    //短信验证码
    sendSmsCode(){
        this.props.sendModifyCode((res) => {
            const result = res.data;
            this.props.notifSend({
                message: result.des,
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            this.setSmsInterval(res.isSuc)
        })
    }

    //新手机短信验证码
    sendNewCode(){
        const { mobile,code,mCode } = this.state

        if(!mobile){
            this.props.notifSend({
                message:this.intl.formatMessage({id:'mobile.text11'}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            return
        }
        if(!code){
            this.props.notifSend({
                message:this.intl.formatMessage({id:'mobile.text12'}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            return
        }

        this.props.sendNewCode({
            code : code,
			mCode : mCode,
			mobile : mobile,
        },(res) => {
            const result = res.data;
            this.props.notifSend({
                message: result.des,
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            if(result.isSuc){
                this.setSmsInterval(result.isSuc);                
            }else{
                this.changeMobileImg();
            }
        })
    }

    //第一步
    doStepOne(){
        const mobileCode = this.state.mobileCode
        this.props.setpOne(mobileCode,(res)=>{
            const result = res.data
            console.log(result)
            if(result.isSuc){
                this.setState({
                    step:2,
                    smsCodeCount:0
                })
            }else{
                this.props.notifSend({
                    message: result.des,
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
            }
        })
    }
    //图片验证码
    changeMobileImg(){
        let date = new Date().getTime();
        this.props.ChangeImgCode(MOBILE_IMG_CODE+"-"+date);
    }
    checkForm(){
        let rt = 1;

        if(!this.state.safePwd){
            this.props.notifSend({
                message: this.intl.formatMessage({id: "withdraw.text45"}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            rt = 0;
        }

        return rt;
    }
    handleModifySubmit(){
       if(this.checkForm()){
            console.log("提交")
            const {mCode,mobile,newMobileCode,safePwd,googleCode,step} = this.state
            
            this.props.saveModifyMobile({
                    countryCode : mCode,
                    newMobileNumber : mobile,
                    newMobileCode : newMobileCode,
                    safePwd : safePwd,
                    googleCode:googleCode,
                    method : 2
            },(res) => {
                    const result = res.data
                    console.log(result)
                    if(result.isSuc){
                        browserHistory.push("/bw/manage/user/mobile")
                    }else{
                        console.log(result);
                        this.props.notifSend({
                            message: result.des,
                            kind: 'info',
                            dismissAfter: DISMISS_TIME
                        });
                        setTimeout(() => {
                        document.getElementsByClassName("notif__message")[0].innerHTML = document.getElementsByClassName("notif__message")[0].innerText.replace(/(href=\').*?(\')/, "$1"+REDIRECT.MODIFY_MONEY+"$2");
                        }, 0);
                        
                    }

            })
        }
    }

    render(){
        const intl = this.intl;
        const {data,imgCode,isloaded,isloading} = this.props.mobile
        return (
            <div className="cont-row">
                <div className="bk-top">
                    <h2><FormattedMessage id="mobile.text4" /></h2>
                </div>
                <div className="vip-tip clearfix">
                    <dl>
                        <dt className="clear_af"><FormattedMessage id="user.text117" /></dt>
                        <dd><FormattedMessage id="mobile.text5" /></dd>
                    </dl>
                </div>
                {
                    this.state.step == 1 && this.state.steps == 0? (
                        <div className="fill-form">
                            <input type="hidden" value={this.state.steps} />
                            <div className="fill-form-bd">
                                <div className="fill-group">
                                    <em className="name"><FormattedMessage id="mobile.text6" /></em>
                                    <input type="text" className="fill-control" readOnly value={data.phonenum} />
                                </div>
                                <div className="fill-group">
                                    <em className="name"><FormattedMessage id="mobile.text7" /></em>
                                    <div className="fill-flex">
                                        <input type="text" className="fill-control" name="mobileCode" onChange={this.handleInputChange} />
                                        <button type="button" onClick={this.settime} disabled={this.state.sendBtn?"":"disabled"} className="btn btn-sms">{this.state.sendBtn? intl.formatMessage({id: "user.text19"}):`${intl.formatMessage({id: "user.text108"})}${this.state.oneMinute}`}</button>
                                    </div>
                                </div>
                                <div className="fill-group">
                                    <em className="name"></em>
                                    <button className="btn btn-submits" onClick={this.doStepOne} ><FormattedMessage id="user.text122" /></button>
                                    <a className="ml10" href="javascript:void(0)" onClick={this.skipStepOne}><FormattedMessage id="mobile.text8" /></a>
                                </div>
                            </div>
                        </div>
                    ) : (
                        <div className="fill-form">
                            <div className="fill-form-bd">
                                <div className="fill-group">
                                    <em className="name"><FormattedMessage id="mobile.text9" /></em>
                                    <input type="text" className="fill-control input-phone" name="mobile" value={this.state.mobile} onChange={this.handleInputChange} />     
                                    <PhoneCode obrainCountryCode={this.changeCountryCode} />                 
                                </div> 
                                
                                <div className="fill-group">
                                    <em className="name"><FormattedMessage id="email.text10" /></em>
                                    <div className="fill-flex">
                                        <input type="text" className="fill-control" name="code" value={this.state.code} onChange={this.handleInputChange} />                            
                                        <img src={imgCode} onClick={this.changeMobileImg} />
                                    </div>
                                </div>
        
                                <div className="fill-group">
                                    <em className="name"><FormattedMessage id="mobile.text10" /></em>
                                    <div className="fill-flex">
                                        <input type="text" className="fill-control" name="newMobileCode" value={this.state.newMobileCode} onChange={this.handleInputChange} />                            
                                        <button type="button" className="btn btn-sms" onClick={this.sendNewCode} disabled={this.state.smsCodeCount? "disabled" :""} >{this.state.smsCodeCount? `${intl.formatMessage({id: "user.text108"})}${this.state.smsCodeCount}`: <FormattedMessage id="user.text19" />}</button>
                                    </div>
                                </div>
                                
                                <div className="fill-group">
                                    <em className="name"><FormattedMessage id="user.text17" /></em>
                                    <input type="password" className="fill-control" name="safePwd" value={this.state.safePwd} onChange={this.handleInputChange} />                            
                                </div>
                                {
                                    data.googleAuth ==2 ?(
                                        <div className="fill-group">
                                            <em className="name"><FormattedMessage id="withdraw.text30" /></em>
                                            <input type="text" className="fill-control" name="googleCode" value={this.state.googleCode} onChange={this.handleInputChange} />                            
                                        </div>
                                    ):""
                                }
                                <div className="fill-group">
                                    <em className="name"></em>
                                    <button type="submit" className="btn btn-submits" onClick={this.handleModifySubmit} ><FormattedMessage id="user.text20" /></button>
                                    {
                                    false &&
                                    <a className="ml10" href="/ac/safepwd_find" target="_blank"><FormattedMessage id="user.text115" /></a>  
                                    }                          
                                </div>
        
                            </div>
                        </div>
                    )
                }
            </div>
        )
    }   
}


const mapStateToProps = (state, ownProps) => ({
    mobile : state.userInfo.mobile
})

const mapDispatchToProps = (dispatch) => {
    return{
        sendSafePwdCode:(cb) => {
            dispatch(sendSafePwdCode(MOBILE_MODIFY_CODETYPE)).then(cb)
        },
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        fetchMobile: () => {
            dispatch(fetchMobile())
        },
        sendModifyCode:(cb) => {
            dispatch(sendModifyCode()).then(cb)
        },
        setpOne: (param,cb) => {
            dispatch(setpOne(param)).then(cb)
        },
        sendNewCode: (params,cb) => {
            dispatch(sendNewCode(params)).then(cb)
        },
        ChangeImgCode: (param) => {
            dispatch(ChangeImgCode(param))
        },
        saveModifyMobile:(params,cb) => {
            dispatch(saveModifyMobile(params)).then(cb)
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(ModifyMobile))















































