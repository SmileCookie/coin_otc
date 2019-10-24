import { connect } from 'react-redux';
import SellBuy from './sellBuy';
import { fetchHasSafePwd,fetchLimitPriceEntrust,fetchPlanEntrust,fetchBatchEntrust,fetchUserInfo} from '../../../redux/modules/buysell'
import { fetchAssetsDetail } from '../../../redux/modules/assets'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend,notifClear } = notifActions;
import { DISMISS_TIME } from '../../../conf';

import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';

const mapStateToProps = (state, ownProps) => {
    return {
        assetsDetail: state.assets.detail.data,
        marketinfo: state.marketinfo,
        marketsConfData: state.marketsConf.marketsConfData,
        vipRate:state.session.baseUserInfo.vipRate,
        buySell:state.buySell,
        integral:state.session.integral,
        moneyLocal:state.money.locale,
        user:state.session.user
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchHasSafePwd: ()=>{
            return dispatch(fetchHasSafePwd())
        },
        fetchLimitPriceEntrust:(safePwd,coinPrice,coinNumber,isBuy,entrustUrlBase,market)=>{
            return dispatch(fetchLimitPriceEntrust(safePwd,coinPrice,coinNumber,isBuy,entrustUrlBase,market))
        },
        fetchPlanEntrust:(planData,entrustUrlBase,market)=>{
            return dispatch(fetchPlanEntrust(planData,entrustUrlBase,market))
        },
        fetchBatchEntrust:(safePwd,price,maxPrice,amount,isBuy,entrustUrlBase,market)=>{
            return dispatch(fetchBatchEntrust(safePwd,price,maxPrice,amount,isBuy,entrustUrlBase,market))
        },
        fetchUserInfo:()=>{
            return dispatch(fetchUserInfo())
        },
        fetchAssetsDetail:() => {
            dispatch(fetchAssetsDetail())
        },
        notifSend: (msg,kind) => {
            // dispatch(notifClear());
            dispatch(notifSend({
                message: msg,
                kind: kind||'info',
                dismissAfter:DISMISS_TIME
            }));
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(SellBuy));