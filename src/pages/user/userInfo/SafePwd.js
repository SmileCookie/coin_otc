import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux'
import { fetchSafePwd,sendSafePwdCode,submitSafePwd } from '../../../redux/modules/userInfo'
import { COIN_KEEP_POINT,DISMISS_TIME, DOMAIN_VIP} from '../../../conf'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend } = notifActions;
import { FormattedMessage, injectIntl } from 'react-intl';
import { formatURL } from '../../../utils'
import Strength from '../../../components/user/strength';

class SafePwd extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            sendBtn : true,
            oneMinute : 60,
            safeLevel:0,
            currentPwd:'',
            safePwd:'',
            newPwd:'',
            mobileCode:'',
            googleCode:'',
            ic: 0

        }
        this.intl = props.intl
        this.settime = this.settime.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleSubmit = this.handleSubmit.bind(this)
        this.fc = this.fc.bind(this)
        this.lc = this.lc.bind(this)
    }   
    fc(){
        this.setState({
            ic: 1
        })
    }
    lc(){
        this.setState({
            ic: 0
        })
    }
    componentDidMount(){
        this.props.fetchSafePwd()
    }

//     componentWillReceiveProps(nextProps) {
//         if(nextProps.safePwd.isloaded) {
//             this.setState({
//                 safePwdInfo: nextProps.safePwd.data
//             })
//         }
//    }
     //发送短信验证码

     //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
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

    handleSubmit(event){
        event.preventDefault();
        const {currentPwd,safePwd,newPwd,mobileCode,googleCode} = this.state
        if(safePwd !== newPwd){
            this.props.notifSend({
                message: this.intl.formatMessage({id: "user.text100"}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            return;
        }
        let level = 0;
        if (safePwd.length >= 8 && safePwd.length <= 20){
            if (/\d/.test(safePwd)) level++;
            if (/[a-z]/.test(safePwd)) level++;
            if (/[A-Z]/.test(safePwd)) level++; 
            if (/\W/.test(safePwd)) level++;
            if(level<2){
                this.props.notifSend({
                    message: this.intl.formatMessage({id: "user.text99"}),
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
                return;
            }
         }else{
            this.props.notifSend({
                message: this.intl.formatMessage({id: "user.text99"}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            return;
        }
        this.props.doSubmitSafePwd({
            currentPwd:currentPwd,
            safePwd:safePwd,
            safeLevel:level,
            newPwd:newPwd,
            mobileCode:mobileCode,
            googleCode:googleCode
        }).then((res) => {
            setTimeout(() => {
                if(res.indexOf("true") > -1){
                    window.location.href = formatURL('/manage');
                }
            }, DISMISS_TIME)
        })
        
        
    }

    render(){
        const {isloading,isloaded,data} = this.props.safePwd
        const intl = this.intl
        return(
            isloading&&!isloaded? (
                <div><FormattedMessage id="user.text101" /></div>
            ) : (
                <div className="cont-row">
                    <div className="bk-top mb0">
                        <h2>{data.hasSafePwd? intl.formatMessage({id: "user.text11"}) : intl.formatMessage({id: "user.text10"}) }{this.props.language.locale == 'en' ? ' ':''}<FormattedMessage id="user.text6" /></h2>
                    </div>
                    <div className="vip-tip">
                        <p>{data.hasSafePwd? intl.formatMessage({id: "user.text112"}) : intl.formatMessage({id: "user.text113"}) }</p>
                    </div>
                    <div className="fill-form">
                        <form onSubmit={this.handleSubmit}>
                            <div className="fill-form-bd">
                                {
                                    data.hasSafePwd? (
                                        <div className="fill-group">
                                            <em className="name"><FormattedMessage id="user.text25" /></em>
                                            <input type="password" className="fill-control"  name="currentPwd" value={this.state.currentPwd} onChange={this.handleInputChange} />
                                        </div>
                                    ) : ""
                                }
                                <div className="fill-group">
                                    <em className="name">{data.hasSafePwd?intl.formatMessage({id: "user.text26"}):intl.formatMessage({id: "user.text114"})}</em>
                                    <input type="password" className="fill-control" name="safePwd" value={this.state.safePwd} onChange={this.handleInputChange}/>
                                </div>
                                <div className="fill-group">
                                    <em className="name"><FormattedMessage id="user.text27" /></em>
                                    <input type="password" className="fill-control" name="newPwd" onKeyUp={this.fc} onBlur={this.lc} value={this.state.newPwd} onChange={this.handleInputChange}/>
                                    {
                                        false && this.state.ic ? <Strength val={this.state.newPwd}></Strength> : null
                                    }
                                </div>
                                <div className="fill-group">
                                    <em className="name">{data.mobileStatu==2?intl.formatMessage({id: "withdraw.text7"}):intl.formatMessage({id: "withdraw.text19"})}</em>
                                    <div className="fill-flex">
                                        <input type="text" className="fill-control" name="mobileCode" value={this.state.mobileCode} onChange={this.handleInputChange}/>
                                        <button type="button" onClick={this.settime} disabled={this.state.sendBtn?"":"disabled"} className="btn btn-sms">{this.state.sendBtn? intl.formatMessage({id: "user.text19"}):`${intl.formatMessage({id: "user.text108"})}${this.state.oneMinute}`}</button> 
                                    </div>
                                </div>
                                {
                                    data.googleAuth ==2? (
                                        <div className="fill-group">
                                            <em className="name"><FormattedMessage id="withdraw.text30" /></em>
                                            <input type="text" className="fill-control"  name="googleCode" value={this.state.googleCode} onChange={this.handleInputChange} />
                                        </div>
                                    ) : ""
                                }
                                <div className="fill-group">
                                    <em className="name"></em>
                                    <button type="submit" className="btn btn-submit-sm"><FormattedMessage id="user.text20" /></button>
                                    {
                                        data.hasSafePwd && <Link href={DOMAIN_VIP + "/ac/safepwd_find"} className="ml10" target="_blank"> <FormattedMessage id="user.text115" /></Link>
                                    }
                                </div>
                            </div>
                        </form>
                    </div>
             </div>
            )
        )
    }
}



const mapStateToProps = (state, ownProps) => {
    return {
        safePwd: state.userInfo.safePwd,
        language: state.language
    }
}

const mapDispatchToProps = (dispatch) =>{
    return {
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        fetchSafePwd:() => {
            dispatch(fetchSafePwd())
        },
        sendSafePwdCode:(cb) => {
            dispatch(sendSafePwdCode()).then(cb)
        },
        doSubmitSafePwd:(params) => {
            return dispatch(submitSafePwd(params)).then(res => {
                let result = res.data
                let state = (/<([^>]+)>([^<>]+)<\/\1>/g.exec(result))[2]
                let des = (/<([^>]+)>([^<>]+)<\/MainData>/g.exec(result))[2]   
                dispatch(notifSend({
                    message: des,
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                }));

                return state;
            })
        }
    }
}

export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(SafePwd))

























