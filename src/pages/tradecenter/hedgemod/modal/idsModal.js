import React from 'react'
import moment from 'moment'
import 'moment/locale/zh-cn';
import {TIMEFORMAT } from '../../../../conf/index';
moment.locale('zh-cn');
import { toThousands } from '../../../../utils'

export default class IdsModal extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            remainingAmount:''
        }
    }
    componentDidMount(){
        console.log(this.props)
        const { remainingAmount } = this.props.item
        this.setState({
            remainingAmount:toThousands(remainingAmount,true)
        })
    }
    componentWillReceiveProps(nextProps){
        console.log(nextProps)
        const { remainingAmount } = nextProps.item
        this.setState({
            remainingAmount:toThousands(remainingAmount,true)
        })
    }
    //输入时 input 设置到 satte
    handleInputChange = (event) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }
    render(){
        const{remainingAmount} = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12 gbcmodal">
            <div className="col-md-8 col-sm-8 col-xs-8">
                <div className="form-group">
                    <label className="col-sm-5 control-label">重新下单数量：</label>
                    <div className="col-sm-7">
                        <input type="text" className="form-control" name="remainingAmount" value={remainingAmount}  onChange={this.handleInputChange} />
                    </div>
                </div>
            </div>
        </div>
            )
    }
}