import React from 'react';
import {connect} from 'react-redux';

import Form from '../../decorator/form';
import Strength from '../../components/user/strength';
import { security } from '../../components/tab/tabdata';
import Sms from '../../components/user/sms';
import HTab from '../../components/tab/htab';
import { doGetAuth, doAuthPayCm } from '../../redux/modules/usercenter'; 

const CODETYPE = 21;
const SMSCODETYPE = 39;

@connect(
    state => ({usercenter: null}),
    {
        doGetAuth,
        doAuthPayCm,
    },
)
@Form
class ResetPayPwd extends React.Component{
    constructor(props){
        super(props);

        this._base = {
            lpwd: '',
            emailcode: '',
            codeType: CODETYPE,
        };

        this._gbase = {
            gcode: '',
        };

        this._smsbase = {
            smscode: '',
        };

        this.base = {
            ...this._base,
            ...this._gbase,
            ...this._smsbase,
        };

        this.state = {
            ...this.base,
            selectedCode: '0',
            opt: -1,
        };

        this.dictionaries = [...Object.keys(this._base)];

        this.cm = this.cm.bind(this);
        this.tabConfig = security(props.intl);
        this.setSelected = this.setSelected.bind(this);
        this.isNotCkPwd = true;
    }
    cm(){
        const { selectedCode } = this.state;

        const dic = this.formatAuthDic(selectedCode, this.dictionaries);

        if(!this.hasError(dic)){
            this.props.doAuthPayCm(this.state, this.callError, 'emailcode');
        }
        
    }
    setSelected(flg){
        this.setState({
            selectedCode: flg
        });
    }
    componentDidMount(){
        // init get code msg queue
        this.initGetCodes(['smscode', 'emailcode']);
        this.props.doGetAuth().then(r => {
            this.setState({
                opt: r,
                selectedCode: '' + (r === 2 || r === 0 ? 0 : (r === 1 ? 1 : -1)),
            });
        });
    }
    render(){
        const { formatMessage } = this.intl;
        const { fIn, bOut, cm, setlpwd, setEmailcode, setGetCodes, tabConfig, setSelected, setGCode,setSmsCode } = this;
        const { errors, lpwd, emailcode, codeType, opt, selectedCode, gcode, smscode } = this.state;
        const { lpwd:elpwd = [], emailcode:eemailcode = [], gcode:egcode = [], smscode:esmscode = [] } = errors;

        return (
            <div className="mfwp">
                <form className="uauth_wp plv">
                    <ul className="list bbyh_1">
                        <li className={elpwd[0] && 'err'}>
                            <h3>{formatMessage({id: '登录密码'})}</h3>
                            <input type="password" className="lj" />
                            <input maxLength="20" autoComplete="off" onFocus={fIn} onBlur={bOut} name="lpwd" value={lpwd} onChange={setlpwd} type="password" className="i1" placeholder={formatMessage({id: "请输入登录密码（水印）"})} maxLength="20" />
                            <span className="ew">{elpwd[0]}</span>
                        </li>

                        <li className={`lst3 ${eemailcode[0] && 'err'}`}>
                            <h3>{formatMessage({id: "邮箱验证码"})}</h3>
                            <div className="plv">
                                <input type="password" className="lj" />
                                <input autoComplete="off" type="text" className="i1" placeholder={formatMessage({id: "请输入邮箱验证码（水印）"})} onPaste={setEmailcode} onChange={setEmailcode} value={emailcode} stopname="emailcode" onFocus={fIn} />
                                <Sms {...{codeType}} fn={(k, v)=>{setGetCodes('emailcode', 1);this.callError(k, v)}} sendUrl="/userSendCode" errorKey="emailcode" otherData={{type: 2}} codeType={CODETYPE} />
                            </div>
                            <span className="ew">{eemailcode[0]}</span>
                        </li>
                    </ul>

                    {opt !== -1 ?
                        <div>
                            <h2 className="ptithx mb20">{formatMessage({id: "安全验证"})}</h2>

                            {            
                                opt !== 2 ?
                                <h3 className="ptithx2" style={{textAlign:'left'}}>{formatMessage({id: opt === 0 ? '谷歌验证' : '短信验证'})}</h3>
                                :
                                <div className="htb_sy0 mb10">
                                    <HTab list={tabConfig} currentFlg={selectedCode} setSelected={setSelected}></HTab>
                                </div> 
                            }
                            
                            <ul className="list bbyh_1">
                            {
                                +selectedCode === 0 ?
                            
                                <li className={`lst3x ${egcode[0] && 'err'}`}>
                                    <input autoComplete="off" type="text" className="i1" placeholder={formatMessage({id: "请输入谷歌验证码（水印）"})} name="gcode" value={gcode} onPaste={setGCode} onChange={setGCode} onFocus={fIn} onBlur={bOut} />
                                    <span className="ew">{egcode[0]}</span>
                                </li>
                                :
                                <li className={`lst3x ${esmscode[0] && 'err'}`}>
                                    <div className="plv">
                                        <input autoComplete="off" type="text" className="i1" placeholder={formatMessage({id: "请输入短信验证码（水印）"})} name="smscode" value={smscode} onPaste={setSmsCode} onChange={setSmsCode} onFocus={fIn} />
                                        <Sms {...{codeType:SMSCODETYPE}} fn={(k, v)=>{setGetCodes('smscode', 1);this.callError(k, v)}} sendUrl="/userSendCode" errorKey="smscode" otherData={{}} codeType={SMSCODETYPE} />
                                    </div>
                                    <span className="ew">{esmscode[0]}</span>
                                </li>
                            }
                            </ul>
                        </div>
                        :null
                    }
                    <div className={`subs plv mb20`}>
                        <input onClick={cm} type="button" value={formatMessage({id: "nuser48"})} className="i3 v" />
                    </div>
                    <p className="alttxt" style={{width:'440px'}}>
                        {formatMessage({id: "温馨提示：为了您的帐户安全，使用重置资金密码功能修改资金密码后将锁定24小时，在此期间不能进行提现、修改密码等操作，可进行交易操作，请等待24个小时后自动解锁。"})}
                    </p>
                </form>
            </div>
        );
    }
}
export default ResetPayPwd;
