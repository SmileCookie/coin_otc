import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import axios from 'axios';
import qs from 'qs';

import Form from '../../decorator/form';
import HTab from '../../components/tab/htab';
import { security } from '../../components/tab/tabdata';
import Sms from '../../components/user/sms';
import { SECOND } from '../../conf';
import { fetchFgPwdAuth, fetchFgPwdAuthCm } from '../../redux/modules/session';
import { formatURL, hideStr } from '../../utils';
import { DOMAIN_VIP } from '../../conf';
import { browserHistory } from 'react-router';
import '../../assets/css/userauth.less';

const CODETYPE = 16;

@connect(
    state => ({session: state.session}),
    {
        fetchFgPwdAuth,
        fetchFgPwdAuthCm,
    }
)
@Form
class ForgotPwdTwo extends React.Component{
    constructor(props){
        super(props);

        this.base = [
            {
                gcode: '',
                codeType: CODETYPE,
                token: localStorage.getItem('token'),
            },{
                smscode: '',
                codeType: CODETYPE,
                token: localStorage.getItem('token'),
            }
        ];

        this.state = {
            ...this.base[0],
            ...this.base[1],
            selectedCode: '0',
            opt: -1,
            email: localStorage.getItem("email"),
        };

        this.tabConfig = security(props.intl);
        this.setSelected = this.setSelected.bind(this);
        this.cm = this.cm.bind(this);
        this.clearCkCm = this.clearCkCm.bind(this);
    }

    componentDidMount(){

        const email = localStorage.getItem("email");
        
        // get auth
        axios.post(DOMAIN_VIP+'/login/userStateCheck', qs.stringify({
            email
        })).then(res => {
            res = res.data;
            // res.isSmsOpen = res.isGoogleOpen = true;
            let flg = -1;
        
            if(res.isSmsOpen && res.isGoogleOpen){
                flg = 2;
            } else if(res.isGoogleOpen){
                flg = 0;
            } else if(res.isSmsOpen) {
                flg = 1;
            }


            if(flg != -1){
                this.setState({
                    opt: flg,
                    selectedCode: '' + (flg > 1 ? 0 : flg),  
                });
                if(flg === 0 || flg === 2){
                    this.setGetCode(1);
                }
            } else {
                browserHistory.push(formatURL('login'));
            }
          
        });
    }

    componentDidUpdate(){
        const { kygcode, kysmscode } = this.state;
        if(!this.ckCm && (kysmscode || kygcode)){
            this.cm();
            this.ckCm = 1;
        }
    }

    clearCkCm(){
        setTimeout(() => {
            this.ckCm = 0;
        });
    }

    cm(){
        
        const selectedCode = this.state.selectedCode;
        const dic = this.base[selectedCode];
        if(!this.hasError(Object.keys(dic))){
            this.props.fetchFgPwdAuthCm({...this.getState(Object.keys(dic)), email: this.state.email}, () => {this.clearCkCm(); return this;}, +selectedCode ? 'smscode' : 'gcode');
        }
    }

    setSelected(flg){
        this.setState({
            selectedCode: flg
        });
        
        this.setGetCode(!+flg);
    }

    render(){
        const { tabConfig, setSelected, setSmsCode, setGCode, cm, fIn, bOut, ckKeyDown, setGetCode } = this;
        const { selectedCode, gcode, smscode, codeType, errors, opt, kygcode, kysmscode, email } = this.state;
        const { formatMessage, locale } = this.intl;
        const { smscode:esmscode = [], gcode:egcode = [] } = errors;
        const isCm = (selectedCode === 1 ? kysmscode:kygcode);

        return (
            <form className={`uauth_wp min_h_d clearfix ${locale}_mw uasp0`}>
                <div className="l">
                    {
                    opt !== 2 ?
                    <h2 className="tith">{formatMessage({id: opt === 0 ? '谷歌验证' : '短信验证'})}</h2>
                    :
                    <div className={`mb50 tboutwp ${locale}_htwp`}>
                        <HTab list={tabConfig} currentFlg={selectedCode} setSelected={setSelected}></HTab>
                    </div>
                    }
                    <ul className="list w0" style={{width:'390px'}}>
                        
                            {
                                +selectedCode === 1 ?
                                (<li className={`lst3x ${esmscode[0] && 'err'}`}><div>
                                    {/* <h3>{formatMessage({id: "请输入短信验证码标题"})}</h3> */}
                                    <div className="plv">
                                        <input autoComplete="off" type="text" className="i1" placeholder={formatMessage({id: "请输入短信验证码（水印）"})} name="smscode" value={smscode} onPaste={setSmsCode} onChange={setSmsCode} onFocus={fIn} list={email} form={CODETYPE} alt={DOMAIN_VIP + '/login/checkCode'} />
                                        <em className="iconfont tl tl_d ">&#xe6a7;</em>
                                        {/* <Link to={formatURL('notSmsGCode')} className="pop">{formatMessage({id: "无法提供短信验证码？"})}</Link> */}
                                        {
                                            kysmscode ?
                                            <svg className="icon suc" aria-hidden="true"><use xlinkHref="#icon-zhengque"></use></svg>
                                            :
                                            <Sms {...{codeType}} fn={(k, v)=>{setGetCode(1);this.callError(k, v)}} sendUrl="/userSendCode" errorKey="smscode" otherData={{userName: email}} codeType={CODETYPE} />
                                        }
                                        
                                    </div>
                                </div>
                                <span className="ew">{esmscode[0]}</span>
                                </li>)
                                :
                                (
                                <li className={`lst3x ${egcode[0] && 'err'}`}><div>
                                    {/* <h3>{formatMessage({id: "请输入谷歌验证码标题"})}</h3> */}
                                    <div className="plv">
                                        <input autoComplete="off" type="text" className="i1" placeholder={formatMessage({id: "请输入谷歌验证码（水印）"})} name="gcode" value={gcode} onPaste={setGCode} onChange={setGCode} onFocus={fIn} onBlur={bOut} list={email} form={CODETYPE} alt={DOMAIN_VIP + '/login/checkGoogle'} />
                                        <em className="iconfont tl tl_d">&#xe6a8;</em>
                                        {/* <Link to={formatURL('notSmsGCode')} className="pop pop_d">{formatMessage({id: "无法提供谷歌验证码？"})}</Link> */}
                                        {
                                            kygcode ? 
                                            <svg className="icon suc" aria-hidden="true"><use xlinkHref="#icon-zhengque"></use></svg>
                                            :
                                            null
                                            /*<Sms {...{codeType}} fn={(k, v)=>{setGetCode(1);this.callError(k, v)}} sendUrl="/userSendCode" errorKey="gcode" />*/
                                        }
                                        
                                    </div>
                                </div>
                                <span className="ew">{egcode[0]}</span>
                                </li>
                                )
                            }
                         
                    </ul>
                    <div className={`subs plv ${isCm ? 'ck' : ''}`}>
                        {
                            isCm ?
                            <em className="iconfont ld">&#xe6ca;</em>
                            :
                            null
                        }
                        <input disabled={isCm} onClick={cm} type="button" value={formatMessage({id: "nuser48"})} className="i3 v" />
                     </div>
                </div>
            </form>
        )
    }
}

export default ForgotPwdTwo;