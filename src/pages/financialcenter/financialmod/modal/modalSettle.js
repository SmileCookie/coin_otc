import React from 'react'
import MoadlSettleInfo from './modalSettleInfo'
import { DOMAIN_VIP } from '../../../../conf'
import { Button, Input, Modal } from 'antd'
const { TextArea } = Input;

export default class ModalSettle extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            item: {},
            memo: '',
            result: {},
            modalHtml: '',
            title: '',
            visible: false
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.watchDetail = this.watchDetail.bind(this)
    }
    componentDidMount() {
        this.setState({
            item: this.props.item,
            memo: this.props.textArea,
            result: this.props.result
        })
    }
    componentWillReceiveProps(nextProps) {
        this.setState({
            item: nextProps.item,
            memo: nextProps.textArea,
            result: nextProps.result
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

    //关闭弹窗
    handleCancel() {
        this.setState({
            visible: false
        })
    }
    //查看详情
    watchDetail(type, id) {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={this.handleCancel}>
                确定
            </Button>,
        ]
        let title = this.state.item.type == 3 ? '未结算提现记录列表' : '未结算充值记录列表'
        this.setState({
            visible: true,
            title: title,
            modalHtml: <MoadlSettleInfo
                operaType={this.state.item.type}
                detailsId={this.state.result.data.detailsBean.detailsid}
                fundType={this.state.item.fundtype}
                id={id}
                detailsusdteid={this.state.result.data.detailsBean.detailsusdteid}
            />
        })
    }

    render() {
        const result = this.props.result
        const { type, name, id, daytag, fundtype, fundTypeName, memo } = this.state.item;
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                {type != 3 && <div className="form-group">
                    <label className="col-sm-2 control-label">当前余额：</label>
                    <div className="col-sm-8">
                        <p className="line34">{result.data.finanaccount.amount}</p>
                    </div>
                </div>
                }
                {type == 3 && <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="col-md-6 col-sm-6 col-xs-6 padding_0">
                        <div className="form-group">
                            <label className="col-sm-2 control-label">当前钱包余额：</label>
                            <div className="col-sm-6">
                                <p className="line34">{result.data.finanaccount.amount}</p>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6 padding_0">
                        <div className="form-group">
                            <label className="col-sm-2 control-label">当前累积余额：</label>
                            <div className="col-sm-6">
                                <p className="line34">{result.data.finanaccount.curtotalamount}</p>
                            </div>
                        </div>
                    </div>
                </div>
                }
                {type != 3 && <div className="form-group">
                    <label className="col-sm-2 control-label">充值金额：</label>
                    <div className="col-sm-8">
                        <p className="line34">{result.data.detailsBean.amount}</p>
                    </div>
                </div>}
                {type == 3 && <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-2 control-label">提现成功金额：</label>
                        <div className="col-sm-8">
                            <p className="line34">{result.data.detailsBean.amount}</p>
                        </div>
                    </div>
                </div>
                }
                {type != 3 && <div className="form-group">
                    <label className="col-sm-2 control-label">上次金额：</label>
                    <div className="col-sm-8">
                        <p className="line34">{result.data.finanbalance.amount}</p>
                    </div>
                </div>}
                {type == 3 && <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="col-md-6 col-sm-6 col-xs-6 padding_0">
                        <div className="form-group">
                            <label className="col-sm-2 control-label">上次钱包金额：</label>
                            <div className="col-sm-8">
                                <p className="line34">{result.data.finanbalance.amount}</p>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6 padding_0">
                        <div className="form-group">
                            <label className="col-sm-2 control-label">上次累积金额：</label>
                            <div className="col-sm-8">
                                <p className="line34">{result.data.finanbalance.pertotalamount}</p>
                            </div>
                        </div>
                    </div>
                </div>
                }
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-2 control-label">结算提醒：</label>
                        <div className="col-sm-9">
                            {result.data.balanceFlag == 0 ? <span className="pad-right20 red">有问题</span> : <span className="pad-right20">无问题</span>}
                            <Button type="more" onClick={() => this.watchDetail(fundtype, id)}>查看详情</Button>
                        </div>
                    </div>
                </div>
                {type == 1 &&
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="list-box">
                            <p className="list-detail blue">1.期初余额（上次金额）+ 发生额（充值金额）= 期末余额（当前余额）。</p>
                            <p className="list-detail blue">2.充值金额不包含手工录入。</p>
                            <p className="list-detail blue">3.如有财务手工录入需要在备注中说明。</p>
                        </div>
                    </div>
                }
                {type == 3 &&
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="list-box">
                            <p className="list-detail blue">期初余额（上次累积金额）+ 发生额（提现成功金额）= 期末余额（当前累积金额）。</p>
                        </div>
                    </div>
                }
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-2 control-label">备注：</label>
                        <div className="col-sm-9 text-box">
                            <TextArea value={this.state.memo} name="memo" onChange={this.handleInputChange} rows={4} />
                        </div>
                    </div>
                </div>
                {this.state.visible && <Modal
                    visible={this.state.visible}
                    title={this.state.title}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width="80%"
                >
                    {this.state.modalHtml}
                </Modal>
                }
            </div>
        )
    }

}


























