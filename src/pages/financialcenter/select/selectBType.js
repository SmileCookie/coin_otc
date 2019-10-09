import React from 'react'
import { Select } from 'antd'
import axios from 'axios'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class SelectAType extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            accountType:[<Option key='0' value='0'>请选择</Option>],
            billType:'0'
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+'/common/queryBillType').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.accountType;
            if(result.code == 0){
                 for(let k in result.data){
                    accountTypeArr.push(<Option key={k} value={k}>{result.data[k]}</Option>)
                 }
                 this.setState({
                    accountType:accountTypeArr
                 }) 
            }
        })
    }


    render(){
        const { billType,accountType } = this.state
        return (
            <div className="form-group">
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-2 control-label'} >{this.props.title?this.props.title:'账单类型：'}</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-10"}>
                    <Select value={this.props.billType}  style={{ width: SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {accountType}
                    </Select>
                </div>
            </div>
        )
    }
}






























