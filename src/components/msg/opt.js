import React from 'react';
import PropTypes from 'prop-types';
import { injectIntl } from 'react-intl';
import { Link } from 'react-router';
import { formatURL } from '../../utils';
import cookie from 'js-cookie';

import '../../assets/css/components/dialog.less';

class Opt extends React.Component{
    static propTypes = {
        status: PropTypes.string.isRequired,
        msg: PropTypes.string.isRequired,
        msg2: PropTypes.string.isRequired,
        ft: PropTypes.string.isRequired,
        closeCb: PropTypes.func,
        hasSalfClick:PropTypes.func,
        
    }
    static defaultProps = {
        status: "0",
        msg: "",
        ft: "",
        msg2: "",
        closeCb: () => {

        },
        hasSalfClick:() =>{

        }
    }
    render(){
        const { msg, ft, status, intl, msg2, closeCb ,hasSalfClick } = this.props;
        const { formatMessage } = intl;
        let _lan = cookie.get('zlan')
        return (
            <div className="dia-wp" style={{width:['jp','ko','kr'].includes(_lan)?'615px':'540px', height:['jp','ko','kr'].includes(_lan) ? 'auto' : 'auto'}}>
                <svg className="icon warning" aria-hidden="true">
                    <use xlinkHref="#icon-zhanghuanquantixing"></use>
                </svg>
                <p className="p">{msg}<br />{msg2}</p>
                <ul className="opt clearfix">
                    <li className="left" onClick={hasSalfClick} >
                        <Link to={formatURL("dEbUpMobile")}><span className="iconfont s">&#xe6d7;</span>{formatMessage({id: "手机验证"})}</Link>
                    </li>
                    <li className="right" onClick={hasSalfClick}>
                        <Link to={formatURL("googleOne")}><span className="iconfont s0">&#xe6d9;</span>{formatMessage({id: "谷歌验证"})}</Link>
                    </li>
                </ul>
                <p className="ft" onClick={closeCb}>{ft}</p>
            </div>
        );
    }
}

export default injectIntl(Opt);