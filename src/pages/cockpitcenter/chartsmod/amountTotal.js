import React from 'react';
import { Modal, Button, message, Select } from 'antd'
import qs from 'qs';
import cookie from 'js-cookie';
import axios from '../../../utils/fetch'
import CockpitSelectModal from '../select/cockpitSelectModal'
import GridLayout from '../dragcomponent/gridLayout'
import EchartsModal from './modal/echartsModal'
import DateControlComp from '../select/dateControlComp'
import {chartsLoading, getPageLayout, moneyTypeCancel, moneyTypeonSave} from '../getdata/asyncGetData'
import { amountFakeData, userFlowFakeData } from '../select/selectData'
import { getDate, getFromLS } from '../../../utils'
import { ALL_DATE, DOMAIN_VIP } from '../../../conf';
import SetPanelModal from '../select/setPanelModal'
import {
    AMOUNT_DEPOSIT_MONEY,   //沉淀资金
    AMOUNT_EXCHANGE_MONEY_DIST, //交易所金额分布
    AMOUNT_HAND_FEE_CHANGE_TREND,   //手续费收取趋势
    AMOUNT_HAND_FEE_HANDICAP,   //手续费盘口统计
    AMOUNT_MONEY_FLOW_TREND,    //资金流动趋势
    AMOUNT_PLATFORM_MONEY_DIST, //平台账户资金分布
    AMOUNT_PRESERV_MONEY_DIST,  //保值资金分布
    AMOUNT_WALLET_CURRENCY_DIST,    //钱包货币分布
    AMOUNT_WALLET_MONEY_DIST,//钱包资金分布
    AMOUNT_USER_MONEY_DIST,
    USER_CHINA_INTERVIEW, USER_GLOBAL_INTERVIEW, //用户货币分布
} from '../static/actionType'
import { TradeTable } from "./tableComponent/tradeTable";

