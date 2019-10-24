import React from 'react';
import {connect} from "react-redux";
import {FormattedMessage, injectIntl,IntlProvider} from 'react-intl';
import '../../assets/css/warning.css';
import updateIcon from '../../assets/img/browserUpdate.png'

class Warning extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            spanFlag: false
        }
        this.maskShow = this.maskShow.bind(this)
    }

    componentDidMount() {
        setTimeout(() =>{
            this.maskShow()
        },0)
    }

    // 浏览器升级 全局遮罩提示
    maskShow() {
        var maskDiv = document.getElementById('mask');
        var warningDiv = document.getElementById('warning');
        if (window.ieFlag) {
            maskDiv.style.display = "block";
            warningDiv.style.display = "block";
        } else {
            maskDiv.style.display = "none";
            warningDiv.style.display = "none";
        }
    }
    // 不同语言下 提示内容宽度自动添加 en-ul class
    getUlClass(){
        let {locale} = this.props;
        // console.log(locale)
        if(locale == 'en' || locale == 'zh'){
            return 'zh-ul'
        }else {
            return  'not-zh-ul'
        }
    }

    render() {
        const {locale,messages} = this.props
        console.log(this.props)
        return (
            <IntlProvider locale={locale} messages={messages[locale]}>
            <div>
                {/*遮罩*/}
                <div className="mask-div" id="mask">

                </div>
                {/*升级提示窗*/}
                <div className="update-warning" id="warning">
                    {/*提示标题*/}
                    <div className="update-warning-title">
                        <p>
                            <img src={updateIcon} alt=""/>
                        </p>
                        <p>
                            <FormattedMessage id="waringTitle"/>
                        </p>
                    </div>
                    {/*提示内容*/}
                    <div className="update-warning-cont">
                        <ul className={this.getUlClass()}>
                            <li>
                                <div>
                                    <span className="chrome">
                                    </span>
                                    <span>
                                        <a target="_blank" href='https://www.google.cn/chrome/'>
                                            <FormattedMessage id="chromeVersion"/>
                                        </a>
                                    </span>
                                </div>
                            </li>
                            <li>
                                <div>
                                    <span className="firefox">
                                    </span>
                                    <span>
                                        <a target="_blank" href='http://www.firefox.com.cn/'>
                                            <FormattedMessage id="firefoxVersion"/>
                                        </a>
                                    </span>
                                </div>
                            </li>
                            <li>
                                <div>
                                    <span className="safari">
                                    </span>
                                    <span>
                                        <a target="_blank" href='https://support.apple.com/zh_CN/downloads/safari'>
                                            <FormattedMessage id="safariVersion"/>
                                        </a>
                                    </span>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            </IntlProvider>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        locale: state.language.locale
    }

}

export default connect(mapStateToProps)(Warning)