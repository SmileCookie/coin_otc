import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import {
    DOMAIN_VIP,
    PAGEINDEX,
    PAGESIZE,
    DEFAULTVALUE,
    TIMEFORMAT_DAYS_ss,
    TIMEFORMAT,
    NUMBERPOINT,
    TIMEFORMAT_DAYS,
    PAGESIZE_200,
    PAGRSIZE_OPTIONS,
    SELECTWIDTH,
    URLS
} from '../../../conf'
import { Button, Pagination, Select, message, Modal } from 'antd'
import { toThousands } from '../../../utils'
import MarketList from '../select/marketList'
import moment from 'moment'
import ModalDetailQueue from './modal/modalDetailQueue'
import { AsyncSelect } from '../../../components/select/asyncSelect'

const { COMMON_GETUSERTYPE } = URLS
const Big = require('big.js')
const Option = Select.Option;

export default class MarketTrade extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            market: 'etc_btc',
            userId: '',
            userName: '',
            moneyMin: '',
            moneyMax: '',
            // numbersMin: '',
            // numbersMax: '',
            queryrecordList: [],//成交记录
            buyList: [],
            shellList: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE_200,
            pageTotal: DEFAULTVALUE,
            // buy_pageIndex: PAGEINDEX,
            // buy_pageSize: PAGESIZE_200,
            // buy_pageTotal: DEFAULTVALUE,
            // shell_pageIndex: PAGEINDEX,
            // shell_pageSize: PAGESIZE_200,
            // shell_pageTotal: DEFAULTVALUE,
            accountType: '0',
            visible: false,
            modalHtml: ''
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.resetState = this.resetState.bind(this)
        this.clickInquireState = this.clickInquireState.bind(this)
        this.resetqueryrecord = this.resetqueryrecord.bind(this)
        this.changPageNum = this.changPageNum.bind(this)         //分页
        this.onShowSizeChange = this.onShowSizeChange.bind(this)   //分页
        // this.buy_changPageNum = this.buy_changPageNum.bind(this)         //buy_分页
        // this.buy_onShowSizeChange = this.buy_onShowSizeChange.bind(this)   //buy_分页
        // this.shell_changPageNum = this.shell_changPageNum.bind(this)         //shell_分页
        // this.shell_onShowSizeChange = this.shell_onShowSizeChange.bind(this)   //shell_分页
        this.clickHide = this.clickHide.bind(this);
        this.handleChange = this.handleChange.bind(this)            //市场
    }

    componentDidMount() {
        // this.chinaTime()
        this.timer = setInterval(() => {
            this.loop() && this.clickInquireState()
        },5000)
        
    }
    loop = async () => {
        let arr = null;
        await this.clickInquireState().then((a) => {
           arr = a || []; 
        })
        
        return !arr.includes(false);
    }
    componentWillUnmount(){
        // clearInterval(this.interval);
        // clearInterval(this.intervalreset);
        clearInterval(this.timer)
    }
    
    // throttle = (func, wait) =>  {
    //     let timeout;
    //     return () => {
    //         let args = arguments;
    //         if (!timeout) {
    //             timeout = setTimeout(() => {
    //                 timeout = null;
    //                 func.apply(this, args)
    //             }, wait)
    //         }
    
    //     }
    // }

    // 点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        })
        this.resetqueryrecord(page, pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.resetqueryrecord(current, size)
        this.setState({
            pageIndex: current,
            pageSize: size
        })
    }
    //买分页
    // buy_changPageNum(page, pageSize) {
    //     this.setState({
    //         buy_pageIndex: page
    //     })
    //     this.resetQueryAxios(1, page, pageSize)
    // }
    //买分页的 pagesize 改变时
    // buy_onShowSizeChange(current, size) {
    //     this.resetQueryAxios(1, current, size)
    //     this.setState({
    //         buy_pageIndex: current,
    //         buy_pageSize: size
    //     })
    // }
    //卖分页
    // shell_changPageNum(page, pageSize) {
    //     this.setState({
    //         shell_pageIndex: page
    //     })
    //     this.resetQueryAxios(0, page, pageSize)
    // }
    //卖分页的 pagesize 改变时
    // shell_onShowSizeChange(current, size) {
    //     this.resetQueryAxios(0, current, size)
    //     this.setState({
    //         shell_pageIndex: current,
    //         shell_pageSize: size
    //     })
    // }
    //市场选择框
    handleChange(value) {
        this.setState({
            market: value
        })
    }

    //交易记录
    resetqueryrecord(currentIndex, currentSize) {
        // clearInterval(this.interval);
        const { userName, userId, moneyMin, moneyMax, numbersMin, numbersMax, pageIndex, pageSize, market, accountType } = this.state;
        // this.interval = setInterval(
        // () => 
        return new Promise((resolve,reject) => {
            axios.post(DOMAIN_VIP + "/marketTrade/queryRecord", qs.stringify({
                type: "",
                status: "",
                accountType,
                market: market,
                userName: userName,
                userId: userId,
                moneyMin: moneyMin,
                moneyMax: moneyMax,
                numbersMin: numbersMin,
                numbersMax: numbersMax,
                entrustId: "",
                pageIndex: currentIndex || pageIndex,
                pageSize: currentSize || pageSize
            })).then(res => {
                const result = res.data;
                if (result.code == 0) {
                    this.setState({
                        queryrecordList: result.data.list,
                        pageTotal: result.data.totalCount,
                        pageIndex:result.data.currPage,
                        pageSize:result.data.pageSize
                    })
                    resolve(true)
                } else {
                    message.warning(result.msg)
                    reject(false)
                }
            })
        })
        
        // , 3000);
    }

    //买单/卖单
    resetQueryAxios(type, currentIndex, currentSize) {
        // clearInterval(this.intervalreset);
        const { userName, userId, moneyMin, moneyMax, numbersMin, numbersMax, buy_pageIndex, buy_pageSize, market, shell_pageIndex, shell_pageSize, accountType } = this.state;
        // let pageindex_1 = type == 1 ? buy_pageIndex : shell_pageIndex;
        // let pageSize_1 = type == 1 ? buy_pageSize : shell_pageSize;
        // this.intervalreset = setInterval(
        //     () =>
        return new Promise((resolve, reject) =>{
            axios.post(DOMAIN_VIP + "/marketTrade/newQuery", qs.stringify({
                market: market,
                userType: accountType,
                moneyMin: moneyMin,
                moneyMax: moneyMax,
                // type: type,
                // status: "3",
                // userName: userName,
                // userId: userId,
                // numbersMin: numbersMin,
                // numbersMax: numbersMax,
                // entrustId: "",
                // pageIndex: currentIndex || pageindex_1,
                // pageSize: currentSize || pageSize_1
            })).then(res => {
                const result = res.data;
                if (result.code == 0) {
                    this.setState({
                        buyList: result.data.buy,
                        shellList: result.data.sell,
                    })
                    resolve(true)
                    // if (type == 1) {
                    //     this.setState({
                    //         buyList: result.data.buy,
                    //         buy_pageTotal: result.data.totalCount,
                    //     })
                    // } else {
                    //     this.setState({
                    //         shellList: result.data.sell,
                    //         shell_pageTotal: result.data.totalCount,
                    //     })
                    // }
                } else {
                    message.warning(result.msg)
                    reject(false)
                }
            })
        } )
        
        //  , 3000);
    }

    // resetQuerySell(type,currentIndex, currentSize){
    //     clearInterval(this.intervalresell);
    //     const { userName, userId, moneyMin, moneyMax, numbersMin, numbersMax, buy_pageIndex, buy_pageSize, market, shell_pageIndex, shell_pageSize } = this.state;
    //     let pageindex_1 = type == 1 ? buy_pageIndex : shell_pageIndex;
    //     let pageSize_1  = type == 1 ? buy_pageSize : shell_pageSize;
    //     this.intervalresell = setInterval(
    //         () =>axios.post(DOMAIN_VIP + "/marketTrade/query", qs.stringify({ 
    //         type:type,
    //         status:"3",
    //         market: market,
    //         userName: userName,
    //         userId: userId,
    //         moneyMin: moneyMin,
    //         moneyMax: moneyMax,
    //         numbersMin: numbersMin,
    //         numbersMax: numbersMax,
    //         entrustId:"",
    //         pageIndex: currentIndex || pageindex_1,
    //         pageSize: currentSize || pageSize_1
    //      })).then(res => {
    //          const result = res.data;
    //          if (result.code == 0) {
    //              if (type == 1){
    //                 this.setState({
    //                     buyList: result.data.list,
    //                     buy_pageTotal: result.data.totalCount,
    //                 })
    //              }else{
    //                 this.setState({
    //                     shellList: result.data.list,
    //                     shell_pageTotal: result.data.totalCount,
    //                 })
    //              }
    //         }else{
    //             message.warning(result.msg)
    //         }
    //      })
    //      , 3000);
    // }

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
        // this.resetqueryrecord(); //交易记录
        // this.resetQueryAxios(1); //买单
        // this.resetQueryAxios(0);//卖单
        return Promise.all([this.resetqueryrecord(),this.resetQueryAxios(1),this.resetQueryAxios(0)])
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
            market: 'etc_btc',
            userId: '',
            userName: '',
            moneyMin: '',
            moneyMax: '',
            // numbersMin: '',
            // numbersMax: '',
            accountType: '0',
        })
    }

    selectUser = user => {
        this.setState({
            accountType: user
        })
    }
    detailQueue = (item) => {
        if (!item) {
            message.error('参数错误！')
            return
        }
        if (Object.prototype.toString.call(item) === '[object Object]') {
            item = {
                ...item,
                market: this.state.market || '',
                userType: this.state.accountType || '',
            }
        }
        this.setState({
            modalHtml: <ModalDetailQueue item={item}/>,
            visible: true
        })
    }
    handleCancel = () => {
        this.setState({ visible: false })
    }

    // judgeAccoutType = ({ typeall, userId }) => typeall.split(',').map(Number).includes(userId) ? '刷量账户' : '用户账户'
    render() {
        Big.RM = 0;
        const {
            showHide, market, moneyMin, moneyMax, queryrecordList, buyList, shellList, accountType,
            visible, modalHtml,pageIndex,pageSize,pageTotal
        } = this.state;
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 盘口管理 > 币币盘口交易
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"}
                       onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <MarketList market={market} col='3' handleChange={this.handleChange}></MarketList>
                                </div>
                                <AsyncSelect title='用户类型' paymod url={COMMON_GETUSERTYPE} value={accountType} onSelectChoose={this.selectUser} />
                                {/*<div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">*/}
                                {/*<div className="form-group">*/}
                                {/*<label className="col-sm-3 control-label">用户编号：</label>*/}
                                {/*<div className="col-sm-8">*/}
                                {/*<input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />*/}
                                {/*</div>*/}
                                {/*</div>*/}
                                {/*</div>*/}
                                {/*<div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">*/}
                                {/*<div className="form-group">*/}
                                {/*<label className="col-sm-3 control-label">用户名：</label>*/}
                                {/*<div className="col-sm-8">*/}
                                {/*<input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} /><b className="icon-fuzzy">%</b>*/}
                                {/*</div>*/}
                                {/*</div>*/}
                                {/*</div>*/}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">价格区间：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="moneyMin"
                                                       value={moneyMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="moneyMax"
                                                       value={moneyMax} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                {/*<div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">*/}
                                {/*<div className="form-group">*/}
                                {/*<label className="col-sm-3 control-label">数量区间：</label>*/}
                                {/*<div className="col-sm-8">*/}
                                {/*<div className="left col-sm-5 sm-box">*/}
                                {/*<input type="text" className="form-control" name="numbersMin" value={numbersMin} onChange={this.handleInputChange} />*/}
                                {/*</div>*/}
                                {/*<div className="left line34">-</div>*/}
                                {/*<div className="left col-sm-5 sm-box">*/}
                                {/*<input type="text" className="form-control" name="numbersMax" value={numbersMax} onChange={this.handleInputChange} />*/}
                                {/*</div>*/}
                                {/*</div>*/}
                                {/*</div>*/}
                                {/*</div>*/}
                                <div className="col-md-3 col-sm-3 col-xs-3 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.clickInquireState}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div
                                    className="table-responsive clear-both-none col-mg-4 col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                    <table border='1'
                                           className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                        <tr className="headings">
                                            <th colSpan="5" className="column-title color_green">买盘</th>
                                        </tr>
                                        <tr className="headings">
                                            {/*<th className="column-title">用户编号</th>*/}
                                            {/*<th className="column-title">账号类型</th>*/}
                                            <th className="column-title">买入</th>
                                            <th className="column-title">买入均价</th>
                                            <th className="column-title">委托数量</th>
                                            <th className="column-title">操作</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {
                                            Array.isArray(buyList) && buyList.length > 0 ? buyList.map((item, index) => {
                                                return (
                                                    <tr key={index}>
                                                        {/*<td className={item.userType != "" ? "color_128fdc" : ""}>{item.userId}</td>*/}
                                                        {/*<td>{this.judgeAccoutType(item)}</td>*/}
                                                        <td>买{index+1}</td>
                                                        <td className='moneyGreen'>{toThousands(new Big(item.unitPrice).toFixed())}</td>
                                                        <td>{item.numbers}</td>
                                                        <td><a href='javascript:void(0);'
                                                               onClick={() => this.detailQueue(item)}>详细队列</a></td>
                                                    </tr>
                                                )
                                            }) : <tr className="no-record">
                                                <td colSpan="10">暂无数据</td>
                                            </tr>
                                        }
                                        </tbody>
                                    </table>
                                    {/*<div className="pagation-box">*/}
                                    {/*{*/}
                                    {/*buy_pageTotal > 0 && <Pagination*/}
                                    {/*size="small"*/}
                                    {/*current={buy_pageIndex}*/}
                                    {/*total={buy_pageTotal}*/}
                                    {/*onChange={this.buy_changPageNum}*/}
                                    {/*showTotal={total => `总共 ${total} 条`}*/}
                                    {/*defaultPageSize={PAGESIZE_200}*/}
                                    {/*onShowSizeChange={this.buy_onShowSizeChange}*/}
                                    {/*showSizeChanger*/}
                                    {/*pageSizeOptions={PAGRSIZE_OPTIONS}*/}
                                    {/*showQuickJumper />*/}
                                    {/*}*/}
                                    {/*</div>*/}
                                </div>
                                <div
                                    className="table-responsive clear-both-none col-mg-4 col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                    <table border='1'
                                           className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                        <tr className="headings">
                                            <th colSpan="5" className="column-title color_red">卖盘</th>
                                        </tr>
                                        <tr className="headings">
                                            {/*<th className="column-title">用户编号</th>*/}
                                            {/*<th className="column-title">账号类型</th>*/}
                                            <th className="column-title">卖出</th>
                                            <th className="column-title">卖出均价</th>
                                            <th className="column-title">委托数量</th>
                                            <th className="column-title">操作</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {
                                            Array.isArray(shellList) && shellList.length > 0 ? shellList.map((item, index) => {
                                                return (
                                                    <tr key={index}>
                                                        {/*<td className={item.userType != "" ? "color_128fdc" : ""}>{item.userId}</td>*/}
                                                        {/*<td>{this.judgeAccoutType(item)}</td>*/}
                                                        <td>卖{index+1}</td>
                                                        <td className='moneyGreen'>{toThousands(new Big(item.unitPrice).toFixed())}</td>
                                                        <td>{item.numbers}</td>
                                                        <td><a href='javascript:void(0);'
                                                               onClick={() => this.detailQueue(item)}>详细队列</a></td>
                                                    </tr>
                                                )
                                            }) : <tr className="no-record">
                                                <td colSpan="10">暂无数据</td>
                                            </tr>
                                        }
                                        </tbody>
                                    </table>
                                    {/*<div className="pagation-box">*/}
                                    {/*{*/}
                                    {/*shell_pageTotal > 0 && <Pagination*/}
                                    {/*size="small"*/}
                                    {/*current={shell_pageIndex}*/}
                                    {/*total={shell_pageTotal}*/}
                                    {/*onChange={this.shell_changPageNum}*/}
                                    {/*showTotal={total => `总共 ${total} 条`}*/}
                                    {/*defaultPageSize={PAGESIZE_200}*/}
                                    {/*onShowSizeChange={this.shell_onShowSizeChange}*/}
                                    {/*showSizeChanger*/}
                                    {/*pageSizeOptions={PAGRSIZE_OPTIONS}*/}
                                    {/*showQuickJumper />*/}
                                    {/*}*/}
                                    {/*</div>*/}
                                </div>
                                <div
                                    className="table-responsive clear-both-none col-mg-4 col-lg-4 col-md-4 col-sm-4 col-xs-4">
                                    <table border='1'
                                           className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                        <tr className="headings">
                                            <th colSpan="5" className="column-title color_yellow">成交记录</th>
                                        </tr>
                                        <tr className="headings">
                                            <th className="column-title">交易时间</th>
                                            <th className="column-title">成交价格</th>
                                            <th className="column-title">成交量</th>
                                            <th className="column-title">买方用户</th>
                                            <th className="column-title">卖方用户</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {
                                            Array.isArray(queryrecordList) && queryrecordList.length > 0 ? queryrecordList.map((item, index) => {
                                                return (
                                                    <tr key={index}>
                                                        <td>{moment(item.times).format(TIMEFORMAT_DAYS_ss)}</td>
                                                        <td className='moneyGreen'>{item.unitprice ? toThousands(item.unitprice) : ''}</td>
                                                        <td>{item.numbers}</td>
                                                        <td>{item.useridbuy}</td>
                                                        <td>{item.useridsell}</td>
                                                    </tr>
                                                )
                                            }) : <tr className="no-record">
                                                <td colSpan="10">暂无数据</td>
                                            </tr>
                                        }
                                        </tbody>
                                    </table>
                                    <div className="pagation-box">
                                    {
                                    pageTotal > 0 && <Pagination
                                    size="small"
                                    current={pageIndex}
                                    total={pageTotal}
                                    onChange={this.changPageNum}
                                    showTotal={total => `总共 ${total} 条`}
                                    defaultPageSize={PAGESIZE_200}
                                    onShowSizeChange={this.onShowSizeChange}
                                    showSizeChanger
                                    pageSizeOptions={PAGRSIZE_OPTIONS}
                                    showQuickJumper />
                                    }
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title='详细队列'
                    width="1200px"
                    onCancel={this.handleCancel}
                    footer={false}
                    destroyOnClose
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}
