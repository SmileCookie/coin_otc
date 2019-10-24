import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS } from '../../conf';
import { TH } from '../../utils'

const FETCH_MARKETS_CONF = 'btcwinex/marketsconf/FETCH_MARKETS_CONF';
const REQUEST_MARKETS_CONF = 'btcwinex/marketsconf/REQUEST_MARKETS_CONF';
const RECIEVE_MARKETS_CONF = 'btcwinex/marketsconf/RECIEVE_MARKETS_CONF';

export const fetchMarketsConf = () => {
    return dispatch => {
        dispatch(requestMarketsConf);
        axios.get(DOMAIN_TRANS + "/getAllMarket?jsoncallback=",{withCredentials:true})
            .then(res => {
                try{
                let data = res["data"]["datas"];
                for(const i in data){
                    //try{
                        TH(data[i].bixMaxNum);
                        TH(data[i].bixMinNum);
                        TH(data[i].takerFeeRate);
                        TH(data[i].db);
                        TH(data[i].entrustUrlBase);
                        TH(data[i].exchangeBi);
                        TH(data[i].exchangeBiEn);
                        TH(data[i].exchangeBiFundsType);
                        TH(data[i].exchangeBiNote);
                        TH(data[i].exchangeBixDian);
                        TH(data[i].exchangeBixNormal);
                        TH(data[i].exchangeBixShow);
                        TH(data[i].feeRate);
                        TH(data[i].ip);
                        TH(data[i].listenerOpen);
                        TH(data[i].market);
                        TH(data[i].maxPrice);
                        TH(data[i].mergePrice);
                        TH(data[i].minAmount);
                        TH(data[i].numberBi);
                        TH(data[i].numberBiEn);
                        TH(data[i].numberBiFullName);
                        TH(data[i].numberBiFundsType);
                        TH(data[i].numberBiNote);
                        TH(data[i].numberBixDian);
                        TH(data[i].numberBixNormal);
                        TH(data[i].numberBixShow);
                        TH(data[i].port);
                        TH(data[i].makerFeeRate);
                        TH(data[i].serNum);
                        //TH(undefined);
                    //} catch(e){
                        //console.log('maping');
                    //}
                }
                
                dispatch(recieveMarketsConf(data));
                //console.log(data,'--->')
                }catch(e){
                    //console.log(123);
                    dispatch(recieveMarketsConf({

                    }));
                }
            });
    }
}

export const requestMarketsConf = () => {
    return {
        type: REQUEST_MARKETS_CONF
    }
}

export const recieveMarketsConf = (marketsConf) => {
    return {
        type: RECIEVE_MARKETS_CONF,
        payload: marketsConf
    }
}

const initialMarketsConfState = {
    isLoading: false,
    isLoaded: false,
    marketsConfData: null
}

const reducer = (state = initialMarketsConfState, action) => {
    switch(action.type) {
        case REQUEST_MARKETS_CONF:
            return Object.assign({}, state, {
                isLoading: true
            });
        case RECIEVE_MARKETS_CONF:
            return Object.assign({}, state, {
                isLoading: false,
                isLoaded: true,
                marketsConfData: action.payload
            });
        default:
            return state;
    }
}

export default reducer;