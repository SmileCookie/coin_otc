/**
 * otc config
 * @author luchao.ding
 */
// development, production
const ENV = process.env.NODE_ENV;
import Cookie from 'js-cookie'
//域名、url相关
const LOCATION = window.location;
const PROTOCOL = LOCATION["protocol"];
export const DOMAIN_BASE = document.domain.split('.').slice(-2).join('.');
const PASSVIP =  ENV == 'development' ? '/bbApi': '';
export const DOMAIN_VIP = PROTOCOL + "//" + LOCATION["host"] + PASSVIP;
//export const DOMAIN_TRANS = PROTOCOL + "//" + "t." + DOMAIN_BASE;
export const DOMAIN_COOKIE = "." + DOMAIN_BASE;
let transApi = PROTOCOL + "//" + "t." + DOMAIN_BASE;
//console.log('+++++++++++++++++++++++++++++++++++++',DOMAIN_BASE.includes('common'));

if(ENV == 'development'){
    transApi = PROTOCOL + "//" +  LOCATION["host"] + '/transApi';
}
export const DOMAIN_TRANS = transApi

const BaseConfig = {
    axiosTimeout: 0,
    defaultData: '--',
    ROOTPATH :'/otc',


};

const Configs = {
    production: {
        APPKEY: "pkfcgjstpza58",// 测试环境 'bmdehs6pbg5fs', //凯里 n19jmcy5n8tz9 // 线上环境 pkfcgjstpza58   // 发版时修改        todo
        api: '/otcweb',
        BBAPI:DOMAIN_VIP

    },
    development: {
        APPKEY: "pkfcgjstpza58",
        api: '/api/otcweb',
        BBAPI:DOMAIN_VIP
    }
};

const configs = {...BaseConfig, ...Configs[ENV]};

export default configs;

//otc+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//分页页码
export const PAGEINDEX = 1;
export const PAGESIZE = 10;

//默认语言
export const DEFAULT_LOCALE = "en";

//获取用户id
export const USERID = Cookie.get('zuid')

//获取用户语言环境
export const LAN =  Cookie.get('zlan')

//资金密码秘钥
export const SECRET = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJG14+94DEgzyd6G8+Ue+lpLKK9uIftpSZ7wvnX3jtw+6SUKldkvL1mYq9W8qIJD7w5t3YQIkVoWIlm5Eba5NcDYgfDC/QnYyr9zfDthlJECvQ8TC0wjy9cOtCC4FntewsqmGxLjTA17Zn0RJpsqXvNFjZEinR6IawvnlhPKJ/IwIDAQAB'

//默认法币
export const DEFAULT_MONEY = "USD";
export const CONF_MONEY={
                            USD:"$",
                            CNY:"¥",
                            EUR:"€",
                            GBP:"£",
                            AUD:"A$"
                        }

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

//cookie过期天数
export const COOKIE_EXPIRED_DAYS = 3000;
//获取资金数据间隔时间
export const FETCH_ACCOUNT_INTERVAL = 5000;
// 交易相关 url
export const TRADEGEADURL = ["/bw/trade","/bw/multitrade","/bw/announcements","/bw/news","/bw/newsDetail","/bw/announcementsDetail","/bw/margin"]
//websocket  URL TODO
export const WEBSOCKETURL = `${PROTOCOL.includes("https") ? 'wss': 'ws'}://socket.${DOMAIN_BASE}/ws`;
//export const WEBSOCKETURL = 'ws://192.168.3.9:8888';
//弹窗消失时间
export const DISMISS_TIME = 2000;
//图片验证码地址
export const URL_IMG_CODE = DOMAIN_VIP + "/imagecode/get-28-100-50";


//默认日期
export const DATA_TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';

//精确到分
export const DATA_MIMUTE_FORMAT = 'YYYY-MM-DD HH:mm';

//精确到分英文
export const DATA_MIMUTE_FORMAT_EN = 'MM-DD-YYYY HH:mm';

//及时发送日期格式
export const DATA_NOW_FORMAT = 'HH:mm'

//判断是否为当天格式
export const DATA_DAY_FORMAT = 'YYYY-MM-DD'

//英文模式时间格式
export const DATA_TIEM_EN = 'MM-DD-YYYY HH:mm:ss'

//otc webscoket channel
export const otcChannel = "otc.order.deal"

//otc+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
