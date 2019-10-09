import React from 'react';
import { Modal,Button,message,Select } from 'antd'
import qs from 'qs';
import axios from '../../../utils/fetch';
import cookie from 'js-cookie';
import CockpitSelectModal from '../select/cockpitSelectModal'
import GridLayout from '../dragcomponent/gridLayout'
import EchartsModal from './modal/echartsModal'
import DateControlComp from '../select/dateControlComp'
import { TradeTable } from './tableComponent/tradeTable'
import {chartsLoading, getPageLayout, moneyTypeCancel, moneyTypeonSave} from '../getdata/asyncGetData'
import {tradeFakeData} from '../select/selectData'
import {getDate, getFromLS,saveToLS} from '../../../utils'
import SetPanelModal from '../select/setPanelModal'
import {ALL_DATE, DOMAIN_VIP} from '../../../conf'
import {
    TRADE_ENGINE,//交易终端
    TRADE_HANDICAP_DEAL,    //盘口成交统计
    TRADE_HANDICAP_DEAL_DSIT, //盘口成交分布统计
    TRADE_NEW_OLD_USER_EXCHANGE,    //新老客户交易构成
    TRADE_RANKING,  //平台排行统计
    TRADE_TATE_TOTAL,   //交易转化率统计
    TRADE_TREND,    //交易趋势
    TRADE_USER_EXCHANGE_MONEY,  //用户交易金额统计
    tradeHandicapCols,
    tradeRateCols1,
    tradeRateCols2,
    tradeRankCols,
    tradeUserOldCols, USER_CHINA_INTERVIEW, USER_GLOBAL_INTERVIEW, USER_EXCHANGE_NUM_RANKING,
} from '../static/actionType'
const { LAST_SEVEN_DAYS,LAST_MONTH } = ALL_DATE;
const Option = Select.Option;
let DATAGRID = { w: 12, h: 10, x: 0, y: 0, minW: 4, minH: 8, maxW: 12, maxH: 18 }
const creteUser = () => {
    let obj = {};
    tradeFakeData.forEach(item => {
        if(item.title == TRADE_NEW_OLD_USER_EXCHANGE || item.title == TRADE_ENGINE) {
            obj[item.title] = {
                filter: Object.assign({}, getDate(LAST_MONTH), { timeType: 1 },{scopeType: ''})
            }
        } else {
            obj[item.title] = {
                filter: Object.assign({}, getDate(LAST_SEVEN_DAYS), { timeType: 1 },{scopeType: ''})
            }
        }
    });
    return obj;
}
const data = [];
let temLayout  = {};
class TradeTotal extends React.Component {
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
            radioCurrency: '',
            radioLegal: '',
            panelTabKey: '1',
            layouts: {},
            fakeData: [],
            userDate: creteUser(),
            marketsList: [<Option key='' value=''>全站</Option>],
            appActiveKey: this.props.appActiveKey,
        }
    }
    componentWillMount() {
        this.requestSetConfig()
    }
    /**
     * @param {Boolean} isTrue  用来判断tab切换传的时间 true 取选择后的时间， false 为最近7天
     */
    requestSetConfig = async (isTrue =false) => {
        axios.post(DOMAIN_VIP+"/setting/queryList",qs.stringify({})).then(res => {
            const result = res.data;
            let bb = '';
            let fb = '';
            let layout = {};
            if(result.code == 0){
                let list = result.data;
                // console.log(list)
                list.forEach(function (item) {
                    if(item.type == '5') {
                        let list1 = item.content.split(',');
                        if(list1.length>1) {
                            bb = list1[0];
                            fb = list1[1];
                        } else {
                            bb = 'USDT';
                            fb = 'CNY';
                        }
                    } else if(item.type == 9) {
                        layout = JSON.parse(item.content);
                    }
                });
                this.temLayout = layout;
                this.setState({
                    radioCurrency: bb || 'USDT',// 货币
                    radioLegal: fb || 'CNY',  //法币类型
                },async () => {
                    await this.requestMarket();
                    this.getChartsData(isTrue);

                });

            }else{
                message.warning(result.msg);
            }
        });
    }
    
    //请求市场
    requestMarket =()=>{
        let that = this;
       return new Promise((resolve,reject) => {

           axios.get(DOMAIN_VIP+'/common/queryMarket').then(res => {
               const result = res.data;
               let _accountTypeArr = [];
               if(result.code == 0){
                   for(let i=0;i<result.data.length;i++){
                       _accountTypeArr.push(<Option key={i} value={(result.data[i]).toUpperCase()}>{(result.data[i]).toUpperCase()}</Option>)
                   }
                   resolve()
                   this.setState({
                       marketsList:[...this.state.marketsList,..._accountTypeArr]
                   },()=>{
                       that.getInitList();
                    //    that.getChartsData();
                   })
               }
           });
       }) 
    }
    getInitList = ()=> {
        const {marketsList} = this.state;
        let arr = [
            {
                key: '3-1',
                col: 2,
                title: TRADE_TATE_TOTAL,//'交易转化率统计',
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                left: true,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    onSelectScopeType={this.onSelectScopeType}
                    pTitle={TRADE_TATE_TOTAL}
                    title='日期控件'
                    isScopeType={true}
                    isDateType={false}
                    isDateComp={true}
                    marketsList={marketsList}
                    isDraggable={this.state.isDraggable}
                />,

                tableComp: [<TradeTable loading={true} columns={tradeRateCols1} tableData={data} key={'123'} />, <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12" key={456} style={{ height: '20px' }}></div>, <TradeTable loading={true} columns={tradeRateCols2} tableData={data} key={789} />]
            },
            {
                key: '3-2',
                col: 1,
                title: TRADE_HANDICAP_DEAL,//'盘口成交统计',
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    pTitle={TRADE_HANDICAP_DEAL}
                    title='日期控件'
                    isScopeType={false}
                    isDateType={false}
                    isDateComp={true}
                    isDraggable={this.state.isDraggable}
                />
            },
            {
                key: '3-3',
                col: 2,
                title: TRADE_HANDICAP_DEAL_DSIT,//'盘口成交分布统计',
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    pTitle={TRADE_HANDICAP_DEAL_DSIT}
                    title='日期控件'
                    isScopeType={false}
                    isDateType={false}
                    isDateComp={true}
                    isDraggable={this.state.isDraggable}
                />,
                tableComp: <TradeTable loading={true} columns={tradeHandicapCols} tableData={data} />
            },
            {
                key: '3-4',
                col: 2,
                title: TRADE_NEW_OLD_USER_EXCHANGE,//'新老客户交易构成',
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    onSelectScopeType={this.onSelectScopeType}
                    pTitle={TRADE_NEW_OLD_USER_EXCHANGE}
                    title='月份控件'
                    isScopeType={true}
                    data={this.state.uedata}
                    isDateType={false}
                    isDateComp={true}
                    marketsList={marketsList}
                    isDraggable={this.state.isDraggable}
                />,
                tableComp: <TradeTable columns={tradeUserOldCols} tableData={data} />
            },
            {
                key: '3-5',
                col: 1,
                title: TRADE_USER_EXCHANGE_MONEY,//'用户交易金额统计',
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    onSelectScopeType={this.onSelectScopeType}
                    pTitle={TRADE_USER_EXCHANGE_MONEY}
                    title='日期控件'
                    isScopeType={true}
                    isDateType={false}
                    isDateComp={true}
                    marketsList={marketsList}
                    isDraggable={this.state.isDraggable}
                />
            },
            {
                key: '3-6',
                col: 1,
                title: TRADE_TREND, //'交易趋势',
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    onSelectDateType={this.onSelectDateType}
                    onSelectScopeType={this.onSelectScopeType}
                    pTitle={TRADE_TREND}
                    title='日期控件'
                    isScopeType={true}
                    isDateType={true}
                    isDateComp={true}
                    marketsList={marketsList}
                    isDraggable={this.state.isDraggable}
                />
            },
            // {
            //     key: '3-7',
            //     col: 2,
            //     title: TRADE_ENGINE, //交易终端
            //     dataGrid: DATAGRID,
            //     comp: <EchartsModal option={{ loading: true }} />,
            //     dateComp: <DateControlComp
            //         onChangeSelectTime={this.onChangeSelectTime}
            //         onSelectScopeType={this.onSelectScopeType}
            //         pTitle={TRADE_ENGINE}
            //         title='月份控件'
            //         isScopeType={true}
            //         isDateType={false}
            //         isDateComp={true}
            //         marketsList={marketsList}
            //         isDraggable={this.state.isDraggable}
            //     />,
            //     tableComp: <TradeTable loading={true} columns={tradeUserOldCols} tableData={data} />
            // },

            {
                key: '3-8',
                col: 1,
                title: TRADE_RANKING,
                dataGrid: DATAGRID,
                comp: <TradeTable loading={true} columns={tradeRankCols} tableData={data} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    pTitle={TRADE_RANKING}
                    title='月份控件'
                    isScopeType={false}
                    isDateType={false}
                    isDateComp={false}
                    isDraggable={this.state.isDraggable}
                />
            },
        ];
        for(var i=0;i<arr.length;i++){
            cookie.set(arr[i].title,"")
        }
        this.setState({
            fakeData: arr,
            layouts: this.temLayout
        });
    };
    componentDidMount() {
        // this.getChartsData()
    }
    componentWillReceiveProps(nextProps) {
        if(this.props.appActiveKey != nextProps.appActiveKey && 
            nextProps.appActiveKey == this.state.appActiveKey){
                this.requestSetConfig(true)
        }
    }
    //输入时 input 设置到 satte
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    
    update = () => {
        this.setState({ iconIsSpin: true, fakeData: this.setEchartsLoading() }, () => {
            this.getChartsData(1);
            setTimeout(() => {
                this.setState({ iconIsSpin: false })
            }, 2000)
        })
    }
    setEchartsLoading = () => {
        return this.state.fakeData.map((item, index) => {
            item.comp = <EchartsModal option={{ loading: true }} />
            return item
        })
    }
    getChartsData = (type) => {
        let filterObj = {};
        if(type) {
            filterObj = this.state.userDate;
        } else {
            filterObj = creteUser();
        }
        tradeFakeData.map((item) => {
            let filters = filterObj[item.title].filter;
            let radioCurrency = this.state.radioCurrency;
            let radioLegal = this.state.radioLegal;
            chartsLoading({...item,isShowTitle:false,...filters, radioCurrency: radioCurrency, radioLegal: radioLegal}).then(payload => {
                let arr = this.state.fakeData.map((elem) => {
                    if (item.title == elem.title) {
                        elem.comp = <EchartsModal option={payload} />;
                        if (elem.tableComp) {
                            if(elem.title == TRADE_TATE_TOTAL){
                                elem.tableComp = [<TradeTable loading={false} columns={payload.tableOption.columns} tableData={payload.tableOption.tableData} key={'123'} />, <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12" key={456} style={{ height: '20px' }}></div>, <TradeTable loading={false} columns={payload.tableOption.columns2} tableData={payload.tableOption.tableData} key={789} />]
                            }else{
                                elem.tableComp = <TradeTable columns={payload.tableOption.columns} tableData={payload.tableOption.tableData} loading={false}/>
                            }
                        }
                        if (elem.title == TRADE_RANKING) {
                            elem.comp = <TradeTable columns={tradeRankCols} tableData={payload.tableOption.tableData} loading={false} />
                        }
                    }
                    return elem
                })
                this.setState({
                    fakeData: arr
                })
            })
        })
    };
    onChangeSelectTime = (date, isDefine,scope) => {
        var sswa=[date.endTime,date.startTime,scope]
        cookie.set(isDefine.pTitle,sswa)
        const { pTitle } = isDefine;
        const { userDate } = this.state;
        let {timeType, scopeType} = userDate[pTitle].filter;
        this.onSelectGetData({ ...date, pTitle, timeType, scopeType });
        saveToLS()
    };
    onSelectDateType = ({ timeType, pTitle }) => {
        const { userDate } = this.state;
        const { startTime, endTime, scopeType } = userDate[pTitle].filter;
        this.onSelectGetData({ startTime, endTime, timeType, pTitle,scopeType })
    };
    onSelectScopeType = ({ scopeType, pTitle }) => {
        const { userDate } = this.state;
        const { startTime, endTime, timeType } = userDate[pTitle].filter;
        this.onSelectGetData({ startTime, endTime, timeType, pTitle,scopeType })
    };
    onSelectGetData = (date) => {
        let { fakeData, userDate,radioCurrency,radioLegal } = this.state;
        const { pTitle } = date;

        chartsLoading({ title: pTitle, isShowTitle: false, ...date, radioCurrency: radioCurrency, radioLegal:radioLegal }).then(payload => {
            let arr = fakeData.map((item) => {
                if (item.title == pTitle) {
                    item.comp = <EchartsModal option={payload} />;
                    if (item.tableComp) {
                        if(item.title == TRADE_TATE_TOTAL){
                            item.tableComp = [<TradeTable loading={false} columns={payload.tableOption.columns} tableData={payload.tableOption.tableData} key={'123'} />, <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12" key={456} style={{ height: '20px' }}></div>, <TradeTable loading={false} columns={payload.tableOption.columns2} tableData={payload.tableOption.tableData} key={789} />]
                        }else{
                            item.tableComp = <TradeTable columns={payload.tableOption.columns} tableData={payload.tableOption.tableData} loading={false}/>
                        }
                    }
                    if (item.title == TRADE_RANKING) {
                        item.comp = <TradeTable columns={tradeRankCols} tableData={payload.tableOption.tableData} loading={false} />
                    }
                    userDate[pTitle].filter = date;
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
        const { radioCurrency, radioLegal} = this.state;
        switch (key) {
            case '0':
                break;
            case '1':
                break;
            case '2':
                this.setState({
                    isDraggable: true,
                    isResizable: true
                });
                break;
            case '3':
                let self = this
                Modal.confirm({
                    title: '确认将当前页面布局恢复至默认状态吗？',
                    okText: '确定',
                    okType: 'more',
                    cancelText: '取消',
                    onOk() {
                        self.resetLayout();
                    },
                    onCancel() {
                        console.log('Cancel')
                    }
                });
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
                });
                break;
            default:
                break;
        }
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
        let that = this;
        this.temLayout=[];
        let id = cookie.get('userId');
        axios.post(DOMAIN_VIP+"/setting/delete",qs.stringify({
            userid:  id,
            type: 9
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                that.setState({
                    // userDate: creteUser(),
                    layouts: {},
                    isDraggable:false,
                    isResizable:false
                    // radioCurrency: 'BTC',// 货币
                    // radioLegal: 'CNY',  //法币类型
                });
                // that.setState({ fakeData: this.setEchartsLoading() }, () => {
                //     that.getChartsData(1);
                // })
            }else{
                message.warning(result.msg);
            }
        });
    };
    handleCancel = () => {
        this.setState({ modalVisible: false })
    };
    //保存布局
    saveLayout = async () => {
        await getPageLayout(9,this.temLayout);
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
    //获取localStronge 的layout目前已经没有用了
    getLayout = () => JSON.parse(JSON.stringify(getFromLS("layouts", '交易统计-tradeTotal') || {}));

    render() {
        const { modalHtml, modalTitle, modalVisible, modalWidth, setKey, iconIsSpin, fakeData, isDraggable, isResizable, layouts } = this.state
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
                        layoutsName='交易统计-tradeTotal'
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

export default TradeTotal