let DATAGRID = { w: 12, h: 10, x: 0, y: 0, minW: 4, minH: 8, maxW: 12, maxH: 18 }
const { LAST_SEVEN_DAYS } = ALL_DATE;
const Option = Select.Option;
const creteUser = () => {
    let obj = {};
    amountFakeData.forEach(item => {
        obj[item.title] = {
            filter: Object.assign({}, getDate(LAST_SEVEN_DAYS), { timeType: 1 }, { scopeType: '' }, { fundsType: 2 })
        };
    });
    return obj;
}
let temLayout = {};
class AmountTotal extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            setKey: '0',
            modalVisible: false,
            modalWidth: '',
            modalHtml: '',
            modalTitle: '',
            iconIsSpin: false,
            isDraggable: false,
            isResizable: false,
            data1: [],
            data2: [],
            data3: [],
            data4: [],
            data5: [],
            layouts: {},
            radioCurrency: '',
            radioLegal: '',
            panelTabKey: '1',
            userDate: creteUser(),
            fakeData: [],
            marketsList: [<Option key='' value=''>全站</Option>],
            fundsTypeList: [],
            appActiveKey:this.props.appActiveKey
        }
    }
    componentWillMount() {
        this.requestSetConfig()
    }
    componentDidMount(){
    }
    componentWillReceiveProps(nextProps) {
        if(this.props.appActiveKey != nextProps.appActiveKey && 
            nextProps.appActiveKey == this.state.appActiveKey){
                this.requestSetConfig(true)            
        }
    }
    /**
     * @param {Boolean} isTrue  用来判断tab切换传的时间 true 取选择后的时间， false 为最近7天
     */
    requestSetConfig = (isTrue) => {
        axios.post(DOMAIN_VIP + "/setting/queryList", qs.stringify({})).then(res => {
            const result = res.data;
            let bb = '';
            let fb = '';
            let layout = {};
            if (result.code == 0) {
                let list = result.data;
                list.forEach(function (item) {
                    if (item.type == '5') {
                        let list1 = item.content.split(',');
                        if (list1.length > 1) {
                            bb = list1[0];
                            fb = list1[1];
                        } else {
                            bb = 'BTC';
                            fb = 'CNY';
                        }
                    } else if (item.type == 10) {
                        layout = JSON.parse(item.content);
                    }
                });
                this.temLayout = layout;
                this.setState({
                    radioCurrency: bb,// 货币
                    radioLegal: fb,  //法币类型
                }, () => {
                    this.requestMarket(isTrue);
                });
            } else {
                message.warning(result.msg);
            }
        });
    }
    //请求市场
    requestMarket = (isTrue) => {
        let that = this;
        axios.get(DOMAIN_VIP + '/common/queryMarket').then(res => {
            const result = res.data;
            let _accountTypeArr = [];
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    _accountTypeArr.push(<Option key={i} value={(result.data[i]).toUpperCase()}>{(result.data[i]).toUpperCase()}</Option>)
                }
                this.setState({
                    marketsList: [...this.state.marketsList, ..._accountTypeArr]
                }, () => {
                    that.requestFundsType(isTrue);
                })
            }
        });
    };
    // 请求币种
    requestFundsType = (isTrue = false) => {
        let that = this;
        axios.get(DOMAIN_VIP + '/common/queryAttr').then(res => {
            const result = res.data;
            let accountTypeArr = []
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    accountTypeArr.push(<Option key={result.data[i].paracode} value={result.data[i].paracode}>{result.data[i].paravalue}</Option>)
                }
                this.setState({
                    fundsTypeList: [...this.state.fundsTypeList, ...accountTypeArr]
                }, () => {
                    that.getInitList();
                    that.getChartsData(isTrue);
                    sessionStorage.setItem('fundsTypeList', JSON.stringify(result.data));
                })
            }
        });
    }
    getInitList = () => {
        const { marketsList, fundsTypeList } = this.state;
        let arr = [
            {
                key: '2-1',
                col: 1,
                title: AMOUNT_EXCHANGE_MONEY_DIST, //交易所金额分布
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
            },
            {
                key: '2-2',
                col: 1,
                title: AMOUNT_PLATFORM_MONEY_DIST, //平台账户资金分布
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
            },
            {
                key: '2-3',
                col: 1,
                title: AMOUNT_WALLET_MONEY_DIST,//钱包资金分布
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
            },
            {
                key: '2-4',
                col: 1,
                title: AMOUNT_USER_MONEY_DIST, //用户货币分布
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
            },
            {
                key: '2-5',
                col: 1,
                title: AMOUNT_WALLET_CURRENCY_DIST,    //钱包货币分布
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
            },
            {
                key: '2-6',
                col: 1,
                title: AMOUNT_PRESERV_MONEY_DIST,  //保值资金分布
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
            },
            {
                key: '2-8',
                col: 1,
                title: AMOUNT_HAND_FEE_HANDICAP,   //手续费盘口统计
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    pTitle={AMOUNT_HAND_FEE_HANDICAP}
                    title='日期控件'
                    isScopeType={false}
                    isDateType={false}
                    isDateComp={true}
                    isFundsType={false}
                />,
            },
            {
                key: '2-9',
                col: 1,
                title: AMOUNT_HAND_FEE_CHANGE_TREND,   //手续费收取趋势
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    onSelectDateType={this.onSelectDateType}
                    onSelectScopeType={this.onSelectScopeType}
                    pTitle={AMOUNT_HAND_FEE_CHANGE_TREND}
                    title='日期控件'
                    isScopeType={true}
                    isDateType={true}
                    isDateComp={true}
                    isFundsType={false}
                    marketsList={marketsList}
                />,
            },
            {
                key: '2-7',
                col: 1,
                title: AMOUNT_MONEY_FLOW_TREND,    //资金流动趋势
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    onSelectDateType={this.onSelectDateType}
                    onSelectFundsType={this.onSelectFundsType}
                    pTitle={AMOUNT_MONEY_FLOW_TREND}
                    title='日期控件'
                    isScopeType={false}
                    isDateType={true}
                    isDateComp={true}
                    isFundsType={true}
                    fundsTypeList={fundsTypeList}
                />,
            },
            // {
            //     key: '2-10',
            //     col: 2,
            //     title: AMOUNT_DEPOSIT_MONEY,   //沉淀资金
            //     dataGrid: DATAGRID,
            //     comp: <EchartsModal option={{ loading: true }} />,
            //     tableComp: <EchartsModal option={{ loading: true }} />,
            //     dateComp: <DateControlComp
            //         onChangeSelectTime={this.onChangeSelectTime}
            //         onSelectDateType={this.onSelectDateType}
            //         onSelectScopeType={this.onSelectScopeType}
            //         pTitle={AMOUNT_MONEY_FLOW_TREND}
            //         title='日期控件'
            //         isScopeType={true}
            //         isDateType={false}
            //         isDateComp={false}
            //         isFundsType={false}
            //         marketsList={marketsList}
            //     />,
            // }
        ];
        let list = arr.map((item, index) => {
            if (item.title == AMOUNT_DEPOSIT_MONEY || item.title == AMOUNT_MONEY_FLOW_TREND) {
                let height = 40;
                if(item.title == AMOUNT_DEPOSIT_MONEY) {
                    height = 50;
                }
                item.dataGrid = Object.assign({}, item.dataGrid, { w: 12,y: height });
                return item
            }
            item.dataGrid = { w: 6, h: 10, x: index % 2 * 6, y: index/2 * 10, minW: 4, minH: 8, maxW: 12, maxH: 18 }
            return item
        });
        for(var i=0;i<arr.length;i++){
            cookie.set(arr[i].title,"")
        }
        this.setState({
            fakeData: list,
            layouts: this.temLayout
        })
    };
    setEchartsLoading = () => {
        return this.state.fakeData.map((item, index) => {
            item.comp = <EchartsModal option={{ loading: true }} />
            return item
        })
    }
    update = () => {
        console.log('update')
        this.setState({ iconIsSpin: true, fakeData: this.setEchartsLoading() }, () => {
            this.getChartsData(1);
            setTimeout(() => {
                this.setState({ iconIsSpin: false })
            }, 2000)
        })
    }
    getChartsData = (type) => {
        //type 如果存在则是选择完币种之后刷新保留条件
        //重置布局的时候也初始化刷新
        let that = this;
        let filterObj = {};
        if (type) {
            filterObj = this.state.userDate;
        } else {
            filterObj = creteUser();
        }
        amountFakeData.map((item) => {
            let radioCurrency = that.state.radioCurrency;
            let radioLegal = that.state.radioLegal;
            let filters = filterObj[item.title].filter;
            chartsLoading({ ...item, isShowTitle: false, ...filters, radioCurrency: radioCurrency, radioLegal: radioLegal }).then(payload => {
                let arr = this.state.fakeData.map((elem, i) => {
                    // elem.dataGrid = Object.assign({},DATAGRID,{x : i % 3 * 4 })
                    if (item.title == elem.title) {
                        elem.comp = <EchartsModal option={payload} />
                        if (elem.tableComp) {
                            // elem.tableComp = <TradeTable loading={false}/>
                            if (elem.title == AMOUNT_DEPOSIT_MONEY) {
                                elem.tableComp = <EchartsModal option={payload} />
                            }
                        }
                    }
                    // if (elem.title == TRADE_RANKING) {
                    //     elem.comp = <TradeTable columns={tradeRankCols} tableData={data} />
                    // }
                    return elem
                })
                this.setState({
                    fakeData: arr
                })
            })
        })
    };
    //时间选择
    onChangeSelectTime = (date, isDefine,scope) => {
        var sswa=[date.endTime,date.startTime,scope]
        cookie.set(isDefine.pTitle,sswa)
        const { pTitle } = isDefine;
        const { userDate } = this.state;
        let { timeType, scopeType, fundsType } = userDate[pTitle].filter;
        this.onSelectGetData({ ...date, pTitle, timeType, scopeType, fundsType });
    };
    //按天 按周 按月选择
    onSelectDateType = ({ timeType, pTitle }) => {
        const { userDate } = this.state;
        const { startTime, endTime, scopeType, fundsType } = userDate[pTitle].filter;
        this.onSelectGetData({ startTime, endTime, timeType, pTitle, scopeType, fundsType })
    };
    onSelectScopeType = ({ scopeType, pTitle }) => {
        const { userDate } = this.state;
        const { startTime, endTime, timeType, fundsType } = userDate[pTitle].filter;
        this.onSelectGetData({ startTime, endTime, timeType, pTitle, scopeType, fundsType })
    };
    onSelectFundsType = ({ fundsType, pTitle }) => {
        const { userDate } = this.state;
        const { startTime, endTime, timeType, scopeType } = userDate[pTitle].filter;
        this.onSelectGetData({ startTime, endTime, timeType, pTitle, scopeType, fundsType })
    };
    onSelectGetData = (date) => {
        let { fakeData, userDate, radioCurrency, radioLegal } = this.state;
        const { pTitle } = date;

        chartsLoading({ title: pTitle, isShowTitle: false, ...date, radioCurrency: radioCurrency, radioLegal: radioLegal }).then(payload => {
            let arr = fakeData.map((item) => {
                if (item.title == pTitle) {
                    if (pTitle == USER_CHINA_INTERVIEW || pTitle == USER_GLOBAL_INTERVIEW) {
                        payload.showTable = false
                    }
                    item.comp = <EchartsModal option={payload} />
                    if (item.tableComp) {
                        item.tableComp = <TradeTable columns={payload.tableOption.columns} tableData={payload.tableOption.tableData} loading={false} />

                    }
                    userDate[pTitle].filter = date
                }
                return item
            });
            this.setState({
                fakeData: arr,
                userDate,
            })
        })
    }
    //设置按钮
    selectSeting = ({ key }) => {
        console.log(typeof key)
        const { radioCurrency, radioLegal, selectedRowKeys, selectedRows, panelTabKey, fakeData } = this.state
        let self = this
        switch (key) {
            case '0':
                break;
            case '1':
            case '2':
                this.setState({
                    isDraggable: true,
                    isResizable: true
                })
                break;
            case '3':
                let self = this
                Modal.confirm({
                    title: '确认将当前页面布局恢复至默认状态吗？',
                    okText: '确定',
                    okType: 'more',
                    cancelText: '取消',
                    onOk() {
                        self.resetLayout()
                    },
                    onCancel() {
                        console.log('Cancel')
                    }
                })
                break;
            case '4':
                this.footer = [
                    <Button key="submit" type="more" onClick={() => this.moneyTypeonSave()}>确认</Button>,
                    <Button key="save" type="more" onClick={this.moneyTypeCancel}>取消</Button>,
                ]
                this.setState({
                    modalHtml: <SetPanelModal
                        setKey={key}
                        radioCurrency={radioCurrency}
                        radioLegal={radioLegal}
                        handleInputChange={this.handleInputChange}
                    />,
                    modalVisible: true,
                    modalWidth: '400px',
                    modalTitle: '统计设置'
                })
                break;
            default:
                break;
        }
        // this.setState({
        //     setKey: key,
        //     fakeData: this.getLayout()
        // })
    };
    //选择改变
    handleInputChange = (event) => {
        const value = event.target.value;
        const name = event.target.name;
        this.setState({
            [name]: value
        });
    };
    moneyTypeonSave = async () => {
        let that = this;
        await moneyTypeonSave(this.state.radioCurrency, this.state.radioLegal);
        that.setState({ fakeData: this.setEchartsLoading(),modalVisible: false }, () => {
            this.getChartsData(1);
        });
    };
    moneyTypeCancel = async() => {
        let list = await moneyTypeCancel();
        this.setState({
            radioCurrency: list[0],// 货币
            radioLegal: list[1],  //法币类型
            modalVisible: false
        });
    };
    //重置layout
    resetLayout = () => {
        // 清空条件刷新
        this.temLayout=[];
        let that = this;
        let id = cookie.get('userId');
        axios.post(DOMAIN_VIP + "/setting/delete", qs.stringify({
            userid: id,
            type: 10
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                that.setState({
                    // userDate: creteUser(),
                    layouts: {},
                    // radioCurrency: 'BTC',// 货币
                    // radioLegal: 'CNY',  //法币类型
                });
                // that.setState({ fakeData: this.setEchartsLoading() }, () => {
                //     that.getChartsData(1);
                // })
            } else {
                message.warning(result.msg);
            }
        });
    };
    handleCancel = () => {
        this.setState({ modalVisible: false })
    }
    //保存布局
    saveLayout = async () => {
        await getPageLayout(10,this.temLayout);
        this.setState({layouts:this.temLayout, isDraggable:false, isResizable:false});
    };
    //取消布局
    cancelLayout = () => {
        this.temLayout = this.state.layouts;
        this.setState({
            isDraggable:false,
            isResizable:false
        })
    };
    onLayoutChange = (temLayout) => {
        this.temLayout = temLayout;
    };
    //获取localStronge 的layout
    getLayout = () => JSON.parse(JSON.stringify(getFromLS("layouts", '金额统计-amountTotal') || {}))
    render() {
        const { modalHtml, modalTitle, modalVisible, modalWidth, setKey, iconIsSpin, fakeData, layouts, isDraggable, isResizable } = this.state
        return (
            <div className="right-con">
                <div>
                    <CockpitSelectModal
                        value={setKey}
                        iconIsSpin={iconIsSpin}
                        update={this.update}
                        handleChange={this.selectSeting}
                        isDraggable={isDraggable}
                        saveLayout={this.saveLayout}
                        handleCancel={this.cancelLayout}
                    />
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <GridLayout
                        fakeData={fakeData}
                        layoutsName='金额统计-amountTotal'
                        isDraggable={isDraggable}
                        isResizable={isResizable}
                        layouts={layouts}
                        onLayoutChange={this.onLayoutChange}
                    />
                </div>
                <Modal
                    visible={modalVisible}
                    title={modalTitle}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width={modalWidth}
                    className="cock_modal"
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}

export default AmountTotal
