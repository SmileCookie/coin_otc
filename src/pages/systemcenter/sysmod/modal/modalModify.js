import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { SELECTWIDTH,TIMEFORMAT } from '../../../../conf'
import { Select,Radio,Input } from 'antd'
const Option = Select.Option
const RadioGroup = Radio.Group;
const { TextArea } = Input;

export default class ModalModify extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            type:'',
            name:'',
            enforceUpdate:false,
            cnName:'',
            enName:'',
            hkName:'',
            jpName:'',
            krName:'',
            size:'',
            url:'',
            released:'',
            cnRemark:'',
            enRemark:'',
            hkRemark:'',
            jpRemark:'',
            krRemark:'',
            num:'',
            dealType:'',
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.business_handleChange = this.business_handleChange.bind(this)
    }

    componentDidMount(){
        let { type,name,enforceUpdate,cnName,enName,hkName,num,size,url,released,cnRemark,enRemark,hkRemark,dealType,jpName,jpRemark,krName,krRemark, } = this.props.item
        //console.log(type)
        this.setState({
            type:type||'android',
            name,cnName,enName,hkName,num,size,url,cnRemark,enRemark,hkRemark,jpName,jpRemark,krName,krRemark,
            enforceUpdate:enforceUpdate||false,
            released:released||false,
            dealType:dealType||''
        })
    }

    componentWillReceiveProps(nextProps){
        let { type,name,enforceUpdate,cnName,enName,hkName,num,size,url,released,cnRemark,enRemark,hkRemark,dealType,jpName,jpRemark,krName,krRemark, } = nextProps.item
        //console.log(type)
        this.setState({
            type:type||'android',
            name,cnName,enName,hkName,num,size,url,cnRemark,enRemark,hkRemark,jpName,jpRemark,krName,krRemark,
            enforceUpdate:enforceUpdate||false,
            released:released||false,
            dealType:dealType||''
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
    handleChange(val){
        this.setState({
            enforceUpdate:val
        })
        this.props.handleChangeUpdate(val)
    }
    handleChangeType(val){
        this.setState({
            type:val
        })
        this.props.handleChangeType(val)
    }
    //交易类型
    business_handleChange(value) {
        this.setState({
            dealType:value
        })
        this.props.business_handleChange(value)
    }

    render(){
        const { type,name,enforceUpdate,cnName,enName,hkName,num,size,url,released,cnRemark,enRemark,hkRemark,dealType,jpName,jpRemark,krName,krRemark, } = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">版本类型：<i>*</i></label>
                        <div className="col-sm-8">
                            <Select defaultValue="android" style={{width:SELECTWIDTH}} value={type}  onChange={this.handleChangeType}>
                                <Option value="android">Android</Option>
                                <Option value="ios">IOS</Option>                                            
                            </Select>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">是否强制更新：<i>*</i></label>
                        <div className="col-sm-8">
                            <Select value={`${enforceUpdate}`} style={{width:SELECTWIDTH}}  onChange={this.handleChange}>
                                <Option value="false">不强制更新</Option>
                                <Option value="true">强制更新</Option>          
                            </Select>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">版本名称(中文)：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="cnName" value={cnName||''}  onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">版本名称(英文)：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="enName" value={enName||''}  onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">版本名称(繁体)：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="hkName" value={hkName||''}  onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">版本名称(日语)：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="jpName" value={jpName||''}  onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">版本名称(韩语)：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="krName" value={krName||''}  onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">版本号：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="num" value={num||''}  onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">包大小(带单位)：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="size" value={size||''}  onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">下载地址：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="url" value={url||''}  onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">交易类型：<i>*</i></label>
                        <div className="col-sm-8">
                        <Select name="dealType" value={dealType} style={{width:SELECTWIDTH}}  onChange={this.business_handleChange}>
                            <Option value="">请选择</Option>
                            <Option value="0">币币</Option>
                            <Option value="1">OTC</Option>                                            
                        </Select>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">是否发布：</label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.handleInputChange} name="released" value={released}>
                                <Radio value={true}>是</Radio>
                                <Radio value={false}>否</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                </div>

                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">内容(中文)：<i>*</i></label>
                        <div className="col-sm-8">
                            <TextArea rows={4} className="widthText" name="cnRemark" value={cnRemark} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">内容(英文)：<i>*</i></label>
                        <div className="col-sm-8">
                            <TextArea rows={4} className="widthText" name="enRemark" value={enRemark} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">内容(繁体)：<i>*</i></label>
                        <div className="col-sm-8">
                            <TextArea rows={4} className="widthText" name="hkRemark" value={hkRemark} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">内容(日语)：<i>*</i></label>
                        <div className="col-sm-8">
                            <TextArea rows={4} className="widthText" name="jpRemark" value={jpRemark} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">内容(韩语)：<i>*</i></label>
                        <div className="col-sm-8">
                            <TextArea rows={4} className="widthText" name="krRemark" value={krRemark} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">AppKey：</label>
                        <div className="col-sm-8">
                            <p></p>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">AppSecret：</label>
                        <div className="col-sm-8">
                            <p></p>
                        </div>
                    </div>
                </div>
            </div>
        )
    }


}






























