/**
 * @description 等六边形
 * @author luchao.ding
 * @since 19/08/02
 */
import React from 'react';
import './hexagon.less';

export default (props) => {
    const { kz, txt } = props;
    return (
        <div className={`hexagon_wp ${kz}`}>
            <div className="l">
                <div className="r">
                    <div className="m" dangerouslySetInnerHTML={{__html:txt}}>
                    
                    </div>
                </div>
            </div> 
        </div>
    )
};