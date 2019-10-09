import React from 'react'
import {Radio} from 'antd'
const RadioGroup = Radio.Group;
export default class ModalRatio extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            feeRatio:0
        }
        this.onChangeRatio = this.onChangeRatio.bind(this)
    }
    onChangeRatio(e){
        this.setState({
            feeRatio:e.target.value,
        })
        this.props.onChangeRatio(e)
    }
    componentDidMount(){
        this.setState({
            feeRatio:this.props.feeRatio  
        })     
    }
    componentWillReceiveProps(nextProps){
        this.setState({
           feeRatio:nextProps.feeRatio 
        })
        
    }

    render(){
        const {feeRatio} = this.state
        return (
                    <div className="form-group">
                        <label className="col-sm-3 control-label">手续费比例：</label>
                        <div className="col-sm-8">
                        <RadioGroup onChange={this.onChangeRatio} value={feeRatio}>
                                <Radio value={0}>50%</Radio>
                                <Radio value={1}>100%</Radio>
                            </RadioGroup>
                        </div>
                     </div>
        )
    }
}