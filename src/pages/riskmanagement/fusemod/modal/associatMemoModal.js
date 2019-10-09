import React, { Component} from 'react';
import { Table,Modal,message } from 'antd'
import moment from 'moment'
import { toThousands } from '../../../../utils'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,TIMEFORMAT,PAGEINDEX,PAGESIZE,DEFAULTVALUE, TIMEFORMAT_ss,PAGRSIZE_OPTIONS20,DEFAULT_OPTIONS } from '../../../../conf'
const {Column} = Table
const confirm = Modal.confirm

export default class AssociatMemoModal extends Component{
    constructor(props){
        super(props)
        this.state = {
            tableSource:[],
            pageIndex:PAGEINDEX,
            pageSize:10,
            pageTotal:DEFAULTVALUE,
            loading:false,
            userid:''
        }
    }
    componentDidMount(){
        this.setState({
            userid:this.props.userid
        },()=>this.requestTable())
        
    }
    componentWillReceiveProps(nextProps){
        this.setState({
            userid:nextProps.userid,
            pageIndex:PAGEINDEX,
            pageSize:10,
        },()=>this.requestTable())
    }
    onChangePageNum = (pageIndex,pageSize) => {
        console.log(111)
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        console.log(222)
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    requestTable = (currentIndex, currentSize) => {
        const {pageIndex,pageSize,userid} = this.state
        axios.post(DOMAIN_VIP+'/coinOwnFuse/queryCoinRelAttr',qs.stringify({
            userid,
            // pageIndex:currentIndex||pageIndex,
            // pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data;
                for(let i=0;i<tableSource.length;i++){
                    // tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].index = i + 1
                    tableSource[i].key = tableSource[i].id
                }
                this.setState({
                    tableSource:tableSource,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg);
            }
        })
    }
    render(){
        const { pageIndex,pageSize,pageTotal,tableSource} = this.state
        let types = ['--','ip','地址']
        return (
            
            <div className='col-md-12 col-sm-12 col-xs-12'>
                <div className="table-responsive table-rap-last">                    
                    <Table dataSource={tableSource}
                     pagination={{
                        showQuickJumper:true,
                        showSizeChanger:true,
                        showTotal:total=>`总共${total}条`,
                        size:'small',
                        pageTotal:pageTotal,
                        pageSize:pageSize,
                        current:pageIndex,
                        pageSizeOptions:DEFAULT_OPTIONS ,
                        defaultPageSize:10,
                        onShowSizeChange:this.onShowSizeChange,
                        onChange:this.onChangePageNum
                    }} 
                    bordered 
                    locale={{emptyText:'暂无数据'}} 
                >
                        <Column title='序号'  dataIndex='index'  key='index' />
                        <Column title='用户编号'  dataIndex='userid'  key='userid'  />
                        <Column title='用户名'  dataIndex='userName'  key='userName'  />
                        <Column title='类型'  dataIndex='type'  key='type' render={text => types[text]} />
                        <Column title='属性'  dataIndex='attr'  key='attr' />
                        {/* <Column title='创建时间'  dataIndex='createtime'  key='createtime' render={text => text ? moment(text).format(TIMEFORMAT_ss) : '--'} /> */}
                    </Table>
                </div>
            </div>
          
        )
    }
}