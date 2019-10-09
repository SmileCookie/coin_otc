import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class SelectAType extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            accountType:this.props.paymod?[]:[<Option key='0' value='0'>请选择</Option>],
            findsType:'0'
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+'/common/queryAttr').then(res => {
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


    render(){
        const { findsType,accountType } = this.state
        return (
            <div className="form-group">
                <label className='col-sm-3 control-label' >资金类型：</label>
                <div className="col-sm-8">
                    <Select showSearch value={this.props.findsType} filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {accountType}
                    </Select>
                </div>
            </div>
        )
    }
}






























