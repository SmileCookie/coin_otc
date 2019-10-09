import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../../conf'
import { Input,Modal,DatePicker,Select,Button,Pagination,message } from 'antd'
import { toThousands,tableScroll } from '../../../../utils'
const { RangePicker } = DatePicker;
const Option = Select.Option;


export default class ModalRechargeAddress extends React.Component{

    constructor(props){
        super(props)
        this.state={
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],
            pageTotal:0,
            height:0,
            tableScroll:{
                tableId:'MDALRCRGARSS',
                x_panelId:'MDALRCRGARSSXX',
                defaultHeight:500,
            }
        }
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    
    componentDidMount(){
        const { pageIndex,pageSize } = this.state
        this.requestTable(pageIndex,pageSize,this.props.id)
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
            this.requestTable(PAGEINDEX,pageSize,nextProps.id)
            this.props.queryClickBtn?this.props.queryClickBtn(false):'';
        }
    }

    //table 列表
    requestTable(currIndex,currSize,currId){
        const { fundsType,address,userId,userName,type } = this.props
        const { pageIndex,pageSize } = this.state
        let url = this.props.modal?"/rechargeAddress/queryAll":"/rechargeAddress/query"
        axios.post(DOMAIN_VIP+url,qs.stringify({
            fundsType,address,userName,type,
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
        this.requestTable(page,pageSize,this.props.id)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size,this.props.id)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }

    render(){
        const { pageIndex,pageSize,tableList,pageTotal } = this.state
        return (
            <div className="x_panel">     
                <div className="x_content">
                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">资金类型</th>
                                    <th className="column-title">地址编号</th>
                                    <th className="column-title">用户编号</th>
                                    <th className="column-title min_214px">充值地址</th>
                                    <th className="column-title">创建时间</th>
                                    <th className="column-title">充值次数</th>
                                    <th className="column-title">地址状态</th>
                                    <th className="column-title">操作</th>                                                                    
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length>0?tableList.map((item,index) => {
                                        return (
                                            <tr key={index}>
                                                <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                <td>{item.fundtypename}</td>
                                                <td>{item.keyid}</td>
                                                <td>{item.userid}</td>
                                                <td>{item.keypre}</td>
                                                <td>{moment(item.createtime).format(TIMEFORMAT)}</td>
                                                <td>{item.usedtimes}</td>
                                                <td>{item.typeName}</td>
                                                <td></td>
                                            </tr>
                                        )
                                    }):<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
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



























