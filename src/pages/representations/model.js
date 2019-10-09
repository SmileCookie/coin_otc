

//获取订单数量
import {post,formData} from "../../net";


// 申诉详情
export async function otcComplain(id) {
    let data ={
        id:id
    }
    // 获取数据
    let result = await post('/web/complain/findOtcComplain',data);
    let rtData = result.data;
    //console.log(rtData);


    // 无论如何都将返回供体数据。
    return Promise.resolve(rtData);
}


// 提交申诉
export async function updComplain(data) {
    console.log(data)
    // 获取数据
    let result = await formData('/web/complain/updComplain',data);
    // let rtData = result.data;
    //console.log(rtData);


    // 无论如何都将返回供体数据。
    return Promise.resolve(result);
}
