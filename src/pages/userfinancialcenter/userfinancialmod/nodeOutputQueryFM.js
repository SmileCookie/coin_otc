/**节点产出查询 */
import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { Button, DatePicker } from 'antd'
import { PAGEINDEX, NODE_TYPE, IS_SHOW, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER, TIMEFORMAT_ss } from 'Conf'
import { toThousands, TE, ckd, dateToFormat, mapGet, isArray, arrayTimeToStr } from 'Utils'
import { SeOp, SingleInput, DoubleInput } from '../../../components/select/asyncSelect'
const { RangePicker } = DatePicker
import { RPicker } from '../../../components/date'
import { ButtonInit } from '../../common/components'

@Decorator()
export default class NodeOutputQueryFM extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            snodenameLk: '',//名称
            snodeaddrLk: '',//地址
            miningamountLt: '',//产出数量开始
            miningamountGt: '',
            heightLt: '',//区块高度开始
            heightGt: '',
            snodetype: '',//节点类型
            snodebeltype: '',//归属类型
            // snodestate: '',//状态
            profitbatchnoLt: '',//分配批次开始
            profitbatchnoGt: '',
            snodeshowflag: '',//显示标志
            time: []
        }
        this.state = {
            ...this.defaultState,
            mining: '',//产出总数量
            nodesum: '',//初创节点产出总量
            nodeamout: '',//固定节点产出总量
            nodeout: '',//动态节点产出总量
        }
    }
    async componentDidMount() {

    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time } = this.state
        const params = Object.keys(this.defaultState).reduce((res, key) => {
            void 0 !== this.state[key] && !isArray(this.state[key]) && (res[key] = this.state[key])
            return res
        }, {
                createtimeLt: arrayTimeToStr(time),
                createtimeGt: arrayTimeToStr(time, 1),
                pageIndex: currIndex || pageIndex,
                pageSize: currSize || pageSize
            })
        const rs = await this.request({ url: '/supernode/miningList', type: 'post' }, params)
        this.setState({
            dataSource: ckd(TE(rs.result).list),
            pageTotal: TE(rs.result).totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,

        })
        for (let i = 0; i < rs.mining.length; i++) {
            const type = rs.mining[i].snodetype
            switch (type) {
                case 1:
                    this.setState({
                        nodesum: rs.mining[i].miningamount
                    })
                    break;
                case 2:
                    this.setState({
                        nodeamout: rs.mining[i].miningamount
                    })
                    break;
                case 3:
                    this.setState({
                        nodeout: rs.mining[i].miningamount
                    })
                    break;
                default:
                    this.setState({
                        mining: rs.mining[i].miningamountall
                    })
            }
        }
    }
    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', className: 'wordLine', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '节点名称', className: 'wordLine', dataIndex: 'snodename' },
            { title: '节点地址', className: 'wordLine', dataIndex: 'snodeaddr' },
            { title: '产出数量', className: 'wordLine', dataIndex: 'miningamount' },
            { title: '区块高度', className: 'wordLine', dataIndex: 'height', },
            { title: '区块时间', className: 'wordLine', dataIndex: 'createtime', render: t => dateToFormat(t.time) },
            { title: '节点类型', className: 'wordLine', dataIndex: 'snodetype', render: t => mapGet(NODE_TYPE, t) },
            { title: '归属类型', className: 'wordLine', dataIndex: 'snodebeltype', render: t => mapGet(NODE_TYPE, t) },
            { title: '显示标志', className: 'wordLine', dataIndex: 'snodeshowflag', render: t => mapGet(IS_SHOW, t) },
            { title: '分配批次', className: 'wordLine', dataIndex: 'profitbatchno' },
        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, dataSource, pageTotal, snodenameLk, snodeaddrLk, snodetype, snodebeltype, snodestate,
            heightGt, heightLt, miningamountGt, miningamountLt, snodeshowflag, profitbatchnoLt, profitbatchnoGt, time, mining, nodesum, nodeamout, nodeout } = this.state
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

                                <SingleInput title='节点名称' name='snodenameLk' fuzzy value={snodenameLk} handleInputChange={this.handleInputChange} />
                                <SingleInput title='节点地址' name='snodeaddrLk' fuzzy value={snodeaddrLk} handleInputChange={this.handleInputChange} />
                                <SeOp title='节点类型' value={snodetype} ops={NODE_TYPE} onSelectChoose={v => this.onSelectChoose(v, 'snodetype')} pleaseC />
                                <SeOp title='归属类型' value={snodebeltype} ops={NODE_TYPE} onSelectChoose={v => this.onSelectChoose(v, 'snodebeltype')} pleaseC />
                                <SeOp title='显示标志' value={snodeshowflag} ops={IS_SHOW} onSelectChoose={v => this.onSelectChoose(v, 'snodeshowflag')} pleaseC />
                                <DoubleInput title='产出数量' nameMin='miningamountLt' nameMax='miningamountGt' valueMin={miningamountLt} valueMax={miningamountGt} handleInputChange={this.handleInputChange} />
                                <DoubleInput title='区块高度' nameMin='heightLt' nameMax='heightGt' valueMin={heightLt} valueMax={heightGt} handleInputChange={this.handleInputChange} />
                                <DoubleInput title='分配批次' nameMin='profitbatchnoLt' nameMax='profitbatchnoGt' valueMin={profitbatchnoLt} valueMax={profitbatchnoGt} handleInputChange={this.handleInputChange} />
                                <RPicker title='区块时间' time={time}  onChangeCheckTime={(date,dateString) => this.onChangeCheckTime(date, dateString, 'time')} />
                                <div className="col-md-12 col-sm-12 col-xs-12 right">
                                    <div className="right">
                                        <ButtonInit
                                            requestTable={() => this.requestTable(PAGEINDEX)}
                                            resetState={this.resetState}
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>
                        }

                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <div className='table-total'>
                                        产出总量：{mining || 0}，
                                        总记录数：{pageTotal}，
                                        初创节点产出总量：{nodesum || 0}，
                                        固定节点产出总量：{nodeamout || 0}，
                                        动态节点产出总量：{nodeout || 0}
                                    </div>
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
                                    // scroll={this.islessBodyWidth() ? { x: 1800 } : {}}
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