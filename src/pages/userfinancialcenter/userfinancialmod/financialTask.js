/**理财TASK */
import Decorator from '../../decorator'
import moment from 'moment';
import CommonTable from '../../common/table/commonTable'
import { Button, message, Modal, Select, DatePicker } from 'antd'
import { PAGRSIZE_OPTIONS20,ON_OFF, PAGESIZE, PAGEINDEX, TIMEFORMAT_ss, SELECTWIDTH } from '../../../conf'
import { toThousands, TE, ckd, dateToFormat, mapGet, } from 'Utils'
import ModalFinancial from './modal/modalFinancial'
const { Option } = Select;

@Decorator()
export default class FinancialTask extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            taskdesc: '',//任务描述
           
        }
        this.state = {
            ...this.defaultState,
            width: '',
            modalHtml: '',
            title: '',
            visible: false,
        }
    }
    async componentDidMount() {
       // await this.requestTable()
    }

    // 修改弹窗
    showModal = (item) => {
        let maxWidth = window.screen.width > 1500 ? "880px" : '660px'
        let titleName = '理财TASK修改'
        this.setState({
            title: titleName,
            visible: true,
            width: maxWidth,
            modalHtml: <ModalFinancial item={item} handleCancel={this.handleCancel} requestTable={this.requestTable} />
        });
    };


    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, taskdesc,  } = this.state

        let params = {
            taskdesc, 
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/finTask/queryList', type: 'post' }, params)
        this.setState({
            dataSource: ckd(TE(result).list),
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }

    handleCancel = () => {
        this.setState({
            visible: false
        })
    }


    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', className: 'wordLine',dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '任务名称', className: 'wordLine',dataIndex: 'taskname' },
            { title: '开启标志', className: 'wordLine',dataIndex: 'taskflag',render:t=>mapGet(ON_OFF,t)  },
            { title: '开启时间', className: 'wordLine',dataIndex: 'tasktime', render: t =>dateToFormat(t) },
            { title: '任务开启时间', className: 'wordLine',dataIndex: 'diststarttime', render: t => dateToFormat(t) },
            { title: '任务结束时间', className: 'wordLine',dataIndex: 'distendtime',render: t => dateToFormat(t)  },
            { title: '计算开始时间', className: 'wordLine',dataIndex: 'callstartdate', render: t => dateToFormat(t) },
            { title: '计算结束时间', className: 'wordLine',dataIndex: 'callenddate',render: t => dateToFormat(t)  },
            { title: '触发值', className: 'wordLine',dataIndex: 'triggerval',  },
            { title: '任务描述', className: 'wordLine',dataIndex: 'taskdesc',  },
            {
                title: '操作', className: 'wordLine',dataIndex: 'op', render: (text, record, index) => {
                    return (
                        <div>
                            <a href="javascript:;" onClick={() => this.showModal(record)} >修改</a>
                        </div>
                    )
                }
            },

        ]
    }
    render() {
        const { title, visible, width, modalHtml, showHide, pageIndex, pageSize, dataSource, pageTotal,  taskdesc } = this.state
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
                                        <label className="col-sm-3 control-label">任务描述：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="taskdesc" value={taskdesc} onChange={this.handleInputChange} />
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
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 60 }}
                    footer={null}
                    onCancel={this.handleCancel}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}