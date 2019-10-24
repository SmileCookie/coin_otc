import React from 'react';
import {connect} from 'react-redux';

import Form from '../../decorator/form';
import Strength from '../../components/user/strength';
import { doSetPayPwd } from '../../redux/modules/usercenter'; 

@connect(
    state => ({usercenter: null}),
    {
        doSetPayPwd,
    },
)
@Form
class SetPayPwd extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            lpwd: '',
            password: '',
            confirmPwd: '',
        };

        this.state = {
            ...this.base,
        };

        this.dictionaries = [...Object.keys(this.base)];
        
        this.setLevel = this.setLevel.bind(this);
        this.cm = this.cm.bind(this);

        this.stPwdErrorKey = "您的密码需要8-20位，包含字母，数字，符号的两种以上x";
        this.pwdErrorKey = "请输入资金密码";
        this.isNotCkPwd = true;
        this.isCkPwd = true;
    }

    setLevel(level){
        // console.log(level);
    }

    cm(){
        if(!this.hasError(this.dictionaries)){
            this.props.doSetPayPwd(this.state, this.callError, 'confirmPwd');
        }
    }

    render(){
        const { formatMessage } = this.intl;
        const { fIn, bOut, setPwd, setConfirmPwd, setLevel, cm, setlpwd } = this;
        const { password, errors, confirmPwd, mvInPwd, lpwd } = this.state;
        const { password:epassword = [], confirmPwd:econfirmPwd = [], lpwd:elpwd = [] } = errors;

        return (
            <div className="mfwp pd1">
                <form className="uauth_wp plv">
                    <ul className="list bbyh_1">
                        <li className={elpwd[0] && 'err'}>
                            <h3>{formatMessage({id: '登录密码'})}</h3>
                            <input maxLength="20" autoComplete="off" onFocus={fIn} onBlur={bOut} readOnly name="lpwd" value={lpwd} onChange={setlpwd} type="text" dislist="password" className="i1" placeholder={formatMessage({id: "请输入登录密码（水印）"})} />
                            <span className="ew">{elpwd[0]}</span>
                        </li>

                        <li className={epassword[0] && 'err'}>
                            <h3>{formatMessage({id: '资金密码'})}</h3>
                            <div className="plv">
                                <input maxLength="20" autoComplete="off" onFocus={fIn} onBlur={bOut} name="password" readOnly value={password} onChange={setPwd} type="text" dislist="password" className="i1" placeholder={formatMessage({id: "请输入资金密码（水印）"})} />
                            </div>
                            {
                            mvInPwd ?
                            <div className="clearfix">
                                <Strength val={password} funct={setLevel} />
                            </div>
                            : null
                            }
                            <span className="ew">{epassword[0]}</span>
                        </li>

                        <li className={`lst3x ${econfirmPwd[0] && 'err'}`}>
                            <h3>{formatMessage({id: "确认密码"})}</h3>
                            <input maxLength="20" autoComplete="off" onFocus={fIn} onBlur={bOut} name="confirmPwd" type="text" dislist="password" value={confirmPwd} onChange={setConfirmPwd} className="i1" placeholder={formatMessage({id: "请输入确认密码（水印）"})} />
                            <span className="ew">{econfirmPwd[0]}</span>
                        </li>
                    </ul>
                    <div className="subs">
                        <input onClick={cm} type="button" value={formatMessage({id: "确定x"})} className="i3 v" />
                    </div>
                    <div className="abalt sp1 abalt_d">
                        <svg className="icon icon-l5 icon_d14" aria-hidden="true"><use xlinkHref="#icon-tongchang-tishi"></use></svg><span style={{'whiteSpace':'normal','width':'710px'}} className="t">{formatMessage({id: "密码至少8-20位，包含字母、数字、符号的两种以上"})}</span>
                    </div>
                </form>
            </div>
        );
    }
}

export default SetPayPwd;