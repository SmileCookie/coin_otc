import React from 'react';
import { connect } from 'react-redux';

import { FormattedMessage,FormattedHTMLMessage} from 'react-intl';
import { Link, browserHistory } from 'react-router';
import { getUserBaseInfo} from '../../../redux/modules/session';
import { isCanJump } from '../../../redux/modules/usercenter';

import { JSEncrypt } from 'jsencrypt';
import axios from 'axios';
import Transfer from '../../manage/transfer';
import { DOMAIN_VIP,EXHANGETOTALDIAN,COIN_KEEP_POINT } from '../../../conf';
import { radioSwitch, formatURL, optPop, trade_pop, } from '../../../utils'
import ReactModal from '../../../components/popBox'
import cookie from 'js-cookie'
const qs = require('qs');
const BigNumber = require('big.js');
const encrypt = new JSEncrypt();
BigNumber.RM = 0;

import './index.css'

class SellBuyForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {


            // 全局属性
            selectType:this.props.selectType,               // 当前选择的委托类型
            doEntrustStatus: false,     // 是否满足提交的条件
            safePwd: '',                // 安全密码
            modalHTML:'',               // 当前弹出框代码
            feeRate:'0',                 // 费率

            checked:'6',               // 单选框选中状态  ‘’ 一直开启，6 关闭6小时 1 一直关闭
            isloading: false,          // 是否正在提交委托
            btnText:'',                // 买入卖出按钮文本

            // 限价委托，批量委托
            price:'',                    // 价格（最低价格）
            maxPrice:'',                // 最高价格
            amount:'',                  // 数量
            total:'',         // 金额
            parentPrice:0,              // 由父组件传过来的价格（当点击买入，卖出委托时触发改变）
            parentAmount:0,             // 由父组件传过来的数量（当点击买入，卖出委托时触发改变）
            lagelMoney:'0.00',                //限价单 价格折算

            // 以下为计划委托属性
            triggerPrice:'',            // 触发价格
            planPrice:'',               // 委托价格
            planAmount:'',              // 计划委托数量
            planTotal:'',     // 计划委托金额
            triggerLagelMoney:'0.00',

