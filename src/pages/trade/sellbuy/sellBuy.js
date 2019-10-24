import React from 'react';
import { FormattedMessage } from 'react-intl';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import { Link } from 'react-router'
import SellBuyForm from './sellBuyForm';
import { formatURL } from 'Utils'
import {ERRORCONFIG} from '../../../conf'

class Practice extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            tab:1,
            formTab:1, // 1 is buy 2 is sell
            buyFeeRate:0,
            sellFeeRate:0,
            dataError:false
        }
    }

    componentDidMount() {
        this.props.fetchUserInfo() // 查询用户的信息
    }
    componentWillReceiveProps(nextProps){
        const { buyFeeRate, sellFeeRate,parentPrice,parentAmount } = this.state
        if(nextProps.user){
            if(nextProps.buySell.userInfo && nextProps.marketinfo.currentMarket){ // 设置交易费率
                this.setState({
                    buyFeeRate:nextProps.buySell.userInfo[nextProps.marketinfo.currentMarket][0]['takerFeeRate'],
                    sellFeeRate:nextProps.buySell.userInfo[nextProps.marketinfo.currentMarket][0]['makerFeeRate']
                })
            }
        }else{
            this.setState({
                buyFeeRate:nextProps.marketsConfData[nextProps.marketinfo.currentMarket]['takerFeeRate'] * 100 + '00',
                sellFeeRate:nextProps.marketsConfData[nextProps.marketinfo.currentMarket]['makerFeeRate'] * 100 + '00'
            })
        }
    }
    checkout(n){
        this.setState({
            tab:n
        })
    }
    changeFormTab(num){
        // this.props.updateSellPrice(0,0)
        this.setState({
            formTab:num,
        })
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
        if(this.state.dataError){
            return (
                <div style={{position:'relative',width:'500px'}}>
                    <div className="iconfont icon-jiazai new-loading" style={{left:'32%',top:'122px'}}></div>
                </div>)
        } 
        const { tab, formTab, buyFeeRate , sellFeeRate, } = this.state
        const { user } = this.props
        //const feesVal = (formTab-1)?sellFeeRate:buyFeeRate;
        const feesVal = buyFeeRate;
        return (
            <Tabs>
                {/* <div className="">
                 <div className="trade-item"> */}
                    <div className="trade-head-title" >
                        <div className="trade-item-title-right">
                            <ul className="buyfromsell">
                                <li className={tab==1?"react-tabs__tab--selected":""} onClick={()=>{this.checkout(1)}}>
                                    <FormattedMessage id="限价单"/>
                                </li>
                                <li className={tab==2?"react-tabs__tab--selected":""} onClick={()=>{this.checkout(2)}}>
                                    <FormattedMessage id="计划单"/>
                                    <div className="sull-tip">
                                        <i className="iconfont icon-bangzhutishi"></i>
                                        <p><FormattedMessage id="即达到触发价格时，系统会按照您设定的交易方向、委托价格和成交数量自动挂单。之后的成交规则与限价委托一样。"/></p>
                                    </div>
                                </li>
                            </ul>
                        </div>
                        <Link style={{display:'none'}} to={user?formatURL('/mg/grade'):formatURL('/login')}>
                            <span className="trade-fee">
                                VIP{this.props.vipRate||0}<FormattedMessage id="手续费率"/>:
                                <b className="trade-feesnum">{+feesVal ? feesVal : '0.000'}%</b>
                            </span>
                        </Link>
                    </div>
                    <div className="up-trade-s">
                    <div className="trade-item-formTab">
                        <ul>
                            <li>&nbsp;</li>
                            <li>&nbsp;</li>
                        </ul>
                    </div>
                    <div className="up-sb-wp clearfix">
                        {
                            tab==1&&<div>
                                {<SellBuyForm {...this.props} isBuy = {1} selectType={0} feesVal={feesVal} formTab= {1}/>}
                                {<SellBuyForm {...this.props} isBuy = {0} selectType={0} feesVal={feesVal} formTab= {2}/>}
                            </div>
                        }
                            
                        {
                            tab==2&&<div>
                                {<SellBuyForm {...this.props} isBuy = {1} selectType={1} feesVal={feesVal} formTab= {1}/>}
                                {<SellBuyForm {...this.props} isBuy = {0} selectType={1} feesVal={feesVal} formTab= {2}/>}
                            </div>
                        }
                    </div>
                    </div>
                {/* </div> 
            </div>*/}
            </Tabs>
        );
    }
}

export default Practice;