/**
 * merge all mode to combinereducer
 * @author luchao.ding
 */
import {combineReducers} from 'redux';

import test from './test';
import language from './language';
import session  from './session'
import chart    from './chart'
import tips  from './tips'
import money from './money'


export default combineReducers({
     test,
     language,
     session,
     chart,
     tips,
     money
});
