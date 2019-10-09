import React from 'react'
import {Input,Radio,Upload,Button,Icon } from 'antd'
const RadioGroup = Radio.Group;
const { TextArea } = Input;

export default class ModalAdverSpace extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            status:'1',
            remark:'',
            bannerGroup:'',
        }
        this.handleInputChange = this.handleInputChange.bind(this)
    }
    componentDidMount(){
        if(this.props.item){
            const {status ,remark,bannerGroup} = this.props.item;
            this.setState({
                status:status||'1',
                remark:remark||'',
                bannerGroup:bannerGroup||'',
            })
        }else{
            this.setState({
                status:'1',
                remark:'',
                bannerGroup:'',
            })
        }
    }
    componentWillReceiveProps(nextProps){
        if(nextProps.item){
            const {status ,remark,bannerGroup} = nextProps.item;
            this.setState({
                status:status||'1',
                remark:remark||'',
                bannerGroup:bannerGroup||''
            })
        }else{
            this.setState({
                status:'1',
                remark:'',
                bannerGroup:'',
            })
        }
    }
    //输入时 input 设置到 state
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]:value
        })
        this.props.handleInputChange(event)
    }
    render(){
        const { remark,status,bannerGroup } = this.state

        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-2 control-label">标题：<i>*</i></label>
                    <div className="col-sm-6">
                        <input type="text"   className="form-control" value={bannerGroup} readOnly />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">状态:</label>
                        <div className="col-sm-8">
                         <RadioGroup onChange={this.handleInputChange} name="status" value={status}>
                            <Radio value={'0'}>关闭</Radio>
                            <Radio value={'1'}>开启</Radio>
                        </RadioGroup>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">备注：</label>
                    <div className="col-sm-8 text-box">
                        <TextArea onChange={this.handleInputChange} value={remark} name="remark" rows={4} />
                    </div>
                </div>
            </div>
        )
    }
}