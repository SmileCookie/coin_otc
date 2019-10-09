import React from 'react';
import {withRouter} from 'react-router';
import { FormattedMessage, injectIntl } from 'react-intl';
import ReactScroll from "react-scrollbar";
import { Scrollbars } from 'react-custom-scrollbars';
import './talkRoom.less'
import E from 'wangeditor'
import {DATA_NOW_FORMAT,USERID,DATA_DAY_FORMAT,DATA_MIMUTE_FORMAT,DATA_MIMUTE_FORMAT_EN} from 'conf'
import {getFirstStr} from '../../utils'
import moment from 'moment'
import confs from '../../conf'
import UploadImage from 'react-upload-images'
import {optPop} from '../../utils'
import ReactModal from '../popBox'
import axios from 'axios'
import{post} from '../../net'
import Cookie from 'js-cookie'
import closeImg from '../../assets/image/del-0@2x.png'



class OctTalkRoom extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            talkInfor:[
                //    {
                //         userType:"system",//self 自己 & user 对面 & system 系统推送
                //         content:`<p>dsddsdsdsdsdss<p/>`,
                //         time: '08:12',
                //         id:Math.random(),
                //    }
            ],
            editorContent:'',
            onChangeScroll:false,
            clearText:false,
            inforH:'',
            imgV:'',
            isGetTalkHistory:true,
            showEmji:false,
            Mstr:'',
            statuesMsg:'',
            stopTalk:false,
            enterH:''
        };
        this.sendEnterInfor = this.sendEnterInfor.bind(this);
        this.keepBottom = this.keepBottom.bind(this);
        this.wangEditor = this.wangEditor.bind(this);
        this.sendTxt = this.sendTxt.bind(this)
        this.insertInfor = this.insertInfor.bind(this)
        this.getTalkHistory = this.getTalkHistory.bind(this)
        this.sendImg = this.sendImg.bind(this)
        //this.getAllTalkInfor = this.getAllTalkInfor.bind(this)
        this.imgI = React.createRef()
        this.stopSend = this.stopSend.bind(this)


    }
    componentWillMount(){
        // //初始化聊天室（融云）
        // rongTalkInit();
        document.removeEventListener("keydown",this.handleEenterKey);
    }
    componentDidMount(){

        document.addEventListener("keydown",this.handleEnterKey);
        //wangEditor
        this.wangEditor();
        let _this = this
        if(this.props.isConnet){
            //获取聊天记录
            this.getTalkHistory()
        }
        //同步聊天室滚动大小铺满
        this.inforHeight(1);
        this.configTalkHeight()
        //检测订单状态
       // this.checkoutOrderStatus()
       window.addEventListener('mousedown',this.hideEmji)
    }
    componentWillReceiveProps(nextProps){
        let {formatMessage} = this.props.intl;
        let that = this;
       
        //获取实时聊天记录
        if(nextProps.count !== this.props.count){
            let {backUseId,id} = this.props.dealInforList;
            let content = nextProps.text;
            let _ids = 'self';
            if(backUseId == content.senderUserId){
                _ids = 'user'
            }
            if(content.content.extra == id && content.messageType == 'TextMessage'){//接受对方 文本信息
                let _content = RongIMLib.RongIMEmoji.emojiToSymbol(content.content.content);
                let _html = `<p>${_content}</p>`
                this.insertInfor(_ids,content.sentTime,_html)
            }
            if(content.content.extra == id && content.messageType == 'ImageMessage'){//接受对方 图片信息
                let _html =  `<img src=${'data:image/jpeg;base64,' + content.content.content} data =${content.content.imageUri} style="max-width:100%;" />`
                this.insertInfor(_ids,content.sentTime , _html)
                setTimeout(()=>{
                    that.initScrollBar();
                }, 500)
            }
        }
        //获取实时系统消息
        if(nextProps.countSys !== this.props.countSys){
            let {backUseId,id} = nextProps.dealInforList;
            let content = nextProps.sysText;
            console.log(content);
            if(content.objectName == 'OTC:OrderMsg' && content.content.message.content.extra == id ){
                let _html = `<h3 style="font-size:16px;color:#fff;">${formatMessage({id:content.content.message.content.title})}<h3/>
                             <pre>${formatMessage({id:content.content.message.content.content})}<pre/>`
                
                let heightConfig = setTimeout( async () =>{
                     this.insertInfor('system',content.sentTime , _html)
                     await this.props.getOrderDetailEvent(false)
                     this.inforHeight();
                     clearInterval(heightConfig)
                },2000)
                //window.location.reload()
            }
        }
        //判断是否需要获取聊天记录
        if(this.props.isConnet !== nextProps.isConnet){
            this.getTalkHistory()
        }
        //实时更新聊天高度
        if(this.props.dealInforList.dealStatue !== nextProps.dealInforList.dealStatue){
            setTimeout(() => {
                that.inforHeight();
                that.initScrollBar();
            }, 100);
        }

    }

    componentWillUnmount(){
        window.removeEventListener('mousedown',this.hideEmji)
        clearInterval(this.setHeight)
    }
    //同步聊天室滚动大小铺满
    inforHeight = (type) =>{
        let num = 32;
        if(type) {
            num = 20;
        }
        //console.log(document.getElementsByClassName('enterContent')[0].clientHeight);
        
        this.setState({
            inforH:document.getElementsByClassName('talkContent')[0].clientHeight - num,
            enterH:document.getElementsByClassName('enterContent')[0].clientHeight - 60
        })
    }

    //实时自适应聊天高度变化
    configTalkHeight = () =>{
        this.setHeight = setInterval(()=>{
            this.inforHeight();
            //this.initScrollBar();
        },200)
    }
    wangEditor(){
        let {formatMessage} = this.props.intl;
        const elem = this.refs.editorElem
        this.editor = new E(elem);
        let defultTex = formatMessage({id:'输入信息，回车发送'});
        this.editor.customConfig.menus = [
            // 'image',
            //  'emoticon'
        ]
        this.editor.customConfig.emotions = [
            {
                // tab 的标题
                title: '😀',
                // type -> 'emoji' / 'image'
                type: 'emoji',
                // content -> 数组
                content: ['😀', '😃', '😄', '😁', '😆']
            }
        ]
        this.editor.customConfig.colors = [
            '#d01c1c'
        ]
        //this.editor.customConfig.uploadImgShowBase64 = true
        this.editor.customConfig.showLinkImg = false
        // 使用 onchange 函数监听内容的变化，并实时更新到 state 中
         // 忽略粘贴内容中的图片
        this.editor.customConfig.pasteIgnoreImg = true
        this.editor.customConfig.onchangeTimeout = 10
        this.editor.customConfig.onchange = html => {
            // let _imgCheck = /img/;
            // let _htmlCheck = _imgCheck.test(this.editor.txt.html())
            // if(_htmlCheck){
            //     this.editor.txt.clear()
            // }
            // // this.setState({
            // //     editorContent: html,
            // // })
        }
        this.editor.create();
        this.editor.$textElem[0].style.overflow = 'auto';
        this.editor.$textElem[0].style.border = 'none';
        this.editor.txt.clear()
        this.editor.$textElem[0].onkeydown = (e) =>{
            if(e.keyCode == '13'){
                return false
            }else{
                this.setState({
                    clearText:true
                })
            }
        }
        this.editor.$textElem[0].onfocus = () =>{
            this.setState({
                clearText:true
            })
        }

    }
    //发送文字信息
    sendTxt(msg ="", msgHtml=""){
        let {backUseId,id} = this.props.dealInforList;
        const RongIMLib = this.props.RongIMLib
        const msgInfor = this.htmlRestore(msg);
        let _this = this;
        let _msg = new RongIMLib.TextMessage({ content: msgInfor, extra:id.toString()});
        let conversationType = RongIMLib.ConversationType.PRIVATE; // 单聊, 其他会话选择相应的消息类型即可
        let targetId = backUseId.toString() //backUseId; //'10001'; // 目标 Id
        RongIMClient.getInstance().sendMessage(conversationType, targetId, _msg, {
            onSuccess: function (message) {
                // message 为发送的消息对象并且包含服务器返回的消息唯一 Id 和发送消息时间戳
                console.log(message);
                 //清空输入框
                _this.editor.txt.html('<p><br/></p>')
                _this.insertInfor('self','',msgHtml)//自己发送信息

            },
            onError: function (errorCode, message) {
                let info = '';
                switch (errorCode) {
                    case RongIMLib.ErrorCode.TIMEOUT:
                        info = '超时';
                        break;
                    case RongIMLib.ErrorCode.UNKNOWN:
                        info = '未知错误';
                        break;
                    case RongIMLib.ErrorCode.REJECTED_BY_BLACKLIST:
                        info = '在黑名单中，无法向对方发送消息';
                        break;
                    case RongIMLib.ErrorCode.NOT_IN_DISCUSSION:
                        info = '不在讨论组中';
                        break;
                    case RongIMLib.ErrorCode.NOT_IN_GROUP:
                        info = '不在群组中';
                        break;
                    case RongIMLib.ErrorCode.NOT_IN_CHATROOM:
                        info = '不在聊天室中';
                        break;
                }
                optPop(()=>{},'发送失败:' + info + errorCode);
            }
        });
    }

     //处理转义符
     htmlRestore = (str) => {
        let s = "";
        if (str.length === 0) {
          return "";
        }
        s = str.replace(/&amp;/g, "&");
        s = s.replace(/&lt;/g, "<");
        s = s.replace(/&gt;/g, ">");
        s = s.replace(/&nbsp;/g, " ");
        s = s.replace(/&#39;/g, "\'");
        s = s.replace(/&quot;/g, "\"");
        return s;
      }

    //发送图片
    sendImg(imgMsg,ImgBase){
        let _this = this;
        const RongIMLib = this.props.RongIMLib
        let {backUseId,id} = this.props.dealInforList;
        let msg = new RongIMLib.ImageMessage({content: ImgBase ,imageUri:imgMsg, extra:id.toString()});
        let conversationType = RongIMLib.ConversationType.PRIVATE; // 单聊, 其他会话选择相应的消息类型即可
        let targetId = backUseId.toString();//backUseId // 目标 Id
        RongIMClient.getInstance().sendMessage(conversationType, targetId, msg, {
            onSuccess: function (message) {
                // message 为发送的消息对象并且包含服务器返回的消息唯一 Id 和发送消息时间戳
                console.log(message);
                
                let imgHtml = `<img src=${'data:image/jpeg;base64,' + ImgBase} data=${imgMsg} style="max-width:100%;" />`
                _this.insertInfor('self','',imgHtml)
                setTimeout(()=>{
                    _this.initScrollBar();
                }, 500)
            },
            onError: function (errorCode, message) {
                let info = '';
                switch (errorCode) {
                    case RongIMLib.ErrorCode.TIMEOUT:
                        info = '超时';
                        break;
                    case RongIMLib.ErrorCode.UNKNOWN:
                        info = '未知错误';
                        break;
                    case RongIMLib.ErrorCode.REJECTED_BY_BLACKLIST:
                        info = '在黑名单中，无法向对方发送消息';
                        break;
                    case RongIMLib.ErrorCode.NOT_IN_DISCUSSION:
                        info = '不在讨论组中';
                        break;
                    case RongIMLib.ErrorCode.NOT_IN_GROUP:
                        info = '不在群组中';
                        break;
                    case RongIMLib.ErrorCode.NOT_IN_CHATROOM:
                        info = '不在聊天室中';
                        break;
                }
                optPop(()=>{},'发送失败:' + info + errorCode);
            }
        });
    }

    //获取聊天记录
    getTalkHistory(){
        let that = this;
        const RongIMLib = this.props.RongIMLib
        const {formatMessage} = this.props.intl
        let {backUseId,id} = this.props.dealInforList;
        let _this = this;
        let conversationType = RongIMLib.ConversationType.PRIVATE; //单聊, 其他会话选择相应的消息类型即可
        let targetId = backUseId.toString();//  backUseId  上线放开￥￥￥￥￥￥￥ // 想获取自己和谁的历史消息，targetId 赋值为对方的 Id
        //console.log(backUseId)
        let timestrap = null; // 默认传 null，若从头开始获取历史消息，请赋值为 0, timestrap = 0;
        let count = 20; // 每次获取的历史消息条数，范围 0-20 条，可以多次获取
        RongIMLib.RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, timestrap, count, {
            onSuccess: function(list, hasMsg) {
                console.log(list);
                
                let _talkList = []
                list.map((item,index) =>{
                    // let id = backUseId == item.senderUserId ?  item.objectName == 'OTC:OrderMsg'? 'user' : 'self'
                    let iden = null
                    if(item.objectName == 'OTC:OrderMsg'){
                        iden = 'system'
                    }else{
                        if(backUseId == item.senderUserId){
                            iden = 'user'

                        }else{
                            iden = 'self'
                        }
                    }
                    if(item.messageType == 'TextMessage' && item.objectName !== 'OTC:OrderMsg'){
                         let _content = RongIMLib.RongIMEmoji.emojiToSymbol(item.content.content);
                         let _html = `<p>${_content}</p>`
                         let  _obj = _this.insertInfor(iden ,item.sentTime, _html,true);
                         _talkList.push(_obj)
                    }
                    if(item.messageType == 'ImageMessage'){
                        let _html =  `<img src=${'data:image/jpeg;base64,' + item.content.content} data=${item.content.imageUri} style="max-width:100%;" />`
                        let  _obj = _this.insertInfor(iden, item.sentTime , _html,true)
                        _talkList.push(_obj)
                    }
                    if(item.objectName == 'OTC:OrderMsg' ){
                        if(item.content.message.content.extra == id){
                            let _html = `<h3 style="font-size:16px;color:#fff;">${formatMessage({id:item.content.message.content.title})}<h3/>
                                         <pre>${formatMessage({id:item.content.message.content.content})}<pre/>`
                            let  _obj = _this.insertInfor(iden ,item.sentTime, _html,true)
                            _talkList.push(_obj)
                        }
                        //let _change = RongIMLib.RongIMEmoji.emojiToHTML(item.content.content);

                    }
                })
                // console.log(_talkList);

                _this.setState({
                    talkInfor:[..._talkList , ..._this.state.talkInfor],
                    // onChangeScroll: true //试了试这个方式也行
                },()=>{
                    if(!hasMsg) {
                        setTimeout(()=>{
                            that.initScrollBar();
                        }, 100);
                    }
                });
                if(hasMsg){
                    _this.getTalkHistory()
                }else{
                    _this.props.isGetTalkHistoryEvent(false)
                }
            },
            onError: function(error) {
                console.log('GetHistoryMessages, errorcode:' + error);
            }
        });
    }


    //获取内容
    async sendEnterInfor(){
        let {editorContent,talkInfor} = this.state;
        let {dealStatue} = this.props.dealInforList
        let stopTalk = dealStatue == 'pass' || dealStatue == 'cancel'
        let isInBalck  = await this.checkoutOrderStatus()
        console.log(isInBalck);
        
        let {formatMessage} = this.props.intl;
        let _imgCheck = /img/;
        let _htmlCheck = _imgCheck.test(this.editor.txt.html())
        let _textContent = this.editor.txt.text().trim();
        _textContent = _textContent.replace(/&nbsp;/ig, "");
        //console.log(this.editor.txt.text());


        if((_textContent.trim() && !_htmlCheck) && this.props.isConnet && !stopTalk && !isInBalck ){

            this.sendTxt(this.editor.txt.text(),this.editor.txt.html()) //发送文本

        }else{
            if(isInBalck){
                this.stopSend(isInBalck)
            }
        }
        //清空输入框
         //this.editor.txt.clear()
         //this.editor.txt.html('<p><br/></p>')
        // let _html = parseDom(editorContent)

    }

    //插入信息流
    insertInfor(identity,time = '',msgHtml="",isHistory = false){
        let {talkInfor} = this.state;
        let _time = null;
        if(identity == 'self' && !time){
            _time =  moment().format(DATA_NOW_FORMAT)
        }else{
            if(moment(time).format(DATA_DAY_FORMAT) == moment().format(DATA_DAY_FORMAT)){//是否同一天
                _time = moment(time).format(DATA_NOW_FORMAT)
            }else{
                if(Cookie.get('zlan') == 'en'){
                    _time =  moment(time).format(DATA_MIMUTE_FORMAT_EN)
                }else{
                    _time =  moment(time).format(DATA_MIMUTE_FORMAT)
                }

            }
        }
        let _userObj = Object.assign({},talkInfor[0],{
            userType:identity,//self 自己 & user 对面 & system 系统推送
            content:msgHtml,
            time: _time,
            id:Math.random(),

        })

        //let _talkInfor = talkInfor.concat(_userObj);
        if(isHistory){
            return _userObj
        }
        this.setState({
            talkInfor:talkInfor.concat(_userObj),
            onChangeScroll:true,
            clearText:false

        },() =>{
        })
    }
    initScrollBar = () => {
        let childH = document.getElementsByClassName('scrollarea-content')[1].clientHeight;
        let content = document.getElementsByClassName('talkContent')[0].clientHeight;
        let margin = parseInt(childH - content + 32);
        if(margin > 0 ){
            this.refs.scrollerBar.scrollArea.scrollYTo(margin);
        }
    };
    //连接失败后不能发送信息
    stopSend(msg){
        optPop(()=>{},msg,{timer:3000},true)
    }


    keepBottom(v){
        let {onChangeScroll}  = this.state;
        let {containerHeight,realHeight} = v;
        if(onChangeScroll){
            if(realHeight  >  containerHeight){
                v.topPosition =  realHeight -  containerHeight;
                this.setState({
                    onChangeScroll:false
                })
            }

        }
    }

    handleEnterKey = (e) => {
        if(e.keyCode === 13){
            this.sendEnterInfor();
        }
    }

     upload = async (e) =>{
        //console.log(e.base64);
        //this.sendImg(e.base64,e.base64)
        let _base64Change = await this.compress(e.base64,180,0.5)
        //console.log(_base64Change); 
        let {dealStatue} = this.props.dealInforList
        let stopTalk = dealStatue == 'pass' || dealStatue == 'cancel'
        let isInBalck  = await this.checkoutOrderStatus()
        //console.log(isInBalck);
        

        if(this.props.isConnet && !isInBalck && !stopTalk){
            axios.post(confs.BBAPI + '/manage/auth/uploadToken').then(res =>{
                if(res.data.isSuc){
                    const { datas:info } = res.data;
                    const fd = new FormData();
                    fd.append('key', info.key);
                    fd.append('Signature', info.token);
                    fd.append('file',e.file);
                    axios.post(info.host, fd).then(r => {
                        this.sendImg(info.host + info.key , _base64Change)
                    }).catch(err =>{

                    });
                }

            })
        }else{
            if(isInBalck){
                this.stopSend(isInBalck)
            }
        }

    }
    uploadSuccress = (res) =>{
        console.log(res);

    }
    emjiClick = (code) =>{
        //console.log(code);
        if(this.editor.txt.text()){
            this.editor.txt.append(`<span>&#x${code.replace('u', '')};</span>`)
        }else{
            this.editor.txt.html(`<span>&#x${code.replace('u', '')};</span>`)
        }

    }

    isShowEmji = (e) =>{
        this.setState({
            showEmji:true
        })
        e.nativeEvent.stopImmediatePropagation();
    }

    hideEmji = (e) =>{
        if(e.srcElement.className!="itemEmji"){
            this.setState({
                showEmji:false
            })
        }
       
    }



    //图片压缩
     compress =(base64String, w, quality) => {
        var getMimeType = function (urlData) {
                var arr = urlData.split(',');
                var mime = arr[0].match(/:(.*?);/)[1];
                // return mime.replace("image/", "");
                return mime;
            };
            var newImage = new Image();
            var imgWidth, imgHeight;
     
            var promise = new Promise(resolve => newImage.onload = resolve);
            newImage.src = base64String;
            return promise.then(() => {
                imgWidth = newImage.width;
                imgHeight = newImage.height;
                var canvas = document.createElement("canvas");
                var ctx = canvas.getContext("2d");
                if (Math.max(imgWidth, imgHeight) > w) {
                    if (imgWidth > imgHeight) {
                        canvas.width = w;
                        canvas.height = w * imgHeight / imgWidth;
                    } else {
                        canvas.height = w;
                        canvas.width = w * imgWidth / imgHeight;
                    }
                } else {
                    canvas.width = imgWidth;
                    canvas.height = imgHeight;
                }
                ctx.clearRect(0, 0, canvas.width, canvas.height);
                ctx.drawImage(newImage, 0, 0, canvas.width, canvas.height);
                var base64 = canvas.toDataURL(getMimeType(base64String), quality);
                var index =  base64.search(";base64,");
                base64 = base64.slice(index+8,base64.length)
                //console.log(base64);
                return base64;
            });
    
       }
    //点击聊天记录
    domClick = (e) =>{
        //console.log(e.target.height)
        if(e.target.src){
            let _imgData = e.target.getAttribute('data');
            
            let Mstr = <div className="openImg" style = {{padding:"20px",maxHeight:'700px',maxWidth:'1200px'}} >
                <img style={{maxHeight:'650px',maxWidth:'1000px'}} src={_imgData} alt=""/>
                <span onClick={() =>this.modal.closeModal()}><img src={closeImg} alt=""/></span>
            </div>
            this.setState({
                Mstr
            },() =>{
                this.modal.openModal();
            })
        }
    }

    //是否黑名单
    checkoutOrderStatus = () =>{
        const {backUseId,dealStatue} = this.props.dealInforList
        return new Promise((resolve,reject) =>{
            post('/web/common/checkBlackUser',{targetUserId:backUseId}).then((res) =>{
                if(res.code == 200){
                     resolve(res.data) 
                }else{
                     resolve(false)
                }
            })
        }
            
        ) 
        // //判断订单状态
        // let stopTalk =  dealStatue == 'pass' || dealStatue == 'cancel'

        // this.setState({
        //     stopTalk
        // })
    }

    render(){
        const {talkInfor,clearText,inforH,imgV,showEmji,stopTalk,enterH} = this.state;
        const {formatMessage} = this.props.intl;
        const {backUseColor,selfUserColor,userInfor,backNickname,dealStatue} = this.props.dealInforList
        const {nickname,emojiList,RongIMLib} = this.props
        return(
            <div className="talkRoom f-14" style={{ height: 'calc(100% - 50px)' }}>
                <div className="talkContent" >
                    <ReactScroll  ref="scrollerBar" className="scrollareas" style={{height:inforH + 'px'}} onScroll={(value) => {this.keepBottom(value)}}>
                        <ul>
                            {
                                talkInfor.map((item,index) =>{
                                    if(item.userType == 'system'){
                                        return <li className="system">
                                            <p className="times">{item.time}</p>
                                            <div dangerouslySetInnerHTML = {{__html: item.content}}></div>
                                        </li>
                                    }else{
                                        return (
                                            <li className={item.userType == 'self'?"selfUser":"otherUser"} key={item.id}>
                                                <div className={item.userType == 'self'?"flex-r flexRev":'flex-r'}>
                                                    <div className="talkImg" style={{backgroundColor:item.userType == 'self' ? selfUserColor:backUseColor}}>
                                                        {item.userType == 'self' ? getFirstStr(nickname) : getFirstStr(backNickname)}
                                                    </div>
                                                    <div className={item.userType == 'self'?'contents SelfTalkInfor':'contents OtherTalkInfor'}>
                                                        <p className="times">{item.time}</p>
                                                        <p onClick={(e) => this.domClick(e)} dangerouslySetInnerHTML = {{__html: item.content}}></p>
                                                    </div>
                                                </div>
                                            </li>
                                        )
                                    }

                                })

                            }
                        </ul>
                    </ReactScroll>
                </div>
                <div className="enterContent flex-c">
                    <div className="control_item">
                        <span onClick={(e) => this.isShowEmji(e)} className="iconfont icon-liaotianbiaoqing">
                        {   showEmji &&
                        <section className="emjiSec">
                            <ReactScroll style={{height: '195px'}}>
                                {
                                    emojiList.length >0 &&
                                    emojiList.map((item,index) =>{
                                        return(
                                            <span  className="itemEmji" onClick={() => this.emjiClick(item.unicode)} dangerouslySetInnerHTML= {{__html: `&#x${item.unicode.replace('u', '')};`}}></span>
                                        )
                                    })

                                }
                            </ReactScroll>
                        </section>
                        }
                           
                        </span>
                        <span onClick={this.imgUpload} className="iconfont icon-liaotiantupian">
                            <UploadImage accept="image/png, image/jpeg, image/jpg" ref={this.imgI} name="file" uploadLink="" className="file_img" onChange={(e)=>{this.upload(e)}} uploadSuccress={(res)=>{this.uploadSuccress(res)}} /> 
                        </span>
                        {/* <span class="iconfont icon-liaotianfujian "></span> */}
                    </div>
                    <p className = {clearText?'defultTest none':'defultTest'}>{formatMessage({id:'输入信息，回车发送'})}</p>
                    <div ref="editorElem" id="editors" style={{textAlign: 'left' , height:enterH + 'px'}} className="flex-c"></div>
                    {
                         dealStatue == 'pass' || dealStatue == 'cancel'?
                            <button style={{backgroundColor:'#737A8D',color:'#9199AF',cursor:'default'}} className="otcSureBtn f-r">{formatMessage({id:'发送'})}</button>
                            :
                            <button onClick={this.sendEnterInfor} onKeyDown={this.handleEnterKey} className="otcSureBtn f-r">{formatMessage({id:'发送'})}</button>
                    }
                </div>
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </ReactModal>
            </div>
        )
    }
}



export default withRouter(injectIntl(OctTalkRoom));