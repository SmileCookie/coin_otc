import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, DEFAULTVALUE, TIMEFORMAT, NUMBERPOINT, SELECTWIDTH, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss} from '../../../conf'
import { Button, Select, DatePicker, message } from 'antd'
import TextReleaseBox from './voteTextRelease'
import moment from 'moment'
const { RangePicker } = DatePicker;
const Option = Select.Option;


export default class DistributeVote extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            status:"",
            beginTime:"",
            endTime:"",
            selectCount:"",
            coin:"",
            time:[],
            modify:"",
            activityId:""
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.show_click = this.show_click.bind(this)
        this.time_onChange = this.time_onChange.bind(this)
        this.handleChange = this.handleChange.bind(this)  
        this.coin_handleChange = this.coin_handleChange.bind(this)
    }
    componentDidMount() {
        if (this.props.activityId){
            axios.post(DOMAIN_VIP +"/voteManage/queryById",qs.stringify({
                activityId: this.props.activityId
            })).then(res => {
                const result = res.data
                console.log(result)
                if( result.code == 0 ){
                        this.setState({
                            status: result.data.activityLimit,
                            selectCount: result.data.selectCount,
                            beginTime: moment(result.data.startTime).format(TIMEFORMAT_ss),
                            endTime: moment(result.data.endTime).format(TIMEFORMAT_ss),
                            time: [moment(result.data.startTime), moment(result.data.endTime)],
                            modify: result.data,
                            activityId: this.props.activityId
                        })
                }else{
                    message.warning(result.msg)
                }
            })
       }else{
            this.setState({
                modify:""
            })
       }
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
    //投票方式
    handleChange(value) {
        this.setState({
            status: value
        })
    }
    //币库
    coin_handleChange(value) {
        this.setState({
            coin: value
        })
    }

    render() {
        const { status, selectCount, coin, time} = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>活动管理>投票发布
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
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
                        <div className="col-md-3 col-sm-3 col-xs-3">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">投票方式：</label>
                                <div className="col-sm-8">
                                    <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.handleChange}>
                                        <Option value="">请选择</Option>
                                        <Option value={2}>按日投票</Option>
                                        <Option value={1}>按活动区间</Option>
                                    </Select>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-3 col-sm-3 col-xs-3">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">票数限制：<i>*</i></label>
                                <div className="col-sm-8">
                                    <input type="text" className="form-control" name="selectCount" value={selectCount} onChange={this.handleInputChange} />
                                </div>
                            </div>
                        </div>
                        <div className="col-md-3 col-sm-3 col-xs-3">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">币库：</label>
                                <div className="col-sm-8">
                                    <Select value={coin} style={{ width: SELECTWIDTH }} onChange={this.coin_handleChange}>
                                        <Option value="">新币库</Option>
                                    </Select>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <TextReleaseBox show_click={this.show_click} {...this.state}/>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}





























