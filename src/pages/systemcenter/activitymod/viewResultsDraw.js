import React from 'react'
import ReactDOM from 'react-dom';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, message, Progress, Modal, Tag, Pagination} from 'antd'
import { DOMAIN_VIP, DEFAULTVALUE,PAGEINDEX,PAGESIZE,TIMEFORMAT, NUMBERPOINT, SELECTWIDTH, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss } from '../../../conf'
const confirm = Modal.confirm;
const Big = require('big.js')

export default class ViewResultsDraw extends React.Component {
    constructor(props) {
        super(props)
        this.state={
            eventId:'',
            headList:{},
            brushAmount:"",
            luckyId:"",
            userId:"",
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            tableList:[],
            ip:'',
            showHide:true
        }
        this.show_click = this.show_click.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this) 
        this.getbruhAmount = this.getbruhAmount.bind(this)
        this.changPageNum = this.changPageNum.bind(this)         //分页
        this.onShowSizeChange = this.onShowSizeChange.bind(this)   //分页
        this.searchgetresultList = this.searchgetresultList.bind(this)
        this.searchReasatt = this.searchReasatt.bind(this)
        this.clickHide = this.clickHide.bind(this)
    }
    componentDidMount () {
        this.setState({
            eventId:this.props.activityId
        },()=>this.getInfo())
        
    }
    getInfo() { //查看抽奖结果信息
        const {eventId,ip} = this.state
        axios.post(DOMAIN_VIP +"/drawManage/getResultInfo",qs.stringify({
            eventId,ip
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    headList: result.data,
                    luckyId:result.data.luckyId
                },()=>{
                    this.getresultList()
                })
            }
        })
    }
    getresultList(currentIndex, currentSize) { //查看抽奖结果明细
        const {luckyId,pageIndex,userId,pageSize,ip} = this.state
        axios.post(DOMAIN_VIP+"/drawManage/getResultList",qs.stringify({
            luckyId,ip,
            userId: userId,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    tableList: result.data.list,
                    pageTotal: result.data.totalCount
                })
            }
        })
    }
    searchgetresultList(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.getInfo()) 
    }
    searchReasatt(){
        this.setState({
            userId:"",
            ip:''
        })
    }

    getbruhAmount(luckyId) { //刷量
        const { brushAmount } = this.state
        const that = this
        confirm({
            title: '确定执行此刷量操作？',
            content: "刷量："+brushAmount+" GBC" ,
            onOk() {
                axios.post(DOMAIN_VIP + "/drawManage/brush", qs.stringify({
                    luckyId, brushAmount
                })).then(res => {
                    const result = res.data
                    if (result.code == 0){
                        message.success("刷量成功")
                        that.getInfo(that.props.activityId)
                    }
                    else{
                        message.error(result.msg)
                    }
                })
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.getresultList(page, pageSize)
        this.setState({
            pageIndex: page,
            pageSize: pageSize
        })
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.getresultList(current, size)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }
    
    
    //修改状态
    show_click(index, userId) {
        const { luckyId } = this.state
        this.props.showHideClick(index, this.props.activityId, luckyId, userId);
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
     //点击收起
     clickHide(){
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    render(){
        const { headList, brushAmount, userId, tableList, pageTotal,showHide, pageIndex ,ip} = this.state

        return(
            <div>
                 <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                    <Button type="primary" onClick={() => { this.show_click(0) }} >返回上一级</Button>
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right mar20" : "iconfont cur_poi icon-zhankai right mar20"} onClick={this.clickHide}></i>  
                </div>
                {showHide &&<div className="x_panel draw_view">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {/* <div className="col-md-8 col-sm-8 col-xs-8"> */}
                        <div className="col-mg-2 col-lg-1to2 col-md-2 col-sm-2 col-xs-2 mb10">
                            创建人：<Tag color="magenta">{headList.createUserName}</Tag>
                        </div>
                        <div className="col-mg-2 col-lg-2to3 col-md-2 col-sm-2 col-xs-2 mb10">
                            创建时间：<Tag color="red">{moment(headList.createTime).format(TIMEFORMAT_ss)}</Tag>
                        </div>
                        <div className="col-mg-2 col-lg-1to2 col-md-2 col-sm-2 col-xs-2 mb10">
                            活动状态：<Tag color="red">{headList.statusView}</Tag>
                        </div>
                        <div className="col-mg-3 col-lg-2to3 col-md-3 col-sm-3 col-xs-3 mb10">
                            开始时间：<Tag color="red">{moment(headList.startTime).format(TIMEFORMAT_ss)}</Tag>
                        </div>
                        <div className="col-mg-3 col-lg-2to3 col-md-3 col-sm-3 col-xs-3 mb10">
                            结束时间：<Tag color="red">{moment(headList.endTime).format(TIMEFORMAT_ss)}</Tag>
                        </div>
                        <div className="col-mg-2 col-lg-1to2 col-md-2 col-sm-2 col-xs-2 mb10">
                            已参加人数：<Tag color="purple">{headList.factUser}</Tag>
                        </div>
                        <div className="col-mg-2 col-lg-2to3 col-md-2 col-sm-2 col-xs-2 mb10">
                            已中出：<Tag color="purple">{headList.occurAmount}GBC</Tag>
                        </div>
                        <div className="col-mg-2 col-lg-1to2 col-md-2 col-sm-2 col-xs-2 mb10">
                            实际中出：<Tag color="purple">{headList.factAmount}GBC</Tag>
                        </div>
                        <div className="col-mg-3 col-lg-2to3 col-md-2 col-sm-2 col-xs-2 mb10">
                            翻倍：<Tag color="purple">{headList.doubleAmount}GBC</Tag>
                        </div>
                        <div className="col-mg-3 col-lg-2to3 col-md-2 col-sm-2 col-xs-2 mb10">
                            刷出：<Tag color="purple">{headList.brush}GBC</Tag>
                        </div>
                        
                        <div className="col-mg-4 col-lg-4to5 col-md-4 col-sm-4 col-xs-4 mb10">
                            奖项规则：<Tag color="magenta">{headList.startSize} - {headList.endSize} GBC</Tag>
                        </div>
                        <div className="col-md-4 col-sm-4 col-xs-4 mb10">
                            已中出：<Tag color="magenta">{headList.occurCount} 个</Tag>
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                            <div className="col-mg-6 col-lg-7 col-md-6 col-sm-6 col-xs-6 ">
                                <label className="left padding_0">用户编号：</label>
                                <input type="text" className="form-control draw_input mar10" style={{ width: 60 }} name="userId" value={userId} onChange={this.handleInputChange} />
                                <label className="left padding_0">屏蔽IP：</label>
                                <input type="text" className="form-control draw_input mar10" style={{ width: 60 }} name="ip" value={ip} onChange={this.handleInputChange} />
                                <Button type="primary" size="small" onClick={this.searchgetresultList} >查找</Button>
                                <Button type="primary" size="small" onClick={this.searchReasatt} >重置</Button>
                            </div>
                            <div className="col-md-3 col-sm-3 col-xs-3 ">
                                <div className="col-mg-5 col-lg-7 col-sm-5 left padding_0">
                                    <label className="left padding_0">刷量：</label>
                                    <input type="text" className="form-control draw_input" style={{ width: 60 }} name="brushAmount" value={brushAmount} onChange={this.handleInputChange} />
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4">
                                    <Button type="primary" size="small" onClick={() => { this.getbruhAmount(headList.luckyId) }} >确定</Button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>}
                <div className="x_panel">
                    <div className="x_content">
                        <div className="table-responsive">
                            <table className="table table-striped jambo_table bulk_action table-linehei">
                                <thead>
                                    <tr className="headings">
                                        <th className="column-title">序号</th>
                                        <th className="column-title">用户编号</th>
                                        <th className="column-title">登录IP</th>
                                        <th className="column-title">抽奖次数</th>
                                        <th className="column-title">获得奖励</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {
                                        tableList.length > 0 ? tableList.map((item, index) => {
                                            return (
                                                <tr key={index}>
                                                    <td>{index+1}</td>
                                                    <td>{item.userId}</td>
                                                    <td>{item.ip}</td>
                                                    <td>
                                                        <a  href="javascript:void(0)" 
                                                            onClick={() => { this.show_click(3,item.userId) }}
                                                            className="mar20">{item.userCount}
                                                        </a>
                                                    </td>
                                                    <td>{item.occurAmount}</td>
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
        )
    }
    
}