/**节点数量计算 */
import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'


@Decorator()
export default class NodeCompute extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            newly: '',
            fixCount: '',
            dynamic: ''
        }
    }
    async componentDidMount() {
        await this.requestTable()
    }




    requestTable = async () => {
        const result = await this.request({ url: '/supernode/nodeNumber', type: 'post' })
        this.setState({
            dataSource: result || [],
            //pageTotal: result.totalCount,
            //pageSize: currSize || pageSize,
            //pageIndex: currIndex || pageIndex,
            newly: result[0].newly,
            fixCount: result[0].fixCount,
            dynamic: result[0].dynamic

        })
    }

    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', className: 'wordLine', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '当前初创数量', className: 'wordLine', dataIndex: 'newly' },
            { title: '当前固定数量', className: 'wordLine', dataIndex: 'fixCount', },
            { title: '当前动态数量', className: 'wordLine', dataIndex: 'dynamic', },
            { title: '投资总额', className: 'wordLine', dataIndex: 'total', },
            { title: '计算固定数量', className: 'wordLine', dataIndex: 'fix', },
            { title: '用户释放金额', className: 'wordLine', dataIndex: 'user', },
            { title: '生态发放金额', className: 'wordLine', dataIndex: 'zoology', },
            { title: '领袖分红金额', className: 'wordLine', dataIndex: 'leader', },
            { title: '计算动态数量', className: 'wordLine', dataIndex: 'ctDyNum', },
            { title: '固定变动数量', className: 'wordLine', dataIndex: 'fixChangeNum', },
            { title: '动态变动数量', className: 'wordLine', dataIndex: 'countChangeNum', },

        ]
    }
    render() {
        const { showHide, dataSource, pageIndex, pageSize ,newly, fixCount, dynamic,} = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">

                    <div className="col-md-12 col-sm-12 col-xs-12">

                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    初创节点数量：{newly || 0}，&nbsp;&nbsp;&nbsp;
                                                    固定节点数量：{fixCount || 0}，&nbsp;&nbsp;&nbsp;
                                                    动态节点数量：{dynamic || 0}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <CommonTable
                                        dataSource={dataSource}
                                        // pagination={
                                        //     {
                                        //         total: pageTotal,
                                        //         pageSize: pageSize,
                                        //         current: pageIndex

                                        //     }
                                        // }
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