import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT,DAYFORMAT } from '../../../conf'
import ModalBillDetail from './modal/modalBillDetailWallet'
import SelectAType from '../select/selectAType'
import SelectBType from '../select/selectBType'
import moment from 'moment'
import { DatePicker,Select,Modal, Button ,Tabs,Pagination} from 'antd'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class BillDetailWallet extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            fundsType:'0',
            type:'0',
            id:'',
            startDate:'',
            endDate:'',
            userName:'',
            userId:'',
            time:[],
            tableScroll:{
                tableId:'BILDETLWLT',
                x_panelId:'BILDETLWLTX',
                defaultHeight:500,
            }
        }
        
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.handleChangeBills = this.handleChangeBills.bind(this)
        this.handleAccountChange = this.handleAccountChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleFundChange = this.handleFundChange.bind(this)
        this.queryClickBtn = this.queryClickBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
    }

    componentDidMount(){
        let time = moment().format(DAYFORMAT)
        var height  =document.querySelector(`#${this.state.tableScroll.x_panelId}`).offsetHeight
        this.setState({
            startDate:time+' 00:00:00',
            endDate:time+' 23:59:59',
            time:[moment(time+'00:00:00', TIMEFORMAT), moment(time+'23:59:59', TIMEFORMAT)],
            xheight:height
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
    //资金类型
    handleFundChange(val){
        this.setState({
            fundsType:val
        })
    }
    //时间空间
    onChangeTime(date, dateString) {
        this.setState({
            startDate:dateString[0],
            endDate:dateString[1],
            time:date
        })
    }

    //账单类型 Select
    handleChangeBills(val){
        this.setState({
            type:val
        })
    }
    //资金类型 Select
    handleAccountChange(val){
        this.setState({
            fundsType:val
        })
    }
   
    //重置按钮
    onResetState(){
        this.setState({
            fundsType:'0',
            type:'0',
            id:'',
            userId:'',
            startDate:'',
            endDate:'',
            userName:'',
            time:[]
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
        const { showHide,fundsType,type,id,startDate,endDate,userName,userId,time,pageIndex,pageSize,tableList,pageTotal } = this.state;
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 账单流水明细 >钱包流水明细
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <SelectAType findsType={fundsType} col='3' handleChange={this.handleFundChange}></SelectAType>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-2 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-2 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-2 control-label">流水编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="id" value={id} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <SelectBType billType={type} col='3' handleChange={this.handleChangeBills}></SelectBType>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">记账时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChangeTime} value={time} format={TIMEFORMAT} showTime={{
                                            defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')],hideDisabledOptions: true
                                          }}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right marTop">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.queryClickBtn(true)}>查询</Button> 
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>  
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <ModalBillDetail {...this.state} queryClickBtn={this.queryClickBtn} />
                    </div>
                </div>
            </div>
        )
    }
}
