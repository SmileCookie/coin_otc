/**
 * @description 本周新VIP加成
 * @author luchao.ding
 * @since 2019/08/05
 */ 
import React from 'react';
import { FormattedMessage, } from 'react-intl';
import TrResult from '../../components/data/trResult';
import {formatDate} from '../../utils'; 
 
export default (props) => {
    const {isLoading, list} = props;
    // console.log(isLoading, list, '====>');
    return(
        <table className="table-history" width="100%">
            <thead>
                <tr>
                    <th style={{width:'15%'}}><FormattedMessage id="日期" /></th>
                    <th><FormattedMessage id="新人加成总额" /></th>
                    <th><FormattedMessage id="新人加成总数" /></th>
                    <th><FormattedMessage id="获得数量" /></th>
                    <th><FormattedMessage id="市场价格" /></th>
                    <th><FormattedMessage id="估值" /></th>
                </tr>
            </thead>
            <tbody>
            {
                    !isLoading && list.length 
                    ?
                    list.map((item, i) => {
                        return(
                            <tr key={item.id}>
                                <td>{formatDate(new Date(+item.distStartTime)).replace(',', '').replace(/\//g, '-').replace(/\d{2}:\d{2}:\d{2}/,'')} - {formatDate(new Date(+item.distEndTime)).replace(',', '').replace(/\//g, '-').replace(/\d{2}:\d{2}:\d{2}/,'')}</td>
                                <td><span className="my_vds_wp">{item.newVipWeekAmount}</span></td>
                                <td>{item.newVipWeekUser}</td>
                                <td><span className="my_vds_wp">{item.bonus_price}</span></td>
                                <td>{item.vds_price} USDT</td>
                                <td>{item.true_price} USDT</td>
                            </tr>
                        )
                    })
                    :<TrResult isLoading={isLoading} cols='6' />
                }
            </tbody>
        </table>
    )
};