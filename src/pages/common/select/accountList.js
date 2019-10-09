import React from 'react'
import { Select,message } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf';
import qs from 'qs';
const Option = Select.Option;

export default class accountList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            arrayList:[<Option key='' value=''>请选择</Option>],
            account:''
        }
    }

    componentWillReceiveProps(props){
        axios.post(DOMAIN_VIP+'/brush/common/platforms',qs.stringify({})).then(res => {
            const result = res.data;
            let accountList = this.state.arrayList;
            if(result.code == 0){
                 for(let i=0;i<result.data.length;i++){
                     accountList.push(<Option key={i+1} value={result.data[i]}>{result.data[i]}</Option>)
                 }
                 this.setState({
                    arrayList:accountList
                 }) 
            }else{
                message.warning(result.msg)
            }
        })
    }

    render(){
        const { arrayList } = this.state;
        return (
            <div className="form-group">
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-2 control-label'} >{this.props.title?this.props.title:'账号'}：</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-10"}>
                    <Select value={this.props.account}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {arrayList}
                    </Select>
                </div>
            </div>
        )
    }

}





























