import React from 'react'
import { Button,Modal,Form,Input} from 'antd'
const FormItem = Form.Item;


 const GoogleCode = Form.create()(
    class extends React.Component {
    constructor(props){
        super(props)
        this.state = {
            modalHtml:'',
            title:'',
            width:'400px',
            googleCode:'',
            checkGoogle:'',
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onCancel = this.onCancel.bind(this)
        this.modalGoogleCodekeyPress = this.modalGoogleCodekeyPress.bind(this)
    }
    componentDidMount(){
        window.addEventListener('keypress',this.modalGoogleCodekeyPress)
    }
    componentWillUnmount(){
        window.removeEventListener('keypress',this.modalGoogleCodekeyPress)
    }
      //google 弹窗添加 回撤 事件
      modalGoogleCodekeyPress(e){
        const {mid} = this.props
        const { visible } = this.props
        const {googleCode,checkGoogle}= this.state
        if(visible){
            if(e.keyCode == 13){
                if(googleCode||checkGoogle){
                    let e = document.createEvent("MouseEvents");
                    e.initEvent("click", true, true);
                    document.getElementById(`clickkey`+`${mid}`).dispatchEvent(e);
                }
            }
        }
    }
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        },()=>this.clickKey(name,value));
        this.props.handleInputChange(event)
    }
       //google六位自动验证
       clickKey(name,value){
            const {check} = this.props
        const {mid} = this.props
        const {googleCode,checkGoogle} = this.state
         if(check=='check'){
            if(name=='googleCode'||'checkGoogle'){
                if(googleCode.length > 5 && checkGoogle.length > 5){
                    let e = document.createEvent("MouseEvents");
                    e.initEvent("click", true, true);
                    document.getElementById(`clickkey`+`${mid}`).dispatchEvent(e);
                }
            }
         }else{
            if(name=='googleCode'){
                if(value.length > 5){
                    let e = document.createEvent("MouseEvents");
                    e.initEvent("click", true, true);
                    document.getElementById(`clickkey`+`${mid}`).dispatchEvent(e);
                }
            }
         }
        
    }
    onCancel(){
        this.props.form.resetFields();
        this.props.onCancel()
    }
    render(){
        const {title,width} =this.state
        const {visible,form, onCreate,check,mid,googleLoading = false} = this.props;
        const { getFieldDecorator } = form;
        const formItemLayout = {
            labelCol: 
                {span: 8}
            ,
            wrapperCol: {
                span: 14
            },
          };
        return(
         <Modal
         zIndex={1001}
        visible={visible}
        title={title}
        width={width}
        onCancel={this.onCancel}
        title="谷歌验证"
        footer={[
            <Button key="back" onClick={this.onCancel}>取消</Button>,
            <Button key="submit" type="more"  id={`clickkey`+`${mid}`} loading={googleLoading} onClick={onCreate}>
                确定
            </Button>,
        ]}
        >
         <Form layout="vertical">
            <FormItem label="Google验证码："  {...formItemLayout} >
            {getFieldDecorator('googleCode', {
                rules: [{ required: true, message: '请输入谷歌验证码!' },{pattern:'^[0-9]*$',message:'请输入数字!'}],
              })(
                <Input ref={(inp) => this.input = inp }  name="googleCode" onChange={this.handleInputChange}/>
              )}
        </FormItem>  
        {check&&<FormItem label="监察员Google："  {...formItemLayout}> 
            {getFieldDecorator('checkGoogle', {
               rules: [{ required: true, message: '请输入监察员Google!' },{pattern:'^[0-9]*$',message:'请输入数字!'}],
            })(
              <Input ref={(inp) => this.inputcheck = inp }  name='checkGoogle' onChange={this.handleInputChange}/> 
            )}
        </FormItem>}
        </Form>    
    </Modal> 

        )
    }

    }
)
export default GoogleCode