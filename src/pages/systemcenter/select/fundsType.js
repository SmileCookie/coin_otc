import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class FundsTypeList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            accountType:this.props.paymod?[]:[<Option key='0' value='0'>请选择</Option>],
        }
    }

    async componentWillMount(){
       await axios.get(DOMAIN_VIP+'/common/queryAttr').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.accountType;
            if(result.code == 0){
                 for(let i=0;i<result.data.length;i++){
                    accountTypeArr.push(<Option key={result.data[i].paracode} value={result.data[i].paracode}>{result.data[i].paravalue}</Option>)
                 }
                 this.setState({
                    accountType:accountTypeArr
                 }) 
            }
        })
        
    }
    componentWillUnmount(){
        this.setState = (state,callback)=>{
            return;
          };  
    }

    render(){
        const { accountType } = this.state
       const {fundsType} = this.props
        return (
            <div className="form-group">
                <label className='col-sm-3 control-label' >
                
                    {this.props.title == ''?'资金类型':(this.props.title !='币种名称'?this.props.title:(<div>币种名称:<i>&ensp;*</i></div>) )}
                
                </label>
                
                <div className="col-sm-8">
               
                    <Select
                        showSearch
                        labelInValue
                        value={{key:fundsType,label:fundsType}}
                        filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        style={{ width:SELECTWIDTH }}
                        onChange={(val) => this.props.handleChange&&this.props.handleChange(val)}
                        onSelect={(val,option) => this.props.handleSelect&&this.props.handleSelect(val,option.props.children)}
                        >                        
                        {accountType}
                    </Select>
                </div>
            </div>
        )
    }
}





























