import React from 'react';
import { connect } from 'react-redux';
import cookie from 'js-cookie';
import { injectIntl } from 'react-intl';
import '../../assets/css/userauth.less';
import { Link } from 'react-router';
import { notGSms } from '../../redux/modules/session';
import { formatURL } from '../../utils'
import { browserHistory } from 'react-router';

@connect(
    state => ({}),
    {
        notGSms,
    }
)
class NotGCode extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            current: '',
            ay: [],
            notSelect: false,
            email: cookie.get('zuname'),
        }
        this.baseUrl = [
            'authOne',
            'authTwo',
            'authThree',
        ];

        this.cm = this.cm.bind(this);
    }
    componentDidMount(){
        const listDom = [...this.refs.wp.getElementsByTagName('li')],
               _this = this;

        let prevObj = null;

        listDom.forEach((obj, i) => {   
            obj.onclick = () => {
                this.setState({
                    notSelect:false
                })
                if(prevObj !== obj){
                    const isCanClick = +_this.state.ay[i];
                    const currentClassName = obj.getAttribute('class');
                    
                    if(isCanClick){
                        try{
                            prevObj.setAttribute('class', prevObj.getAttribute('class').replace(/\s*ac\s*/, ''));
                        }catch(e){

                        }
                        obj.setAttribute('class', 'ac ' + (currentClassName ? currentClassName : ''));

                        prevObj = obj;

                        _this.setState({
                            current: _this.baseUrl[i]
                        });
                    }
                }
            }
        });

        this.props.notGSms({email: this.state.email}).then(res => {
            this.setState({
                ay: res
            });
        });
    }
    cm(){
        const current = this.state.current;
        if(!current){
            this.setState({
                notSelect: true
            });
        }else{
            browserHistory.push(formatURL(current));
        }
    }
    render(){
        const { formatMessage } = this.props.intl;
        const { ay, notSelect } = this.state;
        const { cm } = this;
        let _lan = cookie.get('zlan')
        return (
            <div className="uauth_wp min_h_d m2">
                <h2 className="tith">{formatMessage({id: "请选择一种验证方式"})}</h2>
                <ul className="rcodewp clearfix pointer_d" ref="wp">
                    <li className={!ay[0] && 'nhover'}>
                        <h3 className="normal_d">{formatMessage({id: "验证充值地址"})}</h3>
                        <p className={!ay[0]&&'op'}>{formatMessage({id: "如果您进行过充值操作，并且能够获取充值地址，请选择此项进行验证。"})}</p>
                        <svg className="icon ab" aria-hidden="true"><use xlinkHref="#icon-yanzhengfangshixuanze"></use></svg>
                    </li>
                    <li className={!ay[1] && 'nhover'}>
                        <h3 className="normal_d">{formatMessage({id: "手持证件照片"})}</h3>
                        <p className={!ay[1]&&'op'}>{formatMessage({id: "如果您完成了身份认证，请选择此项，上传手持已认证的证件照片。"})}</p>
                        <svg className="icon ab" aria-hidden="true"><use xlinkHref="#icon-yanzhengfangshixuanze"></use></svg>
                    </li>
                    <li className={`lst3x ${!ay[2] && 'nhover'}`}>
                        <h3 className="normal_d">{formatMessage({id: "验证资金密码"})}</h3>
                        <p className={!ay[2]&&'op'}>{formatMessage({id: "如果您的帐户设置了资金密码，请选择此项进行验证。"})}</p>
                        <svg className="icon ab" aria-hidden="true"><use xlinkHref="#icon-yanzhengfangshixuanze"></use></svg>
                    </li>
                </ul>
                <div className="plv">
                {
                    notSelect ? 
                    <p className="rerr">{formatMessage({id: "请选择一种验证方式"})}</p>
                    :
                    null
                }
                </div>
                <div className="subs">
                    <input onClick={cm} type="button" value={formatMessage({id: "下一步"})} className="i3 v mb20" />
                    
                    <p className="s s_d">{formatMessage({id: "如果您无法提供以上验证信息，请联系我们的"})}<Link>{formatMessage({id: "人工客服"})}</Link><span style={{display:_lan == 'jp' || _lan == 'kr'?'none':'inline-block'}}>{formatMessage({id: "。"})}</span></p>
                </div>
            </div>
        );
    }
}

export default injectIntl(NotGCode);