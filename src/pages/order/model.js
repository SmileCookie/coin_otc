/**
 * model
 * @description 所有的核心数据放在此处处理，控制层只拿处理好的数据。
 * 如果在运算中出现了任何异常直接使用mock数据，视图在渲染如果没有此字段直接用--。
 */
import {post, get} from 'nets';
import {DATA_TIME_FORMAT,USERID} from 'conf'
// 获取模型对应的mock
import {orderLists,orderDetail} from '../../mock/order';
import moment from 'moment';
import {separator,getCoinNum,formatDecimal} from '../../utils'
const BigNumber = require('big.js')


// 工具类 不采用路径简写的形式，因为需要工具看定义
import {isArray} from '../../utils';
// 获取列表数据
async function getOrederList(limit = 15, page = 1, status = '', type= '', recordNo='', market='',startCoinTime ='',endCoinTime='',_this) {
    // 获取数据
    let obj = {
        limit,//每页条数
        page,//页码
        userId:USERID,
        status,//交易状态
        type,//0-买；1-卖
        recordNo,//订单号，模糊查询
        market,//市场 例如 USDT/CNY
        startCoinTime,
        endCoinTime
    }
        //loading
        _this.setState({
            loading:true
        })
    let result = await post('/web/v1/order/query', obj);
         
        //loading 
        _this.setState({
            loading:false
        })
        result = result.data;
    let rtData = orderLists

    // 验证mock中对应的实例是否都存在。
    if(isArray(result.list ) && result.list.length>0){
        // 开始遍历mapper
        let list  =[];
        let orderListdata =  orderLists.list[0] 
        result.list.map(item => {
            //截取市场名字
            item.market = item.market.slice(0,-4)
            // 时间格式化
            let _obj = Object.assign({},orderListdata)
            Object.keys(_obj).map(v=>{  void 0 !== item[v] && ({[v]:_obj[v]}=item)  });
            list.push(_obj);
        });
        //console.log(list);
        rtData = {
            list,
            totalCount:result.totalCount,
            totalPage:result.totalPage,
            currPage:result.currPage
        }
        
    }else{
        rtData = {
            list:[],
            totalCount:result.totalCount,
            totalPage:result.totalPage,
            currPage:result.currPage
        }
    }
    // 无论如何都将返回供体数据。
    return Promise.resolve(rtData);
}

