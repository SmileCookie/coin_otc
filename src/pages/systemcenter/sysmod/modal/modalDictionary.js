import React from 'react'
import axios from '../../../../utils'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Input,Radio,Modal,Button,Tree } from 'antd'
import ModalSysTree from './modalSysTree'
const { TextArea } = Input;
const RadioGroup = Radio.Group;
const TreeNode = Tree.TreeNode;

export default class ModalDictionary extends React.Component{
    constructor(props){
        super(props)
        this.state = {
           mitem:{},
           visible:false,
           width:'',
           title:'',
           modalHtml:''
        }

        this.onChangeStatus = this.onChangeStatus.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.chooseParNode = this.chooseParNode.bind(this)
        this.chooseParNodeBtn = this.chooseParNodeBtn.bind(this)
        this.chooseParNodeSure = this.chooseParNodeSure.bind(this)
    }

    componentDidMount(){
        console.log(this.props)
        this.setState({
            mitem:JSON.parse(JSON.stringify(this.props.item))
        })
    }

    componentWillReceiveProps(nextProps){
        this.setState({
            mitem:JSON.parse(JSON.stringify(nextProps.item))
        })
    }

    //弹窗隐藏
    handleCancel(){
        console.log("handleCancel")
        this.setState({
            visible: false
        });
    }
    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState((preState) => {
            preState.mitem[name] = value
            return { mitem:preState.mitem }
        },() => this.props.handleInputChange(this.state.mitem));

    }
    //状态切换
    onChangeStatus(e){
        this.setState((preState) => {
            preState.mitem["attrstate"] = e.target.value
            return { mitem:preState.mitem }
        },() => this.props.handleInputChange(this.state.mitem));
    }

    //点击确认按钮是
    chooseParNodeSure(){
        const {parentName,parentId} = this.state
        this.setState((preState) => {
            preState.mitem["parenttypedesc"] = parentName
            preState.mitem["parentid"] = parentId
            return { mitem:preState.mitem,visible:false }
        },() => this.props.handleInputChange(this.state.mitem));
    }

    //返回上级节点的 name 和 id
    chooseParNodeBtn(name,id){
        this.setState({
            parentName:name,
            parentId:id
        })
    }
    //选择上级节点
    chooseParNode(){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={this.chooseParNodeSure}>
                确认
            </Button>,
        ]

        this.setState({
            visible:true,
            modalHtml:<ModalSysTree treeData={this.props.treeData} chooseParNodeBtn={this.chooseParNodeBtn} />,
            width:'600px',
            title:'选择上级节点',
        })
    }

    render(){
        const { attrstate,attrid,attrtype,paraname,paravalue,paradesc,parenttypedesc,attrtypedesc,paracode } = this.state.mitem
        const { visible,width,title,modalHtml } = this.state
        return(
            <div>
            <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">字典属性编号：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="attrtype" value={attrtype||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">属性类型说明：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="attrtypedesc" value={attrtypedesc||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">参数类型：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="paracode" value={paracode||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">参数名称：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="paraname" value={paraname||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">参数值：<i>*</i></label>
                        <div className="col-sm-8">
                            <TextArea className="widthText" rows={4} name="paravalue" value={paravalue||''} onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">参数说明：</label>
                        <div className="col-sm-8">
                            <TextArea className="widthText" rows={4} name="paradesc" value={paradesc||''} onChange={this.handleInputChange}/>                                                    
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">上级节点：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control sminp"  name="parenttypedesc" value={parenttypedesc||''} onChange={this.handleInputChange} />
                            <Button type="more" className="marl10" onClick={this.chooseParNode}>选择</Button>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">状态：</label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.onChangeStatus} value={`${attrstate}`||''}>
                                <Radio value={'1'}>启用</Radio>
                                <Radio value={'0'}>停止</Radio>
                            </RadioGroup>
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
            </div>
        )
    }
}




































