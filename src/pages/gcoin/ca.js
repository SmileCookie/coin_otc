import React from 'react';
import { injectIntl } from 'react-intl';
import { Link } from 'react-router';
import { formatURL } from '../../utils';

class Ca extends React.Component{
    constructor(props){
        super(props);
    }

    render(){
        const { formatMessage } = this.props.intl;
        return (
            <div className="gcoin_wp">
                <div className="gcm sppd">
                    <h2 className="gctith2">{formatMessage({id: 'bbyhXXX上币申请'})}</h2>
                    <Link to={formatURL('/coinApply')} className="goup">&lt; {formatMessage({id: '返回上一步'})}</Link>
                    <ul className="gopt_wp clearfix">
                        <li className="fst">
                            <Link>{formatMessage({id: 'bbyh公司信息'})}</Link>
                        </li>
                        <li>
                            <Link>{formatMessage({id: 'bbyh代币信息'})}</Link>
                        </li>
                        <li className="lst">
                            <Link>{formatMessage({id: 'bbyh对接人信息'})}</Link>
                        </li>
                    </ul>
                    {
                        this.props.children
                    }
                </div>
            </div>
        )
    }
}

export default injectIntl(Ca);
