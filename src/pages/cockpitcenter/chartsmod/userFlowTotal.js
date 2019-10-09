import React from 'react';
import { Modal, Button,message } from 'antd'
import qs from 'qs';
import axios from '../../../utils/fetch';
import cookie from 'js-cookie';
import CockpitSelectModal from '../select/cockpitSelectModal'
import GridLayout from '../dragcomponent/gridLayout'
import EchartsModal from './modal/echartsModal'
import DateControlComp from '../select/dateControlComp'
import { TradeTable } from './tableComponent/tradeTable'
import { chartsLoading,getPageLayout,moneyTypeonSave,moneyTypeCancel } from '../getdata/asyncGetData'
import { userFlowFakeData } from '../select/selectData'
import { getFromLS, getDate } from '../../../utils'
import {ALL_DATE, DOMAIN_VIP} from '../../../conf'

import SetPanelModal from '../select/setPanelModal'
import {
    //用户流量
    USER_ACTIVE_FORM, //活跃用户构成
    USER_NEWUSER_TOTAL, //新增用户统计'
    USER_INTERVIEW_FLOW_TREND, //访问流量趋势'
    USER_EXCHANGE_NUM_RANKING, //用户交易量排行'
    USER_CHINA_INTERVIEW, //国内用户访问分布'
    USER_GLOBAL_INTERVIEW, //全球用户访问分布'
    USER_GAIN_RANKING, interviewFlowCols, AMOUNT_DEPOSIT_MONEY, AMOUNT_MONEY_FLOW_TREND,  //用户盈利排行'
} from '../static/actionType'
const { LAST_SEVEN_DAYS } = ALL_DATE;
let DATAGRID = { w: 12, h: 10, x: 0, y: 0, minW: 4, minH: 8, maxW: 12, maxH: 18 }
const creteUser = () => {
    let obj = {}
    userFlowFakeData.forEach(item => {
        obj[item.title] = {
            filter: Object.assign({}, getDate(LAST_SEVEN_DAYS), { timeType: 1 })
        }
    })
    return obj
}

const data = [];
let temLayout = {};

