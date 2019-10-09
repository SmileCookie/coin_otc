import React from 'react'
import axios from '../../../../utils/fetch'
import moment from 'moment'
import qs from 'qs'
import { message,Input,Modal,Button,Pagination} from 'antd'
import { DOMAIN_VIP,TIMEFORMAT,PAGEINDEX ,PAGESIZE} from '../../../../conf'
const { TextArea } = Input;


export default class ModalAddcapital extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            ycoord:'',
            xcoord:''

        }
        // this.requestTable = this.requestTable.bind(this)
        // this.onAuditInfoto = this.onAuditInfoto.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        // this.onResave = this.onResave.bind(this)
        // this.handleCancel = this.handleCancel.bind(this)
        // this.changPageNum = this.changPageNum.bind(this)
        // this.onShowSizeChange = this.onShowSizeChange.bind(this)
    }
    //输入时 input 设置到 satte
    handleInputChange(event) {
    const target = event.target;
    const value = target.type === 'checkbox' ? target.checked : target.value;
    const name = target.name;
    this.setState({
        [name]: value
    },()=>this.props.ChangehandleInput(name,value));
}

    render(){
        const { tableList,ycoord,xcoord,pageIndex,pageSize,memo,visible,modalHtml,title,width,pageTotal } = this.state
        return(
            <div className="x_content">
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                <label className="col-sm-3 control-label">x坐标：</label>
                    <div className="col-sm-9">
                        <div className="left col-sm-7">
                            <input type="text" className="form-control"  name="xcoord" value={xcoord} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
            </div>
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                <label className="col-sm-3 control-label">y坐标：</label>
                    <div className="col-sm-9">
                        <div className="left col-sm-7">
                            <input type="text" className="form-control"  name="ycoord" value={ycoord} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
            </div>
        </div>
           
        )
    }
}
