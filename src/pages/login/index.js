import React from 'react'
import axios from 'axios'
import qs from 'qs'
import cookie from 'js-cookie'
import { Button,message } from 'antd'
import Logo from '../../assets/images/logo.svg'
import user from '../../assets/images/user.png'
import psd from '../../assets/images/psd.png'
import google from '../../assets/images/google.png'
import { DOMAIN_VIP } from '../../conf/index';
import history from '../../utils/history'

export default class Login extends React.Component{

    constructor(props){
        super(props)
        this.state = { 
            username:'',
            password:'',
            googlecode:''
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.loginBtn = this.loginBtn.bind(this)
        this.keylogin = this.keylogin.bind(this)
    }

    componentDidMount(){
        let self = this
        window.addEventListener('keypress',this.keylogin)
    }
   
    componentWillUnmount(){
        window.removeEventListener('keypress',this.keylogin)
    }

    //验证 Google 验证码
    ObtainGoogleCode(){
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode").then(res => {
            const result = res.data;
            if(result.code == 0){
                console.log(result)
            }
        })
    }

    //keylogin
    keylogin(e){
        if(e.keyCode == 13){
            this.loginBtn()
        }
    }
    
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    //登录按钮
    loginBtn(){
        const { username,password,googlecode } = this.state
        if(!username){
            message.warning("请输入用户名！");
            return false;
        }
        if(!password){
            message.warning("请输入密码！");
            return false;
        }

        axios.post(DOMAIN_VIP+"/sys/login",qs.stringify({
            username,
            password,
            googleCode:googlecode
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                cookie.set("token",result.token)
                history.push('/')
             }else{
                 message.warning(result.msg)
                 if(result.msg=='非法登录'){
                    window.location.href=result.url
                 }
             }
        })
    }

    render(){
        const { username,password,googlecode } = this.state
        return(
            <div className="login-box">
                <div className="login-con">
                    {/* <h1 className="logo">
                       
                    </h1> */}
                    <h2 className="title"><span className="logo"><img src={Logo} alt="logo"/></span>后台管理系统</h2>
                    <div className="login-form">
                        <div className="login-group">
                            <span className="img-box">
                                <img src={user} alt="用户名"/>
                            </span>
                            <input type="text" placeholder="用户名" name="username" onChange={this.handleInputChange}/>
                        </div>

                        <div className="login-group">
                            <span className="img-box">
                                <img src={psd} alt="密码"/>
                            </span>
                            <input type="password" placeholder="密码" name="password" onChange={this.handleInputChange}/>
                        </div>

                        <div className="login-group">
                            <span className="img-box">
                                <img src={google} alt="谷歌验证码"/>
                            </span>
                            <input type="text" placeholder="谷歌验证码" name="googlecode" onChange={this.handleInputChange}/>
                        </div>
                        <div className="login-group">
                            <button className="ant-btn-login" onClick={this.loginBtn}>登录</button>
                        </div>
                    </div>
                </div>
            </div>
       ) 
    }
}











































