import { connect } from 'react-redux';
import './summary.css';
import Summary from './summary';
import {fetchSummaryData,summaryRecount} from '../../../redux/modules/summarydata';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import { DISMISS_TIME} from '../../../conf';
const { notifSend,notifClear,notifDismiss } = notifActions;
const mapStateToProps = (state, ownProps) => {
    return {
        user: state.session.user,
        isLoaded:state.summaryData.isLoaded,
        data:state.summaryData.data,
        assets:state.assets.detail.data,
        currentMarket: state.marketinfo.currentMarket,
        marketsConfData:state.marketsConf.marketsConfData,
        money:state.money
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchSummaryData: (market,money) => {
            dispatch(fetchSummaryData(market,money));
        },
        summaryRecount:(market,fun)=>{
            dispatch(summaryRecount(market))
            .then(fun)
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

export default connect(mapStateToProps, mapDispatchToProps)(Summary);