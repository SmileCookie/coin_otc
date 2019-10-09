import React from 'react'
import { Input} from 'antd'

export default class ModalUpdate extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            id:'',
            pvCount:0,
            uvCount:0,
            accessingIp:'',
        }
        this.handleInputChange = this.handleInputChange.bind(this)
    }

    componentDidMount(){
        this.setState({
            id:this.props.id,
            pvCount: this.props.pvCount,
            uvCount: this.props.uvCount,
            accessingIp: this.props.accessingIp
        })
    }

    componentWillReceiveProps(nextProps){
        this.setState({
            id:nextProps.id,
            pvCount: nextProps.pvCount,
            uvCount: nextProps.uvCount,
            accessingIp: nextProps.accessingIp
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
        this.props.handleInputChange(event);
    }

    render(){
        const {id,pvCount,uvCount,accessingIp} = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-3 control-label">PV：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control" name="pvCount" value={pvCount} onChange={this.handleInputChange} />
                    </div>
                </div> 
                <div className="form-group">
                    <label className="col-sm-3 control-label">UV：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control" name="uvCount" value={uvCount} onChange={this.handleInputChange} />
                    </div>
                </div> 
                <div className="form-group">
                    <label className="col-sm-3 control-label">访问IP：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control" name="accessingIp" value={accessingIp} onChange={this.handleInputChange} />
                    </div>
                </div> 
            </div>
        )
    }
}





















































