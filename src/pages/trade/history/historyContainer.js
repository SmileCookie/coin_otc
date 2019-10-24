import { connect } from 'react-redux';
import './history.css';
import Tradehistory from './history';
import { recieveMarkethistory } from '../../../redux/modules/markethistory';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend,notifClear } = notifActions;
import { DISMISS_TIME } from '../../../conf';
const mapStateToProps = (state, ownProps) => {
    return {
        user: state.session.user,
        marketHistorydata:state.marketHistoryData.data,
        mkIsLoading: !state.marketHistoryData.isLoaded,
        mineHistoryIsLoaded: state.mineHistoryData.isLoaded,
        mineHistorydata:state.mineHistoryData.data,
        currentMarket: state.marketinfo.currentMarket,
        marketsConfData: state.marketsConf.marketsConfData,
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        recieveMarkethistory:(data)=>{
            dispatch(recieveMarkethistory(data)); 
        },
        notifSend: (msg,kind) => {
            dispatch(notifClear());
            dispatch(notifSend({
                message: msg,
                kind: kind||'info',
                dismissAfter: DISMISS_TIME
            }));
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Tradehistory);