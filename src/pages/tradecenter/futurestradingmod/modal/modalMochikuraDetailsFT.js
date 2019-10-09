import React from 'react';
import { Table,message } from 'antd'
import moment from 'moment'
import { toThousands } from '../../../../utils'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,TIMEFORMAT,PAGEINDEX,PAGESIZE,DEFAULTVALUE } from '../../../../conf'
const {Column} = Table


export default class ModalMochikuraDetailsFT extends React.Component{
    constructor(props){
        super(props)
        this.state= {
            tableSource:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                // total:0,
                // pageSizeOptions:PAGRSIZE_OPTIONS20,
                // defaultPageSize:PAGESIZE_20
            },
            userid:''
        }
    }
    componentDidMount(){
        const {userid} = this.props
        this.setState({
            userid
        },()=>{
            this.requestTable()
        })
    }
    componentWillUnmount(){

    }
    componentWillReceiveProps(nextProps){
        const {userid} = nextProps
        this.setState({
            userid
        },()=>{
            this.requestTable()
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const {pageIndex,pageSize,pagination,userid} = this.state
        axios.post(DOMAIN_VIP+'/positionDetails/transactionList',qs.stringify({
            userid,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.data.totalCount;
                pagination.onChange = this.onChangePageNum;
                pagination.onShowSizeChange = this.onShowSizeChange
                this.setState({
                    tableSource:tableSource,
                    pagination,
                })
            }else{
                message.warning(result.msg);
            }
        })
    }
    onChangePageNum = (pageIndex,pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    render() {
        const { tableSource,pagination } = this.state
        return (
            <div className='col-md-12 col-sm-12 col-xs-12'>
                <div className="table-responsive">
                    <Table dataSource={tableSource} pagination={pagination} bordered locale={{emptyText:'暂无数据'}}>
                        <Column title='序号' dataIndex='index' render={(text) => (
                            <span>{text}</span>
                        )}/>
                        <Column title='期货市场' dataIndex='futuresid' key='futuresid'/>
                        <Column title='成交编号' dataIndex='id' key='id'/>
                        <Column title='成交价格' dataIndex='filledprice' key='filledprice' render={(text)=>(
                            <span>{toThousands(text)}</span>
                        )}/>
                        <Column title='成交数量' dataIndex='filledamount' key='filledamount'/>
                        {/* <Column title='委托数量' dataIndex='' key=''/> */}
                        <Column title='成交价值' dataIndex='filledvalue' key='filledvalue'/>
                        <Column title='做多方用户ID' dataIndex='longuserid' key='longuserid'/>
                        <Column title='做空方用户ID' dataIndex='shortuserid' key='shortuserid'/>
                        <Column title='成交类型' dataIndex='side' key='side' render={(text)=>(
                            <span>{text==1?'做多':'做空'}</span>
                        )}/>
                        <Column title='成交时间' dataIndex='createtime' key='createtime' render={(text)=>(
                            <span>{text?moment(text).format(TIMEFORMAT):'--'}</span>
                        )}/>
                        <Column title='处理状态' dataIndex='status' key='status' render={(text)=>{
                            switch(text){
                                case 0:
                                    return '初始'
                                    break;
                                case 1:
                                    return '处理中'
                                    break;
                                case 2:
                                    return '处理完成'
                                    break;
                                default:
                                    return '--'
                                    break;
                            }
                        }}/>
                        {/* <Column title='预收平仓佣金' dataIndex='' key=''/> */}
                        {/* <Column title='未实现盈亏' fixed='right' dataIndex='' key=''/> */}
                        {/* <Column title='已实现盈亏' fixed='right' dataIndex='' key=''/> */}
                        {/* <Column title='委托时间' fixed='right' dataIndex='' key=''/> */}
                        {/* <Column title='详情' fixed='right' dataIndex='' key='' render={(text,record)=>(
                            <a href='javascript:void(0);' onClick={()=>this.checkDetail(record)}>成交记录</a>
                        )}/> */}
                    </Table>
                </div>
            </div>
        )
    }

}