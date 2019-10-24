import React from 'react';
import Select from 'react-select';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Link, browserHistory } from 'react-router';
import './mainView.less'
import Pages from '../../../components/pages';
import ReactModal from '../../../components/popBox/index';
import Transfer from '../../manage/transfer/index';

const tableHeader = [
    {
        name: <FormattedMessage id="otc币种" />
    }, {
        name: <FormattedMessage id="otc全称" />
    }, {
        name: <FormattedMessage id="otc类型" />
    }, {
        name: <FormattedMessage id="otc时间" />
    }, {
        name: <FormattedMessage id="otc交易金额" />
    }, {
        name: <FormattedMessage id="otc总额" />
    }, {
        name: <FormattedMessage id="otc操作" />
    }
], coinList = [
    {
        coin: "USDT",
        fullName: "Litecoin",
        type: "我的钱包划入",
        time: "2018-7-9 10:22:30",
        totalPrice: "0.00000000",
        rental: "11930.00",
    },{
        coin: "USDT",
        fullName: "Litecoin",
        type: "购买",
        time: "2018-7-9 10:22:30",
        totalPrice: "0.00000000",
        rental: "11930.00",
    },{
        coin: "USDT",
        fullName: "Litecoin",
        type: "我的钱包划入",
        time: "2018-7-9 10:22:30",
        totalPrice: "0.00000000",
        rental: "11930.00",
    },{
        coin: "USDT",
        fullName: "Litecoin",
        type: "购买",
        time: "2018-7-9 10:22:30",
        totalPrice: "0.00000000",
        rental: "11930.00",
    },
];
class OctCapitalFrozen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            screenTimeType: 0,
            selectedOption: "all",
            modalStr: ""
        }

        this.handleChangeTime = this.handleChangeTime.bind(this);
        this.handleChangeSelect = this.handleChangeSelect.bind(this);
        this.recordDetails = this.recordDetails.bind(this);
        
    }

    handleChangeTime(id) {
        this.setState({
            screenTimeType: id
        })
    }

    handleChangeSelect(id) {
        this.setState({
            selectedOption: id
        })
    }
    turnoverModal(fundsType) {
        //此方法为划转详情，引用/pages/manage/transfer/index
        this.detailRefresh(fundsType);
        this.modal.openModal();

    }
    detailRefresh(fundsType) {
        this.setState({ modalStr: <Transfer closeModal={this.modal.closeModal} fromtype={1} totype={2} fundsType={fundsType} /> })
    }
    recordDetails() {
        let template = (<div className="copitalModal coFont11">
            <div className="copitalModalTitle bg-pop-title">
                <h5 className="f-18">
                    <FormattedMessage id="otc资金记录详情" />
                </h5>
                <i onClick={() => this.modal.closeModal()} className="close iconfont icon-guanbi-moren coFont12 f-14 divHand"></i>
            </div>

            <ul className="copitalModalList coFont11 f-14">
            <li>
                <span><FormattedMessage id="otc币种" /></span>
                <span>BTC</span>
            </li>
            <li>
                <span><FormattedMessage id="otc交易金额" /></span>
                <span className="coFont10">+0.226600BTC</span>
            </li>
            <hr />
            <li>
                <span><FormattedMessage id="otc类型" /></span>
                <span>钱包划入</span>
            </li>
            <li>
                <span><FormattedMessage id="otc状态" /></span>
                <span>交易成功</span>
            </li>
            <li>
                <span><FormattedMessage id="otc总额" /></span>
                <span>6.000000BTC</span>
            </li>
            <li>
                <span><FormattedMessage id="otc手续费" /></span>
                <span>0.050000BTC</span>
            </li>
            <li>
                <span><FormattedMessage id="otc订单编号" /></span>
                <span>200111844</span>
            </li>
            <li>
                <span><FormattedMessage id="otc时间" /></span>
                <span>2017-9-8 10:22:32</span>
            </li>
        </ul>

        </div>);


        this.setState({
            modalStr: template
        });

        this.modal.openModal();
    }

    render() {
        const { selectedOption, screenTimeType, modalStr } = this.state;
        return (
            <div className="dealContent coFont1 f-14 coFont1 capitalDealContent">
                <h2 className="capitalTitle"><FormattedMessage id="otc资金记录" /></h2>
                <div className="capitalContent">
                    <div className="capitalActions">
                        <div className="capitalActionsCurrency select-new">
                            <h5 className="CurrencyType">
                                <FormattedMessage id="otc币种" />:
                            </h5>
                            <div className="record-head CurrencySelect">
                                <Select
                                    value={selectedOption}
                                    clearable={false}
                                    searchable={false}
                                    onChange={this.handleChangeSelect}
                                    options={[
                                        { value: 'all', label: '全部' },
                                        { value: 'btc', label: 'BTC' },
                                        { value: 'eos', label: 'EOS' },
                                        { value: 'eth', label: 'ETH' }
                                    ]}
                                />
                            </div>
                        </div>

                        <div className="capitalActionsTime">
                            <h5 className="TimeTitle">
                                <FormattedMessage id="otc时间" />:
                            </h5>
                            <ul className="tab-time">
                                <li>
                                    <label className={screenTimeType == 0 ? "iconfont icon-danxuan-yixuan" : 'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(0)}></label>
                                    <span> <FormattedMessage id="otc全部" /></span>
                                </li>
                                <li>
                                    <label className={screenTimeType == 1 ? "iconfont icon-danxuan-yixuan" : 'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(1)}></label>
                                    <span> <FormattedMessage id="otc7天内" /></span>
                                </li>
                                <li>
                                    <label className={screenTimeType == 2 ? "iconfont icon-danxuan-yixuan" : 'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(2)}></label>
                                    <span> <FormattedMessage id="otc15天内" /></span>
                                </li>
                                <li>
                                    <label className={screenTimeType == 3 ? "iconfont icon-danxuan-yixuan" : 'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(3)}></label>
                                    <span> <FormattedMessage id="otc30天内" /></span>
                                </li>
                            </ul>

                        </div>
                        <div className="capitalExportButton">
                            <div className="history_list_right">
                                <Link to="/"><div className="history_list"><FormattedMessage id="otc导出资金记录" /></div></Link>
                            </div>
                        </div>
                    </div>
                    <div className="entrust-con bk-entrust bk_table">
                        <table className="table-entrust">
                            <thead>
                                <tr className="border_left border_right border_bottom">
                                    {
                                        tableHeader.map((item, index) => {
                                            return <th key={index}>
                                                {item.name}
                                            </th>
                                        })
                                    }
                                </tr>
                            </thead>
                            <tbody>
                                {coinList.map((item, index) => {
                                    return (
                                        <tr key={index} className="border_left border_right border_bottom">
                                            <td>{item.coin}</td>
                                            <td>{item.fullName}</td>
                                            <td>{item.type}</td>
                                            <td>{item.time}</td>
                                            <td>{item.totalPrice}</td>
                                            <td>{item.rental}</td>
                                            <td className="capitalTableDetail f-14 coFont10">
                                                <button onClick={this.recordDetails}><FormattedMessage id="otc查看详情" /></button>
                                            </td>
                                        </tr>
                                    )
                                })}
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className="tablist">
                    <Pages {...this.props} total={9} pagesize={9} pageIndex={1} />
                </div>
                <ReactModal ref={modal => this.modal = modal}>
                    {modalStr}
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

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(OctCapitalFrozen));