            closeStatu:this.props.userInfo.checkTrans||0, //默认状态始终开启
            defaultState:0,
            buyFeeRate:'',              //买-费率
            sellFeeRate:'',             //卖-费率
            setPwd:false,
            isEnableRs:1,               //是否启用重置按钮

        }

        this.buyUnitPrice = React.createRef()
        this.buyNumber = React.createRef()
        this.buyTriggerPrice = React.createRef()
        this.buyPlanPrice = React.createRef()
        this.buyPlanNumber = React.createRef()
        this.canUseMoney = React.createRef()

        this.doEntrust = this.doEntrust.bind(this)
        this.resetInput = this.resetInput.bind(this)
        this.openSafePwdModal = this.openSafePwdModal.bind(this)
        this.getRadioValue = this.getRadioValue.bind(this)
        this.takeLagelMoney = this.takeLagelMoney.bind(this)

        this.p = React.createRef();
        this.configFee = this.configFee.bind(this);
    }
    componentDidMount(){
        this.setState({ // 设置父组件传过来的价格和数量（默认都是0）
            parentPrice:this.props.price,
            parentAmount:this.props.amount
        },()=>{
            // console.log(this.state.price);
        })

        // init 重置资金密码trade
        axios.get(DOMAIN_VIP + '/manage/account/download/resetPayPwdAsh').then((res) => {
            this.setState({
                isEnableRs: !res.data.datas.ashLockStatus
            })
        });


    }
    componentWillReceiveProps(nextProps){
        try{
            let { assetsDetail } = this.props
            let { price, defaultState,amount,parentPrice,parentAmount } = this.state
            if(!price&&!defaultState&&nextProps.marketinfo.data){
                this.changeDefaultPrice(nextProps)
                this.setState({
                    defaultState:1
                })
            }else if(this.props.marketinfo.currentMarket != nextProps.marketinfo.currentMarket){
                this.setState({
                    price:'',
                    amount:'',
                    total:'0.00000000',
                    defaultState:0,
                    triggerPrice:'',
                    planPrice:'',
                    planAmount:'',
                })
            }
            if(nextProps.price!=parentPrice || nextProps.amount!=parentAmount){// 点击委托列表时触发，改变文本框内容
                let price = nextProps.price,
                    amount = nextProps.amount,
                    total = new BigNumber(nextProps.price).times(nextProps.amount).toFixed(EXHANGETOTALDIAN),
                    parentPrice = nextProps.price,
                    parentAmount = nextProps.amount
                if(nextProps.user){
                    let canUseMoney;
                    let coin = nextProps.marketinfo.currentMarket.toUpperCase() // 币种全称（btc_usdt）
                    BigNumber.RM = 0;
                    if(nextProps.isBuy){
                        price = nextProps.price,
                            total = new BigNumber(nextProps.price).times(nextProps.amount).toFixed(EXHANGETOTALDIAN),
                            parentPrice = nextProps.price,
                            parentAmount = nextProps.amount;
                        try{
                            if(assetsDetail){
                                canUseMoney = new BigNumber(assetsDetail[coin.split('_')[1]]['balance']).toFixed(this.marke.exchangeBixDian);  // 余额
                            }
                        }catch(e){

                        }
                        if(parseFloat(total) > parseFloat(canUseMoney)){
                            total = canUseMoney;
                            amount = new BigNumber(total?total:0).div(price).toFixed(this.marke.numberBixDian)
                        }
                    }else{
                        price = nextProps.price,
                            total = new BigNumber(nextProps.price).times(nextProps.amount).toFixed(EXHANGETOTALDIAN),
                            parentPrice = nextProps.price,
                            parentAmount = nextProps.amount;
                        if(assetsDetail){
                            canUseMoney = new BigNumber(assetsDetail[coin.split('_')[0]]['balance']).toFixed(this.marke.numberBixDian);  // 余额
                        }
                        if(parseFloat(amount) > parseFloat(canUseMoney)){
                            amount = canUseMoney;
                            total = new BigNumber(price).times(amount).toFixed(EXHANGETOTALDIAN)
                        }
                    }
                }
                this.setState({
                    price:price,
                    amount:amount,
                    total:total,
                    parentPrice:parentPrice,
                    parentAmount:parentAmount
                })
            }
        }catch(e){

        }
    }
    changeDefaultPrice(nextProps){
        try{
            let buyPrice = 0;
            let sellPrice = 0;
            let total = 0;
            let amount = this.state.amount||0;
            this.marke = nextProps.marketsConfData[nextProps.marketinfo.currentMarket];
            if(nextProps.marketinfo['data']['listUp'].length>0){
                buyPrice = nextProps.marketinfo['data']['listUp'][0][0]
                buyPrice = new BigNumber(buyPrice).toFixed(this.marke.exchangeBixDian)
                total = new BigNumber(buyPrice).times(amount).toFixed(EXHANGETOTALDIAN)
            }
            if(nextProps.marketinfo['data']['listDown'].length>0){
                sellPrice = nextProps.marketinfo['data']['listDown'][0][0]
                sellPrice = new BigNumber(sellPrice).toFixed(this.marke.exchangeBixDian)
                total = new BigNumber(sellPrice).times(amount).toFixed(EXHANGETOTALDIAN)
            }
            this.takeLagelMoney(buyPrice)
            if(nextProps.isBuy){
                this.setState({price:buyPrice,total:total})
            }else{
                this.setState({price:sellPrice,total:total})
            }
        }catch(e){

        }
    }

    //计算手续费
    configFee(data){
        console.log(data)
        let db = data.substring(0, data.indexOf(".") + 9);
        return db
    }
    // 设置确认弹窗代码
    openConfirmModal(){
        let isBuy = this.props.isBuy
        let market = this.props.marketinfo.currentMarket
        let marketCoin = market.toUpperCase().split('_')[1] // 当前选中的市场（usdt btc）
        let choseCoin = market.toUpperCase().split('_')[0]  // 当前选择的币种

        let total = this.state.price
        let num = this.state.amount
        let str = <div className="modal-btn">
            {isBuy?
                <p>
                    <FormattedHTMLMessage id="确定要以NXXX的价格买入N个XXX" values={{num:num,choseCoin:choseCoin}}/>
                </p>
                :
                <p>
                    <FormattedHTMLMessage id="确定要以NXXX的价格卖出N个XXX" values={{num:num,choseCoin:choseCoin}}/>
                </p>
            }
            <div className="modal-foot">
                <a className="btn ml10" onClick={() => this.modal.closeModal()}><FormattedMessage id="cancel"/></a>
                <a className="btn ml10" onClick={() => this.checkSafePwd()}><FormattedMessage id="sure"/></a>
            </div>
        </div>;
        this.setState({modalHTML:str},()=>{
            this.modal.openModal();
        })
    }

    getRadioValue = (val) =>{

        this.setState({
            closeStatu:val
        })

    }

    // 改变安全密码的值
    updataSafePwd(event){
        this.setState({safePwd:event.target.value})
    }

    // 设置安全密码弹窗代码
    openSafePwdModal(){
        console.log(this.state.setPwd)
        let {intl} = this.props;
        const { closeStatu } = this.state;
        // debugger;
        const verifyStatus = this.props.userInfo.checkTrans||0;
        const ppwLock = this.props.userInfo.ppwLock;
        //let templateStr;
        // if(ppwLock){
        //     alert("操a's'd你s那阿大撒")
        //     templateStr = <a className="reset-link pull-right" onClick={(e) =>e.preventDefault()} style={{color:'#737A8D'}}><FormattedMessage id="重置资金密码trade"/></a>
        // }else{
        //     alert("二")
        //     templateStr = <Link className={`reset-link pull-right ${!this.state.isEnableRs || this.state.setPwd ? 'stopLink' : ''}`} to={(this.state.isEnableRs && !this.state.setPwd)? "/bw/mg/resetPayPwd" : ""} style={{[(!this.state.isEnableRs || this.state.setPwd)?'color':'']:'#737A8D'}}><FormattedMessage id="重置资金密码trade"/></Link>
        // }
        //const ppwLock = false;
        this.props.isCanJump({opt:5}).then(r => { //状态5是交易验证的
            //true是没有禁止
            let flag = r.isSuc;

            let str = <div className="Jua-table-inner Jua-table-main " style={{width:'auto'}}>
                <div className="head react-safe-box-head">
                    <h3><FormattedMessage id="本次交易需要资金密码验证"/></h3>
                </div>
                <div className="body">
                    <div className="bk-page-table">
                        <div className="bk-page-tableCell">
                            <div id="safeWordForm">
                                <div className="form-group clearfix">
                                    <label htmlFor="safePwd" className="control-label sr-only"><FormattedMessage id="本次交易需要资金密码验证"/></label>
                                    <input type="password" className="input-hidden" />
                                    <input maxLength="20" onPaste={(e)=> {e.preventDefault()}} readOnly type="text" onBlur={()=>{
                                        const obj = this.p.current;
                                        obj.setAttribute("readonly", true);
                                    }} onKeyDown={(e)=>{
                                        const KCODE = e.keyCode;
                                        const obj = this.p.current;
                                        if(KCODE === 8){
                                            if(obj.value.length === 1){
                                                this.rbflg = 1;
                                                obj.setAttribute("type", "text");
                                                obj.value = '';
                                                this.setState({
                                                    safePwd: "",
                                                })
                                            }
                                        }

                                        if(this.rbflg){
                                            setTimeout(()=>{
                                                obj.setAttribute("type", "password");
                                            })
                                        }

                                        return KCODE !== 40 && KCODE !== 38;
                                    }} ref={this.p} onFocus={()=>{
                                        const obj = this.p.current;
                                        setTimeout(()=>{
                                            obj.setAttribute("type", "password");
                                            obj.removeAttribute("readonly");
                                        });
                                    }} className="trade-pwd-inp" placeholder={intl.formatMessage({id: '请输入您的资金密码'})} id="safePwd" name="safePwd" onChange={this.updataSafePwd.bind(this)} />

                                </div>

                                <div style={{"marginBottom":"15px"}}>
                                    {/* {
                                        ppwLock?
                                            <a className="reset-link pull-right my-dis" onClick={(e) =>e.preventDefault()} ><FormattedMessage id="重置资金密码trade"/></a>
                                            :
                                            <Link className={`reset-link pull-right`} to={"/bw/mg/resetPayPwd"} ><FormattedMessage id="重置资金密码trade"/></Link>
                                    } */}
                                    <Link className={`reset-link pull-right ${!this.state.isEnableRs || this.state.setPwd||ppwLock ? 'stopLink' : ''}`} to={(this.state.isEnableRs && !this.state.setPwd&&!ppwLock)? "/bw/mg/resetPayPwd" : ""} style={{[(!this.state.isEnableRs || this.state.setPwd||ppwLock)?'color':'']:'#737A8D'}}><FormattedMessage id="重置资金密码trade"/></Link>
                                    <div style={{"clear":"both"}}></div>
                                </div>
                                <div className="trade-radio-box mb20" style={{position:'relative'}}>
                                    {!flag?
                                        <label className={`trade-label disab`}>
                                            <div className="user-defined"><span className="circle"></span></div>
                                            <FormattedMessage id="始终开启"/>
                                        </label> :
                                        <label className={`trade-label ${verifyStatus == 0?'active':''}`} onClick={() =>{this.getRadioValue(0)}}>
                                            <div className="user-defined"><span className="circle"></span></div>
                                            <FormattedMessage id="始终开启"/>
                                        </label>
                                    }

                                    {
                                        !flag?
                                            <label className={`trade-label disab`}>
                                                <div className="user-defined"><span className="circle"></span></div>
                                                <FormattedMessage id="sellbuy.p5"/>
                                            </label>:
                                            <label className={`trade-label ${verifyStatus == 6?'active':''}`} onClick={() =>{this.getRadioValue(6)}}>
                                                <div className="user-defined"><span className="circle"></span></div>
                                                <FormattedMessage id="sellbuy.p5"/>
                                            </label>
                                    }
                                    {
                                        !flag?
                                            <label className={`trade-label disab`}>
                                                <div className="user-defined"><span className="circle"></span></div>
                                                <FormattedMessage id="始终关闭"/>
                                            </label>:
                                            <label className={`trade-label ${verifyStatus == 1?'active':''}`} onClick={() =>{this.getRadioValue(1)}}>
                                                <div className="user-defined"><span className="circle"></span></div>
                                                <FormattedMessage id="始终关闭"/>
                                            </label>

                                    }

                                    {/* 价格阻挡层 */}
                                    {
                                        !flag?<div style={{width:'100%',height:'100%',position:'absolute'}} onClick={(e) => e.preventDefault()}>
                                        </div>:null
                                    }


                                </div>
                                <input type="hidden" id="needMobile" name="needMobile" value="false"/>
                                <input type="hidden" id="needPwd" name="needPwd" value="true"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="foot">
                    <a id="JuaBtn_8_2" role="button" className="btn btn-outgray btn-sm" onClick={() => this.setState({closeStatu:verifyStatus},() => this.modal.closeModal())}><FormattedMessage id="cancel"/></a>
                    <a id="JuaBtn_8_1" role="button" className="btn btn-primary btn-sm" onClick={() => this.changeIsSafePwd()}><FormattedMessage id="user.text20"/></a>
                </div>
                <div className="zoom"></div>
            </div>

            this.setState({modalHTML:str},()=>{
                this.modal.openModal();
            })
            radioSwitch('trade-label')

        });
    }
    // 修改委托方式触发
    changeSelectType(num) {
        this.setState({selectType:num})
    }

    // 文本框输入触发
    changeValue(unit, event){
        try{
            let val = this.checkNumber(event.target.value,unit)
            let isBuy = this.props.isBuy
            BigNumber.RM = 0
            let targetId = event.target.getAttribute("data-id")
            if(targetId == 'buyUnitPrice'){
                let total  = new BigNumber(this.state.amount?this.state.amount:0).times(val?val:0).toFixed(EXHANGETOTALDIAN)
                this.setState({
                    price:val,
                    total:total
                })
            }else if(targetId == 'buyNumber'){
                let total  = new BigNumber(this.state.price?this.state.price:0).times(val?val:0).toFixed(EXHANGETOTALDIAN)
                this.setState({
                    amount:val,
                    total:total
                })

            }else if(targetId == 'realBuyAccount'){
                let am = new BigNumber(val?val:0).div(this.state.price).toFixed(this.marke.numberBixDian)
                this.setState({
                    amount:am,
                    total:val
                })
            }else if(targetId == 'buyMaxPrice'){
                this.setState({
                    maxPrice:val,
                })

            }else if(targetId == 'buyTriggerPrice'){
                this.setState({
                    triggerPrice:val,
                })
            }else if(targetId == 'buyPlanPrice'){
                let total  = new BigNumber(this.state.planAmount?this.state.planAmount:0).times(val?val:0).round(this.marke.exchangeBixDian,3).toFixed(EXHANGETOTALDIAN)
                this.setState({
                    planPrice:val,
                    planTotal:total
                })
            }else if(targetId == 'buyPlanNumber'){
                let total  = new BigNumber(this.state.planPrice?this.state.planPrice:0).times(val?val:0).round(this.marke.exchangeBixDian,3).toFixed(EXHANGETOTALDIAN)
                this.setState({
                    planAmount:val,
                    planTotal:total
                })
            }else if(targetId == 'buyPlanMoney'){
                let am = new BigNumber(val?val:0).div(this.state.planPrice).toFixed(this.marke.numberBixDian)
                this.setState({
                    planAmount:am,
                    planTotal:val
                })
            }
        }catch(e){

        }
    }
    //计算价格 的 法币折算
    takeLagelMoney(num){
        try{
            const { marketinfo,moneyLocal } = this.props

            if(marketinfo.data){
                let marketCoin = marketinfo.currentMarket.split('_')[1]
                if(marketCoin&&num){
                    BigNumber.RM = 0;
                    marketCoin = marketCoin=="usdt"?"usd":marketCoin;
                    const legalMon = marketinfo.data[`exchangeRate${marketCoin.toUpperCase()}`][moneyLocal.name]
                    const legalMonNum = new BigNumber(num).times(legalMon).toFixed(2)
                    return `${legalMonNum}  ${moneyLocal.name}`

                }else{
                    return "0.00 " + moneyLocal.name
                }
            }
        } catch(e){

        }
    }

    // 输入框规则校验
    checkNumber (value,unit) {
        if (value != "") {
            if (this.isNumber(value)) {
                var valueStr = value + "";
                if (valueStr.indexOf(".") != -1) {
                    var newStr,
                        intStr = valueStr.split(".")[0] + "",
                        floatStr = valueStr.split(".")[1] + "";
                    if (floatStr.split("").length > unit) {
                        newStr = intStr + "." + floatStr.substr(0, unit);
                        value = newStr;
                    }
                }
            }else{
                value = ''
            }
        }
        try{

        }catch(e){

        }

        return value
    }

    // 提交委托成功后清空文本框
    resetInput(isSuc){
        if(isSuc){// 提交成功后重置所有 提交失败只重置按钮的文字和提交状态
            this.setState({
                maxPrice:'',
                amount:'',
                total:'',
                triggerPrice:'',
                planPrice:'',
                planAmount:'',
                planTotal:'',
                btnText:'',
                isloading:false,
                safePwd:'',
                //parentPrice:0,
                parentAmount:0,
            })
        }else{
            this.setState({
                isloading:false,
                safePwd:'',
                btnText:this.props.isBuy?<FormattedMessage id="sellbuy.BUY"/>:<FormattedMessage id="sellbuy.SELL"/>
            })
        }

    }

    // 判断字符串是否为数字
    isNumber(val) {
        var re = /^[0-9]+\.?[0-9]*$/;  //判断正整数 /^[1-9]+\.?[0-9]*]*$/
        if (!re.test(val)) {
            return false
        }
        return true
    }

    // 提交委托前处理函数
    doEntrust(){
        if (!this.props.user){
            let str = <div className="Jua-table-inner Jua-table-main ">
                <div className="head react-safe-box-head">
                    <h3 className="tc"><FormattedMessage id="sellbuy.p8"/></h3>
                </div>

                <div className="foot">
                    <a id="JuaBtn_8_2" role="button" className="btn btn-outgray btn-sm" onClick={() => this.modal.closeModal()}><FormattedMessage id="cancel"/></a>
                    <a id="JuaBtn_8_1" role="button" className="btn btn-primary btn-sm" onClick={()=>{this.modal.closeModal();window.location.href = formatURL('/login')}}><FormattedMessage id="去登录"/></a>
                </div>
                <div className="zoom"></div>
            </div>;

            this.setState({modalHTML:str},()=>{
                this.modal.openModal();
            })
            return;
        }
        // if(this.state.isloading) return optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.p9"}),undefined,true) // 正在提交中提示
        if(this.state.isloading) return trade_pop({
            msg: this.props.intl.formatMessage({id:"sellbuy.p9"}),
            style: 1,
        })// 正在提交中提示

        if(this.reConfirm()) {
            if(this.state.selectType == 1){
                this.checkSafePwd()
            }else{
                this.openConfirmModal()
            }

        }
    }

    // 如果买入或者卖出数量小于可用数量弹出提示充值或划转
    ctCoin(coinType = '', type = 0){

        let str = <div className="Jua-table-inner Jua-table-main ">
            <div className="head react-safe-box-head">
                <h3 className="tc"><FormattedMessage id={type === 0 ? '您的可用资金不足' : 'sellbuy.a4'}/></h3>
            </div>

            <div className="foot">
                <a id="JuaBtn_8_2" role="button" className="btn btn-primary btn-sm bbyh-btns" onClick={() => {browserHistory.push(formatURL('/manage/account/charge?coint='+coinType))}}><FormattedMessage id="去充值"/></a>
                {/* <a id="JuaBtn_8_1" role="button" className="btn btn-primary btn-sm" onClick={()=>{

                    this.setState({modalHTML:<Transfer closeModal={this.modal.closeModal} fromtype={1} totype={2} fundsType={coinType}/> })

                }}><FormattedMessage id="去划转"/></a> */}
            </div>
            <div className="zoom"></div>
        </div>;

        this.setState({modalHTML:str},()=>{
            this.modal.openModal();
        })
    }

    // 验证提交信息
    reConfirm() {
        let { assetsDetail, isBuy} = this.props;
        let price = parseFloat(this.state.price),
            maxPrice = parseFloat(this.state.maxPrice),
            amount = parseFloat(this.state.amount),
            total = parseFloat(this.state.total),
            triggerPrice = parseFloat(this.state.triggerPrice),
            planPrice = parseFloat(this.state.planPrice),
            planAmount = parseFloat(this.state.planAmount),
            planTotal = parseFloat(this.state.planTotal),
            market = this.props.marketinfo.currentMarket,
            marketCoin = market.toUpperCase().split('_')[1], // 当前选中的市场（usdt btc）
            choseCoin = market.toUpperCase().split('_')[0],  // 当前选择的币种
            marketsInfo = this.props.marketsConfData[market],
            maxPriceConf = marketsInfo.maxPrice;
        let canUseMoney = parseFloat(this.canUseMoney.current.innerHTML);
        try{
            if(isBuy){
                canUseMoney = new BigNumber(assetsDetail[marketCoin]['balance']).toFixed(this.marke.exchangeBixDian);  // 余额
            }else{
                canUseMoney = new BigNumber(assetsDetail[choseCoin]['balance']).toFixed(this.marke.numberBixDian);
            }
        } catch(e){

        }
        switch (this.state.selectType) {
            case 0: // 限价委托
                if (!price || price == 0) {
                    this.buyUnitPrice.current.focus();
                    //this.props.notifSend(<FormattedMessage id="sellbuy.a1"/>,'warning')
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a1"}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"sellbuy.a1"}),
                        style: 1,
                    });
                    return false
                }
                if(price > maxPriceConf){
                    this.buyUnitPrice.current.focus();
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"委托价格偏离市场价格过高，请核实后再次尝试。"}),undefined,true)
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"委托价格偏离市场价格过高，请核实后再次尝试。"}),
                        style: 1,
                    });
                    // this.props.notifSend(<FormattedMessage id="委托价格偏离市场价格过高，请核实后再次尝试。"/>,'warning')
                    return false
                }
                if (!amount || amount == 0) {
                    this.buyNumber.current.focus();
                    //this.props.notifSend(<FormattedMessage id="sellbuy.a2"/>,'warning')
                    // optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a2"}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"sellbuy.a2"}),
                        style: 1,
                    });
                    return false
                }
                if(amount < marketsInfo.bixMinNum){
                    // optPop(()=>{}, this.props.intl.formatMessage({id:"委托数量不得低于XX"},{num:marketsInfo.bixMinNum,coin:choseCoin}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"委托数量不得低于XX"},{num:marketsInfo.bixMinNum,coin:choseCoin}),
                        style: 1,
                    });
                    return false
                }
                if(amount > marketsInfo.bixMaxNum){
                    // optPop(()=>{}, this.props.intl.formatMessage({id:"委托数量不得高于XX"},{num:marketsInfo.bixMaxNum,coin:choseCoin}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"委托数量不得高于XX"},{num:marketsInfo.bixMaxNum,coin:choseCoin}),
                        style: 1,
                    });
                    return false
                }
                if(this.props.isBuy){
                    if (total > canUseMoney) {
                        // this.props.notifSend(<FormattedMessage id="sellbuy.a3" values={{num:total,bic:marketCoin}}/>,'warning')
                        this.ctCoin(marketCoin, 0)
                        return false
                    }
                }else{
                    if (amount > canUseMoney) {
                        this.buyNumber.current.focus();
                        // this.props.notifSend(<FormattedMessage id="sellbuy.a4" values={{num:amount,bic:choseCoin}}/>,'warning')
                        this.ctCoin(choseCoin, 1)
                        return false
                    }
                }

                break;
            case 1: // 计划委托

                if (!triggerPrice || triggerPrice == 0) {
                    this.buyTriggerPrice.current.focus();
                    // this.props.notifSend(<FormattedMessage id="sellbuy.a5"/>,'warning')
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a5"}),undefined,true)
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"sellbuy.a5"}),
                        style: 1,
                    });
                    return false
                }
                if (!planPrice || planPrice == 0) {
                    this.buyPlanPrice.current.focus();
                    // this.props.notifSend(<FormattedMessage id="sellbuy.a1"/>,'warning')
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a1"}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"sellbuy.a1"}),
                        style: 1,
                    });
                    return false
                }
                if(planPrice > maxPriceConf){
                    this.buyUnitPrice.current.focus();
                    // this.props.notifSend(<FormattedMessage id="委托价格偏离市场价格过高，请核实后再次尝试。"/>,'warning')
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"委托价格偏离市场价格过高，请核实后再次尝试。"}),undefined,true)
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"委托价格偏离市场价格过高，请核实后再次尝试。"}),
                        style: 1,
                    });
                    return false
                }
                if (!planAmount || planAmount == 0) {
                    this.buyPlanNumber.current.focus();
                    // this.props.notifSend(<FormattedMessage id="sellbuy.a2"/>,'warning')
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a2"}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"sellbuy.a2"}),
                        style: 1,
                    });
                    return false
                }
                if(planAmount < marketsInfo.bixMinNum){
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"委托数量不得低于XX"},{num:marketsInfo.bixMinNum,coin:choseCoin}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"委托数量不得低于XX"},{num:marketsInfo.bixMinNum,coin:choseCoin}),
                        style: 1,
                    });
                    return false
                }
                if(planAmount > marketsInfo.bixMaxNum){
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"委托数量不得高于XX"},{num:marketsInfo.bixMaxNum,coin:choseCoin}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"委托数量不得高于XX"},{num:marketsInfo.bixMaxNum,coin:choseCoin}),
                        style: 1,
                    });
                    return false
                }
                if(this.props.isBuy){
                    if (planTotal > canUseMoney) {
                        //this.props.notifSend(<FormattedMessage id="sellbuy.a3" values={{num:planTotal,bic:marketCoin}}/>,'warning')
                        this.ctCoin(marketCoin, 0)
                        return false
                    }
                }else{
                    if (!planAmount || planAmount > canUseMoney) {
                        this.buyPlanNumber.current.focus();
                        //this.props.notifSend(<FormattedMessage id="sellbuy.a4" values={{num:planAmount,bic:choseCoin}}/>,'warning')
                        this.ctCoin(choseCoin, 1)
                        return false
                    }
                }
                break;

            case 2: // 批量委托
                if (!maxPrice || maxPrice == 0) {
                    this.buyMaxPrice.current.focus();
                    //this.props.notifSend(<FormattedMessage id="sellbuy.a1"/>,'warning')
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a1"}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"sellbuy.a1"}),
                        style: 1,
                    });
                    return false
                }
                if (!price || price == 0) {
                    this.buyUnitPrice.current.focus();
                    //this.props.notifSend(<FormattedMessage id="sellbuy.a1"/>,'warning')
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a1"}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"sellbuy.a1"}),
                        style: 1,
                    });
                    return false
                }
                if (!amount || amount == 0) {
                    this.buyNumber.current.focus();
                    //his.props.notifSend(<FormattedMessage id="sellbuy.a2"/>,'warning')
                    //optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a2"}),undefined,true);
                    trade_pop({
                        msg: this.props.intl.formatMessage({id:"sellbuy.a2"}),
                        style: 1,
                    });
                    return false
                }
                if(this.props.isBuy){
                    if (price - maxPrice > 0) {
                        // this.props.notifSend(<FormattedMessage id="sellbuy.a7"/>,'warning')
                        //optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a7"}),undefined,true)
                        trade_pop({
                            msg: this.props.intl.formatMessage({id:"sellbuy.a7"}),
                            style: 1,
                        });
                        return false
                    }
                    try{
                        let tot = new BigNumber(maxPrice * amount).toFixed(this.marke.exchangeBixDian)
                        if(tot > canUseMoney){
                            this.ctCoin(marketCoin, 0)
                            //this.props.notifSend(<FormattedMessage id="sellbuy.a3" values={{num:tot,bic:marketCoin}}/>,'warning')
                            return false
                        }
                    } catch(e){

                    }
                    return false;
                }else{
                    if (price - maxPrice > 0) {
                        // this.props.notifSend(<FormattedMessage id="sellbuy.a8"/>,'warning')
                        //optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a8"}),undefined,true)
                        trade_pop({
                            msg: this.props.intl.formatMessage({id:"sellbuy.a8"}),
                            style: 1,
                        });
                        return false
                    }
                    if(amount > canUseMoney){
                        this.ctCoin(choseCoin, 1)
                        // this.props.notifSend(<FormattedMessage id="sellbuy.a4" values={{num:amount,bic:choseCoin}}/>,'warning')
                        return false
                    }
                }
                break;
            default:
                break;
        }
        return true
    }

    // 判断此次提交是否需要安全密码
    checkSafePwd(){
        try{
            this.props.fetchHasSafePwd().then((res) => { // 获取是否需要安全密码
                let needSafeWord = eval(res["data"]).des
                if(needSafeWord !='false'){
                    this.openSafePwdModal()
                }else{
                    this.doSubmit()
                }
            });
        }catch(e){}
    }

    // 修改安全认证的模式（每次都认证，6小时内不需要认证，永不认证）
    changeIsSafePwd(){
        if (this.state.safePwd == "") {
            optPop(()=>{}, this.props.intl.formatMessage({id:"请输入您的资金密码"}), undefined,true)
            return ;
        };
        let _lan = cookie.get('zlan');
        let _lang;
        switch(_lan){
            case 'cn':
                _lang = 1
                break;
            case 'en':
                _lang = 2
                break;
            case 'hk':
                _lang = 0
                break;
            case 'jp':
                _lang = 3
                break;
            case 'kr':
                _lang = 4
                break;
            default:
                _lang = 2
                break;

        }
        axios.post(DOMAIN_VIP + "/manage/safePwdForEnturst", qs.stringify({
            payPass : encodeURIComponent(this.state.safePwd),
            lang:_lang
        })).then((res)=>{

            var data = res.data;
            //封修改资金密码
            let setPwd = data.datas.ashLockStatus ? true : false;
            this.setState({
                setPwd
            },() => this.openSafePwdModal(setPwd))
            //debugger;
            if(data.isSuc){
                const { closeStatu } = this.state

                // if(closeStatu!=''){
                // debugger;
                axios.get(DOMAIN_VIP + "/login/getPubTag?t=" + new Date().getTime()).then((res)=>{
                    encrypt.setPublicKey(res.data.datas.pubTag);
                    axios.post(DOMAIN_VIP + "/manage/useOrCloseSafePwd", qs.stringify({
                        payPass: encrypt.encrypt(this.state.safePwd),
                        closeStatu,
                        needMobile: false,
                        needPwd: true
                    }))
                        .then((resdata) =>{
                            // if(resdata.data.isSuc){
                            //     //this.doSubmit()  //提交信息
                            //     //资金密码输入正确，切换模式成功，关闭弹框


                            //     // optPop(() =>{},resdata.data.des,{timer: 1500},true)
                            // }else{
                            //      //错误第五次的时候关闭弹窗
                            //     if(resdata.data.des.indexOf('_5')!= -1){
                            //         // let newdes = resdata.data.des.slice(0,-2);
                            //         // optPop(() =>{},newdes,{timer: 1500},true);
                            //         this.props.getUserBaseInfo()//刷新数据关闭弹窗
                            //         // this.modal.closeModal();
                            //     }else{
                            //         // optPop(() =>{},resdata.data.des,{timer: 1500},true)
                            //         this.props.getUserBaseInfo() //刷新数据，不关闭弹窗
                            //     }
                            // }
                            this.props.getUserBaseInfo()//刷新数据关闭弹窗
                            //this.modal.closeModal();
                            this.doSubmit()
                        })

                })

                // }

            }else{
                //this.props.notifSend(data.des,'warning')
                optPop(()=>{}, data.des,undefined,true)
            }
        })
    }

    // 验证完毕开始提交
    doSubmit(){
        this.modal.closeModal()
        this.setState({btnText:<FormattedMessage id="sellbuy.a10"/>,isloading:true})
        let price = parseFloat(this.state.price),
            maxPrice = parseFloat(this.state.maxPrice),
            amount = parseFloat(this.state.amount),
            total = parseFloat(this.state.total),
            triggerPrice = parseFloat(this.state.triggerPrice),
            planPrice = parseFloat(this.state.planPrice),
            planAmount = parseFloat(this.state.planAmount),
            planTotal = parseFloat(this.state.planTotal);

        let market = this.props.marketinfo.currentMarket
        let marketCoin = market.toUpperCase().split('_')[1] // 当前选中的市场（usdt btc）
        let choseCoin = market.toUpperCase().split('_')[0]  // 当前选择的币种
        let entrustUrlBase = this.props.marketsConfData[market]['entrustUrlBase']
        switch (this.state.selectType) {
            case 0:
                this.props.fetchLimitPriceEntrust(encodeURIComponent(this.state.safePwd),price,amount,this.props.isBuy,entrustUrlBase,market).then(res=>{
                    let re = res.data
                    if(re.isSuc){
                        this.props.fetchAssetsDetail()
                        console.log(123,'===>');
                        //optPop(()=>{}, re.des)
                        trade_pop({
                            msg: re.des
                        })
                    }else{
                        trade_pop({
                            msg: re.des,
                            style: 1,
                        })
                        // optPop(()=>{}, re.des,undefined,true)
                    }
                    this.resetInput(re.isSuc)
                })
                break;
            case 1:
                let planData
                if (this.props.isBuy == 1) {
                    planData = {
                        safePassword: encodeURIComponent(this.state.safePwd),
                        isBuy: this.props.isBuy,
                        buyPlanMoney: planTotal ? planTotal : 0,
                        buyTriggerPrice: triggerPrice ? triggerPrice : 0,
                        buyPlanPrice: planPrice ? planPrice : 0
                    }
                } else {
                    planData = {
                        safePassword: encodeURIComponent(this.state.safePwd),
                        isBuy: this.props.isBuy,
                        sellPlanNumber: planAmount ? planAmount : 0,
                        sellTriggerPrice: triggerPrice ? triggerPrice : 0,
                        sellPlanPrice: planPrice ? planPrice : 0
                    }
                }
                this.props.fetchPlanEntrust(planData,entrustUrlBase,market).then(res=>{
                    let re = res.data
                    if(re.isSuc){
                        this.props.fetchAssetsDetail()
                        trade_pop({
                            msg: re.des
                        })
                    }else{
                        trade_pop({
                            msg: re.des,
                            style: 1,
                        })
                        // optPop(()=>{}, re.des,undefined,true)
                    }
                    this.resetInput(re.isSuc)
                })
                break;
            case 2:
                this.props.fetchBatchEntrust(encodeURIComponent(this.state.safePwd),price,maxPrice,amount,this.props.isBuy,entrustUrlBase,market).then(res=>{
                    let re = res.data
                    if(re.isSuc){
                        this.props.fetchAssetsDetail()
                        var data = re.des.split(":");
                        this.props.intl.formatMessage({id:"sellbuy.a11"}, {data0:data[0],data1:data[1],data2:data[2],choseCoin:choseCoin,marketCoin:marketCoin})
                    }else{
                        // optPop(()=>{}, this.props.intl.formatMessage({id:"sellbuy.a12"}), undefined,true)
                        trade_pop({
                            msg: this.props.intl.formatMessage({id:"sellbuy.a12"}),
                            style: 1,
                        });
                    }
                    this.resetInput(re.isSuc)
                })
                break;

            default:
                break;
        }
    }

    // 点击余额自动填充文本框
    setBalance(balance,Proportion){
        try{
            const { price } = this.state
            if(!price){
                return
            }

            if(this.props.isBuy){
                let am = new BigNumber(balance).times(Proportion).div(price).toFixed(this.marke.numberBixDian);
                let balancePro = new BigNumber(price).times(am).toFixed(EXHANGETOTALDIAN);

                this.setState({
                    amount:am,
                    total:balancePro
                })
            }else{
                let balancePro = new BigNumber(balance).times(Proportion).toFixed(this.marke.numberBixDian);
                let total  = new BigNumber(this.state.price).times(balancePro).toFixed(EXHANGETOTALDIAN);
                this.setState({
                    amount:balancePro,
                    total:total
                })
            }
        }catch(e){

        }
    }

    // render前 数据格式化
    dataFormat(marketinfo,assetsDetail){
        let result = {}
        let coin = marketinfo.currentMarket.toUpperCase() // 币种全称（btc_usdt）
        const { price,amount,total,maxPrice,triggerPrice,planPrice,planAmount,planTotal } = this.state
        BigNumber.RM = 0;
        result.marketCoin = coin.split('_')[1] // 当前选中的市场（usdt btc）
        result.choseCoin = coin.split('_')[0]  // 当前选择的币种

        try{
            if(this.props.isBuy){ // 买入
                if(assetsDetail){
                    result.balance = new BigNumber(assetsDetail[coin.split('_')[1]]['balance']).toFixed(this.marke.exchangeBixDian)  // 余额
                }
                result.btn = this.state.btnText ==''?<FormattedMessage id="sellbuy.BUY"/>:this.state.btnText
            } else{ // 卖出
                if(assetsDetail){
                    result.balance = this.checkNumber(assetsDetail[coin.split('_')[0]]['balance'],this.marke.numberBixDian)  // 余额
                }
                result.btn = this.state.btnText ==''?<FormattedMessage id="sellbuy.SELL"/>:this.state.btnText
            }
        } catch(e){
            result.balance  = new BigNumber(0).toFixed(this.marke.exchangeBixDian);
            result.btn = this.state.btnText ==''?<FormattedMessage id="sellbuy.BUY"/>:this.state.btnText
        }
        if(result.balance){
            let value = result.balance + "";
            value = (value.lastIndexOf(".") === value.length - 1 ? value.substring(0, value.length-1) : value);
            result.balance = value;
        }

        result.price = this.checkNumber(price,this.marke.exchangeBixDian)
        result.amount = this.checkNumber(amount,this.marke.numberBixDian)
        result.total = total?new BigNumber(total).toFixed(COIN_KEEP_POINT):total
        result.maxPrice = this.checkNumber(maxPrice,this.marke.exchangeBixDian)
        result.triggerPrice = this.checkNumber(triggerPrice,this.marke.exchangeBixDian)
        result.planPrice = this.checkNumber(planPrice,this.marke.exchangeBixDian)
        result.planAmount = planAmount
        result.planTotal = planTotal?new BigNumber(planTotal).toFixed(COIN_KEEP_POINT):planTotal
        return result
    }

    // 鼠标经过显示下拉框
    showSelect(event) {
        const fieldInput = this.refs.fieldInput;
        fieldInput.closeMenu();
        fieldInput.handleMouseDown(event);
    }
    // 鼠标离开隐藏下拉框
    hideSelect(event) {
        const fieldInput = this.refs.fieldInput;
        fieldInput.closeMenu();
    }
    render() {
        const { isBuy, user,marketsConfData,marketinfo,assetsDetail,intl} = this.props;
        this.marke = marketsConfData[marketinfo.currentMarket];
        if(!this.marke) return (<div className="sellBuloading">...LOADING</div>);
        const result = this.dataFormat(marketinfo,assetsDetail);


        console.log(this.props)
        let {feesVal,formTab} = this.props;
        let BigTop = BigNumber()
        BigTop.RM = 3
        return (
            <div className={isBuy?"buy":"sell"}>
                <div className="trade-content bk-trans-form ">
                    {user?
                        <p className="haveUser">
                            <span>
                                <span className="up-ha"><FormattedHTMLMessage id="XXX可用" values={{coin:isBuy?result.marketCoin:result.choseCoin}}/>:</span>
                                <span className="up-hb">
                                    <em className="balance-a" ref={this.canUseMoney} onClick={this.setBalance.bind(this,result.balance,1)}>
                                        {result.balance?result.balance:"0.0"}
                                    </em>
                                </span>
                            </span>
                            {false && isBuy&&result.marketCoin!="USDT"?<Link to={'/bw/manage/account/charge?coint='+result.marketCoin} target="_blank">
                                <FormattedMessage id="Deposit"/>
                            </Link>:""}
                            {false && !isBuy?<Link to={'/bw/manage/account/charge?coint='+result.choseCoin} target="_blank">
                                <FormattedMessage id="Deposit"/>
                            </Link>:""}

                            {
                                assetsDetail && assetsDetail[result[isBuy ? 'marketCoin' : 'choseCoin']] && assetsDetail[result[isBuy ? 'marketCoin' : 'choseCoin']].canCharge
                                    ?
                                    <Link to={'/bw/manage/account/charge?coint='+(isBuy?result.marketCoin:result.choseCoin)} target="_blank">
                                        <FormattedMessage id="Deposit"/>
                                    </Link>:""
                            }
                        </p>
                        :
                        isBuy ?
                            <p className="haveUser haveUser-nologin">
                            <span>
                                <Link to='/bw/login'><FormattedMessage id="Login" /></Link>  <FormattedMessage id="or"/>  <Link to='/bw/signup/'><FormattedMessage id="OpenAccount" /></Link>  <FormattedMessage id="toTrade" />
                            </span>
                            </p>
                            :
                            null

                    }

                    <div id="buyDefaultForm" className="clearfix" style={{display:this.state.selectType == 1?'none':'block'}}>
                        <div className="form-row">
                            <div className="form-group" >
                                <input type="text" placeholder={result.price==''?intl.formatMessage({id: '请输入价格'}): ""} data-id="buyUnitPrice" ref={this.buyUnitPrice} name="buyUnitPrice" maxLength="15" value={result.price} autoComplete='off' onChange={this.changeValue.bind(this,this.marke.exchangeBixDian)}/>
                                <span className="trade-input-desc">{result.marketCoin}</span>
                            </div>
                        </div>
                        <div className="form-row">
                            ≈ {this.takeLagelMoney(result.price)}
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                <input type="text" placeholder={result.amount==''?intl.formatMessage({id: '请输入购买数量'}):''} data-id="buyNumber" ref={this.buyNumber} name="buyNumber" maxLength="15" value={result.amount} autoComplete='off' onChange={this.changeValue.bind(this,this.marke.numberBixDian)} />
                                <span className="trade-input-desc">{result.choseCoin}</span>
                            </div>
                        </div>
                        <div className="form-row buyProportion" style={{display:this.state.selectType==2?'none':'block'}}>
                            <div className="form-group bbyh-tradeEm">
                                <em onClick={this.setBalance.bind(this,result.balance,0.25)}>25%</em>
                                <em onClick={this.setBalance.bind(this,result.balance,0.5)}>50%</em>
                                <em onClick={this.setBalance.bind(this,result.balance,0.75)}>75%</em>
                                <em onClick={this.setBalance.bind(this,result.balance,1)}>100%</em>
                            </div>
                        </div>
                        <div className="form-row buyDefaultLabel" style={{display:this.state.selectType==2?'none':'block'}}>
                            <div className="form-group" >
                                <FormattedMessage id="总额"/>:&nbsp;<span className="num">{result.total||'0.00000000'} {result.marketCoin}</span>
                            </div>
                            {
                                false
                                &&
                                <div className="form-group fee" style={{display:'none'}}>
                                    <Link to="">
                                        <FormattedMessage id="手续费"/></Link>:&nbsp;<span className="num">{
                                    formTab ==1&&result.amount?
                                        //
                                        this.configFee(BigTop(result.amount).times(feesVal).div(100).toFixed(9))
                                        :formTab == 2&&result.total?
                                        this.configFee(BigTop(result.total).times(feesVal).div(100).toFixed(9)):'0.00000000'} {formTab == '1'? result.choseCoin : result.marketCoin}</span>
                                </div>
                            }
                        </div>
                    </div>

                    <div id="buyPlanForm" className="clearfix" style={{display:this.state.selectType == 1?'block':'none'}}>
                        <div className="form-row" style={{marginBottom:'3px'}}>
                            <div className="form-group ">
                                <input type="text" name="buyTriggerPrice" className="money-amount-text" autoComplete='off' data-id="buyTriggerPrice" ref={this.buyTriggerPrice} placeholder={intl.formatMessage({id: '请输入触发价格'})} maxLength="15" value={result.triggerPrice} onChange={this.changeValue.bind(this,this.marke.exchangeBixDian)}/>
                                <span className="trade-input-desc">{result.marketCoin}</span>
                            </div>
                        </div>
                        <div className="form-row">
                            <div className="form-group ">
                                <input type="text" name="buyPlanPrice" className="money-amount-text" autoComplete='off' data-id="buyPlanPrice" ref={this.buyPlanPrice} placeholder={intl.formatMessage({id: '请输入委托价格'})} maxLength="15" value={result.planPrice} onChange={this.changeValue.bind(this,this.marke.exchangeBixDian)}/>
                                <span className="trade-input-desc">{result.marketCoin}</span>
                            </div>
                        </div>
                        <div className="form-row">
                            ≈ {this.takeLagelMoney(result.planPrice)}
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                <input type="text" data-id="buyPlanNumber" autoComplete='off' placeholder={intl.formatMessage({id: '请输入购买数量'})} ref={this.buyPlanNumber} name="buyPlanNumber" maxLength="15" value={result.planAmount} onChange={this.changeValue.bind(this,this.marke.numberBixDian)} />
                                <span className="trade-input-desc">{result.choseCoin}</span>
                            </div>
                        </div>
                        <div className="form-row buyDefaultLabel">
                            <div className="form-group">
                                <FormattedMessage id="总额"/>:&nbsp;<span className="num">{result.planTotal||'0.00000000'} {result.marketCoin}</span>
                            </div>
                            {
                                false
                                &&
                                <div className="form-group" style={{display:'none'}}>
                                    <FormattedMessage id="手续费"/>:&nbsp;<span className="num">{
                                    formTab == 1&&result.planAmount?
                                        this.configFee(BigTop(result.planAmount).times(feesVal).div(100).toFixed(9))
                                        :formTab == 2&&result.planTotal?
                                        this.configFee(BigTop(result.planTotal).times(feesVal).div(100).toFixed(9)) : '0.00000000'} {formTab == '1'? result.choseCoin : result.marketCoin}</span>
                                </div>
                            }
                        </div>
                    </div>
                    <div className="form-row-btn" style={{marginTop:'0'}}>
                        <div className="button-box">
                            {
                                true ?
                                    <button className={`${isBuy?"btn-buy":"btn-sell"} ${result.marketCoin=="BTC"&&result.choseCoin=="ABCDE"?"disabled":""}`} onClick={this.doEntrust.bind(this,isBuy)} id="buyBtn">{result.btn}</button>
                                    :
                                    <div className={`${isBuy?"btn-buy":"btn-sell"} lk`}>
                                        <Link to='/bw/login'><FormattedMessage id="Login" /></Link> / <Link to='/bw/signup'><FormattedMessage id="OpenAccount" /></Link>
                                    </div>

                            }
                            <div className="zk"></div>
                        </div>
                    </div>
                </div>
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.modalHTML}
                </ReactModal>
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        userInfo: state.session.baseUserInfo,

    }
};
const mapDispatchToProps = {
    getUserBaseInfo,
    isCanJump

};



export default connect(mapStateToProps, mapDispatchToProps)(SellBuyForm);
