/**
 *  全局tips状态
 */
const  init = {
    globalMsgList: []
}

// types
const PUSH = 'PUSH';
const POP = 'POP';

// action
const pushMsg = (data) => {
    return {
        type: PUSH,
        data: data
    }
}

const popMsg = (data) => {
    return {
        type: POP,
        data: data
    }
}


// push msg
export const pushGlobalTips = (data) => (dispatch) => {
    dispatch(pushMsg(data))
}


export const testGlobalTips = () => (dispatch) => {
    dispatch(pushMsg("这是测试内容" + new Date().toLocaleTimeString()))
}


export const popGlobalTips = () => (dispatch) => {
    dispatch(popMsg())
}


// reducer
export  const reducer = (state = init, action = {}) => {
    let {globalMsgList} = state,
        len = globalMsgList.length;
    switch(action.type){
        case PUSH:
            const arr = [action.data];
            return {
                ...state,
                globalMsgList:arr
            }
        case POP:
            if (len > 0) {
                globalMsgList.splice(0 , 1)
            }
            return {
                ...state,
                globalMsgList:[...globalMsgList]
            }
        default:
            return state;
            break;
    }
};

export default reducer