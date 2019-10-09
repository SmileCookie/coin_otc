import React from 'react';
import { Table,Modal,message } from 'antd'
// import moment from 'moment'
import { toThousands } from '../../../utils'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,TIMEFORMAT,PAGEINDEX,PAGESIZE,DEFAULTVALUE, TIMEFORMAT_ss ,PAGESIZE_50,PAGESIZE_20,PAGRSIZE_OPTIONS20} from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
const {Column} = Table
const confirm = Modal.confirm

const Big = require('big.js')
export default class PopWindows extends React.Component{
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
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE_20
            },
           
        }
    }
    
    
    
    render() {
        let tablelist = this.props.list
        const sum = this.props.item.conversion
        const {pagination} = this.state
        return (
            <div className="x_panel">                        
            <div className="x_content">
            <div className="table-responsive table-box">
                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                        <thead>
                            <tr className="headings">
                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                        资产折算：{toThousands(sum,true) } 
                                </th>
                            </tr>
                        </thead>
                    </table>
                    <Table
                        dataSource={tablelist}
                        bordered={true}
                        // onChange={this.handleChangeTable}
                        locale={{emptyText:'暂无数据'}}
                        pagination={pagination}
                        // scroll={pageSize != 10 ? { y: 500 } : {}}
                       >
                        <Column title='用户编号' dataIndex='userid' key='userId' />
                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypeName' />
                        <Column title='总金额' dataIndex='totalAmount' key='totalAmount'  className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                        <Column title='可用金额' dataIndex='balance' key='availableBalance'  className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                        <Column title='冻结金额' dataIndex='freez' key='amountFrozen'  className="moneyGreen" sorter="true" render={(text)=>toThousands(text,true)} />
                        {/* <Column title='操作' dataIndex='op' key='op' render={(text,record)=>{
                            return (<div>
                                <a className="mar10" href="javascript:void(0)" onClick={() => this.coinChargeModal(record,'doCharge')}>充{record.fundstypename}</a>
                                {limitBtn.indexOf('doCharge')>-1?<a className="mar10" href="javascript:void(0)" onClick={() => this.coinChargeModal(record,'doCharge')}>充{record.fundstypename}</a>:''}
                            {limitBtn.indexOf('doDeduction')>-1?<a className="mar10" href="javascript:void(0)" onClick={() => this.coinDeductModal(record,'doDeduction')}>扣{record.fundstypename}</a>:''}
                            {
                               limitBtn.indexOf('doFreez')>-1?(record.balance>0&&<a className="mar10" href="javascript:void(0)" onClick={()=>this.coinFreezeModal(record,'doFreez')}>冻结可用资金</a>):''
                             }
                            {
                               limitBtn.indexOf('unFreez')>-1?(record.freez>0&&<a className="mar10" href="javascript:void(0)" onClick={()=>this.coinUnfreezeModal(record,'unFreez')}>解冻冻结资金</a>):''
                             }
                            </div>)
                        }} /> */}
                    </Table>
                </div>
            </div>
        </div>
        )
    }
}

