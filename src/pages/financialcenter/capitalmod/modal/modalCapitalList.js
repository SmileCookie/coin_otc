import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../../../conf'
import SelectAType from '../../select/selectAType'
import { Button,Pagination } from 'antd'
import { toThousands } from '../../../../utils'
const BigNumber = require('big.js')

export default class ModalCapitalList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],
            pageTotal:0,
            currId:'',
        }
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
    }

    componentDidMount(){
        const { pageIndex,pageSize } = this.state
        this.setState({
            currId:this.props.id
        },()=>this.requestTable())
    }

    componentWillReceiveProps(nextProps){
        const { pageSize} = this.state
        if(nextProps.isreLoad){
            this.setState({
                pageIndex:PAGEINDEX,
                // pageSize:PAGESIZE,
                currId:nextProps.id
            },()=>this.requestTable())
            this.props.queryClickBtn?this.props.queryClickBtn(false):"";
        }
    }

    //table List
    requestTable(currIndex,currSize){
        const { fundsType,userId,minBalance,maxBalance,userName,borrowMin,borrowMax,freezMin,freezMax,
                withDrawMin,withDrawMax,lendingMin,lendingMax,entrustMin,entrustMax,totalMoneyMin,totalMoneyMax } = this.props
          
        const { pageIndex,pageSize,currId } = this.state
        axios.post(DOMAIN_VIP+'/capitalCount/query',qs.stringify({
            fundsType,minBalance,maxBalance,userName,borrowMin,borrowMax,freezMin,freezMax,
            withDrawMin,withDrawMax,lendingMin,lendingMax,entrustMin,entrustMax,totalMoneyMin,totalMoneyMax,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize,
            userId:currId||userId
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

     //点击分页
     changPageNum(page,pageSize){
        this.setState({
            pageIndex:page,
            pageSize:pageSize
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


    render(){
        const { pageIndex,pageSize,pageTotal,tableList } = this.state
        return (
            <div className="x_panel">
                <div className="x_content">
                    <div className="table-responsive">
                        <table className="table table-striped jambo_table bulk_action table-linehei">
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">资金类型</th>
                                    <th className="column-title">用户编号</th>
                                    <th className="column-title">总金额</th>
                                    <th className="column-title">可用金额</th>
                                    <th className="column-title hide">融资融币借入金额</th>
                                    <th className="column-title">冻结金额</th>
                                    <th className="column-title">提现冻结金额</th>
                                    <th className="column-title hide">放贷冻结金额</th>
                                    <th className="column-title">挂单委托冻结金额</th>                                                                              
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length>0?
                                    tableList.map((item,index) => {
                                        BigNumber.RM = 0;
                                        return (
                                            <tr key={index}>
                                                <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                <td>{item.fundstypename}</td>
                                                <td>{item.userid}</td>
                                                <td>{toThousands(new BigNumber(item.balance).plus(item.freez).toFixed())}</td>
                                                <td>{toThousands(item.balance)}</td>
                                                <td className="hide">{item.insucces}</td>
                                                <td>{toThousands(item.freez)}</td>
                                                <td>{toThousands(item.withdrawfreeze)}</td>
                                                <td className="hide">{toThousands(item.outwait)}</td>
                                                <td>{toThousands(new BigNumber(item.freez).minus(item.withdrawfreeze).minus(item.outwait).toFixed())}</td>
                                            </tr>
                                        )
                                    })
                                    :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                }
                            </tbody>
                        </table>
                    </div>
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








































