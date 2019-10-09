import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class SelectAType extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            accountType:this.props.dealType?[]:[<Option key='0' value=''>请选择</Option>],
            dealType:''
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+'/walletBill/dealTypeList').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.accountType;
            if(result.code == 0){
                for(let key in result.data){
                    accountTypeArr.push(<Option key={key} value={key}>{result.data[key]}</Option>)
                }
            }
        })
    }


    render(){
        const { dealType,accountType } = this.state
        return (
            <div className="form-group">
                <label className='col-sm-3 control-label' >交易类型：</label>
                <div className="col-sm-8">
                    <Select value={this.props.dealType}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChangedealType(val)}>
                        {accountType}
                    </Select>
                </div>
            </div>
        )
    }
}






























