import axios from '../../../utils/fetch'
import { PAGEINDEX, PAGESIZE, PAGRSIZE_OPTIONS20, TIMEFORMAT_ss, } from '../../../conf'
import { Button, Modal, message } from 'antd'
import { toThousands, islessBodyWidth, pageLimit, } from '../../../utils'
import CommonTable from '../../common/table/commonTable'
import Decorator from '../../decorator'

const platType = ['邮件', '大陆短信', '港澳台+国际短信']

@Decorator()
export default class MessageManagePlatform extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            limitBtn: [],
            tableSource: [],
            visible: false,
            weight: ''

        }
    }
    componentDidMount() {
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('msgPlat', this.props.permissList)
        })
    }

    requestTable = async (currIndex, currSize) => {
        const result = await this.request({ url: '/sys/msg/plat/list' })
        this.setState({
            tableSource: result.list
        })
    }
    operate = (params, url, title) => {
        let self = this;
        Modal.confirm({
            title: `你确定要${title}吗？`,
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.request({ url: `/sys/msg/plat/${url}`, type: 'post' }, params).then(res => {
                    self.requestTable()
                })

            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    editWeight = (id) => {
        this.footer = [
            <Button key="back" onClick={this.onhandleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.editWeightBtn(id)}>
                保存
            </Button>,
        ]
        this.setState({ visible: true,weight:'' })

    }
    editWeightBtn = (id) => {
        const { weight } = this.state
        if(weight == ''){
            message.warning('权重不能为空')
            return 
        }
        this.request({ url: `/sys/msg/plat/weight`, type: 'post', isP: true }, { id, weight }).then(res => {
            this.setState({ visible: false })
            this.requestTable()
        })
    }
    onhandleCancel = () => {
        this.setState({visible:false})
    }
    createColumns = (pageIndex, pageSize) => {
        const { limitBtn, weight } = this.state

        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => index + 1 },
            // { title: '主键', dataIndex: 'id' },
            { title: '平台编码', dataIndex: 'code' },
            { title: '平台名称', dataIndex: 'platname' },
            { title: '平台类型', dataIndex: 'type', render: text => platType[text] },
            { title: '是否开启', dataIndex: 'open', render: text => text == 0 ? '关闭' : '开启' },
            { title: '权重', dataIndex: 'weight' },
            {
                title: '操作', dataIndex: 'op', render: (text, record) => {
                    return <span>
                        {limitBtn.indexOf('open') > -1 && record.open == 0 && <a href="javascript:void(0);" className='mar10' onClick={() => this.operate({ id: record.id }, 'open', '启用')}>启用</a>}
                        {limitBtn.indexOf('close') > -1 && record.open == 1 && <a href="javascript:void(0);" className='mar10' onClick={() => this.operate({ id: record.id }, 'close', '停用')}>停用</a>}
                        {limitBtn.indexOf('weight') > -1 && <a href="javascript:void(0);" onClick={() => this.editWeight(record.id, 'weight', '修改权重')}>修改权重</a>}
                    </span>
                }
            },
        ]
    }
    render() {
        const { showHide, pageIndex, pageSize, pageTotal, tableSource, visible, weight } = this.state
        this.dialogHtml = <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
            <div className="form-group">
                <label className="col-sm-3 control-label">权重<i>*</i></label>
                <div className="col-sm-8">
                    <input type="text" className="form-control" name="weight" value={weight} onChange={this.handleInputChange} />
                </div>
            </div>
        </div>
        return (
            <div className="right-con">
                <div className="page-title">
                    {/* <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i> */}
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <CommonTable
                                        dataSource={tableSource}
                                        columns={this.createColumns(pageIndex, pageSize)}
                                        requestTable={this.requestTable}
                                        scroll={islessBodyWidth() ? { x: 1800 } : {}}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title='修改权重'
                    onOk={this.handleOk}
                    onCancel={this.onhandleCancel}
                    width='500px'
                    footer={this.footer}
                >
                    {
                        this.dialogHtml
                    }
                </Modal>
            </div>
        )
    }
}