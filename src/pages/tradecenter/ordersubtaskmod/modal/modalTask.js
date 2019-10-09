import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Select, Button } from 'antd'
const Option = Select.Option;

export default class ModalTask extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            modseqNum: '',
            modcycle: '',
            modtargetPrice: '',
            modadrS: '',
            modadrE:'',
            modtargetDiff:''
        }

    }

    componentDidMount() {
        const { item} = this.props
        console.log(item)
        let adrArr = item.adr ? item.adr.split('') : ['','','']
        console.log(adrArr)
        this.setState({
            modadrE:adrArr[2] || '',
            modadrS:adrArr[0] || '',
            modseqNum:item.seqNum || '',
            modcycle: item.cycle || '',
            modtargetPrice: item.targetPrice || '',
            modtargetDiff:item.targetDiff || ''
        })
    }

    componentWillReceiveProps(nextProps) {
        const { item } = nextProps
        let adrArr = item.adr ? item.adr.split('') : ['','','']
        this.setState({
            modadrE:adrArr[2] || '',
            modadrS:adrArr[0] || '',
            modseqNum:item.seqNum || '',
            modcycle: item.cycle || '',
            modtargetPrice: item.targetPrice || '',
            modtargetDiff:item.targetDiff || ''
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


    render() {
        const { modadrE,modadrS, modtargetPrice, modcycle, modseqNum,modtargetDiff } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                {this.props.create&&<div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">顺序：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control" name="modseqNum" value={modseqNum} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>}
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">周期(分钟)：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control" name="modcycle" value={modcycle} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                {this.props.create&&<div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">目标价格：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control" name="modtargetPrice" value={modtargetPrice} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>}
                {this.props.create&&<div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">涨跌比：</label>
                        <div className="col-sm-8">
                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="modadrS" value={modadrS} onChange={this.handleInputChange} /></div>
                            <div className="left line34">:</div>
                            <div className="col-sm-4 left sm-box"><input type="text" className="form-control" name="modadrE" value={modadrE} onChange={this.handleInputChange} /></div>
                        </div>
                    </div>
                </div>}
                {this.props.recover&&<div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">目标价差：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control" name="modtargetDiff" value={modtargetDiff} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>}
            </div>
        )
    }
}




























