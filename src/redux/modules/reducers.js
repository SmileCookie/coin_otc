import { combineReducers } from 'redux';
import { reducer as notifReducer } from 'redux-notifications';

import language from './language';
import session from './session';
import assets from './assets';
import marketsConf from './marketsconf';
import markets from './markets';
import marketinfo from './marketinfo';
import entrustrecord from './entrustrecord';
import loginLogs from './loginlogs';
import account from './account';
import level from './level';
import security from './security';
import withdraw from './withdraw';
import userInfo from './userInfo';
import deposit from './deposit';
import wallet from './wallet'
import otcDetail from './otcdetail'
import marketdepthchartdata from './marketdepthchartdata';
import marketHistoryData from './markethistory';
import mineHistoryData from './minehistory';
import summaryData from './summarydata';
import buySell from './buysell';
import orderHistpry from './orderhistory';
import orderhistory24H from './orderhistory24H';
import currentHistory from './currenthistory';
import money from './money';
import coinList from './coinsList'
import transferlist from './transferList'
import transferData from './transfer'
import distriHistory from './distriHistory'
import entrust from './entrust'
import repo from './repo';
import repolist from './repolist';
import repolistmine from './repolistmine';
import trade from './trade'
import news from './news'
import vote from './vote'
import positioin from './position';
import entrustcd from './entrustcd';
import socket from './socket';
//import { R as chat } from 'oasis-client-dep';
import header from './header'
import transDetail from './transDetail'
//otc 全局tip
import otcTips from './otcTips'

const rootReducer = combineReducers({
    //chat,
    notifs: notifReducer,
    language,
    session,
    assets,
    marketsConf,
    markets,
    marketinfo,
    entrustrecord,
    account,
    level,
    otcDetail,
    transDetail,
    security,
    withdraw,
    userInfo,
    loginLogs,
    deposit,
    marketdepthchartdata,
    marketHistoryData,
    mineHistoryData,
    summaryData,
    buySell,
    orderHistpry,
    currentHistory,
    orderhistory24H,
    money,
    coinList,
    distriHistory,
    entrust,
    repo,
    wallet,
    repolist,
    repolistmine,
    trade,
    news,
    transferData,
    transferlist,
    vote,
    header,
    positioin,
    entrustcd,
    socket,
    otcTips
})

export default rootReducer;
