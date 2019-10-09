import React from 'react'
import { Input,Radio,Button,Modal } from 'antd'
import ModalMTree from './modalMTree'
const { TextArea } = Input
const RadioGroup = Radio.Group;


export default class ModalModify extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            menuFind:1,
            visible:false,
            title:'',
            modalHtml:'',
            loading:false,
            urlDisable:true,
            signDisable:true,
            checkParmenuId:'',
            checkParentMenuName:''
        }
        this.changeRadio = this.changeRadio.bind(this)
        this.chooseType = this.chooseType.bind(this)
        this.handleOk = this.handleOk.bind(this)
        this.showModal = this.showModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.chooseTypeBtn = this.chooseTypeBtn.bind(this)
        this.chooseTypeBtn = this.chooseTypeBtn.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.chooseTypeSure = this.chooseTypeSure.bind(this)
    }

    componentDidMount(){
        
        const { menuFind,menuName,menuOrder,menuSign,icon,menuUrl,parentMenuName } = this.props
        console.log(menuUrl)
        this.setState({
            menuFind,menuName,menuOrder,menuSign,icon,menuUrl,parentMenuName,urlDisable:menuFind==1?false:true
        })

    }

    componentWillReceiveProps(nextProps){
        console.log(nextProps)
        const { menuFind,menuName,menuOrder,menuSign,icon,menuUrl,parentMenuName } = nextProps
        this.setState({
            menuFind,menuName,menuOrder,menuSign,icon,menuUrl,parentMenuName,
            urlDisable:menuFind==1?false:true,
            signDisable:true,
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

    changeRadio(e){
        const val = e.target.value
        this.setState((prevState) => {
            if(val == 0){
                return {
                        menuFind: val,
                        urlDisable:true,
                        signDisable:true
                    }
            }else if(val == 1){
                return {
                        menuFind: val,
                        urlDisable:false,
                        signDisable:true
                    }

            }else if(val == 2){
                return {
                        menuFind: val,
                        urlDisable:true,
                        signDisable:false
                    }
            }
        });
        this.props.changeRadio(val)

    }
    //选择带回
    chooseType(){
        this.footer = [ <Button key="back" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" type="more" loading={this.state.loading} onClick={this.chooseTypeSure}>
                            确定
                        </Button> ]
        this.setState({
            visible:true,
            modalHtml:<ModalMTree checkable={false} chooseTypeBtn={this.chooseTypeBtn} />,
            title:"选择菜单"
        })
    }
    //选择带回按钮
    chooseTypeBtn(menuId,menuName){
        this.setState({
            checkParmenuId:menuId,
            checkParentMenuName:menuName
        })
    }
    //选择带回按钮  确认按钮
    chooseTypeSure(){
        const { checkParmenuId,checkParentMenuName } = this.state
        this.setState({
            parmenuId:checkParmenuId,
            parentMenuName:checkParentMenuName,
            visible:false
        })
        this.props.setParentId(checkParmenuId)
    }

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
        const { visible,title,loading,modalHtml,urlDisable,signDisable,menuFind,menuName,menuOrder,menuSign,icon,menuUrl,parentMenuName, } = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-4 control-label">功能类型：<i>*</i></label>
                    <div className="col-sm-8">
                        <RadioGroup onChange={this.changeRadio} value={this.state.menuFind}>
                            <Radio value={0}>目录</Radio>
                            <Radio value={1}>功能</Radio>
                            <Radio value={2}>按钮</Radio>
                        </RadioGroup>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">功能名称：<i>*</i></label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="menuName" value={menuName||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">图&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;标：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="icon" value={icon||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">显示顺序：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="menuOrder" value={menuOrder||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">上级功能名称：</label>
                    <div className="col-sm-8">
                        <input type="text" style={{width:"250px"}} className="form-control left mar20" name="parentMenuName" value={parentMenuName||''}  onChange={this.handleInputChange} readOnly />
                        <Button type="more" className="line34 btn34" onClick={this.chooseType}>选择</Button>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">功能 URL：</label>
                    <div className="col-sm-8">
                        <TextArea  value={menuUrl||''} className="widthText" disabled={urlDisable} name="menuUrl" onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">授权标识：</label>
                    <div className="col-sm-8">
                        <TextArea  value={menuSign||''} name="menuSign" disabled={signDisable} onChange={this.handleInputChange} />
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



































