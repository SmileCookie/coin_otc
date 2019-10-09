
import React from 'react';
import Logo from '../../assets/image/base/logo-btcwinex.png'
import { Link } from 'react-router-dom';
import cookie from 'js-cookie'
import {withRouter} from 'react-router'
import { connect } from 'react-redux'
import { FormattedMessage,injectIntl } from 'react-intl';
import './header.less';
import confs from '../../conf'
import {CONF_MONEY, DOMAIN_VIP,otcChannel} from '../../conf'
import axios from 'axios'
import LanguageToggle from '../languageToggle/index'
import{checkVipEvent} from '../../redux/module/session'
import controls from '../../assets/image/business/control.png'
import controls_hover from '../../assets/image/business/control_hover.png'
import shangjia from '../../assets/image/business/vip.png'
import{optPop} from '../../utils'
import ReactModal from '../popBox'
import{setMoney,fetchAssetsTotal,fetchOtcAssetsTotal,fetchrWalletTotal,fetchTransAssetsTotal} from '../../redux/module/money'
const BigNumber = require('big.js')
//coinList: state.account.detail,
@connect(
    state => ({
        userInfor:state.session.userInfor,
        loading:state.session.loading,
        checkVipObj:state.session.checkVipObj,
        money: state.money,
        moneylogo: state.money.locale,
        moneyrate: state.money.rate.exchangeRateUSD,
        isShowMoneyTag: false,
        webScoketEvent:state.session.webScoketEvent,
    }),
   {
        checkVipEvent,
        setMoney,
        fetchAssetsTotal,
        fetchOtcAssetsTotal,
        fetchrWalletTotal,
        fetchTransAssetsTotal
   }
)
class Header extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            total:localStorage.getItem('total') || 0,
            //moneyNow:localStorage.getItem('moneyNow') || "USD",
            dialogHTML:'',
            op:true,
            hoverImgs:false
        };
       this.ch = window.location.href;
    }

    componentWillMount(){

    }
    componentDidMount() {
       this.props.checkVipEvent()
       if(this.props.userInfor.data){
            this.apms();
            this.interval = setInterval(()=>{
                this.apms();
            },5000)
        }
        this.showMoneyTag();
    }
    componentWillUnmount(){
        clearInterval(this.interval)
    }
    showMoneyTag() { // 是否显示理财中心
        axios.get(DOMAIN_VIP+'/manage/financial/userFinCenInfo').then((res)=>{
            const data = res.data.datas;
            const c = +data.authPayFlag;
            if (2 === c) {
                this.setState({
                    isShowMoneyTag: true
                })
            }
            ([2,3].includes(c)) && localStorage.setItem(cookie.get('zuid')+'ispay', 1);
        })
    }

      //去认证
      openGoCheck = () =>{
        let str =  <div className="image-dialog" style={{padding:'20px'}}>
                     <div className="dialog-cont" style={{paddingTop:'18px'}}>
                         <p style={{fontSize:'18px'}}><FormattedMessage id="尚未实名认证"/></p>
                         <p style={{paddingTop:"33px",fontSize:'16px'}}><FormattedMessage id="请您先完成实名认证后，再申请广告商家认证"/></p>
                     </div>
                     <div className="dialog-footer" style={{marginTop:'80px'}}>
                         <a style={{ lineHeight: "44px"}} href="/bw/mg/authenOne" className="btn submit"><FormattedMessage id="前往认证" /></a>
                     </div>
                 </div>
         this.setState({dialogHTML:str},()=>{
             this.dialog.openModal();
         });
     }

    //商家跳转
    goUserStatus = () =>{
        //this.openGoCheck()
        //console.log(this.props.userInfor.data.cardStatus);

        // const {userInfor} = this.props;
        const {storeStatus,storeType} = this.props.checkVipObj
        // if(userInfor.data.cardStatus !== 6){
        //     this.openGoCheck()
        //     return
        // }
        axios.get(confs.api + '/web/v1/store/lockStatus').then(res =>{
            if(res.status == 200){
                let {code,msg} = res.data
                //console.log(msg);

                if(code == 200){
                    window.location.href = `/otc/business`
                    // if(storeStatus == 1 && storeType == 1){
                    //     window.location.href = `/otc/business`
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
                }else{
                    optPop(() =>{},msg,{timer: 1500},true)
                }
            }
        })
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

    }

    // 判断登录前 路由
    checkWhereJumpFrom = () =>{
        let path = window.location.pathname;
        if ( path.includes('/otc/trade')) {
            localStorage.setItem('otcLoginBefore','/otc/trade');
        }
    }

    //切换币种计算市场，总额
    sw = () =>{
        this.setState({
            op: true
        })
    }

    hd = () => {
        this.setState({
            op: false
        })
    }

    setMoney = (name) =>{
        // console.log(name)
        this.props.setMoney(name);
    }

    apms = () =>{
        this.pms([this.props.fetchAssetsTotal(),this.props.fetchOtcAssetsTotal(),this.props.fetchrWalletTotal(),this.props.fetchTransAssetsTotal()]);
    }

    pms = (ay = []) =>{
        Promise.all(ay).then((res)=>{
            var sum = new BigNumber(0);

            res.forEach((item, i)=>{
                //console.log(item);
                var n = item.total_usdt != 0 ? item.total_usdt : 0;
                sum = sum.plus(n);
            });

            try{
                //console.log(this.props.moneylogo.name);
                const E = this.props.moneyrate[this.props.moneylogo.name];
                //console.log(E);

                sum = sum.times(E ? E : 0);
            }catch(e){
                sum = 0;
            }
            //console.log(sum);

            this.tosum = sum.toFixed(2) + '';
            //console.log(this.tosum);

            this.forceUpdate();
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
        // if(this.props.webScoketEvent){
        //     let sessionId = cookie.get("zsessionId")
        //     this.props.webScoketEvent.send(JSON.stringify({ "event": 'unsub',"channel":otcChannel,"params":{"token":sessionId,"type":"web"}}))
        // }
        window.location.href = '/login/logout/'
        //console.log(this.props.webScoketEvent);

    }


    render() {
        const {formatMessage} = this.props.intl;
        const {op,hoverImgs, isShowMoneyTag} = this.state;
        const ch = this.ch;
        const {storeStatus,storeType,load} = this.props.checkVipObj
        const {userInfor,loading,money} = this.props
        let moneyNow = money.locale.name.toUpperCase();
        let total = 0
        if(money.rate.exchangeRateUSD){
            total = this.tosum;
        }

        return (
            <header className="header up-mx-wp">
                <div className="contain clearfix">
                    <div className="header-left">
                        <a className="logo" href="/">
                            <img  src={Logo} />
                        </a>
                        <ul className="nav clearfix">
                            <li>
                                <a title={formatMessage({id:"币币交易"})} href="/bw/trade/" className={ch.includes("/opo") ? 'bbyh-choosed' : ''} >
                                    <FormattedMessage id="币币交易"/>
                                </a>
                            </li>
                            <li className="hot">
                                <a title={formatMessage({id:"多屏看板"})} href="/bw/multitrade"   className={ch.includes("/opd") ? 'bbyh-choosed' : ''} >
                                    <FormattedMessage id="多屏看板"/>
                                </a>
                            </li>
                            <li>
                                <a title={formatMessage({id:"阿波罗计划"})} href="/bw/money"  className={ch.includes("/ablo") ? 'bbyh-choosed' : ''}>
                                    <FormattedMessage id="阿波罗计划"/>
                                </a>
                            </li>
                            <li>
                                <a title={formatMessage({id:"法币交易"})} href="/otc/trade"  className={ch.includes("/otc") ? 'bbyh-choosed' : ''}>
                                    <FormattedMessage id="法币交易"/>
                                </a>
                            </li>
                        </ul>
                    </div>
                    <div className="header-right">
                        {/* <LanguageToggle theme="mobile" /> */}
                        {
                            (userInfor.code == '200' && cookie.get("zloginStatus")==1 && !load) &&
                            <div className="nav-manage">
                                    {
                                         ( (storeStatus == 1 &&  storeType == 1) || (storeStatus !== 1 &&  storeType == 2) ) ?
                                        <div className="controls" >
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

                        <div className="nav-manage">
                            <div>
                                <div id="bbyh-moneyIn" style={{color:'#FFFFFF'}}>
                                    <Link className="nbspa" to=""><i className="iconfont icon-gonggao-moren"></i></Link>
                                    <i style={{marginLeft: '5px'}} className="per-icon-jiao"></i>
                                </div>
                                <ul>
                                    <li className="big-menu" style={{textAlign:'center'}}>
                                        <a href="/bw/news">
                                            <p><FormattedMessage id="新闻"/></p>
                                        </a>
                                    </li>
                                    <li className="big-menu" style={{textAlign:'center'}}>
                                        <a href="/bw/announcements">
                                            <p><FormattedMessage id="公告"/></p>
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        {
                            userInfor.code == '200' && cookie.get("zloginStatus")==1? (
                                <div className="nav-manage">
                                    <div>
                                        <div id="bbyh-moneyIn" style={{color:'#FFFFFF'}}><a href="/otc/otcOrder"><i className="iconfont icon-weituoguanli-moren"></i><FormattedMessage id="订单"/></a></div>
                                    </div>
                                    <div>
                                        <div id="bbyh-moneyIn" style={{color:'#FFFFFF'}}><a href="/otc/advertisement"><i className="iconfont icon-weituoguanli-moren"></i><FormattedMessage id="广告管理"/></a></div>
                                    </div>
                                    <div>
                                        <div id="bbyh-moneyIn" style={{color:'#FFFFFF'}}>
                                            <a className="nbspa" href="/bw/manage/account/balance"><i className="iconfont icon-zichanguanli-moren"></i><FormattedMessage id="资产管理"/></a>
                                            <i style={{marginLeft: '5px'}} className="per-icon-jiao"></i>
                                        </div>
                                        <ul>
                                            <li className="big-menu">
                                                <a href="/bw/manage/account/balance">
                                                    <p><FormattedMessage id="我的钱包"/></p>
                                                </a>
                                            </li>
                                            <li className="big-menu">
                                                <a href="/bw/manage/account/currency">
                                                    <p><FormattedMessage id="交易账户"/></p>
                                                </a>
                                            </li>
                                        </ul>
                                    </div>
                                    <div className="nav-manage-banlence">
                                        <div>
                                            <a href="/bw/mg/account">
                                                <i className="iconfont icon-zhanghu-moren"></i>
                                                <i className="per-icon-jiao"></i>
                                            </a>
                                    </div>
                                    <ul>
                                        <li className="big-menu">
                                            <a href="/bw/mg/account">
                                                <p><FormattedMessage id="用户中心"/></p>
                                                <i>{userInfor.data.email}</i>
                                                <em className="vip-grade"></em>
                                            </a>
                                        </li>
                                        {
                                            isShowMoneyTag &&
                                            <li className="big-menu">
                                                <a href="/bw/manage/account/cmoney">
                                                    <p><FormattedMessage id="理财中心"/></p>
                                                </a>
                                            </li>
                                        }
                                        <li className="big-menu">
                                            <a href="/bw/manage/account/">
                                                <p className="gray"><FormattedMessage id="资产折算"/>:</p>
                                                <i className="light">
                                                    ≈ {total}
                                                    &nbsp;{moneyNow}
                                                </i>
                                            </a>
                                        </li>
                                        <li>
                                            <a onClick={this.logout}  target="_self"><FormattedMessage id="退出登录" /></a>
                                        </li>
                                    </ul>
                                 </div>
                                </div>
                            ):(
                                <div className="nav-account">
                                    <a className="btn-login"  href="/bw/login" onClick={this.checkWhereJumpFrom}><FormattedMessage id="登录" /></a>
                                    <a className="btn-signup" href="/bw/signup"><FormattedMessage id="注册" /></a>
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
                </div>
                <ReactModal ref={ref => this.dialog =ref}>
                    {this.state.dialogHTML}
                </ReactModal>
            </header>
        )
    }
}

export default withRouter(injectIntl(Header));
