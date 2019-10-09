import React from 'react'
import axios from '../../../../utils/fetch'
import moment from 'moment'
import qs from 'qs'
import { message } from 'antd'
import { DOMAIN_VIP,TIMEFORMAT } from '../../../../conf'


export default class ModalMemo extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            ucmId:'',
            tableList:[]
        }
        this.requestTable = this.requestTable.bind(this)
    }

    componentDidMount(){
        this.setState({
            ucmId : this.props.ucmId
        },()=>this.requestTable())
        // this.requestTable(this.props.ucmId)
    }

    componentWillReceiveProps(nextProps){
        this.setState({
            ucmId : nextProps.ucmId
        },()=>this.requestTable())
        // this.requestTable(nextProps.ucmId)
    }

    requestTable(){
        const {ucmId} = this.state
        axios.post(DOMAIN_VIP+"/capitalMonitor/checkRemark",qs.stringify({
            id:ucmId
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    tableList:result.list
                })    
            }else{
                message.warning(result.msg)
            }
        })
    }

    render(){
        const { tableList } = this.state
        return(
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
                            tableList.map((item,index) => {
                                return (
                                    <tr key={index}>
                                        <td>{index+1}</td>
                                        <td>{item.operusername}</td>
                                        <td>{item.opertime?moment(item.opertime).format(TIMEFORMAT):''}</td>
                                        <td>{item.memo}</td>
                                    </tr>
                                )
                            })
                            :<tr className="no-record"><td colSpan="15">暂无数据</td></tr>
                        }
                    </tbody>
                </table>
            </div>
        )
    }
}

































