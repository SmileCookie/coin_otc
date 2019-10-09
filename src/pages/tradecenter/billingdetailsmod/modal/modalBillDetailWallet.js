import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,DAYFORMAT,PAGRSIZE_OPTIONS20 } from '../../../../conf'
import moment from 'moment'
import { toThousands,tableScroll } from '../../../../utils'
import { DatePicker,Select,Modal, Button ,Tabs,Pagination,message} from 'antd'
const TabPane = Tabs.TabPane;

export default class ModalBillDetailWallet extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],            
            tableListNow:[],
            tableListAgo:[],
            pageTotalNow:0,
            pageTotalAgo:0,
            tableName:1,
            height:0,
            tableScroll:{
                tableId:'MDALBILDETWET',
                x_panelId:'MDALBILDETWETXX',
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
        // this.requestTable(pageIndex,pageSize,this.props.curId,tableName)
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
            const { tableName } = this.state
            // this.setState({
            //     pageIndex:PAGEINDEX,
            //     pageSize:PAGESIZE
            // })
            this.requestTable(PAGEINDEX,pageSize,nextProps.curId,tableName)
            this.props.queryClickBtn&&this.props.queryClickBtn(false)
        }
    }
    //请求列表
    requestTable(currIndex,currSize,currId,currKey){
        const { fundsType,type,id,userId,startValue,startDate,endDate,userName } = this.props
        const { tableName,pageIndex,pageSize } = this.state
        if(fundsType!=0||type!=0||id||userId||startDate||endDate||userName){
            axios.post(DOMAIN_VIP+'/billDetailWallet/query',qs.stringify({
                fundsType,type,id,startValue,startDate,endDate,userName,
                tableName:currKey||tableName,
                pageIndex:currIndex||pageIndex,
                pageSize:currSize||pageSize,
                userId:currId||userId
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    if(tableName == 1){
                        this.setState({
                            tableListNow:result.data.list,
                            pageTotalNow:result.data.totalCount
                        })
                    }else{
                        this.setState({
                            tableListAgo:result.data.list,
                            pageTotalAgo:result.data.totalCount
                        })
                    }
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
        const { tableName } = this.state
        this.setState({
            pageIndex:page,
            pageSize:pageSize
        })
        this.requestTable(page,pageSize,this.props.curId,tableName)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        const { tableName } = this.state
        this.requestTable(current,size,this.props.curId,tableName)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }
    //tabs 回调
    callbackTabs= key =>{
        const { pageIndex , pageSize } = this.state
        this.setState({
            tableName:key,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE
        })
        // this.requestTable(pageIndex,pageSize,this.props.curId,key)
    }
    render(){
        const { pageSize,pageIndex,tableListAgo,tableListNow,pageTotalNow,pageTotalAgo,tableName } = this.state
        let tableList = tableName == 1?tableListNow:tableListAgo;
        let pageTotal = tableName == 1?pageTotalNow:pageTotalAgo;
        return(
            <div className="x_panel">
                <div className="x_content">
                    <Tabs onChange={this.callbackTabs}>
                        <TabPane tab="最近3天账单" key="1"></TabPane>
                        <TabPane tab="3天前账单" key="2"></TabPane>
                    </Tabs>
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
                                    <th className="column-title">余额</th>                                                                                                                   
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
                                                <td>{item.fundstypename}</td>
                                                <td>{item.userid}</td>
                                                <td>{item.id}</td>
                                                <td>{item.typename}</td>
                                                <td>{moment(item.sendtime).format(TIMEFORMAT)}</td>
                                                <td>{item.inout==1?'+':item.inout==2?'-':''}{toThousands(item.amount)}</td>
                                                <td>{item.balance==0?item.balance:toThousands(item.balance)}</td>
                                                <td>{item.remark}</td>                                                                                                                  
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

























