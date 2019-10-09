

import React from 'react'
import { Input } from 'antd'
const { TextArea } = Input

export default class ModalMemo extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            modifyTextArea:''
        }

        this.handleInputChange = this.handleInputChange.bind(this)
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
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.onChange(event)
    }

    render(){
        const { modifyTextArea } = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-3 control-label">备注：</label>
                    <div className="col-sm-9 text-box">
                        <TextArea name="modifyTextArea" rows={4} value={modifyTextArea} onChange={this.handleInputChange} />
                    </div>
                </div>
            </div>
        )
    }
}



























