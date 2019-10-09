import React from 'react';
import { Link } from 'react-router-dom';
import cookie from 'js-cookie'
import {withRouter} from 'react-router'
import Headers from '../components/header/header'
import Footer from '../components/footer/footer'
import { connect } from 'react-redux'
import { FormattedMessage,injectIntl } from 'react-intl';
import {getUserBaseInfo,getCoinMarkInfor,getRongToken,saveSocketEvent} from '../redux/module/session'
import {getChatApiEvent,getEmojiEvent,saveReTxtEvent,isConnect,getSysTextEvent} from '../redux/module/chart'
import {fetchRate} from '../redux/module/money'
import {pushGlobalTips} from '../redux/module/tips'
import ScrollArea from 'react-scrollbar'
import {Styles, ThemeFactory} from "../components/transition";
import GlobelTip from '../components/globalTip'
import {post} from '../net'
//import Chat from 'components/chat';
import confs from 'conf';


// 设置title相关的
import setTitle from "../utils/setTitle";
import TitleSet from "../components/setTitle";



const { APPKEY } = confs;
import {requsetWebsocket} from '../utils/index'

//乘法
Number.prototype.mul = function (arg) {
    var m=0,s1=this.toString(),s2=arg.toString();
    try{m+=s1.split(".")[1].length}catch(e){}
    try{m+=s2.split(".")[1].length}catch(e){}
    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m)
}

@connect(
    state => ({
       loading:state.session.loading,
       pageNum:state.session.pageNum,
       topNum:state.session.topNum,
       coinData:state.session.coinData,
       userInfor:state.session.userInfor,
       count:state.chart.count,
       rongToken: state.session.rongToken

    }),
   {
        getUserBaseInfo,
        getCoinMarkInfor,
        getChatApiEvent,
        getEmojiEvent,
        saveReTxtEvent,
        isConnect,
        pushGlobalTips,
        getSysTextEvent,
        fetchRate,
        saveSocketEvent
        //getRongToken
   }
)

