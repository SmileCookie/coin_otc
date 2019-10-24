import React from 'react';
import { connect } from 'react-redux';
import cookie from 'js-cookie';
import Form from '../../decorator/form';
import Sms from '../../components/user/sms';
import { SECOND } from '../../conf';
import { emCodeCheck } from '../../redux/modules/session';
import '../../assets/css/userauth.less';

const CODETYPE = 14;

@connect(
    state => ({}),
    {
        emCodeCheck,
    }
)
@Form
class NotSmsGCode extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            emcode: '',
            codeType: CODETYPE,
        }

        this.state = {
            ...this.base,
            ckFlg: 0,
            email: cookie.get('zuname'),
        };

        this.dictionaries = [...Object.keys(this.base)];
        this.dictionaries.pop();
        this.cm = this.cm.bind(this);
    }
    setCk(){
        setTimeout(() => {
            !this.ckFlg &&
            this.setState({
                ckFlg: true
            });
        })
    }
    cm(){
        //this.cl();
        this.setCk();
        if(!this.hasError(this.dictionaries)){
            const d = this.getState(this.dictionaries);
            d.code = d.emcode;
            d.email = this.state.email;
            d.codeType = CODETYPE;
            this.props.emCodeCheck(d, this, 'emcode');
        }
    }
    componentDidMount(){
        const { formatMessage } = this.intl;
        const init = [formatMessage({id: "请获取邮箱验证码"})]
        this.dictionaries.forEach((v, i) => {
            this.callError(v, init[i]);
        });
    }
    render(){
        const { formatMessage } = this.intl;
        const { setEmCode, clearsError, cm, setCk, bOut, setGetCodes, } = this;
        const { emcode, errors, codeType, ckFlg, email } = this.state;
        const { emcode:eemcode = [] } = errors;

        return (
            <form className="uauth_wp min_h_d clearfix">
                <div className="l">
                    <h2 className="tith">
                        {
                            this.props.location.query.all ? formatMessage({id: "重置二次验证"}) : this.props.location.query.p ? formatMessage({id: "重置短信验证"}) : formatMessage({id: "重置谷歌验证"})
                        }
                    </h2>
                    <ul className="list">
                        <li className={`${ckFlg && eemcode[0] && 'err'} lst3x`}>
                            <h3>{formatMessage({id: "邮箱验证码"})}</h3>
                            <div className="plv isp2 isp3">
                                <input autoComplete="off" onFocus={(e)=>{this.setCk();this.fIn(e)}} name="emcode"  type="text" className="i1" placeholder={formatMessage({id: "请输入邮箱验证码（水印）"})} value={emcode} onPaste={setEmCode} onChange={setEmCode} />
                                <svg className="ep" aria-hidden="true"><use xlinkHref="#icon-youxiangyanzheng"></use></svg>
                                <Sms clearFn={() => {setGetCodes('emcode', 1);}} {...{codeType}} fn={(k, v)=>{setGetCodes('emcode', 1);this.setCk();this.callError(k, v)}} sendUrl="/userSendCode" errorKey="emcode" otherData={{userName: email, type:1}} codeType={CODETYPE} />
                            </div>
                            <span className="ew">{ckFlg ? eemcode[0] : null}</span>
                        </li>
                    </ul>
                    <div className="subs mb20">
                        <input onClick={cm} type="button" value={formatMessage({id: "下一步"})} className="i3 v" />
                    </div>
                    {this.props.location.query.all?<p className="alt">{formatMessage({id: "重置操作会导致您的帐户失去谷歌和短信验证的保护，请确保您的邮箱及登录密码安全，并在重置成功后立刻重新绑定谷歌或短信验证，以免造成损失。"})}</p> : this.props.location.query.p?<p className="alt">{formatMessage({id: "重置操作会导致您的账户失去短信验证的保护，请确保您的邮箱及登录密码安全，并在重置成功后立刻重新绑定短信验证，以免造成损失。"})}</p>:<p className="alt">{formatMessage({id: "重置操作会导致您的账户失去谷歌验证的保护，请确保您的邮箱及登录密码安全，并在重置成功后立刻重新绑定谷歌验证，以免造成损失。"})}</p>}
                </div>
            </form>
        );
    }
}

export default NotSmsGCode;