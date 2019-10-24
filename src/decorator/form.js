import React from 'react';
import { injectIntl } from 'react-intl';
import { connect } from 'react-redux';
import { actions as notifActions } from 'redux-notifications';
const { notifSend } = notifActions;
import axios from 'axios';
import qs from 'qs';
import { optPop, isIE,optJump } from '../utils'
import cookie from 'js-cookie'
import { DISMISS_TIME } from '../conf';
import { isEmail, ckPwd, isMobiles, isIdCardNo, isPassport, smpIsIdCardNo, } from '../utils';
import {changeImgCode, getUserBaseInfo} from '../redux/modules/session';
import { URL_IMG_CODE, DOMAIN_VIP } from '../conf';
import { isFloat } from '../utils'
const BigNumber = require('big.js')
// Exception
const NotArrayException = (ay = []) => {
    throw new Error('key is Array');
};

const REPEAT = 3;
const KEYDOWNTIME = 300;

/**
 * form decorator
 */
export default (Comp) => {
    @connect(
        state => ({state: {}}),
        (dispatch) => {
            return {
                notifSend: (msg) => {
                    dispatch(notifSend({
                        message: msg,
                        kind: 'info',
                        dismissAfter: DISMISS_TIME
                    }));
                },
                changeImgCode: (imgCode) => {
                    dispatch(changeImgCode(imgCode));
                },
                // 获取用户信息
                getUserInfo:() =>{
                    dispatch(getUserBaseInfo(dispatch));
                }
            }
        }
    )
    @injectIntl
    class Rt extends React.Component{
        render(){
            return <Cp {...this.props} />
        }
    }



    class Cp extends Comp{
        constructor(props){
            super(props);
            this.state = {
                ...this.state,
                errors: {

                },
                hasErrorRepeat: 0,
                needImgCode: 0,
                // 0 is when click submit start check, 1 start check. focus and blur.
                cm: 0,
                // focus in pwd
                mvInPwd: 0,
                // keydown then check code.
                kycodegoogle: 0,
                kycodesms: 0,
                kysmscode: 0,
                kygcode: 0,
                // wheather get code
                isGetCode: 0,
                // when page exists any Sms use this.
                isGetCodes: {

                },
                isJump:false,
                nickFlag: false
            }

            // keydown check code.
            this.queueTimer = {
                kycodegoogle: null,
                kycodesms: null,
                kysmscode: null,
                kygcode: null,
            }

            // async error queuq
            this.asyncErrorQueue = {};

            // error queuq
            this.errorQueue = {};

            // upload img info
            this.imgInfo = {};

            // console.log(props);
            this._changeHandler = this._changeHandler.bind(this);
            this.setUserName = this.setUserName.bind(this);
            this.setPwd = this.setPwd.bind(this);
            this.setCode = this.setCode.bind(this);
            this.emailRepeatFlg = 0;
            this.intl = this.props.intl;
            this.setNike = this.setNike.bind(this);
            this.setConfirmPwd = this.setConfirmPwd.bind(this);
            this.setImgCode = this.setImgCode.bind(this);
            this.setTId = this.setTId.bind(this);
            this.setCkCode = this.setCkCode.bind(this);
            this.setEmail = this.setEmail.bind(this);
            this.setTel = this.setTel.bind(this);
            this.setNeedImgCode = this.setNeedImgCode.bind(this);
            this.setRegAgreement = this.setRegAgreement.bind(this);
            this.setTuijianId = this.setTuijianId.bind(this);
            this.setCm = this.setCm.bind(this);
            this.changeImgCode = this.changeImgCode.bind(this);
            this.fIn = this.fIn.bind(this);
            this.bOut = this.bOut.bind(this);
            this.setMCode = this.setMCode.bind(this);
            this.setMobile = this.setMobile.bind(this);
            this.setCodeType = this.setCodeType.bind(this);
            this.setSecurity = this.setSecurity.bind(this);
            this.setEmCode = this.setEmCode.bind(this);
            this.setGCode = this.setGCode.bind(this);
            this.setSmsCode = this.setSmsCode.bind(this);
            this.setPayPwd = this.setPayPwd.bind(this);
            this.setPayAddress = this.setPayAddress.bind(this);
            this.ckKeyDown = this.ckKeyDown.bind(this);
            this.callError = this.callError.bind(this);
            this.setGetCode = this.setGetCode.bind(this);
            this.setEmailcode = this.setEmailcode.bind(this);
            this.setPaycode = this.setPaycode.bind(this);
            this.setlpwd = this.setlpwd.bind(this);
            this.setcashAmount = this.setcashAmount.bind(this)
            this.setNickName = this.setNickName.bind(this) // 设置昵称
            this.setBankOpeningBank = this.setBankOpeningBank.bind(this) // 设置开户行
            this.setBankOpeningBranch = this.setBankOpeningBranch.bind(this) // 设置开户支行
            this.setBankCard = this.setBankCard.bind(this) // 设置开户支行
            this.setReBankCard = this.setReBankCard.bind(this) // 设置开户支行
            this.setAccountNumber = this.setAccountNumber.bind(this) // 设置开户支行
            // init isGetCodes
            this.initGetCodes = this.initGetCodes.bind(this);
            this.setGetCodes = this.setGetCodes.bind(this);
            this.setmemo = this.setmemo.bind(this)
            this.setbbremark = this.setbbremark.bind(this)
            this.setaddress = this.setaddress.bind(this)
            this.settransAmount = this.settransAmount.bind(this)
            // form save to self, redux funct can use this funct.
            self._form = this;

            this.Uc = this.Uc.bind(this);
            this.payPwdCheck = this.payPwdCheck.bind(this)
            this.emailcodeCheck = this.emailcodeCheck.bind(this)
            this.onlyNum = this.onlyNum.bind(this)
            this.uploadInfor = this.uploadInfor.bind(this)

            this.disabledBrowserList = this.disabledBrowserList.bind(this)

            // 理财账户在注册新增三个字段
            this.setVidAddress = this.setVidAddress.bind(this);
            this.setVidTAddress = this.setVidTAddress.bind(this);
            this.setTCode = this.setTCode.bind(this);

            // 理财中心
            this.setUserVID = this.setUserVID.bind(this);
            this.setPInvitationCode	= this.setPInvitationCode.bind(this);

            // 投保保单
            this.setUType = this.setUType.bind(this);
            this.setFMoney = this.setFMoney.bind(this);
            this.setFPic = this.setFPic.bind(this);

            // 设置排位
            this.setRankNum = this.setRankNum.bind(this);
        }

        componentDidMount(){
            super.componentDidMount && super.componentDidMount();

            this._removeBrowserList();
            this._changeInputType();

        }

        // 如果type是password干掉下拉框
        _removeBrowserList(){
            [...document.getElementsByTagName("input")].filter(v => v.getAttribute("dislist") === 'password').
            forEach(v => {
                if(!isIE()){
                    v.onfocus = () => {
                        setTimeout(()=>{
                            v.setAttribute("type", "password");
                            v.removeAttribute("readonly");
                        });
                    }

                    v.onblur = () => {
                        v.setAttribute("readonly", true);
                    }

                    v.onkeydown = (e) => {
                        const KCODE = e.keyCode;

                        if(KCODE === 8){
                            if(v.value.length === 1){
                                this.rbflg = 1;
                                v.setAttribute("type", "text");
                                this.setState({
                                    [v.getAttribute("name")]: "",
                                })
                            }
                        }

                        if(this.rbflg){
                            setTimeout(()=>{
                                v.setAttribute("type", "password");
                            })
                        }

                        return KCODE !== 40 && KCODE !== 38;
                    }
                } else {
                    v.setAttribute("type", "password");
                    v.removeAttribute("readonly");
                }
            })

        }

        _changeInputType(){
            [...document.getElementsByTagName("input")].filter(v => v.getAttribute("dislist") === 'text').
            forEach(v => {
                if(!isIE()){
                    v.onfocus = () => {
                        setTimeout(()=>{
                            v.setAttribute("type", "text");
                            v.removeAttribute("readonly");
                        });
                    }

                    v.onblur = () => {
                        v.setAttribute("readonly", true);
                    }

                    v.onkeydown = (e) => {
                        const KCODE = e.keyCode;

                        if(KCODE === 8){
                            if(v.value.length === 1){
                                this.rbflg = 1;
                                v.setAttribute("type", "password");
                                this.setState({
                                    [v.getAttribute("name")]: "",
                                })
                            }
                        }

                        if(this.rbflg){
                            setTimeout(()=>{
                                v.setAttribute("type", "text");
                            })
                        }

                        return KCODE !== 40 && KCODE !== 38;
                    }
                } else {
                    v.setAttribute("type", "text");
                    v.removeAttribute("readonly");
                }
            })

        }

        componentWillUnmount(){
            super.componentWillUnmount && super.componentWillUnmount();
            //重写组件的setState方法，直接返回空
            this.setState = (state,callback)=>{
              return;
            };
        }
        settransAmount(v){
            this.changeIcon()
            this._changeHandler('transAmount', v);
        }
        onlyNum(e){
            let value = e.target.value.replace(/[^\d^\.]+/g,'');
            let name =  e.target.name
            this.setState({
                [name]:value
            })
        }
        //上传状态
        uploadInfor(key,type){
        // type:1 上传中，2 上传成功 3 上传失败
            if(type == 1){
                this.setState({
                    ['loading_' + key] : true,
                    ['success_' + key] : false,
                    ['showInfor_' + key] : false,
                })
            } else
            if(type == 2){
                this.setState({
                    ['loading_' + key] : false,
                    ['success_' + key] : true,
                    ['showInfor_' + key] : true,
                })
            } else
            if(type == 3){
                this.setState({
                    ['loading_' + key] : false,
                    ['success_' + key] : false,
                    ['showInfor_' + key] : true,
                })
            } else {
                this.setState({
                    ['loading_' + key] : false,

                })
            }

         }
         disabledBrowserList(obj = null){
            if(obj){
                setTimeout(()=>{
                    obj.setAttribute("type", "password");
                    obj.removeAttribute("readonly");
                });

                obj.onkeydown = (e) => {
                    const KCODE = e.keyCode;
                    if(KCODE === 8){
                        if(obj.value.length === 1){
                            this.rbflg = 1;
                            obj.setAttribute("type", "text");
                            this.setState({
                                [obj.getAttribute("name")]: "",
                            })
                        }
                    }

                    if(this.rbflg){
                        setTimeout(()=>{
                            obj.setAttribute("type", "password");
                        })
                    }

                    return KCODE !== 40 && KCODE !== 38;
                }
            }

         }
        setRankNum(v){
            // 如果合法方可输入，否则拦截禁止渲染视图。
            const obj = v.target;
            const maxs = obj.getAttribute("maxs");
            const value = obj.value;

            (!value || /^\d*$/.test(value) && +value > 0 && +value <= maxs) && this._changeHandler('rankNum', v);
        }
        // set
        // 设置理财首页的两个字段
        setUserVID(v){
            this._changeHandler('userVID', v);
        }
        setPInvitationCode(v){
            this._changeHandler('pInvitationCode', v);
        }
        // 设置理财注册三个字段
        setVidAddress(v){
            this._changeHandler('vidAddress', v);
        }
        setVidTAddress(v){
            this._changeHandler('vidTAddress', v);
        }
        setTCode(v){
            this._changeHandler('tCode', v);
        }
        // 投保保单
        setUType(v){
            this._changeHandler('uType', v);
        }
        setFMoney(v){
            this._changeHandler('fMoney', v);
        }
        setFPic(v){
            this._changeHandler('fPic', v);
        }

        setlpwd(v){
            this._changeHandler('lpwd', v);
        }
        setmemo(v){

            this._changeHandler('memo', v);
        }
        setbbremark(v){
            this._changeHandler('bbremark', v);
        }

        setcashAmount(v){
            const obj = v.target;
            const fn = obj.value.split(".");
            if(fn[0].length > 15 || (fn[1] && fn[1].length > 8)){
                let tmpStr = '';
                if(fn[0].length > 15){
                    tmpStr = fn[0].substring(0, 16)
                    tmpStr += fn[1] ? ('.' + fn[1]) : '';
                }

                if(fn[1] && fn[1].length > 8){
                    tmpStr = '';
                    tmpStr = fn[1].substring(0, 9)
                    tmpStr = fn[0] + '.' + tmpStr;
                }
                v.target.value = tmpStr;
            }


            this._changeHandler('cashAmount', v)

        }
        setPaycode(v){
            this._clearStateKey('paycode', v);
            if(v.target.value.length < 7){
                this._changeHandler('paycode', v);
            }
        }
        setEmailcode(v){
            this._clearStateKey('emailcode', v);
            if(v.target.value.length < 7){
                this._changeHandler('emailcode', v);
            }
        }
        setGetCodes(k, v){
            this.setState({
                isGetCodes: {
                    ...this.state.isGetCodes,
                    [k]: v
                }
            });
        }
        setPayAddress(v){
            this._changeHandler('payAddress', v);
        }
        setPayPwd(v){
            this._changeHandler('payPwd', v);
        }
        setAccountNumber(v){
            this._changeHandler('accountNumber', v);
        }
        setSmsCode(v){
            this._clearStateKey('smscode', v);
            if(v.target.value.length < 5){
                this._changeHandler('smscode', v);
            }
        }
        setGCode(v){
            this._clearStateKey('gcode', v);
            if(v.target.value.length < 7){
                this._changeHandler('gcode', v);
            }
        }
        setEmCode(v){
            this._clearStateKey('emcode', v);
            if(v.target.value.length < 7){
                this._changeHandler('emcode', v);
            }
        }
        setSecurity(v){
            this._changeHandler('security', v);
        }
        setCodeType(v){
            this._changeHandler('codeType', v);
        }
        setMobile(v){
            this._changeHandler('mobile', v);
        }
        setMCode(v){
            this._changeHandler('mCode', v);
        }
        setCm(v){
            this.setState({
                cm: v
            });
        }
        setTuijianId(v){
            this._changeHandler('tuijianId', v);
        }
        setRegAgreement(v){
            this._changeHandler('regAgreement', v);
        }
        setNeedImgCode(){
            this.setState({
                needImgCode: 1
            });
        }
        setTel(v){
            this._changeHandler('tel', v);
        }
        setEmail(v){
            this._changeHandler('email', v);
        }
        setCkCode(k){
            this._changeHandler('ccode', v);
        }
        setTId(v){
            this._changeHandler('tid', v);
        }
        setImgCode(v){
            this._changeHandler('imgCode', v);
        }
        setConfirmPwd(v){
            this._changeHandler('confirmPwd', v);
        }
        setNike(v){
            this._changeHandler('nike', v);
        }
        setCode(v){
            if(!this.notCk){
                this._clearStateKey('code', v);
                if(['codesms', 'codegoogle'].indexOf(v.target.name) === -1 ||
                ('codesms' === v.target.name && v.target.value.length < 5)
                ||
                ('codegoogle' === v.target.name && v.target.value.length < 7)
                ){
                    this._changeHandler('code', v);
                }
            } else {
                this._changeHandler('code', v);
            }
        }
        setPwd(v){
            this._changeHandler('password', v);
        }
        setUserName(v){
            this._changeHandler('username', v);
        }
        setaddress(v){
            this._changeHandler('address', v);
        }

        setNickName(v){
            this._changeHandler('nickname', v);
        }
        setBankOpeningBank(v){
            this._changeHandler('bankOpeningBank', v);
            this._changeHandler('kai', v);
        }
        setBankOpeningBranch(v){
            this._changeHandler('bankOpeningBranch', v);
            this._changeHandler('kai', v);
        }
        setBankCard(v){
            // v.replace(/[0-9]/g,'');
            this._changeHandler('bankCard', v);
        }
        setReBankCard(v){
            this._changeHandler('reBankCard', v);
        }
        setGetCode(flg = 0){
            this.setState({
                isGetCode: flg
            });
        }

        _changeHandler(k, v){
            const target = v.target;
            let vs="";
            let value = target.type === 'checkbox' ? target.checked :k=="nickname"? target.value.replace(/[^\w\.\/]/ig,''):target.value;

            if(k === 'fPic')
            {
                /^[0-9]*(\.[0-9]{0,4})?$/.test(value) && this.setState({[k]: value});
            }
            else if(k === 'fMoney'){                
                /^[1-9]*(\.[0-9]{0,1})?$/.test(value) && +value <= +target.getAttribute("maxs") && this.setState({[k]: value});

            } else if(k == 'memo'){
                if(value.length <= 20){
                    console.log(value.length)
                    this.setState({[k]: value})
                }
            }else if(k == 'bankCard'||k=="reBankCard"){
                vs=value.replace(/[^\d]/g,'')
                this.setState({[k]: vs})
            }else if(k == 'bankOpeningBank'||k=="bankOpeningBranch"){
                vs=value.replace(/[^\a-\z\A-\Z0-9\u4E00-\u9FA5]/g,'')
                this.setState({[k]: vs})
            }else if(k == 'kai'){
                this.setState({[k]: false})
            }else if(k == 'nickname'){
                vs=value.replace(/[^\a-\z\A-\Z0-9\u4E00-\u9FA5]/g,'')
                this.setState({[k]: vs})
            } else{
                this.setState({
                    [k]: value
                });
            }
            // console.log('========',value)
        }
        // flg is get code 1 is get code
        _setError(k, v, flg = 0){
            let { errors } = this.state,
            cd = this._isArray(v) && v.length;

            !this.errorQueue[k] && (this.errorQueue[k] = []);

            this.errorQueue[k][flg] = cd ? v : [];
// console.log(this.errorQueue[k]);
            setTimeout(() => {
                this.setState(prevState => {
                    return {
                        errors: {
                            ...(prevState.errors),
                            [k]: cd ? (
                                errors[k] ? errors[k].unshift(...v) : errors[k] = [...v],
                                [...new Set(errors[k])]
                            ) : [...(this.errorQueue[k].filter(v => {
                                return v.length > 0;
                            })[0] || [])],
                        }
                    }
                });
            },0);
        }

        _hasError(k = ''){
           return !this.errorQueue[k].filter(v => v.length > 0).length;
        }

        callError(k, v){
            this._setError(k, v?[v]:[], 1);
            // keydown
            const KEY = 'ky' + k;
            if(this.state[KEY]){
                this.setState({
                    [KEY]: 0
                });
            }
        }

        clearsError(k = [], flg = 0){
            k.forEach(v => {
                this._setError(v, "",flg);
            });
            return new Promise((r,e) => {
                setTimeout(() => {
                    r(1);
                })
            })
        }

        changeImgCode() {
            let now = +new Date;
            this.props.changeImgCode(URL_IMG_CODE + "?t=" + now);
        }

        _isEmpty(v, errors = [], msg = ''){
            !v && errors.push(msg);
            return !v;
        }
        // 敏感字校验
        _hasSensitiveWords(v,t,errors = [],msg = ''){
            let test = t.split(',');
            for(let item of test){
                if (v.indexOf(item) !== -1) {
                    errors.push(msg)
                    break;
                }
            }
            return !v;
        }
        // 字符长度校验
        _ckStringLength(v,len,error =[],msg = ''){
            if (v.length > len){
                error.push(msg)
            }
            return !v;
        }
        // 只能是英文和数字
        _onlyEn(v,errors = [],msg = ''){
            let test = /^[a-z0-9]+$/i;
            if (!test.test(v)){
                errors.push(msg)
            }
            return !v
        }
        // get
        getState(ay = []){
            !this._isArray(ay) && NotArrayException();

            let rt = {};
            ay.map(v => rt[v] = this.state[v]);
            return rt;
        }

        _showMsg(msg = ''){
            this.props.notifSend(msg);
        }
        _cktoken(){
            return true;
        }
        // 理财首页2个字段
        _ckuserVID(){
            const KEY = 'userVID',
                 {userVID} = this.state,
                 {intl} = this.props,
                 errors = [];

            if(userVID.length > 0 && userVID.length !== 35){
                errors.push(intl.formatMessage({id: "地址输入错误"}));
            }
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        _ckpInvitationCode(){
            const KEY = 'pInvitationCode',
                 {pInvitationCode} = this.state,
                 {intl} = this.props,
                 errors = [];

            if(!/^[\dA-Z]{8}$/.test(pInvitationCode)){
                errors.push(intl.formatMessage({id: "邀请码输入错误"}));
            }
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        // 理财 vid 三个字段验证
        _ckvidAddress(){
            return true;
        }
        _ckvidTAddress(){
            return true;
        }
        _cktCode(){
            return true;
        }

        // 投保保单
        _ckuType(){
            return true;
        }
        _ckfMoney(){
            const KEY = 'fMoney',
                  { fMoney } = this.state,
                  errors = [],
                  { intl } = this.props;
            if(!fMoney){
                errors.push(intl.formatMessage({id:"输入可用份额"}))
            }

            this._setError(KEY, errors);
            
            return this._hasError(KEY);
        }
        _ckrankNum(){
            const KEY = 'rankNum',
            { rankNum } = this.state,
            errors = [],
            { intl } = this.props;

            if(!rankNum){
                errors.push(intl.formatMessage({id:"直推数量错误"}))
            }

            this._setError(KEY, errors);
            
            return this._hasError(KEY);
        }
        _ckfPic(){
            const KEY = 'fPic',
                  { fPic } = this.state,
                  errors = [],
                  { intl } = this.props;
            if(!+fPic){
                errors.push(intl.formatMessage({id:"触发价格错误"}))
            }
            
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }

        _cktransAmount(){


            const KEY = 'transAmount',
            { transAmount} = this.state,
                  errors = [],
                  { intl } = this.props;
            // this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: "XXX的划转数量不得为空"}).replace('XXX',this.state.coin));
            if(!transAmount || !isFloat(transAmount)||transAmount == 0){
                errors.push(intl.formatMessage({id: "划转数量格式不正确。"}));
            }
            if(!isFloat(transAmount)){
                this.setState({
                    transAmount:''
                })
            }
            this._setError(KEY, errors);
            return this._hasError(KEY);
        }
        _ckcashAmount(){
             let rt = true;
            const errors = [],
                  { cashAmount} = this.state,
                  {minD,downloadLimit,dayCash,everyTimeCash,fees} = this.state.drawList,
                  { intl } = this.props;
            const KEY = 'cashAmount';



            if(!cashAmount || !isFloat(cashAmount)){
            errors.push(intl.formatMessage({id: "XXX的提现数量不得为空"}).replace('XXX',this.state.UpperCurrentCoin));
            }
            if(cashAmount < minD){
                errors.push(intl.formatMessage({id: "数量不得低于XXX"}).replace('XXX',minD));
            }
            if(cashAmount > everyTimeCash){
                console.log('lala');
                errors.push(intl.formatMessage({id: "bbyh超出该币种单笔提现额度。"}));
            }
            this._setError('cashAmount', errors);
            let actualAccount =''
            if(this.state.cashAmount == ''||!isFloat(cashAmount)){
                this.setState({
                    cashAmount:''
                })
                actualAccount == ''
            }else{
                actualAccount = String(new BigNumber(Number(this.state.cashAmount)).minus(new BigNumber(Number(fees))))
                if(actualAccount<=0){
                    actualAccount = 0
                }
            }
            this.setState({
                actualAccount,
            })
            return this._hasError(KEY);
        }

        // check
        _cklastName(){
            let rt = true;
            const errors = [];
            const KEY = 'lastName';
            const { formatMessage } = this.intl;

            this._isEmpty(this.state[KEY], errors, formatMessage({id: "请输入姓氏"}));
            this._onlyWorld(this.state[KEY], errors, '姓氏中不得包含数字或特殊字符');
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        _ckfirstName(){
            let rt = true;
            const errors = [];
            const KEY = 'firstName';

            this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: "请输入名字"}));
            this._onlyWorld(this.state[KEY], errors, '名字中不得包含数字或特殊字符');
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        _onlyWorld(d = '', errors = [], msg = ''){
            !/^[a-zA-Z\u4E00-\u9FA5\uF900-\uFA2D ]+$/.test(d) && errors.push(this.intl.formatMessage({id: msg}));
        }
        _ckcardId(){
            let rt = true;
            const errors = [];
            const KEY = 'cardId';

            this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: this.isIdCard ? "请输入身份证号码" : "请输入护照号"}));
            const fn = this.isIdCard ? (sessionStorage.getItem("countryCode") === '+86' ? isIdCardNo : smpIsIdCardNo) : isPassport;
            !fn(this.state[KEY]) && errors.push(this.intl.formatMessage({id: this.isIdCard ? "身份证号码错误" : "护照号码错误"}));

            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        _ckstartDate(){
            let rt = true;
            const errors = [];
            const KEY = 'startDate';

            this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: "请选择证件有效"}));

            this._setError(KEY, errors);

            return this._hasError(KEY);

        }
        _ckendDate(){
            let rt = true;
            const errors = [];
            const KEY = 'endDate';

            this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: "请选择证件有效"}));
            if(new Date(this.state.endDate) - new Date(this.state.startDate) < 0){
                errors.push(this.intl.formatMessage({id: "证件有效期的截止时间不得早于开始时间"}));
            }
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        _ckImgInfo(key, errors, intl){
            const { file=null } = {...this.imgInfo[key]};
            if(file){
                const { size = 0, type = '' } = file;
                const FMB = 5 * 1024 ** 2;
                const types = ['jpg', 'jpeg', 'png'];
                let isError = false;

                if(!types.some(v => type.indexOf(v) > -1)){
                    errors.push(intl.formatMessage({id: '图片格式仅支持.jpg .jpeg .png。'}));
                    isError = true;
                }
                if(FMB < size){
                    errors.push(intl.formatMessage({id: '图片大小不得超过5M。'}));
                    isError = true;
                }

                if(this.imgInfo.fg && isError){
                    errors.unshift(intl.formatMessage({id: '仅支持.jpg .jpeg .png格式照片，大小不超过5M'}));
                }
            }
        }
        _ckfrontalImg(flg = -1){
            let rt = true;
            const errors = [];
            const KEY = 'frontalImg';

            flg && this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: this.sk ? this.sk : (this.isIdCard ? "请上传身份证正面照片" : "请上传护照照片")}));
            this._ckImgInfo(KEY, errors, this.intl);
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        _ckbackImg(flg = -1){
            let rt = true;
            const errors = [];
            const KEY = 'backImg';

            flg && this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: "请上传身份证背面照片"}));
            this._ckImgInfo(KEY, errors, this.intl);
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        _ckloadImg(flg = -1){
            let rt = true;
            const errors = [];
            const KEY = 'loadImg';

            flg && this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: this.isIdCard ? "请上传手持身份证照片" : "请上传手持护照照片"}));
            this._ckImgInfo(KEY, errors, this.intl);
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        _ckpaycode(){
            let rt = true;
            const errors = [];
            const KEY = 'paycode';

            this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: "请输入谷歌验证码"}));
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        _ckaddress(){
            const errors = [];
            const KEY = 'address';

            this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: "请输入提现地址"}));
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        // _ckfrontalImg(){
        //     let rt = true;
        //     const errors = [];
        //     const KEY = 'frontalImg';
        //     const frontalImg = this.state.frontalImg;

        //     this._isEmpty(frontalImg, errors, this.intl.formatMessage({id: "请上传手持证件照。"}));
        //     this._setError(KEY, errors);

        //     return this._hasError(KEY);
        // }
        _cktuijianId(){
            let rt = true;
            return rt;
        }
        _ckcountryCode(){
            return true;
        }
        _ckcountName(){
            return true;
        }
        _ckcardType(){
            return true;
        }
        _ckregAgreement(){
            let rt = true;

            const errors = [],
                  { regAgreement } = this.state,
                  { intl } = this.props;

            !regAgreement && errors.push(intl.formatMessage({id: "请您同意用户服务条款"})) && (rt = false);

            this._setError('regAgreement', errors);

            return rt;
        }
        _ckemail(flg){
            // return this._ckusername();
            let rt = true;

            const errors = [],
                  { email } = this.state,
                  { intl } = this.props,
                  isEml = isEmail(email);

            !email && errors.push(intl.formatMessage({id: "请输入电子邮件地址_w"}));
            !isEml && errors.push(intl.formatMessage({id: "请输入正确的电子邮件地址。"}));

            if(flg === 0){
                // start async
                console.log('0----0');
            }

            this._setError('email', errors);

            return this._hasError("email");
        }
        /**
         *
         * @param {number} flg
         * @description 0 google code
         *              1 mobile code
         */
        _ckccode(flg = -1){
            let rt = true;

            const errors = [];


            return true;
        }
        async _cknike(){
            let rt = true;

            if(this._ckusername()){
                // await
            }

            return rt;
        }
        _ckconfirmPwd(){
            let rt = true;

            const { confirmPwd,  password } = this.state;
            const errors = [];

            if(!confirmPwd || confirmPwd !== password){
                // !confirmPwd  ?
                //              errors.push(this.intl.formatMessage({id: "请输入确认密码"}))
                //              // errors.push(this.intl.formatMessage({id: "您的两次密码输入不一致。"}))
                //              :
                             errors.push(this.intl.formatMessage({id: "您的两次密码输入不一致。"}));

                rt = false;
            }

            this._setError('confirmPwd', errors);

            return rt;
        }
        _ckusername(){
            let rt = true;

            const errors = [],
                  { username } = this.state,
                  { intl } = this.props,
                  isEml = isEmail(username);

            !username && errors.push(intl.formatMessage({id: "请输入电子邮件"}));
            !isEml && errors.push(intl.formatMessage({id: "请输入正确的电子邮件地址。"}));

            if(errors.length || !isEml){
                // this._showMsg(errors[0]);
                rt = false;
                this.emailRepeatFlg++;
            }

            this._setError('username', errors);

            return rt;
        }
        _ckpassword(){
            let rt = true;
            const { password } = this.state;
            const errors = [];
            !password && errors.push(this.intl.formatMessage({id: this.pwdErrorKey || "请输入登录密码"})) && (rt = false);
            (!this.isNotCkPwd || this.isCkPwd) && !ckPwd(password) && errors.push(this.intl.formatMessage({id: this.stPwdErrorKey ? this.stPwdErrorKey : "您的密码需为8-20位，包含字母，数字，符号的两种以上_w"})) && (rt = false);
            this._setError('password', errors);

            return rt;
        }
        _cklpwd(){
            let rt = true;
            const { lpwd } = this.state;
            const KEY = 'lpwd';
            const { formatMessage } = this.intl;

            const errors = [];
            this._isEmpty(lpwd, errors, formatMessage({id: "请输入登录密码"}));
            !this.isNotCkPwd && !ckPwd(lpwd) && errors.push(formatMessage({id: "您的密码需为8-20位，包含字母，数字，符号的两种以上"}));

            this._setError(KEY, errors);

            return this._hasError(KEY);
        }
        ckAsyncError(cb = () => {}){
            const asyncQueue = Object.values(this.asyncErrorQueue);
            if(!asyncQueue.length){
                cb.call(this);
            } else {
                Promise.all(asyncQueue).then((res) => {
                    if(res.every(r => r === true)){
                        cb.call(this);
                    } else {
                        cb.call(this, false);
                    }
                });
            }
        }
        _ckcode(){
            let rt = true;
            const { needImgCode, code } = this.state,
                    errors = [];

            if(needImgCode){
                !code && errors.push(this.intl.formatMessage({id: "图形验证码不得为空"})) && (rt = false);
                if(code){
                    this.asyncErrorQueue.code = new Promise((resove, reject) => {
                        axios.get(DOMAIN_VIP+'/register/checkImgCode?' + qs.stringify({code}))
                        .then(res => {
                            if(!res.data.isSuc){
                                //this.changeImgCode && this.changeImgCode();
                                errors.push(res.data.des);
                                resove(false);
                            }
                            resove(true);
                            this._setError('code', errors);
                        });
                    });
                }
            }
// console.log(needImgCode, !code, errors, this.intl.formatMessage({id: "nuser6"}), rt);
            this._setError('code', errors);

            return rt;
        }
        _ckAll(keys = [], flg = -1){
            let rt = true;
            console.log(keys)
            for(let i in keys){
                if(!this['_ck' + keys[i]](flg)){
                    rt = false;
                    // break;
                }
            }

            return rt;
        }
        // otc-昵称 校验
        _cknickname(){
            let key = 'nickname',
                errors = [],
                len = 8;
            this._isEmpty(this.state[key], errors, this.intl.formatMessage({id: "请输入昵称"}));
            this._onlyEn(this.state[key], errors, this.intl.formatMessage({id: "昵称含有非法符号，请重新输入"}));
            this._ckStringLength(this.state[key], len, errors, this.intl.formatMessage({id: "长度不能大于XXX字符"}).replace(/XXX/,len));
            if (errors.length == 0){
                this._saveNickName(this.state[key],key);
            }
            this._setError(key,errors,0);
            return this._hasError(key)
        }
        // 保存 昵称
        _saveNickName(v,key){
            const colors = ['#9BC979','#E9BE69','#76A4C8','#E4A184','#3E85A2'];
            var n = Math.floor((Math.random()*5));
            let color = colors[n],
                nickname = this.state[key];

            const data = new FormData();
            data.append('nickname', nickname);
            data.append('color', color);
            axios.get(DOMAIN_VIP +'/manage/saveNickName',{
                params:{ nickname: nickname,color: color }
            } ).then((res) =>{
                // console.log(token)
                // console.log(res.data.resMsg.code);
                // let code = res.data.resMsg.code,
                // console.log(JSON.parse(res.data))
                let code=res.data.isSuc
                let msg = res.data.des;
                if (code == true) {
                    optPop(() =>{
                        this.props.getUserInfo();
                    },msg,{timer: 2000})
                    // getUserBaseInfo();
                }else{
                    let errors = []
                    errors.push(msg);
                    this._setError(key,errors,0);
                }
            })
        }
        _ckbankOpeningBank(){
            let key = 'bankOpeningBank',
                errors = [],
                len = 30;
            this._isEmpty(this.state[key], errors, this.intl.formatMessage({id: "请输入开户行"}));
            this._ckStringLength(this.state[key], len, errors, this.intl.formatMessage({id: "长度不能大于XXX字符"}).replace(/XXX/,len));
            this._setError(key,errors,0);
            return this._hasError(key);
        }

        _ckbankOpeningBranch(){
            let key = 'bankOpeningBranch',
                errors = [],
                len = 30;
            this._isEmpty(this.state[key], errors, this.intl.formatMessage({id: "请输入开户支行"}));
            this._ckStringLength(this.state[key], len, errors, this.intl.formatMessage({id: "长度不能大于XXX字符"}).replace(/XXX/,len));
            this._setError(key,errors,0);
            return this._hasError(key);
        }

        _ckbankCard(){
            let key = 'bankCard',
                errors = [],
                len = 19;
            this._isEmpty(this.state[key], errors, this.intl.formatMessage({id: "请输入您的银行卡号"}));
            this._ckStringLength(this.state[key], len, errors, this.intl.formatMessage({id: "长度不能大于XXX字符"}).replace(/XXX/,len));
            this._setError(key,errors,0);
            return this._hasError(key);
        }

        _ckreBankCard(){
            let key = 'reBankCard',
                key2 = 'bankCard',
                errors = [],
                len = 19;
            let b = this.state[key],
                r = this.state[key2];


            this._isEmpty(this.state[key], errors, this.intl.formatMessage({id: "请再次输入您的银行卡号"}));
            this._ckStringLength(this.state[key], len, errors, this.intl.formatMessage({id: "长度不能大于XXX字符"}).replace(/XXX/,len));
            if(b != r){
                errors.push(this.intl.formatMessage({id: "银行卡号和确认卡号不一致，请重新输入"}))
            }
            this._setError(key,errors,0);
            return this._hasError(key);
        }

        _ckaccountNumber(){
            let key = 'accountNumber',
                errors = [];
            this._isEmpty(this.state[key], errors, this.intl.formatMessage({id: "请输入您的支付宝帐号"}));
            this._setError(key,errors,0);
            return this._hasError(key);
        }

        _cktel(){
            let rt = isMobiles(this.state.tel);

            !rt && this._setError('tel', [this.intl.formatMessage({id: "mobile.text11"})]);

            return rt;
        }

        _ckmCode(){
            let rt = true;

            return rt;
        }

        _ckemcode(){
            let rt = true;
            const errors = [];
            const KEY = 'emcode';
            const emcode = this.state.emcode;

            // this._isEmpty(emcode, errors, this.intl.formatMessage({id: "请输入邮箱验证码。"}));
            // emcode.length !== 6 && errors.push(this.intl.formatMessage({id: "请输入正确邮箱验证码。"}));
            this._setError(KEY, errors);

            return this._basecode({
                dkey: KEY,
                eyKey: '请输入邮箱验证码',
                rpKey: '邮箱',
            }) && this._hasError(KEY);
        }

        _ckgcode(){
            let rt = true;
            const errors = [];
            const KEY = 'gcode';
            const intl = this.intl

            this._isEmpty(this.state[KEY], errors, intl.formatMessage({id: "请输入谷歌验证码"}));
            // !this.state.isGetCode && errors.push(intl.formatMessage({id: "请获取%%验证码"}).replace('%%', intl.formatMessage({id: "谷歌"})));
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }

        _basecode(opt = {
            dkey: 'smscode',
            eyKey: '请输入短信验证码',
            rpKey: '短信',

        }){
            const { dkey, eyKey, rpKey } = opt;

            let rt = true;
            const errors = [];
            const KEY = dkey;
            const intl = this.intl;

            let needCkCode = false;

            if(Object.keys(this.state.isGetCodes).length){
                !this.state.isGetCodes[KEY] && (needCkCode = 1);
            } else {
                !this.state.isGetCode && (needCkCode = 1);
            }

            if(needCkCode){
                errors.push(intl.formatMessage({id: "请获取%%验证码"}).replace('%%', intl.formatMessage({id: rpKey})));
            } else {
                this._isEmpty(this.state[KEY], errors, intl.formatMessage({id: eyKey}));
            }

            this._setError(KEY, errors);
            return this._hasError(KEY);
        }

        _ckemailcode(){
            return this._basecode({
                dkey: 'emailcode',
                eyKey: '请输入邮箱验证码',
                rpKey: '邮箱',
            });
        }
        _ckmemo(){
            const errors = [];
            const KEY = 'memo';
            const intl = this.intl;
            const { memo } = this.state
            const patrn = /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）¥——\-+={}|《》？：“”【】、；‘’，。、]/im;
            // this._isEmpty(this.state[KEY], errors, intl.formatMessage({id: "请输入地址标签"}));
            memo.length > 20 && errors.push(this.intl.formatMessage({id: "bbyh长度不可以超过20个字符"}));
            if(patrn.test(memo)){
                errors.push(this.intl.formatMessage({id: "bbyh地址备注存在非法字符"}));
            }
            // !this.state.isGetCode && errors.push(intl.formatMessage({id: "请获取%%验证码"}).replace('%%', intl.formatMessage({id: "谷歌"})));
            this._setError(KEY, errors);
            return this._hasError(KEY);
        }
        _ckbbremark(){
            const errors = [];
            const KEY = 'bbremark';
            const intl = this.intl;
            const { bbremark } = this.state
            if(bbremark.trim() == ''){
                errors.push(this.intl.formatMessage({id: "请输入地址标签"}));
            }
            // const patrn = /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）¥——\-+={}|《》？：“”【】、；‘’，。、]/im;
            // // this._isEmpty(this.state[KEY], errors, intl.formatMessage({id: "请输入地址标签"}));
            // bbremark.length > 20 && errors.push(this.intl.formatMessage({id: "withdraw.text78"}));
            // if(patrn.test(bbremark)){
            //     errors.push(this.intl.formatMessage({id: "标签存在非法字符"}));
            // }
            // // !this.state.isGetCode && errors.push(intl.formatMessage({id: "请获取%%验证码"}).replace('%%', intl.formatMessage({id: "谷歌"})));
            this._setError(KEY, errors);
            return this._hasError(KEY);
        }
        _cksmscode(){
            return this._basecode();
        }

        _ckmobile(){
            const { mobile } = this.state,
                  errors = [],
                  isNMobile = !isMobiles(mobile);

            let rt = true;

            !mobile && errors.push(this.intl.formatMessage({id: "请输入手机号"}));
            isNMobile && errors.push(this.intl.formatMessage({id: "正确的手机格式"}));

            if(!mobile || isNMobile){
                rt = false;
            }

            this._setError('mobile', errors);

            return rt;
        }

        _ckcodegoogle(){
            let rt = true;
            const errors = [];
            const KEY = 'codegoogle',
                  intl = this.intl;

            this._isEmpty(this.state.code, errors, intl.formatMessage({id: "请输入谷歌验证码"}));
            !this.state.isGetCode && errors.push(intl.formatMessage({id: "请获取%%验证码"}).replace('%%', intl.formatMessage({id: "谷歌"})));
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }

        _ckcodesms(){
            let rt = true;
            const errors = [];
            const KEY = 'codesms',
                  intl = this.intl;


            if(!this.state.isGetCode){
                errors.push(intl.formatMessage({id: "请获取%%验证码"}).replace('%%', intl.formatMessage({id: "短信"})));
            } else {
                this._isEmpty(this.state.code, errors, intl.formatMessage({id: "请输入短信验证码"}));
            }

            this._setError(KEY, errors);
            return this._hasError(KEY);
        }

        _ckcodeType(){
            let rt = true;

            return rt;
        }

        _cksecurity(){
            let rt = true;

            return rt;
        }

        _ckpayPwd(){
            let rt = true;

            const KEY = 'payPwd',
                  errors = [];
            try{
                !this.state.drawList.safePwd&&errors.push(this.intl.formatMessage({id: "设置资金密码"}));
            }
            catch(e){

            }
            this._isEmpty(this.state[KEY], errors, this.intl.formatMessage({id: "请输入资金密码（提示）"}));

            this._setError(KEY, errors);
            return this._hasError(KEY);
        }


        _ckselectCode(){
            let rt = true;

            return rt;
        }

        _ckpayAddress(){
            let rt = true;
            const errors = [];
            const KEY = 'payAddress';

            this._isEmpty(this.state.payAddress, errors, this.intl.formatMessage({id: "请输入充值地址"}));
            this._setError(KEY, errors);

            return this._hasError(KEY);
        }

        fIn(e){
            const key = this._getTgKey(e);
            key === 'password' && this.setState({
                mvInPwd: 1
            })
            this._setError(key, []);
            this._setError(key, [], 1);
        }

        bOut(e){
            console.log(e)
            const k = this._getTgKey(e);
            console.log(k)
           if(this.state.focusBlu){
                this.setState({
                    focusBlu:''
                })
            }

            this.clearQueueKey(k);
            this.hasError([k]);
            this.Uc(k);
        }
        // 资金密码及时校验
        payPwdCheck(e){
            const k = this._getTgKey(e);
            console.log(k)
            let errors = []
            if(this.state[k]!==''){
                axios.post(DOMAIN_VIP+'/manage/account/download/checkSafe',
                qs.stringify({safePwd: this.state[k]}))
                .then((res) => {
                if(res.data.isSuc == false){
                    errors.push(res.data.des)
                    this._setError(k, errors);

                }
                });
            }
            this.clearQueueKey(k);
            this.hasError([k]);
            this.Uc(k);

         }
         emailcodeCheck(e){

         }
        Uc(key = ''){
            if(['lastName', 'firstName'].some(r => r === key)){
                const v = this.state[key];

                if(v){
                    const item = v.split('');
                    const fst = item[0].toUpperCase();
                    item.shift();

                    this.setState({
                        [key]: fst + item.join(""),
                    });
                }

            }
        }

        _getTgKey(e, key = ''){
            const tg = (e.target || e);
            const name = tg.getAttribute(key ? key : 'name');
            return name ? name : tg.getAttribute('stopname');
        }

        ckKeyDown(e){
            return false;
            if(this.state.isGetCode){
                const name = 'ky' + this._getTgKey(e);
                const url = this._getTgKey(e, 'alt') || '';
                const email = this._getTgKey(e, 'list') || '';
                const codeType = this._getTgKey(e, 'form');
                const target = e.target;
                const isGoogle = this._getTgKey(e, 'cc') || '';

                clearTimeout(this.queueTimer[name]);

                this.queueTimer[name] = setTimeout(() => {

                    let v = target.value;
                    if(this._isSendCode(name, v)){
                        if(!this._isSendLock){
                            axios.post(url, qs.stringify({[isGoogle ? 'code' : 'code']: v, email, codeType, flag: true})).then((res) => {
                                if(!res.data.isSuc){
                                    try{
                                        res.data.datas.smscode && (res.data.datas.codesms = res.data.datas.smscode);
                                    } catch(e){

                                    }
                                    this.makeResult(res.data, true);
                                }
                                this.setState({
                                    [name]: res.data.isSuc
                                })
                            });
                            this._isSendLock = 1;
                        }
                    } else {
                        this._isSendLock = 0;
                    }

                }, KEYDOWNTIME);
            }
        }

        _isSendCode(name = '', v){
            return name.indexOf('sms') > -1 && v.length === 4 || name.indexOf('google') > -1 && v.length === 6;
        }

        hasError(keys = [], flg = -1){
            let rt = false;
            if(this._isArray(keys)){
                if(!this._ckAll(keys, flg)){
                    rt = true;
                }
            } else {
                NotArrayException();
            }

            rt ?
                this.setState({
                    hasErrorRepeat: ++this.state.hasErrorRepeat
                })
                :
                this.setState({
                    hasErrorRepeat: 0
                });

            if(this.emailRepeatFlg > REPEAT){
                // this.setNeedImgCode();
            }
            return rt;
        }

        fbEvent(k, opt = 0){
            // when click submit button, start focus and blur check.
            if(this.state.cm){
                if(!opt){
                    this.setState({
                        errors: {
                            ...this.state.errors,
                            [k]: []
                        }
                    });
                } else {
                    this.clearQueueKey(k);
                    this.hasError([k]);
                }
            }
        }
        clearQueueKey(k = ''){
            if(this.state[k]){
                this.callError(k);
            }
        }
        initGetCodes(key = []){
            !this._isArray(key) && NotArrayException();

            let r = {};

            key.reduce((obj, k) => {
                obj[k] = false;
                return obj;
            }, r);

            this.setState({
                isGetCodes: r
            });
        }

        formatAuthDic(selectedCode = '0', dictionaries = []){
            return dictionaries.concat(Object.keys(selectedCode === '0' ? (this._gbase) : (selectedCode === '1' ? this._smsbase : {})));
        }

        cbShowAllErrors(errors = []){

            const keys = Object.keys(errors);

            this._isObject(errors) && (errors = [
                ...keys.map(v => ({
                    key: v,
                    msg: errors[v],
                }))
            ]);

            !this._isArray(errors) && NotArrayException();

            errors.forEach(v => {
                this.callError(v.key, v.msg);
            });
        }

        makeResult(res = {}, showSuccess = false, opt = {timer: 3000}){
            if(!res.des){
                //console.log('000000000000000')
                this.cbShowAllErrors(res.datas);
            } else {
                if(res.datas.isJump){
                    optJump('/bw/trade/',res.des,this.intl.formatMessage({id: "withdraw.text49"}))
                }else{
                    optPop(() => {}, res.des, opt, showSuccess);
                }
            }
        }

        // base
        _getType(k){
            return Object.prototype.toString.call(k).slice(8, -1);
        }
        _isArray(ay = []){
            return this._getType(ay) === 'Array';
        }
        _isObject(obj = {}){
            return this._getType(obj) === 'Object';
        }
        _isAllNumber(number = ''){
            return /^\d+$/.test(number);
        }
        _clearStateKey(key = '', v = {}){
            const cv = v.target.value;
            if(cv && !this._isAllNumber(cv)){
                const ln = cv.substring(0, cv.length -1);
                setTimeout(() => {
                    this.setState({
                        [key]: this._isAllNumber(ln) ? ln : '',
                    })
                }, 0)
            }
        }
        // render
        render(){
            return super.render();
        }
    }

    return Rt;
};

