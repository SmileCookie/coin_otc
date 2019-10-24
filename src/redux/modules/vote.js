import axios from 'axios'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,BBYH_PAGESIZE } from '../../conf'

const REQUESTVOTE = 'btcwinex/vote/REQUESTVOTE';
const RECIEVEVOTE = 'btcwinex/vote/RECIEVEVOTE';

const RECIEVEVOTEDETAIL = 'btcwinex/vote/RECIEVEVOTEDETAIL';


//公告列表
const requestVote = () => ({
    type:REQUESTVOTE
})
const receiveVote = (data) => {
    return{
        type:RECIEVEVOTE,
        payload:{
            data
        }
    }
}
export const fetchvote = ({pageIndex=PAGEINDEX,type=0,title=''}={}) => dispatch => {
    dispatch(requestVote())
    return axios.post(DOMAIN_VIP+`/msg/newsOrAnnList`,
    qs.stringify({
        type:1,
        pageIndex:pageIndex,
        pageSize:BBYH_PAGESIZE,
        noticeType:type,
        title:title
    }),
    {
        headers:{
        'Content-type': 'application/x-www-form-urlencoded;charset=UTF-8'
    }
    }).then((res)=>{
        const result = res.data;
        if(result.isSuc){
            dispatch(receiveVote(result.datas))
        }
    })
}

const receiveVoteDetail = (data) => ({
    type:RECIEVEVOTEDETAIL,
    payload:{
        data
    }
})

export const fetchVoteDetail = (id,type, flg) => dispatch => {
    if(!flg){
    axios.get(DOMAIN_VIP+'/msg/newsdetails',{params:{id,type}}).then(res => {
        // console.log(res)
        const result = res.data;
        if(result.isSuc){
            dispatch(receiveVoteDetail(result.datas.newsdetial))
        }
    })}else{
        
        dispatch(receiveVoteDetail(null))
    }
}


const initalState = {
    isloading:false,
    isloaded:false,
    list:null,
    voteDetail:null
}

const reducer = (state = initalState,action) => {
    switch (action.type){
        case REQUESTVOTE:
            return Object.assign({},state,{
                isloading:true
            })
        case RECIEVEVOTE:
            return Object.assign({},state,{
                isloading:false,
                isloaded:true,
                list:action.payload.data
            })
        case RECIEVEVOTEDETAIL:
            return Object.assign({},state,{
                voteDetail:action.payload.data
            })
        default:
            return state
    }
}

export default reducer;