//获取订单数量
async function getOrederNum() {
    // 获取数据
    let result = await post('/web/v1/order/getOrderSum');
    let rtData = result.data;
    //console.log(rtData);
    
        
    // 无论如何都将返回供体数据。
    return Promise.resolve(rtData);
}
//获取用户信息
async function getUserInfors(id){
    let payList = await post('/web/v1/trade/querUserPayment',{recordId:id});
    let payMethod=[];
        payList = payList.data;
        
    // let rtdata_1 = await get(`/web/payment/${id}/1`)//支付宝
    // let rtdata_2 = await get(`/web/payment/${id}/2`)//银行卡
    //     rtdata_1 = rtdata_1.data;
    //     rtdata_2 = rtdata_2.data;
    payList.map((item,index) =>{
        if(item.paymentType == 2){
            let _obj = {
                type:'bank',
                name:item.accountName,
                payName:item.bankOpeningBank,
                payAdress:item.bankOpeningBranch,
                payCode:item.accountNumber
            }
            payMethod.push(_obj)
        }
        if(item.paymentType == 1){
            let _obj = {
                type:'alipay',
                name:item.accountName,
                alipayPayCode:item.accountNumber,
                alipayPayImgUrl:item.qrcodeUrl
            }
            payMethod.push(_obj)
        }
    })
    
        // if(rtdata_2){
        //     let _obj = {
        //         type:'bank',
        //         name:rtdata_2.accountName,
        //         payName:rtdata_2.bankOpeningBank,
        //         payAdress:rtdata_2.bankOpeningBranch,
        //         payCode:rtdata_2.accountNumber
        //     }
        //     //判断是否关闭此支付方式
        //     if(rtdata_2.enable == 1){
        //         payMethod.push(_obj)
        //     }
            
        // }
        // if(rtdata_1){
        //     let _obj = {
        //         type:'alipay',
        //         name:rtdata_1.accountName,
        //         alipayPayCode:rtdata_1.accountNumber,
        //         alipayPayImgUrl:rtdata_1.qrcodeUrl
        //     }
        //     if(rtdata_1.enable == 1){
        //         payMethod.push(_obj)
        //     }
        // } 
        
    let rtdata = {
        payMethod,
    }
    return rtdata

}
//获取订单详情
async function getOrderDetail(id,_this){
    console.log(_this.props.coinData);
    //获取截断
    //getCoinNum
    
    let result = await post('/web/v1/order/getInfo',{id})
        console.log(result);
        //如果不是正常返回进入异常订单
        if(result.code !== 200){
            _this.setState({
                loading:false,
                erroPage:true
            })
        }
        
        result = result.data;
        if(result){
            //对比数据
            Object.keys(orderDetail).map(v=>{  void 0 !== result[v] && ({[v]:orderDetail[v]}=result)  });
        }

        //获取截断小数位（法币和该币种市场）
        const coinInfor = getCoinNum(_this.props.coinData,orderDetail.market.slice(0,-4));
        const {marketL,payL} = coinInfor
       
        //获取用户信息
    let userInfor = await getUserInfors(orderDetail.id);
    // if(USERID == orderDetail.buyUserId){
    //     userInfor =  //卖方信息
    // }else{
    //     userInfor =  await getUserInfors(orderDetail.buyUserId) //买方信息
    // }

    //订单状态判断取值
    let dealProcess,dealStatue; 
    switch(orderDetail.status){
        case 1:
                dealProcess = 'doing';
                dealStatue  = 'hasOrder';
                break;
        case 2:
                dealProcess = 'doing';
                dealStatue  = 'hasPay';
                break;
        case 3:
                dealProcess = 'done';
                dealStatue  = 'pass';
                break;
        case 4:
                dealProcess = 'done';
                dealStatue  = 'cancel';
                break;
        case 5:
                dealProcess = 'done';
                dealStatue  = 'error';
                break;
        case 6:
                dealProcess = 'done';
                dealStatue  = 'appeal';
                break;
        default:
                dealProcess = 'done';
                dealStatue  = 'error';
                break;         
    }
    
    let rtData = {
        id:orderDetail.id,//订单id
        orderListInfor:{//订单详情列表
                orderMoney: separator(new BigNumber(orderDetail.myMoney).toFixed(payL)),
                orderNum:new BigNumber(orderDetail.coinNumber).toFixed(marketL),
                price:separator(new BigNumber(orderDetail.coinPrice).toFixed(payL)),
                charge:USERID == orderDetail.buyUserId ? orderDetail.buyUserFeeStr : orderDetail.sellUserFeeStr,
                realMoney : orderDetail.finalNumStr,
                time:orderDetail.coinTime,
                marker:orderDetail.payRemark        
            },
            //用户信息
            userInfor,
            dealType:USERID == orderDetail.buyUserId ? 'buy' :'sell',//buy买币订单&sell卖币订单
            icon : orderDetail.market.slice(0,-4),//币种
            orderCode:orderDetail.recordNo,//订单编号
            dealProcess,//订单完成状态      doing正在进行&&done已经完成  
            dealStatue,//订单进程状态    doing:[hasOrder(已经下单或者正要下单),hasPay(待付款或者已经下单)]   //done:[pass(交易完成),cancel(交易取消),error(异常订单),appeal(申述)]
            statusName:orderDetail.statusName,             
            appealStatue:orderDetail.complainStatus,//订单是否有申述   2:订单已经完成申述 1:申述中 0:未受理
            leaveTimes:orderDetail.timeDiff,//交易剩余时间
            buyUserMoblie:orderDetail.buyUserMoblie,//买方手机号
            sellUserMoblie:orderDetail.sellUserMoblie,//卖方手机号
            sellUserColor:orderDetail.sellUserColor,//卖方头像色
            buyUserColor:orderDetail.buyUserColor,//买方头像色
            buyUserId:orderDetail.buyUserId,
            sellUserId:orderDetail.sellUserId,
            adUserId:orderDetail.adUserId,//广告方ID
            backUseId: USERID == orderDetail.buyUserId ? orderDetail.sellUserId : orderDetail.buyUserId,//对面用户id
            backUseColor:USERID == orderDetail.buyUserId ? orderDetail.sellUserColor : orderDetail.buyUserColor,//对面用户头像颜色
            selfUserColor:USERID == orderDetail.buyUserId ? orderDetail.buyUserColor : orderDetail.sellUserColor,//自己头像颜色
            complainId:orderDetail.complainId,//申诉ID
            remindTimes:orderDetail.remindTimes,//提醒卖家次数 大于3不提示
            appealId:orderDetail.appealId,//是否发起申诉
            backNickname:USERID == orderDetail.buyUserId ? orderDetail.sellUserName : orderDetail.buyUserName,//对方的昵称
            backCardname:USERID == orderDetail.buyUserId ? orderDetail.sellerCardName : orderDetail.buyerCardName,//对方真实姓名
            selfCardname:USERID == orderDetail.buyUserId ? orderDetail.buyerCardName : orderDetail.sellerCardName//自己真实姓名
        }

    return Promise.resolve(rtData);
}

export { getOrederList,getOrederNum,getOrderDetail};