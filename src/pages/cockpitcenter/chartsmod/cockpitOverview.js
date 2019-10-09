import React from 'react';
// import echarts from 'echarts'
import { Button, Select, Modal,message } from 'antd'
import arrayMove from 'array-move';
import cookie from 'js-cookie';
import CountStatisticsModal from './modal/countStatisticsModal'
import CockpitSelectModal from '../select/cockpitSelectModal'
// import DragBox from '../drag/drag'
import SetPanelModal from '../select/setPanelModal'
import SortableComponent from '../dragcomponent/sortable'
import EchartsModal from './modal/echartsModal'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { TradeTable } from './tableComponent/tradeTable'
import { getFromLS,getDate } from '../../../utils'
import { PIE_CHARTS, LINE_CHARTS, FUNNEL_CHARTS, BAR_CHARTS, LIST_TABLE, MAP_CHARTS } from '../static/static'
import {chartsLoading, getPageLayout, moneyTypeCancel, moneyTypeonSave} from '../getdata/asyncGetData'
import { DOMAIN_VIP, ALL_DATE, DAYFORMAT } from '../../../conf'
import { TRADE_HANDICAP_DEAL_DSIT, TRADE_TREND,TRADE_RANKING,AMOUNT_USER_MONEY_DIST,AMOUNT_MONEY_FLOW_TREND,AMOUNT_HAND_FEE_CHANGE_TREND,USER_NEWUSER_TOTAL,USER_INTERVIEW_FLOW_TREND,USER_GLOBAL_INTERVIEW,tradeRankCols,USER_CHINA_INTERVIEW,USER_EXCHANGE_NUM_RANKING } from '../static/actionType'
import moment from 'moment';
import {tradeFakeData,amountFakeData, userFlowFakeData, employerFakeData} from '../select/selectData';
const { LAST_SEVEN_DAYS } = ALL_DATE
const fakeData = [];
let oldTemLayout = {};  //  保存旧的布局，给后端传的数据
let ooldTemLayout = {}; //拖拽交易 、金额时保存子模块的大小布局，一个中间变量

