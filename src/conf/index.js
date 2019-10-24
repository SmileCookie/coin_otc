//默认语言
export const DEFAULT_LOCALE = "en";
//获取交易数据间隔时间
export const FETCH_TRADE_INTERVAL = 3000;
//获取资金数据间隔时间
export const FETCH_ACCOUNT_INTERVAL = 3000;
//倒计时1秒
export const COUNTDOWN_INTERVAL = 1000;
//域名、url相关
const LOCATION = window.location;
const PROTOCOL = LOCATION["protocol"];
export const DOMAIN_BASE = document.domain.split('.').slice(-2).join('.');
export const DOMAIN_VIP = PROTOCOL + "//" + LOCATION["host"];
export const DOMAIN_TRANS = PROTOCOL + "//" + "t." + DOMAIN_BASE;
export const DOMAIN_COOKIE = "." + DOMAIN_BASE;
// 迁移基路径
export const BASE_UIR = '/bw/';

export const SECRET = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJG14+94DEgzyd6G8+Ue+lpLKK9uIftpSZ7wvnX3jtw+6SUKldkvL1mYq9W8qIJD7w5t3YQIkVoWIlm5Eba5NcDYgfDC/QnYyr9zfDthlJECvQ8TC0wjy9cOtCC4FntewsqmGxLjTA17Zn0RJpsqXvNFjZEinR6IawvnlhPKJ/IwIDAQAB'


// export const OTC_UIR = PROTOCOL + "//" + "otc." + DOMAIN_BASE;  // 生产环境
// export const OTC = '/otc';  // 调试环境

export const OTC_UIR = DOMAIN_VIP;   // 调试环境
export const OTC = process.env.NODE_ENV == "production" ? '/otcweb' : "/otc";  // 生产环境
console.log('OTC ===== 路径', OTC)
// 证件类型号
// 1 cardId 2 passport
export const CARDTYPES = [1,2];

//图片验证码地址
export const URL_IMG_CODE = DOMAIN_VIP + "/imagecode/get-28-100-50";
//手机认证图片验证码
export const MOBILE_IMG_CODE = DOMAIN_VIP + "/imagecode/get-28-80-32";
// qiniu
export const UPLOAD_PATH = 'https://upload.qiniup.com/';
//cookie相关
export const COOKIE_PREFIX = "z";
export const COOKIE_IS_LOGGIN = COOKIE_PREFIX + "uon";
export const COOKIE_UID = COOKIE_PREFIX + "uid";
export const COOKIE_UNAME = COOKIE_PREFIX + "uname";
export const COOKIE_LOGIN_STATUS = COOKIE_PREFIX + "loginStatus";
export const COOKIE_LAN = COOKIE_PREFIX + "lan";
export const COOKIE_IP_AUTH = COOKIE_PREFIX + "ipauth";
export const COOKIE_GOOGLE_AUTH= COOKIE_PREFIX + "googleauth";
export const COOKIE_FROM_URL= COOKIE_PREFIX + "fromurl";
export const COOKIE_MARKETS_FAV = "userCollectMarket";
export const COOKIE_MONEY = "currency";
//默认法币
export const DEFAULT_MONEY = "USD";
export const CONF_MONEY={
                            USD:"$",
                            CNY:"¥",
                            EUR:"€",
                            GBP:"£",
                            AUD:"A$"
                        }
//cookie过期天数
export const COOKIE_EXPIRED_DAYS = 3000;

//弹窗消失时间
export const DISMISS_TIME = 2000;

//分页页码
export const PAGEINDEX = 1;
export const PAGESIZE = 10;
export const BBYH_PAGESIZE = 15;
export const PAGESIZEFIVE = 5;
export const PAGESIZETHIRTY = 30;
export const PAGESIZESIX = 60;

//分页按钮的个数
export const PAGEBTNNUMS = 5;

// 判断登录设备 APP 网页
export const LOGINVIEWPORT = 1;
// 首次登录cookie name
export const COOKIE_FIRST = 'first';

export const STATUS = 211;

//币种保留位数
export const COIN_KEEP_POINT = 8
//默认币种 BTC
export const DEFAULT_COIN_TYPE = 'btc'
export const DEFAULT_MARKETCOIN_TYPE = 'btc_usdt'

//一分钟
export const COUNT_DOWN_ONE_MINUTE = 60
//手机认证 codeType
export const MOBILE_AUTH_CODETYPE = 2
//手机修改 codeType
export const MOBILE_MODIFY_CODETYPE = 3
//百分比 保留位数
export const PERCENTAGE = 2
// 修改资金密码
export const MONEY_MODIFY_CODETYPE = 18
// 安全设置
export const SAFEAUTH = 14
//手机默认国家区号
export const MOBILE_COUNTRY_CODE = "+86"
// 跳转地址
export const REDIRECT = {
    MODIFY_MONEY: BASE_UIR + 'manage/user/safePwd',
}

