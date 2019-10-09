import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import { Button,Select,Pagination,message,Table } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,PAGRSIZE_OPTIONS,PAGESIZE_50} from '../../../conf'
import { toThousands,kpEventLisInquiry } from '../../../utils'
import HandicapMarket from '../../common/select/handicapMarketFT'
import AccountTypeList from '../../common/select/accountTypeList'
const { Column } = Table
const Option = Select.Option


export default class HandicapTradingFT extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            buyList:[],
            sellList:[],
            pageTotal_buy:DEFAULTVALUE,
            pageTotal_sell:DEFAULTVALUE,
            pageIndex_buy:PAGEINDEX,
            pageIndex_sell:PAGEINDEX,
            pageSize_buy:PAGESIZE_50,
            pageSize_sell:PAGESIZE_50,
            paginationBuy:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS,
                defaultPageSize:PAGESIZE_50
            },
            paginationSell:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS,
                defaultPageSize:PAGESIZE_50
            },
            userid:'',
            priceS:'',
            priceE:'',
            amountS:'',
            amountE:'',
            username:'',
            type:'',
            action:'',
            futuresid:'',
            accounttype:'',
            appActiveKey:this.props.appActiveKey
        }
    }
    componentDidMount(){
        this.requestTableBuy()
        this.requestTableSell()
        kpEventLisInquiry(this.props.appActiveKey, this.state.appActiveKey, this.keyDown)
            // if(this.props.appActiveKey==this.state.appActiveKey){
            //     window.addEventListener('keypress',this.keyDown)
            // }
        

    }
    componentWillReceiveProps(nextProps){
        // if(nextProps.appActiveKey==this.state.appActiveKey){
        //     window.addEventListener('keypress',this.keyDown)
        // }else{
        //     window.removeEventListener('keypress',this.keyDown)
        // }
        kpEventLisInquiry(nextProps.appActiveKey, this.state.appActiveKey, this.keyDown)
    }
    componentWillUnmount(){
        // window.removeEventListener('keypress',this.keyDown)
        kpEventLisInquiry(this.keyDown)
    }
    clickHide = () => {
        this.setState({
            showHide:!this.state.showHide
        })
    }
    keyDown = (e) => {
        message.success('盘口交易')
        if(e.keyCode == 13) this.inquiry()
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>{
            this.requestTableBuy()//做多
            this.requestTableSell()//做空
        })       
    }
    resetState = () => {
        this.setState({
            userid:'',
            priceS:'',
            priceE:'',
            amountS:'',
            amountE:'',
            username:'',
            type:'',
            action:'',
            futuresid:'',
            accounttype:'',
        })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name
        this.setState({
            [name]:value
        })
    }
    //账户类型
    handleSelectAccount = val => {
        this.setState({
            accounttype:val
        })
    }
    //盘口市场
    handleSelectMarket = val => {
        this.setState({
            futuresid:val
        })
    }
    //委托价格类型
    selectDetegatePrice = val => {
        this.setState({
            type:val
        })
    }
    //委托动作类型
    selectDetegateAction = val => {
        this.setState({
            action:val
        })
    }
    onChangePageNum_buy = (pageIndex,pageSize) => {
        this.setState({
            pageIndex_buy:pageIndex,
            pageSize_buy:pageSize
        })
        this.requestTableBuy(pageIndex,pageSize)
    }
    onShowSizeChange_buy = (currentIndex,currentSize) => {
        this.setState({
            pageIndex_buy:currentIndex,
            pageSize_buy:currentSize,
        })
        this.requestTableBuy(currentIndex,currentSize)
    }
    onChangePageNum_sell = (pageIndex,pageSize) => {
        this.setState({
            pageIndex_sell:pageIndex,
            pageSize_sell:pageSize
        })
        this.requestTableSell(pageIndex,pageSize)
    }
    onShowSizeChange_sell = (currentIndex, currentSize) => {
        this.setState({
            pageIndex_sell:currentIndex,
            pageSize_sell:currentSize,
        })
        this.requestTableSell(currentIndex,currentSize)
    }
    requestTableBuy = (currentIndex,currentSize) => {
        const {paginationBuy,pageIndex_buy,pageSize_buy,pageIndex_sell,pageSize_sell,userid,username,priceS,priceE,amountS,amountE,type,action,futuresid,accounttype,} = this.state
        axios.post(DOMAIN_VIP+'/handicapTrading/list',qs.stringify({
            userid,username,priceS,priceE,amountS,amountE,type,action,futuresid,accounttype,
            pageIndex:currentIndex||pageIndex_buy,
            pageSize:currentSize||pageSize_buy
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let buyList = result.dataLong.list;
                for(let i=0;i<buyList.length;i++){
                    buyList[i].index = (result.dataLong.currPage-1)*result.dataLong.pageSize+i+1;
                    buyList[i].key = buyList[i].id
                }
                paginationBuy.total = result.dataLong.totalCount;
                paginationBuy.onChange = this.onChangePageNum_buy;
                paginationBuy.onShowSizeChange = this.onShowSizeChange_buy
                this.setState({
                    buyList:buyList,
                    paginationBuy,
                })
            }else{
                message.warning(result.msg)
            }

        })
    }
    requestTableSell = (currentIndex,currentSize) => {
        const {paginationSell,pageIndex_sell,pageSize_sell,userid,username,priceS,priceE,amountS,amountE,type,action,futuresid,accounttype} = this.state
        axios.post(DOMAIN_VIP+'/handicapTrading/listShort',qs.stringify({
            userid,username,priceS,priceE,amountS,amountE,type,action,futuresid,accounttype,
            pageIndex:currentIndex||pageIndex_sell,
            pageSize:currentSize||pageSize_sell
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let sellList = result.dataShort.list;
                for(let i=0;i<sellList.length;i++){
                    sellList[i].index = (result.dataShort.currPage-1)*result.dataShort.pageSize+i+1;
                    sellList[i].key = sellList[i].id
                }
                paginationSell.total = result.dataShort.totalCount;
                paginationSell.onChange = this.onChangePageNum_sell;
                paginationSell.onShowSizeChange = this.onShowSizeChange_sell
                this.setState({
                    sellList:sellList,
                    paginationSell,
                })
            }else{
                message.warning(result.msg)
            }

        })
    }
    render(){
        const { paginationSell,paginationBuy,showHide,buyList,sellList,pageIndex_buy,pageIndex_sell,pageSize_buy,pageSize_sell,pageTotal_buy,pageTotal_sell,userid,priceS,priceE,amountS,amountE,username,type,action,futuresid,accounttype} = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 期货交易中心 > 盘口管理 > 盘口交易
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right":' iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HandicapMarket marketType={futuresid} handleChange={this.handleSelectMarket} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-ms-4 col-sm-4 col-xs-4">
                                    <AccountTypeList accType={accounttype}  handleChange={this.handleSelectAccount}/>
                                    {/* <div className="form-group">
                                        <label className="col-sm-3 control-label">账户类型:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} onChange={this.handleSelectAccount}>
                                                <Option key='' value={''}>请选择</Option>
                                                <Option key='0' value={0}>做市账户</Option>
                                                <Option key='1' value={1}>用户账户</Option>
                                            </Select>
                                        </div>
                                    </div> */}
                                </div>
                                <div className="col-mg-4 col-lg-6 col-ms-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-ms-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="username" value={username} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">//去掉
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托方向:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} value={''} onChange={this.handleSelectDetegateType}>
                                                <Option key='0' value={0}>做多</Option>
                                                <Option key='1' value={1}>做空</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">价格类型:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} value={type} onChange={this.selectDetegatePrice}>
                                                <Option key='' value={''}>请选择</Option>
                                                <Option key='0' value={0}>限价</Option>
                                                <Option key='1' value={1}>市价</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">动作类型:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} value={action} onChange={this.selectDetegateAction}>
                                                <Option key='0' value={''}>请选择</Option>
                                                {/* <Option key='-1' value={-1}>取消</Option> */}
                                                <Option key='1' value={1}>开仓</Option>
                                                <Option key='2' value={2}>平仓</Option>
                                                <Option key='3' value={3}>强平</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托价格:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='priceS' value={priceS} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='priceE' value={priceE} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托数量:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='amountS' value={amountS} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='amountE' value={amountE} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="right">
                                    <Button type="primary" onClick={this.inquiry}>查询</Button>
                                    <Button type="primary" onClick={this.resetState}>重置</Button>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div  className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="table-responsive">
                                        <table style={{margin:'0'}} className='table table-striped jambo_table bulk_action table-linehei"'>
                                            <thead>
                                                <tr>
                                                    <th style={{textAlign:'left'}} colSpan="6" className="column-title color_green">做多盘口</th>
                                                </tr>
                                            </thead>
                                        </table>
                                        <Table dataSource={buyList} bordered pagination={paginationBuy} locale={{emptyText:'暂无数据'}}>
                                            <Column title='用户编号' dataIndex='userid' key='userid' />
                                            <Column title='账户类型' dataIndex='userid' key='22' render={(text,record)=>record.typeall.indexOf(text)>-1?'做市账户':'用户账户'} />
                                            <Column title='委托方向' dataIndex='index' key='index' render={(text)=><span>买{text}</span>} />
                                            <Column title='委托价格类型' dataIndex='type' key='type' render={(text)=>text==1?'市价':'限价'}/>
                                            <Column title='委托动作类型' dataIndex='action' key='action' render={(text)=>{
                                                switch(text){
                                                    case -1:
                                                        return '取消'
                                                        break;
                                                    case 1:
                                                        return '开仓'
                                                        break;
                                                    case 2:
                                                        return '平仓'
                                                        break;
                                                    case 3:
                                                        return '强平'
                                                        break;
                                                    default:
                                                        return '--'
                                                        break;
                                                }
                                            }} />
                                            <Column title='委托价格' dataIndex='price' key='price' className='moneyGreen' render={(text)=>toThousands(text)} />
                                            <Column  title='委托数量' dataIndex='surplusamount' key='surplusamount' />
                                        </Table> 
                                    </div> 
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="table-responsive">
                                        <table style={{margin:'0'}} className='table table-striped jambo_table bulk_action table-linehei"'>
                                            <thead>
                                                <tr>
                                                    <th colSpan="6" style={{textAlign:'left'}} className="column-title color_red">做空盘口</th>
                                                </tr>
                                            </thead>
                                        </table>
                                        <Table dataSource={sellList} bordered pagination={paginationSell} locale={{emptyText:'暂无数据'}}>
                                            <Column title='用户编号' dataIndex='userid' key='userid' />
                                            <Column title='账户类型' dataIndex='userid' key='22' render={(text,record)=>record.typeall.indexOf(text)>-1?'做市账户':'用户账户'} />
                                            <Column title='委托方向' dataIndex='index' key='index' render={(text)=><span>卖{text}</span>} />
                                            <Column title='委托价格类型' dataIndex='type' key='type' render={(text)=>text==1?'市价':'限价'}/>
                                            <Column title='委托动作类型' dataIndex='action' key='action' render={(text)=>{
                                                switch(text){
                                                    case -1:
                                                        return ''
                                                        break;
                                                    case 1:
                                                        return '开仓'
                                                        break;
                                                    case 2:
                                                        return '平仓'
                                                        break;
                                                    case 3:
                                                        return '强平'
                                                        break;
                                                    default:
                                                        return '--'
                                                        break;
                                                }
                                            }} />
                                            <Column title='委托价格' dataIndex='price' key='price' className='moneyGreen' render={(text)=>toThousands(text)} />
                                            <Column  title='委托数量' dataIndex='surplusamount' key='surplusamount' />
                                        </Table>                                        
                                    </div>    
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
