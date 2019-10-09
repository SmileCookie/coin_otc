import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,PAGRSIZE_OPTIONS20,TIMEFORMAT_ss } from '../../../conf'
import { Button,Table,message, } from 'antd'
const { Column } = Table

export default class ScheduledLog extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            tableSource:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            logId:'',
            jobId:'',
            rejobId:'',
            beanName:'',
            methodName:'',
            params:'',
            status:'',
            times:'',
            createTime:'',
            pagination:{
                showSizeChanger:true,//是否可以改变 pageSize
                showQuickJumper:true,//是否可以快速跳转至某页
                showTotal:total=>`总共 ${total} 条`,
                size:'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            }

        }

        this.showHide = this.showHide.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.show_click = this.show_click.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
    }

    componentDidMount(){
        this.requestTable()
    }
    requestTable(currentIndex,currentSize){
        const {pageIndex,pageSize,pagination,rejobId} = this.state
        let self = this
        axios.get(DOMAIN_VIP+'/scheduleLog/list', {params:{
            page:currentIndex||pageIndex,
            limit:currentSize||pageSize,
            jobId:rejobId
        }}).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.page.list
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.page.currPage-1)*result.page.pageSize+i+1;
                    tableSource[i].key = tableSource[i].logId;
                }
                pagination.total = result.page.totalCount;
                // pagination.onChange = self.changPageNum
                // pagination.onShowSizeChange = self.onShowSizeChange
                this.setState({
                    tableSource:tableSource,
                    pagination
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //点击收起
    showHide(){
        const { showHide } = this.state
        this.setState({
            showHide: !showHide
        })
    }
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestTable(page,pageSize))
    }
    //分页pageSize改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }
    // 查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
        
    }
    //修改状态
    show_click(index) {
        this.props.showHideClick(index);
    }
    //输入时 input 设置到 state
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'check' ? target.check : target.value;
        const name = target.name
        this.setState({
            [name]: value
        })
    }
    render(){
        const { showHide,rejobId,tableSource,pagination } = this.state       
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>系统管理>日志列表
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-3 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">任务ID：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="rejobId" value={rejobId} onChange={this.handleInputChange} />
                                            {/* <b className="icon-fuzzy">%</b> */}
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquireBtn} >查询</Button>
                                        {/* <Button type="primary" >重置</Button> */}
                                        <Button type="more" onClick={()=>this.show_click(0)}>返回</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive table-checkbox">
                                    <Table dataSource={tableSource} locale={{emptyText:'暂无数据'}} bordered pagination={pagination}>
                                        <Column title='序号' dataIndex='index' key='index' render={(text)=>(
                                            <span>{text}</span>
                                        )}/>
                                        <Column title='日志ID' dataIndex='logId' key='logId' />
                                        <Column title='任务ID' dataIndex='jobId' key='jobId' />
                                        <Column title='bean名称' dataIndex='beanName' key='beanName' />
                                        <Column title='方法名称' dataIndex='methodName' key='methodName' />
                                        <Column title='参数' dataIndex='params' key='params' />
                                        <Column title='状态' dataIndex='status' render={(text,record) => (
                                            (() => {
                                                return text == 0 ? <span>正常</span>:<span>暂停</span>
                                            })()
                                        )} />
                                        <Column title='耗时(单位：毫秒)' dataIndex='times' key='times' />
                                        <Column title='执行时间' dataIndex='createTime' key='createTime' render={(text)=>text?moment(text).format(TIMEFORMAT_ss):'--'} />
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}



