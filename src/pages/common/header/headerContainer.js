import React from 'react';
import { connect } from 'react-redux';
import cookie from 'js-cookie';
import Header from './header';
import { logout } from '../../../redux/modules/session'
import { fetchAssetsDetail, fetchAssetsTotal ,fetchrWalletTotal} from '../../../redux/modules/assets'
import { fetchOtcAssetsTotal } from '../../../redux/modules/otcdetail'
import {checkVipEvent} from '../../../redux/modules/account'
import { fetchTransAssetsTotal,fetchTransInfo} from '../../../redux/modules/transDetail'
import {chooseSectionType} from '../../../redux/modules/header'
import {fetchIntegral} from '../../../redux/modules/level'
import { setLang } from '../../../redux/modules/language'
import { setMoney } from '../../../redux/modules/money'

import { modifyFoot } from '../../../redux/modules/trade'

const mapStateToProps = (state) => {
    return {
        session: state.session,
        assets: state.assets,
        integral:state.level.integral,
        money:state.money,
        footStau:state.trade.footStau,
        header:state.header,
        checkVipObj:state.account.checkVipObj,
        userInfo: state.session.baseUserInfo,
    };
}

const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        fetchTransAssetsTotal : () =>{
            return dispatch(fetchTransAssetsTotal());
        },
        fetchTransInfo:() =>{
            return dispatch(fetchTransInfo());
        },
        fetchOtcAssetsTotal: () => {
            return dispatch(fetchOtcAssetsTotal());
        },
        fetchAssetsDetail: () => {
            dispatch(fetchAssetsDetail());
        },
        fetchAssetsTotal: () => {
            return dispatch(fetchAssetsTotal());
        },
        fetchrWalletTotal:()=>{
            return dispatch(fetchrWalletTotal());
        },
        logout: () => {
            dispatch(logout());
        },
        fetchIntegral:()=>{
            dispatch(fetchIntegral());
        },
        setMoney:(name)=>{
            dispatch(setMoney(name))
        },
        modifyFoot:(type) => {
            dispatch(modifyFoot(type))
        },
        chooseSectionType:(data) =>{
            dispatch(chooseSectionType(data))
        },
        checkVipEvent:() =>{
            dispatch(checkVipEvent())
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Header);