import React from 'react';
import { Button, Modal, Tabs } from 'antd'
import CountStatisticsModal from './modal/countStatisticsModal'
import CockpitSelectModal from '../select/cockpitSelectModal'
import GridLayout from '../dragcomponent/gridLayout'
import SetPanelModal from '../select/setPanelModal'
import EchartsModal from './modal/echartsModal'
import DateControlComp from '../select/dateControlComp'
import { employerFakeData } from '../select/selectData'
import { getFromLS } from '../../../utils'
import { chartsLoading } from '../getdata/asyncGetData'
const TabPane = Tabs.TabPane;
const fakeData = [];
import {
    //资方统计
    EMPLOYER_REGISTER, //资方用户注册
    EMPLOYER_EXCHAGNE_PROP, //资方用户交易占比'
    EMPLOYER_EXCHANGE_NUM_TREND, //资方用户交易量趋势'
    EMPLOYER_EXCHANGE_FREQ_TREND, //资方用户交易频率趋势'
    EMPLOYER_WITHDRAW_PROP, //资方用户提现占比统计'
    EMPLOYER_DEPOSIT_PROP, //资方用户存入与抛售统计'
    EMPLOYER_TO_PLATFORM_USER,  //转为平台用户统计'
    EMPLOYER_TO_X_USER, // 转为X用户统计
    EMPLOYER_HAND_FEE_DEVOTE, AMOUNT_DEPOSIT_MONEY, AMOUNT_MONEY_FLOW_TREND, // 资方用户手续费贡献统计
} from '../static/actionType';

let DATAGRID = { w: 12, h: 10, x: 0, y: 0, minW: 2, minH: 3, maxW: 12, maxH: 18 };


