import React from 'react'
import moment from 'moment'
import { TIMEFORMAT } from '../../../../conf'
import { Radio ,Input} from 'antd'
const RadioGroup = Radio.Group;
const { TextArea } = Input;

export default class ModalGoogle extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            status:'',
            addTime:'',
            userName:'',
            type:'',
            infoShow:'',
            beforeInfoShow:'',
            tab:'',
            memo:'',
            verifyTimeShowString:'',
            status:'',
        }
        this.onChangeRadio = this.onChangeRadio.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
    }

    componentDidMount(){
        console.log(this.props.item)
        const { addTimeShowString,userName,type,infoShow,beforeInfoShow,status,memo,verifyTimeShowString } = this.props.item
        this.setState({
            addTime:addTimeShowString,
            userName,
            type:type==1?'更改手机':type==2?'更改Google':'挂失手机',
            infoShow,
            beforeInfoShow,
            memo,
            verifyTimeShowString,
            status:status==0?'待审核':status==1?'已拒绝':status==2?'已通过':'已撤销',
            tab:this.props.tab
        })
    }
    componentWillReceiveProps(nextProps){
        console.log()
        const { addTimeShowString,userName,type,infoShow,beforeInfoShow,status,memo,verifyTimeShowString  } = nextProps.item
        this.setState({
            addTime:addTimeShowString,
            userName,
            type:type==1?'更改手机':type==2?'更改Google':'挂失手机',
            infoShow,
            beforeInfoShow,
            memo,
            verifyTimeShowString,
            status:status==0?'待审核':status==1?'已拒绝':status==2?'已通过':'已撤销',
            tab:nextProps.tab
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

    //radio change 时
    onChangeRadio(e){
        this.setState({
            status: e.target.value,
        });
        this.props.onModalRadio(e.target.value)
    }

    render(){
        const { addTime,userName,type,infoShow,beforeInfoShow,status,memo,verifyTimeShowString,tab } = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-3 control-label">申请时间：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control" name="addTime"  value={addTime} readOnly />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">用户名：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control" name="userName"  value={userName} readOnly />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">申请操作：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control" name="type"  value={type} readOnly />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">申请修改：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control" name="infoShow"  value={infoShow} readOnly />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">修改后：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control" name="beforeInfoShow"  value={beforeInfoShow} readOnly />
                    </div>
                </div>
                {
                    tab==0&&<div className="form-group">
                                <label className="col-sm-3 control-label">我来审核：</label>
                                <div className="col-sm-9">
                                    <RadioGroup onChange={this.onChangeRadio} value={this.state.status}>
                                        <Radio value={2}>通过用户申请</Radio>
                                        <Radio value={1}>拒绝用户申请</Radio>
                                        <Radio value={3}>撤消用户申请</Radio>
                                    </RadioGroup>
                                </div>
                            </div>
                }
                {
                    tab==0&&<div className="form-group">
                                <label className="col-sm-3 control-label">备注：</label>
                                <div className="col-sm-9 text-box">
                                    <TextArea  name="auditMemo" onChange={this.handleInputChange} />
                                </div>
                            </div>
                }
                {
                    tab!=0&&<div className="form-group">
                                <label className="col-sm-3 control-label">状态：</label>
                                <div className="col-sm-9 text-box">
                                    <input type="text" className="form-control" name="status"  value={status} readOnly />
                                </div>
                            </div>
                }
                {
                    tab!=0&&<div className="form-group">
                                <label className="col-sm-3 control-label">备注：</label>
                                <div className="col-sm-9 text-box">
                                    <TextArea className="bgeee" name="memo" value={memo} onChange={this.handleInputChange} readOnly/>
                                </div>
                            </div>
                }
                {
                    tab!=0&&<div className="form-group">
                                <label className="col-sm-3 control-label">审核时间：</label>
                                <div className="col-sm-9 text-box">
                                    <input type="text" className="form-control" name="verifyTimeShowString"  value={verifyTimeShowString} readOnly />
                                </div>
                            </div>
                }

            </div>
        )
    }
}
