class CockpitOverview extends React.Component {
    constructor(props) {
        super(props)
        this.default = {
            selectedRowKeys: {},//选中项的 key 数组
            selectedRows: {},//选中项的 item 数组
            panelTabKey: '1',//面板key
        }
        this.state = {
            setKey: '0',
            modalVisible: false,
            modalWidth: '',
            modalHtml: '',
            modalTitle: '',
            isDraggable: false,
            isResizable: false,
            iconIsSpin: false,
            radioCurrency: '',
            radioLegal: '',
            panelTabKey: '1',
            selectedRowKeys: {},//选中项的 key 数组
            selectedRows: {},//选中项的 item 数组
            statisticsItems: [],
            topList: [1, 2, 3, 4], //上面统计的列表，防止初始化的时候一片空白
            // temLayout: {},
            appActiveKey:this.props.appActiveKey
        }
    }
    componentDidMount() {
        this.getInitData();
    }
    componentWillReceiveProps(nextProps) {
        if(this.props.appActiveKey != nextProps.appActiveKey && 
            nextProps.appActiveKey == this.state.appActiveKey){
                this.getInitData()
        }
        
    }
    getInitData = ()=> {
        axios.post(DOMAIN_VIP+"/setting/queryList",qs.stringify({})).then(res => {
            const result = res.data;
            let bb = '';
            let fb = '';
            let sort = []; // 最外层排序
            let projectList = []; //资方统计模块里的内容
            let tradeList = []; //交易统计的内容
            let amountList = [];//金额统计的内容
            let userFlowList = [];//流量统计的内容
            let layOutsList = [];
            if(result.code == 0){
                let list = result.data;
                list.forEach(function (item) {
                    switch(item.type){
                        // 之前是分了四个字段来存储，现在用一个类型存四个tab的东西
                        case 1:
                            let obj;
                            try {
                                obj = JSON.parse(item.content)                               
                                projectList = obj[1];// 资方统计
                                tradeList = obj[3]; // 交易统计
                                amountList = obj[2]; // 金额统计
                                userFlowList = obj[4]; // 流量统计
                            } catch (error) {
                                message.error(error)
                                projectList = [];// 资方统计
                                tradeList = [{
                                    name: '交易统计',
                                    url:'/cockpitcenter/chartsmod/tradeTotal',
                                    id: 3,
                                    layouts: {},
                                }]; // 交易统计
                                amountList = [{
                                    name: '金额统计',
                                    url:'/cockpitcenter/chartsmod/amountTotal',
                                    id: 2,
                                    layouts: {},
                                }]; // 金额统计
                                userFlowList = [{
                                    name: '流量统计',
                                    url:'/cockpitcenter/chartsmod/userFlowTotal',
                                    id: 4,
                                    layouts: {},
                                }]; // 流量统计
                            }
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            let list1 = item.content.split(',');
                            if(list1.length>1) {
                                bb = list1[0];
                                fb = list1[1];
                            } else {
                                bb = 'BTC';
                                fb = 'CNY';
                            }
                            break;
                        case 6:
                            // 首览排序的
                            sort = item.content.split(',');
                            break;
                        case 12:
                            layOutsList = item.content ? JSON.parse(item.content) : [];
                            break;
                    }
                });
                let arr = this.getInitList(sort,projectList,tradeList,amountList,userFlowList,layOutsList );
                this.setState({
                    radioCurrency: bb,// 货币
                    radioLegal: fb,  //法币类型
                    statisticsItems: arr
                },() => {
                    this.getCountStatis(fb,bb);
                    this.getDefaultData();
                });

            }else{
                message.warning(result.msg);
            }
        });
    };
    getInitList = (sort,projectList,tradeList,amountList,userFlowList,layOutsList ) => {
        let arr = [
            {
                name: '金额统计',
                url:'/cockpitcenter/chartsmod/amountTotal',
                id: 2,
                layouts: {},
                fakeData: [
                    { key: '2-4', comp: <EchartsModal option={{ loading: true }} />, pId: 2,title:AMOUNT_USER_MONEY_DIST,chartsType:PIE_CHARTS },
                    { key: '2-7', comp: <EchartsModal option={{ loading: true }} />, pId: 2,title:AMOUNT_MONEY_FLOW_TREND,chartsType:LINE_CHARTS },
                    { key: '2-9', comp:<EchartsModal option={{ loading: true }} />, pId: 2,title:AMOUNT_HAND_FEE_CHANGE_TREND,chartsType:LINE_CHARTS },
                ]
            },
            {
                name: '交易统计',
                url:'/cockpitcenter/chartsmod/tradeTotal',
                id: 3,
                layouts: {},
                fakeData: [
                    { key: '3-3', comp: <EchartsModal option={{ loading: true }} />, pId: 3,title:TRADE_HANDICAP_DEAL_DSIT,chartsType:PIE_CHARTS },
                    { key: '3-6', comp: <EchartsModal option={{ loading: true }} />, pId: 3,title:TRADE_TREND,chartsType:LINE_CHARTS },
                    { key: '3-8', comp: <TradeTable loading={true} columns={tradeRankCols} tableData={[]} />, pId: 3,title:TRADE_RANKING,chartsType:LIST_TABLE}
                ]
            },
            {
                name: '流量统计',
                url:'/cockpitcenter/chartsmod/userFlowTotal',
                id: 4,
                layouts: {},
                fakeData: [
                    { key: '4-2', comp: <EchartsModal option={{ loading: true }} />, pId: 4,title:USER_NEWUSER_TOTAL,chartsType:LINE_CHARTS },
                    { key: '4-3', comp: <EchartsModal option={{ loading: true }} />, pId: 4,title:USER_INTERVIEW_FLOW_TREND,chartsType:LINE_CHARTS },
                    { key: '4-5', comp: <EchartsModal option={{ loading: true }} />, pId: 4,title:USER_GLOBAL_INTERVIEW,chartsType:MAP_CHARTS },
                ]
            }
        ];
        let list = [];
        sort.forEach(function (k) {
            arr.map(function (item) {
                if(k == item.url) {
                    item.layouts = layOutsList[item.url] || {};
                    item.fakeData = [];
                    let dataList = item.id == 2 ? amountFakeData : item.id == 3 ? tradeFakeData : item.id == 4 ? userFlowFakeData: employerFakeData;
                    let defaultDataList = item.id == 2 ? amountList : item.id == 3 ? tradeList : item.id == 4 ? userFlowList: projectList;
                    for(let i = 0; i<defaultDataList.length;i++) {
                        for(let j = 0; j<dataList.length;j++) {
                            if(defaultDataList[i] == dataList[j].tagTitle) {
                                let obj = {key: `${item.id}-${dataList[j].key}`,comp: <EchartsModal option={{ loading: true }} />,pId: item.id,title: dataList[j].title,chartsType:dataList[j].chartsType };
                                if(defaultDataList[i] == 'TRADE_RANKING') {
                                    obj = {key: `${item.id}-${dataList[j].key}`,comp: <TradeTable loading={true} columns={tradeRankCols} tableData={[]} />,pId: item.id,title: dataList[j].title,chartsType:dataList[j].chartsType };
                                }
                                item.fakeData.push(obj);
                                break;
                            }
                        }
                    }
                    list.push(item);
                }
            });
        });
        return list;
    };
    getCountStatis = async (unit,bb )=> {
        const { startTime, endTime } = getDate(LAST_SEVEN_DAYS);
        let arr = [
            { startTitle: '交易页PV', endTitle: '今日', unit: '次', url: '/jyConversionRateCookie/overview' },
            { startTitle: '新增会员', endTitle: '总会员', unit: '人', url: '/yhnewuser/overview' },
            { startTitle: '交易量', endTitle: '总交易量', unit: bb, url: '/jyTradeCurrent/overview' },
            { startTitle: '收入', endTitle: '总收入', unit: bb, url: '/jeFees/overview' },
        ];
        let promises = arr.map((item) => {
            let params = item.url == '/jeFees/overview' || item.url == '/jyTradeCurrent/overview' ? { currencyType: bb }:{FCurrencyType: unit};
            return new Promise((resolve, reject) => {
                axios.post(DOMAIN_VIP + item.url, qs.stringify(params)).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        let obj = result.data ? result.data[0] : {}
                        obj.yesterdaytime = moment(obj.yesterdaytime).format(DAYFORMAT)
                        resolve({ ...obj, ...item })
                    } else {
                        message.warning(result.msg);
                        reject(result.msg)
                    }
                })
            }).then(res => res).catch(error => ({error,startTitle:item.startTitle}))
        });
        Promise.all(promises)
            .then(result => {
                this.setState({topList:result})
            })
            .catch(err => console.log(err))
    }
    //设置按钮
    selectSeting = ({ key }) => {
        const { radioCurrency, radioLegal, selectedRowKeys, selectedRows, panelTabKey, statisticsItems } = this.state
        let self = this
        switch (key) {
            case '0':
                break;
            case '1':
                this.footer = [
                    <Button key="submit" type="more" onClick={() => this.onSavePanel()}>确认</Button>,
                    <Button key="save" type="more" onClick={this.handleCancel}>取消</Button>,
                ]
                this.setState({
                    modalHtml: <SetPanelModal
                        setKey={key}
                        selectedRowKeys={selectedRowKeys}
                        selectedRows={selectedRows}
                        onSelectChangeTable={this.onSelectChangeTable}
                        panelTabKey={panelTabKey}
                        tabCallback={this.tabCallback}
                        statisticsItems={statisticsItems}
                    />,
                    modalVisible: true,
                    modalWidth: '600px',
                    modalTitle: '设置面板内容'
                })
                break;
            case '2':
                //自定义布局后将旧的state的布局给ooldTemLayout
                ooldTemLayout = this.initLayOuts()
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
        this.setState({
            setKey: key
        })
    };
    moneyTypeonSave = async () => {
        await moneyTypeonSave(this.state.radioCurrency, this.state.radioLegal);
        this.setState({
            statisticsItems: this.setEchartsLoading(),
            modalVisible: false
        },()=>{
            this.getCountStatis(this.state.radioLegal,this.state.radioCurrency)
            this.getDefaultData();
        })
    };
    moneyTypeCancel = async() => {
        let list = await moneyTypeCancel();
        this.setState({
            radioCurrency: list[0],// 货币
            radioLegal: list[1],  //法币类型
            modalVisible: false
        });
    };
    saveObj = (comp, key, pId) => {
        return {
            key: key,
            comp,
            loading: true,
            pId,
        }
    }
    handleCancel = () => {
        this.setState({ modalVisible: false, ...this.default })
    }
    //设置面板key，暂时没用了
    tabCallback = (panelTabKey) => {
        this.setState({ panelTabKey })
    }
    getData = (comp, key, pId,oldFake) => {
        const { statisticsItems } = this.state
        let newstatisticsItems = statisticsItems.map((elem, elemIndex) => {
            if (elem.id == pId) {
                elem.fakeData.map((fake, fakeIndex) => {
                    if (fake.key == key) {
                        fake.title = oldFake.title
                        fake.comp = comp
                        fake.loading = false
                    }
                    return fake
                })
            }
            return elem
        })
        this.setState({
            statisticsItems: newstatisticsItems,
        })


    }
    //保存面板内容
    onSavePanel = () => {
        const {}  = this.state;
        // 如果初始化的时候
        const {selectedRows, statisticsItems,radioCurrency, radioLegal } = this.state;
        if(JSON.stringify(selectedRows)== '{}') {
            //长度为0的话说明没有改变就不刷新也不保存
            this.setState({
                modalVisible: false
            });
            message.success('保存成功')
            return;
        }
        let newArr = statisticsItems.map((item, index) => {
            item.fakeData = [];
            for (let i = 0; i < selectedRows[item.id].length; i++) {
                switch (selectedRows[item.id][i].chartsType) {
                    case LINE_CHARTS:
                    case PIE_CHARTS:
                    case BAR_CHARTS:
                    case FUNNEL_CHARTS:
                    case MAP_CHARTS:
                        item.fakeData.push(this.saveObj(<EchartsModal option={{ loading: true }} title={selectedRows[item.id][i].title} />, `${item.id}-${selectedRows[item.id][i].key}`, item.id));
                        chartsLoading({...selectedRows[item.id][i],radioCurrency: radioCurrency, radioLegal: radioLegal}).then((payload) => {
                            this.getData(<EchartsModal option={payload} />, `${item.id}-${selectedRows[item.id][i].key}`, item.id,selectedRows[item.id][i])
                        });
                        break;
                    case LIST_TABLE:
                        item.fakeData.push(this.saveObj(<TradeTable loading={true} title={selectedRows[item.id][i].title} />, `${item.id}-${selectedRows[item.id][i].key}`, item.id));
                        chartsLoading({...selectedRows[item.id][i],radioCurrency: radioCurrency, radioLegal: radioLegal}).then((payload) => {
                            this.getData(<TradeTable title={selectedRows[item.id][i].title} columns={tradeRankCols} tableData={payload.tableOption.tableData} loading={false} />, `${item.id}-${selectedRows[item.id][i].key}`, item.id,selectedRows[item.id][i])
                        });
                        break;
                    default:
                        break;
                }
            }
            return item
        });
        this.saveSelectItem();
        let list = newArr.map(item=> {
            item.layouts = oldTemLayout[item.url];
            return item;
        });
        this.setState({
            statisticsItems: list,
            modalVisible: false,
            ...this.default
        })

    };
    saveSelectItem = () => {
        const {selectedRows} = this.state;
        //  如果没有改动就不保存
        if(JSON.stringify(selectedRows)== '{}') {
            message.success('保存成功')
            return;
        }
        let obj = {};
        for(let item in selectedRows) {
            obj[item] = selectedRows[item].map(data=>{
                return data.tagTitle
            })
        }
        let id = cookie.get('userId');
        // 用type = 1 来保存选择的checkbox
        axios.post(DOMAIN_VIP+"/setting/insertOrUpdate",qs.stringify({
            type: 1,
            userid:  id,
            content: JSON.stringify(obj)
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success('保存成功');
            }else{
                message.warning(result.msg)
            }
        }).catch(err => {
            console.info(err);
        });
    };
    getDefaultData = () => {
        const { statisticsItems } = this.state
        let arr = statisticsItems.map((item,index) => {
            const {radioCurrency, radioLegal}  = this.state;
            item.fakeData.map((fake,i) => {
                chartsLoading({...fake,isShowTitle:true, radioCurrency: radioCurrency, radioLegal: radioLegal}).then(payload => {
                    if(fake.title == TRADE_RANKING || fake.title == USER_EXCHANGE_NUM_RANKING){
                        this.getData(<TradeTable columns={tradeRankCols} title={fake.title} tableData={payload.tableOption.tableData} loading={payload.loading} />, fake.key, fake.pId,fake)
                    }else{
                        this.getData(<EchartsModal option={payload} />, fake.key, fake.pId,fake)
                    }
                })
                return fake
            })
            return item
        })
        this.setState({
            statisticsItems:arr
        })
    };
    setEchartsLoading = () => {
        return this.state.statisticsItems.map((item) => {
            item.fakeData.map((fake)=>{
                if(fake.title == TRADE_RANKING) {
                    fake.comp = <TradeTable loading={true} columns={tradeRankCols} tableData={[]} />;
                } else {
                    fake.comp = <EchartsModal option={{ loading: true }} />;
                }
            });
            return item;
        })
    };
    update = () => {
        const {radioCurrency,// 货币
            radioLegal//法币类型
        } = this.state
        this.setState({ iconIsSpin: true, statisticsItems: this.setEchartsLoading() }, () => {
            this.getCountStatis(radioLegal,radioCurrency);
            this.getDefaultData();
            setTimeout(() => {
                this.setState({ iconIsSpin: false, })
            }, 4000)
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
    //table 多选框按钮选中时
    onSelectChangeTable = (selectedRowKeys, selectedRows) => {
        this.setState({
            selectedRowKeys,
            selectedRows,
        });
    };
    //layout排序
    onSortEnd = (oldIndex, newIndex) => {
        this.setState(({ statisticsItems }) => ({
            statisticsItems: arrayMove(statisticsItems, oldIndex, newIndex),
        }));
    };
    //保存布局
    saveLayout = async () => {
        await getPageLayout(12,oldTemLayout);
        let statisticsItems = this.getLayout(oldTemLayout);
        this.setState({statisticsItems, isDraggable:false, isResizable:false});
    };
    //取消布局
    cancelLayout = () => {
        //将ooldTemLayout或者state的布局赋值给oldTemLayout，自定义布局后，直接点取消his.initLayOuts()，否则取ooldTemLayout
        oldTemLayout = JSON.stringify(ooldTemLayout) == '{}' ?  this.initLayOuts() : JSON.parse(JSON.stringify(ooldTemLayout));
        
        this.setState({
            isDraggable:false,
            isResizable:false,
            statisticsItems:this.getLayout(oldTemLayout)
        })
    };
    // 取消的时候把临时变量布局恢复
    initLayOuts =()=> {
        const {statisticsItems} = this.state;
        let obj = {};
        for(let i = 0; i<statisticsItems.length;i++) {
            obj[statisticsItems[i].url] = statisticsItems[i].layouts;
        }
        return obj;
    };
    //设置布局
    getLayout = (temLayout) => {
        let arr = this.state.statisticsItems.map((item) => {
            for (let key in temLayout) {
                if(item.url == key) {
                    item.layouts = temLayout[item.url];
                }
            }
            return item
        });
        return arr
    };
    onLayoutChange = (temLayout, url) => {  
        //将变化的布局用oldTemLayout保存  
        let obj = oldTemLayout;
        obj[url] = temLayout;
        oldTemLayout = obj;
        //将变化的布局setstate，实现大模块移动时小模块布局不变
        this.setState({
            statisticsItems:this.getLayout(obj)
        })
    };
    //重置layout
    resetLayout = () => {
        // 清空条件刷新
        let that = this;
        let id = cookie.get('userId');
        axios.post(DOMAIN_VIP+"/setting/delete",qs.stringify({
            userid:  id,
            type: 12
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let arr = this.state.statisticsItems;
                let newArr = arr.map((item) => {
                    item.layouts = {};
                    return item;
                });
                this.setState({
                    statisticsItems: newArr
                });
                oldTemLayout = {}
            }else{
                message.warning(result.msg);
            }
        });
    };
    onDelete = (key) => {
        let first = key.split('-')[0]
        let last = key.split('-')[1]
        this.setState(({ statisticsItems }) => {
            return {
                statisticsItems: statisticsItems.map(item => {
                    if (item.id == first) {
                        item.fakeData = item.fakeData.filter(elem => elem.key != key)
                    }
                    return item
                })
            }
        })
    }
    render() {
        const { setKey, modalHtml, modalTitle, modalVisible, modalWidth, isDraggable, isResizable, iconIsSpin, statisticsItems } = this.state
        return (
            <div className="right-con">
                <div>
                    <CockpitSelectModal 
                    isShow={true} 
                    value={setKey} 
                    iconIsSpin={iconIsSpin} 
                    handleChange={this.selectSeting} 
                    update={this.update} 
                    isDraggable={isDraggable} 
                    saveLayout={this.saveLayout}
                    handleCancel={this.cancelLayout}
                    />
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel" style={{ padding: 0, background: 'transparent', border: 'none' }}>
                            <div className="x_content" style={{ padding: 0 }}>
                                <CountStatisticsModal list={this.state.topList}  />
                                <SortableComponent
                                    statisticsItems={statisticsItems}
                                    fakeData={fakeData}
                                    saveToLS={this.saveToLS}
                                    getLayout={this.getLayout}
                                    isDraggable={isDraggable}
                                    isResizable={isResizable}
                                    onSortEnd={this.onSortEnd}
                                    onLayoutChange={this.onLayoutChange}
                                    onDelete={this.onDelete}
                                    _this={this.props._this}
                                />
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

export default CockpitOverview