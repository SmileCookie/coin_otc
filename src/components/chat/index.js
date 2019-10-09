import React from 'react';
import { connect } from 'react-redux';
import ChatBase from './chatBase';

import confs from 'conf';

const { APPKEY } = confs;

@connect(
    state => ({
        userInfor:state.session.userInfor,
    })
)
class Chat extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            // 融云句柄
            ChatApi: null,
            // 获取可以发送消息的句柄
            sendMsgObj: null,
            // 表情
            emojiList: [],
        }

        // 是否已经链接过
        this.isConnect = 0;

        this.initSDK = this.initSDK.bind(this);
    }
    componentDidMount(){
        // 初始化融云获取句柄
        this.initSDK();
        //console.log('-------------->>>');
    }

    // 获取token后进行链接融云服务器
    componentWillReceiveProps(nprops){
        //console.log(nprops,'32234234234');
        if(!this.isConnect && nprops.userInfor.datas){
            this.connect(this.state.ChatApi);
            this.isConnect = 1;
        }
    }

    // 初始化融云sdk
    initSDK(){
        import('../../lib/chatSDK').then(()=>{
            // 异步加载融云的SDK
            // 初始化
            RongIMLib.RongIMClient.init(APPKEY);
            // 监控链接状态
            this.listenStatus(RongIMClient);
            
            // 线上需要注释此句
            this.connect(RongIMClient);

            // 设置句柄
            this.setState({
                ChatApi: RongIMLib,
            })

            // 获取Emoji
            return import('../../lib/chatEmoji');
           
        }).then(() => {
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

            this.setState({
                emojiList: RongIMLib.RongIMEmoji.list
            });

            //this.forceUpdate();
        });
    }
    
    // 链接
    connect(RongIMClient){
        const {token = 'WE+6B6A6mdkUmvjimf+NweeS2SSNwMDnQXSmRxe1Fv7xxytChvcIzJCm9vd3tPo2/vxIQ78sHa9yFQiLsA2kbg=='} = (this.props.userInfor.datas || {});
        const _this = this;
        RongIMClient.connect(token, {
            onSuccess: function(userId) {
                //console.log('Connect successfully. ' + userId, '@@@2');
                // 发送一条测试消息
                // setTimeout(()=>{
                //     _this.testMsg();
                // },5000)
                _this.setState({
                    sendMsgObj: RongIMClient.getInstance()
                });
            },
            onTokenIncorrect: function() {
                console.log('token 无效');
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
            }
        });
    }

    // 监控链接状态
    listenStatus(RongIMClient){
        RongIMClient.setConnectionStatusListener({
            onChanged: function (status) {
                // status 标识当前连接状态
                switch (status) {
                    case RongIMLib.ConnectionStatus.CONNECTED:
                        console.log('链接成功');
                        break;
                    case RongIMLib.ConnectionStatus.CONNECTING:
                        console.log('正在链接');
                        break;
                    case RongIMLib.ConnectionStatus.DISCONNECTED:
                        console.log('断开连接');
                        break;
                    case RongIMLib.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT:
                        console.log('其他设备登录');
                        break;
                    case RongIMLib.ConnectionStatus.DOMAIN_INCORRECT:
                        console.log('域名不正确');
                        break;
                    case RongIMLib.ConnectionStatus.NETWORK_UNAVAILABLE:
                        console.log('网络不可用');
                        break;
                }
            }
        });
    }

    render(){
        const {ChatApi, sendMsgObj, emojiList} = this.state;
        return(
            <div>
                {
                ChatApi
                ?
                <ChatBase ChatApi={ChatApi} sendMsgObj={sendMsgObj} emojiList={emojiList} />
                :
                null
                }
            </div>
        )
    }
}

export default Chat;