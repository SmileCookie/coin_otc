import React from 'react';
import axios from 'axios';
import {connect} from 'react-redux';
import { Link } from 'react-router';
import qs from 'qs';
import cookie from 'js-cookie';
import { browserHistory } from 'react-router';
import Confirm from '../../components/msg/confirm';
import { FormattedMessage,FormattedHTMLMessage} from 'react-intl';
import Form from '../../decorator/form';
import '../../assets/css/userauth.less';
import { formatURL, optPop } from '../../utils';
//import { changeImgCode } from '../../redux/modules/session';
import { fetchGetReferee,rememberSiginInfor} from '../../redux/modules/session';
import { URL_IMG_CODE, AUTH_URL, DOMAIN_VIP, DOMAIN_COOKIE, COOKIE_EXPIRED_DAYS, COOKIE_FIRST ,COOKIE_LAN} from '../../conf';
import Strength from '../../components/user/strength';
import CheckBox from '../../components/form/checkbox';

@connect(
    state => ({session: state.session}),
    { fetchGetReferee,rememberSiginInfor }
)
@Form
class SignupForm extends React.Component{
    constructor(props) {
        super(props);
        
        // this.pwdErrorKey = '您的密码需为8-20位，包含字母，数字，符号的两种以上';

        const tid = props.location.query.tid;

        this.base = {
            email: '',
            password: '',
            code: '',
            tuijianId: tid ? tid : '',
            confirmPwd: '',
            regAgreement: false,
        }

        this.state = {
            ...this.base,
            ckFlg: false,
            showPwd: false,
            showLongErrDig: false,
            showLongErrText: '',
            //控制是否显示同意用户协议
            isShowUserConfrim:false
        };
        
        this.dictionaries = [...Object.keys(this.base)];
        this.cm = this.cm.bind(this);
        this.setShowPwd = this.setShowPwd.bind(this);
        this.setLevel = this.setLevel.bind(this);
        this.setCk = this.setCk.bind(this);
        this.cb = this.cb.bind(this);
        this.notCk = true;
        this.rememberSigin = this.rememberSigin.bind(this);
    }

    setCk(state){
        this.setState({
            regAgreement: state,
            isShowUserConfrim:true
        });
    }
    setLevel(level){
        // console.log(level);
    }
    ss(){
        console.log(111)
        if(document.body.clientHeight>document.getElementById("root").clientHeight){
           
        var fhei=document.getElementById("footer").clientHeight
        let heights= document.body.clientHeight-40-fhei-100-140
        document.getElementById("center_d").style.height=heights+"px"
        console.log(fhei)
    }else{
        console.log("内容填满屏幕")
    }
    }
    setShowPwd(){
        this.setState({
            showPwd: !this.state.showPwd
        })
    }

    componentWillMount(){
        this.changeImgCode();
        let {rememberSigin} = this.props.session;
        // console.log(this.state)
        setTimeout(this.ss,1)
        if(JSON.stringify(rememberSigin) !== '{}'){
            this.setState({
                email:rememberSigin.email,
                password:rememberSigin.password,
                confirmPwd:rememberSigin.confirmPwd,
                code:rememberSigin.code
            })
        }
    }

    componentDidMount(){
        this.setNeedImgCode();
        
        this.props.fetchGetReferee();
    }

    setCkFlg(){
        this.setState({
            ckFlg: true
        })
    }
    
    componentDidUpdate(){
        if(!this.fstRd && this.state.regAgreement){
            this.setCkFlg();
            this.fstRd = true;
        }
    }

    cm(){
        this.setCkFlg();
        this.ckAsyncError(this._cm);
    }
    _cm(as = true){
        if(!this.hasError(this.dictionaries, 0)){
            if(as){
                const regData = this.getState(this.dictionaries);
                // console.log(regData);  
                axios.post(DOMAIN_VIP +"/register/emailReg", qs.stringify(regData)).then((res) => {
                    
                    res = res.data;

                    if(res.isSuc){
                        cookie.set(COOKIE_FIRST, 1, {
                            expires: COOKIE_EXPIRED_DAYS,
                            domain: DOMAIN_COOKIE,
                            path: '/'
                        });
                        localStorage.setItem("email", this.state.email);
                        localStorage.setItem("id", res.des);
                        this.clearsError(['email', 'code'], 1);
                        optPop(() => {
                            // jump to emailTips
                            browserHistory.push(formatURL('active'));
                        }, this.props.intl.formatMessage({id: "注册成功，请您登录邮箱激活帐号!"}));
                        
                    } else {
                        // res.des = 'IP';
                        this.complete();
                        // clear form all error.
                        // if(res.des.indexOf('IP') === -1){
                        //     this.clearsError(['email', 'code'], 1);
                        //     setTimeout(() => {
                        //         this.callError(res.datas, res.des);
                        //     });
                        // } else {
                        //     this.optLongErrDig(true);
                        //     this.setState({
                        //         showLongErrText: res.des,
                        //     })
                        // }
                        
                        if(res.des){
                            this.optLongErrDig(true);
                            this.setState({
                                showLongErrText: res.des,
                            })
                        } else {
                            this.clearsError(['email', 'code'], 1);
                            setTimeout(() => {
                                this.cbShowAllErrors(res.datas);
                            });
                        }
                    }
                }, this.complete);
            } else {
                this.changeImgCode();
            }
        } else {
            this.changeImgCode();
        }
    }

