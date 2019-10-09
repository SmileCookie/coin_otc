import React from 'react';
import { Table } from 'antd'
const {Column} = Table

export default class ModalInsuranceFundsManageFT extends React.Component{
    constructor(props){
        super(props)
        this.state= {
            tableSource:[],
        }
    }
    componentDidMount(){
        // this.requestTable()
    }
    componentWillUnmount(){

    }
    componentWillReceiveProps(nextProps){
        
    }
    render() {
        const { tableSource } = this.state
        return (
            <div className='col-md-12 col-sm-12 col-xs-12'>
                <div className="table-responsive">
                    <Table tableSource={tableSource} bordered  scroll={{x:1500}} locale={{emptyText:'暂无数据'}}>
                        <Column title='序号' fixed dataIndex='index' render={(text) => (
                            <span>{text}</span>
                        )}/>
                        <Column title='期货市场' fixed dataIndex='' key=''/>
                        <Column title='用户编号' fixed dataIndex='' key=''/>
                        <Column title='成交编号'  dataIndex='' key=''/>
                        <Column title='持仓ID' dataIndex='' key=''/>
                        <Column title='交易类型' dataIndex='' key=''/>
                        <Column title='爆仓数量' dataIndex='' key=''/>
                        <Column title='价值(BTC)' dataIndex='' key=''/>
                        <Column title='持仓价格' dataIndex='' key=''/>
                        <Column title='标记价格' dataIndex='' key=''/>
                        <Column title='成交价格' dataIndex='' key=''/>
                        <Column title='爆仓价格' dataIndex='' key=''/>
                        <Column title='保证金' dataIndex='' key=''/>
                        <Column title='盈亏'  dataIndex='' key=''/>
                        <Column title='保险基金' dataIndex='' key=''/>
                        <Column title='爆仓时间' dataIndex='' key=''/>
                        <Column title='是否穿仓' dataIndex='' key=''/>
                    </Table>
                </div>
            </div>
        )
    }

}