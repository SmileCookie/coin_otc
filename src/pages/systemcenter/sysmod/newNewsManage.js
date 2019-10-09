import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, DEFAULTVALUE, PAGRSIZE_OPTIONS20, TIMEFORMAT, TIMEFORMAT_ss, SELECTWIDTH, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER } from '../../../conf'
import { Button, Select, DatePicker, Modal, message } from 'antd'
import NewNewsDistribute from './newNewsDistribute'
const { RangePicker } = DatePicker;
const Option = Select.Option
//公告类型
const _placard = {
    1: '新币上线',
    2: '系统维护',
    3: '最新活动',
    4: '平台动态'
}
const _types = {
    1: '公告',
    2: '新闻'
}
const _langs = {
    cn: '简体中文',
    hk: '繁体中文',
    en: 'ENGLISH',
    jp: '日语',
    kr: '韩语'
}
const objToArr = o => Object.keys(o).map(v => ({ k: v, c: o[v] }))

const SeOp = ({ title, value, onSelectChoose, ops }) => (
    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
        <div className="form-group">
            <label className="col-sm-3 control-label">{title}：</label>
            <div className="col-sm-9">
                <Select defaultValue='' value={value} style={{ width: SELECTWIDTH }} onChange={onSelectChoose} >
                    {[<Option key='' value=''>请选择</Option>, ...ops.map(op => <Option key={op.k} value={op.k}>{op.c}</Option>)]}
                </Select>
            </div>
        </div>
    </div>
)


@Decorator({lb:'news'})
export default class NewNewsManage extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            type: '',
            language: '',
            noticeType: '',
            time: [],
            adminName: '',
            title: '',
            remark: '',
        }
        this.state = {
            ...this.defaultState,
            pageTabs: true,
            dataSource:[],
            id:null,
        }
    }
    componentDidMount() {
        this.requestTable()
    }
    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time, language, noticeType, remark, adminName, type} = this.state
        let params = {
            language, noticeType, remark, adminName, type,
            pubTimeStart: time.length ? moment(time[0]).format(TIMEFORMAT_ss) : '',
            pubTimeEnd: time.length ? moment(time[1]).format(TIMEFORMAT_ss) : '',
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: '/news/v2/list'}, params)
        this.setState({
            dataSource:result.list,
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })

    }
    onOperate = async (id, url,title) => {
        Modal.confirm({
            title:`你确定要${title}吗？`,
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk:() => {
                this.request({ url, type: 'post', isP: true }, { id }).then(() => this.requestTable())                
            },
            onCancel(){
                console.log('Cancel')
            } 
        })
    }
    onEdit = async id => {
        this.toIssue()
        this.setState({id})
        // await this.request({url:'/news/v2/detail',type:'post'},{id})
    }
    createColumns = (pageIndex, pageSize) => {
        const { limitBtn } = this.state
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '基础ID', dataIndex: 'id' },
            { title: '类型', dataIndex: 'type', render: t => _types[t] || '--' },
            { title: '公告类型', dataIndex: 'noticeType', render: t => _placard[t] || '--' },
            { title: '说明', dataIndex: 'remark', },
            { title: '语言类型', dataIndex: 'hasLan', },
            { title: '发布时间', dataIndex: 'pubTime', render: text => text ? moment(text).format(TIMEFORMAT_ss) : '' },
            { title: '发布人', dataIndex: 'adminName' },
            { title: '是否置顶', dataIndex: 'top', render: t => t ? '是' : '否' },
            {
                title: '操作', dataIndex: 'op', render: (t, r) => <span>
                    {limitBtn.includes('editItemOfNew') && <a href='javascript:void(0);' className="mar10" onClick={() => this.toIssue(r.id)} >修改</a>}
                    {limitBtn.includes('deleteItemOfNew') && <a href='javascript:void(0);' className="mar10" onClick={() => this.onOperate(r.id, '/news/v2/delete','删除')} >删除</a>}
                    {limitBtn.includes('topItemOfNew') && r.top ?
                        <a href='javascript:void(0);' className="mar10" onClick={() => this.onOperate(r.id, '/news/v2/cancel/top','取消置顶')} >取消置顶</a>
                        :
                        <a href='javascript:void(0);' className="mar10" onClick={() => this.onOperate(r.id, '/news/v2/top','置顶')} >置顶</a>
                    }
                </span>
            },
        ]
    }
    toIssue = (id) => { this.setState({ pageTabs: !this.state.pageTabs,id: id ? id : null }) }
    render() {
        const { showHide, pageIndex, pageSize, pageTotal, dataSource, type, language, noticeType, time, pageTabs, adminName, remark, id, limitBtn } = this.state
        return (
            <div className="right-con">
                   {pageTabs?
                    <div>
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
                                                <label className="col-sm-3 control-label">说明：</label>
                                                <div className="col-sm-8 ">
                                                    <input type="text" className="form-control" name="remark" value={remark} onChange={this.handleInputChange} />
                                                    <b className="icon-fuzzy">%</b>
                                                </div>
                                            </div>
                                        </div>
                                        <SeOp title='类型' value={type} onSelectChoose={v => this.onSelectChoose(v, 'type')} ops={objToArr(_types)} />
                                        <SeOp title='语言' value={language} onSelectChoose={v => this.onSelectChoose(v, 'language')} ops={objToArr(_langs)} />
                                        <SeOp title='公告类型' value={noticeType} onSelectChoose={v => this.onSelectChoose(v, 'noticeType')} ops={objToArr(_placard)} />
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">发布人：</label>
                                                <div className="col-sm-8 ">
                                                    <input type="text" className="form-control" name="adminName" value={adminName} onChange={this.handleInputChange} />
                                                    <b className="icon-fuzzy">%</b>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">成交时间：</label>
                                                <div className="col-sm-8">
                                                    <RangePicker
                                                        showTime={{
                                                            defaultValue: SHOW_TIME_DEFAULT
                                                        }}
                                                        format={TIMEFORMAT_ss}
                                                        placeholder={TIME_PLACEHOLDER}
                                                        onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'time')}
                                                        value={time}
                                                    />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-md-4 col-sm-4 col-xs-4 right">
                                            <div className="right">
                                                <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>
                                                <Button type="primary" onClick={this.resetState}>重置</Button>
                                                {limitBtn.includes('editItemOfNew') && <Button type="primary" onClick={() => this.toIssue()}>发布</Button>}
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
                    </div>
                    :
                    <NewNewsDistribute id={id} toIssue={this.toIssue} SeOp={{ _types, _placard }} requestTable ={this.requestTable} /> } 
            </div>
        )
    }
}