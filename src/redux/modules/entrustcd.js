/**
 * save balances condition
 * @author dlc
 */

// states
const condition = {
    current: {

    },
    list: {

    },
    transrecord: {

    },
};

// types
const SAVE_CURRENT = '/bw/entrust/current';
const SAVE_LIST = '/bw/entrust/list';
const SAVE_TRANSRECORD = '/bw/entrust/transrecord';
// key
const CURRENT = 'current';
const LIST = 'list';
const TRANSRECORD = 'transrecord';

// action


// doaction
const doSaveConditionCurrent = (data = {}) => {
    return {
        type: SAVE_CURRENT,
        preload: data,
    }
};

const doSaveConditionList = (data = {}) => {
    return {
        type: SAVE_LIST,
        preload: data,
    }
};

const doSaveConditionTransrecord = (data = {}) => {
    return {
        type: SAVE_TRANSRECORD,
        preload: data,
    }
};


// reducer
const reducer = (init = condition, action = {}) => {
    let rt = init;

    switch(action.type){
        case SAVE_CURRENT:
            rt = fmData(CURRENT, action.preload, init);
            break;
        case SAVE_LIST:
            rt = fmData(LIST, action.preload, init);
            break;
        case SAVE_TRANSRECORD:
            rt = fmData(TRANSRECORD, action.preload, init);
            break;
        default:;break;
    }

    return rt;
};

// util 
// format data
const fmData = (mergeKey, data, init) => {
    return Object.assign({}, init, {
        [mergeKey]: data
    });
}

// export
export {doSaveConditionCurrent,doSaveConditionList,doSaveConditionTransrecord};
export default reducer;
