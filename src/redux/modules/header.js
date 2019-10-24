import axios from 'axios';

const SET_CHOOSE_HEAD = 'btcwinex/assets/FETCH_ASSETS_TOTAL';

export const chooseSectionType = (asstes) => {
    return {
        type: SET_CHOOSE_HEAD,
        payload: asstes
    }
}

const initialAssetsState = {
    detail: {
        isloading: false,
        isloaded:false,
        data:null
    },
   chooseItem:'bbProject'
}

const reducer = (state = initialAssetsState, action) => {
    switch(action.type) {
        case SET_CHOOSE_HEAD:
            return Object.assign({},state,{
                detail:Object.assign({},state,{
                    chooseItem:action.payload
                })
            })
         default:
            return state;
    }
}

export default reducer;
