import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,TIMEFORMAT } from '../../../../conf'
import { Select,Input,message } from 'antd'
const Option = Select.Option;
const { TextArea } = Input;

export default class ModalDetail extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            id:'',
            operusername:'',
            realName:'',
            opertime:'',
            busiid:'',
            operip:'',
            operbeforelog:'',
            operafterlog:'',
            operchangelog:''
        }
        this.requestTable = this.requestTable.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
    }

    componentDidMount(){
        this.setState({
            id:this.props.id
        },() => this.requestTable())
    }

    componentWillReceiveProps(nextProps){
        this.setState({
            id:nextProps.id
        },() => this.requestTable())
    }

    requestTable(){
        const { id } = this.state
        axios.post(DOMAIN_VIP+"/operInfo/queryInformation",qs.stringify({
            id
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    operusername:result.loglogrecords.operusername,
                    realName:result.loglogrecords.realName,
                    opertime:result.loglogrecords.opertime,
                    busiid:result.loglogrecords.busiid,
                    operip:result.loglogrecords.operip,
                    operbeforelog:result.loglogrecords.operbeforelog,
                    operafterlog:result.loglogrecords.operafterlog,
                    operchangelog:result.loglogrecords.operchangelog
                })
            }else{
                message.warning(result.msg)
            }
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
    }

    render(){
        const { operusername,realName,opertime,busiid,operip,operbeforelog,operafterlog,operchangelog } = this.state 
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">登录名：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="operusername" value={operusername}  readOnly onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">真实姓名：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="realName" value={realName||""}  readOnly onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">操作时间：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="opertime" value={moment(opertime).format(TIMEFORMAT)}  readOnly onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">日志类型：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="operusername" value={operusername}  readOnly onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">对应业务ID：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="busiid" value={busiid||""}  readOnly onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">操作IP：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="operip" value={operip}  readOnly onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">操作前信息：</label>
                        <div className="col-sm-8">
                            <TextArea className="widthText bgeee" rows={4} name="operbeforelog" value={operbeforelog} readOnly onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">操作后信息：</label>
                        <div className="col-sm-8">
                            <TextArea className="widthText bgeee" rows={4} name="operafterlog" value={operafterlog||""} readOnly onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label">操作变更信息：</label>
                        <div className="col-sm-8">
                            <TextArea className="widthText bgeee" rows={4} name="operchangelog" value={operchangelog} readOnly onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
            </div>
        )
    }

}



































