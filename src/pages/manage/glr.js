/**
 * @description 获取回馈
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
                    <th><FormattedMessage id="日期" /></th>
                    <th><FormattedMessage id="数量" /></th>
                    <th><FormattedMessage id="市场价格" /></th>
                    <th><FormattedMessage id="估值" /></th>
                    <th><FormattedMessage id="VID地址" /></th>
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
                                <td><span className="my_vds_wp">{item.bonus_price}</span></td>
                                <td>{item.vds_price} USDT</td>
                                <td>{item.true_price} USDT</td>
                                <td>{item.remark}</td>
                                <td>{item.dealflagName}</td>
                            </tr>
                        )
                    })
                    :<TrResult isLoading={isLoading} cols='6' />
                }
            </tbody>
        </table>
    )
};