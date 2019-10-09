import React from 'react'
import {Input} from 'antd'
export default class ModalUpdate extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            id:'',
            bcUserId:'',
            bcFrequency:'',
            feeRatio:'',
            baseBalance:'',
            luckyUserId:'',
            withdrawFrequency:'',
            withdrawAddress:'',
            webUrl:''
        }
        this.handleInputChange = this.handleInputChange.bind(this)
    }
    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(name,value)
    }
    componentDidMount(){
        const{id,bcUserId,bcFrequency,feeRatio,baseBalance,luckyUserId,withdrawFrequency,withdrawAddress,webUrl}=this.props.item
        this.setState({
            id,
            bcUserId,
            bcFrequency,
            feeRatio,
            baseBalance,
            luckyUserId,
            withdrawFrequency,
            withdrawAddress,
            webUrl
        })     
    }
    componentWillReceiveProps(nextProps){
        const{id,bcUserId,bcFrequency,feeRatio,baseBalance,luckyUserId,withdrawFrequency,withdrawAddress,webUrl}=nextProps.item
        this.setState({
            id,
            bcUserId,
            bcFrequency,
            feeRatio,
            baseBalance,
            luckyUserId,
            withdrawFrequency,
            withdrawAddress,
            webUrl
        })  
    }

    render(){
        const {bcUserId,bcFrequency,feeRatio,baseBalance,luckyUserId,withdrawFrequency,withdrawAddress,webUrl} = this.state
        return (
            <div>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">回购账号id：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="bcUserId" value={bcUserId||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                     <div className="form-group">
                        <label className="col-sm-3 control-label">回购执行频率：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="bcFrequency" value={bcFrequency||''} onChange={this.handleInputChange}/>
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">手续费比例：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="feeRatio" value={feeRatio==0?'50%':'100%'||''} onChange={this.handleInputChange} disabled={true}/>
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">打底金额usdc：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="baseBalance" value={baseBalance||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">抽奖账号id：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="luckyUserId" value={luckyUserId||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">回购转出频率：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="withdrawFrequency" value={withdrawFrequency||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">分红地址：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="withdrawAddress" value={withdrawAddress||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">分红地址查看链接：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="webUrl" value={webUrl||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
            </div>
        )
    }
}