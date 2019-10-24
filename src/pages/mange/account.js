import React from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import cookie from 'js-cookie';
import { browserHistory } from 'react-router';
import { Link } from 'react-router';
import { formatURL, mobileFormat, formatDate, optPop, } from "../../utils";
import SigupFirst from '../../components/popBox/sigupFirst';
import {changeImgCode, getUserBaseInfo} from '../../redux/modules/session';
import { FormattedMessage, injectIntl } from 'react-intl';
import Confirm from '../../components/msg/confirm';
import { doSelectModel, getModel, isCanJump } from '../../redux/modules/usercenter';
import {fetchBlackList, fetchPaySetting, fetchUserPayInfo} from '../../redux/modules/account';
import {DISMISS_TIME, DOMAIN_VIP, OTC,OTC_UIR} from '../../conf/index'
import Form from '../../decorator/form';
import ReactModal from "../../components/popBox";
import UserCenter from '../../components/user/userCenter'

import Ali from '../../assets/img/pay-ali.png'
import Card from '../../assets/img/pay-card.png'
import otcConfig from '../../assets/img/otc-config.png'
import blackcion from '../../assets/img/black-list.png'
import renZheng from '../../assets/images/renzheng.png'
import duigou  from '../../assets/images/duigou.png'
import { node } from 'prop-types';


import EntrustModal from '../../components/entrustBox';
import VerifyCheck from './accountItem/index';

