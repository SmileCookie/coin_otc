import React from 'react'
import { Select } from 'antd'
import axios from '../../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../../conf'
const Option = Select.Option

export default class MarketList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            typeList:this.props.paymod?[]:[<Option key='0' value=''>请选择</Option>],
            market:'etc_btc'
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+'/common/queryMarket').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.typeList;
            if(result.code == 0){
                 for(let i=0;i<result.data.length;i++){
                    accountTypeArr.push(<Option key={i} value={result.data[i]}>{(result.data[i]).toUpperCase()}</Option>)
                 }
                 this.setState({
                    typeList:accountTypeArr
                 }) 
            } 
        })
    }

    render(){
        const { market,typeList } = this.state
        
        return (
            <div className="form-group">
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-2 control-label'} >交易市场：</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-10"}>
                    <Select value={this.props.market}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {typeList}
                    </Select>
                </div>
            </div>
        )
    }

}