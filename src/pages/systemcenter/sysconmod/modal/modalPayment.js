import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP ,SELECTWIDTH} from '../../../../conf'
import { Input,Modal,Button,Radio,Select} from 'antd'
const RadioGroup = Radio.Group;
const Option = Select.Option;

export default class ModalPayment extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            enable:false,
            paymentType:0,
            iconUrl:'',
        }
        this.handleInputChange= this.handleInputChange.bind(this)
        this.handleChange = this.handleChange.bind(this)
        
    
    }
    componentDidMount(){
       
        const {enable,paymentType,paymentName,iconUrl,} = this.props.item
        console.log(enable)
        this.setState({
            enable:enable||false,
            paymentType:`${paymentType}`=='undefined'?0:`${paymentType}`,
            iconUrl:iconUrl||'',
        },()=>{console.log(this.state.paymentType)})
    }
    handleChange(value,opt){
        this.setState({
            paymentType:value
        })
        this.props.handlePayChange(value,opt)
    }
    componentWillReceiveProps(nextProps){
        const {enable,paymentType,paymentName,iconUrl,} = nextProps.item
        this.setState({
            enable:enable||false,
            paymentType:`${paymentType}`=='undefined'?0:`${paymentType}`,
            iconUrl:iconUrl||'',
        },()=>{console.log(this.state.paymentType)})
    }
    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }

    render(){
            const {enable,paymentType,iconUrl}= this.state
            return(
                <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                <div className="col-md-12 col-sm-12 col-xs-12">
                     <div className="form-group">
                         <label className="col-sm-3 control-label"> 是否开启</label>
                         <div className="col-sm-8">
                            <RadioGroup onChange={this.handleInputChange} name="enable" value={enable}>
                                <Radio value={false}>不开启</Radio>
                                <Radio value={true}>开启</Radio>
                            </RadioGroup>
                         </div>
                    </div>
                 </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">付款方式：</label>
                        <div className="col-sm-8">
                        <Select value={paymentType||'0'}  style={{ width:SELECTWIDTH }} onChange={(val,opt) => this.handleChange(val,opt)}>
                                {/* <Option value=''>请选择</Option> */}
                                <Option value='0'>支付宝</Option>
                                <Option value='1'>微信</Option>
                                <Option value='2'>银行</Option>
                                <Option value='3'>BTC</Option>
                            </Select>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">图标地址：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="iconUrl" value={iconUrl||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                 </div>
             </div>

        )
    }
}