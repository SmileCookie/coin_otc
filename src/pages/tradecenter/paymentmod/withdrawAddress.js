import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalWithAddress from './modal/modalWithdrawAddress'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,SELECTWIDTH,URLS } from '../../../conf'
import FundsTypeList from '../../common/select/fundsTypeList'
import { Input,Modal,DatePicker,Select,Button,Pagination } from 'antd'
import {tableScroll} from '../../../utils'
const { COMMON_QUERYATTRUSDTE } = URLS
const { RangePicker } = DatePicker;
const Option = Select.Option;

export default class WithdrawAddress extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            fundsType:'2',
            address:'',
            userId:'',
            userName:'',
            isDelete:'',
            auth:'',
            isreLoad:false,
            tableScroll:{
                tableId:'WIHDWAESS',
                x_panelId:'WIHDWAESSX',
                defaultHeight:500,
            }
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleCommandid = this.handleCommandid.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.onChangeSubTime = this.onChangeSubTime.bind(this)
        this.handleOperation = this.handleOperation.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.queryClickBtn = this.queryClickBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
    }
    componentDidMount(){
        var height  =document.querySelector(`#${this.state.tableScroll.x_panelId}`).offsetHeight
        this.setState({
            xheight:height
        })
    }
    //select 资金类型
    handleChangeType(val){
        this.setState({
            fundsType:val
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

    //地址状态
    handleCommandid(value) {
        this.setState({
            isDelete:value
        })
    }
    //是否已认证
    handleOperation(val){
        this.setState({
            auth:val
        })
    }

    //确认时间
    onChangeTime(date, dateString) {
        this.setState({
            confirmStartDate:dateString[0],
            confirmEndDate:dateString[1]
        })
    } 
    //提交时间
    onChangeSubTime(date, dateString) {
        this.setState({
            startTime:dateString[0],
            endTime:dateString[1]
        })
    } 
    //重置状态
    onResetState(){
        this.setState({
            fundsType:'2',
            address:'',
            userId:'',
            userName:'',
            isDelete:'',
            auth:'',
        })
    }

    //查询按钮
    queryClickBtn(val){
        this.setState({
            isreLoad:val
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }

    render(){
        const { showHide,fundsType,address,userId,userName,isDelete,auth,pageIndex,pageSize,pageTotal,modalHtml,tableList } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 充提管理 > 提现地址
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList url={COMMON_QUERYATTRUSDTE} col='3' paymod="1" fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">地址状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={isDelete} style={{ width: SELECTWIDTH }} onChange={this.handleCommandid}>
                                                <Option value="">请选择</Option>
                                                <Option value="0">正常</Option>
                                                <Option value="1">已删除</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">是否已认证：</label>
                                        <div className="col-sm-8">
                                            <Select value={auth} style={{ width: SELECTWIDTH }} onChange={this.handleOperation}>
                                                {/* <Option value="">全部认证</Option> */}
                                                <Option value="">已认证</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">提现地址：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="address" value={address} onChange={this.handleInputChange} /> 
                                            <b className="icon-fuzzy">%</b> 
                                        </div>
                                    </div>
                                </div>
                                
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={() => this.queryClickBtn(true)}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>                                        
                                    </div>
                                </div>
                            </div>
                        </div>}

                       <ModalWithAddress {...this.state} queryClickBtn={this.queryClickBtn} />

                    </div>
                </div>
            </div>
        )
    }
}









































