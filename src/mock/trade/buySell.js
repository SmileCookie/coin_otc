/**
 * @description 购买出售虚拟货币
 */
// 广告详情Moc
export const DetailsMoc = {
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
    market: '--',// 市场
    orderTime: '--', // 创建时间
    color: '--', // 头像颜色
    onlineStatus: '0',
};
// 购买需要校验的
export const CheckInfo = {
    configInfo: {
        coinBixBalance: '--', // 余额
        adValidTimeConf: '--',// 广告有效期
        coinBixDian: '--', //虚拟货币小数点位数
        legalBixDian: '--',//法币小数点位数
    },
    adInfo: {
        remarks: '--', // 广告商备注
    }
};
export const CheckMoc = {
    coinBixBalance: '--', // 余额
    adValidTimeConf: '--',// 广告有效期
    coinBixDian: '--', //虚拟货币小数点位数
    legalBixDian: '--',//法币小数点位数
    orderOverTime: '--', // 付款超时时间
    userCancleNumConf: '3', // 取消次数
};
