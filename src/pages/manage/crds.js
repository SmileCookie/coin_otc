/**
 * @description 投保记录
 * @author luchao.ding
 * @since 2019/08/05
 */ 
import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import TrResult from '../../components/data/trResult';
import { formatDate } from '../../utils'; 
import { Link } from "react-router";

export default injectIntl((props) => {
    
    const {isLoading, list, intl, } = props;
    
    return(
        <table className="table-history tb_kz" width="100%">
            <thead>
                <tr>
                    <th style={{paddingLeft:'50px',width:'300px'}}><FormattedMessage id="投保日期" /></th>
                    <th className=""><FormattedMessage id="用户" /></th>
                    <th className="pmtr"><FormattedMessage id="投保数量" /></th>
                    <th className="pmtr"><FormattedMessage id="tx-分红权" /></th>
                    <th className="pmtr"><FormattedMessage id="触发价格" /></th>
                    <th className="pmtc"><FormattedMessage id="价格状态" /></th>
                </tr>
            </thead>
            <tbody>
            {
                !isLoading && list.length 
                ?(
                list.map((item, i) => {
                    return(
                        <tr key={item.id}>
                            <td style={{paddingLeft:'50px'}}>{formatDate(new Date(+item.insureTime)).replace(',', '').replace(/\//g, '-')}</td>

                            <td className="">
                            
                            {
                                item.userName
                            }
                          
                            </td>

                            <td className="pmtr">
                            <span className="my_vds_wp">
                            {
                                item.insureAmount
                            } </span>
                            </td>

                            <td className="pmtr">
                            {
                                item.dividendRight
                            }
                            </td>

                            <td className="pmtr">
                            {
                                item.insureAvergPrice
                            } USDT
                            </td>

                            <td className="pmtc">
                            {
                                item.insureRollBackFlagDesc
                            }
                            </td>
                        </tr>
                    )
                })
                
                )
                :<TrResult isLoading={isLoading} cols='6' />
            }    
            </tbody>
        </table>
    )
});