/**VDS行情 */
import Decorator from '../../decorator'
import axios from '../../../utils/fetch'
import CommonTable from '../../common/table/commonTable'
import ModalMarket from './modal/modalMarket'
import { Button, message, Modal, Select } from 'antd'
import { DOMAIN_VIP, PAGRSIZE_OPTIONS20, PAGESIZE, PAGEINDEX, SELECTWIDTH } from '../../../conf'
const { Option } = Select;


@Decorator()
export default class VdsMarket extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            platform: '',// 平台
            market: '',// 市场
            isQuant: '',// 是否是刷量参考平台 0-是 1-不是
            isHedge: '',//是否是保值参考平台 0-是 1-不是
            marketList: [<option value='' key='0'>请选择</option>],
            visible: false,
            modalHtml: ''
        }
        this.state = {
            ...this.defaultState,
            width: '',
            footer:''
        }


    }



    async componentDidMount() {
        await this.selectData()
        this.requestTable()

    }

    selectChange = (value) => {
        this.setState({
            market: value
        })
    }
    //请求数据
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, market } = this.state
        let params = {
            market,
            //pageIndex: currIndex || pageIndex,
            //pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/brush/config/getConfigMarketPlatform', type: 'post' }, params)
        this.setState({
            dataSource: result || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }


    handleOk = e => {

        this.setState({
            visible: false,
        });
    };

    handleCancel = e => {

        this.setState({
            visible: false,
        });
    };
    // 点击修改按钮，弹出弹框
    showModal = (record) => {
        const footer=null;
        this.setState({
            visible: true,
            width: '450px',
            footer,
            modalHtml: <ModalMarket record={record} requestTable={this.requestTable} handleCancel={this.handleCancel} />
        });

    };


    resetData=()=>{
        this.setState({
            market:''
        })
    }

    // 下拉框数据

    selectData = () => {
        axios.get(DOMAIN_VIP + '/common/queryMarket').then(res => {
            const result = res.data;
            let selectList = [];
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    selectList.push(<Option key={i + 1} value={result.data[i]}>{result.data[i]}</Option>)
                }
                this.setState({
                    marketList: [<Option key='0' value=''>请选择</Option>, ...selectList]
                });
            }
        })
    }


    createColumns = (pageIndex, pageSize) => {
        let states = ['是', '否']
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '市场', dataIndex: 'market', },
            { title: '平台', dataIndex: 'platform' },
            { title: '是否是刷量参考平台', dataIndex: 'isQuant', render: t => states[t] },
            { title: '是否是保值参考平台', dataIndex: 'isHedge', render: t => states[t] },
            {
                title: '操作', dataIndex: 'op', render: (text, record, index) => <a href="javascript:;" onClick={() => this.showModal(record)} >修改</a>

            },
        ]
    }
    render() {
        const { footer,visible, width, modalHtml, showHide, pageIndex, pageSize, dataSource, pageTotal, market, marketList } = this.state
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
                                        <label className="col-sm-3 control-label">市场:</label>
                                        <div className="col-sm-8">
                                            <Select
                                                showSearch
                                                value={market}
                                                style={{ width: SELECTWIDTH }}
                                                onChange={this.selectChange}
                                            >
                                                {marketList}
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>
                                        <Button type="primary" onClick={this.resetData}>重置</Button>
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
                    title="修改数据"
                    visible={visible}
                    width={width}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={footer}
                >
                    {modalHtml}
                </Modal>

            </div>
        )
    }
}