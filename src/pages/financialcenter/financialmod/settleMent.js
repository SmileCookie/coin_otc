import React from 'react'
import axios from '../../../utils/fetch';
import qs from 'qs'
import { Select,Modal,Button,Table,Pagination,message,DatePicker,Input } from 'antd'
import moment from 'moment'
import MemoInfo from './modal/memoInfo'
import AddMemo from './modal/addMemo'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import SelectAType from '../select/selectAType'
import GoogleCode from '../../common/modal/googleCode'
import SelectChoice from '../select/selectChoice'
import { toThousands,pageLimit,tableScroll } from '../../../utils'
import { stringify } from 'querystring';
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const { TextArea } = Input;
const Option = Select.Option;

export default class SettleMent extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            findsType:'0',
            accountId:'0',
            group:'0',
            beginTime:'',
            endTime:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],
            pageTotal:0,
            visible:false,
            modalHtml:'',
            memo:'',
            modifyTextArea:'',
            modifyId:'',
            title:'',
            width:'600px',
            time:[],
            type:'',
            limitBtn: [],
            check:'',
            googVisibal:false,
            item:{},
            googletype:'',
            height:0,
            tableScroll:{
                tableId:'STTLMT',
                x_panelId:'STTLMTX',
                defaultHeight:500,
            }
        }

        this.handleChangeType = this.handleChangeType.bind(this)
        this.handleChangeChoice = this.handleChangeChoice.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChange = this.onChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.addMemo = this.addMemo.bind(this)
        this.handleChangegroup = this.handleChangegroup.bind(this)
        this.onSettleSave = this.onSettleSave.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.viewMemo = this.viewMemo.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleChangeFind = this.handleChangeFind.bind(this)

        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('settlement',this.props.permissList)
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

    //查询 按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
    }

    //table 请求
    requestTable(currentIndex,currentSize){
        const {findsType, accountId,group,beginTime,endTime,pageIndex,pageSize,memo,type } = this.state
        axios.post(DOMAIN_VIP+"/settlement/query",qs.stringify({
            fundType:findsType,
            accountId:accountId,
            group:group,
            beginTime:beginTime,
            endTime:endTime,
            memo:memo,
            type,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code ==0 ){
                this.setState({
                    tableList:result.data.finanbalanceList,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    //弹窗隐藏
    handleCancel(){
        this.setState({ 
            visible: false
        });
    }
    //点击分页
    changPageNum(page,pageSize){
        this.requestTable(page,pageSize)
        this.setState({
            pageIndex:page,
            pageSize:pageSize
        })
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }
    //重置
    onResetState(){
        this.setState({
            findsType:'0',
            accountId:'0',
            group:'0',
            beginTime:'',
            endTime:'',
            memo:'',
            time:[],
            type:''
        })
    }

    //google 验证弹窗
    modalGoogleCode(item,type){
        this.setState({
            googVisibal:true,
            item,
            googletype:type,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const {item,googletype } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(googletype == 'view'){
                    this.viewMemo(item)
                }else{
                    this.onSettleSave(item)
                }
                this.setState({
                    googVisibal: false
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    //添加备注
    addMemo(item){
        const { modifyTextArea } = this.state
        this.footer=[
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => {if(this.state.modifyTextArea){
                this.modalGoogleCode(item.id)
            }else{
                message.warning("请输入添加的备注！")
            }}}>
                确认
            </Button>,
        ]
        this.setState({
            visible:true,
            title:'新增备注',
            width:'600px',
            modifyTextArea:'',
            modalHtml:<AddMemo handleInputChange={this.handleInputChange} />
        })
    }
    onSettleSave(id){
        let self = this;
        const { modifyTextArea } = this.state 
        if(modifyTextArea){
            axios.post(DOMAIN_VIP+'/settlement/insertMemo',qs.stringify({
                id:id,
                memo:modifyTextArea
            })).then((res) => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg);
                    this.setState({
                        visible:false,
                        googleCode:''
                    })
                    self.requestTable();
                }else{
                    message.warning(result.msg)
                }
            })
        }else{
            message.warning('请输入备注');
        }
    }

    //查看备注
    viewMemo(id){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={this.handleCancel}>
                确定
            </Button>,
        ]
        axios.post(DOMAIN_VIP+'/settlement/queryMemo',qs.stringify({
            id:id
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:true,
                    title:'结算备注',
                    width:'900px',
                    modalHtml:<MemoInfo  tableList={result.data} />
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //资金类型 select
    handleChangeType(val){
        this.setState({
            findsType:val
        })
    }
    //账户选择 select
    handleChangeChoice(val){
        this.setState({
            accountId: val
        })
    }
    //结算类型 select
    handleChangegroup(val){
        this.setState({
            group: val
        })
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

    //时间 select
    onChange(date, dateString){
        this.setState({
            beginTime:dateString[0],
            endTime:dateString[1],
            time:date
        })
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
    handleChangeFind(val){
        this.setState({
            type:val
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
        const { showHide,findsType,accountId,group,tableList,type,pageTotal,memo,visible,title,time,modifyTextArea,pageIndex,pageSize,modalHtml,width,limitBtn  } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 账务管理 > 充提结算查询
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <SelectAType col='3' findsType={findsType} handleChange={this.handleChangeType}></SelectAType>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <SelectChoice findsType={accountId} handleChange={this.handleChangeChoice}></SelectChoice>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                            <label className="col-sm-3 control-label">账户类型：</label>
                                            <div className="col-sm-8">
                                                <Select value={type} style={{ width: SELECTWIDTH }} onChange={this.handleChangeFind}>
                                                    <Option value="">请选择</Option>
                                                    <Option value="1">充值账户</Option>
                                                    <Option value="3">提现账户</Option>
                                                </Select>
                                            </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">结算类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={group} style={{ width: SELECTWIDTH }}  onChange={this.handleChangegroup}>
                                                <Option value="0">请选择</Option>
                                                <Option value="00">上班结算</Option>
                                                <Option value="24">下班结算</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">结算时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChange} value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">备注：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="memo" value={memo} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>                                        
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">资金类型</th>
                                                <th className="column-title">操作员名称</th>
                                                <th className="column-title">结算编号</th>
                                                <th className="column-title">账户名称</th>
                                                <th className="column-title">结算时间</th>
                                                <th className="column-title">结算余额</th>
                                                <th className="column-title">结算类型</th>
                                                <th className="column-title min_214px">备注</th> 
                                                <th className="column-title">操作</th>                           
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.fundTypeName}</td>
                                                            <td>{item.createname}</td>
                                                            <td>{item.id}</td>
                                                            <td>{item.finanaccount?item.finanaccount.name:''}</td>
                                                            <td>{moment(item.createtime).format(TIMEFORMAT)}</td>
                                                            <td>{toThousands(item.amount)}</td>
                                                            <td>{item.group=="00"?"上班结算":"下班结算"}</td>
                                                            <td><span className="flexp">{item.memo}</span></td>
                                                            <td>
                                                                {limitBtn.indexOf('insertMemo')>-1?<a className="mar10" href="javascript:void(0)" onClick={() => this.addMemo(item)}>添加备注</a>:''}
                                                                {limitBtn.indexOf('queryMemo')>-1?<a href="javascript:void(0)" onClick={()=>this.modalGoogleCode(item.id,'view')}>查看备注</a>:''}
                                                            </td>
                                                        </tr>
                                                    )
                                                }):<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                <div className="pagation-box">
                                    {pageTotal>0&&
                                        <Pagination 
                                            size="small" 
                                            current={pageIndex}
                                            total={pageTotal}  
                                            showTotal={total => `总共 ${total} 条`}
                                            onChange={this.changPageNum}
                                            onShowSizeChange={this.onShowSizeChange}
                                            showSizeChanger 
                                            showQuickJumper
                                            pageSizeOptions={PAGRSIZE_OPTIONS20}
                                            defaultPageSize={PAGESIZE}
                                                 />
                                    }
                                    </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                      { modalHtml }  
                </Modal>   
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='SM'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                />   
            </div>
        )
    }
}







































