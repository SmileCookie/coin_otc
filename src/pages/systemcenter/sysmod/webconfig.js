// 网站底部配置
import React from 'react'
import decorator from '../../decorator'
import { DOMAIN_VIP, PAGEINDEX, SELECTWIDTH } from '../../../conf'
import { Button, message, Select, Modal } from 'antd'
import CommonTable from '../../common/table/commonTable'
import ModalEdite from './modal/modalEdite'
import axios from '../../../utils/fetch'
const { Option } = Select;


@decorator()
export default class WebConfig extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            name: ''

        }
        this.state = {
            ...this.defaultState,
            selectList: [<Option key='0' value=''>请选择</Option>],
            visible: false,
            width: '',
            modalHtml: '',
            title: '',
            loading: false
        }
    }

    componentDidMount() {
        this.requestTable()
        //this.selectData()
    }

    handleCancel = () => {
        this.setState({
            visible: false
        })
    }

    requestTable = async (currentIndex, currentSize) => {
        const { pageIndex, pageSize, name } = this.state
        const params = { pageIndex, pageSize, name }
        const result = await this.request({ url: '/webBottom/list', type: 'post' }, params)
        this.setState({
            dataSource: result.list,
            pageTotal: result.totalCount,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize
        })
    }

    // 查询按钮
    inquireBtn = () => {
        this.setState({
            pageIndex: PAGEINDEX
        }, () => this.requestTable())

    }

    //修改/新增弹窗
    showModal = (item) => {
        const titleName = item.id ? '修改数据' : '新增数据'
        this.setState({
            visible: true,
            width: '600px',
            title: titleName,
            modalHtml: <ModalEdite item={item.id ? item : ''} dataSource={this.state.dataSource} selectList={this.state.selectList} handleCancel={this.handleCancel} requestTable={this.requestTable} />,

        })
    }


    // 删除数据
    deleteData = (id) => {
        const self = this
        Modal.confirm({
            title: '您确定要删除吗?',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                self.deleteDataBtn(id)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    deleteDataBtn = async (id) => {
        await this.request({ url: '/webBottom/del', isP: true }, { id })
        this.requestTable()
    }

    //名称列表
    // selectData = () => {
    //     axios.get(DOMAIN_VIP + '/webBottom/name').then(res => {
    //         const result = res.data;
    //         let defaultList = [];
    //         if (result.code == 0) {
    //             for (let i = 0; i < result.data.length; i++) {
    //                 defaultList.push(<Option key={i + 1} value={result.data[i]}>{result.data[i]}</Option>)
    //             }
    //             this.setState({
    //                 selectList: [<Option key='0' value=''>请选择</Option>, ...defaultList]
    //             });
    //         }
    //     })
    // }

    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '名称', dataIndex: 'name', },
            { title: '地址', dataIndex: 'address', },
            {
                title: '图片', dataIndex: 'image', render: (text, record, index) => {
                    return (
                        <div>
                            <img src={record.image} alt="图片" style={{ maxWidth: 100 }} />
                        </div>
                    )
                }
            },
            { title: '显示顺序', dataIndex: 'sort', },
            {
                title: '操作', dataIndex: 'option', render: (text, record) => (

                    <div>
                        <a href="javascript:void(0)" style={{ marginRight: 6 }} onClick={() => this.showModal(record)}>修改</a>
                        <a href="javascript:void(0)" onClick={() => this.deleteData(record.id)}>删除</a>

                    </div>

                ),

            }
        ]
    }


    render() {
        const { showHide, pageSize, pageIndex, dataSource, pageTotal, name, selectList, title, visible, width, modalHtml, } = this.state
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
                                <div className="col-mg-3 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">名称：</label>
                                        <div className="col-sm-8">
                                            <Select name="name" value={name} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'name')}>
                                                {/* {selectList} */}
                                                <Option value=''>请选择</Option>
                                                <Option value='facebook'>facebook</Option>
                                                <Option value='t.me'>t.me</Option>
                                                <Option value='twitter'>twitter</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquireBtn} >查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        <Button type="primary" onClick={this.showModal}>新增</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
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
                    style={{ top: 50 }}
                    onCancel={this.handleCancel}
                    footer={null}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}



