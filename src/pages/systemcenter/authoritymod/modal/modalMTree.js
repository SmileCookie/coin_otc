import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import ModalFrom from './modalFrom'
import {Modal,Tree,Input,message } from 'antd'
import { DOMAIN_VIP } from '../../../../conf/index';
const TreeNode = Tree.TreeNode;
const { TextArea } = Input;

export default class ModalTree extends React.Component{
    
    constructor(props) {
        super(props)
        this.state = {
            expandedKeys: ['1'],
            autoExpandParent: true,
            checkedKeys: [],
            selectedKeys: [],
            treeData:[]
        }

        this.onExpand = this.onExpand.bind(this)
        this.onCheck = this.onCheck.bind(this)
        this.onSelect = this.onSelect.bind(this)
        this.renderTreeNodes = this.renderTreeNodes.bind(this)
        this.renderALlTree = this.renderALlTree.bind(this)
        this.requestTree = this.requestTree.bind(this)
    }

    componentDidMount(){
        if(this.props.funManage){
            this.setState({
                menuList:this.props.treeData
            },() => this.renderALlTree())
        }else{
            this.requestTree()
        }
    }
    componentWillReceiveProps(nextProps){
        if(nextProps.funManage){
            this.setState({
                menuList:nextProps.treeData
            },() => this.renderALlTree())
        }else{
            this.requestTree()
        }
    }
    //请求菜单树的接口
    requestTree(){
        axios.get(DOMAIN_VIP+"/sys/menu/list").then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    menuList:result.menuList
                },() => this.renderALlTree())
            }else{
                message.warning(result.msg)
            }
        })
    }

    //遍历整个菜单树   
    renderALlTree(){
        const { menuList } = this.state
        let treeData = [];
        let firstMenu = menuList.filter((currentValue,index,arr) => {
                            return currentValue.parentId == 0
                        });
        for(let i = 0;i<firstMenu.length;i++){
            let treeNode = {
                    title: firstMenu[i].name,
                    key: firstMenu[i].menuId,
                    parentId:firstMenu[i].parentId
                }
            let treeChild = []
            for(let j = 0;j<menuList.length;j++){
                if(firstMenu[i].menuId == menuList[j].parentId ){
                    let treeSecChild = {
                            title: menuList[j].name,
                            key: menuList[j].menuId,
                            parentId:menuList[j].parentId
                        }
                    let treeThreeChild = [];
                    for(let k = 0;k<menuList.length;k++){
                        if(menuList[j].menuId == menuList[k].parentId){
                            let treeThreeChildNode = {
                                title: menuList[k].name,
                                key: menuList[k].menuId,
                                parentId:menuList[k].parentId,
                            }
                            
                            let treefourChild = [];
                            for(let h = 0;h<menuList.length;h++){
                                if(menuList[k].menuId == menuList[h].parentId){
                                    treefourChild.push({
                                        title: menuList[h].name,
                                        key: menuList[h].menuId,
                                        parentId:menuList[h].parentId,
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

    //tree function
    onExpand(expandedKeys){
        // if not set autoExpandParent to false, if children expanded, parent can not collapse.
        // or, you can remove all expanded children keys.
        this.setState({
          expandedKeys,
          autoExpandParent: false,
        });
    }
    onCheck(checkedKeys){
        this.setState({ checkedKeys });
    }
    onSelect(selectedKeys, info){
        this.setState({ selectedKeys });
        this.props.chooseTypeBtn&&this.props.chooseTypeBtn(info.node.props.eventKey,info.node.props.title)
        this.props.getMenuid&&this.props.getMenuid(info.node.props.eventKey)
    }
    renderTreeNodes(data){
        return data.map((item) => {
          if (item.children) {
            return (
              <TreeNode title={item.title} key={item.key} dataRef={item}>
                {this.renderTreeNodes(item.children)}
              </TreeNode>
            );
          }
          return <TreeNode {...item} />;
        });
    }
    
    render(){
        return (
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <Tree
                        checkable={this.props.checkable}
                        showIcon
                        onExpand={this.onExpand}
                        expandedKeys={this.state.expandedKeys}
                        autoExpandParent={this.state.autoExpandParent}
                        onCheck={this.onCheck}
                        checkedKeys={this.state.checkedKeys}
                        onSelect={this.onSelect}
                        selectedKeys={this.state.selectedKeys}
                    >
                        {this.renderTreeNodes(this.state.treeData)}
                    </Tree>   
                </div>
        )
    }
}

























