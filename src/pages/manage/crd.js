/**
 * @description 保险记录
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
    const dic = [1,2,3]; // 如果是这三种状态标注蓝色可点
    return(
        <table className="table-history tb_kz" width="100%">
            <thead>
                <tr>
                    <th><FormattedMessage id="日期" /></th>
                    <th className="pmtc"><FormattedMessage id="委托数量" /></th>
                    <th className="pmtr"><FormattedMessage id="触发价格" /></th>
                    <th className="pmtr"><FormattedMessage id="t-理论收益" /></th>
                    <th className="pmtr"><FormattedMessage id="分红权重" /></th>
                    <th className="pmtc"><FormattedMessage id="状态" /></th>
                    <th className="pvm_wp">
                        <i className="question_point">
                            <em>
                                <FormattedMessage id="cmpx" />
                            </em>
                        </i>
                        <FormattedMessage id="定格时间" />
                    </th>
                </tr>
            </thead>
            <tbody>
            {
                !isLoading && list.length 
                ?(
                list.map((item, i) => {
                    return(
                        <tr key={item.id}>
                            <td>{formatDate(new Date(+item.createTime)).replace(',', '').replace(/\//g, '-')}</td>

                            <td className="pmtc">
                            <span className="my_vds_wp">
                            {
                                item.insureInvestAmount
                            }
                            </span>
                            </td>

                            <td className="pmtr">
                            {
                                item.triggerPrice
                            } USDT
                            </td>

                            <td className="pmtr">
                            {
                                item.theoryPrice
                            } USDT
                            </td>

                            <td className="pmtr">
                            {
                                item.dividendRight /** 缺失 */
                                
                            }
                            </td>

                            <td className="pmtc">
                            {
                                item.triggerFlagDesc.map((v, i)=><span onClick={()=>{
                                    if(dic.includes(+item.jumpFlag[i])){
                                        item.opt = +item.jumpFlag[i];
                                        props.cb(item);
                                    }
                                }} className={dic.includes(+item.jumpFlag[i]) ? 'pmcanck':''} style={{marginRight:'10px', display:'inline-block', verticalAlign:'top',}} dangerouslySetInnerHTML={{__html:v}}></span>)
                            }
                            </td>

                            <td>
                            {item.triggerTime ? formatDate(new Date(+item.triggerTime)).replace(',', '').replace(/\//g, '-') : '--'}<br />{item.freezTime ? formatDate(new Date(+item.freezTime)).replace(',', '').replace(/\//g, '-') : '--'}
                            </td>
                        </tr>
                    )
                })
                
                )
                :<TrResult isLoading={isLoading} cols='7' />
            }    
            </tbody>
        </table>
    )
});