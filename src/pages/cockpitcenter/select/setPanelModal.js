import React from 'react'
import { Tabs, Table, Radio, Checkbox, Row } from 'antd'
import { employerFakeData, amountFakeData, userFlowFakeData, tradeFakeData, ONE, TWO, THREE, FOUR } from './selectData'
const TabPane = Tabs.TabPane;
const { Column } = Table;
const RadioGroup = Radio.Group;


export default class SetPanelModal extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            selectedRowKeys: {},//选中项的 key 数组
            selectedRows: {},//选中项的 item 数组
            radioCurrency: 'USDT',
            radioLegal: 'CNY',
            panelTabKey: '2',
            statisticsItems: [],
            dataSource: {1: [],2: [],3: [],4: []}
        }
    }

    componentDidMount() {
        const { radioCurrency, radioLegal, statisticsItems } = this.props
        this.setState({
            radioCurrency,
            radioLegal,
            statisticsItems,
            dataSource:{1:this.chooseData(ONE),2: this.chooseData(TWO),3: this.chooseData(THREE), 4: this.chooseData(FOUR)}
        },()=>{
            this.getNewcheckbox();
        });
    }

    async componentWillReceiveProps(nextProps) {
        const { radioCurrency, radioLegal, statisticsItems } = nextProps
        await this.setState({
            radioCurrency,
            radioLegal,
            statisticsItems,
            panelTabKey: '2'
        });
        this.getNewcheckbox();
    }
    //table 多选框按钮选中时
    onSelectChangeTable = (selectedRowKeys) => {
        let {panelTabKey,selectedRows} = this.state;
        let that = this;
        selectedRows[panelTabKey] = [];
        selectedRowKeys.forEach(function (k) {
            that.state.dataSource[panelTabKey].forEach(function (item) {
                if(k == item.key) {
                    selectedRows[panelTabKey].push(item);
                }
            });
        });
        let rowKeys = this.state.selectedRowKeys;
        rowKeys[panelTabKey]= selectedRowKeys;
        this.setState({
            selectedRowKeys: rowKeys,
            selectedRows,
        });
        this.getNewcheckbox(this.state.panelTabKey);
        this.props.onSelectChangeTable(selectedRowKeys, selectedRows)
    }
    //多选框置灰对于checkbox
    getNewcheckbox = (panelTabKey) => {
        let { statisticsItems,selectedRowKeys,selectedRows,dataSource } = this.state;
        if(panelTabKey) {
            // 操作者一个tab下面的判断剩余的是否可编辑
            dataSource[panelTabKey].forEach(function (item) {
                let list = selectedRowKeys[panelTabKey];
                if(list.length >2) {
                    if(list.indexOf(item.key) != '-1') {
                        item.isdisabled = false;
                    } else {
                        item.isdisabled = true;
                    }
                }else {
                    item.isdisabled = false;
                }
            });
        } else {
            if(statisticsItems == null){
                return;
            }
            for (let i = 0; i < statisticsItems.length; i++) {
                let item = statisticsItems[i];
                // 初始化选中的id
                selectedRowKeys[item.id] = item.fakeData.map(fData=>{
                    return Number(fData.key.slice(fData.key.indexOf('-')+1));
                });
                // 初始化选中的rows
                selectedRows[item.id] = [];
                dataSource[item.id].forEach(function (row) {
                    if(selectedRowKeys[item.id].indexOf(row.key) != '-1') {
                        selectedRows[item.id].push(row);
                    }
                });
            }
            let panelKey = this.state.panelTabKey;
            dataSource[panelKey].forEach(function (item) {
                let list = selectedRowKeys[panelKey] || [];
                if(list.length >2) {
                    if(list.indexOf(item.key) != '-1') {
                        item.isdisabled = false;
                    } else {
                        item.isdisabled = true;
                    }
                } else {
                    item.isdisabled = false;
                }
            });
        }
        this.setState({
            dataSource,
            selectedRowKeys,
            selectedRows
        });
    };
    //输入时 input 设置到 satte
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }
    onSelect = (record, selected, selectedRows, nativeEvent) => {
        // console.log(record, selected, selectedRows, nativeEvent)
    }
    tabCallback = async (tabKey) => {
        // console.log(tabKey)
        await this.setState({
            panelTabKey: tabKey,
        });
        // this.props.tabCallback && this.props.tabCallback(tabKey);
        this.getNewcheckbox(tabKey);
    }
    //选择dataSource
    chooseData = type => {
        switch (type) {
            case ONE:
                return employerFakeData
            case TWO:
                return amountFakeData
            case THREE:
                return tradeFakeData
            case FOUR:
                return userFlowFakeData
            default:
                break
        }
    }
    render() {
        const { selectedRowKeys, selectedRows, panelTabKey, radioCurrency, radioLegal,dataSource } = this.state
        const rowSelection = {
            selectedRowKeys,
            selectedRows,
            onChange: this.onSelectChangeTable,
            getCheckboxProps: this.getCheckboxProps,
            onSelect: this.onSelect,
            fixed: true
        };
        // console.log(this.props)
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                {this.props.setKey == '1' ? <div><Tabs className="cock_tab" defaultActiveKey={panelTabKey} activeKey={panelTabKey} onChange={this.tabCallback}>
                    {/*<TabPane tab="项目统计" key="1"></TabPane>*/}
                    <TabPane tab="金额统计" key="2"></TabPane>
                    <TabPane tab="交易统计" key="3"></TabPane>
                    <TabPane tab="用户统计" key="4"></TabPane>
                </Tabs>
                    <div className="col-md-12 col-sm-12 col-xs-12 table-rap-3 side-hidden-print-cock" style={{ height: '500px', overflow: 'auto' }}>
                        {/*<Table*/}
                        {/*dataSource={dataSource}*/}
                        {/*showHeader={false}*/}
                        {/*rowSelection={rowSelection}*/}
                        {/*locale={{ emptyText: '暂无数据' }}*/}
                        {/*pagination={false}*/}
                        {/*>*/}
                        {/*<Column width='150px' title='序号' dataIndex='img' key='img' render={text =>*/}
                        {/*<div style={{ width: '120px', height: '90px',}}>*/}
                        {/*<img style={{ width: '100%', height: '100%' }} src={text} alt='图片' />*/}
                        {/*</div>*/}
                        {/*} />*/}
                        {/*<Column title='序号' dataIndex='t' key='t' render={(text, record) =>*/}
                        {/*<div>*/}
                        {/*<p>{record.title}</p>*/}
                        {/*<p>{record.description}</p>*/}
                        {/*</div>*/}
                        {/*} />*/}
                        {/*</Table>*/}
                        <Checkbox.Group style={{ width: '100%' }} value={selectedRowKeys[panelTabKey]} onChange={this.onSelectChangeTable}>
                            {dataSource[panelTabKey].map((row) => {
                                return (
                                    <div className="cockpit_modal" key={row.key}>
                                        <Checkbox disabled={row.isdisabled} style={{float: 'left',marginTop: '4%'}} value={row.key}></Checkbox>
                                        <div style={{ width: '130px', height: '92px', marginLeft: '10px',float: 'left', border: '1px solid #E7E9ED', borderRadius: '2px'}}>
                                            <img style={{ width: '60%', height: '60%', marginTop: '15%', marginLeft: '20%' }} src={row.img} alt='图片' />
                                        </div>
                                        <div style={{ marginLeft: '10px',float: 'left',width: '68%'}}>
                                            <p style={{fontSize: '13px', margin: '0px', lineHeight: '24px', fontWeight: '600',marginBottom: '4px'}}>{row.title}</p>
                                            <p style={{fontSize: '12px', margin: '0px', lineHeight: '18px',color: '#a8a4a4'}}>{row.description}</p>
                                        </div>
                                    </div>
                                );
                            })}
                        </Checkbox.Group>
                    </div>
                </div> : ''}
                {
                    this.props.setKey == '4' && <div>
                        <div className="form-group radio_group_box" style={{marginTop:'20px'}}>
                            <label className="col-sm-12 control-label cock_title">统计货币设置</label><br />
                            <div className="col-sm-12" style={{paddingLeft: '20px'}}>
                                <RadioGroup className="radio_val_box" onChange={this.handleInputChange} name='radioCurrency' value={radioCurrency}>
                                    <Radio value="USDT">USDT</Radio>
                                    <Radio value="BTC">BTC</Radio>
                                    <Radio value="ETH">ETH</Radio>
                                </RadioGroup>
                            </div>
                        </div>
                        <div className="form-group radio_group_box">
                            <label className="col-sm-12 control-label cock_title">统计法币设置</label><br />
                            <div className="col-sm-12" style={{paddingLeft: '20px'}}>
                                <RadioGroup className="radio_val_box second_radio_val_box" onChange={this.handleInputChange} name='radioLegal' value={radioLegal}>
                                    <Radio value="USD">USD</Radio>
                                    <Radio value="CNY">CNY</Radio>
                                    <Radio value="GBP">GBP</Radio>
                                </RadioGroup>
                            </div>
                        </div>
                    </div>
                }
            </div>
        )
    }
}