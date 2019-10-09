import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
// import SelectAType from '../../select/selectAType'
import ModalqueryDetail from './modal/modalqueryDetail'
import moment from 'moment'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT,PAGESIZE_200, PAGRSIZE_OPTIONS,TIMEFORMAT_ss} from '../../../conf'
import { Button,DatePicker,Tabs,Pagination,Modal,Select,message } from 'antd'
import { toThousands,pageLimit } from '../../../utils'
import MarketList from '../select/marketList'
import SelectUserTypeList from '../../common/select/selectUserTypeList'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const TabPane = Tabs.TabPane;
const Option = Select.Option;

export default class EntrustRecord extends React.Component{

    constructor(props) {
        super(props)
        this.state = {
            market:'etc_btc',
            userName:'',
            userId:'',
            type:'',
            status:'',
            beginTime:'',
            endTime:'',
            time:[],
            moneyMin:'',
            moneyMax:'',
            numbersMin:'',
            numbersMax:'',
            totalMoneyMin:'',
            totalMoneyMax:'',
            entrustId:'',
            triggerPriceBegin:'',
            triggerPriceEnd:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE_200,
            visible:false,
            title:'',
            modalHtml:'',
            tableList:'',
            // pageTotal:0,
            showHide:true,
            limitBtn: [],
            // numbersSum:'',
            // totalMoneySum:'',
            // completeNumberSum:'',
            // completeTotalMoneySum:'',
            numbersSumNow:'',
            totalMoneySumNow:'',
            completeNumberSumNow:'',
            completeTotalMoneySumNow:'',
            numbersSumAgo:'',
            totalMoneySumAgo:'',
            completeNumberSumAgo:'',
            completeTotalMoneySumAgo:'',
            tableName:1,
            tableListAgo:[],
            tableListNow:[],
            pageTotalAgo:0,
            pageTotalNow:0,
            accountType:'2',
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        // this.onChangeEntrustTime = this.onChangeEntrustTime.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleOk = this.handleOk.bind(this)
        this.showModal = this.showModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.handleTypeChange = this.handleTypeChange.bind(this)
        this.handleStatusChange = this.handleStatusChange.bind(this)
        this.showDetail = this.showDetail.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
    }

    componentDidMount(){
        this.setState({
            limitBtn: pageLimit('entrustRecord', this.props.permissList)
        })
    }
    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
        
    }

    //弹窗 ok 
    handleOk(){
        this.setState({ loading: true });
        setTimeout(() => {
          this.setState({ 
              loading: false, 
              visible: false 
          });
        }, 3000);
    }
    //弹窗显示
    showModal(){
        this.setState({
          visible: true,
        });
    }
     //select 变化时
     handleChange(value){
        this.setState({
            market:value
        })
    }
    //select变化时
    handleTypeChange(value){
        this.setState({
            type:value
        })
    }
    //select变化时
    handleStatusChange(value){
        this.setState({
            status:value
        })
    }
    //弹窗隐藏
    handleCancel(){
        this.setState({ 
            visible: false,
            vipRate:'',
            jifen:'',
            discount:'',
            memo:''
        });
    }
       //明细
       showDetail(item){
           const{market} = this.state
        this.footer=[
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]
        this.setState({
            visible:true,
            title:'委托记录成交明细',
            modalHtml:<ModalqueryDetail enId= {item.entrustid} enMarket = {market}/>
            // <ModalGoogle item={item} tab={this.state.tab} onModalRadio={this.onModalRadio} handleInputChange={this.handleInputChange} />
           
        })
        // console.log(item.market)
    }
    
