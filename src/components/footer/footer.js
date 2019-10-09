import React from 'react';
import { withRouter } from 'react-router'
import { FormattedMessage} from 'react-intl';
import { COOKIE_LAN } from '../../conf'
import cookie from 'js-cookie';
import Logo from '../../assets/image/base/logo-btcwinex.png'
import API from '../../assets/image/API.png'

import './footer.less';

class Footer extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            weixin:false,
            qq:false,
            wheelLeft:0,
            // links: []
        }
        this.mouse_qqover = this.mouse_qqover.bind(this);
        this.mouse_qqout = this.mouse_qqout.bind(this);
    }
    componentDidMount() {
        // get link data
        // axios.get(DOMAIN_VIP+'/getFriendUrl').then((res)=>{
        //     const links = res.data.datas;
        //     links.length
        //     &&
        //     this.setState({
        //         links
        //     })
        // })
    }
    mouse_qqover(){
        this.setState({ qq: true })
    }
    mouse_qqout(){
        this.setState({ qq: false })
    }
    render() {
        const { footStau } = this.props;
        let LANG = cookie.get(COOKIE_LAN);
        let _hideInfor;
        if(LANG == 'hk'){
            _hideInfor = '隱私條款'
        }
        if(LANG == 'en'){
            _hideInfor = 'Privacy Policy'
        }
        if(LANG == 'cn'){
            _hideInfor = '隐私条款'
        }
        if(LANG == 'kr'){
            _hideInfor = '개인정보 보호정책'
        }
        if(LANG == 'jp'){
            _hideInfor = 'プライバシーポリシー'
        }
        //console.log(LANG)
       
        return (
            <div className="footer" id="footer" ref="footer" style={{display:!footStau?"block":"none"}}>
                <div className="container clearfix">
                    <div className="bk-footer-hd-box" style={{"width":"auto"}}>
                        <a className="footer-logo" href="/">
                            <img src={Logo} />
                        </a>
                        <div className="bk-footer-hd-left clearfix">
                            <ul>
                                <li>
                                    <a href="/terms/service/">
                                        <i className="iconfont icon-fuwutiaokuan" style={{fontSize:'11px'}}></i>
                                        <FormattedMessage id="服务条款" />
                                    </a>
                                </li>
                                <li>
                                    <a href="/bw/chargeList/leve">
                                        <i className="iconfont icon-shouxufei" style={{fontSize:'11px'}}></i>
                                        <FormattedMessage id="手续费" />
                                    </a>
                                </li>
                                <li>
                                    <a href="/terms/termsPrivacy/">
                                        <i className="iconfont icon-yinsizhengce" style={{fontSize:'15px'}}></i>
                                        <span>{_hideInfor}</span>
                                    </a>
                                </li>
                                <li>
                                    <a href="/terms/relief/">
                                        <i className="iconfont icon-gonggao-yiru" style={{fontSize:'15px'}}></i>
                                        <FormattedMessage id="免责声明" />
                                    </a>
                                </li>
                                <li>
                                    <a href="https://www.btcwinex.com/login/zendesk/" target="_blank">
                                        <i className="iconfont icon-bangzhuzhongxin"></i>
                                        <FormattedMessage id="帮助中心" />
                                    </a>
                                </li>
                                <li>
                                    <a href="https://github.com/btcwinex/btcwinex-api" target="_blank">
                                        <i style={{width:'14px',height:'14px',display:'inline-block',marginRight:'7px'}}>
                                            <img style={{width:'14px',height:'14px'}} src={API} alt=""/>
                                        </i>
                                        <span>API</span>
                                    </a>
                                </li>
                               {
                                LANG == 'cn' &&
                                    <li>
                                        <a href="/sitemap" target="_blank">
                                            <i className="iconfont icon-bangzhuzhongxin"></i>
                                            <span>网站地图</span>
                                        </a>
                                    </li>
                               }           
                            </ul>
                        </div>
                        <div className="bk-footer-hd-center">
                            <FormattedMessage id="商务" />
                            business@btcwinex.com
                            &nbsp;&nbsp;
                            <FormattedMessage id="客服" />
                            support@btcwinex.com 
                            {/*
                                links.length>0&&links.map((item,index) => {
                                    return <a href={item.url} key={item.url+index} target="_blank">{item.name}</a>
                                })
                            */}
                        </div>
                        <div className="bk-footer-hd-right">
                            <div className="share_it clearfix">
                                <a href="https://www.facebook.com/BtcwinexTrading/" target="_blank" className="item">
                                    <i className="iconfont icon-facebook-icon-moren"></i>
                                </a>
                                <a href="https://t.me/BtcwinexOfficial" className="item qq">
                                    <div className="qrcode" style={{ display: this.state.qq ? "block" : "none" }}></div>
                                    <i className="iconfont icon-dianbaoqun-icon-moren"></i>
                                </a>
                                <a href="https://twitter.com/Btcwinex" target="_blank" className="item">
                                    <i className="iconfont icon-twitter-icon-moren"></i>
                                </a>
                                {/* <a onMouseOver={this.mouse_qqover} onMouseOut={this.mouse_qqout} className="item qq">
                                    <div className="qrcode" style={{ display: this.state.qq ? "block" : "none" }}></div>
                                    <i className="iconfont icon-dianbaoqun-icon-moren"></i>
                                </a>
                                <a href={`${DOMAIN_VIP}/v2/contactUs`} className="item">
                                    <i className="iconfont icon-youxiang-icon-moren"></i>
                                </a>
                                <a href="http://weibo.com/HelloBtcwinex" target="_blank" className="item">
                                    <i className="iconfont icon-xinlang-icon-moren"></i>
                                </a> */}
                            </div>
                        </div>
                    </div>
                </div>
                <p>
                    Copyright © 2019 btcwinex.    
                </p>
            </div>
        )
    }
}

export default withRouter(Footer);