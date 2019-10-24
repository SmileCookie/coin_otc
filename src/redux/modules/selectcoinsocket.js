/**
 * 负责切换盘口处理订阅与取消订阅
 * @author luchao
 * @date 05-31-2019
 */
class SelectCoinSocket{
    constructor(){
        this.currentCondition = '';
        this.prevCondition = '';
        this.ws = null;
    }
    // 过滤非当前的数据，如果连续返回非当前币种对应的数据触发unsub 且再次sub当前币种数据。它消费socket的返回数据。
    filter(socketData){

    }
    // 订阅

    // 取消订阅

    // 条件切换，以及初始化
    init(ws = null, condition = ''){
        this.ws = ws;
        // 开启默认订阅
    }
}

export default SelectCoinSocket;