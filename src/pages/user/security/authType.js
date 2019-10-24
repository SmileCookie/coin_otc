import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router'; 
import GetCode from '../../../components/phonecode/getCode';
import list from '../../../components/phonecode/country';
import { formatURL } from '../../../utils'
import { connect } from 'react-redux';
import { getAuthTypeInfo } from '../../../redux/modules/userInfo'
import { DOMAIN_VIP } from '../../../conf/index';
// get base image
import PassportImg from '../../../assets/img/user_id_3.png';
import IdCard from '../../../assets/img/user_id_2.png';

class AuthType extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            selectedCode: '+86',
            redirect: '',
            isLoaded: 0
        }

        this.getCurrentSelectedCode = this.getCurrentSelectedCode.bind(this);
    }

    componentDidMount(){
        this.initSessionStorage();

        this.props.getAuthTypeInfo().then((res)=>{
            const code = res.data.datas.selectedCode;
            if(code){
                const country = list.country.find(r => r.code == code);
                this.getCurrentSelectedCode(country.code, country.name);
            }
            this.setState({
                isLoaded: 1
            })
        });
    }

    initSessionStorage(){
        if(sessionStorage.getItem("countryCode") && sessionStorage.getItem("countName")){
            this.setState({
                selectedCode : sessionStorage.getItem("countryCode")
            })
        }else{
            this.getCurrentSelectedCode(list.country[1].code, list.country[1].name);
        }
    }

    getCurrentSelectedCode(code = "", name = ""){
        sessionStorage.setItem("countryCode", code);
        sessionStorage.setItem("countName", name);
    }

    render(){
        const authType = this.props.authType;

        if(authType.redirect){
            let path = authType.redirect.replace(DOMAIN_VIP, '');
            path = path ? path : '/';
            this.props.router.push(formatURL(path));
        }

        return (
             
            <div className="auth_wp">
            {this.state.isLoaded ? (<div>
                <h2 className="tith"><FormattedMessage id="user.text49" /></h2>
                <div className="from_prev">
                    <Link to={formatURL('authentication')}>
                        <span> &lt; </span>
                        <FormattedMessage id="nav.text1" />
                    </Link>
                </div>

                <div className="user_auth_content">
                    <div className="country_head">
                        <div className="country_head_title">
                            <FormattedMessage id="user.text66" />
                        </div>
                        <div className="clearfix">
                            <span className="guojia"><FormattedMessage id="user.text67" /></span>
                            <div className="left">
                                <GetCode selectedCode={this.state.selectedCode} list={list.country} getCurrentSelectedCode={this.getCurrentSelectedCode}></GetCode>
                            </div>
                        </div>

                        <div className="country_type">
                            <div className="country_type_head">
                                <FormattedMessage id="user.text68" />
                            </div>
                            <div className="country_type_body clearfix">
                                <Link to={formatURL('passportauth')} className="count_item">
                                    <img src={PassportImg} className="user_auth_img" />
                                    <div className="user_auth_a_text"><FormattedMessage id="user.text71" /></div>
                                </Link>
                                <Link to={formatURL('idcardauth')} className="count_item mar0">
                                    <img src={IdCard} className="user_auth_img_2" />
                                    <div className="user_auth_a_text"><FormattedMessage id="user.text72" /></div>
                                </Link>
                                <div className="count_item_1">
                                    <h5><FormattedMessage id="user.text69" /></h5>
                                    <p>
                                    <FormattedMessage id="user.text70" />
                                    </p>
                                </div>

                            </div>
                        </div>

                    </div>
                </div>
                </div>): (<div><FormattedMessage id="user.text101" /></div>)}
            </div>
            
        );
    }    
}

const mapStateToProps = (state) => {
    return {
        authType: state.userInfo.authType
    }
}

const mapDispatchToProps = {
    getAuthTypeInfo
}

export default connect(mapStateToProps, mapDispatchToProps)(AuthType);