import React from 'react';
import { connect } from 'react-redux';

import Form from '../../decorator/form';

import { Link } from "react-router";
import { formatURL } from "../../utils";
import { CopyToClipboard } from 'react-copy-to-clipboard';
import { getGoogleSInfo } from '../../redux/modules/usercenter'; 
import { optPop } from '../../utils';
import { injectIntl } from 'react-intl';

@connect(
    state => ({usercenter: null}),
    {
        getGoogleSInfo,   
    },
)
@Form
class GoogleOne extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            info: {}
        }
    }
    componentDidMount(){
        this.props.getGoogleSInfo().then(r => {
            this.setState({
                info: r,
            })
        });
    }
    render(){
        const { info } = this.state;
        const { formatMessage } = this.props.intl;
      
        return (                
            <div className="content">
                <div className="google">
                    <div className="google-step">
                        <div className="google-title">{formatMessage({id: "第一步，下载并安装谷歌验证器APP"})}</div>
                        <div className="google-con clearfix">
                            <div className="google-app clearfix" onClick={()=>{
                                info.ioslink && (window.open(info.ioslink));
                            }}>
                                <div className="app-img">
                                    <i className=" iconfont icon-AppStore"></i>
                                </div>
                                <div className="app-text">
                                    <p>{formatMessage({id: "下载安装"})}</p>
                                    <p>App Store</p>
                                </div>
                            </div>
                            <div className="google-app clearfix" onClick={()=>{
                                info.ioslink && (window.open(info.glink));
                            }}>
                                <div className="app-img">
                                    <i className=" iconfont icon-GooglePlay"></i>
                                </div>
                                <div className="app-text">
                                    <p>{formatMessage({id: "下载安装"})}</p>
                                    <p>Google Play</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="google-step step-top60">
                        <div className="google-title">{formatMessage({id: "第二步，下载并安装谷歌验证器APP"})}</div>
                        <div className="google-con clearfix">
                            <div className="google-ewm">
                                <img src={`data:image/jpg;base64,${info.qrcode ? info.qrcode : 'ddddddd'}`} />
                            </div>
                            <div className="ewm-text">
                                <p>
                                    <span>{info.key}</span>
                                    <CopyToClipboard onCopy={() => {
                                        optPop(()=>{}, formatMessage({id: '复制成功'}))
                                    }} text={info.key}>
                                        <a href="javascript:void(0)">{formatMessage({id: '复制'})}</a>
                                    </CopyToClipboard>
                                </p>
                                <p>
                                    {formatMessage({id: "扫描左侧二维码添加密钥，或手动输入密钥"})}
                                </p>
                            </div>
                        </div>
                    </div>
                    <Link className="next-btn" to={formatURL('setGCkCode')}>{formatMessage({id: "下一步"})}</Link>
                </div>
            </div>       
        )
    }
}

export default injectIntl(GoogleOne);