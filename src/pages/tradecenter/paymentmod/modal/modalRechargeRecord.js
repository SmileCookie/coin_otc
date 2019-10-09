
import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,SELECTWIDTH ,PAGRSIZE_OPTIONS20, TIMEFORMAT_ss} from '../../../../conf'
import { Input,Modal,DatePicker,Select,Button,Pagination,message } from 'antd'
import { toThousands,tableScroll } from '../../../../utils'

export default class ModalRechargeRecord extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],
            pageTotal:0,
            currId:'',
            height:0,
            tableScroll:{
                tableId:'ModalRechargeRecord',
                x_panelId:'ModalRechargeRecordxx',
                defaultHeight:500,
            },
            amountsum:''
        }
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    // componentDidMount(){
    //     const { pageIndex,pageSize } = this.state
    //     this.setState({
    //         currId:this.props.id
    //     },()=>this.requestTable())
    // }
    componentDidMount(){
        const { pageIndex,pageSize } = this.state
        this.setState({
            currId:this.props.id
        },()=>this.requestTable())
        
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
            this.setState({
                pageIndex:PAGEINDEX,
                currId:nextProps.id
            },()=>this.requestTable())
            this.props.queryClickBtn&&this.props.queryClickBtn(false)
        }
    }

    //table 列表
    requestTable(currIndex,currSize){
        let { fundstype,toaddr,userid,username,status,rechargeMin,rechargeMax,beginTime,endTime,configStartTime,configEndTime,billTime} = this.props
        const { pageIndex,pageSize,currId } = this.state
        if(!Array.isArray(billTime)) billTime = []
        let billStartTime =  billTime.length ? moment(billTime[0]).format(TIMEFORMAT_ss) : '',
        billEndTime  =  billTime.length ? moment(billTime[1]).format(TIMEFORMAT_ss) : '';
        axios.post(DOMAIN_VIP+'/recharge/query',qs.stringify({
            fundstype,toaddr,username,status,rechargeMin,
            rechargeMax,beginTime,endTime,configStartTime,configEndTime,
            billStartTime,billEndTime,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize,
            userid:currId||userid
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
        axios.post(DOMAIN_VIP+'/recharge/sum',qs.stringify({
            fundstype,toaddr,username,status,rechargeMin,
            rechargeMax,beginTime,endTime,configStartTime,configEndTime,
            billStartTime,billEndTime,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize,
            userid:currId||userid
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    amountsum:result.data[0]&&result.data[0].amountsum
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
        const { pageTotal,pageSize,pageIndex,tableList,amountsum} = this.state
        return(
            <div className="x_panel">                 
                <div className="x_content">
                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                            {this.props.display?"":<thead>
                                <tr className="headings">
                                    <th style={{textAlign:'left'}} className="column-title" colSpan='11'>充值金额：{toThousands(amountsum,true)}</th>
                                </tr>
                            </thead>}
                            <thead>
                                <tr className="headings">
                                    <th className="column-title min_69px">序号</th>
                                    <th className="column-title">资金类型</th>
                                    <th className="column-title">充值编号</th>
                                    <th className="column-title">用户编号</th>
                                    <th className="column-title">充值金额</th>
                                    <th className="column-title min_68px">状态</th>
                                    <th className="column-title">充值时间</th>
                                    <th className="column-title">确认时间</th>
                                    <th className="column-title">记账时间</th>
                                    <th className="column-title">充值地址</th> 
                                    <th className="column-title">备注</th>                           
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length>0?tableList.map((item,index) => {
                                        return (
                                            <tr key={index}>
                                                <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                <td>{item.fundstypeName}</td>
                                                <td>{item.detailsid}</td>
                                                <td>{item.userid}</td>
                                                <td>{toThousands(item.amount,true)}</td>
                                                <td>{item.showStatu}</td>
                                                <td>{item.sendtime?moment(item.sendtime).format(TIMEFORMAT):'--'}</td>
                                                <td>{item.configtime?moment(item.configtime).format(TIMEFORMAT):'--'}</td>
                                                <td>{item. billtime?moment(item. billtime).format(TIMEFORMAT):'--'}</td>
                                                <td><a href={item.url+item.addhash}>{item.toaddr}</a></td>
                                                <td>确认次数：{item.confirmtimes}</td>
                                            </tr>
                                        )
                                    }):<tr className="no-record"><td colSpan="11">暂无数据</td></tr>
                                }
                            </tbody>
                        </table>
                    </div>
                    <div className="pagation-box">
                        {pageTotal>0&&
                            <Pagination 
                                size="small" 
                                current={pageIndex}
                                total={pageTotal}  
                                showTotal={total => `总共 ${total} 条`}
                                onChange={this.changPageNum}
                                onShowSizeChange={this.onShowSizeChange}
                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                defaultPageSize={PAGESIZE}
                                showSizeChanger 
                                showQuickJumper
                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                defaultPageSize={PAGESIZE}
                                 />
                        }
                        </div>
                </div>
            </div>
        )
        
    }   

}



















