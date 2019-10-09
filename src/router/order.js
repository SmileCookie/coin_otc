/**
 * router config
 * @description router config all router setting in this file
 * @author wm
 */
import React from 'react';
import Lazy from './lazy';
import BaseConfig from '../conf'
const OtcOrder  = Lazy(React.lazy(() => import('../pages/order')));
const OrderDetail  = Lazy(React.lazy(() => import('../pages/order/orderDetail')));


export default [
    {
        path: BaseConfig.ROOTPATH + '/otcOrder',
        components: OtcOrder,
        key: 'OtcOrder',
    },
    {
        path: BaseConfig.ROOTPATH + '/orderDetail/:id',
        components: OrderDetail,
        key: 'OrderDetail',
    },
];