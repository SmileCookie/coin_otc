/**
 * @description 广告管理
 */
let obj = {
    id: '--', // id
    orderStatus  : '--', // 0-已上架；1-已下架；2-以隐藏；
    orderStatusName: '--',
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
    flag: '',  // 图章
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
