import React from 'react'
import ReactDOM from 'react-dom'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Select,Modal,Button,Table,Pagination,message,DatePicker } from 'antd'
import { toThousands,pageLimit,tableScroll } from '../../../utils'
import GoogleCode from '../../common/modal/googleCode'
import HedgeContractTransactRecordModal from './modal/hedgeContractTransactRecordModal'
const confirm = Modal.confirm;
const Option = Select.Option;
const { Column} = Table
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;

export default class HedgeContractTransactionRecord extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            visible:false,
            googVisibal:false,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],
            pageTotal:0,
            modalHtml:'',
            time:[],
            loading:false,
            width:'',
            market:'',
            platformPlace:'',
            platformHedge:'',
            hedgeType:'',
            hedgeStatus:'',
            sumLossAmount:'0',
            fromTime:'',
            toTime:'',
            replatformPlace:'BITMEX',
            replatformHedge:'BITMEX',
            rehedgeType:'MARKET',
            height:0,
            tableScroll:{
                tableId:'HDECTATNSRD',
                x_panelId:'HDECTATNSRDX',
                defaultHeight:500,
            }

        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.handleChangeSelect = this.handleChangeSelect.bind(this)
        this.requestSelectDefault = this.requestSelectDefault.bind(this)
        this.onEditHedgeConfigModal = this.onEditHedgeConfigModal.bind(this)
        this.onEditHedgeConfigModalBtn = this.onEditHedgeConfigModalBtn.bind(this)
        this.handleChangeSelectModal = this.handleChangeSelectModal.bind(this)

        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.handleGoogleCancel = this.handleGoogleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)

    }
    componentDidMount(){
        this.requestTable()
        this.requestSelectDefault() 
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillReceiveProps(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillUnmount(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight){
        this.setState({
            xheight
        })
    }
    //查询 按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        })
        this.requestTable(PAGEINDEX,PAGESIZE)
    }
    //点击收起
    clickHide() {
        let { showHide,xheight,pageSize } = this.state;
            if(showHide&&pageSize>10){
                this.setState({
                    showHide: !showHide,
                    height:xheight,
                })
            }else{
                this.setState({
                    showHide: !showHide,
                    height:0
                })
            }
            // this.setState({
            //     showHide: !showHide,
            // })
    }
    //重置状态
    onResetState(){
        this.setState({
            market:'',
            platformPlace:'',
            platformHedge:'',
            hedgeType:'',
            hedgeStatus:'',
            time:[],
            fromTime:'',
            toTime:''
        })
    }
    //关闭弹窗
    handleCancel(){
        this.setState({
            visible: false,
            loading:false
        })
    }
    //select默认状态
    requestSelectDefault(){
        axios.get(DOMAIN_VIP+"/brush/futures/hedge/config/get").then(res => {
            const result = res.data;
            console.log(result)
            if(result.code ==0&&result.data ){
                this.setState({
                        replatformPlace:result.data.place,
                        replatformHedge:result.data.hedge,
                        rehedgeType: result.data.type,
                    modalState:{
                        platformPlace:result.data.place,
                        platformHedge:result.data.hedge,
                        hedgeType: result.data.type,
                    }
                },()=>console.log(this.state))
            }else{
                message.warning(result.msg)
            }
        })
    }
    //点击分页
    changPageNum(page,pageSize){
        this.requestTable(page,pageSize)
        this.setState({
            pageIndex:page,
            pageSize:pageSize
        })
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
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
    } 
     //时间 select
     onChangeTime(date, dateString){
        this.setState({
            fromTime:dateString[0],
            toTime:dateString[1],
            time:date
        })
    }
    //资金类型 select
    handleChangeType(val){
        this.setState({
            fundType:val
        })
    }
    //select选择框
    handleChangeSelect(val,type){
        if(type == 'hedgeStatus'){//下单状态
            this.setState({
                hedgeStatus:val,
            })
        }else if(type == 'hedgeType'){//下单类型
            this.setState({
                hedgeType:val,
            })
        }else if(type == 'platformPlace'){//下单平台
            this.setState({
                platformPlace:val,
            })
        }else if(type == 'platformHedge'){//对冲平台
            this.setState({
                platformHedge:val,
            })
        }else if(type =='market'){
            this.setState({
                market:val
            })
        }
    }
    //弹框内的select
    handleChangeSelectModal(val,type){
        if(type == 'hedgeType'){//下单类型
            this.setState({
                rehedgeType:val,
            })
        }else if(type == 'platformPlace'){//下单平台
            this.setState({
                replatformPlace:val,
            })
        }else if(type == 'platformHedge'){//对冲平台
            this.setState({
                replatformHedge:val,
            })
        }
    }
    //table 请求
    requestTable(currentIndex,currentSize){
        const {pageIndex,pageSize,market,platformHedge,platformPlace,hedgeStatus,hedgeType,fromTime,toTime} = this.state
        axios.post(DOMAIN_VIP+"/brush/futures/hedge/order/list",qs.stringify({
            market,platformHedge,platformPlace,hedgeStatus,hedgeType,fromTime,toTime,
            pageNo:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code ==0 ){
                this.setState({
                    tableList:result.data.list?result.data.list:[],
                    pageTotal:result.data.totalCount,
                    sumLossAmount:result.data.sumLossAmount?result.data.sumLossAmount:'0'
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //修改对冲配置弹框
    onEditHedgeConfigModal(){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={this.onEditHedgeConfigModalBtn}>保存修改</Button>
        ]
        this.setState({
            visible:true,
            title:'修改对冲配置默认值',
            width:'500px',
            modalHtml:<HedgeContractTransactRecordModal handleChangeSelect={this.handleChangeSelectModal} item={this.state.modalState}/>
        })
    }
    //修改对冲配置 按钮
    onEditHedgeConfigModalBtn(){
        const {rehedgeType,replatformHedge,replatformPlace} = this.state
        axios.post(DOMAIN_VIP+"/brush/futures/hedge/config/update",qs.stringify({
            type:rehedgeType,
            hedge:replatformHedge,
            place:replatformPlace
        })).then(res => {
            const result = res.data
            if(result.code==0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                })
                this.requestSelectDefault()
                this.requestTable()
            }else{
                message.warning(result.msg)
            }
        })
    }
        //google弹窗
        modalGoogleCode(item,type){
            this.setState({
                googVisibal:true,
                googleSpace:item,
                googleType:type
            })
        }
        //google 按钮
        modalGoogleCodeBtn(values){
            const {googleSpace,googleType} = this.state
            const {googleCode} = values
            axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
                googleCode
            })).then(res => {
                const result = res.data
                if(result.code == 0){
                    this.setState({
                        googVisibal:false
                    })
                    if(googleType=='edit'||googleType=='add'){
                        this.onAddEditAdverSpaceBtn(googleSpace)
                    }else if(googleType == 'del'){
                        this.onDeleteBtn(googleSpace)
                    }else if(googleType == 'photoEdit'){
                        this.onSelectPhotosBtn(googleSpace)
                    }
                }else{
                    message.warning(result.msg)
                }
            })
        }
        //google 校验并获取一组输入框的值
        handleCreate(){
            const form = this.formRef.props.form;
            form.validateFields((err,values) => {
                if(err) return;
                //重置输入框的值
                form.resetFields();
                this.modalGoogleCodeBtn(values)
            })
        }
        saveFormRef(formRef){
            this.formRef = formRef
        }
        //google 弹窗关闭
        handleGoogleCancel(){
            this.setState({
                googVisibal:false
            })
        }
    render(){
        const {tableList,showHide,pageIndex,pageSize,pageTotal,visible,googVisibal,time,width,market,platformHedge,platformPlace,hedgeStatus,hedgeType,sumLossAmount} = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 期货保值管理 > 对冲合约交易记录
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">市场：</label>
                                        <div className="col-sm-8">
                                            <Select value={market} style={{ width: SELECTWIDTH }} onChange={(val)=>this.handleChangeSelect(val,'market')}>
                                                <Option value=''>请选择</Option>
                                                <Option value='btc_usdc'>btc_usdc</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对冲下单类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={hedgeType} style={{ width: SELECTWIDTH }} onChange={(val)=>this.handleChangeSelect(val,'hedgeType')}>
                                                <Option value=''>请选择</Option>
                                                <Option value='MARKET'>市价单</Option>
                                                <Option value='LIMIT'>限价单</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">下单平台：</label>
                                        <div className="col-sm-8">
                                            <Select value={platformPlace} style={{ width: SELECTWIDTH }} onChange={(val)=>this.handleChangeSelect(val,'platformPlace')}>
                                                <Option value=''>请选择</Option>
                                                <Option value='BITMEX '>BITMEX</Option>
                                                <Option value='TDEX'>TDEX</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对冲平台：</label>
                                        <div className="col-sm-8">
                                            <Select value={platformHedge} style={{ width: SELECTWIDTH }} onChange={(val)=>this.handleChangeSelect(val,'platformHedge')}>
                                                <Option value=''>请选择</Option>
                                                <Option value='BITMEX'>BITMEX</Option>
                                                <Option value='TDEX'>TDEX</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对冲下单状态：</label>
                                        <div className="col-sm-8">
                                            <Select value={hedgeStatus} style={{ width: SELECTWIDTH }} onChange={(val)=>this.handleChangeSelect(val,'hedgeStatus')}>
                                                <Option value=''>新建</Option>
                                                <Option value='1'>交易中</Option>
                                                <Option value='2'>已成交</Option>
                                                <Option value='3'>交易失败</Option>
                                                <Option value='4'>已撤单</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">下单成交时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker 
                                            showTime={{
                                                defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                            }}
                                            format="YYYY-MM-DD HH:mm:ss"
                                            placeholder={['Start Time', 'End Time']}
                                            onChange={this.onChangeTime }
                                            value={time}
                                        />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">合计笔数：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" readOnly  name="pageTotal" value={pageTotal} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">损失金额：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" readOnly  name="sumLossAmount" value={toThousands(sumLossAmount,true)}  />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-12 col-sm-12 col-xs-12 marTop">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        <Button type="primary" onClick={this.onEditHedgeConfigModal}>修改对冲配置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">    
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList&&this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto table-responsive-fixed" >
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll ">
                                        <thead>
                                            <tr className="headings" >
                                                <th className="column-title">序号</th>
                                                <th className="column-title">下单平台</th>
                                                <th className="column-title">下单号</th>
                                                <th className="column-title">下单方向</th>
                                                <th className="column-title">下单价格</th>
                                                <th className="column-title">下单数量</th>
                                                <th className="column-title">下单类型</th>
                                                <th className="column-title">成交时间</th>
                                                <th className="column-title">对冲平台</th>
                                                <th className="column-title">对冲单号</th>
                                                <th className="column-title">对冲方向</th>
                                                <th className="column-title">对冲价格</th>
                                                <th className="column-title">对冲数量</th>
                                                <th className="column-title">对冲市场</th>
                                                <th className="column-title">对冲下单类型</th>
                                                <th className="column-title">对冲单状态</th>
                                                <th className="column-title">对冲单下单时间</th>
                                                <th className="column-title">对冲单成交时间</th>
                                                <th className="column-title">对冲单成交数量</th>
                                                <th className="column-title">对冲单剩余数量</th>
                                                <th className="column-title">对冲损失金额</th>
                                                <th className="column-title">对冲耗时(单位秒)</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {    
                                                tableList.length>0?tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index} >
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.originEntrustPlatform}</td>
                                                            <td>{item.originEntrustId}</td>
                                                            <td>{item.originEntrustSide==1?'买':'卖'}</td>
                                                            <td>{toThousands(item.originEntrustPrice)}</td>
                                                            <td>{item.originEntrustNum}</td>
                                                            <td>{item.originEntrustType=='MARKET'?'市价单':'限价单'}</td>
                                                            <td>{moment(item.originTransrecordTime).format(TIMEFORMAT)}</td> 
                                                            <td>{item.hedgeEntrustPlatform}</td>
                                                            <td>{item.hedgeEntrustId}</td>
                                                            <td>{item.hedgeEntrustSide==1?'买':'卖'}</td>
                                                            <td>{toThousands(item.hedgeEntrustPrice)}</td>
                                                            <td>{item.hedgeEntrustNum}</td>
                                                            <td>{item.hedgeEntrustMarket}</td>
                                                            <td>{item.hedgeEntrustType=='MARKET'?'市价单':'限价单'}</td>    
                                                            <td>{
                                                                (() => {
                                                                    switch(item.hedgeEntrustStatus){
                                                                        case 1:
                                                                            return '交易中';
                                                                        break; 
                                                                        case 2:
                                                                            return '已成交';
                                                                        break;
                                                                        case 3:
                                                                            return '交易失败';
                                                                        break;
                                                                        case 4:
                                                                            return '已撤单';
                                                                        break;
                                                                        default:
                                                                            return '新建' 
                                                                    }
                                                                })()
                                                                }
                                                            </td>
                                                            <td>{moment(item.hedgeCreationTime).format(TIMEFORMAT)}</td>
                                                            <td>{moment(item.hedgeTransrecordTime).format(TIMEFORMAT)}</td>
                                                            <td>{item.hedgeExecutedAmount}</td>
                                                            <td>{item.hedgeRemainingAmount}</td>
                                                            <td>{toThousands(item.hedgeLossAmount)}</td>   
                                                            <td>{item.hedgeTime}</td>                                        
                                                        </tr>
                                                    )
                                                }):<tr className="no-record"><td colSpan="22">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                <div className="pagation-box">
                                {
                                    pageTotal>0 && <Pagination
                                                size="small"
                                                current={pageIndex}
                                                total={pageTotal}
                                                onChange={this.changPageNum}
                                                showTotal={total => `总共 ${total} 条`}
                                                onShowSizeChange={this.onShowSizeChange}
                                                showSizeChanger
                                                showQuickJumper
                                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                defaultPageSize={PAGESIZE} />
                                }
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={this.state.title}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width={width}
                >
                    {this.state.modalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange = {this.handleInputChange}
                    mid='TFS'
                    visible={this.state.googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )
    }

}