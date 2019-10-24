import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import AuthenticationOpt from './authenticationopt';
import { formatURL } from '../../../utils';

import { connect } from 'react-redux';
import { getAuthInfo } from '../../../redux/modules/userInfo';

class Authentication extends React.Component{
    constructor(props){
        super(props);
    }

    componentDidMount(){
        // getAuthInfo
        this.props.getAuthInfo();
    }

    render(){
        
        const authInfos = this.props.authInfos;

        const isLoaded = !!authInfos.authStatus;

        const authStatus = authInfos.authStatus;

        let authStr = null;

        let opt = -1;

        if(authInfos.isBlack){
            opt = 0;
        } else if(!authInfos.isBlack && authInfos.isLock){
            opt = 1;
        } else {
            authStatus != 4 ? opt = 3 : opt = 2;
        }

        switch(authStatus){
            case 5:
                authStr = (
                    <section className="user_auth_under">
                        <span className="user_svg_bg"></span>
					    <br/>
                        <FormattedMessage id="user.text62"/>
                    </section>
                );
            break;
            case 6:
                this.props.router.push(formatURL('/manage/user'));
                break;
            case 7:
                authStr = (
                    <section className="user_auth_under fail_user_auth">
                        <span className="user_svg_bg user_auth_id_bg_2"></span>
					    <br/>
                        <FormattedMessage id="user.text63"/>
                        <div className="user_auth_fail">
                        <h5>
                        <FormattedMessage id="user.text64"/>
                        </h5>
                        <FormattedMessage id="user.text65"/>
                        {authInfos.reason}
                        </div>
                        <AuthenticationOpt lng={this.props.intl.locale == 'en'} date={+authInfos.lockTime} opt={opt}></AuthenticationOpt>
                    </section>

                );
            break;
            default:
                authStr = (<div className="user_auth_statement_warp"> 
                <section className="user_auth_statement">
                    <h5><FormattedMessage id="user.text50"/></h5>
                    <div className="sttement_div">
                        <p>
                            1、<FormattedMessage id="user.text51"/>
                        </p>
                        <p>
                            2、<FormattedMessage id="user.text52"/>
                        </p>
                        <p>
                            3、<FormattedMessage id="user.text53"/>
                        </p>
                        <p>
                            4、<FormattedMessage id="user.text54"/>
                        </p>
                        <p>
                            5、<FormattedMessage id="user.text55"/>
                        </p>
                        <p>
                            6、<FormattedMessage id="user.text56"/>
                        </p>
                    </div>
                </section>
                <AuthenticationOpt date={+authInfos.lockTime} opt={opt}></AuthenticationOpt>				
            </div>);
            break;
        }

        return (
        
        <div className="auth_wp">
        {
            isLoaded ? (
            <div>
                <h2 className="tith"><FormattedMessage id="user.text49"/></h2>
                {authStr}
            </div>) : (
            <div><FormattedMessage id="user.text101" /></div>
            )
        }
        </div>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        authInfos: state.userInfo.authInfo
    }
}

const mapDispatchToProps = {
    getAuthInfo
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(Authentication));