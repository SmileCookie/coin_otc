import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../../../conf'
import { Pagination } from 'antd'

export default  class ModalMatch extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            tableList:[],
            pageTotal:0,
            pageSize:PAGESIZE,
            pageIndex:PAGEINDEX
        }

        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
    }

    componentDidMount(){
        this.setState({
            id:this.props.id,
            pageSize:PAGESIZE,
            pageIndex:PAGEINDEX
        },()=>this.requestTable(this.props.id))
        
    }

    componentWillReceiveProps(nextProps){
        this.setState({
            id:nextProps.id,
            pageSize:PAGESIZE,
            pageIndex:PAGEINDEX
        },()=>this.requestTable(nextProps.id))
        
    }

    requestTable(currId){
        const { pageIndex,pageSize,id } = this.state
        axios.post(DOMAIN_VIP+"/msgTemplateRule/matchPreviewInfo",{
            id:currId||id,
            pageIndex,
            pageSize
        }).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.page.list,
                    pageTotal:result.page.totalCount
                })
            }
        })
    }

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestTable())
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        
        this.setState({
            pageIndex:current,
            pageSize:size
        },()=>this.requestTable())
    }

    render(){
        const { pageTotal,pageIndex,pageSize,tableList } = this.state
        return (
            <div className="table-responsive">
                <table className="table table-striped jambo_table bulk_action table-linehei">
                    <thead>
                        <tr className="headings">
                            <th className="column-title">序号</th>
                            <th className="column-title">用户编号</th>
                            <th className="column-title">{this.props.sendmodecode ==1?"手机":"邮箱"}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            tableList.length>0?
                            tableList.map((item,index) => {
                                return (
                                    <tr key={index}>
                                        <td>{(pageIndex-1)*pageSize+index+1}</td>
                                        <td>{item.id}</td>
                                        <td>{item.userContact.safeMobile}</td>
                                    </tr>
                                )
                            })
                            :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                        }
                    </tbody>
                </table>
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





























































