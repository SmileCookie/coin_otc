import React from 'react';
import { connect } from 'react-redux';
import cookie from 'js-cookie';
import PropType from 'prop-types'
import { fetchUserinfo, setLang } from '../redux/modules/session';
import { fetchMarketsConf } from '../redux/modules/marketsconf';
import { fetchRate } from '../redux/modules/money';
import CT from '../components/context/index';
import Header from './common/header/headerContainer';
import Footer from './common/footer/footerContainer';
import TradeTip from './common/tradeTip'
import SafeAuthWin from './common/safeAuthWin/safeAuthWin'
import {COOKIE_GOOGLE_AUTH,COOKIE_IP_AUTH} from '../conf/index'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import {DISMISS_TIME} from '../conf/index'
import ErrorComponent from './common/ErrorComponent'

import{requsetWebsocket} from '../utils'
import { startWebSocket } from '../redux/modules/socket';
import {pushGlobalTips} from '../redux/modules/otcTips'
import GlobelTip from './common/globalTip'
const { notifSend } = notifActions;
import ScrollArea from 'react-scrollbar'


import '../assets/js/iconfont';
import '../assets/css/bootstrap.css'
import '../assets/css/common.css'
import '../assets/css/iconfont'
import '../assets/css/font/iconfont.js'
import '../assets/css/dgq.less'
import '../assets/css/table.less'

class App extends React.Component {

    constructor(props){
        super(props)
        this.state = {
            googleAuth:false,
            ipAuth:false,
            isShow:false,
            maxheight:document.body.clientHeight + 'px',
        }
        // this.addPswInput = this.addPswInput.bind(this);
        // this.makeArry = this.makeArry.bind(this);
        this.socketHandle = null;
        this.selectTabBrow = this.selectTabBrow.bind(this);
    }

    selectTabBrow(e){
        // 只要切换了tab或者不在可是局域全局记录，不记录redux了
        window.isvisibilitychange = true;
        window.isvisibilitychangeTradeHistory = true;

        if(this.socketHandle){
            if('visible' === document.visibilityState){
                // console.log('start');
                // 开启
                this.socketHandle = this.props.startWebSocket();
            } else {
                // 关闭
                this.socketHandle();
            }
        }
    }

    componentDidMount() {



        // 进入应用，开启全栈唯一一条webscoket，其余业务请自行定于自己相关的内容。
        this.socketHandle = this.props.startWebSocket();

        // 当不在当前窗口的时候，干掉socket链接
        document.addEventListener('visibilitychange', this.selectTabBrow);

        // window.onerror = (e) => {
        //     if(window.location.href.includes("isdebug")){
        //         console.log(e);
        //     }
        //     return true;
        // }
        this.props.fetchRate()
        this.props.fetchSession()
        this.props.fetchMarketsConf()
        this.checkIsNeedSafeAuth()

        //禁止全局滚动
        document.documentElement.style.overflow='hidden';
        window.onresize= () => {
            let _len =  document.body.clientHeight
            this.setState({
                    maxheight: _len + 'px'
                })
           }
        //    document.addEventListener('touchstart', function(event) {
        //     // 判断默认行为是否可以被禁用
        //     if (event.cancelable) {
        //         // 判断默认行为是否已经被禁用
        //         if (!event.defaultPrevented) {
        //             event.preventDefault();
        //         }
        //     }
        // }, false);
        // this.interval = setInterval(() => this.props.fetchRate(),1000)


    }
    componentWillUnmount(){
        // 释放资源
        document.removeEventListener('visibilitychange', this.selectTabBrow)
        clearInterval(this.interval)
    }

    componentWillReceiveProps(nextProps){
        //改变page滚动到指定位置
        if(nextProps.goTopCount !== this.props.goTopCount){
            this.refs.scrolls.scrollArea.scrollYTo(nextProps.goTopNum);
        }
         //OTC websocket
         if(nextProps.user !== this.props.user && nextProps.user && cookie.get("zloginStatus")!=4 &&  !nextProps.location.pathname.includes("loginAuthRoute")){
                //console.log(nextProps);
                this.requsetWebsocketEvent()

         }



    }

