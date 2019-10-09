import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Button,Radio,Input, message } from 'antd'
const RadioGroup = Radio.Group;
const { TextArea } = Input;

export default class ModalOnlinePeak extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            apiName:"",
            apiCode:"",
            url:"",
            open:0,
            remark:"",
            id:""
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.setIncr = this.setIncr.bind(this)
        this.resetData = this.resetData.bind(this)
    }

    componentDidMount(){
        const { list } = this.props
        this.resetData()
        if(list){
            this.setState({
                apiName:list.apiName,
                apiCode:list.apiCode,
                url:list.url,
                open:list.open,
                remark:list.remark,
                id:list.id
            })
        }
    }

    componentWillReceiveProps(nextProps){
        if(nextProps.list){
            this.setState({
                apiName:nextProps.list.apiName,
                apiCode:nextProps.list.apiCode,
                url:nextProps.list.url,
                open:nextProps.list.open,
                remark:nextProps.list.remark,
                id:nextProps.list.id
            })
        }else{
            this.resetData()
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
    setIncr(){
        const { apiName,apiCode,url,open,remark,id } = this.state
        const { list,set } = this.props
        const URL = list?"/apiConfig/edit":"/apiConfig/add";
        if(set === 0){
            this.props.handleCancel()
            return false;
        }
        if(!apiName){
            message.warning("请输入接⼝名称")
            return false;
        }
        if(!apiCode){
            message.warning("请输入接⼝编码")
            return false;
        }
        if(!apiName){
            message.warning("请输入接⼝URL")
            return false;
        }
        axios.post(DOMAIN_VIP+URL,qs.stringify({
            apiName,
            apiCode,
            url,
            open,
            remark,
            id
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.props.requestOnline()
            }
            this.resetData()
            this.props.messageNotice(result.msg)
            this.props.handleCancel()
        })
    }
    //重置状态
    resetData(){
        this.setState({
            apiName:"",
            apiCode:"",
            url:"",
            open:"",
            remark:"",
            id:""
        })
    }

    render(){
        const { apiName,apiCode,url,open,remark,id } = this.state
        const { set,list } = this.props
        return (
            <div className="right-con">
                <div className="x_panel">                 
                    <div className="x_content">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">接⼝名称：</label>
                                <div className="col-sm-8">
                                    <input type="text" className="form-control" name="apiName" readOnly={list&&!set} value={apiName} onChange={this.handleInputChange} /> 
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">*接口编码：</label>
                                <div className="col-sm-8">
                                    <input type="text" className="form-control" name="apiCode" readOnly={list&&!set} value={apiCode} onChange={this.handleInputChange} />
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">*接口URL：</label>
                                <div className="col-sm-8">
                                    <input type="text" className="form-control" name="url" readOnly={list&&!set} value={url} onChange={this.handleInputChange} />
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">*是否开启：</label>
                                <div className="col-sm-8">
                                    <RadioGroup onChange={this.handleInputChange} disabled={list&&!set} name="open" value={open}>
                                        <Radio value={0}>关闭</Radio>
                                        <Radio value={1}>开启</Radio>
                                    </RadioGroup>
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12 marbot10">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">接口备注：</label>
                                <div className="col-sm-8">
                                    <TextArea rows={4} value={remark} readOnly={list&&!set} name="remark" onChange={this.handleInputChange}/>
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12 martop10">
                            <Button className="ant-btn ant-btn-primary  right" onClick={this.setIncr}>{!list?"增加PAI开关配置":set==0?"确认":"修改PAI开关配置"}</Button>       
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}




























