import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,SELECTWIDTH,DEFAULTVALUE,PAGEINDEX,PAGESIZE_50,PAGRSIZE_OPTIONS,PAGESIZE} from '../../../conf'
import { Select,Button,Pagination,message,Table } from 'antd'
import { tableScroll,toThousands,kpEventLisInquiry } from '../../../utils'
import HandicapMarket from '../../common/select/handicapMarketFT'
import AccountTypeList from '../../common/select/accountTypeList'
const Option = Select.Option
const { Column } = Table

export default class HandicapMergerTransactionFT extends React.Component{
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
            userId:'',
            futuresId:'',
            username:'',
            priceMin:'',
            priceMax:'',
            numMin:'',
            numMax:'',
            accounttype:'',
            appActiveKey:this.props.appActiveKey
        }
    }
    componentDidMount(){
        this.requestTableBuy()
        this.requestTableSell()
        // console.log(this.props)
        
       
            // if(this.props.appActiveKey==this.state.appActiveKey){
            //     window.addEventListener('keypress',this.keyDown)
            // }
            kpEventLisInquiry(this.props.appActiveKey, this.state.appActiveKey, this.keyDown)    
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
    //账户类型
    handleSelectAccount = val => {
        this.setState({
            accounttype:val
        })
    }
    //盘口市场
    handleSelectMarket = val => {
        this.setState({
            futuresId:val
        })
    }
    //重置
    resetState = () => {
        this.setState({
            userId:'',
            futuresId:'',
            username:'',
            priceMin:'',
            priceMax:'',
            numMin:'',
            numMax:'',
            accounttype:''
        })
    }
    keyDown = (e) => {
        message.success('盘口合并交易')
        if(e.keyCode == 13) this.inquiry()
    }
    //查询
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>{
            this.requestTableBuy()
            this.requestTableSell()
        })       
    }
    //输入 input 设置 state
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]:value
        })
    }
    //请求数据 做多
    requestTableBuy = (currentIndex, currentSize) => {
        const { pageIndex_buy,pageSize_buy,paginationBuy,userId,username,priceMax,priceMin,numMax,numMin,futuresId,accounttype } = this.state;
        axios.post(DOMAIN_VIP+'/futuresMerger/query',qs.stringify({
            userId,username,priceMax,priceMin,numMax,numMin,futuresId,accounttype,
            pageIndex:currentIndex||pageIndex_buy,
            pageSize:currentSize||pageSize_buy,
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let buyList = result.data.list;
                for(let i=0;i<buyList.length;i++){
                    buyList[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    buyList[i].key = buyList[i].userId
                }
                paginationBuy.total = result.data.totalCount;
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
    //请求数据 做空
    requestTableSell = (currentIndex, currentSize) => {
        const { paginationSell,pageIndex_sell,pageSize_sell,userId,username,priceMax,priceMin,numMax,numMin,futuresId,accounttype } = this.state;
        axios.post(DOMAIN_VIP+'/futuresMerger/queryshort',qs.stringify({
            userId,username,priceMax,priceMin,numMax,numMin,futuresId,accounttype,
            pageIndex:currentIndex||pageIndex_sell,
            pageSize:currentSize||pageSize_sell,
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let sellList = result.data.list;
                for(let i=0;i<sellList.length;i++){
                    sellList[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    sellList[i].key = sellList[i].userId
                }
                paginationSell.total = result.data.totalCount;
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
    //pageIndex
    onChangePageNum_buy = (pageIndex,pageSize) => {
        this.setState({
            pageIndex_buy:pageIndex,
            pageSize_buy:pageSize
        })
        this.requestTableBuy(pageIndex,pageSize)
    }
    //pageSize
    onShowSizeChange_buy = (currentIndex,currentSize) => {
        this.setState({
            pageIndex_buy:currentIndex,
            pageSize_buy:currentSize
        })
    }
    //pageIndex
    onChangePageNum_sell = (pageIndex,pageSize) => {
        this.setState({
            pageIndex_sell:pageIndex,
            pageSize_sell:pageSize
        })
        this.requestTableBuy(pageIndex,pageSize)
    }
    //pageSize
    onShowSizeChange_sell = (currentIndex,currentSize) => {
        this.setState({
            pageIndex_sell:currentIndex,
            pageSize_sell:currentSize
        })
    }
    render(){
        const { paginationBuy,paginationSell,showHide,buyList,sellList,pageTotal_buy,pageIndex_buy,pageSize_buy,pageTotal_sell,pageIndex_sell,pageSize_sell,userId,username,priceMax,priceMin,numMax,numMin,futuresId,accounttype } = this.state
        return (
            <div className='right-con'>
               <div className='page-title'>
                    当前位置： 数据中心 > 期货交易中心 > 盘口管理 > 盘口合并交易
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
               </div>
               <div className='clearfix'></div>
               <div className='row'>
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HandicapMarket marketType={futuresId} handleChange={this.handleSelectMarket} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <AccountTypeList accType={accounttype}  handleChange={this.handleSelectAccount}/>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">用户编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='username' value={username} onChange={this.handleInputChange} />
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
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托均价:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='priceMin' value={priceMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='priceMax' value={priceMax} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托数量:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='numMin' value={numMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='numMax' value={numMax} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="table-responsive">
                                        <table style={{margin:'0'}} className='table table-striped jambo_table bulk_action table-linehei"'>
                                            <thead>
                                                <tr>
                                                    <th style={{textAlign:'left'}} colSpan="6" className="column-title color_green">做多盘口</th>
                                                </tr>
                                            </thead>
                                        </table>
                                        <Table dataSource={buyList} bordered pagination={paginationBuy} locale={{emptyText:'暂无数据'}} >
                                            <Column title='序号' dataIndex='index' key='index' />
                                            <Column title='用户编号' dataIndex='userId' key='userId' />
                                            <Column title='账户类型' dataIndex='userId' key='22'  render={(text,record)=>record.typeall.indexOf(text)>-1?'做市账户':'用户账户'}/>
                                            <Column title='委托均价' dataIndex='avgPrice' key='avgPrice' className='moneyGreen' render={(text)=>toThousands(text)} />
                                            <Column title='委托数量' dataIndex='numbers' key='numbers' />
                                        </Table>                                        
                                    </div>                                    
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="table-responsive">
                                        <table style={{margin:'0'}} className='table table-striped jambo_table bulk_action table-linehei"'>
                                            <thead>
                                                <tr>
                                                    <th style={{textAlign:'left'}} colSpan="6" className="column-title color_red">做空盘口</th>
                                                </tr>
                                            </thead>
                                        </table>
                                        <Table dataSource={sellList} bordered pagination={paginationSell} locale={{emptyText:'暂无数据'}} >
                                            <Column title='序号' dataIndex='index' key='index' />
                                            <Column title='用户编号' dataIndex='userId' key='userId' />
                                            <Column title='账户类型' dataIndex='userId' key='22' render={(text,record)=>record.typeall.indexOf(text)>-1?'做市账户':'用户账户'} />
                                            <Column title='委托均价' dataIndex='avgPrice' key='avgPrice' className='moneyGreen' render={(text)=>toThousands(text)} />
                                            <Column title='委托数量' dataIndex='numbers' key='numbers' />
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