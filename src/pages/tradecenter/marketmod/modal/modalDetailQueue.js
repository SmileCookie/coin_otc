import axios from '../../../../utils/fetch'
import {
    PAGEINDEX,
    PAGESIZE,
    DOMAIN_VIP,
    PAGRSIZE_OPTIONS20,
    TIMEFORMAT_ss,
    SHOW_TIME_DEFAULT,
    TIME_PLACEHOLDER
} from '../../../../conf'
import { Button, DatePicker, message } from 'antd'
import { toThousands } from '../../../../utils'
import CommonTable from '../../../common/table/commonTable'
import Decorator from '../../../decorator'

const { RangePicker } = DatePicker;

@Decorator()
class ModalDetailQueue extends React.Component {
    constructor(props) {
        super(props);
        this.defaultState = {
            userName: '',
            userId: '',
            time: [],
        };
        this.state = {
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            dataSource: [],
            pageTotal: 0,
            ...this.defaultState,
            item: props.item,
        }
    }

    componentDidMount() {
        this.requestTable();
    }

    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, userId, userName, item, time } = this.state;
        let submitTimeS = Array.isArray(time) && time.length > 0 ? moment(time[0]).format('x') : '';
        let submitTimeE = Array.isArray(time) && time.length > 0 ? moment(time[1]).format('x') : '';

        axios.post(DOMAIN_VIP + '/marketTrade/newQueryDetail', qs.stringify({
            userId,
            userName,
            submitTimeS,
            submitTimeE,
            market: item.market || '',
            userType: item.userType || 0,
            unitPrice: item.unitPrice || 0,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                const dataSource = result.data.list || result.data || [];
                this.setState({
                    dataSource,
                    pageTotal: result.data.totalCount,
                    pageSize: currSize || pageSize,
                    pageIndex: currIndex || pageIndex,
                })
            } else {
                message.error(result.msg);
            }
        })
    };

    createColumns = (pageIndex, pageSize) => {
        return [
            {
                title: '序号',
                dataIndex: 'index',
                render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1
            },
            { title: '用户编号', dataIndex: 'userId' },
            // { title: '用户名称', dataIndex: 'userName' },
            { title: '委托价格', className: 'moneyGreen', dataIndex: 'unitPrice', render: text => toThousands(text, true) },
            { title: '委托数量', dataIndex: 'numbers', },
            { title: '委托时间', dataIndex: 'submitTime', render: text => text ? moment(text).format(TIMEFORMAT_ss) : '' },
        ]
    };

    render() {
        const { time, pageIndex, pageSize, pageTotal, userId, userName, dataSource } = this.state;
        return (
            <div className="row">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="x_panel">
                        <div className="x_content">
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">用户编号：</label>
                                    <div className="col-sm-8">
                                        <input
                                            type="text"
                                            className="form-control"
                                            name="userId"
                                            value={userId}
                                            onChange={this.handleInputChange}
                                        />
                                    </div>
                                </div>
                            </div>
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">用户名：</label>
                                    <div className="col-sm-8">
                                        <input
                                            type="text"
                                            className="form-control"
                                            name="userName"
                                            value={userName}
                                            onChange={this.handleInputChange}
                                        />
                                    </div>
                                </div>
                            </div>
                            <div className="col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">委托时间：</label>
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
                    />
                </div>
            </div>
        )
    }
}

export default ModalDetailQueue
