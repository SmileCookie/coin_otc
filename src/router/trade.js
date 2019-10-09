/**
 * router config
 * @description router config all router setting in this file
 * @author mm
 */
import React from 'react';
import Lazy from './lazy';
import BaseConfig from '../conf'
const Trade  = Lazy(React.lazy(() => import('../pages/trade')));
const BuySell  = Lazy(React.lazy(() => import('../pages/trade/buySell')));


export default [
    {
        path: BaseConfig.ROOTPATH + '/trade',
        components: Trade,
        key: 'trade',
    },
    {
        path: BaseConfig.ROOTPATH + '/buySell/:id',
        components: BuySell,
        key: 'buySell',
    },
];