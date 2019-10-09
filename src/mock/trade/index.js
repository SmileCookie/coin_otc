/**
 * @description 交易列表
 */
let obj = {
    id: '--', // id
    orderStatus  : '--', // 0-已上架；1-已下架；2-以隐藏；
    orderType: '--', // 订单类型:0-买；1-卖
    coinComplateNumber: '--', // 交易完成数量
    coinPrice: '--', // 币种价格
    coinTotalNumber: '--', //币种数量
    coinTypeName: '--', // 币种类型名
    legalTypeId: '--',//法币类型
    maxNumber: '--', // 最大限额
    minNumber: '--', // 最小限额
    paymentTypes: '--', //支付方式
    userName: '--',// 用户名
    userId: '--', // 广告用户ID
    orderTotal: '--',// 总订单笔数
    color: '--', // 头像颜色
    onlineStatus: '0',
};
// 广告列表的moc
export const MockPage = {
    totalCount:0,
    totalPage:0,
    currPage:0,
    list: [obj]
};
export const MockListObj = obj;
// 币种列表
export const coinInfo = {
    coinName: '--', // 名称
    fundsType: '--', // id
};