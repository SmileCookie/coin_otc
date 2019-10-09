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
import DetailModal from '../components/detailmodals'

//保值记录异常
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
            modalHtml:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            timerId:'',
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
            title:'',
            time:[],
            types:'',
            solveValue:'0',
            market:'',
            timeS:'',
            timeE:'',
            RowKeys: [],
            limitBtn:[]
        }
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.clickHide = this.clickHide.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChangeState = this.handleChangeState.bind(this)
        this.handleChangeSolve = this.handleChangeSolve.bind(this)
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render(){
        const {time,pageSize,showHide,tableSource,pagination,googVisibal,pageIndex,solveValue,check,market,modalHtml,visible,width,title,RowKeys,limitBtn} = this.state
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：风控管理 > 保值异常 > 保值记录异常
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide &&<div   className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                 <MarketRequests market={market} underLine={true}  handleChange={this.handleChangeSelect} col = '3' />
                                </div>
                                
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList title='资金类型' fundsType={fundstype} handleChange={this.selectFundsType}/>
                                </div> */}
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">状态：</label>
                                        <div className="col-sm-8">
                                                <Select value={solveValue} style={{width:SELECTWIDTH}}
                                                    onChange={this.handleChangeSolve}
                                                >
                                                   <Option value=''>请选择</Option>
                                                    <Option value = '1'>正常</Option>
                                                    <Option value = '2'>异常</Option>
                                                </Select>
                                        </div>
                                    </div> 
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">解决状态：</label>
                                        <div className="col-sm-8">
                                                <Select value={solveValue} style={{width:SELECTWIDTH}}
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
                                                <Column title='序号' dataIndex='index' key='index' />
                                                <Column title='交易市场' dataIndex='entrust_market' key='entrust_market' />
                                                <Column title='用户下单笔数' dataIndex='user_numbers' key='user_numbers' />
                                                <Column title='保值下单笔数' dataIndex='hedging_numbers' key='hedging_numbers' />
                                                <Column title='遗漏笔数' dataIndex='numbers' key='numbers' />
                                                <Column title='解决状态' dataIndex='states'key='states' render={text=>text==1?'已解决':'未解决'}/>
                                                <Column title='报警时间' dataIndex='date_time' key='date_time' render = {(parameter)=>{
                                                    return parameter?moment(parameter).format(TIMEFORMAT_ss):'--'
                                                }}/>
                                                {/* <Column title='标记时间' dataIndex='' key='' render = {(parameter)=>{
                                                    return parameter?moment(parameter).format(TIMEFORMAT_ss):'--'
                                                }}/> */}
                                               <Column title='操作' dataIndex='id' key='id' render = {(parameter,record)=>{
                                                   return(<div>
                                                            {limitBtn.indexOf('update') > -1 && record.states==0?<a href='javascript:void(0)' className='mar10' onClick={()=> this.requestState(parameter,1)}>标记</a>:''}
                                                            <a href='javascript:void(0);' onClick={()=>this.openModal(record)}>查看详情</a>
                                                   </div>
                                                   
                                                   )
                                               }
                                                    
                                                }/>
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
            <Modal
             visible={visible}
             title={title}
             width={width}
             onCancel={this.handleCancel}
             footer={null}
             >
                {modalHtml}
            </Modal>
       </div>
        )
    }
    componentDidMount(){
        this.setState ({
            limitBtn : pageLimit('coinQtRecordabnormal',this.props.permissList)
        })
        this.requestTable()
    }
    componentDidUpdate(){

    }
    shouldComponentUpdate(nextpros,nextstate){
        // console.log(nextstate.tableSource == this.state.tableSource)
      
        // for(var key in nextstate){
        //     if(nextstate[key] == this.state[key]){
        //         console.log(key)
        //         console.log(1)
        //         console.log(this.state[key])
        //         return true
                
        //     }
        // }
        return true
    }
    onChangeCheckTime = (date, dateString) => {
        // console.log(date,dateString)
        this.setState({
            timeS:dateString[0]?moment(dateString[0]).format('x'):'',
            timeE:dateString[1]?moment(dateString[1]).format('x'):'',
            time:date
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        
        const { pageIndex,pageSize,pagination,timeS,timeE,market,solveValue} = this.state
        axios.post(DOMAIN_VIP+'/coinQtRecordabnormal/list',qs.stringify({
            timeS,timeE,market,states:solveValue,
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
                axios.post(DOMAIN_VIP+'/coinQtRecordabnormal/update',qs.stringify({id,states:el})).then(res => {
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
    handleChangeSolve=(value)=>{
        this.setState({
            solveValue:value
        })
    }
    //盘口选择
    handleChangeSelect = value =>{
        this.setState({
            market:value,
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
     inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    //重置按钮
    resetState = () => {
        this.setState(()=>({
            title:'',
            time:[],
            types:'',
            solveValue:'0',
            market:'',
            timeS:'',
            timeE:''
    }),()=>{
        this.requestTable()
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
    //打开弹框
    openModal(item){
        this.lookDetail(item).then((tableSource)=>{
            
            this.setState({
                visible:true,
                title:'详情',
                width:'1400px',
                modalHtml:<DetailModal tableSource={tableSource}/>
            })
            console.log(tableSource)
        }).catch(error => console.log(error))
    }
    lookDetail(item){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/coinQtRecordabnormal/query',qs.stringify({
                market:item.entrust_market,
                timeS:item.start_time,
                timeE:item.end_time
            })).then(res => {
                const result = res.data;
                if(result.code == 0){     
                    if(result.data.list){
                        let tableSource = result.data.list
                        for(let i=0;i<tableSource.length;i++){
                            tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                            tableSource[i].key = tableSource[i].id
                        }
                        resolve(tableSource)                       
                    }else{
                        message.warning(result.data)
                    }           
                }else{
                    message.warning(result.msg)
                    reject(result.msg)
                }
            })
        })
    }
    //弹框关闭
    handleCancel = () => {
        this.setState({
            visible:false
        })
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
                axios.post(DOMAIN_VIP + '/coinQtRecordabnormal/updateall', qs.stringify({idlist: self.state.RowKeys.toString()})).then(res => {
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