import React from "react";
import ReactDOM from "react-dom";
import { Provider } from 'react-redux';
import { AppContainer } from "react-hot-loader";
import { BrowserRouter } from "react-router-dom";
import Router from "./router";
import $ from 'jquery';
import store from './redux';
import LanguageProvider from 'components/languageProvider';
import translationMessages from './lan';
import './assets/style/base/index.less'
import './assets/style/base/layout.less'
//import './assets/style/base/common.less';
import './assets/style/modal/index.css'
import './assets/font/iconfont.css'
import './assets/font/iconfont.js'
import 'animate.css'

/*初始化*/
renderWithHotReload(Router);

/*热更新*/
if (module.hot) {
    module.hot.accept("./router/index.js", () => {
        const Router = require("./router/index.js").default;
        renderWithHotReload(Router);
    });
}

function renderWithHotReload(Router) {
    ReactDOM.render(
        <AppContainer>
            <div>
                <Provider store={store()}>
                    <LanguageProvider messages={translationMessages}>
                        <BrowserRouter>
                            <Router />
                        </BrowserRouter>
                    </LanguageProvider>
                </Provider>
                <div id="popout">
                    <div className="bbyh-shadowwp"></div>
                    <div id="pop">
                        <svg className="ep" aria-hidden="true"><use xlinkHref="#icon-zhucewenanicon"></use></svg><span id="poptxt">tt</span>
                    </div>
                </div>
            </div>
        </AppContainer>,
        document.getElementById("app")
    );
}

// 判断该浏览器支不支持 serviceWorker
// if ('serviceWorker' in navigator) {
//     window.addEventListener('load', () => {
//         navigator.serviceWorker
//             .register('/service-worker.js')
//             .then(registration => {
//                 console.log('service-worker registed')
//             })
//             .catch(error => {
//                 console.log('service-worker registed error')
//             })
//     })
// }