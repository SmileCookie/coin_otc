import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Select,Button } from 'antd'
const Option = Select.Option;

export default class ModalOnlinePeak extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            startHour:0,
            endHour:24,
            curveRange:"",
            incr:"",
            id:""
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.startHourClick = this.startHourClick.bind(this)
        this.setIncr = this.setIncr.bind(this)
        this.endHourClick = this.endHourClick.bind(this)
        this.resetData = this.resetData.bind(this)
    }

    componentDidMount(){
        console.log(this.props.incrItem)
        const { incrItem } = this.props
        if(incrItem){
            this.setState({
                startHour:incrItem.startHour,
                endHour:incrItem.endHour,
                curveRange:`${incrItem.min}-${incrItem.max}`,
                incr:incrItem.incr,
                id:incrItem.id
            })
        }
    }

    componentWillReceiveProps(nextProps){
        if(nextProps.incrItem){
            this.setState({
                startHour:nextProps.incrItem.startHour,
                endHour:nextProps.incrItem.endHour,
                curveRange:`${nextProps.incrItem.min}-${nextProps.incrItem.max}`,
                incr:nextProps.incrItem.incr,
                id:nextProps.incrItem.id
            })
        }
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
    //开始时间
    startHourClick(val){
        this.setState({
            startHour:val
        })
    }
    //结束时间
    endHourClick(val){
        this.setState({
            endHour:val
        })
    }
    setIncr(){
        const { startHour,endHour,curveRange,incr,id } = this.state
        const URL = this.props.incrItem?"/onlineNumConfig/peak/edit":"/onlineNumConfig/peak/add"
        const rangeArr = curveRange.split("-")
        axios.post(DOMAIN_VIP+URL,qs.stringify({
            configType:this.props.configType,
            startHour,
            endHour,
            max:rangeArr[1],
            min:rangeArr[0],
            incr,
            id
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.props.requestOnline()
            }
            this.resetData()
            this.props.handleCancel()
            this.props.messageNotice(result.msg)
        })
    }
    //重置状态
    resetData(){
        this.setState({
            startHour:0,
            endHour:24,
            curveRange:"",
            incr:"",
            id:""
        })
    }

    render(){
        const { startHour,endHour,curveRange,incr } = this.state
        return (
            <div className="right-con">
                <div className="x_panel">                 
                    <div className="x_content">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">开始时间：</label>
                                <div className="col-sm-8">
                                    <Select value={startHour} style={{ width: 120 }} onChange={this.startHourClick}>
                                        {
                                            "x".repeat(24).split("").map((item,index) => {
                                                return <Option value={index} key={`${index}`}>{index}</Option>   
                                            })
                                        }
                                    </Select>
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">结束时间：</label>
                                <div className="col-sm-8">
                                    <Select value={endHour} style={{ width: 120 }} onChange={this.endHourClick}>
                                        {
                                            "x".repeat(24).split("").map((item,index) => {
                                                return <Option value={index+1} key={index}>{index+1}</Option>   
                                            })
                                        }
                                    </Select>
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">峰值区间：</label>
                                <div className="col-sm-8">
                                    <input type="text" className="form-control" name="curveRange" value={curveRange} onChange={this.handleInputChange} />
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">浮动值：</label>
                                <div className="col-sm-8">
                                    <input type="text" className="form-control" name="incr" value={incr} onChange={this.handleInputChange} />
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <Button className="ant-btn ant-btn-primary" onClick={this.setIncr}>{this.props.incrItem?"修改":"增加"}峰值</Button>       
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}




























