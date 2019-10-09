
import React from 'react'
import axios from '../../../../utils/fetch'
import { SELECTWIDTH } from '../../../../conf'
import { message,Select } from 'antd'
const Option = Select.Option;

export default class HedgeContractTransactRecordModal extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            platformPlace:'BITMEX',
            platformHedge:'BITMEX',
            hedgeType:'MARKET',
        }
        this.handleChangeSelect = this.handleChangeSelect.bind(this)

    }

    componentDidMount(){
        console.log(this.props.item)
        if(this.props.item){
            const {platformHedge,platformPlace,hedgeType} = this.props.item
            this.setState({
                platformPlace,
                platformHedge,
                hedgeType
            })
        }
        
    }
    componentWillReceiveProps(nextProps){
        if(nextProps.item){
            const {platformHedge,platformPlace,hedgeType} = nextProps.item
            this.setState({
                platformPlace,
                platformHedge,
                hedgeType
            })
        }
    }
    //select选择框
    handleChangeSelect(val,type){
        if(type == 'hedgeType'){//下单类型
            this.setState({
                hedgeType:val
            })
        }else if(type == 'platformPlace'){
            this.setState({
                platformPlace:val
            })
        }else if(type == 'platformHedge'){
            this.setState({
                platformHedge:val
            })
        }
        this.props.handleChangeSelect(val,type)
    }



    render(){
        const { platformHedge,platformPlace,hedgeType} = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">

                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">对冲下单类型：</label>
                        <div className="col-sm-7">
                            <Select value={hedgeType} style={{ width: SELECTWIDTH }} onChange={(val)=>this.handleChangeSelect(val,'hedgeType')}>
                                <Option value={'MARKET'}>市价单</Option>
                                <Option value={'LIMIT'}>限价单</Option>
                            </Select>
                        </div> 
                    </div>
                </div> 

                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">下单平台：</label>
                        <div className="col-sm-7">
                            <Select value={platformPlace} style={{ width: SELECTWIDTH }} onChange={(val)=>this.handleChangeSelect(val,'platformPlace')}>
                                <Option value={'BITMEX'}>BITMEX</Option>
                                <Option value={'TDEX'}>TDEX</Option>
                            </Select>
                        </div> 
                    </div>
                </div> 

                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">对冲平台：</label>
                        <div className="col-sm-7">
                            <Select value={platformHedge} style={{ width: SELECTWIDTH }} onChange={(val)=>this.handleChangeSelect(val,'platformHedge')}>
                                <Option value={'BITMEX'}>BITMEX</Option>
                                <Option value={'TDEX'}>TDEX</Option>
                            </Select>
                        </div> 
                    </div>
                </div> 
            </div>
        )
    }
}
