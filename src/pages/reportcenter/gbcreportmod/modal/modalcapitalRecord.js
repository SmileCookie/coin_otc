import React from 'react'
import axios from 'axios'
import qs from 'qs'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT} from '../../../../conf'
import moment from 'moment'
import { toThousands } from '../../../../utils'

export default class ModalcapitalRecord extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            entrustId:'',
            tableList:'',
            pageTotal:''
        }
    }
    componentDidMount(){
        this.setState({
            entrustId :this.props.enId
        },()=>this.requestTable())
    }
    componentWillReceiveProps(nextProps){
        this.setState({
            entrustId : nextProps.enId
        },()=>this.requestTable())
    }
    requestTable(){
        const {entrustId} = this.state
       console.log(entrustId)
        axios.get(DOMAIN_VIP+'/gbcreportmod/gbcRepoTrack/deallist',{params: {
            entrustId
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
                                <th className="column-title">成交编号</th>
                                <th className="column-title">成交时间</th>
                                <th className="column-title">买家用户编号</th>
                                <th className="column-title">买单委托编号</th> 
                                <th className="column-title">卖家用户编号</th>
                                <th className="column-title">卖单委托编号</th> 
                                <th className="column-title">成交单价</th> 
                                <th className="column-title">成交数量</th> 
                                <th className="column-title">成交总金额</th> 
                                <th className="column-title">委托类型</th>    
                                <th className="column-title">状态</th>                                           
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
                                            <td>{item.times?moment(item.times).format(TIMEFORMAT):''}</td>
                                            <td>{item.useridbuy}</td>
                                            <td>{item.entrustidbuy}</td>
                                            <td>{item.useridsell}</td>
                                            <td>{item.entrustidsell}</td>
                                            <td>{toThousands(item.unitprice,true)}</td>
                                            <td>{item.numbers}</td>
                                            <td>{item.totalprice}</td>
                                            <td>{item.typesName==1?'买入':item.types==-1?'取消':'卖出'}</td>
                                            <td>处理成功</td>
                                        </tr>
                                    )
                                }):
                                <tr className="no-record"><td colSpan="13">暂无数据</td></tr>
                            }
                        </tbody>
                    </table>
                </div>
            </div> 
        )
    }

}