    optLongErrDig(opt = false){
        this.setState({
            showLongErrDig: opt
        });
    }

    complete(){
        this.setState({ code: '' });
        this.changeImgCode();
    }

    cb(){
        this.optLongErrDig();
    }

    //save infor
    rememberSigin(){
        let {email,password,confirmPwd,code} = this.state;
        let _obj = {
            email,
            password,
            confirmPwd,
            code
        }
        console.log(_obj)
        // this.props.rememberSiginInfor(_obj)
    }

    render(){
        const { formatMessage } = this.intl;
        const { changeImgCode, setRegAgreement, setEmail, setPwd, setConfirmPwd, setCode, cm, setTuijianId, fIn, bOut, setShowPwd, setLevel, setCk, cb } = this;
        const { imgCode, referee ,rememberSigin} = this.props.session;
        const { regAgreement, email, password, confirmPwd, code, tuijianId, errors, ckFlg, showPwd, mvInPwd, showLongErrDig, showLongErrText ,} = this.state;
        const { email:eemail = [], password:epassword = [], confirmPwd:econfirmPwd = [], code: ecode = [], regAgreement:eregAgreement = []} = errors;
         let _lan = cookie.get(COOKIE_LAN);
        return (
            <form className="uauth_wp pt_d clearfix">
                {
                    showLongErrDig ? <Confirm msg={showLongErrText} ok={formatMessage({id: "我知道了"})} isNotCancel={true} cb={cb} /> : null
                }
                <div className="center_d" id="center_d">
                    <div className="l l_d">
                        <h2 className="tith">{formatMessage({id: "欢迎注册"})}</h2>
                        <ul className="list">
                            {/* <li className="fst">
                                <p className="mb10">
                                <svg className="icon icon_d14" aria-hidden="true"><use xlinkHref="#icon-denglu-tishi"></use></svg><span className="tith2">{formatMessage({id: "请确认您正在访问："})} {AUTH_URL}</span>
                                </p>
                                <div className="plv">
                                    <em className="lock"><svg className="icon icon_d14" aria-hidden="true"><use xlinkHref="#icon-denglu-suo"></use></svg></em>
                                    <input type="text" readOnly={true} value={AUTH_URL} className="i0" />
                                </div>
                            </li> */}

                            <li className={eemail[0] && 'err'}>
                                <h3>{formatMessage({id: "电子邮件"})}</h3>
                                <input type="text" className="lj" name="email" placeholder={formatMessage({id: "请输入电子邮件（水印）"})} />
                                <input autoComplete="off" onFocus={fIn} onBlur={bOut} name="email" value={email} onChange={setEmail} type="text" className="i1" placeholder={formatMessage({id: "请输入电子邮件（水印）"})} tabIndex="1" />
                              
                                <span className="ew">{eemail[0]}</span>
                            </li>

                            <li className={epassword[0] && 'err'}>
                                <h3>{formatMessage({id: "密码"})}</h3>
                                <div className="plv">
                                {
                                    false
                                    &&
                                    <span onClick={setShowPwd} className={`sw iconfont ${showPwd ? 'ac' : ''}`}><svg className="icon icon_d14" aria-hidden="true"><use xlinkHref="#icon-yincangmima-guan"></use></svg></span>
                                }
                                    <input type="password" className="lj" />
                                    <input autoComplete="off" maxLength="20" onFocus={fIn} onBlur={bOut} name="password" value={password} onChange={setPwd} type={showPwd ? 'text' : 'password'} className="i1" placeholder={formatMessage({id: "请输入登录密码（水印）"})} tabIndex="2" />
                                    <input type="password" className="lj" />
                                </div>
                                {
                                mvInPwd ?
                                <div className="clearfix">
                                    <Strength val={password} funct={setLevel} />
                                </div>
                                : null
                                }
                                <span className="ew">{epassword[0]}</span>
                            </li>

                            <li className={econfirmPwd[0] && 'err'}>
                                <h3>{formatMessage({id: "确认密码"})}</h3>
                                <input type="password" className="lj" />
                                <input autoComplete="off" maxLength="20" onFocus={fIn} onBlur={bOut} name="confirmPwd" type="password" value={confirmPwd} onChange={setConfirmPwd} className="i1" placeholder={formatMessage({id: "请输入确认密码（水印）"})} tabIndex="3" />
                                <input type="password" className="lj" />
                                <span className="ew">{econfirmPwd[0]}</span>
                            </li>
                            {
                            tuijianId
                            &&
                            <li>
                                <h3>{formatMessage({id: "推荐人ID"})}</h3>
                                <input autoComplete="off" name="tuijianId" disabled={true} type="text" value={tuijianId} className="i1" placeholder={formatMessage({id: "nuser77"})} tabIndex="4" />
                            </li>
                            }

                            <li className={`plv lst2 ${ecode[0] && 'err'}`}>
                                <h3>{formatMessage({id: "图形验证码"})}</h3>
                                <div className="plv">
                                    <input maxLength="10" autoComplete="off" onFocus={fIn} onBlur={bOut} name="code" type="text" value={code} onChange={setCode} className="i1" placeholder={formatMessage({id: "请输入图形验证码（水印)"})} tabIndex="5" />
                                    <img src={imgCode} onClick={changeImgCode} className="imgCode" />
                                </div>
                                <span className="ew">{ecode[0]}</span>
                            </li>
                            <li className={`readme lst ${!regAgreement && ckFlg && 'bbyh-sp-err'}`}>
                                <CheckBox setCk={setCk} isCk={regAgreement} key={regAgreement+""}  />
                                <input style={{visibility: 'hidden'}} type="checkbox"  name="regAgreement" className="agreement icon_d14" checked={regAgreement} onChange={setRegAgreement}  />
                                {
                                    _lan == 'jp'?
                                    <span style={{cursor: 'pointer',verticalAlign: 'initial'}} onClick={()=>{setCk(!regAgreement)}}>
                                        <FormattedMessage  id="我已阅读并同意《用户协议》" values={{name : <a href="/terms/service" style={{color:'rgb(62, 133, 162)',verticalAlign:'baseline'}}>{'「利用規約」'}</a>}} />
                                        <span className="ew" >{ckFlg && !regAgreement && formatMessage({id: "请您同意用户服务条款"})}</span>
                                    </span>
                                    :
                                    _lan == 'kr'?
                                    <span style={{cursor: 'pointer',verticalAlign: 'initial'}} onClick={()=>{setCk(!regAgreement)}}>
                                        <FormattedMessage  id="我已阅读并同意《用户协议》" values={{name : <a href="/terms/service" style={{color:'rgb(62, 133, 162)',verticalAlign:'baseline'}}>{'이용약관'}</a>}} />
                                        <span className="ew" >{ckFlg && !regAgreement && formatMessage({id: "请您同意用户服务条款"})}</span>
                                    </span>
                                    :
                                    <span>
                                         <span style={{cursor: 'pointer',verticalAlign: 'initial'}} onClick={()=>{setCk(!regAgreement)}}>{formatMessage({id: "我已阅读并同意"})}</span><a href="/terms/service" target="_blank" className="rm" ><span style={{color:'#3E85A2','verticalAlign': 'baseline'}}>{formatMessage({id: "《用户协议》"})}</span></a>
                                         <span className="ew" >{ckFlg && !regAgreement && formatMessage({id: "请您同意用户服务条款"})}</span>
                                    </span>

                                }
                               
                            </li>
                        </ul>

                        <div className="subs">
                            <input onClick={cm} style={{display:_lan == 'jp'?'block':'inline-block'}} type="button" value={formatMessage({id: "立即注册"})} className="i3 v" />
                            <span className="c1">{formatMessage({id: "已有账号？"})}</span>
                            <Link to={formatURL('login')} className="i4 v">{formatMessage({id: "去登录"})}</Link>
                        </div>
                    </div>

                    <div className="bbyh-signUpText">
                        <div className="p0 sp">
                            <div className="bbyh-headers">
                                <p className="mb10">
                                    <svg className="icon icon_d14" aria-hidden="true"><use xlinkHref="#icon-denglu-tishi"></use></svg><span className="tith2">{formatMessage({id: "请确认您正在访问："})} {AUTH_URL}</span>
                                </p>
                                <div className="plv">
                                    <em className="lock"><svg className="icon icon_d14" aria-hidden="true"><use xlinkHref="#icon-denglu-suo"></use></svg></em>
                                    <input type="text" readOnly={true} value={AUTH_URL} className="i0" />
                                </div>
                            </div>
                            <p>{formatMessage({id: "您提供的电子邮件地址"})}</p>
                            <p>{formatMessage({id: "将成为您的UID，作为您帐户的唯一标识。"})}</p>
                        </div>
                        <ul className="list2">
                            {
                            false
                            &&
                            <li>
                                <svg className="icon" aria-hidden="true"><use xlinkHref="#icon-zhucewenanicon"></use></svg>
                                <span>{formatMessage({id: "为了更便捷的操作，推荐使用手机号码注册帐户"})}</span>
                            </li>
                            }
                            <li>
                                <svg className="icon" aria-hidden="true"><use xlinkHref="#icon-zhucewenanicon"></use></svg>
                                <span>{formatMessage({id: "请不要使用您在其他地方使用的登录密码_w"})}</span>
                            </li>
                            <li>
                                <svg className="icon" aria-hidden="true"><use xlinkHref="#icon-zhucewenanicon"></use></svg>
                                <span>{formatMessage({id: "您的密码需为8-20位，包含字母，数字，符号的两种以上"})}</span>
                            </li>
                        </ul>
                    </div>
                </div>
            </form>
        );
    }
}

export default SignupForm;