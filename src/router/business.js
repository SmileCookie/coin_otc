/**
 * router config
 * @description router config all router setting in this file
 * @author kj
 */
import React from 'react';
import Lazy from './lazy';
import BaseConfig from '../conf'
const Business  = Lazy(React.lazy(() => import('../pages/business'))); // 商家认证首页

export default [
    {
        path: BaseConfig.ROOTPATH + '/business',
        components: Business,
        key: 'business',
    }
];