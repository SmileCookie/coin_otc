import React,{Component} from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,DatePicker,Tabs,Pagination,Select,message,Table,Modal } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import  MarketRequests  from '../../common/select/marketrequests'
import {toThousands,pageLimit} from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList';
//资金低于预警值
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const {Column} = Table
class PrewarningValue extends Component{
    constructor(props){
        super(props);
        this.state ={
            googVisibal:false,
            visible:false,
            isreLoad:false,
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            
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
            time:[],
            types:'',
            fundstype:'0',
            state:'0',
            createtimeS:'',
            alarmtimemax:'',
            createtimeE:'',
            platform:'',
            accountname:'',
            RowKeys: [],
            limitBtn:[]
        }
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.clickHide = this.clickHide.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChangeState = this.handleChangeState.bind(this)
        this.selectFundsType = this.selectFundsType.bind(this)
        this.handleChangeSolve = this.handleChangeSolve.bind(this)
        this.handleChangePlatform = this.handleChangePlatform.bind(this)
        this.handleEnter = this.handleEnter.bind(this)
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render(){
        const {time,pageSize,showHide,tableSource,pagination,googVisibal,fundstype,pageIndex,state,check,accountname,platform,RowKeys,limitBtn } = this.state
        return (
            <div className='right-con ' tabIndex = {1} onKeyDown={this.handleEnter}>
                <div className="page-title" >
                    当前位置：风控管理 > 期货交易业务 > 资金低于预警值
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide &&<div   className="x_panel">
                            <div className="x_content" >
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保值平台：</label>
                                        <div className="col-sm-8">
                                                <Select  value = {platform} style={{width:SELECTWIDTH}} onChange={this.handleChangePlatform}>
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
                                        <label className="col-sm-3 control-label">保值账号：</label>
                                        <div className="col-sm-8">
                                                <input type="text" className="form-control" name='accountname' value={accountname}  onChange = {this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList title='资金类型' fundsType={fundstype} handleChange={this.selectFundsType}/>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">解决状态：</label>
                                        <div className="col-sm-8">
                                                <Select value={state} style={{width:SELECTWIDTH}}
                                                    onChange={this.handleChangeSolve}
                                                >
                                                    <Option value = ''>全部</Option>
                                                    <Option value = '1'>已解决</Option>
                                                    <Option value = '0'>未解决</Option>
                                                </Select>
                                        </div>
                                    </div> 
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">报警时间</label>
                                        <div className="col-sm-8">
                                                <RangePicker 
                                                showTime={{
                                                    defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime }
                                                value={time}
                                            />
                                                
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                            <div className="form-group right">
                                                <Button type="primary" onClick={this.inquiry}>查询</Button>
                                                <Button type="primary" onClick={this.resetState}>重置</Button>
                                                {limitBtn.indexOf('updateall') > -1 &&<Button type="primary" disabled={!RowKeys.length} onClick={this.batchMarking}>批量标记</Button>}
                                            </div>
                                    </div>
                                    
                            </div>
                        </div>}
                        <div className="x_panel">
                                    <div className="x_content">
                                        <div className="table-responsive">
                                            <Table dataSource={tableSource} bordered pagination={{...pagination,current:pageIndex}} locale={{emptyText:'暂无数据'}}
                                            onChange={this.sorter}
                                                   rowSelection={{
                                                       selectedRowKeys: RowKeys,
                                                       onChange: this.onSelectChange,
                                                   }}
                                                >
                                                <Column title='序号' dataIndex='index'key='index' />
                                                <Column title='保值平台' dataIndex='platform' key='platform' />
                                                <Column title='保值账号' dataIndex='accountname' key='accountname' />
                                                <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' />
                                                <Column title='可用余额' dataIndex='balance' key='balance' className='moneyGreen'render={text=>toThousands(text,true)}/>
                                                <Column title='触发余额' dataIndex='triggerbalance' key='triggerbalance' className='moneyGreen'render={text=>toThousands(text,true)}/>
                                                <Column title='总余额' dataIndex='totalbalance' key='totalbalance' className='moneyGreen'render={text=>toThousands(text,true)}/>
                                                <Column title='报警时间' dataIndex='datetime' key='datetime' render = {(parameter)=>{
                                                    return parameter?moment(parameter).format(TIMEFORMAT_ss):'--'
                                                }}/>
                                                <Column title='标记时间' dataIndex='tagtime' key='tagtime' render = {(parameter)=>{
                                                    return parameter?moment(parameter).format(TIMEFORMAT_ss):'--'
                                                }}/>
                                                <Column title='解决状态' dataIndex='state' key='state' render={text=>text==1?'已解决':'未解决'} />
                                                <Column title='操作'  dataIndex='id'key='id' render = {(parameter,record)=>{
                                                    return (
                                                        <div>
                                                            {limitBtn.indexOf('update') > -1 &&record.state==0?<a  onClick={(e)=> {e.preventDefault();this.requestState(parameter,1) }}href='javascript:(0)'>标记</a>:''}
                                                        </div>
                                                    )
                                                }}/>
                                            </Table>
                                        </div>
                                </div>
                            </div>
                    </div>
        
            </div>
            <GoogleCode 
                wrappedComponentRef={this.saveFormRef}
                check={check}
                handleInputChange={this.handleInputChange}
                mid='CAE'
                visible={googVisibal}
                onCancel={this.onhandleCancel}
                onCreate={this.handleCreate}/>
       </div>
        )
    }
    componentDidMount(){
        this.setState ({
            limitBtn : pageLimit('coinQtBelowwarning',this.props.permissList)
        })
        this.requestTable()
    }
    componentDidUpdate(){

    }
    onChangeCheckTime = (date, dateString) => {
        // console.log(date,dateString)
        this.setState({
            createtimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            createtimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            time:date
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        
        const { pageIndex,pageSize,types,pagination,state,createtimeS,createtimeE,fundstype,accountname,platform} = this.state
        axios.post(DOMAIN_VIP+'/coinQtBelowwarning/list',qs.stringify({
            types,state,createtimeS,createtimeE,fundstype,accountname,platform,
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
    requestState(parameter,el){
        let id = parameter,self = this;
        Modal.confirm({
            title:'你确定要标记吗？',
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                axios.post(DOMAIN_VIP+'/coinQtBelowwarning/update',qs.stringify({id,state:el})).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        self.requestTable()
                    } 
                })
            },
            onCancel(){
                console.log('Cancel')
            } 
        })
    }
    handleChangeSolve(value){
        this.setState({
            state:value
        })
    }
    handleChangePlatform(value){
        this.setState({
            platform:value
        })
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
     handleEnter=(e)=>{
        console.log(1)
        if(e.keyCode ==13){
            
            this.inquiry()
        }
    }
     inquiry = () => {
        this.setState({
            pageIndex :PAGEINDEX,
        },()=>this.requestTable())       
    }
    
    //重置按钮
    resetState = () => {
        this.setState(()=>({
            fundstype:'0',
            state:'0',
            createtimeS:'',
            alarmtimemax:'',
            createtimeE:'',
            platform:'',
            accountname:'',
            time:[],
    }),()=>{
        this.requestTable()
    })
    
    }
    //资金类型
    selectFundsType = v => {
        this.setState({
            fundstype:v
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
            
            this.setState({
                showHide: !showHide,
            })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name] : value,
        })
    }
    
    handleChange(value){
        this.setState({
            types:value
        })
    }
    handleChangeState(value){
        this.setState({
            state:value
        })
    }
    handleCreate = () =>{
        const form = this.formRef.props.form;
        form.validateFields((err, valus) => {
            if(err){
                return;
            }
            form.resetFields();
            this.modalGoogleCodeBtn(valus)
        })
    }
    saveFormRef = formRef => {
        this.formRef = formRef
    }
    //google 验证弹窗
    modalGoogleCode = (item,type) => {
        this.setState({
            googVisibal:true,
            item,
            googletype:type
        })
    }
    //google按钮
    modalGoogleCodeBtn = (value) => {
        const { item,googletype } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
              
                this.setState({
                    googVisibal:false
                })
                
            }else{
                message.warning(result.msg)
            }
        })
    }
    commonCheckModal = (item, type) => {
    let self = this, mtitle;
    if(item === 1){
        mtitle = '您确定要限制该帐户体现功能吗？'
    }else if(item === 2){
        mtitle = '您确定要冻结该帐户吗？'
    }else if(item === 3){
        mtitle = '您确定要冻结该帐户吗？'
    }
    Modal.confirm({
        title: mtitle,
        okText: '确定',
        okType: 'more',
        cancelText: '取消',
        onOk() {
            self.modalGoogleCode(item,type);
            
        },
        onCancel() {
            console.log('Cancel');
        },
    });
}
    //google弹窗关闭
    onhandleCancel = () =>{
        this.setState({
            googVisibal:false
        })
    }
    sorter=(pagination, filters,sorter)=>{
        console.log(sorter)
    }
    onSelectChange = (selectedRowKeys) => {
        this.setState({
            RowKeys: selectedRowKeys
        });
    };
    batchMarking = () => {
        let self = this;
        Modal.confirm({
            title: '你确定要批量标记选中的数据吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + '/coinQtBelowwarning/updateall', qs.stringify({idlist:self.state.RowKeys.toString()})).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        self.setState({
                            RowKeys: []
                        }, ()=> {
                            self.requestTable();
                        });
                    }
                })
            },
            onCancel() {
            }
        })
    }
}
export default PrewarningValue
