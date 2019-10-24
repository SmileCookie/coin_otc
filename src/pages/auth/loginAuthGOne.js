import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { browserHistory } from 'react-router';
import axios from 'axios'
import { formatURL, hideStr, optPop, } from '../../utils';
import Form from '../../decorator/form';
import Sms from '../../components/user/sms';
import { SECOND, DOMAIN_VIP } from '../../conf';
import HTab from '../../components/tab/htab';
import GetHTabData from '../../components/tab/tabdata';
import { fetchSecondCm, fetchFstSecondCm } from '../../redux/modules/session';
import '../../assets/css/userauth.less';
import AutoSendCode from "../../components/user/autoSendCode";
import loadingImg from "../../assets/images/Spinner.png";

const PK = 'codegoogle';
const CODETYPE = 63;

@connect(
    state => ({session: state.session}),
    {
        fetchSecondCm,
        fetchFstSecondCm,
    }
)
@Form
class LoginAuthGOne extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            code: '',
            codeType: CODETYPE
        };

        this.state = {
            ...this.base,
            iscm: 0,
            statues:true,
            noAccess:'',
            loading:false

        };

        this.ckCm = 0;

        this.dictionaries = [...Object.keys(this.base)];
        this.cm = this.cm.bind(this);
        this.clearCkCm = this.clearCkCm.bind(this);

        this.tabConfig = GetHTabData(props.intl, props.location.query);
    }

    cm(){

        const [p, ...dic] = this.dictionaries;
        dic.push(PK);

        this.setState({
            iscm: 1,
        })

        if(!this.hasError(dic, 0)){
            this.setState({
                loading:true
            })
            this.props.fetchSecondCm(this.getState(this.dictionaries), () => {this.clearCkCm(); return this;}, PK, this.props.intl.formatMessage);
        } else {
            this.clearMobileError();
        }
    }
    clearCkCm(){
        setTimeout(() => {
            this.ckCm = 0;
        });
        this.setState({
            loading: false
        })
    }
    // 获取子组件 二次验证码
    getAutoCode = (code) =>{
        this.setState({
            code:code
        },() =>{
            this.cm();
        })
    }

    componentDidMount(){
        this.props.fetchFstSecondCm();
        this.setGetCode(1);
        axios.get(DOMAIN_VIP + '/login/checkIsLock').then((res) =>{
            let data = res.data;
            let statues = data.isSuc;
            if(!statues){
                this.setState({
                    statues,
                    noAccess:data.des
                })
            }
        })
    }
    componentWillReceiveProps(props){
        if(props.session.secondCm){
            browserHistory.push(formatURL(''));
        }
    }
    componentDidUpdate(){
        if(!this.ckCm && this.state.kycodegoogle){
            this.cm();
            this.ckCm = 1;
        }
    }
    clearMobileError(){
        this.callError('mobile', '');
    }
    clearAllErrors =() =>{
        this._setError(PK,'',0);
        this._setError(PK,'',1);
        // this.clearMobileError();
    }
    render(){
        const { formatMessage, locale } = this.intl;
        const { cm, bOut, setCode, tabConfig, props, ckKeyDown, setGetCode } = this;
        const { codeType, errors, code, kycodegoogle, iscm ,statues , noAccess,loading} = this.state;
        const { mobile:emobile = [], codegoogle: ecode = [] } = errors;
        const { all } = props.location.query;
        const { email, twoAuth } = props.session.baseUserInfo;
        const cd = false && iscm && !kycodegoogle;
        let display = loading ? "inline-block" : "none"
        return (
            <form className={`uauth_wp min_h_d clearfix ${locale}`}>
                <div className="l">
                    {
                        all ?
                            <div className={`mb50 ${locale}_htwp sp`}>
                                <HTab list={tabConfig} currentFlg="loginAuthGOne"></HTab>
                            </div>
                            :
                            null
                    }
                    {
                        !all ?
                            <h2 className="tith">{formatMessage({id: "谷歌二次认证"})}</h2>
                            :
                            null
                    }
                    <ul className="list">
                        <li className="fst">
                            <span className="cn">{formatMessage({id: "当前帐号："})}</span><span className="em">{hideStr(email)}</span>
                        </li>
                        <li className={`lst3x ${(ecode[0] || (cd)) && 'err'}`}>
                            <h3>{formatMessage({id: "nuser34"})}</h3>
                            <div className="plv">
                                <AutoSendCode len={6} func={this.getAutoCode} PK={PK} clearError={this.clearAllErrors} />
                                {/*<input autoComplete="off"  onFocus={(e)=>{this.clearMobileError();this.fIn(e)}} onBlur={bOut} name={PK} value={code} onChange={setCode} onPaste={setCode}  type="text" className="i1" placeholder={formatMessage({id: "请输入谷歌验证码（水印）"})} onKeyDown={ckKeyDown}  list={email} form={CODETYPE} alt={DOMAIN_VIP + '/login/doLoginAuthen'} cc="1" />*/}
                                {/*<em className="iconfont tl tl_d">&#xe6a8;</em>*/}
                                {!kycodegoogle ?
                                    null/*<Sms {...{codeType}} fn={(k, v)=>{setGetCode(1);this.callError(k, v)}} sendUrl="/userSendCode" errorKey={PK} />*/:null
                                }
                                {/* if(!statues){optPop(()=>{},noAccess,undefined,true)} else { */}
                                <Link onClick={()=>{if(!twoAuth){
                                    all?browserHistory.push(formatURL(`notSmsGCode?all=${all}`)):browserHistory.push(formatURL(`notSmsGCode`))
                                }else{optPop(()=>{},formatMessage({id: "关闭二次认证功能已被锁定，请24小时之后再试"}),undefined,true)}}} className="pop">{formatMessage({id: "无法提供谷歌验证码？"})}</Link>
                                {
                                    kycodegoogle
                                        ?
                                        <svg className="icon suc" aria-hidden="true"><use xlinkHref="#icon-zhengque"></use></svg>: null
                                }
                                <p className="auto-code-loading" style={{display:display}}><img src={loadingImg} alt=""/></p>
                            </div>
                            <span className="ew">{ecode[0]}</span>
                            {
                                !ecode[0] && cd
                                    ?
                                    <span className="ew">{formatMessage({id: "nuser131"}).replace('%%', formatMessage({id: "nuser132"}))}</span>
                                    :
                                    null
                            }
                        </li>
                    </ul>
                    {/*<div className={`subs mb20 plv ${kycodegoogle && 'ck'}`}>*/}
                    {/*{*/}
                    {/*    kycodegoogle*/}
                    {/*    ?*/}
                    {/*    <em className="iconfont ld">&#xe6ca;</em>*/}
                    {/*    :*/}
                    {/*    null*/}
                    {/*}*/}
                    {/*    <input disabled={kycodegoogle} onClick={cm} type="button" value={formatMessage({id: "提交"})} className="i3 v" />*/}
                    {/*</div>*/}
                    <p className="alt" style={{marginTop:'180px'}}>{formatMessage({id: "温馨提示：登录成功后可以在帐户中心中修改验证方式。"})}</p>
                </div>
            </form>
        );
    }
}

export default LoginAuthGOne;