class UserFlowTotal extends React.Component {
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
            appActiveKey:this.props.appActiveKey
        }
    }
    componentWillMount() {
        this.requestSetConfig()
    }
    async componentWillReceiveProps(nextProps) {
        if(this.props.appActiveKey != nextProps.appActiveKey && 
            nextProps.appActiveKey == this.state.appActiveKey){
                await this.requestSetConfig(true)
                // this.getChartsData(1)
        }
    }
    requestSetConfig = (isTrue = false) => {
        axios.post(DOMAIN_VIP+"/setting/queryList",qs.stringify({})).then(res => {
            const result = res.data;
            let bb = '';
            let fb = '';
            let layout = {};
            if(result.code == 0){
                let list = result.data;
                list.forEach(function (item) {
                    if(item.type == '5') {
                        let list1 = item.content.split(',');
                        if(list1.length>1) {
                            bb = list1[0];
                            fb = list1[1];
                        } else {
                            bb = 'BTC';
                            fb = 'CNY';
                        }
                    } else if(item.type == 11) {
                        layout = JSON.parse(item.content);
                    }
                });
                this.temLayout = layout;
                this.setState({
                    radioCurrency: bb,// 货币
                    radioLegal: fb,  //法币类型
                },() => {
                    this.getInitList();

                    this.getChartsData(isTrue);
                });
                
            }else{
                message.warning(result.msg);
            }
        });
    }
    getInitList = ()=> {
        let that = this;
        let arr = [
            {
                key: '4-1',
                col: 1,
                title: USER_ACTIVE_FORM, //活跃用户构成
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                // tableComp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    onSelectDateType={this.onSelectDateType}
                    pTitle={USER_ACTIVE_FORM}
                    title='日期控件'
                    isScopeType={false}
                    isDateType={true}
                    isDateComp={true}
                    // timeType={that.state.userDate[USER_ACTIVE_FORM].filter.timeType}
                />,
            },
            {
                key: '4-2',
                col: 1,
                title: USER_NEWUSER_TOTAL, //新增用户统计'
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    onSelectDateType={this.onSelectDateType}
                    pTitle={USER_NEWUSER_TOTAL}
                    title='日期控件'
                    isScopeType={false}
                    isDateType={true}
                    isDateComp={true}
                />,
            },
            {
                key: '4-3',
                col: 1,
                title: USER_INTERVIEW_FLOW_TREND, //访问流量趋势'
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    onSelectDateType={this.onSelectDateType}
                    pTitle={USER_INTERVIEW_FLOW_TREND}
                    title='日期控件'
                    isScopeType={false}
                    isDateType={true}
                    isDateComp={true}
                />,
            },
            {
                key: '4-4',
                col: 1,
                title: USER_EXCHANGE_NUM_RANKING, //用户交易量排行'
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    pTitle={USER_EXCHANGE_NUM_RANKING}
                    title='日期控件'
                    isScopeType={false}
                    isDateType={false}
                    isDateComp={true}
                />,
            },
            {
                key: '4-5',
                col: 2,
                title: USER_CHINA_INTERVIEW, //国内用户访问分布'
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    pTitle={USER_CHINA_INTERVIEW}
                    title='日期控件'
                    isScopeType={false}
                    isDateType={false}
                    isDateComp={true}
                />,
                tableComp: <TradeTable loading={true} columns={interviewFlowCols} tableData={data} />
            },
            {
                key: '4-6',
                col: 2,
                title: USER_GLOBAL_INTERVIEW, //全球用户访问分布'
                dataGrid: DATAGRID,
                comp: <EchartsModal option={{ loading: true }} />,
                dateComp: <DateControlComp
                    onChangeSelectTime={this.onChangeSelectTime}
                    pTitle={USER_GLOBAL_INTERVIEW}
                    title='日期控件'
                    isScopeType={false}
                    isDateType={false}
                    isDateComp={true}
                />,
                tableComp: <TradeTable loading={true} columns={interviewFlowCols} tableData={data} />
            }
        ]
        for(var i=0;i<arr.length;i++){
            cookie.set(arr[i].title,"")
        }
        this.setState({
            fakeData: arr,
            layouts: this.temLayout
        })
    };
    setEchartsLoading = () => {
        return this.state.fakeData.map((item, index) => {
            item.comp = <EchartsModal option={{ loading: true }} />
            return item
        })
    };
    // 全局刷新保持条件不变
    update = () => {
        this.setState({ iconIsSpin: true, fakeData: this.setEchartsLoading() }, () => {
            this.getChartsData(1);
            setTimeout(() => {
                this.setState({ iconIsSpin: false })
            }, 2000)
        })
    };
    getChartsData = (type) => {
        //type 如果存在则是选择完币种之后刷新保留条件
        //重置布局的时候也初始化刷新
        let filterObj = {};
        if(type) {
            filterObj = this.state.userDate;
        } else {
            filterObj = creteUser();
        }
        userFlowFakeData.map((item) => {
            let filters = filterObj[item.title].filter;
            let radioCurrency = this.state.radioCurrency;
            let radioLegal = this.state.radioLegal;
            chartsLoading({ ...item, isShowTitle: false, ...filters, radioCurrency: radioCurrency, radioLegal: radioLegal }).then(payload => {
                let arr = this.state.fakeData.map((elem, i) => {
                    // elem.dataGrid = Object.assign({},DATAGRID,{x : i % 3 * 4 })
                    if (item.title == elem.title) {
                        if (item.title == USER_CHINA_INTERVIEW || item.title == USER_GLOBAL_INTERVIEW) {
                            payload.showTable = false
                        }
                        if(elem.title == USER_EXCHANGE_NUM_RANKING){
                            elem.comp = <TradeTable columns={payload.tableOption.columns} tableData={payload.tableOption.tableData} hideTitle ={true} title={elem.title} loading={false} />
                        }else{
                            elem.comp = <EchartsModal option={payload} />
                        }
                        if (elem.tableComp) {
                            elem.tableComp = <TradeTable columns={payload.tableOption.columns} tableData={payload.tableOption.tableData} loading={false} />

                        }
                    }
                    // if (elem.title == TRADE_RANKING) {
                    //     elem.comp = <TradeTable columns={tradeRankCols} tableData={data} />
                    // }
                    return elem
                });
                this.setState({
                    fakeData: arr
                })
            })
        })
    };
    onChangeSelectTime = (date, isDefine,scope) => {
        var sswa=[date.endTime,date.startTime,scope]
        cookie.set(isDefine.pTitle,sswa)
        console.log(date, isDefine);
        const { pTitle } = isDefine;
        const { userDate,radioCurrency, radioLegal } = this.state;
        let {timeType} = userDate[pTitle].filter;
        this.onSelectGetData({ ...date, pTitle, timeType, radioCurrency: radioCurrency, radioLegal:radioLegal });

    };
    onSelectDateType = ({ timeType, pTitle }) => {
        // console.log(timeType)
        const { userDate } = this.state;
        const { startTime, endTime } = userDate[pTitle].filter;
        // console.log(userDate

        this.onSelectGetData({ startTime, endTime, timeType, pTitle })
    };
    onSelectGetData = (date) => {
        let { fakeData, userDate } = this.state
        const { pTitle } = date

        chartsLoading({ title: pTitle, isShowTitle: false, ...date }).then(payload => {
            let arr = fakeData.map((item) => {
                if (item.title == pTitle) {
                    if (pTitle == USER_CHINA_INTERVIEW || pTitle == USER_GLOBAL_INTERVIEW) {
                        payload.showTable = false
                    }
                    if(item.title == USER_EXCHANGE_NUM_RANKING){
                        item.comp = <TradeTable columns={payload.tableOption.columns} tableData={payload.tableOption.tableData} hideTitle={true} title={item.title} loading={false} />
                    }else{
                        item.comp = <EchartsModal option={payload} />
                    }
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
        const { radioCurrency, radioLegal } = this.state
        let self = this
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
    }
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
        this.temLayout=[]
        let that = this;
        let id = cookie.get('userId');
        axios.post(DOMAIN_VIP+"/setting/delete",qs.stringify({
            userid:  id,
            type: 11
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                that.setState({
                    layouts: {},                  
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
        await getPageLayout(11,this.temLayout);
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
    getLayout = () => JSON.parse(JSON.stringify(getFromLS("layouts", '流量统计-userFlowTotal') || {}));
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
                        layoutsName='流量统计-userFlowTotal'
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

export default UserFlowTotal
