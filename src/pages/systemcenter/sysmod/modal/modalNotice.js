import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { SELECTWIDTH,TIMEFORMAT } from '../../../../conf'
import { Select,Radio,Input,DatePicker} from 'antd'
const Option = Select.Option
const RadioGroup = Radio.Group;
const { TextArea } = Input;

export default class ModalNotice extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            msgPublishAuto:'',
            msgContent:false,
            momsgTitle:'',
            msgType:'',
            msgPublishTimeStr:'',
            channel:'',
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        // this.handleChange = this.handleChange.bind(this)
        // this.handleChangeType = this.handleChangeType.bind(this)
        this.onChange = this.onChange.bind(this)
    }

    componentDidMount(){
        let {msgPublishAuto,msgContent,msgTitle,msgType,msgPublishTimeStr,channel} = this.props.item
        console.log(this.props.item)
        this.setState({
            msgType:msgType||0,
            msgContent,
            momsgTitle:msgTitle,
            msgPublishAuto:msgPublishAuto||false,
            msgPublishTimeStr:msgPublishTimeStr||moment().format("YYYY-MM-DD HH:mm:ss"),
            channel:channel||0
        })
    }

    componentWillReceiveProps(nextProps){
        let { msgPublishAuto,msgContent,msgTitle,msgType,msgPublishTimeStr,channel} = nextProps.item
        console.log(this.props.item)
        this.setState({
            msgType:msgType||0,
            msgPublishAuto:msgPublishAuto||false,
            msgContent,
            momsgTitle:msgTitle,
            msgPublishTimeStr:msgPublishTimeStr||moment().format("YYYY-MM-DD HH:mm:ss"),
            channel:channel||0
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
        this.props.handleInputChange(event)
    }
    // handleChange(val){
    //     this.setState({
    //         enforceUpdate:val
    //     })
    //     this.props.handleChangeUpdate(val)
    // }
    // handleChangeType(val){
    //     this.setState({
    //         type:val
    //     })
    //     this.props.handleChangeType(val)
    // }
    onChange(date, dateString){
        console.log(date, dateString);
        this.setState({
            msgPublishTimeStr:dateString
        })
        this.props.onChange(dateString)
        
    }

    render(){
        const {msgPublishAuto,msgContent,momsgTitle,msgType,msgPublishTimeStr,channel} = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">标题：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="momsgTitle" value={momsgTitle||''}  onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">内容：<i>*</i></label>
                        <div className="col-sm-8">
                            <TextArea rows={4} className="widthText" name="msgContent" value={msgContent} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">是否自动发布：</label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.handleInputChange} name="msgPublishAuto" value={msgPublishAuto}>
                                <Radio value={true}>是</Radio>
                                <Radio value={false}>否</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg ">消息类型：</label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.handleInputChange} name="msgType" value={msgType}>
                                <Radio value={0}>系统消息</Radio>
                                <Radio value={1}>公告</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">发布时间：<i>*</i></label>
                        <div className="col-sm-4">
                            <DatePicker showTime onChange={this.onChange} format="YYYY-MM-DD HH:mm:ss" value={moment(msgPublishTimeStr, 'YYYY-MM-DD HH:mm:ss')}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">消息通道：</label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.handleInputChange} name="channel" value={channel}>
                                <Radio value={0}>app</Radio>
                                <Radio value={1}>web</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                </div>
       
            </div>
        )
    }


}