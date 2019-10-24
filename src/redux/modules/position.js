const init = {
    prices: []
}

const REQPIC = "btcwinex/trade/REQPIC";
const REQPICRESET = "btcwinex/trade/REQPIC";
 
export const reqPic = (data) => {
    return {
        type: REQPIC,
        preload: data,
    }
}

export default (state = init, action) => {
    switch(action.type){
        case REQPIC:
        //console.log(action.preload);
            return Object.assign({}, state, {prices: action.preload});break;
        default:
            return state;break;
    }
}