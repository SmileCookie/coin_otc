import {get,post,axios} from 'nets';
const CHATAPI = "CHATAPI"
const EMOJI   = "EMOJI"
const TEXT    = "TEXT"
const IMAGS   = "IMAGS"
const CONNET  = "CONNET"
const HISTORY = "HISTORY"
const SYSTEXT = "SYSTEXT"
const init = {
    chatApi:null,
    emojiList:null,
    text:null,
    count:0,
    countSys:0,
    sysText:null,
    isConnet:false,
    isGetTalkHistory:true
}

//存储融云API
const getChatApi = (data) =>{
    return {
        type: CHATAPI,
        preload: data,
    }
} 
//存储融云表情
const getEmoji = (data) =>{
    return {
        type: EMOJI,
        preload: data,
    }
}

//实时存储信息
const saveReTxt = (data) =>{
    return {
        type: TEXT,
        preload: data,
    }
}

 
//存储融云API
const getChatApiEvent = (chatApi) =>  (dispatch) => {
    dispatch(getChatApi(chatApi))
  
}

//存储融云表情
const getEmojiEvent = (emojiList) =>  (dispatch) => {
    dispatch(getEmoji(emojiList))
  
}

//实时存储信息
const saveReTxtEvent = (data) => (dispatch) =>{
    dispatch(saveReTxt(data))
}

//是否连通融云
const isConnect = (data) => (dispatch) =>{
    dispatch(saveConnect(data))
}

const saveConnect = (data) =>{
    return {
        type: CONNET,
        preload: data,
    }
}

const ifGetTalkHistory = (data) => {
    return {
        type: HISTORY,
        preload: data,
    }
}

const getSysText = (data) =>{
    return {
        type: SYSTEXT,
        preload: data,
    }
}

const isGetTalkHistoryEvent = (data) =>  (dispatch) =>{
    dispatch(ifGetTalkHistory(data))
}

//获取系统消息
const getSysTextEvent = (data) =>  (dispatch) =>{
    dispatch(getSysText(data))
}

//获取离线信息
// const getSysEvent = (data) =>  (dispatch) =>{
//     dispatch(getSysText(data))
// }

export default (state = init, action = {}) => {
    switch(action.type){
        case CHATAPI:
            return Object.assign({}, state, {
                chatApi:action.preload
            });
            break;
        case EMOJI:
            return Object.assign({}, state, {
                emojiList:action.preload
            });
            break;
        case TEXT:
            return Object.assign({}, state, {
                text : action.preload,
                count: state.count + 1
            });
            break;
        case CONNET:
            return Object.assign({}, state, {
                isConnet : action.preload,
            });
            break;
        case HISTORY:
            return Object.assign({}, state, {
                isGetTalkHistory : action.preload,
            });
            break;
        case SYSTEXT:
            return Object.assign({}, state, {
                sysText : action.preload,
                countSys: state.countSys + 1
            });
        break;
        default:
            return state;
            break;
    }
};

export {getChatApiEvent,getEmojiEvent,saveReTxtEvent,isConnect,isGetTalkHistoryEvent,getSysTextEvent}