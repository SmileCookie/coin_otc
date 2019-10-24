export default function (pathname) {

    //统一全局调用入口在 语言切换组件中

    //针对币币交易参数的多种情况统一限制
    if (pathname.includes("/bw/trade")) {
        //币币交易下的
        return "币币交易";
    } else if(pathname.includes("/bw/news")){

        return "新闻"

    }else if(pathname.includes("/bw/announcements")){

        return "公告"
    }else if(pathname.includes('forgotPwd')||pathname.includes('fgPwd')){
        return "nuser106" //忘记密码

    }else{

        switch (pathname) {

            //操作是写在app入口页的组件中的
            case "/bw/login/": //登陆
                return "登录"
                break;
            case "/bw/login": //登陆
                return "登录"
                break;
            case "/bw/signup": //注册
                return "注册"
                break;
            case "/bw/entrust/current": //当前委托
                return "当前委托_w"
                break;
            case "/bw/entrust/list": //历史委托
                return "ORDER-HISTORY"
                break;
            case "/bw/entrust/transrecord": //历史成交
                return "TRADE-HISTORY"
                break;
            case "/bw/manage/account/currency": //交易账户
                return "交易账户"
                break;
            case "/bw/manage/account/fcurrency": //交易账户
                return "交易账户"
                break;
            case "/bw/manage/account/lcurrency": //交易账户
                return "交易账户"
                break;
            case "/bw/manage/account/chargeDownHistory": //我的钱包历史记录
                return "历史记录"
                break;
            case "/bw/manage/account/fbchargeDownHistory": //交易账户历史记录
                return "历史记录"
                break;
            case "/bw/mg/account": //个人中心
                return "Account"
                break;
            case "/bw/mg/grade": //个人中心
                return "Account"
                break;
            case "/bw/mg/loginLog": //个人中心
                return "Account"
                break;
            case "/bw/chargeList/leve": //手续费
                return "footer.text16"
                break;
            case "/bw/multitrade": //多屏看板
                return "多屏看板"
                break;
            case "/bw/money": //阿波罗计划
                return "阿波罗计划"
                break;


                //涉及到动态的相关页面，这里返回的值是作为判断禁止高级页面的覆盖
                //操作是写在页面里的组件中的
            case "/bw/manage/account/charge": //充值 涉及到动态写在页面中
                return "充值"
                break;
            case "/bw/manage/account/download": //提现
                return "提现"
                break;

                //我的钱包和交易账户因为 从多屏看板跳过去的时候路由地址没有发生变化，所以通过页面级进行操作
            case "/bw/manage/account/balance": //我的钱包
                return "我的钱包"
                break;
            
            default:
                return "默认首页"


        }

    }
}