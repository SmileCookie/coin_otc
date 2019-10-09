/**
 * router config
 * @description router config all router setting in this file
 * @author mm
 */
import React from 'react';
import Lazy from './lazy';
import BaseConfig from '../conf'
const Advertisement  = Lazy(React.lazy(() => import('../pages/advertisement'))); // 广告管理
const AdvertiseDetails  = Lazy(React.lazy(() => import('../pages/advertisement/advertiseDetails'))); // 广告详情
const RateStatement  = Lazy(React.lazy(() => import('../pages/advertisement/rateStatement'))); // 费率说明
const BuyOrSellAdvertisement  = Lazy(React.lazy(() => import('../pages/advertisement/buyOrSellAdvertisement'))); // 购买或者出售广告


export default [
    {
        path: BaseConfig.ROOTPATH + '/advertisement',
        components: Advertisement,
        key: 'advertisement',
    },
    {
        path: BaseConfig.ROOTPATH + '/advertiseDetails/:id',
        components: AdvertiseDetails,
        key: 'advertiseDetails',
    },
    {
        path: BaseConfig.ROOTPATH + '/rateStatement',
        components: RateStatement,
        key: 'rateStatement',
    },
    {
        path: BaseConfig.ROOTPATH + '/buyOrSellAdvertisement/:type',
        name: BaseConfig.ROOTPATH + '/buyOrSellAdvertisement',
        components: BuyOrSellAdvertisement,
        key: 'buyOrSellAdvertisement',
    }
];