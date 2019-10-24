import React from "react";
import {connect} from "react-redux";
import Form from "../../decorator/form";
import axios from 'axios';
import {browserHistory, Link} from 'react-router';
import {URL_IMG_CODE, DOMAIN_VIP, OTC_UIR, OTC} from '../../conf';
import {optPop, isIE, optJump, formatURL} from '../../utils'
import CT from '../../components/context/index' // 滚动
import {paySetPublicKey} from '../../redux/modules/session'

import './balances/balances.less'
import './withdraw/withdraw.less'
import {isCanJump} from "../../redux/modules/usercenter";
import {fetchPaySetting, fetchUserPayInfo} from "../../redux/modules/account";

@connect(
    state => ({
        userInfo:state.userInfo,
        cardPay:state.account.cardPay,
        userPayInfo:state.account.userPayInfo,
    }),
    {paySetPublicKey,fetchPaySetting,fetchUserPayInfo}
)

@Form
class CardPaySetting extends React.Component{
    constructor(props){
        super(props);
        this.base = {
            bankOpeningBank:'',
            bankOpeningBranch:'',
            bankCard:'',
            reBankCard:'',
            payPwd:''
        }
        this.state ={
            ...this.base,
            accountName:'',
            accountNumber:'',
            enable:'',
            id:'',
            kai:true,
            paymentType:'',
            bankOpeningBranch:'',
            qrcodeUrl:'',
            userId:'',
            scrollFlag:true,
            ...this.props.cardPay,
            ...this.props.userPayInfo
        }
        this.ckList = Object.keys(this.base); // 检查errorList
        this.scrollFlg = false;


        this.saveCardInfo = this.saveCardInfo.bind(this)
        this.goBack = this.goBack.bind(this)
        this.saveCardPwd = this.saveCardPwd.bind(this)
    }

    componentWillMount() {

    }
    componentDidMount() {
        this.props.fetchPaySetting(2); // 获取银行卡设置信息
        this.props.fetchUserPayInfo();

        // console.log(this.ckList);
        // console.log(this.props.userPayInfo);
    }
    componentWillReceiveProps(nextProps ,nextContext) {
        if(this.state.kai==true){
            this.setState({
                ...nextProps.cardPay
            })
        }

    }

