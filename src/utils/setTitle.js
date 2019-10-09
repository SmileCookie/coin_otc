
// 法币交易相关

export default function (pathname) {
    if(pathname.includes('/otc/buySell')||pathname.includes('/otc/trade')){
        return "购买";
    }else if(pathname.includes('/otc/advertiseDetails')){
        return "广告详情";

    }else if(pathname.includes('/otc/orderDetail')){
        return "订单详情";
    }else if(pathname.includes('/otc/buyOrSellAdvertisement')) {
        return "发布广告";
    }else{
    switch (pathname) {


        //路由跳转块的，根据地址更改即可
        //操作是写在app入口组件中的
        case "/otc/advertisement":
            return "广告管理_1";
            break;

        case "/otc/otcOrder":
            return "我的订单";
            break;
        // case "/otc/business":     //商家认证和工作台的都在这个里面
        //     return "商家认证";
        //     break;

        default:
            return "默认首页";
    }
}
}
