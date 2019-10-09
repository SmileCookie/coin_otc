import React,{Component} from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,DatePicker,Tabs,Pagination,Select,message,Table,Modal } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20,DAYFORMAT} from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import Markeyquests from '../../common/select/marketrequests'
//推荐币管理
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const {Column} = Table
class Recommends extends Component{
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
            market: ''
        }
        // this.selectFundsType = this.selectFundsType.bind(this)
        this.handleChange = this.handleChange.bind(this)
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render(){
        const {time,pageSize,showHide,tableSource,pagination,googVisibal,fundstype,pageIndex,state,check,accountname,market } = this.state
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：系统中心 > 系统管理 > 推荐币管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide &&<div   className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <Markeyquests title='盘口市场:' market={market} handleChange={this.handleChange} col='3' underLine={true}></Markeyquests>
                                </div>
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
                                                onChange={this.sorter}>
                                                <Column title='序号' dataIndex='index'key='index' />
                                                <Column title='盘口市场' dataIndex='entrustmarket' key='entrustmarket' render ={(entrustmarket)=>entrustmarket.toUpperCase()}/>
                                                <Column title='推荐时间' dataIndex='datetime' key='datetime' render = {(datetime)=>{
                                                     return datetime?moment(datetime).format(DAYFORMAT):'--'
                                                }}/>
                                                <Column title='状态' dataIndex='recommend' key='recommend' render = {recommend =>recommend==0?'未推荐':'已推荐'}/>
                                                <Column title='操作'  dataIndex='id'key='id' render={(id,obj)=>{
                                                    return(
                                                        <div>
                                                            {
                                                                obj.recommend == 0 ? <a href='javascript=(0);' onClick={(e)=>{e.preventDefault();this.requestState(id,1)}} className="mar10">推荐</a> 
                                                                :
                                                                <a href='javascript=(0);' onClick={(e)=>{e.preventDefault();this.requestState(id,0)}} className="mar10">取消推荐</a> 
                                                            }
                                                          
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
        this.requestTable()
    }
    componentDidUpdate(){

    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        
        const { pageIndex,pageSize,pagination,market} = this.state
        axios.post(DOMAIN_VIP+'/recommendCoin/list',qs.stringify({
            entrustmarket:market,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize,
    
        })).then(res => {
            const result = res.data;
            
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
                console.log(this.state.tableSource)
            }else{
                message.warning(result.msg);
            }
        })
    }
    requestState(id,type){
        let self = this;
        Modal.confirm({
            title:`你确定要${type===0?`取消推荐吗` :`推荐吗`}？`,
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                axios.post(DOMAIN_VIP+'/recommendCoin/update',qs.stringify({id,recommend:type})).then(res => {
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
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })  
        this.requestTable(pageIndex,pageSize)
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
    //盘口市场
    handleChange=(value)=>{
        this.setState({
            market: value
        })
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
            market:'',
    }),()=>{
        this.requestTable()
    })
    
    }
    onChangePageNum = (pageIndex,pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    
    //点击收起
    clickHide() {
        let { showHide } = this.state;
            
            this.setState({
                showHide: !showHide,
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
    
    //google弹窗关闭
    onhandleCancel = () =>{
        this.setState({
            googVisibal:false
        })
    }
    sorter=(pagination, filters,sorter)=>{
        console.log(sorter)
    }

}
export default Recommends
