import React from 'react';
import PropTypes from 'prop-types';
import { injectIntl } from 'react-intl';
import { Link } from 'react-router';

import '../../assets/css/components/dialog.less';

class Confirm extends React.Component{
    static propTypes = {
        msg: PropTypes.string,
        cancel: PropTypes.string,
        ok: PropTypes.string,
        cb: PropTypes.func,
        isNotCancel: PropTypes.bool,
    }
    static defaultProps = {
        msg: '',
        cancel: '',
        ok: '',
        cb: (flg = 0) => {

        },
        isNotCancel: false,
    }
    constructor(props){
        super(props);

        const { msg, cancel, ok, intl } = this.props;
        this.state = {
            msg: msg || '',
            ok: ok || intl.formatMessage({id: '继续切换'}),
            cancel: cancel || intl.formatMessage({id: '取消'}),
        }
    }
    render(){
        const { msg, cancel, ok } = this.state;
        const { cb, isNotCancel, msg:ms } = this.props;

        return (
            <div>
                <div className="dm"></div>
                <div className={`dia-wp sp0 ${isNotCancel ? 'k' : ''}`}>
                    <p className="p lf">{ms}</p>
                    <div className="opt2">
                        {!isNotCancel ? <Link onClick={() => {cb(0)}} className="left alert_cancel">{cancel}</Link> : null}<Link onClick={() => {cb(1)}} className="right ac alert_hover">{ok}</Link>
                    </div>
                </div>
            </div>
        );
    }
}

export default injectIntl(Confirm);