    checkIsNeedSafeAuth(){
        let googleAuth = cookie.get(COOKIE_GOOGLE_AUTH)
        let ipAuth = cookie.get(COOKIE_IP_AUTH)
        this.setState({
            googleAuth:googleAuth || false,
            ipAuth:ipAuth || false
        })
        if(googleAuth=='true' || ipAuth=='true'){
            this.setState({
                isShow:true
            })
        }
    }
     //连接otc ws
     requsetWebsocketEvent(){
        let sessionId = cookie.get("zsessionId")
        requsetWebsocket(
            { "event": "sub","channel": "otc.order.deal","params":{"token":sessionId,"type":"web"}},
            (res) => {
                console.log(res);

                if(res.code == '0000'){
                    this.asyncCode = true;
                }
                if(this.asyncCode && res.data){
                    if(res.channel == 'otc.order.deal'){
                        let msg = JSON.parse(res.data);
                        //console.log(msg);
                        this.isSystemMsg(msg)
                    }
                }
            },
            this,
        )

    }
    //otc系统消息
    isSystemMsg(msg){
        let content = msg.message;
        let extra = msg.recordId;
        let _obj = {
                    content,
                    extra
                }
        this.props.pushGlobalTips(_obj)
    }

    render() {
        const { isLoaded,user} = this.props;
        const {maxheight} = this.state;
        if(!isLoaded){
            return <div></div>
        }

        return (
            <ErrorComponent>

                    <ScrollArea ref="scrolls" className="firstScroll" style={{maxHeight:maxheight}}>
                      <Content>
                        <div>
                            {
                                user?(
                                    <SafeAuthWin  isShow = {this.state.isShow} googleAuth={this.state.googleAuth} ipAuth={this.state.ipAuth} notifSend = {this.props.notifSend} />
                                ):('')
                            }
                            <Header />
                            {(user&&cookie.get("zloginStatus")!=4) && <TradeTip location={this.props.location} />}
                                {this.props.children}

                            {/* <button onClick={this.changeTop}></button> */}
                            {/* otc交易 消息弹窗 */}
                            <GlobelTip/>
                            <Footer />

                            <Notifs transitionLeaveTimeout={1} />
                            {/* 在路由改变的方法中定义的全局title */}


                        </div>

                      </Content>

                    </ScrollArea>
            </ErrorComponent>
        )
    }
}

class Content extends React.Component {
    constructor(){
        super()
        this.ThemeContext = CT();
        this.handleSomeAction = this.handleSomeAction.bind(this);
    }
    render(){
        const ThemeContext = this.ThemeContext
        return (
           <div>
               <ThemeContext.Provider value={this.handleSomeAction}>{this.props.children}</ThemeContext.Provider>
           </div>
        );
    }

    handleSomeAction(){
        this.context.scrollArea.scrollTop();
    }
}
Content.contextTypes = {
    scrollArea: PropType.object
};

const mapStateToProps = (state, ownProps) => {
    return {
        socket: state.socket,
        isLoaded: state.marketsConf.isLoaded,
        user:state.session.user,
        goTopCount:state.account.goTopCount,
        goTopNum:state.account.goTopNum
    }
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchSession: () => {
            dispatch(fetchUserinfo());
            dispatch(setLang());
        },
        fetchMarketsConf: () => {
            dispatch(fetchMarketsConf());
        },
        fetchRate:()=>{
            dispatch(fetchRate())
        },
        startWebSocket:()=>{
            return dispatch(startWebSocket());
        },
        pushGlobalTips:(obj) =>{
            dispatch(pushGlobalTips(obj))
        },
        notifSend: (msg,kind) => {
            dispatch(notifSend({
                message: msg,
                kind: kind||'info',
                dismissAfter: DISMISS_TIME
            }));
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(App);
