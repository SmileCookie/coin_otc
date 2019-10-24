import { createStore, combineReducers, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import rootReducer from '../modules/reducers';
import logger from 'redux-logger'

const middlewares = [thunk]
// if (process.env.NODE_ENV === 'dev') {
//     middlewares.push(logger);
// }
const enhancer = applyMiddleware(...middlewares);

const configureStore = (inititalState) => {
    return createStore(rootReducer, inititalState, enhancer);
};

export default configureStore;


