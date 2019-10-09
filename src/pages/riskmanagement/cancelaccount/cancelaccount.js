import React,{Component} from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,DatePicker,Tabs,Pagination,Select,message,Table,Modal } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import StateSelect   from '../modal/stateselect'
import {pageLimit} from '../../../utils'
     

//关键信息修改预警
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const {Column} = Table
class Cancelaccount extends Component{
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
            type:'',
            userid:'',
            editType:['--','资金密码','登录密码','谷歌验证','手机号码'],
            state:'0',
            RowKeys: [],
            limitBtn: [],
        }
        this.clickHide = this.clickHide.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChangeSelect = this.handleChangeSelect.bind(this)
        this.handleChangeState = this.handleChangeState.bind(this)
    }
    componentDidMount(){      
        this.requestTable();
        this.setState({
            limitBtn: pageLimit('coinInfoupdate', this.props.permissList)
        });
    }
    componentDidUpdate(){

    }    
    //请求数据
    requestTable = (currentIndex, currentSize) => {        
        const { pageIndex,pageSize,pagination,userid,type,state} = this.state
        axios.post(DOMAIN_VIP+'/coinInfoupdate/list',qs.stringify({
            userid,type,state,
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
                axios.post(DOMAIN_VIP+'/coinInfoupdate/update',qs.stringify({id,state:el})).then(res => {
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
        this.setState({
            type:'',
            userid:'',
            state:'0',
            pageIndex:PAGEINDEX
        },()=>{
            this.requestTable()
        })
    }
    //点击收起
    clickHide() {
        let {showHide} = this.state
        this.setState({
            showHide:!showHide,
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
    handleChangeState(value){
        this.setState({
            state:value
        })
    }
    handleChangeSelect(value) {
        this.setState({
            type:value
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
        if(item === 2){
            mtitle = '您确定要限制帐户体现功能吗？'
        }else if(item === 3){
            mtitle = '您确定要冻结该帐户吗？'
        }else if(item === 4){
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
    onSelectChange = (selectedRowKeys) => {
        this.setState({
            RowKeys: selectedRowKeys
        });
    };
    batchMarking = () => {
        self = this;
        Modal.confirm({
            title: '你确定要批量标记选中的数据吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + '/coinInfoupdate/updateall', qs.stringify({idlist: self.state.RowKeys.toString()})).then(res => {
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
    render(){
        const {showHide,tableSource,pagination,googVisibal,check,editType,userid,type,pageIndex,RowKeys,limitBtn} = this.state
        
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：风控管理 > 币币交易业务 > 关键信息修改预警
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
            <div className="row">
               <div className="col-md-12 col-sm-12 col-xs-12">
               {showHide &&<div   className="x_panel">
                       <div className="x_content">
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                               <div className="form-group">
                                   <label className="col-sm-3 control-label">用户编号：</label>
                                   <div className="col-sm-8">
                                        <input type="text" className="form-control" name='userid' value={userid} onChange = {this.handleInputChange}/>
                                   </div>
                               </div>
                           </div>
                           <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                               <div className="form-group">
                                   <label className="col-sm-3 control-label">修改类型</label>
                                   <div className="col-sm-8">
                                   <Select style={{width:SELECTWIDTH}} value={type} onChange={this.handleChangeSelect} >
                                           <Option value=''>请选择</Option>
                                           <Option value='5'>资金密码</Option>
                                           <Option value='4'>登录密码</Option>
                                           <Option value='8'>谷歌验证</Option>
                                           <Option value='3'>手机号码</Option>
                                        </Select>
                                   </div>
                               </div>
                           </div>
                           <StateSelect value={this.state.state} changeState={(val)=>this.handleChangeState(val)}/>
                           <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        {limitBtn.indexOf('updateall')>-1 && <Button type="primary" disabled={!RowKeys.length} onClick={this.batchMarking}>批量标记</Button>}
                                    </div>
                            </div>
                            
                       </div>
                   </div>}
                   <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table dataSource={tableSource} bordered pagination={{...pagination,current:pageIndex}} locale={{emptyText:'暂无数据'}}
                                       rowSelection={{
                                           selectedRowKeys: RowKeys,
                                           onChange: this.onSelectChange,
                                       }}
                                        >
                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='用户编号' dataIndex='userid' key='userid' />
                                        <Column title='修改类型' dataIndex='authenTypeName' key='authenTypeName' />
                                        <Column title='修改频率（日）' dataIndex='frequency' key='frequency'  />
                                        <Column title='访问IP' dataIndex='ip' key='ip' />
                                        <Column title='报警时间' dataIndex='alarmtime' key='alarmtime' render={text=>text?moment(text).format(TIMEFORMAT_ss):'--'}/>
                                        <Column title='状态' dataIndex='state' key='state' render={text=>text==1?'已解决':'未解决'} />
                                        <Column title='操作'  dataIndex='id'key='id' render = {(parameter,record)=>{
                                            return (
                                                <div>
                                                    {record.state==0 && (limitBtn.indexOf('update')>-1)?<a href='javascript:void(0);' onClick={()=> this.requestState(parameter,1)}>标记</a>:''}
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
}
export default Cancelaccount
