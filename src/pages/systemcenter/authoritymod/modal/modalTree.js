import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, DOMAIN_BASE } from '../../../../conf'
import ModalFrom from './modalFrom'
import {Modal,Tree,Input } from 'antd'
const TreeNode = Tree.TreeNode;
const { TextArea } = Input;

export default class ModalTree extends React.Component{
    
    constructor(props) {
        super(props)
        this.state = {
            userID:'',
            userName:'',
            remarks:'',
            expandedKeys: ['1'],
            autoExpandParent: true,
            checkedKeys: [],
            selectedKeys: [],
            treeData:[],
            halfCheckedKeys:[],
            showKeys:[]
        }

        this.onExpand = this.onExpand.bind(this)
        this.onCheck = this.onCheck.bind(this)
        this.onSelect = this.onSelect.bind(this)
        this.renderTreeNodes = this.renderTreeNodes.bind(this)
        this.renderALlTree = this.renderALlTree.bind(this)
        this.renderPersonTree = this.renderPersonTree.bind(this)
    }

    componentDidMount(){
        this.renderPersonTree(this.props.item.roleId);
    }

    componentWillReceiveProps(nextProps){
        this.renderPersonTree(nextProps.item.roleId);
    }

    //获取当前用户的导航
    renderPersonTree(roleId){
        let self = this;
        axios.get(DOMAIN_VIP+"/sys/menu/nav",{
            params:{
                roleId:roleId
            }
        }).then(res => {
            const result = res.data;
            if(result.code == 0){
                let checkedKeys = [],
                    showKeys = [];
                for(let i=0;i<result.menuList.length;i++){
                    checkedKeys.push(`${result.menuList[i].menuId}`)
                    for(let j=0;j<result.menuList[i].list.length;j++){
                        checkedKeys.push(`${result.menuList[i].list[j].menuId}`)
                        if(!result.menuList[i].list[j].list.length){
                            showKeys.push(`${result.menuList[i].list[j].menuId}`)
                        }
                        for(let k=0;k<result.menuList[i].list[j].list.length;k++){
                            checkedKeys.push(`${result.menuList[i].list[j].list[k].menuId}`)
                            if(!result.menuList[i].list[j].list[k].list.length){
                                showKeys.push(`${result.menuList[i].list[j].list[k].menuId}`)
                            }
                            if(result.menuList[i].list[j].list[k].list.length > 0){
                                for(let x=0; x<result.menuList[i].list[j].list[k].list.length; x++){
                                    checkedKeys.push(`${result.menuList[i].list[j].list[k].list[x].menuId}`)
                                    showKeys.push(`${result.menuList[i].list[j].list[k].list[x].menuId}`)
                                }
                            }else{
                                showKeys.push(`${result.menuList[i].list[j].list[k].menuId}`)
                            }
                        }
                    }
                }
                self.renderALlTree(checkedKeys,showKeys)
            }
        })
    }
    //遍历整个菜单树   
    renderALlTree(checkedKeys,showKeys){
        axios.get(DOMAIN_VIP+"/sys/menu/list").then(res => {
            const result = res.data;
            if(result.code == 0){
                let treeData = [];
                let firstMenu = result.menuList.filter((currentValue,index,arr) => {
                                    return currentValue.parentId == 0
                                });
                for(let i = 0;i<firstMenu.length;i++){
                    let treeNode = {
                            title: firstMenu[i].name,
                            key: firstMenu[i].menuId,
                            parentId:firstMenu[i].parentId
                        }
                    let treeChild = []
                    for(let j = 0;j<result.menuList.length;j++){
                        if(firstMenu[i].menuId == result.menuList[j].parentId ){
                            let treeSecChild = {
                                    title: result.menuList[j].name,
                                    key: result.menuList[j].menuId,
                                    parentId:result.menuList[j].parentId
                                }
                            let treeThreeChild = [];
                            for(let k = 0;k<result.menuList.length;k++){
                                if(result.menuList[j].menuId == result.menuList[k].parentId){
                                    let treeThreeChildNode = {
                                        title: result.menuList[k].name,
                                        key: result.menuList[k].menuId,
                                        parentId:result.menuList[k].parentId,
                                    }
                                    
                                    let treefourChild = [];
                                    for(let h = 0;h<result.menuList.length;h++){
                                        if(result.menuList[k].menuId == result.menuList[h].parentId){
                                            treefourChild.push({
                                                title: result.menuList[h].name,
                                                key: result.menuList[h].menuId,
                                                parentId:result.menuList[h].parentId,
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
                    treeData,
                    checkedKeys,
                    showKeys
                })
                this.props.chooseTreeKey(checkedKeys)
            }
        })
    }
    
    //tree function
    onExpand(expandedKeys){
        // if not set autoExpandParent to false, if children expanded, parent can not collapse.
        // or, you can remove all expanded children keys.
        this.setState({
          expandedKeys,
          autoExpandParent: false,
        });
    }
    onCheck(checkedKeys,e){
        this.setState({ 
            checkedKeys,
            showKeys:checkedKeys,
            halfCheckedKeys:e.halfCheckedKeys
        });
        let allKeys = checkedKeys.concat(e.halfCheckedKeys)
        this.props.chooseTreeKey(allKeys)
    }
    onSelect(selectedKeys, info){
        this.setState({ selectedKeys });
    }
    renderTreeNodes(data){
        let type = this.props.type?false:true
        return data.map((item) => {
          if (item.children) {
            return (
              <TreeNode title={item.title} key={item.key} dataRef={item}  >
                {this.renderTreeNodes(item.children)}
              </TreeNode>
            );
          }
          return <TreeNode {...item}  />;
        });
    }
    
    render(){
        const { treeData,showKeys } = this.state
        const { roleName,remark } = this.props.item
        let type = this.props.type?false:true
        return (
                <div className="col-md-12 col-sm-12 col-xs-12 hei600">
                    <div className="col-md-7 col-sm-7 col-xs-7">
                        <ModalFrom  userName={roleName} remarks={remark} readOnly />
                    </div>
                    <div className="col-md-5 col-sm-5 col-xs-5 tree_slidmin600">
                        <Tree
                            checkable
                            multiple
                            showIcon
                            disabled={type}
                            onExpand={this.onExpand}
                            expandedKeys={this.state.expandedKeys}
                            autoExpandParent={this.state.autoExpandParent}
                            onCheck={this.onCheck}
                            checkedKeys={this.state.showKeys}
                            onSelect={this.onSelect}
                            selectedKeys={this.state.selectedKeys}
                        >
                            {this.renderTreeNodes(treeData)}
                        </Tree>
                    </div>
                </div>
        )
    }
}

























