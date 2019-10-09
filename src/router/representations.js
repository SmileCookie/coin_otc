/**
 * router config
 * @description router config all router setting in this file
 * @author mm
 */
import React from 'react';
import Lazy from './lazy';
import BaseConfig from '../conf'
const Representations  = Lazy(React.lazy(() => import('../pages/representations'))); // 申诉详情


export default [
    {
        path: BaseConfig.ROOTPATH + '/representations/:id',
        components: Representations,
        key: 'representations',
    }
];