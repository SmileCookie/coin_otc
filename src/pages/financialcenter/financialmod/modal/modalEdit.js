import React from 'react'
import { Input } from 'antd'
const { TextArea } = Input;

export default class ModalEdit extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            name:'',
            fundTypeName:'',
            typeStr:'',
            modifyAmount:'',
            modifyMemo:''
        }

        this.handleInputChange = this.handleInputChange.bind(this)
    }

    componentDidMount(){
        const { modifyItem } = this.props 
        this.setState({
            name:modifyItem.name,
            fundTypeName:modifyItem.fundTypeName,
            typeStr:modifyItem.typeStr,
            modifyAmount:modifyItem.amount,
            modifyMemo:modifyItem.memo
        })
    }

    componentWillReceiveProps(nextProps){
        const { modifyItem } = nextProps 
        this.setState({
            name:modifyItem.name,
            fundTypeName:modifyItem.fundTypeName,
            typeStr:modifyItem.typeStr,
            modifyAmount:modifyItem.amount,
            modifyMemo:modifyItem.memo
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
        this.props.handleInputChange(event)
    }

    render(){
        const { name,fundTypeName,typeStr,modifyAmount,modifyMemo } = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-2 control-label">账户名称：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={name} readOnly />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">资金类型：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={fundTypeName} readOnly />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">账户类型：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={typeStr} readOnly />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">余额：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={modifyAmount} name="modifyAmount" onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">备注：</label>
                    <div className="col-sm-8 text-box">
                        <TextArea onChange={this.handleInputChange} value={modifyMemo} name="modifyMemo" rows={4} />
                    </div>
                </div>
            </div> 
        )
    }
}































