import React, { Component } from 'react';
import ReactEcharts from 'echarts-for-react'
import { Icon, Statistic, Spin } from 'antd'
// import { toThousands } from '../../../../utils'
import { chartsLoading } from '../../getdata/asyncGetData'
import { TradeTable } from '../tableComponent/tradeTable'

export default class EchartsModal extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: false,
            loading: false,
            showTable: false,
            noData: false,
            totalTitle:'',
            option: {
                title: {
                    text: this.props.title ? this.props.title : '',
                    // subtext: 'ECharts 示例副标题',
                },
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    // orient: 'vertical',
                    // top: 'middle',
                    bottom: 0,
                    left: 'center',
                    icon: 'circle',
                    data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎']
                },
                series: [{
                    name: '销量',
                    type: 'pie',
                    selectedMode: 'single',
                    selectedOffset: 10,
                    animation: false,
                    label: {
                        show: true,
                        position: 'outside',
                        formatter: "{b} \n {c} ({d}%)"
                    },
                    data: [
                        { value: 3335, name: '直接访问' },
                        { value: 310, name: '邮件营销' },
                        { value: 234, name: '联盟广告' },
                        { value: 135, name: '视频广告' },
                        { value: 1548, name: '搜索引擎' }
                    ],
                }]
            }
        }

    }
    componentDidMount() {
        const { option } = this.props
        this.setState({
            loading: option.loading || false,
            option: option.option,
            showTable: option.showTable || false,
            noData: option.noData || false,
            totalTitle:option.totalTitle || '',
            isImage: option.isImage,
            persentData: option.persentData
        })
        // console.log(this.props.option)

    }
    componentWillReceiveProps(nextProps) {
        // console.log(nextProps.option)
        const { option } = nextProps
        this.setState({
            loading: option.loading || false,
            option: option.option,
            showTable: option.showTable || false,
            noData: option.noData || false,
            totalTitle:option.totalTitle || '',
            isImage: option.isImage,
            persentData: option.persentData
        })
    }
    render() {
        const { option, showTable, noData,totalTitle,isImage, persentData } = this.state;
        return (
            this.state.loading ?
                <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)',textAlign: 'center' }}>
                    <Spin indicator={<Icon type="loading" style={{ fontSize: 60 }} spin />}></Spin>
                </div>
                :
                noData
                    ?
                    <p style={{ position: 'absolute', top: '50%', left: '50%', fontSize: '16px', transform: 'translate(-50%,-50%)', textAlign: 'center'}}>{noData}</p>
                    :
                    isImage ?
                        <div style={{position: 'relative'}}>
                            <img title="图片" alt="图片" style={{width:'60%',height: '100%', marginTop: '6%',marginLeft: '20%'}} src={isImage} />
                            <p style={{position: 'absolute', top:'40%',right: '30%'}}>
                                <span>委托转化率</span><br/>
                                <span>{persentData[0].entrustrate}%</span>
                            </p>
                            <p style={{position: 'absolute', top:'52%',right: '18%'}}>
                                <span>交易转化率</span><br/>
                                <span>{persentData[0].traderate}%</span>
                            </p>
                            <p style={{position: 'absolute', top:'66%',right: '36%'}}>
                                <span>成交转化率</span><br/>
                                <span>{persentData[0].transactionrate}%</span>
                            </p>
                        </div>
                        :
                        <div style={{ height: '100%', width: '100%', display: 'flex', position: 'relative'}}>
                            <div className='echarts-total-title'>
                                {totalTitle&&totalTitle}
                            </div>
                            <ReactEcharts
                                ref={(e) => { this.echarts_react = e; }}
                                option={option}
                                notMerge={true}
                                style={{ width: showTable ? '55%' : '100%', height: '100%' }}
                            />
                            {showTable && <div style={{ width: '45%',overflow:'auto' }}>
                                <TradeTable columns={this.props.option.tableOption.columns} tableData={this.props.option.tableOption.tableData} loading={false} />
                            </div>}
                        </div>

        )
    }
}