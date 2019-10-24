import React from 'react';
import { connect } from 'react-redux'
import { Link,browserHistory } from 'react-router'
import PhoneCode from '../../../components/phonecode'
import { fetchMobile ,ChangeImgCode,submitMobileAuth,sendMobileCode,sendMobileECode, sendNewCode, sendSafePwdCode} from '../../../redux/modules/userInfo'
import { MOBILE_AUTH_CODETYPE,DISMISS_TIME,MOBILE_IMG_CODE,COUNTDOWN_INTERVAL,COUNT_DOWN_ONE_MINUTE,MOBILE_COUNTRY_CODE, SAFEAUTH} from '../../../conf'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import { FormattedMessage, injectIntl } from 'react-intl';
import { formatDate, mobileFormat, formatURL, languageFormat } from '../../../utils';

const { notifSend } = notifActions;
const BigNumber = require('big.js')

class Mobile extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            mobile:"",
            mCode:MOBILE_COUNTRY_CODE,
            code:"",
            mobileCode:"",
            emailCode:"",
            googleCode:"",
            smsCodeCount:60,
            mailCodeCount:60,
            sendBtn:true,
            sendBtn2:true,            
        }

        this.intl = props.intl
        this.changeMobileImg = this.changeMobileImg.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.sendSmsCode = this.sendSmsCode.bind(this)
        this.sendMailCode = this.sendMailCode.bind(this)
        this.changeCountryCode = this.changeCountryCode.bind(this)
        this.handleSubmit = this.handleSubmit.bind(this)
    }

    componentDidMount(){
        this.props.doFetchMobile()
    }

    componentWillUnmount(){
        clearInterval(this.smsInterval)
        clearInterval(this.mailInterval)
    }

    changeMobileImg(){
        let date = new Date().getTime();
        this.props.ChangeImgCode(MOBILE_IMG_CODE+"-"+date);
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

    //短信验证码
    sendSmsCode(){

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
            cid: 2
        },(res) => {
            const result = res.data;
            this.props.notifSend({
                message: result.des,
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            if(result.isSuc){
                if(this.state.sendBtn){
                    this.setState({sendBtn:false})
                }
                this.setSmsInterval(result.isSuc);                
            }else{
                this.changeMobileImg();
            }
        })
    }
    setSmsInterval(){
        BigNumber.RM=2;                
        let clickTime = new Date().getTime();
        this.smsInterval = setInterval(()=>{
            let diffTime =  new BigNumber((new Date().getTime() - clickTime)/1000).toFixed(0)
            if(COUNT_DOWN_ONE_MINUTE - diffTime > 0){
                this.setState({
                    smsCodeCount:COUNT_DOWN_ONE_MINUTE-diffTime
                })
            }else{
                this.setState({
                    smsCodeCount:0,
                    sendBtn: true
                })
                clearInterval(this.smsInterval)
            }
            
        },COUNTDOWN_INTERVAL)
    }
    //邮箱验证码
    sendMailCode(){
        

        this.props.sendSafePwdCode((res) => {
            console.log(res)
            let result = res.data;
            this.props.notifSend({
                message: result.des,
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });

            if(result.isSuc){
                if(this.state.sendBtn2){
                    this.setState({sendBtn2:false})
                    this.mailCode();
                }
                
            } 
        });


    }

    mailCode(){
        BigNumber.RM=2;
        let clickTime = new Date().getTime();
        console.log(1)
        this.mailInterval = setInterval(()=>{
            let diffTime =  new BigNumber((new Date().getTime() - clickTime)/1000).toFixed(0)
            console.log(diffTime)
            if(COUNT_DOWN_ONE_MINUTE - diffTime > 0){
                this.setState({
                    mailCodeCount:COUNT_DOWN_ONE_MINUTE-diffTime
                })
            }else{
                this.setState({
                    mailCodeCount:0,
                    sendBtn2:true
                })
                clearInterval(this.mailInterval)
            }
        },COUNTDOWN_INTERVAL)
    }

    handleSubmit(e){
        e.preventDefault();
        const { mobile,mCode,code,mobileCode,emailCode,googleCode,codeType } = this.state
        this.props.doSubmitMobileAuth({
            mobile:mobile,
            mCode:mCode,
            code:code,
            mobileCode:mobileCode,
            emailCode:emailCode,
            googleCode:googleCode
        })
    }


    render(){
        const { isloading,isloaded,imgCode,data } = this.props.mobile;
        const intl = this.props.intl;
        const lg = this.props.language.locale;
        const format = lg == 'en' ? 'MM-dd-yyyy hh:mm:ss':'yyyy-MM-dd hh:mm:ss';
        const phonenum = data.phonenum;
        const lgUrl = languageFormat(lg);

        return(
            isloading && !isloaded ? (
                <div><FormattedMessage id="user.text101" /></div>
            ) : (
                <div className="cont-row">
                    <div className="bk-top">
                        <h2><FormattedMessage id="email.text2" /></h2>
                    </div>
                    {
                        data.verifyUserInfo.status == 0 ? (
                            <div className="vip-tip clearfix">
                                <dl>
                                    <dt className="clear_af"><FormattedMessage id="user.text117" /></dt>
                                    <dd><p className="mb20">{intl.formatMessage({id: "mobile.text1"}).replace('%%', formatDate(data.verifyUserInfo.addTimeShow, format))}</p><a href={`https://btcwinex.zendesk.com/hc/${lgUrl}/requests/new`} target="_blank" className="btn btn-set wid150"><FormattedMessage id="user.text121" /></a></dd>
                                </dl>
                            </div>
                        ) 
                        : 
                       false && data.mobileStatu == 1 ? 
                        (
                            <div className="vip-tip clearfix">
                                <dl>
                                    <dt className="clear_af"><FormattedMessage id="user.text117" /></dt>
                                    <dd className="ktp">
                                        {data.verifyUserInfo.status==2 ? (<FormattedMessage id="mobile.text3" />) : ""}
                                        {data.verifyUserInfo.status==1 ? (<FormattedMessage id="mobile.text2" />) : ""}
                                    </dd>
                                </dl>
                            </div>
                        )
                        :
                        data.mobileStatu == 2 ? (
                            <div className="vip-tip clearfix">
                                <dl>
                                    <dt className="clear_af"><FormattedMessage id="user.text117" /></dt>
                                    <dd className="ktp">
                                        {data.verifyUserInfo.status==-1 ? (<span><FormattedMessage id="email.text5" /><span className="text-primary">{phonenum}</span> </span>) : ""}
                                        {data.verifyUserInfo.status==2 ? (<span><FormattedMessage id="email.text6" /><span className="text-primary">{phonenum}</span></span>) : ""}
                                        {data.verifyUserInfo.status==1 ? (<span><FormattedMessage id="email.text7" /><span className="text-primary">{phonenum}</span></span>) : ""}
                                        {data.verifyUserInfo.status==3 ? (<span><FormattedMessage id="email.text8" /><span className="text-primary">{phonenum}</span></span>) : ""}
                                        <Link className="btn btn-primary btn-sm ml15 wid80"	to={formatURL("/manage/user/mobileModify")}><FormattedMessage id="user.text11" /></Link>
                                    </dd>
                                </dl>
                            </div>
                        ) : (
                            <div className="fill-form">
                                <div className="fill-form-bd">
                                    <div className="fill-group">
                                        <em className="name"><FormattedMessage id="email.text9" /></em>
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
                                        <em className="name"><FormattedMessage id="user.text18" /></em>
                                        <div className="fill-flex">
                                            <input type="text" className="fill-control" name="mobileCode" value={this.state.mobileCode} onChange={this.handleInputChange} />                            
                                            <button className="btn btn-sms" disabled={this.state.sendBtn?"":"disabled"}  onClick={this.sendSmsCode}>{this.state.sendBtn?  intl.formatMessage({id: "user.text19"}):`${intl.formatMessage({id: "user.text108"})}${this.state.smsCodeCount}`    }</button>
                                        </div>
                                    </div>
        
                                    <div className="fill-group">
                                        <em className="name"><FormattedMessage id="email.text15" /></em>
                                        <div className="fill-flex">
                                            <input type="text" className="fill-control" name="emailCode" value={this.state.emailCode} onChange={this.handleInputChange} />                            
                                            <button className="btn btn-sms" disabled={this.state.sendBtn2?"":"disabled"} onClick={this.sendMailCode} >{this.state.sendBtn2? intl.formatMessage({id: "user.text19"}):`${intl.formatMessage({id: "user.text108"})}${this.state.mailCodeCount
                                            }` }</button>
                                        </div>
                                    </div>

                                    {
                                        data.googleAuth == 2?(
                                            <div className="fill-group">
                                                <em className="name"><FormattedMessage id="withdraw.text30" /></em>
                                                <input type="text" className="fill-control" name="googleCode" value={this.state.googleCode} onChange={this.handleInputChange} />                            
                                            </div>
                                        ) : ""
                                    }

                                    <div className="fill-group">
                                        <em className="name"></em>
                                        <button type="submit" className="btn btn-submit" onClick={this.handleSubmit}><FormattedMessage id="user.text20" /></button>                            
                                    </div>

                                </div>
                            </div>
                        )
                    }
                </div>
            )
        )
    }
}


const mapStateToProps = (state, ownProps) => ({
     mobile : state.userInfo.mobile,
     language: state.language
})

const mapDispatchToProps = (dispatch) => {
    return{
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        sendNewCode: (params,cb) => {
            dispatch(sendNewCode(params)).then(cb)
        },
        doFetchMobile: (params) => {
            dispatch(fetchMobile(params))
        },
        sendSafePwdCode:(cb) => {
            dispatch(sendSafePwdCode(SAFEAUTH)).then(cb)
        },
        ChangeImgCode: (params) => {
            dispatch(ChangeImgCode(params))
        },
        sendMobileCode: (params) => {
            dispatch(sendMobileCode(params))
        },
        sendMobileECode: (params) => {
            dispatch(sendMobileECode(params))
        },
        doSubmitMobileAuth: (params) => {
            dispatch(submitMobileAuth(params)).then(res => {
                const result = res.data
                console.log(result)
                dispatch(notifSend({
                    message: result.des,
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                }))
                if(result.isSuc){
                    window.location.reload(true);
                }
            })
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(Mobile))

















































