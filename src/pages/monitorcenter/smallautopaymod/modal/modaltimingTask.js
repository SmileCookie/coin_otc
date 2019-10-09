import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import 'moment/locale/zh-cn';
import { DOMAIN_VIP,TIMEFORMAT,SELECTWIDTH } from '../../../../conf/index';
import { Select,Modal,Button,Pagination,Input,DatePicker,Radio,TimePicker} from 'antd'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Option = Select.Option;
const RadioGroup = Radio.Group;
moment.locale('zh-cn');
export default class ModaltimingTask extends React.Component{
    constructor(props){
        super(props)
        this.state = {
                jobName:'',
                jobStartTime:'',
                jobEndTime:'',
                jobStatus:'',
                jobInterval:'',
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.changeRadio = this.changeRadio.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
    }
    componentDidMount(){
        this.setState({
            jobName:this.props.item.jobName,
            jobStartTime:this.props.item.jobStartTime?moment(this.props.item.jobStartTime,"HH:mm:ss"):'',
            jobEndTime:this.props.item.jobEndTime?moment(this.props.item.jobEndTime,"HH:mm:ss"):'',
            jobStatus:String(this.props.item.jobStatus),
            jobInterval:this.props.item.jobInterval
        })
    }
    changeRadio(e){
        this.setState({
            jobStatus: e.target.value,
        });
        this.props.changeRadio(e.target.value)
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
                jobName:nextProps.item.jobName,
                jobStartTime:nextProps.item.jobStartTime?moment(nextProps.item.jobStartTime,"HH:mm:ss"):'',
                jobEndTime:nextProps.item.jobEndTime?moment(nextProps.item.jobEndTime,"HH:mm:ss"):'',
                jobStatus:String(nextProps.item.jobStatus),
                jobInterval:nextProps.item.jobInterval
            },()=>{
                console.log(this.state.jobStartTime)
            })
    }
    //时间控件
    onChangeCheckTime(time, timeString) {
        this.setState({
            jobEndTime:time,
        })
        this.props.ModalchengeTime(timeString)
    }
    onChangeTime(time, timeString){
        this.setState({
            jobStartTime:time,
        })
        this.props.onChengeTime(timeString)
    }
    render(){
        const {jobName,jobStartTime,jobEndTime,jobInterval} =this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
              <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">作业名称：</label>
                        <div className="col-sm-6">
                            <input type="text" className="form-control" name="jobName" value={jobName} disabled={true}/>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">开始执行时间：</label>
                        <div className="col-sm-5">
                         <TimePicker value={jobStartTime} onChange={this.onChangeTime}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">结束执行时间：</label>
                        <div className="col-sm-5">
                        <TimePicker value={jobEndTime} onChange={this.onChangeCheckTime}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">功能类型：</label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.changeRadio} value={this.state.jobStatus}>
                                <Radio value={"1"}>启用</Radio>
                                <Radio value={"0"}>停用</Radio>
                            </RadioGroup>
                        </div>
                    </div> 
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">时间间隔：</label>
                        <div className="left col-sm-5">
                            <input type="text" className="form-control"  name="jobInterval" value={jobInterval} onChange={this.handleInputChange} disabled={true}/>
                        </div>
                        <div className="left line34 pad10 col-sm-2">秒</div>
                    </div>               
                </div>
            </div>
        )
    }
}