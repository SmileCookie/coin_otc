import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf' 
import { Table } from 'antd'
const { Column, ColumnGroup } = Table;

export default class ModalTable extends React.Component{
    
    constructor(props){
        super(props)
        this.state = {
            selectedRowKeys:[],
            selectedRows:[],
            tableSource:[],
        }

        this.onChange = this.onChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
    }

    componentDidMount(){
        this.requestTable()
    }
    componentWillReceiveProps(){
        this.requestTable()
    }

    requestTable(){
        axios.get(DOMAIN_VIP+"/sys/role/select").then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.list;
                for(let i=0;i < tableSource.length;i++){
                    tableSource[i].index = i+1;
                    tableSource[i].key = tableSource[i].roleId;
                }
                this.setState({
                    tableSource,
                    selectedRowKeys:this.props.roleIdList
                })
            }
        })
    }

    onChange(selectedRowKeys, selectedRows){
        this.setState({
            selectedRowKeys,
            selectedRows
        })
        this.props.onSelectRoles(selectedRowKeys,selectedRows)
    }

    render(){
        const { selectedRowKeys,selectedRows,tableSource } = this.state
        const rowSelection = {
            selectedRowKeys,
            selectedRows,
            onChange: this.onChange
        }; 
        return(
            <div className="table-responsive">
                <Table dataSource={tableSource} rowSelection={rowSelection} bordered pagination={false} locale={{emptyText:'暂无数据'}}>
                    <Column title="序号" dataIndex="index" key="index" />  
                    <Column title="角色编号" dataIndex="roleId" key="roleId" />  
                    <Column title="角色名称" dataIndex="roleName" key="roleName" />  
                    <Column title="角色描述" dataIndex="remark" key="remark" />                             
                </Table>
            </div>
        )
    }
}






















