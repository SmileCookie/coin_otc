//部门管理>选择负责人带回
import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import {DOMAIN_VIP,PAGEINDEX,PAGESIZE} from '../../../../conf'
import { message } from 'antd'

export default class ModalPer extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            tableList:[],
            page:PAGEINDEX,
            limit:10000,
            username:'',
            userId:'',
            realName:'',
            qroleId:'0',
            qstatus:'',
            qlock:'',
            quserId:'',
            quserName:'',
            qrealName:'',
        }

        this.requestTable = this.requestTable.bind(this)
        this.chooseUser = this.chooseUser.bind(this)
    }

    componentDidMount(){
        this.requestTable()
    }

    //操作员列表信息
    requestTable(currIndex,currSize){
        const { page,limit,quserName,quserId,qrealName,qroleId,qstatus,qlock } = this.state
        axios.get(DOMAIN_VIP+'/sys/user/list',{
            params:{
                page:currIndex||page,
                limit:currSize||limit,
                userName:quserName,
                userId:quserId,
                realName:qrealName,
                roleId:qroleId,
                status:qstatus,
                lock:qlock
            }
        }).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.page.list,
                    pageTotal:result.page.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    chooseUser(item){
        this.props.chooseUserBtn(item.userId,item.username)
    }

    render(){
        const { tableList } = this.state
        return(
            <div className="table-responsive">
                <table className="table table-striped jambo_table bulk_action table-linehei">
                    <thead>
                        <tr className="headings">
                            <th className="column-title">序号</th>
                            <th className="column-title">操作员编号</th>
                            <th className="column-title">登录名</th>
                            {/* <th className="column-title">真实姓名</th>
                            <th className="column-title">角色</th>        */}
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
                                        <td>{item.userId}</td>
                                        <td>{item.username}</td>
                                        {/* <td>{item.realname}</td>
                                        <td>{item.roleNames}</td> */}
                                        <td><a href="javascript:void(0)" onClick={()=>this.chooseUser(item)}>选择带回</a></td>                                       
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





























