
/**行情偏离 */
import React,{Component} from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,DatePicker,Tabs,Pagination,Select,message,Table,Modal } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20,DAYFORMAT} from '../../../conf'
import  MarketRequests  from '../../common/select/marketrequests'
import {toThousands} from '../../../utils'

const TabPane = Tabs.TabPane;
const Option = Select.Option;
const {Column} = Table
class QuotesDeviate extends Component{
    constructor(props){
        super(props);
        this.state ={
            visible:false,
            keyValue:false,
            isreLoad:false,
            showHide:true,
            showButton:false,
            tabKey:'1',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            width:'',
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            
            tableSource:[],
            entrustmarket:'',
            state:'0',
            pandect:[],
            status: '',
            vdsData: []
        }
        this.clickHide = this.clickHide.bind(this);
        this.handleChange = this.handleChange.bind(this)
        
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render(){
        const {time,pageSize,showHide,tableSource,pagination,pageIndex,entrustmarket ,state,pandect,status,vdsData,tabKey} = this.state
        // console.log(pandect)
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：风控管理 > 保值异常 > 行情偏离
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide &&<div   className="x_panel">
                            <div className="x_content">
                                {(tabKey == '1' || tabKey == '2') && (
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <MarketRequests market={entrustmarket}  handleChange={this.handleChangeSelect} col = '3' underLine={true}/>
                                    </div>
                                )}
                                {(tabKey == '1' || tabKey == '2') && (
                                    <div  className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">状态：</label>
                                            <div className="col-sm-8">
                                                <Select value={state} onChange = {this.handleChange} style={{width:SELECTWIDTH}}>
                                                    <Option value=''>全部</Option>
                                                    <Option value='0'>异常</Option>
                                                    <Option value='1'>正常</Option>
                                                </Select>
                                            </div>
                                        </div>
                                    </div>
                                )}
                                {tabKey == '3' && (
                                    <div  className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">状态：</label>
                                            <div className="col-sm-8">
                                                <Select value={status} onChange = {this.handleStatusChange} style={{width:SELECTWIDTH}}>
                                                    <Option value=''>全部</Option>
                                                    <Option value='0'>BTC涨</Option>
                                                    <Option value='1'>USDT涨</Option>
                                                    <Option value='1'>正常</Option>
                                                </Select>
                                            </div>
                                        </div>
                                    </div>
                                )}
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                            <div className="form-group right">
                                                {this.state.showButton&&<Button type="primary" onClick={this.inquiry}>刷新</Button>}
                                                <Button type="primary" onClick={this.inquiry}>查询</Button>
                                                <Button type="primary" onClick={this.resetState}>重置</Button>                                        
                                            </div>
                                    </div>
                                
                                    
                            </div>
                        </div>}
                        <div className="x_panel">
                                    <div className="x_content">
                                        <div className="table-responsive">
                                            <Tabs defaultActiveKey="1" onChange={this.handleTabChange} >
                                                <TabPane tab='行情偏离' key='1'>
                                                    <Table dataSource={tableSource} bordered pagination={{...pagination,current:pageIndex}} locale={{emptyText:'暂无数据'}}
                                                onChange={this.sorter}
                                                    >
                                                        <Column title='序号' dataIndex='index'key='index' />
                                                        <Column title='交易市场' dataIndex='entrustMarket' key='entrustMarket' render = {(parameter)=>{
                                                            return parameter.toUpperCase()
                                                        }}/>
                                                        <Column title='买卖方向' dataIndex='types' key='types' render ={parameter => parameter == 1 ? '买':'卖'}/>
                                                        <Column title='平台价格' dataIndex='transactionPrice' key='transactionPrice' className='moneyGreen' render={ parameter => toThousands(parameter,true)}/>
                                                        <Column title='第三方均价' dataIndex='otherTransactionPrice' key='otherTransactionPrice' className='moneyGreen' render={ parameter => toThousands(parameter,true)}/>                                                        }/>
                                                        <Column title='行情偏离百分比' dataIndex='marketDeparture' key='marketDeparture' render={text=>text*100+'%'}/>
                                                        <Column title='持续时间' dataIndex='continuousTime' key='continuousTime' render={(parameter)=>{
                                                            return this.changeTime(parameter)
                                                        }}/>
                                                        <Column title='状态' dataIndex='state' key='state' render ={parameter => parameter == 1 ? '正常':'异常'}/>
                                                        <Column title='报警时间' dataIndex='dateTime' key='dateTime' render = {(parameter)=>{
                                                            return parameter?moment(parameter).format(TIMEFORMAT):'--'
                                                        }}/>
                                                        <Column title='恢复时间' dataIndex='restoreTime' key='restoreTime' render = {(parameter)=>{
                                                            return parameter?moment(parameter).format(TIMEFORMAT):'--'
                                                        }}/>

