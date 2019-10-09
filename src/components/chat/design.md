1、入口
<Chat />
2、配置文件
APPKEY 上线需要替换
3、class Chat 获取 用户信息 userInfor uid+昵称+portraitUri 生成的token 
4、初始化 initSDK
    1、异步加载SDK
    2、使用APPKEY初始化
    3、开启监控
    4、删除connect 此处不需要因为这个需要在获取了基础用户信息才能做。
    5、异步加载emoji
5、componentWillReceiveProps
    1、消费userInfor 来初始化连接。
    2、消费过一次就不在消费

6、带着ChatApi sendMsgObj emojiList 渲染ChatBase
7、sendMsgObj存在才能发消息出去 
8、emojiList 图标表情列表