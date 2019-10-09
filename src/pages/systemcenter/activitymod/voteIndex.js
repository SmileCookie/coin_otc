import React from 'react'
import axios from 'axios'
import qs from 'qs'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss, NUMBERPOINT, PAGESIZE_200, PAGRSIZE_OPTIONS, SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button, Pagination, Select, DatePicker, message, Modal } from 'antd'
import moment from 'moment'
import { pageLimit,tableScroll } from '../../../utils'
const Big = require('big.js')
const Option = Select.Option;
const { RangePicker } = DatePicker;
const confirm = Modal.confirm;

export default class VoteIndex extends React.Component {

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
            time:[],
            height:0,
            tableScroll:{
                tableId:'VOTIEX',
                x_panelId:'VOTIEXX',
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
        this.show_click = this.show_click.bind(this)
        this.setVoteStatus = this.setVoteStatus.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.resetVoteList();
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
    //点击分页
    changPageNum(page, pageSize) {
        this.resetVoteList(page, pageSize);
        this.setState({
            pageIndex: page,
            pageSize: pageSize
        })
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.resetVoteList(current, size);
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }
    //投票状态
    handleChange(value) {
        this.setState({
            status: value
        })
    }
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

    resetVoteList(currentIndex, currentSize) {
        const { userName, status, beginTime, endTime, pageIndex, pageSize, pageTotal } = this.state
        axios.post(DOMAIN_VIP + "/voteManage/query", qs.stringify({
            activityNameJson: userName,
            state: status,
            lan: "cn",
            beginTime: beginTime,
            endTime: endTime,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    tableList: result.data.list,
                    pageTotal: result.data.totalCount
                })
            }
        })
    }

    //查询
    clickInquireState() {
        this.resetVoteList();
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
            showHide: true,
            endTime: '',
            beginTime: '',
            userName: '',
            status: '',
            time: [],
        })
    }
    //修改状态
    show_click(index,ids){
        this.props.showHideClick(index, ids);
    }
    //改变活动状态
    setVoteStatus(activityId,index){
        const that = this;
        confirm({
            title: '确定执行此操作？',
            content: '',
            onOk() {
                axios.post(DOMAIN_VIP + "/voteManage/changeState", qs.stringify({
                    state: index,
                    activityId: activityId
                })).then(res => {
                    const result = res.data
                    if (result.code == 0) {
                        message.success("更改成功");
                        that.resetVoteList();
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

    render() {
        const limitBtn = this.props.limitBtn
        const { status, userName, showHide, tableList, pageIndex, pageSize, pageTotal, time } = this.state
        let page_index = new Big(pageIndex);
        let page_size = new Big(pageSize);
        let ones = new Big(1);
        const now_page = page_index.times(page_size).minus(page_size).plus(ones);
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 活动管理 > 投票管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">投票标题：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">投票状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="0">未开始</Option>
                                                <Option value="1">进行中</Option>
                                                <Option value="2">暂停</Option>
                                                <Option value="3">结束</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4 mb10">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">创建时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                value={time}
                                                showTime={{ format: TIMEFORMAT_DAYS_ss }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.time_onChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.clickInquireState} >查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={ () => {this.show_click(1)} } >新增</Button>:''}
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
                                                <th className="column-title min_153px">投票编号</th>
                                                <th className="column-title">投票标题</th>
                                                <th className="column-title">创建时间</th>
                                                <th className="column-title min_214px">投票时间</th>
                                                <th className="column-title">投票状态</th>
                                                <th className="column-title">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ? tableList.map((item, index) => {

                                                    return (
                                                        <tr key={index}>
                                                            <td>{new Big(index).plus(now_page).toFixed()}</td>
                                                            <td>{item.activityId}</td>
                                                            <td > <a target="_blank" href={`${item.url}?preview_state=${item.activityId}`}>{item.activityNameJson}</a> </td>
                                                            <td>{moment(item.createTime).format(TIMEFORMAT)}</td>
                                                            <td>{moment(item.startTime).format(TIMEFORMAT)}至{moment(item.endTime).format(TIMEFORMAT)}</td>
                                                            <td>
                                                                {
                                                                    (() => {
                                                                        switch (item.state) {
                                                                            case 0:
                                                                                return '未开始'
                                                                                break;
                                                                            case 1:
                                                                                return '进行中'
                                                                                break;
                                                                            case 2:
                                                                                return '暂停'
                                                                                break;
                                                                            case 3:
                                                                                return '结束'
                                                                                break;
                                                                            case 4:
                                                                                return '删除'
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                    })()
                                                                }
                                                            </td>
                                                            <td>
                                                                {
                                                                    (() => {
                                                                        switch (item.state) {
                                                                            case 0:
                                                                                return (
                                                                                    <div>
                                                                                        {limitBtn.indexOf('changeState')>-1?
                                                                                        <Button 
                                                                                            onClick={() => { this.setVoteStatus(item.activityId,1) }} 
                                                                                            size="small" type="primary" >开启</Button>:''}
                                                                                        {limitBtn.indexOf('update')>-1?
                                                                                        <Button 
                                                                                            onClick={() => { this.show_click(2, item.activityId) }}
                                                                                            size="small" type="primary" >修改</Button>:''}
                                                                                        {limitBtn.indexOf('changeState')>-1?<Button 
                                                                                            onClick={() => { this.setVoteStatus(item.activityId, 4) }} 
                                                                                            size="small" type="primary">删除</Button>:''}
                                                                                    </div>
                                                                                )
                                                                                break;
                                                                            case 1:
                                                                                return (
                                                                                    <div>
                                                                                        {limitBtn.indexOf('changeState')>-1?<Button 
                                                                                            onClick={() => { this.setVoteStatus(item.activityId, 2) }} 
                                                                                            size="small" type="primary" >暂停</Button>:''}
                                                                                        {limitBtn.indexOf('changeState')>-1?<Button 
                                                                                            onClick={() => { this.setVoteStatus(item.activityId, 3) }} 
                                                                                            size="small" type="primary" >停止</Button>:''}
                                                                                        {limitBtn.indexOf('update')>-1?
                                                                                        <Button 
                                                                                            onClick={() => { this.show_click(2, item.activityId) }}
                                                                                            size="small" type="primary" >修改</Button>:''}
                                                                                        {limitBtn.indexOf('voteResult')>-1?
                                                                                        <Button 
                                                                                            onClick={() => { this.show_click(3, item.activityId) }}
                                                                                            size="small" type="primary" >查看结果</Button>:''}
                                                                                    </div>
                                                                                )
                                                                                break;
                                                                            case 2:
                                                                                return (
                                                                                    <div>
                                                                                        {limitBtn.indexOf('changeState')>-1?
                                                                                        <Button 
                                                                                            onClick={() => { this.setVoteStatus(item.activityId, 1) }} 
                                                                                            size="small" type="primary" >开启</Button>:''}
                                                                                        {limitBtn.indexOf('changeState')>-1?
                                                                                        <Button 
                                                                                            onClick={() => { this.setVoteStatus(item.activityId, 3) }} 
                                                                                            size="small" type="primary" >停止</Button>:''}
                                                                                        {limitBtn.indexOf('voteResult')>-1?
                                                                                        <Button 
                                                                                        onClick={() => { this.show_click(3, item.activityId)}}
                                                                                        size="small" type="primary" >查看结果</Button>:''}
                                                                                        {limitBtn.indexOf('update')>-1?
                                                                                        <Button 
                                                                                            onClick={() => { this.show_click(2, item.activityId) }}
                                                                                            size="small" type="primary" >修改</Button>:''}
                                                                                    </div>
                                                                                )
                                                                                break;
                                                                            default:
                                                                                return (
                                                                                    <div>
                                                                                        {limitBtn.indexOf('voteResult')>-1?
                                                                                        <Button 
                                                                                            onClick={() => { this.show_click(3, item.activityId) }}
                                                                                            size="small" type="primary" >查看结果</Button>:''}
                                                                                        {limitBtn.indexOf('update')>-1?
                                                                                        <Button 
                                                                                            onClick={() => { this.show_click(2, item.activityId) }}
                                                                                            size="small" type="primary" >修改</Button>:''}
                                                                                        {limitBtn.indexOf('changeState')>-1?
                                                                                        <Button 
                                                                                            onClick={() => { this.setVoteStatus(item.activityId, 4) }} 
                                                                                            size="small" type="primary" >删除</Button>:''}
                                                                                    </div>
                                                                                )
                                                                                break;
                                                                        }
                                                                    })()
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





























