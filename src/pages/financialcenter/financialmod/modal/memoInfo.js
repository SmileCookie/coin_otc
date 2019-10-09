import React from 'react'
import moment from 'moment'
import { TIMEFORMAT } from '../../../../conf'

const MemoInfo = ({tableList}) => {
    return (
        <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="table-responsive">
                    <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                        <thead>
                            <tr className="headings">
                                <th className="column-title">序号</th>
                                <th className="column-title">操作员名称</th>
                                <th className="column-title">备注时间</th>
                                <th className="column-title">备注</th>
                            </tr>
                        </thead>
                        <tbody>
                            {
                                tableList.length>0?
                                tableList.map((item,index)=>{
                                    return (
                                        <tr key={index}>
                                            <td>{index+1}</td>
                                            <td>{item.operusername}</td>
                                            <td>{moment(item.opertime).format(TIMEFORMAT)}</td>
                                            <td>{item.memo}</td>
                                        </tr>
                                    )
                                }):<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                            }
                        </tbody>
                    </table>
                </div>
            </div>
    )
}

export default  MemoInfo ;





























