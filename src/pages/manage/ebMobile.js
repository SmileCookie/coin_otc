import React from 'react';
import { connect } from 'react-redux';

import Form from '../../decorator/form';
import GetCode from '../../components/phonecode/getCode';
import list from '../../components/phonecode/country';
import Sms from '../../components/user/sms';
import { doGetMobileInfo, doEbAuthMobile } from '../../redux/modules/usercenter'; 
import { mobileFormat, formatURL } from '../../utils';
import { getUserBaseInfo } from '../../redux/modules/session'; 
import { NEBMOBILE} from '../../conf';
import { browserHistory } from 'react-router';

const CODETYPE = 34;
@connect(
    state => ({usercenter: state.session.baseUserInfo}),
    {
        doGetMobileInfo,
        getUserBaseInfo,
        doEbAuthMobile,   
    },
)
@Form
class EbMobile extends React.Component{
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

        this.props.getUserBaseInfo().then(r => {
            if(r.mobileStatus === 0){
                browserHistory.push(formatURL(NEBMOBILE));
            }
        });

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
            this.props.doEbAuthMobile(this.state, this.callError, 'smscode')
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
                    <ul className="list">
                        <li>
                            <h3>{formatMessage({id: '手机号'})}</h3>
                            <div className="plv k">
                                <input autoComplete="off" readOnly={true} onChange={setMobile} value={`${selectedCode}${mobileFormat(mobile)}`} type="text" className="i1" placeholder={formatMessage({id: "请输入手机号（水印）"})} />
                            </div>
                        </li>
                        <li className={`lst3x ${esmscode[0] && 'err'}`}>
                            <h3>{formatMessage({id: "短信验证码"})}</h3>
                            <div className="plv">
                                <input autoComplete="off" type="text" className="i1" placeholder={formatMessage({id: "请输入短信验证码（水印）"})} name="smscode" value={smscode} onPaste={setSmsCode} onChange={setSmsCode} onFocus={fIn} />
                                <Sms {...{codeType}} fn={(k, v)=>{setGetCodes('smscode', 1);this.callError(k, v)}} sendUrl="/userSendCode" errorKey="smscode" otherData={{}} codeType={codeType} />
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

export default EbMobile;