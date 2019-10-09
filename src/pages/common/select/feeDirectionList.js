import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class FeeDirectionList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            feeTypeList:this.props.paymod?[]:[<Option key='0' value=''>请选择</Option>],
        }
    }

    componentDidMount(){
        // axios.post(DOMAIN_VIP+'/futures/list').then(res => {
        //     const result = res.data;
        //     let feeTypeList = this.state.feeTypeList;
        //     if(result.code == 0){
        //          for(let i=0;i<result.list.length;i++){
        //             feeTypeList.push(<Option key={result.list[i].futuresid} value={result.list[i].futuresid}>{result.list[i].futuresname}</Option>)
        //          }
        //          this.setState({
        //             feeTypeList,
        //          }) 
        //     }
        // })


        
    }


    render(){
        const { feeTypeList } = this.state
        return (
            <div className="form-group">
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-3 control-label'} >{this.props.title?this.props.title:'费用方向'}：</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-8"}>
                    <Select value={this.props.feeDirection} style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {this.props.paymod?'':<Option key='0' value=''>请选择</Option>}
                        <Option key='1' value={1}>冷到其他</Option>
                        <Option key='2' value={2}>热充到其他</Option>
                        <Option key='3' value={3}>热提到其他</Option>
                    </Select>
                </div>
            </div>
        )
    }
}






























