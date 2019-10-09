import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import 'moment/locale/zh-cn';
import { DOMAIN_VIP,TIMEFORMAT,SELECTWIDTH } from '../../../../conf/index';
import { Select,Modal,Button,Pagination,Input,DatePicker } from 'antd'
import FundsTypeList from '../../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Option = Select.Option;
moment.locale('zh-cn');
export default class ModalsaveMars extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            id:'0',
            tranDate:moment().format("YYYY-MM-DD"),
            summary:'',
            accountType:[<Option key='0' value=''>请选择</Option>],
            tableList:'',
            income:'',
            expense:'',
            fundsType:'0',
            changePosition:'',
            companyChangeType:'',
            comment:'',
        }
        this.handleChangeType =this.handleChangeType.bind(this)
        this.positionChange = this.positionChange.bind(this)
        this.accountType = this.accountType.bind(this)
        this.companyChange =this.companyChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
    }
    componentDidMount(){
        this.setState({
            id:this.props.item.id?this.props.item.id:'0',
            tranDate:this.props.item.transDate?moment( this.props.item.transDate).format("YYYY-MM-DD"):moment().format("YYYY-MM-DD"),
            summary:this.props.item.summary?this.props.item.summary:'',
            income:this.props.item.income?this.props.item.income:'0',
            expense:this.props.item.expense?this.props.item.expense:'0',
            fundsType:this.props.item.fundsType?String(this.props.item.fundsType):'0',
            changePosition:this.props.item.changePosition?this.props.item.changePosition:'',
            companyChangeType:this.props.item.companyChangeType?this.props.item.companyChangeType:'',
            accountingType:this.props.item.accountingType?this.props.item.accountingType:'',
            comment:this.props.item.comment?this.props.item.comment:'',
            accountType:this.props.accountType,
        },()=> {
            console.log(typeof(this.state.fundsType))})
    }
    handleChangeType(value) {
        this.setState({
            fundsType:value
        })
        this.props.ModalhandleChangeType(value)
    }
    positionChange(value){
        this.setState({
            changePosition:value
        }) 
        this.props.ModalpositionChange(value)
    }
    accountType(value){
        this.setState({
            accountingType:value
        })
        this.props.ModalaccountType(value)
    }
    companyChange(value){
        this.setState({
            companyChangeType:value
        })
        this.props.ModalcompanyChange(value)
    }
    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }
    componentWillReceiveProps(nextProps){
            this.setState({
                id:nextProps.item.id?nextProps.item.id:'0',
                tranDate:moment(nextProps.item.transDate).format("YYYY-MM-DD"),
                summary:nextProps.item.summary?nextProps.item.summary:'',
                income:nextProps.item.income?nextProps.item.income:'0',
                expense:nextProps.item.expense?nextProps.item.expense:'0',
                fundsType:nextProps.item.fundsType?String(nextProps.item.fundsType):'',
                changePosition:nextProps.item.changePosition?nextProps.item.changePosition:'',
                companyChangeType:nextProps.item.companyChangeType?nextProps.item.companyChangeType:'',
                accountingType:nextProps.item.accountingType?nextProps.item.accountingType:'',
                comment:nextProps.item.comment?nextProps.item.comment:'',
            })
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            // beginTime:dateString[0]+" 00:00:00",
            // endTime:dateString[1]+" 23:59:59",
            tranDate:dateString,
            time:date
        })
        this.props.ModalchengeTime(dateString)
    }
    render(){
        const {id,accountType,tranDate,summary,income,expense,fundsType,changePosition,companyChangeType, accountingType,comment} =this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">交易日期：<i>*</i></label>
                        <div className="col-sm-5">
                         <DatePicker
                            format="YYYY-MM-DD"
                            placeholder="Select Time"
                            onChange={this.onChangeCheckTime}
                            value={moment(tranDate,"YYYY-MM-DD")}
                        />
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">摘要：<i>*</i></label>
                        <div className="col-sm-6">
                            <input type="text" className="form-control" name="summary" value={summary} onChange={this.handleInputChange}/>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <FundsTypeList fundsType={fundsType} handleChange={this.handleChangeType} />
                    {/* <div className="form-group">
                        <label className="col-sm-5 control-label">币种类型：<i>*</i></label>
                        <div className="col-sm-6">
                            <Select value={fundsType}  style={{ width: SELECTWIDTH }} onChange={this.handleChangeType} >
                                {accountType} 
                            </Select>
                        </div>
                    </div>                */}
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">收入：<i>*</i></label>
                        <div className="col-sm-6">
                            <input type="text" className="form-control" name="income" value={income} onChange={this.handleInputChange}/>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">支出：<i>*</i></label>
                        <div className="col-sm-6">
                            <input type="text" className="form-control" name="expense" value={expense} onChange={this.handleInputChange}/>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">变动位置：<i>*</i></label>
                        <div className="col-sm-6">
                            <Select value={changePosition}  style={{ width: SELECTWIDTH }} onChange={this.positionChange} >
                                <Option value=''>请选择</Option> 
                                <Option value='1'>平台</Option>
                                <Option value='2'>钱包</Option>
                            </Select>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">公司资金变动类型：<i>*</i></label>
                        <div className="col-sm-6">
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
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">统计类别：<i>*</i></label>
                        <div className="col-sm-6">
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
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">备注：</label>
                        <div className="col-sm-5">
                            <input type="text" className="form-control" name="comment" value={comment} onChange={this.handleInputChange}/>
                        </div>
                    </div>               
                </div>
            </div>
        )
    }
}