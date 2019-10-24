import React from 'react';
import { connect } from 'react-redux';
import axios from 'axios';
import Form from '../../decorator/form';
import Sms from '../../components/user/sms';
import { optPop} from "../../utils";
import { SECOND,DOMAIN_VIP } from '../../conf';
import { fetchFgAuth } from '../../redux/modules/session';
//注入弹框
import EntrustModal from "../../components/entrustBox";

import '../../assets/css/userauth.less';
import './fgPwdOneCss/index.less';

const CODETYPE = 16;

@connect(
    state => ({session: null}),
    {
        fetchFgAuth,
    }
)
@Form
class FgPwdOne extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            email: '',
            emcode: '',
            codeType: CODETYPE,
        };

        this.state = {
            ...this.base,
            ckFlg: 0,
            Mstr:''
        };

        this.dictionaries = [...Object.keys(this.base)];

        this.cm = this.cm.bind(this);
        this.openshow = this.openshow.bind(this);
        this.modalDetail = this.modalDetail.bind(this);
        this.sendEmail = this.sendEmail.bind(this);
    }
    setCk(){
        setTimeout(() => {
            !this.ckFlg &&
            this.setState({
                ckFlg: true
            });
        });
    }
    componentDidMount(){
        console.log(this)
        const { formatMessage } = this.intl;
        this.modalDetail();
        this.callError('emcode', formatMessage({id: "nuser113"}));
        // axios.get(DOMAIN_VIP + '/login/forgotLoginCookie').then((res) =>{
        //     console.log(res)
        // })
    }
    cm(){
        this.setCk();
        if(!this.hasError(this.dictionaries)){
            const d = this.getState(this.dictionaries);
            d.code = d.emcode;
            this.props.fetchFgAuth(d, this, 'emcode');
        }
    }

    modalDetail(){
        const { formatMessage } = this.intl;
        let str = <div className="fg-moadlDetail">
            <div className="head ">
                <a className="right iconfont icon-guanbi-moren" onClick={() => this.modal.closeModal()}></a>
            </div>
            <div className="fg-entrust">
                <p>{formatMessage({id:'你的账户未激活，请点击'})}&nbsp;<a onClick={this.sendEmail}>{formatMessage({id:'nuser127'})}</a>&nbsp;{formatMessage({id:'前去激活'})}</p>
            </div>
        </div>

        this.setState({
            Mstr:str
        })
    }

    sendEmail(e){
        e.preventDefault();
        let that = this;
        const { formatMessage } = this.intl;
        axios.get(DOMAIN_VIP + "/register/reSendActEmail?email=" + this.state.email).then((res)=>{

            console.log(res.data)

            if(res.data.isSuc){
                //发送成功
                optPop(() =>{},formatMessage({id:'我们已发送邮件至%%，请登录您的邮箱查收并点击链接来激活帐号。'}).replace('%%',this.state.email),{timer: 3000},true)
                //这里后台跳转至设置新密码页面
                //that.props.router.push('/bw/forgotPwdThree')
                that.modal.closeModal();

            }else{
                optPop(() =>{},res.data.des,{timer: 3000},true)
                that.modal.closeModal();
            }
        })
    }

    openshow (){
        this.modal.openModal();
    }

    render(){
        const { formatMessage } = this.intl;
        const { codeType, email, emcode, errors, ckFlg } = this.state;
        const { setEmail, setEmCode, cm, fIn, bOut, setGetCodes, hasError, } = this;
        const { email:eemail = [], emcode:eemcode = [] } = errors;
        return (
            <form className="uauth_wp min_h_d clearfix">
                <div className="l">
                    <h2 className="tith">{formatMessage({id: "nuser106"})}</h2>
                    <ul className="list">
                        <li className={`fst ${eemail[0] && 'err'}`}>
                            <h3>{formatMessage({id: "nuser117"})}</h3>
                            <div className="plv">
                                <input type="text" autoComplete="off" className="i1" placeholder={formatMessage({id: "请输入电子邮件（水印）forget"})} onChange={setEmail} value={email} name="email" onFocus={fIn} onBlur={bOut} />
                                <svg className="ep" aria-hidden="true"><use xlinkHref="#icon-youxiangyanzheng"></use></svg>
                            </div>
                            <span className="ew">{eemail[0]}</span>
                        </li>
                        <li className={`${ckFlg && eemcode[0] && 'err'} lst3x`}>
                            <h3>{formatMessage({id: "邮箱验证码"})}</h3>
                            <div className="plv">
                                <input type="text" className="i1" autoComplete="off" placeholder={formatMessage({id: "请输入邮箱验证码（水印）"})} onChange={setEmCode} onPaste={setEmCode} value={emcode} name="emcode" onFocus={(e)=>{this.setCk();this.fIn(e)}}   />
                                <Sms openshow={this.openshow} getCkFn={() => {return !this.hasError(['email'])}} {...{codeType}} fn={(k, v, rt = {})=>{console.log(rt);setGetCodes('emcode', 0);if(rt.isSuc){setGetCodes('emcode', 1);};this.setCk();this.callError(k, '');if(!rt.isSuc){if(Object.keys(rt.datas).length){this.makeResult(rt);}else{this.callError(k, v);}}}} sendUrl="/userSendCode" errorKey="emcode" otherData={{userName: email, type:1}} codeType={CODETYPE} />
                            </div>
                            <span className="ew">{ckFlg ? eemcode[0] : null}</span>
                        </li>
                    </ul>

                    <div className="subs">
                        <input onClick={cm} type="button" value={formatMessage({id: "nuser48"})} className="i3 v" />
                    </div>
                </div>
                {/* <div><span onClick={this.openshow}>test点击</span></div> */}
                <EntrustModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </EntrustModal>
            </form>
        );
    }
}
export default FgPwdOne;
