import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import JiFenTypeList from '../../common/select/jiFenTypeList'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { DatePicker,Button,Pagination,Modal,Input,message } from 'antd'
import { pageLimit,tableScroll } from '../../../utils'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const { TextArea } = Input;

export default class IntegralBill extends React.Component{

    constructor(props) {
        super(props)
        this.state = {
            showHide:true,
            userid:'',
            username:'',
            type:'',
            jifenS:'',
            jifenE:'',
            timeS:'',
            timeE:'',
            tableList:[],
            pageTotal:0,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            modalNum:'',
            modalusername:'',
            modalMemo:'',
            visible:false,
            time:[],
            modalHtml:'',
            limitBtn: [],
            checkGoogle:'',
            googVisibal:false,
            googleSpace:'',
            googleType:'',
            height:0,
            tableScroll:{
                tableId:'ITGABL',
                x_panelId:'ITGABLX',
                defaultHeight:500,
            }
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.addIntergralBill = this.addIntergralBill.bind(this)
        this.handleOk = this.handleOk.bind(this)
        this.showModal = this.showModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.addBillBtn = this.addBillBtn.bind(this)
        this.handleTypeChange = this.handleTypeChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('integralBill', this.props.permissList)
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
    //弹窗 ok 
    handleOk(){
        this.setState({ loading: true });
        setTimeout(() => {
          this.setState({ 
              loading: false, 
              visible: false 
          });
        }, 3000);
    }
    //弹窗显示
    showModal(){
        this.setState({
          visible: true,
        });
    }
    //弹窗隐藏
    handleCancel(){
        console.log("handleCancel")
        this.setState({ 
            visible: false,
            vipRate:'',
            jifen:'',
            discount:'',
            memo:'',
            modalNum:'',
            modalusername:'',
            modalMemo:'',
            modalHtml:''
        });
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

    //输入时 input 设置到 satte
    handleInputChange(event,check){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    //时间改变
    onChangeTime(date, dateString) {
        console.log(date, dateString);
        this.setState({
            timeS:dateString[0],
            timeE:dateString[1],
            time:date
        })
    }
    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
    }

    requestTable(currIndex,currSize){
        const { userid,username,type,jifenS,jifenE,timeS,timeE,pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+'/integralBill/queryList',qs.stringify({
            userId:userid,
            username,
            type,
            jifenS,
            jifenE,
            timeS,
            timeE,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            console.log(result)
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
    onResetState(){
        this.setState({
            userid:'',
            username:'',
            type:'',
            jifenS:'',
            jifenE:'',
            timeS:'',
            timeE:'',
            time:[]
        })
        
    }
    //积分类型 Select
    handleTypeChange(val){
        this.setState({
            type:val
        })
    }
  
    //google 验证弹窗
    modalGoogleCode(url,type,check){
       
        this.setState({
            googVisibal:true,
            googleSpace:url,
            googleType:type,
            check
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { googleSpace,googleType ,check} = this.state
        const {googleCode,checkGoogle } = value
        let aurl =  check ?"/common/checkTwoGoogleCode":"/common/checkGoogleCode"
        axios.post(DOMAIN_VIP+aurl,qs.stringify({
            googleCode,checkGoogle
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({googVisibal:false})
                this.addBillBtn(googleSpace,googleType)
            }else{
                message.warning(result.msg)
            }
        })
    }

    //添加积分扣除积分
    addIntergralBill(type,check){
        let url = type == 'add'?'/integralBill/add':'/integralBill/deduct';
        let title = type == 'add'?'添加积分':'扣除积分'; 
        let saveType = type == 'add'? 0:1;      
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(url,saveType,check)}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            title:title,
            username:'',
            jifen:'',
            memo:''
        })
    }
    //添加扣除 按钮
    addBillBtn(url,saveType){
        const { modalNum,modalusername,modalMemo } = this.state
        if(!modalusername){
            message.warning('用户名不能为空！')
            return false
        }
        if(!modalNum){
            message.warning('数量不能为空！')
            return false
        }
        axios.post(DOMAIN_VIP+url,qs.stringify({
            username:modalusername,
            jifen:modalNum,
            memo:modalMemo,
            saveType
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    modalNum:'',
                    modalusername:'',
                    modalMemo:'',
                    modalHtml:''
                })
                this.requestTable();
            }else{
                message.warning(result.msg)
            }
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
        const { showHide,tableList,pageTotal,pageIndex,pageSize,userid,username,type,jifenS,jifenE ,modalMemo,modalusername,modalNum,visible,title,time,modalHtml,limitBtn } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > VIP管理 > 积分流水
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="userid" value={userid} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="username" value={username} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                   <JiFenTypeList jifenType={type} col='3' handleChange={this.handleTypeChange} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">积分时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChangeTime} value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">积分区间：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control"  name="jifenS" value={jifenS} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control"  name="jifenE" value={jifenE} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-12 col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" className="mar4" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" className="mar4" onClick={this.onResetState}>重置</Button> 
                                        {limitBtn.indexOf('add')>-1?<Button type="primary" className="mar4" onClick={() => this.addIntergralBill('add','check')}>添加积分</Button>:''}  
                                        {limitBtn.indexOf('deduct')>-1?<Button type="primary" className="mar4" onClick={() => this.addIntergralBill('deduct','check')}>扣除积分</Button>:''}                                             
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
                                                <th className="column-title">用户编号</th>
                                                <th className="column-title">积分时间</th>
                                                <th className="column-title">积分类型</th>
                                                <th className="column-title">积分</th>
                                                <th className="column-title">描述</th>                       
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return(
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.userid}</td>
                                                            <td>{moment(item.addtime).format(TIMEFORMAT)}</td>
                                                            <td>{item.typeShowNew}</td>
                                                            <td>{item.jifen}</td>
                                                            <td>{item.memo}</td>
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                        
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
                <Modal
                    visible={visible}
                    title={title}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {modalHtml?modalHtml:<div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">用户名：<i>*</i></label>
                            <div className="col-sm-9">
                                <input type="text" className="form-control" name="modalusername" value={modalusername} onChange={this.handleInputChange} />
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-sm-3 control-label">数量：<i>*</i></label>
                            <div className="col-sm-9">
                                <input type="text" className="form-control" name="modalNum" value={modalNum} onChange={this.handleInputChange} />
                            </div>
                        </div> 
                        <div className="form-group">
                            <label className="col-sm-3 control-label">备注：</label>
                            <div className="col-sm-9 text-box">
                                <TextArea  name="modalMemo" value={modalMemo} onChange={this.handleInputChange} />
                            </div>
                        </div>         
                    
                    </div>}
                </Modal>        
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='IB'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                />     
            </div>
        )
    }

}






























































