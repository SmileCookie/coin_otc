import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH } from '../../../conf'
import {message,Input,Modal,Button,Select,Pagination } from 'antd'
import ModalAddCurrencyOTC from './modal/modalAddCurrencyOTC'
import { pageLimit} from '../../../utils'
const Option = Select.Option

export default class CurrencyManageOTC extends React.Component{
    constructor(props){
        super(props)
        this.state = {
           tableList:[],
           visible:false,
           title:'',
           width:'',
           modalHtml:'',
           status:'',
           value:'',
           type:'',
           showHide:true,
           coinTypeId: '',
           pageIndex: PAGEINDEX,
           pageSize: PAGESIZE,
           pageTotal:0,
           coinType: '',
            coinName:'' ,
            coinFullName: '',
            coinTag: '',
            coinFees: '',
            coinCanWithdraw: '',
            coinCanCharge:'',
            coinConfirmTimes: '',
            coinMinWithdraw: '',
            coinBixDian: '',
            coinWithdrawTimes: '',
            coinDayCash: '',
            coinMaxWithdraw: '',
            autoDownloadLimit: '',
            minimumCharge: '',
            coinUrl : '',
            recoinType: '',
            item:'',
            loading:false,
            limitBtn:[]
        }
        this.requestTable = this.requestTable.bind(this)
        this.changeDetail = this.changeDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onAuditInfoBtn = this.onAuditInfoBtn.bind(this)
        this.deleteItem = this.deleteItem.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.onResetState = this.onResetState.bind(this)
        //this.handleChangeState = this.handleChangeState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.handleFundChange =  this.handleFundChange.bind(this)
        //this.getPushAddress = this.getPushAddress.bind(this)
        this.setcoinUrl= this.setcoinUrl.bind(this)
        this.handleSelectChange = this.handleSelectChange.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('otcCointype',this.props.permissList)
        })
    }
    requestTable(currentIndex,currentSize){
        const { name,recoinType,pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+'/otcCointype/query',qs.stringify({
            coinType:recoinType,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
         })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //地址推送
    // getPushAddress(){
    //     axios.get(DOMAIN_VIP + "/common/push").then(res => {
    //         const result = res.data
    //         if(result.code == 0){
    //             message.success(result.msg)
    //         }else{
    //             message.warning(result.msg)
    //         }
    //     })
    // }

    //查询 按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
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
     //输入时 input 设置到 satte
     handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    // 下拉选择，设置到state
    handleSelectChange(stateObj) {
        this.setState(stateObj)
    }
    //资金类型
    handleFundChange(val){
        this.setState({
            recoinType:val
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    setcoinUrl(coinUrl){
        this.setState({
            coinUrl
        })
    }
    changeDetail(item){
       let mtitle= item ?'修改信息':'新增'
        this.setState({
            item,
            visible:true,
            title:mtitle,
            width:'1200px',
            modalHtml:<ModalAddCurrencyOTC setcoinUrl={this.setcoinUrl} item={item} handleInputChange ={this.handleInputChange} handleSelectChange={this.handleSelectChange}/>,
            coinType:item.coinType|| 1,
            coinName:item.coinName||'',
            coinFullName:item.coinFullName||'',
            coinTag:item.coinTag||'',
            coinCanWithdraw:item.coinCanWithdraw||0,
            coinFees:item.coinFees||'',
            coinCanCharge:item.coinCanCharge||0,
            coinConfirmTimes:item.coinConfirmTimes||'',
            coinMaxWithdraw:item.coinMaxWithdraw||'',
            coinBixDian:item.coinBixDian||'',
            coinWithdrawTimes:item.coinWithdrawTimes||'',
            coinDayCash:item.coinDayCash||'',
            coinUrl:item.coinUrl||'',
            coinMinWithdraw:item.coinMinWithdraw||'',
            autoDownloadLimit:item.autoDownloadLimit||'',
            minimumCharge:item.minimumCharge||'',
            fundsType:item.fundsType||0
        })
    }
    //重置
    onResetState(){
        this.setState({
            recoinType:''
        })
    }
    onAuditInfoBtn(){
        this.setState({
            loading:true
        })
        const {coinType,coinName,coinFullName,coinTag,coinCanWithdraw,coinFees,coinCanCharge,
            coinConfirmTimes,coinMaxWithdraw,coinBixDian,coinWithdrawTimes,coinDayCash,coinUrl,coinMinWithdraw,
            autoDownloadLimit,minimumCharge,item,fundsType}=this.state
        // if (coinType===''||coinName===''||coinFullName===''||coinTag===''||coinCanWithdraw===''||coinFees===''||coinCanCharge===''||coinConfirmTimes===''||coinMaxWithdraw===''||coinBixDian===''||coinWithdrawTimes===''||coinDayCash===''||coinMinWithdraw===''||autoDownloadLimit===''||minimumCharge==='') {
        //     message.warning('必填项不能为空！')
        //     return false
        // }
        if (coinType===''||coinName===''||coinFullName===''||coinTag==='') {
            message.warning('必填项不能为空！')
            return false
        }
        let params = {
            coinType,
            coinName,
            coinFullName,
            coinTag,
            coinUrl,
            // coinCanWithdraw,
            // coinFees,
            // coinCanCharge,
            // coinConfirmTimes,
            // coinMaxWithdraw,
            // coinBixDian,
            // coinWithdrawTimes,
            // coinDayCash,
            // coinMinWithdraw,
            // autoDownloadLimit,
            // minimumCharge,
            fundsType
        }
        let url = ''
        if (item.id) {
            params.id = item.id
            url = '/otcCointype/update'
        }else {
            url = '/otcCointype/insert'
        }
        axios.post(DOMAIN_VIP+url, qs.stringify(params)).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState(
                    {
                        visible:false,
                        loading:false
                    }
                )
                this.requestTable()
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        })
    }
    //关闭弹窗
    handleCancel(){
        this.setState({
            visible:false,
            loading:false
        })
    }
    //删除
    deleteItem(id){
        let self = this;
        Modal.confirm({
            title: "确定删除本项吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+"/otcCointype/delete",qs.stringify({
                        id
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.requestTable()
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    
    render(){
        const {tableList,visible,title,width,modalHtml,showHide,recoinType,pageTotal,pageIndex,pageSize,limitBtn} = this.state
        return(
            <div className="right-con">
            <div className="page-title">
                当前位置：系统中心 > 系统管理 > 币种管理(OTC)
                <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
            </div>
            <div className="clearfix"></div>
            <div className="row">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide&&<div className="x_panel">                        
                        <div className="x_content">
                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                 <div className="form-group">
                                    <label className="col-sm-3 control-label">币种类型:</label>
                                    <div className="col-sm-8">
                                        <Select value={recoinType} style={{ width: SELECTWIDTH }} onChange={this.handleFundChange}>
                                            <Option value="">请选择</Option>
                                            <Option value={1}>虚拟货币</Option>
                                            {/*<Option value={2}>法币</Option>*/}
                                        </Select>
                                    </div>
                                </div>                                
                            </div>
                        </div>
                        <div className="col-md-6 col-sm-6 col-xs-6 right">
                            <div className="right">
                                <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                <Button type="primary" onClick={this.onResetState}>重置</Button>
                                {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={this.changeDetail}>新增</Button>:''}
                                {/* <Button type="primary" onClick={()=>this.getPushAddress()}>地址推送</Button>  */}
                            </div>
                        </div>
                    <div>
                </div>
             </div>}
            
             
            <div className="x_panel">                 
                <div className="x_content">
                    <div className="table-responsive-fixed">
                        <table border='1' className="table table-striped jambo_table bulk_action table-linehei table-more center" style={{'width': '100%'}}>
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">币种姓名</th>
                                    <th className="column-title">币种类型</th>
                                    <th className="column-title">币种全称</th>
                                    <th className="column-title">币种单位</th>
                                    <th className="column-title min_153px">图标地址</th>
                                    <th className="column-title">操作</th>                   
                                {/*
                                    <th className="column-title">是否支持充值</th>
                                    <th className="column-title">是否支持提现</th>
                                    <th className="column-title">充值到账确认次数</th>
                                    <th className="column-title">提币网络手续费率</th>
                                    <th className="column-title">最小提现额度</th>
                                    <th className="column-title">最大提现额度</th>
                                    <th className="column-title">小数点位数</th>
                                    <th className="column-title">提现到账确认次数</th>
                                    <th className="column-title">每日每个账号上限</th>
                                    <th className="column-title">小额自动打币限额</th>
                                    <th className="column-title">最低收费</th>                                                                                                           
                                */}
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length>0?
                                    tableList.map((item,index) => {
                                        return (
                                            <tr key={index}>
                                                <td>{(pageIndex - 1) * pageSize + index + 1}</td>
                                                <td>{item.coinName}</td>
                                                <td>{item.coinType==1?'虚拟货币':'法币'}</td>
                                                <td>{item.coinFullName}</td>
                                                <td>{item.coinTag}</td>
                                                <td>{item.coinUrl}</td>
                                                <td>
                                                    {limitBtn.indexOf('update')>-1?<a className="mar20" onClick ={()=>this.changeDetail(item)}>修改</a>:''}
                                                    {limitBtn.indexOf('delete')>-1?<a onClick ={()=>this.deleteItem(item.id)}>删除</a>:''}
                                                </td>
                                            {/*
                                                <td>{item.coinCanCharge?'是':'否'}</td>
                                                <td>{item.coinCanWithdraw?'是':'否'}</td>
                                                <td>{item.coinConfirmTimes}</td>
                                                <td>{item.coinFees}</td>
                                                <td>{item.coinMinWithdraw}</td>
                                                <td>{item.coinMaxWithdraw}</td>
                                                <td>{item.coinBixDian}</td>
                                                <td>{item.coinWithdrawTimes}</td>
                                                <td>{item.coinDayCash}</td>
                                                <td>{item.autoDownloadLimit}</td>
                                                <td>{item.minimumCharge}</td>
                                            */}
                                            </tr>
                                        )
                                    })
                                    :<tr className="no-record"><td colSpan="20">暂无数据</td></tr>
                                }
                            </tbody>
                        </table>
                    </div>
                    <div className="pagation-box">
                        {pageTotal>0&&
                            <Pagination 
                                size="small" 
                                current={pageIndex}
                                total={pageTotal}  
                                showTotal={total => `总共 ${total} 条`}
                                onChange={this.changPageNum}
                                onShowSizeChange={this.onShowSizeChange}
                                showSizeChanger 
                                showQuickJumper />
                        }
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={[
                        <Button key="back" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.onAuditInfoBtn()}>
                            保存修改
                        </Button>,
                    ]}
                    style={{marginTop:'-80px'}}
                    >
                    {modalHtml}            
                </Modal>
            </div>
            </div>
             </div>
            </div>
        )
    }
}