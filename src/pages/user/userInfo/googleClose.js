import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { connect } from 'react-redux';
import { fetchSafePwd, sendSafePwdCode, doCloseGoogleAuth } from '../../../redux/modules/userInfo'
import { DISMISS_TIME, SAFEAUTH } from '../../../conf';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend } = notifActions;
import { formatURL } from '../../../utils';

class GoogleClose extends React.Component{
    constructor(props){
        super(props);

        this.commitData = {
            mobileCode: '',
            gCode: '',
        }

        this.state = {
            sendBtn: true,
            oneMinute: 60,
            ...this.commitData,
        }
        
        this.handleInputChange = this.handleInputChange.bind(this)
        this.settime = this.settime.bind(this)
        this.doCloseGoogleAuth = this.doCloseGoogleAuth.bind(this)
    }

    componentDidMount(){
        this.props.fetchSafePwd()
    }

    doCloseGoogleAuth(){
        Object.keys(this.commitData).reduce((obj, v)=>{
            obj[v] = this.state[v];
            return this.commitData;
        }, this.commitData);
        
        this.props.doCloseGoogleAuth(this.commitData).then((result) => {
            const res = result.data;
            if(res.isSuc){
                window.location.href = formatURL('/manage/user');
            } else {
                this.props.notifSend({
                    message: res.des,
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
            }
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

    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    render(){
        const {isloading,isloaded,data} = this.props.safePwd
        const intl = this.props.intl
        const {sendBtn, mobileCode, oneMinute, gCode} = this.state

        return (
            isloading&&!isloaded? (
                <div><FormattedMessage id="user.text101" /></div>
            ) : (
            <div className="cont-row">
                <div className="bk-top mb0">
                    <h2>
                        <FormattedMessage id="google.text12" />
                    </h2>
                </div>
                <div className="fill-form clearfix">

                    <div className="fill-form-bd">
                        <div className="fill-group">
                            <em className="name"><FormattedMessage id="withdraw.text30" /></em>
                            <input type="text" className="fill-control" value={gCode} onChange={this.handleInputChange} name="gCode" />
                        </div>
                        
                        <div className="fill-group clearfix">
                        <em className="name">{data.mobileStatu==2?intl.formatMessage({id: "withdraw.text7"}):intl.formatMessage({id: "withdraw.text19"})}</em>
                            <div className="fill-flex">
                                <input type="text" className="fill-control" name="mobileCode" value={mobileCode} onChange={this.handleInputChange}/>
                                <button type="button" onClick={this.settime} disabled={sendBtn?"":"disabled"} className="btn btn-sms">{sendBtn? intl.formatMessage({id: "user.text19"}):`${intl.formatMessage({id: "user.text108"})}${oneMinute}`}</button> 
                            </div>
                        </div>
                    </div>

                    
                    {
                        data.googleAuth ==2 && 
                           <div className="fill-group">
                                <em className="name"></em>
                                <button type="submit" onClick={this.doCloseGoogleAuth} className={`btn btn-submit-sm ${data.mobileStatu !=2 ? 'hide' : ''}`}><FormattedMessage id="user.text123" /></button>
                            </div>
                    }
                    

                </div>
            </div>
            )
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        safePwd: state.userInfo.safePwd
    }
};

const mapDispatchToProps = (dispatch) =>{
    return {
        fetchSafePwd:() => {
            dispatch(fetchSafePwd())
        },
        sendSafePwdCode:(cb) => {
            dispatch(sendSafePwdCode(SAFEAUTH)).then(cb)
        },
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        doCloseGoogleAuth: (values) => {
            return dispatch(doCloseGoogleAuth(values));
        }
    }
};

export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(GoogleClose));