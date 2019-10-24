import React, { Component } from 'react';
import { Link } from 'react-router';
import SignupTip from './signupTip';
import { URL_IMG_CODE, DISMISS_TIME,DOMAIN_VIP,DOMAIN_COOKIE, COOKIE_EXPIRED_DAYS, COOKIE_FIRST } from '../../conf';
import { hasChinese,hasOther,hasLetter,isEmail,isAllNumber } from '../../utils';
import { FormattedMessage, injectIntl } from 'react-intl';
import axios from 'axios';
import GetCode from '../../components/phonecode/getCode';
import list from '../../components/phonecode/country';
import Strength from '../../components/user/strength';
import cookie from 'js-cookie';

const qs = require('qs');

class SignupForm extends Component {

    constructor(props) {
        super(props)
        this.state = {
            paseord_fed:false,  //密码强度提示
            userType:2,   //注册类型  1 手机   2 邮箱
            checked: true,  //是否勾选用户须知
            userName:"",   //用户名
            setPassword:"", //密码
            repeatPassword:"", //再次输入密码
            pwdLevel:0,   //密码长度
            needImgCode: true,
            takeMsgCode: false,
            msgCode: '',
            regAgreement: true,
            imgCode: '',
            selectedCode: '+86',
            ucontrol: false,
            countryCode: '+86',
        }
 
        this.checkout_cked = this.checkout_cked.bind(this);//阅读条款
        this.submit_click = this.submit_click.bind(this);// 点击注册
        this.input_name_change = this.input_name_change.bind(this); //手机号码/电子邮箱
        this.username_check = this.username_check.bind(this);  //注册时 用户名验证
        this.input_setpassword_change = this.input_setpassword_change.bind(this); //密码
        this.input_setpassword_focus = this.input_setpassword_focus.bind(this); //密码focus
        this.input_setpassword_blur = this.input_setpassword_blur.bind(this);    // 密码blur
        this.input_repeatPassword_change = this.input_repeatPassword_change.bind(this) // 重复输入密码
        this.changeImgCode = this.changeImgCode.bind(this); //验证码图片
        this.input_imgCode_change = this.input_imgCode_change.bind(this);
        this.input_msgCode_change = this.input_msgCode_change.bind(this);
        this.getMsgCode = this.getMsgCode.bind(this);
        this.getCurrentSelectedCode = this.getCurrentSelectedCode.bind(this);

        this.checkUserNike = this.checkUserNike.bind(this);

        this.intl = props.intl;

        this.lockRequest = false;
    }
    input_name_change (e){// 手机号码/电子邮箱
        let value = e.target.value;
        this.setState({ userName:value });
    }
    input_imgCode_change (e){
        let value = e.target.value;
        this.setState({
            imgCode: value
        })
    }
    getMsgCode(){
        
        if(this.lockRequest){
            return this.props.notifSend({
                message: this.intl.formatMessage({id: "sellbuy.p9"}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
        } else {
            let { userName,userType,imgCode,needImgCode } = this.state;

            if(userName.length < 6) {
                return this.props.notifSend({
                    message: this.intl.formatMessage({id: "reg.text2"}),
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
            }

            // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

            if(userType == 1 && !isAllNumber(userName.replace(/[+,-]/g,""))) {
                return this.props.notifSend({
                    message: this.intl.formatMessage({id: "reg.text20"}),
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
            }

            if(imgCode.length < 4 && needImgCode) {
                return this.props.notifSend({
                    message: this.intl.formatMessage({id: "reg.text15"}),
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
            }

            let data = {
                phonenumber: userName,
        		countryCode: this.state.countryCode,
        		code: imgCode,
    			codeType: 1
            }

            this.lockRequest = true;

            console.log(data);

            axios.post(DOMAIN_VIP + "/register/sendCode", qs.stringify(data))
            .then((res)=>{
                res = res.data;
                if(res.isSuc){
                    this.setTime();
                } else {
                    this.setState({
                        imgCode: ''
                    })
                    this.changeImgCode();
                }

                this.props.notifSend({
                    message: res.des,
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
                
            },()=>{})['finally'](()=>{
                this.lockRequest = false;
            })
            

        }

        // console.log(axios);
    }

    setTime(){
        let time = 60;
        const sms = this.refs.sms;

        !this.bt && (this.bt = sms.innerHTML);

        let run = function(){
            setTimeout(()=>{
                if(--time > 0){
                    sms.innerHTML = '<span>' + this.intl.formatMessage({id: "user.text129"}).replace('[$1]', time) + '</span>';
                    sms.setAttribute("disabled", "disabled");
                    run.call(this);
                } else {
                    sms.innerHTML = this.bt;
                    sms.removeAttribute("disabled");
                }
            }, 1000);
        }

        run.call(this);
        
    }

    checkUserNike(){

        let { userName:nike } = this.state;

        if(nike.length == 0){
            this.setState({
                ucontrol: false
            })
        } else {

            nike = nike.trim();

            if( hasLetter(nike) || hasOther(nike) || hasChinese(nike) ){
                if(isEmail(nike)){
                    this.setState({
                        userType: 2
                    })
                } else {
                    this.setState({
                        userType: 0
                    })
                }
                this.setState({
                    ucontrol: false
                })
            } else {
                this.setState({
                    ucontrol: true,
                    userType: 1
                })
            }
        }
    }

    username_check (){ //注册时 用户名验证
        let { userName,userType } = this.state;
        userName = userName.trim();
        if(typeof userName == "undefined" || userName.length == 0 ){  //没有数据时，短信验证框隐藏
        }
        if( hasLetter(userName) || hasOther(userName) || hasChinese(userName) ){ //注册是否为空
           if(isEmail(userName)){
                userType = 2;
           }
           else{
                userType = 0;
           }
        }else{
            userType = 1;
        }
        return userType;
    }
    input_setpassword_change (e){// 密码
        let value = e.target.value;
        this.setState({ setPassword:value, paseord_fed:true });
    }
    input_setpassword_focus(){  // 密码验证提示框
        this.setState({ paseord_fed:true });
    }
    input_setpassword_blur(){   //密码强度验证
        let { setPassword,pwdLevel } = this.state;
        let lever = 0;
        if (setPassword.length >= 8 && setPassword.length <= 20){
            if (/\d/.test(setPassword)) lever++; 
            if (/[a-z]/.test(setPassword)) lever++; 
            if (/[A-Z]/.test(setPassword)) lever++; 
            if (/\W/.test(setPassword)) lever++; 
            if (lever > 1 && setPassword.length > 12) lever++;
        }
        lever = lever*20;
        if( lever > 20 ){
            this.setState({ pwdLevel:lever, paseord_fed:false });
        }
        else{
            this.setState({paseord_fed:true});
        }
    }
    input_repeatPassword_change (e){ // 重复输入密码
        let value = e.target.value;
        this.setState({ repeatPassword:value });
    }

    input_msgCode_change(e){
        let value = e.target.value;
        this.setState({ msgCode:value });
    }

    changeImgCode() { //验证码图片
        let now = +new Date;
        this.props.up_changeImgCode(URL_IMG_CODE + "?t=" + now);
    }

    getCurrentSelectedCode(code = "", name = ""){
        //console.log(code, name);
        //this.setState({countryCode:code})
        this.setState({countryCode:code, selectedCode:code})
    }
    
    submit_click(){ //点击注册
        // 当提交后，再发送请求禁止再次点击
        if(this.lockRequest){
            this.props.notifSend({
                message: this.intl.formatMessage({id: "sellbuy.p9"}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
        } else {
            let userType = this.username_check(); //用户名验证 注册类型判断
            let { countryCode, userName ,setPassword,pwdLevel,repeatPassword, needImgCode, imgCode, takeMsgCode, msgCode, regAgreement} = this.state;
            // if(userType == 0) {
            if(userName == ""){
                return this.props.notifSend({
                        message: this.intl.formatMessage({id: "reg.text1"}),
                        kind: 'info',
                        dismissAfter: DISMISS_TIME
                    });
            }
            if(userName.length < 6){
                return this.props.notifSend({
                        message: this.intl.formatMessage({id: "reg.text2"}),
                        kind: 'info',
                        dismissAfter: DISMISS_TIME
                    });
            }
            if(userType == 2 && !isEmail(userName)){
                return this.props.notifSend({
                        message: this.intl.formatMessage({id: "reg.text3"}),
                        kind: 'info',
                        dismissAfter: DISMISS_TIME
                    });
            } 
            if(userType == 1 && !isAllNumber(userName.replace(/[+,-]/g,""))){
                return this.props.notifSend({
                        message: this.intl.formatMessage({id: "reg.text4"}),
                        kind: 'info',
                        dismissAfter: DISMISS_TIME
                    });
            }
            if(pwdLevel < 40) {
                return this.props.notifSend({
                        message: this.intl.formatMessage({id: "user.text99"}),
                        kind: 'info',
                        dismissAfter: DISMISS_TIME
                    });
            }
            if(setPassword != repeatPassword) {
                return this.props.notifSend({
                        message: this.intl.formatMessage({id: "user.text100"}),
                        kind: 'info',
                        dismissAfter: DISMISS_TIME
                    });
            }

            if(imgCode.length < 4 && needImgCode) {
                return this.props.notifSend({
                    message: this.intl.formatMessage({id: "reg.text15"}),
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
            }

            if(userType == 1 && msgCode == '') {
                return this.props.notifSend({
                    message: this.intl.formatMessage({id: "reg.text16"}),
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
            }

            if(userType == 1 && msgCode.length < 6) {
                return this.props.notifSend({
                    message: this.intl.formatMessage({id: "reg.text17"}),
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
            }

            if(regAgreement == false) {
                return this.props.notifSend({
                    message: this.intl.formatMessage({id: "reg.text18"}),
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                });
            }

            this.lockRequest = true;
            
            const regData = {
                phonenumber: userName,
                email: userName,
                password: setPassword,
                code: imgCode,
                mobileCode: msgCode,
                pwdLevel,
                tuijianId: '',
                countryCode: countryCode,
                regAgreement
            };

            function complete(){
                // console.log(this);
                this.setState({ imgCode: '' });
                this.changeImgCode();
                this.lockRequest = false;
            }

            axios.post(DOMAIN_VIP +"/register/"+ (userType == 2 ? "emailReg" : "mobileReg"), qs.stringify(regData)).then(
                (res) => {

                    res = res.data;
                    
                    if(res.isSuc){

                        cookie.set(COOKIE_FIRST, 1, {
                            expires: COOKIE_EXPIRED_DAYS,
                            domain: DOMAIN_COOKIE,
                            path: '/'
                        });
                        
                        if(userType == 2){
                            window.location.href = DOMAIN_VIP + "/register/emailTips?type=1&nid="+res.des;
                        } else {
                            this.props.notifSend({
                                message: this.intl.formatMessage({id: "reg.text21"}),
                                kind: 'info',
                                dismissAfter: DISMISS_TIME
                            });

							setTimeout(function(){
								window.location.href = DOMAIN_VIP + "/bw/manage/";
							}, 1500);
                        }

                    }else{
                        complete.call(this);

                        return this.props.notifSend({
                            message: res.des,
                            kind: 'info',
                            dismissAfter: DISMISS_TIME
                        });
                    }
                },
                () => {
                    complete.call(this);
                }
            )['finally'](()=>{

            });

        }
    }
    checkout_cked() {  //阅读条款
        let { checked } = this.state;
        checked = !checked;
        this.setState({ checked })
        this.setState({regAgreement: checked})
    }
    render() {
        const { imgCode } = this.props.session;
        const { ucontrol } = this.state;
        console.log(imgCode);
        return (
            <div className="container">
                <div className="login-main clearfix">
                    <SignupTip />  {/* 左边文案区域 */}
                    <div className="login-box sigup_box clearfix">
                        <h4 className="text_h4"><FormattedMessage id="level.obtainIntegral5" /></h4>
                        <div className="sigin_from">
                            <div className="from_item"> {/* 手机号码/电子邮箱 */}
                                <div className="from_text"><FormattedMessage id="login.box2" /></div>
                                <div className="from_input clearfix">
                                {
                                    ucontrol
                                    &&
                                    <div className="ot_country_select">
                                        <GetCode showCode="1" selectedCode={this.state.selectedCode} list={list.country} getCurrentSelectedCode={this.getCurrentSelectedCode}></GetCode>
                                    </div>
                                }
                                    <div className="ico_div">
                                        <svg className="icon" aria-hidden="true">
                                            <use xlinkHref="#icon-man-user"></use>
                                        </svg>
                                    </div>
                                    <input  type="text" name="nike" id="nike" className="form_control" 
                                            onChange={ this.input_name_change }
                                            onKeyUp={ this.checkUserNike }
                                            onBlur={ this.checkUserNike }
                                            value = { this.state.userName }
                                            tabIndex="10" />
                                </div>
                            </div>
                            <div style={{height: 'auto', paddingBottom: '20px'}} className="from_item"> {/* 密码 */}
                                <div className="from_text"><FormattedMessage id="login.box3" /></div>
                                <div className="from_input clearfix">
                                    <div className="ico_div">
                                        <svg className="icon" aria-hidden="true">
                                            <use xlinkHref="#icon-locked-padlock"></use>
                                        </svg>
                                    </div>
                                    <input  type="password" className="form_control" id="password" 
                                            value = { this.state.setPassword }
                                            onChange={ this.input_setpassword_change }
                                            onFocus = { this.input_setpassword_focus }
                                            onBlur = { this.input_setpassword_blur }
                                            name="password" 
                                            tabIndex="11" />
                                </div>
                                {
                                this.state.paseord_fed && 
                                <div className="o_wp" style={{paddingTop:'5px'}}>
                                    <Strength val={this.state.setPassword} />
                                </div>
                                }
                            </div>
                            {   // 密码强度提示 
                            false && this.state.paseord_fed ? (
                                <div className="passtext"> 
                                    <span className="jingbao_icon">
                                        <svg className="icon" aria-hidden="true">
                                            <use xlinkHref="#icon-tishi"></use>
                                        </svg>
                                    </span>
                                    <FormattedMessage id="user.text24" />
                                </div>
                            ) : ""
                            }
                            <div className="from_item"> {/* 重复输入密码 */}
                                <div className="from_text"><FormattedMessage id="reg.text5" /></div>
                                <div className="from_input clearfix">
                                    <div className="ico_div">
                                        <svg className="icon" aria-hidden="true">
                                            <use xlinkHref="#icon-locked-padlock"></use>
                                        </svg>
                                    </div>
                                    <input  type="password" className="form_control" id="confirmPwd" 
                                            onChange = { this.input_repeatPassword_change }
                                            value = { this.state.repeatPassword }
                                            name="confirmPwd" tabIndex="12" />
                                </div>
                            </div>
                            
                           
                            <div className="from_item"> {/* 图形验证码 */}
                                <div className="from_text"><FormattedMessage id="reg.text6" /></div>
                                <div className="from_input imgCode clearfix">
                                    <div className="ico_div">
                                        <svg className="icon" aria-hidden="true">
                                            <use xlinkHref="#icon-anquan"></use>
                                        </svg>
                                    </div>
                                    <input type="text" name="imgCode" id="imgCode" className="form_control" tabIndex="13" onChange = { this.input_imgCode_change } value = { this.state.imgCode }  />
                                    <img className="imgcode" onClick={this.changeImgCode} src={imgCode} ref="imgCode"  />
                                </div>
                            </div>
                            {
                            ucontrol
                            &&
                            <div className="from_item">    {/* 短信验证码 */}
                                <div className="from_text"><FormattedMessage id="reg.text7" /></div>
                                <div className="from_input clearfix">
                                    <div className="ico_div">
                                        <svg className="icon" aria-hidden="true">
                                            <use xlinkHref="#icon-duanxin"></use>
                                        </svg>
                                    </div>

                                     
                                        <input type="text" name="msgCode" id="msgCode" className="form_control" tabIndex="14" onChange = { this.input_msgCode_change } value = { this.state.msgCode } />
                                        <div className="input-group-btn" style={{position:'relative'}}>
                                            <div className="btn-group" style={{position:'absolute',right:0}}>
                                                <button type="button" role="msgCode" id="sendMsgCode" ref="sms" onClick={this.getMsgCode} className="btn text-nowrap line-left">
                                                    <FormattedMessage id="reg.text19" />
                                                </button>
                                            </div>
                                        </div>
                                    

                                </div>
                            </div>
                            }

                            <div className="checkbox">  {/* 阅读条款 */}
                                <input type="checkbox" onChange={this.checkout_cked} checked={this.state.checked} name="agreement" id="agreement" />
                                <label className="check_label" onClick={this.checkout_cked} ><FormattedMessage id="reg.text8" /></label>
                                <span className="text-blue">&nbsp;<Link to="http://www.common.com/terms/service" target="_blank"><FormattedMessage id="reg.text9" /></Link></span>
                            </div>
                            <section onClick={this.submit_click} className="submit"> {/* 立即注册 */}
                            <FormattedMessage id="login.tip3" />
                            </section>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default injectIntl(SignupForm);
