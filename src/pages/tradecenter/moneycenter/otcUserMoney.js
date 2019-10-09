/**数据中心 》 资金中心 》 用户资金 》 otc用户资金  */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { PAGEINDEX, DOMAIN_VIP, SELECTWIDTH,PAGESIZE,PAGRSIZE_OPTIONS,URLS } from '../../../conf'
import { Button, Select, Table,message,Modal } from 'antd'
import { toThousands,pageLimit } from '../../../utils';
import FundsTypeList from '../../common/select/fundsTypeList'
import ModalTransfer from './modal/modaltransfer'
import GoogleCode from '../../common/modal/googleCode'
import { AsyncSelect } from '../../../components/select/asyncSelect'
const Big = require('big.js')
const Option = Select.Option;
const { Column } = Table;
const { COMMON_GETUSERTYPE } = URLS

export default class OtcUserMoney extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            fundsType: "2",
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: '50',
            pageTotal: 0,
            begin: "",
            end: "",
            time: [],
            userId: "",
            userName: "",
            sortType: "",
            puobalancesum: 0,
            tradingFrozen: "",
            puofrozenfeesum: "",
            totalMoney: "",
            moneyMin: '',
            moneyMax: '',
            frozenFeeMin: '',
            frozenFeeMax: '',
            frozenTradeMin: '',
            frozenTradeMax: '',
            totalMoneyMin: '',
            totalMoneyMax: '',
            puofrozenwithdrawsum:0,
            tableDataInterface: DOMAIN_VIP + "/otc/query",
            summaryDataInterface: DOMAIN_VIP + "/otc/sum",
            money:'',
            modalWidth:'',
            modalTitle:'',
            visible:false,
            modalHtml:'',
            limitBtn:[],
            accountType:'0',//用户类型
        }
        this.clickHide = this.clickHide.bind(this);
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.requestTable = this.requestTable.bind(this);
        this.handleChangeType = this.handleChangeType.bind(this);
        this.onResetState = this.onResetState.bind(this);
        this.changPageNum = this.changPageNum.bind(this);
        this.onShowSizeChange = this.onShowSizeChange.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.requestSort = this.requestSort.bind(this);
        this.handleChangeTable = this.handleChangeTable.bind(this);
    }
    componentDidMount() {
        this.requestTable()
        this.setState({
            limitBtn:pageLimit('walletUserCapital',this.props.permissList)
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    handleChangeTable(pagination, filters, sorter) {

        console.log(sorter);
        // sorter.field
        // sorter.order
        // this.setState({
        //     sortType: sorter.order
        // }, () => this.requestTable());
    }
    handleInputChange(e) {
        const target = e.target;
        const value = target.value;
        const name = target.name
        let json = new Object();
        json[name] = value;
        this.setState(json);
    }
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            begin: dateString[0],
            end: dateString[1],
            time: date
        })
    }
    //点击分页
    changPageNum(page, pageSize) {
        this.setState({
            pageIndex: page
        }, () => this.requestTable(page,pageSize))

    }
    //分页的 pagesize 改变时
    onShowSizeChange(current, size) {
        this.setState({
            pageIndex: current,
            pageSize: size
        }, () => this.requestTable(current,size))
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    handleChangeType(value) {
        this.setState({
            fundsType: value
        })
    }
    //划转资金弹窗
    coinTransferModal = (item,type) => {
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more"  onClick={() => this.modalGoogleCode(item, type,'check')}>
                确认
            </Button>,
        ]
        this.setState({
            visible: true,
            modalWidth: '900px',
            modalTitle: '划转资金',
            money:'',
            modalHtml: <ModalTransfer handleInputChange={this.handleInputChange} from='法币账户' to='我的钱包' handleSelectChange = {this.handleSelectChange} item={item}/>
        })
    }
    handleCancel = () => {
        this.setState({visible:false})
    }
    //划转资金
    Transfer = (item) => {
        const { cointypeid, userid,  } = item
        const {money} = this.state
        let self = this
        axios.post(DOMAIN_VIP + '/walletUserCapital/transfer', qs.stringify({
            fundsType: cointypeid,
            from:'3',
            to: '1',
            userId: userid,
            amount: money,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                message.success(result.msg)
                this.setState({
                    visible: false,
                    money: '',
                })
                this.requestTable()
            } else {
                message.warning(result.msg)
            }
        })
    }
    onResetState() {
        this.setState({
            fundsType: "2",
            time: [],
            userId: "",
            userName: "",
            moneyMin: '',
            moneyMax: '',
            totalMoneyMin: '',
            totalMoneyMax: '',
            frozenFeeMin: '',
            frozenFeeMax: '',
            frozenTradeMin: '',
            frozenTradeMax: '',
            accountType:'0'
        })
    }
    requestSort(type) {
        this.setState({ sortType: type }, () => this.requestTable())
    }
    requestTable(currIndex, currSize) {
        const {
            summaryDataInterface,
            tableDataInterface,
            fundsType,
            pageIndex,
            pageSize,
            userId,
            moneyMin,
            moneyMax,
            totalMoneyMin,
            totalMoneyMax,
            frozenFeeMin,
            frozenFeeMax,
            frozenTradeMin,
            frozenTradeMax,
            userName,
            accountType
        } = this.state
        const parameter = {
            userId,
            userName,
            coinTypeId: fundsType,
            moneyMin,
            moneyMax,
            totalMoneyMin,
            totalMoneyMax,
            frozenFeeMin,
            frozenFeeMax,
            frozenTradeMin,
            frozenTradeMax,
            accountType,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }

        axios.post(tableDataInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {

                Big.RM = 0;
                let tableList = result.data.list;

                tableList.map((item, index) => {
                    item.index = (result.data.currPage - 1) * result.data.pageSize + index + 1;
                    item.key = item.id;
                    item.totalAmount = new Big(item.balance).plus(item.frozenwithdraw).plus(item.frozenfee).plus(item.frozentrade)
                })
                this.setState({
                    tableList,
                    pageSize: result.data.pageSize,
                    pageTotal: result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
        axios.post(summaryDataInterface, qs.stringify(parameter)).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.setState({
                    puobalancesum:result.data[0]&&result.data[0].puobalancesum,
                    puofrozenfeesum:result.data[0]&&result.data[0].puofrozenfeesum,
                    puofrozentradesum:result.data[0]&&result.data[0].puofrozentradesum,
                    puofrozenwithdrawsum:result.data[0]&&result.data[0].puofrozenwithdrawsum,
                    totalMoney: result.data[0]&&result.data[0].allsum,

                })
            }else{
                message.warning(result.msg)
            }
        })


    }
    // //google 验证弹窗
    modalGoogleCode = (item,type,check) => {
        this.setState({
            googVisibal:true,
            item,
            type,
            check,
        })
    }

    //google 按钮
    modalGoogleCodeBtn = (value) => {
        const { item,type,check} = this.state
        const {googleCode,checkGoogle} = value
        let url =  check ?"/common/checkTwoGoogleCode":"/common/checkGoogleCode"
        axios.post(DOMAIN_VIP+url,qs.stringify({
            googleCode,checkGoogle
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if (type == "tansfer") {
                    this.Transfer(item)
                }
                this.setState({
                    googVisibal: false
                })
                
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCreate = () => {
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.modalGoogleCodeBtn(values)
        });
      }
      saveFormRef = (formRef) => {
        this.formRef = formRef;
      }
      //用户类型
    selectUserType = (v,k) => {
        this.setState({[k]: v})
    }
    render() {
        Big.RM = 0;
        const {
            puobalancesum,
            showHide,
            totalMoney,
            puofrozentradesum,
            puofrozenfeesum,
            fundsType,
            tableList,
            pageIndex,
            pageSize,
            pageTotal,
            userId,
            userName,
            moneyMin,
            moneyMax,
            frozenFeeMin,
            frozenFeeMax,
            frozenTradeMax,
            frozenTradeMin,
            totalMoneyMin,
            totalMoneyMax,
            puofrozenwithdrawsum,

            modalHtml,modalTitle,modalWidth,visible,limitBtn,
            accountType
        } = this.state

        return (
            <div className="right-con">
                <div className="page-title">
                当前位置：财务中心 > 用户资金 > 法币用户资金
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <AsyncSelect title='用户类型' paymod url={COMMON_GETUSERTYPE} value={accountType} onSelectChoose={v => this.selectUserType(v,'accountType')} />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList  fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8 ">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">总金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="totalMoneyMin" value={totalMoneyMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="totalMoneyMax" value={totalMoneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">可用金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="moneyMin" value={moneyMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="moneyMax" value={moneyMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易冻结：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="frozenTradeMin" value={frozenTradeMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="frozenTradeMax" value={frozenTradeMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">广告冻结：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="frozenFeeMin" value={frozenFeeMin} onChange={this.handleInputChange} /></div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="frozenFeeMax" value={frozenFeeMax} onChange={this.handleInputChange} /></div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }

                        <div className="x_panel">
                            <div className="x_content">


                                <div className="table-responsive table-box">
                                    <table style={{ margin: '0' }} className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th style={{ textAlign: 'left' }} colSpan="17" className="column-title">
                                                    总资金：{toThousands(totalMoney,true)} &nbsp;&nbsp;&nbsp;
                                                    交易冻结：{toThousands(puofrozentradesum,true)} &nbsp;&nbsp;&nbsp;
                                                    可用余额：{toThousands(puobalancesum,true)} &nbsp;&nbsp;&nbsp;
                                                    广告冻结：{toThousands(puofrozenfeesum,true)}&nbsp;&nbsp;&nbsp;
                                                    提现冻结：{toThousands(puofrozenwithdrawsum,true)}
                                                </th>
                                            </tr>
                                        </thead>
                                    </table>

                                    <Table
                                        dataSource={tableList}
                                        bordered={true}
                                        onChange={this.handleChangeTable}
                                        locale={{emptyText:'暂无数据'}}
                                        // scroll={pageSize > 10 ? { y: 500 } : {}}
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.changPageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            showSizeChanger: true,
                                            showQuickJumper: true,
                                            pageSizeOptions:PAGRSIZE_OPTIONS
                                        }} >

                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='资金类型' dataIndex='coinTypeName' key='fundstypeName' />
                                        <Column title='用户编号' dataIndex='userid' key='userId' />
                                        <Column title='用户类型' dataIndex='accountTypeName' key='accountTypeName' />
                                        <Column title='总金额' dataIndex='totalAmount' key='totalAmount' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='可用余额' dataIndex='balance' key='balance' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='交易冻结' dataIndex='frozentrade' key='frozentrade' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='广告冻结' dataIndex='frozenfee' key='frozenfee' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='提现冻结' dataIndex='frozenwithdraw' key='frozenwithdraw' sorter="true" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title='操作' dataIndex='op' key='op' render={(text,record)=>{
                                            return (<div>
                                                {limitBtn.indexOf('transfer')>-1?<a className="mar10" href="javascript:void(0)" onClick={() => this.coinTransferModal(record,'tansfer')}>划转资金</a>:''}
                                                {/* <a className="mar10" href="javascript:void(0)" onClick={() => this.coinChargeModal(record,'doCharge')}>充{record.fundstypename}</a>
                                                {limitBtn.indexOf('doCharge')>-1?<a className="mar10" href="javascript:void(0)" onClick={() => this.coinChargeModal(record,'doCharge')}>充{record.fundstypename}</a>:''}
                                            {limitBtn.indexOf('doDeduction')>-1?<a className="mar10" href="javascript:void(0)" onClick={() => this.coinDeductModal(record,'doDeduction')}>扣{record.fundstypename}</a>:''}
                                            {
                                               limitBtn.indexOf('doFreez')>-1?(record.balance>0&&<a className="mar10" href="javascript:void(0)" onClick={()=>this.coinFreezeModal(record,'doFreez')}>冻结可用资金</a>):''
                                             }
                                            {
                                               limitBtn.indexOf('unFreez')>-1?(record.freez>0&&<a className="mar10" href="javascript:void(0)" onClick={()=>this.coinUnfreezeModal(record,'unFreez')}>解冻冻结资金</a>):''
                                             } */}
                                            </div>)
                                        }} />
                                    </Table>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={modalTitle}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width={modalWidth}
                >
                    {modalHtml}
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='OTCUMY'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                />
            </div>
        )

    }
}