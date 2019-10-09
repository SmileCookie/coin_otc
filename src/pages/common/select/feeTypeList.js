import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class FeeTypeList extends React.Component{

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
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-3 control-label'} >{this.props.title?this.props.title:'费用类型'}：</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-8"}>
                    <Select value={this.props.feeType} style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {
                            (() => {
                                switch(this.props.showOption){
                                    case '平台手续费':
                                        return    <Option key='1' value={'1'}>平台手续费</Option>
                                    case 'true':
                                        return    [
                                                <Option key='1' value={'1'}>平台手续费</Option>,
                                                <Option key='2' value={'2'}>平台运营</Option>,
                                                <Option key='3' value={'3'}>平台其他</Option>]
                                    default:
                                        return    [<Option key='' value=''>请选择</Option>,
                                                <Option key='1' value={'1'}>平台手续费</Option>,
                                                <Option key='2' value={'2'}>平台运营</Option>,
                                                <Option key='3' value={'3'}>平台其他</Option>]
                                }
                            })()
                        }
                    </Select>
                </div>
            </div>
        )
    }
}






























