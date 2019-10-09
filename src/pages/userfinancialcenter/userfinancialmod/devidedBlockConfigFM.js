/**分红区块配置 */
import Decorator from '../../decorator'
import FundsTypeList from '../../common/select/fundsTypeList'
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, Select } from 'antd'
import { PAGRSIZE_OPTIONS20, PAGESIZE, PAGEINDEX, TIMEFORMAT_ss, SELECTWIDTH } from '../../../conf'
import { toThousands } from 'Utils';
const { Option } = Select;
const Big = require('big.js')

@Decorator()
export default class DevidedBlockConfigFM extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            blocktype: '',
            blockremark: "",
            blockheightStart: '',
            blockheightEnd: '',
        }
        this.state = {
            ...this.defaultState
        }
    }
    async componentDidMount() {

    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, blocktype, blockremark, blockheightStart, blockheightEnd, } = this.state
        let params = {
            blocktype, blockremark, blockheightStart, blockheightEnd,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/proFitblockConfig/list', type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    createColumns = (pageIndex, pageSize) => {
        let states = ['--', '当前区块', '分红区块']
        Big.RM = 0;
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '区块类型', dataIndex: 'blocktype', render: t => states[t] },
            { title: '区块高度', dataIndex: 'blockheight' },
            { title: '区块备注', dataIndex: 'blockremark' },
            // { title: '操作', className: 'moneyGreen', dataIndex: 'balance', },
        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, dataSource, pageTotal, blocktype, blockremark, blockheightStart, blockheightEnd, } = this.state
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
                                        <label className="col-sm-3 control-label">区块类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={blocktype} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'blocktype')} >
                                                <Option value=''>请选择</Option>
                                                <Option value='1'>当前区块</Option>
                                                <Option value='2'>分红区块</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">区块高度：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="blockheightStart" value={blockheightStart} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="blockheightEnd" value={blockheightEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">区块备注：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="blockremark" value={blockremark} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
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