    requestTable(currIndex,currSize){
        const {market,userName, userId,type,status,beginTime, endTime, moneyMin,moneyMax,numbersMin,numbersMax,totalMoneyMin,totalMoneyMax,entrustId,pageIndex,pageSize,pageTotal,tableName,accountType} = this.state
        axios.post(DOMAIN_VIP+'/entrustRecord/query',qs.stringify({
            market,userName, userId,type,status,beginTime, endTime:Number(endTime)+999, moneyMin,moneyMax,numbersMin,numbersMax,totalMoneyMin,totalMoneyMax,entrustId,tableName,accountType,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                if(tableName==1){
                    this.setState({
                        tableListNow:result.data.list,
                        pageTotalNow:result.data.totalCount,
                        numbersSumNow:result.data.list[0]&&result.data.list[0].numbersSum,
                        totalMoneySumNow:result.data.list[0]&&result.data.list[0].totalMoneySum,
                        completeNumberSumNow:result.data.list[0]&&result.data.list[0].completeNumberSum,
                        completeTotalMoneySumNow:result.data.list[0]&&result.data.list[0].completeTotalMoneySum
                    })
                }else{
                    this.setState({
                        tableListAgo:result.data.list,
                        pageTotalAgo:result.data.totalCount,
                        numbersSumAgo:result.data.list[0]&&result.data.list[0].numbersSum,
                        totalMoneySumAgo:result.data.list[0]&&result.data.list[0].totalMoneySum,
                        completeNumberSumAgo:result.data.list[0]&&result.data.list[0].completeNumberSum,
                        completeTotalMoneySumAgo:result.data.list[0]&&result.data.list[0].completeTotalMoneySum
                    })
                }
                // this.setState({
                //     tableList:result.data.list,
                //     pageTotal:result.data.totalCount,
                //     numbersSum:result.data.list[0]&&result.data.list[0].numbersSum,
                //     totalMoneySum:result.data.list[0]&&result.data.list[0].totalMoneySum,
                //     completeNumberSum:result.data.list[0]&&result.data.list[0].completeNumberSum,
                //     completeTotalMoneySum:result.data.list[0]&&result.data.list[0].completeTotalMoneySum
                // })
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

    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            beginTime:dateString[0]?moment((dateString[0])).format('x'):'',
            endTime:dateString[1]?moment((dateString[1])).format('x'):'',
            time:date
        })
    }



    //重置
    onResetState(){
        this.setState({
            market:'etc_btc',
            userName:'',
            userId:'',
            type:'',
            status:'',
            beginTime:'',
            endTime:'',
            time:[],
            moneyMin:'',
            moneyMax:'',
            numbersMin:'',
            numbersMax:'',
            totalMoneyMin:'',
            totalMoneyMax:'',
            entrustId:'',
            accountType:'2'
        })
    }

    //点击收起
    clickHide(){
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    //tabs 回调
    callbackTabs= key =>{
        const { pageIndex , pageSize } = this.state
        this.setState({
            tableName:key,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE_200
        })
        // this.requestTable(pageIndex,pageSize,this.props.curId,key)
    }
    selectUser = user => {
        this.setState({
            accountType:user
        })
    }
    judgeAccoutType = ({typeall,userid}) => {
        return typeall.split(',').map(Number).includes(userid) ? '刷量账户' : '用户账户'
    }
    render(){
        const {market,showHide,userName,userId,type,status,beginTime, endTime,time, moneyMin,moneyMax,numbersMin,numbersMax,totalMoneyMin,totalMoneyMax,entrustId,triggerPriceBegin,triggerPriceEnd,pageIndex,pageSize,visible,title,modalHtml,limitBtn,
            // numbersSum,totalMoneySum,completeNumberSum,completeTotalMoneySum 
            numbersSumAgo,numbersSumNow,totalMoneySumAgo,totalMoneySumNow,completeNumberSumAgo,completeNumberSumNow,completeTotalMoneySumAgo,completeTotalMoneySumNow,tableName,pageTotalAgo,pageTotalNow,tableListAgo,tableListNow,accountType
        } = this.state
            let numbersSum = tableName ==1 ?numbersSumNow:numbersSumAgo;
            let totalMoneySum = tableName ==1 ?totalMoneySumNow:totalMoneySumAgo;
            let completeNumberSum = tableName ==1 ?completeNumberSumNow:completeNumberSumAgo;
            let completeTotalMoneySum = tableName ==1 ?completeTotalMoneySumNow:completeTotalMoneySumAgo;
            let pageTotal = tableName ==1 ?pageTotalNow:pageTotalAgo;
            let tableList = tableName ==1 ?tableListNow:tableListAgo;
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 盘口管理 > 币币委托记录
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>                    
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <MarketList market={market} col='3' handleChange={this.handleChange}></MarketList>
                                </div>
                                <SelectUserTypeList value={accountType} handleChange={this.selectUser} />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-9">
                                            <input type="text" className="form-control"  name="userId" value={userId} onChange={this.handleInputChange} />
                                            
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-9">
                                            <input type="text" className="form-control"  name="userName" value={userName} onChange={this.handleInputChange} /><b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托类型：</label>
                                        <div className="col-sm-9">
                                        <Select value={type} style={{ width: SELECTWIDTH }} onChange={this.handleTypeChange} >
                                                <Option  value=''>请选择</Option>
                                                <Option  value='1'>买入</Option>
                                                <Option  value='0'>卖出</Option>
                                                {/* <Option  value='-1'>取消</Option> */}
                                        </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">处理状态：</label>
                                        <div className="col-sm-8">
                                        <Select value={status} style={{ width: SELECTWIDTH }} onChange={this.handleStatusChange} >
                                                <Option value=''>请选择</Option>
                                                <Option value='0'>待成交</Option>
                                                <Option value='2'>已成交</Option>
                                                <Option value='3'>部分成交</Option>
                                                <Option value='1'>取消</Option>
                                        </Select>
                                        </div>
                                    </div>
                                </div>
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托单价：</label>
                                        <div className="col-sm-8 ">
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="moneyMin" value={moneyMin} onChange={this.handleInputChange} /></div>
                                         <div className="left line34">-</div>
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="moneyMax" value={moneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托数量：</label>
                                        <div className="col-sm-8 ">
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="numbersMin" value={numbersMin} onChange={this.handleInputChange} /></div>
                                         <div className="left line34">-</div>
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="numbersMax" value={numbersMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托总金额：</label>
                                        <div className="col-sm-8 ">
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="totalMoneyMin" value={totalMoneyMin} onChange={this.handleInputChange} /></div>
                                         <div className="left line34">-</div>
                                         <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="totalMoneyMax" value={totalMoneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托编号：</label>
                                        <div className="col-sm-9">
                                                 <input type="text" className="form-control"  name="entrustId" value={entrustId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">委托时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker 
                                              showTime={{
                                                defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                            }}
                                            format={TIMEFORMAT_ss}
                                              placeholder={['Start Time', 'End Time']}
                                             onChange={this.onChangeCheckTime }
                                             value={time}
                                             />
                                        </div>
                                    </div>
                                </div>
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
                                <Tabs onChange={this.callbackTabs}>
                                    <TabPane tab="最近3天委托" key="1"></TabPane>
                                    <TabPane tab="3天前委托" key="2"></TabPane>
                                </Tabs>
                                <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{textAlign:'left'}} colSpan='14'>委托数量：{toThousands(numbersSum)},委托总金额：{toThousands(totalMoneySum)}，已成交数量：{toThousands(completeNumberSum)},已成交金额:{toThousands(completeTotalMoneySum)} </th> 
                                            </tr>
                                        </thead>
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">委托编号</th>
                                                <th className="column-title">账户类型</th>
                                                <th className="column-title">委托用户编号</th>
                                                <th className="column-title">委托类型</th>
                                                <th className="column-title">委托单价</th>
                                                <th className="column-title">委托数量</th>
                                                <th className="column-title">委托总金额</th>  
                                                <th className="column-title">已成交数量</th>
                                                <th className="column-title">已成交金额</th>
                                                <th className="column-title">处理状态</th> 
                                                <th className="column-title">委托来源</th>  
                                                <th className="column-title">委托时间</th>
                                                <th className="column-title">操作</th>                     
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        
                                                         <tr style={item.userType?{color:'#1A90FF'}:{color:''}} key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.entrustid}</td>
                                                            <td>{this.judgeAccoutType(item)}</td>
                                                            <td>{item.userid}</td>
                                                            <td>{
                                                                // item.types == 1?"买入":item.types==0?"卖出":"取消"
                                                                item.types==1?'买入':'卖出'
                                                                }
                                                            </td>
                                                            <td className='moneyGreen'>{item.unitprice?toThousands(item.unitprice):''}</td>
                                                            <td>{item.numbers}</td>
                                                            <td className='moneyGreen'>{item.totalmoney?toThousands(item.totalmoney):''}</td>
                                                            <td>{item.completenumber}</td>
                                                            <td className='moneyGreen'>{item.completetotalmoney?toThousands(item.completetotalmoney):''}</td>
                                                            <td>{item.statusName}</td>
                                                            <td>{item.webName}</td>
                                                            <td>{moment(item.submittime).format(TIMEFORMAT)}</td>
                                                            <td>
                                                                {limitBtn.indexOf('queryDetail')>-1?<a href="javascript:void(0)" onClick={() => this.showDetail(item)}>明细</a>:''}                                                 
                                                            </td> 
                                                        </tr>
                                                    )
                                                }):
                                                <tr className="no-record"><td colSpan="14">暂无数据</td></tr>
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
                                                defaultPageSize={PAGESIZE_200}
                                                onShowSizeChange={this.onShowSizeChange}
                                                showSizeChanger
                                                pageSizeOptions={PAGRSIZE_OPTIONS}
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
                    width="1200px"
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {modalHtml}            
                </Modal>
            </div>
        )
    }

}






























































