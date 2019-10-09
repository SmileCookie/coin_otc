import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../conf'
import { Input,Radio,Modal,Button,Tree,message } from 'antd'
import { pageLimit } from '../../../utils'
import ModalDictionary from './modal/modalDictionary'
const { TextArea } = Input;
const RadioGroup = Radio.Group;
const TreeNode = Tree.TreeNode;

export default class SysDictionary extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            visible:false,
            item:{},
            modifyItem:{},
            expandedKeys: [],
            autoExpandParent: true,
            checkedKeys: [],
            selectedKeys: [],
            treeData:[],
            halfCheckedKeys:[],
            showKeys:[],
            width:'',
            title:'',
            modalHtml:'',
            attrid:'',
            limitBtn: [],
            limitBtns: [],
            loading: false
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestTreeList = this.requestTreeList.bind(this)
        this.onExpand = this.onExpand.bind(this)
        this.onCheck = this.onCheck.bind(this)
        this.onSelect = this.onSelect.bind(this)
        this.renderTreeNodes = this.renderTreeNodes.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.addSysItem = this.addSysItem.bind(this)
        this.addSysItembtn = this.addSysItembtn.bind(this)
        this.deleteSysDicn = this.deleteSysDicn.bind(this)
        this.getPushAddress = this.getPushAddress.bind(this)
    }

    componentDidMount(){
        this.requestTreeList()
        this.setState({
            limitBtn: pageLimit('SysDictionary', this.props.permissList),
            limitBtns: pageLimit('common', this.props.permissList)
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(val){
        this.setState({
            modifyItem:val
        })
    }
   
    
    //左面树形菜单
    requestTreeList(type){
        axios.post(DOMAIN_VIP+"/SysDictionary/list").then(res => {
            const result = res.data;
            console.log(result)
            if(result.code == 0){
                let treeData = [];
                let firstMenu = result.List.filter((currentValue,index,arr) => {
                                    return currentValue.parentid == 0
                                });            
                for(let i = 0;i<firstMenu.length;i++){
                    let treeNode = {
                            title: firstMenu[i].attrtypedesc,
                            key: firstMenu[i].attrid,
                            menuid:result.List[i].attrtype,
                            parentid:firstMenu[i].parentid,
                            floor:1
                        }
                    let treeChild = []
                    for(let j = 0;j<result.List.length;j++){
                        if(firstMenu[i].attrtype == result.List[j].parentid ){
                            let treeSecChild = {
                                    title: result.List[j].attrtypedesc,
                                    key: result.List[j].attrid,
                                    menuid:result.List[j].attrtype,
                                    parentid:result.List[j].parentid,
                                    floor:2
                                }
                            let treeThreeChild = [];
                            for(let k = 0;k<result.List.length;k++){
                                if(result.List[j].attrtype == result.List[k].parentid){
                                    let treeThreeChildNode = {
                                        title: result.List[k].attrtypedesc,
                                        key: result.List[k].attrid,
                                        menuid:result.List[k].attrtype,
                                        parentid:result.List[k].parentid,
                                        floor:3
                                    }
                                    
                                    let treefourChild = [];
                                    for(let h = 0;h<result.List.length;h++){
                                        if(result.List[k].attrtype == result.List[h].parentid){
                                            let treeFourChildNode = {
                                                title: result.List[h].attrtypedesc,
                                                key: result.List[h].attrid,
                                                menuid:result.List[h].attrtype,
                                                parentid:result.List[h].parentid,
                                                floor:4
                                            }
                                            let treeFiveChild = []
                                            for(let l = 0; l < result.List.length;l++){
                                                if(result.List[h].attrtype == result.List[l].parentid){
                                                    treeFiveChild.push({
                                                        title: result.List[l].attrtypedesc,
                                                        key: result.List[l].attrid,
                                                        menuid:result.List[l].attrtype,
                                                        parentid:result.List[l].parentid,
                                                        floor:5
                                                    })
                                                }
                                            }
                                            if(treeFiveChild.length>0){
                                                treeFourChildNode.children = treeFiveChild
                                            }
                                            treefourChild.push(treeFourChildNode)
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
                    if(treeChild.length > 0){
                        treeNode.children = treeChild
                    }
                    treeData.push(treeNode)
                }
                if(type == 'mod'||type == 'add'){
                    this.setState({
                        treeData
                    })
                }else{
                    this.setState({
                        treeData,
                        expandedKeys:[`${treeData[0].key}`]
                    })
                }
               
            }
        })
    }
    // toTree = (data) => {
    //     // 删除 所有 children,以防止多次调用
    //     data.forEach( (item) => {
    //         delete item.children;
    //     });
 
    //     // 将数据存储为 以 id 为 KEY 的 map 索引数据列
    //     var map = {};
    //     data.forEach( (item) => {
    //         map[item.attrtype] = item;
    //     });
    //    console.table(map);
    //     var val = [];
    //     let floor = 0
    //     data.forEach( (item) => {
    //         // 以当前遍历项，的pid,去map对象中找到索引的id
    //         var parent = map[item.parentid];
    //         item.title=item.attrtypedesc;
    //         item.key = item.attrid;
    //         item.menuid = item.attrtype;
    //         item.parentid= item.parentid;
    //         // 好绕啊，如果找到索引，那么说明此项不在顶级当中,那么需要把此项添加到，他对应的父级中
    //         if (parent) {
                
    //             (parent.children || ( parent.children = [] )).push(item);
    //         } else {
    //             //如果没有在map中找到对应的索引ID,那么直接把 当前的item添加到 val结果集中，作为顶级
    //             val.push(item);
    //         }
    //     });
    //     console.table(val)
    //     console.table(val[0].children)
    //     return val;
    // }
    //tree function
    onExpand(expandedKeys){
        console.log('onExpand', expandedKeys);
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
            halfCheckedKeys:e.halfCheckedKeys
        });
        let allKeys = checkedKeys.concat(e.halfCheckedKeys)
        this.props.chooseTreeKey(allKeys)
    }
    onSelect(selectedKeys, info){
        console.log('onSelect', info);
        console.log(selectedKeys)
        this.setState({ selectedKeys });
        this.inquireMenuInfo(selectedKeys.join())
    }
    renderTreeNodes(data){
        return data.map((item) => {
          if (item.children) {
            return (
              <TreeNode title={item.title} key={item.key} dataRef={item} >
                {this.renderTreeNodes(item.children)}
              </TreeNode>
            );
          }
          return <TreeNode {...item} />;
        });
    }
    //查询菜单信息
    inquireMenuInfo(attrId){
        if(attrId){
            this.setState({
                attrid:attrId
            })
            axios.post(DOMAIN_VIP+"/SysDictionary/listById",qs.stringify({
                attrId
            })).then(res => {
                const result = res.data
                if(result.code == 0){
                    this.setState({
                        item:result.list,
                        modifyItem:result.list
                    })
                }
            })
        }
    }
    //弹窗隐藏
    handleCancel(){
        console.log("handleCancel")
        this.setState({
            visible: false,
            loading:false
        });
    }

    //修改||新增
    addSysItem(type){
        const { item,treeData } = this.state
        let newItem = type == 'add'?{}:item
        let title = type == 'add'?"新增字典属性":"修改字典属性"
        
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.addSysItembtn(type)}>
                确认
            </Button>,
        ]
        this.setState({
            visible:true,
            width:"800px",
            title:title,
            modalHtml:<ModalDictionary item={newItem} treeData={treeData} handleInputChange={this.handleInputChange}/>
        })
    }
    //按钮
    addSysItembtn(type){
        const { modifyItem } = this.state
        console.log(modifyItem)
        const {attrtype,paracode,paraname,paravalue} = modifyItem
        if(!attrtype){
            message.warning('字典属性编号不能为空！')
            return false
        }
        if(!paracode){
            message.warning('参数类型不能为空！')
            return false
        }
        if(!paraname){
            message.warning('参数名称不能为空！')
            return false
        }
        if(!paravalue){
            message.warning('参数值不能为空！')
            return false
        }
        let url = type == 'add'?"/SysDictionary/add":"/SysDictionary/editOrUpdate"
        this.setState({
            loading:true
        })
        axios.post(DOMAIN_VIP+url,qs.stringify(modifyItem)).then(res => {
            const result = res.data
            if(result.code == 0){
                console.log(result)
                message.warning(result.msg)
                this.setState({
                    visible:false,
                    loading:false,
                    item:JSON.parse(JSON.stringify(modifyItem))
                })
                this.requestTreeList(type)
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        })
    }

    //删除
    deleteSysDicn(){
        let self = this;
        const { attrid } = this.state
        Modal.confirm({
            title: "您确定要删除该属性节点？",
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+"/SysDictionary/delete",qs.stringify({
                        attrid
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.requestTreeList()
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    //地址推送
    getPushAddress(type){
        let url = type == 1? "/common/push":'/common/deepPush'
        axios.get(DOMAIN_VIP + url).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }

    render(){
        const { treeData,item,visible,modalHtml,width,title,limitBtn,limitBtns } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 系统管理 > 系统字典
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel minhei500">
                            <div className="x_content">
                                <div className="col-md-4 col-sm-4 col-xs-4 tree_slid"> 
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
                                </div>
                                <div className="col-md-8 col-sm-8 col-xs-8"> 
                                    <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                                        <div className="col-md-6 col-sm-6 col-xs-6">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">字典属性编号：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control"  name="attrtype" value={item.attrtype||''} onChange={this.handleInputChange} readOnly />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-6 col-sm-6 col-xs-6">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">属性类型说明：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control"  name="attrtypedesc" value={item.attrtypedesc||''} onChange={this.handleInputChange} readOnly />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                                        <div className="col-md-6 col-sm-6 col-xs-6">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">参数类型：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control"  name="paracode" value={item.paracode||''} onChange={this.handleInputChange} readOnly/>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-6 col-sm-6 col-xs-6">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">参数名称：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control"  name="paraname" value={item.paraname||''} onChange={this.handleInputChange} readOnly/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                                        <div className="col-md-6 col-sm-6 col-xs-6">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">参数值：</label>
                                                <div className="col-sm-8">
                                                    <TextArea className="widthText bgeee" rows={4} name="paravalue" value={item.paravalue||''} readOnly onChange={this.handleInputChange}/>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-6 col-sm-6 col-xs-6">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">参数说明：</label>
                                                <div className="col-sm-8">
                                                    <TextArea className="widthText bgeee" rows={4} name="paradesc" value={item.paradesc||''} readOnly onChange={this.handleInputChange}/>                                                    
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                                        <div className="col-md-6 col-sm-6 col-xs-6">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">上级节点：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control"  name="parenttypedesc" value={item.parenttypedesc||''} onChange={this.handleInputChange} readOnly/>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-6 col-sm-6 col-xs-6">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">状态：</label>
                                                <div className="col-sm-8">
                                                    <RadioGroup value={Number(item.attrstate)} disabled>
                                                        <Radio value={1}>启用</Radio>
                                                        <Radio value={0}>停止</Radio>
                                                    </RadioGroup>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12 martop200">
                                        <div className="right">
                                            {limitBtn.indexOf('add')>-1?<Button type="primary" onClick={() => this.addSysItem('add')}>新增</Button>:''}
                                            {limitBtn.indexOf('editOrUpdate')>-1?<Button type="primary" onClick={() => this.addSysItem('mod')}>修改</Button>:''}
                                            {limitBtn.indexOf('delete')>-1?<Button type="primary" onClick={this.deleteSysDicn}>删除</Button>:''}
                                            {limitBtns.indexOf('push')>-1?<Button type="primary" onClick={()=>this.getPushAddress(1)}>地址推送</Button>:''}  
                                            {limitBtns.indexOf('deepPush')>-1?<Button type="primary" onClick={()=>this.getPushAddress(2)}>市场深度推送</Button>:''}       
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    { modalHtml }
                </Modal> 
            </div>
        )
    }
}






















