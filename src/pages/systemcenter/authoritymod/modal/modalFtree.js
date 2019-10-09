import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import ModalMTree from './modalMTree'
import ModalModify from './modalModify'

export default class ModalFtree extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            visible:false,
            menuFind:'',
            menuName:'',
            menuOrder:'',
            menuSign:'',
            menuUrl:'',
            parentMenuName:'',
            icon:''
        }

        this.getMenuid = this.getMenuid.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
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

    //获取 menuId
    getMenuid(id){
        this.setState({
            menuId:id
        },()=>this.requestTable())
        this.props.setTreeMenuid(id)
    }

    requestTable(){
        axios.get(DOMAIN_VIP+`/sys/menu/info/${this.state.menuId}`).then(res => {
            const result = res.data;
            if(result.code == 0){
                console.log(result)
                this.setState({
                    menuFind:result.menu.type,
                    menuName:result.menu.name,
                    menuOrder:result.menu.orderNum,
                    menuSign:result.menu.perms,
                    menuUrl:result.menu.url,
                    parentMenuName:result.menu.parentName,
                    icon:result.menu.icon,
                    parentId:result.menu.parentId
                })
            }
        })
    }

    render(){
        return(
            <div className="col-md-12 col-sm-12 col-xs-12 hei400">
                <div className="col-md-4 col-sm-4 col-xs-4 tree_slidmin">
                    <ModalMTree 
                        funManage={this.props.funManage}
                        getMenuid={this.getMenuid} 
                        treeData={this.props.treeData}
                        />
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8 borderLeft">
                    <ModalModify
                        {...this.state} 
                        handleInputChange={this.handleInputChange}  
                        setParentId={this.props.setParentId}
                        />
                </div>
            </div>
        )
    }
}





























