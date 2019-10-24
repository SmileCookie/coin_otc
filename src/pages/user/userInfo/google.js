import React from 'react';
import { connect } from 'react-redux'
import { fetchGoogle,ModifyGoogle,submitGoogle,sendGoogleCode} from '../../../redux/modules/userInfo'
import { COIN_KEEP_POINT,DISMISS_TIME} from '../../../conf'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Link } from 'react-router';
import { formatURL,formatDate } from '../../../utils';
import { languageFormat } from '../../../utils'

const { notifSend } = notifActions;

class Google extends React.Component{
    
    constructor(props){
        super(props)
        this.state = {
            googleAuthInfo:{
                verifyUserInfo:{
                    status:-1
                }
            },
            gCode:"",
            mobileCode:"",
            sendBtn:true,
            oneMinute:60,
        }
        this.settime = this.settime.bind(this)
        this.handleSubmit = this.handleSubmit.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
    }

    componentDidMount(){
        this.props.fetchGoogle();
    }

    componentWillReceiveProps(nextProps) {
        if(nextProps.googleInfo.isloaded) {
            this.setState({
                googleAuthInfo: nextProps.googleInfo.data
            })
        }
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
        this.props.sendGoogleCode((res) => {
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
    handleSubmit(){
        const { gCode,mobileCode } = this.state

        this.props.doSubmitGoogle({
            secret : this.state.googleAuthInfo.secret,
            gCode : gCode,
            mobileCode : mobileCode
        }, this.props)


    }

    render(){
        const {isloading,isloaded} = this.props.googleInfo;
        const lg = this.props.language.locale;
        const format = lg == 'en' ? 'MM-dd-yyyy hh:mm:ss':'yyyy-MM-dd hh:mm:ss';
        const intl = this.props.intl;
        const lgUrl = languageFormat(lg);
      
        // console.log(this.state.googleAuthInfo.verifyUserInfo.addTimeShow)
        return (
            isloading && !isloaded ? (
                <div className="loading"><FormattedMessage id="user.text101" /></div>
            ) : (
                <div className="cont-row">
                    <div className="bk-top mb0">
                        <h2><FormattedMessage id="google.text1" /></h2>
                    </div>
                    {
                    false ?(
                    <div className="vip-tip clearfix">
                            <dl>
                                <dt className="clear_af"><FormattedMessage id="user.text117" /></dt>
                                <dd className="ktp">
                                    <FormattedMessage id="google.text10" /><br />
                                    <FormattedMessage id="google.text11" />
                                </dd>
                            </dl>
                    </div>):''
                    }
                    {
                        this.state.googleAuthInfo.googleAuth == 2 && this.state.googleAuthInfo.method == ''?(
                         <div className="vip-tip clearfix">
                            <dl>
                                <dt className="clear_af"><FormattedMessage id="user.text102" /></dt>
                                <dd>
                                    {   
                                        this.state.googleAuthInfo.verifyUserInfo.status == 0 ?
                                        (<div><p className="mb20">{intl.formatMessage({id: "google.text2"}).replace('%%', formatDate(this.state.googleAuthInfo.verifyUserInfo.addTimeShow, format))}</p><a href={`https://btcwinex.zendesk.com/hc/${lgUrl}/requests/new`} target="_blank" className="btn btn-set wid150"><FormattedMessage id="user.text121" /></a></div>) :
                                        this.state.googleAuthInfo.verifyUserInfo.status== 2 ?
                                        <FormattedMessage id="google.text3" /> :
                                        this.state.googleAuthInfo.verifyUserInfo.status == 1 ?
                                        <FormattedMessage id="google.text4" /> :
                                        this.state.googleAuthInfo.verifyUserInfo.status == 3 ?
                                        <FormattedMessage id="google.text5" /> : null
                                    }
                                    {   this.state.googleAuthInfo.verifyUserInfo.status != 0
                                        &&
                                        <span>
                                            <FormattedMessage id="google.text6" />
                                            <button type="button" className="btn btn-primary ml15 w80" onClick={this.props.ModifyGoogle}><FormattedMessage id="user.text11" /></button>
                                            <a className={`btn btn-set ml15  w80 ${this.state.googleAuthInfo.mobileStatu!=2?'hide':'' }`} href={formatURL('/manage/user/googleClose')}><FormattedMessage id="user.text103" /></a>
                                        </span>
                                    }
                                </dd>
                            </dl>
                         </div>
                        ):("")}
                        {
                            this.state.googleAuthInfo.method ==1 || this.state.googleAuthInfo.googleAuth != 2?(
                               this.state.googleAuthInfo.verifyUserInfo.status == 0? (
                                  <div className="vip-tip clearfix">
                                    <dl>
                                        <dt className="clear_af"><FormattedMessage id="user.text102" /></dt>
                                        <dd>{intl.formatMessage({id: "google.text2"}).replace('%%', formatDate(this.state.googleAuthInfo.verifyUserInfo.addTimeShow, format))}</dd>
                                    </dl>
                                  </div>
                                ) : (
                                    this.state.googleAuthInfo.verifyUserInfo == null || this.state.googleAuthInfo.verifyUserInfo.status !== 0?(
                                     <div>
                                      <div className="vip-tip clearfix">
                                        <dl className="tip-google sp">
                                            <dt className="img-google"><img src={`/manage/getGoogleAuthQr?secret=${this.state.googleAuthInfo.secret}`} className=""/></dt>
                                            <dd>1.<FormattedMessage id="google.text8" /></dd>
                                            <dd>2.<FormattedMessage id="user.text104" /><span className="notice">{this.state.googleAuthInfo.secret}</span></dd>
                                            <dd>3.<FormattedMessage id="google.text9" /></dd>
                                            <dd>4.<FormattedMessage id="user.text105" /></dd>
                                            <dd>5.<FormattedMessage id="user.text106" /><a className="google_text_a" href='/login/zendesk/?viewFlag=googleauth' target="_blank"><FormattedMessage id="footer.text12" /></a><FormattedMessage id="user.text111" /></dd>
                                        </dl>
                                      </div>
                                      <div className="fill-from">
                                            <div className="fill-group">
                                                <em className="name">{intl.formatMessage({id: "user.text107"})}</em>
                                                <input type="text" className="fill-control bg-gray"  name="secret" value={this.state.googleAuthInfo.secret} readOnly />
                                            </div>
                                            
                                            <div className="fill-group">
                                                <em className="name">{intl.formatMessage({id: "withdraw.text30"})}</em>
                                                <input type="text" className="fill-control"  name="gCode" value={this.state.gCode} onChange={this.handleInputChange} />
                                            </div>
                    
                                            <div className="fill-group">
                                                <em className="name">{this.state.googleAuthInfo.mobileStatu==2?intl.formatMessage({id: "withdraw.text7"}):intl.formatMessage({id: "withdraw.text19"})}</em>
                                                <div className="fill-flex">
                                                    <input type="text" className="fill-control"  name="mobileCode" value={this.state.mobileCode} onChange={this.handleInputChange} />
                                                    <button type="button" className="btn btn-sms" onClick={this.settime} disabled={this.state.sendBtn?"":"disabled"} >{this.state.sendBtn? intl.formatMessage({id: "user.text19"}) : `${intl.formatMessage({id: "user.text108"})}${this.state.oneMinute}`}</button>
                                                </div>
                                            </div>
                    
                                            <div className="fill-group">
                                                <em className="name"></em>
                                                <button onClick={this.handleSubmit} className="btn btn-submit-sm">
                                                   {this.state.googleAuthInfo.googleAuth!=2 ? intl.formatMessage({id: "user.text109"}) : intl.formatMessage({id: "user.text110"})}
                                                </button>
                                            </div>
                                          </div>
                                        </div>
                                    ):(
                                        ""
                                    )
                                )
                            ):(
                                ""
                            )
                    }
                    
                </div>

            )

        )
    }
}



const mapStateToProps = (state, ownProps) => {
    return {
        googleInfo: state.userInfo.google,
        language: state.language
    }
}

const mapDispatchToProps = dispatch => {
    return{
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        fetchGoogle:() => {
            dispatch(fetchGoogle())
        },
        ModifyGoogle:() => {
            dispatch(ModifyGoogle())
        },
        sendGoogleCode:(cb) => {
            dispatch(sendGoogleCode()).then(cb)
        },
        doSubmitGoogle:(params, props) => {
            dispatch(submitGoogle(params)).then(res => {
                const result = res.data
                dispatch(notifSend({
                    message: result.des,
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                }))

                if(result.isSuc){
                    setTimeout(() => {
                        window.location.reload(true);
                    }, DISMISS_TIME);
                }
            })
        }
    }
}


export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(Google))























