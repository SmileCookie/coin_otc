/**外网行情获取失败预警 */
import React,{Component} from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,DatePicker,Tabs,Pagination,Select,message,Table,Modal } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20,DAYFORMAT} from '../../../conf'
import  MarketRequests  from '../../common/select/marketrequests'
import PlatformsList from '../../common/select/platformsList'
import {toThousands} from '../../../utils'

// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const {Column} = Table
class ExtranetQuotesGet extends Component{
    constructor(props){
        super(props);
        this.state ={
            visible:false,
            isreLoad:false,
            showHide:true,
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
            otherplatform:'',
            state:'',
            status:'0'
        }
        this.clickHide = this.clickHide.bind(this)
        
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render(){
        const {time,pageSize,showHide,tableSource,pagination,pageIndex,entrustmarket,otherplatform,state,status } = this.state
        let states = ['中断', '正常']
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：风控管理 > 保值异常 > 外网行情获取失败预警
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide &&<div   className="x_panel">
                            <div className="x_content">
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                 <MarketRequests market={entrustmarket}  handleChange={this.handleChangeSelect} col = '3' />
                                </div> */}
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    {/* <PlatformsList platform={otherplatform} col='3' handleChange={this.handleChangePlatform}/> */}
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">三方平台：</label>
                                        <div className="col-sm-8">
                                                <Select value={otherplatform} onChange = {this.handleChangePlatform} style={{width:SELECTWIDTH}}>
                                                    <Option value=''>请选择</Option>
                                                    <Option value='HUOBI'>HUOBI</Option>
                                                    <Option value='OKEX'>OKEX</Option>
                                                    <Option value='POLONIEX'>POLONIEX</Option>
                                                    <Option value='BINANCE'>BINANCE</Option>
                                                </Select>
                                        </div>
                                    </div> 
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">状态：</label>
                                        <div className="col-sm-8">
                                                <Select value={status} onChange = {this.selectStatus} style={{width:SELECTWIDTH}}>
                                                    <Option value=''>全部</Option>
                                                    <Option value='0'>中断</Option>
                                                    <Option value='1'>正常</Option>
                                                </Select>
                                        </div>
                                    </div> 
                                </div>
                                {/* <SelectStateList value ={state} handleChange={this.selectStatus} />                                */}
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                            <div className="form-group right">
                                                <Button type="primary" onClick={this.inquiry}>查询</Button>
                                                <Button type="primary" onClick={this.resetState}>重置</Button>                                        
                                            </div>
                                    </div>
                                    
                            </div>
                        </div>}
                        <div className="x_panel">
                                    <div className="x_content">
                                        <div className="table-responsive">
                                            <Table dataSource={tableSource} bordered pagination={{...pagination,current:pageIndex}} locale={{emptyText:'暂无数据'}}
                                            onChange={this.sorter}
                                                >
                                                <Column title='序号' dataIndex='index'key='index' />
                                                {/* <Column title='交易市场' dataIndex='entrustmarket' key='entrustmarket' /> */}
                                                <Column title='第三方平台' dataIndex='otherPlatform' key='otherPlatform' />
                                                <Column title='状态' dataIndex='state' key='state' render={text=>states[text]} />
                                                <Column title='报警时间' dataIndex='dateTime' key='dateTime' render = {(parameter)=>{
                                                    return parameter?moment(parameter).format(TIMEFORMAT):'--'
                                                }}/>
                                                <Column title='停止时间(分)' dataIndex='stopTime' key='stopTime' render={text=>{
                                                    return this.changeTime(text)
                                                    // return text?moment.duration(text).asMinutes().toFixed(2):'--'
                                                    }}/>
                                                <Column title='恢复时间' dataIndex='restoreTime' key='restoreTime' render = {(parameter)=>{
                                                    return parameter?moment(parameter).format(TIMEFORMAT):'--'
                                                }}/>
                                                {/* <Column title='操作' dataIndex='op' key='op' render = {(text,record)=>
                                                   {return record.state==1?'':<a href='javascript:void(0);' onClick={()=> this.requestState(record.id)}>标记</a>}
                                                }/> */}
                                            </Table>
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
        const { pageIndex,pageSize,types,pagination,entrustmarket,otherplatform,state,status } = this.state
        axios.post(DOMAIN_VIP+'/coinQtForfailure/list',qs.stringify({
            // entrustmarket,
            otherplatform,state:status,
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
    //格式化时间
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
    }
    //三方平台
    handleChangePlatform = value=>{
        this.setState({
            otherplatform:value
        })
    }
    selectStatus = (v) => {
        this.setState({
            state:v
        })
    }
    selectStatus = status => {
        this.setState({status})
    }
    //
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
        },()=>this.requestTable())       
    }
    //重置按钮
    resetState = () => {
        this.setState(()=>({
           entrustmarket:'',
           otherplatform:'',
           status:'0'
    }),()=>{
        this.requestTable()
        })
    
    }
    // requestState = (id) => {
    //     let self = this;
    //     Modal.confirm({
    //         title:'你确定要标记吗？',
    //         okText:'确定',
    //         okType:'more',
    //         cancelText:'取消',
    //         onOk(){
    //             axios.post(DOMAIN_VIP+'/coinQtForfailure/overview',qs.stringify({id})).then(res => {
    //                 const result = res.data;
    //                 if(result.code == 0){
    //                     self.requestTable()
    //                 } 
    //             })
    //         },
    //         onCancel(){
    //             console.log('Cancel')
    //         } 
    //     })
    // }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
            
            this.setState({
                showHide: !showHide,
            })
    }
    
}
export default ExtranetQuotesGet




