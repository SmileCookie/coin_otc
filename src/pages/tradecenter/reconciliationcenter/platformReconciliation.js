/**交易平台对账 */
import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, NUMBERPOINT, SELECTWIDTH,TIMEFORMAT_ss ,PAGRSIZE_OPTIONS} from '../../../conf'
import { Button, DatePicker, Select, Table,Modal,message } from 'antd'
import { toThousands,pageLimit } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList'
import SelectState from '../../../components/select/selectState'
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;
const Big = require('big.js');
const Option = Select.Option;
const { Column } = Table;

export default class PlatformReconciliation extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            fundsType: "0",
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: '50',
            pageTotal: 0,
            begin: "",
            end: "",
            time: null,
            state:''
        }
        this.clickHide = this.clickHide.bind(this);
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this);
        this.requestTable = this.requestTable.bind(this);
        this.handleChangeType = this.handleChangeType.bind(this);
        this.onResetState = this.onResetState.bind(this);
        this.changPageNum = this.changPageNum.bind(this);
        this.onShowSizeChange = this.onShowSizeChange.bind(this);
    }
    componentDidMount() {
        this.setState({
            limitBtn: pageLimit('checkreconciliation', this.props.permissList)
        })
        this.requestTable()
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
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
    onResetState() {
        this.setState({
            fundsType: "0",
            begin: "",
            end: "",
            time: null,
            state:''
        })
    }
    requestTable(currIndex, currSize) {
        const {state, fundsType, begin, end, pageIndex, pageSize } = this.state
        axios.post(DOMAIN_VIP + '/checkreconciliation/list', qs.stringify({
            reportdateStart:begin, state,
            reportdateEnd:end, 
            fundstype:fundsType,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                Big.RM = 0;
                let tableList = result.data.list;
                tableList.map((item, index) => {
                    item.index = (result.data.currPage - 1) * result.data.pageSize + index + 1;
                    item.key = item.id;
                    item.reportdate = moment(item.reportdate).format('YYYY-MM-DD');
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
    }
    changeState=(val)=>{
        this.setState({
            state:val
        })
    }
    updateStatus = id => {
        let self = this;
        Modal.confirm({
            title:'你确定要改变状态吗？',
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                axios.post(DOMAIN_VIP+'/checkreconciliation/update',qs.stringify({id})).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        self.requestTable()
                    } 
                })
            },
            onCancel(){
                console.log('Cancel')
            } 
        })
    }
    render() {
        Big.RM = 0;
        const { state,showHide, accountType, fundsType, tableList, time, pageIndex, pageSize, pageTotal, } = this.state

        let columns = []
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：对账中心 > 交易平台对账 > 交易平台对账
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">

                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FundsTypeList  fundsType={fundsType} handleChange={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">对账日期：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                placeholder={['Start Time', 'End Time']}
                                                onChange={this.onChangeCheckTime}
                                                value={time}
                                            />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <SelectState state={this.state.state} changeState={this.changeState} />
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
                                <div className="table-responsive">

                                    <Table
                                        dataSource={tableList}
                                        bordered={true}
                                        scroll={{x:3600}}
                                        locale={{emptyText:'暂无数据'}}
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
                                        }}>
                                        <Column title="序号" dataIndex="index" key="index"  />
                                        <Column title="资金类型" dataIndex="fundstypeName" key="fundstypeName"  />
                                        <Column title="钱包 → 币币(1)" dataIndex="wallettobibi" key="wallettobibi" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="币币 ← 钱包(2)" dataIndex="wallettobibiin" key="wallettobibiin" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        {/* <Column title="钱包 → 期货" dataIndex="wallettofutures" key="wallettofutures" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="期货 ← 钱包" dataIndex="wallettofuturesin" key="wallettofuturesin" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                         */}
                                        <Column title="钱包 → 法币(1)" dataIndex="wallettootc" key="wallettootc" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="法币 ← 钱包(2)" dataIndex="wallettootcin" key="wallettootcin" className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                        <Column title="币币 → 钱包(1)" dataIndex="bibitowallet" key="bibitowallet" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="钱包 ← 币币(2)" dataIndex="bibitowalletin" key="bibitowalletin" className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                        <Column title="法币 → 钱包(1)" dataIndex="otctowallet" key="otctowallet" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="钱包 ← 法币(2)" dataIndex="otctowalletin" key="otctowalletin" className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                        <Column title="币币 → 法币(1)" dataIndex="bibitootc" key="bibitootc" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="法币 ← 币币(2)" dataIndex="bibitootcin" key="bibitootcin" className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                        <Column title="法币 → 币币(1)" dataIndex="otctobibi" key="otctobibi" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="币币 ← 法币(2)" dataIndex="otctobibiin" key="otctobibiin" className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                        <Column title="钱包 → 理财(1)" dataIndex="wallettofinancial" key="wallettofinancial" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="理财 ← 钱包(2)" dataIndex="wallettofinancialin" key="wallettofinancialin" className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                        <Column title="理财 → 钱包(1)" dataIndex="financialtowallet" key="financialtowallet" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="钱包 ← 理财(2)" dataIndex="financialtowalletin" key="financialtowalletin" className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                        <Column title="币币 → 理财(1)" dataIndex="bibitofinancial" key="bibitofinancial" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="理财 ← 币币(2)" dataIndex="bibitofinancialin" key="bibitofinancialin" className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                        <Column title="理财 → 币币(1)" dataIndex="financialtobibi" key="financialtobibi" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="币币 ← 理财(2)" dataIndex="financialtobibiin" key="financialtobibiin" className="moneyGreen" render={(text)=>toThousands(text,true)} />

                                        <Column title="法币 → 理财(1)" dataIndex="otctofinancial" key="otctofinancial" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="理财 ← 法币(2)" dataIndex="otctofinancialin" key="otctofinancialin" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        
                                        <Column title="理财 → 法币(1)" dataIndex="financialtootc" key="financialtootc" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="法币 ← 理财(2)" dataIndex="financialtootcin" key="financialtootcin" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        
                                        {/* <Column title="期货 → 钱包" dataIndex="futurestowallet" key="futurestowallet" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="钱包 ← 期货" dataIndex="futurestowalletin" key="futurestowalletin" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="币币 → 期货" dataIndex="bibitofutures" key="bibitofutures" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="期货 ← 币币" dataIndex="bibitofuturesin" key="bibitofuturesin" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="期货 → 币币" dataIndex="futurestobibi" key="futurestobibi" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="币币 ← 期货" dataIndex="futurestobibiin" key="futurestobibiin" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="期货 → OTC" dataIndex="futurestootc" key="futurestootc" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="OTC  ← 期货" dataIndex="futurestootcin" key="futurestootcin" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="OTC  → 期货" dataIndex="otctofutures" key="otctofutures" className="moneyGreen" render={(text)=>toThousands(text,true)} />
                                        <Column title="期货 ← OTC" dataIndex="otctofuturesin" key="otctofuturesin" className="moneyGreen" render={(text)=>toThousands(text,true)} /> */}
                                        <Column title="对账日期" dataIndex="reportdate" key="reportdate" />
                                        <Column title="状态" dataIndex="state" key="sss" render={(state,record) => state == 0 ? '正常' : <a href='javascript:void(0);' onClick={()=>this.updateStatus(record.id)} >异常</a>} />
                                    </Table>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        )

    }
}