/**
 * router config
 * @description router config all router setting in this file
 * @author luchao.ding
 */
import React from 'react';

// lazy component
import Lazy from './lazy';

//模块引进
import Trade from './trade'
import Order from './order'
import Advertisement from './advertisement';
import Representations from './representations';
import Business from './business';
import BaseConfig from '../conf'
const App    =  Lazy(React.lazy(() => import('../pages/App')));
// const Home   =  Lazy(React.lazy(() => import('../pages/demoPage')));
const Nopage =  Lazy(React.lazy(() => import('../pages/notfound')));
const {ROOTPATH} = BaseConfig;

export default [
    {
        path: ROOTPATH,
        components: App,
        key: 'App',
        routes: [
            //公共组件集合
            // {
            //     path: ROOTPATH + '/home',
            //     components: Home,
            //     key: 'cd',
            // },
            //交易
            ...Trade,
            //订单
            ...Order,
            // 广告管理
            ...Advertisement,
            // 申诉
            ...Representations,
            // 商家
            ...Business,
            //404
            {
                path: ROOTPATH + '/*',
                components: Nopage,
                key: 'Nopage',
            }

        ]
    },
    //全局接口失败跳转
    {
        path: '/error',
        components: Nopage,
        key: 'Nopage',
    }
];