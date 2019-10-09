import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss, NUMBERPOINT, PAGESIZE_200, PAGRSIZE_OPTIONS, SELECTWIDTH, TIMEFORMAT_DAYS,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button, Pagination, Select, DatePicker, Modal, message } from 'antd'
import { pageLimit } from "../../../utils"
import moment from 'moment'
const Big = require('big.js')
const Option = Select.Option;
const { RangePicker } = DatePicker;
const confirm = Modal.confirm;

export default class NewsIndex extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            endTime: '',
            beginTime: '',
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            tableList: [],
            userName: '',
            status: '',
            userTitle: '',
            langs: '',
            types: '',
            time: [],
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.clickInquireState = this.clickInquireState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)         //分页
        this.onShowSizeChange = this.onShowSizeChange.bind(this)   //分页
        this.clickHide = this.clickHide.bind(this);
        this.status_handleChange = this.status_handleChange.bind(this)            //
        this.langs_handleChange = this.langs_handleChange.bind(this)
        this.type_handleChange = this.type_handleChange.bind(this)
        this.time_onChange = this.time_onChange.bind(this)
        this.time_onOk = this.time_onOk.bind(this)
        this.resetState = this.resetState.bind(this)
        this.deleteNews = this.deleteNews.bind(this)
        this.newsTop = this.newsTop.bind(this)
        this.show_click = this.show_click.bind(this)
        this.newsNotTop = this.newsNotTop.bind(this)
        // this.business_handleChange = this.business_handleChange.bind(this)
    }

    componentDidMount() {
        this.resetInquire();
    }

    //点击分页
    changPageNum(page, pageSize) {
        this.resetInquire(page, pageSize);
        this.setState({
            pageIndex: page,
            pageSize: pageSize
        })
    }
    //分页的 pagesize 改变时
    onShowSizeChange(page, pageSize) {
        this.resetInquire(page, pageSize);
        this.setState({
            pageIndex: page,
            pageSize: pageSize
        })
    }
    //类型
    status_handleChange(value) {
        this.setState({
            status: value
        })
    }
    //语言
    langs_handleChange(value) {
        this.setState({
            langs: value
        })
    }
    //公告类型
    type_handleChange(value) {
        this.setState({
            types: value
        })
    }
    //交易类型
    // business_handleChange(value){
    //     this.setState({
    //         dealType:value
    //     })
    // }
    //时间选择框
    time_onChange(value, dateString) {
        this.setState({
            beginTime: dateString[0],
            endTime: dateString[1],
            time: value
        })
    }
    time_onOk(value) {
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    resetInquire(currentIndex, currentSize) {
        const { userTitle, status, langs, types, userName, beginTime, endTime, tableList, pageIndex, pageSize, } = this.state;
        axios.post(DOMAIN_VIP + "/news/newsManager", qs.stringify({
            title: userTitle,
            type: status,
            language: langs,
            pubTimeStart: beginTime,
            pubTimeEnd: endTime,
            adminName: userName,
            noticeType: types,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    tableList: result.page.list,
                    pageTotal: result.page.totalCount
                })

            }
        })
    }
    //修改状态
    show_click(index,ids) {
        this.props.showHideClick(index, ids);
    }

    //查询
    clickInquireState() {
        this.setState({
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
        })
        this.resetInquire(PAGEINDEX, PAGESIZE);
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    //重置
    resetState() {
        this.setState({
            endTime: '',
            beginTime: '',
            userName: '',
            status: '',
            userTitle: '',
            langs: '',
            types: '',
            time: [],
        })
    }
    //置顶
    newsTop(value) {
        axios.post(DOMAIN_VIP + "/news/newsManagerForTop", qs.stringify({
            id: value,
        })).then(res => {
            const result = res.data;
            if (result.status == 1) {
                message.success('置顶成功');
                this.resetInquire();
            }
        });;
    }
    //取消置顶
    newsNotTop(value){
        axios.post(DOMAIN_VIP +"/news/newsManagerForNotTop",qs.stringify({
            id:value
        })).then(res => {
            const result = res.data
            if (result.status == 1) {
                message.success('取消置顶成功');
                this.resetInquire();
            }
        })
    }
    //删除
    deleteNews(value, title) {
        const Rthis = this;
        confirm({
            title: '你确定要删除本条吗?',
            content: title,
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + "/news/newsManagerForDelete", qs.stringify({
                    id: value,
                })).then(res => {
                    const result = res.data;
                    console.log(result)
                    if (result.status == 1) {
                        Rthis.resetInquire()
                    }
                });
            },
            onCancel() {
                console.log('Cancel');
            },
        });

    }

    render() {
        Big.RM = 0;
        const {limitBtn} = this.props
        // console.log(this.props)
        const { status, userName, showHide, time, tableList, userTitle, langs, types, pageTotal, pageIndex, pageSize, } = this.state;
        let page_index = new Big(pageIndex);
        let page_size = new Big(pageSize);
        let ones = new Big(1);
        const now_page = page_index.times(page_size).minus(page_size).plus(ones);
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>系统管理>新闻管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">标题：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userTitle" value={userTitle} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.status_handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">公告</Option>
                                                <Option value="2">新闻</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">交易类型：</label>
                                        <div className="col-sm-8">
                                            <Select name="dealType" value={dealType} style={{width:SELECTWIDTH}}  onChange={this.business_handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="0">币币</Option>
                                                <Option value="1">OTC</Option>                                            
                                            </Select>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">语言：</label>
                                        <div className="col-sm-8">
                                            <Select value={langs} style={{ width: SELECTWIDTH }} onChange={this.langs_handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="cn">简体中文</Option>
                                                <Option value="hk">繁体中文</Option>
                                                <Option value="en">ENGLISH</Option>
                                                <Option value="jp">日语</Option>
                                                <Option value="kr">韩语</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">公告类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={types} style={{ width: SELECTWIDTH }} onChange={this.type_handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">新币上线</Option>
                                                <Option value="2">系统维护</Option>
                                                <Option value="3">最新活动</Option>
                                                <Option value="4">平台动态</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发布人：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4 mb10">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发布时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{ format: TIMEFORMAT_DAYS_ss }}
                                                format={TIMEFORMAT_ss}
                                                onOk={this.time_onOk}
                                                onChange={this.time_onChange}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>


                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.clickInquireState} >查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        {limitBtn.indexOf('editItemOfNew')>-1?<Button type="primary" onClick={() => { this.show_click(1) }}>发布</Button>:''}
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title min_69px">序号</th>
                                                <th className="column-title min_68px">编号</th>
                                                <th className="column-title">标题</th>
                                                <th className="column-title min_68px">类型</th>
                                                <th className="column-title min_68px">语言</th>
                                                <th className="column-title">发布时间</th>
                                                <th className="column-title min_82px">发布人</th>
                                                <th className="column-title min_96px">公告类型</th>
                                                <th className="column-title min_220px">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ? tableList.map((item, index) => {

                                                    return (
                                                        <tr key={index}>
                                                            <td>{new Big(index).plus(now_page).toFixed()}</td>
                                                            <td>{item.id}</td>
                                                            <td className="td_width_300">{item.title}</td>
                                                            <td>{item.type == 1 ? "公告" : "新闻"}</td>
                                                            <td>{item.languageStr}</td>
                                                            <td>{moment(item.pubTime).format(TIMEFORMAT)}</td>
                                                            <td>{item.adminName}</td>
                                                            <td>
                                                                {
                                                                    (() => {
                                                                        switch (item.noticeType) {
                                                                            case 1:
                                                                                return '新币上线'
                                                                                break;
                                                                            case 2:
                                                                                return '系统维护'
                                                                                break;
                                                                            case 3:
                                                                                return '最新活动'
                                                                                break;
                                                                            case 4:
                                                                                return '平台动态'
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                    })()
                                                                }

                                                            </td>
                                                            <td>
                                                                {limitBtn.indexOf('editItemOfNew')>-1?<Button size="small" type="primary" onClick={() => { this.show_click(1, item.id) }} >修改</Button>:''}
                                                                {limitBtn.indexOf('deleteItemOfNew')>-1?<Button size="small" type="primary" onClick={() => { this.deleteNews(item.id, item.title) }} >删除</Button>:''}
                                                                {
                                                                    limitBtn.indexOf('topItemOfNew')>-1?(item.top ?
                                                                        <Button size="small" type="primary" onClick={() => { this.newsNotTop(item.id) }} >取消置顶</Button>
                                                                        : 
                                                                        <Button size="small" type="primary" onClick={() => { this.newsTop(item.id) }} >置顶</Button>)
                                                                    :''
                                                                }
                                                                
                                                            </td>
                                                        </tr>
                                                    )
                                                }) : <tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                <div className="pagation-box">
                                    {
                                        pageTotal > 0 && <Pagination
                                            size="small"
                                            current={pageIndex}
                                            total={pageTotal}
                                            onChange={this.changPageNum}
                                            showTotal={total => `总共 ${total} 条`}
                                            onShowSizeChange={this.onShowSizeChange}
                                            showSizeChanger

                                            showQuickJumper
                                            pageSizeOptions={PAGRSIZE_OPTIONS20}
                                            defaultPageSize={PAGESIZE}
                                             />
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}





























