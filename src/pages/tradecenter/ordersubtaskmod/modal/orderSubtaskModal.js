import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalTask from './modalTask'
import { DOMAIN_VIP, DAYFORMAT, TIMEFORMAT_ss } from '../../../../conf'
import { Modal, Table, Button, message } from 'antd'
import { toThousands } from '../../../../utils'
const { Column } = Table

export default class OrderSubtaskModal extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            tableSource: [],
            visible: false,
            title: '',
            modalHtml: "",
            taskId: '',
            modseqNum: '',
            modcycle: '',
            modtargetPrice: '',
            modadrS: '',
            modadrE: '',
            market: '',
            modtargetDiff: '',
            disType: '',
            exchangeRate :0
        }

    }
    componentDidMount() {
        console.log(this.props)
        const { id, market, } = this.props.item
        this.setState({
            taskId: id,
            market,
            disType: this.props.disType
        }, () => this.requestTable())
    }

    componentWillReceiveProps(nextProps) {
        const { id, market, } = nextProps.item
        this.setState({
            taskId: id,
            market,
            disType: nextProps.disType
        }, () => this.requestTable())
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
    requestTable = () => {
        const { taskId } = this.state
        axios.get(DOMAIN_VIP + '/brush/trader/task/detail', {
            params: {
                taskId
            }
        }).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let detailsArr = result.data.details;
                let recoversArr = result.data.recovers;
                let taskArr = result.data.task;
                let tableSource = [...detailsArr, ...recoversArr]
                for (let i = 0; i < tableSource.length; i++) {
                    // tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = i
                }
                this.setState({
                    tableSource,
                    // pageTotal:result.data.totalCount
                    exchangeRate:taskArr.exchangeRate || 0
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    //删除
    deleteItem = (id) => {
        let self = this;
        Modal.confirm({
            title: "确定删除本项吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP + "", qs.stringify({
                        id
                    })).then(res => {
                        const result = res.data;
                        if (result.code == 0) {

                            resolve(result.msg)
                        } else {
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                })
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    addEdit = (item, type) => {
        // debugger
        console.log(item)
        let adrArr = item.adr ? item.adr.split('') : ['', '', '']
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.onSave(item, type)}>保存修改</Button>,
        ];
        this.setState({
            title: '添加/修改 子任务',
            visible: true,
            modadrE: adrArr[2] || '',
            modadrS: adrArr[0] || '',
            modseqNum: item.seqNum || '',
            modcycle: item.cycle || '',
            modtargetPrice: item.targetPrice || '',
            modalHtml: <ModalTask handleInputChange={this.handleInputChange} item={item} create={true} />
        })
    }
    onSave = (item, type) => {
        const { market, taskId, modadrE, modadrS, modtargetPrice, modcycle, modseqNum } = this.state
        console.log(this.state)
        switch (type) {
            case 'add':
                axios.post(DOMAIN_VIP + '/brush/trader/detail/add', qs.stringify({
                    market, taskId,
                    seqNum: modseqNum,
                    cycle: modcycle,
                    targetPrice: modtargetPrice,
                    adr: `${modadrS}:${modadrE}`
                })).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        this.setState({
                            visible: false
                        }, () => this.requestTable())

                    } else {
                        message.warning(result.msg)
                    }
                })
                break;
            case 'edit':
                axios.post(DOMAIN_VIP + '/brush/trader/detail/modify', qs.stringify({
                    market, taskId,
                    seqNum: modseqNum,
                    cycle: modcycle,
                    targetPrice: modtargetPrice,
                    adr: `${modadrS}:${modadrE}`,
                    id: item.id
                })).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        this.setState({
                            visible: false
                        }, () => this.requestTable())
                    } else {
                        message.warning(result.msg)
                    }
                })
                break;
            case 'end':
                let self = this
                console.log(item)
                Modal.confirm({
                    title: '您确定要结束吗？',
                    okText: '确定',
                    okType: 'more',
                    cancelText: '取消',
                    onOk() {
                        axios.post(DOMAIN_VIP + '/brush/trader/detail/modify', qs.stringify({
                            market:item.market, taskId:item.taskId,
                            seqNum: item.seqNum,
                            cycle: item.cycle,
                            targetPrice: item.targetPrice,
                            adr: item.adr,
                            id: item.id,
                            status: 2
                        })).then(res => {
                            const result = res.data;
                            if (result.code == 0) {
                                self.setState({
                                    visible: false
                                }, () => self.requestTable())
                            } else {
                                message.warning(result.msg)
                            }
                        })
                    },
                    onCancel() {
                        console.log('Cancel');
                    },
                })
                break;
            default:
                break
        }
    }
    restore = (item, type) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.restoreBtn(item, type)}>保存修改</Button>,
        ];
        this.setState({
            title: '添加/修改 恢复子任务',
            visible: true,
            modtargetDiff: item.targetDiff || '',
            modcycle: item.cycle || '',
            modalHtml: <ModalTask item={item} handleInputChange={this.handleInputChange} recover={true} />
        })
    }
    restoreBtn = (item, type) => {
        const { market, taskId, modadrE, modadrS, modtargetPrice, modcycle, modseqNum, modtargetDiff } = this.state
        console.log(this.state)
        switch (type) {
            case 'addRecover':
                axios.post(DOMAIN_VIP + '/brush/trader/recover/add', qs.stringify({
                    market, taskId,
                    cycle: modcycle,
                    targetDiff: modtargetDiff
                })).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        this.setState({
                            visible: false
                        }, () => this.requestTable())
                    } else {
                        message.warning(result.msg)
                    }
                })
                break;
            case 'editRecover':
                axios.post(DOMAIN_VIP + '/brush/trader/recover/modify', qs.stringify({
                    market, taskId,
                    cycle: modcycle,
                    targetDiff: modtargetDiff,
                    id: item.id
                })).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        this.setState({
                            visible: false
                        }, () => this.requestTable())
                    } else {
                        message.warning(result.msg)
                    }
                })
                break;
            default:
                break;
        }
    }
    render() {
        const { tableSource, visible, modalHtml, title, disType } = this.state
        const statusArr = ['初始', '执行中', '结束']
        return (
            <div className="right-con">
                <div className="x_panel">
                    <div className="x_content">

                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <h4 className="col-sm-4">子任务列表 &nbsp;&nbsp;BTC价格：{toThousands(this.state.exchangeRate,true)}</h4>
                            <div className="col-sm-8">
                                {disType ? <Button className="right ant-btn-primary" onClick={() => this.addEdit('', 'add')}>创建子任务</Button> : ''}
                                {disType ? <Button className="right ant-btn-primary" onClick={() => this.restore('', 'addRecover')}>创建恢复子任务</Button> : ''}
                            </div>
                        </div>
                        <div className="table-responsive">
                            <Table dataSource={tableSource} locale={{ emptyText: '暂无数据' }} bordered pagination={false}>
                                <Column title='序号' dataIndex='id' render={(text, record, index) => (
                                    <span>{index + 1}</span>
                                )} />
                                <Column title='市场' dataIndex='market' />
                                <Column title='顺序' dataIndex='seqNum' />
                                <Column title='周期(分钟)' dataIndex='cycle' />
                                <Column title='目标价格' className='moneyGreen' dataIndex='targetPrice' render={text => toThousands(text, true)} />
                                <Column title='涨跌比' dataIndex='adr' />
                                <Column title='当前目标价格' className='moneyGreen' dataIndex='curTargetPrice' render={text => toThousands(text, true)} />
                                <Column title='开始时间' dataIndex='startTime' render={(text) => text ? moment(text).format(TIMEFORMAT_ss) : '--'} />
                                <Column title='结束时间' dataIndex='endTime' render={(text) => text ? moment(text).format(TIMEFORMAT_ss) : '--'} />
                                <Column title='状态' dataIndex='status' render={text => statusArr[text]} />
                                {/* <Column title='' dataIndex='f' render={(time) => {
                                    return time ? moment(time).format(DAYFORMAT) : '--'
                                }} /> */}
                                <Column title='操作' dataIndex='key' render={(text, record, index) => {
                                    return <span>
                                        {record.targetPrice && <a href="javascript:void(0);" onClick={() => this.addEdit(record, 'edit')} className="mar10" >修改子任务</a>}
                                        {record.targetDiff && <a href="javascript:void(0);" onClick={() => this.restore(record, 'editRecover')} className="mar10" >修改恢复子任务</a>}
                                        {record.targetPrice ? record.status != 2 && <a href="javascript:void(0);" onClick={() => this.onSave(record, 'end')} className="mar10" >结束</a> : ''}
                                        {/* <a href="javascript:void(0);" onClick={() => this.deleteItem(text)}>删除</a> */}
                                    </span>

                                }} />
                            </Table>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={600}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}






































