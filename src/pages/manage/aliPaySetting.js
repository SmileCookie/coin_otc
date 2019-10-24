import React from "react";
import {connect} from "react-redux";
import Form from "../../decorator/form";
import FileUpload from '../../components/upload'

import './balances/balances.less'
import './withdraw/withdraw.less'

import CT from '../../components/context/index'
import {browserHistory, Link} from "react-router";
import axios from "axios";
import {DOMAIN_VIP, OTC,OTC_UIR} from "../../conf";
import {formatURL, optPop} from "../../utils";
import {changeImgCode, paySetPublicKey} from '../../redux/modules/session'
import {fetchPaySetting, fetchUserPayInfo} from "../../redux/modules/account";


// let scrollFlag = true
// console.log(scrollFlag)
@connect(
    state => ({
        userInfo:state.userInfo,
        aliPay:state.account.aliPay,
        userPayInfo:state.account.userPayInfo,
    }),
    {paySetPublicKey,fetchPaySetting,fetchUserPayInfo}
)


@Form
class AliPaySetting extends React.Component{
    constructor(props){
        super(props);
        this.upload ={
            fileList: []
        }
        this.base ={
            payPwd:'',
            accountNumber:''
        }
        this.state = {
            ...this.base,
            ...this.upload,
            accountName:'',
            accountNumber:'',
            enable:'',
            id:'',
            paymentType:'',
            bankOpeningBranch:'',
            bankOpeningBanks:'',
            qrcodeUrl:'',
            userId:'',
            imgList: [],
            editFlag: this.props.editFlag,
            scrollFlag: true
            // ...this.props.aliPay,

        }
        this.ckList = Object.keys(this.base);

        this.goBack = this.goBack.bind(this)
        this.saveCardInfo = this.saveCardInfo.bind(this)
        this.saveAliPwd = this.saveAliPwd.bind(this)
        this.getImgList = this.getImgList.bind(this)
}

    componentDidMount() {
        this.props.fetchPaySetting(1); // 获取支付宝 信息
        this.props.fetchUserPayInfo();
    }
    componentWillReceiveProps(nextProps, nextContext) {
        this.setState({
            // ...nextProps.aliPay
        })
    }

    saveAliPwd(){
        // console.log('setpwd=========')
        this.props.paySetPublicKey(this.state.payPwd,this.saveCardInfo)
    }
    saveCardInfo(key){
        let {id} = this.state;
        console.log('id     ====',id)
        if (!id){
            id = '0'
        }
        // console.log(this.hasError(this.ckList,0));
        if (!this.hasError(this.ckList,0)){
            const data = new FormData();
            let url = ''
            if(this.state.imgList[0]){
                url = this.state.imgList[0].url
                // url = this.state.imgList[0]
            }
            data.append('id',this.props.aliPay.id);
            data.append('accountNumber', this.state.accountNumber);
            data.append('qrcodeUrl', url);
            data.append('enable', 1);
            data.append('accountName', this.props.userPayInfo.cardName);
            data.append('pwd',key);
            axios.post(OTC_UIR + OTC +'/web/payment/alipay', data).then((res)=>{
                console.log(res);
                let msg = res.data.msg;
                if (res.data.code == 200){
                    optPop(() =>{
                    },msg,{timer: 2000})
                    let url = '/mg/account'
                    browserHistory.push(formatURL(url));
                }else{
                    optPop(() =>{
                    },msg,{timer: 2000})
                }
            })
        }
    }
    goBack(){
        browserHistory.push('/bw/mg/account')
    }
    getImgList(v){
        // let { imgList } = this.state;
        let arr = [];
        this.setState({
            imgList:arr.concat(v)
        },() =>{
            console.log(this.state.imgList)
        })
    }
    render() {
        const { formatMessage } = this.intl;
        let {accountNumber,errors,payPwd,scrollFlag} = this.state;
        // accountNumber = ''
        const { fIn,bOut,setPayPwd,setAccountNumber} = this;
        let {userPayInfo} = this.props
        // console.log('userpayInfo====='   + scrollFlag )
        let { payPwd:epayPwd=[],accountNumber:eaccountNumber = []} = errors;
        let cm = CT();
        // console.log('id     ====',this.props)
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
                                            <h3>{formatMessage({id:'设置支付宝'})}</h3>
                                        </div>
                                        <section className="from_withdrawal">
                                            <div className="input_div">
                                                <h5 className="clearfix"><span>{formatMessage({id:'姓名'})}</span></h5>
                                                <div className="0 input_div_1 input_div_2">
                                                    <input type="text"  autoComplete="off" value={userPayInfo.cardName}
                                                           name="account" className="input_1 input_2 input_3" disabled={true} />
                                                </div>
                                                <span className="ew"></span>
                                            </div>
                                            <div className="input_div">
                                                <h5><span>{formatMessage({id:'支付宝帐号'})}</span></h5>
                                                <div className={`0 input_div_1 input_div_2 ${eaccountNumber[0] && 'err'}`}>
                                                    <input type="text"  autoComplete="off" placeholder={formatMessage({id:'请输入您的支付宝帐号'})} className="input_1 safePwd" name="accountNumber"
                                                           onFocus={fIn} onBlur={bOut}  onChange={setAccountNumber}  value={accountNumber} />
                                                           <input className="input_1 input_2 input_3 ss" readonly    value />
                                                </div>
                                                <span className="ew">{eaccountNumber[0]}</span>
                                            </div>
                                            <div className="input_div">
                                                <h5><span>{formatMessage({id:'收款二维码'})}</span></h5>
                                                <div className="upload-div">
                                                    <FileUpload limit='1' getImgList={this.getImgList} filetype="image"/>
                                                </div>
                                                <span className="ew"></span>
                                            </div>
                                            <div className={`input_div `}>
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
                                                <span className="btn save" id="submit" onClick={this.saveAliPwd}>{formatMessage({id:'确定'})}</span>
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
                                                    // console.log(this.state.scrollFlg)
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

export default AliPaySetting
