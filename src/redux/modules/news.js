import axios from 'axios'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZEFIVE } from '../../conf'
import { TH, Types } from '../../utils'

const REQUESTNEWS = 'btcwinex/news/REQUESTNEWS';
const RECIEVENEWS = 'btcwinex/news/RECIEVENEWS';
const RECIEVENEWSDETAIL = 'btcwinex/news/RECIEVENEWSDETAIL';

const requestNews = () => ({
    type:REQUESTNEWS
})
const receiveNews = (data) => {
    return{
        type:RECIEVENEWS,
        payload:{
            data
        }
    }
}

export const fetchNews = (pageIndex=PAGEINDEX) => dispatch => {
    dispatch(requestNews())
    return axios.get(DOMAIN_VIP+`/msg/newsOrAnnList?type=2&pageIndex=${pageIndex}&pageSize=${PAGESIZEFIVE}`).then((res)=>{
        try{
        const result = res.data;
        if(result.isSuc){
            // try{
            //     // 数据校验
            //     const { count, datalist, pageIndex, pageSize, total } = result.datas;
            //     TH(count);
            //     TH(datalist);
            //     TH(pageIndex);
            //     TH(pageSize);
            //     TH(total);

            //     if(Types.isArray(datalist)){
            //         for(const item of datalist){
            //             TH(item.digest);
            //             TH(item.id);
            //             TH(item.languageStr);
            //             TH(item.myId);
            //             TH(item.noticeType);
            //             TH(item.nt);
            //             TH(item.photo);
            //             TH(item.pubTime);
            //             TH(item.pubTimePage);
            //             TH(item.pubTimeStr);
            //             TH(item.recommend);
            //             TH(item.source);
            //             TH(item.sourceLink);
            //             TH(item.title);
            //             TH(item.top);
            //             TH(item.type);
            //         }
            //     }

            //     dispatch(receiveNews(result.datas))
                
            // }catch(e){
            //     console.log(e)
            // }
            dispatch(receiveNews(result.datas))
        }}catch(e){}
        
    })
}

const receiveNewsDetail = (data) => {
    return{
        type:RECIEVENEWSDETAIL,
        payload:{
            data
        }
    }
}

export const fetchNewsDetail = (id,type) => dispatch => {
    console.log(type)
    axios.get(DOMAIN_VIP+'/msg/newsdetails',{params:{id,type}}).then(res => {
        const result = res.data
        if(result.isSuc){
            dispatch(receiveNewsDetail(result.datas))
        }
    })
}

const initalState = {
    isloading:false,
    isloaded:false,
    list:null,
    newsDetail:null
}

const reducer = (state = initalState,action) => {
    switch (action.type){
        case REQUESTNEWS:
            return Object.assign({},state,{
                isloading:true
            })
        case RECIEVENEWS:
            return Object.assign({},state,{
                isloading:false,
                isloaded:true,
                list:action.payload.data
            })
        case RECIEVENEWSDETAIL:
            return Object.assign({},state,{
                newsDetail:action.payload.data
            })
        
        default:
            return state
    }
}

export default reducer;












