import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT } from '../../../../conf'
import { Pagination,Button } from 'antd'

export default  class ModalPlate extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            tableList:[],
            sendmodecode:'1',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0
        }

        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
    }

    componentDidMount(){
        const { pageIndex,pageSize } = this.state
        this.setState({
            sendmodecode:this.props.sendmodecode
        })
        this.requestTable(pageIndex,pageSize,this.props.sendmodecode)
    }

    componentWillReceiveProps(nextProps){
        const { pageIndex,pageSize } = this.state
        this.requestTable(pageIndex,pageSize,nextProps.sendmodecode)
        this.setState({
            sendmodecode:nextProps.sendmodecode
        })
    }

    requestTable(currIndex,currSize,currCode){
        const { pageIndex,pageSize,sendmodecode } = this.state
        axios.post(DOMAIN_VIP+"/msgTemplateRule/queryList",qs.stringify({
            sendmodecode:currCode||sendmodecode,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }
        })
    }

     //点击分页
     changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestTable(page,pageSize))
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
        const { pageIndex,pageSize,tableList,pageTotal } = this.state
        return (
            <div className="table-responsive">
                <table className="table table-striped jambo_table bulk_action table-linehei">
                    <thead>
                        <tr className="headings">
                            <th className="column-title">序号</th>
                            <th className="column-title">模版名称</th>
                            <th className="column-title">模版描述</th>
                            <th className="column-title">发送渠道</th>
                            <th className="column-title">发送区域</th>
                            <th className="column-title">模版状态</th>
                            <th className="column-title">创建人</th>     
                            <th className="column-title">创建时间</th>     
                            <th className="column-title">操作</th>     
                        </tr>
                    </thead>
                    <tbody>
                        {
                            tableList.length>0?
                            tableList.map((item,index) => {
                                return (
                                    <tr key={index}>
                                        <td>{(pageIndex-1)*pageSize+index+1}</td>
                                        <td>{item.templatename}</td>
                                        <td>{item.templatedesc}</td>
                                        <td>{item.sendmodecode==1?"短信":"邮件"}</td>
                                        <td>{item.sendareacode==1?"国内":item.sendareacode==2?"国际":"全球"}</td>
                                        <td>{item.templatestatus==1?"正常":"停用"}</td>
                                        <td>{item.creuser}</td>
                                        <td>{moment(item.credate).format(TIMEFORMAT)}</td>
                                        <td><Button type="more" onClick={() => this.props.chooseMould(item)}>选择</Button></td>
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




























