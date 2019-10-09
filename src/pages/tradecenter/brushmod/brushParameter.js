import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import ModalModify from './modal/modalModify'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT, PAGESIZE_200, SELECTWIDTH, TIMEFORMAT_DAYS } from '../../../conf'
import { Button, Pagination, Select,Modal,message } from 'antd'
import { toThousands,pageLimit } from '../../../utils'
import moment from 'moment'
const Big = require('big.js')
const Option = Select.Option;

export default class BrushParameter extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            tableList: [],
            visible:false,
            width:'',
            title:'',
            modalHtml:'',
            item:{},
            limitBtn: [],
            googVisibal:false,
            type:'',
            check:'',
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modalModify = this.modalModify.bind(this)
        this.modalModifyBtn = this.modalModifyBtn.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }

    componentDidMount() {
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('config', this.props.permissList)
        })
    }

    requestTable(){
        axios.get(DOMAIN_VIP+"/brush/config/gbc/list").then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    tableList:result.data
                })
            }else{
                message.warning(result.code)
            }
        })
    }

    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]:value
        });
    }

     //弹窗隐藏
     handleCancel(){
        console.log("handleCancel")
        this.setState({ 
            visible: false
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
    modalModify(market){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={this.modalGoogleCode}>
                手别抖,没问题再提交
            </Button>,
        ]
        axios.get(DOMAIN_VIP+"/brush/config/gbc/get",{
            params:{
                market:market
            }
        }).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    visible:true,
                    width:"1000px",
                    title:"GBC刷量参数配置",
                    modalHtml:<ModalModify item={result.data} handleInputChange={this.handleInputChange} />,
                    item:result.data
                })
            }else{
                message.warning(result.msg)
            }
        })
        
    }

    modalModifyBtn(){
        const { item } = this.state
        axios.post(DOMAIN_VIP+"/brush/config/gbc/update",qs.stringify(item)).then(res => {
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
        const { visible,title,width,modalHtml,limitBtn } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > GBC刷量管理 > GBC参数管理
            </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive min_tableFixe">
                                    <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">市场</th>
                                                <th className="column-title">刷量用户ID</th>
                                                <th className="column-title">目标价格上限区间</th>
                                                <th className="column-title">目标价格下限区间</th>
                                                <th className="column-title">目标价格区间随机生成频率(秒)</th>
                                                <th className="column-title">目标价格生命周期(秒)</th>
                                                <th className="column-title">预警买一卖一价</th> 
                                                <th className="column-title">委托触发倒计时区间(秒)</th>
                                                <th className="column-title">委托价格浮动区间(小数)</th>
                                                <th className="column-title">委托区间(GBC)</th>
                                                <th className="column-title">委托单个数区间</th>
                                                <th className="column-title">回弹触发倒计时(秒)</th>
                                                <th className="column-title">回弹价格浮动区间(小数)</th>
                                                <th className="column-title">回弹量区间(GBC)</th>
                                                <th className="column-title">回弹单个数区间</th>
                                                <th className="column-title">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                this.state.tableList.length > 0 ? this.state.tableList.map((item, index) => {

                                                    return (
                                                        <tr key={index}>
                                                            <td>{item.market}</td>
                                                            <td>{item.qtUserId}</td>
                                                            <td>{toThousands(item.targetPriceUpperStart)}-{toThousands(item.targetPriceUpperEnd)}</td>
                                                            <td>{toThousands(item.targetPriceLowerStart)}-{toThousands(item.targetPriceLowerEnd)}</td>
                                                            <td>{item.targetPriceChangeTime}</td>
                                                            <td>{item.targetPriceLifecycleStart}-{item.targetPriceLifecycleEnd}</td>
                                                            <td>{toThousands(item.warnBuyPrice)}-{toThousands(item.warnSellPrice)}</td>
                                                            <td>{item.entrustPollingTimeStart}-{item.entrustPollingTimeEnd}</td>
                                                            <td>{toThousands(item.entrustPriceStart)}-{toThousands(item.entrustPriceEnd)}</td>
                                                            <td>{item.entrustAmountStart}-{item.entrustAmountEnd}</td>
                                                            <td>{item.entrustOrderNumberStart}-{item.entrustOrderNumberEnd}</td>
                                                            <td>{item.entrustConversePollingTimeStart}-{item.entrustConversePollingTimeEnd}</td>
                                                            <td>{toThousands(item.entrustConversePriceStart)}-{toThousands(item.entrustConversePriceEnd)}</td>
                                                            <td>{item.entrustConverseAmountStart}-{item.entrustConverseAmountEnd}</td>
                                                            <td>{item.entrustConverseOrderNumberStart}-{item.entrustConverseOrderNumberEnd}</td>
                                                            <td>
                                                                {limitBtn.indexOf('gbcGet')>-1?<a href="javascript:void(0)" onClick={() => this.modalModify(item.market)}>修改</a>:''}
                                                            </td>
                                                        </tr>
                                                    )
                                                }) : <tr className="no-record"><td colSpan="16">暂无数据</td></tr>
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
                 mid='BP'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}





























