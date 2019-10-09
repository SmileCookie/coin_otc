
import React from 'react'
import axios from 'axios'
import qs from 'qs'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT} from '../../../../conf'
import moment from 'moment'
import { toThousands } from '../../../../utils'

export default class ModalcapitalSource extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            batchid:'',
            tableList:'',
            pageTotal:''
        }
    }
    componentDidMount(){
        this.setState({
            batchid :this.props.enId
        },()=>this.requestTable())
    }
    componentWillReceiveProps(nextProps){
        this.setState({
            batchid : nextProps.enId
        },()=>this.requestTable())
    }
    requestTable(currIndex,currSize){
        const {batchid} = this.state
       console.log(batchid)
        axios.get(DOMAIN_VIP+'/gbcreportmod/gbcRepoTrack/moneylist',{params: {
            batchid
        }}).then(res => {
            const result = res.data;
            console.log(result);
            if(result.code == 0){
                this.setState({
                    tableList:result.data,
                    pageTotal:result.data.totalCount
                })
            }   
        })
    }

    render(){
        const {entrustId,pageIndex,pageSize,pageTotal,tableList} = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="table-responsive">
                    <table className="table table-striped jambo_table bulk_action table-linehei table-border">
                        <thead>
                            <tr className="headings">
                                <th className="column-title">序号</th>
                                <th className="column-title">成交记录编号</th>
                                <th className="column-title">成交市场</th>
                                <th className="column-title">成交用户</th>
                                <th className="column-title">手续费金额</th> 
                                <th className="column-title">回购比例</th>
                                <th className="column-title">手续费占比</th> 
                                <th className="column-title">手续费本币种</th>
                                <th className="column-title">BTC_USDC汇率</th> 
                                <th className="column-title">手续费原始金额</th>                                                
                                <th className="column-title">成交时间</th> 
                                <th className="column-title">成交单价</th> 
                                <th className="column-title">成交数量</th> 
                                <th className="column-title">成交总价</th> 
                            </tr>
                        </thead>
                        <tbody>
                            {
                                tableList.length>0?
                                tableList.map((item,index)=>{
                                    return (
                                        <tr key={index}>
                                            <td>{index+1}</td>
                                            <td>{item.transrecordid}</td>
                                            <td>{item.market}</td>
                                            <td>{item.userid}</td>
                                            <td>{item.amount}</td>
                                            <td>{item.feepercent}</td>
                                            <td>{item.feeratio}</td>
                                            <td>{item.currency}</td>
                                            <td>{item.btcusdcprice}</td>
                                            <td>{toThousands(item.originamount,true)}</td>
                                            <td>{item.transrecordtime?moment(item.transrecordtime).format(TIMEFORMAT):''}</td>
                                            <td>{toThousands(item.unitprice,true)}</td>
                                            <td>{item.numbers}</td>
                                            <td>{toThousands(item.totalprice,true)}</td>
                                        </tr>
                                    )
                                }):
                                <tr className="no-record"><td colSpan="14">暂无数据</td></tr>
                            }
                        </tbody>
                    </table>
                </div>
            </div> 
        )
    }

}