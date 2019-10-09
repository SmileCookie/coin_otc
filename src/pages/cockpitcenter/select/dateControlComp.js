import React from 'react'
import { Popover, Button, Radio, DatePicker,Select } from 'antd'
import moment from 'moment'
import {TIMEFORMAT_ss, ALL_DATE, DAYFORMAT, MONTHFORMAT, DOMAIN_VIP} from '../../../conf'
import { dataURItoBlob,getDate,judgeDate } from '../../../utils';
import axios from "../../../utils/fetch";
import cookie from 'js-cookie';
const { YESTERDAY, TODAY, LAST_WEEK, THIS_WEEK, LAST_MONTH, THIS_MONTH, LAST_YEAR, THIS_YEAR, LAST_SEVEN_DAYS, LAST_THIRTH_DAYS, GOING_ONLINE } = ALL_DATE
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const Option = Select.Option
const { RangePicker, MonthPicker } = DatePicker

const RADIO_TIME = 'radioTime', NULL_TIME = 'nullTime', CHECK_TIME = 'checkTime'

const _plus999 = (date) => {
    console.log(date)
    return date * 1 + 999 + ''
}

const MonthContent = ({ onChangeMonthTime, monthTime }) =>
    <div className="col-mg-9 col-lg-9 col-md-9 col-sm-9 col-xs-9">
        <MonthPicker
            defaultValue={moment('2015-01', MONTHFORMAT)}
            format={MONTHFORMAT}
            onChange={(date, dateString) => onChangeMonthTime(date, dateString)}
            value={monthTime} />
    </div>

const Content = ({ dateCompHide, onChangeCheckTime, time, onChangeRadio, radioValue, monthTime, onChangeMonthTime, isShowMonth }) =>

    <div className="right-con" >
        <div className="clearfix"></div>
        <div className="row">
        
            <div className="x_panel " style={{ width: '500px', height: '300px' }}>
                <div className="x_content" style={{ position: 'relative' }}>
                    {!isShowMonth && <div className="col-mg-3 col-lg-3 col-md-3 col-sm-3 col-xs-3">
                        <div className="form-group">
                            <RadioGroup onChange={onChangeRadio} defaultValue={THIS_YEAR} value={radioValue} size="small">
                                <RadioButton value={YESTERDAY}>昨日</RadioButton>&nbsp;
                                <RadioButton value={TODAY}>今日</RadioButton>
                                <RadioButton value={LAST_WEEK}>上周</RadioButton>&nbsp;
                                <RadioButton value={THIS_WEEK}>本周</RadioButton>
                                <RadioButton value={LAST_MONTH}>上月</RadioButton>&nbsp;
                                <RadioButton value={THIS_MONTH}>本月</RadioButton>
                                <RadioButton value={LAST_YEAR}>去年</RadioButton>&nbsp;
                                <RadioButton value={THIS_YEAR}>本年</RadioButton>
                                <RadioButton value={LAST_SEVEN_DAYS}>过去7天</RadioButton>&nbsp;
                                <RadioButton value={LAST_THIRTH_DAYS}>过去30天</RadioButton>
                                <RadioButton value={GOING_ONLINE}>上线至今</RadioButton>&nbsp;
                            </RadioGroup>
                        </div>
                    </div>}
                    {!isShowMonth && <div className="col-mg-8 col-lg-8 col-md-9 col-sm-9 col-xs-9">
                        <RangePicker
                            defaultValue = {[moment('2015/01/01', DAYFORMAT), moment('2015/01/01', DAYFORMAT)]}
                            format={DAYFORMAT}
                            onChange={(date, dateString) => onChangeCheckTime(date, dateString)}
                            value={time} />
                    </div>}
                    {isShowMonth && <MonthContent monthTime={monthTime} onChangeMonthTime={onChangeMonthTime} />}
                </div>
                <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12" style={{ position: 'absolute', bottom: '0', right: '0' }}>
                    <div className="form-group right ">
                        <Button type="primary" onClick={() => dateCompHide(true)}>确认</Button>
                        <Button type="default" onClick={() => dateCompHide(false)} >取消</Button>
                    </div>
                </div>
            </div>
        </div>
    </div>


