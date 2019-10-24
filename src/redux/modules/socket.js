/**
 * single websocket
 * @author luchao
 * @date 05-31-2019
 */
import Reconnectingwebsocket from 'reconnectingwebsocket';
import pako from 'pako';
import { WEBSOCKETURL } from '../../conf';
import { isFunc } from '../../utils';

let createWebSocket = false;

const initState = {
    ws: null,
    msgCallbacks: [],
    clearCallbacks: [],
};

const SETWEBSOCKET = 'setWebSocket';
const RESETSOCKET = 'resetsocket';

const setWebSocket = (ws = null) => {
    // 如果当前句柄或者就赋值
    if(ws){
        return {
            type: SETWEBSOCKET,
            preload: ws,
        }
    } else {
        // 否则直接清空
        return {
            type: RESETSOCKET
        }
    }
};
let socketIsLive = true;
let pongCount = 0;
let liveTimer = null;

// 启动websocket创建连接
const startWebSocket = () => (dispatch, getState) => {
    let clearResources = () => {};

    if(!createWebSocket){
        // create
        const WS = new Reconnectingwebsocket(WEBSOCKETURL);
        webSocketEvent(WS, getState, dispatch);

        // 不停的发ping包
        const ping = setInterval(() => {
            try{
                WS.send("ping");
            }catch(e){

            }
        },1000);

        clearResources = () => {
            // 关闭socket 释放资源
            WS.close();
            // 清空模型
            dispatch(setWebSocket(null));
            // 清空定时器
            clearInterval(ping);
            clearInterval(getSocketIsLive);
            clearTimeout(liveTimer);
            // 清空指针
            socketIsLive = true;
            createWebSocket = false;
            pongCount = 0;
        };

        // 如果socket 挂了重新启动
        const getSocketIsLive = setInterval(() => {
            if(!socketIsLive){
                // console.log('0000000')
                // 还原释放上次所有的信息
                clearResources();
                // 重启websocket
                dispatch(startWebSocket());
            }
        }, 1000);

        createWebSocket = true;
    }
    // 返回函数释放所有的资源。
    return clearResources;
};



const webSocketEvent = (ws = null, socket = () => {}, dispatch = null) => {
    if(ws && dispatch){
        ws.onopen = () => {
            dispatch(setWebSocket(ws));
        }

        // 获取服务端的数据进行订阅分发
        ws.onmessage = (evt) => {

            if(evt.data instanceof Blob){
                var reader = new FileReader();
                reader.onload = () => {
                    try{
                        let result = JSON.parse(pako.inflate(reader.result, {to:'string'}));
                        if(window.location.href.includes('debug')){
                            console.log(result,'===--->');
                        }
                        socket().socket.msgCallbacks.forEach((v) => {
                            isFunc(v) && v(result);
                        });
                        pongCount = 0;
                    }catch(e){
                        pongCount++;
                        if(pongCount > 10){
                            //console.log('iiiiiii')
                            //console.log(pako.inflate(reader.result, {to:'string'}));
                            // socketIsLive = false;
                            // 因为能ping通必然链接还在触发订阅模型重新订阅
                            dispatch(setWebSocket(null));
                            dispatch(setWebSocket(ws));
                        }
                    }
                }
                reader.readAsArrayBuffer(evt.data);
            }

            clearTimeout(liveTimer);
            liveTimer = setTimeout(() => {
                socketIsLive = false;
            },20000);

        }

        // 当发生错误
        ws.onerror = () => {

        }

        // 当服务器主动断开分发订阅
        ws.onclose = () => {
            try{
                socket().socket.clearCallbacks.forEach((v) => {
                    isFunc(v) && v();
                })
            }catch(e){

            }
        }
    }
};

// 订阅消息
const SUBSCRIBEMSG = 'SUBSCRIBEMSG';
const doSubscribeMsg = (task = () => {}) => {
    return {
        type: SUBSCRIBEMSG,
        preload: task
    }
};

// 订阅关闭
const SUBSCRIBECLOSE = 'SUBSCRIBECLOSE';
const doSubscribeClose = (task = () => {}) => {
    return {
        type: SUBSCRIBECLOSE,
        preload: task
    }
};

// 测试重连机制
// (() => {
//     setInterval(() => {
//         console.log('iiiiii');
//         socketIsLive = false;
//     }, 4000);
// })();

export default (state = initState, action = {}) => {
    switch(action.type){
        case SUBSCRIBEMSG:
            return Object.assign({}, state, {msgCallbacks: [...state.msgCallbacks,action.preload]});
            break;
        case SUBSCRIBECLOSE:
            return Object.assign({}, state, {clearCallbacks: [...state.clearCallbacks,action.preload]});
            break;
        case SETWEBSOCKET:
            return Object.assign({}, state, {ws: action.preload});
            break;
        case RESETSOCKET:
            return Object.assign({}, state, initState);
            break;
        default:
            return state;
            break;
    }
};

export { startWebSocket, doSubscribeMsg, doSubscribeClose };