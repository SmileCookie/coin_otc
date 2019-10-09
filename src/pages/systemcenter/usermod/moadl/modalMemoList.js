import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT } from '../../../../conf'
import { Tabs,Pagination,message } from 'antd'
const TabPane = Tabs.TabPane


export default class ModalMemoList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            memoList:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            id:null
        }

        this.requestTableMemo = this.requestTableMemo.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
    }

    componentWillReceiveProps(nextProps){
        const { pageIndex,pageSize } = this.state
        if(nextProps.id != this.state.id){
            this.requestTableMemo(pageIndex,pageSize,nextProps.id)
            this.setState({
                id:nextProps.id
            })
        }
    }

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestTableMemo(page,pageSize,this.props.id))
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTableMemo(current,size,this.props.id)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }

    //备注信息列表
    requestTableMemo(currindex,currSize,currId){
        const { pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+'/userInfo/queryUserMemoById',qs.stringify({
            busiid:currId,
            pageIndex:currindex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            console.log(result)
            if(result.code == 0){
                this.setState({
                    memoList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    render(){
        const { pageIndex,pageSize,memoList,pageTotal } = this.state
        return(
            <div className="table-responsive">
                <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                    <thead>
                        <tr className="headings">
                            <th className="column-title">序号</th>
                            <th className="column-title">操作员名称</th>
                            <th className="column-title">备注时间</th>
                            <th className="column-title">备注</th>                   
                        </tr>
                    </thead>
                    <tbody>
                        {
                            memoList.length>0?
                            memoList.map((item,index)=>{
                                return (
                                    <tr key={index}>
                                        <td>{(pageIndex-1)*pageSize+index+1}</td>
                                        <td>{item.operusername}</td>
                                        <td>{moment(item.opertime).format(TIMEFORMAT)}</td>
                                        <td>{item.memo}</td>
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





















