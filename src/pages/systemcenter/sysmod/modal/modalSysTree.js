import React from 'react'
import axios from '../../../../utils/index'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Input,Radio,Modal,Button,Tree,message  } from 'antd'
const TreeNode = Tree.TreeNode;

export default class ModalSysTree extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            expandedKeys: [],
            autoExpandParent: true,
            checkedKeys: [],
            selectedKeys: [],
            treeData:[],
            showKeys:[],
        }

        this.onExpand = this.onExpand.bind(this)
        this.onCheck = this.onCheck.bind(this)
        this.onSelect = this.onSelect.bind(this)
        this.renderTreeNodes = this.renderTreeNodes.bind(this)
    }   


    //tree function
    onExpand(expandedKeys){
        console.log('onExpand', arguments);
        // if not set autoExpandParent to false, if children expanded, parent can not collapse.
        // or, you can remove all expanded children keys.
        this.setState({
          expandedKeys,
          autoExpandParent: false,
        });
    }
    onCheck(checkedKeys,e){
        console.log('onCheck', checkedKeys);
        console.log(e)
        this.setState({ 
            checkedKeys,
            showKeys:checkedKeys,
        });
    }
    onSelect(selectedKeys, info){
        console.log('onSelect', info);
        console.log(selectedKeys)
        this.setState({ selectedKeys });
        this.props.chooseParNodeBtn(info.node.props.title,info.node.props.dataRef.menuid)
    }
    renderTreeNodes(data){
        return data.map((item) => {
          if(item.floor !== 5){
            if (item.children) {
                return (
                  <TreeNode title={item.title} key={item.key} dataRef={item} >
                    {this.renderTreeNodes(item.children)}
                  </TreeNode>
                );
              }
              return <TreeNode {...item} />;
          }  
        });
    }
    render(){
        const { treeData } = this.props
        return(
            <Tree
                showIcon
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
        )
    }
}





























