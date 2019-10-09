import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Input,Modal,Button,Select,Radio } from 'antd'
const Option = Select.Option
const RadioGroup = Radio.Group;

export default class ModalAddBank extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            name: '',
            state:'' ,
            seq: '',
            val: ''
        }
        this.handleInputChange= this.handleInputChange.bind(this)
    
    }
    componentDidMount(){
        const {name,state,seq} = this.props.item
        this.setState({
            name:name||'',
            state:state||0,
            seq:seq||'',
        })
    }
    componentWillReceiveProps(nextProps){
        const {name,state,seq} = nextProps.item
        this.setState({
            name:name||'',
            state:state||0,
            seq:seq||'',
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
    
        render(){
            const {name,state,seq}= this.state
            return(
                <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                <div className="col-md-12 col-sm-12 col-xs-12">
                     <div className="form-group">
                         <label className="col-sm-3 control-label"> 银行名称:<i>*</i></label>
                         <div className="col-sm-8">
                            <form>
                            <input type="text"  className="form-control"  name="name" value={name||''} onChange={this.handleInputChange} />
                            </form>
                         </div>
                    </div>
                 </div>
                 <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">状态:</label>
                        <div className="col-sm-8">
                             {/* <input type="text" className="form-control"  name="state" value={state||''} onChange={this.handleInputChange} /> */}
                             <RadioGroup onChange={this.handleInputChange} name="state" value={state}>
                                <Radio value={0}>正常</Radio>
                                <Radio value={1}>删除</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                 </div>
                 <div className="col-md-12 col-sm-12 col-xs-12">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">排序:<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control" value={this.state.val}  name="seq" value={seq||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                 </div>
                 {/* <div className="col-md-12 col-sm-12 col-xs-12">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">分组类型：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="type" value={type||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                 </div> */}
             </div>

        )
    }
}