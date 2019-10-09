import React from 'react'
import { Input } from 'antd'
import BrushAccount from "../../../common/select/brushAccount";
import FundsTypeList from '../../../common/select/fundsTypeList';

const { TextArea } = Input;

export default class BrushModal extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            ftype: '0',
            virtualMoney:'',
            accountType: '',
            type: ''
        };

        this.handleInputChange = this.handleInputChange.bind(this)
    }

    componentDidMount(){
        const {ftype,virtualMoney,accountType,type  } = this.props.item;
        this.setState({
            ftype: ftype+'',
            virtualMoney,
            accountType: accountType+ '',
            type: type
        });
    }

    componentWillReceiveProps(nextProps){
        const {ftype,virtualMoney,accountType,type  } = nextProps.item;
        this.setState({
            ftype: ftype+'',
            virtualMoney,
            accountType: accountType+ '',
            type
        });
    }

    //输入时 input 设置到 satte
    handleInputChange=(event)=>{
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }
    handleMTypeChange=(val)=>{
        this.setState({
            accountType:val
        });
        this.props.handleMTypeChange(val);
    }
    selectFundsType=(val)=>{
        this.setState({
            ftype:val
        })
        this.props.selectFundsType(val);
    }
    render(){
        const { ftype,virtualMoney, accountType,type} = this.state;
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <BrushAccount col='2' type={type} accountType={accountType}  handleChange={this.handleMTypeChange} />
                </div>
                <div className="form-group">
                    <FundsTypeList title='资金类型' fundsType={ftype} handleChange={this.selectFundsType} />
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">虚拟资金：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" name="virtualMoney" value={virtualMoney} onChange={this.handleInputChange}/>
                    </div>
                </div>
            </div> 
        )
    }
}































