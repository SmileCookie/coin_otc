import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button, message, Progress, Modal, Tag, Pagination } from 'antd'
import { DOMAIN_VIP, DEFAULTVALUE, PAGEINDEX, PAGESIZE, TIMEFORMAT, NUMBERPOINT, SELECTWIDTH, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss } from '../../../conf'

export default class DrawShowUser extends React.Component {
    constructor(props) {
        super(props)
        this.state={
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            tableList: []
        }
        this.show_click = this.show_click.bind(this)
        this.changPageNum = this.changPageNum.bind(this)         //分页
        this.onShowSizeChange = this.onShowSizeChange.bind(this)   //分页
    }
    
    componentDidMount() {
        this.getresultList();
    }
    getresultList(currentIndex, currentSize){
        const { luckyId, userId } = this.props
        const { pageIndex, pageSize } = this.state
        axios.post(DOMAIN_VIP + "/drawManage/showUserDetail", qs.stringify({
            luckyId,
            userId,
            pageIndex: currentIndex || pageIndex,
            pageSize: currentSize || pageSize
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                this.setState({
                    tableList: result.data.list,
                    pageTotal: result.data.totalCount
                })
            }
        })
    }

    //修改状态
    show_click(index) {
        this.props.showHideClick(index, this.props.activityId);
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
    
    
    render() {
        const { pageTotal, pageIndex, tableList} = this.state
        return (
            <div>
                <div className="x_panel">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <Button type="primary" onClick={() => { this.show_click(2) }} >返回上一级</Button>
                        <Button type="primary" onClick={() => { this.show_click(0) }} >返回抽奖主菜单</Button>
                    </div>
                </div>
                <div className="x_panel">
                    <div className="x_content">
                        用户领取明细
                        <div className="table-responsive">
                            <table className="table table-striped jambo_table bulk_action table-linehei">
                                <thead>
                                    <tr className="headings">
                                        <th className="column-title">用户ID</th>
                                        <th className="column-title">抽奖时间</th>
                                        <th className="column-title">获得奖励</th>
                                        <th className="column-title">是否到账</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {
                                        tableList.length > 0 ? tableList.map((item, index) => {
                                            return (
                                                <tr key={index}>
                                                    <td>{item.userId}</td>
                                                    <td>{moment(item.updateTime).format(TIMEFORMAT)}</td>
                                                    <td>{item.occurAmount}</td>
                                                    <td>是</td>
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
        );
    }
}