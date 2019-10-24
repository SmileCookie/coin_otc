import React from 'react';
import {isNickName,isURL,isName,isFloat,isWeChatNum,isMobiles,getCooperInfor,isSpecial} from "./util"
import {WORDLIMIT,NUMLIMIT} from '../../conf/index'
import {cooperArr} from '../../conf/index'
import { FormattedMessage,injectIntl} from 'react-intl'
import CooperationType from '../../components/cooperationType'
import {sendInvitation,controlData} from '../api'
import GetCode from '../../components/getCode';
import listCountry from '../../utils/country'
import img1 from './images/avatar01.png'
import img2 from './images/avatar02.png'
import img3 from './images/avatar03.png'

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
            cardsInfor:[
                {
                    imgUrl:img1,
                    name:'Aaron',
                    content:'chipfex_01@sina.com'
                },
                {
                    imgUrl:img2,
                    name:'Helen',
                    content:'chipfex_02@sina.com'
                },
                {
                    imgUrl:img3,
                    name:'Nina',
                    content:'chipfex_03@sina.com'
                }
            ]
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
        this.getScrollTop = this.getScrollTop.bind(this);
    }
    componentDidMount(){
        // window.scrollTo(0,0);
        this.getScrollTop();
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
        if(linkManErr || !linkMan){
            this.setState({
                linkManErr:<FormattedMessage id="请输入正确的姓名！"/>
            })
            return false
        }
        if(phoneNumErr || !phoneNum){
            this.setState({
                phoneNumErr:<FormattedMessage id="请输入正确的电话号码！"/>
            })
            return false
        }
        if(weChatNumErr || !weChatNum){
            this.setState({
                weChatNumErr:<FormattedMessage id="请输入正确的微信号！"/>
            })
            return false
        }
        if(mediaNameErr || !mediaName){
            this.setState({
                mediaNameErr:<FormattedMessage id="请输入正确的钱包地址！"/>
            })
            return false
        }else{
            this.sendSubmit();
        }

        
    }
     getScrollTop(){
            var scrollTop=0;
            if(document.documentElement&&document.documentElement.scrollTop){
                setTimeout (() =>{
                    document.documentElement.scrollTop = 0;
                },10)
                
                console.log(scrollTop)
            }else if(document.body){

                scrollTop=document.body.scrollTop;
                
            }
                return scrollTop;
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
                if(mediaName=="" || !isWeChatNum(mediaName)){                 
                    this.setState({
                        mediaNameErr:<FormattedMessage id="请输入正确的钱包地址！"/>
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
                        linkManErr:<FormattedMessage id="请输入正确的姓名！"/>
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
    //发送请求
    sendSubmit(){
        let {mediaName,phoneNum,linkMan,weChatNum,selectedCode} = this.state;
        let _obj ={
           name:linkMan,
           code:selectedCode,
           mobile:phoneNum,
           wechat:weChatNum,
           walletAddress:mediaName,

        }
        console.log(_obj)
        // sendInvitation(_obj).then((res) =>{
        //     // console.log(JSON.parse(res))
        //     let _data  = controlData(res)
        //     console.log(JSON.parse(_data))
        // }).catch(err =>{
        //     console.log(err)
        // })
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

    
    render(){
        const {mediaName,mediaNameErr,phoneNum,phoneNumErr,linkMan,linkManErr,weChatNum,weChatNumErr,cooperArr,isChooseCoop,selectedCode,cardsInfor} = this.state
        let {getCurrentSelectedCode} = this;
        return (
            <div className="market-content">
                <div className="market-top">
                    <div className="big-title100"><FormattedMessage id="恭喜您成为我们的一员"/></div>
                    <div className="s-title" style={{fontSize:'24px',fontWeight:'normal',lineHeight:'50px'}}><FormattedMessage id="1.工资每月15号结算"/><br />
                    <FormattedMessage id="2.现金奖励每月20号结算"/></div>
                </div>
                <div className="market-bottom">
                    <div className="bo-center linkCard">
                        <p><FormattedMessage id="官方联系人"/></p>
                        <div className="cards">
                        {
                            cardsInfor.map((item,index) =>{
                                return(
                                    <div className="card" key={item.name}>
                                        <div className="linkImg">
                                            <img className="center" src={item.imgUrl} alt=""/>
                                        </div>
                                        <p className="p1">{item.name}</p>
                                        <p className="p2">{item.content}</p>
                                    </div>
                                )
                            })
                        }
                           
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default MediaCooperation;