import React from 'react';
import { connect } from 'react-redux';

import Form from '../../decorator/form';
import GetCode from '../../components/phonecode/getCode';
import list from '../../components/phonecode/country';
import Sms from '../../components/user/sms';
import { doGetMobileInfo, doAuthMobile } from '../../redux/modules/usercenter'; 
import { mobileFormat } from '../../utils';

const CODETYPE = 33;
@connect(
    state => ({usercenter: null}),
    {
        doGetMobileInfo,
        doAuthMobile,   
    },
)
@Form
class UpMobile extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            mobile: '',
            smscode: '',
            codeType: CODETYPE,
        };

        this.state = {
            ...this.base,
            selectedCode: '+86',
        }
        this.getCurrentSelectedCode = this.getCurrentSelectedCode.bind(this);
        this.dictionaries = [...Object.keys(this.base)];

        this.cm = this.cm.bind(this);
    }

    componentDidMount(){
        // init get code msg queue
        this.initGetCodes(['smscode']);
        
        const rs = this.props.doGetMobileInfo();
        rs.then((r) => {
            this.setState({
                mobile: r.mobile,
                selectedCode: r.code,
            });
        });
    }

    cm(){
        if(!this.hasError(this.dictionaries)){
            this.props.doAuthMobile(this.state, this.callError, 'smscode')
        }
    }

    getCurrentSelectedCode(code = "", name = ""){
        //console.log(code, name);
        //this.setState({countryCode:code})
        this.setState({countryCode:code, selectedCode:code})
    }
    render(){
        const { formatMessage } = this.intl;
        const { selectedCode, mobile, smscode, codeType, errors } = this.state;
        const { getCurrentSelectedCode, setMobile, setSmsCode, cm, fIn, bOut, setGetCodes } = this;
        const { mobile:emobile = [], smscode:esmscode = [] } = errors;

        return (
            <div className="mfwp" style={{paddingBottom: '180px'}}>
                <form className="uauth_wp">
                    <ul className="list bbyh_1">
                        <li>
                            <h3>{formatMessage({id: '原手机号'})}</h3>
                            <div className="plv k">
                                <input readOnly={true} onChange={setMobile} value={`${selectedCode}${mobileFormat(mobile).replace(' ', '')}`} type="text" className="i1" placeholder={formatMessage({id: "请输入手机号（水印）"})} />
                            </div>
                        </li>
                        <li className={`lst3x ${esmscode[0] && 'err'}`}>
                            <h3>{formatMessage({id: "原手机短信验证码"})}</h3>
                            <div className="plv">
                                <input autoComplete="off" type="text" className="i1" placeholder={formatMessage({id: "请输入短信验证码（水印）"})} name="smscode" value={smscode} onPaste={setSmsCode} onChange={setSmsCode} onFocus={fIn} />
                                <Sms {...{codeType}} fn={(k, v)=>{setGetCodes('smscode', 1);this.callError(k, v)}} sendUrl="/userSendCode" errorKey="smscode" otherData={{}} codeType={codeType} />
                            </div>
                            <span className="ew">{esmscode[0]}</span>
                        </li>
                    </ul>

                    <div className="subs">
                        <input onClick={cm} type="button" value={formatMessage({id: "下一步"})} className="i3 v" />
                     </div>
                </form>
            </div>
        );
    }
}

export default UpMobile;