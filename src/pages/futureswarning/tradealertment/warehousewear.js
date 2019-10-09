//穿仓单
import React,{Component} from 'react'
import { Divider } from 'antd';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { 
    Button,
    DatePicker,
    Select,
    message,
    Table,
    Modal,
    Card,
    Form,
    Row,
    Col,
    Input
 } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20,PAGESIZE_50} from '../../../conf'
import GoogleCode from '../../common/modal/googleCode'
import Marketrequests from '../../common/select/marketrequests'
import {toThousands} from '../../../utils'


const Big = require('big.js')
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const {Column} = Table
class Warehousewear extends Component{
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
            modalHtml:'',
            width:'',
            title:'',
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
            tableSource:[{time:1}],
            time:[],
            createtimeS:'',
            createtimeE:'',
            conversionS :'',
            conversionE:'',
            userid:'',
            tableList:[],
            modalHtml:'',
            title:'',
            strMoney:'',
            memo:'',
            width:'',
            googleCode:'',
            checkGoogle:'',
            totalMoneyMin:'',
            totalMoneyMax:'',
            limitBtn: [],
            check:'',
            googVisibal:false,
            item:{},
            type:'',
            marketType:'',
            max:'',
            min:''
        }
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.clickHide = this.clickHide.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleChangeState = this.handleChangeState.bind(this)
        // this.handleChangeType = this.handleChangeType.bind(this);
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    componentWillMount(){
        // await this.requestTable()
    }
    componentDidMount(){
        // await this.requestTable()
         
    }
    componentDidUpdate(){

    }
    onChangeCheckTime(date, dateString) {
        this.setState({
            createtimeS:dateString[0]?moment(dateString[0]).format('x'):'',
            createtimeE:dateString[1]?moment(dateString[1]).format('x'):'',
            time:date
        })
       
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        
        const { pageIndex,pageSize,pagination,createtimeS,createtimeE,marketType,userid,max,min} = this.state
        axios.post(DOMAIN_VIP+'',qs.stringify({
            createtimeS,createtimeE,marketType,userid,max,min,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize,
    
        })).then(res => {
        //     const result = res.data;
        //     if(result.code == 0){
        //         let tableSource = result.data.list;
        //         for(let i=0;i<tableSource.length;i++){
        //             tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
        //             tableSource[i].key = tableSource[i].id
        //         }
        //         pagination.total = result.data.totalCount;
        //         pagination.onChange = this.onChangePageNum;
        //         pagination.onShowSizeChange = this.onShowSizeChange
        //         this.setState({
        //             tableSource:tableSource,
        //             pagination,
        //         })
        //     }else{
        //         message.warning(result.msg);
        //     }
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
                axios.post(DOMAIN_VIP+'/coinLargeAccount/update',qs.stringify({id,state:el})).then(res => {
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
       
    requestModal(){

        // const { fundType,userName,moneyMin,moneyMax,freezMoneyMin,freezMoneyMax,pageIndex,pageSize,totalMoneyMin,totalMoneyMax } = this.state
        // axios.post(DOMAIN_VIP+'/userCapital/query',qs.stringify({
        //     fundType:fundType,
        //     userId:userid,
        //     userName:userName,
        //     moneyMin:moneyMin,
        //     moneyMax:moneyMax,
        //     freezMoneyMin:freezMoneyMin,
        //     freezMoneyMax:freezMoneyMax,
        //     pageIndex:pageIndex,
        //     pageSize:pageSize,
        //     totalMoneyMin,totalMoneyMax
        // })).then(res => {
        //     const result = res.data;
        //     if(result.code == 0){
        //         Big.RM = 0;
               
        //         let tableList = result.data.list;
        //         // console.log(tableList)
        //         tableList.map((item, index) => {
        //             item.index = (result.data.currPage - 1) * result.data.pageSize + index + 1;
        //             item.key = index;
        //             item.totalAmount = new Big(item.balance).plus(item.freez)
        //         })
        //         let mtitle = '用户资产明细'
        //         this.setState({
        //             title:mtitle,
        //             pageSize: result.data.pageSize,
        //             pageTotal: result.data.totalCount,
        //             visible:true,
        //             width:'1200px',
        //             modalHtml:<Popwindows {...this.state}  item = {item}list={tableList}/>
        //         })
        //     }else{
        //         message.warning(result.msg)
        //     }
        // })
       
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
            
            pageIndex:PAGEINDEX,
            
        },()=>{
            this.requestTable()
        })
    }
    //输入时 input 设置到 state
    handleInputChange=(event)=>{
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        // this.setState({
        //     [name]: value
        // });
        this.setState(()=>({
            [name]: value
        }))
        
    }
    //交易市场下拉框
    handleChange=(val)=>{
        this.setState({
            marketType:val
        })
    }
    handleChangeType(value) {
        this.setState({
            fundsType:value
        })
    }
    selectFundsType = v => {
        this.setState({
           fundstype:v
        })
    }
    handleChangeState(value){
        this.setState({
            state:value
        })
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
        const { item,type } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code === 0){
                //改变操作符状态
                
                
            }else{
                message.warning(result.msg)
            }
        })
    }
    commonCheckModal = (item, type) => {
    let self = this, mtitle;
    if(item === 1){
        mtitle = '您确定要冻结该帐户吗？'
        
    }
    if(item === 2){
        mtitle = '您确定要限制该帐户提现功能吗？'
       
    }
    if(item === 3){
        mtitle = '您确定要冻结该帐户吗'
       
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
     //弹框 
     onBookModal =async (type,item) => {
       
        let userid = item.userid
        await this.requestModal(userid,item)
    }
    //弹框关闭
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    //编辑弹框
    //google弹窗关闭
    onhandleCancel = () =>{
        this.setState({
            googVisibal:false
        })
    }
    
    
    render(){
        const {time,pageSize,showHide,tableSource,pagination,googVisibal,check, conversionS, conversionE,userid,visible,title,width,pageIndex,marketType,max,min} = this.state
        return (
            <div className='right-con '>
                 <div className="page-title">
                    当前位置：风控管理 > 期货交易业务 > 穿仓单
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
          
               {showHide &&<div className="n_panel">
                       
                       <Card style={{marginBottom:'20px'}}>
            <Form layout="vertical" hideRequiredMark  className="ant-advanced-search-form">
            <Row gutter={16} >
            <Col xl={{ span: 6}} lg={{ span: 8 }} md={{ span: 12 }} sm={24} xs={30} >
                        <Form.Item label={'用户编号：'} style={{marginBottom:0}}>
                            <Input type="text"  onChange ={this.handleInputChange}style={{width:'100%'}} value={userid} name='userid'/>
                        </Form.Item>
                        
                    </Col>
                <Col lg={8} md={12} sm={24} xl={6} xs={30} >
                            <Form.Item >
                                   <Marketrequests changeStyle={true} handleChange={this.handleChange} market={marketType}/>
                            </Form.Item>
                    </Col>
                    <Col xl={{ span:4}} lg={{ span: 8 }} md={{ span: 12 }} sm={24} xs={30} style={{padding:0}}>
                       
                                <Form.Item label={'穿仓金额：'}>
                                <Col xl={{ span: 24}} lg={{ span: 16 }} md={{ span: 24 }} sm={48} xs={60}>
                                    <Input type="text"  onChange ={this.handleInputChange}style={{width:'100%'}} value={max} name='max'/>
                                </Col>
                                </Form.Item>
                    </Col>
                    
                    
                        <Col  xl={{ span: 6, }} lg={{ span: 8 }} md={{ span:12}} sm={24} xs={30}>
                            <Form.Item label={'报警时间：'}>
                                <RangePicker 
                                    showTime={{
                                        defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                    }}
                                            
                                    format={TIMEFORMAT_ss}
                                    placeholder={['Start Time', 'End Time']}
                                    style={{width:'100%'}}
                                    onChange={(date,dateString)=>this.onChangeCheckTime(date,dateString) }
                                    value={time}
                                />
                            </Form.Item>
                        </Col>
                   
                    </Row>
            </Form>
                  
                            <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>                                        
                                    </div>
                            </div>
                    </Card>
                           
                            
                       </div>
                   }
                   <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table dataSource={tableSource} bordered pagination={{...pagination,current:pageIndex}} locale={{emptyText:'暂无数据'}}
                                     onChange={this.sorter}
                                        >
                                        <Column title='序号' dataIndex=''key='' />
                                        <Column title='用户编号' dataIndex='' key='' />
                                        <Column title='交易市场' dataIndex='' key='' />
                                        <Column title='交易类型' dataIndex='' key=''  />
                                        <Column title='仓位数量'  dataIndex=''key='' />
                                        <Column title='持仓价值（BTC）'  dataIndex=''key='' />
                                        <Column title='开仓价格'  dataIndex=''key='' />
                                        <Column title='强平价格'  dataIndex=''key='' />
                                        <Column title='爆仓价格'  dataIndex=''key='' />
                                        <Column title='持仓保证金（BTC)'  dataIndex=''key='' />
                                        <Column title='穿仓金额(BTC)'  dataIndex=''key='' />
                                        <Column title='保险基金金额'  dataIndex=''key='' />
                                        <Column title='穿仓时间'  dataIndex=''key='' />
                                        <Column title='操作'  dataIndex=''key='' render={()=>{
                                            return(
                                                <div>
                                                    <a href='javascript:void(0);' >查看</a>
                                                </div>
                                            )
                                        }} />
                                    </Table>
                                </div>
                            </div>
                        </div>
                
        
            
            <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer = {null}
                    >
                    {this.state.modalHtml}            
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
export default Warehousewear
