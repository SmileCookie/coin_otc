import React from 'react';

import Form from '../../decorator/form';
import '../../assets/css/userauth.less';
import Strength from '../../components/user/strength';
import { connect } from 'react-redux';
import { fetchFgPwdUp } from '../../redux/modules/session';

@connect(
    state => ({session: null}),
    {
        fetchFgPwdUp,
    }
)
@Form
class ForgotPwdThree extends React.Component{
    constructor(props){
        super(props);

        this.pwdErrorKey = '请输入新登录密码';
        this.stPwdErrorKey="set您的密码需要8-20位，包含字母，数字，符号的两种以上"

        this.base = {
            password: '',
            confirmPwd: '',
            token: localStorage.getItem('token'),
        }

        this.state = {
            ...this.base,
        }

        this.cm = this.cm.bind(this);
        this.dictionaries = [...Object.keys(this.base)];
    }

    cm(){
        if(!this.hasError(this.dictionaries, 0)){
            this.props.fetchFgPwdUp({...this.getState(this.dictionaries), email: localStorage.getItem("email")}, () => {return this;}, 'password', this.intl.formatMessage);
        }
    }

    render(){
        const { formatMessage } = this.intl;
        const { password, confirmPwd, mvInPwd, errors } = this.state;
        const { setPwd, setConfirmPwd, fIn, bOut, cm } = this;
        const { password:epassword = [], confirmPwd:econfirmPwd = [] } = errors;

        return (
        <form className="uauth_wp min_h_d clearfix">
            <div className="l">
                <h2 className="tith">{formatMessage({id: "设置新密码"})}</h2>
                <ul className="list">
                    <li className={epassword[0] && 'err'}>
                        {/* <h3>{formatMessage({id: "nuser82"})}</h3> */}
                        <input maxLength="20" type="password" name="password" autoComplete="off" onFocus={fIn} onBlur={bOut} value={password} onChange={setPwd} className="i1" placeholder={formatMessage({id: "请输入新登录密码（水印）"})} />
                        {
                            mvInPwd
                            ?
                            <div className="clearfix">
                                <Strength val={password} />
                            </div>
                            : null
                        }
                        <span className="ew">{epassword[0]}</span>
                    </li>
                    <li className={`${econfirmPwd[0] && 'err'} lst3x`}>
                        {/* <h3>{formatMessage({id: "nuser85"})}</h3> */}
                        <input maxLength="20" type="password" name="confirmPwd" autoComplete="off" onFocus={fIn} onBlur={bOut} value={confirmPwd} onChange={setConfirmPwd} className="i1" placeholder={formatMessage({id: "请输入确认密码（水印）"})} />
                        <span className="ew">{econfirmPwd[0]}</span>
                    </li>
                </ul>
                <div className="subs">
                    <input type="button" onClick={cm} value={formatMessage({id: "确定f"})} className="i3 v" />
                </div>
            </div>
        </form>);
    }
}

export default ForgotPwdThree;