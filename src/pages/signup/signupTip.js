import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';

class SignupTip extends React.Component{
    render() {
        return (
            <div className="login-tip">
                <div className="open_top_img"></div>
                <h3 className="sigleft_h3"><FormattedMessage id="reg.text11" /></h3>
                <p className="sigleft_p">
				<FormattedMessage id="reg.text12" />
                </p>
                <div className="open_text clearfix">
                    <span className="open_okicon"></span>
                    <p className="text_p"><FormattedMessage id="reg.text13" /> </p>
                </div>
                <div className="open_text clearfix">
                    <span className="open_okicon"></span>
                    <p className="text_p"><FormattedMessage id="reg.text14" /></p>
                </div>
                <div className="open_text clearfix">
                    <span className="open_okicon"></span>
                    <p className="text_p"><FormattedMessage id="reg.text10" /></p>
                </div>
            </div>
        )
    }
}

export default injectIntl(SignupTip);
