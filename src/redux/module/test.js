
/**
 * 测试
 */
const initState = {
    count: 0,
    isLoading:false
}

//types

const ADD_REQUEST = 'add_request'
const ADD_RESPOSE = 'add_respose'


//actions

const add = (data) => {

    return {
        type: ADD_RESPOSE,
        payload: data
    }
}
const addRequest = () => {

    return {
        type: ADD_REQUEST,
    }
}

//add

const todoAdd = (params = 1) => dispatch => {
    dispatch(addRequest())
    setTimeout(() => {
        dispatch(add(params))
    }, 1000)
}

//reducer

export default (state = initState, action = {}) => {
    switch (action.type) {
        case ADD_REQUEST:
            return Object.assign({}, state, { isLoading: true })
        case ADD_RESPOSE:
            return Object.assign({}, state, { count: action.payload,isLoading:false })
        default:
            return state
    }
}

export { todoAdd }