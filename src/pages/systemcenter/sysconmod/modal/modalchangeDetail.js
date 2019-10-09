import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Input,Modal,Button } from 'antd'

export default class ModalchangeDetail extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            key:'',
            remark:'',
            value:'',
            type:'',
            huitype:false
        }
        this.handleInputChange= this.handleInputChange.bind(this)
        
    
    }
    componentDidMount(){
        const {key,remark,value,type} = this.props.item
        console.log(this.props.huitype)
        this.setState({
            key:key||'',
            remark:remark||'',
            value:value||'',
            type:type||'',
            huitype:this.props.huitype
        })
    }
    componentWillReceiveProps(nextProps){
        console.log(nextProps.huitype)
        const {key,remark,value,type} = nextProps.item
        this.setState({
            key:key||'',
            remark:remark||'',
            value:value||'',
            type:type||'',
            huitype:nextProps.huitype
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
            const {key,remark,value,type,huitype}= this.state
            return(
                <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                <div className="col-md-12 col-sm-12 col-xs-12">
                     <div className="form-group">
                         <label className="col-sm-3 control-label"> 配置key：<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="key" value={key||''} onChange={this.handleInputChange}  disabled={huitype}/>
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
                 <div className="col-md-12 col-sm-12 col-xs-12">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">配置值：<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="value" value={value||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                 </div>
                 <div className="col-md-12 col-sm-12 col-xs-12">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">分组类型：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="type" value={type||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                 </div>
             </div>

        )
    }
}