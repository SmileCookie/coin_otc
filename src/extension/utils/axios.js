import axios from 'axios'
import qs  from 'qs';

export function get(url,params){
    return new Promise((resolve,rejects) =>{
        axios.get(url,{
            params:params
        }).then(res =>{
            resolve(res.data)
        }).catch(err =>{
            rejects(err)
        })
    })
}

export function post(url,params){
    return new Promise((resolve,rejects) =>{
        axios.post(url, qs.stringify(params))
        .then(res =>{
             controlData(res.data)
            resolve(res.data)
        }).catch(err =>{
            rejects(err)
        })
    })
}

