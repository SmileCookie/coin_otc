import React from 'react';
import { connect } from 'react-redux'
import { Link } from 'react-router'
import { FormattedMessage, injectIntl } from 'react-intl';
import { sendCode,fetchMailbox,fetchMailInfo } from '../../../redux/modules/userInfo'
import { COIN_KEEP_POINT,DISMISS_TIME} from '../../../conf'
import { isEmail, formatURL } from '../../../utils'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend } = notifActions;

class Email extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            sendBtn:true,
            oneMinute:60,
            email:'',
            payPwd:'',
            mobileCode:''
        }
        this.settime = this.settime.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.sendemail = this.sendemail.bind(this)
        this.intl = this.props.intl
    }

    componentDidMount() {
        this.props.fetchMailInfo()
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

    //发送短信验证码
    settime(){
        this.props.sendCode((res) => {
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

    //提交
    sendemail(key){
        const {email,payPwd,mobileCode} = this.state
        console.log(isEmail(email))
        if(key != "repost" && !email){
            this.props.notifSend({
                message: this.intl.formatMessage({id:'email.text16'}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            return;
        }
        if(key != "repost" && email){
            if(!isEmail(email)){
                this.props.notifSend({
                    message: this.intl.formatMessage({id:'email.text16'}),
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
                return;
            }
        }
        if (key != "repost" && !payPwd){
            this.props.notifSend({
                message: this.intl.formatMessage({id:'user.text119'}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            return;
        }
        if (key != "repost") {
           if (!mobileCode) {
                this.props.notifSend({
                    message: this.intl.formatMessage({id:'user.text120'}),
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
                return false;
            }
        }
        let step = '';
        if(key){
            step = key
        }
        this.props.fetchMailbox({
            step:key,
            email:this.state.email,
            payPwd:this.state.payPwd,
            mobileCode:this.state.mobileCode
        },(res) => {
            console.log(res.data)
            let result = res.data
            this.props.notifSend({
                message: result.des,
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            setTimeout(() => {
                document.getElementsByClassName("notif__message")[0].innerHTML = document.getElementsByClassName("notif__message")[0].innerText;
            }, 0);
            if(result.isSuc){
                setTimeout(()=>{
                    this.props.router.push(formatURL('/manage/user'))
                }, DISMISS_TIME)
            }

        })

    }   
        

    render(){
        const { isloading,isloaded,safeAuth,emailStatu,step,email,mobileStatu,source='116710782@qq.com',isError } = this.props.emailInfo;
        const intl = this.props.intl;
        return (
            <div className="cont-row">
                <div className="bk-top mb0">
                    <h2><FormattedMessage id="user.text22" /></h2>
                </div>
                {
                    safeAuth && emailStatu != 2 ? (
                        <div className="vip-tip clearfix">
                            <dl>
                                <dt className="clear_af"><FormattedMessage id="user.text116" /></dt>
                                <dd>
                                    <FormattedMessage id="user.text14" />
                                    <Link className="c0" to={formatURL('/manage/user/safePwd')}><strong><FormattedMessage id="user.text15" /></strong></Link>
                                </dd>
                            </dl>
                        </div>
                    ) : ""
                }
                {
                    emailStatu == 2 ? (
                        <div className="vip-tip clearfix">
                            <dl>
                                <dt className="clear_af"><FormattedMessage id="user.text117" /></dt>
                                <dd>
                                    <FormattedMessage id="email.text3" /><span className="notice">{email}</span>
                                </dd>
                            </dl>
                        </div>
                    ) : (
                        ""
                    )
                }

                {
                    emailStatu != 2 ? (
                        step == 'one' ? (
                
                            <div className="fill-form">
                                <div className="fill-form-bd">
                                    <div className="fill-group">
                                        <em className="name"><FormattedMessage id="email.text1" /></em>
                                        <input type="text" className="fill-control" value={email} readOnly={true} />
                                    </div>
                                    <div className="fill-group">
                                        <em className="name"><FormattedMessage id="withdraw.text6" /></em>
                                        <input type="password" className="fill-control" />
                                    </div>
                                    <div className="fill-group">
                                        <em className="name"></em>
                                        <input type="button" className="btn btn-set" value={intl.formatMessage({id:'user.text20'})} />
                                        {
                                        false
                                        &&
                                        <Link href="/ac/safepwd_find" target="_blank" className="mbr15 c0"><FormattedMessage id="user.text115" /></Link>
                                        }
                                    </div>
                                </div>
                            </div>

                        ) : (step == 'next' ? (

                                <div className="vip-tip clearfix">
                                    <dl>
                                        <dt className="clear_af"><FormattedMessage id="user.text117" /></dt>
                                        <dd>
                                            <span>{email}</span> <FormattedMessage id="email.text11" />
                                            <a className="c0" target="_blank" href={`http://mail.${source.split("@")[1]}`}> <FormattedMessage id="email.text12" /></a>
                                        </dd>
                                        <dd><FormattedMessage id="email.text13" /><input type="button" className="hq btn btn-primary btn-sm wid80 ml20 bornone" onClick={()=>{this.sendemail('repost')}} value={intl.formatMessage({id:'email.text14'})} /></dd>
                                    </dl>
                                </div>

                        ) : (
                            <div>
                                <input type="text" className="autoComplete" />
                                <input type="password" className="autoComplete" />
                                <div className="fill-form">
                                    <div className="fill-group">
                                        <em className="name">{step=="third" ? intl.formatMessage({id:'user.text118'}) : '' }<FormattedMessage id="user.text16" /></em>
                                        <input className="fill-control" type="text" name="email" autoComplete="off" onChange={this.handleInputChange} />
                                    </div>
                                    <div className="fill-group">
                                        <em className="name"><FormattedMessage id="user.text17" /></em>
                                        <input className="fill-control" type="password" name="payPwd" autoComplete="off" onChange={this.handleInputChange} />
                                    </div>
                                    <div className={`fill-group ${mobileStatu != 2 ? 'hide':''} `}>
                                        <em className="name">{mobileStatu == 2 ? intl.formatMessage({id:'user.text18'}) : intl.formatMessage({id:'withdraw.text19'})}</em>
                                        <div className="fill-flex">
                                            <input className="fill-control" type="text" name="mobileCode" autoComplete="off" onChange={this.handleInputChange} />
                                            <button onClick={this.settime} disabled={this.state.sendBtn?"":"disabled"} className="btn btn-sms">{this.state.sendBtn? intl.formatMessage({id:'user.text19'}):`${intl.formatMessage({id:'user.text108'})}${this.state.oneMinute}`}</button>
                                        </div>
                                    </div>
                                    <div className="fill-group">
                                        <em className="name"></em>
                                        <button className="btn btn-submit-sm" onClick={() => this.sendemail("hassend")}><FormattedMessage id="user.text20" /></button>
                                        {
                                        false
                                        &&
                                        <Link href="/ac/safepwd_find" className="ml10" target="_blank"> <FormattedMessage id="user.text21" /></Link>
                                        }
                                    </div>
                                </div>
                            </div>
                        ))
                    ) : ""
                }






            </div>
        )
    }
}



const mapStateToProps = (state, ownProps) => {
    return {
        emailInfo:state.userInfo.email
    }
}

const mapDispatchToProps = (dispatch) =>{
    return {
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        sendCode: (cb) => {
            dispatch(sendCode()).then(cb)
        },
        fetchMailbox: (params,cb) => {
            dispatch(fetchMailbox(params)).then(cb)
        },
        fetchMailInfo: () => {
            dispatch(fetchMailInfo())
        }

    }
}

export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(Email))








