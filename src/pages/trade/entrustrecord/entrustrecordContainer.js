import { connect } from 'react-redux';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import { fetchEntrustRecord, cancelEntrust,batchCancelEntrust,cancelAllStop } from '../../../redux/modules/entrustrecord';
import {fetchOrderHistory} from '../../../redux/modules/orderhistory';
import {fetchOrderHistory24H} from '../../../redux/modules/orderhistory24H';
import { fetchAssetsDetail } from '../../../redux/modules/assets'
import './entrustrecord.css';
import Entrustrecord from './entrustrecord';

import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import { STATUS ,DISMISS_TIME} from '../../../conf';
const { notifSend,notifClear } = notifActions;

const mapStateToProps = (state, ownProps) => {
    return {
        user: state.session.user,
        currentMarket: state.marketinfo.currentMarket,
        entrustrecord: state.entrustrecord,
        orderhistory:state.orderHistpry,
        orderhistory24H:state.orderhistory24H,
        marketsConf: state.marketsConf.marketsConfData
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchEntrustRecord: (market) => {
            let lastTime = +new Date;
            dispatch(fetchEntrustRecord(market,1, 3, lastTime, 30, 1));
            dispatch(fetchEntrustRecord(market,2, -1, lastTime, 30, 1));
            // dispatch(fetchEntrustRecord(market,1, 2, lastTime, 0, 1));
        },
        fetchLimitRecord: (market,pageSize,fun) => {
            let lastTime = +new Date;
            dispatch(fetchEntrustRecord(market,1, 3, lastTime, pageSize, 1,fun));
        },
        fetchStopRecord: (market,pageSize,fun) => {
            let lastTime = +new Date;
            dispatch(fetchEntrustRecord(market,2, -1, lastTime, pageSize, 1,fun));
        },
        cancelEntrust:(market,entrustid,plantype,fun) => {
            console.log(entrustid)
            dispatch(cancelEntrust(market,entrustid,plantype))
                .then(fun)
        },
        batchCancelEntrust:(market,plantype,types,minPrice,maxPrice,fun)=>{
            dispatch(batchCancelEntrust(market,plantype,types,minPrice,maxPrice))
                .then(fun)
        },
        fetchOrderHistory:(market,includeCancel,timeType,type,pageNum,pageSize)=>{
            let lastTime = +new Date;
            dispatch(fetchOrderHistory(market,includeCancel,timeType,type,pageNum,pageSize,lastTime))
        },
        fetchOrderHistory24H:(market,includeCancel,timeType,type,pageNum,pageSize,fun)=>{
            let lastTime = +new Date;
            dispatch(fetchOrderHistory24H(market,includeCancel,timeType,type,pageNum,pageSize,lastTime,fun))
        },
        cancelAllStop:(market,fun)=>{
            dispatch(cancelAllStop(market)).then(fun)
        },
        fetchAssetsDetail:() => {
            dispatch(fetchAssetsDetail())
        },
        notifSend:(inf,kind)=>{
            dispatch(notifClear());
            dispatch(notifSend({
                    message: inf,
                    kind: kind||'info',
                    dismissAfter: DISMISS_TIME
                })
            );
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(Entrustrecord));