// 理财TASK修改
import React from 'react'
import Decorator from '../../../decorator'
import { Button, Select, message, DatePicker, Divider, Input } from 'antd'
import { SELECTWIDTH, TIMEFORMAT_ss } from '../../../../conf'
import { judgeDate } from 'Utils'
import moment from 'moment'
const { Option } = Select;
const { TextArea } = Input;

@Decorator()
export default class ModalFinancial extends React.Component {

    constructor(props) {
        super(props)
        this.defaultstate = {
            taskname: '',//任务名称
            taskflag: '',// 开启标志，0关闭，1开启
            tasktime: null,//开启时间
            callstartdate: null,//计算数据开始时间
            callenddate: null,//计算数据结束时间
            triggerval: '',//触发值
            taskdesc: '',//任务描述
        }

        this.state = {
            id: '',
            ...this.defaultstate,

        }

    }

    componentDidMount() {
        this.getData(this.props.item)
    }
    componentWillReceiveProps(nextprops) {
        this.getData(nextprops.item)
    }
    getData = (item) => {
        const { id, taskname, taskflag, tasktime, callstartdate, callenddate, triggerval, taskdesc, } = item
        this.setState({
            id, taskname, taskflag, triggerval: triggerval || '', taskdesc: taskdesc || '',
            tasktime: tasktime ?  moment(tasktime) : '',
            callstartdate: callstartdate ? moment(callstartdate) : '',
            callenddate: callenddate ? moment(callenddate) : '',
        })

    }




    // 修改数据
    updateData = () => {
        const { id, taskname, taskflag, tasktime, callstartdate, callenddate, triggerval, taskdesc, } = this.state
        let params = {
            id, taskflag, taskname, triggerval, taskdesc,
            tasktimeString: judgeDate(tasktime) || '',
            callstartdateString: judgeDate(callstartdate) || '',
            callenddateString: judgeDate(callenddate) || '',
           
        }
        let msg = '修改成功';
        this.request({ url: '/finTask/update', type: 'post', msg, isP: true }, params)
        this.props.handleCancel()
        this.props.requestTable()


    }

    render() {
        const { taskname, taskflag, tasktime, callstartdate, callenddate, triggerval, taskdesc, } = this.state
        return (
            <div>
                <div className="x_content">
                    <div className="col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">任务名称：</label>
                            <div className="col-sm-8 ">
                                <input type="text" className="form-control" name="taskname" value={taskname} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                    <div className="col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-4 control-label">开启标志：</label>
                            <div className="col-sm-8">
                                <Select value={taskflag} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'taskflag')} >
                                    <Option value={0}>关闭</Option>
                                    <Option value={1}>开启</Option>
                                </Select>
                            </div>
                        </div>
                    </div>
                    <div className="col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">开启时间：</label>
                            <div className="col-sm-8 ">
                                <DatePicker
                                    showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                                    format={TIMEFORMAT_ss}
                                    onChange={(v, d) => this.onChangeCheckTime(v, d, 'tasktime')}
                                    placeholder='选择时间'
                                    value={tasktime}
                                />
                            </div>
                        </div>
                    </div>
                    <div className="col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">计算开始时间：</label>
                            <div className="col-sm-8 ">
                                <DatePicker
                                    showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                                    format={TIMEFORMAT_ss}
                                    onChange={(v, d) => this.onChangeCheckTime(v, d, 'callstartdate')}
                                    placeholder='选择时间'
                                    value={callstartdate}
                                />
                            </div>
                        </div>
                    </div>
                    <div className="col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">计算结束时间：</label>
                            <div className="col-sm-8 ">
                                <DatePicker
                                    showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                                    format={TIMEFORMAT_ss}
                                    onChange={(v, d) => this.onChangeCheckTime(v, d, 'callenddate')}
                                    placeholder='选择时间'
                                    value={callenddate}
                                />

                            </div>
                        </div>
                    </div>
                    <div className="col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">触发值：</label>
                            <div className="col-sm-8 ">
                                <input type="text" className="form-control" name="triggerval" value={triggerval} onChange={this.handleInputChange} />

                            </div>
                        </div>
                    </div>
                    <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12" style={{marginTop:10}}>
                        <div className="form-group">
                            <label className="col-sm-3 control-label">节点描述：</label>
                            <div className="col-sm-9 ">
                                {/* <input type="text" className="form-control" name="taskdesc" value={taskdesc} onChange={this.handleInputChange} /> */}
                                <TextArea placeholder='最多不超过50个字' name="taskdesc" value={taskdesc} onChange={this.handleInputChange} autosize={{ minRows: 2, maxRows: 4 }} maxLength='50' />
                            </div>
                        </div>
                    </div>
                </div>
                <Divider />
                <div className="addFooter">
                    <div className="col-md-6 col-sm-6 col-xs-6 right">
                        <div className="right">
                            <Button type="primary" onClick={() => this.props.handleCancel()}>取消</Button>
                            <Button type="primary" onClick={this.updateData}>保存修改</Button>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}


