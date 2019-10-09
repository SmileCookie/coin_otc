import React from 'react'
import { Input } from 'antd'
const { TextArea } = Input;


export default class BlowRemarksModal extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            modifyTextArea:''
        }
    }

    componentDidMount(){
        this.setState({
            modifyTextArea:this.props.memo
        })
    }

    componentWillReceiveProps(nextProps){
        this.setState({
            modifyTextArea:nextProps.memo
        })
    }

    //输入时 input 设置到 satte
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }
    
    render(){
        const {modifyTextArea} = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-2 control-label">备注：</label>
                    <div className="col-sm-8">
                        <TextArea name="modifyTextArea" value={modifyTextArea} rows={4} onChange={this.handleInputChange} />
                    </div>
                </div>
            </div>
        )
    }
}