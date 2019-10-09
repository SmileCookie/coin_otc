import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP ,PAGEINDEX,PAGESIZE,SELECTWIDTH,PAGRSIZE_OPTIONS20} from '../../../conf'
import {message,Input,Modal,Button,Select,Pagination} from 'antd'
import ModalPayment from './modal/modalPayment'
import { pageLimit,tableScroll} from '../../../utils'
const Option = Select.Option;

export default class OtcPaymentType extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
           tableList:[],
           visible:false,
           title:'',
           width:'',
           modalHtml:'',
           paymentType:'',
           pageIndex:PAGEINDEX,
           pageSize:PAGESIZE,
           pageTotal:0,
           mopaymentType:'',
           paymentName:'',
           enable:false,
           iconUrl:'',
           item:'',
           loading:false,
           limitBtn:[],
           dealType:'',
           height:0,
            tableScroll:{
                tableId:'otcPYTE',
                x_panelId:'otcPYTEX',
                defaultHeight:500,
            }
        }
        this.requestTable = this.requestTable.bind(this)
        this.changeDetail = this.changeDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onAuditInfoBtn = this.onAuditInfoBtn.bind(this)
        this.deleteItem = this.deleteItem.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.handlePayChange = this.handlePayChange.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('otcPaymentType',this.props.permissList)
        })
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
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
        
    }
    requestTable(currIndex,currSize){
        const {paymentType,pageIndex,pageSize} = this.state
        axios.post(DOMAIN_VIP+'/otcPaymentType/query',qs.stringify({
            paymentType,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
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
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        })
        this.requestTable(page,pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }
    handleChange(value){
        this.setState({
            paymentType:value
        })
    }
    handlePayChange(value,opt){
        this.setState({
            mopaymentType:value,
            paymentName:opt.props.children
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
    changeDetail(item){
       let mtitle= item?'修改信息':'新增'
        this.setState({
            item,
            visible:true,
            title:mtitle,
            width:'600px',
            modalHtml:<ModalPayment item={item}  handleInputChange ={this.handleInputChange} handlePayChange={this.handlePayChange}/>,
            mopaymentType:item.paymentType||0,
            iconUrl:item.iconUrl||'',
            paymentName:item.paymentName||'支付宝',
            enable:item.enable||false,
            
        })
    }
    //重置
    onResetState(){
        this.setState({
            paymentType:'',
        })
    }
    onAuditInfoBtn(){
        //console.log(22222)
        this.setState({loading:true})
        const {mopaymentType,iconUrl,paymentName,enable,item,}=this.state
        item.id?
        axios.post(DOMAIN_VIP+'/otcPaymentType/update',qs.stringify({
            id:item.id,paymentType:mopaymentType,iconUrl,paymentName,enable,
        })).then(res => {
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
        }):axios.post(DOMAIN_VIP+'/otcPaymentType/insert',qs.stringify({
            paymentType:mopaymentType,iconUrl,paymentName,enable,
         })).then(res => {
             const result = res.data;
             if(result.code == 0){
                 message.success(result.msg)
                 this.setState(
                     {
                         visible:false
                     }
                 )
                 this.requestTable()
             }else{
                 message.warning(result.msg)
             }
         })
    }
    handleCancel(){
        this.setState({
            visible:false,
            loading:false,
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
                    axios.post(DOMAIN_VIP+"/otcPaymentType/delete",qs.stringify({
                        id
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.requestTable()
                            self.setState({
                                loading:false
                            })
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                            self.setState({
                                loading:false
                            })
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
        const {tableList,visible,title,width,modalHtml,showHide,paymentType,pageIndex,pageSize,pageTotal,limitBtn} = this.state
        return(
            <div className="right-con">
            <div className="page-title">
                当前位置：系统中心 > 配置中心 > 支付方式
                <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
            </div>
            <div className="clearfix"></div>
            <div className="row">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                        
                        <div className="x_content">

                            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                <div className="form-group">
                                    <label className='control-label col-sm-3'>付款方式：</label>
                                    <div className="col-sm-8">
                                        <Select value={paymentType}  style={{ width:SELECTWIDTH }} onChange={(val) => this.handleChange(val)}>
                                            <Option value=''>请选择</Option>
                                            <Option value='0'>支付宝</Option>
                                            <Option value='1'>微信</Option>
                                            <Option value='2'>银行</Option>
                                            <Option value='3'>BTC</Option>
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
                            </div>
                        </div>
                    <div>
                </div>
             </div>}
            
             
            <div className="x_panel">                 
                <div className="x_content">
                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                        <table className="table table-striped jambo_table bulk_action table-linehei table_scroll table-more">
                            <thead>
                                <tr className="headings">
                                    <th className="column-title">序号</th>
                                    <th className="column-title">付款方式</th>
                                    <th className="column-title">图标地址</th>
                                    <th className="column-title">是否开启</th>
                                    <th className="column-title">操作</th>                   
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    tableList.length>0?
                                    tableList.map((item,index) => {
                                        return (
                                            <tr key={index}>
                                                <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                <td>{item.paymentName}</td>
                                                <td>{item.iconUrl}</td>
                                                <td>{item.enable?'开启':'关闭'}</td>
                                                <td>
                                                    {limitBtn.indexOf('update')>-1?<a className="mar20" onClick ={()=>this.changeDetail(item)}>修改</a>:''}
                                                    {limitBtn.indexOf('delete')>-1?<a onClick ={()=>this.deleteItem(item.id)}>删除</a>:''}
                                                </td>
                                            </tr>
                                        )
                                    })
                                    :<tr className="no-record"><td colSpan="20">暂无数据</td></tr>
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
                                                showTotal={total => `总共 ${total} 条`}
                                                onChange={this.changPageNum}
                                                onShowSizeChange={this.onShowSizeChange}
                                                showSizeChanger
                                                showQuickJumper
                                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                defaultPageSize={PAGESIZE}
                                                />
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