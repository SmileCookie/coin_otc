import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP, DEFAULTVALUE, TIMEFORMAT, NUMBERPOINT, SELECTWIDTH, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss } from '../../../conf'
import { Button, Select, DatePicker, message, Radio, Checkbox } from 'antd'
import TextReleaseBox from './drawTextRelease'
const { RangePicker } = DatePicker;
const RadioGroup = Radio.Group;
const Option = Select.Option;
export default class DrawPush extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            time:[],
            beginTime: "",   //活动开始时间
            endTime: "",         //活动结束时间
            cycleLimitType:"",      //抽奖规则
            cycleLimitType_num:"",  //上面的次数
            cycleLimitType_num_1: "",  //上面的次数
            relateEventId:"",   // 关联的活动id
            isDouble: false,   //奖金翻倍
            isHighest:false,  //再来一次
            disabled: true,
            limitCount:"",//抽奖上限
            ruleType:"", //奖金规则
            jackpotSize:"",//奖金总值
            radixPoint:"",//小数位
            startSize: "",  //抽奖开始范围
            endSize: "",    //抽奖结束范围
            modify:"",
            relate_list:[],
            last:false
        }
        this.show_click = this.show_click.bind(this)
        this.time_onChange = this.time_onChange.bind(this)
        this.radioOnChange = this.radioOnChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)  
        this.handleradioinpChange = this.handleradioinpChange.bind(this)
        this.checkedChange = this.checkedChange.bind(this)
        this.checkedChangeDouble = this.checkedChangeDouble.bind(this)
        this.radioOnChangeRuleType = this.radioOnChangeRuleType.bind(this)
        this.textnextProps = this.textnextProps.bind(this)
    }
    componentDidMount() {
        axios.post(DOMAIN_VIP + "/drawManage/getRelatedInfo").then(res => {
            const result = res.data
            if (result.code == 0) {
                this.setState({
                    relate_list: result.data.list,
                    last:true
                },() => {
                    this.textnextProps()
                })
            }
        })
        
        
    }
    //cmdmountfun
    textnextProps(){
        if (this.props.activityId) {
            axios.post(DOMAIN_VIP + "/drawManage/showLuckyById", qs.stringify({
                eventId: this.props.activityId
            })).then(res => {
                const result = res.data
                if (result.code == 0) {
                    const luckyEvent = result.data.luckyEvent;
                    const eventInfo = result.data.eventInfo;
                    const luckyRule = result.data.luckyRule[0];
                    this.setState({
                        time: [moment(eventInfo.startTime), moment(eventInfo.endTime)],
                        beginTime: moment(eventInfo.startTime).format(TIMEFORMAT_ss),   //活动开始时间
                        endTime: moment(eventInfo.endTime).format(TIMEFORMAT_ss),         //活动结束时间
                        cycleLimitType: luckyEvent.cycleLimitType,      //抽奖规则
                        cycleLimitType_num: luckyEvent.cycleLimitType == "01" ? luckyEvent.cycleLimitCount : "",  //上面的次数
                        cycleLimitType_num_1: luckyEvent.cycleLimitType == "02" ? luckyEvent.cycleLimitCount : "",  //上面的次数
                        relateEventId: luckyEvent.cycleLimitType == "03" ? luckyEvent.relateEventId : "",   // 关联的活动id
                        isDouble: luckyEvent.isDouble == "01" ? false : true,   //奖金翻倍
                        isHighest: luckyEvent.isHighest == "01" ? false : true,  //再来一次
                        disabled: luckyEvent.cycleLimitType == "03" ? false : true,
                        limitCount: luckyEvent.limitCount,//抽奖上限
                        ruleType: luckyRule.ruleType, //奖金规则
                        jackpotSize: luckyRule.jackpotSize,//奖金总值
                        radixPoint: luckyRule.radixPoint,//小数位
                        startSize: luckyRule.startSize,  //抽奖开始范围
                        endSize: luckyRule.endSize,    //抽奖结束范围
                        modify: eventInfo,
                        activityId: this.props.activityId
                    })
                } else {
                    message.warning(result.msg)
                }
            })

        }
        else {
            this.setState({
                modify: ""
            })
        }
    }
    
    //修改状态
    show_click(index) {
        this.props.showHideClick(index);
    }
    //时间选择框
    time_onChange(value, dateString) {
        this.setState({
            beginTime: dateString[0],
            endTime: dateString[1],
            time: value
        })
    }
    radioOnChange (e)  {
        if (e.target.value == "03"){
            this.setState({
                cycleLimitType: e.target.value,
                disabled:false
            });
        }else{
            this.setState({
                cycleLimitType: e.target.value,
                disabled: true,
                isDouble: false,
                isHighest: false,
            });
        }
    }
    radioOnChangeRuleType(e){
        this.setState({
            ruleType: e.target.value
        });
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    handleradioinpChange(value){
        this.setState({
            relateEventId: value
        })
    }
    checkedChange(e){
        this.setState({
            isHighest: e.target.checked,
        });
    }
    checkedChangeDouble(e) {
        this.setState({
            isDouble: e.target.checked,
        });
    }
    render() {
        const { time, cycleLimitType, relateEventId, cycleLimitType_num, cycleLimitType_num_1, limitCount, ruleType,jackpotSize,radixPoint,
            endSize, startSize, relate_list} = this.state
      return (
        <div className="right-con">
            <div className="page-title">
                当前位置：系统中心>活动管理>抽奖发布
            </div>
            <div className="clearfix"></div>
            <div className="row">
                  <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                    <div className="col-md-4 col-sm-4 col-xs-4">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">发布时间：<i>*</i></label>
                            <div className="col-sm-8">
                                <RangePicker
                                    value={time}
                                    showTime={{ format: TIMEFORMAT_DAYS_ss }}
                                    format={TIMEFORMAT_ss}
                                    onChange={this.time_onChange} />
                            </div>
                        </div>
                    </div>
                    <div className="col-md-7 col-sm-7 col-xs-7">
                        <div className="form-group right">
                            <Button type="primary" onClick={() => { this.show_click(0) }} >返回上一级</Button>
                        </div>
                    </div>
                </div>
                <div className="x_panel">
                  <div className="col-md-12 col-sm-12 col-xs-12 radio_main_draw">
                      <RadioGroup onChange={this.radioOnChange} value={cycleLimitType}>
                        <span className="left">抽奖限制：<i>*</i></span>  
                        <div className="col-mg-2 col-lg-3 col-md-2 col-sm-2 col-xs-2">
                            <Radio value="01">
                                <label className="col-sm-2 left">每天：</label>
                                <div className="col-sm-4 left">
                                    <input type="text" className="form-control form-control-radio" name="cycleLimitType_num" value={cycleLimitType_num} onChange={this.handleInputChange} />
                                </div>
                                <label className="col-sm-1 left">次(暂不可用)</label>
                            </Radio>
                        </div>
                        <div className="col-mg-3 col-lg-4 col-md-3 col-sm-3 col-xs-3">
                            <Radio value="02">
                                <label className="col-mg-3 col-lg-4 col-sm-3 left">活动期间只抽：</label>
                                <div className="col-sm-3 left">
                                    <input type="text" className="form-control form-control-radio" name="cycleLimitType_num_1" value={cycleLimitType_num_1} onChange={this.handleInputChange} />
                                </div>
                                <label className="col-sm-2 left">次(暂不可用)</label>
                            </Radio>
                        </div>
                        <div className="col-mg-2 col-lg-4 col-md-2 col-sm-2 col-xs-2">
                            <Radio value="03">
                                <label className="col-sm-5 left">关联其它活动：</label>
                                <div className="col-sm-3 left">
                                    <Select value={relateEventId} style={{ width: 100,height:22,lineHeight:22 }} onChange={this.handleradioinpChange}>
                                              <Option value="">请选择</Option>
                                        {
                                            relate_list.length>0 && relate_list.map((item,index)=>{
                                                return(
                                                    <Option key={index} value={item.activityId}>{item.activityNameJson}</Option>
                                                )
                                            })
                                        }
                                        
                                    </Select>
                                </div>
                            </Radio>
                        </div>
                        <div className="col-mg-2 col-lg-3 col-md-2 col-sm-2 col-xs-2">
                              <Checkbox
                                  checked={this.state.isHighest}
                                  disabled={this.state.disabled}
                                  onChange={this.checkedChange}
                              >
                              再赋予一次抽奖机会
                              </Checkbox>
                        </div>
                          <div className="col-mg-1 col-lg-2 col-md-1 col-sm-1 col-xs-1">
                              <Checkbox
                                  checked={this.state.isDouble}
                                  disabled={this.state.disabled}
                                  onChange={this.checkedChangeDouble}
                              >
                                  奖金翻倍
                              </Checkbox>
                          </div>
                      </RadioGroup>
                    </div>
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="">
                            <label className="col-md-1 col-sm-1 col-xs-1 padding_0" style={{ width: 130 }}>每位用户抽奖上限：<i>*</i></label>
                            <div className="col-sm-4">
                                <input type="text" className="form-control draw_input mar10" name="limitCount" value={limitCount} onChange={this.handleInputChange} />
                                <span>次</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="x_panel">
                    <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                        <RadioGroup onChange={this.radioOnChangeRuleType} value={ruleType}>
                              <span className="left">奖金规则：<i>*</i></span>
                                  <Radio value="01">
                                      设置上限
                                  </Radio>
                                  <Radio value="02">
                                      组合规则(暂不可用)
                                  </Radio>
                        </RadioGroup>   
                    </div>
                    <div className="col-mg-3 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                        <label className="col-md-1 col-sm-1 col-xs-1 padding_0" style={{ width: 90 }}>奖金总额度：<i>*</i></label>
                        <div className="col-mg-6 col-lg-9 col-md-6 col-sm-6 col-xs-6">
                            <input type="text" className="form-control draw_input mar10" name="jackpotSize" value={jackpotSize} onChange={this.handleInputChange} />
                            <span>GBC</span>
                        </div>
                    </div>
                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                        <label className="col-md-4 col-sm-4 col-xs-4 padding_0">每次抽奖奖金范围：<i>*</i></label>
                        <input type="text" className="form-control draw_input mar10" name="startSize" value={startSize} onChange={this.handleInputChange} />
                        <span className="left mar10">-</span>
                        <input type="text" className="form-control draw_input mar10" name="endSize" value={endSize} onChange={this.handleInputChange} />
                    </div>
                    <div className="col-mg-3 col-lg-6 col-md-3 col-sm-3 col-xs-3">
                          <label className="col-md-1 col-sm-1 col-xs-1 padding_0" style={{ width: 100 }}>允许几位小数：<i>*</i></label>
                          <div className="col-md-9 col-sm-9 col-xs-9">
                              <input type="text" className="form-control draw_input mar10" name="radixPoint" value={radixPoint} onChange={this.handleInputChange} />
                              <span>0为不可生成小数</span>
                          </div>
                    </div>
                </div>
                <div className="x_panel">
                    {
                          this.state.last && <TextReleaseBox show_click={this.show_click} {...this.state} />
                    }
                      
                </div>
            </div>
        </div>
      )
    }
}