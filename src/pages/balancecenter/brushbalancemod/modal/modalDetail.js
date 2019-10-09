import React from 'react'
import axios from '../../../../utils/fetch'
import moment from 'moment'
import 'moment/locale/zh-cn';
import { TIMEFORMAT, DOMAIN_VIP } from '../../../../conf/index';
moment.locale('zh-cn');

export default class ModalDetail extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            id: '',
            tableList: []
        }
    }
    componentDidMount() {
        this.setState({
            id: this.props.id,
        }, () => this.requestTable())
    }
    componentWillReceiveProps(nextProps) {
        this.setState({
            id: nextProps.id,
        }, () => this.requestTable())
    }
    requestTable() {
        const { id } = this.state
        axios.get(DOMAIN_VIP + '/brush/balance/entrust/details', {
            params: {
                id
            }
        }).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    tableList: result.data
                })
            }
        })
    }

    render() {
        const { tableList } = this.state
        return (
            <div className="x_panel">
                <div className="x_content">
                    <div className="table-responsive">
                        <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">对冲ID</th>
                                    {/* <th className="column-title">对冲委托ID</th> */}
                                    <th className="column-title">成交价格</th>
                                    <th className="column-title">委托数量</th>
                                    <th className="column-title">成交数量</th>
                                    <th className="column-title">剩余数量</th>
                                    <th className="column-title">保值状态</th>

                                    <th className="column-title">保值平台</th>
                                    <th className="column-title">成交时间</th>
                                    <th className="column-title">保值委托ID</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length > 0 ?
                                        tableList.map((item, index) => {
                                            return (
                                                <tr key={index}>
                                                    <td>{index + 1}</td>
                                                    <td>{item.id}</td>
                                                    {/* <td>{item.enturstId}</td> */}
                                                    <td>{item.price}</td>
                                                    <td>{item.number}</td>
                                                    <td>{item.executedNumber}</td>
                                                    <td>{item.remainingNumber}</td>
                                                    <td>{item.status}</td>
                                                    <td>{item.platform}</td>
                                                    <td>{moment(item.addTime).format(TIMEFORMAT)}</td>
                                                    <td>{item.enturstId}</td>
                                                </tr>
                                            )
                                        }) :
                                        <tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                }
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        )
    }
}