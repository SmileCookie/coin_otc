import React from 'react';
import { injectIntl } from 'react-intl';

import axios from 'axios';
import qs from 'qs';
import { DOMAIN_VIP } from '../../conf';
import { optPop } from '../../utils';
import '../../assets/css/userauth.less';

class Active extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            isck: true,
            defaults: props.intl.formatMessage({id: "重新发送激活邮件"}),
            ...props.location.query
        }

        

        this.cm = this.cm.bind(this);
    }
    componentDidMount(){
        // auth
        const userId = localStorage.getItem("id");

        axios.post('/login/checkRegister', qs.stringify({userId})).then(res => {
            res = res.data;
            if(res.des){
                window.location.href = res.des;
            }
        });
 
        this.refs.tith.innerHTML = this.props.intl.formatMessage({id: "我们已发送邮件至%%，请登录您的邮箱查收并点击链接来激活帐号。"}).replace('%%', '<a href="/bw/login" class="st">' + (this.state.email ? this.state.email : localStorage.getItem("email")) + '</a>');
    }

    cm(){
        if(this.state.isck){
            
            axios.post(DOMAIN_VIP + '/register/reSendEmail', qs.stringify({nid: this.state.nid?this.state.nid:localStorage.getItem("id")})).then((res)=>{
                res = res.data;
                !res.isSuc && optPop(() => {}, res.des);
                if(res.isSuc){
                    let time = 60;
                    const t = setInterval(() => {
                        this.setState({
                            isck: false,
                            defaults: this.props.intl.formatMessage({id:'user.text129'}).replace('[$1]', --time),
                        });
                        if(time === 0){
                            clearInterval(t);
                            this.setState({
                                isck: true,
                                defaults: this.props.intl.formatMessage({id: "重新发送激活邮件"})
                            });
                        }
                    },1000)
                }
            });
        }
    }

    render(){
        const { formatMessage } = this.props.intl;
        const { cm } = this;
        const { defaults } = this.state;

        return (
            <div className="uauth_wp min_h_d clearfix">
                <div className="l lsp2">
                    <h2 className="tith">{formatMessage({id: "帐号激活"})}</h2>
                    <ul className="list">
                        <li className="lst3x">
                            <h3 ref="tith"></h3>
                        </li>
                    </ul>
                    <div className="subs">
                        <input onClick={cm} type="button" value={defaults} className="i3 v shw" />
                    </div>
                </div>
            </div>
        );
    }
}

export default injectIntl(Active);