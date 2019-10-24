import React from 'react';
import '../../assets/css/userauth.less';
import { FormattedMessage } from 'react-intl';

export default class ReadService extends React.Component{
    render(){
    return (<div className="rs_wp">
        <div className="m">
            <h2 className="tith2"><FormattedMessage id="服务条款" /></h2>
            <p>
                <pre>
                    <FormattedMessage id="rsp0" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="1.我们的服务" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp1" />
                </pre>
            </p>


            <h3 className="tith3"><FormattedMessage id="2.适用范围" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp2" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="3.交易数字资产的风险" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp3" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="4.注册账户及身份验证" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp4" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="5.账户安全" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp5" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="6.帐户关闭" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp6" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="7.用户的权利和义务" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp7" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="8.服务费用" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp8" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="9.遵守当地法律" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp9" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="10.服务变更" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp10" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="11.可分割性" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp11" />
                </pre>
            </p>

            <h3 className="tith3"><FormattedMessage id="12.解释权" /></h3>
            <p>
                <pre>
                    <FormattedMessage id="rsp12" />
                </pre>
            </p>

        </div>
    </div>);
    }
}