import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { message} from 'antd'
import 'moment/locale/zh-cn';
import {TIMEFORMAT,DOMAIN_VIP } from '../../../../conf/index';
moment.locale('zh-cn');

export default class ModalwallteBill extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            id:'',
            tableList:[]
        }
    }
    componentDidMount(){
        this.setState({
            id:this.props.id,
        }, ()=> this.requestTable())
    }
    componentWillReceiveProps(nextProps){
            this.setState({
                id:nextProps.id,
            }, ()=> this.requestTable())
    }
    requestTable(){
        const {id} = this.state
        axios.post(DOMAIN_VIP+'/walletBillDetail/queryDetail',qs.stringify({
            txId:id
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data
                })
            } else{
                this.setState({
                    tableList:[]
                })
                message.warning(result.msg)
            } 
        })
    }
    
    render(){
        const{tableList} = this.state
        return(
        <div className="x_panel">
            <div className="x_content">
                <div className="table-responsive">
                <table className="table table-striped jambo_table bulk_action table-memo">
                        <thead>
                            <tr className="headings">
                                <th className="column-title">序号</th>
                                <th className="column-title wid300 min_153px">交易流水号</th>
                                <th className="column-title min_153px">地址</th>
                                <th className="column-title">金额</th>
                                <th className="column-title">资金类型</th>
                                <th className="column-title">发送方</th> 
                                <th className="column-title">接收方</th>
                                <th className="column-title">交易金额</th> 
                                <th className="column-title">网络费</th>  
                                <th className="column-title">交易类型</th>
                                <th className="column-title">区块高度</th> 
                                <th className="column-title">确认时间</th>                  
                            </tr>
                        </thead>
                        <tbody>
                            {
                                tableList.length>0?
                                tableList.map((item,index)=>{
                                    return (
                                            <tr key={index}>
                                            <td>{index+1}</td>
                                            <td>{item.txId}</td>
                                            <td>{item.address}</td>
                                            <td>{item.amount}</td>
                                            <td>{item.fundsType}</td>
                                            <td>{item.sendWallet}</td>
                                            <td>{item.receiveWallet}</td>
                                            <td>{item.txAmount}</td>
                                            <td>{item.fee}</td>
                                            <td>{item.dealType}</td>
                                            <td>{item.blockHeight}</td>
                                            <td>{moment(item.configTime).format(TIMEFORMAT)}</td>
                                        </tr>
                                    )
                                }):
                                <tr className="no-record"><td colSpan="12">暂无数据</td></tr>
                            }
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
            )
    }
}