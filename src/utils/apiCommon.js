import axios from 'axios'
import qs from 'qs'
import { DOMAIN_VIP ,TIMEFORMAT} from '../conf/index'
import moment from 'moment'


//获取列表
export const normalList = (URL,data={},changeKey)=>{
    return new Promise((resolve,reject) =>{
        axios.post(DOMAIN_VIP + URL,qs.stringify(data)).then((res) => {
            let  result = res.data
            if(result.code == 0){
                    let tableSource = result.data.list;
                    for(let i=0;i<tableSource.length;i++){
                        tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                        tableSource[i].key = tableSource[i].id,
                        tableSource[i][changeKey] = moment(tableSource[i].createtime).format(TIMEFORMAT)
                    }
                    resolve(tableSource)
                }else{
                    resolve(result.msg)
                }
        })
        .catch((err) =>{
            reject(err)
        })
    })
}