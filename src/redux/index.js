/**
 * create store
 * @author luchao.ding
 */
import {applyMiddleware, createStore} from 'redux';
import thunk from 'redux-thunk';
import logger from 'redux-logger';

import cbine from './module/combinereducer';

const middle = [thunk];
if (process.env.NODE_ENV !== 'production') {
    middle.push(logger);
}

export default (initState) => {
    return createStore(
        cbine,
        initState,
        applyMiddleware(...middle)
    )
}