import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE ,TIMEFORMAT} from '../../../../conf'
import { Pagination } from 'antd'


export default class ModalBlack extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            id:'',
            tableList:[]
        }
        this.requestTable = this.requestTable.bind(this)
    }
    componentDidMount(){
        this.setState({
            id : this.props.id
        },()=>this.requestTable())
    }
    componentWillReceiveProps(nextProps){
    //     if(nextProps.id){
    //         this.props = nextProps;
    //             this.requestTable()
    //   }
        this.setState({
            id : nextProps.id
        },()=>this.requestTable())
    }
    requestTable(){
        const {id} = this.state
        axios.post(DOMAIN_VIP+'/blacklist/queryMemo',qs.stringify({
            id
        })).then(res => {
            const result = res.data;
            console.log(result);
            if(result.code == 0){
                this.setState({
                    tableList:result.data
                })
            }   
        })
    }
    render(){
        const { tableList,pageTotal,pageIndex,pageSize } = this.state
        return(
            <div className="table-responsive">
                <table className="table table-striped jambo_table bulk_action table-linehei table-border">
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
                                        :<tr className="no-record"><td colSpan="4">暂无数据</td></tr>
                                    }
                    </tbody>
                </table>
            </div>
        )
    }
}






























