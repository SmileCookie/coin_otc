import React from 'react';
import { Table,Modal,message } from 'antd'
import moment from 'moment'
import { toThousands } from '../../../utils'
import { DOMAIN_VIP,TIMEFORMAT,PAGEINDEX,PAGESIZE,DEFAULTVALUE, TIMEFORMAT_ss ,PAGESIZE_50,PAGESIZE_20,PAGRSIZE_OPTIONS20} from '../../../conf'
const {Column} = Table
const confirm = Modal.confirm

const Big = require('big.js')
export default class DetailModal extends React.Component{
    constructor(props){
        super(props)
      
    }    
    render() {
        Big.RM = 0;
        let entrustType = ['卖出', '买入']
        return (
            <div className="x_panel">     
             <div className="x_content">                     
             <div className="table-responsive">
                    <Table
                        dataSource={this.props.tableSource}
                        bordered={true}
                        // onChange={this.handleChangeTable}
                        locale={{emptyText:'暂无数据'}}
                        pagination={this.props.pagination}
                        // scroll={pageSize != 10 ? { y: 500 } : {}}
                       >
                        <Column title='序号' dataIndex='index' key='index' />
                        <Column title='成交编号' dataIndex='transrecordid' key='transrecordid' />
                        <Column title='用户编号' dataIndex='entrustuserid' key='entrustuserid' />
                        {/* <Column title='买家用户编号' dataIndex='' key=''   />
                        <Column title='买单委托编号' dataIndex='' key=''   />
                        <Column title='卖家用户编号' dataIndex='' key=''   />
                        <Column title='卖单委托编号' dataIndex='' key=''   /> */}
                        <Column title='成交单价'  dataIndex='entrustprice' key='entrustprice'   sorter={true}/>
                        <Column title='成交数量' dataIndex='entrustnum' key='entrustnum'   sorter={true}/>
                        <Column title='成交总金额' dataIndex='entrustid' key='entrustid'   sorter={true} render={(text,record)=>record.entrustprice*record.entrustnum}/>
                        <Column title='委托类型' dataIndex='entrusttype' key='entrusttype' render={text=>entrustType[text]}  />
                        {/* <Column title='状态' dataIndex='' key=''   /> */}
                        <Column title='成交时间' dataIndex='addtime' key='addtime' render={text=>text?moment(text).format(TIMEFORMAT_ss):'--'} />
                    </Table>
                </div>
            </div>
            </div> 
        )
    }
}

