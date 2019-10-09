import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,DAYFORMAT,PAGRSIZE_OPTIONS20 } from '../../../../conf'
import moment from 'moment'
import { toThousands,tableScroll } from '../../../../utils'
import { DatePicker,Select,Modal, Button ,Tabs,Pagination,message} from 'antd'
const TabPane = Tabs.TabPane;

export default class ModalBillDetailOTC extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[], 
            pageTotal:0,
            tableScroll:{
                tableId:'MDALBLDEotC',
                x_panelId:'MDALBLDEotCXX',
                defaultHeight:500,
            }
        }
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.getHeight = this.getHeight.bind(this)

    }

    componentDidMount(){
        // const { pageIndex,pageSize,tableName } = this.state
        // this.requestTable(pageIndex,pageSize,this.props.curId)
    }
    componentDidMount(){
        // const { pageIndex,pageSize,tableName } = this.state
        // this.requestTable(pageIndex,pageSize,this.props.curId)
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`)
    }
    componentWillUnmount(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight){
        this.setState({
            xheight
        })
    }
    componentWillReceiveProps(nextProps){
        const { pageSize} = this.state
        if(!nextProps.showHide&&this.state.pageSize>10){
            this.setState({
                height:nextProps.xheight
            })
        }else{
            this.setState({
                height:0,
            })
        }
        if(nextProps.isreLoad){
            // this.setState({
            //     pageIndex:PAGEINDEX,
            //     pageSize:PAGESIZE
            // })
            this.requestTable(PAGEINDEX,pageSize,nextProps.curId)
            this.props.queryClickBtn&&this.props.queryClickBtn(false)
        }
    }
    //请求列表
    requestTable(currIndex,currSize,currId){
        const { coinTypeId,id,userId,startValue,addTimeStart,addTimeEnd,userName,action } = this.props
        const { pageIndex,pageSize } = this.state
        if(coinTypeId!=0||id||userId||addTimeStart||addTimeEnd||userName||action){
            axios.post(DOMAIN_VIP+'/otcBill/list',qs.stringify({
                coinTypeId,id,startValue,addTimeStart,addTimeEnd,userName,action,
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
        }else{
            message.warning("至少输入一条查询条件")
        }
    }

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page,
            pageSize:pageSize
        })
        this.requestTable(page,pageSize,this.props.curId)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size,this.props.curId)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }

    

    render(){
        const { pageSize,pageIndex,tableList,tableListNow,pageTotalNow,pageTotalAgo,pageTotal } = this.state
        return(
            <div className="x_panel">
                <div className="x_content">
                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">资金类型</th>
                                    <th className="column-title">用户编号</th>
                                    <th className="column-title">账单流水号</th>                                                
                                    <th className="column-title">账单类型</th>
                                    <th className="column-title">记账时间</th>
                                    <th className="column-title">发生额</th>   
                                    <th className="column-title">广告费</th>                                                                                                                 
                                    <th className="column-title">余额</th> 
                                    <th className="column-title">保证金余额</th>                                                                                                                   
                                    <th className="column-title">备注</th>                                                                                                   
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length>0?
                                    tableList.map((item,index) => {
                                        return (
                                            <tr key={index}>
                                                <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                <td>{item.coinTypeName}</td>
                                                <td>{item.userid}</td>
                                                <td>{item.id}</td>
                                                <td>{item.actionname}</td>
                                                <td>{moment(item.addtime).format(TIMEFORMAT)}</td>
                                                <td className='moneyGreen'>{item.inout==1?'+':item.inout==2?'-':''}{toThousands(item.amount)}</td>
                                                <td className='moneyGreen'>{toThousands(item.fee)}</td>
                                                <td className='moneyGreen'>{toThousands(item.curramount)}</td>
                                                <td className='moneyGreen'>{toThousands(item.storefreezbalance)}</td>
                                                <td>{item.memo}</td>                                                                                                                  
                                            </tr>
                                        )
                                    })
                                    :<tr className="no-record"><td colSpan="11">暂无数据</td></tr>
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
                                    showQuickJumper
                                    pageSizeOptions={PAGRSIZE_OPTIONS20}
                                    defaultPageSize={PAGESIZE}
                                     />
                    }
                </div>
            </div>
        )
    }
}

























