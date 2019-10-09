/**
 * @description 广告管理
 */
let obj = {
    id: '--', // id
    orderNo: '--', // 订单编号
    coinTime: '--', // 交易时间
    type: '--', //0买 1卖
    coinNumber: '--', // 交易数量
    coinPrice: '--', // 交易金额
    status: '--',//1-等待付款；2-已付款；3-交易完成；4-交易取消；5-异常订单；6-申诉中
    sellUserName: '--', // 卖家名称
    sellerCardName: '--', // 卖家真实名称
    buyUserName: '--', // 买家名称
    buyerCardName: '--', // 买家真实名称
    sellUserId: '', // 卖家id
    buyUserId: '', // 买家id
    recordNo: '--', // 订单编号

};
// 广告列表的moc
export const MockPage = {
    id: '--', // id
    orderStatus  : '--', // 0-已上架；1-已下架；2-以隐藏；
    orderType: '--', // 订单类型:0-买；1-卖
    coinComplateNumber: '--', // 交易完成数量
    coinPrice: '--', // 币种价格
    coinTotalNumber: '--', //币种数量
    coinTotal: '--', //币种数量
    coinTypeId: '--', // 币种类型
    coinTypeName: '--', // 币种类型名
    legalTypeId: '--',//法币类型
    maxNumber: '--', // 最大限额
    minNumber: '--', // 最小限额
    orderNo: '--',// 广告编号
    reason: '--', // 下架或者隐藏原因
    orderTime: '--', // 创建时间
    typeOperation: '--', //是否显示上架按钮
    deductFeeSum: '--', // 已扣手续费
    deductFeeSumNew: 0, // 新的已扣手续费
    lastNumber: '--', // 剩余数量
    otrList: [obj]
};
export const MockListObj = obj;
