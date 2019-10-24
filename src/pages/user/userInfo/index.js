import React from 'react';
import { Link } from 'react-router';
import Email from './email';
import Google from './google';
import Mobile from './mobile';
import LoginPassword from './loginPassword';
import SafePwd from './SafePwd';
import { FormattedMessage, injectIntl } from 'react-intl';
import { mobileFormat, emailFormat } from '../../../utils'
import { formatURL } from '../../../utils/index';
import { connect } from 'react-redux';
import { getUserBaseInfo } from '../../../redux/modules/session'; 
import SigupFirst from './sigupFirst.js';

class UserInfo extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            moduleIndex:0
        }
        
        this.changeModuleIndex = this.changeModuleIndex.bind(this);
        
        this.dateFormat = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        };

        // date split
        this.sp = '-';

        //get user data, when jump this page.
        props.userInfo.isLoaded = false;
        props.getUserBaseInfo();
    }

    changeModuleIndex(id){
        this.setState({moduleIndex:id})
    }

    render(){
        let userInfo = this.props.userInfo;
        
        if(!userInfo.isLoaded){
            return <div><FormattedMessage id="user.text101" /></div>;
        }
        
        userInfo = Object.assign({}, userInfo, {
            previousLogin: this.props.intl.formatDate(userInfo.previousLogin, this.dateFormat).replace(',', '').replace(/\//g, this.sp),
            mobile: mobileFormat(userInfo.mobile)
        })

        
        const moduleIndex = this.state.moduleIndex;
        const l = {
            className: userInfo.userSafeLevel == 1 ? 'low' : (userInfo.userSafeLevel == 2 ? 'in' : 'high'),
            status: userInfo.userSafeLevel ? userInfo.userSafeLevel : 0
        };

        let isEnTime = false;
        const altClassName = (isEnTime = this.props.language.locale === 'en') ? 'en' : 'cn';
        let pLogin = userInfo.previousLogin;
        if(pLogin && this.props.language.locale == 'zh-hant-hk'){
            let tmp = pLogin.split(' ');
            let tmpDate = tmp[0].split(this.sp);
            pLogin = `${tmpDate.reverse().join(this.sp)} ${tmp[1]}`;
        }
        
        return (
            
            <div className="cont-row mg_wp">
                {userInfo.userSafeLevel == 1 && <SigupFirst />}
                <div className="bk-top mb0">
                    <h2><FormattedMessage id="user.text30" /></h2>
                </div>
                <ul className="clearfix mangen_index_head">
                    <li className="clearfix">
                        <span className="left">
                            <i className="iconfont icon">&#xe65e;</i>
                        </span>
                        <span className="left">UID：{userInfo.userName}</span>
                        {
                            userInfo.authStatus != 6 
                            ? 
                            (
                            <span className="left">
                                <span className="user_status user_status_1"><FormattedMessage id="user.text31" /></span>
                                <Link to={formatURL('auth/authentication')}  className="user_href">
                                    <FormattedMessage id="user.text33" />
                                </Link>
                            </span>
                            )
                            :
                            (<span className="user_status user_status_2"><FormattedMessage id="user.text32" /></span>)
                        }
                    </li>

                    <li className="clearfix">
                        <span className="left">
                            <FormattedMessage id="user.text34" />
                        </span>
                     
                        <div className={'left s0 ' + l.className}>
                            <i className="iconfont">&#xe65d;</i>
                            <span>
                                {
                                    <FormattedMessage id={'user.text' + (+l.status + 34) } />
                                }
                            </span>
                            <span className="iconfont help_alt">
                                <span>&#xe657;</span>
                                <div className={'help_div ' + altClassName}>
                                    <p className="clearfix">
                                        <span className="low tips_icons">
                                            <span className="iconfont">
                                                <span>&#xe65d;</span>
                                            </span>
                                        </span>
                                        <span className="low pwdlevel_text"><FormattedMessage id="user.text35" /></span>
                                        <span className="help_text">
                                        <FormattedMessage id="user.text38" />
                                        </span>
                                    </p>
                                    <p className="clearfix">
                                        <span className="in tips_icons">
                                            <span className="iconfont">
                                                <span>&#xe65d;</span>
                                            </span>
                                        </span>
                                        <span className="in pwdlevel_text"><FormattedMessage id="user.text36" /></span>
                                        <span className="help_text">
                                        <FormattedMessage id="user.text39" />
                                        </span>
                                    </p>
                                    <p className="clearfix">
                                        <span className="high tips_icons">
                                            <span className="iconfont">
                                                <span>&#xe65d;</span>
                                            </span>
                                        </span>
                                        <span className="high pwdlevel_text"><FormattedMessage id="user.text37" /></span>
                                        <span className="help_text">
                                        <FormattedMessage id="user.text40" />
                                        </span>
                                    </p>
                                    <span className="xia"></span>
                                </div>
                            </span>
                        </div>
                    </li>
                    <li><span><FormattedMessage id="user.text41" /></span><span>{pLogin}</span>&nbsp;&nbsp;&nbsp;<span>IP：</span><span>{userInfo.loginIp}</span></li>
                </ul>

                <ul className="user_list clearfix">
		            <li className="user_head_li"> <span className="user_border"></span><FormattedMessage id="user.text42" /></li>

                    <li className="user_body_li left clearfix">
                        <p className="p1">
                        <span className="icon bk">
                            <span className="iconfont">
                                <span>&#xe65a;</span>
                            </span>
                        </span>
                        </p>
                        <p className="p2 relative_10">      
                            <span className="user_list_name">
                            <FormattedMessage id="user.text2" />
                            {
                                userInfo.emailSatus == 2 ? <span className="user_mobile_emil_title">{`(${emailFormat(userInfo.email)})`}</span> : null
                            }
                            </span>
                            <span className="user_list_text">
                            <FormattedMessage id="user.text44" />
                            </span>
                        </p>
                        {
                            userInfo.emailSatus != 2 ? (
                            <p className="p3">
                                <Link to={formatURL('user/email')}  className="btn btn-set"><FormattedMessage id="user.text10" /></Link>
                            </p>) : <p className="p3"><a className="btn btn-sm btn-primary" style={{visibility:'hidden'}}>&nbsp;</a></p>
                        }
                    </li>

                    <li className="user_body_li right mb40">
                        <p className="p1">
                            <span className="icon iphone_icon">
                                <span className="iconfont">
                                    <span>&#xe65c;</span>
                                </span>
                            </span> 
                        </p>
                        <p className="p2 relative_10">
                            <span className="user_list_name">
                                <FormattedMessage id="user.text3" />
                                {
                                    userInfo.mobileStatus == 2 ? (
                                    <span className="user_mobile_emil_title">{`(${userInfo.mobile})`}</span>
                                    ):null
                                }
                            </span>
                            <span className="user_list_text">
                            <FormattedMessage id="user.text45" />
                            </span>
                        </p>
                        <p className="p3">
                            <Link to={formatURL('user/mobile')} className={`btn btn-sm ${userInfo.mobileStatus == 2?'btn-primary':'btn-set'}`}>
                            <FormattedMessage id={`user.text${userInfo.mobileStatus == 2 ? '11':'10'}`} /></Link>
                        </p>
                    </li>

                    <li className="user_head_li"><span className="user_border"></span><FormattedMessage id="user.text43" /></li>

                    <li className="user_body_li left clearfix mb10">
                        <p className="p1">
                            <span className="icon iphone_icon">
                                <span className="iconfont">
                                    <span>&#xe65b;</span>
                                </span>
                            </span> 
                        </p>
                        <p className="p2">
                            <span className="user_list_name">
                            <FormattedMessage id="user.text5" />
                            </span>
                            <span className="user_list_text">
                            <FormattedMessage id="user.text46" />
                                {false ? (<em className="high">强</em>):null}
                            </span>
                        </p>
                        <p className="p3"><Link to={formatURL('user/pwd')} className="btn btn-modify"><FormattedMessage id="user.text11" /></Link></p>
                    </li>


                    <li className="user_body_li right mb10">
                        <p className="p1">
                            <span className="icon iphone_icon">
                                <span className="iconfont">
                                    <span>&#xe658;</span>
                                </span>
                            </span> 
                        </p>
                        <p className="p2">
                            <span className="user_list_name">
                            <FormattedMessage id="user.text6" />
                            </span>
                            <span className="user_list_text">
                            <FormattedMessage id="user.text47" />
                                {false ? (<em className="high">帐户未设置资金密码</em>):null}
                            </span>
                        </p>
                        <p className="p3">
                        <Link to={formatURL('user/safePwd')} className={`btn btn-sm ${userInfo.hasSafe?'btn-primary':'btn-set'}`}><FormattedMessage id={`user.text${userInfo.hasSafe ? '11':'10'}`} /></Link>
                        </p>
                    </li>

                    <li className="user_body_li left clearfix">
                        <p className="p1">
                            <span className="icon iphone_icon">
                                <span className="iconfont">
                                    <span>&#xe659;</span>
                                </span>
                            </span> 
                        </p>
                        <p className="p2 relative_10">
                            <span className="user_list_name">
                            <FormattedMessage id="user.text4" />
                            </span>
                            <span className="user_list_text">
                            <FormattedMessage id="user.text48" />
                                {false ? (<em className="high">帐户未设置谷歌验证</em>):null}
                            </span>
                        </p>
                        <p className="p3"><Link to={formatURL('user/google')} className={`btn btn-sm ${userInfo.googleAuth == 2 ? 'btn-primary':'btn-set'}`}><FormattedMessage id={`user.text${userInfo.googleAuth == 2 ? '11':'10'}`} /></Link></p>
                    </li>

                </ul>

                {false ? (
                <ul className="user-info-list">
                    <li>
                       <p className="p1"><FormattedMessage id="user.text2" /></p>
                       <p className="p2">
                       <i className="set-icon">
                          <svg className="icon" aria-hidden="true">
                             <use xlinkHref="#icon-fawenguanli_wancheng"></use>
                          </svg>
                       </i> 
                       <FormattedMessage id="user.text7" />
                       </p>
                       <p className="p3">
                          <Link to={formatURL('user/email')}  className="btn btn-set"><FormattedMessage id="user.text10" /></Link>
                       </p>
                    </li>
                    <li>
                        <p className="p1"><FormattedMessage id="user.text3" /></p>
                        <p className="p2">
                            <i className="set-icon user-check">
                                <svg className="icon" aria-hidden="true">
                                    <use xlinkHref="#icon-fawenguanli_wancheng"></use>
                                </svg>
                            </i> 
                            +86****8217
                        </p>
                        <p className="p3"><Link to={formatURL('user/mobile')} className="btn btn-modify"><FormattedMessage id="user.text11" /></Link></p>
                    </li>
                    <li>
                        <p className="p1"><FormattedMessage id="user.text4" /></p>
                        <p className="p2">
                            <i className="set-icon">
                                <svg className="icon" aria-hidden="true">
                                    <use xlinkHref="#icon-fawenguanli_wancheng"></use>
                                </svg>
                            </i> 
                            <FormattedMessage id="user.text8" />
                        </p>
                        <p className="p3"><Link to={formatURL('user/google')} className="btn btn-set"><FormattedMessage id="user.text10" /></Link></p>
                    </li>
                    <li>
                        <p className="p1"><FormattedMessage id="user.text5" /></p>
                        <p className="p2">
                        <i className="set-icon user-check">
                            <svg className="icon" aria-hidden="true">
                            <use xlinkHref="#icon-fawenguanli_wancheng"></use>
                            </svg>
                        </i> 
                        <FormattedMessage id="user.text9" />
                        </p>
                        <p className="p3"><Link to={formatURL('user/pwd')} className="btn btn-modify"><FormattedMessage id="user.text11" /></Link></p>
                    </li>
                    <li>
                        <p className="p1"><FormattedMessage id="user.text6" /></p>
                        <p className="p2">
                        <i className="set-icon user-check">
                            <svg className="icon" aria-hidden="true">
                            <use xlinkHref="#icon-fawenguanli_wancheng"></use>
                            </svg>
                        </i> 
                        <FormattedMessage id="user.text9" />
                        </p>
                        <p className="p3"><Link to={formatURL('user/safePwd')} className="btn btn-modify"><FormattedMessage id="user.text11" /></Link></p>
                    </li>
                </ul>
                ):null}

            </div>
        )
    }
}
const mapStateToProps = (state) => {
    return {
        language : state.language,
        userInfo: state.session.baseUserInfo
    }
};
const mapDispatchToProps = {
    getUserBaseInfo
};
export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(UserInfo));