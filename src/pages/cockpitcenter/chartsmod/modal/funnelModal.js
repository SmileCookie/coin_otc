import React, { Component } from 'react';
import ReactEcharts from 'echarts-for-react'
import { Icon, Statistic, } from 'antd'
// import { toThousands } from '../../../../utils'

export default class FunnelModal extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: false,
            option: {
                title: {
                    text: this.props.title ? this.props.title : '',
                    subtext: 'ECharts 示例副标题',
                },
                tooltip: {},
                legend: {
                    // orient: 'vertical',
                    // top: 'middle',
                    bottom: 0,
                    left: 'center',
                    icon:'circle',
                    data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎']
                },
                series: [{
                    name: '销量',
                    type: 'funnel',
                    selectedMode: 'single',
                    selectedOffset: 10,
                    animation: false,
                    left: 'center',
                    top: 60,
                    //x2: 80,
                    bottom: 60,
                    width: '80%',
                    // height: {totalHeight} - y - y2,
                    min: 0,
                    max: 100,
                    minSize: '0%',
                    maxSize: '100%',
                    sort: 'descending',
                    gap: 2,
                    label: {
                        show: true,
                        position: 'inside',
                        formatter: "{a} {b} : {c} ({d}%)"
                    },
                    data: [
                        { value: 20, name: '直接访问' },
                        { value: 100, name: '邮件营销' },
                        { value: 80, name: '联盟广告' },
                        { value: 40, name: '视频广告' },
                        { value: 60, name: '搜索引擎' }
                    ],
                }]
            }
        }

    }
    componentDidMount() {


    }
    componentWillReceiveProps() {

    }
    render() {
        const { option } = this.state
        return (
            <ReactEcharts
                ref={(e) => { this.echarts_react = e; }}
                option={option}
                notMerge={true}
                style={{ width: '100%', height: '100%' }}
            />
            // '暂无数据'
        )
    }
}