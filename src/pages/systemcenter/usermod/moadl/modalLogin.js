import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,PAGRSIZE_OPTIONS20 } from '../../../../conf'
import { Pagination,message } from 'antd'
import { tableScroll } from '../../../../utils'

export default class ModalLogin extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            tableList:[],
            pageTotal:0,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            id:'',
            tableScroll:{
                tableId:'MODLLOGN',
                x_panelId:'MODLLOGNXXX',
                defaultHeight:500,
            }
        }

        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
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
        if(nextProps.isInquire){
            this.setState({
                pageIndex:PAGEINDEX,
                pageSize:PAGESIZE
            })
            this.requestTable(PAGEINDEX,PAGESIZE,nextProps.id)
            this.props.queryBtnclick?this.props.queryBtnclick(false):"";
        }
    }   


    requestTable(currIndex,currSize,currId){
        const { userId,userName,loginIp,city,loginMode,isInquire } = this.props
        const { pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+'/loginInfo/queryList',qs.stringify({
            userId:currId||userId,
            userName,
            loginIp,
            city,
            loginMode,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.code)
            }
            
        })
    }

    render(){
        const { tableList,pageTotal,pageIndex,pageSize } = this.state
        return(
            <div className="x_panel">
               
                <div className="x_content">
                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">用户编号</th>
                                    <th className="column-title">登录时间</th>
                                    <th className="column-title">IP</th>
                                    <th className="column-title">城市</th>
                                    <th className="column-title">登录方式</th>                                              
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length>0?
                                    tableList.map((item,index) => {
                                        return (
                                            <tr key={index}>
                                                <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                <td>{item.userId}</td>
                                                <td>{moment(item.date).format(TIMEFORMAT)}</td>
                                                <td><a href={`http://www.ip138.com/ips138.asp?ip=${item.ip}&action=2`} target="_blank">{item.ip}</a></td>
                                                <td>{item.city}</td>
                                                <td>{item.describe}</td>
                                            </tr>
                                        )
                                    }) 
                                    :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
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
                                        onChange={this.changPageNum}
                                        showTotal={total => `总共 ${total} 条`}
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


























