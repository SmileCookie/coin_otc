import React from 'react';

import Form from '../../decorator/form';
import '../../assets/css/userauth.less';
import PhoneCode from '../../components/phonecode';
import Sms from '../../components/user/sms';
import { MOBILE_AUTH_CODETYPE } from '../../conf';
import CheckBox from '../../components/form/checkbox';

@Form
class FinishedInfo extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            mCode: '+86',
            mobile: '',
            codeType: MOBILE_AUTH_CODETYPE,
            code: '',
            security: true
        }

        this.state = {
            ...this.base
        }

        this.changeCountryCode = this.changeCountryCode.bind(this);
        this.cm = this.cm.bind(this);
        this.dictionaries = [...Object.keys(this.base)];
        this.setCk = this.setCk.bind(this);
    }
    setCk(state){
        this.setState({
            security: state
        });
    }
    componentDidMount(){
        this.setNeedImgCode();
    }
    cm(){
        if(!this.hasError(this.dictionaries, 0)){
            console.log('>>>><<<<<<');
        }
    }
    changeCountryCode(mCode = ''){
        this.setState({
            mCode
        });
    }
    render(){
        const { formatMessage } = this.intl;
        const { changeCountryCode, setMobile, setCode, setSecurity, cm, setCk } = this;
        const { mCode, mobile, codeType, errors, code, security } = this.state;
        const { mobile:emobile = [], code: ecode = [] } = errors;
        return (
            <form className="uauth_wp min_h_d clearfix">
                <div className="l">
                    <h2 className="tith">{formatMessage({id: "nuser87"})}</h2>
                    <ul className="list">
                        <li className={emobile[0] && 'err'}>
                            <h3>{formatMessage({id: "nuser91"})}</h3>
                            <div className="plv">
                                <div className="hover_d">
                                    <PhoneCode obrainCountryCode={changeCountryCode} />
                                    <em className="arr iconfont">&#xe681;</em>
                                </div>
                                <input type="text" className="i1 sp i1_d_color" value={mobile} onChange={setMobile} placeholder={`${formatMessage({id: "nuser121"})}${formatMessage({id: "nuser91"})}`} />
                                <span className="ew">{emobile[0]}</span>
                                {/*countryCode*/}
                            </div>
                        </li>

                        <li className={`lst2 ${ecode[0] && 'err'}`}>
                            <h3>{formatMessage({id: "nuser21"})}</h3>
                            <div className="plv">
                                <input type="text" value={code} onChange={setCode} className="i1" placeholder={formatMessage({id: "请输入邮箱验证码（水印）"})} />
                                <Sms {...{mCode, mobile, codeType}} fn={(k, v)=>{this.callError(k, v)}} />
                            </div>
                            <span className="ew">{ecode[0]}</span>
                        </li>

                        <li className="lst3x readme">
                            <CheckBox setCk={setCk} isCk={security} />
                            <input checked={security} onChange={setSecurity} type="checkbox" className="agreement icon_d14" />
                            <span>{formatMessage({id: "nuser90"})}</span>
                        </li>
                    </ul>

                    <div className="subs">
                        <input onClick={cm} type="button" value={formatMessage({id: "确定f"})} className="i3 v" />
                    </div>
                </div>
            </form>
        );
    }
}

export default FinishedInfo;