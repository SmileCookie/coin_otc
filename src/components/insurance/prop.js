/**
 * 保险保单提交成功消息
 * @description 消费外部数据结构
 * @author luchao.ding
 * @since 2019-08-28
 */
import React from 'react';
import { FormattedMessage } from 'react-intl';
import {Link} from "react-router";

export default (props) => {
    
    return (
        <div className="my_tip_wp bx2">
        {
        !props.dealFlg
        ?
        <React.Fragment>
            <p className="i"></p>
            <p className="p"><FormattedMessage id="nuse" values={
                {
                    v: <span className="my_vds_wp">{props.v}</span>,
                    f: <span className="font-fff">{props.f}</span>,
                    p: <span className="font-fff">{props.p} USDT</span>,
                }
            } /></p>
            <a href="/bw/manage/account/cmonerd" className="lccenter"><FormattedMessage id="前往保险中心" /></a>
        </React.Fragment>
        :
        <React.Fragment>
            <p className="p">
                <FormattedMessage id="csbl" />
            </p>
            <a href="/bw/money" className="lccenter"><FormattedMessage id="前往阿波罗计划" /></a>
        </React.Fragment>
        }
        </div>
    )
};