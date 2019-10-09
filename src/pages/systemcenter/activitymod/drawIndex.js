import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss, NUMBERPOINT, PAGESIZE_200, PAGRSIZE_OPTIONS, SELECTWIDTH, TIMEFORMAT_DAYS,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button, Pagination, Select, DatePicker, message, Modal } from 'antd'
import { pageLimit,tableScroll } from '../../../utils'
import moment from 'moment'
const Big = require('big.js')
const Option = Select.Option;
const { RangePicker } = DatePicker;
const confirm = Modal.confirm;

export default class DrawIndex extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            tableList: [],
            userName: '',
            status: '',
            height:0,
            tableScroll:{
                tableId:'DWIDX',
                x_panelId:'DWIDXXX',
                defaultHeight:500,
            }
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.clickInquireState = this.clickInquireState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)         //分页
        this.onShowSizeChange = this.onShowSizeChange.bind(this)   //分页
        this.clickHide = this.clickHide.bind(this);
        this.handleChange = this.handleChange.bind(this)            //市场
        this.time_onChange = this.time_onChange.bind(this)
        this.time_onOk = this.time_onOk.bind(this)
        this.resetState = this.resetState.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changeState = this.changeState.bind(this)
        this.show_click = this.show_click.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillReceiveProps(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillUnmount(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight){
        this.setState({
            xheight
        })
    }

    requestTable(currentIndex, currentSize) {
        const { userName, status, pageIndex, pageSize } = this.state
        axios.post(DOMAIN_VIP + "/drawManage/queryList", qs.stringify({
            title: userName, 
            status: status, 
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                this.setState({
                    tableList: result.data.list,
                    pageTotal: result.data.totalCount
                })
            } else {
                message.warning(result.msg)
            }
        })
    }

    //点击分页
    changPageNum(page, pageSize) {
        this.requestTable(page, pageSize)
        this.setState({
            pageIndex: page,
            pageSize: pageSize
        })
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.requestTable(current, size)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }
    //抽奖状态
    handleChange(value) {
        this.setState({
            status: value
        })
    }
    //时间选择框
    time_onChange(value, dateString) {
        this.setState({
            beginTime: dateString[0],
            endTime: dateString[1]
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

    //查询
    clickInquireState() {
        this.requestTable()
    }
    //点击收起
    clickHide() {
        let { showHide,xheight,pageSize } = this.state;
            if(showHide&&pageSize>10){
                this.setState({
                    showHide: !showHide,
                    height:xheight,
                })
            }else{
                this.setState({
                    showHide: !showHide,
                    height:0
                })
            }
            // this.setState({
            //     showHide: !showHide,
            // })
    }
    //重置
    resetState() {
        this.setState({
            status: '',
            userName: '',
        })
    }
    //更改活动状态
    changeState(state,id){
        const that = this;
        confirm({
            title: '确定执行此操作？',
            content: '',
            onOk() {
                axios.post(DOMAIN_VIP + "/drawManage/changeState", qs.stringify({
                    status: state,
                    eventId: id
                })).then(res => {
                    const result = res.data
                    if (result.code == 0){
                        message.success("更改成功,2秒后自动刷新");
                        setTimeout(() => {
                            that.requestTable()
                        }, 2000);
                    } else {
                        message.error(result.msg);
                    }
                })
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //修改状态
    show_click(index, ids) {
        this.props.showHideClick(index, ids);
    }
    render() {
        const limitBtn = this.props.limitBtn
        const { status, userName, showHide, tableList, pageIndex, pageSize, pageTotal} = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>活动管理>抽奖管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>

                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">抽奖标题：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">抽奖状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="01">未开始</Option>
                                                <Option value="02">进行中</Option>
                                                <Option value="03">暂停</Option>
                                                <Option value="04">结束</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.clickInquireState} >查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={() => { this.show_click(1)}} >新增</Button>:''}
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title ">抽奖编号</th>
                                                <th className="column-title wid300 min_153px">抽奖标题</th>
                                                <th className="column-title">创建时间</th>
                                                <th className="column-title min_214px">抽奖时间</th>
                                                <th className="column-title">抽奖状态</th>
                                                <th className="column-title">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ? tableList.map((item, index) => {

                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex - 1) * pageSize + index + 1}</td>
                                                            <td>{item.eventId}</td>
                                                            <td><a target="_blank" href={item.url}>{item.eventTitleJson}</a></td>
                                                            <td>{moment(item.createTime).format(TIMEFORMAT)}</td>
                                                            <td>{moment(item.startTime).format(TIMEFORMAT)}至{moment(item.endTime).format(TIMEFORMAT)}</td>
                                                            <td>{item.statusView}</td>
                                                            {item.statusFlag == '01' ?
                                                                <td>
                                                                    {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" onClick={() => { this.show_click(1, item.eventId) }} className="mar20">修改</a>:''}
                                                                    {limitBtn.indexOf('changeState')>-1?<a href="javascript:void(0)" onClick={() => { this.changeState("05", item.eventId)} } className="mar20">删除</a>:''}
                                                                    {limitBtn.indexOf('changeState')>-1?<a href="javascript:void(0)" onClick={() => { this.changeState("01", item.eventId)} } >开启</a>:''}
                                                                </td> :
                                                                item.statusFlag == '02' ?
                                                                    <td>
                                                                        {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" onClick={() => { this.show_click(1, item.eventId) }} className="mar20">修改</a>:''}
                                                                        {limitBtn.indexOf('changeState')>-1?<a href="javascript:void(0)" onClick={() => { this.changeState("04", item.eventId) }}  className="mar20">停止</a>:''}
                                                                        {limitBtn.indexOf('changeState')>-1?<a href="javascript:void(0)" onClick={() => { this.changeState("03", item.eventId) }}  className="mar20">暂停</a>:''}
                                                                        {limitBtn.indexOf('getResultInfo')>-1?<a href="javascript:void(0)" onClick={() => { this.show_click(2, item.eventId) }} >查看结果</a>:''}
                                                                    </td> :
                                                                    item.statusFlag == '03' ?
                                                                        <td>
                                                                            {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" onClick={() => { this.show_click(1, item.eventId) }} className="mar20">修改</a>:''}
                                                                            {limitBtn.indexOf('changeState')>-1?<a href="javascript:void(0)" onClick={() => { this.changeState("04", item.eventId) }}  className="mar20">停止</a>:''}
                                                                            {limitBtn.indexOf('changeState')>-1?<a href="javascript:void(0)" onClick={() => { this.changeState("02", item.eventId) }}  className="mar20">开始</a>:''}
                                                                            {limitBtn.indexOf('getResultInfo')>-1?<a href="javascript:void(0)" onClick={() => { this.show_click(2, item.eventId) }} >查看结果</a>:''}
                                                                        </td> :
                                                                        <td>
                                                                            {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" onClick={() => { this.show_click(1, item.eventId) }} className="mar20">修改</a>:''}
                                                                            {limitBtn.indexOf('changeState')>-1?<a href="javascript:void(0)" onClick={() => { this.changeState("05", item.eventId) }}  className="mar20">删除</a>:''}
                                                                            {limitBtn.indexOf('getResultInfo')>-1?<a href="javascript:void(0)" onClick={() => { this.show_click(2, item.eventId) }} >查看结果</a>:''}
                                                                        </td>
                                                            }
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





























