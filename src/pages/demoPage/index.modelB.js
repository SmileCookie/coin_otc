/**
 * 模型B采用一种更为常用的数据格式，同时更为复杂，作为前端研发的人员必须要深知mapper的数据格式，把需要验证的内容分层验证。如果子层不对拿默认的mock替换
 */
// 获取mock
import MockB from 'mock/B';
// 引入工具类
import {isArray,containsObj} from '../../utils';
/**
 * @function MB
 * @returns Promise
 */
async function MB() {
    /**
     * 这里我就不取接口了，没有合适的，我自己定义一个模拟一个。
     */
    const my = [{id:1,name:2}];

    // 构造模型需要的返回值
    let rt = {datas:[]};
    
    if(isArray(my) && my.length > 0){
        // 开始遍历mapper
        my.forEach(v => {
            try{
                // 每一项都走mock实例
                containsObj(MockB, v);

                // 如果验证通过进入模型确认值
                rt.datas.push(v);
            }catch(e){
                // 如果发生异常直接使用默认值
                rt.datas.push(MockB);
            }
        })
    }

    return Promise.resolve(rt);
}

export { MB };