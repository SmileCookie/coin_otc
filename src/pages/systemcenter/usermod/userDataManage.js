// 用户资料管理
import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'
import AuthenTypeList from '../../common/select/authenTypeList'
import { TIMEFORMAT, TIMEFORMAT_ss, SHOW_TIME_DEFAULT, PAGEINDEX } from '../../../conf'
import { Button, Select, message, DatePicker, Divider, Modal } from 'antd'
import { arrayTimeToStr, isArray, ckd, TE, dateToFormat, } from '../../../utils';
import { SingleInput } from '../../../components/select/asyncSelect'
import { RPicker } from '../../../components/date'
import ModalTel from './moadl/modalTel'
import ModalInfo from './moadl/modalInfo'
import UserInfoDetail from './userInfoDetail'
const { RangePicker, } = DatePicker;

@Decorator()
export default class UserDataManage extends React.Component {

    constructor(props) {
        super(props)
        this.defaultState = {
            userId: '',
            userName: '',
            des: '',
            authenType: '',
            time: [],
        }

        this.state = {
            ...this.defaultState,
            visible: false,
            modalHtml: '',
            width: '',
            footer: '',
            title: '',
            pageTabs: '1',
            detailUser: null,
            number:'1',
           
        }


    }

    componentDidMount() {
        this.requestTable()
    }

    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, time, } = this.state
        const params = Object.keys(this.defaultState).reduce((res, key) => {
            void 0 !== this.state[key] && !isArray(this.state[key]) && (res[key] = this.state[key])
            return res
        }, {
            startTime:arrayTimeToStr(time),
            endTime:arrayTimeToStr(time, 1),
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })
        const result = await this.request({ url: '/authenLog/queryList', type: 'post' }, params)
        this.setState({
            dataSource: ckd(TE(result).list),
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex,

        })
    }


    // 弹框取消
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }
    // 查看身份认证信息
    seeUserInfo = (record) => {
        let modalHtml = <ModalInfo item={record} />
        this.setState({
            visible: true,
            title: "查看身份认证信息",
            width: '1100px',
            modalHtml: modalHtml
        });
    }


    //用户详情切换
    toIssue = (detailUser = null, number = '1') => {
        this.setState({
            pageTabs: number,
            detailUser,
        })
    }

    // 列表信息
    createColumns = (pageIndex, pageSize) => {
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '用户编号', dataIndex: 'userId', render: (t, r) => <a href="javascript:void(0)" onClick={() => this.toIssue({ userId: r.userId,number:1 }, 3)}>{t}</a> },
            { title: '操作时间', dataIndex: 'time', render: t => dateToFormat(t) },
            { title: '变更类型', dataIndex: 'authenTypeName', },
            { title: '详细描述', dataIndex: 'des', },
            { title: 'IP', dataIndex: 'ip', },
            {
                title: '操作', dataIndex: 'option', render: (text, record) => (
                    <div><a href="javascript:void(0)" disabled={record.type==13?  '':'disabled'} onClick={() => this.seeUserInfo(record)}>查看身份认证信息</a></div>
                ),

            }
        ]
    }

    render() {
        const { number,pageTabs, modalHtml, width, title, visible, showHide, pageIndex, pageSize, dataSource, pageTotal, time, userId, userName, des, authenType, } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>用户管理>用户资料管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    {pageTabs == 1 &&
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            {showHide && <div className="x_panel">
                                <div className="x_content">
                                    <SingleInput title='用户编号' name='userId' value={userId} handleInputChange={this.handleInputChange} />
                                    <SingleInput title='用户名' name='userName' value={userName} handleInputChange={this.handleInputChange} />
                                    <SingleInput title='详细描述' name='des' fuzzy value={des} handleInputChange={this.handleInputChange} />
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <AuthenTypeList title="变更类型" authenType={authenType} col="3" handleChange={(v) => this.onSelectChoose(v, 'authenType')} />
                                    </div>
                                    <RPicker title='操作时间' time={time} onChangeCheckTime={(date, dateString) => this.onChangeCheckTime(date, dateString, 'time')} />
                                    <div className="col-md-4 col-sm-4 col-xs-4 right">
                                        <div className="right">
                                            <Button type="primary" onClick={() => this.requestTable(PAGEINDEX)}>查询</Button>
                                            <Button type="primary" onClick={this.resetState}>重置</Button>
                                            <Button type="primary" onClick={() => this.toIssue({number:1}, 2)}>修改手机号</Button>
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
                        </div>}

                    {pageTabs == 2 && <ModalTel number={number} permissList={this.props.permissList} toIssue={this.toIssue} user={this.state.detailUser} requestTable={this.requestTable} handleCancel={this.handleCancel} />}
                    {pageTabs == 3 &&
                        <UserInfoDetail number={number} permissList={this.props.permissList} toIssue={this.toIssue} user={this.state.detailUser} />
                    }
                </div>
                < Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 60 }}
                    onCancel={this.handleCancel}
                    footer={null}
                >
                    {modalHtml}
                </Modal >

            </div>
        )
    }
}




