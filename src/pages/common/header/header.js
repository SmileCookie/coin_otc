/**
 * @file é€šç”¨å¯¼èˆª
 * @author Ray
 * @time 2017-09-29
 */
import React from 'react';
import { fetchManageInfo, } from '../../../redux/modules/account'
import { fetchWalletInfo } from '../../../redux/modules/wallet'
import { Link,browserHistory,withRouter } from 'react-router';
import { FormattedMessage,injectIntl } from 'react-intl';
import LanguageToggle from '../../../components/languageToggle'
import HeaderTrade from './headerTrade'
import { FETCH_ACCOUNT_INTERVAL, COOKIE_LAN, CONF_MONEY, TRADEGEADURL } from '../../../conf'
import { cutDigits,optPop} from '../../../utils'
import Logo from '../../../assets/images/logo-btcwinex.png'
import './header.less';
import axios from 'axios';
import cookie from 'js-cookie'
import {formatURL} from '../../../utils/index'
import { connect } from 'react-redux'
import { DOMAIN_VIP,COIN_KEEP_POINT } from '../../../conf'
const BigNumber = require('big.js')
import ReactModal from "../../../components/popBox";
import controls from '../../../assets/images/control.png'
import controls_hover from '../../../assets/images/control_hover.png'
import shangjia from '../../../assets/images/vip.png'
import {backToOtc} from "../../../redux/modules/session";
//coinList: state.account.detail,
@connect(
    state => ({
        coinList: state.account.detail,
        record: state.account.record,
        total: state.assets.total,
        lang: state.language.locale,
        moneylogo: state.money.locale,
        moneyrate: state.money.rate.exchangeRateUSD,
        assets: state.assets,
        money: state.money,
        user: state.session.user,
        otcFlag: state.session.otcFlag,
        isHaveChooseStyles:true,
        ws:state.socket.ws
    }),
    (dispatch) => {
        return {
            fetchManageInfo: (cb) => {
                dispatch(fetchManageInfo()).then(cb)
            },
            fetchWalletInfo: (cb) => {
                dispatch(fetchWalletInfo()).then(cb)
            },
            backToOtc: () =>{
                dispatch(backToOtc());
            }
        }
    }
)
class Header extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showFundDropdown: false,
            showUserDropdown: false,
            chooseSection:props.header.chooseItem,
            isHaveChooseStyle:false,
            op: true,
            riw:"",
            isShowMoneyTag : false,
            dialogHTML:'',
            hoverImgs:false,
            user:'',
            otcFlag:''
        };
        this.listenUserStateTimer = null;
        this.userLogout = this.userLogout.bind(this)
        this.hideTradeHead = this.hideTradeHead.bind(this)
        this.chooserSection = this.chooserSection.bind(this)
        this.checkUrl = this.checkUrl.bind(this);
        this.sw = this.sw.bind(this);
        this.hd = this.hd.bind(this);
        this.jmp = this.jmp.bind(this);
        this.ch = window.location.href;
        this.pms = this.pms.bind(this);
        this.apms = this.apms.bind(this);

        this.getPayTimer = null;

        this.listenUserState = this.listenUserState.bind(this);
    }
    apms(){
        this.pms([this.props.fetchAssetsTotal(),this.props.fetchOtcAssetsTotal(),this.props.fetchrWalletTotal(),this.props.fetchTransAssetsTotal()]);
    }
    pms(ay = []){
        Promise.all(ay).then((res)=>{
            var sum = new BigNumber(0);

            res.forEach((item, i)=>{
                //console.log(item);
                var n = item.total_usdt != 0 ? item.total_usdt : 0;
                sum = sum.plus(n);
            });

            try{
                const E = this.props.moneyrate[this.props.moneylogo.name];
                sum = sum.times(E ? E : 0);
            }catch(e){
                sum = 0;
            }

            this.tosum = sum.toFixed(2) + '';
            this.forceUpdate();
        })
    }
    jmp(url = ''){
        url && (window.location.href = url);
    }

    sw(){
        this.setState({
            op: true
        })
    }
    hd(){
        this.setState({
            op: false
        })
    }
    componentDidMount() {
        localStorage.setItem('isintransfer', 0);
        this.setState({
            user:this.props.user,
            otcFlag: this.props.otcFlag
        })
        this.listenUserStateTimer = setInterval(()=>{
            this.listenUserState();
        },30)
        // 获取支付状态
        localStorage.setItem(cookie.get('zuid')+'ispay', 0);

        axios.get(DOMAIN_VIP+'/manage/financial/userFinCenInfo').then((res)=>{
            const data = res.data.datas;

            const c = +data.authPayFlag;

            ([2,3].includes(c)) && localStorage.setItem(cookie.get('zuid')+'ispay', 1);
        })

        // 获取支付状态，由于赶时间不用redux了
        this.getPayTimer = setInterval(()=>{
            const isPayFlg = localStorage.getItem(cookie.get('zuid')+'ispay');
            if(isPayFlg !== this.isPayFlg){
                this.setState({
                    isShowMoneyTag: +isPayFlg
                })
            }
            this.isPayFlg = isPayFlg;
        },50)
        //console.log(this.props)
        this.props.fetchManageInfo();
        //this.props.fetchWalletInfo();
        this.interval = setInterval(() => {
            this.state.otcFlag ? this.props.fetchManageInfo(this.props.backToOtc) : this.props.fetchManageInfo()
            //this.props.fetchWalletInfo();
        }, FETCH_ACCOUNT_INTERVAL)
        this.hideTradeHead(this.props.location.pathname)
        this.checkUrl();
        if(cookie.get("zlan")=="jp"){
            this.setState({
                riw: "nav clearfix haiw",
            })
        }else{
            this.setState({
                riw: "nav clearfix",
            })
        }
        if(this.props.session.user){
            this.apms();
            this.props.fetchAssetsDetail()
            this.interval = setInterval(()=>{
                this.apms();
                this.props.fetchTransInfo()
                //this.props.fetchAssetsTotal();
                //this.props.fetchOtcAssetsTotal();
                //this.props.fetchrWalletTotal();
                this.props.fetchAssetsDetail()
            },FETCH_ACCOUNT_INTERVAL)
        }

        //获取商家认证信息
        this.props.checkVipEvent()
    }

    formatFundsDetail(result) {
        BigNumber.RM = 0;
        let record = [];
        let i = 0;
        try{
            let exchangeRate = this.props.moneyrate[this.props.moneylogo.name]
            if (result && exchangeRate) {
                for (let key in result) {
                    var funds = result[key];
                    record[i] = {};
                    //console.log(result)
                    var total = funds.total;
                    // console.log(total)
                    var usdExchange = funds.usdExchange != "--" && exchangeRate ? funds.usdExchange * total * exchangeRate : 0;
                    // console.log(usdExchange)
                    record[i].valuation = new BigNumber(usdExchange ? usdExchange : 0).toFixed(2);
                    i++;
                }
            }
        } catch(e){

        }
        return record;
    }

    componentWillReceiveProps(nextProps) {
        if(nextProps.otcFlag){
            this.setState({
                otcFlag:nextProps.otcFlag
            })
        }
        if(nextProps.user){
            this.setState({
                user:nextProps.user
            })
            setTimeout(() => {
                // var sum = new BigNumber(0);
                // this.formatFundsDetail(nextProps.coinList.data).forEach((item)=>{
                //     var n = item.valuation != 0 ? item.valuation : 0;
                //     if(!isNaN(n)){
                //         sum = sum.plus(n)
                //     }
                // })
                // this.tosum = sum + '';
                // this.forceUpdate();
            });
        }
        if(nextProps.session.user && !this.props.session.user){
            // this.props.fetchAssetsTotal();
            // this.props.fetchrWalletTotal();
            // this.props.fetchOtcAssetsTotal();
            this.apms();
        }
        if(nextProps.location.pathname != this.props.location.pathname){
            this.hideTradeHead(nextProps.location.pathname)
            this.checkUrl();
        }

        if(this.ch !== window.location.href){
            this.ch = window.location.href;
            //this.forceUpdate(); //刷新头部导航状态
        }
    }
    componentWillUnmount(){
        clearInterval(this.listenUserStateTimer);
        clearInterval(this.interval)
        clearInterval(this.getPayTimer);

    }

    componentDidUpdate(){
        let cp = this.props.location.pathname.split("/");
        const dm = !this.dm ? (this.dm = document.getElementById("rs")) : this.dm;

        cp = cp[cp.length - 1] ? cp[cp.length - 1] : cp[cp.length - 2];

        if(['trade', 'multitrade', 'announcements', 'news'].includes(cp)){
            dm.setAttribute("class", "trade-wps")
        } else {
            dm.removeAttribute("class");
        }
    }

    toggleFundDropdown() {
        this.setState({
            showFundDropdown: this.state.showFundDropdown?false:true
        });
    }
    toggleUserDropdown() {
        this.setState({
            showUserDropdown: this.state.showUserDropdown?false:true
        });
    }
    userLogout(e){
        clearInterval(this.interval);
        // this.props.logout();
    }
    setMoney(name){
        // console.log(name)
        this.props.setMoney(name);
    }

    hideTradeHead(path){
        const hideState = TRADEGEADURL.filter((item,index,arr) => {
            return path.includes(item)
        })
        const footStauBool = hideState.length>0?true:false;
        this.props.modifyFoot(footStauBool)
    }
    chooserSection(type){
         this.props.chooseSectionType(type)
         this.setState({
            chooseSection:type
         })
    }
    checkUrl(){
        let pathName = browserHistory.getCurrentLocation().pathname;
        if(pathName.indexOf('trade') > 0 || pathName.indexOf('multitrade') > 0 || pathName.indexOf('announcements') > 0 || pathName.indexOf('news') > 0){
             this.setState({
                isHaveChooseStyle:true
            })
        }else{
            this.setState({
                isHaveChooseStyle:false
            })
        }
    }

    listenUserState(){
        // 如果双开划转有一个退出当被监控到直接到登录。
        const {user} = this.props.session;
// console.log(user && cookie.get("zloginStatus")!=4 && cookie.get('zuon'), '====->')
        if(!(user && cookie.get("zloginStatus")!=4 && cookie.get('zuon'))){
            +localStorage.getItem('isintransfer') && browserHistory.push(formatURL('/login'));
            localStorage.setItem('isintransfer', 0);
        }
    }

     //去认证
     openGoCheck = () =>{
        let str =  <div className="image-dialog" style={{padding:'20px'}}>
                     <div className="dialog-cont">
                         <p style={{fontSize:'18px'}}><FormattedMessage id="尚未实名认证"/></p>
                         <p><FormattedMessage id="请您先完成实名认证后，再申请广告商家认证"/></p>
                     </div>
                     <div className="dialog-footer" style={{marginTop:'80px'}}>
                         <a href="/bw/mg/authenOne" className="btn submit"><FormattedMessage id="前往认证" /></a>
                     </div>
                 </div>
         this.setState({dialogHTML:str},()=>{
             this.dialog.openModal();
         });
 }

    //商家跳转
    goUserStatus = () =>{
        const {storeStatus,storeType} = this.props.checkVipObj
        axios.get(DOMAIN_VIP + '/otcweb/web/v1/store/lockStatus').then(res =>{
            if(res.status == 200){
                let {code,msg} = res.data
                if(code == 200){
                    window.location.href = `/otc/business`
                    // if(storeStatus == 1 && storeType == 1){

                    // }
                    // if(storeStatus !== 1 && storeType == 1){
                    //     window.location.href = '/otc/business'
                    // }
                    // if(storeStatus == 0 && storeType == 2){
                    //     window.location.href = `/bw/mg/cancleUserInforIng`
                    // }
                    // if(storeStatus == 1 && storeType == 2){
                    //     window.location.href = '/otc/business'
                    // }
                    // if(storeStatus == 2 && storeType == 2){
                    //     window.location.href = `/bw/mg/cancleUserInforBack`
                    // }
                    // if(storeStatus == -1){
                    //     window.location.href = '/otc/business'
                    // }
                    // if(storeStatus == 1){
                    //     window.location.href = `/bw/mg/cancleUserInfor`
                    // }
                    // if(storeStatus == 0){
                    //     window.location.href = `/bw/mg/cancleUserInforIng`
                    // }
                    // if(storeStatus == 2){
                    //     window.location.href = `/bw/mg/cancleUserInforBack`
                    // }
                }else{
                    optPop(() =>{},msg,{timer: 1500},true)
                }
            }
        })
    }

    hoverImg = ()=>{
        this.setState({
             hoverImgs:true
        })

     }
     unHoverImg = () =>{
         this.setState({
             hoverImgs:false
         })
     }

     logout = () =>{
        // let _this = this
        // if(_this.props.ws){
        //     let sessionId = cookie.get("zsessionId")
        //     _this.props.ws.send(JSON.stringify({ "event": 'unsub',"channel":otcChannel,"params":{"token":sessionId,"type":"web"}}))
        // }
        window.location.href = '/login/logout/'
     }

    render() {
        const { user } = this.state;
        // console.log('header==============' + user);
        const {chooseItem} = this.props.header;
        const {formatMessage} = this.props.intl;
        const ch = this.ch;
        const { assets, money, footStau } = this.props;
        const {storeStatus,storeType,loading} = this.props.checkVipObj
        let moneyNow = money.locale.name.toUpperCase();
        let {chooseSection,isHaveChooseStyle,op,isShowMoneyTag,hoverImgs} = this.state;
        let total = 0;
        try{
            if(money.rate.exchangeRateUSD){
                //total = assets.total?cutDigits(assets.total.total_legal_tender*money.rate.exchangeRateUSD[moneyNow],2):''
                total = this.tosum;
                localStorage.setItem('total',total);
                localStorage.setItem('moneyNow',moneyNow);
            }
        } catch(e){}
        return (
            <header className={`header up-mx-wp ${this.ch.includes('money') || this.ch.includes('smn') ? 'lc':''}`}>
                <div className="contain clearfix">
                    <div className="header-left">
                        <a className="logo" href="/">
                            <img  src={Logo} />
                        </a>
                        <ul className={ this.state.riw}>
                            <li>
                                <Link title={formatMessage({id:"币币交易中心"})} onClick={()=>{this.jmp("/bw/trade/")}} className={ch.includes("/trade") ? 'bbyh-choosed' : ''} >
                                    <FormattedMessage id="币币交易中心"/>
                                </Link>
                            </li>

                            <li className="hot">
                                <Link title={formatMessage({id:"多屏看板"})} onClick={()=>{this.jmp("/bw/multitrade")}}  className={ch.includes("/multitrade") ? 'bbyh-choosed' : ''} >
                                    <FormattedMessage id="多屏看板"/>
                                </Link>
                            </li>

                            {/* <li>
                                <Link title={formatMessage({id:"公告"})} onClick={()=>{this.jmp("/bw/announcements")}}  className={ch.includes("/announcements") ? 'bbyh-choosed' : ''}>
                                    <FormattedMessage id="公告"/>
                                </Link>
                            </li>

                            <li>
                                <Link title={formatMessage({id:"新闻"})} onClick={()=>{this.jmp("/bw/news")}}  className={ch.includes("/news") ? 'bbyh-choosed' : ''}>
                                    <FormattedMessage id="新闻"/>
                                </Link>
                            </li> */}
                            {
                            true
                            &&
                            <li>
                                <Link title={formatMessage({id:"理财"})} onClick={()=>{this.jmp("/bw/money")}}  className={ch.includes("/money") ? 'bbyh-choosed' : ''}>
                                    <FormattedMessage id="理财"/>
                                </Link>
                            </li>
                            }

                            <li>
                                <a title={formatMessage({id:"法币交易"})} onClick={()=>{this.jmp("/otc/trade")}}  className={ch.includes("/otc") ? 'bbyh-choosed' : ''}>
                                    <FormattedMessage id="法币交易"/>
                                </a>
                            </li>
                        </ul>
                    </div>
                    <div className="header-right">
                        <div className="nav-manage">
                                {
                                     (user&&cookie.get("zloginStatus")==1 && !loading) &&
                                     <div className="userVip">
                                          {
                                               ( (storeStatus == 1 &&  storeType == 1) || (storeStatus !== 1 &&  storeType == 2) )?
                                                <div className="controls">
                                                    <div  onClick={this.goUserStatus} onMouseOver ={this.hoverImg} onMouseLeave={this.unHoverImg}>
                                                        <i style={{top:'0'}}><img style={{width:'14px'}} src={hoverImgs ? controls_hover : controls} alt=""/></i><FormattedMessage id="工作台"/>
                                                    </div>
                                                </div>
                                                :
                                                <div className="shangjia">
                                                    <div  onClick={this.goUserStatus}>
                                                        <i style={{top:'-1px'}}><img style={{width:'16px'}} src={shangjia} alt=""/></i><FormattedMessage id="商家认证"/>
                                                    </div>
                                                </div>

                                             }
                                     </div>
                                }

                            <div>
                                <div id="bbyh-moneyIn" style={{color:'#FFFFFF'}}>
                                    <Link className="nbspa" to=""><i className="iconfont icon-gonggao-moren"></i></Link>
                                    <i style={{marginLeft: '5px'}} className="per-icon-jiao"></i>
                                </div>
                                <ul>
                                    <li className="big-menu" style={{textAlign:'center'}}>
                                        <Link to="/bw/news">
                                            <p><FormattedMessage id="新闻"/></p>
                                        </Link>
                                    </li>
                                    <li className="big-menu" style={{textAlign:'center'}}>
                                        <Link to="/bw/announcements">
                                            <p><FormattedMessage id="公告"/></p>
                                        </Link>
                                    </li>
                                </ul>
                            </div>
                    </div>
                        <LanguageToggle theme="mobile" />
                        {
                            user&&cookie.get("zloginStatus")==1? (
                                <div className="nav-manage">
                                    <div>
                                        <div id="bbyh-moneyIn" style={{color:'#FFFFFF'}}>
                                            <Link className="nbspa" to="/bw/manage/account/balance"><i className="iconfont icon-zichanguanli-moren"></i><FormattedMessage id="资产管理"/></Link>
                                            <i style={{marginLeft: '5px'}} className="per-icon-jiao"></i>
                                        </div>
                                        <ul>
                                            <li className="big-menu">
                                                <Link to="/bw/manage/account/balance">
                                                    <p><FormattedMessage id="我的钱包"/></p>
                                                </Link>
                                            </li>
                                            <li className="big-menu">
                                                <Link to="/bw/manage/account/currency">
                                                    <p><FormattedMessage id="交易账户"/></p>
                                                </Link>
                                            </li>
                                        </ul>
                                    </div>
                                    <div>
                                        <div onClick={() =>{browserHistory.push(formatURL('/entrust/current'))}}><i className="iconfont icon-weituoguanli-moren"></i><FormattedMessage id="委托管理"/></div>
                                    </div>
                                    <div className="nav-manage-banlence">
                                        <div>
                                            <Link to="/bw/mg/">
                                                <i className="iconfont icon-zhanghu-moren"></i>
                                                <i className="per-icon-jiao"></i>
                                            </Link>
                                        </div>
                                        <ul>
                                            <li className="big-menu">
                                                <Link to="/bw/mg/">
                                                    <p><FormattedMessage id="用户中心"/></p>
                                                    <i>{user.username}</i>
                                                    <em className="vip-grade"></em>
                                                </Link>
                                            </li>
                                            {
                                            true && isShowMoneyTag ?
                                            <li className="big-menu">
                                                <Link to="/bw/manage/account/cmoney"><p className=""><FormattedMessage id="理财中心"/></p></Link>
                                            </li>
                                            :null
                                            }
                                            <li className="big-menu">
                                                <Link to="">
                                                    <p className="gray"><FormattedMessage id="资产折算"/>:</p>
                                                    <i className="light">
                                                    ≈ {total}
                                                        &nbsp;{moneyNow}
                                                    </i>
                                                </Link>
                                            </li>
                                            <li>
                                                <a onClick={this.logout} target="_self"><FormattedMessage id="退出登录" /></a>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            ):(
                                <div className="nav-account">
                                    <Link className="btn-login"  to="/bw/login"><FormattedMessage id="登录" /></Link>
                                    <Link className="btn-signup" to="/bw/signup"><FormattedMessage id="注册" /></Link>
                                </div>
                            )
                        }
                        <div className="nav-manage">
                            <div className="nav-manage-money">
                                <div onMouseEnter={this.sw}>
                                    <i className="coinkey-now">{CONF_MONEY[moneyNow]}</i>
                                    <i className="per-icon-jiao"></i>
                                </div>
                                {op&&<ul>
                                        {Object.keys(CONF_MONEY).map((key, index) => {
                                            return <li key={key} onClick={()=>{this.setMoney(key)}}>
                                                        <a onClick={this.hd} href="javascript:void(0);" style={{position:'relative'}}>
                                                            <i style={{position:"absolute"}} className="coinkey">{CONF_MONEY[key]}</i>
                                                            <span style={{display:'block',paddingLeft:"40px"}}>
                                                                <FormattedMessage id={key.toUpperCase()} />
                                                            </span>
                                                        </a>
                                                    </li>
                                            })
                                        }
                                    </ul>
                                }
                            </div>
                        </div>
                        <LanguageToggle theme="white" />
                    </div>

                    {footStau&&<HeaderTrade />}
                </div>

                <ReactModal ref={ref => this.dialog =ref}>
                            {this.state.dialogHTML}
                </ReactModal>
            </header>
        )
    }
}

export default withRouter(injectIntl(Header));
