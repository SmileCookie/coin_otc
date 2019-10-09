import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalRechargeAddress from './modal/modalRechargeAddress'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,SELECTWIDTH,URLS } from '../../../conf'
import FundsTypeList from '../../common/select/fundsTypeList'
import { Input,Modal,DatePicker,Select,Button,Pagination } from 'antd'
const { COMMON_QUERYATTRUSDTE } = URLS
const { RangePicker } = DatePicker;
const Option = Select.Option;

export default class RechargeAddress extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            fundsType:'2',
            address:'',
            userId:'',
            userName:'',
            type:'0',
            time:[],
            tableScroll:{
                tableId:'RCRARSS',
                x_panelId:'RCRARSSX',
                defaultHeight:500,
            }
        }
        this.handleChangeType = this.handleChangeType.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.onChangeConfig = this.onChangeConfig.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleChangegroup = this.handleChangegroup.bind(this)
        this.queryClickBtn = this.queryClickBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
    }
    componentDidMount(){
        var height  =document.querySelector(`#${this.state.tableScroll.x_panelId}`).offsetHeight
        this.setState({
            xheight:height
        })
    }
    //资金类型 select
    handleChangeType(val){
        this.setState({
            fundsType:val
        })
    }
    //
    handleChangegroup(val){
        this.setState({
            type:val
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
    //充值开始时间
    onChangeTime(date, dateString){
        this.setState({
            beginTime:dateString[0],
            endTime:dateString[1],
        })
    }
    //确认时间
    onChangeConfig(date, dateString){
        this.setState({
            configStartTime:dateString[0],
            configEndTime:dateString[1]
        })
    }
      
    //重置状态
    onResetState(){
        this.setState({
            fundsType:'2',
            address:'',
            userId:'',
            userName:'',
            type:'0',
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
                showHide: !showHide,
            })
    }

    render(){
        const { showHide,fundsType,address,userId,userName,type,pageIndex,pageSize,tableList,pageTotal} = this.state 
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 充提管理 > 充值地址
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
                                            <Select value={type} style={{ width: SELECTWIDTH }}  onChange={this.handleChangegroup}>
                                                <Option value="0">请选择</Option>
                                                <Option value="1">已充值</Option>
                                                <Option value="2">未充值</Option>
                                                <Option value="3">未分配</Option>                                                
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">充值地址：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="address" value={address} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={() => this.queryClickBtn(true)}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>                                        
                                    </div>
                                </div>
                            </div>
                        </div>}

                        <ModalRechargeAddress {...this.state} queryClickBtn={this.queryClickBtn} />

                    </div>
                </div>
            </div>
        )
    }
}





















































