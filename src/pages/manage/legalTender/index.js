import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { connect } from 'react-redux'
import { Link, browserHistory } from 'react-router';
import Transfer from '../transfer'
import '../../../assets/css/check.less'
import ReactModal from '../../../components/popBox/index';
import './legalTender.less'
const BigNumber = require('big.js')

const tableHeader = [
    {
        name: <FormattedMessage id="otc币种" />
    }, {
        name: <FormattedMessage id="otc全称" />
    }, {
        name: <FormattedMessage id="otc总额" />
    }, {
        name: <FormattedMessage id="otc可用余额" />
    }, {
        name: <FormattedMessage id="otc冻结资金" />
    }, {
        name: "人民币估值"
    }, {
        name: <FormattedMessage id="otc操作" />
    }
],
    coinList = [
        {
            coin: "USDT",
            fullName: "Litecoin",
            totalPrice: "100.00000000",
            available: "100.00000000",
            onHold: "0.00000000",
            valuation: "11930.00",
        }, {
            coin: "ETC",
            fullName: "Ethereum Classic",
            totalPrice: "100.00000000",
            available: "100.00000000",
            onHold: "0.00000000",
            valuation: "100.00",
        }, {
            coin: "USDT",
            fullName: "ZCash",
            totalPrice: "100.00000000",
            available: "100.00000000",
            onHold: "0.00000000",
            valuation: "11930.00",
        }, {
            coin: "ETC",
            fullName: "Litecoin",
            totalPrice: "100.00000000",
            available: "100.00000000",
            onHold: "0.00000000",
            valuation: "100.00",
        }, {
            coin: "USDT",
            fullName: "Ethereum Classic",
            totalPrice: "100.00000000",
            available: "100.00000000",
            onHold: "0.00000000",
            valuation: "11930.00",
        }, {
            coin: "ETC",
            fullName: "Ethereum Classic",
            totalPrice: "100.00000000",
            available: "100.00000000",
            onHold: "0.00000000",
            valuation: "100.00",
        },
    ]

