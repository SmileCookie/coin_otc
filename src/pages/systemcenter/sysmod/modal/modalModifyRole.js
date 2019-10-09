import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import 'moment/locale/zh-cn';
import { DOMAIN_VIP,TIMEFORMAT,SELECTWIDTH } from '../../../../conf/index';
import { Button,Radio } from 'antd'
const RadioGroup = Radio.Group;
moment.locale('zh-cn');
export default class ModalModifyRole extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            addName:'',
            addAddr:'',
            mailServerHost:'',
            mailServerPort:'',
            emailUserName:'',
            emailPassword:'',
            status:1
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeRatio = this.onChangeRatio.bind(this)
    }
    componentDidMount(){
        const {sendName,fromAddr,mailServerHost,mailServerPort,emailUserName,emailPassword,status} = this.props.item
        this.setState({
            addName:sendName||'',
            addAddr:fromAddr||'',
            mailServerHost:mailServerHost||'',
            mailServerPort:mailServerPort||'',
            emailUserName:emailUserName||'',
            emailPassword:emailPassword||'',
            status:status||1
        })
    }
    componentWillReceiveProps(nextProps){
        console.log()
        const {sendName,fromAddr,mailServerHost,mailServerPort,emailUserName,emailPassword,status} = nextProps.item
        this.setState({
            addName:sendName||'',
            addAddr:fromAddr||'',
            mailServerHost:mailServerHost||'',
            mailServerPort:mailServerPort||'',
            emailUserName:emailUserName||'',
            emailPassword:emailPassword||'',
            status:status||1
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
        this.props.handleInputChange(event)
    }
    //时间控件
  
    onChangeRatio(e){
        this.setState({
            status:e.target.value,
        })
        this.props.onChangeRatio(e)
    }
    render(){
        const {addName,addAddr,mailServerHost,mailServerPort,emailUserName,emailPassword,status} =this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                 <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">发送名称：</label>
                        <div className="col-sm-6">
                            <input type="text" className="form-control" name="addName" value={addName} onChange={this.handleInputChange}/>
                            <b className="icon-mast">*</b>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">发送邮箱：</label>
                        <div className="col-sm-6">
                            <input type="text" className="form-control" name="addAddr" value={addAddr} onChange={this.handleInputChange}/>
                            <b className="icon-mast">*</b>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">HOST：</label>
                        <div className="col-sm-6">
                            <input type="text" className="form-control" name="mailServerHost" value={mailServerHost} onChange={this.handleInputChange}/>
                            <b className="icon-mast">*</b>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">端口：</label>
                        <div className="col-sm-6">
                            <input type="text" className="form-control" name="mailServerPort" value={mailServerPort} onChange={this.handleInputChange}/>
                            <b className="icon-mast">*</b>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">账号：</label>
                        <div className="col-sm-6">
                            <input type="text" className="form-control" name="emailUserName" value={emailUserName} onChange={this.handleInputChange}/>
                            <b className="icon-mast">*</b>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">密码：</label>
                        <div className="col-sm-6">
                            <input type="password" className="form-control" name="emailPassword" value={emailPassword} onChange={this.handleInputChange}/>
                            <b className="icon-mast">*</b>
                        </div>
                    </div>               
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">状态：</label>
                        <div className="col-sm-6">
                            <RadioGroup onChange={this.onChangeRatio} name="status" value={status}>
                                <Radio value={1}>正常</Radio>
                                <Radio value={2}>注销</Radio>
                            </RadioGroup>
                        </div>
                    </div>               
                </div>
                </div>
        )
    }
}