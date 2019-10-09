import React from 'react'
// import qs from 'qs'
import { Input,Select,message } from 'antd'
// import axios from '../../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../../conf'
// import cookie from 'js-cookie'
// import FeeTypeList from '../../../common/select/feeTypeList'
// import FeeDirectionList from '../../../common/select/feeDirectionList'
// import FundsTypeList from '../../../common/select/fundsTypeList'
// import { toThousands } from '../../../../utils'
// const { TextArea } = Input;
const Option = Select.Option;

export default class Modaltransfer extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            accountid:'',
            money:''
        }
        this.handleInputChange = this.handleInputChange.bind(this)
    }

    componentDidMount(){
        const {money,accountid} = this.props
        console.log(money)
        this.setState({
            [money]:'',
            [accountid]:'',
        })

    }

    componentWillReceiveProps(nextProps){
        // const { userName,editItem } = nextProps
        // this.setState({
        //     reaccname:userName,
        //     mcostdirection:editItem&&editItem.costdirection||1,
        //     mdownloadamount:editItem&&editItem.downloadamount||'',
        //     mdownloadaddress:editItem&&editItem.downloadaddress||'',
        //     tmp:editItem&&editItem.tmp||'',
        //     mfeetype:'1',
        //     mfundstype:'0',
        //     availableAmount:0,
        //     mfundstypename:'',
        // })
       
        this.setState({
            accountid:'',
            money:''
        })
    }


    //账户下拉选择框
    handleChange=(value)=>{
        this.setState({
            accountid:value
        })
        this.props.handleSelectChange(value)
    }
    //输入时 input 设置到 satte
    handleInputChange = event =>{
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }
    // //资金类型
    // selectFundsType = v => {
    //     this.setState({
    //         mfundstype:v
    //     },()=>{
    //         this.requestMoney().then((availableAmount)=>{
    //             this.props.mselectFundsType(v,availableAmount)
    //         })
    //     })
    // }
    // //费用类型
    // selectFeeType = v => {
    //     this.setState({
    //         mfeetype:v
    //     })
    //     this.props.mselectFeeType(v)
    //     // this.props.selectReFeeType(v)
    // }
    // selectFeeDirect = v => {
    //     this.setState({
    //         mcostdirection:v
    //     })
    //     this.props.selectFeeDirect(v)
    // }
    // requestMoney = () => {
    //     const { mfeetype,mfundstype } = this.state
    //     let feetype = '';
    //     if(mfeetype == 2){
    //         feetype = 2
    //     }else{
    //         feetype = 9
    //     }
    //     let availableAmount = 0;
    //     return new Promise((resolve, reject) => {
    //         axios.post(DOMAIN_VIP+'/feeAccountCheck/balance',qs.stringify({            
    //             feetype,type:mfundstype
    //         })).then(res => {
    //             const result = res.data;
    //             if(result.code == 0){
    //                 try{
    //                     availableAmount = result.operationData.currentamount - result.operationData.freezeamount;
    //                     this.setState({
    //                         availableAmount,
    //                     })   
    //                     resolve(availableAmount)                    
    //                 }catch(error){
    //                     message.warning('错误'+error)
    //                     console.log(error)
    //                 }               
    //             }else{
    //                 message.warning(result.msg);
    //             }
    //         })
    //     })
    // }
    // //资金类型
    // handleSelect = (v, option) => {
    //     this.setState({
    //         mfundstype:v,
    //         mfundstypename:option
    //     },()=>{
    //         this.requestMoney().then((availableAmount)=>{
    //             this.props.mselectFundsType(v,availableAmount,option+'其他')
    //         })
    //     })
    // }
    render(){
        const {accountid,money} = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-2 control-label">划转资金：</label>
                    <div className="col-sm-6" style={{width:'235px'}}>
                        <input type="text" className="form-control" value={this.props.from||'我的钱包'}  name='reaccname' readOnly /><br /><br />
                    </div>
                    <div className="form-group" style={{display:'inline'}}>
                    <label className="col-sm-2 control-label">划转到：</label>
                    <div className="">
                    {this.props.to ?
                        <input type="text" className="form-control" value={this.props.to||'我的钱包'}  name='reaccname' readOnly />
                    :
                       <Select value={accountid} onChange={this.handleChange} style={{width:SELECTWIDTH}}>
                            <Option value=''>请选择</Option>
                            <Option value='2'>币币账户</Option>
                            <Option value='3'>法币账户</Option>
                            {/* <Option value='4'>期货账户</Option>   */}
                            <Option value='5'>理财账户</Option>  
                        </Select>}
                    </div>
                </div>
                </div>
                
               
                <div className="form-group">
                    <label className="col-sm-2 control-label">金额：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={money}  name='money'  onChange={this.handleInputChange}/><br /><br />
                    </div>
                </div>
                {/* // <FeeTypeList title='费用类型' showOption='true' col={2} feeType={mfeetype} handleChange={this.selectFeeType} /> */}
                
                {/* <FeeDirectionList title='费用方向' col={2} feeDirection={mcostdirection} paymod={true} handleChange={this.selectFeeDirect} />
                <div className="form-group">
                    <label className="col-sm-2 control-label">提现金额:<i>*</i></label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" value={mdownloadamount} name="mdownloadamount" onChange={this.handleInputChange} />
                        可用：<span className='moneyGreen' style={{margin:'0 5px'}}>{toThousands(this.props.item?this.props.item.availableAmount:availableAmount)}</span>{this.props.item?this.props.item.fundstypename:mfundstypename}
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">提现地址：<i>*</i></label>
                    <div className="col-sm-8 text-box">
                        <TextArea onChange={this.handleInputChange} value={mdownloadaddress} name="mdownloadaddress" rows={4} />
                        <p className="blank-spacing"></p>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">备注：<i>*</i></label>
                    <div className="col-sm-8 text-box">
                        <TextArea onChange={this.handleInputChange} value={tmp} name="tmp" rows={4} />
                    </div>
                </div> */}
            </div> 
        )
    }
}































