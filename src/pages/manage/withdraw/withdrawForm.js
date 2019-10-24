import React from 'react'
import axios from 'axios'
import qs from 'qs'
import Modal from 'react-modal';
import Form from '../../../decorator/form';
import HTab from '../../../components/tab/htab';
import Sms from '../../../components/user/sms';
import { connect } from 'react-redux'
import { optPop } from '../../../utils';
import Opt from '../../../components/msg/opt';
import { getUserBaseInfo ,withdrawal,Cashwithdrawal} from '../../../redux/modules/session';
import cookie from 'js-cookie';
import { FormattedMessage, injectIntl } from 'react-intl';
import { security } from '../../../components/tab/tabdata';
import { Link } from 'react-router';
import ReactModal from '../../../components/popBox'
import { COIN_KEEP_POINT,DISMISS_TIME,COUNTDOWN_INTERVAL,DOMAIN_VIP,COOKIE_UNAME} from '../../../conf'
import { chooseDownCoin,fetchWithdraw, } from '../../../redux/modules/withdraw'
import { isFloat } from '../../../utils'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend,notifClear,notifDismiss } = notifActions;
import { fetchSms } from '../../../redux/modules/withdraw'
const BigNumber = require('big.js')
import MoneyOpt from '../../../components/msg/moneyOpt'
const CODETYPE = 8;
@connect(
    state => ({session: state.session}),
    (dispatch) => {
        return {
            hideImgCode: () => {
                dispatch(hideImgCode());
            },

            doLogin: (receiveAddress, cashAmount,safePwd,mobileCode,googleCode,fees,fundsType,upperName,codeType) => {
                return dispatch(withdrawal({
                        receiveAddress,
                        cashAmount,
                        safePwd,
                        mobileCode,
                        googleCode,
                        fees,
                        fundsType,
                        upperName,
                        codeType
                    })
                )

            },
            doLogins:(value) =>{
                return dispatch(Cashwithdrawal({value})
                )
            }
        }
    }
)
@Form
class WithdrawForm extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            sendBtn:true,
            oneMinute:60,
            payPwd :"",
            gcode : "",
            cashAmount : "",
            Mstr:"",
            isGoogleOpen:true,
            isSMsOpen:true,
            memo:'',
            location:window.location.href,
            address:'',
            drawList:{}, // 提现说明
            codeType: CODETYPE,
            ckFlg: 0,
            token:"",
            selectedCode: '0',
            opt: -1,
            smscode:'',
            UpperCurrentCoin:'',
            actualAccount:'',
            addressId:'',
            focusBlu:'',
            isCanWithdraw:true,
            props: {
                ln: props.language === 'zh' ? 'cn' : props.language === 'en' ? 'en' : 'hk'
            },
            outConfirmTimes: 0,
            isShowStop:false,
            isEnableRs:1,               //是否启用重置按钮
            agree:null,
            descript:'',
            withdrawLength: 8,
        }
        this.tabConfig = security(props.intl);
        this.cutDigits = this.cutDigits.bind(this)
        this.checkNumber = this.checkNumber.bind(this)
        this.settime = this.settime.bind(this)
        this.submitDown = this.submitDown.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.cutDigits = this.cutDigits.bind(this)
        this.moadlSms = this.moadlSms.bind(this)
        this.maxDraw = this.maxDraw.bind(this)
        this.submitModal = this.submitModal.bind(this)
        this.makeSureSub = this.makeSureSub.bind(this)
        this.setSelected = this.setSelected.bind(this)
        this.setFoucs = this.setFoucs.bind(this)
        this.closeWithdraw = this.closeWithdraw.bind(this)
        this.iscanWithdraw = this.iscanWithdraw.bind(this)
        this.setLineWidth = this.setLineWidth.bind(this)
        this.close = this.close.bind(this)
        this.hasSalfClick = this.hasSalfClick.bind(this)


    }

    componentDidMount(){
        // init 调取用户信息
        axios.get(DOMAIN_VIP+'/manage/user', qs.stringify({})).then(r => {
            console.log(r.data.datas.isSmsOpen)
            this.setState({
                isSMsOpen: r.data.datas.isSmsOpen,
                isGoogleOpen: r.data.datas.isGoogleOpen,
            })
        })
        // init 重置资金密码trade
        axios.get(DOMAIN_VIP + '/manage/account/download/resetPayPwdAsh').then((res) => {
            this.setState({
                isEnableRs: !res.data.datas.ashLockStatus
            })
        });

        this.initGetCodes(['payPwd']);
        this.setState({
            memo:this.props.memo,
            address:this.props.address,
            UpperCurrentCoin:this.props.curCoin.toUpperCase()
        })
        const addressId = this.props.location.query.addressId
        this.setState({
            addressId
        })
        const agree = this.props.location.query.agree
        this.setState({
            agree
        })
        let {withdrawLength} = this.state;
        if (agree == 102) { // usdt ERC20 协议 提现长度处理 目前是写死 agree 102
            withdrawLength = 6;
            this.setState({
                withdrawLength
            })
        }
        const queryCoin = this.props.location.query.coint
        if(queryCoin != this.props.curCoin){
            this.props.chooseDownCoin(queryCoin)
        }
        axios.post(DOMAIN_VIP+"/manage/account/download/downloadDetailsJson",qs.stringify({
            addressId,
            coint:queryCoin
        })).then(res => {
            console.log('==========>>>>>>>>>>' + res);
            try{
            const result = res.data
            let rt = -1;
            if(result.datas.googleOpen&&result.datas.smsOpen){
                rt = 2;
            } else if(result.datas.googleOpen){
                rt = 0;
            } else if(result.datas.smsOpen){
                rt = 1;
            }
            let _balance = new BigNumber(result.datas.balance).toFixed(8)
            const drawList = Object.assign({},result.datas,{
                balance:_balance
            })
            // console.log(drawList)
            // console.log(result)
            this.setState({
                opt:rt,
                selectedCode: '' + (rt === 2 || rt === 0 ? 0 : (rt === 1 ? 1 : -1)),
                drawList:drawList,
                outConfirmTimes: result.datas.outConfirmTimes,
            },() =>{
                    this.getAdress()
                })

                if (agree == 102){
                    this.setState({
                        drawList:{
                            ...drawList,
                            fees:drawList.usdtefees,
                            minD:drawList.usdteminD
                        }
                    })
                }
            }catch(e){}
        })
        this.iscanWithdraw(queryCoin)
    }
    componentWillReceiveProps(nextProps){
        const thisCoin = this.props.location.query.coint
        const nextCoin = this.props.location.query.coint
        if(thisCoin != nextCoin){
            this.iscanWithdraw(nextCoin)
        }
    }
    setSelected(flg){
        this.setState({
            selectedCode: flg
        });
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

    //全部提现
    maxDraw(){
        const { balance,fees } = this.state.drawList;
        // let  _balance = Number(balance).toFixed(8);
        //      _balance = _balance.substring(0, _balance.indexOf(".") + 9);
        console.log(fees)
        this.setState({
            cashAmount:balance,
            actualAccount:new BigNumber(balance).minus(new BigNumber(fees))
        },()=> console.log(this.state.actualAccount))
    }
    setCk(){
        !this.ckFlg &&
        this.setState({
            ckFlg: true
        });

    }
    setFoucs(name){
        this.setState({
            focusBlu:name
        })
    }
    //提币数量限制
    checkNumber(e){
        // console.log(e.target.value.replace(/[^\d]/g,''))
        let {withdrawLength} = this.state;
        let value = e.target.value
        let unit = COIN_KEEP_POINT;
        const { balance,fees } = this.state.drawList
        let reg = /[^0-9.]/g;
        if(value < 0||value==''||reg.test(value)||!isFloat(value)){
            this.setState({
                cashAmount:'',
            })
        }else if((e.keyCode>=48&&e.keyCode<=57)||(e.keyCode>=96&&e.keyCode<=105)){
			BigNumber.RM = 0;
            let $thatVal = value;
			let $balance = Number(balance)
            let $fees = fees;
			if($thatVal > $balance){
                this.setState({
                    cashAmount:new BigNumber($balance).toFixed(withdrawLength),
                    actualAccount:new BigNumber($balance).minus(new BigNumber($fees))
                })
			}else{
				if($thatVal.toString().split(".")[1]&&$thatVal.toString().split(".")[1].length>withdrawLength){
                    $thatVal = 	new BigNumber($thatVal).toFixed(withdrawLength)
                    this.setState({
                        cashAmount:$thatVal,
                        actualAccount:new BigNumber($thatVal).minus(new BigNumber($fees))
                    })
                }

            }

        }

    }

    //点击过安全验证
    hasSalfClick(){
        console.log(this.state.location)
        cookie.set("tz",this.state.location)
    }

    //截取几位小数位数
    cutDigits(num,few=COIN_KEEP_POINT){
        BigNumber.RM = 0;
        const nums = new BigNumber(num).toFixed(few);
        return nums;
    }

    //短信弹窗
    moadlSms(des){
        let str = <div className="modal-btn">
            <p>{des}</p>
            <div className="modal-foot">
                <a className="btn ml10" onClick={() => this.modal.closeModal()}><FormattedMessage id='withdraw.text49' /></a>
            </div>
        </div>;
        this.setState({
            Mstr:str
        })
        this.modal.openModal();
    }

    //发送短信验证码
    settime(){
        this.props.fetchSms((res) => {
            let result = res.data;
            // this.moadlSms(result.des)
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
                    },COUNTDOWN_INTERVAL)
                }
            }
        });
    }

    //提交 提币
    submitDown(e){
        const {isCanWithdraw} = this.state;
        if(!isCanWithdraw){
            this.setState({
                isShowStop:true
            })

            return false
        }

        e.preventDefault();
        const assetsDetail = this.props.assetsDetail
        const { formatMessage } = this.intl;
        let name = this.props.curCoin;
        let { minD,fees,payGoogleAuth,payMobileAuth,payEmailAuth,address,googleOpen} = this.state.drawList;
        // let submitStatus = 1;
        let formInfo = null,formInfoStr = "";
        const upperName = name.toUpperCase();
        let fundsType = assetsDetail[upperName].fundsType
        this.setCk();
        let mobileCode = '',
            googleCode = '';
        let alldata = []
        let submitDtata = {}
        if(this.state.opt=='-1'){
            submitDtata = {
                safePwd:this.state.payPwd,
                mobileCode:'',
                googleCode:'',
                codeType:CODETYPE,
                amount:this.state.cashAmount,
            }
            alldata = ['cashAmount','payPwd']
        }else if(this.state.opt=='2'){
            if(this.state.selectedCode=='1'){
                submitDtata = {
                    safePwd:this.state.payPwd,
                    mobileCode:this.state.smscode,
                    googleCode:'',
                    codeType:CODETYPE,
                    amount:this.state.cashAmount,
                }
                alldata = ['cashAmount','payPwd','smscode']
            }else if(this.state.selectedCode=='0'){
                submitDtata = {
                    safePwd:this.state.payPwd,
                    mobileCode:'',
                    googleCode:this.state.gcode,
                    codeType:CODETYPE,
                    amount:this.state.cashAmount,
                }
                alldata = ['cashAmount','payPwd','gcode']
            }
        }else if(this.state.opt=='0'){
            submitDtata = {
                safePwd:this.state.payPwd,
                mobileCode:'',
                googleCode:this.state.gcode,
                codeType:CODETYPE,
                amount:this.state.cashAmount,
            }
            alldata = ['cashAmount','payPwd','gcode']
        }else if(this.state.opt=='1'){
            submitDtata = {
                safePwd:this.state.payPwd,
                mobileCode:this.state.smscode,
                googleCode:'',
                codeType:CODETYPE,
                amount:this.state.cashAmount,
            }
            alldata = ['cashAmount','payPwd','smscode']
        }

        if(!this.hasError(alldata)){

            this.props.doLogin(address, this.state.cashAmount,this.state.payPwd,this.state.smscode,this.state.gcode,fees,fundsType,upperName,CODETYPE).then((res) => {
                res = eval(res.data);
                if(!res.isSuc){
                    // callError(key, res.des);
                    this.makeResult(res, true);
                } else {
                    this.token = res.des;
                    this.submitModal();
                    formInfo = Object.assign({},formInfo,{
                        receiveAddress:address,
                        cashAmount:this.state.cashAmount,
                        safePwd:this.state.payPwd,
                        mobileCode:this.state.smscode,
                        googleCode:this.state.gcode,
                        fees:fees,
                        fundsType,
                        upperName:upperName
                    })

                    this.setState({
                        formInfo
                    })
                }
            });
            // submitStatus = -1;
            //    axios.post(DOMAIN_VIP+`/manage/account/download/submit/${upperName}`,qs.stringify(submitDtata)).then(res => {
            //    try{
            //     res = eval(res.data);
            //         if(!res.isSuc){
            //             // callError(key, res.des);
            //             this.makeResult(res, true);
            //         } else {
            //             this.token = res.des;
            //             this.submitModal();
            //             formInfo = Object.assign({},formInfo,{
            //                 receiveAddress:address,
            //                 cashAmount:this.state.cashAmount,
            //                 safePwd:this.state.payPwd,
            //                 mobileCode:this.state.smscode,
            //                 googleCode:this.state.gcode,
            //                 fees:fees,
            //                 fundsType,
            //             })

            //             this.setState({
            //                 formInfo`
            //             })
            //         }
            //     }catch(e){}
            //    })


        }


    }
    //make sure download
    makeSureSub(){
        if(this.mkCkFlg){
            return;
        }
        this.mkCkFlg = 1;
        const {formInfo} = this.state
        formInfo.token = this.token;
        let self = this
        this.props.doLogins(formInfo).then((res) => {
            console.log(res);
            const {des,isSuc,datas} = res.data
            let kindInfo = isSuc? 'info':'warning'
            optPop(() => {
                this.mkCkFlg = 0;
            }, des, undefined, !isSuc);

            if(isSuc){
                this.setState({
                    receiveAddress : "",
                    cashAmount : "",
                    payPwd : "",
                    gcode : "",
                    memo:''
                })
                this.props.router.push(`/bw/manage/account/download?coint=${this.props.curCoin}`)
            }})

        // this.props.fetchWithdraw(formInfo,(res) => {
        //     console.log(res);
        //     const {des,isSuc,datas} = res.data
        //     let kindInfo = isSuc? 'info':'warning'
        //     optPop(() => {
        //         this.mkCkFlg = 0;
        //     }, des, undefined, !isSuc);

        //     if(isSuc){
        //         this.setState({
        //             receiveAddress : "",
        //             cashAmount : "",
        //             payPwd : "",
        //             gcode : "",
        //             memo:''
        //         })
        //         this.props.router.push(`/bw/manage/account/download?coint=${this.props.curCoin}`)
        //     }

        // })
    }
    //modal
    submitModal(){

        const { fees,memo,address,addressTag } = this.state.drawList
        const UpperCurrentCoin = this.props.curCoin.toUpperCase()
        const { cashAmount } = this.state
        let title = <div style={{padding: '40px 0 20px 0'}} className="tiltes_center"><FormattedMessage  id="提现确认" /></div>;
        let html_body = <div className="alertBox_text mb20">
            <div className="alert-left">
                <div className="mb10"><FormattedMessage  id="提现数量：" /></div>
                <div className="mb10"><FormattedMessage  id="网络手续费：" /></div>
                <div className="mb10"><FormattedMessage  id="yybh地址备注" /></div>
                {
                    addressTag&&
                    <div className="mb10"><FormattedMessage  id="yybh地址标签" /></div>
                }
                <div className="mb10"><FormattedMessage  id="withdraw.text15" /></div>
            </div>
            <div className="alert-right">
                {cashAmount?<div className="mb10">{cashAmount} {UpperCurrentCoin}</div>:<div className="mb10 hei28"></div>}
                {fees?<div className="mb10">{fees} {UpperCurrentCoin}</div>:<div className="mb10 hei28"></div>}
                {memo?<div className="mb10">{memo}</div>:<div className="mb10 hei28">--</div>}
                {addressTag && <div className="mb10">{addressTag}</div>}
                {address?<div className="mb10">{address}</div>:<div className="mb10 hei28"></div>}
            </div>

        </div>;
        let foot_btn = <div className="btns_div mt20">
            <span className="btn close_alertBox" onClick={this.modal.closeModal}><FormattedMessage  id="cancel" /></span>
            <span className="btn submit" onClick={this.makeSureSub}><FormattedMessage  id="sure" /></span>
        </div>;
        let modalHtml = <div className="alertBox">
            <div className="alertBox_back"></div>
            <div className="alertBox_body" style={{padding: '0 60px',borderRadius:0}}>
                {title}
                {html_body}
                {foot_btn}
            </div>
        </div>;
        this.setState({
            Mstr:modalHtml
        })
        this.modal.openModal();
    }

    //判断币种是否可提现
    iscanWithdraw(coinType){
        try{
            //判断币种是否可充值
            axios.get(DOMAIN_VIP+'/manage/isCanOper?' + qs.stringify({coinName:coinType}))
                .then(res => {
                    let _data = res.data.datas;
                    console.log(_data)
                    let {canWithdraw} = _data;
                    this.setState({
                        isCanWithdraw:canWithdraw,
                    },() =>{
                        console.log(this.state.isCanWithdraw)
                    })
                })
        }catch(e){
            return;
        }
    }

    close(){
        window.location.href="/bw/manage/account/download?coint="+this.state.UpperCurrentCoin
    }
    closeWithdraw(){
        this.setState({
            isShowStop:false
        })
    }
    setLineWidth(lang){
        switch(lang){
            case 'en':
                return  '270px'
            case 'jp':
                return  '292px'
            case 'kr':
                return '340px'
            default:
                return  '346px'
        }
    }


    getTipInfor(inConfirmTimes = '',outConfirmTimes = ''){
        let iconType  = this.props.location.query.coint.toUpperCase();
        let fundsType = null
        axios.get(DOMAIN_VIP + "/manage/getAssetsDetail").then(res =>{
            if(res.status == '200'){
                //console.log(res);

                let data = eval(res["data"]);
                for(let i in data){
                    if(i == iconType){
                        fundsType = data[i].fundsType
                    }
                }
                this.getTipText(fundsType,inConfirmTimes,outConfirmTimes)

            }
        })
    }

    getTipText(fundsType,arg1,arg2){
        let {minD,usdteminD,canMaxAmount,fees,usdtefees,outConfirmTimes,everyTimeCash} = this.state.drawList
        axios.post(DOMAIN_VIP + "/withdrawalDescript",qs.stringify({fundsType})).then(res =>{
            let data = res["data"];
            if(data.isSuc){
                if(data.datas){

                    //let descript = 'jdksjkdakdljjkjdkaldsdsd,##count1##,djakjdakjdlk##coin##,dsadad##count2##'
                    // let {inConfirmTimes,outConfirmTimes} = this.state;
                    let coin = this.props.location.query.coint;
                    let {agree } = this.state;
                    if (coin.toUpperCase() == "USDT"){
                        let list = data.datas;
                        for(let l of list){
                            let name = agree == 102 ? "USDTE" : "USDT"
                            if (l.coinName.toUpperCase() == name){
                                l.descript = l.descript.replace(new RegExp('##coin##','g'),coin);
                                l.descript = l.descript.replace(new RegExp('##minimum##','g'),agree == 102 ? usdteminD : minD)
                                l.descript = l.descript.replace(new RegExp('##maximum##','g'),everyTimeCash)
                                l.descript = l.descript.replace(new RegExp('##miner-fee##','g'),agree == 102  ? usdtefees :fees)
                                l.descript = l.descript.replace(new RegExp('##count##','g'),outConfirmTimes)
                                this.setState({
                                    descript: l.descript
                                })
                            }
                        }
                    }else {

                        let descript = data.datas.descript;
                        descript = descript.replace(new RegExp('##coin##','g'),coin);
                        descript = descript.replace(new RegExp('##minimum##','g'), minD)
                        descript = descript.replace(new RegExp('##maximum##','g'),everyTimeCash)
                        descript = descript.replace(new RegExp('##miner-fee##','g'),fees)
                        descript = descript.replace(new RegExp('##count##','g'),outConfirmTimes)
                        // let regs= /(\w*)##coin##(.*)##count1##(.*)##count2##(.*)/g;
                        // descript = descript.replace(regs,`$1${coin}$2${inConfirmTimes}$3${outConfirmTimes}$4`)
                        this.setState({
                            descript
                        },() =>{
                            console.log('==========>>MMM' + this.state.descript)
                        })
                    }
                }
            }

        })
    }

    getAdress = () =>{
        // axios.get(DOMAIN_VIP + "/manage/account/charge/rechargecoininfo").then(res =>{
        //     let data = res.data;
        //     if(data.isSuc){
        //         let list = data.datas.list;
        //         //console.log(list);
        //
        //         for(let i =0 ; i<list.length;i++){
        //             if(list[i].coinName == this.props.location.query.coint.toLowerCase()){
        //                 // this.setState({
        //                 //     addressTag:list[i].addressTag,
        //                 //     currentAddress:list[i].address,
        //                 // })
        //                 this.getTipInfor(list[i].inConfirmTimes,list[i].outConfirmTimes)
        //             }
        //         }
        //
        //
        //     }
        //
        // })
        this.getTipInfor('','')
    }
    render(){
        // console.log(this.state.drawList);
        const { formatMessage } = this.intl;
        // console.log(this.state.drawList)
        const UpperCurrentCoin = this.props.curCoin.toUpperCase()
        const {fIn, bOut,payPwdCheck,setcashAmount,setGetCodes,setPayPwd,tabConfig,setSelected,setGCode,setSmsCode} = this
        const {address,memo,availableDownload,lockStatus,lockTips,downloadLimit, balance,dayCash,everyTimeCash,fees,minD,todayCash,canWithdraw,payEmailAuth,payGoogleAuth,payMobileAuth,authResult,safePwd,addressTag} = this.state.drawList;
        const { sendBtn,oneMinute,cashAmount,payPwd ,codeType,errors,ckFlg,opt,selectedCode,gcode,smscode,actualAccount,addressId,focusBlu,props:sprops,isCanWithdraw,isShowStop, outConfirmTimes,withdrawLength,descript} = this.state
        const { cashAmount:ecashAmount = [], payPwd:epayPwd=[],gcode:egcode = [], smscode:esmscode = []} = errors;
        const userName = cookie.get(COOKIE_UNAME)
        const lang = cookie.get("zlan")
        const { close,hasSalfClick} = this;
        // console.log(balance)
        // let  _balance = Number(balance).toFixed(8);
            //  _balance = _balance.substring(0, _balance.indexOf(".") + 9);
        return (
            <div className="content ">
                {this.state.isSMsOpen === true ?"":this.state.isGoogleOpen === true?"":<div className={`sigup_tips_one ${sprops.ln === 'en' ? 'kc' : ''}`}>
                    <div className="tips_bg"></div>
                    <Opt  hasSalfClick={hasSalfClick} closeCb={close} msg={`${formatMessage({id: '为了您的账号安全，提现时需要您开启安全验证。请选择您的安全验证方式。'})}`} msg2={formatMessage({id:"请选择您的安全验证方式。"})} ft={formatMessage({id: '稍后设置'})} />

                </div>
                }
                <section className="withdrawal_details ${canWithdraw}">
                    <h2 className="mb40"><FormattedMessage  id="balance.text16" /> {UpperCurrentCoin}</h2>
                    <div className="withdrawal_contion">
                        <div className="bbyh-mg">
                            <div className="withdrawal_details_head mb20">

                                <div className="address_div">
                                    <div className="item clearfix mb10">
                                        <span className="width_item_1 left"><FormattedMessage  id="bbyh地址备注" /><FormattedMessage  id="withdraw.text73" /></span>
                                        <span className="width_item_1 right">{memo? memo : '--'}</span>
                                    </div>
                                    {   addressTag &&
                                    <div className="item clearfix mb10">
                                        <span className="width_item_1 left"><FormattedMessage  id="bbyh地址标签" /><FormattedMessage  id="withdraw.text73" /></span>
                                        <span className="width_item_1 right">{addressTag}</span>
                                    </div>
                                    }
                                    <div className="item clearfix mb10">
                                        <span className="width_item_2 left"><FormattedMessage  id="yybh提现地址" /></span>
                                        <span className="width_item_2 right">{address}</span>
                                    </div>
                                    <div className="item clearfix">
                                        <span className="width_item_3 left"><FormattedMessage  id="withdraw.text5" /></span>
                                        <span className="width_item_3 right">{fees}</span>
                                    </div>
                                </div>
                            </div>

                            <section className="from_withdrawal">
                                <input type="text" name="cashAmount" className="inp-hide"  />
                                <input type="password" name="safePwd" className="inp-hide" />
                                <div className="input_div">
                                    <h5 className="clearfix">
                                        <FormattedMessage  id="withdraw.text4" />
                                        <div className="limit_24">
                                        <span style={{color:'#ffffff'}}>
                                            <b><FormattedMessage  id="balance.text5" /></b>{downloadLimit} BTC
                                        </span>
                                            <span className="mar10 mal20" style={{color:'#ffffff'}}>
                                            <b><FormattedMessage  id="balance.text6" /></b>{availableDownload} BTC
                                        </span>
                                            {authResult==0&&<Link to="/bw/mg/authenOne"><FormattedMessage  id="balance.text4" /></Link>}
                                        </div>
                                    </h5>
                                    <div className={`${ckFlg && ecashAmount[0] && 'err'} input_div_1 input_div_2`} style={{borderColor:focusBlu=='cashAmount'?'#3E85A2':''}}>
                                        <input type="text" placeholder={formatMessage({id: "请输入提现数量"})} autoComplete="off" onKeyUp={this.checkNumber} ref={(inp) => this.cashAmountInput = inp}   name="cashAmount" value={cashAmount} onChange={setcashAmount} className="input_1 input_2"  onFocus={(e)=>{this.setCk();this.setFoucs('cashAmount');this.fIn(e)}} onBlur={bOut}/>
                                        <div className="bitunit">
                                            <FormattedMessage  id="withdraw.text68" />{balance}
                                            <a href="javascript:void(0)" onClick={this.maxDraw} className="btn-maxdraw" id="maxDraw"><FormattedMessage  id="withdraw.text69" /></a>
                                        </div>
                                    </div>
                                    <span className="ew">{ckFlg ? ecashAmount[0] : null}{/超出|Exceeding/.test(ecashAmount[0]) ? everyTimeCash + UpperCurrentCoin : ''}</span>
                                </div>
                                <div className="input_div">
                                    <h5><FormattedMessage  id="实际到账" /></h5>
                                    <div className="actual_account">{ actualAccount?new BigNumber(String(actualAccount)).toFixed(withdrawLength):actualAccount}</div>
                                </div>
                                <div className="input_div">

                                    <h5><FormattedMessage  id="withdraw.text6" /></h5>
                                    {safePwd?<div className={`${ckFlg && epayPwd[0] && 'err'} input_div_1`} style={{borderColor:focusBlu=='payPwd'?'#3E85A2':''}}>
                                            <input type="text" maxLength="20"  readOnly  name="payPwd"  autoComplete="nope" value={payPwd}  placeholder={formatMessage({id: "请输入您的资金密码2"})} className="input_1 safePwd"  onChange={setPayPwd} ref="p" onFocus={(e)=>{this.disabledBrowserList(this.refs.p);this.setCk(payPwd);this.setFoucs('payPwd');this.fIn(e)}} onBlur={(e)=>{this.refs.p.setAttribute("readonly", true);bOut(e);}}/>
                                            <div className="ptith"><Link style={{[!this.state.isEnableRs?'color':'']:'#737A8D'}} to={this.state.isEnableRs ? "/bw/mg/resetPayPwd?router=widthdraw&&coint="+UpperCurrentCoin+"&addressId="+addressId : ''}><span>{formatMessage({id: "重置资金密码"})}</span></Link></div>

                                        </div>
                                        :
                                        <div className={`${ckFlg && epayPwd[0] && 'err'} input_div_1`}><span className="setpwd"><Link to={"/bw/mg/setPayPwd?router=widthdraw&&coint="+UpperCurrentCoin+"&addressId="+addressId}>{formatMessage({id: "设置资金密码2"})}</Link></span></div>

                                    }
                                    <span className="ew">{ckFlg ? epayPwd[0] : null}</span>
                                </div>

                                {/* <div className="input_div mb20"> */}
                                {/* <h5>
                                <FormattedMessage  id="邮箱验证码" />
                                </h5> */}
                                {/* <div className={`${ckFlg && eemailcode[0] && 'err'} input_div_1 input_div_2`} style={{borderColor:focusBlu=='emailcode'?'#3E85A2':''}}>
                                    <input  type="text" name="emailcode" style={{display:'none'}}/>
                                    <input type="text" className="input_1 input_2"  placeholder={formatMessage({id: "请输入邮箱验证码（水印）"})} onPaste={setEmailcode} autoComplete="off" value={emailcode} onChange={setEmailcode} name="emailcode"  onFocus={(e)=>{this.setCk();this.setFoucs('emailcode');this.fIn(e)}} />
                                    {/* <a href="javascript:void(0);" id="sendCodeBtn" onClick={this.settime} disabled={sendBtn?"":"disabled"} > {sendBtn ? <FormattedMessage id='withdraw.text8' /> : <FormattedMessage id='withdraw.text42' values={{time:oneMinute}} /> }</a> */}
                                {/* <Sms {...{codeType}} fn={(k, v)=>{setGetCodes('emailcode', 1);this.setCk();this.callError(k, v)}} sendUrl="/userSendCode" errorKey="emailcode" otherData={{userName,type:1,currency:UpperCurrentCoin}} codeType={CODETYPE} /> */}
                                {/* </div> */}
                                {/* <span className="ew">{ckFlg ? eemailcode[0] : null}</span> */}
                                {/* </div> */}

                                {opt !== -1 ?
                                    <div className="input_div">
                                        <h2 className="ptithx text-center"><div className="txt-line" style={{width:this.setLineWidth(lang)}}></div><span style={{padding:'0 18px'}}>{formatMessage({id: "安全验证"})}</span><div className="txt-line" style={{width:this.setLineWidth(lang)}}></div></h2>

                                        {
                                            opt ==2&&
                                            <div className="htb_sy0 mb10 fz14p">
                                                <HTab list={tabConfig} currentFlg={selectedCode} setSelected={setSelected}></HTab>
                                            </div>
                                        }

                                        <ul className="list">
                                            {
                                                +selectedCode === 0 ?

                                                    <li className={`lst ${egcode[0] && 'err'}`}>
                                                        <span className="lst_title">{formatMessage({id: "google验证(提现)"})}</span>
                                                        <div className="plv"  style={{borderColor:focusBlu=='gcode'?'#3E85A2':''}}>
                                                            <input type="text" className="i1 padl100" placeholder={formatMessage({id: "google验证(提现水印)"})}  name="gcode" value={gcode} onPaste={setGCode} onChange={setGCode} onFocus={(e)=> {this.setFoucs('gcode');fIn(e)}}onBlur={bOut} />
                                                        </div>
                                                        <span className="ew">{egcode[0]}</span>
                                                    </li>
                                                    :
                                                    <li className={`lst ${esmscode[0] && 'err'}`} >
                                                        <span className="lst_title">{formatMessage({id: "nuser108"})}</span>
                                                        <div className="plv cb"  style={{borderColor:focusBlu=='smscode'?'#3E85A2':''}}>
                                                            <input type="text" className="i1 i1_color padl100" placeholder={formatMessage({id: "bbyh请输入短信验证码"})} onPaste={setSmsCode} name="smscode" value={smscode} onChange={setSmsCode} onFocus={(e)=> {this.setFoucs('smscode');fIn(e)}} />
                                                            <Sms {...{codeType}} fn={(k, v)=>{setGetCodes('smscode', 1);this.callError(k, v)}} sendUrl="/userSendCode" errorKey="smscode" otherData={{userName,currency:UpperCurrentCoin}} codeType={CODETYPE} />
                                                        </div>
                                                        <span className="ew">{esmscode[0]}</span>
                                                    </li>
                                            }
                                        </ul>
                                    </div>
                                    :null }
                                {/* } */}
                                <div className="pay-tip">
                                    <h4><FormattedMessage id="withdraw.text13" values={{propTag:UpperCurrentCoin}}/></h4>
                                    {/*<p>1. <FormattedMessage id="withdraw.text9" values={{propTag:UpperCurrentCoin,fee:fees}}/></p>*/}
                                    {/*{this.props.language!="en"?*/}
                                    {/*    <p>2. <FormattedMessage id="withdraw.text10" values={{propTag:UpperCurrentCoin,everyTimeCash:everyTimeCash,dayCash:dayCash}}/></p>:*/}
                                    {/*    <p>2. <FormattedMessage id="withdraw.text10" values={{everyTimeCash:everyTimeCash,propTag:UpperCurrentCoin,everyTimeCash:everyTimeCash,propTag:UpperCurrentCoin}}/></p>*/}
                                    {/*}*/}
                                    {/*<p>3. <FormattedMessage id="每日提现额度为" values={{propTag:UpperCurrentCoin, dayCash:dayCash}}/></p>*/}
                                    {/*<p>4. <FormattedMessage id="withdraw.text11" values={{propTag:UpperCurrentCoin, s:outConfirmTimes}}/></p>*/}
                                    <p dangerouslySetInnerHTML={{ __html: descript }}></p>
                                </div>
                                {lockStatus ==1?
                                    <button className="disabled" disabled id="submit"><FormattedMessage id='account.text3' /></button>
                                    :<button className="submit" onClick={this.submitDown}  id="submit"><FormattedMessage id='account.text3' /></button>}
                                {lockStatus ==1&&<div className="posi_top10"><FormattedMessage  id="提示：您的账户已经被锁定，在此期间不能进行提现操作，请等待24小时后自动解锁。" />  </div>}

                            </section>

                        </div>
                    </div>
                </section>
                {
                    isShowStop &&
                    <div className={`sigup_tips_one ${sprops.ln === 'en' ? 'kc' : ''}`}>
                        <div className="tips_bg"></div>
                        <MoneyOpt type="1" closeCb={this.closeWithdraw} msg={`${formatMessage({id: 'bbyh%%币种已暂停提现服务'}).replace(/%%/g,this.props.location.query.coint.toUpperCase())}`} />
                    </div>
                }
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </ReactModal>
            </div>
        )
    }

}

const mapStateToProps = (state,ownProps) => {
    return {
        curCoin:state.withdraw.curCoin,
        language:state.language.locale,
        drawList:state.withdraw.drawList.datas,
        baseUserInfo:state.session.baseUserInfo,
        assetsDetail: state.assets.detail.data,
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
        fetchSms: (cb) => {
            dispatch(fetchSms()).then(cb)
        },
        chooseDownCoin: (params) => {
            dispatch(chooseDownCoin(params))
        },
        fetchWithdraw: (values,cb) => {
            dispatch(fetchWithdraw(values)).then(cb)
        }
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(WithdrawForm))
































