import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, SELECTWIDTH } from 'Conf'
import { Tabs, Button, Modal, Select } from 'antd'
import { toThousands, dateToFormat } from 'Utils'
import { AsyncSelect, SeOp } from "../../../components/select/asyncSelect";
import ModalAddEditMarket from './modal/modalAddEditMarket'
const { TabPane } = Tabs;
const Option = Select.Option
const _userType = {
    1: '普通用户',
    2: '广告商家'
}

/**
 * Date 2019-07-25 上版本的 市场配置
 * 不要删除不要删除不要删除！！！
 */


@Decorator()
export default class MarketConfig extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            market: '',
            type: ''
        }
        this.state = {
            tk: '1',
            modalHtml: '',
            marketsList: [],
            modalTitle: '',
            ...this.defaultState,
        }
    }
    async componentDidMount() {
        let arr = [<Option key='0' value=''>请选择</Option>]
        const rs = await this.request({ url: '/sys/market/getMarketName', type: 'post' })
        for (let i = 0; i < rs.length; i++) {
            arr.push(<Option key={i + 1} value={rs[i]}>{rs[i].toUpperCase()}</Option>)
        }
        await this.setState({
            marketsList: arr
        })
        this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, market, tk, type } = this.state
        let url = {
            1: '/userConfig/query',
            2: '/marketConfig/query'
        }
        let params = {
            market, type: Number(type) || -1,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: url[tk], type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })
    }
    updStatus = ({id,enable}) => {
        const {tk} = this.state
        let urls = {
            1:'/userConfig/stop',
            2:'/marketConfig/stop'
        }
        Modal.confirm({
            title:`你确定要${enable == 1 ?'停用':'开启'}吗？`,
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk:async () => {
                await this.request({url:urls[tk],type:'post'},{id})
                this.requestTable()
            },
            onCancel(){
                console.log('Cancel')
            }
        })
    }
    createColumns = (pageIndex, pageSize) => {
        if (this.state.tk == 1) {
            return [
                { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
                { title: 'z', dataIndex: 'type', render: t => _userType[t] || '--' },
                { title: '用户数量', dataIndex: 'userCount', render: text => toThousands(text, true) },
                { title: '同时上架广告数', dataIndex: 'userAdNumMax', render: text => toThousands(text, true) },
                { title: '同时进行订单数', dataIndex: 'userOrderNumMax', },
                { title: '累计取消次数', dataIndex: 'userCancleTimes', },
                { title: '累计申诉次数', dataIndex: 'complainTimes', },
                { title: '指标显示周期', dataIndex: 'displayCycle', },
                { title: '提醒间隔时间', dataIndex: 'intervalTime', },
                { title: '保证金', dataIndex: 'depositNum', render: text => toThousands(text, true) },
                { title: '状态', dataIndex: 'enable', render: text => text == 1 ? '开启' : '停用' },
                {
                    title: '操作', render: (record) => <span>
                        <a href="javascript:void(0)" className="mar10" onClick={() => this.addEdit(1, record)}>修改</a>
                        <a href="javascript:void(0)" className="mar10" onClick={() => this.updStatus(record)}>{record.enable == 1 ? '停用' : '开启'}</a>
                    </span>
                },
            ]
        }
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '市场全称', dataIndex: 'market', },
            { title: '用户类型', dataIndex: 'type', render: t => _userType[t] || '--' },
            { title: '手续费-购买广告', dataIndex: 'adBuyFee', render: text => toThousands(text, true) },
            { title: '手续费-出售广告', dataIndex: 'adSellFee', render: text => toThousands(text, true) },
            { title: '广告有效期', dataIndex: 'adValidTime', },
            { title: '订单时效', dataIndex: 'orderOverTime', },
            { title: '出售限额', dataIndex: 'sellMaxNum', },
            { title: '买入限额', dataIndex: 'buyMaxNum', },
            { title: '虚拟币位数', dataIndex: 'coinBixDian', render: text => toThousands(text, true) },
            { title: '法币位数', dataIndex: 'legalBixDian', render: text => toThousands(text, true) },
            { title: '状态', dataIndex: 'enable', render: text => text == 1 ? '开启' : '不开启' },
            {
                title: '操作', render: (record) => <span>
                    <a href="javascript:void(0)" className="mar10" onClick={() => this.addEdit(2, record)}>修改</a>
                    <a href="javascript:void(0)" className="mar10" onClick={() => this.updStatus(record.id)}>{record.enable == 1 ? '停用' : '开启'}</a>
                </span>
            },
        ]
    }
    tcb = async tk => {
        await this.setState({ tk })
        this.requestTable()
    }
    addEdit = (type, item = null) => {
        const { marketsList } = this.state
        this.setState({
            modalTitle: `修改/新增${type == 1 ? '用户' : '市场'}限制`,
            modalVisible: true,
            modalHtml: <ModalAddEditMarket _tk={this.state.tk} requestTable={this.requestTable} item={item} marketsList={marketsList} _type={type} _userType={_userType} handleCancel={this.handleCancel} />
        })
    }
    handleCancel = () => {
        this.setState({ modalVisible: false })
    }
    render() {
        const { showHide, pageTotal, pageIndex, pageSize, dataSource, tk, modalHtml, modalVisible, marketsList, market, type } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">市场名称：</label>
                                        <div className="col-sm-9">
                                            <Select value={market} style={{ width: SELECTWIDTH }} onChange={v => this.onSelectChoose(v, 'market')} >
                                                {marketsList}
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <SeOp title='用户类型' value={type} onSelectChoose={v => this.onSelectChoose(v, 'type')} ops={_userType} pleaseC={true} />
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        <Button type="primary" onClick={() => this.addEdit(1)}>新增用户限制</Button>
                                        <Button type="primary" onClick={() => this.addEdit(2)}>新增市场限制</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Tabs onChange={this.tcb} defaultActiveKey='1' activeKey={tk}>
                                        <TabPane tab='用户限制' key={'1'}></TabPane>
                                        <TabPane tab='市场限制' key={'2'}></TabPane>
                                    </Tabs>
                                    <CommonTable
                                        dataSource={dataSource}
                                        pagination={
                                            {
                                                total: pageTotal,
                                                pageSizeOptions: PAGRSIZE_OPTIONS20,
                                                defaultPageSize: PAGESIZE,
                                                pageSize: pageSize,
                                                current: pageIndex

                                            }
                                        }
                                        columns={this.createColumns(pageIndex, pageSize)}
                                        requestTable={this.requestTable}
                                    // scroll={this.islessBodyWidth() ? { x: 1800 } : {}}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={modalVisible}
                    title={this.state.modalTitle}
                    width={900}
                    onCancel={this.handleCancel}
                    footer={null}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}