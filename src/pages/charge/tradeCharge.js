import React from 'react';
import Form from '../../decorator/form';
import Pages from '../../components/pages';
import {fetchIntegral } from '../../redux/modules/level'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../conf'
import '../../assets/css/table.less';
import axios from 'axios';
import cookie from 'js-cookie'
import qs from 'qs';
import { connect } from 'react-redux';
import { separator,formatDate} from '../../utils';
import thunk from '../../../node_modules/redux-thunk';
const BigNumber = require('big.js');
import '../../assets/css/chargeList.less'


@connect(
    state => ({
        lng: state.language.locale,
        integral:state.level.integral,
        userInfo: state.session.baseUserInfo,
    }),
    (dispatch) => {
        return {
            fetchIntegral: () => {
                dispatch(fetchIntegral())
            }
        }
    }
)
@Form
class Leve extends React.Component{
    constructor(props){
        super(props)

        this.state = {
            tableList:[
               1,1,1,1,1,1,1,1,1
            ],
        }
        this.requestTable = this.requestTable.bind(this)
        this.checkNum = this.checkNum.bind(this)
    }
    componentDidMount(){
      
        this.requestTable();
    }

    requestTable(){
        axios.get(DOMAIN_VIP+"/getMarketFee").then(res => {
            //console.log(res.data)
            const result = res.data
            if(result.isSuc){
                for(let i=0;i<result.datas.length;i++){
                    result.datas[i].marketName = result.datas[i].marketName.replace('_','/').toUpperCase();
                    result.datas[i].sellFeeRate = this.checkNum(result.datas[i].sellFeeRate * 100) + '%'
                    result.datas[i].buyFeeRate = this.checkNum(result.datas[i].buyFeeRate * 100) + '%'
                }
                //console.log(result.datas)
                this.setState({
                    tableList:result.datas
                })
            }
        })
        
    }
    checkNum(num, rank = 6){
        if(!num) return(0);
        const sign = num / Math.abs(num);
        const number = num * sign;
        const temp = rank - 1 - Math.floor(Math.log10(number));
        let ans;
        if (temp > 0) {
            ans = parseFloat(number.toFixed(temp));
        }
        else if (temp < 0) {
            ans = Math.round(number / Math.pow(10, temp)) * temp;
        }
        else {
            ans = Math.round(number);
        }
        return (ans * sign);
    }
   
    render(){
        const {tableList} = this.state;
        const { formatMessage } = this.intl;
        const integral = this.props.integral;
       
        return(
             <div className="tableContent" style={{paddingTop:'60px'}}>
                <div className="grade-text">
                    <h3>{formatMessage({id: "new提示"})}</h3>
                    <p>{formatMessage({id: "1、挂单是提交一笔买入或卖出委托订单，该订单并未与买一，卖一价成交.挂单可以提高盘口深度增加买卖盘的流动性。"})}</p>
                    <p>{formatMessage({id: "2、当其他订单主动买入或卖出你挂出的订单，你将支付这笔订单交易手续费（价格相同的情况下，按照委托优先的原则后者支付吃单手续费）。"})}</p>
                    <p>{formatMessage({id: "3、吃单是直接吃掉当前委托买入或卖出的订单。"})}</p>
                    <p>{formatMessage({id: "4、主动吃掉当前委托订单，你将支付吃单交易手续费。"})}</p>
                    <p>{formatMessage({id: "5、交易手续费按照成交总额扣除，未成交部分不扣手续费。手续费以当前交易币种结算。"})}</p>
                    <p>{formatMessage({id: '6、VIP等级越高，需支付的手续费越少，详见“帐户等级”。'})}</p>
                </div>      
                <div className="grade-effect">
                        <div className="grade-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>{formatMessage({id: "交易对"})}</th>
                                        <th>{formatMessage({id: "挂单"})}</th>
                                        <th>{formatMessage({id: "吃单"})}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                {
                                    tableList.length>0&&tableList.map((item,index)=>{
                                        //let _marketName = item.marketName.replace('_','/')
                                        return(
                                            <tr key={index}>
                                                <td>{item.marketName}</td>
                                                <td>{item.sellFeeRate}</td>
                                                <td>{item.buyFeeRate}</td>
                                            </tr>
                                        )
                                    })
                                }
                                </tbody>
                            </table>
                        </div>
                    </div>
             </div>
        )
    }
}
export default Leve