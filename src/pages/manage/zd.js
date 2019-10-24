/**
 * @description 指导奖励
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
                    <th><FormattedMessage id="新进用户" /></th>
                    <th><FormattedMessage id="建点奖励" /></th>
                    <th><FormattedMessage id="奖励比例" /></th>
                    <th><FormattedMessage id="指导奖励" /></th>
                    <th><FormattedMessage id="状态" /></th>
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
                                <td>{item.from_user_name}</td>
                                <td><span className="my_vds_wp">{item.bonus_profit_amount}</span></td>
                                <td>{item.up_level}</td>
                                <td><span className="my_vds_wp">{item.bonus_price}</span></td>
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