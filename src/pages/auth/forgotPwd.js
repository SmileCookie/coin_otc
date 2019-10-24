import React from 'react';

import Form from '../../decorator/form';
import HTab from '../../components/tab/htab';
import { security } from '../../components/tab/tabdata';
import Sms from '../../components/user/sms';
import { SECOND } from '../../conf';
import '../../assets/css/userauth.less';

@Form
class ForgotPwd extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            smscode: '',
            gcode: '',
            emcode: '',
            codeType: SECOND,
        };

        this.state = {
            ...this.base,
            selectedCode: '1'
        };

        this.tabConfig = security(props.intl);
        this.setSelected = this.setSelected.bind(this);
        this.dictionaries = [...Object.keys(this.base)];
    }
    setSelected(flg){
        this.setState({
            selectedCode: flg
        });
    }
    render(){
        const { formatMessage } = this.intl;
        const { tabConfig, setSelected, setEmCode, setGCode, setSmsCode } = this;
        const { selectedCode, smscode, gcode, emcode, errors, codeType } = this.state;
        const { smscode:esmscode = [], gcode:egcode = [], emcode:eemcode = [] } = errors;
        
        return (
            <form className="uauth_wp min_h_d clearfix">
                <div className="l">
                    <h2 className="tith">{formatMessage({id: "nuser106"})}</h2>
                    <ul className="list">
                        <li className="fst">
                            <h3>{formatMessage({id: "nuser66"})}</h3>
                            <div className="plv">
                                <input type="text" className="i1" placeholder={formatMessage({id: "请输入邮箱验证码（水印）"})} value={emcode} onChange={setEmCode} onPaste={setEmCode} />
                                <svg className="ep" aria-hidden="true"><use xlinkHref="#icon-youxiangyanzheng"></use></svg>
                                <Sms {...{codeType}} fn={(k, v)=>{this.callError(k, v)}} sendUrl="test1" errorKey="emcode" />
                            </div>
                        </li>
                        <li>
                            <h3 className="b1">{formatMessage({id: "nuser107"})}</h3>
                            <div className="security spw">
                                <HTab list={tabConfig} currentFlg={selectedCode} setSelected={setSelected}></HTab>
                            </div>
                        </li>
                        <li className="lst3x">
                            {
                            +selectedCode === 1 ?
                            (<div><h3>{formatMessage({id: "请输入短信验证码"})}</h3>
                            <input type="text" className="i1" placeholder={formatMessage({id: "请输入短信验证码（水印）"})} value={smscode} onPaste={setSmsCode} onChange={setSmsCode} /></div>)
                            :
                            (
                                <div><h3>{formatMessage({id: "请输入谷歌验证码"})}</h3>
                                <input type="text" className="i1" placeholder={formatMessage({id: "请输入谷歌验证码（水印）"})} value={gcode} onPaste={setGCode} onChange={setGCode} /></div>
                            )
                            }
                        </li>
                    </ul>

                     <div className="subs">
                        <input type="button" value={formatMessage({id: "nuser48"})} className="i3 v" />
                     </div>
                </div>
            </form>
        );
    }
}

export default ForgotPwd;