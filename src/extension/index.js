import 'babel-polyfill';
import 'intl';

import React from 'react';
import ReactDOM from 'react-dom';
import { IntlProvider } from 'react-intl'
import { Router, browserHistory,Route,IndexRedirect,IndexRoute } from 'react-router';
import getRoutes from './routes';

ReactDOM.render(   
        
            <Router history={browserHistory} >
                {getRoutes()}
            </Router>

    ,
    document.getElementById("root")
)