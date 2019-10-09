/** 理财账户对账 */
import Decorator from '../../decorator'
import FundsTypeList from '../../common/select/fundsTypeList'
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, DatePicker } from 'antd'
import { PAGRSIZE_OPTIONS20, PAGESIZE, PAGEINDEX, TIMEFORMAT_ss } from '../../../conf'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
import { toThousands,arrayTimeToStr } from '../../../utils'
const Big = require('big.js')

@Decorator()
export default class FinancialAccountReconFM extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            fundsType: '0',
            time: []
        }

    }
    componentDidMount() {
        this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time, fundsType } = this.state
        let params = {
            fundstype: fundsType,
            reportdateStart: arrayTimeToStr(time),
            reportdateEnd: arrayTimeToStr(time, 1),
            pageIndex: currIndex || pageIndex, pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/billReconciliationFinancial/list', type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    updateStatus = id => {
        let self = this;
        Modal.confirm({
            title:'你确定要改变状态吗？',
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                let params = {id}
                self.request({ url: '/billReconciliationFinancial/update', type: 'post' }, params).then(res => {
                    self.requestTable()
                })
            },
            onCancel(){
                console.log('Cancel')
            } 
        })
    }
    createColumns = (pageIndex, pageSize) => {
        let states = ['正常', '异常']
        Big.RM = 0;
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '资金类型', dataIndex: 'fundstypeName' },
            //注释平台对账
            // { title: <React.Fragment>平台对账<br />(E-F)-(G+H)</React.Fragment>, dataIndex: 'ddddd',render:(t,r) => toThousands(new Big(r.shiftto).minus(r.rollout).minus(r.balance).minus(r.transactionfee),true)},
            { title: '差额', dataIndex: 'difference', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: <React.Fragment>用户转入<br />(E)</React.Fragment>, className: 'moneyGreen', dataIndex: 'shiftto', render: t => toThousands(t, true) },
            { title: <React.Fragment>用户转出<br />(F)</React.Fragment>, className: 'moneyGreen', dataIndex: 'rollout', render: t => toThousands(t, true) },
            { title: <React.Fragment>理财账户余额<br />(G)</React.Fragment>, className: 'moneyGreen', dataIndex: 'balance', render: t => toThousands(t, true) },
            { title: <React.Fragment>理财投资总额<br />(H)</React.Fragment>, className: 'moneyGreen', dataIndex: 'invest', render: t => toThousands(t, true) },
            { title: '建点奖励', dataIndex: 'superNode', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '指导奖励', dataIndex: 'director', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '晋升奖励', dataIndex: 'promot', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '全球领袖分红', dataIndex: 'globalLeader', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: 'VDS生态回馈', dataIndex: 'vdsEco', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: 'VIP分红奖励', dataIndex: 'vipBonus', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '新人加成', dataIndex: 'newerBuffer', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '复投基金', dataIndex: 'reInvest', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '复投基金复投', dataIndex: 'reInvestDouble', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: 'VDS生态回馈转入超级节点搭建', dataIndex: 'vdsEcoShiftToSuperNode', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '复投基金转入超级节点搭建', dataIndex: 'reInvestShiftToSuperNode', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '出局奖励转入超级节点搭建', dataIndex: 'outGameShiftToSuperNode', className: 'moneyGreen', render: t => toThousands(t, true) },
            
            /**
             * @author oliver
             * @de 添加  http://jira.oswaldlink.f3322.net:10080/browse/XJYPTYYHT-500 
             * */
            { title: '理财保险投资', dataIndex: 'insureInvest', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '子账号划入', dataIndex: 'subShiftIn', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '子账号划出', dataIndex: 'subRollout', className: 'moneyGreen', render: t => toThousands(t, true) },
            { title: '回本加成', dataIndex: 'rebateBuffer', className: 'moneyGreen', render: t => toThousands(t, true) },

            { title: '对账日期', dataIndex: 'reportdate', render: (t, record) => t ? moment(t).format('YYYY-MM-DD') : '--' },
            { title: '状态', dataIndex: 'state', render: (state,record) => state == 0 ? '正常' : <a href='javascript:void(0);' onClick={()=>this.updateStatus(record.id)} >异常</a> }
        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, dataSource, pageTotal, fundsType, time } = this.state
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
                                    <FundsTypeList fundsType={fundsType} handleChange={(v) => this.onSelectChoose(v,'fundsType')} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对账日期：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'time')}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-4 col-sm-4 col-xs-4 right">
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
                                        scroll={{x:3000}}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
