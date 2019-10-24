import React from 'react';
import PropTypes from 'prop-types';
import { injectIntl } from 'react-intl';
import { Link } from 'react-router';
import { formatURL } from '../../utils';

import '../../assets/css/components/dialog.less';

class moneyOpt extends React.Component{
    static propTypes = {
        status: PropTypes.string.isRequired,
        msg: PropTypes.string.isRequired,
        ft: PropTypes.string.isRequired,
        closeCb: PropTypes.func,

        
    }
    static defaultProps = {
        status: "0",
        msg: "",
        ft: "",
        closeCb: () => {

        },
    }
    render(){
        const { msg, ft, status, intl, closeCb ,type } = this.props;
        const { formatMessage } = intl;
        console.log(type)
        return (
            <div className="dia-wp">
                <svg className="icon warning" aria-hidden="true">
                    <use xlinkHref="#icon-zhanghuanquantixing"></use>
                </svg>
                <p className="p">{msg}</p>
                <button onClick={closeCb} className="bbyh-sure">{formatMessage({id: type =='0'? "bbyh我已知晓，确认使用":'bbyh我已知晓'})}</button>
            </div>
        );
    }
}

export default injectIntl(moneyOpt);