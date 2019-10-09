//部门管理>上级部门名称
import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import {Modal,Tree,Input,message } from 'antd'
import { DOMAIN_VIP } from '../../../../conf/index';
const TreeNode = Tree.TreeNode;

export default class ModalDTree extends React.Component{
    constructor(props){
        super(props)
        this.state={
            menuList:[],
            treeData:[],
            checkedKeys: [],
            selectedKeys: [],//选中的树节点
            autoExpandParent: true,//是否展开父节点
            expandedKeys: ['1'],
        }

        this.requstTree = this.requstTree.bind(this)
        this.renderTreeNodes = this.renderTreeNodes.bind(this)
        this.requstTreeAll = this.requstTreeAll.bind(this)
        this.onCheck = this.onCheck.bind(this)
        this.onSelect = this.onSelect.bind(this)
        this.onExpand = this.onExpand.bind(this)
    }
    componentDidMount(){
        if(this.props.deptManage){
            this.setState({
                menuList:this.props.treeData
            },()=>this.requstTreeAll())
        }else{
            this.requstTree()
        }
    }
    componentWillReceiveProps(nextProps){
        if(nextProps.deptManage){
            this.setState({
                menuList:nextProps.treeData
            },()=>this.requstTreeAll())
        }else{
            this.requstTree()
        }
    }
    //请求树菜单
    requstTree(){
        axios.post(DOMAIN_VIP+'/sys/sysDept/queryListForSelectTab').then(res => {
            const result = res.data
            if(result.code == 0){                
                this.setState({
                    menuList:result.data
                },()=>this.requstTreeAll())
            }else{
                message.warning(result.msg)
            }
        })
    }
    //遍历整个菜单树   
    requstTreeAll(){
        
        const { menuList } = this.state
        let treeData = [];
        let firstMenu = menuList.filter((currentValue,index,arr) => {
                            return currentValue.parentId == null
                        });
        for(let i = 0;i<firstMenu.length;i++){
            let treeNode = {
                    title: firstMenu[i].deptName,
                    key: firstMenu[i].deptId,
                    parentId:firstMenu[i].parentId,
                    deptNo:firstMenu[i].deptNo,
                    deptLeaderName:firstMenu[i].deptLeaderName,
                    parentDeptName:firstMenu[i].parentDeptName
                }
            let treeChild = []
            for(let j = 0;j<menuList.length;j++){
                if(firstMenu[i].deptId == menuList[j].parentId ){
                    let treeSecChild = {
                            title: menuList[j].deptName,
                            key: menuList[j].deptId,
                            parentId:menuList[j].parentId,
                            deptNo:menuList[j].deptNo,
                            deptLeaderName:menuList[j].deptLeaderName,
                            parentDeptName:menuList[j].parentDeptName
                        }
                    let treeThreeChild = [];
                    for(let k = 0;k<menuList.length;k++){
                        if(menuList[j].deptId == menuList[k].parentId){
                            let treeThreeChildNode = {
                                title: menuList[k].deptName,
                                key: menuList[k].deptId,
                                parentId:menuList[k].parentId,
                                deptNo:menuList[k].deptNo,
                                deptLeaderName:menuList[k].deptLeaderName,
                                parentDeptName:menuList[k].parentDeptName
                            }
                            
                            let treefourChild = [];
                            for(let h = 0;h<menuList.length;h++){
                                if(menuList[k].deptId == menuList[h].parentId){
                                    treefourChild.push({
                                        title: menuList[h].deptName,
                                        key: menuList[h].deptId,
                                        parentId:menuList[h].parentId,
                                        deptNo:menuList[h].deptNo,
                                        deptLeaderName:menuList[h].deptLeaderName,
                                        parentDeptName:menuList[h].parentDeptName
                                    })
                                }
                            }
                            if(treefourChild.length>0){
                                treeThreeChildNode.children = treefourChild
                            }
                            treeThreeChild.push(treeThreeChildNode)
                        }
                    }
                    
                    if(treeThreeChild.length>0){
                        treeSecChild.children = treeThreeChild
                    }
                    
                    treeChild.push(treeSecChild)
                }
            }
            treeNode.children = treeChild
            treeData.push(treeNode)
        }
        this.setState({
            treeData
        })
    }
    //点击复选框
    onCheck(checkedKeys){
        this.setState({
            checkedKeys
        })
    }
    //选中的树节点
    onSelect(selectedKeys,info){
        this.setState({
            selectedKeys
        })
        this.props.chooseTypeBtn&&this.props.chooseTypeBtn(info.node.props.deptNo,info.node.props.title,info.node.props.eventKey,info.node.props.parentId)
        this.props.setParentId&&this.props.setParentId(info.node.props.deptNo,info.node.props.title,info.node.props.eventKey,info.node.props.parentId,info.node.props.deptLeaderName,info.node.props.parentDeptName)
    }
    //展开、收起节点是触发
    onExpand(expandedKeys){
        this.setState({
            expandedKeys,
            autoExpandParent: false,
          });
    }
    renderTreeNodes(data){
        return data.map((item) => {
            if(item.children){
                return (
                    <TreeNode title={item.title} key={item.key} deptNo={item.deptNo} dataRef={item}>
                        {this.renderTreeNodes(item.children)}
                    </TreeNode>
                );
            }
            return <TreeNode {...item} dataRef={item} deptNo={item.deptNo}/>
        });
    }
    render(){
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                    <Tree 
                        checkable={this.props.checkable}
                        onCheck={this.onCheck}
                        onSelect={this.onSelect}
                        onExpand={this.onExpand}
                        expandedKeys={this.state.expandedKeys}
                        selectedKeys={this.state.selectedKeys}
                        checkedKeys={this.state.checkedKeys}
                        autoExpandParent={this.state.autoExpandParent}
                    >
                        {this.renderTreeNodes(this.state.treeData)}
                    </Tree>   
                </div>
        )
    }
}