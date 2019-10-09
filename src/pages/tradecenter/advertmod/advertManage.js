import Decorator from 'DTPath'
import CommonTable from 'CTable'
import ModalDetail from './modal/modalDetail'
import { PAGEINDEX,DOMAIN_VIP , PAGRSIZE_OPTIONS20, PAGESIZE, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER, TIMEFORMAT_ss,OTC_FUNDSTYPE } from 'Conf'
import { SeOp } from '../../../components/select/asyncSelect'
import axios from '../../../utils/fetch'
import DischargeRecord from './modal/dischargeRecord'
import { Tabs, Button, Modal, message ,Select, Popover, Checkbox, DatePicker } from 'antd'
import { toThousands, dateToFormat,saveToSS,getFromSS,removeFromSS,splitArr,arrayTimeToStr } from 'Utils'
import UserInfoDetail from '../../systemcenter/usermod/userInfoDetail'
const { TabPane } = Tabs;
const {  RangePicker, } = DatePicker;
const Big = require('big.js')

@Decorator({ lb: 'advertisement' })
export default class AdvertManage extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            orderNo: '',
            userId: "",
            userName: '',
            orderStatus: '-1',
            reason: '',
            time: [],
            fundstype: '',
            email:'', //
        }
        this.state = {
            visible: false,
            modalHtml: '',
            title: '',
            width: '',
            tk: '0',//tab 标签      0： 已上架 1： 已下架 2： 已隐藏
            ...this.defaultState,
            pageTabs: true,
            detailUser:null,
            loading:false
        }
        this.goofn = () => new Map([
            ['default',v => this.saveReson(v)],
        ]) 
    }
    componentDidMount() {
        this.setState({
            appActiveKey: this.props.appActiveKey,
        })
        this.sendListen()

    }
    componentWillReceiveProps(nextProps) {
        if (nextProps.appActiveKey == this.state.appActiveKey) {
            this.sendListen()
        }
    }
    componentWillUnmount(){
        
    }
    sendListen = async () => {   
        let _key = splitArr(getFromSS() || '',0,'-')
        let _value = splitArr(getFromSS() || '',1,'-')
        await this.setState({
            [_key]:_value
        })
        await this.requestTable()
        // setTimeout(() => { removeFromSS()},0)
        removeFromSS()
    }
    showDetail = (id) => {
        let maxWidth = window.screen.width > 1500 ? "1700px" : '1300px'
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]

        this.setState({
            visible: true,
            title: "查看订单",
            width: maxWidth,
            modalHtml: <ModalDetail id={id} />
        })
    }

    changeStatus = (item) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(item)}>确定</Button>
        ]
        if (this.input) {
            this.input.value = ""
        }
        this.setState({
            visible: true,
            title: "广告下架",
            width: '400px',
            reason: '',
            modalHtml: <div className="form-group">
                <label>请输入下架原因：</label>
                <div className="col-sm-12">
                    <input type="text" ref={(inp) => this.input = inp} className="form-control" name="reason" onChange={this.handleInputChange} />
                </div>
            </div>
        })
    }
    saveReson = ({id, orderType}) => {
        const { reason } = this.state

        this.setState({ loading: true })
        axios.post(DOMAIN_VIP + '/advertisement/downFramAd', qs.stringify({
            id, orderType, reason
        })).then(res => {
            const result = res.data;
            if (result.code == '0') {
                message.success(result.msg)
                this.setState({
                    visible: false,
                    loading: false
                })
                this.requestTable()
            } else {
                message.warning(result.msg)
                this.setState({
                    loading: false
                })
            }
        })
    }
    requestTable = async (currIndex, currSize) => {
        const { orderNo, userId, userName, orderStatus, pageIndex, pageSize, tk, time, fundstype,email } = this.state
        let params = {
            orderNo, userId, userName, orderStatus: tk, fundstype,
            email,
            createtimeS:arrayTimeToStr(time),
            createtimeE: arrayTimeToStr(time,1),
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/advertisement/list', type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })
    }
    jumpPage = ({ orderNo }) => {
        let _url = { name: "订单管理", url: "/tradecenter/ordermod/orderManage", key: 700100010062 }
        saveToSS('orderNo' + '-' + orderNo)
        this.props._this.add(_url)

    }
    createColumns = (pageIndex, pageSize) => {
        const { limitBtn, tk } = this.state
        Big.RM = 0;
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '广告编号', dataIndex: 'orderNo', render: (t, r) => <a href='javascript:void(0);' onClick={() => this.dischargeDetail(r)}>{t}</a> },
            { title: '状态', dataIndex: 'orderStatusName' },
            { title: '创建时间', className: `${tk == 0 ? '' : 'hide'}`, dataIndex: 'orderTime', render: t => dateToFormat(t) },
            { title: '隐藏时间', className: `${tk == 2 ? '' : 'hide'}`, dataIndex: 'hideTime', render: t => dateToFormat(t) },
            { title: '下架时间', className: `${tk == 1 ? '' : 'hide'}`, dataIndex: 'lowerShelfTime', render: t => dateToFormat(t) },
            { title: '广告类型', dataIndex: 'orderType', render: t => t == 0 ? "买" : '卖' },
            { title: '货币类型', dataIndex: 'coinTypeName', },
            { title: '单价/CNY', dataIndex: 'coinPrice', className: `'moneyGreen' ${tk == 2 ? 'hide' : ''}`, render: t => toThousands(t, true) },
            { title: '单笔限额', dataIndex: 'maxNumber', render: (t, r) => `[${r.minNumber},${r.maxNumber}]` },
            { title: `${tk == 1 ? '下架人ID' : '发布人ID'}`, dataIndex: 'userId', render: (t, r) => <a href='javascript:void(0);' onClick={() => this.toIssue(r)}>{tk == 1 ? r.lowerShelfId == 0 ? '系统' : r.lowerShelfId : r.userId}</a> },
            { title: '订单数量', dataIndex: 'ordersum', render: (t, r) => <a href='javascript:void(0);' onClick={() => this.jumpPage(r)}>{t}</a> },
            { title: '累计成交币量', dataIndex: 'coinComplateNumber', },
            { title: '累计手续费/币', dataIndex: 'amount', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: `${tk == 1 ? '下架原因' : '隐藏原因'}`, className: `${tk == 0 ? 'hide' : ''}`, dataIndex: 'reason', },
            {
                title: '操作', className: `${tk == 0 ? '' : 'hide'}`, dataIndex: 'op', render: (t, r) => <span>
                    {/* {limitBtn.indexOf('Recordlist') > -1 ? <a href="javascript:void(0)" className='mar20' onClick={() => this.showDetail(r.orderNo)}>查看订单</a> : ''} */}
                    {limitBtn.indexOf('downFramAd') > -1 ? r.orderStatus == 1 ? '' : <a href="javascript:void(0)" onClick={() => this.changeStatus(r)}>下架广告</a> : ''}
                </span>
            },
        ]
    }
    dischargeDetail = async ({ orderNo }) => {
        let maxWidth = window.screen.width > 1500 ? "800px" : '700px'
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>关闭</Button>
        ]
        const result = await this.request({ url: '/advertisement/record', type: 'post' }, {
            recordNo: orderNo
        })
        this.setState({
            visible: true,
            title: "广告上下架记录",
            width: maxWidth,
            modalHtml: <DischargeRecord dataSource={result} />
        })
    }
    tcb = async tk => {
        await this.setState({ tk })
        this.requestTable(PAGEINDEX)
    }
    handleCancel = () => this.setState({ visible: false })
    toIssue = (detailUser = null) => this.setState({ pageTabs: !this.state.pageTabs, detailUser, })
    render() {
        Big.RM = 0;
        const { showHide, orderNo, userId, pageTabs, userName, orderStatus, fundstype, pageIndex, pageSize, pageTotal, dataSource, visible, title, modalHtml, width, limitBtn, tk, time,email } = this.state
        return (
            <div className="right-con">
                {pageTabs
                    ?
                    <div>
                        <div className="page-title">
                            当前位置：数据中心 > OTC交易中心 > 广告管理 > 广告管理
                            <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                        </div>
                        <div className="clearfix"></div>
                        <div className="row">
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                {showHide && <div className="x_panel">
                                    <div className="x_content">

                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-4 control-label">广告编号：</label>
                                                <div className="col-sm-8 ">
                                                    <input type="text" className="form-control" name="orderNo" value={orderNo} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">用户编号：</label>
                                                <div className="col-sm-8 ">
                                                    <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">邮箱</label>
                                                <div className="col-sm-8 ">
                                                    <input type="text" className="form-control" name="email" value={email} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                        {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名称：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div> */}
                                        <SeOp title='货币类型' value={fundstype} onSelectChoose={v => this.onSelectChoose(v, 'fundstype')} ops={OTC_FUNDSTYPE} />
                                        {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">广告状态：</label>
                                        <div className="col-sm-9">
                                            <Select value={orderStatus} style={{ width: SELECTWIDTH }} onChange={v => this.onSelectChoose(v, 'orderStatus')} >
                                                <Option value='-1'>请选择</Option>
                                                <Option value='0'>已上架</Option>
                                                <Option value='1'>已下架</Option>
                                                <Option value='2'>已隐藏</Option>
                                            </Select>

                                        </div>
                                    </div>
                                </div> */}
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">日期范围：</label>
                                                <div className="col-sm-8">
                                                    <RangePicker
                                                        showTime={{
                                                            defaultValue: SHOW_TIME_DEFAULT
                                                        }}
                                                        format={TIMEFORMAT_ss}
                                                        placeholder={TIME_PLACEHOLDER}
                                                        onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'time')}
                                                        value={time}
                                                    />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-6 col-sm-6 col-xs-6 right">
                                            <div className="right">
                                                <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>
                                                <Button type="primary" onClick={this.resetState}>重置</Button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                }
                                <div className="x_panel">
                                    <div className="x_content">
                                        <div className="table-responsive">
                                            <Tabs onChange={this.tcb} defaultActiveKey='1' activeKey={tk}>
                                                <TabPane tab='已上架' key={'0'}></TabPane>
                                                <TabPane tab='已隐藏' key={'2'}></TabPane>
                                                <TabPane tab='已下架' key={'1'}></TabPane>
                                            </Tabs>
                                            <CommonTable
                                                dataSource={dataSource}
                                                pagination={
                                                    {
                                                        total: pageTotal,
                                                        pageSize: pageSize,
                                                        current: pageIndex
                                                    }
                                                }
                                                columns={this.createColumns(pageIndex, pageSize)}
                                                requestTable={this.requestTable}
                                                scroll={{ x: 2000 }}
                                            />
                                        </div>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                    :
                    <UserInfoDetail permissList={this.props.permissList}  toIssue={this.toIssue}  user={this.state.detailUser} />
                }
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 120 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}
                </Modal>
            </div>
        )

    }
}