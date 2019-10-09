import React,{Component} from 'react'
import {Select} from 'antd'
import {SELECTWIDTH} from '../../../conf'
const Option = Select.Option

export default class SelectUserTypeList extends Component {
    constructor(props){
        super(props)
    }
    render(){
        return(
            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                <div className="form-group">
                        <label className="col-sm-3 control-label">账号类型：</label>
                        <div className="col-sm-8">
                            <Select  value={this.props.value} style={{ width:SELECTWIDTH}} onChange={this.props.handleChange}>
                                <Option value=''>全部</Option>
                                <Option value='1'>刷量账户</Option>
                                <Option value='2'>用户账户</Option>
                            </Select>
                        </div>
                </div>
            </div>
        )
    }
}
