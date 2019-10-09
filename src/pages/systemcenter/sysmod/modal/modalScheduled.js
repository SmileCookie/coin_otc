import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Input,Modal,Button } from 'antd'

export default class ModalScheduled extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            beanName:'',
            methodName:'',
            params:'',
            cronExpression:'',
            remark:''
        }
        this.handleInputChange= this.handleInputChange.bind(this)
        
    
    }
    componentDidMount(){
        const {beanName,methodName,params,cronExpression,remark} = this.props.item
        
        this.setState({
            beanName:beanName||'',
            methodName:methodName||'',
            params:params||'',
            cronExpression:cronExpression||'',
            remark:remark||''
        })
    }
    componentWillReceiveProps(nextProps){
        
        const {beanName,methodName,params,cronExpression,remark} = nextProps.item
        this.setState({
            beanName:beanName||'',
            methodName:methodName||'',
            params:params||'',
            cronExpression:cronExpression||'',
            remark:remark||''
        })
    }
    //输入时 input 设置到 state
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
            const {beanName,methodName,params,cronExpression,remark}= this.state
            return(
                <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                <div className="col-md-12 col-sm-12 col-xs-12">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">bean名称：<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="beanName" value={beanName||''} onChange={this.handleInputChange} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-12 col-sm-12 col-xs-12">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">方法名称：<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="methodName" value={methodName||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                 </div>
                 <div className="col-md-12 col-sm-12 col-xs-12">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">参数：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="params" value={params||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                 </div>
                 <div className="col-md-12 col-sm-12 col-xs-12">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">cron表达式：<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="cronExpression" value={cronExpression||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                 </div>
                 <div className="col-md-12 col-sm-12 col-xs-12">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">备注：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="remark" value={remark||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                 </div>
             </div>

        )
    }
}