import React from 'react'
import { Input,Select,message } from 'antd'
import { pageLimit,tableScroll,showChooseType} from '../../../../utils'
const { TextArea } = Input;

export default class ModalMarketControl extends React.Component{

    constructor(props){
        super(props)
        this.state = {

            Beizu:props.item.memo
        }
        console.log(props)

    }

    componentDidMount(){


    }

    componentWillReceiveProps(nextProps){
        if(nextProps.item.id !== this.props.item.id ){
           this.setState({
                Beizu:nextProps.item.memo
           })
        }
    }

    //输入时 input 设置到 satte
    handleInputChange = event =>{
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }

    render(){
        let {item,sureType} = this.props;
        let {Beizu} = this.state;
        let _cooperType = showChooseType(item.cooperateType);
        let th_3, th_4, th_5;
        switch(sureType){
            case 0:
                th_3 = '媒体名称'
                th_4 = '联系人'
                th_5 = '电话'
            break;
            case 1:
                th_3 = '姓名'
                th_4 = '联系人'
                th_5 = '电话'
            break;
            case 3:
                th_3 = '钱包名称'
                th_4 = '官方链接'
                th_5 = '联系人'
            break;
            case 4:
                th_3 = '姓名'
                th_4 = '电话'
                th_5 = '微信'
            break;
        }

        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-2 control-label">{th_3}：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={item.name || item.walletName}  name='' readOnly /><br /><br />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">{th_4}：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={ (()=>{
                                                                    switch(sureType){
                                                                        case 0:
                                                                            return item.userName
                                                                        case 1:
                                                                            return item.userName
                                                                        case 3:
                                                                            return item.websitesLink
                                                                        case 4:
                                                                            return item.mobile
                                                                        default:
                                                                            return item.userName
                                                                    }
                                                                })()}  name='' readOnly /><br /><br />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">{th_5}：</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={ (()=>{
                                                                    switch(sureType){
                                                                        case 0:
                                                                            return item.mobile
                                                                        case 1:
                                                                            return item.mobile
                                                                        case 3:
                                                                            return item.userName
                                                                        case 4:
                                                                            return item.wechat
                                                                        default:
                                                                            return item.mobile
                                                                    }
                                                                })()}  name='' readOnly /><br /><br />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">{sureType == 4?'钱包地址：':'微信号：'}</label>
                    <div className="col-sm-6">
                        <input type="text" className="form-control" value={(()=>{
                                                                    switch(sureType){
                                                                        case 0:
                                                                            return item.wechat
                                                                        case 1:
                                                                            return item.wechat
                                                                        case 3:
                                                                            return item.wechat
                                                                        case 4:
                                                                            return item.walletAddress
                                                                        default:
                                                                            return item.wechat
                                                                    }
                                                                })()}  name='' readOnly /><br /><br />
                    </div>
                </div>
                {   sureType !== 4 &&
                     <div className="form-group">
                     <label className="col-sm-2 control-label">合作类型：</label>
                     <div className="col-sm-6">
                         <input type="text" className="form-control" value={ _cooperType.map((name,index) =>{
                                                                        return name
                                                                    })
                                                                }  name='' readOnly /><br /><br />
                     </div>
                 </div>
                }
                <div className="form-group">
                    <label className="col-sm-2 control-label">备注：<i>*</i></label>
                    <div className="col-sm-8 text-box">
                        <TextArea onChange={this.handleInputChange} value={Beizu} name="Beizu" rows={4} />
                    </div>
                </div>
            </div> 
        )
    }
}
