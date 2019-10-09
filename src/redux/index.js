
import { applyMiddleware, createStore } from 'redux'
import { createLogger } from 'redux-logger'
import thunk from 'redux-thunk'
import cbine from './module'
let middle = [thunk]
const logger = createLogger()

console.log(process.env.NODE_ENV)
if(process.env.NODE_ENV === 'development'){
    middle = [...middle,logger]
}
export default (initialState) => createStore(
    cbine,
    initialState,
    applyMiddleware(...middle),

)