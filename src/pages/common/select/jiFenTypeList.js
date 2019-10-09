import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf'
const Option = Select.Option

export default class JiFenTypeList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            typeList:[<Option key='' value=''>请选择</Option>],
            jifenType:''
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+'/common/getJiFenTypeList').then(res => {
            const result = res.data;
            let typeList = this.state.typeList;
            if(result.code == 0){
                 for(let i=0;i<result.data.length;i++){
                    typeList.push(<Option key={result.data[i].key} value={result.data[i].key}>{result.data[i].value}</Option>)
                 }
                 this.setState({
                    typeList:typeList
                 }) 
            }
        })
    }

    render(){
        const { jifenType,typeList } = this.state
        return (
            <div className="form-group">
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-2 control-label'} >积分类型：</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-10"}>
                    <Select value={this.props.jifenType}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {typeList}
                    </Select>
                </div>
            </div>
        )
    }

}





























