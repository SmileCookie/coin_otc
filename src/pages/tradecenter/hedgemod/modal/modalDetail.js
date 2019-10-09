import React from 'react'
import moment from 'moment'
import 'moment/locale/zh-cn';
import {TIMEFORMAT } from '../../../../conf/index';
moment.locale('zh-cn');

export default class ModalDetail extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            item:[],
            index:'',
        }
    }
    componentDidMount(){
        this.setState({
            item:this.props.item,
            index:this.props.index
        })
    }
    componentWillReceiveProps(nextProps){
        console.log(nextProps)
            this.setState({
                item:nextProps.item,
                index:nextProps.index
            })
    }
    render(){
        const{item,index} = this.state
        return(
        <div className="x_panel">
            <div className="x_content">
                <div className="table-responsive">
                <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                        <thead>
                            <tr className="headings">
                                <th className="column-title">序号</th>
                                <th className="column-title">原订单ID</th>
                                <th className="column-title">原订单价格</th>
                                <th className="column-title">原订单下单时间</th>
                                <th className="column-title">亏损金额</th>
                                <th className="column-title">平仓操作人</th>                  
                            </tr>
                        </thead>
                        <tbody>
                    
                        <tr>
                        <td>{index}</td>
                        <td>{item.originId}</td>
                        <td>{item.originPrice}</td>
                        <td>{moment(item.originAddTime).format(TIMEFORMAT)}</td>
                        <td>{item.lossAmount}</td>
                        <td>{item.operator}</td>
                    </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
            )
    }
}