import React from 'react'
import { Select } from 'antd'
import axios from 'axios'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class SelectChoice extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            accountChoose:[<Option key='0' value='0'>请选择</Option>]
        }
    }
    
    componentDidMount(){
        axios.get(DOMAIN_VIP+'/accountManage/account').then(res => {
            const result = res.data
            let accountChooseArr = this.state.accountChoose;            
            if(result.code == 0){
                for(let i=0;i<result.data.length;i++){
                    accountChooseArr.push(<Option key={result.data[i].id} value={result.data[i].id}>{result.data[i].name}</Option>)
                }
                this.setState({
                    accountChoose:accountChooseArr
                })
            }
        })
    }


    render(){
        const { accountChoose } = this.state
        return (
            <div className="form-group">
                <label className="col-sm-3 control-label">账户选择：</label>
                <div className="col-sm-9">
                    <Select value={this.props.findsType}  style={{ width: SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {accountChoose}
                    </Select>
                </div>
            </div>
        )
    }
}






























