import React from 'react'
import ReactDOM from 'react-dom'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Select,Modal,Button,Table,Pagination,message,DatePicker } from 'antd'
import ModalCloseSecondVerify from './modal/modalCloseSecondVerify'
import GoogleCode from '../../common/modal/googleCode'
import { tableScroll } from '../../../utils'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Option = Select.Option;



export default class CloseSecondVerify extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            tableList:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            userid:'',
            time:[],
            startTime:'',
            endTime:'',
            status:0,
            visible:false,
            title:'',
            modalHtml:'',
            restatus:1,
            googleCode:'',
            googleSpace:'',
            height:0,
            tableScroll:{
                tableId:'csSDVY',
                x_panelId:'csSDVYX',
                defaultHeight:500,
            }

        }

        this.requestTable = this.requestTable.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleChangeStatus = this.handleChangeStatus.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.onVerifyModal = this.onVerifyModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onVerifyBtn = this.onVerifyBtn.bind(this)
        this.onChangeRadio = this.onChangeRadio.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.handleGoogleCancel = this.handleGoogleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
        
    }

    componentDidMount(){
        this.requestTable()
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
        },()=>this.requestTable())
    }
    //输入时 input 设置到 state
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //请求列表
    requestTable(currentIndex,currentSize){
        const { userid,pageIndex,pageSize,startTime,endTime,status } = this.state
        axios.post(DOMAIN_VIP+"/doubleCheck/query",qs.stringify({
            status,userid,
            createtimeS:startTime,
            createtimeE:endTime,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
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
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page,
            pageSize:pageSize
        },()=>this.requestTable(page,pageSize))
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.setState({
            pageIndex:current,
            pageSize:size
        },()=>this.requestTable(current,size))
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
    //关闭弹框
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //重置状态
    onResetState(){
        this.setState({
            userid:'',
            startTime:'',
            endTime:'',
            time:[]
        })
    }
    //切换按钮
    onChangeRadio(e){
        this.setState({
            restatus:e
        })
    }
    //时间控件
     onChangeCheckTime(date, dateString) {
        this.setState({
            startTime:dateString[0]+" 00:00:00",
            endTime:dateString[1]+" 23:59:59",
            time:date
        })
       
    }
    //切换状态
    handleChangeStatus(val){
        this.setState({
            status:val
        })
    }
    //审核弹框
    onVerifyModal(item){
        this.footer=[
            <Button key='back' onClick={this.handleCancel}>取消</Button>,
            <Button key='submit' type='more' onClick={item.status==0?()=>this.modalGoogleCode(item):this.handleCancel}>保存修改</Button>
        ]
        this.setState({
            visible:true,
            title:'二次审核--实名认证',
            width:'700px',
            modalHtml:<ModalCloseSecondVerify item={item} onChangeRadio={this.onChangeRadio} />
        })
    }
    //确认审核按钮
    onVerifyBtn(item){
        const { restatus } = this.state
        axios.post(DOMAIN_VIP+"/doubleCheck/pass",qs.stringify({
          id:item.id,status:restatus
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false
                })
                this.requestTable()
            }else{
                message.warning(result.msg)
            }
        })
    }
    //google弹窗
    modalGoogleCode(item){
        this.setState({
            googVisibal:true,
            googleSpace:item,
        })
    }
    //google 按钮
    modalGoogleCodeBtn(values){
        const {googleSpace} = this.state
        const {googleCode} = values
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                },()=>this.onVerifyBtn(googleSpace))
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err,values) => {
            if(err) return;
            form.resetFields();
            this.modalGoogleCodeBtn(values)
        })
    }
    saveFormRef(formRef){
        this.formRef = formRef
    }
    handleGoogleCancel(){
        this.setState({
            googVisibal:false
        })
    }
    render(){
        const { showHide,tableList,userid,pageIndex,pageSize,pageTotal,time,status,width,visible,title } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 审核管理 > 二次审核--实名认证
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                                <div className="x_content">
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">状态：</label>
                                            <div className="col-sm-8">
                                                <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.handleChangeStatus}>
                                                    <Option value={0}>待审核</Option>                                                    
                                                    <Option value={1}>通过</Option>    
                                                    <Option value={2}>拒绝</Option>                                                
                                                </Select>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">用户ID：</label>
                                            <div className="col-sm-8">
                                                <input type="text" className="form-control"  name="userid" value={userid} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">时间：</label>
                                            <div className="col-sm-8">
                                            <RangePicker 
                                                showTime={{
                                                    defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format="YYYY-MM-DD HH:mm:ss"
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime }
                                                value={time}
                                            />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-4 col-sm-4 col-xs-4 right">
                                        <div className="right">
                                            <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                            <Button type="primary" onClick={this.onResetState}>重置</Button>                                            
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
                                                <th className="column-title">用户ID</th>
                                                <th className="column-title">用户名</th>
                                                <th className="column-title">类型</th>
                                                <th className="column-title">审核状态</th>
                                                <th className="column-title">创建时间</th> 
                                                <th className="column-title">审核时间</th> 
                                                <th className="column-title">审核人</th> 
                                                <th className="column-title">操作</th>                                                                                              
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.userid}</td>
                                                            <td>{item.userName}</td>
                                                            <td>{item.type == 1 ? '关闭二次验证':''}</td>
                                                            <td>{item.statusName}</td>
                                                            <td>{moment(item.createtime).format(TIMEFORMAT)}</td>  
                                                            <td>{item.verifytime?moment(item.verifytime).format(TIMEFORMAT):'--'}</td>
                                                            <td>{item.checkUser}</td>
                                                            <td>
                                                                {item.status==0?
                                                                
                                                                <a href="javascript:void(0)" onClick={()=>this.onVerifyModal(item)}>审核</a>
                                                                :
                                                                <a href="javascript:void(0)" onClick={()=>this.onVerifyModal(item)}>查看</a>}
                                                            </td>                                                          
                                                        </tr>
                                                    )    
                                                })  
                                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
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
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width={width}
                >
                    {this.state.modalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange = {this.handleInputChange}
                    mid='CSVY'
                    visible={this.state.googVisibal}
                    onCancel={this.handleGoogleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )
    }
}
