@Form
class Account extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            mv: false,
            imv: false,
            model: 0,
            modelDia: 0,
            tflg: 0,
            inputFlag: false,
            nickname: '',
            homePage:this.props.homePage,
            targetId:'',
            uid:'',
            dialogHTML:'',
            blackList:[],
            storeStatus:'',
            storeType:'',



            //交易验证 状态 0 6 1 使本地和线上数据同步
            //verifyStatus:this.props.userInfo.checkTrans,

        }

        this.cmv = this.cmv.bind(this);
        this.cmvo = this.cmvo.bind(this);
        this.icmv = this.icmv.bind(this);
        this.imvo = this.imvo.bind(this)
        this.mdia = this.mdia.bind(this);
        this.cb = this.cb.bind(this);
        this.isCanJump = this.isCanJump.bind(this);
        this.nickInputOnFocus = this.nickInputOnFocus.bind(this);
        this.resetNickName = this.resetNickName.bind(this);
        this.handelChange = this.handelChange.bind(this);
        this.cleanNickName = this.cleanNickName.bind(this);
        this.saveNickName = this.saveNickName.bind(this);
        this.openDialog = this.openDialog.bind(this);
        this.togglePayState = this.togglePayState.bind(this);
        this.showHomePage = this.showHomePage.bind(this);
        this.showMyHomePage = this.showMyHomePage.bind(this);
        this.paySetting = this.paySetting.bind(this);

        this.verifyModal = this.verifyModal.bind(this);
        this.changeVerifyStatus = this.changeVerifyStatus.bind(this);

    }
    cb(flg){
        // if 1 will send to interface modify
        if(flg){
            const upData = {
                category: 4,
                type: !this.state.model ? 2 : 1,
            };
            this.props.doSelectModel(upData).then(r => {
                if(r.opt){
                    // modify after will change model status
                    this.setState({
                        model: !this.state.model,
                        tflg: 1,
                    });
                }
            });
        }

        // close dialog
        this.setState({
            modelDia: 0,
        });
    }
    mdia(){
        let { model } = this.state;
        this.setState({
            modelDia: 1,
        });
        // console.log(model);
    }
    cmv(){
        this.setState({
            mv: 1,
        });
    }
    cmvo(){
        this.setState({
            mv: 0,
        });
    }
    imvo(){
        this.setState({
            imv: false,
        });
    }
    icmv(){
        this.setState({
            imv: true,
        });
    }

    componentDidMount(){
        let userInfo = this.props.userInfo;
        if(!userInfo.guideFlg){
            this.props.getUserBaseInfo();
        }
        let zuid = cookie.get('zuid')
        this.setState({
            uid:zuid,
            blackList:[...this.props.blackList]
        })
        // this.props.getUserBaseInfo();

        this.props.getModel().then(r => {
            this.setState({
                model: r < 2 ? 0 : 1,
                tflg: r,
            });
        });

        this.props.fetchPaySetting(1);
        this.props.fetchPaySetting(2);
        this.props.fetchUserPayInfo();
        this.props.fetchBlackList();
        this.checkUserInfors();
    }
    componentWillReceiveProps(nextProps,nextContext){
        this.setState({
            blackList:[...nextProps.blackList]
        })
    }
    openDialog = () =>{
        this.modal.openModal();
    }

    openModalInfo=(msg)=>{
        //confirm demo
        let str = <div className="image-dialog">
            <div className="dialog-title">
                <svg ><use xlinkHref="#icon-zhanghuanquantixing"/></svg>
            </div>
            <div className="dialog-cont">
                {msg}
            </div>
            <div className="dialog-footer">
                <span className="btn cancel" onClick={() => {this.dialog.closeModal()}}><FormattedMessage className="btn cancel"  id="取消" /></span> <Link to="/bw/mg/authenOne" className="btn submit"><FormattedMessage id="前往认证" /></Link>
            </div>
        </div>

        this.setState({dialogHTML:str},()=>{
            this.dialog.openModal();
        });
    };


    handelChange = (e) =>{
        this.setState({
            nickname: e.target.value
        },() =>{
            console.log(this.state.nickname)
        })
    }
    // 重置 nickname
    resetNickName(){
        this.setState({
            nickname:''
        },()=>{
            this.nickInputOnFocus()
        })
    }
    // 取消
    cleanNickName(){
        let {fIn} = this;
        let e = this.nickInput;
        fIn(e);
        this.setState({
            nickname:'',
            inputFlag: false
        },() =>{
            // this.nickInput.onblur();
            // this.nickInput.setAttribute('readonly')
        })
    }
    // nick焦点
    nickInputOnFocus(){
        this.setState({
            inputFlag: true
        },() =>{
            this.nickInput.focus()
        })
    }
    // 保存 昵称
    saveNickName(){
        let {bOut} = this;
        let e = this.nickInput;
        bOut(e);
    }
    showMyHomePage(){
        let id = this.state.uid;
        this.showHomePage(id);
    }
    // 显示个人主页
    showHomePage(id){
        let data = new FormData(),
            targetId = parseInt(id);
        let zuid = cookie.get('zuid')
        this.setState({
            targetId:targetId
        })
        data.append('targetUserId',targetId)
        data.append('userId',zuid)
        axios.post(OTC_UIR + OTC + '/web/common/getAvgPassTime', data).then((res)=>{
            console.log(res);
            let msg = res.data.msg;
            if (res.data.code == 200){
                this.setState({
                    homePage: res.data.data
                },() =>{
                    this.openDialog();
                })
            }else{
                optPop(() =>{
                },msg,{timer: 1500})
            }
        })
    }

    isCanJump(url, opt = -1){
        this.props.isCanJump({opt}).then(r => {
            if(r.isSuc){
                browserHistory.push(url);
            } else {
                optPop(() => {}, r.des, undefined, true);
            }
        });
    }
    // 切换otc支付设置 状态
    togglePayState(id,type,state){
        let {aliPay,cardPay} = this.props;
        let {formatMessage} = this.props.intl;
        if (state == 0){
            if (aliPay.enable == 0 || cardPay.enable == 0){
                optPop(() =>{
                    // this.props.fetchPaySetting(type);
                },formatMessage({id:'至少开启一种收款方式'}),{timer: 1500});
                return false
            }
        }
        let data = new FormData();
        id = parseInt(id),
            state = parseInt(state);
        data.append("id",id);
        data.append("enable",state);
        axios.post(OTC_UIR + OTC + '/web/payment/updateEnable', data).then((res)=>{
            // console.log(res);
            let msg = res.data.msg;
            if (res.data.code == 200){
                optPop(() =>{
                    this.props.fetchPaySetting(type);
                },msg,{timer: 1500});
            }else{
                optPop(() =>{
                },msg,{timer: 1500})
            }
        })
    }
    removeBlackList(id){
        let data = new FormData();
        data.append("id",id);
        axios.post(OTC_UIR + OTC + '/web/blacklist/delBlacklist', data).then((res)=>{
            // console.log(res);
            let msg = res.data.msg;
            if (res.data.code == 200){
                optPop(() =>{
                    this.props.fetchBlackList();
                },msg,{timer: 1500});
            }else{
                optPop(() =>{
                },msg,{timer: 1500})
            }
        })
    }

    // 支付设置跳转
    paySetting(url,msg){
        let userInfo = this.props.userInfo;
        let {formatMessage} = this.props.intl;
        let sre
        console.log("======================="+userInfo.authStatus)
        axios.post(OTC_UIR + OTC + '/web/payment/passWordLock').then((res)=>{
            // console.log(res);
            let msg = res.data.msg;
            if (res.data.code == 200){
                if(userInfo.authStatus==4){
                    sre= formatMessage({id:'请先身份认证'})
                }else if(userInfo.authStatus==5){
                    sre= formatMessage({id:'身份认证审核中，请稍后再试'})
                }else{
                    sre= formatMessage({id:'身份认证失败，请重新认证'})
                }
                if (userInfo.authStatus ==7||userInfo.authStatus == 4){
                    this.openModalInfo(sre)

                }else if(userInfo.authStatus==5){
                    optPop(() =>{
                    },sre,{timer: 1500})
                    return false
                 } else {
                     //this.isCanJump(formatURL(url), -1)
                     //直接跳转
                     browserHistory.push(formatURL(url));
                 }
            }else{
                optPop(() =>{
                },msg,{timer: 1500})
            }
        })

    }

    // 获取用户是否商家认证
    checkUserInfors = () =>{
        axios.get(DOMAIN_VIP + '/manage/auth/authenticationJson').then(res =>{
            if(res.status == 200){
                let {storeStatus,storeType} = res.data.datas
                this.setState({
                    storeStatus,
                    storeType:storeType || 1
                })
            }

        })
    }

    //去认证
    openGoCheck = () =>{
        let str =  <div className="image-dialog" style={{padding:'20px'}}>
            <div className="dialog-cont">
                <p style={{fontSize:'18px'}}><FormattedMessage id="尚未实名认证"/></p>
                <p><FormattedMessage id="请您先完成实名认证后，再申请广告商家认证"/></p>
            </div>
            <div className="dialog-footer" style={{marginTop:'80px'}}>
                <a href="/bw/mg/authenOne" className="btn submit"><FormattedMessage id="前往认证" /></a>
            </div>
        </div>
        this.setState({dialogHTML:str},()=>{
            this.dialog.openModal();
        });
    }


    //判断是否锁定
    checkoutIsClock = (type) =>{
        let {storeStatus,storeType} = this.state;
        axios.get(DOMAIN_VIP + '/otcweb/web/v1/store/lockStatus').then(res =>{
            if(res.status == 200){
                let {code,msg} = res.data
                if(code == 200){
                    if(storeStatus == 1 && storeType == 1){
                        window.location.href = `/bw/mg/cancleUserInfor`
                    }
                    if(storeStatus !== 1 && storeType == 1){
                        window.location.href = '/otc/business'
                    }
                    if(storeStatus == 0 && storeType == 2){
                        window.location.href = `/bw/mg/cancleUserInforIng`
                    }
                    if(storeStatus == 1 && storeType == 2){
                        window.location.href = '/otc/business'
                    }
                    if(storeStatus == 2 && storeType == 2){
                        window.location.href = `/bw/mg/cancleUserInforBack`
                    }
                    // if(storeStatus == -1){
                    //     window.location.href = '/otc/business'
                    // }
                    // if(storeStatus == 1){
                    //     window.location.href = `/bw/mg/cancleUserInfor`
                    // }
                    // if(storeStatus == 0){
                    //     window.location.href = `/bw/mg/cancleUserInforIng`
                    // }
                    // if(storeStatus == 2){
                    //     window.location.href = `/bw/mg/cancleUserInforBack`
                    // }
                }else{
                    optPop(() =>{},msg,{timer: 1500},true)
                }
            }
        })
    }

    cancleSure = ()=>{
        let {storeStatus} = this.state;
        window.location.href = `/bw/mg/cancleUserInfor?statu=${storeStatus}`
        // browserHistory.push()
    }

    //交易验证弹窗
    verifyModal(){
        //是否弄错5次后被24小时禁止
        this.props.isCanJump({opt:5}).then(r => { //状态5是交易验证的
            console.log(r);
            if(r.isSuc){
                this.verify.openModal()
            } else {

                optPop(() =>{},r.des,{timer: 1500},true);
            }
        });

    }

    changeVerifyStatus(state){

        if(state == 0){
            this.verify.closeModal()
        }
        //成功后重新获取用户基本信息
        this.props.getUserBaseInfo();

    }


    render(){
        const {userInfo, aliPay,cardPay, blackList}= this.props;
        const verifyStatus = userInfo.checkTrans;
        console.log(userInfo)
        // console.log('userinfo======',cardPay)
        const { formatMessage } = this.props.intl;
        const { mv, model, modelDia, tflg ,imv,inputFlag,nickname,errors,nickFlag,storeStatus,storeType} = this.state;
        // console.log('blackList======',blackList)
        const { cmv, mdia, cb, cmvo, isCanJump,icmv,imvo ,fIn,bOut,setNickName} = this;
        let {nickname:enickname = []} = errors;
        let pLogin = userInfo.previousLogin;
        const altClassName = this.props.language.locale === 'en' ? 'en' : 'cn';
        pLogin = altClassName === 'en' ? formatDate(pLogin, 'MM-dd-yyyy hh:mm:ss') : formatDate(pLogin);
        // console.log(userInfo.nickname)

        return (
            <div className="content">

                { modelDia ? <Confirm cb={cb} msg={formatMessage({id: !model ? (tflg === 0 ? '您正在切换高级模式，开启后，添加新地址时将进行安全验证，并锁定该地址24小时。您是否要继续？' : '您正在切换高级模式，开启后，您的账户将锁定24小时，在此期间不支持提现操作，添加新地址时将进行安全验证，并锁定该地址24小时。您是否要继续？'):'您正在切换初级模式，开启后，添加新地址时将不会进行安全验证。并且您的账户将被锁定24小时，在此期间不支持提现操作，可正常交易。您是否要继续？'})} /> : null }

                { (userInfo.userSafeLevel == 1 && userInfo.guideFlg) && <SigupFirst /> }
                {/* <SigupFirst /> */}
                <div className="account clearfix">
                    <div className="top clearfix">
                        <div className={`account-left ${!userInfo.color ? '' : 'activez' }`} style={{background:userInfo.color}} onClick={() =>{ this.showMyHomePage()}} >
                            {!userInfo.color?<svg className="icon" aria-hidden="true">
                                <use xlinkHref="#icon-zhanghu-yonghutouxiang"></use>
                            </svg>:userInfo.nickname.substr(0,1).toLocaleUpperCase() }

                        </div>
                        <div className="account-right">
                            <p>
                                <span>UID: {userInfo.userName}</span>
                                {
                                    userInfo.authStatus != 6 ? <span className="wrz">{formatMessage({id:"未认证"})}</span> : null
                                }
                                {
                                    userInfo.authStatus != 6
                                        ?
                                        <Link className="text-btn" to={formatURL('authenOne')}>{formatMessage({id:"开启身份认证"})}</Link>
                                        :
                                        <span className="rz">{formatMessage({"id":"已认证"})}</span>
                                }
                                {/*
                                    userInfo.authStatus != 6
                                    ?
                                    (
                                    <span className="left">
                                        <span className="user_status user_status_1"><FormattedMessage id="user.text31" /></span>
                                        <Link to={formatURL('auth/authentication')}  className="user_href">
                                            <FormattedMessage id="user.text33" />
                                        </Link>
                                    </span>
                                    )
                                    :
                                    (<span className="user_status user_status_2"><FormattedMessage id="user.text32" /></span>)
                                */}
                            </p>
                            {/* 昵称  */}
                            {
                                !userInfo.nickname
                                    ?
                                    <p className={`nick-p  ${enickname[0]  && 'err'}`}>
                                        <span>{formatMessage({"id":"昵称:"})}
                                            {inputFlag?"":`(${formatMessage({"id":"未设置"})})`}
                                            <input maxLength="8" disabled={!inputFlag} className={`nick-input ${inputFlag ? 'active' : '' }`}  id="nick-input" name="nickname" type="text" autoComplete="off"   value={nickname}
                                                   placeholder={inputFlag ? formatMessage({"id":"昵称设置后不可修改"}) : `(${formatMessage({"id":"未设置"})})`}
                                                   ref={(input) => { this.nickInput = input; }} onChange={setNickName} onFocus={fIn}  />
                                            {
                                                nickname ? <button onClick={this.resetNickName} className="icon-shanchu-moren iconfont"></button> : null
                                            }
                                        </span>
                                        {
                                            inputFlag ?
                                                <span>
                                                    <Link  className="text-btn pb" onClick={this.saveNickName}>{formatMessage({"id": "确定"})}</Link>
                                                    &nbsp;&nbsp;
                                                    <Link  className="text-btn pb" onClick={this.cleanNickName}>{formatMessage({"id": "取消"})}</Link>
                                                </span>
                                                :
                                                < label htmlFor="nick-input" className="text-btn pb" onClick={this.nickInputOnFocus}>{formatMessage({"id": "设置"})}</label>

                                        }
                                        <span className="ew">{enickname[0]}</span>
                                    </p>
                                    :
                                    <p>
                                        <span>{formatMessage({"id":"昵称:"})} {userInfo.nickname}</span>
                                    </p>
                            }
                            {
                                !userInfo.mobile
                                    ?
                                    <p>
                                        <span>{formatMessage({"id":"手机号: "})}{formatMessage({"id":"(未填写)"})}</span>
                                        <Link className="text-btn pb" onClick={() => {isCanJump(formatURL('setMobile'), 4)}}>{formatMessage({"id":"设置"})}</Link>
                                    </p>
                                    :
                                    <p>
                                        <span>{formatMessage({"id":"手机号: "})}({`${mobileFormat(userInfo.mobile)}`})</span>
                                        <Link className="text-btn pb" onClick={() => {isCanJump(formatURL('upMobile'), 4)}}>{formatMessage({"id":"修改"})}</Link>
                                    </p>
                            }
                            <p>{formatMessage({id: "user.text41"})} {pLogin} &nbsp;&nbsp; IP: {userInfo.loginIp}</p>
                        </div>
                    </div>
                    <div className="account-center"></div>
                    <ul className="account-bottom">
                        <li className="clearfix">
                            <span className='font_16px'>
                                <svg className="icon icon_d14 icon-r" aria-hidden="true">
                                    <use xlinkHref="#icon-zhanghu-denglumima"></use>
                                </svg>
                                {formatMessage({id: "登录密码"})}
                            </span>
                            <div className="account-bot">
                                <span>{formatMessage({id:"登录时使用"})}</span>
                                <Link className="text-btn pb" onClick={() => {isCanJump(formatURL('upPwd'), 0)}}>{formatMessage({id:"修改"})}</Link>
                            </div>
                        </li>
                        <li className="clearfix">
                            <span className='font_16px'>
                                <svg className="icon icon-r" aria-hidden="true">
                                    <use xlinkHref="#icon-zhanghu-zijinmima"></use>
                                </svg>
                                {formatMessage({id:"资金密码"})}
                                {
                                    !userInfo.hasSafe
                                        ?
                                        <svg className="icon icon-5" aria-hidden="true">
                                            <use xlinkHref="#icon-tishi-hongse"></use>
                                        </svg>
                                        :
                                        null
                                }
                            </span>
                            <div className="account-bot">
                                <span>{formatMessage({id:"交易、提现时使用"})}</span>
                                {   !userInfo.hasSafe
                                    ?
                                    <Link className="text-btn pb" onClick={()=>{isCanJump(formatURL('setPayPwd'), 1)}}>{formatMessage({id:"设置"})}</Link>
                                    :
                                    <Link className="text-btn pb" onClick={()=>{isCanJump(formatURL('resetPayPwd'), 1)}}>{formatMessage({id:"修改"})}</Link>
                                }
                            </div>
                        </li>
                        <li className="clearfix">
                            <span className='font_16px'>
                                <svg className="icon icon-r" aria-hidden="true">
                                    <use xlinkHref="#icon-zhanghu-gugeyanzheng"></use>
                                </svg>
                                {formatMessage({id: "谷歌验证"})}
                            </span>
                            <div className="account-bot">
                                <span>{formatMessage({id: "体现，安全设置时用以验证谷歌二次验证"})}</span>
                                {
                                    !userInfo.isGoogleOpen
                                        ?
                                        <Link className="text-btn pb" onClick={()=>{isCanJump(formatURL('googleOne'), 2)}}>{formatMessage({id: "设置"})}</Link>
                                        :
                                        <Link className="text-btn pb" onClick={()=>{isCanJump(formatURL('closeG'), 2)}}>{formatMessage({id: "关闭"})}</Link>
                                }

                            </div>
                        </li>
                        <li className="clearfix">
                            <span className='font_16px'>
                                <svg className="icon icon-r" aria-hidden="true">
                                    <use xlinkHref="#icon-zhanghu-shoujiyanzheng"></use>
                                </svg>
                                {formatMessage({id: "手机验证"})} {userInfo.mobile ? `(${mobileFormat(userInfo.mobile)})` : null}
                            </span>
                            <div className="account-bot">
                                <span>{formatMessage({id: "体现，及安全设置时用以收取验证短信"})}</span>
                                {
                                    userInfo.isSmsOpen
                                        ?
                                        <Link className="text-btn pb" onClick={()=>{isCanJump(formatURL('closeMobileCk'), 3)}}>{formatMessage({id: "关闭"})}</Link>
                                        :
                                        <Link className="text-btn pb" onClick={()=>{isCanJump(userInfo.mobileStatus !== 2 ? formatURL('dEbUpMobile') : formatURL('ebMobile'), 3)}}>{formatMessage({id: "开启"})}</Link>
                                }
                            </div>
                        </li>
                        <li className="clearfix">
                            <span className='font_16px'>
                                <svg className="icon icon-r" aria-hidden="true">
                                    <use xlinkHref="#icon-zhanghu-chongzhidizhi"></use>
                                </svg>
                                {formatMessage({id: "提现地址验证"})}{formatMessage({id: !model ? '（初级模式）' : '（安全模式）'})}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                            </span>
                            <div className="account-bot">
                                <span>{formatMessage({id: "用于数字货币新增提现地址时的安全设置"})}</span>
                                <Link onMouseEnter={cmv} onMouseLeave={cmvo} onClick={mdia} className="text-btn">
                                    {mv ?
                                        <svg className="icon icon10" aria-hidden="true"><use xlinkHref="#icon-qiehuanchongzhitixian-yiru"></use></svg>
                                        :
                                        <svg className="icon icon10" aria-hidden="true"><use xlinkHref="#icon-qiehuanchongzhitixian-moren"></use></svg>
                                    }
                                    {formatMessage({id: !model ? '切换安全模式' : '切换初级模式'})}
                                    <i onMouseEnter={icmv} onMouseLeave={imvo} className="iconfont ts-show icon-l5 icon-tongchang-tishi"></i>
                                </Link>
                            </div>
                            {
                                imv
                                    ?
                                    <div className="caveat-modal ssts" >{!model ? formatMessage({id: "切换后，增加新地址时将进行安全验证，并锁定该地址24小时。"}) : formatMessage({id: '切换后，增加新地址时将不会进行安全验证，并不会锁定该地址24小时。'})}</div>
                                    :
                                    null
                            }
                        </li>

                        {/* 新增的功能块，交易验证 */}

                        <li className="clearfix">
                            <span className='font_16px'>
                                <svg className="icon icon-r" aria-hidden="true">
                                    <use xlinkHref="#icon-zhanghu-shoujiyanzheng"></use>
                                </svg>
                                {formatMessage({id: "交易验证"})} {verifyStatus == 0 ?`（${formatMessage({id: "始终开启"})}）`:verifyStatus == 6?`（${formatMessage({id: "sellbuy.p5"})}）`: `（${formatMessage({id: "始终关闭"})}）`}
                            </span>
                            <div className="account-bot">
                                <span>{formatMessage({id: "用于设置交易时资金密码输入频率"})}</span>
                                <a className="text-btn pb" onClick={this.verifyModal}>{formatMessage({id:"设置"})}</a>
                            </div>
                        </li>











                        {/* ******************************************************************* */}

                        <div className="otc-title bd-tp"><h3 className="">{formatMessage({id: "法币设置"})}</h3></div>
                        <li className="clearfix">
                            <div className="clearboth otc-pay">
                                <span className='font_16px'>
                                    <i className="icon icon-r"><img src={otcConfig} alt=""/></i>
                                    {formatMessage({id: "收款设置"})}
                                </span>
                                <div className="account-bot">
                                    <span>{formatMessage({id: "用于法币交易时向对方展示，务必使用本人的收款方式"})}</span>
                                </div>
                            </div>
                            <div className="clearboth otc-pay">
                                <span className='font_16px pd-lt40'>
                                    <i className="icon icon-r"><img src={Card} alt=""/></i>
                                    {formatMessage({id: "银行卡"})}
                                </span>
                                {
                                    !cardPay.accountNumber ?
                                        <div className="account-bot">
                                            <span>{formatMessage({id: "未设置"})}</span>
                                            <Link className="text-btn pb" onClick={() =>{this.paySetting('cardPaySetting')}}>{formatMessage({id: "设置"})}</Link>
                                        </div>
                                        :
                                        <div className="account-bot">
                                            <span className='has-set'>{cardPay.bankOpeningBank}&nbsp;&nbsp;&nbsp;&nbsp;{cardPay.accountNumber}&nbsp;&nbsp;&nbsp;&nbsp;{cardPay.accountName}</span>
                                            <span className="btn-span">
                                        <Link className="text-btn pb" onClick={() =>{this.paySetting('cardPaySetting')}}>{formatMessage({id: "修改"})}</Link>
                                                {
                                                    cardPay.enable == 1 ?
                                                        <Link className="text-btn pb" onClick={() =>{this.togglePayState(cardPay.id,'2','0')}}>{formatMessage({id: "收款关闭"})}</Link>
                                                        :
                                                        <Link className="text-btn pb" onClick={() =>{this.togglePayState(cardPay.id,'2','1')}}>{formatMessage({id: "收款开启"})}</Link>
                                                }
                                        </span>
                                        </div>
                                }

                            </div>
                            <div className="clearboth otc-pay">
                                <span className='font_16px pd-lt40'>
                                    <i className="icon icon-r"><img src={Ali} alt=""/></i>
                                    {formatMessage({id: "支付宝"})}
                                </span>
                                {
                                    !aliPay.accountNumber ?
                                        <div className="account-bot">
                                            <span>{formatMessage({id: "未设置"})}</span>
                                            <Link className="text-btn pb" onClick={() =>{this.paySetting('aliPaySetting',formatMessage({id: "请先认证身份"}))}}>{formatMessage({id: "设置"})}</Link>
                                        </div>
                                        :
                                        <div className="account-bot">
                                            <span className='has-set'>{aliPay.accountNumber}&nbsp;&nbsp;&nbsp;&nbsp;{aliPay.accountName}</span>
                                            <span className="btn-span">

                                            <Link className="text-btn pb" onClick={() =>{this.paySetting('aliPaySetting',formatMessage({id: "请先认证身份"}))}}>{formatMessage({id: "修改"})}</Link>
                                                {
                                                    aliPay.enable == 1 ?
                                                        <Link className="text-btn pb" onClick={() =>{this.togglePayState(aliPay.id,'1','0')}}>{formatMessage({id: "收款关闭"})}</Link>
                                                        :
                                                        <Link className="text-btn pb" onClick={() =>{this.togglePayState(aliPay.id,'1','1')}}>{formatMessage({id: "收款开启"})}</Link>
                                                }
                                            </span>
                                        </div>
                                }
                            </div>
                        </li>
                        <li className="clearfix">
                            <span className="font_16px">
                                <i style={{paddingRight:'22px'}}>
                                    <img style={{width:'16px'}} src={renZheng} alt=""/>
                                </i>
                                {formatMessage({id: "商家认证"})}
                            </span>
                            <div className="account-bot">
                                 <span>
                                    { ( (storeStatus == 1 &&  storeType == 1) || (storeStatus !== 1 &&  storeType == 2) )?
                                        <span>
                                                {formatMessage({id: "已认证"})}
                                            <i style={{paddingLeft:'6px'}}>
                                                    <img style={{width:'16px'}} src={duigou} alt=""/>
                                                </i>
                                            </span>
                                        : formatMessage({id: "未认证"})}
                                     {/* {
                                       (storeStatus == 1 &&  storeType == 1)&&
                                            <i style={{paddingLeft:'6px'}}>
                                                    <img style={{width:'16px'}} src={duigou} alt=""/>
                                            </i>
                                    } */}

                                 </span>
                                {
                                    ( (storeStatus == 1 &&  storeType == 1) || (storeStatus !== 1 &&  storeType == 2) )?
                                        <a className="text-btn pb" onClick={() =>this.checkoutIsClock()}>{formatMessage({id:"取消"})}</a>
                                        :
                                        <a className="text-btn pb" onClick={() =>this.checkoutIsClock()}>{formatMessage({id:"申请"})}</a>

                                }

                            </div>
                        </li>
                        <li className="clearfix">
                            <div className="clearboth">
                                <span className='font_16px'>
                                    <i className="icon icon-r"><img src={blackcion} alt=""/></i>
                                    {formatMessage({id: "黑名单"})}
                                </span>
                                <div className="account-bot">
                                    <span>{formatMessage({id: "移入黑名单的用户将无法交易"})}</span>
                                </div>
                            </div>
                            <div>
                                <ul>
                                    {
                                        blackList.length > 0 ?
                                            blackList.map((item,index) => {
                                                // console.log(item.blackUserId);
                                                return (
                                                    <li className="dehw">
                                                        <div className="clearboth otc-pay">
                                                    <span className='font_16px pd-lt40 black-list' onClick={() =>{this.showHomePage(item.blackUserId)}}>
                                                        <i className="name" style={{backgroundColor : item.color}}>{item.nickName.substr(0,1).toUpperCase()}</i>
                                                        <i>{item.nickName}</i>
                                                    </span>
                                                            <div className="account-bot">
                                                                <span>{formatMessage({id: "于Y%%Y D%%D 移入"}).replace('Y%%Y',formatDate(item.addTime,'yyyy-MM-dd')).replace('D%%D',formatDate(item.addTime,'hh:mm:ss'))}</span>
                                                                <Link className="text-btn pb" onClick={() =>{this.removeBlackList(item.id)}}>{formatMessage({id: "移除黑名单"})}</Link>
                                                            </div>
                                                        </div>
                                                    </li>
                                                )
                                            })
                                            :
                                            null
                                    }

                                </ul>
                            </div>
                        </li>
                    </ul>
                </div>
                <ReactModal ref={modal => this.modal = modal}   >
                    <UserCenter modal={this.modal}  hoemPage={this.state.homePage} targetId={this.state.targetId} uid={this.state.uid}/>
                </ReactModal>
                <ReactModal ref={ref => this.dialog =ref}>
                    {this.state.dialogHTML}
                </ReactModal>

                {/* 交易验证时的弹窗 */}
                <EntrustModal ref={modal => this.verify = modal}>
                    {/* {this.state.Mstr} */}
                    <VerifyCheck chooseBtnStatus={verifyStatus} changState={this.changeVerifyStatus} hasSafe={userInfo.hasSafe} ppwlock={userInfo.ppwLock}/>
                </EntrustModal>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        language : state.language,
        userInfo: state.session.baseUserInfo,
        isSalfClick: state.loginLogs.salfClick,
        aliPay:state.account.aliPay,
        cardPay:state.account.cardPay,
        blackList:state.account.blackList,
        homePage:state.account.homePage
    }
};
const mapDispatchToProps = {
    getUserBaseInfo,
    doSelectModel,
    getModel,
    isCanJump,
    fetchPaySetting,
    fetchUserPayInfo,
    fetchBlackList
};
export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(Account));
