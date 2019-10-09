//部门Tree弹框
import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import ModalDTree from './modalDTree'
import ModalDepModify from './modalDepModify'

export default class ModalDepTree extends React.Component{
    constructor(props){
        super(props)
        this.state={
            deptName:'',
            deptNo:'',
            deptLeaderName:'',
            parentDeptName:'',
            deptId:'',
            deptLeaderId:'',
            parentId:'',
            parentDeptName:'',
            treeData:[]
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.setParentId = this.setParentId.bind(this)
        this.requstTable = this.requstTable.bind(this)
        this.setdeptId = this.setdeptId.bind(this)
    }
    componentDidMount(){
        this.setState({
            deptName:'',
            deptNo:'',
            parentDeptName:'',
            deptLeaderName:'',
        })
        this.requstTable()
    }
    componentWillReceiveProps(){
        this.setState({
            deptName:'',
            deptNo:'',
            parentDeptName:'',
            deptLeaderName:'',
        })
        this.requstTable()
    }
    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }
    //设置 parentId
    setParentId(id,name,checkdeptNo,checkdeptId,checkdeptLeaderName,checkparentDeptName){
        this.setState({
            deptNo:id,
            deptName:name,
            deptLeaderName:checkdeptLeaderName,
            parentDeptName:checkparentDeptName
        },()=>this.requstTable())
        console.log(id,name,checkdeptNo,checkdeptId,checkdeptLeaderName,checkparentDeptName)
        this.props.setParentId(id,name,checkdeptNo,checkdeptId,checkdeptLeaderName,checkparentDeptName)
    }
    setdeptId(id,name,checkdeptNo,checkdeptId){
        this.props.setdeptId(id,name,checkdeptNo,checkdeptId)
    }
    //请求树菜单
    requstTable(){
        axios.post(DOMAIN_VIP+'/sys/sysDept/queryListForSelectTab').then(res => {
            const result = res.data
            if(result.code == 0){                
                this.setState({
                    treeData:result.data
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    render(){

        return(
            <div className="col-md-12 col-sm-12 col-xs-12 ">
                <div className="col-md-5 col-sm-5 col-xs-5 ">
                    <ModalDTree 
                         deptManage={this.props.deptManage}
                         setParentId={this.setParentId} 
                         treeData={this.state.treeData}
                        />
                </div>

                <div className="col-md-7 col-sm-7 col-xs-7 borderLeft">
                    <ModalDepModify
                         {...this.state} 
                        handleInputChange={this.props.handleInputChange}  
                        setParentId={this.props.setParentId}
                        setDeptLeaderId={this.props.setDeptLeaderId}
                        setdeptId = {this.setdeptId}
                        />
                </div>
            </div>
        )
    }
}