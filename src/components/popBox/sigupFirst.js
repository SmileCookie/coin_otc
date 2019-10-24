import React from 'react';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import { formatURL } from '../../utils';
import { Link } from 'react-router';
import cookie from 'js-cookie';
import { getUserBaseInfo } from '../../redux/modules/session'; 
import { DOMAIN_COOKIE, COOKIE_EXPIRED_DAYS, COOKIE_FIRST } from '../../conf';
import Opt from '../../components/msg/opt';
import {salfClick} from  '../../redux/modules/loginlogs';
// assets
import sigup_tips from '../../assets/img/sigup_tips.png';
import form from '../../decorator/form';

class SigupFirst extends React.Component{
    constructor(props){
        super(props);
        const K = COOKIE_FIRST + '_' + (props.baseUserInfo.mobile ? props.baseUserInfo.mobile : props.baseUserInfo.userName);
        // console.log(props.isSalfClick)
        this.state = {
            props: {
                ln: props.language.locale === 'zh' ? 'cn' : props.language.locale === 'en' ? 'en' : 'hk'
            },
            
            // firstFlg: !cookie.get(K) && /register|signup|emailConfirm/.test(document.referrer) && !props.isSalfClick
            firstFlg: !props.isSalfClick

        }
        
        this.close = this.close.bind(this)
        this.hasSalfClick = this.hasSalfClick.bind(this)

        cookie.set(K, 1, {
            expires: COOKIE_EXPIRED_DAYS,
            domain: DOMAIN_COOKIE,
            path: '/'
        })

    }

     close(){
        this.setState({firstFlg:false})
        this.hasSalfClick()
    
    }

    //点击过安全验证
    hasSalfClick(){
        const {salfClick,getUserBaseInfo} = this.props;
        getUserBaseInfo();
        salfClick()
    }

    render(){
        const {props:sprops} = this.state;
        const { formatMessage } = this.props.intl;
        const { close,hasSalfClick} = this;
       
        return (
            this.state.firstFlg
            &&
            <div className={`sigup_tips_one ${sprops.ln === 'en' ? 'kc' : ''}`}>
                <div className="tips_bg"></div>
                <Opt  hasSalfClick={hasSalfClick} closeCb={close} msg={`${formatMessage({id: '为了您的账号安全，我们强烈建议您开启安全验证。'})}`} msg2={formatMessage({id:"请选择您的安全验证方式。"})} ft={formatMessage({id: '暂不设置'})} />
            </div>
        )
    }
}
const mapStateToProps = (state) => {
    return {
        language : state.language,
        baseUserInfo : state.session.baseUserInfo,
        isSalfClick: state.loginLogs.salfClick
    }
};
const mapDispatchToProps = {
        salfClick,
        getUserBaseInfo
};

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(SigupFirst));