/**
 * 测试redux 模型
 */
const init = {
    a: 0
};

// types
const ADD = 'add';

// action
const add = (data) => {
    return {
        type: ADD,
        preload: data,
    }
}

// do
const doAdd = (data) => (dispatch) => {
    setTimeout(() => {
        dispatch(add(1))
    },1000) 
}

// reducer
export default (state = init, action = {}) => {
    switch(action.type){
        case ADD:
            return Object.assign({}, state, {a:state.a + action.preload});
            break;
        default:
            return state;
            break;
    }
};

// 导出对外的方法
export { doAdd };