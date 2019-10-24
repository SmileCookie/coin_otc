import React from 'react';
import { IndexRoute, Route ,IndexRedirect, Redirect} from 'react-router';


import axios from 'axios';
import { LOGINR, FGPWD, NOTS, DOMAIN_VIP } from './conf/';
import { browserHistory } from 'react-router';
import { formatURL } from './utils';
import Market from './pages/index'

const qs = require('qs');

const WalletCooperation  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/market/walletCooperation').default);
    }, 'walletCooperation');
};
const CelebrityCooperation  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/market/celebrityCooperation').default);
    }, 'celebrityCooperation');
};
const MediaCooperation  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/market/mediaCooperation').default);
    }, 'mediaCooperation');
};
const InnerOne  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/market/innerOne').default);
    }, 'innerOne');
};
const InnerTwo  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/market/innerTwo').default);
    }, 'innerTwo');
};
const PartTime  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/market/partTime').default);
    }, 'partTime');
};
const PartTimeSuccess  = (location, cb) => {
    require.ensure([], require => {
        cb(null, require('./pages/market/partTimeSuccess').default);
    }, 'partTime');
};

const createRoutes = () => {   
    return (
        <Route path="/bw/extension" component={Market}>
            <IndexRoute  getComponent={InnerOne} />
            <Route path="market/innerOne" getComponent={InnerOne} />
            <Route path="market/innerTwo/:id" getComponent={InnerTwo} />
            <Route path="market/walletCooperation" getComponent={WalletCooperation} />
            <Route path="market/celebrityCooperation" getComponent={CelebrityCooperation} />
            <Route path="market/MediaCooperation" getComponent={MediaCooperation} />
            <Route path="market/PartTime" getComponent={PartTime} />
            <Route path="market/PartTimeSuccess" getComponent={PartTimeSuccess} />    
        </Route>        
        )
};

export default createRoutes;