import React from 'react';
import { Table,Modal,message } from 'antd'
// import moment from 'moment'
import { toThousands } from '../../../utils'
import { DOMAIN_VIP,TIMEFORMAT,PAGEINDEX,PAGESIZE,DEFAULTVALUE, TIMEFORMAT_ss ,PAGESIZE_50,PAGESIZE_20,PAGRSIZE_OPTIONS20} from '../../../conf'
const {Column} = Table
const confirm = Modal.confirm

//const Big = require('big.js')
export default class PlatformModal extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
        }
      
    }    
    render() {
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
                        pagination={this.state.pagination}
                        // scroll={pageSize != 10 ? { y: 500 } : {}}
                       >
                        <Column title='序号' dataIndex='index' key='index' />
                        <Column title='保值平台' dataIndex='entrustPlatform' key='entrustPlatform' />
                        <Column title='成交数量' dataIndex='executedAmount' key='executedAmount'  />
                        <Column title='交易类型' dataIndex='entrustType' key='entrustType' render={text=>entrustType[text]}  />
                    </Table>
                </div>
            </div>
            </div> 
        )
    }
}

