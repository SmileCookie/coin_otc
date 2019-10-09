import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import OrderSubtaskModal from './modal/orderSubtaskModal'
import AddModal from './modal/addModal'
import StartModal from './modal/startModal'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, SELECTWIDTH, PAGRSIZE_OPTIONS20, TIMEFORMAT_ss,DEFAULTVALUE } from '../../../conf'
import { message, Modal, Table, Button } from 'antd'
const { Column } = Table


export default class OrderSubtask extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            tableSource: [],
            visible: false,
            modalHtml: "",
            showHide: true,
            width: '',
            market: '',
            startPrice:'',
            priceType:'',
            mdelayTime:'',
            title:'修改/详情',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
        }


    }
    componentDidMount() {
        this.requestTable()
    }
    clickHide = () => {
        this.setState({
            showHide: !this.state.showHide
        })
    }
    inquiry = () => {
        this.requestTable()
    }
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize} = this.state
        axios.get(DOMAIN_VIP + '/brush/trader/task/list',{ params: {
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize,
        }}).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.page.list || [];
                console.log(result)
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = (result.page.currPage - 1) * result.page.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id
                }
                this.setState({
                    tableSource
                })
            } else {
                message.warning(result.msg)
            }
        }).catch(error => console.error(error))
    }
    //输入时 input 设置到 satte
    handleInputChange = (event) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //弹框隐藏
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }
    onEdit = (item,type) => {
        this.footer = type == 1 ?[
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            // <Button key="submit" type="more" onClick={this.onEditBtn}>保存修改</Button>,
        ] : [];
        this.setState({
            visible: true,
            title:'修改/详情',
            width: '1100px',
            modalHtml: <OrderSubtaskModal item={item} disType={type}/>
        })
    }
    onEditBtn = () => {

    }
    onChangePageNum = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
    }
    add = () => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={this.onAddBtn}>保存修改</Button>,
        ];
        this.setState({
            visible: true,
            width: '500px',
            modalHtml: <AddModal handleSelectMarket={this.handleSelectMarket} changeTodoName={this.changeTodoName}  changestartPrice={this.changestartPrice} />
        })
    }
    onAddBtn = () => {
        const { market,startPrice,priceType } = this.state
        axios.post(DOMAIN_VIP + '/brush/trader/task/add', qs.stringify({
            market,startPrice,priceType
        })).then(res => {
            let result = res.data
            if (result.code == 0) {
                this.setState({
                    visible: false
                }, () => this.requestTable())

            } else {
                message.warning(result.msg)
            }
        })
    }
    

    handleSelectMarket = (market) => {
        this.setState({ market })
    }
    changeTodoName=(priceType)=> {
        this.setState({
            priceType
        })
    }
    changestartPrice=(startPrice)=> {
        this.setState({
            startPrice
        })
    }

    onStartTask = item => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.onStartTaskBtn(item)}>确定</Button>,
        ];
        this.setState({
            visible: true,
            width: '500px',
            title:'开启',
            modalHtml: <StartModal handleInputChange={this.handleInputChange} />
        })
        // let self = this
        // Modal.confirm({
        //     title: "您确定开启？",
        //     okText: '确定',
        //     okType: 'more',
        //     cancelText: '取消',
        //     onOk() {
        //         axios.post
        //     },
        //     onCancel() {
        //         console.log('Cancel');
        //     },
        // });
    }
    onStartTaskBtn = (item) => {
        const { mdelayTime } = this.state
        let delayTime = mdelayTime * 60 * 1000;
        axios.post(DOMAIN_VIP + '/brush/trader/task/start', qs.stringify({
            taskId: item.id,
            delayTime:delayTime
        })).then(res => {
            let result = res.data;
            if (result.code == 0) {
                this.setState({
                    visible:false
                },()=>this.requestTable())
            } else {
                message.warning(result.msg)
            }
        })
    }
    onStopTask = (item) => {
        let self = this
        Modal.confirm({
            title: "您确定停止？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                console.log(111)
                axios.post(DOMAIN_VIP + '/brush/trader/task/stop', qs.stringify({
                    taskId: item.id
                })).then(res => {
                    let result = res.data;
                    if (result.code == 0) {
                        self.requestTable()
                    } else {
                        message.warning(result.msg)
                    }
                })
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    render() {
        const { tableSource, visible, modalHtml, showHide, width,title,pageIndex,pageSize,pageTotal } = this.state
        let status = ['初始', '运行中(操盘阶段)', '停止', '运行中(恢复阶段)', '结束']
        let condition = ['<=','>=' ]
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：任务单
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.add}>新增</Button>
                                        {/* <Button type="primary" onClick={this.onStartTask}>启动</Button>
                                        <Button type="danger" onClick={this.onStopTask}>停止</Button> */}
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive ">
                                    <Table dataSource={tableSource}
                                     locale={{ emptyText: '暂无数据' }}
                                      bordered 
                                      pagination={{
                                        size: "small",
                                        pageSize: pageSize,
                                        current: pageIndex,
                                        total: pageTotal,
                                        onChange: this.onChangePageNum,
                                        showTotal: total => `总共 ${total} 条`,
                                        onShowSizeChange: this.onShowSizeChange,
                                        pageSizeOptions: PAGRSIZE_OPTIONS20,
                                        defaultPageSize: PAGESIZE,
                                        showSizeChanger: true,
                                        showQuickJumper: true
                                    }}
                                    >
                                        <Column title='序号' dataIndex='index' render={(text, record, index) => (
                                            <span>{text }</span>
                                        )} />
                                        <Column title='市场' dataIndex='market' />
                                        <Column title='状态' dataIndex='status' render={text => status[text]} />
                                        <Column title='启动价格条件'   dataIndex='priceType' render={(text,r) => <span>{condition[text]}{r.startPrice}</span> } />
                                        {/* <Column title='启动价格' colSpan='0' dataIndex='startPrice' /> */}
                                        <Column title='计划启动时间' dataIndex='startTime' render={(text) => text ? moment(text).format(TIMEFORMAT_ss) : '--'} />
                                        <Column title='操作' dataIndex='configTypeclick' render={(text, record) => {
                                            return <span>
                                                <a href="javascript:void(0);" onClick={() => this.onEdit(record, 0)} className="mar10" >详情</a>
                                                {record.status == 2 || record.status == 4 ? '' : <a href="javascript:void(0);" onClick={() => this.onEdit(record, 1)} className="mar10" >修改</a>}
                                                {record.status == 0 ? <a href="javascript:void(0);" onClick={() => this.onStartTask(record)} className="mar10" >开启</a>:''}
                                                {record.status == 2 || record.status == 4 ? '' :<a href="javascript:void(0);" onClick={() => this.onStopTask(record)}>停止</a>}
                                            </span>
                                        }} />
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}






