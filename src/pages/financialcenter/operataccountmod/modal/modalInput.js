import React,{Component} from 'react';
import { Table,Modal,message,Input } from 'antd'
const { TextArea } = Input;
class ModalInput extends Component {
    constructor(props){
        super(props)
        this.state={
            tmp:''
        }
    }
    
    handleInputChange = event =>{
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }
    componentWillReceiveProps(nextprops){
        this.setState({
            tmp:''
        })
    }
    render(){
        const {tmp} = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-2 control-label">备注：<i>*</i></label>
                        <div className="col-sm-8 text-box">
                            <TextArea onChange={this.handleInputChange} value={tmp} name="tmp" rows={4} />
                        </div>
                </div>
            </div>
        )
    }
}
export default ModalInput