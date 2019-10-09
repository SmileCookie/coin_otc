import React from 'react';
import Upload from '../upload/baseUpload';

export default (props) => {

    const {ChatApi, sendMsgObj, emojiList} = props;
    
    ChatApi.RongIMClient.setOnReceiveMessageListener({
        // 接收到的消息
        onReceived: function (message) {
            // 判断消息类型
            switch(message.messageType){
                case RongIMClient.MessageType.TextMessage:
                    console.log(3344, message);
                    // message.content.content => 文字内容
                    break;
                case RongIMClient.MessageType.VoiceMessage:
                    // message.content.content => 格式为 AMR 的音频 base64
                    break;
                case RongIMClient.MessageType.ImageMessage:
                    // message.content.content => 图片缩略图 base64
                    // message.content.imageUri => 原图 URL
                    break;
                case RongIMClient.MessageType.LocationMessage:
                    // message.content.latiude => 纬度
                    // message.content.longitude => 经度
                    // message.content.content => 位置图片 base64
                    break;
                case RongIMClient.MessageType.RichContentMessage:
                    // message.content.content => 文本消息内容
                    // message.content.imageUri => 图片 base64
                    // message.content.url => 原图 URL
                    break;
                case RongIMClient.MessageType.InformationNotificationMessage:
                    // do something
                    break;
                case RongIMClient.MessageType.ContactNotificationMessage:
                    // do something
                    break;
                case RongIMClient.MessageType.ProfileNotificationMessage:
                    // do something
                    break;
                case RongIMClient.MessageType.CommandNotificationMessage:
                    // do something
                    break;
                case RongIMClient.MessageType.CommandMessage:
                    // do something
                    break;
                case RongIMClient.MessageType.UnknownMessage:
                    // do something
                    break;
                default:
                    // do something
            }
        }
    });

    const test = () =>{
        if(sendMsgObj){
        var msg = new ChatApi.TextMessage({ content: 'hello RongCloud!', extra: '附加信息' });
        var conversationType = ChatApi.ConversationType.PRIVATE; // 单聊, 其他会话选择相应的消息类型即可
        var targetId = '333'; // 目标 Id
        sendMsgObj.sendMessage(conversationType, targetId, msg, {
            onSuccess: function (message) {
                // message 为发送的消息对象并且包含服务器返回的消息唯一 Id 和发送消息时间戳
                console.log(message);
            },
            onError: function (errorCode, message) {
                var info = '';
                switch (errorCode) {
                    case ChatApi.ErrorCode.TIMEOUT:
                        info = '超时';
                        break;
                    case ChatApi.ErrorCode.UNKNOWN:
                        info = '未知错误';
                        break;
                    case ChatApi.ErrorCode.REJECTED_BY_BLACKLIST:
                        info = '在黑名单中，无法向对方发送消息';
                        break;
                    case ChatApi.ErrorCode.NOT_IN_DISCUSSION:
                        info = '不在讨论组中';
                        break;
                    case ChatApi.ErrorCode.NOT_IN_GROUP:
                        info = '不在群组中';
                        break;
                    case ChatApi.ErrorCode.NOT_IN_CHATROOM:
                        info = '不在聊天室中';
                        break;
                }
                console.log('发送失败: ' + info + errorCode);
            }
        });
        }
    }

    return (
        <div>
            <Upload />
            {
            <input type="button" value="send" onClick={test} />
            }
            <ul>
                {
                    emojiList.length 
                    ?
                    emojiList.map((v)=>{
                        return (
                            <li dangerouslySetInnerHTML={{__html: `&#x${v.unicode.replace('u', '')};`}}>
                            
                            </li>
                        )
                    })
                    :null
                }
            </ul>
            <textarea>
                &#x1f600;
            </textarea>
        </div>
    )
}