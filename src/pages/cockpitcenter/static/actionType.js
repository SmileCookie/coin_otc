import React from 'react';
import { Icon } from 'antd';
//交易TRADE_
export const TRADE_TATE_TOTAL = '交易转化率统计';
export const TRADE_HANDICAP_DEAL = '盘口成交统计';
export const TRADE_HANDICAP_DEAL_DSIT = '盘口成交分布统计';
export const TRADE_NEW_OLD_USER_EXCHANGE = '新老客户交易构成';
export const TRADE_USER_EXCHANGE_MONEY = '用户交易金额统计';
export const TRADE_TREND = '交易趋势';
export const TRADE_ENGINE = '交易终端统计';
export const TRADE_RANKING = '平台排行统计';


//金额
export const AMOUNT_EXCHANGE_MONEY_DIST = '交易所金额分布';
export const AMOUNT_PLATFORM_MONEY_DIST = '平台账户资金分布';
export const AMOUNT_WALLET_MONEY_DIST = '钱包资金分布';
export const AMOUNT_USER_MONEY_DIST = '用户货币分布';
export const AMOUNT_WALLET_CURRENCY_DIST = '钱包货币分布';
export const AMOUNT_PRESERV_MONEY_DIST = '保值资金分布';
export const AMOUNT_MONEY_FLOW_TREND = '资金流动趋势';
export const AMOUNT_HAND_FEE_HANDICAP = '手续费盘口统计';
export const AMOUNT_HAND_FEE_CHANGE_TREND = '手续费收取趋势';
export const AMOUNT_DEPOSIT_MONEY = '沉淀资金';


//用户流量
export const USER_ACTIVE_FORM = '活跃用户构成'
export const USER_NEWUSER_TOTAL = '新增用户统计'
export const USER_INTERVIEW_FLOW_TREND = '访问流量趋势'
export const USER_EXCHANGE_NUM_RANKING = '用户交易量排行'
export const USER_CHINA_INTERVIEW = '国内用户访问分布'
export const USER_GLOBAL_INTERVIEW = '全球用户访问分布'
export const USER_GAIN_RANKING = '用户盈利排行'

//资方统计
export const EMPLOYER_REGISTER = '资方用户注册'
export const EMPLOYER_EXCHAGNE_PROP = '资方用户交易占比'
export const EMPLOYER_EXCHANGE_NUM_TREND = '资方用户交易量趋势'
export const EMPLOYER_EXCHANGE_FREQ_TREND = '资方用户交易频率趋势'
export const EMPLOYER_WITHDRAW_PROP = '资方用户提现占比统计'
export const EMPLOYER_DEPOSIT_PROP = '资方用户存入与抛售统计'
export const EMPLOYER_TO_PLATFORM_USER = '转为平台用户统计'
export const EMPLOYER_TO_X_USER = '转为X用户统计'
export const EMPLOYER_HAND_FEE_DEVOTE = '资方用户手续费贡献统计'

export const tradeRateCols1 = [{
    title: '浏览次数',
    dataIndex: 'browseNumAll',
    key: 'browseNumAll',
    // render: text => <a href="javascript:;">{text}</a>,
  }, {
    title: '委托人数',
    className: 'column-money',
    dataIndex: 'entrustnumber',
    key: 'entrustnumber',
  }, {
    title: '委托数量（笔数）',
    dataIndex: 'entrustamount',
    key: 'entrustamount',
  },{
    title: '委托币量（BTC）',
    dataIndex: 'usercoinentrust',
    key: 'usercoinentrust',
  },
  {
    title: '委托金额（USD）',
    dataIndex: 'useramountentrust',
    key: 'useramountentrust',
  }];
export const tradeRateCols2 = [{
    title: '浏览人数',
    dataIndex: 'num',
    key: 'num',
    // render: text => <a href="javascript:;">{text}</a>,
  }, {
    title: '成交人数',
    className: 'column-money',
    dataIndex: 'tradenumber',
    key: 'tradenumber',
  }, {
    title: '有效委托（笔数）',
    dataIndex: 'effectiveentrustamount',
    key: 'effectiveentrustamount',
  },{
    title: '成交币量（BTC）',
    dataIndex: 'usercointrade',
    key: 'usercointrade',
  },
  {
    title: '成交金额（USD）',
    dataIndex: 'useramounttrade',
    key: 'useramounttrade',
  }];