                                                </Table>
                                                </TabPane>
                                                <TabPane tab='行情总览' key='2'>
                                                    <Table dataSource={pandect} bordered pagination={{...pagination,current:pageIndex}} locale={{emptyText:'暂无数据'}}
                                                    onChange={this.sorter}
                                                        >
                                                            <Column title='序号' dataIndex='index'key='index' />
                                                            <Column title='交易市场' dataIndex='market' key='market' />
                                                            <Column title='币安' dataIndex='binance' key='binance' />
                                                            <Column title='火币' dataIndex='huobi' key='huobi' />
                                                            <Column title='P网' dataIndex='poloniex' key='poloniex' />
                                                            <Column title='Btcwinex' dataIndex='mine' key='mine' />
                                                            <Column title='OKex' dataIndex='okex' key='okex' />
                                                            <Column title='均价' dataIndex='avg' key='avg' />
                                                    </Table>   
                                                </TabPane>
                                                {/*<TabPane tab='VDS行情' key='3'>*/}
                                                    {/*<Table dataSource={vdsData} bordered pagination={{...pagination,current:pageIndex}} locale={{emptyText:'暂无数据'}}*/}
                                                           {/*onChange={this.sorter}*/}
                                                    {/*>*/}
                                                        {/*<Column title='序号' dataIndex='index'key='index' />*/}
                                                        {/*<Column title='USDT盘口价格（$）' dataIndex='market' key='market' />*/}
                                                        {/*<Column title='BTC盘口价格（$）' dataIndex='binance' key='binance' />*/}
                                                        {/*<Column title='行情偏离' dataIndex='huobi' key='huobi' />*/}
                                                        {/*<Column title='持续时间' dataIndex='poloniex' key='poloniex' />*/}
                                                        {/*<Column title='状态' dataIndex='bitfinex' key='bitfinex' />*/}
                                                        {/*<Column title='报警时间' dataIndex='okex' key='okex' />*/}
                                                        {/*<Column title='恢复时间' dataIndex='avg' key='avg' />*/}
                                                    {/*</Table>*/}
                                                {/*</TabPane>*/}
                                            </Tabs>
                                           
                                        </div>
                                </div>
                            </div>
                    </div>
        
            </div>
            
       </div>
        )
    }
    componentDidMount(){
        this.requestTable()
    }
    componentDidUpdate(){

    }
    
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        
        const { pageIndex,pageSize,pagination,state,entrustmarket,} = this.state
        axios.post(DOMAIN_VIP+'/coinQtMarketdeparture/list',qs.stringify({
            market:entrustmarket,state,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize,
    
        })).then(res => {
            const result = res.data;
            // console.log(result)
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.data.totalCount;
                pagination.onChange = this.onChangePageNum;
                pagination.onShowSizeChange = this.onShowSizeChange
                this.setState({
                    tableSource:tableSource,
                    pagination,
                })
            }else{
                message.warning(result.msg);
            }
        })
    }
    //切换面板后请求数据
    requestPandect = (currentIndex, currentSize) => {
        
        const { pageIndex,pageSize,pagination,entrustmarket,} = this.state
        axios.post(DOMAIN_VIP+'/coinQtMarketdeparture/query',qs.stringify({
            market:entrustmarket,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize,
    
        })).then(res => {
            const result = res.data;
           
            if(result.code == 0 && result.data.list){
                let tableSource = result.data.list;
                if(tableSource.length > 0){
                    for(let i=0;i<tableSource.length;i++){
                        tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                        tableSource[i].key = tableSource[i].id || i
                    }
                    pagination.total = result.data.length;
                    pagination.onChange = this.onChangePageNum;
                    pagination.onShowSizeChange = this.onShowSizeChange
                }
                
                this.setState({
                    pandect:tableSource,
                    pagination,
                })
            }else{
                message.warning(result.msg);
                    pagination.total = '';
                    pagination.onChange = this.onChangePageNum;
                    pagination.onShowSizeChange = this.onShowSizeChange
                    this.setState({
                        pagination,
                        // keyValue:false,
                    })
            }
        })
    }
     
    //格式化时间
    //65564416
    changeTime(time){
        if(time){
            let hour = parseInt(time / 3600000)
            let newtime =parseInt(time / 60000 % 60);
            let second =parseInt(time / 1000 % 60) 
            if(hour > 0){
                return hour + '时' + newtime + '分' + second+'秒'
            }else{
                return newtime + '分' + second+'秒'
            }
        }else{
            return '--'
        }
    }
    //交易市场下拉菜单
    handleChangeSelect = value=> {
        this.setState({
            entrustmarket:value,
        })
    };
    handleChange = value =>{
        this.setState({
            state:value
        })
    };
    handleStatusChange = value => {
        this.setState({
            status: value
        })
    };
    //切换面板后
    handleTabChange = key =>{
        let num = this.state.pandect.length;
        if(key === '2'){
            this.requestPandect(PAGEINDEX);
            this.setState({
                showButton:true,
                tabKey:'2'
            })
        }
        if(key === '2'&& num == 0 ){
            
            this.setState({
                keyValue:true,
            })
        }
        if(key === '1'){
            this.requestTable(PAGEINDEX);
            this.setState({
                keyValue:false,
                showButton:false,
                tabKey:'1'
            })
        }
        if(key === '3') {
            // 请求第三个列表数据
            this.setState({
                keyValue:false,
                showButton:false,
                tabKey:'3'
            })
        }
    }
    onChangePageNum = (pageIndex,pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
     //查询按钮
     inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>{
            if(this.state.tabKey == '1'){
                this.requestTable()
              }else if(this.state.tabKey == '2'){
                this.requestPandect()
              }
        })  
            
           
    }
    //重置按钮
    resetState = () => {
        this.setState(()=>({
            entrustmarket:'',
            state:'0',
            status: ''
    }),()=>{
        if( this.state.tabKey == '2' ){
            this.requestPandect()
          }else if ( this.state.tabKey == '1' ){
              this.requestTable()
          }
    })
    
    }
   
    //点击收起
    clickHide() {
        let { showHide } = this.state;
            
            this.setState({
                showHide: !showHide,
            })
    }
    
}

export default QuotesDeviate
