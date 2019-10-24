import 'babel-polyfill';
import 'intl';
import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { applyRouterMiddleware, Router, browserHistory } from 'react-router';
import { useScroll } from 'react-router-scroll';
import configureStore from './redux/store/configureStore';
import getRoutes from './routes';
import LanguageProvider from './components/languageProvider';
import translationMessages from './locale'
import ErrorComponent from './pages/common/ErrorComponent'
import Warning from './pages/warning'

import './assets/css/bootstrap.css'
import './assets/css/common.css'
import './assets/css/dgq.less'


const store = configureStore();
ReactDOM.render(
    <ErrorComponent>
         <div>
            <Provider store={store}>
                <LanguageProvider messages={translationMessages}>
                    <Router history={browserHistory} render={applyRouterMiddleware(useScroll())}>
                        {getRoutes(store)}
                    </Router>
                </LanguageProvider>
            </Provider>
             {/*浏览器升级提示*/}
             <Provider store={store}>
                 <Warning messages={translationMessages}>
                 </Warning>
             </Provider>
            <div id="popout">
                <div className="bbyh-shadowwp"></div>
            
                <div id="pop">
                    <svg className="ep" aria-hidden="true"><use xlinkHref="#icon-zhucewenanicon"></use></svg><span id="poptxt">tt</span>
                </div>
            </div>
            <div id="popoJump">
                <div className="bbyh-shadowwp"></div>
            
                <div id="pops">
                    <span id="poptxts">xxxx</span>
                    <div id="jumpBtn">
                        <a id="JumpLink" href="javascript:void(0)" role="button" className="btn btn-primary btn-sm bbyh-btns">
                            <span id="confrimJump">确认</span>
                        </a>
                    </div>
                </div>
            </div>
            <div id="r-pop">
                <div id="up-r-pop-msg"></div>
            </div>
        </div>
    </ErrorComponent>,
    document.getElementById("root")
)