# 币币平台的组件文档。
## Strength
### 参数
1. val 当前文本框的值。
2. funct 向外输出当前的密码强度。注意只是输出了当前的强度值，展示效果封装在组件内部。
### demo 登录页登录密码

## Sms
### 参数
1. mCode 手机验证码。
2. mobile 手机号。
<code>
mCode，mobile 要么同时出现要么不要出现。成对出现或者不成对出现。
不允许单独之一出现否则报请输入手机号。
<Sms {...{mCode, mobile, codeType}} fn={(k, v)=>{this.callError(k, v)}} />
</code>
3. codeType
每种业务都对应自己的业务码，比如设置手机号和二次验证的获取，获取短信验证码业务码是不同的，将来如果别的业务一定找服务端明确。
4. fn
<code>
获取验证码后调用外边传进来的回调，传入获取结果
参数1 字段索引
参数2 错误描述
参数3 报文体
fn(KEY, res.des, res);
fn(KEY, '', res);
fn('mobile', this.props.intl.formatMessage({id:'nuser92'}));
</code>
5. sendUrl 这个很简单就是去哪个地址请求。
6. errorKey 这个就是在哪个字段下面显示错误。
7. otherData 这个算是后期优化的产物，就是把需要的数据以对象的形式扔进来然后发到服务端。
8. isCk 是否启用验证如果0 点击没有任何的效果，如果1才会验证。 
9. getCkFn 和上面一样只不过这个是函数的形式true验证
10. clearFn 这个为了应对读秒结束后需要对一些状态的恢复
### demo 用户中心设置手机号，各种验证开启设置都有。

## AllWithdraw 组件咱无应用场景不介绍。

## HTab 选择tab
### 参数
1. list 所有标题的对象集合{tith,link}
2. currentFlg 当前选中哪个
3. setSelected 当选中的那个返回传给订阅者。
### demo 忘记密码模块

## Crumbs 外导航 根据路由渲染面包屑
## Nav 用户中心的导航 根据传入数据渲染面包屑
### 参数
1. path 当前路径
2. ay 需要显示的面包屑
<code>
Crumbs
static propTypes = {
        path: PropTypes.string.isRequired,
        ay: PropTypes.array.isRequired,
    }
Nav
path: PropTypes.string.isRequired,
        ay: PropTypes.arrayOf(
            PropTypes.shape({
                name: PropTypes.string.isRequired,
                link: PropTypes.string.isRequired,
            })
        ).isRequired,
<Crumbs path={this.props.location.pathname} ay={this.props.routes} />
<Nav path={this.props.location.pathname} ay={USERCENTERTAB} />
</code>
### demo 用户中心

## Confirm 确认吐司
### 参数
1. msg 提示消息文字
2. cancel 取消按钮文字
3. ok 确认按钮文字 
4. cb 点击了取消还是确定0取消1确认回调。
5. isNotCancel 是否需要取消。
### demo 用户中心首页

## Opt 谷歌短信认证的弹出
### 参数
1. status 暂无作用
2. msg 消息体1
3. msg2 消息体2
4. ft  按钮名字
5. closeCb 关闭后的回调等待订阅。

## CheckBox、Radio 复选框
### 参数
1. isCk 是否选中默认不选中。
2. setCk 返回当前选中与否的状态，等待被订阅。
### demo 注册 test

## Select 选择框
### 参数
1. list 选择框的填充数据
2. currentCode 当前选中的
3. getCode 返回选中的值
### demo 身份验证


