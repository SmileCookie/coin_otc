import React, { Component} from 'react';
import { Table,Modal,message } from 'antd'
import moment from 'moment'
import { toThousands } from '../../../../utils'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,TIMEFORMAT,PAGEINDEX,PAGESIZE,DEFAULTVALUE, TIMEFORMAT_ss,PAGRSIZE_OPTIONS20,DEFAULT_OPTIONS } from '../../../../conf'
const {Column} = Table
const confirm = Modal.confirm

export default class ModalOneKeySettle extends Component{
    constructor(props){
        super(props)
        this.state = {
            tableSource:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            loading:false
        }
    }
    componentDidMount(){

    }
    componentWillReceiveProps(nextProps){
        this.setState(()=>({
            loading:true
        }))
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
    // requestTable = (currentIndex, currentSize) => {
    //     const {pageIndex,pageSize,pageTotal} = this.state
    //     axios.post(DOMAIN_VIP+'',qs.stringify({
    //         pageIndex:currentIndex||pageIndex,
    //         pageSize:currentSize||pageSize
    //     })).then(res => {
    //         const result = res.data;
    //         if(result.code == 0){
    //             let tableSource = result.data.list;
    //             for(let i=0;i<tableSource.length;i++){
    //                 tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
    //                 tableSource[i].key = tableSource[i].id
    //             }
    //             this.setState({
    //                 tableSource:tableSource,
    //                 pageTotal:result.data.totalCount
    //             })
    //         }else{
    //             message.warning(result.msg);
    //         }
    //     })
    // }
    render(){
        const { pageIndex,pageSize,pageTotal} = this.state
        const {list: tableSource,errorSum,successSum} = this.props.item
        let balanceFlag =[<span style={{color:'#e35f4d'}}>有问题</span>,'无问题']
        return (
            
            <div className='col-md-12 col-sm-12 col-xs-12'>
                <div className="table-responsive table-rap-last">
                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                        <thead>
                            <tr className="headings">
                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                    有问题：<span style={{color:'#e35f4d'}}>{errorSum}</span>，无问题：{successSum}，可以结算：{successSum}
                                </th>
                            </tr>
                        </thead>
                    </table>
                    
                    <Table dataSource={tableSource}
                     pagination={{
                        showQuickJumper:true,
                        showSizeChanger:true,
                        showTotal:total=>`总共${total}条`,
                        size:'small',
                        // pageTotal:pageTotal,
                        // pageSize:pageSize,
                        // current:pageIndex,
                        pageSizeOptions:DEFAULT_OPTIONS ,
                        // defaultPageSize:PAGESIZE,
                        // onShowSizeChange:this.onShowSizeChange,
                        // onChange:this.onChangePageNum
                    }} 
                    bordered 
                    locale={{emptyText:'暂无数据'}} 
                    scroll={{x:2390}}
                >
                        <Column title='序号'  dataIndex='index'  key='index' />
                        <Column title='资金类型'  dataIndex='fundtypeName'  key='fundtypeName'  />
                        <Column title='账户类型'  dataIndex='name'  key='name' />
                        <Column className='moneyGreen' title='当前余额(充值账户)'  dataIndex='amount'  key='amount' render={text=>toThousands(text,true)}  />
                        <Column className='moneyGreen' title='充值余额(充值账户)'  dataIndex='amountRecharge'  key='amountRecharge' render={text=>toThousands(text,true)}  />
                        <Column className='moneyGreen' title='上次余额(充值账户)'  dataIndex='amountLast'  key='amountLast' render={text=>toThousands(text,true)} />
                        <Column className='moneyGreen' title='当前钱包余额(提现账户)'  dataIndex='balance'  key='balance' render={text=>toThousands(text,true)}  />
                        <Column className='moneyGreen' title='当前累计余额(提现账户)'  dataIndex='curtotalamount'  key='curtotalamount' render={text=>toThousands(text,true)}   />
                        <Column className='moneyGreen' title='提现成功金额(提现账户)'  dataIndex='balanceDetails'  key='balanceDetails 'render={text=>toThousands(text,true)} />
                        <Column className='moneyGreen' title='上次钱包金额(提现账户)'  dataIndex='balanceWallet'  key='balanceWallet' render={text=>toThousands(text,true)}   />
                        <Column className='moneyGreen' title='上次累计金额(提现账户)'  dataIndex='pertotalamount'  key='pertotalamount' render={text=>toThousands(text,true)}  />
                        <Column title='结算提醒'  dataIndex='balanceFlag'  key='balanceFlag' render={text=>balanceFlag[text]} />
                        {/* <Column title='结算结果'  dataIndex='action'  key='action' /> */}
                        <Column width='25%' title='备注'  dataIndex='newmemo'  key='newmemo' />
                    </Table>
                </div>
            </div>
          
        )
    }
}