import React from 'react';
import { Link } from 'react-router'
import { connect } from 'react-redux'
import { savePassword } from '../../../redux/modules/userInfo'
import { FormattedMessage, injectIntl } from 'react-intl';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend } = notifActions;
import { URL_IMG_CODE, DISMISS_TIME } from '../../../conf';
import { formatURL } from '../../../utils'
import Strength from '../../../components/user/strength';

class LoginPassword extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            pwdLevel:0,
            currentPassword:'',
            pwd:'',
            repassWord:'',
            ic: 0
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.checkPwdStrength = this.checkPwdStrength.bind(this)
        this.ModifyLoginPassword = this.ModifyLoginPassword.bind(this)
        this.intl = props.intl;
        this.fc = this.fc.bind(this)
        this.lc = this.lc.bind(this)
    }

    fc(){
        this.setState({
            ic: 1
        })
    }
    lc(){
        this.setState({
            ic: 0
        })
        this.checkPwdStrength();
    }

     //输入时 input 设置到 satte
     handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //判断密码强度
    checkPwdStrength(){
        let level = 0,pwd=this.state.pwd,index=1;
	    if (pwd.length >= 8 && pwd.length <= 20){
		    if (/\d/.test(pwd)) level++; 
		    if (/[a-z]/.test(pwd)) level++; 
		    if (/[A-Z]/.test(pwd)) level++; 
		    if (/\W/.test(pwd)) level++; 
		    if (level > 1 && pwd.length > 12) level++;
	    }
	    if(level < 1) index = 1;
	    if(level == 2) index = 2;
	    if(level == 3) index = 3;
	    if(level > 3) index = 4;
        this.setState({pwdLevel:level*20})
    }

    ModifyLoginPassword(){
        const {pwdLevel,currentPassword,pwd,repassWord} = this.state
         
        if(!currentPassword){
            this.props.notifSend({
                message: this.intl.formatMessage({id: "user.text98"}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            return;
        }
        if(pwdLevel < 40){
            this.props.notifSend({
                message: this.intl.formatMessage({id: "user.text99"}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            return;
        }
        if(pwd !== repassWord){
            this.props.notifSend({
                message: this.intl.formatMessage({id: "user.text100"}),
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            return;
        }
        this.props.savePassword({
            currentPassword:currentPassword,
            pwd:pwd,
            pwdLevel:pwdLevel
        }).then((res)=>{
            this.props.notifSend({
                message: res.data.des,
                kind: 'info',
                dismissAfter: DISMISS_TIME
            });
            if(res.data.isSuc){
                setTimeout(() => {
                    this.props.router.push(formatURL('/manage/user'));
                }, DISMISS_TIME);
            }
        })


    }


    render(){
        return (
            <div className="cont-row">
                <div className="bk-top mb0">
                    <h2><FormattedMessage id="user.text23" /></h2>
                    <div className="vip-tip">
                        <p><FormattedMessage id="user.text24" /></p>
                    </div>
                </div>

                <div className="fill-form">
                    <div className="fill-group">
                        <em className="name"><FormattedMessage id="user.text25" /></em>
                        <input className="fill-control" type="password" name="currentPassword" onChange={this.handleInputChange} autoComplete="off" />
                    </div>
                    <div className="fill-group">
                        <em className="name"><FormattedMessage id="user.text26" /></em>
                        <input className="fill-control" value={this.state.pwd} type="password" name="pwd" onFocus={this.fc} onKeyUp={this.fc} onBlur={this.lc} onChange={this.handleInputChange} autoComplete="off" />
                        {this.state.ic ? <Strength val={this.state.pwd}></Strength> : null}
                    </div>
                    <div className="fill-group">
                        <em className="name"><FormattedMessage id="user.text27" /></em>
                        <input className="fill-control" type="password" name="repassWord" autoComplete="off" onChange={this.handleInputChange} />                        
                    </div>
                    <div className="fill-group">
                        <em className="name"></em>
                        <button onClick={this.ModifyLoginPassword} className="btn btn-submit-sm"><FormattedMessage id="user.text28" /></button>
                        <Link to="/ac/password_find" className="ml10" target="_blank"><FormattedMessage id="user.text29" /></Link>
                    </div>

                </div>
            </div>
        )
    }
}


const mapStateToProps = (state, ownProps) => {
    return {
        
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        savePassword:(params,cb) => {
            return dispatch(savePassword(params)).then(cb)
        }
    }
}

export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(LoginPassword))









