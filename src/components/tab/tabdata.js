import { formatURL } from '../../utils';
import qs from 'qs';

export default (intl, all = '') => {
    all && (all = '?' + qs.stringify(all));

    return [
        {
            tith: intl.formatMessage({id: "谷歌二次认证"}),
            link: formatURL('loginAuthGOne' + all)
        },{
            tith: intl.formatMessage({id: "短信二次认证"}),
            link: formatURL('loginAuthSmsOne' + all)
        }
    ];
};

// security
const security = (intl) => {
    return[
        {
            tith: intl.formatMessage({id: "谷歌验证"}),
            link: ''
        },
        {
            tith: intl.formatMessage({id: "nuser108"}),
            link: ''
        }
    ]
};

export { security };

const historyList =(intl) =>{
    return[
        {
            tith: intl.formatMessage({id: "充值记录"}),
            link: ''
        },
        {
            tith: intl.formatMessage({id: "withdraw.text21"}),
            link: ''
        }, {
            tith: intl.formatMessage({id: "DISTRIBUTION"}),
            link: ''
        }
    ] 
}
export { historyList };


const historyListSp =(intl) =>{
    return[
        {
            tith: intl.formatMessage({id: "充值记录"}),
            link: ''
        },
        {
            tith: intl.formatMessage({id: "withdraw.text21"}),
            link: ''
        }
    ] 
}
export { historyListSp };

const fbHistoryList = (intl) => {
    return [
        {
            tith: intl.formatMessage({id: "划转记录"}),
            link: ''
        },
        {
            tith: intl.formatMessage({id: "DISTRIBUTION"}),
            link: ''
        }
    ]
};
export { fbHistoryList };

const cointranList =(intl) =>{
    return[
         {
            tith: intl.formatMessage({id: "划转记录"}),
            link: ''
        },{
            tith: intl.formatMessage({id: "DISTRIBUTION"}),
            link: ''
        }
    ] 
}
export { cointranList };

const capitalList =(intl) =>{
    return[
        {
            tith: intl.formatMessage({id: "我的钱包"}),
            link: ''
        }, {
            tith: intl.formatMessage({id: "币币账户"}),
            link: ''
        }
    ] 
}
export { capitalList };


const MoneyList = (intl) => {
    return [
        {
            tith: intl.formatMessage({id: "划转记录"}),
            //link: formatURL('fbchargeDownHistory?'+qs.stringify({type:2, fl: 0}))
            link: '',
        },
        {
            tith: intl.formatMessage({id: "投资记录"}),
            //link: formatURL('fbchargeDownHistory?'+qs.stringify({type:2, fl: 1}))
            link: '',
        },
        {
            tith: intl.formatMessage({id: "收益记录"}),
            link: '',
            //link: formatURL('fbchargeDownHistory?'+qs.stringify({type:2, fl: 2}))
        }
    ]
};
// 理财中心
const Mc = (intl) => {
    return [
        {
            tith: intl.formatMessage({id: "信息总览"}),
            //link: formatURL('fbchargeDownHistory?'+qs.stringify({type:2, fl: 0}))
            link: '',
        },
        {
            tith: intl.formatMessage({id: "VIP奖励"}),
            //link: formatURL('fbchargeDownHistory?'+qs.stringify({type:2, fl: 0}))
            link: '',
        },
        {
            tith: intl.formatMessage({id: "推广奖励"}),
            //link: formatURL('fbchargeDownHistory?'+qs.stringify({type:2, fl: 0}))
            link: '',
        }
    ]
};
export { MoneyList, Mc };