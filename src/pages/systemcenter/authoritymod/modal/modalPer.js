import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import {DOMAIN_VIP} from '../../../../conf'
import { message } from 'antd'

export default class ModalPer extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            tableList:[]
        }

        this.requestTable = this.requestTable.bind(this)
        this.chooseDept = this.chooseDept.bind(this)
    }

    componentDidMount(){
        this.requestTable()
    }

    requestTable(){
        axios.post(DOMAIN_VIP+"/sys/sysDept/queryListForSelectTab").then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    tableList:result.data
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    chooseDept(item){
        this.props.choosePerBtn(item.deptName,item.deptId)
    }

    render(){
        const { tableList } = this.state
        return(
            <div className="table-responsive">
                <table className="table table-striped jambo_table bulk_action table-linehei">
                    <thead>
                        <tr className="headings">
                            <th className="column-title">序号</th>
                            <th className="column-title">部门编号</th>
                            <th className="column-title">部门名称</th>
                            <th className="column-title">部门负责人</th>
                            <th className="column-title">上级部门名称</th>    
                            <th className="column-title">操作</th>                                                                                                                 
                        </tr>
                    </thead>
                    <tbody>
                        {
                            tableList.length>0?
                            tableList.map((item,index) => {
                                return(
                                    <tr key={index}>
                                        <td>{index+1}</td>
                                        <td>{item.deptNo}</td>
                                        <td>{item.deptName}</td>
                                        <td>{item.deptLeaderName}</td>
                                        <td>{item.parentDeptName}</td>
                                        <td><a href="javascript:void(0)" onClick={()=>this.chooseDept(item)}>选择带回</a></td>                                       
                                    </tr>
                                )
                            })
                            :<tr className="no-record"><td colSpan="13">暂无数据</td></tr>
                            
                        }
                    </tbody>
                </table>
            </div>
        )
    }

}





























