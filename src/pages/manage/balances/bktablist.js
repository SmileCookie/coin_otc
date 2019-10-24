import React from 'react';
import { COIN_KEEP_POINT } from '../../../conf'
const BigNumber = require('big.js')
import { FormattedMessage, injectIntl ,FormattedDate} from 'react-intl';
import { formatDate } from '../../../utils'

export default class Bktablist extends React.Component{
   constructor(props){
       super(props);
       this.state = {
           billDetail : []
       }
       this.formatBillDetail = this.formatBillDetail.bind(this);
   }
   componentDidMount(){
      this.props.fetchManageRecord()
   }
   componentWillReceiveProps(nextProps) {
        if(nextProps.record.isloaded) {
            let billDetail = this.formatBillDetail(nextProps.record.data);
            this.setState({
                billDetail: billDetail
            })
        }
   }

   formatBillDetail(json){
        BigNumber.RM=0;
        let record = [];
        if(json){
            for(let i = 0; i<json.length; i++){
                let id = json[i].id;
                let showType = json[i].showType;
                let sendTime = json[i].sendTime.time;
                let amount = json[i].amount;
                let balance = json[i].balance;
                let coinName = json[i].coinName;
                let inout = json[i].bt.inout;
                let fees = json[i].fees;
                let status = json[i].status;
                record[i] = {};
                record[i].id = id;
                record[i].showType = showType;
                record[i].sendTime =  formatDate(sendTime);
                record[i].showType = showType;
                record[i].amount = new BigNumber(amount).toFixed(COIN_KEEP_POINT);
                record[i].balance = new BigNumber(balance).toFixed(COIN_KEEP_POINT);
                record[i].coinName = coinName;
                record[i].inout = inout == 1 ? "+" : "-";
                record[i].numFees = fees;
                record[i].fees = new BigNumber(fees).toFixed(COIN_KEEP_POINT);
                record[i].status = status;
                }
            }
            return record ;
    }
   render(){
        const {isloading,isloaded} = this.props.record
        return (
            isloading&&!isloaded? (
                <div>...LOADING</div>
            ) : (
                    <div className="bk-tabList">
                        <h2 className="bk-tabList-hd"><FormattedMessage id="account.text8" /></h2>
                        <div className="bk-tabList-bd">
                            <table width="100%" className="table-striped">
                            <thead>
                                <tr>
                                    <th><FormattedMessage id="account.text9" /></th>
                                    <th><FormattedMessage id="account.text10" /></th>
                                    <th><FormattedMessage id="account.text11" /></th>
                                    <th><FormattedMessage id="account.text12" /></th>
                                    <th><FormattedMessage id="account.text13" /></th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    this.state.billDetail.length ? (
                                        this.state.billDetail.map((item,index) => {
                                            return (
                                                <tr key={index}>
                                                  <td className="pc_block">{item.sendTime}</td>
                                                  <td className="pc_block">{item.coinName}</td>
                                                  <td className={"mobile_block money-coin " + item.coinName.toLowerCase()}></td>
                                                  <td>{item.showType}</td>
                                                  <td className="pc_block">
                                                    {item.inout}{item.amount} {item.coinName}={item.balance} {item.coinName}<br/>
                                                    { item.numFees > 0? `说明：${item.fees} ${item.coinName}` : '' } 
                                                  </td>
                                                  <td className="mobile_block">{item.inout}{item.amount}</td>
                                                  <td className="mobile_block ">{item.sendTime}</td>
                                                  <td>{item.status}</td>
                                                </tr>
                                            )
                                        })
                                    ) : (
                                        <tr className="tr-norecord">
                                            <td className="noRecord" colSpan="10">
                                                    <svg className="icon" aria-hidden="true">
                                                        <use xlinkHref="#icon-tishi"></use>
                                                    </svg>
                                                    <FormattedMessage id="common.norecord" />
                                            </td>
                                        </tr>
                                    )
                                }
                            </tbody>
                            </table>
                        </div>
                    </div>
                )
            )
   }
}





