class APP extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            maxheight:document.documentElement.clientHeight,
            //msgList:'有新消息'
        };
        this.ws = null
        this.initSDK = this.initSDK.bind(this)
        this.listenStatus = this.listenStatus.bind(this)
        this.connect = this.connect.bind(this)
        this.getRongToken = this.getRongToken.bind(this)
        this.isSystemMsg = this.isSystemMsg.bind(this)
        this.requsetWebsocketEvent = this.requsetWebsocketEvent.bind(this)
    }
    componentWillMount(){
        this.props.getUserBaseInfo()
        //this.props.getRongToken()
        this.props.getCoinMarkInfor()
        //console.log('===============' + document.referrer);

    }
    componentDidMount() {

        //全局滚动设置
        document.documentElement.style.overflow='hidden';
        window.onresize= () => {
            let _len =  document.documentElement.clientHeight
            this.setState({
                    maxheight: _len + 'px'
                })
        }
        this.getRongToken()

        //获取汇率
        this.props.fetchRate()


    }

    componentWillReceiveProps(nextProps){
        //改变page滚动到指定位置
        if(nextProps.pageNum !== this.props.pageNum){
             this.refs.scrollerBar.scrollArea.scrollYTo(nextProps.topNum);
        }
         //websocket
        if(nextProps.userInfor.code !== this.props.userInfor.code && nextProps.userInfor.data && cookie.get("zloginStatus")!=4){
            console.log('websocet begin ++++++++++++++');
            this.requsetWebsocketEvent()
        }
    }

    componentWillUnmount(){

        clearInterval(this.otcSetTime)
    }

    //获取融云token
    getRongToken(){
        post('/api/v1/user/registerRongCloud').then(res =>{
            if(res.code == 200){
                this.initSDK(res.data.rongCloudToken)
            }
        })
    }

    //融云初始化
    initSDK(token = ""){
        import('../lib/chatSDK').then(() =>{
            let _this = this;
             // 异步加载融云的SDK
            // 初始化
            RongIMLib.RongIMClient.init(APPKEY);
             // 监控链接状态
             this.listenStatus(RongIMClient);
             //连接服务器
             this.connect(RongIMClient,token)

             //储存融云API
             this.props.getChatApiEvent(RongIMLib)

             //获取表情库
             import('../lib/chatEmoji').then(() =>{
                RongIMLib.RongIMEmoji.init();
                // 表情信息可参考 http://unicode.org/emoji/charts/full-emoji-list.html
                const config = {
                    size: 24, // 大小, 默认 24, 建议18 - 58
                    url: '//f2e.cn.ronghub.com/sdk/emoji-48.png', // Emoji 背景图片
                    lang: 'zh', // Emoji 对应名称语言, 默认 zh
                    // 扩展表情
                    extension: {
                        dataSource: {
                            u1F914: {
                                en: 'thinking face', // 英文名称
                                zh: '思考', // 中文名称
                                tag: '🤔', // 原生 Emoji
                                position: '0 0' // 所在背景图位置坐标
                            }
                        },
                        url: '//cdn.ronghub.com/thinking-face.png' // 新增 Emoji 背景图 url
                    }
                };
                RongIMLib.RongIMEmoji.init(config);
                console.log(RongIMLib.RongIMEmoji.list);
                _this.props.getEmojiEvent(RongIMLib.RongIMEmoji.list)

            })
        })

    }

    //监听融云事件
    listenStatus(RongIMClient){
        let _this = this;
        //连接状态监听器
        RongIMClient.setConnectionStatusListener({
            onChanged: function (status) {
                // status 标识当前连接状态
                switch (status) {
                    case RongIMLib.ConnectionStatus.CONNECTED:
                        console.log('链接成功');
                        _this.props.isConnect(true)
                        break;
                    case RongIMLib.ConnectionStatus.CONNECTING:
                        console.log('正在链接');
                        break;
                    case RongIMLib.ConnectionStatus.DISCONNECTED:
                        console.log('断开连接');
                        break;
                    case RongIMLib.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT:
                        console.log('其他设备登录');
                        _this.props.isConnect(false)
                        break;
                    case RongIMLib.ConnectionStatus.DOMAIN_INCORRECT:
                        console.log('域名不正确');
                        _this.props.isConnect(false)
                        break;
                    case RongIMLib.ConnectionStatus.NETWORK_UNAVAILABLE:
                        console.log('网络不可用');
                        _this.props.isConnect(false)
                        break;
                }
            }
        });
         // 消息监听器
         RongIMClient.setOnReceiveMessageListener({
            // 接收到的消息
                onReceived: function (message) {
                    // 判断消息类型
                    switch(message.messageType){
                        case RongIMClient.MessageType.TextMessage:
                            //保存到redux

                            if(!message.offLineMessage){
                                _this.props.saveReTxtEvent(message)
                            }//判断是否离线消息
                            console.log(message);
                            // message.content.content => 文字内容
                            break;
                        case RongIMClient.MessageType.VoiceMessage:
                            // message.content.content => 格式为 AMR 的音频 base64
                            break;
                        case RongIMClient.MessageType.ImageMessage:
                                console.log(message);
                                if(!message.offLineMessage){//判断是否离线消息
                                    _this.props.saveReTxtEvent(message)
                                }
                            // message.content.content => 图片缩略图 base64
                            // message.content.imageUri => 原图 URL
                            break;
                        case RongIMClient.MessageType.LocationMessage:
                                console.log(message);
                            // message.content.latiude => 纬度
                            // message.content.longitude => 经度
                            // message.content.content => 位置图片 base64
                            break;
                        case RongIMClient.MessageType.RichContentMessage:
                                console.log(message);
                            // message.content.content => 文本消息内容
                            // message.content.imageUri => 图片 base64
                            // message.content.url => 原图 URL
                            break;
                        case RongIMClient.MessageType.InformationNotificationMessage:
                                console.log(message);
                            // do something
                            break;
                        case RongIMClient.MessageType.ContactNotificationMessage:
                                console.log(message);
                            // do something
                            break;
                        case RongIMClient.MessageType.ProfileNotificationMessage:
                                console.log(message);
                            // do something
                            break;
                        case RongIMClient.MessageType.CommandNotificationMessage:
                                console.log(message);
                            // do something
                            break;
                        case RongIMClient.MessageType.CommandMessage:
                                console.log(message);
                            // do something
                            break;
                        case RongIMClient.MessageType.UnknownMessage:
                                console.log(message);
                                 //判断是否为系统消息
                                //_this.isSystemMsg(message)
                                //储存redux 系统消息
                                if(!message.offLineMessage){
                                    _this.props.getSysTextEvent(message)
                                }

                            // do something
                            break;
                        default:
                            // do something
                    }
                }
        });

    }

    //连接融云服务器
    connect(RongIMClient,rongCloudToken){
        //const {rongToken} = this.props
        // let rongCloudToken = 'OScZpvGNPVdZX2ULYVPp4Q2oevW1prkPTV23QKGKQuvpH8pLmRNYwv6c055GCA+HGVngiivL+nQbsb/IYj29bQ==';
        const _this = this;
        RongIMClient.connect(rongCloudToken, {
            onSuccess: function(userId) {
                console.log('Connect successfully. ' + userId, '@@@2');
                _this.props.isConnect(true)
            },
            onTokenIncorrect: function() {
                console.log('token 无效');
                _this.props.isConnect(false)
            },
            onError: function(errorCode){
                var info = '';
                switch (errorCode) {
                    case RongIMLib.ErrorCode.TIMEOUT:
                        info = '超时';
                        break;
                    case RongIMLib.ConnectionState.UNACCEPTABLE_PAROTOCOL_VERSION:
                        info = '不可接受的协议版本';
                        break;
                    case RongIMLib.ConnectionState.IDENTIFIER_REJECTED:
                        info = 'appkey不正确';
                        break;
                    case RongIMLib.ConnectionState.SERVER_UNAVAILABLE:
                        info = '服务器不可用';
                        break;
                }
                console.log(info);
                _this.props.isConnect(false)
            }
        });
    }

    //系统消息
    isSystemMsg(msg){
        let content = msg.message;
        let extra = msg.recordId;
        let _obj = {
                    content,
                    extra
                }
        this.props.pushGlobalTips(_obj)
        // if(msg.objectName == 'OTC:OrderTipsMsg'){
        //     let {title} = msg.content.message.content;
        //     let {content} = msg.content.message.content;
        //     let {extra}   = msg.content.message.content;
        //     let _obj = {
        //         title,
        //         content,
        //         extra
        //     }
        //     this.props.pushGlobalTips(_obj)
        // }
    }

    //连接ws
    requsetWebsocketEvent(){
        let sessionId = cookie.get("zsessionId")
        requsetWebsocket(
            { "event": "sub","channel": "otc.order.deal","params":{"token":sessionId,"type":"web"}},
            (res) => {
                console.log(res)
                if(res.code == '0000'){
                    this.props.saveSocketEvent(this.ws)
                    this.asyncCode = true;
                }
                if(this.asyncCode && res.data){
                    //debugger
                    if(res.channel == 'otc.order.deal'){
                        let msg = JSON.parse(res.data);
                        console.log(msg);
                        this.isSystemMsg(msg)
                    }
                }
            },
            this
        )
    }

    render() {
        const {loading} = this.props
        const {maxheight} = this.state
        let titleval =  setTitle(this.props.location.pathname);
       
        return (
                loading ? //缓存页面防止闪动
                <div></div>
                :
                <ScrollArea ref="scrollerBar" style={{maxHeight:maxheight}}>
                  <div>
                    <Headers/>
                    <div id="otc-content" style={{ minHeight: 'calc(100vh - 330px)' }}>
                        {this.props.cd}
                    </div>
                    <GlobelTip/>
                    <Footer/>
                     { <TitleSet titleval={titleval} />}
                </div>
                </ScrollArea>

            )
    }
}

export default APP;
