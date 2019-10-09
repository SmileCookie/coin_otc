import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf'
const Option = Select.Option

export default class vipRateList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            typeList:[<Option key='' value=''>请选择</Option>],
            vipType:''
        }
        this.requestTable = this.requestTable.bind(this)
    }

    componentDidMount(){
        this.requestTable()
    }

    //select 列表
    requestTable(){
        axios.get(DOMAIN_VIP+'/common/getVipRateList').then(res => {
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
        const { vipType,typeList } = this.state
        return (
            <div className="form-group">
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-2 control-label'} >用户等级：</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-10"}>
                    <Select value={this.props.vipType}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {typeList}
                    </Select>
                </div>
            </div>
        )
    }

}





























