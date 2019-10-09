import React from 'react'
import { Select } from 'antd'
import axios from '../../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../../conf/index'
const Option = Select.Option

export default class ModalOper extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            accountType:[<Option key='0' value='0'>请选择</Option>],
            logtypeid:'0'
        }
        console.log(props)

        this.onChangeVal = this.onChangeVal.bind(this)
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+'/common/getLogTypeList').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.accountType;
            if(result.code == 0){
                 for(let i=0;i<result.data.length;i++){
                    accountTypeArr.push(<Option key={result.data[i].typeCode} value={result.data[i].typeCode}>{result.data[i].value}</Option>)
                 }
                 this.setState({
                    accountType:accountTypeArr
                 }) 
            }
        })
    }

    //select 改变时
    onChangeVal(val,option){
        console.log(val)   
        console.log(option.props.keyvalue)  
        //(val) => this.props.handleChange(val)
        this.props.handleChange(option.props.keyvalue)
    }

    render(){
        const { logtypeid,accountType } = this.state
        return (
            <div className="form-group">
                <label className='col-sm-3 control-label' >日志类型：</label>
                <div className="col-sm-8">
                    {/* <Select
                        allowClear
                        mode="combobox"
                        placeholder="请选择"
                        style={{ width:SELECTWIDTH }}
                        onSelect={(val,option)=> this.props.handleChangeType(option.props.keyvalue)} >
                        {accountType}
                    </Select> */}
                      <Select value={this.props.logtypeid}  style={{ width: SELECTWIDTH }} onChange={(val) => this.props.handleChangeType(val)}>
                        {accountType}
                    </Select>
                </div>
            </div>
        )
    }
}





























