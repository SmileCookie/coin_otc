import React from 'react'
import { toThousands } from '../../../../utils'

export default class ModalPlayCoin extends React.Component{

    constructor(props){
        super(props)
        this.state = {

        }
    }

    render(){
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="table-responsive">
                    <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
                        <thead>
                            <tr className="headings">
                                <th className="column-title">序号</th>
                                <th className="column-title">资金类型</th>
                                <th className="column-title">选中笔数</th>
                                <th className="column-title">提现金额</th>
                                <th className="column-title">实际提现金额</th>
                                <th className="column-title">提现网络费</th>    
                                <th className="column-title">热提钱包余额</th>                                           
                                <th className="column-title">资金是否充足</th>                                           
                                <th className="column-title">用户资金状态</th>              
                            </tr>
                        </thead>
                        <tbody>
                            {
                                tableList.map((item,index) => {
                                    return (
                                        <tr key={index}>
                                            <td>{index+1}</td>
                                            <td>{item.fundstypename}</td>
                                            <td>{item.count}</td>
                                            <td>{toThousands(item.amount)}</td>
                                            <td>{toThousands(item.afterAmount)}</td>
                                            <td>{item.fees}</td>
                                        </tr>
                                    )
                                })
                            }
                        </tbody>
                    </table>
                </div>
            </div>
        )
    }

}





















































