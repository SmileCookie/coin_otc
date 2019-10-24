/**
 * @description 超级主节点累积分配
 * @author luchao.ding
 * @since 2019/08/05 
 */ 
import React from 'react';
import { FormattedMessage, } from 'react-intl';
import TrResult from '../../components/data/trResult';
import {formatDate} from '../../utils'; 
const BigNumber = require('big.js')

export default (props) => {
    const {isLoading, list, total, totalWeight} = props;
    // console.log(isLoading, list, '====>');
    let F = 0;
    try{
        F = new BigNumber(total).div(totalWeight).toFixed(0);
        // console.log(total, totalWeight, '@2@@@@@@@');
    }
    catch(e){

    }
    return(
        <table className="table-history" width="100%">
            <thead>
                <tr>
                    <th><FormattedMessage id="日期" /></th>
                    <th><FormattedMessage id="收益笔数" /></th>
                    <th><FormattedMessage id="分配数量" /></th>
                    <th><FormattedMessage id="市场价格" /></th>
                    <th><FormattedMessage id="分配估值" /></th>
                    <th style={{textAlign:'center'}}><FormattedMessage id="全网分红权" />
                    <p><FormattedMessage id="(1分红权≈18 USDT)" values={{p:F}} /></p></th>
                    <th><FormattedMessage id="状态-1" /></th>
                </tr>
            </thead>
            <tbody>
            {
                    !isLoading && list.length 
                    ?
                    list.map((item, i) => {
                        return(
                            <tr key={item.id}>
                                <td>{formatDate(new Date(+item.bonus_time)).replace(',', '').replace(/\//g, '-')}</td>
                                <td>{item.superNodeProfitCountStr}</td>
                                <td><span className="my_vds_wp">{item.bonus_price}</span></td>
                                <td>{item.vds_price} USDT</td>
                                <td>{item.true_price} USDT</td>
                                <td style={{textAlign:'center'}}>{item.superNodeProfitVipWeight}</td>
                                <td>{item.dealflagName}</td>
                            </tr>
                        )
                    })
                    :<TrResult isLoading={isLoading} cols='7' />
                }
            </tbody>
        </table>
    )
};