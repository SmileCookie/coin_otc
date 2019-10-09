import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class BillTypeList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            arrList:this.props.paymod?[]:[<Option key='0' value=''>请选择</Option>],
        }
    }

    componentDidMount(){
        // axios.post(DOMAIN_VIP+'/futures/list').then(res => {
        //     const result = res.data;
        //     let arrList = this.state.arrList;
        //     if(result.code == 0){
        //          for(let i=0;i<result.list.length;i++){
        //             arrList.push(<Option key={result.list[i].futuresid} value={result.list[i].futuresid}>{result.list[i].futuresname}</Option>)
        //          }
        //          this.setState({
        //             arrList,
        //          }) 
        //     }
        // })


        
    }


    render(){
        const { arrList } = this.state
        return (
            <div className="form-group">
                <label className='col-sm-3 control-label' >{this.props.title?this.props.title:'账户类型'}：</label>
                <div className="col-sm-8">
                    <Select value={this.props.billType} style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        <Option key='' value={''}>请选择</Option>
                        <Option key='0' value={0}>交易奖励</Option>{/*value是暂时的 */}
                        <Option key='1' value={1}>交易买入</Option>
                        <Option key='2' value={2}>交易卖出</Option>
                        <Option key='55' value={55}>转出</Option>
                        <Option key='56' value={56}>转入</Option>
                        <Option key='3' value={3}>交易手续费</Option>
                        <Option key='4' value={4}>广告手续费</Option>
                    </Select>
                </div>
            </div>
        )
    }
}






