//积分等级规则
export const LEVEL_RULES = [[0,100.0,"--"],[10000,90.0,"--"],[30000,80.0,"--"],[60000,70.0,"--"],[100000,60.0,"--"],[150000,50.0,"--"],[300000,45.0,"--"],[500000,40.0,"--"],[800000,35.0,"--"],[1200000,30.0,"--"],[2000000,0,"--"]]
// 交易页各组件刷新频率
export const FETCH_MARKETS_DATA = 5000
export const FETCH_MARKETS_INFO = 1000 //之前 2000
export const FETCH_MARKETS_DEPTH = 60000
export const FETCH_HISTORY_INFO = 1000  //之前 3000
export const FETCH_SUMMARY_DATA = 5000
export const FETCH_ENTRUST_RECORD = 3000
export const FETCH_REPO_INFO = 3000
//期货刷新频率
export const FETCH_FUTURE_RATE = 1000
//合约简介
export const FETCH_FUTURE_DEALDES_RATE = 5000

//页面 header 高度
export const HEADER_HEIGHT = 40
export const HEADER_HEIGHT_NEW = 78
export const HEADER_HEIGHT_MARGIN = 8
export const ERRORCONFIG = 1//打开监控异常
// 登录二次验证
export const SECOND = 63
// 登录页面上的url
export const AUTH_URL = 'https://www.btcwinex.com';

//交易总额 保留8位
export const EXHANGETOTALDIAN = 8
// 交易页面
export const TRADE = 'trade';

// 首页
export const IDX = '/';

// 登录页
export const LOGINR = BASE_UIR + 'login';

// 用户中心页
export const USERCENTER = BASE_UIR + 'mg/account';

// 新交易页
export const NTRADE = BASE_UIR + 'trade';

// 人工客服
export const KF = BASE_UIR;

// 二次认证
export const SAUTH = BASE_UIR + 'loginAuthRoute';

// 设置密码
export const SETPASSWORD = BASE_UIR + 'forgotPwdThree';

// 忘记密码首页
export const FGPWD = BASE_UIR + 'fgPwdOne';
// 无法短信谷歌二次认证首页
export const NOTS = BASE_UIR + 'notSmsGCode';

// 用户中心
export const CT = 'mg';
export const CTS = 'mgs';
//页面内容最小高度
export const PAGEMINHEIGHT = 830;

// 账户页面
export const ACCOUNT = 'account';

// 修改手机号
export const UPMOBILE = 'dUpMobile';

// 修改手机号已有手机号
export const EBMOBILE = 'ebMobile';

// 修改手机号没有手机号
export const NEBMOBILE = 'dEbUpMobile';
//websocket  URL
export const WEBSOCKETURL = `${PROTOCOL.includes("https") ? 'wss': 'ws'}://socket.${DOMAIN_BASE}/ws`;
export const OTCWEBSOCKETURL = 'ws://192.168.3.9:8888';

//export const WEBSOCKETURL = `ws://socket.bitstaging/ws`;

// 交易相关 url
export const TRADEGEADURL = ["/bw/trade","/bw/multitrade","/bw/announcements","/bw/news","/bw/newsDetail","/bw/announcementsDetail","/bw/margin"]
// 用户中心账户、等级、登录日志
export const USERCENTERTABBASE = BASE_UIR + CT + '/';
export const USERCENTERTAB = [
    {
        name: '账户',
        link: USERCENTERTABBASE + 'account',
    },
    {
        name: '等级',
        link: USERCENTERTABBASE + 'grade',
    },
    {
        name: '登录日志',
        link: USERCENTERTABBASE + 'loginLog',
    }
];

//资金管理我的钱包、币币账户、法币帐户、期货帐户
export const MONEYMANAGEMENTBASE = BASE_UIR + 'manage/account/';
export const MONEYMANAGEMENT = [
    {
        name: '我的钱包',
        link: MONEYMANAGEMENTBASE + 'balance',
    },
    {
        name: '币币账户',
        link: MONEYMANAGEMENTBASE + 'currency',
    }
];

//期货默认合约名称代码
export const DEFAULT_FUTURE_DEAL = 'btc-usdc'
//默认日期
export const DATA_FORMAT = 'YYYY-MM-DD'
export const DATA_FORMAT_FUTURE = 'YYYY/MM/DD'
export const DATA_FORMAT_EN = 'MM-DD-YYYY'
export const FUTURE_DETAL_POS = 4

export const EOSTYPE = ['BTS','XEM','STEEM','EOS']
export const TIMER = {}

//委托列表 50条
export const PRACTICE_LIST_NUM = 50

// 默认值
export const DFData = '--';

export const USDTARGLIST = [
    {
        value:0,
        name:'OMIN协议'
    },
    {
        value:1,
        name:'ERC20协议'
    }
]

//otc webscoket channel
export const otcChannel = "otc.order.deal"
