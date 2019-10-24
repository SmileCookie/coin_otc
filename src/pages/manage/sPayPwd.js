import React from 'react';
import { connect } from 'react-redux';

import Form from '../../decorator/form';
import Strength from '../../components/user/strength';
import { doRsPayPwd } from '../../redux/modules/usercenter'; 

@connect(
    state => ({usercenter: null}),
    {
        doRsPayPwd,
    },
)
@Form
class SPayPwd extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            password: '',
            confirmPwd: '',
            token: localStorage.getItem('token'),
        };

        this.state = {
            ...this.base,
        };

        this.dictionaries = [...Object.keys(this.base)];
        
        this.setLevel = this.setLevel.bind(this);
        this.cm = this.cm.bind(this);
        this.stPwdErrorKey = "bbyh您的密码需要8-20位，包含字母，数字，符号的两种以上";
    }

    cm(){
        if(!this.hasError(this.dictionaries)){
            this.props.doRsPayPwd(this.state, this.callError, 'confirmPwd');
        }
    }

    setLevel(){

    }

    render(){
        const { formatMessage } = this.intl;
        const { fIn, bOut, setPwd, setConfirmPwd, setLevel, cm } = this;
        const { password, errors, confirmPwd, mvInPwd } = this.state;
        const { password:epassword = [], confirmPwd:econfirmPwd = [] } = errors;

        return (
            <div className="mfwp pd2">
                <form className="uauth_wp plv">
                    <ul className="list bbyh_1">
                        <li className={epassword[0] && 'err'}>
                            <h3>{formatMessage({id: '新资金密码'})}</h3>
                            <div className="plv">
                                <input autoComplete="off" onFocus={fIn} onBlur={bOut} name="password" value={password} onChange={setPwd} type="text" readOnly dislist="password" className="i1" placeholder={formatMessage({id: "请输入新资金密码（水印）"})} maxLength="20" />
                            </div>
                            {
                            mvInPwd ?
                            <div className="clearfix">
                                <Strength val={password} funct={setLevel} />
                            </div>
                            : null
                            }
                            <span className="ew">{epassword[0] ? (password ? epassword[0] : formatMessage({id: "请输入新资金密码"})) : null}</span>
                        </li>

                        <li className={`lst3x ${econfirmPwd[0] && 'err'}`}>
                            <h3>{formatMessage({id: "确认新密码"})}</h3>
                            <input autoComplete="off" onFocus={fIn} onBlur={bOut} name="confirmPwd" type="text" dislist="password" readOnly value={confirmPwd} onChange={setConfirmPwd} className="i1" placeholder={formatMessage({id: "请输入确认密码（水印）"})} maxLength="20" />
                            <span className="ew">{econfirmPwd[0]}</span>
                        </li>

                    </ul>
                    <div className="subs">
                        <input onClick={cm} type="button" value={formatMessage({id: "确定x"})} className="i3 v" />
                    </div>
                    <div className="abalt sp2 abalt_d">
                        <svg className="icon icon-l5 icon_d14" aria-hidden="true"><use xlinkHref="#icon-tongchang-tishi"></use></svg><span style={{'whiteSpace':'normal','width':'710px'}} className="t">{formatMessage({id: "密码至少8-20位，包含字母、数字、符号的两种以上"})}</span>
                    </div>
                </form>
            </div>
        )
    }
}
export default SPayPwd;