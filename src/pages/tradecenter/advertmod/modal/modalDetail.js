import React from 'react'
import axios from './../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { toThousands } from './../../../../utils'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT, TIMEFORMAT_ss} from '../../../../conf'
import {Pagination} from 'antd'


export default class ModalDetail extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            orderNo:'',
            tableList:[],
        }
    }
    componentDidMount(){
        this.setState(
            {
                orderNo:this.props.id
            },()=>{
                this.requestTable()
            }
        )
        
    }
    componentWillReceiveProps(nextProps){
        this.setState(
            {
                orderNo:nextProps.id
            },()=>{
                this.requestTable()
            }
        )
    }
    requestTable(){
        const {orderNo} = this.state
            axios.post(DOMAIN_VIP+'/advertisement/Recordlist',qs.stringify({
                orderNo
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    this.setState({
                        tableList:result.data
                    })
                }   
            })
        }
    render(){
        const{tableList}= this.state
        return(
            <div className="x_panel">
                <div className="x_content">
                    <div className="table-responsive">
                        <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title must_153px">订单编号</th>
                                    <th className="column-title">交易方</th>
                                    <th className="column-title">货币类型</th>  
                                    <th className="column-title">交易币量</th>
                                    <th className="column-title">手续费</th>
                                    <th className="column-title">单价(CNY)</th> 
                                    <th className="column-title">交易金额(CNY)</th>
                                    <th className="column-title">交易时间</th>
                                    <th className="column-title">订单状态</th>
                                </tr>
                            </thead>
                            <tbody>
                            {
                                tableList.length>0?
                                tableList.map((item,index) => {
                                    return (
                                        <tr key={index}>
                                            <td>{index+1}</td>
                                            <td>{item.recordNo}</td>
                                            <td>{item.dealUserName}</td>
                                            <td>{item.coinTypeName}</td>
                                            <td>{item.coinNumber}</td>
                                            <td>{item.freeAmount}</td>
                                            <td>{item.coinPrice?toThousands(item.coinPrice):''}</td>
                                            <td>{toThousands(item.sumAmount)}</td>                                                         
                                            <td>{item.coinTime ? moment(item.coinTime).format(TIMEFORMAT_ss) : '--'}</td>  
                                            <td>{item.statusName}</td>
                                        </tr>
                                    )
                                })
                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                            }
                        </tbody>
                        </table>
                    </div>
                    
                </div>
            </div>
        )
        
    }
}