class TabSwitchLegalTender extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            filterVal: '',
            isHideZerobalance: false,
            clickNum: 0,
            sortName: '',
            coinList: [],
            availableDownload: 0,
            downloadLimit: 0,
            btnStus: 0,
            bordeBlue: false,
            Mstr: '',
            fromselectCode: 1,
            selectItem: {},
            toselectCode: 1,
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.touchSortReacrd = this.touchSortReacrd.bind(this)
        this.changeFocus = this.changeFocus.bind(this)
        this.changeBlur = this.changeBlur.bind(this)
        this.setZerobalance = this.setZerobalance.bind(this)
        this.detailRefresh = this.detailRefresh.bind(this)
    }
    //点击 th 事件
    touchSortReacrd(name) {
        let { clickNum, sortName } = this.state
        let newClickNum,
            targetName = name
        if (targetName == sortName || !sortName) {
            newClickNum = ++clickNum
            if (newClickNum > 2) {
                newClickNum = 0
            }
        } else {
            newClickNum = 1
        }
        this.setState({
            sortName: targetName,
            clickNum: newClickNum
        })
    }
    setZerobalance() {
        this.setState({
            isHideZerobalance: !this.state.isHideZerobalance
        })

    }
    //点击 th 排序
    sortRecord(data) {
        const { clickNum, sortName } = this.state
        let sortData = [];
        let clickSortName = sortName;
        let sortABC = function (a, b) {
            return a[clickSortName].toLowerCase() < b[clickSortName].toLowerCase() ? -1 : 1;
        }
        let sortNumber2 = function (a, b) {
            return a[clickSortName] - b[clickSortName];
        }
        if (clickSortName && clickNum !== 0) {
            if (clickNum == 2) {
                if (sortName == 'propTag' || sortName == 'coinFullName') {
                    sortData = data.sort(sortABC)
                } else {
                    sortData = data.sort(sortNumber2)
                }
            } else if (clickNum == 1) {
                if (sortName == 'propTag' || sortName == 'coinFullName') {
                    sortData = data.sort(sortABC).reverse();
                } else {
                    sortData = data.sort(sortNumber2).reverse()
                }
            }
            return sortData
        } else {
            return data;
        }
    }
    turnoverModal(fundsType) {
        //此方法为划转详情，引用/pages/manage/transfer/index
        this.detailRefresh(fundsType);
        this.modal.openModal();

    }
    detailRefresh(fundsType) {
        this.setState({ Mstr: <Transfer closeModal={this.modal.closeModal} fromtype={1} totype={2} fundsType={fundsType} isFiatAccount={true} /> })
    }
    handleInputChange() { }
    //搜索框边框
    changeFocus() {
        this.setState({
            bordeBlue: true
        })
    }
    changeBlur() {
        this.setState({
            bordeBlue: false
        })
    }
    render() {
        console.log(this.state.Mstr);
        const { bordeBlue, isHideZerobalance, sortName, clickNum, availableDownload, downloadLimit, authResult, btnStus } = this.state
        const { formatMessage } = this.props.intl;
        return (
            <div className="legal-tender">
                <div className="assets-detail">
                    <div className="assets-detail-right">
                        <div className="assets-detail-value">
                            <b><FormattedMessage id="balance.text3" /></b>
                            <div className="val-con">
                                <i id="cerrencyUnit">￥</i> <span id="cerrencyTotal">22522946782.86</span> / <em id="totalBtc">8159672.5855190</em> BTC
                                </div>
                        </div>

                    </div>
                    <div className="assets-detail-left left">
                        <div className={bordeBlue ? "input-box borde-blue" : 'input-box'}>
                            <input type="text" name="filterVal" ref={(input) => { this.handleInputChange = input; }} placeholder={"输入币种进行搜索"} />
                            <button onClick={btnStus == 1 ? this.clearFilterVal : null} className={btnStus == 0 ? "iconfont icon-search-bizhong" : "iconfont icon-shanchu-moren"}></button>
                        </div>
                        <label htmlFor="hideCoin">
                            <div className={`${isHideZerobalance ? "bg-white" : ""} checkboxitem`}>
                                <i className={isHideZerobalance ? "iconfont icon-xuanze-yixuan" : "iconfont icon-xuanze-weixuan "} onClick={this.setZerobalance}></i>
                            </div>
                            <FormattedMessage id="balance.text2" />
                        </label>

                    </div>
                    <div className="history_list_right">
                        <div className="history_list_right">
                            <Link to="/bw/manage/account/capitalFrozen">
                                <div className="history_list">
                                    冻结记录
                            </div>
                            </Link>
                        </div>
                        <div className="history_list_right recordBtn">
                            <Link to="/bw/manage/account/capitalRecord">
                                <div className="history_list">
                                    <FormattedMessage id="otc资金记录" />
                                </div>
                            </Link>
                        </div>
                    </div>
                </div>
                <div className="data-sheets">
                    <table width="100%" className="norm-table table-account">
                        <thead id="tableSort">
                            <tr>
                                {
                                    tableHeader.map((item, index) => {
                                        return <th key={index} onClick={() => item.sortName && this.touchSortReacrd(item.sortName)}>
                                            <span className="more-box">
                                                {item.sortName == 'valuation' && <FormattedMessage id={this.props.moneylogo.name} />}
                                                {item.name}
                                                <i className={sortName == item.sortName && clickNum == 1 ? "show" : sortName == item.sortName && clickNum == 2 ? "show more" : ""}></i>
                                            </span>
                                        </th>
                                    })
                                }
                            </tr>
                        </thead>
                        <tbody id="fundsDetail" className="bgtbody">
                            {coinList.map((item, index) => {
                                return (
                                    <tr key={index} className="border_left border_right border_bottom">
                                        <td>{item.coin}</td>
                                        <td className="text-left">{item.fullName}</td>
                                        <td className="text-right">{item.totalPrice}</td>
                                        <td className="text-right">{item.available}</td>
                                        <td className="text-right">{item.onHold}</td>
                                        <td className="text-right">{item.valuation}</td>
                                        <td className="text-center capitalTableDetail f-14 coFont10">
                                            <button onClick={this.turnoverModal.bind(this, item.coin)}><FormattedMessage id="otc划转" /></button>
                                        </td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </table>
                </div>
                <ReactModal ref={modal => this.modal = modal} >
                    {this.state.Mstr}
                </ReactModal>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        user: state.session.user,
    }
};
const mapDispatchToProps = {

};

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(TabSwitchLegalTender));