export default class DateControlComp extends React.Component {
    constructor(props) {
        super(props)
        // if(cookie.get(pTitle)!=""){
        //     var aa=JSON.parse(cookie.get(pTitle))
        //     date.endTime=aa[0]
        //     date.startTime=aa[1]
        // }
        console.log(cookie.get(props.pTitle))
        var ss=""
        if(cookie.get(props.pTitle)==""||JSON.parse(cookie.get(props.pTitle))[2]!=null){
            var sser=cookie.get(props.pTitle)!=""?JSON.parse(cookie.get(props.pTitle))[2].state:""
             ss=cookie.get(props.pTitle)==""?LAST_SEVEN_DAYS:sser=="昨天"?YESTERDAY:sser=="今天"?TODAY:sser=="上周"?LAST_WEEK:sser=="本周"?THIS_WEEK:sser=="上个月"?LAST_MONTH:sser=="本月"?THIS_MONTH:sser=="去年"?LAST_YEAR:sser=="本年"?THIS_YEAR:sser=="7天"?LAST_SEVEN_DAYS:sser=="30天"?LAST_THIRTH_DAYS:GOING_ONLINE;
        }
      
        this.defaultRadio = {
            radioValue:ss,
        }
        this.emptyRadio = {
            radioValue: null
        }
        this.defaultCTime = {
            time: [],
            startTime: '',
            endTime: '',
        }
        this.emptyMonth = {
            monthTime: null
        }
        this.state = {
            dateVisible: false,
            ...this.defaultCTime,
            ...this.defaultRadio,
            ...this.emptyMonth,
            isShowMonth: false,
            isDefine: false,
            toPropsDate: null,
            title:'',
            pTitle:'',
            timeType:1,
            scopeType: '',
            fundsType: '2',
            fundsTypeList: [],
            marketsList:[<Option key='' value=''>全站</Option>],
            defaultList: {}
        }
    }
    
