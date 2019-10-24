import React from 'react';
import {isNickName,isURL,isName,isFloat,isWeChatNum,getCooperInfor} from "./util"
import { FormattedMessage,injectIntl} from 'react-intl'
import CooperationType from '../../components/cooperationType'
import {walletCooperate,controlData} from '../api'
import { browserHistory } from 'react-router';
import ReactModal from '../../components/popBox/index'

class WalletCooperation extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            walletName:"",
            linkURL:'',
            linkMan:'',
            weChatNum:'',
            walletNameErr:'',            
            linkURLErr:'',
            linkManErr:'',
            weChatNumErr:'',
            cooperArr:[
                {
                    contentText: "首页广告-文案文案",
                    contentTitle: "首页广告-获得交易所上币权",
                    imgStyle: "list-AD",
                    isChecked: false,
                    title: "首页广告",
                    type: "AD",
                },
                {
                    contentText: "战略合作-文案文案",
                    contentTitle: "战略合作-获得交易所上币权",
                    imgStyle: "list-strategy",
                    isChecked: false,
                    title: "战略合作",
                    type: "Strategy",
                },
                {
                    contentText: "媒体支持-文案文案",
                    contentTitle: "媒体支持-获得交易所上币权",
                    imgStyle: "list-media",
                    isChecked: false,
                    title: "媒体支持",
                    type: "Media",
                },

        ],//合作类型
            submitSuc:false,
            selected:true,
            AD:false,
            Strategy:false,
            Media:false,
            isChooseCoop:false,
            Mstr:''
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.bOut = this.bOut.bind(this)
        this.fIn = this.fIn.bind(this)
        // this.chooseCooper = this.chooseCooper.bind(this)
        this.submit = this.submit.bind(this)
        this.showCooperErr = this.showCooperErr.bind(this)
        this.sendSubmit = this.sendSubmit.bind(this);
        this.coopersChooseCofrim = this.coopersChooseCofrim.bind(this)
        this.showStatusType = this.showStatusType.bind(this)
        this.showBakcInfor = this.showBakcInfor.bind(this)
    }
    componentWillMount(){
        // let _type = ['AD','Media','Strategy'];
        // let _newCooper = getCooperInfor(_type);
        // this.setState({
        //     cooperArr:_newCooper
        // },() =>{
        //     console.log(_newCooper)
        // })
    }
    componentDidMount(){

    }
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
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
        const {AD,Strategy,Media,walletName,walletNameErr,linkURL,linkURLErr,linkMan,linkManErr,weChatNum,weChatNumErr} = this.state
        if(!walletName || walletNameErr){
            this.setState({
                walletNameErr:<FormattedMessage id="请输入正确的钱包名称！"/>
            })
            return false
        }
        if(!linkURL || linkURLErr){
            this.setState({
                linkURLErr:<FormattedMessage id="请输入正确的官网链接！"/>
            })
            return false
        }
        if(!linkMan || linkManErr){
            this.setState({
                linkManErr:<FormattedMessage id="请输入正确的联系人！"/>
            })
            return false
        }
        if(!weChatNum || weChatNumErr){
            this.setState({
                weChatNumErr:<FormattedMessage id="请输入正确的微信号！"/>
            })
            return false
        }
        if(!(AD || Strategy || Media)){
            this.showCooperErr()
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
        let {walletName,linkURL,linkMan,weChatNum} = this.state;
        let _cooperateType = this.coopersChooseCofrim();
        console.log(_cooperateType)
        let _obj ={
            walletName:walletName,
            websitesLink:linkURL,
            userName:linkMan,
            wechat:weChatNum,
            cooperateType:_cooperateType,
        }
        walletCooperate(_obj).then((res) =>{
            // let _data  = controlData(res)
            // _data = JSON.parse(_data);
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
             if(item.type == 'Strategy' && item.isChecked){
                 _arr.push(6)
             }
             if(item.type == 'Media' && item.isChecked){
                 _arr.push(3)
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
            case 'walletName':
                this.setState({
                    walletNameErr:""
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
        const {walletName,linkURL,linkMan,weChatNum} = this.state
        switch(type){
            case "walletName":
                if(walletName=="" || !isNickName(walletName)){                 
                    this.setState({
                        walletNameErr:<FormattedMessage id="请输入正确的钱包名称！"/>
                    })
                }else{
                    console.log(walletName)
                    this.setState({
                        walletNameErr:'',                        
                    })
                }
                break;
            case "linkURL":
                if(linkURL=="" || !isURL(linkURL)){
                    console.log('error'+linkURL)
                    this.setState({
                        linkURLErr:<FormattedMessage id="请输入正确的官网链接！"/>
                    })
                }else{
                    console.log(linkURL)
                    this.setState({
                        linkURLErr:'',                        
                    })
                }
                break;
            case "linkMan":
                if(linkMan=="" || !isName(linkMan)){
                    console.log('error'+linkMan)
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
                if(weChatNum=="" || !isWeChatNum(weChatNum)){
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
        const {walletName,walletNameErr,linkURL,linkURLErr,linkMan,linkManErr,weChatNum,weChatNumErr,submitSuc,AD,strategy,manu,isChooseCoop,cooperArr} = this.state

        return (
            <div className="market-content">
                <div className="market-top">
                    <div className="big-title"><FormattedMessage id="数字资产交易与您合作共赢"/></div>
                    <div className="s-title"><FormattedMessage id="尊敬的战略合作伙伴:"/><br />
                        <FormattedMessage id="全球区块链资产交易所即将上线，我们诚挚邀请您的加入！"/></div>
                </div>
                <div className="market-bottom">
                    <div className="bo-center">
                        <div className="cen-input">
                            <div className="input-common">
                                <div className="input-cc">
                                    <div className="input-label"><i></i><FormattedMessage id="钱包名称"/></div>
                                    <div className="input-box">
                                        <input className={`${walletNameErr && "input-focus"}`} placeholder="" name="walletName" onChange={this.handleInputChange} onFocus={()=>this.fIn("walletName")} onBlur={()=>this.bOut("walletName")}/>
                                    </div>
                                    <span className="warn-err">{walletNameErr}</span>
                                </div>
                                <div className="input-cc">
                                    <div className="input-label"><i></i><FormattedMessage id="官方链接"/></div>
                                    <div className="input-box">
                                        <input className={`${linkURLErr && "input-focus"}`} placeholder="" name="linkURL" onChange={this.handleInputChange} onFocus={()=>this.fIn("linkURL")} onBlur={()=>this.bOut("linkURL")}/>
                                    </div>
                                    <span className="warn-err">{linkURLErr}</span>
                                </div>
                            </div>
                            <div className="input-common">
                                <div className="input-cc">
                                    <div className="input-label"><i></i><FormattedMessage id="联系人"/></div>
                                    <div className="input-box">
                                        <input className={`${linkManErr && "input-focus"}`} placeholder="" name="linkMan" onChange={this.handleInputChange} onFocus={()=>this.fIn("linkMan")} onBlur={()=>this.bOut("linkMan")} />
                                    </div>
                                    <span className="warn-err">{linkManErr}</span>
                                </div>
                                <div className="input-cc">
                                    <div className="input-label"><i></i><FormattedMessage id="微信号"/></div>
                                    <div className="input-box">
                                        <input className={`${weChatNumErr && "input-focus"}`} placeholder="" name="weChatNum" onChange={this.handleInputChange} onFocus={()=>this.fIn("weChatNum")} onBlur={()=>this.bOut("weChatNum")} />
                                    </div>
                                    <span className="warn-err">{weChatNumErr}</span>
                                </div>
                            </div>
                        </div>
                        <div className="cooper-title"><FormattedMessage id="合作类型"/></div>
                    </div>
                    <CooperationType data={cooperArr} isChooseCoop={isChooseCoop} chooseCooper = {type => this.chooseCooper(type)}/>
                    <a className="sub-btn" disabled href="javascript:void(0)" onClick={this.submit}><FormattedMessage id="提交"/></a>
                </div>
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </ReactModal>
            </div>
        );
    }
}

export default WalletCooperation;