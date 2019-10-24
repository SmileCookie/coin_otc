import React from 'react';
import {isEmail,isMobiles,isSafePassWord,isFloat} from './util'
import { DOMAIN_VIP,URL_IMG_CODE ,COOKIE_LAN} from '../../conf'
import { FormattedMessage ,injectIntl} from 'react-intl'
import {BeasUrlImg,emailReg,controlData} from '../api'
import GetCode from '../../components/getCode';
import listCountry from '../../utils/country'
import ReactModal from '../../components/popBox/index'
import { browserHistory } from 'react-router';
import cookie from 'js-cookie';
import imgShap from './images/Shape.png'


class InnerOne extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            eMail:"",
            phoneNumber:'',
            passWord:'',
            isShowPSW:true,
            verCode:'',
            line:false,
            eMailErr:'',
            phoneNumberErr:'',
            verCodeErr:'',
            passWordErr:'',
            submitSuc:false,
            verCodeChance:3,
            isGetVerCode:false,//判断一分钟内是否点击再次获取验证码
            verCodeTime:59,
            codeImg:BeasUrlImg + (+new Date),
            selectedCode: '+86',//国家码
            countryCode:'+86',//国家码
            Mstr:''
            
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.fIn = this.fIn.bind(this)
        this.bOut = this.bOut.bind(this)
        this.showPSW = this.showPSW.bind(this)
        this.submit = this.submit.bind(this)
        this.getVerCode = this.getVerCode.bind(this)
        this.changImgCode = this.changImgCode.bind(this)
        this.getCurrentSelectedCode = this.getCurrentSelectedCode.bind(this)
        this.sendSubmit = this.sendSubmit.bind(this);
        this.showBakcInfor = this.showBakcInfor.bind(this)
        this.showStatusType = this.showStatusType.bind(this)
    }
    componentDidMount(){
        let now = +new Date;
        let src = URL_IMG_CODE + "?t=" + now;
        console.log(src)
    }
    componentWillMount(){
        window.localStorage.removeItem('userEmail');
    }
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        if(name == 'phoneNumber'){
            let _num = isFloat(value);
            console.log(_num)
            if(_num){
                this.setState({
                    [name]: value
                })
            }else{
                return false
            }
        }else{
            this.setState({
                [name]: value
            })
        }
        
    }
    //显示密码
    showPSW(){
        const {isShowPSW} = this.state
        this.setState({
            isShowPSW:!isShowPSW
        })
    }
    //input获取焦点时
    fIn(type){
        switch(type){
            case "eMail":
                this.setState({
                    eMailErr:"",
                })
                break;
            case "phoneNumber":
                this.setState({
                    phoneNumberErr:"",
                })
                break;
            case "passWord":
                this.setState({
                    passWordErr:"",
                })
                break;
            case "verCode":
                this.setState({
                    verCodeErr:"",
                })
                break;
            default:
                break;
        }
    }
    //input失去焦点时
    bOut(type){
        const {eMail,phoneNumber,passWord,verCode} = this.state
        switch(type){
            case "eMail":
                if(eMail==""){
                    this.setState({
                        eMailErr:<FormattedMessage id="邮箱不能为空"/>
                    })
                }else if(!isEmail(eMail)){
                    this.setState({
                        eMailErr:<FormattedMessage id="邮箱格式不正确"/>
                    })
                }else{
                    this.setState({
                        eMailErr:false
                    })
                }
                break;
            case "phoneNumber":
                if(phoneNumber==""){
                    this.setState({
                        phoneNumberErr:<FormattedMessage id="手机号不能为空"/>
                    })
                }else if(!isMobiles(phoneNumber)){
                    this.setState({
                        phoneNumberErr:<FormattedMessage id="手机号格式不正确"/>
                    })
                }else{
                    this.setState({
                        phoneNumberErr:false
                    })
                }
                break;
            case "passWord":
                if(passWord == ""){
                    this.setState({
                        passWordErr:<FormattedMessage id="密码不能为空"/>
                    })
                }else if(!isSafePassWord(passWord) || passWord.length<8 || passWord.length>20){
                    this.setState({
                        passWordErr:<FormattedMessage id="您的密码需为8-20位，包含字母，数字，符号的两种以上！"/>
                    })   
                }
                break;
            case "verCode":
                if(verCode == ""){
                    this.setState({
                        verCodeErr:<FormattedMessage id="验证码不能为空"/>
                    })
                }
                break;
            default:
                break;
        }
    }
    //提交按钮
    submit(){
        const {passWord,verCode,eMail,phoneNumber,verCodeChance,eMailErr,phoneNumberErr} = this.state;
        if(!eMail){
            this.setState({
                eMailErr:<FormattedMessage id="邮箱不能为空"/>
            })
            return false;
        }
        if(eMailErr){
            this.setState({
                eMailErr:<FormattedMessage id="邮箱格式不正确"/>
            })
            return false;
        }
        if(!phoneNumber){
            this.setState({
                phoneNumberErr:<FormattedMessage id="手机号不能为空"/>
            })
            return false;
        }
        if(phoneNumberErr){
            this.setState({
                phoneNumberErr:<FormattedMessage id="手机号格式不正确"/>
            })
            return false;
        }
        if(!passWord || !isSafePassWord(passWord) || passWord.length<8 || passWord.length>20){
            console.log(passWord)
            this.setState({
                passWordErr:<FormattedMessage id="您的密码需为8-20位，包含字母，数字，符号的两种以上！"/>
            })
        }
        if(!verCode){
            this.setState({
                verCodeErr:<FormattedMessage id="验证码不能为空"/>
            })
        }else{
            this.sendSubmit();
        }
    }
    sendSubmit(){
        let {eMail,selectedCode,phoneNumber,passWord,verCode} = this.state;
        let _obj ={
            email:eMail,
            password:passWord,
            mobile:selectedCode + " " + phoneNumber,
            code:verCode
        }
        console.log(_obj)
        emailReg(_obj).then((res) =>{
            // console.log(JSON.parse(res))
            // let _data  = controlData(res)
            //   _data = JSON.parse(_data);
            console.log(res)
              if(res.isSuc){
                  console.log('ok')
                  window.localStorage.setItem('userEmail',eMail);
                  this.showStatusType();
                  setTimeout(() =>{
                      browserHistory.push('/market/innerTwo/3')
                  },2000)
                  
              }else{
                let _err = res.datas.smscode;
                this.showBakcInfor(_err,'确认')
              }
        }).catch(err =>{
            console.log(err)
        })
    }
    //获取验证码
    getVerCode(){
        const {passWord,verCode,phoneNumber,isGetVerCode} = this.state;
        if(!phoneNumber){
            this.setState({
                phoneNumberErr:"手机号不能为空！"
            })
            return false;
        } 
        let self = this
        //clearInterval(timer); 
        if(isGetVerCode){
            clearInterval(timers)
            let t = this.state.verCodeTime;
            let timers = setInterval(()=>{
                let temp = t--;
                self.setState({
                    //isGetVerCode:true,
                    //verCodeTime:temp,
                    verCodeErr:`重复提交，请等待${temp}秒后再次尝试。`
                },()=>console.log(this.state.verCodeTime))
                if(temp<=0){
                    clearInterval(timers)
                    self.setState({
                        verCodeErr:'',
                        isGetVerCode:false
                    })
                }
            },1000)
            return false
        }

        console.log('稍后再试')
        clearInterval(timer)
        let t = 59;
        let timer = setInterval(()=>{
            let temp = t--;
            self.setState({
                isGetVerCode:true,
                verCodeTime:temp
            })
            if(temp<0){
                clearInterval(timer)
                self.setState({
                    verCodeErr:'',
                    isGetVerCode:false
                })
            }
        },1000)
        
        // axios.post(DOMAIN_VIP+"", qs.stringify({
        //     passWord,verCode
        // })).then(res => {
        //     const result = res.data;
        //     if(result.code == 0){

        //     }
        // })
    }
    //国家码
    getCurrentSelectedCode(code = "", name = ""){
        //console.log(code, name);
        //this.setState({countryCode:code})
        this.setState({countryCode:code, selectedCode:code},() =>{
            console.log(this.state.selectedCode)
        })
    }
    //改变验证码
    changImgCode(){
        this.setState({
            codeImg:BeasUrlImg + (+new Date)
        })
    }

    //提示弹窗
    showBakcInfor(msg,btn){
        let str = (<div className="Err-popsUp">
                        <div className="popText">{msg}</div>
                        <button className="popBtn" onClick={() => this.modal.closeModal()}><FormattedMessage id ={btn}/></button>
                  </div>);
        this.changeStateMstr(str)
        this.modal.openModal();

    }

    //3秒提示
    showStatusType(){
        
        let str = (<div className="popsUp">
                        <span className="sureType"></span> 
                        <div className="popText" style={{ padding: '0 20px'}}><FormattedMessage id="提交成功"/></div>
                    </div>);
        this.changeStateMstr(str)
        this.modal.openModal();
        setTimeout(() =>{
            this.modal.closeModal()
        },2000)
    }
    //Mstr
    changeStateMstr(str){
        this.setState({Mstr:str})
    }


    render(){
        const {isShowPSW,phoneNumber,verCode,eMailErr,phoneNumberErr,verCodeErr,passWordErr,submitSuc,isGetVerCode ,codeImg,selectedCode} = this.state;
        let {getCurrentSelectedCode} = this;
        let {formatMessage} = this.props.intl;
        let locale = cookie.get(COOKIE_LAN);
        return (
            <div className="market-content inner-bg">
                <div className="inner-top">
                    <div className={locale == 'en' ? 'inner-title-s big-en' : 'inner-title-s'}>
                        {/* <span style={{position:'relative'}}>
                            <FormattedMessage id="由{%%}领投," values={{"%%":'xxx'}}/>
                            {
                                locale !== 'en' &&
                                <span className="text_line1"></span>
                            }
                            
                        </span>
                        <span style={{position:'relative'}}>
                            <FormattedMessage id="{%%}跟投" values={{"%%":'xxx'}}/>
                            {
                                locale !== 'en' &&
                                <span className="text_line2"></span>
                            }
                        </span> */}
                    </div>
                    <div className={locale == 'en'?'inner-title-big big-en':'inner-title-big'}><FormattedMessage id="引领数字交易 成就万千梦想"/></div>
                </div>
                <div className="inner-bottom">
                    <div className="form-title">
                        <FormattedMessage id='填写信息获得创世用户大礼' />
                        <img className={locale == 'en' && 'big-en-img'} src={imgShap} alt=""/>
                    </div>
                    <div className="inner-form">
                        <div className="inner-input">
                            <div className="input-label"><FormattedMessage id="电子邮件" /></div>
                            <div className="input-box" >
                                <input className={`${eMailErr && 'err'} `} name="eMail" placeholder="" onChange={this.handleInputChange} onFocus={()=>this.fIn("eMail")} onBlur={()=>this.bOut("eMail")} />
                            </div>
                            <span className="warn-err">{eMailErr}</span>
                        </div>
                        <div className="inner-input">
                            <div className="input-label"><FormattedMessage id="手机号码" /></div>
                            <div className="input-box hasPhoneCode zuceCode">
                                <GetCode startMove={ true } showCode="1" selectedCode={ selectedCode } list={listCountry.country} getCurrentSelectedCode={ getCurrentSelectedCode }></GetCode>
                                <input className={`${phoneNumberErr && 'err'} `} name="phoneNumber" value={phoneNumber} placeholder="" onChange={this.handleInputChange} onFocus={()=>this.fIn("phoneNumber")} onBlur={()=>this.bOut("phoneNumber")} />
                            </div>
                            <span className="warn-err">{phoneNumberErr}</span>
                        </div>
                        <div className="inner-input">
                            <div className="input-label"><FormattedMessage id="密码" /></div>
                            <div className="input-box">
                                <input className={`${passWordErr && 'err'}`} type={`${isShowPSW&&"password"}`} name="passWord" placeholder="" onChange={this.handleInputChange} onFocus={()=>this.fIn("passWord")} onBlur={()=>this.bOut("passWord")}/>
                                <span onClick={this.showPSW}  className={`iconfont pass-yan ${!isShowPSW &&'showStyle'} icon-yincangmima-guan`}></span>
                            </div>
                            <span className="warn-err">{passWordErr}</span>
                        </div>
                        <div className="inner-input">
                            <div className="input-label"><FormattedMessage id="验证码" /></div>
                            <div className="input-box">
                                <input className={`${verCodeErr && 'err'}`} maxLength="4" value={verCode} name="verCode" placeholder="" onChange={this.handleInputChange} onFocus={()=>this.fIn("verCode")} onBlur={()=>this.bOut("verCode")}/>
                                {/* <a className="input-get" href="javascript:void(0)" onClick={this.getVerCode}>点击获取</a> */}
                                <img className="codeImg"  src={codeImg} onClick={this.changImgCode} alt=""/>
                            </div>
                            <span className="warn-err">{verCodeErr}</span>
                        </div>
                        {/* <img src="http://192.168.3.18:8089/imagecode/get-28-100-50?t=1542334897965" alt=""/> */}
                        {submitSuc && <div className="submit-success inner-suc"><FormattedMessage id="提交成功" /></div>}
                        <a href="javascript:void(0)" onClick={this.submit} className="submit-btn"><FormattedMessage id="提交" /></a>
                    </div>
                </div>
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </ReactModal>
            </div>
        )
    }
}
 
export default injectIntl(InnerOne)