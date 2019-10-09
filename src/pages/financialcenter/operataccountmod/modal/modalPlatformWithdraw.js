import React from 'react'
import qs from 'qs'
import { Input, Select, message } from 'antd'
import axios from '../../../../utils/fetch'
import { DOMAIN_VIP } from '../../../../conf'
import cookie from 'js-cookie'
import FeeTypeList from '../../../common/select/feeTypeList'
import FeeDirectionList from '../../../common/select/feeDirectionList'
import FundsTypeList from '../../../common/select/fundsTypeList'
import { toThousands } from '../../../../utils'
const { TextArea } = Input;

export default class ModalPlatformWithdraw extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            reaccname: this.props.userName,
            id: '',
            mcostdirection: 1,
            mdownloadamount: '',
            mdownloadaddress: '',
            tmp: '',
            mfeetype: '1',
            mfundstype: '0',
            availableAmount: 0
        }
    }

    componentDidMount() {
        const { userName, editItem } = this.props
        this.setState({
            reaccname: userName,
            mcostdirection: editItem && editItem.costdirection || 1,
            mdownloadamount: editItem && editItem.downloadamount || '',
            mdownloadaddress: editItem && editItem.downloadaddress || '',
            tmp: editItem && editItem.tmp || '',
            mfeetype: '1',
            mfundstype: '0',
            availableAmount: 0,
            mfundstypename: ''
        })

    }

    componentWillReceiveProps(nextProps) {
        const { userName, editItem } = nextProps
        this.setState({
            reaccname: userName,
            mcostdirection: editItem && editItem.costdirection || 1,
            mdownloadamount: editItem && editItem.downloadamount || '',
            mdownloadaddress: editItem && editItem.downloadaddress || '',
            tmp: editItem && editItem.tmp || '',
            mfeetype: '1',
            mfundstype: '0',
            availableAmount: 0,
            mfundstypename: '',
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
    //资金类型
    selectFundsType = v => {
        this.setState({
            mfundstype: v
        }, () => {
            this.requestMoney().then((availableAmount) => {
                this.props.mselectFundsType(v, availableAmount)
            })
        })
    }
    //费用类型
    selectFeeType = v => {
        this.setState({
            mfeetype: v
        })
        this.props.mselectFeeType(v)
        // this.props.selectReFeeType(v)
    }
    selectFeeDirect = v => {
        this.setState({
            mcostdirection: v
        })
        this.props.selectFeeDirect(v)
    }
    requestMoney = () => {
        const { mfeetype, mfundstype } = this.state
        let feetype = '';
        if (mfeetype == 2) {
            feetype = 2
        } else {
            feetype = 9
        }
        let availableAmount = 0;
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP + '/feeAccountCheck/balance', qs.stringify({
                feetype, type: mfundstype
            })).then(res => {
                const result = res.data;
                if (result.code == 0) {
                    try {
                        availableAmount = result.operationData.currentamount - result.operationData.freezeamount;
                        this.setState({
                            availableAmount,
                        })
                        resolve(availableAmount)
                    } catch (error) {
                        message.warning('错误' + error)
                        console.log(error)
                    }
                } else {
                    message.warning(result.msg);
                }
            })
        })
    }
    //资金类型
    handleSelect = (v, option) => {
        this.setState({
            mfundstype: v,
            mfundstypename: option
        }, () => {
            this.requestMoney().then((availableAmount) => {
                this.props.mselectFundsType(v, availableAmount, option + '其他')
            })
        })
    }
    render() {
        const { reaccname, id, mcostdirection, mdownloadaddress, mdownloadamount, tmp, mfeetype, mfundstype, availableAmount, mfundstypename } = this.state
        // console.log(this.props.item.feetype)
        let feeType
        switch (this.props.item ? this.props.item.feetype : this.props.mfeetype) {
            case 1:
                feeType = '平台手续费'
                break;
            case 2:
                feeType = '平台运营'
                break;
            case 3:
                feeType = '保险基金'
                break;
            case 9:
                feeType = '平台其他'
                break;
            default:
                feeType = '--'
                break;
        }
        let isHidden = this.props.isHidden ? this.props.isHidden : false
        console.log(this.props)
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-2 control-label">提现人：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={reaccname} name='reaccname' readOnly /><br /><br />
                        {this.props.item && <span>币种：{this.props.item.fundstypename}</span>}
                    </div>
                </div>
                {!this.props.item && <FundsTypeList fundsType={mfundstype} handleSelect={this.handleSelect} />}

                <div className="form-group">
                    <label className="col-sm-2 control-label">费用类型：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={feeType} name='mfeetype' readOnly /><br /><br />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">账户名称：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={this.props.item ? this.props.item.accname : mfundstypename && mfundstypename + '其他'} name='maccname' readOnly /><br /><br />
                    </div>
                </div>
                {/* // <FeeTypeList title='费用类型' showOption='true' col={2} feeType={mfeetype} handleChange={this.selectFeeType} /> */}

                <FeeDirectionList title='费用方向' col={2} feeDirection={mcostdirection} paymod={true} handleChange={this.selectFeeDirect} />
                <div className="form-group">
                    <label className="col-sm-2 control-label">提现金额:<i>*</i></label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" value={mdownloadamount} name="mdownloadamount" onChange={this.handleInputChange} />
                        {isHidden?"":<span>
                            可用：
                            <span className='moneyGreen' style={{ margin: '0 5px' }}>
                                {toThousands(this.props.item ? this.props.item.availableAmount : availableAmount)}
                            </span>
                            {this.props.item ? this.props.item.fundstypename : mfundstypename}
                        </span>}
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">提现地址：<i>*</i></label>
                    <div className="col-sm-8 text-box">
                        <TextArea onChange={this.handleInputChange} value={mdownloadaddress} name="mdownloadaddress" rows={4} />
                        <p className="blank-spacing"></p>
                    </div>
                </div>
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































