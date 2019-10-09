import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class AccountNameList extends React.Component{

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
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-3 control-label'} >{this.props.title?this.props.title:'账户名称'}：</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-8"}>
                    <Select value={this.props.accName} style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        <Option key='' value={''}>请选择</Option>
                        {/* <Option key='1' value={410002}>期货交易手续费</Option>
                        <Option key='2' value={210002}>币币交易手续费</Option>
                        <Option key='3' value={310002}>OTC交易手续费</Option>
                        <Option key='4' value={110002}>提现手续费</Option>
                        <Option key='5' value={3020}>保险留存</Option> */}
                        {/* <Option key='1' value={'期货交易手续费'}>期货交易手续费</Option> */}
                        <Option key='2' value={'币币交易手续费'}>币币交易手续费</Option>
                        {/* <Option key='3' value={'OTC交易手续费'}>OTC交易手续费</Option> */}
                        <Option key='4' value={'钱包提现手续费'}>钱包提现手续费</Option>
                        {/* <Option key='5' value={'期货保险留存'}>期货保险留存</Option> */}
                    </Select>
                </div>
            </div>
        )
    }
}






























