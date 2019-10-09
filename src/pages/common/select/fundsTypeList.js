import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class FundsTypeList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            accountType:this.props.paymod?[]:[<Option key='0' value={this.props.defaultValue == '' ? this.props.defaultValue : '0'}>请选择</Option>],
            url:props.url || '/common/queryAttr'
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+this.state.url).then(res => {
            const result = res.data;
            let accountTypeArr =[], arrListABC = [];
            if(result.code == 0){
                 for(let i=0;i<result.data.length;i++){
                    accountTypeArr.push(<Option key={result.data[i].paracode} value={result.data[i].paracode}>{result.data[i].paravalue}</Option>)
                 }
                 for(let i=0;i<result.data.length;i++){
                    arrListABC.push(<Option key={result.data[i].paracode} value={result.data[i].paravalue}>{result.data[i].paravalue}</Option>)
                 }
                 switch(this.props.type){
                    case 1://value为字母
                        this.setState({
                            accountType:[<Option key='0' value=''>请选择</Option>,...arrListABC]
                        });
                    break;
                    default://value  default 为数字
                        this.setState({
                            accountType:[...this.state.accountType,...accountTypeArr]
                        }) 
                    break;                                        
                 }
            }
        })
    }

    render(){
        const { accountType } = this.state       
        return (
            <div className="form-group">
                <label className='col-sm-3 control-label' >                
                    {this.props.title ? this.props.title : '资金类型'}                
                </label>                
                <div className="col-sm-8">               
                    <Select
                        showSearch
                        value={this.props.fundsType}
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





























