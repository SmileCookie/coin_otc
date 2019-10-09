import React from 'react'
import { Input } from 'antd'
const { TextArea } = Input

  
export default class ModalForm extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            userName:'',
            remarks:''
        }
        this.handleInputChange = this.handleInputChange.bind(this)
    }

    componentDidMount(){
        const { userName,remarks,readOnly } = this.props
        this.setState({
            userName,
            remarks,
            readOnly
        })
    }

    componentWillReceiveProps(nextProps){
        const { userName,remarks,readOnly } = nextProps
        this.setState({
            userName,
            remarks,
            readOnly
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }

    render(){
        const { userName,remarks,readOnly } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group mbt20">
                    <label className="col-sm-4 control-label">角色名称：<i>*</i></label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="userName" value={userName} readOnly={readOnly} onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group mbt20">
                    <label className="col-sm-4 control-label">角色描述：</label>
                    <div className="col-sm-8">
                        <TextArea rows={4} className={readOnly?"bgeee":""} name="remarks" value={remarks} readOnly={readOnly} onChange={this.handleInputChange} />
                    </div>
                </div>
            </div>
        )
    }
}
























