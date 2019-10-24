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

class MediaCooperation extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            mediaName:"",
            phoneNum:'',
            linkMan:'',
            weChatNum:'',
            mediaNameErr:'',            
            phoneNumErr:'',
            linkManErr:'',
            weChatNumErr:'',
            cooperArr:[
                
            ],//合作类型
            AD:false,//1
            User:false,//2
            Media:false,//3
            Reminder:false,//4
            sendChooseCooper:[],
            isChooseCoop:false,
            selectedCode: '+86',//国家码
            countryCode:'+86',//国家码
            Mstr:''
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.bOut = this.bOut.bind(this)
        this.fIn = this.fIn.bind(this)
        // this.chooseCooper = this.chooseCooper.bind(this)
        this.submit = this.submit.bind(this)
        this.showCooperErr = this.showCooperErr.bind(this);
        this.sendSubmit = this.sendSubmit.bind(this);
        this.coopersChooseCofrim = this.coopersChooseCofrim.bind(this)
        this.getCurrentSelectedCode = this.getCurrentSelectedCode.bind(this);
        this.showStatusType = this.showStatusType.bind(this)
        this.showBakcInfor = this.showBakcInfor.bind(this)
    }
    componentDidMount(){
        
    }
    componentWillMount(){
        let _type = ['AD','User','Media','Reminder'];
        let _newCooper = getCooperInfor(_type);
        this.setState({
            cooperArr:_newCooper
        })
    }
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        if(name == 'phoneNum'){
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
        const {AD,User,Media,Reminder,mediaNameErr,phoneNumErr,linkManErr,weChatNumErr,mediaName,phoneNum,linkMan,weChatNum} = this.state
        if(mediaNameErr || !mediaName){
            this.setState({
                mediaNameErr:<FormattedMessage id="请输入正确的媒体名称！"/>
            })
            return false
        }
        if(phoneNumErr || !phoneNum){
            this.setState({
                phoneNumErr:<FormattedMessage id="请输入正确的电话号码！"/>
            })
            return false
        }
        if(linkManErr || !linkMan){
            this.setState({
                linkManErr:<FormattedMessage id="请输入正确的联系人！"/>
            })
            return false
        }
        if(weChatNumErr){
            this.setState({
                weChatNumErr:<FormattedMessage id="请输入正确的微信号！"/>
            })
            return false
        }
        if(!(AD|| User || Media || Reminder)){
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
    //input获取焦点
    fIn(type){
        switch(type){
            case 'mediaName':
                this.setState({
                    mediaNameErr:""
                })
                break;
            case 'phoneNum':
                this.setState({
                    phoneNumErr:""
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
        const {mediaName,phoneNum,linkMan,weChatNum} = this.state
        switch(type){
            case "mediaName":
                if(mediaName=="" || !isNickName(mediaName) || isSpecial(mediaName)){                 
                    this.setState({
                        mediaNameErr:<FormattedMessage id="请输入正确的媒体名称！"/>
                    })
                }else{
                    
                    this.setState({
                        mediaNameErr:'',                        
                    })
                }
                break;
            case "phoneNum":
                if(phoneNum=="" || !isMobiles(phoneNum)){
                    
                    this.setState({
                        phoneNumErr:<FormattedMessage id="请输入正确的电话号码！"/>
                    })
                }else{
                    console.log(phoneNum)
                    this.setState({
                        phoneNumErr:'',                        
                    })
                }
                break;
            case "linkMan":
                if(linkMan=="" || !isName(linkMan) || isSpecial(linkMan)){
                   
                    this.setState({
                        linkManErr:<FormattedMessage id="请输入正确的联系人！"/>
                    })
                }else{
                    console.log(linkMan)
                    this.setState({
                        linkManErr:'',                        
                    })
                }
                break;
            case "weChatNum":
                if( !isWeChatNum(weChatNum)){
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
    //发送请求
    sendSubmit(){
        let {mediaName,phoneNum,linkMan,weChatNum,selectedCode} = this.state;
        let _cooperateType = this.coopersChooseCofrim();
        console.log(_cooperateType)
        let _obj ={
            type:0,
            code:selectedCode,
            mobile:phoneNum,
            userName:linkMan,
            name:mediaName,
            wechat:weChatNum || 0,
            cooperateType:_cooperateType,
        }
        sendInvitation(_obj).then((res) =>{
            // console.log(JSON.parse(res))
              if(res.isSuc){
                this.showStatusType();
                setTimeout(() =>{
                  browserHistory.push('/market/innerTwo/2')
                },2000)
              }else{
                  let _err = res.datas.smscode;
                  this.showBakcInfor(_err,'确认')                
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
            if(item.type == 'Reminder' && item.isChecked){
                _arr.push(4)
            }
       })
       if(_arr.length == 0){
           return ''
       }else{
            _arr = JSON.stringify(_arr);
            return _arr
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
        const {mediaName,mediaNameErr,phoneNum,phoneNumErr,linkMan,linkManErr,weChatNum,weChatNumErr,cooperArr,isChooseCoop,selectedCode} = this.state
        let {getCurrentSelectedCode} = this;
        return (
            <div className="market-content">
                <div className="market-top">
                    <div className="big-title100"><FormattedMessage id="机会"/> <FormattedMessage id="与"/> <FormattedMessage id="共赢"/></div>
                    <div className="s-title"><FormattedMessage id="致各位媒体朋友"/>:<br />
                    <FormattedMessage id="错过互联网，还要错过区块链吗？我们需要您的加入与支持！"/></div>
                </div>
                <div className="market-bottom">
                    <div className="bo-center">
                        <div className="cen-input">
                            <div className="input-common">
                                <div className="input-cc">
                                    <div className="input-label"><i></i><FormattedMessage id="媒体名称"/></div>
                                    <div className="input-box">
                                        <input className={`${mediaNameErr && "input-focus"}`} value={mediaName} maxLength={WORDLIMIT}  name="mediaName" onChange={this.handleInputChange} onFocus={()=>this.fIn("mediaName")} onBlur={()=>this.bOut("mediaName")}/>
                                    </div>
                                    <span className="warn-err">{mediaNameErr}</span>
                                </div>
                                <div className="input-cc">
                                    <div className="input-label"><i></i><FormattedMessage id="电话"/></div>
                                    <div className="input-box hasPhoneCode">
                                        <GetCode startMove={ true } showCode="1" selectedCode={ selectedCode } list={listCountry.country} getCurrentSelectedCode={ getCurrentSelectedCode }></GetCode>
                                        <input className={`${phoneNumErr && "input-focus"}`} value={phoneNum} maxLength={NUMLIMIT}  name="phoneNum" onChange={this.handleInputChange} onFocus={()=>this.fIn("phoneNum")} onBlur={()=>this.bOut("phoneNum")}/>
                                    </div>
                                    <span className="warn-err">{phoneNumErr}</span>
                                </div>
                            </div>
                            <div className="input-common">
                                <div className="input-cc">
                                    <div className="input-label"><i></i><FormattedMessage id="联系人"/></div>
                                    <div className="input-box">
                                        <input className={`${linkManErr && "input-focus"}`} maxLength={WORDLIMIT} value={linkMan} name="linkMan" onChange={this.handleInputChange} onFocus={()=>this.fIn("linkMan")} onBlur={()=>this.bOut("linkMan")} />
                                    </div>
                                    <span className="warn-err">{linkManErr}</span>
                                </div>
                                <div className="input-cc">
                                    <div className="input-label"><FormattedMessage id="微信号"/></div>
                                    <div className="input-box">
                                        <input className={`${weChatNumErr && "input-focus"}`} value={weChatNum} maxLength={NUMLIMIT}  name="weChatNum" onChange={this.handleInputChange} onFocus={()=>this.fIn("weChatNum")} onBlur={()=>this.bOut("weChatNum")} />
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

export default MediaCooperation;