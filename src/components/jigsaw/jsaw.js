import React from 'react';
import PropTypes from 'prop-types';
import './jigsaw.less';
import './jigsaw.js';
import { FormattedMessage, injectIntl } from 'react-intl';

class JSaw extends React.Component{
    static propTypes = {
        isSuc: PropTypes.func.isRequired,
        errMsg: PropTypes.string,
        doLg: PropTypes.func.isRequired,
        cDp: PropTypes.func.isRequired,
    }
    componentDidMount(){
        var _self = this;
        
        jigsaw.init({
            el: document.getElementById('captcha'),
            onSuccess: function() {
                _self.props.isSuc({e: 1})
                _self.props.doLg();
            },
            onFail: cleanMsg,
            onRefresh: cleanMsg,
            onClose: close,
            tith: _self.props.intl.formatMessage({id:"向右滑动滑块填充拼图"}),
          })
          function close(){
            _self.props.cDp();
          }
          function cleanMsg() {
            _self.props.isSuc({e: 0})
          }
        
    }
    render(){
        return (<div className="ab">
            <div className="container">
                <div id="captcha" style={{position: 'relative'}}></div>
                <div id="msg" className="ew">{this.props.errMsg}</div>
            </div>
        </div>)
    }
}

export default injectIntl(JSaw);