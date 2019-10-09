import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP, TIMEFORMAT, SELECTWIDTH } from '../../../conf'
import { Button , message, Modal } from 'antd'
import { toThousands,pageLimit } from '../../../utils'
import ModalModifyHanging from './modal/modalModifyHanging'

export default class HangingOrderParameterManage extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            tableList: [],
            visible:false,
            width:'',
            title:'',
            limitBtn: [],
            googVisibal:false,
            type:'',
            check:'',
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.modalModify = this.modalModify.bind(this)
        this.modalModifyBtn = this.modalModifyBtn.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('config', this.props.permissList)
        })
    }
    componentWillUnmount(){
    }

    requestTable(){
        axios.get(DOMAIN_VIP + "/brush/config/cancelList").then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    tableList:result.data
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //弹窗隐藏
    handleCancel(){
        this.setState({ 
            visible: false,
        });
        this.requestTable()
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    //google 验证弹窗
    modalGoogleCode(){
        this.setState({
            googVisibal:true,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { googleCode } = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                })
                this.modalModifyBtn()
            }else{
                message.warning(result.msg)
            }
        })
    }

    //修改弹窗
    modalModify(item){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode()}>
                手别抖,没问题再提交
            </Button>,
        ]
        
        this.setState({
            visible:true,
            width:"1000px",
            title:"挂撤单配置参数修改",
            modalHtml:<ModalModifyHanging item={item} handleInputChange={this.handleInputChange} />,
            item:item
        }) 
    }

    modalModifyBtn(){
        const { item } = this.state
        if(!Number.isInteger(item.supCancelUserId-0)||item.supCancelUserId < 1){
            message.warning("自挂撤账号规则：>=1 整数数字");
            return false;
        }
        if(item.pollingTime < 1){
            message.warning("主程序等待时间规则：>=1 数字");
            return false;
        }
        if(item.supCancelPlaceNumRateStart > 1 || item.supCancelPlaceNumRateStart < 0){
            message.warning("挂单数量比例起始规则：（0,1）大于0小于1   数字");
            return false;
        }
        if(item.supCancelPlaceNumRateEnd > 1 || item.supCancelPlaceNumRateEnd < 0){
            message.warning("挂单数量比例终止规则：（0,1）大于0小于1   数字");
            return false;
        }
        if(!Number.isInteger(item.supCancelCountStart-0)||item.supCancelCountStart < 1){
            message.warning("每秒挂单笔数起始规则：>=1 整数数字");
            return false;
        }
        if(!Number.isInteger(item.supCancelCountEnd-0)||item.supCancelCountEnd < 1){
            message.warning("每秒挂单笔数结束规则：>=1 整数数字");
            return false;
        }
        if(item.supCancelRate > 1 || item.supCancelRate < 0){
            message.warning("挂单数量比例终止规则：（0,1）大于0小于1   数字");
            return false;
        }
        if(!Number.isInteger(item.supCancelDepth-0)||item.supCancelDepth <= 1){
            message.warning("挂撤单操作范围的深度规则：>1 整数数字");
            return false;
        }

        axios.post(DOMAIN_VIP+"/brush/config/updateCancel",qs.stringify(item)).then(res => {
            const result = res.data
            if(result.code == 0 ){
                message.success(result.msg)
                this.setState({
                    visible:false
                },()=>this.requestTable())
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.modalGoogleCodeBtn(values)
        });
    }
    saveFormRef(formRef){
        this.formRef = formRef;
    }
    //谷歌弹窗关闭
    onhandleCancel(){
        this.setState({
            googVisibal: false 
        })
    }

    render() {
        const { tableList,visible,width,title,modalHtml,limitBtn } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 量化交易管理 > 挂撤单配置管理
            </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">市场</th>
                                                <th className="column-title">主程序轮询时间（秒）</th>
                                                <th className="column-title">自挂撤账号</th>
                                                <th className="column-title">挂单数量比例区间</th>
                                                <th className="column-title">每秒挂单笔数区间</th>
                                                <th className="column-title">撤单比例</th>
                                                <th className="column-title">挂撤单操作范围的深度</th>
                                                <th className="column-title">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ? tableList.map((item, index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{item.market}</td>
                                                            <td>{item.pollingTime}</td>
                                                            <td>{item.supCancelUserId}</td>
                                                            <td>{item.supCancelPlaceNumRateStart}-{item.supCancelPlaceNumRateEnd}</td>
                                                            <td>{item.supCancelCountStart}-{item.supCancelCountEnd}</td>
                                                            <td>{item.supCancelRate}</td>
                                                            <td>{item.supCancelDepth}</td>
                                                            <td>
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" onClick={() => this.modalModify(item)}>修改</a>:''}
                                                            </td>                                                         
                                                        </tr>
                                                    )
                                                }) : <tr className="no-record"><td colSpan="14">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    { modalHtml }
                </Modal> 
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='BPMP'
                 visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}





























