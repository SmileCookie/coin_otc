import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP, TIMEFORMAT, SELECTWIDTH } from '../../../conf'
import { Button, message, Modal } from 'antd'
import { toThousands,pageLimit } from '../../../utils'
import ModalModify from './modal/modalModify'

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
        axios.get(DOMAIN_VIP + "/brush/config/list").then(res => {
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
            title:"量化参数修改",
            modalHtml:<ModalModify item={item} handleInputChange={this.handleInputChange} />,
            item:item
        }) 
    }

    modalModifyBtn(){
        const { item } = this.state
        if(item.pollingTime < 1){
            message.warning("主程序等待时间规则：>=1 数字");
            return false;
        }
        if(item.brushForecastCancelDealRate > 1 || item.brushForecastCancelDealRate < 0){
            message.warning("刷量时撤单前吃单的比例规则：（0,1）大于0小于1   数字");
            return false;
        }
        if(item.thirdPlatformPriceBase <= 0){
            message.warning("外网成交价格浮动比例规则：>0  数字");
            return false;
        }
        if(item.thirdPlatformNumberBase <= 0){
            message.warning("完全成交需要第三方平台数量是用户单数量的固定倍数规则：>1  数字");
            return false;
        }
        if(item.dealUserNumberStart <= 0){
            message.warning("成交用户单数量区间开始规则：>0  数字");
            return false;
        }
        if(item.dealUserNumberEnd <= 0){
            message.warning("成交用户单数量区间结束规则：>0  数字");
            return false;
        }
        if(item.safePriceInterval <= 0){
            message.warning("撤单时买一卖一价格安全差值规则：>0  数字");
            return false;
        }
        if(item.lowNumberStart <= 0){
            message.warning("盘口厚度低量区间开始规则：>0  数字");
            return false;
        }
        if(item.lowNumberEnd <= 0){
            message.warning("盘口厚度低量区间结束规则：>0  数字");
            return false;
        }
        if(item.highNumberStart <= 0){
            message.warning("盘口厚度高量区间开始规则：>0  数字");
            return false;
        }
        if(item.highNumberEnd <= 0){
            message.warning("盘口厚度高量区间结束规则：>0  数字");
            return false;
        }
        if(item.lowNumberStartSell <= 0){
            message.warning("卖盘盘口厚度低量区间开始规则：>0  数字");
            return false;
        }
        if(item.lowNumberEndSell <= 0){
            message.warning("卖盘盘口厚度低量区间结束规则：>0  数字");
            return false;
        }
        if(item.highNumberStartSell <= 0){
            message.warning("卖盘盘口厚度高量区间开始规则：>0  数字");
            return false;
        }
        if(item.highNumberEndSell <= 0){
            message.warning("卖盘盘口厚度高量区间结束规则：>0  数字");
            return false;
        }
        if(item.priceRatioStart <= 0){
            message.warning("盘口密度区间开始规则：>0  数字");
            return false;
        }
        if(item.priceRatioEnd <= 0){
            message.warning("盘口密度区间结束规则：>0  数字");
            return false;
        }
        if(item.priceRatioEnd <= 0){
            message.warning("盘口密度区间结束规则：>0  数字");
            return false;
        }
        if(Number.isNaN(item.floatPriceBuy)){
            message.warning("标买一浮动比例 ：任意数字");
            return false;
        }
        if(item.targetPriceRate < 0){
            message.warning("目标买一卖一价差比例 ：任意数字");
            return false;
        }
        const numberRatioArr = item.numberRatio.split(":");
        if(!Number.isInteger(numberRatioArr[0]-0)||!Number.isInteger(numberRatioArr[1]-0)){
            message.warning("盘口高低量比规则：整数数字比例，例如(1:3,2:5),整数:整数 ");
            return false;
        }
        const numberRatioSellArr = item.numberRatioSell.split(":");
        if(item.numberRatioSell){
            if(!Number.isInteger(numberRatioSellArr[0]-0)||!Number.isInteger(numberRatioSellArr[1]-0)){
                message.warning("卖盘盘口高低量比规则：整数数字比例，例如(1:3,2:5),整数:整数 ");
                return false;
            }
        }
        if(!Number.isInteger(item.qtUserId-0)||item.qtUserId < 1){
            message.warning("量化交易账号规则：	>=1 整数数字");
            return false;
        }
        if(!Number.isInteger(item.qtSuperUserId-0)||item.qtSuperUserId < 1){
            message.warning("刷单账户规则规则：	>=1 整数数字");
            return false;
        }
        if(item.brushBuildBatchRate > 1 || item.brushBuildBatchRate < 0){
            message.warning("铺单时需要批量铺单的价格差比例规则：（0,1）大于0小于1   数字");
            return false;
        }
        axios.post(DOMAIN_VIP+"/brush/config/update",qs.stringify(item)).then(res => {
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
                    当前位置：数据中心 > 量化交易管理 > 量化参数管理
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
                                                <th className="column-title">量化交易账号</th>
                                                <th className="column-title">主程序等待时间(秒)</th>
                                                <th className="column-title">盘口厚度高量区间</th>
                                                <th className="column-title">盘口厚度低量区间</th>
                                                <th className="column-title">盘口厚度高低量比</th>
                                                <th className="column-title">盘口密度区间</th>
                                                <th className="column-title">成交用户单数量区间</th>

                                                <th className="column-title">外网成交价格浮动比例</th>
                                                <th className="column-title">完全成交需要第三方平台数量是用户单数量的固定倍数</th>
                                                <th className="column-title">撤单时买一卖一价格安全差值</th>
                                                <th className="column-title">刷量时撤单前吃单的比例</th>
                                                <th className="column-title">铺单时需要批量铺单的价格差比例</th>

                                                <th className="column-title">刷单账户</th>

                                                <th className="column-title">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ? tableList.map((item, index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{item.market}</td>
                                                            <td>{item.qtUserId}</td>
                                                            <td>{item.pollingTime}</td>
                                                            <td>{item.highNumberStart}-{item.highNumberEnd}</td>
                                                            <td>{item.lowNumberStart}-{item.lowNumberEnd}</td>
                                                            <td>{item.numberRatio}</td>
                                                            <td>{item.priceRatioStart}-{item.priceRatioEnd}</td>
                                                            <td>{item.dealUserNumberStart}-{item.dealUserNumberEnd}</td>  
                                                            <td>{item.thirdPlatformPriceBase}</td>
                                                            <td>{item.thirdPlatformNumberBase}</td>
                                                            <td>{item.safePriceInterval}</td>
                                                            <td>{item.brushForecastCancelDealRate}</td>
                                                            <td>{item.brushBuildBatchRate}</td>
                                                            <td>{item.qtSuperUserId}</td>
                                                            <td>
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" onClick={() => this.modalModify(item)}>修改</a>:''}
                                                            </td>                                                         
                                                        </tr>
                                                    )
                                                }) : <tr className="no-record"><td colSpan="15">暂无数据</td></tr>
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
                 mid='BPM'
                 visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}





