export const tradeHandicapCols = [{
    title: '盘口',
    dataIndex: 'entrustmarket',
    key: 'entrustmarket'
    // render: text => <a href="javascript:;">{text}</a>,
  }, {
    title: '交易单数',
    className: 'column-money',
    dataIndex: 'tradeamount',
    key: 'tradeamount'
  }, {
    title: '数量占比',
    dataIndex: 'ratesum',
    key: 'ratesum',
    render: text => {
      let str = text + '%';
      return str;
    }
  },{
    title: '交易币量（BTC）',
    dataIndex: 'usercoin',
    key: 'usercoin'
  },
  {
    title: '币量占比',
    dataIndex: 'ratecoin',
    key: 'ratecoin',
    render: text => {
      let str = text + '%';
      return str;
    }
  }];
export const tradeUserOldCols = [{
    title: '',
    dataIndex: 'usertype',
    key:'usertype',
    render: text => {
      const type = text == '0' ? '老用户' : '新用户';
      return type;
    },
  }, {
    title: '交易币量（BTC）',
    className: 'column-money',
    dataIndex: 'usercoin',
    key: 'usercoin',
  }, {
    title: '较前一月',
    dataIndex: 'coinrate',
    key: 'coinrate',
    render: text => {
      const num = text == null ? '/' : parseFloat(text) > 0 ? <span><Icon type="arrow-up" />{text}%</span>: parseFloat(text) < 0?<span><Icon type="arrow-down" />{text}%</span>:<span>0.00%</span>
      return num;
    }
  },{
    title: '交易人数',
    dataIndex: 'tradenumber',
    key: 'tradenumber',
  },
  {
    title: '较前一月',
    dataIndex: 'numberrate',
    key: 'numberrate',
    render: text => {
      const num = text == null ? '/' : parseFloat(text) > 0 ? <span><Icon type="arrow-up" />{text}%</span>: parseFloat(text) < 0?<span><Icon type="arrow-down" />{text}%</span>:<span>0.00%</span>
      return num;
    }
  }];
  export const tradeEngineCols = [{
    title: '',
    dataIndex: 'clienttype',
    // render: text => <a href="javascript:;">{text}</a>,
  }, {
    title: '交易币量（BTC）',
    className: 'column-money',
    dataIndex: 'usercoin',
  }, {
    title: '较前一月',
    dataIndex: 'coinrate',
    render: text => {
      const num = text == null ? '/' : parseFloat(text) > 0 ? <span><Icon type="arrow-up" />{text}%</span>: parseFloat(text) < 0?<span><Icon type="arrow-down" />{text}%</span>:<span>0.00%</span>
      return num;
    }
  },{
    title: '成交人数',
    dataIndex: 'tradenumber',
  },
  {
    title: '较前一月',
    dataIndex: 'numberrate',
    render: text => {
      const num = text == null ? '/' : parseFloat(text) > 0 ? <span><Icon type="arrow-up" />{text}%</span>: parseFloat(text) < 0?<span><Icon type="arrow-down" />{text}%</span>:<span>0.00%</span>
      return num;
    }
  }];
export const tradeRankCols = [{
    title: '平台名称',
    dataIndex: 'platform',
    // render: text => <a href="javascript:;">{text}</a>,
  }, {
    title: '排名',
    className: 'column-money',
    dataIndex: 'ranking',
    render: text => {
      let str = text ? text : '-';
      return str;
    },
  }, {
    title: '相对昨日',
    dataIndex: 'change',
    render: text => {
      let str = text == null ? '-' : parseFloat(text) < 0 ? <span style={{ color: '#FF0000'}}>{text}</span> : parseFloat(text) > 0 ? <span style={{color: '#008000'}}>+{text}</span>:<span>--</span>;
      return str;
    },
  }];
export const interviewFlowCols = [{
  title: '省份',
  width: 200,
  dataIndex: 'name',
}, {
  title: '访问(U/V)',
  width: 200,
  dataIndex: 'num',
}];

export const interviewWorldCols = [{
  title: '国家',
  width: 200,
  dataIndex: 'noMap',
}, {
  title: '访问(U/V)',
  width: 200,
  dataIndex: 'num',
}];