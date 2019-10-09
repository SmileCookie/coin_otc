import React from 'react'
import ReactDOM from 'react-dom'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalsaveMars from './modal/modalsaveMars'
import GoogleCode from '../../common/modal/googleCode'
import { toThousands,pageLimit,tableScroll } from '../../../utils'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Select,Modal,message} from 'antd'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;
   
export default class Mars extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            fundsType:"0",
            beginTime:"",
            endTime:"",
            tableList:"",
            time:[],
            changePosition:'',
            companyChangeType:'',
            accountingType:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            visible:false,
            title:'',
            id:'0',
            modalHtml:'',
            summary:'',
            income:0,
            expense:0,
            comment:'',
            ModalfundsType:'',
            ModalchangePosition:'',
            ModalaccountingType:'',
            ModalcompanyChangeType:'',
            tranDate:moment().format("YYYY-MM-DD"),
            summarydy:'',
            commentdy:'',
            limitBtn: [],
            item:'',
            loading:false,
            check:'',
            googVisibal:false,
            googleSpace:{},
            type:'',
            check:'',
            height:0,
            tableScroll:{
                tableId:'MARSROP',
                x_panelId:'MARSROPX',
                defaultHeight:500,
                height:0,
            }

        }
        this.clickHide = this.clickHide.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.positionChange = this.positionChange.bind(this)
        this.accountType = this.accountType.bind(this)
        this.companyChange = this.companyChange.bind(this)
        this.showDetail = this.showDetail.bind(this)
        this.handleCancel =this.handleCancel.bind(this)
        this.ModalhandleChangeType = this.ModalhandleChangeType.bind(this)
        this.ModalpositionChange = this.ModalpositionChange.bind(this)
        this.ModalaccountType = this.ModalaccountType.bind(this)
        this.ModalcompanyChange = this.ModalcompanyChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.ModalchengeTime = this.ModalchengeTime.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)

        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.saveAudit = this.saveAudit.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('mars', this.props.permissList)
        })
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillReceiveProps(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillUnmount(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight){
        this.setState({
            xheight
        })
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            beginTime:dateString[0]+" 00:00:00",
            endTime:dateString[1]+" 23:59:59",
            time:date
        })
       
    }
      //输入时 input 设置到 satte
      handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
     //修改
     showDetail(item){
         const {accountType} = this.state
        this.footer=[
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modifyRoleBtn(item)}>
                保存修改
            </Button>,
        ]
        console.log(item)
        if(item)
        this.setState({
            item:item,
            visible:true,
            title:'编辑火星表',
            modalHtml:<ModalsaveMars item={item} accountType={accountType}
             ModalhandleChangeType={this.ModalhandleChangeType}
             ModalpositionChange={this.ModalpositionChange}
             ModalaccountType={this.ModalaccountType}
             ModalcompanyChange={this.ModalcompanyChange}
             handleInputChange = {this.handleInputChange}
             ModalchengeTime = {this.ModalchengeTime}
             />,
            id:item.id?item.id:'0',
            summary:item.summary?item.summary:'',
            income:item.income?item.income:'0',
            expense:item.expense?item.expense:'0',
            comment:item.comment?item.comment:'',
            tranDate:item.transDate?moment(item.transDate).format("YYYY-MM-DD"):moment().format("YYYY-MM-DD"),
            ModalfundsType:item.fundsType?String(item.fundsType):'0',
            ModalchangePosition:item.changePosition?item.changePosition:'',
            ModalaccountingType:item.accountingType?item.accountingType:'',
            ModalaccountingType:item.accountingType?item.accountingType:'',
            ModalcompanyChangeType:item.companyChangeType?item.companyChangeType:'',
        })
    }
    modifyRoleBtn(item){
        const {id,summary,income,tranDate,expense,comment,ModalfundsType, ModalchangePosition,ModalaccountingType,ModalcompanyChangeType,} = this.state
        console.log(item)
        if(item){
            console.log(111111)
        }
        if(tranDate == ''){
            message.warning('交易日期不能为空！')
        } else if(summary==''){
            message.warning('摘要不能为空！')
        }else if(ModalfundsType == ''){
            message.warning('请选择币种类型！')
        }else if(income == ''){
            message.warning('请输入收入！')
        }else if(expense == ''){
            message.warning('请输入支出！')
        }else if(ModalchangePosition == ''){
            message.warning('请选择变动位置！')
        }else if(ModalcompanyChangeType == ''){
            message.warning('请选择公司资金变动类型！')
        }else if(ModalaccountingType == ''){
            message.warning('统计类别！')
        }else{
            this.setState({
                loading:true
            })
            axios.post(DOMAIN_VIP+"/mars/saveMars",qs.stringify({
            id,summary,income,tranDate,expense,comment,fundsType:ModalfundsType,changePosition:ModalchangePosition,accountingType:ModalaccountingType,companyChangeType:ModalcompanyChangeType
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg);
                this.requestTable();
                this.setState({
                    visible:false,
                    loading:false,
                })
            }else{
                message.error(result.msg);
                this.setState({
                    loading:false
                })
            }
        })
        }
        
    }
    marsAudit(id){
        let self = this
        Modal.confirm({
            title: '确认审核该条数据？',
            okText: '确认',
            cancelText: '取消',
            onOk() {self.modalGoogleCode(id)},
            okType:'danger',
            onCancel() {},
        });
    }

     //g //google 按钮
    modalGoogleCodeBtn(value){
        const {googleSpace } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                    this.setState({
                        googVisibal: false
                    })
                    this.saveAudit(googleSpace)
                    
            }else{
                
                message.warning(result.msg)
            }
        })
    }
    saveAudit(id){
        axios.post(DOMAIN_VIP+"/mars/check",qs.stringify({
            id
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                    this.requestTable()
                    message.success(result.msg)
            }else{
                
                message.warning(result.msg)
            }
    })
}
    //oogle 验证弹窗
     modalGoogleCode(id){
        this.setState({
            googVisibal:true,
            googleSpace:id,
        })
    }
    //弹窗隐藏
    handleCancel(){
        this.setState({ 
            visible: false,
            loading:false
        });
    }
     //点击收起
     clickHide() {
        let { showHide,xheight,pageSize } = this.state;
            if(showHide&&pageSize>10){
                this.setState({
                    showHide: !showHide,
                    height:xheight,
                })
            }else{
                this.setState({
                    showHide: !showHide,
                    height:0
                })
            }
            // this.setState({
            //     showHide: !showHide,
            // })
    }
    handleChangeType(value) {
        this.setState({
            fundsType:value
        })
    }
    positionChange(value){
        this.setState({
            changePosition:value
        }) 
    }
    accountType(value){
        this.setState({
            accountingType:value
        })
    }
    companyChange(value){
        this.setState({
            companyChangeType:value
        })
    }
    ModalchengeTime(dateString){
        this.setState({
            tranDate:dateString
        }) 
    }
    ModalhandleChangeType(value) {
        this.setState({
            ModalfundsType:value
        })
    }
    ModalpositionChange(value){
        this.setState({
            ModalchangePosition:value
        }) 
    }
    ModalaccountType(value){
        this.setState({
            ModalaccountingType:value
        })
    }
    ModalcompanyChange(value){
        this.setState({
            ModalcompanyChangeType:value
        })
    }
    onResetState(){
        this.setState({
            fundsType:"0",
            beginTime:"",
            endTime:"",
            time:[],
            changePosition:'',
            companyChangeType:'',
            accountingType:'',
            commentdy:'',
            summarydy:''
        })
    }
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        })
        this.requestTable(page,pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }
    requestTable(currIndex,currSize){
        const { beginTime,endTime,fundsType,changePosition,companyChangeType,accountingType,pageIndex,pageSize,summarydy,commentdy} = this.state
        axios.post(DOMAIN_VIP+"/mars/query",qs.stringify({
            beginTime,endTime,fundsType,changePosition,companyChangeType,accountingType,summary:summarydy,comment:commentdy,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.modalGoogleCodeBtn(values)
        });
      }
      saveFormRef(formRef){
        this.formRef = formRef;
      }
        //谷歌弹窗关闭
    onhandleCancel(){
        this.setState({
            googVisibal: false 
        })
    }
    render(){
        const {item,showHide,modalHtml,visible,title,paraList,fundsType,tableList,time,changePosition,companyChangeType,accountingType,pageIndex,pageSize,pageTotal,summarydy,commentdy,limitBtn} = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 财务类报表 > 火星表
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList  fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker 
                                        format="YYYY-MM-DD"
                                        placeholder={['Start Time', 'End Time']}
                                       onChange={this.onChangeCheckTime }
                                       value={time}
                                       />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">变动位置：</label>
                                        <div className="col-sm-8">
                                        <Select value={changePosition}  style={{ width: SELECTWIDTH }} onChange={this.positionChange} >
                                            <Option value=''>请选择</Option> 
                                            <Option value='1'>平台</Option>
                                            <Option value='2'>钱包</Option>
                                        </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">公司资金变动类型：</label>
                                        <div className="col-sm-8">
                                        <Select value={companyChangeType}  style={{ width: SELECTWIDTH }} onChange={this.companyChange} >
                                            <Option value=''>请选择</Option> 
                                            <Option value='1'>公司资金减少（T）</Option>
                                            <Option value='2'>公司资金增加（T）</Option>
                                            <Option value='3'>公司资金减少（F）</Option>
                                            <Option value='4'>公司资金增加（F）</Option>
                                            <Option value='5'>不影响公司资金</Option>
                                        </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">统计类型：</label>
                                        <div className="col-sm-8">
                                        <Select value={accountingType}  style={{ width: SELECTWIDTH }} onChange={this.accountType} >
                                            <Option value=''>请选择</Option> 
                                            <Option value='1'>资金减少（T）</Option>
                                            <Option value='2'>资金增加（T）</Option>
                                            <Option value='3'>资金减少（F）</Option>
                                            <Option value='4'>资金增加（F）</Option>
                                            <Option value='5'>不影响本表</Option>
                                        </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">摘要：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="summarydy" value={summarydy} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4 left">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">备注：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="commentdy" value={commentdy} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                               
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable()}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        <Button type="primary" onClick={this.showDetail}>添加</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                         }
                            <div className="x_panel">
                            
                                    <div className="x_content">
                                        <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                            <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                                <thead>
                                                    <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title">交易日期</th>
                                                    <th className="column-title min_68px">币种类型</th>
                                                    <th className="column-title">摘要</th>
                                                    <th className="column-title">收入</th>   
                                                    <th className="column-title">支出</th>
                                                    <th className="column-title min_68px">变动位置</th>
                                                    <th className="column-title">公司资金变动类型</th> 
                                                    <th className="column-title">统计类型</th>
                                                    <th className="column-title min_82px">录入人</th> 
                                                    <th className="column-title min_82px">审核人</th> 
                                                    <th className="column-title">审核时间</th>
                                                    <th className="column-title">备注</th>
                                                    <th className="column-title min_68px">操作</th>                  
                                                    </tr>
                                                </thead>
                                                <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        
                                                         <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{moment(item.transDate).format("YYYY-MM-DD")}</td>
                                                            <td>{item.fundsTypeName}</td>
                                                            <td>{item.summary}</td>
                                                            <td>{toThousands(item.income,true)}</td>
                                                            <td>{toThousands(item.expense,true)}</td>
                                                            <td>{item.changePositionName}</td>
                                                            <td>{item.companyChangeTypeName}</td>
                                                            <td>{item.accountingTypeName}</td>
                                                            <td>{item.operator}</td>
                                                            <td>{item.auditior}</td>
                                                            <td>{item.checkDate?moment(item.checkDate).format("YYYY-MM-DD"):''}</td>
                                                            <td>{item.comment}</td>
                                                            <td>
                                                            {limitBtn.indexOf('saveMars')>-1?<a href="javascript:void(0)" className='mar20' onClick={() => this.showDetail(item)}>修改</a>:''}
                                                            {limitBtn.indexOf('check')>-1?item.checkState=='1'?'':<a href="javascript:void(0)"  onClick={() => this.marsAudit(item.id)}>审核</a>:''}
                                                            </td>
                                                        </tr>
                                                    )
                                                }):
                                                <tr className="no-record"><td colSpan="14">暂无数据</td></tr>
                                            }
                                        </tbody>
                                            </table>
                                        </div>
                                        <div className="pagation-box">
                                {
                                    pageTotal>0 && <Pagination
                                                size="small"
                                                current={pageIndex}
                                                total={pageTotal}
                                                onChange={this.changPageNum}
                                                showTotal={total => `总共 ${total} 条`}
                                                onShowSizeChange={this.onShowSizeChange}
                                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                defaultPageSize={PAGESIZE}
                                                showSizeChanger
                                                showQuickJumper />
                                }
                                </div>
                                    </div>
                                </div>

                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width="700px"
                    onCancel={this.handleCancel}
                    footer={[
                        <Button key="back" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modifyRoleBtn(item)}>
                            保存修改
                        </Button>,
                    ]}
                    >
                    {modalHtml}            
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='MAR'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                />
            </div>
        )
        
    }
}