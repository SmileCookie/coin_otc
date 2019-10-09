import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { Pagination,Radio,Input } from 'antd'
const RadioGroup = Radio.Group;
const { TextArea } = Input;

export default class ModalModify extends React.Component{
    constructor(props){
        super(props)

        this.state = {
            Mid:'',
            Mtemplatename:'',
            Mruledatabase:'',
            Msendmodecode:'',
            Msendareacode:'',
            Mtemplatestatus:'',
            Mrulesql:'',
            Mtemplatedesc:'',
        }

        this.handleInputChange = this.handleInputChange.bind(this)
    }

    componentDidMount(){
        const { item } = this.props
        this.setState({
            Mid:item.id||'',
            Mtemplatename:item.templatename||'',
            Mruledatabase:item.ruledatabase||'',
            Msendmodecode:item.sendmodecode||1,
            Msendareacode:item.sendareacode||'',
            Mtemplatestatus:item.templatestatus||'',
            Mrulesql:item.rulesql||'',
            Mtemplatedesc:item.templatedesc||''
        })
    }

    componentWillReceiveProps(nextProps){
        const { item } = nextProps
        this.setState({
            Mid:item.id||'',
            Mtemplatename:item.templatename||'',
            Mruledatabase:item.ruledatabase||'',
            Msendmodecode:item.sendmodecode||1,
            Msendareacode:item.sendareacode||'',
            Mtemplatestatus:item.templatestatus||'',
            Mrulesql:item.rulesql||'',
            Mtemplatedesc:item.templatedesc||''
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
        if(name == 'Msendmodecode'){
            if(value==2){
                this.setState({
                    Msendareacode:3
                })
            }else{
                this.setState({
                    Msendareacode:this.props.item.sendareacode
                })
            }
        }
        this.props.handleInputChange(event)
    }


    render(){
        const { Mtemplatename,Mruledatabase,Msendmodecode,Msendareacode,Mtemplatestatus,Mrulesql,Mtemplatedesc } = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">模版名称：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="Mtemplatename" value={Mtemplatename} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">规则数据库：<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="Mruledatabase" value={Mruledatabase} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">发送渠道：</label>
                        <div className="col-sm-8">
                            <RadioGroup name="Msendmodecode"  onChange={this.handleInputChange} value={Msendmodecode}>
                                <Radio value={1}>短信</Radio>
                                <Radio value={2}>邮件</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">发送区域：</label>
                        <div className="col-sm-8">
                            {
                                Msendmodecode==1?<RadioGroup name="Msendareacode"  onChange={this.handleInputChange} value={Msendareacode}>
                                    <Radio value={1}>国内</Radio>
                                    <Radio value={2}>国际</Radio>
                                    <Radio value={4}>港澳台</Radio>                                
                                    </RadioGroup>
                                    :
                                    <RadioGroup value={Msendareacode}>
                                        <Radio value={3}>全球</Radio>
                                    </RadioGroup>
                            }
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">模版状态：</label>
                        <div className="col-sm-8">
                            <RadioGroup name="Mtemplatestatus"  onChange={this.handleInputChange} value={Mtemplatestatus}>
                                <Radio value={1}>正常</Radio>
                                <Radio value={2}>停用</Radio>                              
                            </RadioGroup>
                        </div>
                    </div>
                </div>

                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">规则SQL：<i>*</i></label>
                        <div className="col-sm-8">
                            <TextArea className="widthText" row={4} name="Mrulesql" value={Mrulesql}  onChange={this.handleInputChange}  />
                        </div>
                    </div>
                </div>

                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">模版描述：<i>*</i></label>
                        <div className="col-sm-8">
                            <TextArea className="widthText" row={4} name="Mtemplatedesc" value={Mtemplatedesc}  onChange={this.handleInputChange}  />
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}






















