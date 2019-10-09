import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { toThousands,tableScroll } from '../../../utils'
import ModalwallteBill from './modal/modalwallteBill'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT,PAGRSIZE_OPTIONS20,URLS} from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Select ,Modal} from 'antd'
import SelectBType from '../select/selectBType'
import FundsTypeList from '../../common/select/fundsTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const { COMMON_QUERYATTRUSDTE } = URLS
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class WalletBill extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            fundsType:"0",
            walletType:"",
            // dealType:"0",
            confirmStart:"",
            confirmEnd:"",
            time:[],            
            heightStart:"",
            heightEnd:"",
            walletId:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            tableList:[],
            visible:false,
            modalHtml:'',
            title:'',
            width:'',
            height:0,
            tableScroll:{
                tableId:'WALTBILAA',
                x_panelId:'WALTBILAAX',
                defaultHeight:500,
                height:0,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleChangewalletType = this.handleChangewalletType.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChangedealType = this.handleChangedealType.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.showDetail = this.showDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
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
    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())    
    }
     //资金类型 select
     handleChangeType(val){
        this.setState({
            fundsType:val
        })
    }
    //
    showDetail(id){
        let maxWidth = window.screen.width > 1500?"1700px":'1300px'
        this.footer= [
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]
        
        this.setState({
            visible:true,
            title:"钱包流水明细",
            width:maxWidth,
            modalHtml:<ModalwallteBill id={id}/>
        })
    }
    handleCancel(){
        this.setState({
            visible:false
        })
    }
     handleChangewalletType(val){
        this.setState({
            walletType:val
        })
    }
    handleChangedealType(val){
        this.setState({
            dealType:val
        })
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            confirmStart:dateString[0],
            confirmEnd:dateString[1],
            time:date
        })
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
            // }))
    }
    requestTable(currIndex,currSize){
        const { fundsType,walletType,confirmStart,confirmEnd,heightEnd,heightStart,walletId,pageIndex,pageSize} = this.state
        axios.post(DOMAIN_VIP+'/walletBill/queryList',qs.stringify({
            fundsType,walletType,confirmStart,confirmEnd,heightEnd,heightStart,walletId,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }   
        })
    }
    onResetState(){
        this.setState({
            fundsType:'0',
            dealType:'0',
            walletType:'',
            heightStart:'',
            heightEnd:'',
            time:[],
            confirmStart:"",
            confirmEnd:"",
            walletId:''
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

    render(){
        const {showHide,accountType,fundsType,time,walletType,dealType,confirmStart,confirmEnd,heightEnd,heightStart,walletId,pageIndex,pageSize,pageTotal,tableList,visible,title,modalHtml,width}=this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 支付中心对账 > 钱包流水
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList url={COMMON_QUERYATTRUSDTE} col='3' fundsType={fundsType} handleChange={this.handleChangeType}></FundsTypeList>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">钱包类型：</label>
                                        <div className="col-sm-9">
                                        <Select value={walletType}  style={{ width: SELECTWIDTH }} onChange={this.handleChangewalletType} >
                                            <Option value=''>请选择</Option>
                                           <Option value='1'>热充钱包</Option>
                                           <Option value='2'>冷钱包</Option>
                                           <Option value='3'>热提钱包</Option>
                                        </Select>
                                            
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <SelectBType col='3' paymod="1" billType={dealType} handleChange={this.handleChangedealType}></SelectBType>
                                </div> */}
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">区块高度：</label>
                                        <div className="col-sm-8 ">
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="heightStart" value={heightStart} onChange={this.handleInputChange} /></div>
                                         <div className="left line34">-</div>
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="heightEnd" value={heightEnd} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">确认时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker 
                                        showTime={{
                                            defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                          }}
                                        format="YYYY-MM-DD HH:mm:ss"
                                        placeholder={['Start Time', 'End Time']}
                                       onChange={this.onChangeCheckTime }
                                       value={time}
                                       />
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">钱包编号：</label>
                                        <div className="col-sm-8 ">
                                         <input type="text" className="form-control" name="walletId" value={walletId} onChange={this.handleInputChange} />
                                         <b className="icon-fuzzy">（模糊匹配）</b>
                                        </div>
                                    </div>
                                </div> */}
                               
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.inquireBtn()}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                         }
                            <div className="x_panel">
                                <div className="x_content">
                                    <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                        <table className="table table-striped jambo_table bulk_action  table-linehei table_scroll">
                                            <thead>
                                                <tr className="headings">
                                                    <th className="column-title">序号</th>
                                                    <th className="column-title wid300 must_153px">交易流水号</th>
                                                    <th className="column-title">资金类型</th>
                                                    <th className="column-title">发送方</th>  
                                                    <th className="column-title">接收方</th>
                                                    <th className="column-title">交易金额</th>
                                                    <th className="column-title">网络费</th> 
                                                    <th className="column-title">交易类型</th>               
                                                    <th className="column-title">区块高度</th>
                                                    <th className="column-title">确认时间</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td> <a href="javascript:void(0)" onClick={() => this.showDetail(item.txId)}>{item.txId}</a></td>
                                                            <td>{item.fundsType}</td>
                                                            <td>{item.sendWallet}</td>
                                                            <td>{item.receiveWallet}</td>
                                                            <td className='moneyGreen'>{toThousands(item.txAmount,true)}</td>
                                                            <td className='moneyGreen'>{toThousands(item.fee,true)}</td>
                                                            <td>{item.dealType}</td>
                                                            <td>{item.blockHeight}</td>                                                            
                                                            <td>{moment(item.configTime).format(TIMEFORMAT)}</td>
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
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
                                                    pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                    defaultPageSize={PAGESIZE}
                                                    showSizeChanger
                                                    showQuickJumper />
                                    }
                                </div>
                                </div>
                            </div>

                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 60 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}            
                </Modal>
            </div>
        )
        
    }
}