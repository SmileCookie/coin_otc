import React, { Component } from 'react';
import ReactEcharts from 'echarts-for-react'
import { Icon, Statistic,Spin } from 'antd'
// import { toThousands } from '../../../../utils'

export default class BarModal extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: false,
            option: {
                title: { text: this.props.title ? this.props.title : '' },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        crossStyle: {
                            color: '#999'
                        }
                    }
                },
                legend: {
                    // orient: 'vertical',
                    // top: 'middle',
                    top: 0,
                    left: 'center',
                    icon: 'circle',
                    data: ['买入', '卖出']
                },
                xAxis: [{
                    type: 'category',
                    axisLabel: {
                        showMaxLabel: true
                    },
                    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
                }],
                yAxis: [{}],
                series: [
                    {
                        name: '买入',
                        stack: '总量',
                        label: {
                            show: true,
                        },
                        type: 'bar',
                        data: [5, 20, 36, 10, 10, 30, 50, 20, 36, 10, 15, 20]
                    },
                    {
                        name: '卖出',
                        type: 'bar',
                        stack: '总量',
                        label: {
                            show: true,
                        },
                        data: [8, 20, 30, 15, 10, 20, 5, 20, 34, 14, 10, 20]
                    },
                    {
                        name: '折现',
                        type: 'line',
                        data: [8, 20, 30, 15, 10, 20, 5, 20, 34, 14, 10, 20]
                    },
                ]
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
             <div> {false? <ReactEcharts
                ref={(e) => { this.echarts_react = e; }}
                option={option}
                notMerge={true}
                style={{ width: '100%', height: '100%' }}
            /> :<Spin indicator={<Icon type="loading" style={{ fontSize: 24,textAlign:'center',lineHeight:'100%' }} spin />}></Spin>}</div>
        )
    }
}