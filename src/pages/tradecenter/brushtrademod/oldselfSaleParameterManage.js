import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP, TIMEFORMAT, SELECTWIDTH } from '../../../conf'
import { Button , message, Modal } from 'antd'
import { toThousands,pageLimit } from '../../../utils'
import ModalModify from './modal/modalModifySelf'

export default class BrushParameterManage extends React.Component {

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
        axios.get(DOMAIN_VIP + "/brush/config/dealFKList").then(res => {
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
            title:"（旧）自成交配置参数修改",
            modalHtml:<ModalModify item={item} handleInputChange={this.handleInputChange} />,
            item:item
        }) 
    }

    modalModifyBtn(){
        const { item } = this.state
        if(!Number.isInteger(item.userId-0)||item.userId < 1){
            message.warning("挂撤单账号规则：>=1 整数数字");
            return false;
        }
        if(item.pollingTime < 1){
            message.warning("主程序等待时间规则：>=1 数字");
            return false;
        }
        if(item.numberStart <= 0){
            message.warning("自成交挂单总数量起始规则：>0  数字");
            return false;
        }
        if(item.numberEnd <= 0){
            message.warning("自成交挂单总数量结束规则：>0  数字");
            return false;
        }
        if(!Number.isInteger(item.pollingTime-0)||item.pollingTime < 1 || !Number.isInteger(item.pollingFrequency-0)||item.pollingFrequency < 1){
            message.warning("主程序调用频次规则：>=1 整数数字");
            return false;
        }
        axios.post(DOMAIN_VIP+"/brush/config/updateFKDeal",qs.stringify(item)).then(res => {
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
                    当前位置：数据中心 > 量化交易管理 > 旧自成交配置管理
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
                                                <th className="column-title">挂撤单账号</th>
                                                {/* <th className="column-title">主程序等待时间</th> */}
                                                <th className="column-title">主程序调用频次</th>
                                                <th className="column-title">自成交挂单总数量区间</th>
                                                <th className="column-title">每次成交笔数</th>
                                                <th className="column-title">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ? tableList.map((item, index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{item.market}</td>
                                                            <td>{item.userId}</td>
                                                            {/* <td>{item.pollingTime}</td> */}
                                                            <td>每 {item.pollingTime} 秒，调用 {item.pollingFrequency} 次</td>
                                                            <td>{item.numberStart}-{item.numberEnd}</td>
                                                            <td>{item.count}</td>
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
                 mid='BPMOOO'
                 visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}





























