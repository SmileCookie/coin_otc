/**
 * @description 回本详情
 * @author luchao.ding
 * @since 2019/08/05
 */ 
import React from 'react';
import { FormattedMessage, } from 'react-intl';
import TrResult from '../../components/data/trResult';
import { formatDate } from '../../utils'; 

export default (props) => {
    const { isLoading, list } = props;
    // console.log(isLoading, list, '====>');
    return(
        <table className="table-history" width="100%">
            <thead>
                <tr>
                    <th><FormattedMessage id="回本日期" /></th>
                    <th><FormattedMessage id="投资基数" /></th>
                    <th><FormattedMessage id="t-理论收益" /></th>
                    <th><FormattedMessage id="回本数量" /></th>
                    <th><FormattedMessage id="复投" /></th>
                </tr>
            </thead>
            <tbody>
            {
                    !isLoading && list.length 
                    ?
                    list.map((item, i) => {
                        return(
                            <tr key={item.id}>
                                <td>{formatDate(new Date(+item.dealTime)).replace(',', '').replace(/\//g, '-')}</td>
                                <td>{item.investAmount} USDT</td>
                                <td>{item.expectProfitUsdt} USDT</td>
                                <td>{item.recoveryUsdt} USDT</td>
                                <td>{item.returnTypeDESC}</td>
                            </tr>
                        )
                    })
                    :<TrResult isLoading={isLoading} cols='5' />
                }
            </tbody>
        </table>
    )
};