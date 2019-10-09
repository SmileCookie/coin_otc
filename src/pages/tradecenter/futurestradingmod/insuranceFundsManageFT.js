import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,Select,Pagination,message,DatePicker,Modal,Table } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,PAGRSIZE_OPTIONS,PAGESIZE_50,TIMEFORMAT_ss,SHOW_TIME_DEFAULT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { toThousands } from '../../../utils'
import ModalInsuranceFundsManageFT from './modal/modalInsuranceFundsManageFT'
import HandicapMarket from '../../common/select/handicapMarketFT'
const { RangePicker } = DatePicker
const Option = Select.Option
const {Column} = Table

export default class InsuranceFundsManageFT extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            tableSource:[],
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            visible:false,
            modalHtml:'',
            title:'',
            width:'',
            time:[],
            userid:'',
            futuresid:'',
            businesstype:'',
            fundpriceS:'',
            fundpriceE:'',
            retainpriceS:'',
            retainpriceE:'',
            createtimeS:'',
            createtimeE:'',
            totalFundPrice:0,
            totalRetainPrice:0,
            totalSurplusclosefee:0,
            totalRetainPriceBalance:0,
            totalFundPriceBalance:0,

        }
    }
    componentDidMount(){
        this.requestTable()
    }
    componentWillUnmount(){

    }
    clickHide = () => {
        this.setState({
            showHide:!this.state.showHide
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    resetState = () => {
        this.setState({
            time:[],
            userid:'',
            futuresid:'',
            businesstype:'',
            fundpriceS:'',
            fundpriceE:'',
            retainpriceS:'',
            retainpriceE:'',
            createtimeS:'',
            createtimeE:'',
            // totalFundPrice:0,
            // totalRetainPrice:0,
            // totalSurplusclosefee:0,
        })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name] : value
        })
    }
    //交易类型
    selectBusiness = val => {
        this.setState({
            businesstype:val
        })
    }
    //期货市场
    handleSelectMarket = val => {
        this.setState({
            futuresid:val
        })
    }
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        // console.log(date, dateString);
        this.setState({
            createtimeS:dateString[0]&&moment(dateString[0]).format('x'),
            createtimeE:dateString[1]&&moment(dateString[1]).format('x'),
            time:date
        })
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize,pagination,userid,futuresid,businesstype,fundpriceS,fundpriceE,retainpriceS,retainpriceE,createtimeS,createtimeE, } = this.state
        axios.post(DOMAIN_VIP+'/insuranceFund/list',qs.stringify({
            userid,futuresid,businesstype,fundpriceS,fundpriceE,retainpriceS,retainpriceE,createtimeS,createtimeE,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.data.totalCount;
                pagination.onChange = this.onChangePageNum;
                pagination.onShowSizeChange = this.onShowSizeChange
                this.setState({
                    tableSource,
                    pagination,
                    totalFundPrice:result.totalFundPrice,
                    totalRetainPrice:result.totalRetainPrice,
                    totalSurplusclosefee:result.totalSurplusclosefee,
                    totalRetainPriceBalance:result.totalRetainPriceBalance,
                    totalFundPriceBalance:result.totalFundPriceBalance
                })
            }else{
                message.warning(result.msg);
            }
        })
    }
    onChangePageNum = (pageIndex,pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    checkDetail = item => {
        this.footer= [
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]
        this.setState({
            visible:true,
            title:"详情",
            width:'1200px',
            modalHtml:<ModalInsuranceFundsManageFT item={item} />
        })
    }
    sorter = (pagination, filters, sorter) =>{
        // console.log(pagination, filters, sorter&&sorter.order&&sorter.order.slice(0,3)?sorter.order.slice(0,3):'')
    }
    render(){
        const { showHide,tableSource,pagination,time,visible,modalHtml,width,title,userid,futuresid,businesstype,fundpriceS,fundpriceE,retainpriceS,retainpriceE,totalFundPrice,totalRetainPrice,totalSurplusclosefee,totalRetainPriceBalance,totalFundPriceBalance } = this.state
        return(
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 数据中心 > 期货交易中心 > 保险基金管理
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <HandicapMarket marketType={futuresid} handleChange={this.handleSelectMarket} />
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className='form-group'>
                                        <label className="col-sm-3 control-label">用户编号:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userid" value={userid} onChange={this.handleInputChange}/>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名:</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name='' value={''} onChange={this.handleInputChange}/>
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">业务类型:</label>
                                        <div className="col-sm-8">
                                            <Select style={{width:SELECTWIDTH}} value={businesstype} onChange={this.selectBusiness}>
                                                <Option key='' value={''}>请选择</Option>
                                                <Option key='1' value={1}>强平增加</Option>
                                                <Option key='2' value={2}>穿仓减少</Option>
                                                {/* <Option key='2' value={2}>收益转出</Option> */}
                                                {/* <Option key='3' value={3}>佣金结余</Option> */}
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保险基金头寸:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text"  className="form-control" name='fundpriceS' value={fundpriceS} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='fundpriceE' value={fundpriceE} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">保险留存头寸:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text"  className="form-control" name='retainpriceS' value={retainpriceS} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='retainpriceE' value={retainpriceE} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">账户余额:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='' value={''} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='' value={''} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">时间刷选:</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue:SHOW_TIME_DEFAULT
                                                }}
                                                placeholder={['Start Time', 'End Time']}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onChangeCheckTime}
                                                value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table style={{margin:'0'}} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                                <tr className="headings">
                                                    <th style={{textAlign:'left'}} colSpan="17" className="column-title">
                                                    保险基金头寸发生额：{toThousands(totalFundPrice)}，
                                                    保险留存头寸发生额：{toThousands(totalRetainPrice)}，
                                                    保险佣金结余：{toThousands(totalSurplusclosefee)}，
                                                    保险基金余额：{toThousands(totalFundPriceBalance)}，
                                                    保险留存余额：{toThousands(totalRetainPriceBalance)}
                                                    </th>
                                                </tr>
                                            </thead>
                                    </table>
                                    <Table dataSource={tableSource} bordered pagination={pagination} onChange={this.sorter} locale={{emptyText:'暂无数据'}} >
                                        <Column title='序号'  dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )}/>
                                        <Column title='期货市场'  dataIndex='futuresid' key='futuresid'/>
                                        <Column title='用户编号' dataIndex='userid' key='userid'/>
                                        <Column title='业务编号' dataIndex='id' key='id'/>
                                        <Column title='业务类型' dataIndex='businesstype' key='businesstype' render={(text)=>{
                                            switch(text){
                                                case 1:
                                                    return '强平增加';
                                                case 2:
                                                    return '穿仓减少';
                                                default:
                                                    return ''
                                            }
                                        }}/>
                                        <Column title='保险基金系数' sorter dataIndex='insurancefundrate' key='insurancefundrate'/>
                                        <Column className='moneyGreen' title='保险基金头寸' sorter dataIndex='fundPrice' key='fundPrice' render={(text)=>toThousands(text,true)}/>
                                        <Column className='moneyGreen' title='保险留存头寸' sorter dataIndex='retainPrice' key='retainPrice' render={(text)=>toThousands(text,true)}/>
                                        <Column className='moneyGreen' title='保险佣金' sorter dataIndex='surplusclosefee' key='surplusclosefee' render={(text)=>toThousands(text,true)}/>
                                        <Column title='时间'  dataIndex='createtime' key='createtime' render={(text)=>text?moment(text).format(TIMEFORMAT_ss):'--'}/>
                                        <Column title='操作'  dataIndex='action' key='action' render={(text,record) => (
                                            <a href='javascript:void(0);' onClick={()=>this.checkDetail(record)}>一键追踪</a>
                                        )}/>
                                    </Table>                                                                        
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    width={width}
                    title={title}
                    onOk={this.handleOk}
                    footer={this.footer}
                    onCancel={this.handleCancel}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}