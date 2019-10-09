import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import VipRateList from '../../common/select/vipRateList'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button,Tabs,Pagination } from 'antd'
import { tableScroll } from '../../../utils'
const TabPane = Tabs.TabPane;

export default class CapitalCount extends React.Component{

    constructor(props) {
        super(props)
        this.state = {
            showHide:true,
            userid:'',
            username:'',
            vipRateType:'',
            jifenS:'',
            jifenE:'',
            tableUser:[],
            pageTotal:0,
            tableSum:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            height:0,
            tableScroll:{
                tableId:'URVP',
                x_panelId:'URVPX',
                defaultHeight:500,
            }
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.tabsCallback = this.tabsCallback.bind(this)
        this.requestUserTable = this.requestUserTable.bind(this)
        this.requestSumTable = this.requestSumTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleVipChange = this.handleVipChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    
    componentDidMount(){
        this.requestUserTable()
        this.requestSumTable()
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
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page,
            pageSize:pageSize
        })
        this.requestUserTable(page,pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestUserTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }
    //用户等级 Select
    handleVipChange(val){
        this.setState({
            vipRateType:val
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
    //tab切换
    tabsCallback(key){
        console.log(key)
    }
    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestUserTable())
        
    }
    //用户等级列表
    requestUserTable(currIndex,currSize){
        const { userid,username,vipRateType,jifenS,jifenE,pageSize,pageIndex } = this.state
        axios.post(DOMAIN_VIP+'/integralBill/queryUserVipList',qs.stringify({
            userid,
            username,
            vipRateType,
            jifenS,
            jifenE,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            console.log(result)
            if(result.code == 0){
                this.setState({
                    tableUser:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //用户等级汇总
    requestSumTable(){
        axios.post(DOMAIN_VIP+'/integralBill/getUserVipCount')
        .then(res => {
            const result = res.data;
            let tableSum = [];
            if(result.code == 0){
                for(let k in result.data){
                    let obj = {}
                    obj.name = k
                    obj.jifen = result.data[k]
                    tableSum.push(obj)
                }
                this.setState({
                    tableSum
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //重置状态
    onResetState(){
        this.setState({
            userid:'',
            username:'',
            type:'',
            jifenS:'',
            jifenE:'',
            vipRateType:''
        })
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

    render(){
        const { showHide,tableUser,tableSum,pageTotal,pageIndex,pageSize,vipRateType,jifenS,jifenE,userid,username } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>积分管理>用户等级
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            
                            <div className="x_content">
                                <div className="col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="userid" value={userid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="username" value={username} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <VipRateList vipType={vipRateType} col="3" handleChange={this.handleVipChange} />
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">积分区间：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control"  name="jifenS" value={jifenS} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control"  name="jifenE" value={jifenE} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>   
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            
                            <div className="x_content">
                            <Tabs defaultActiveKey="1" onChange={this.tabsCallback}>
                                <TabPane tab="用户等级" key="1">
                                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableUser.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                            <thead>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">用户编号</th>
                                                    <th className="column-title">用户等级</th>
                                                    <th className="column-title">用户积分</th>                     
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {
                                                    tableUser.length>0?
                                                    tableUser.map((item,index) => {
                                                        return (
                                                            <tr key={index}>
                                                                <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                                <td>{item.userid}</td>
                                                                <td>{item.vipRateName}</td>
                                                                <td>{item.jifen}</td>
                                                            </tr>
                                                        )
                                                    })   
                                                    :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                                }
                                            </tbody>
                                        </table>
                                    </div>
                                    <div className="pagation-box">
                                        {
                                            pageTotal>0 && <Pagination
                                                        size="small"
                                                        current={pageIndex}
                                                        total={pageTotal}
                                                        showTotal={total => `总共 ${total} 条`}
                                                        onChange={this.changPageNum}
                                                        onShowSizeChange={this.onShowSizeChange}
                                                        showSizeChanger
                                                        showQuickJumper 
                                                        pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                        defaultPageSize={PAGESIZE}
                                                        />
                                        }
                                    </div>
                                </TabPane>
                                <TabPane tab="等级汇总" key="2">
                                    <div className="table-responsive">
                                        <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
                                            <thead>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">用户等级</th>
                                                    <th className="column-title">用户数量</th>                    
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {
                                                    tableSum.map((item,index) => {
                                                        return(
                                                            <tr key={index}>
                                                                <td>{index+1}</td>
                                                                <td>{item.name}</td>
                                                                <td>{item.jifen}</td>
                                                            </tr>
                                                        )
                                                    })
                                                }
                                            </tbody>
                                        </table>
                                    </div>
                                </TabPane>
                            </Tabs>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}






























































