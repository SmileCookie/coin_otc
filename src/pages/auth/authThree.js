import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';

import cookie from 'js-cookie';
import Form from '../../decorator/form';
import { formatURL } from '../../utils';
import { fetchPay } from '../../redux/modules/session';
import '../../assets/css/userauth.less';

@connect(
    state => ({state: null}),
    {
        fetchPay,
    }
)
@Form
class AuthThree extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            payPwd: '',
            token: localStorage.getItem('token'),
        }

        this.state = {
            ...this.base,
            email: cookie.get('zuname'),
        }

        this.dictionaries = [...Object.keys(this.base)];
        this.cm = this.cm.bind(this);
        
    }
    cm(){
        if(!this.hasError(this.dictionaries, 0)){
            const d = this.getState(this.dictionaries);
            const { email, payPwd } = this.state;
            d.userName = email;
            d.safePwd = payPwd;

            this.props.fetchPay(d, this, 'payPwd', this.props.intl.formatMessage);
        }
    }
    render(){
        const { formatMessage } = this.intl;
        const { setPayPwd, cm, fIn, bOut } = this;
        const { payPwd, errors } = this.state;
        const { payPwd:epayPwd = [] } = errors;

        return (
            <form className="uauth_wp min_h_d clearfix">
                <div className="l">
                    <div className="plv">
                        <Link to={formatURL('notGCode')} className="iconfont bk">&#xe6a3;</Link>
                        <h2 className="tith">{formatMessage({id: "bbyh身份验证"})}</h2>
                    </div>
                    <ul className="list">
                        <li className={`lst3x ${epayPwd[0] && 'err'}`}>
                            <h3>{formatMessage({id: "资金密码"})}</h3>
                            <input maxLength="20" type="text" dislist="password" readOnly onChange={setPayPwd} value={payPwd} name="payPwd" onFocus={fIn} onBlur={bOut} className="i1" placeholder={formatMessage({id: "请输入资金密码（水印）"})} />
                            <span className="ew">{epayPwd[0]}</span>
                        </li>
                    </ul>
                    <div className="subs">
                        <input onClick={cm} type="button" value={formatMessage({id: "确定"})} className="i3 v" />
                    </div>
                </div>
            </form>
        );
    }
}

export default AuthThree;

