import React from 'react';
import { connect } from 'react-redux';

import Form from '../../decorator/form';
import GetCode from '../../components/phonecode/getCode';
import list from '../../components/phonecode/country';
import Sms from '../../components/user/sms';
import { doUpMobile } from '../../redux/modules/usercenter'; 
import { mobileFormat } from '../../utils';

const CODETYPE = 32;
@connect(
    state => ({usercenter: null}),
    {
        doUpMobile,   
    },
)
@Form
class DUpMobile extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            mobile: '',
            smscode: '',
            codeType: CODETYPE,
            token: localStorage.getItem('token'),
        };

        this.state = {
            ...this.base,
            selectedCode: '+86',
        }
        this.getCurrentSelectedCode = this.getCurrentSelectedCode.bind(this);
        this.dictionaries = [...Object.keys(this.base)];

        this.cm = this.cm.bind(this);
    }

    cm(){
        if(!this.hasError(this.dictionaries)){
            this.props.doUpMobile(this.state, this.callError, 'smscode')
        }
    }

    getCurrentSelectedCode(code = "", name = ""){
        //console.log(code, name);
        //this.setState({countryCode:code})
        this.setState({countryCode:code, selectedCode:code})
    }
    componentDidMount(){
        // init get code msg queue
        this.initGetCodes(['smscode']);
    }
    render(){
        const { formatMessage } = this.intl;
        const { selectedCode, mobile, smscode, codeType, errors } = this.state;
        const { getCurrentSelectedCode, setMobile, setSmsCode, cm, fIn, bOut, setGetCodes } = this;
        const { mobile:emobile = [], smscode:esmscode = [] } = errors;

        return (
            <div className="mfwp" style={{paddingBottom: '180px'}}>
                <form className="uauth_wp">
                    <ul className="list">
                        <li className={emobile[0] && 'err'}>
                            <h3>{formatMessage({id: '新手机号'})}</h3>
                            <div className="plv k">
                                <div className="hover_d">
                                    <em className="iconfont iconfont_d15">&#xe681;</em>
                                    <GetCode startMove={ true } showCode="1" selectedCode={ selectedCode } list={list.country} getCurrentSelectedCode={ getCurrentSelectedCode }></GetCode>
                                </div>
                                <input autoComplete="off" name="mobile" onChange={setMobile} value={mobile} style={{paddingLeft: '80px'}} type="text" className="i1" placeholder={formatMessage({id: "请输入手机号（水印）"})} onFocus={fIn} onBlur={bOut} />
                            </div>
                            <span className="ew">{emobile[0]}</span>
                        </li>
                        <li className={`lst3x ${esmscode[0] && 'err'}`}>
                            <h3>{formatMessage({id: "短信验证码"})}</h3>
                            <div className="plv">
                                <input autoComplete="off" type="text" className="i1" placeholder={formatMessage({id: "请输入短信验证码（水印）"})} name="smscode" value={smscode} onPaste={setSmsCode} onChange={setSmsCode} onFocus={fIn} />
                                <Sms getCkFn={() => {return !this.hasError(['mobile'])}} {...{codeType}} fn={(k, v)=>{setGetCodes('smscode', 1);this.callError(k, v)}} sendUrl="/userSendCode" errorKey="smscode" otherData={{mobile, selectedCode}} codeType={codeType} />
                            </div>
                            <span className="ew">{esmscode[0]}</span>
                        </li>
                    </ul>

                    <div className="subs">
                        <input onClick={cm} type="button" value={formatMessage({id: "确定x"})} className="i3 v" />
                     </div>
                </form>
            </div>
        );
    }
}

export default DUpMobile;