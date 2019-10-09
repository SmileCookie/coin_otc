import React, { Component } from 'react';
import ReactEcharts from 'echarts-for-react'
import { Icon, Statistic, } from 'antd'
// import { toThousands } from '../../../../utils'

export default class LineModal extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: false,
            option: {
                title: {
                    text: this.props.title ? this.props.title : '',
                },
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    top:20,
                    data: ['邮件营销', '联盟广告', '视频广告', '直接访问', '搜索引擎']
                },
                xAxis: {
                    name: '日期',
                    type: 'category',
                    boundaryGap: false,
                    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                },
                yAxis: [
                    {
                        name: '护甲值',
                        type: 'value',
                        // max: 500
                    },
                    {
                        name: '血量',
                        // nameLocation: 'start',
                        // max: 5,
                        type: 'value',
                        // inverse: true
                    }
                ],
                series: [
                    // {
                    //     name: '销量',
                    //     type: 'line',
                    //     smooth: true,//曲线
                    //     data: [5, 20, 36, 10, 10]
                    // }
                    {
                        name: '邮件营销',
                        yAxisIndex: 1,
                        type: 'line',
                        // stack: '总量',
                        data: [120, 132, 101, 134, 90, 230, 210]
                    },
                    {
                        name: '联盟广告',
                        type: 'line',
                        // stack: '总量',
                        data: [220, 182, 191, 234, 290, 330, 310]
                    },
                    {
                        name: '视频广告',
                        type: 'line',
                        // stack: '总量',
                        data: [150, 232, 201, 154, 190, 330, 410]
                    },
                    {
                        name: '直接访问',
                        type: 'line',
                        // stack: '总量',
                        data: [320, 332, 301, 334, 390, 330, 320]
                    },
                    {
                        name: '搜索引擎',
                        type: 'line',
                        // stack: '总量',
                        data: [820, 932, 901, 934, 1290, 1330, 130]
                    }
                ]
            }
        }

    }
    componentDidMount() {
console.log(this.props)
    }
    componentWillReceiveProps(nextProps) {
console.log(nextProps)
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
        )
    }
}