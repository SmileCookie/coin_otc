import React from 'react';
import { connect } from 'react-redux';
import { browserHistory } from 'react-router';
import cookie from 'js-cookie'
import axios from 'axios'
import Form from '../../decorator/form';
import '../../assets/css/userauth.less';
import { formatURL, optPop } from '../../utils';
import { Link } from 'react-router';
import { login, changeImgCode, loginSuccess, showImgCode, hideImgCode, } from '../../redux/modules/session';
import { URL_IMG_CODE, AUTH_URL,DOMAIN_VIP} from '../../conf';
import JSaw from '../../components/jigsaw/jsaw';

@connect(
    state => ({session: state.session}),
    (dispatch) => {
        return {
            hideImgCode: () => {
                dispatch(hideImgCode());
            },
            doLogin: (data, that) => {

                let nike = data.username||'';
                let pwd = data.password||'';
                let code = data.code||'';
                let countryCode = data.countryCode||'+86';
                let safe = data.safe;
                dispatch(login({
                    nike,
                    pwd,
                    code,
                    countryCode,
                    safe
                })).then((res) => {
                    let result = res.data;
                    if(result.isSuc){
                        cookie.remove("multiTrade")
                        cookie.set("zsessionId",result.datas.sessionId)
                        dispatch(loginSuccess(that));
                    }else{
                        that.setState({
                            dc: 0,
                        });
                        console.log('vvvv---vvvv')
                        result.datas.isRoute && (window.location.href = result.des);

                        let now = +new Date();
                        dispatch(changeImgCode(URL_IMG_CODE + "?t=" + now));
                        console.log(result);
                        
                        if(!result.des){
                            that.clearsError(Object.keys(result.datas), 1);
                            // console.log(result.datas.code);
                            if(result.datas.code){
                                console.log('>><<<<');
                                dispatch(showImgCode());
                            }
                            setTimeout(() => {
                                that.cbShowAllErrors(result.datas);
                            });
                        } else {
                            !result.datas.isRoute && optPop(() => {}, result.des, undefined, true);
                        }

                        that.setCode({
                            target: {
                                value: ''
                            }
                        }); 
                    }
                })
            }
        }
    }
)
@Form
class Login extends React.Component {
    constructor(props){
        super(props);

        this.base = {
            username: '',
            password: '',
            code: '',
        }
        this.state = {
            ...this.base,
            showPwd: false,
            derrMsg: '',
            dc: 0,
        };

        this.opt = {
            isSuc: 0,
        }

        this.stPwdErrorKey = "电子邮件或密码错误";
        this.cm = this.cm.bind(this);
        this.setShowPwd = this.setShowPwd.bind(this);
        this.dictionaries = [...Object.keys(this.base)];
        this.notCk = true;
        this.kd = this.kd.bind(this);
        this.isHdSuc = this.isHdSuc.bind(this);
        this.doLg = this.doLg.bind(this);
        this.cDp = this.cDp.bind(this);
        this.clearCookie = this.clearCookie.bind(this);
    }
    cDp(){
        this.setState({
            dc: 0,
        })
    }
    doLg(){
        
    }
    isHdSuc(e){
        this.opt.isSuc = e.e;
    }
    hd(cb = () => {}){
        initNECaptcha({
            element: '#captcha',
            captchaId: '607965dc30104508b57fbb1b3bbab114',
            mode: 'popup',
            width: '320px',
            onVerify: (e) => {
                cb(e);
            },
        }, function (instance) {
            // 初始化成功后得到验证实例instance，可以调用实例的方法
            instance.popUp()
        }, function (err) {
            // 初始化失败后触发该函数，err对象描述当前错误信息
            cb(1);
        })
    }
    kd(e){
        if(e.keyCode === 13){
            this.cm();
        }
    }
    ss(){
        if(window.screen.height>document.getElementById("root").clientHeight){
           
        var fhei=document.getElementById("footer").clientHeight
        let heights= document.body.clientHeight-40-fhei-100-140
        document.getElementById("center_d").style.height=heights+"px"
        console.log(fhei)
    }

    }
    componentDidMount(){
        // start need check blur and focus.
        this.setCm(1);
      
       
         
            setTimeout(this.ss,1)
           console.log(11)
           
      
        // document.getElementById('pswInput').addEventListener('paste', function(e) {
        //     e.preventDefault();
        // });
    }
    cm(){
        this.ckAsyncError(this._cm);
    }
    clearCookie(){
         axios.get(DOMAIN_VIP + '/login/forgotLoginCookie').then((res) =>{
            console.log(res)
        })
    }
    _cm(as = true){
        // when click submit will set cm is 1, then start focus and bulr check.
        // this.setCm(1);

        if(!this.hasError(this.dictionaries)){
            if(as){
                const state = this.getState(this.dictionaries);
                const { needImgCode } = this.props.session;
                state.safe = needImgCode? 0:1;

                this.doLg = () => {
                    this.clearsError(this.dictionaries, 1);
                    this.props.doLogin(state, this);
                }
                if (true || (navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i))) {
                    this.doLg();
                } else {
                    this.setState({
                        dc: 1,
                    })
                }
                //this.hd((e) => {
                //    if(!e || e === 1){
                // if(this.opt.isSuc){
                    
                // } else {
                   // this.setState({
                   //     derrMsg: this.props.intl.formatMessage({id: "划转数量格式不正确。"})
                   // })
                // }
                //    }
               // })
                
            } else{
                this.changeImgCode();
            }
        } else {
            this.changeImgCode();
        }
        
    }
    setShowPwd(){
        this.setState({
            showPwd: !this.state.showPwd
        })
    }
    componentWillReceiveProps(props){
        !this.state.needImgCode && props.session.needImgCode && this.setNeedImgCode();
    }

    componentWillUnmount(){
        this.setState({
            needImgCode: 0
        })
        this.props.hideImgCode();
    }

    render(){
       
        const { username, password, code, showPwd, needImgCode:nc, errors, derrMsg, dc, } = this.state;
        const { formatMessage } = this.intl;
        const { cm, setUserName, setPwd, setShowPwd, changeImgCode, setNeedImgCode, setCode, kd, isHdSuc, doLg, cDp, } = this;
        const { imgCode, needImgCode } = this.props.session;

        const disabled = !Object.keys(this.base).every((v) => {
            let rt = true;

            //(v === 'code' && !needImgCode) ? rt = true : (rt = (errors[v] && !errors[v].length));

            return rt;
        });
        
        const { username:eusername, password:epassword, code:ecode } = errors;
        const LFLG = eusername && eusername[0] || epassword && epassword[0];

        return (
            <form className="uauth_wp uauth_wp_d min_h_d clearfix">
            
                
                <div className="center_d" id="center_d">
                    <div className="l l_d">
                        <h2 className="tith">{formatMessage({id: "欢迎登录"})}</h2>
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
                            {
                                /* className={eusername && eusername[0] && 'err'} */
                            }
                            <li className={eusername && eusername[0] && 'err bbyh-err'}>
                                <h3>{formatMessage({id: "电子邮件"})}</h3>
                                <input type="text" className="lj" />
                                <input autoComplete="off" onFocus={() => {this.fbEvent('username', 0)}} onBlur={() => {this.fbEvent('username', 1)}}  type="text" onChange={setUserName} value={username} className="i1" placeholder={formatMessage({id: "请输入电子邮件（水印）"})} tabIndex="1" />
                                <input type="text" className="lj" />
                                {
                                true &&
                                <span className="ew">{eusername && eusername[0]}</span>
                                }
                            </li>
                            {/* ${epassword && epassword[0] && 'err'} */}
                            <li className={`${!needImgCode && 'lst3x'} ${epassword && epassword[0] && 'err bbyh-err'}`}>
                                <h3>{formatMessage({id: "密码x"})}</h3>
                                <div className="plv lg">
                                    <span onClick={setShowPwd} className={`sw iconfont ${showPwd ? 'ac' : ''}`}><svg className="icon icon_d14" aria-hidden="true"><use xlinkHref={showPwd ? '#icon-yincangmima-kai' : '#icon-yincangmima-guan'}></use></svg></span>
                                    <input type="password" className="lj" />
                                    <input maxLength="20" autoComplete="off" onPaste={(e)=> {e.preventDefault()}} onFocus={() => {this.fbEvent('password', 0)}} onBlur={() => {this.fbEvent('password', 1)}} type={showPwd ? 'text' : 'password'} onChange={setPwd} value={password} className="i1" placeholder={formatMessage({id: "请输入登录密码（水印）"})} onKeyDown={kd} tabIndex="2" />
                                    <input type="password" className="lj" />
                                </div>
                                {
                                true &&
                                <span className="ew">{epassword && epassword[0]}</span>
                                }
                                {
                                    false &&
                                    <span className="ew">{(epassword ? epassword[0] : '') || formatMessage({id: "电子邮件或密码错误"})}</span>
                                }
                            </li>
                            {
                                needImgCode ? (
                                    <li className={`lst3x plv ${ecode && ecode[0] && 'err bbyh-err'}`}>
                                        <h3>{formatMessage({id: "图形验证码"})}</h3>
                                        <div className="plv">
                                            <input maxLength="10" autoComplete="off" onFocus={() => {this.fbEvent('code', 0)}} onBlur={() => {this.fbEvent('code', 1)}} onChange={setCode} value={code} type="text" className="i1" placeholder={formatMessage({id: "请输入图形验证码 （水印）"})} tabIndex="3" />
                                            <img src={imgCode} onClick={changeImgCode} className="imgCode" />
                                        </div>
                                        <span className="ew">{ecode && ecode[0]}</span>
                                    </li>
                                ) : null
                            }
                        </ul>
                        {dc ?
                        <div>
                            <div className="sd"></div>
                            <JSaw isSuc={isHdSuc} errMsg={derrMsg} doLg={doLg} cDp={cDp} /> 
                        </div> : null
                        }
                        <div className="subs">
                            <input type="button" onClick={cm} value={formatMessage({id: "登录"})} className={`i3 v ${(disabled) && 'disable'}`} disabled={disabled} />
                            <Link onClick={this.clearCookie}  to={formatURL('fgPwdOne')} className="i4 v">{formatMessage({id: "忘记密码？x"})}</Link>
                        </div>
                    </div>
                    <div className="bbyh-signUpText" style={{top:'0',paddingBottom:'80px'}}>
                        <div className="p0">
                            <div className="bbyh-headers">
                                <p className="mb10">
                                    <svg className="icon icon_d14" aria-hidden="true"><use xlinkHref="#icon-denglu-tishi"></use></svg><span className="tith2" style={{fontSize:'14px'}}>{formatMessage({id: "请确认您正在访问："})} {AUTH_URL}</span>
                                </p>
                                <div className="plv" >
                                    <em className="lock"><svg className="icon icon_d14" aria-hidden="true"><use xlinkHref="#icon-denglu-suo"></use></svg></em>
                                    <input type="text" readOnly={true} value={AUTH_URL} className="i0" />
                                </div>
                            </div>
                            <p>{formatMessage({id: "还不是XXX的用户？"})}</p>
                            <p>{formatMessage({id: "立即注册，在全球领先的数字资产交易平台开始交易。"})}</p>
                        </div>
                        <Link to={formatURL('signup')} className="i4 v">{formatMessage({id: "免费注册"})}</Link>
                    </div>
                </div>
            </form>
        )
    }
}

export default Login;