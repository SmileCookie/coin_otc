import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, PAGESIZE_10, SELECTWIDTH, TIMEFORMAT, TIMEFORMAT_ss } from 'Conf'
import { toThousands, dateToFormat } from 'Utils'


@Decorator()
export default class ModalViewDetail extends React.Component {
    constructor(props) {
        super(props)
    }

    
    componentDidMount() {
        this.setProps(this.props)
    }
    componentWillReceiveProps(nextProps) {
        this.setProps(nextProps)
    }
    setProps = async (props) => {
        await this.setState({userId:props.userId})
        this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
       const {userId} = this.state
        const result = await this.request({ url: '/storeAuth/queryAuthList', type: 'post' }, {userId})
        this.setState({
            dataSource: result || [],
            // pageTotal: result.totalCount,
            // pageSize: currSize || pageSize,
            // pageIndex: currIndex || pageIndex
        })

    }
    createColumns = (pageIndex, pageSize) => {


        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '用户编号', dataIndex: 'userId', },
            { title: '申请时间', dataIndex: 'applyTime', render: t => dateToFormat(t) },
            { title: '申请类型', dataIndex: 'applyType' },
            // { title: '法币账户USDT余额', dataIndex: 'userId' },
            { title: '保证金余额USDT', dataIndex: 'storeFreez',render:t => toThousands(t,true) },
            { title: '审核时间', dataIndex: 'auditTime', render: t => dateToFormat(t) },
            { title: '审核人', dataIndex: 'auditUserName', },
            { title: '审核结果', dataIndex: 'statusName' },
            { title: '原因', dataIndex: 'reson', },
            // { title: '操作', dataIndex: 'op',render:(t,r) => <a  href='javascript:void(0);' onClick={this.props}>查看</a> },
        ]
    }
    render() {
        const {dataSource,pageIndex,pageSize,pageTotal} = this.state
        return <div className="table-responsive">
            <CommonTable
                dataSource={dataSource}
                // pagination={
                //     {
                //         total: pageTotal,
                //         pageSizeOptions: PAGRSIZE_OPTIONS20,
                //         defaultPageSize: PAGESIZE,
                //         pageSize: pageSize,
                //         current: pageIndex

                //     }
                // }
                columns={this.createColumns(pageIndex, pageSize)}
                requestTable={this.requestTable}
                scroll={{ x: 1800 }}
            />
        </div>
    }
}