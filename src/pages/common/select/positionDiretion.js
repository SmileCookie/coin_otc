import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf/index'
const Option = Select.Option

export default class PositionDiretion extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            sideArr:this.props.paymod?[]:[<Option key='0' value=''>请选择</Option>],
        }
    }

    componentDidMount(){
        // axios.post(DOMAIN_VIP+'/futures/list').then(res => {
        //     const result = res.data;
        //     let sideArr = this.state.sideArr;
        //     if(result.code == 0){
        //          for(let i=0;i<result.list.length;i++){
        //             sideArr.push(<Option key={result.list[i].futuresid} value={result.list[i].futuresid}>{result.list[i].futuresname}</Option>)
        //          }
        //          this.setState({
        //             sideArr,
        //          }) 
        //     }
        // })


        
    }


    render(){
        const { sideArr } = this.state
        return (
            <div className="form-group">
                <label className='col-sm-3 control-label' >{this.props.title}：</label>
                <div className="col-sm-8">
                    <Select showSearch value={this.props.sideType} filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        <Option key='' value={''}>请选择</Option>
                        <Option key='0' value={0}>做空</Option>
                        <Option key='1' value={1}>做多</Option>
                    </Select>
                </div>
            </div>
        )
    }
}






























