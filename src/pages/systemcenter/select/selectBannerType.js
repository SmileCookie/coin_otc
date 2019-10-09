import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class SelectBannerType extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            accountType:[<Option key='0' value=''>请选择</Option>],
            findsType:''
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+'/bannerGroup/common').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.accountType;
            if(result.code == 0){
                 for(let i=0;i<result.data.length;i++){
                    accountTypeArr.push(<Option key={result.data[i].id} value={result.data[i].id}>{result.data[i].bannerGroup}</Option>)
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
                <label className='col-sm-3 control-label' >{this.props.label}：</label>
                <div className="col-sm-8">
                    <Select showSearch value={this.props.findsType} filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}  style={{ width:SELECTWIDTH }} onSelect={(val,key)=>this.props.handleChange(val,key)} >
                        {accountType}
                    </Select>
                </div>
            </div>
        )
    }
}
