/**
 * @description 理财中心的树形图附属表格 - https://www.gbccoin.com/bw/manage/account/cmoneyTree
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
                    <th><FormattedMessage id="矩阵级别" /></th>
                    <th><FormattedMessage id="容量" /></th>
                    <th><FormattedMessage id="计数" /></th>
                    <th><FormattedMessage id="空缺" /></th>
                    {
                    false
                    &&
                    <th><FormattedMessage id="未激活" /></th>
                    }
                </tr>
            </thead>
            <tbody>
            {
                    !isLoading && list.length 
                    ?
                    list.map((item, i) => {
                        return(
                            <tr key={item.id}>
                                <td>{item.hierarchyLevel}</td>
                                <td>{item.hierarchyTotalNum}</td>
                                <td>{item.userActiveNum}</td>
                                <td>{item.userEmptyNum}</td>
                                {
                                false
                                &&
                                <td>{item.userNoActiveNum}</td>
                                }
                            </tr>
                        )
                    })
                    :<TrResult isLoading={isLoading} cols='4' style={{height:'200px', lineHeight:'200px'}}  />
                }
            </tbody>
        </table>
    )
};