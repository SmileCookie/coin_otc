// 币币账户管理
import React from 'react'
import decorator from '../../decorator'
import { DOMAIN_VIP, PAGEINDEX, SELECTWIDTH } from '../../../conf'
import { Button, message, Select, Modal, Checkbox } from 'antd'
import CommonTable from '../../common/table/commonTable'
import { dateToFormat, ckd } from '../../../utils'
const { Option } = Select;


@decorator()
export default class CurrencyManagement extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            name: ''
        }
        this.state = {
            ...this.defaultState,
            visible: false,
            width: '',
            modalHtml: '',
            title: '',

        }
    }

    componentDidMount() {
        this.requestTable()
    }

    handleCancel = () => {
        this.setState({
            visible: false
        })
    }

    requestTable = async (currentIndex, currentSize) => {
        const result = await this.request({ url: '/brush/config/market/buysell' })
        this.setState({
            dataSource: ckd(result),
        })
    }

    // 查询按钮
    inquireBtn = () => {
        this.setState({
            pageIndex: PAGEINDEX
        }, () => this.requestTable())

    }
    // 修改单条买、卖盘保值弹窗
    update = (item, num, type) => {
        const state = num ? '停止' : '启动'
        const self = this
        Modal.confirm({
            title: `您确定${state}吗?`,
            okText: '确定',
            okType: 'success',
            cancelText: '取消',
            onOk() {
                // if (type === 'buy') {
                //     if (item.isBuy === num) {
                //         message.error('操作重复')
                //         return
                //     }
                // }
                // if (type === 'sell') {
                //     if (item.isSell === num) {
                //         message.error('操作重复')
                //         return
                //     }
                // }

                self.updateBtn(item, num, type)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    // 修改单条买、卖盘保值状态配置按钮
    updateBtn = async (item, num, type) => {
        const { id, market, isBuy, isSell, } = item
        const buyParams = { id, market, isBuy: num, isSell,flag:0 }
        const sellParams = { id, market, isBuy, isSell: num,flag:1 }
        const params = type == 'buy' ? buyParams : sellParams
        await this.request({ url: '/brush/config/market/buysell/modify', type: 'post' }, params)
        this.requestTable()
    }


    // 修改全部买、卖盘保值弹窗
    updateAll = (flag, type) => {
        const typeName = flag == 0 ? '买盘' : '卖盘'
        const state = type ? '停止' : '启动'
        const self = this
        Modal.confirm({
            title: `您确定${typeName}全部${state}吗?`,
            okText: '确定',
            okType: 'success',
            cancelText: '取消',
            onOk() {

                self.updateBtnAll(flag, type)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    //修改全部买、卖盘保值状态按钮
    updateBtnAll = async (flag, type) => {
        const { dataSource } = this.state
        let idArr = [];

        for (let i in dataSource) {
            if (flag == 0) {
                if (dataSource[i].isBuy != type) {
                    idArr.push(dataSource[i].id)
                }
            } else if (flag == 1) {
                if (dataSource[i].isSell != type) {
                    idArr.push(dataSource[i].id)
                }
            }

        }
        if(!idArr.length) {
            message.warning("操作重复！")
            return false

        }
        
        const params={flag,type}

        await this.request({ url: '/brush/config/market/buysell/modifyAll', type: 'post' }, params)
        this.requestTable()
    }

    createColumns = (pageIndex, pageSize) => {
        const ray = { background: '#f0f0f0' }
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '交易市场', dataIndex: 'market', },
            { title: '保值平台', dataIndex: 'platforms', },
            {
                title: '买盘保值', dataIndex: 'o', render: (text, record, index) => {
                   return (
                        <div>
                            <Button type={record.isBuy ? 'primary':'' } disabled={record.isBuy ? false : true} onClick={() => this.update(record, 0, 'buy')} >启动</Button>
                            <Button type={record.isBuy ? '':'primary' } disabled={record.isBuy ? true : false} onClick={() => this.update(record, 1, 'buy')} >停止</Button>
                        </div>
                    )
                }
            },
            { title: '操作时间', dataIndex: 'updateBuyTime', render: t => dateToFormat(t) },
            {
                title: '卖盘保值', dataIndex: 'p', render: (text, record, index) => {
                    return (
                        <div>
                            <Button type={record.isSell ? 'primary' : ''} disabled={record.isSell ? false : true} onClick={() => this.update(record, 0, 'sell')} >启动</Button>
                            <Button type={record.isSell ?  '' : 'primary'} disabled={record.isSell ? true : false} onClick={() => this.update(record, 1, 'sell')} >停止</Button>
                        </div>
                    )
                }
            },
            { title: '操作时间', dataIndex: 'updateSellTime', render: t => dateToFormat(t) },
        ]
    }


    render() {
        const { showHide, pageSize, pageIndex, dataSource,} = this.state
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
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={() => this.updateAll(0, 0)} >买盘全启动</Button>
                                        <Button type="primary" onClick={() => this.updateAll(0, 1)} >买盘全停止</Button>
                                        <Button type="primary" onClick={() => this.updateAll(1, 0)} >卖盘全启动</Button>
                                        <Button type="primary" onClick={() => this.updateAll(1, 1)} >卖盘全停止</Button>

                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
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