    saveCardPwd(){
        // console.log('setpwd=========');
        this.props.paySetPublicKey(this.state.payPwd,this.saveCardInfo)
    }
    // 只能输入数字
    kp = (e) =>{
        // console.log(e);
        if((e.keyCode >= 47 && e.keyCode<=58) || (e.keyCode>=96 && e.keyCode<=105) || e.keyCode == 8) {
            e.returnValue=true;
        }else{
            e.returnValue=false;
        }
    }
    saveCardInfo(key){
        let {id} = this.state;
        if (!id){
            id = '0'
        }
        // console.log(this.hasError(this.ckList,0));
        if (!this.hasError(this.ckList,0)){
            const data = new FormData();
            data.append('bankOpeningBank', this.state.bankOpeningBank);
            data.append('id', id);
            data.append('accountNumber', this.state.bankCard);
            data.append('chkBankNo', this.state.reBankCard);
            data.append('bankOpeningBranch', this.state.bankOpeningBranch);
            data.append('enable', 1);
            data.append('accountName', this.props.userPayInfo.cardName);
            data.append('pwd',key);
            axios.post(OTC_UIR + OTC + '/web/payment/bindBank', data).then((res)=>{
                console.log(res);
                let msg = res.data.msg;
                if (res.data.code == 200){
                    optPop(() =>{
                    },msg,{timer: 1500})
                    let url = '/mg/account'
                    browserHistory.push(formatURL(url));
                }else{
                    optPop(() =>{
                    },msg,{timer: 1500})
                }
            })
        }
    }
    goBack(){
        browserHistory.push('/bw/mg/account')
    }
    render() {
        //console.log(this.state.reBankCard);
        // console.log(this.state.kai);
        const { formatMessage } = this.intl;
        let {pwd:payPwd,uid,errors,bankOpeningBank,bankOpeningBranch,bankCard,reBankCard,scrollFlag} = this.state;
        const {fIn,bOut,setBankOpeningBank,setBankOpeningBranch,setPayPwd,setBankCard,setReBankCard} = this;
        let { payPwd:epayPwd=[],bankOpeningBank:ebankOpeningBank= [],bankOpeningBranch: ebankOpeningBranch = [],bankCard:ebankCard = [],reBankCard:ereBankCard = [],} = errors;
        let {userPayInfo} = this.props
        let cm = CT();
        return (
            <cm.Consumer>
                {
                    (gotoTop) => {

                        return (
                            <section className="withdrawal_details">
                                <div className="withdrawal_contion">
                                    <div className="bbyh-mg">
                                        <div className="withdrawal_details_head mb20">
                                            <div className="address_div">
                                                <h3>{formatMessage({id:'设置银行卡'})}</h3>
                                            </div>
                                            <section className="from_withdrawal">
                                                <div className="input_div">
                                                    <h5 className="clearfix"><span>{formatMessage({id:'姓名'})}</span></h5>
                                                    <div className="0 input_div_1 input_div_2">
                                                        <input type="text"  autoComplete="off" value={userPayInfo.cardName}
                                                               name="cashAmount" className="input_1 input_2 input_3"  disabled={true}/>
                                                    </div>
                                                    <span className="ew"></span>
                                                </div>
                                                <div className="input_div">
                                                    <h5><span>{formatMessage({id:'开户行'})}</span></h5>
                                                    <div className={`0 input_div_1 input_div_2 ${ebankOpeningBank[0] && 'err'}`}>
                                                        <input maxLength="30" type="text" name="bankOpeningBank" autoComplete="off" placeholder={formatMessage({id:'请输入开户行'})} className="input_1 safePwd"
                                                               onFocus={fIn} onBlur={bOut} onChange={setBankOpeningBank} value={bankOpeningBank}/>
                                                    </div>
                                                    <span className="ew">{ebankOpeningBank[0]}</span>
                                                </div>
                                                <div className="input_div mb20">
                                                    <h5><span>{formatMessage({id:'开户支行'})}</span></h5>
                                                    <div className={`0 input_div_1 input_div_2 ${ebankOpeningBranch[0] && 'err'}`}>
                                                        <input maxLength="30" type="text" className="input_1 input_2 input_3" placeholder={formatMessage({id:'请输入您的开户支行'})} autoComplete="off" name="bankOpeningBranch"
                                                               onFocus={fIn} onBlur={bOut} onChange={setBankOpeningBranch} value={bankOpeningBranch}/>
                                                    </div>
                                                    <span className="ew">{ebankOpeningBranch[0]}</span>
                                                </div>
                                                <div className="input_div mb20">
                                                    <h5><span>{formatMessage({id:'银行卡号'})}</span></h5>
                                                    <div className={`0 input_div_1 input_div_2 ${ebankCard[0] && 'err'}`}>
                                                        <input onKeyUp={(e) =>{this.kp(e)}} maxLength="19" type="text" className="input_1 input_2 input_3" placeholder={formatMessage({id:'请输入您的银行卡号'})} autoComplete="off"
                                                               name="bankCard" onFocus={fIn} onBlur={bOut} onChange={setBankCard} value={bankCard}/>
                                                    </div>
                                                    <span className="ew">{ebankCard[0]}</span>
                                                </div>
                                                <div className="input_div mb20">
                                                    <h5><span>{formatMessage({id:'确认卡号'})}</span></h5>
                                                    <div className={`0 input_div_1 input_div_2 ${ereBankCard[0] && 'err'}`}>
                                                        <input onKeyUp={(e) =>{this.kp(e)}} type="text" className="input_1 input_2 input_3" maxLength="19" placeholder={formatMessage({id:'请再次输入您的银行卡号'})} autoComplete="off"
                                                               name="reBankCard" onFocus={fIn} onBlur={bOut} onChange={setReBankCard} value={reBankCard}/>
                                                        <input className="input_1 input_2 input_3 ss" readonly  onFocus={fIn} onBlur={bOut} onChange={setReBankCard}  value />

                                                    </div>

                                                    <span className="ew">{ereBankCard[0]}</span>
                                                </div>
                                                <div className="input_div mb20">
                                                    <h5><span>{formatMessage({id:'资金密码'})}</span></h5>
                                                    {
                                                        userPayInfo.safePwd ?
                                                            <div className={`0 input_div_1 input_div_2 ${epayPwd[0] && 'err'}`}>
                                                                <input type="password" className="input_1 input_2 input_3" placeholder={formatMessage({id:'请输入您的资金密码'})} autoComplete="off"
                                                                       name="payPwd" value={payPwd} onFocus={fIn} onBlur={bOut} onChange={setPayPwd}/>
                                                            </div>
                                                            :
                                                            <div className={`0 input_div_1 input_div_2 ${epayPwd[0] && 'err'}`}><span className="setpwd"><Link to={"/bw/mg/setPayPwd"}>{formatMessage({id: "设置资金密码2"})}</Link></span></div>
                                                    }
                                                    <span className="ew">{epayPwd[0]}</span>
                                                </div>
                                                <div className="pay-tip">
                                                    <h4><span>{formatMessage({id:'温馨提示'})}：</span></h4>
                                                    <p>1. <span>{formatMessage({id:'请设置您的收款方式，请务必保障是您本人所有的账号。'})}</span></p>
                                                    <p>2. <span>{formatMessage({id:'设置好的收款方式将在交易时向买方展示并给您打款，请确保信息无误。'})}</span></p>
                                                </div>
                                                <div className="pay-footer">
                                                    <span className="btn cancel" id="submit1" onClick={this.goBack}>{formatMessage({id:'取消'})}</span>
                                                    <span className="btn save" id="submit" onClick={this.saveCardPwd}>{formatMessage({id:'确定'})}</span>
                                                </div>
                                            </section>
                                        </div>
                                    </div>

                                </div>

                                {/* 路由切换时 滚动到顶部 scrollbar Bug*/}
                                {
                                    scrollFlag  &&  ((func) => {
                                        console.log('===============>   gotoTop111     <===============')
                                        func();
                                        setTimeout(() =>{
                                            this.setState({
                                                scrollFlag: false
                                            },() =>{
                                                console.log(this.state.scrollFlg)
                                            })
                                        }, 500)

                                    })(gotoTop)
                                }
                            </section>

                        );

                    }
                }
            </cm.Consumer>
        )
    }
}

export default CardPaySetting
