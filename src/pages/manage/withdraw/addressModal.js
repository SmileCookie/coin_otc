import React from 'react';
import axios from 'axios';
import qs from 'qs';
import { connect } from 'react-redux'
import { Link } from 'react-router'
import { COIN_KEEP_POINT,DISMISS_TIME,DOMAIN_VIP,COUNT_DOWN_ONE_MINUTE} from '../../../conf'
import { getWithdrawAddressAuthenType,addWithDrawAddress } from '../../../redux/modules/withdraw'
import { FormattedMessage,FormattedHTMLMessage, injectIntl ,FormattedDate} from 'react-intl';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend,notifClear,notifDismiss } = notifActions;


class AddressModal extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            memo:'',
            address:'',
            sendBtn:true,
            oneMinute:COUNT_DOWN_ONE_MINUTE,
        }
        this.submitStatus = 1
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onSubmitAddress = this.onSubmitAddress.bind(this)
        this.settime = this.settime.bind(this)
        this.checkSubAddress = this.checkSubAddress.bind(this)
    }

    componentDidMount(){
        this.props.getAddressAuthenType()
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
        const { address,memo,sendCode } = this.state
        if(memo.length > 20 ){
            this.props.notifSend({
                message:<FormattedMessage  id="withdraw.text78" />,
                kind: 'warning',
                dismissAfter: DISMISS_TIME
            })
            return false;
        }
        if(address == ""){
            this.props.notifSend({
                message:<FormattedMessage  id="withdraw.text79" />,
                kind: 'warning',
                dismissAfter: DISMISS_TIME
            })
            return false;
        }
        axios.post(DOMAIN_VIP+"/userSendCode",qs.stringify({
            codeType: 10,
            currency:this.props.curCoin
        })).then(res => {
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
        }) 
    }

    checkSubAddress(){
        const { address,memo,sendCode } = this.state
        if(memo.length > 20 ){
            this.props.notifSend({
                message:<FormattedMessage  id="withdraw.text78" />,
                kind: 'warning',
                dismissAfter: DISMISS_TIME
            })
            return false;
        }
        if(address == ""){
            this.props.notifSend({
                message:<FormattedMessage  id="withdraw.text79" />,
                kind: 'warning',
                dismissAfter: DISMISS_TIME
            })
            return false;
        }
    }
    
    onSubmitAddress(){
        const { address,memo,sendCode } = this.state
        if(memo.length > 20 ){
            this.props.notifSend({
                message:<FormattedMessage  id="withdraw.text78" />,
                kind: 'warning',
                dismissAfter: DISMISS_TIME
            })
            return;
        }
        if(address == ""){
            this.props.notifSend({
                message:<FormattedMessage  id="withdraw.text79" />,
                kind: 'warning',
                dismissAfter: DISMISS_TIME
            })
            return;
        }
        if(this.props.addressType == 2 && sendCode == ""){
            this.props.notifSend({
                message:<FormattedMessage  id="withdraw.text80" />,
                kind: 'warning',
                dismissAfter: DISMISS_TIME
            })
            return;
        }
        if(this.submitStatus==-1){
            this.props.notifSend({
                message:<FormattedMessage  id="withdraw.text81" />,
                kind: 'warning',
                dismissAfter: DISMISS_TIME
            })
           return;
       }

       this.submitStatus = -1;
        //添加提现地址
        this.props.addWithDrawAddress({
            address:address,
            memo:memo,
            mobileCode:sendCode
        },(res) => {
            const result = res.data
            this.submitStatus = 1
            
            if(result.isSuc){
                this.props.notifSend({
                    message:result.des,
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                })
               this.props.router.push(`/bw/manage/account/download?coint=${this.props.curCoin}`)
            }else{
                this.props.notifSend({
                    message:result.des,
                    kind: 'warning',
                    dismissAfter: DISMISS_TIME
                })
            }
        })
    }

    render(){
        const { curCoin,mobileStatu,intl,addressType } = this.props
        const { sendBtn } = this.state
        return (
            <section className="new_address">
                <h2><FormattedMessage  id="withdraw.text64" values={{coint:curCoin}}  /></h2> 
                {addressType!=2&&<div className="item mb20 head_text_tips"><FormattedMessage  id="withdraw.text75" />"<Link to="/bw/manage/auth?address=address_withdraw"><FormattedMessage  id="withdraw.text88" /></Link>"</div>}
                <div className="item mb10">
                    <h5><FormattedMessage  id="withdraw.text66" /></h5>
                    <div className="input_warp">
                        <input type="text" name="memo" maxLength="20" className="input_item" onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="item mb10">
                    <h5><FormattedMessage  id="withdraw.text82" /></h5>
                    <div className="input_warp">
                        <input type="text" name="address" className="input_item" onChange={this.handleInputChange} />
                    </div>
                </div>
                { addressType==2&&
                    <div className="item">
                        <div className="mb5">
                            <h5>
                            {mobileStatu == 2 ?<FormattedMessage  id="user.text18" />:<FormattedMessage  id="email.text15" />}                                                
                            </h5>
                            <div className="input_warp input_warp_bor clearfix">
                                <input type="text" name="sendCode" className="input_item input_send" placeholder={intl.formatMessage({id:'withdraw.text77'})} onChange={this.handleInputChange}/>
                                <button className="send_btn hover_color" onClick={this.settime} disabled={sendBtn?"":"disabled"}>
                                    {sendBtn? intl.formatMessage({id:'withdraw.text8'}):`${intl.formatMessage({id:'user.text108'})}${this.state.oneMinute}`}
                                </button>
                            </div>
                        </div>
                        <div className="text_bottom_tips mb40"><FormattedMessage  id="withdraw.text76" /></div>
                    </div>
                }
                <button className="address_btn" onClick={this.onSubmitAddress} disabled={this.submitStatus?"":"disabled"}><FormattedMessage  id="withdraw.text67" /></button>
            </section>
        )
    }
}

const mapStateToProps = (state,ownProps) => {
    return {
        curCoin:state.withdraw.curCoin,
        addressType:state.withdraw.withdrawAddressAuthenType,
        mobileStatu:state.session.baseUserInfo.mobileStatus
    }
}

const mapDispatchToProps = dispatch => {
    return {
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        notifClear: () => {
            dispatch(notifClear());
        },
        notifDismiss: (msg) => {
            dispatch(notifClear(msg));
        },
        getAddressAuthenType:()=>{
            dispatch(getWithdrawAddressAuthenType())
        },
        addWithDrawAddress:(values,cb) => {
            dispatch(addWithDrawAddress(values)).then(cb)
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(AddressModal))





























