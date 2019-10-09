
import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import {ThemeFactory, Styles} from '../transition';
import { connect } from 'react-redux';
import Pages from "../page";
import {pageClick} from "../../redux/module/session";
import { PAGEINDEX, PAGESIZE, USERID, DATA_TIME_FORMAT, DATA_TIEM_EN } from '../../conf';
import moment from 'moment';
import cookie from 'js-cookie';
import { separator } from '../../utils';
const BigNumber = require('big.js');

@connect(
    state => ({
        userInfor:state.session.userInfor,
    }),
    {
        pageClick
    }
)
class RecentList extends React.Component{
    constructor(props){
        super(props);
        let propsData = this.props.tabData || {}
        this.state = {
            tabList: propsData.list || [],
            totalPage: propsData.totalPage || 0,
            pageSize: propsData.pageSize || PAGESIZE,
            count: propsData.totalCount || 0,
            pageIndex: propsData.currPage || 0,
        };
        this.currentPageClick = this.currentPageClick.bind(this)
    }
    componentDidMount(){
    }
    componentWillReceiveProps(props) {
    	let propsData = props.tabData || {} 
        this.setState({
            tabList: propsData.list || [],
            totalPage: propsData.totalPage || 0,
            pageSize: propsData.pageSize || PAGESIZE,
            count: propsData.totalCount || 0,
            pageIndex: propsData.currPage || 0,
        });
    }
    currentPageClick(values) {
		this.props.pageClick(300);
        this.setState({
            pageIndex:values
        },() => {
            this.props.recentPageClick(values)
        });
    }
    render(){
        const {tabList, loading, totalPage, pageSize, pageIndex, count } = this.state
        const {formatMessage} = this.props.intl
        const LAN = cookie.get('zlan')
        return (
            <React.Fragment>
                <div className="table_box">
                    <table className="table_content">
                        <thead>
                        <tr>
                            <th className="text-left"><FormattedMessage id="订单编号." /></th>
                            <th className="text-right"><FormattedMessage id="类型" /></th>
                            <th className="text-right"><FormattedMessage id="交易对象" /></th>
                            <th className="text-right"><FormattedMessage id="货币" /></th>
                            <th className="text-right"><FormattedMessage id="单价" /></th>
                            <th className="text-right"><FormattedMessage id="交易数量" /></th>
                            <th className="text-right"><FormattedMessage id="交易金额" /></th>
                            <th className="text-right"><FormattedMessage id="状态" /></th>
                            
                        </tr>
                        </thead>
                        <tbody id="historyEntrustList">
                        {
                            tabList.length > 0? tabList.map((item,index) => {
                                return (
                                    <tr key={index}>
                                        <td className="text-left high_light" onClick={() => {window.location.href=`/otc/orderDetail/${item.id}`}}>{item.recordNo}</td>
                                        <td className={`text-right ${item.dealType == 'sell' ? 'red': 'green'}`}>{item.dealType == 'sell' ? formatMessage({id:"出售"}):formatMessage({id:"购买"})}</td>
                                        <td className="text-right high_light" onClick={() => {this.props.showUserCenter(item)}}>{item.buyUserId == USERID ? item.sellUserName: item.buyUserName}</td>
                                        <td className="text-right">{item.market}</td>
                                        <td className="text-right">{separator(new BigNumber(item.coinPrice || 0).toFixed(2))} CNY</td>
                                        <td className="text-right">{new BigNumber(item.coinNumber).toFixed(item.marketL)} {item.market}</td>
                                        <td className="text-right">{separator(new BigNumber(item.coinPriceTotal || 0).toFixed(2))} CNY</td>
                                        <td className="text-right">{formatMessage({id: item.statusName})}</td>
                                    </tr>
                                )
                            }):
                                loading ?
                                    (
                                        <tr>
                                            <td className="loading_td" colSpan="8">
                                                {ThemeFactory.getThemeInstance(Styles.ThemeA)}
                                            </td>
                                        </tr>
                                    )
                                    :
                                	(
                                		<tr className="nodata">
		                                    <td className="billDetail_no_list" colSpan="8">
		                                        <p className="entrust-norecord">
		                                            <svg className="icon" aria-hidden="true">
		                                                <use xlinkHref="#icon-tongchang-tishi"></use>
		                                            </svg>
		                                            <FormattedMessage id="当前项无相关数据" />
		                                        </p>
		                                    </td>
		                                </tr>
                            		)
                        }
                        </tbody>
                    </table>
                </div>
                {
                    totalPage > 1 &&
                    <div className="tablist">
                        <Pages
                            pageIndex={pageIndex}
                            pagesize={pageSize}
                            total={count}
                            ref="pages"
                            currentPageClick={this.currentPageClick}
                        />
                    </div>
                }
            </React.Fragment>
        )
    }
}

export default injectIntl(RecentList);