import React from 'react'
import { Input,Form} from 'antd'
const { TextArea } = Input
const FormItem = Form.Item;

export default class ModalCapital extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            username:'',
            fundstypename:'',
            strMoney:'',
            memo:'',
            fee:'',
            type:'',
            balance:'',
            freez:''
        }
        this.handleInputChange = this.handleInputChange.bind(this)
    }
   
      

    componentDidMount(){
        const {username,fundstypename,balance,freez} = this.props.item
        this.setState({
            username,
            fundstypename,
            balance,
            freez,
            strMoney:'',
            memo:'',
            fee:'',
            type:this.props.type
        })
    }

    componentWillReceiveProps(nextProps){
        const {username,fundstypename,balance,freez} = nextProps.item
        this.setState({
            username,
            fundstypename,
            balance,
            freez:freez,
            strMoney:'',
            memo:'',
            fee:'',
            type:nextProps.type
        })
    }

    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }

    render(){
        // const { getFieldDecorator } = this.form;
       
        const { fundstypename,username,strMoney,memo,fee,type,balance,freez } = this.state
        const formItemLayout = {
            labelCol: {
              xs: { span: 24 },
              sm: { span: 5 },
            },
            wrapperCol: {
              xs: { span: 24 },
              sm: { span: 12 },
            },
          };
        return(
            
            <div className="col-md-12 col-sm-12 col-xs-12">
            <Form>
                {
                    (type==1||type==2)&&
            //         <FormItem
            //             {...formItemLayout}
            //             >
            //             {getFieldDecorator('fundstypename')(
            //     <Input  name="googleCode" onChange={this.handleInputChange} readOnly/>
            //   )}
            //    </FormItem>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">财务账户：</label>
                        <div className="col-sm-9">
                            <input type="text" className="form-control"  value={fundstypename} onChange={this.handleInputChange} readOnly />
                        </div>
                    </div>
                }
                <div className="form-group">
                    <label className="col-sm-3 control-label">用户名：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control" name="username" value={username} onChange={this.handleInputChange} readOnly />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">{type==1?'充值':type==2?'扣除':type==3?'冻结':'解冻'}{fundstypename}数量：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control" name="strMoney" value={strMoney} onChange={this.handleInputChange} />
                        {type==2&&<span className="blue">可扣除：{balance} {fundstypename}</span>}
                        {type==3&&<span className="blue">可冻结：{balance} {fundstypename}</span>} 
                        {type==4&&<span className="blue">可解冻：{freez} {fundstypename}</span>}                         
                    </div>
                </div>
                {
                    type == 1&&<div className="form-group">
                        <label className="col-sm-3 control-label">手续费：</label>
                        <div className="col-sm-9">
                            <input type="text" className="form-control" name="fee" value={fee} onChange={this.handleInputChange} />
                            <span>正值代表网站收入，负值代表网站支出。</span>
                        </div>
                    </div>
                }
                <div className="form-group">
                    <label className="col-sm-3 control-label">描述：</label>
                    <div className="col-sm-9 text-box">
                        <TextArea  name="memo" value={memo} onChange={this.handleInputChange} />
                    </div>
                </div>
                </Form>
            </div>
        )
    }

}








































