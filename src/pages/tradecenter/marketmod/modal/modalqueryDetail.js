
import React from 'react'
import axios from 'axios'
import qs from 'qs'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT} from '../../../../conf'
import moment from 'moment'
import { toThousands } from '../../../../utils'
import {Pagination} from 'antd'

export default class ModalqueryDetail extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            entrustId:'',
            market:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:'',
            pageTotal:''
        }
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)

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
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        })
        this.requestTable(page,pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }
    requestTable(currIndex,currSize){
        const {entrustId,pageIndex,pageSize,market} = this.state
    //    console.log(entrustId)
        axios.post(DOMAIN_VIP+'/entrustRecord/queryDetail',qs.stringify({
            entrustId:entrustId,
            market:this.props.enMarket,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            var sortTime = (result.data.list).sort
            // console.log(result);
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
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
                                <th className="column-title">成交时间</th>
                                <th className="column-title">成交价格</th>
                                <th className="column-title">成交数量</th>
                                <th className="column-title">买方用户编号</th> 
                                <th className="column-title">买方委托编号</th>
                                <th className="column-title">卖方用户编号</th> 
                                <th className="column-title">卖方委托编号</th> 
                            </tr>
                        </thead>
                        <tbody>
                            {
                                tableList.length>0?
                                tableList.map((item,index)=>{
                                    return (
                                        <tr key={index}>
                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                            <td>{moment(item.times).format(TIMEFORMAT)}</td>
                                            <td>{item.totalprice?toThousands(item.totalprice):''}</td>
                                            <td>{item.numbers}</td>
                                            <td>{item.useridbuy}</td>
                                            <td>{item.entrustidbuy}</td>
                                            <td>{item.useridsell}</td>
                                            <td>{item.entrustidsell}</td>
                                        </tr>
                                    )
                                }):
                                <tr className="no-record"><td colSpan="14">暂无数据</td></tr>
                            }
                        </tbody>
                    </table>
                </div>
                <div className="pagation-box">
                    {
                        pageTotal>0 && <Pagination
                                    size="small"
                                    current={pageIndex}
                                    total={pageTotal}
                                    showTotal={total => `总共 ${total} 条`}
                                    onChange={this.changPageNum}
                                    onShowSizeChange={this.onShowSizeChange}
                                    showSizeChanger
                                    showQuickJumper />
                    }
                </div>
            </div>
        )
    }

}





































