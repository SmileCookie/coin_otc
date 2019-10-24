/**
 * @description 所有的记录当没有数据或着加载中统一处理
 * @author luchao.ding
 * @since 07/31/2019
 */
import React from 'react';

import Result from './result';

const TrResult = (props) => {
    const { isLoading, cols = 1, style = {} } = props;

    return (
        <tr className="nohover">
            <td colSpan={cols} style={{height:'400px',color:'#fff', textAlign:'center',lineHeight:'400px', ...style}}>
                <Result isLoading={isLoading} />
            </td>
        </tr>
    )
}

export default TrResult;