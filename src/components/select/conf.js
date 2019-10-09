/**
 * @author
 * 
 */
const _userType =  Symbol('userType')
const map =  new Map([
    [_userType,{
        1:'OTC普通用户',
        2:'OTC广告商家'
    }],    //Otc 参数配置
])

 export const getMap = key => map.has(key) && map.get(key)
 