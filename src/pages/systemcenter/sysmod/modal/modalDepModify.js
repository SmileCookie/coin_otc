import React from 'react'
import { Input,Radio,Button,Modal } from 'antd'
import ModalDTree from './modalDTree'
import ModalUser from './modalUser'
const { TextArea } = Input
const RadioGroup = Radio.Group;


export default class ModalDepModify extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            deptName:'',
            deptNo:'',
            parentId:'',
            parentDeptName:'',
            deptLeaderName:'',
            visible:false,
            title:'',
            modalHtml:'',
            deptId:'',
            checkParentDeptId:'',
            checkParentDeptName:'',
            checkDeptLeaderId:'',
            checkDeptLeaderName:'',
            deptLeaderId:'',
            deptLeaderName:'',
            checkdeptNo:'',
            checkdeptId:'',

        }
        this.chooseType = this.chooseType.bind(this)
        this.handleOk = this.handleOk.bind(this)
        this.showModal = this.showModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.chooseTypeBtn = this.chooseTypeBtn.bind(this)
        this.chooseTypeBtn = this.chooseTypeBtn.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.chooseTypeSure = this.chooseTypeSure.bind(this)
        this.chooseUser = this.chooseUser.bind(this)
        this.chooseUserBtn = this.chooseUserBtn.bind(this)
        //this.chooseUserSure = this.chooseUserSure.bind(this)
    }

    componentDidMount(){        
        const { deptName,deptNo,deptLeaderName,parentDeptName } = this.props.item?this.props.item:this.props
        this.setState({
            deptName:deptName||'',
            deptNo:deptNo||'',
            deptLeaderName:deptLeaderName||'',
            parentDeptName:parentDeptName||''
        })
    }
    componentWillReceiveProps(nextProps){
        const { deptName,deptNo,deptLeaderName,parentDeptName  } = nextProps.item?nextProps.item:nextProps
        this.setState({
            deptName:deptName||'',
            deptNo:deptNo||'',
            deptLeaderName:deptLeaderName||'',
            parentDeptName:parentDeptName||''
        })
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
    //选择上级部门名称带回
    chooseType(){
        this.footer = [ <Button key="back" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" type="more" loading={this.state.loading} onClick={this.chooseTypeSure}>
                            确定
                        </Button> ]
        this.setState({
            visible:true,
            modalHtml:<ModalDTree treeData={this.props.treeData} checkable={false} chooseTypeBtn={this.chooseTypeBtn} />,
            title:"选择上级部门菜单"
        })
    }
    //选择上级部门带回按钮
    chooseTypeBtn(deptNo,deptName,deptId,parentId){
        console.log(deptNo,deptName,deptId,parentId)
        this.setState({
            checkParentDeptId:parentId,
            checkParentDeptName:deptName,
            checkdeptNo:deptNo,
            checkdeptId:deptId,
        })
    }
    //选择上级部门带回按钮  确认按钮
    chooseTypeSure(){
        const { checkParentDeptId,checkParentDeptName,checkdeptNo,checkdeptId} = this.state
        this.setState({
            parentId:checkParentDeptId,
            parentDeptName:checkParentDeptName,
            visible:false
        })
        this.props.setdeptId(checkParentDeptId,checkParentDeptName,checkdeptNo,checkdeptId)
    }
    //选择部门负责人带回
    chooseUser(){
        this.footer = [ <Button key="back" onClick={this.handleCancel}>取消</Button>,
                         ]
        this.setState({
            visible:true,
            modalHtml:<ModalUser  chooseUserBtn={this.chooseUserBtn} />,
            title:"选择部门负责人菜单"
        })
    }
    //部门负责人选择带回按钮
    chooseUserBtn(deptId,deptName){
        console.log(deptId,deptName)
        this.setState({
            deptLeaderId:deptId,
            deptLeaderName:deptName,
            visible:false
        })
        this.props.setDeptLeaderId(deptId,deptName)
    }
    // //部门负责人选择带回按钮  确认按钮
    // chooseUserSure(){
    //     const { checkDeptLeaderId,checkDeptLeaderName } = this.state
    //     this.setState({
    //         deptLeaderId:checkDeptLeaderId,
    //         deptLeaderName:checkDeptLeaderName,
    //         visible:false
    //     })
    //     this.props.setDeptLeaderId(checkDeptLeaderId,checkDeptLeaderName)
    // }
    //弹窗 ok 
    handleOk(){
        this.setState({ loading: true });
        setTimeout(() => {
          this.setState({ 
              loading: false, 
              visible: false 
          });
        }, 3000);
    }
    //弹窗显示
    showModal(){
        this.setState({
          visible: true,
        });
    }
    //弹窗隐藏
    handleCancel(){
        this.setState({ visible: false });
    }
    
    render(){
        const { visible,title,loading,modalHtml,deptName,deptNo,deptLeaderName,parentDeptName  } = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-4 control-label">部门名称：<i>*</i></label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="deptName" value={deptName||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">部门编号：<i>*</i></label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="deptNo" value={deptNo||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">部门负责人：<i>*</i></label>
                    <div className="col-sm-8">
                        <input type="text"  className="form-control left mar20" name="deptLeaderName" value={deptLeaderName||''}  onChange={this.handleInputChange} readOnly />
                        <Button type="more" className="line34 btn34" onClick={this.chooseUser}>选择</Button>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">上级部门名称：</label>
                    <div className="col-sm-8">
                        <input type="text"  className="form-control left mar20" name="parentDeptName" value={parentDeptName||''}  onChange={this.handleInputChange} readOnly />
                        <Button type="more" className="line34 btn34" onClick={this.chooseType}>选择</Button>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {modalHtml}
                </Modal>
            </div>  
        )
    }
}



































