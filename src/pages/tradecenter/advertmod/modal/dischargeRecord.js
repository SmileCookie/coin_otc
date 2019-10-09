import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { toThousands, dateToFormat } from 'Utils'

@Decorator()
export default class DischargeRecord extends React.Component {
    constructor(props) {
        super(props)
    }
    createColumns = () => {
        let states = ['已上架','已下架','已隐藏',]
        return  [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => index+1},
            { title: '记录时间', dataIndex: 'recordingTime', render: t => dateToFormat(t) },
            { title: '状态', dataIndex: 'state',render:t => states[t] || '--' },
            { title: '广告编号', dataIndex: 'orderNo' },
            { title: '发布人ID', dataIndex: 'authorId', },
            { title: '货币类型', dataIndex: 'fundstypeName', },
            { title: '操作人ID', dataIndex: 'adminId',render:t => t == 0 ? '系统' : t },
            { title: '变更原因', dataIndex: 'reason', },
        ]
    }
    render() {
        return (
            <div className="table-responsive">
                <CommonTable
                    dataSource={this.props.dataSource}
                    // pagination={
                    //     {
                    //         total: pageTotal,
                    //         pageSize: pageSize,
                    //         current: pageIndex

                    //     }
                    // }
                    columns={this.createColumns()}
                    // requestTable={this.requestTable}
                />
            </div>
        )
    }
}