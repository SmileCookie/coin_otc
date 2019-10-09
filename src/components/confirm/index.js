import React from 'react';
import PropTypes from 'prop-types';
import { injectIntl} from 'react-intl';
import {withRouter} from 'react-router';
import '../../assets/style/base/confirm.css'
import image from '../../assets/image/account_safe_tip.png';

class Confirm extends React.Component{
    static propTypes = {
        title: PropTypes.string,
        content: PropTypes.string,
        okText: PropTypes.string,
        cancelText: PropTypes.string,
        cb: PropTypes.func,
        isNotCancel: PropTypes.bool,
        safeIcon: PropTypes.bool
    }
    static defaultProps = {
        title: '',
        content: '',
        okText: '',
        cancelText: '',
        cb: (flg = 0) => {
        },
        isNotCancel: false,
        safeIcon: false,
    }
    constructor(props){
        super(props);

        const { title, content, okText, cancelText,isNotCancel,safeIcon } = this.props;
        this.state = {
            title: title || '',
            content: content || '',
            okText: okText || '',
            cancelText: cancelText || '',
            isNotCancel: isNotCancel || false,
            safeIcon: safeIcon || false
        }
    }
    componentDidMount() {
        let o = document.querySelector(".confirm_content");
        let h = o.offsetHeight; //高度
        let w = o.offsetWidth; //宽度
        o.style.transform = `translate(${-w/2}px,${-h/2}px)`;
    }
    cancel = (e, type) => {
        this.props.cb(type);
    };
    render(){
        const { title, content, okText,cancelText } = this.state;
        const { isNotCancel, safeIcon } = this.props;
        const {formatMessage} = this.props.intl;
        return (
            <div>
                <div className="mask"></div>
                <div className="confirm_content">
                    <img  className={`${!safeIcon ?'no_show':''}`} src={image} />
                    {/*<svg className={`${!safeIcon ?'no_show':'icon_label'}`} aria-hidden="true"><use xlinkHref="#icon-zhanghuanquantixing"></use></svg>*/}
                    <div className={`confirm_title ${!title ?'no_show':''}`}>{title}</div>
                    <div className={`confirm_main_content ${!content ?'no_show':''}`}>{content}</div>
                    <div className="but_box">
                        {isNotCancel || <input type="button" className="btn cancel" value={cancelText ? cancelText : formatMessage({id:"取消"})} onClick={e=>this.cancel(e, 'cancel')} />}
                        <input type="button" className="btn submit margin_r0" value={okText ? okText : formatMessage({id:"确定"})} onClick={e=>this.cancel(e, 'sure')} />
                    </div>
                </div>
            </div>
        );
    }
}

export default withRouter(injectIntl(Confirm));