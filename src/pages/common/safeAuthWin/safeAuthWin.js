// components  
  
import React from 'react';  
import { Link } from 'react-router';  
import ReactModal from '../../../components/popBox'
import axios from 'axios';
import {DOMAIN_VIP} from '../../../conf/index'  
class SafeAuthWin extends React.Component {  
  constructor() {  
    super();  
    this.state={
      googleAuthText:'',
      ipAuthText:''
    }
  }  
  componentDidMount(){
    if(this.props.isShow){
      this.modal.openModal();
    }
  }
  componentWillReceiveProps(nextProps){
  }
  // 安全认证输入框，改变触发事件
  updataInput(event){
      if(event.target.id == 'vercode'){
          this.setState({googleAuthText:event.target.value},()=>{
              if(this.state.googleAuthText.length>=6){
                  this.sendAuth();
              }
          })
      }else if(event.target.id == 'mobileCode'){
          this.setState({ipAuthText:event.target.value})
      }
  }
  sendAuth(){
      var datas = {}
      let vercode =this.state.googleAuthText
      let mobileCode = this.state.ipAuthText 
      let loginGoogleAuth = this.props.googleAuth
      let loginIpAuth = this.props.ipAuth
      if(loginGoogleAuth == 'true'){
          if(vercode.length < 6) return this.props.notifSend('请输入合法的验证码','warning')
          datas.vercode = vercode;
      }
      if(loginIpAuth == 'true'){
          if(mobileCode.length < 6) return this.props.notifSend('请输入合法的验证码22','warning')
          datas.mobileCode = mobileCode;
      }

      axios.get(DOMAIN_VIP +"/login/doLoginAuthen?r=" + new Date().getTime(), {
          params:datas
      }).then((res)=>{
          let re = res.data
          if(re.isSuc){
              this.props.notifSend('验证成功')
              window.location.reload()
          }else{
              this.props.notifSend(re.des,'warning')
          }
      })
                          
    }
    cancel(){
        window.location.href="/login/logout/";
    }
  render() {  
    const props = this.props;  
    return (  
      <ReactModal ref={modal => this.modal = modal} clickNotClose = {true}>
          <div className="Jua-table-inner Jua-table-main">
              <div className="head react-safe-box-head" style={{cursor: 'auto'}}>
                  <h3>登录安全验证</h3>
              </div>
              <div className="body">        
                  <div className="bk-page-table">
                      <div className="bk-page-tableCell">
                          <div id="loginAuthForm">    
                              <div className="form-group" id="loginGoogleAuth" style={{display:this.props.googleAuth=='true'?'display':'none'}}>      
                                  <label htmlFor="vercode" className="control-label">本次登录需要Google验证码：</label>      
                                  <input type="text" placeholder="Google验证码" className="form-control" id="vercode" name="vercode"  onChange={this.updataInput.bind(this)}/>    
                              </div>    
                              <div className="form-group" id="loginIpAuth" style={{display:this.props.ipAuth=='true'?'display':'none'}}> 
                                  <label htmlFor="vercode" className="control-label">本次登录需要进行异地登录验证：</label>      
                                  <div className="input-group"> 
                                      <input type="text" placeholder="短信/邮件验证码" className="form-control" id="mobileCode" name="mobileCode"  onChange={this.updataInput.bind(this)}/>        
                                      <div className="input-group-btn">         
                                          <div className="btn-group">            
                                              <button type="button" role="msgCode" id="sendMsgCode" className="btn text-nowrap" style={{borderLeft:'none'}}>点击获取</button>          
                                          </div>        
                                      </div>      
                                  </div>    
                              </div>
                              <p style={{fontSize:'12px'}}>温馨提示：登录成功后可以在安全设置中修改登录验证方式。</p>
                          </div>
                      </div>
                  </div>   
              </div>    
              <div className="foot">
              <a id="JuaBtn_1_1" role="button" className="btn btn-primary btn-sm" onClick={()=>{this.cancel()}}>取消</a>
                  <a id="JuaBtn_1_1" role="button" className="btn btn-primary btn-sm" onClick={()=>{this.sendAuth()}}>提交</a>
              </div>    
              <div className="zoom"></div> 
          </div>
      </ReactModal> 
    );  
  }  
}  

export default SafeAuthWin;  