import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,Select,Pagination,message,DatePicker,Table,Modal } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import FundsTypeList from '../../common/select/fundsTypeList'
import ModalInput from './modal/modalInput'
import GoogleCode from '../../common/modal/googleCode'
import { pageLimit } from '../../../utils'
const { RangePicker} = DatePicker
const {Column} = Table
const confirm = Modal.confirm
const Option = Select.Option
export default class ConfirmAccountEntry extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            visible:false,
            modalHtml:'',
            title:'',
            width:'',
            tmp:'',
            tableSource:[],
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
            time:[],
            googVisibal:false,
            check:'',
            item:{},
            fundstype:'0',
            amountS:'',
            amountE:'',
            configtimeS:'',
            configtimeE:'',
            confirmation:'',
            type:0,
            // verifyVisible:false,
            recordVisible:false,
            reviewVisible:false,
            limitBtn:[]


        }
    }
    componentDidMount(){        
        this.requestTable()
        this.requestVisible()
        this.setState({
            limitBtn: pageLimit('feeAccountCheck', this.props.permissList)
        },()=>console.log(this.state.limitBtn))
        
    }
    componentWillUnmount(){
    }
   
    clickHide = () => {
        this.setState({
            showHide:!this.state.showHide
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    resetState = () => {
        this.setState({
            fundstype:'0',
            amountS:'',
            amountE:'',
            configtimeS:'',
            configtimeE:'',
            time:[],
            confirmation:''
        },()=>this.requestTable())
    }
    //控制审核按钮
    // 700100010205  运营提现复核
    // 700100010207  运营提现记录
    requestVisible = () =>{
        axios.get(DOMAIN_VIP+"/sys/menu/getHomePageInfo").then(res => {
            const result = res.data
            console.log(result)
            if(result.code == 0){
                for(let i=0;i<result.menuIdList.length;i++){
                    // if(result.menuIdList[i] == '700100010199'){
                    //     this.setState({
                    //         verifyVisible:true
                    //     })
                    // }else
                     if(result.menuIdList[i] == '700100010205'){
                        this.setState({
                            reviewVisible:true
                        })
                    }else if(result.menuIdList[i] == '700100010207'){
                        this.setState({
                            recordVisible:true
                        })
                    }
                }
            }
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
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    selectFundsType = v => {
        this.setState({
           fundstype:v
        })
    }
    handleSelectChange = v =>{
        this.setState({
            confirmation:v
        })
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            configtimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            configtimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            time:date
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        
        const { pageIndex,pageSize,pagination,undstype,amountS,amountE,configtimeS,configtimeE,type,fundstype,confirmation} = this.state
        axios.post(DOMAIN_VIP+'/feeAccountCheck/account',qs.stringify({
            fundstype,amountS,amountE,configtimeS,configtimeE,type,confirmation,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize,
    
        })).then(res => {
            const result = res.data;
            console.log(result)
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
    openInput=(record)=>{
        this.footer= [
            <Button key="submit" type="more" onClick={()=>this.onSave(record)}>提交</Button>,
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]
        this.setState({
            visible:true,
            title:"备注",
            width:'700px',
            modalHtml:<ModalInput handleInputChange={this.handleInputChange} />
        })
    }
    onSave=(record)=>{
        let self = this
        if(this.state.tmp === ''){
            message.warning('内容不能为空');
            return;
        }
        axios.post(DOMAIN_VIP+'/feeAccountCheck/remarks',qs.stringify({
            choiceId:record.id,remarks:this.state.tmp,    
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                self.requestTable()
                this.setState({
                    visible:false,
                    tmp:''
                })
            }else{
                message.warning(result.msg);
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
    
    //google验证弹窗
    modalGoogleCode = (item,type) => {
        this.setState({
            googVisibal:true,
            item,
            googletype:type
        })
    }
    //google 按钮
    modalGoogleCodeBtn = (value) => {
        const { item,googletype,fundstype } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.onBookModal(item,googletype)
                this.setState({
                    googVisibal:false
                })
                
            }else{
                message.warning(result.msg)
            }
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
    
   //1：记账  
   onBookModal = (item,googletype) =>{
    //    const {fundstype} = this.state;
       let fundsType = item.fundstype;
       let type = googletype
       let choiceId = item.id;
       let txAmount = item.txamount
       let url = type  === 1 ? '/feeAccountCheck/verify1' : '/feeAccountCheck/verify2'
       let  strParameter = qs.stringify({fundsType,type,choiceId,txAmount})
    axios.post(DOMAIN_VIP+url,strParameter).then(res =>{
        this.requestTable()
        console.log(res.data)
    })
   }
   //记账复核
   commonCheckModal = (item, type) => {
    let self = this, mtitle;
    
    if(type === 1){
        mtitle = '您确定要记账吗？'
    }else if( type === 2){
        mtitle = '您确定要记账复核吗？'
    }
    Modal.confirm({
        title: mtitle,
        okText: '确定',
        okType: 'more',
        cancelText: '取消',
        onOk() {
            
            self.modalGoogleCode(item,type)
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
    render(){
        const { showHide,tableSource,pagination,time,googVisibal,check,fundstype,recordVisible,reviewVisible ,confirmation,visible,width,modalHtml,title,limitBtn} = this.state
        return(
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 财务中心 > 运营账户管理 > 确认入账
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <FundsTypeList title='资金类型' fundsType={fundstype} handleChange={this.selectFundsType} />
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">提现金额:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='amountS' value={this.state.amountS} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='amountE' value={this.state.amountE} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">入账状态:</label>
                                        <div className="col-sm-8">
                                            <Select  value = {confirmation} onChange={this.handleSelectChange} style={{width:SELECTWIDTH}}>
                                                <Option value=''>请选择</Option>
                                                <Option value='0'>未记账</Option>
                                                <Option value='1'>待复核</Option>
                                                <Option value='2'>已入账</Option>
                                            </Select>
                                        </div>
                                       
                                        
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">确认时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker 
                                                showTime={{
                                                    defaultValue:[moment('00:00:00','HH:mm,ss'),moment('23:59:59','HH:mm,ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onChangeCheckTime}
                                                value={time}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="right">
                                    <div className="form-group right">
                                        {limitBtn.includes("account")&&<Button type="primary" onClick={this.inquiry}>查询</Button>}
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table dataSource={tableSource} bordered pagination={pagination} locale={{emptyText:'暂无数据'}}  >
                                        <Column title='序号' dataIndex='index'key='index'  />
                                        <Column title='交易流水号' dataIndex='uuid' key='uuid' className='inLine' />
                                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' />
                                        <Column title='发送方' dataIndex='sendwallet' key='sendwallet' />
                                        <Column title='接收方' dataIndex='receivewallet' key='receivewallet' />
                                        <Column title='交易金额' dataIndex='txamount' key='txamount' />
                                        <Column title='网络费' dataIndex='fee' key='fee' />
                                        <Column title='交易类型' dataIndex='dealtype' key='dealtype' render = {(parameter)=>{
                                            switch (parameter){
                                                    case 1 :
                                                        return '充值';
                                                        break;
                                                    case 2 :
                                                        return '提现(热提)';
                                                        break;
                                                    case 3 :
                                                        return '冷钱包到热提钱包转账';
                                                        break;
                                                    case 4 :
                                                        return '其他到热提';
                                                        break;
                                                    case 5 :
                                                        return '其他到热提';
                                                        break;
                                                    case 6 :
                                                        return '其他到冷';
                                                        break;
                                                    case 7 :
                                                        return '冷到其他';
                                                        break;
                                                    case 8 :
                                                        return '热提到其他';
                                                        break;
                                                    default :
                                                        return '--';
                                                        break;
                                            }
                                        }}/>
                                        
                                        <Column title='区块高度' dataIndex='blockheight' key='blockheight' />
                                        <Column title='确认时间' dataIndex='configtime' key= 'configtime' render = {(parameter)=>{
                                        return (
                                            parameter?moment(parameter).format(TIMEFORMAT_ss):'--'
                                            )
                                        }}/>
                                        <Column title='状态' dataIndex='confirmation' key='confirmation' render = {(parameter)=>{
                                                switch (parameter){
                                                    case 0 :
                                                        return '未记账';
                                                        break;
                                                    case 1 :
                                                        return '待复核';
                                                        break;
                                                    case 2 :
                                                        return '已入账';
                                                        break;
                                                    default :
                                                        break;
                                                }
                                            }
                                        }

                                        />
                                        <Column title='操作' dataIndex='confirmation' key='action' render = {(parameter,record)=>{
                                            // switch (parameter) {
                                            //     case 0 :
                                            //         return (<span>{recordVisible?<a className="mar10" onClick={()=>this.commonCheckModal(record,1)} href='javascript:void(0);' >记账</a>:""}</span>);
                                            //         break;
                                            //     case 1 :
                                            //         return (<span>{reviewVisible?<a className="mar10" onClick={()=> this.commonCheckModal(record,2)} href='javascript:void(0);'>记账复核</a>:''}</span>);
                                            //         break;
                                            //     default :
                                            //         return ''
                                            // }
                                            if(parameter === 0){
                                                return (<span>{limitBtn.includes('remarks')?<a className='mar10'onClick={()=>{this.openInput(record)}}>备注</a>:''}{limitBtn.includes('verify1')?<a className="mar10" onClick={()=>this.commonCheckModal(record,1)} href='javascript:void(0);' >记账</a>:""}</span>)
                                            }
                                            if(parameter === 1){
                                                return (<span>{limitBtn.includes('remarks')?<a className='mar10'onClick={()=>{this.openInput(record)}}>备注</a>:''}{limitBtn.includes('verify2')?<a className="mar10" onClick={()=> this.commonCheckModal(record,2)} href='javascript:void(0);'>记账复核</a>:''}</span>)
                                            }
                                            return (<span>{limitBtn.includes('remarks')?<a className='mar10'onClick={()=>{this.openInput(record)}}>备注</a>:''}</span>)
                                        }}/>
                                        <Column title='备注' dataIndex='tmp' key='tmp' />
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    width={width}
                    title={title}
                    onOk={this.handleOk}
                    footer={this.footer}
                    onCancel={this.handleCancel}
                >
                    {modalHtml}
                </Modal>
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