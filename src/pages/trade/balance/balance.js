import React from 'react';
import { FormattedMessage,FormattedHTMLMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import {numToKiloMillion,formatSort} from '../../../utils';
const BigNumber = require('big.js');
BigNumber.RM = 0;
class Balance extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showZero:true,
            sdicBase:{
                key:"balance",
                reverse:0
            }
        }
    }
    componentDidMount() {
    }
    changeShowZero(){
        this.setState({
            showZero: !this.state.showZero
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
                return (<i>↑</i>)
            }else if(sdicBaseRev==-1){
                return (<i>↓</i>)
            }else if(sdicBaseRev == 0){
                return (<i></i>)
            }
        }else{
            return (<i></i>)
        }
    }
    detailDataFormat(data){
        let resultDate ={}
        BigNumber.RM = 0;
        Object.keys(data).map(
            (key, index) => {
                resultDate[key] = data[key];
                resultDate[key].totalUSD = data[key].usdExchange=="--"?-1:new BigNumber(parseFloat(data[key].total)).times(parseFloat(data[key].usdExchange));
            }
        )
        return resultDate
    }
    render() {
        const { user ,assets,money} = this.props;
        // console.log(assets.detail.data);
        const showZero = this.state.showZero;
        BigNumber.RM = 0;
        let detailDataFormed ;
        let marketsAssetsDetail ;
        let totalAll = 0;
        if(assets.detail.data){
            detailDataFormed = this.detailDataFormat(assets.detail.data)
            marketsAssetsDetail = formatSort(detailDataFormed,this.state.sdicBase);
            
            // Object.keys(marketsAssetsDetail).map(
            //     (key, index) => {
            //         let toAdd = marketsAssetsDetail[key].totalUSD
            //         if(toAdd!=-1){
            //             totalAll += parseFloat(toAdd);
            //         }
            //     }
            // )
        }
        // if(assets.total){
        //     totalAll = assets.total.total_legal_tender;
        // }
        let rate = 1;
        if(money.rate.exchangeRateUSD){
            rate = money.rate.exchangeRateUSD[money.locale.name.toUpperCase()];
        }
        return (
            <div className="sidebar-balance">
                <div className="trade-item-title" >
                    <div className="trade-item-title-right">
                    <input type='checkbox' checked={showZero?"checked":""} onChange={()=> {this.changeShowZero()}}/><span onClick={()=> {this.changeShowZero()}}> <FormattedMessage id="Hide0"/></span>
                    </div>
                    <h4><FormattedMessage id="sidebarBalance"/></h4>
                </div>
                {user?
                    <div className="trade-content">
                        <table>
                            <thead>
                                <tr>
                                    <th>&nbsp;</th>
                                    <th className="text-right" onClick={()=>{this.changeSdicBase("total")}}><FormattedMessage id="sidebarTatal"/>{this.returnIReverse("total")}</th>
                                    <th className="text-right" onClick={()=>{this.changeSdicBase("totalUSD")}}><em>{money.locale.name.toUpperCase()}</em> <FormattedMessage id="sidebarValue"/>{this.returnIReverse("totalUSD")}</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    assets.detail.data?(
                                        Object.keys(marketsAssetsDetail).map(
                                            (key, index) => {
                                                let item = marketsAssetsDetail[key];
                                                BigNumber.RM = 0;
                                                totalAll+=item.totalUSD==-1?0:new BigNumber(item.totalUSD).times(rate).toFixed(2)-0;
                                                if(showZero&&item.total==0){
                                                }else{
                                                    return  <tr key={key}>
                                                                <td>{key}</td>
                                                                <td className="text-right">{item.total==0?0:new BigNumber(item.total).toFixed(8)}</td>
                                                                <td className="text-right">{item.totalUSD==-1?"--":new BigNumber(item.totalUSD).times(rate).toFixed(2)}</td>
                                                            </tr>
                                                }
                                            }
                                        )
                                    ):(
                                        <tr>
                                            <td colSpan="3">
                                                <i className="norecord">
                                                    <svg className="icon">
                                                        <use xlinkHref="#icon-tishi"></use>
                                                    </svg>
                                                </i> &nbsp;
                                                <FormattedMessage id="balance.norecord" />
                                            </td>
                                        </tr>
                                    )
                                }
                            </tbody>
                        </table>
                        <p className="totalAll">{money.locale.logo} {totalAll}</p>
                    </div>
                :
                <p className="needlogin">
                    <i className="norecord">
                        <svg className="icon">
                            <use xlinkHref="#icon-tishi"></use>
                        </svg>
                    </i> &nbsp;
                    <FormattedMessage id="balance.norecord" />
                </p>
                }
            </div>
        )
    }
}
export default Balance;