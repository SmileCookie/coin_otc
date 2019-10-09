import React from 'react'
import axios from '../../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../../conf'
import { Select,message } from 'antd'
const Option = Select.Option

export default class ModalRole extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            accountType:this.props.paymod?[]:[<Option key='0' value='0'>请选择</Option>],
            findsType:'0'
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+'/sys/role/select').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.accountType;
            if(result.code == 0){
                 for(let i=0;i<result.list.length;i++){
                    accountTypeArr.push(<Option key={result.list[i].roleId} value={result.list[i].roleId}>{result.list[i].roleName}</Option>)
                 }
                 this.setState({
                    accountType:accountTypeArr
                 }) 
            }else{
                message.warning(result.msg)
            }
        })
    }


    render(){
        const { findsType,accountType } = this.state
        return (
            <div className="form-group">
                <label className='col-sm-3 control-label' >角色：</label>
                <div className="col-sm-8">
                    <Select value={this.props.roleId}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {accountType}
                    </Select>
                </div>
            </div>
        )
    }
}






