    componentDidMount() {
        const { title,pTitle,marketsList,fundsTypeList,data} = this.props;
        this.setDefaultDate({ title,pTitle,marketsList,fundsTypeList });
        // this.props.onChangeSelectTime(date, {isDefine : true, pTitle: pTitle})
    }
    componentWillReceiveProps(nextProps) {
        let  { title,pTitle,marketsList,fundsTypeList, } = nextProps;
        // this.setState({
        //     timeType: 1,
        //     radioValue: LAST_SEVEN_DAYS,
        // });
        if(this.state.pTitle != pTitle) {
            this.setDefaultDate({ title,pTitle,marketsList,fundsTypeList })
        }
    }
    dateCompHide = (isDefine) => {
        let {radioValue, time, monthTime, defaultList,startTime,endTime} = this.state;
        let ssz=""
        if(isDefine) {
            defaultList[this.state.pTitle].radioValue = radioValue;
            defaultList[this.state.pTitle].time = time;
            defaultList[this.state.pTitle].monthTime = monthTime;
           
            this.props.onChangeSelectTime(this.state.toPropsDate, {isDefine,pTitle:this.state.pTitle},radioValue);
            defaultList[this.state.pTitle].startTime = startTime;
            defaultList[this.state.pTitle].endTime = endTime;
            // this.props.onChangeSelectTime(this.state.toPropsDate, {isDefine,pTitle:this.state.pTitle});
        } else {
            radioValue = defaultList[this.state.pTitle].radioValue;
            time = defaultList[this.state.pTitle].time;
            monthTime = defaultList[this.state.pTitle].monthTime;
            startTime = defaultList[this.state.pTitle].startTime;
            endTime = defaultList[this.state.pTitle].endTime;
        }
        this.setState({ dateVisible: false, isDefine,defaultList,radioValue,time, monthTime,startTime,endTime   })
    }
    handleVisibleChange = (dateVisible) => {
        // this.setDefaultDate()
        let {radioValue, time, monthTime, defaultList,startTime,endTime} = this.state;
        if(dateVisible){           
            this.setState({ dateVisible });
        }else{
            radioValue = defaultList[this.state.pTitle].radioValue;
            time = defaultList[this.state.pTitle].time;
            monthTime = defaultList[this.state.pTitle].monthTime;
            startTime = defaultList[this.state.pTitle].startTime;
            endTime = defaultList[this.state.pTitle].endTime;
            this.setState({dateVisible,defaultList,radioValue,time, monthTime,startTime,endTime})
        }
    }
    onChangeRadio = (e) => {
        let value = e.target.value
        this.setState({ radioValue: value })
        this.onChangeTime(getDate(value), RADIO_TIME)
    }
    //日期时间控件
    onChangeCheckTime = (date, dateString) => {
        let startTime, endTime, payload = CHECK_TIME;
        startTime = dateString[0] && moment(dateString[0] +" 00:00:00").format('x');
        endTime = dateString[1] && _plus999(moment(dateString[1] + " 23:59:59").format('x'));
        
        if (!startTime && !endTime) {
            payload = NULL_TIME;
        } else {
            // 由于开始结束时间是字符串moment解析不出来特此转化
            startTime = parseInt(startTime);
            endTime = parseInt(endTime);
        }       
        this.setState({
            startTime,
            endTime,
            time: date
        })
        this.onChangeTime({
            startTime: startTime,
            endTime: endTime,
        }, payload)
    }
    //月份时间
    onChangeMonthTime = (date, dateString) => {
        if (!date) {
            this.setMonthDefault()
            return
        }
        let dateArr = dateString ? dateString.split('-') : ['', '']
        let startTime = '', endTime = '';
        startTime = moment([dateArr[0], dateArr[1] - 1]).valueOf()
        endTime = moment(startTime).endOf('month').valueOf()
        this.setState({
            monthTime: date
        })
        this.onChangeTime({ startTime, endTime })
    }
    //设置默认月份
    setMonthDefault = (defaultMonth = LAST_MONTH) => {
        //  默认获取上个月
        this.setState({
            monthTime: moment().month(moment().month() - 1).startOf('month')
        })
        return getDate(defaultMonth)
    }
    //设置组件默认时间
    setDefaultDate = ({ title,pTitle,marketsList,fundsTypeList } = this.state) => {
        let defaultList = this.state.defaultList;
        let date;
        let origDate = null;

        if (title == '日期控件') {
            date = getDate(LAST_SEVEN_DAYS);
            origDate = LAST_SEVEN_DAYS;
            this.setState({
                ...this.defaultRadio,time:[]
            })
        } else if (title == '月份控件') {
            this.setState({ isShowMonth: true });
            origDate = moment().month(moment().month() - 1).startOf('month');
            date = this.setMonthDefault()
        }
        defaultList[pTitle] = {'radioValue':LAST_SEVEN_DAYS,'time': [], 'monthTime': origDate};
        if(cookie.get(pTitle)!=""){
            date.endTime=JSON.parse(cookie.get(pTitle))[0]
           date.startTime=JSON.parse(cookie.get(pTitle))[1]
          }
        this.setState({ toPropsDate: date,title,pTitle,marketsList,fundsTypeList,defaultList });
        return date
    }
    //整合日期
    onChangeTime = (date, type) => {
        switch (type) {
            case RADIO_TIME:
                this.setState({ ...this.defaultCTime })
                break;
            case CHECK_TIME:
                this.setState({ ...this.emptyRadio })

                break;
            case NULL_TIME:
                this.setState({ ...this.defaultRadio })
                date = getDate(LAST_SEVEN_DAYS)
                break;
            default:
                break;
        }
        // this.props.onChangeSelectTime(date)
        this.setState({
            toPropsDate: date
        })
    }
    //
    onSelectDateType = (timeType) => {
        this.setState({timeType})
        const {pTitle} = this.state
        this.props.onSelectDateType({timeType,pTitle})
    }
    onSelectScopeType = scopeType => {
        this.setState({scopeType})
        const {pTitle} = this.state;
        this.props.onSelectScopeType({scopeType,pTitle});
    }
    onSelectFundsType = fundsType => {
        this.setState({fundsType})
        const {pTitle} = this.state;
        this.props.onSelectFundsType({fundsType,pTitle});
    }
    showTime = (pTitle,title,radioValue,startTime,endTime,monthTime) => {
        var  startTimes=""
        var  endTimes=""
       if(cookie.get(pTitle)!=""){
        endTimes=endTime==""?JSON.parse(cookie.get(pTitle))[0]:endTime
        startTimes=startTime==""?JSON.parse(cookie.get(pTitle))[1]:startTime
       }
       var radioValues=radioValue==""?null:radioValue
        switch(title){
            case '日期控件':
                if(radioValues != null){
                    return `${moment(getDate(radioValue).startTime).format('YYYY-MM-DD')}~${moment(getDate(radioValue).endTime).format('YYYY-MM-DD')}`
                }else{
                    return `${moment(startTimes).format('YYYY-MM-DD')} ~ ${moment(endTimes).format('YYYY-MM-DD')}`
                }
            case '月份控件':  
                return moment(monthTime).format('YYYY-MM')
        }
    }
    render() {
        const { dateVisible,marketsList,fundsTypeList,time,monthTime,toPropsDate,isDefine,defaultList,radioValue,startTime,endTime } = this.state;
        return (
            <div className='right'>
                {this.props.isScopeType&&<div className='right'>
                    <Select style={{width:'140px'}} defaultValue={''} value={this.state.scopeType} onChange={this.onSelectScopeType}>
                        {marketsList}
                    </Select>
                </div>}
                {this.props.isFundsType&&<div className='right'>
                    <Select style={{width:'120px'}} defaultValue={'0'} value={this.state.fundsType} onChange={this.onSelectFundsType}>
                        {fundsTypeList}
                    </Select>
                </div>}
                {this.props.isDateType&&<div className='right mar10'>
                    <Select style={{width:'120px'}} defaultValue={1} value={this.state.timeType} onChange={this.onSelectDateType}>
                        <Option key='1' value={1}>按天</Option>
                        <Option key='2' value={2}>按周</Option>
                        <Option key='3' value={3}>按月</Option>
                    </Select>
                </div>}
                {this.props.isDateComp&&<Popover
                    content={<Content
                        dateCompHide={this.dateCompHide}
                        onChangeCheckTime={this.onChangeCheckTime}
                        onChangeRadio={this.onChangeRadio}
                        onChangeMonthTime={this.onChangeMonthTime}
                        {...this.state}
                    />}
                    trigger="click"
                    visible={this.state.dateVisible}
                    onVisibleChange={this.handleVisibleChange}
                    placement='bottom'
                >
                    <a href='javascript:void(0);' style={{display:'block',padding:'0 10px',height:'30px',lineHeight:'30px',backgroundColor:'#FFF',color:'#333',textAlign:'center',borderRadius:'5px'}} className="right mar10" >{this.showTime(this.props.pTitle,this.props.title,radioValue,startTime,endTime,monthTime)}</a>
                </Popover>}
            </div>
        )
    }
}