class ProjectTotal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            setKey: '0',
            modalVisible: false,
            modalWidth: '',
            modalHtml: '',
            modalTitle: '',
            iconIsSpin: false,
            isDraggable: false,
            isResizable: false,
            layouts:{},
            radioCurrency: 0,
            radioLegal: 0,
            panelTabKey: '1',
            tabKey: '1',
            title: '项目统计-projectTotal-1',
            oldfakeData: [
                {
                    key: '5-1',
                    col: 1,
                    title: EMPLOYER_REGISTER, //资方用户注册
                    dataGrid: DATAGRID,
                    comp: <EchartsModal option={{ loading: true }} />,
                    dateComp: <DateControlComp
                        onChangeSelectTime={this.onChangeSelectTime}
                        pTitle={EMPLOYER_REGISTER}
                        title='日期控件'
                        isScopeType={false}
                        isDateType={true}
                        isDateComp={true}
                    />,
                },
                {
                    key: '5-4',
                    col: 1,
                    title: EMPLOYER_EXCHANGE_FREQ_TREND, //资方用户交易频率趋势'
                    dataGrid: DATAGRID,
                    comp: <EchartsModal option={{ loading: true }} />,
                    dateComp: <DateControlComp
                        onChangeSelectTime={this.onChangeSelectTime}
                        pTitle={EMPLOYER_EXCHANGE_FREQ_TREND}
                        title='日期控件'
                        isScopeType={true}
                        isDateType={true}
                        isDateComp={true}
                    />,
                },
                {
                    key: '5-2',
                    col: 1,
                    title: EMPLOYER_EXCHAGNE_PROP, //资方用户交易占比'
                    dataGrid: DATAGRID,
                    comp: <EchartsModal option={{ loading: true }} />,
                    dateComp: <DateControlComp
                        onChangeSelectTime={this.onChangeSelectTime}
                        pTitle={EMPLOYER_EXCHAGNE_PROP}
                        title='日期控件'
                        isScopeType={true}
                        isDateType={false}
                        isDateComp={true}
                    />,
                },
                {
                    key: '5-3',
                    col: 1,
                    title: EMPLOYER_EXCHANGE_NUM_TREND, //资方用户交易趋势'
                    dataGrid: DATAGRID,
                    comp: <EchartsModal option={{ loading: true }} />,
                    dateComp: <DateControlComp
                        onChangeSelectTime={this.onChangeSelectTime}
                        pTitle={EMPLOYER_EXCHANGE_NUM_TREND}
                        title='日期控件'
                        isScopeType={true}
                        isDateType={true}
                        isDateComp={true}
                    />,
                },
                {
                    key: '5-5',
                    col: 1,
                    title: EMPLOYER_WITHDRAW_PROP, //资方用户提现占比统计'
                    dataGrid: DATAGRID,
                    comp: <EchartsModal option={{ loading: true }} />,
                    dateComp: <DateControlComp
                        onChangeSelectTime={this.onChangeSelectTime}
                        pTitle={EMPLOYER_WITHDRAW_PROP}
                        title='日期控件'
                        isScopeType={false}
                        isDateType={false}
                        isDateComp={true}
                    />,
                },
                {
                    key: '5-6',
                    col: 1,
                    title: EMPLOYER_DEPOSIT_PROP, //资方用户存入与抛售统计'
                    dataGrid: DATAGRID,
                    comp: <EchartsModal option={{ loading: true }} />,
                    dateComp: <DateControlComp
                        onChangeSelectTime={this.onChangeSelectTime}
                        pTitle={EMPLOYER_DEPOSIT_PROP}
                        title='日期控件'
                        isScopeType={true}
                        isDateType={true}
                        isDateComp={true}
                    />
                },
                {
                    key: '5-7',
                    col: 1,
                    title: EMPLOYER_TO_PLATFORM_USER, //转为平台用户统计'
                    dataGrid: DATAGRID,
                    comp: <EchartsModal option={{ loading: true }} />,
                    dateComp: <DateControlComp
                        onChangeSelectTime={this.onChangeSelectTime}
                        pTitle={EMPLOYER_TO_PLATFORM_USER}
                        title='日期控件'
                        isScopeType={true}
                        isDateType={false}
                        isDateComp={true}
                    />,
                },
                {
                    key: '5-8',
                    col: 1,
                    title: EMPLOYER_TO_X_USER, //转为X用户统计'
                    dataGrid: DATAGRID,
                    comp: <EchartsModal option={{ loading: true }} />,
                    dateComp: <DateControlComp
                        onChangeSelectTime={this.onChangeSelectTime}
                        pTitle={EMPLOYER_TO_X_USER}
                        title='日期控件'
                        isScopeType={true}
                        isDateType={false}
                        isDateComp={true}
                    />,
                },
                {
                    key: '5-9',
                    col: 1,
                    title: EMPLOYER_HAND_FEE_DEVOTE, //资方用户手续费贡献统计'
                    dataGrid: DATAGRID,
                    comp: <EchartsModal option={{ loading: true }} />,
                    dateComp: <DateControlComp
                        onChangeSelectTime={this.onChangeSelectTime}
                        pTitle={EMPLOYER_HAND_FEE_DEVOTE}
                        title='日期控件'
                        isScopeType={false}
                        isDateType={false}
                        isDateComp={true}
                    />,
                }
            ],
            fakeData: []
        }
    }
    componentWillMount() {
        this.setState({ layouts: this.getLayout('项目统计-projectTotal-1') })
        let arr = this.state.oldfakeData.map((item, index) => {
            let list = [EMPLOYER_EXCHAGNE_PROP, EMPLOYER_EXCHANGE_NUM_TREND,EMPLOYER_WITHDRAW_PROP,EMPLOYER_DEPOSIT_PROP, EMPLOYER_TO_PLATFORM_USER, EMPLOYER_TO_X_USER];
            if (list.indexOf(item.title) == -1) {
                item.dataGrid = Object.assign({}, item.dataGrid, { w: 12 });
                return item;
            }
            // item.dataGrid = { w: 6, h: 10, x: (index-1) % 2 * 6, y: 0, minW: 2, minH: 3, maxW: 12, maxH: 18 };
            // if(index >= 4) {
                item.dataGrid = { w: 6, h: 10, x: index % 2 * 6, y: 0, minW: 2, minH: 3, maxW: 12, maxH: 18 };
            // }
            return item
        });
        let arrayList = this.getDataList(arr, this.state.tabKey);
        this.setState({
            oldfakeData: arr,
            fakeData: arrayList,
            layouts: this.getLayout('项目统计-projectTotal-1')
        })
    }
    componentDidMount() {
        //初始化图表
        this.getChartsData();
    }
    getChartsData = () => {
        employerFakeData.map((item) => {
            chartsLoading({...item,isShowTitle:false}).then(payload => {
                let arr = this.state.oldfakeData.map((elem) => {
                    if (item.title == elem.title) {
                        elem.comp = <EchartsModal option={payload} />
                    }
                    return elem
                });
                this.setState({
                    oldfakeData: arr
                })
            })
        });
        let list = this.getDataList(this.state.oldfakeData, this.state.tabKey);
        this.setState({
            fakeData: list
        })
    }
    getDataList = (arr, key) => {
        let val = parseInt(key);
        switch (val) {
            case 1:
                return arr.slice(0,4);
            case 2:
                return arr.slice(4,6);
            case 3:
                return arr.slice(6,8);
            case 4:
                return arr.slice(8);
            default:
                break;
        }
    };
    onChangeSelectTime = (date, isDefine) => {
        console.log(date, isDefine)
    };
    //设置按钮
    selectSeting = ({ key }) => {
        const { radioCurrency, radioLegal } = this.state;
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
                let self = this;
                Modal.confirm({
                    title: '确认要恢复默认统计内容及页面布局吗？',
                    okText: '确定',
                    okType: 'more',
                    cancelText: '取消',
                    onOk() {
                        self.resetLayout();
                    },
                    onCancel() {
                    }
                });
                break;
            case '4':
                this.footer = [
                    <Button key="submit" type="more" onClick={() => this.onSave()}>确认</Button>,
                    <Button key="save" type="more" onClick={this.handleCancel}>取消</Button>,
                ];
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
    handleCancel = () => {
        this.setState({ modalVisible: false })
    };
    update = () => {
        console.log('update')
        this.setState({ iconIsSpin: true,fakeData: this.setEchartsLoading() }, () => {
            this.getChartsData()
            setTimeout(() => {
                this.setState({ iconIsSpin: false })
            }, 2000)
        })
    };
    setEchartsLoading = () => {
        return this.state.fakeData.map((item, index) => {
            item.comp = <EchartsModal option={{ loading: true }} />
            return item
        })
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
    //重置layout
    resetLayout = () => {
        this.setState({layouts:{}})
    };
    tabKetChange = (key) => {
        let str = '项目统计-projectTotal-' + key;
        let layouts = this.getLayout('项目统计-projectTotal-' + key);
        let list = this.getDataList(this.state.oldfakeData, key);
        this.setState({
            fakeData: list,
            tabKey: key,
            layouts,
            title: str
        })
    };
    getLayout = (tag) => JSON.parse(JSON.stringify(getFromLS("layouts", tag) || {}));
    render() {
        const { modalHtml, modalTitle, modalVisible, modalWidth, setKey, iconIsSpin, fakeData,layouts,isDraggable,isResizable,tabKey,title } = this.state;
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置：驾驶舱 >  项目统计
                    <CockpitSelectModal value={setKey} iconIsSpin={iconIsSpin} handleChange={this.selectSeting} update={this.update} />
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel" style={{ padding: 0, background: 'transparent', border: 'none' }}>
                            <div className="x_content" style={{ padding: 0 }}>
                                <CountStatisticsModal type="project" />
                                <Tabs className="cock_tab" defaultActiveKey={tabKey} activeKey={tabKey} onChange={this.tabKetChange}>
                                    <TabPane tab="项目方引用统计" key="1"></TabPane>
                                    <TabPane tab="套现统计" key="2"></TabPane>
                                    <TabPane tab="转化统计" key="3"></TabPane>
                                    <TabPane tab="盈利统计" key="4"></TabPane>
                                </Tabs>
                                <div className="row">
                                    <GridLayout
                                        fakeData={fakeData}
                                        layoutsName={title}
                                        isDraggable={isDraggable}
                                        isResizable={isResizable}
                                        layouts={layouts}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
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
export default ProjectTotal