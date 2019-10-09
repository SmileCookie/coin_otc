
import React from 'react'
import ReactDOM from 'react-dom'
import axios from 'axios'
import qs from 'qs'
import moment from 'moment'
import { Button, message, Pagination,DatePicker, Progress, Modal } from 'antd'
import { DOMAIN_VIP, DEFAULTVALUE, TIMEFORMAT, NUMBERPOINT, SELECTWIDTH, PAGEINDEX, PAGESIZE, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss } from '../../../conf'
const confirm = Modal.confirm;
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js')

export default class VoteLog extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            tableList: [],
            showHide:true,
            userId:'',
            time:[],
            beginTime:'',
            endTime:'',
            activityId:'',
            coinId:'',
            voteIp:''
        }
        this.show_click = this.show_click.bind(this)
        this.resetVoteList = this.resetVoteList.bind(this)
        this.changPageNum = this.changPageNum.bind(this)         //分页
        this.onShowSizeChange = this.onShowSizeChange.bind(this)   //分页
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.resetState = this.resetState.bind(this)
    }

    componentDidMount() {
        this.setState({
            activityId:this.props.activityId,
            coinId: this.props.coin_id
        },()=>this.resetVoteList())
        
    }
    resetVoteList(currentIndex, currentSize) {
        const { activityId,coinId,pageIndex, pageSize, pageTotal,userId,beginTime,endTime ,voteIp} = this.state
        axios.post(DOMAIN_VIP + "/voteManage/voteLog", qs.stringify({
           activityId,coinId,userId,beginTime,endTime,voteIp,
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
    //点击收起
    clickHide(){
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
     //时间控件
     onChangeCheckTime(date, dateString) {
        this.setState({
            beginTime:dateString[0],
            endTime:dateString[1],
            time:date
        })
    }
    //修改状态
    show_click(index, ids) {
        this.props.showHideClick(index, ids);
    }
    resetState(){
        this.setState({
            userId:'',
            beginTime:'',
            endTime:'',
            time:[],
            voteIp:''
        })
    }
    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },()=>this.resetVoteList())
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.resetVoteList(page, pageSize);
        this.setState({
            pageIndex: page
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
    render() {
        Big.RM = 0;
        const { showHide,tableList, pageIndex, pageSize, pageTotal,userId,time,voteIp} = this.state
        return (
            <div>
                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                    <Button type="primary" onClick={() => { this.show_click(3, this.props.activityId) }} >返回上一级</Button>
                    <Button type="primary" onClick={() => { this.show_click(0) }} >返回投票</Button>
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right mar20" : "iconfont cur_poi icon-zhankai right mar20"} onClick={this.clickHide}></i>  
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户ID：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                               
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">屏蔽IP：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="voteIp" value={voteIp} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">投票时间：</label>
                                            <div className="col-sm-8">
                                            <RangePicker 
                                              showTime={{
                                                  defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                              format="YYYY-MM-DD HH:mm:ss"
                                              placeholder={['Start Time', 'End Time']}
                                             onChange={this.onChangeCheckTime }
                                             value={time}
                                             />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-3 col-sm-3 col-xs-3 right">
                                    <div className="form-group">
                                        <Button type="primary" onClick={()=>this.inquireBtn()} >查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                    <div className="x_panel">
                        <div className="x_content">
                            <div className="table-responsive">
                                <table className="table table-striped jambo_table bulk_action table-linehei">
                                    <thead>
                                        <tr className="headings">
                                            <th className="column-title">序号</th>
                                            <th className="column-title">用户ID</th>
                                            <th className="column-title">登陆IP</th>
                                            <th className="column-title">投票时间</th>
                                            <th className="column-title">选项</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {
                                            tableList.length > 0 ? tableList.map((item, index) => {
                                                return (
                                                    <tr key={index}>
                                                        <td>{index+1}</td>
                                                        <td>{item.userId}</td>
                                                        <td>{item.voteIp}</td>
                                                        <td>{moment(item.voteTime).format(TIMEFORMAT)}</td>
                                                        <td>{item.voteName}</td>
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
                                        showQuickJumper />
                                }
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}





























