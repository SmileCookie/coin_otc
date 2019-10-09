/**
 * @description mock B 范例
 * @author luchao.ding
 */
import confs from 'conf';

// 获取mock的默认数据
const { defaultData } = confs;

export const orderLists = {
       list:[
                {
                        buyUserFee: defaultData,//买方手续费
                        buyUserName: defaultData,//买方姓名
                        coinNumber: 0,//交易数量
                        coinPrice: 0,//币种单价
                        coinTime: 0,//交易时间
                        type:0,
                        myMoney:0,//总额
                        adType:null,
                        id:defaultData,
                        market: "--/--",//交易市场
                        orderId: defaultData,//广告ID
                        recordNo: defaultData,//订单编号
                        sellUserFee: defaultData,//卖方手续费率
                        sellUserName: defaultData,//卖方姓名
                        statusName: defaultData,//交易状态
                        buyUserId:defaultData,//买方用户ID
                        sellUserId:defaultData,//卖方用户ID
                        buyUserFeeStr:0,//买方手续费字符串
                        sellUserFeeStr:0,//卖方手续费字符串
                        buyerCardName:null,//买方真实姓名
                        sellerCardName:null//卖方真实姓名

                }
       ],
       totalCount:0,
       totalPage:0,
       currPage:0
}

export const orderDetail = {
        id:defaultData,//订单id
        type:0,//买、卖广告类型
        recordNo:defaultData,//订单编号
        market:'--/--',//市场
        status:0,//订单状态码
        statusName:defaultData,//订单状态
        myMoney:0,//付款金额
        coinNumber:1,//交易数量
        coinPrice:0,//单价
        buyUserId:0,//买方id
        buyUserColor:null,//买方头像颜色
        buyUserFee:0,//买方手续费
        buyUserMoblie:defaultData,//买方手机号
        sellUserFee:0,//卖方手续费
        sellUserId:0,//卖方id
        sellUserColor:null,//卖方头像颜色
        sellUserMoblie:defaultData,//卖方手机号
        finalNum:0,//实际到账
        coinTime:0,// 交易时间
        payRemark:null,//付款备注
        complainStatus:null,//申述状态
        complainStatusName:defaultData,//申述状态名称
        timeDiff:0,//剩余时间
        adUserId:0,//广告方ID
        complainId:null,//申诉id
        remindTimes:null,//提醒卖家次数 大于3不提示
        appealId:0,
        sellUserName:null,//卖家昵称
        buyUserName:null,//买家昵称
        buyUserFeeStr:0,//买家手续费字符
        sellUserFeeStr:0,//卖家手续费字符
        finalNumStr:0,//成交价字符
        buyerCardName:'--',//买方真实姓名
        sellerCardName:'--'//卖方真实姓名

}

// export default {orderLists};
