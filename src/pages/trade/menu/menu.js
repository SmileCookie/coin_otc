import React from 'react'
import { Link } from 'react-router'
import { FormattedMessage,injectIntl} from 'react-intl';
import cookie from 'js-cookie'
import './menu.css'

class TradeMenu extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            lang:cookie.get("zlan")
        }
        this.getLan = this.getLan.bind(this);
    }

    getLan(){
        let {locale} = this.props.intl;
        //  console.log(locale)
        let _lan = null;
        if(locale == 'en'){
            _lan = 'en'
        }
        if(locale == 'zh'){
            _lan = 'cn'
        }
        if(locale == 'zh-hant-hk'){
            _lan = 'hk'
        }
        return _lan
    }
    render(){
        let lang  = this.getLan();
        return(
            <div className={`trade_slider_nav trade_slider_nav_${lang}`}>
                <ul>
                    <li>
                        <Link to={`/bw/trade/${this.props.curMarket||''}`} activeClassName="active">
                            <i className="iconfont icon-bibijiaoyi-xuanzhong"></i>
                            <FormattedMessage id="币币交易" />
                        </Link>
                    </li>
                    <li>
                        <Link to="/bw/multitrade" activeClassName="active">
                            <i className="iconfont icon-duopingkanban-xuanzhong"></i>
                            <FormattedMessage id="多屏看板" />   
                        </Link>
                    </li>
                    <li>
                        <Link to="/bw/announcements" activeClassName="active">
                            <i className="iconfont icon-gonggao-xuanzhong"></i>
                            <FormattedMessage id="公告" />
                        </Link>
                    </li>
                    <li>
                        <Link to="/bw/news" activeClassName="active">
                            <i className="iconfont icon-xinwen-xuanzhong"></i>
                            <FormattedMessage id="新闻" />
                        </Link>
                    </li>
                </ul>
            </div>
        )
    }
}

export default injectIntl(TradeMenu);









































