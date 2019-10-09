import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf'
const Option = Select.Option

export default class ruleTypeList extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            typeList:[<Option key={0} value={0}>请选择</Option>],
            scoreName:''
        }
    }

    componentDidMount(){
        this.initData()
    }

    //select 列表
    initData(){
        axios.post(DOMAIN_VIP+'/integralRule/queryRuleType').then(res => {
            const result = res.data;
            let typeList = this.state.typeList;
            if(result.code == 0){
                 for(let i=0;i<result.List.length;i++){
                    typeList.push(<Option key={result.List[i].paravalue} value={result.List[i].paravalue}>{result.List[i].paraname}</Option>)
                 }
                 this.setState({
                    typeList:typeList
                 }) 
            }
        })
    }

    render(){
        const {typeList } = this.state;
        return (
            <div className="form-group">
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-2 control-label'} >名称：</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-10"}>
                    <Select value={this.props.scoreName}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {typeList}
                    </Select>
                </div>
            </div>
        )
    }

}





























