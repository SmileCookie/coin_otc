import React from 'react';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import { FormattedMessage, injectIntl } from 'react-intl';
import { withRouter } from 'react-router'
import SidebarList from './sidebarList.js';
import SidebarListDnd from './sidebarListDnd.js';
import {isMobile} from '../../../utils';
import { FETCH_MARKETS_INFO,ERRORCONFIG } from '../../../conf';
const BigNumber = require('big.js');

class Sidebar extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            initTab: 1,
            showEstimated:true,
            sdicBase:{
                key:"key",
                reverse:0
            },
            btnStus:0,
            coinSearch:'',
            dataError:false
        }
        this.defaultTab = this.defaultTab.bind(this);
        this.changeShowEstimated = this.changeShowEstimated.bind(this);
        this.clearFilterVal = this.clearFilterVal.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.tabsOnselect = this.tabsOnselect.bind(this)
        this.chooseMarket = this.chooseMarket.bind(this)
    }
    componentDidMount(){
        //throw new Error(2222)
        
     
        const { marketsConfData , money, pathName} = this.props;
        const thisName = pathName || "btc_usdt";
        const allName = thisName+"_hotdata_"+marketsConfData[thisName].numberBiFullName;
        const allnameArry = allName.split("_");
        this.defaultTab(allnameArry[1]);
        this.props.changeMarketinfo(thisName,allName,money.locale.name);
        this.interval = setInterval(()=>{
            this.props.fetchMarketsData();
        },FETCH_MARKETS_INFO)
        
        //根据路由判断市场
        let _market = this.props.location.query.market;
        if(_market){
            this.chooseMarket(_market)
        }
        
    }
    chooseMarket(market){
        let _index = 1;
        market == 'store' ? _index = 0 : market == 'usdt'? _index = 1 : _index = 2;
        this.setState({
            initTab:_index
        })
    }
    defaultTab(MarketName){
        const tabIndex = MarketName=="btc"?2:1;
        //this.tabsOnselect(tabIndex)
    }
    //tab onSelect event
    tabsOnselect(index){
        this.setState({
            initTab:index
        },()=>{console.log(index)})
    }
    changeShowEstimated(){
        this.setState({
            showEstimated: !this.state.showEstimated
        })
    }
    changeSdicBase(nextKey){
        const thisKey = this.state.sdicBase.key;
        const thisReverse = this.state.sdicBase.reverse;
        let nextReverse
        
        if(thisKey==nextKey){
            if(thisReverse==0){
                nextReverse = -1;
            }else if(thisReverse==-1){
                nextReverse=1
            }else if(thisReverse==1){
                nextReverse=0
            }
        }else{
            nextReverse=-1
        }
        this.setState({
            sdicBase:{
                key:nextKey,
                reverse:nextReverse
            }
        })
    }
    returnIReverse(key){
        const sdicBaseKey = this.state.sdicBase.key;
        const sdicBaseRev = this.state.sdicBase.reverse;
        if(key==sdicBaseKey){
            if(sdicBaseRev==1){
                return (<i className="icon paixujiantou-daoxu"></i>)
            }else if(sdicBaseRev==-1){
                return (<i className="icon paixujiantou-zhengxu"></i>)
            }else if(sdicBaseRev == 0){
                return (<i className="icon paixujiantou-moren"></i>)
            }
        }else{
            return (<i className="icon paixujiantou-moren"></i>)
        }
    }
    componentWillUnmount() {
        // window.removeEventListener('scroll', this.topByWheel);
        clearInterval(this.interval);
    }
    topByWheel(){
        let isM = isMobile();
        const sider = this.refs.sidebar
        if(sider && !isM){
            
            let scrollTop =  document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;
            let scrollLeft =  document.documentElement.scrollLeft || window.pageXOffset || document.body.scrollLeft;
            // console.log(scrollTop);
            let domHeight = document.body.clientHeight;
            let sidebarHeight = this.refs.sidebar.clientHeight;
            // let top = 0;
            let top = 0;
            if(scrollTop>70){
                sider.style.position = 'fixed';
                sider.style.left = -scrollLeft+"px";
                sider.style.marginLeft = 10+"px";
               
                if(scrollTop >= domHeight-409-sidebarHeight){
                    // console.log(domHeight+":"+sidebarHeight+"=> scrollTop:"+scrollTop);
                    // top =  new BigNumber(domHeight).minus(sidebarHeight).minus(479);
                    top =  new BigNumber(domHeight).minus(409).minus(sidebarHeight).minus(scrollTop);
                }
            }
            if(scrollTop<70){
                sider.style.position = 'absolute';
                sider.style.left = 0;
                sider.style.marginLeft = 0;
            } 
            sider.style.top = top + "px";
        }
    }
    componentWillReceiveProps(nextProps){
        if(nextProps.pathName&&(nextProps.pathName != this.props.pathName)){
            const allnameArry = nextProps.pathName.split("_")
            this.defaultTab(allnameArry[1]);
        }
    }
    //清除弹窗
    clearFilterVal(){
        this.setState({
            coinSearch:'',
            btnStus:0
        })
    }
    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        if(name == 'coinSearch'&&value){
            this.setState(preState => {
                if(preState.btnStus != 1){
                    return {btnStus:1}
                }
            })
        }else{
            this.setState(preState => {
                if(preState.btnStus != 0){
                    return {btnStus:0}
                }
            })
        }
    }

    componentDidCatch(){
        if(window.ERRORCONFIG){
            this.setState({
                dataError:true
            })
            // console.log(err,infor)
        }else{
            // console.log(err,infor)
        }
    }

    render() {
        //throw new Error(2222)
        if(this.state.dataError){
             
            return (
                    <div className="sidebar">
                        <div className="iconfont icon-jiazai new-loading"></div>
                    </div>
                 )
        } 
        const { intl,isLoaded, markets , currentMarket ,marketsConfLoaded,marketsConfData,money,dropMenu} = this.props;
        const { btnStus , coinSearch, initTab,dataError } = this.state
        const {locale} = this.props.intl;
        // console.log(locale)
        if(!isLoaded||!marketsConfLoaded){
            return <div>Loading...</div>;
        }
        
        let rateBtc = markets.USDT.btc_usdt_hotdata_Bitcoin[0];
        //console.log(dropMenu);
        let NowListComponent = dropMenu?SidebarListDnd:SidebarList;
        return (
            <div className="sidebar" ref="sidebar">
                <div className="sider-search">
                    <h3><FormattedMessage id="市场" /></h3>
                    <div className="search-form">
                        <input type="text" placeholder={intl.formatMessage({id:'币种搜索'})} name="coinSearch" value={coinSearch} onChange={this.handleInputChange} />
                        <button onClick={btnStus==1?this.clearFilterVal:null} className={btnStus==0?"iconfont icon-search-bizhong":"iconfont icon-shanchu-moren"}></button>
                    </div>
                </div>
                <Tabs onSelect={this.tabsOnselect} selectedIndex={initTab}>
                    <div className="sidebar-title">
                        <TabList className="mark clearfix">
                            <Tab>&#xe6ad;<FormattedMessage id="自选" /></Tab>
                            <Tab><span>USDT</span></Tab>
                            <Tab><span>BTC</span></Tab>
                        </TabList>
                    </div>
                    <p className="side_th">
                        <span className="side_th_market" onClick={()=>{this.changeSdicBase("key")}}>
                            <FormattedMessage id="市场"/>
                            {this.returnIReverse("key")}
                        </span>
                        <span className="side_th_price" onClick={()=>{this.changeSdicBase("0")}}>
                            <FormattedMessage id="价格"/>
                            {this.returnIReverse("0")}
                        </span>
                        <span className="side_th_change" onClick={()=>{this.changeSdicBase("8")}}>{this.returnIReverse("8")}
                            <FormattedMessage id="涨幅"/>
                        </span>
                    </p>
                    <TabPanel>
                        <NowListComponent 
                            marketType = "FAV" 
                            sdicBase={this.state.sdicBase} 
                            showEstimated={this.state.showEstimated} 
                            markets = { markets.FAV }  
                            rate={rateBtc} 
                            currentMarket={currentMarket} 
                            changeMarketinfo={this.props.changeMarketinfo} 
                            marketsConfData={marketsConfData} 
                            fetchMarketsData={this.props.fetchMarketsData}
                            user = {this.props.user}
                            money = {money}
                            coinSearch = {coinSearch}
                            recieveMarket = {this.props.recieveMarket}
                            locale={locale}
                        />
                    </TabPanel>
                    <TabPanel>
                        <NowListComponent 
                            marketType = "USDT" 
                            sdicBase={this.state.sdicBase} 
                            showEstimated={this.state.showEstimated} 
                            markets = { markets.USDT } 
                            rate={rateBtc} 
                            currentMarket={currentMarket} 
                            changeMarketinfo={this.props.changeMarketinfo} 
                            marketsConfData={marketsConfData} 
                            fetchMarketsData={this.props.fetchMarketsData}
                            user = {this.props.user}
                            money = {money}
                            coinSearch = {coinSearch}
                            recieveMarket = {this.props.recieveMarket}
                            locale={locale}
                        />
                    </TabPanel>
                    <TabPanel>
                        <NowListComponent 
                            marketType = "BTC" 
                            sdicBase={this.state.sdicBase} 
                            showEstimated={this.state.showEstimated} 
                            markets = { markets.BTC }  
                            rate={rateBtc} 
                            currentMarket={currentMarket} 
                            changeMarketinfo={this.props.changeMarketinfo} 
                            marketsConfData={marketsConfData} 
                            fetchMarketsData={this.props.fetchMarketsData}
                            user = {this.props.user}
                            money = {money}
                            coinSearch = {coinSearch}
                            recieveMarket = {this.props.recieveMarket}
                            locale={locale}
                        />
                    </TabPanel>
                    
                </Tabs>
            </div>
        )
    }
}

export default injectIntl(withRouter(Sidebar));