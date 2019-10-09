import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf'
import qs from "qs";
const Option = Select.Option;

export default class brushAccount extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            typeList:[<Option key='' value=''>请选择</Option>],
            accountType:''
        }
    }

    componentDidMount(){
        this.requestTable()
    }

    //select 列表
    requestTable = () =>{
        axios.post(DOMAIN_VIP+'/brushamount/btushUser',qs.stringify({
        })).then(res => {
            const result = res.data;
            let typeList = this.state.typeList;
            if(result.code == 0){
                 for(let i=0;i<result.data.length;i++){
                    typeList.push(<Option key={result.data[i]} value={result.data[i]}>{result.data[i]}</Option>)
                 }
                 this.setState({
                    typeList:typeList
                 }) 
            }
        })
    }

    render(){
        const { typeList } = this.state;
        return (
            <div className="form-group">
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-3 control-label'} >刷量账号：</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col*3) :"col-sm-8"}>
                    <Select value={this.props.accountType}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)} disabled={this.props.type == 'edit'}>
                        {typeList}
                    </Select>
                </div>
            </div>
        )
    }

}





























