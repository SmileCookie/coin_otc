import React from 'react';
import {isNickName,isURL,isName,isFloat,isWeChatNum,isMobiles,getCooperInfor,isSpecial} from "./util"
import {WORDLIMIT,NUMLIMIT} from '../../conf/index'
import {cooperArr} from '../../conf/index'
import { FormattedMessage,injectIntl} from 'react-intl'
import CooperationType from '../../components/cooperationType'
import {sendInvitation,controlData} from '../api'
import GetCode from '../../components/getCode';
import listCountry from '../../utils/country'
import { browserHistory } from 'react-router';
import ReactModal from '../../components/popBox/index'

class CelebrityCooperation extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            socialNum:"",//姓名
            linkURL:'',
            linkMan:'',//电话
            weChatNum:'',
            socialNumErr:'',            
            linkURLErr:'',
            linkManErr:'',
            weChatNumErr:'',
            cooperArr:[],//合作类型
            AD:false,
            User:false,
            Media:false,
            Others:false,
            isChooseCoop:false,
            selectedCode: '+86',//国家码
            countryCode:'+86',//国家码
            Mstr:''
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.bOut = this.bOut.bind(this)
        this.fIn = this.fIn.bind(this)
        this.chooseCooper = this.chooseCooper.bind(this)
        this.submit = this.submit.bind(this)
        this.showCooperErr = this.showCooperErr.bind(this);
        this.sendSubmit = this.sendSubmit.bind(this);
        this.coopersChooseCofrim = this.coopersChooseCofrim.bind(this);
        this.getCurrentSelectedCode = this.getCurrentSelectedCode.bind(this);
        this.showStatusType = this.showStatusType.bind(this);
        this.showBakcInfor = this.showBakcInfor.bind(this);

    }

    componentDidMount(){

    }
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        if(name == 'linkMan'){
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
    componentWillMount(){
         console.log(listCountry)
        let _type = ['AD','User','Media','Others'];
        let _newCooper = getCooperInfor(_type);
        this.setState({
            cooperArr:_newCooper
        })
    }
    //选择合作类型
    chooseCooper(type){
        let {cooperArr} = this.state;
        let _obj = {};
        console.log(type)
        cooperArr.map((value,index) =>{
            if(value.type == type){
                _obj = Object.assign({},value,{
                    isChecked:!cooperArr[index].isChecked
                })
                this.setState({
                    [type]:!cooperArr[index].isChecked
                })
                cooperArr.splice(index,1,_obj)
            }
        })
        this.setState({cooperArr:cooperArr},() =>{
            console.log(this.state)
        })
    }
    //提交按钮
    submit(){
        const {AD,User,Media,Others,socialNum,socialNumErr,walletNameErr,linkManErr,linkURL,linkMan,weChatNum,weChatNumErr,linkURLErr} = this.state
        if(!socialNum || socialNumErr){
            this.setState({
                socialNumErr:<FormattedMessage id="请输入正确的姓名！"/>
            })
            return false
        }
        if(!linkURL || linkURLErr){
            this.setState({
                linkURLErr:<FormattedMessage id="请输入正确的社交平台链接！"/>
            })
            return false
        }
        if(!linkMan || linkManErr){
            this.setState({
                linkManErr:<FormattedMessage id="请输入正确的电话号码！"/>
            })
            return false
        }
        if(weChatNumErr){
            this.setState({
                weChatNumErr:<FormattedMessage id="请输入正确的微信号！"/>
            })
            return false
        }
        if(!(AD|| User || Media || Others)){
            this.showCooperErr();
        }else{
            this.sendSubmit();
        }
    }

    showCooperErr(){
        this.setState({isChooseCoop:true});
        setTimeout(() =>{
            this.setState({
                isChooseCoop:false
            })
        },2000)
    }
     //发送请求
     sendSubmit(){
        let {selectedCode,linkMan,socialNum,linkURL,weChatNum} = this.state;
        let _cooperateType = this.coopersChooseCofrim();
        console.log(_cooperateType)
        let _obj ={
            type:1,
            code:selectedCode,
            mobile:linkMan,
            name:socialNum,
            platformLine:linkURL,
            wechat:weChatNum || 0,
            cooperateType:_cooperateType,
        }
        console.log(_obj)
        sendInvitation(_obj).then((res) =>{
            // console.log(JSON.parse(res))
            // let _data  = controlData(res)
            //     _data = JSON.parse(_data);
            if(res.isSuc){
                console.log('ok')
                this.showStatusType();
                  setTimeout(() =>{
                    browserHistory.push('/market/innerTwo/2')
                  },2000)
                
            }else{
                let _err = res.datas.smscode;
                // alert(_err)
                this.showBakcInfor(_err,'确认')
              //   message.info(_err)
            }
        }).catch(err =>{
            console.log(err)
        })
    }

    //判断合作类型
    coopersChooseCofrim(){
       let {cooperArr} = this.state;
       let _arr = [];
       cooperArr.map((item,index) =>{
            if(item.type == 'AD' && item.isChecked){
                _arr.push(1)
            }
            if(item.type == 'User' && item.isChecked){
                _arr.push(2)
            }
            if(item.type == 'Media' && item.isChecked){
                _arr.push(3)
            }
            if(item.type == 'Others' && item.isChecked){
                _arr.push(5)
            }
       })
       if(_arr.length == 0){
           return ''
       }else{
            _arr = JSON.stringify(_arr);
            return _arr
       }
    }
    //input获取焦点
    fIn(type){
        switch(type){
            case 'socialNum':
                this.setState({
                    socialNumErr:""
                })
                break;
            case 'linkURL':
                this.setState({
                    linkURLErr:""
                })
                break;
            case 'linkMan':
                this.setState({
                    linkManErr:""
                })
                break;
            case 'weChatNum':
                this.setState({
                    weChatNumErr:""
                })
                break;
            default:
                break;
        }
    }
    //input 失去焦点
    bOut(type){
        const {socialNum,linkURL,linkMan,weChatNum} = this.state
        switch(type){
            case "socialNum":
                if(socialNum=="" || !isName(socialNum)){                 
                    this.setState({
                        socialNumErr:<FormattedMessage id="请输入正确的姓名！"/>
                    })
                }else{
                    console.log(socialNum)
                    this.setState({
                        socialNumErr:'',                        
                    })
                }
                break;
            case "linkURL":
                if(linkURL=="" || !isURL(linkURL)){
                    console.log('error'+linkURL)
                    this.setState({
                        linkURLErr:<FormattedMessage id="请输入正确的社交平台链接！"/>
                    })
                }else{
                    console.log(linkURL)
                    this.setState({
                        linkURLErr:'',                        
                    })
                }
                break;
            case "linkMan":
                if(linkMan=="" || !isMobiles(linkMan)){
                    console.log('error'+linkMan)
                    this.setState({
                        linkManErr:<FormattedMessage id="请输入正确的电话号码！"/>
                    })
                }else{
                    console.log(linkMan)
                    this.setState({
                        linkManErr:'',                        
                    })
                }
                break;
            case "weChatNum":
                if(!isWeChatNum(weChatNum)){
                    console.log('error'+weChatNum)
                    this.setState({
                        weChatNumErr:<FormattedMessage id="请输入正确的微信号！"/>
                    })
                }else{
                    console.log(weChatNum)
                    this.setState({
                        weChatNumErr:'',                        
                    })
                }
                break;
            default:
                break;
        }
    }

    //国家码
    getCurrentSelectedCode(code = "", name = ""){
        //console.log(code, name);
        //this.setState({countryCode:code})
        this.setState({countryCode:code, selectedCode:code},() =>{
            console.log(this.state.selectedCode)
        })
    }

     //sucess popup
     showStatusType(){
        let str = (<div className="popsUp">
                        <span className="sureType"></span> 
                        <div className="popText" style={{ padding: '0 20px'}}><FormattedMessage id="提交成功"/></div>
                    </div>);
        this.setState({Mstr:str})
        this.modal.openModal();
        setTimeout(() =>{
            this.modal.closeModal()
        },2000)
    }

    //err popup
    showBakcInfor(msg,btn){
        let str = (<div className="Err-popsUp">
                        <div className="popText">{msg}</div>
                        <button className="popBtn" onClick={() => this.modal.closeModal()}><FormattedMessage id={btn}/></button>
                  </div>);
        this.setState({Mstr:str})
        this.modal.openModal();

    }

    render(){
        const {cooperArr,isChooseCoop,socialNum,socialNumErr,linkURL,linkURLErr,linkMan,linkManErr,weChatNum,weChatNumErr,AD,strategy,manu,selectedCode} = this.state
        let {getCurrentSelectedCode} = this;
        return (
            <div className="market-content">
                <div className="market-top">
                    <div className="big-title100"><FormattedMessage id="机会"/> <FormattedMessage id="与"/> <FormattedMessage id="共赢"/></div>
                    <div className="s-title"><FormattedMessage id="尊敬的战略合作伙伴:"/><br />
                        <FormattedMessage id="全球区块链资产交易所即将上线，我们诚挚邀请您的加入！"/></div>
                </div>
                <div className="market-bottom">
                    <div className="bo-center">
                        <div className="cen-input">
                            <div className="input-common">
                                <div className="input-cc">
                                    <div className="input-label"><i></i><FormattedMessage id="姓名"/></div>
                                    <div className="input-box">
                                        <input className={`${socialNumErr && "input-focus"}`} value={socialNum} placeholder="" name="socialNum" onChange={this.handleInputChange} onFocus={()=>this.fIn("socialNum")} onBlur={()=>this.bOut("socialNum")} />
                                    </div>
                                    <span className="warn-err">{socialNumErr}</span>
                                </div>
                                <div className="input-cc">
                                    <div className="input-label"><i></i><FormattedMessage id="社交平台链接"/></div>
                                    <div className="input-box">
                                        <input className={`${linkURLErr && "input-focus"}`} value={linkURL} placeholder="" name="linkURL" onChange={this.handleInputChange} onFocus={()=>this.fIn("linkURL")} onBlur={()=>this.bOut("linkURL")}/>
                                    </div>
                                    <span className="warn-err">{linkURLErr}</span>
                                </div>
                            </div>
                            <div className="input-common">
                                <div className="input-cc">
                                    <div className="input-label"><i></i><FormattedMessage id="电话"/></div>
                                    <div className="input-box hasPhoneCode">
                                        <GetCode startMove={ true } showCode="1" selectedCode={ selectedCode } list={listCountry.country} getCurrentSelectedCode={ getCurrentSelectedCode }></GetCode>
                                        <input className={`${linkManErr && "input-focus"}`} placeholder="" name="linkMan" onChange={this.handleInputChange} value={linkMan} onFocus={()=>this.fIn("linkMan")} onBlur={()=>this.bOut("linkMan")} />
                                    </div>
                                    <span className="warn-err">{linkManErr}</span>
                                </div>
                                <div className="input-cc">
                                    <div className="input-label"><FormattedMessage id="微信号"/></div>
                                    <div className="input-box">
                                        <input className={`${weChatNumErr && "input-focus"}`} value={weChatNum} placeholder="" name="weChatNum" onChange={this.handleInputChange} onFocus={()=>this.fIn("weChatNum")} onBlur={()=>this.bOut("weChatNum")} />
                                    </div>
                                    <span className="warn-err">{weChatNumErr}</span>
                                </div>
                            </div>
                        </div>
                        <div className="cooper-title"><FormattedMessage id="合作类型"/></div>
                    </div>
                    <CooperationType data={cooperArr} isChooseCoop={isChooseCoop} chooseCooper = {type => this.chooseCooper(type)}/>
                    <a className="sub-btn" href="javascript:void(0)" onClick={this.submit}><FormattedMessage id="提交"/></a>
                </div>
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </ReactModal>
            </div>
        );
    }
}

export default CelebrityCooperation;