// 期货市场
import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class HandicapMarket extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            handicapMarketArr:this.props.paymod?[]:[<Option key='0' value=''>请选择</Option>],
        }
    }

    componentDidMount(){
        axios.post(DOMAIN_VIP+'/futures/list').then(res => {
            const result = res.data;
            let handicapMarketArr = this.state.handicapMarketArr;
            if(result.code == 0){
                 for(let i=0;i<result.list.length;i++){
                    handicapMarketArr.push(<Option key={result.list[i].futuresid} value={result.list[i].futuresid}>{result.list[i].futuresid}</Option>)
                 }
                 this.setState({
                    handicapMarketArr,
                 }) 
            }
        })
    }


    render(){
        const { handicapMarketArr } = this.state
        return (
            <div className="form-group">
                <label className='col-sm-3 control-label' >{this.props.title?this.props.title:'期货市场'}：</label>
                <div className="col-sm-8">
                    <Select showSearch value={this.props.marketType} filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {handicapMarketArr}
                    </Select>
                </div>
            </div>
        )
    }
}






























