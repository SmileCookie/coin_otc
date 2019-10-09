import React, { Component } from 'react';
import ReactEcharts from 'echarts-for-react'
import { Icon, Statistic,Spin } from 'antd'
// import { toThousands } from '../../../../utils'
import { chartsLoading } from '../../getdata/asyncGetData'

export default class PieModal extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: false,
            loading: false,
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
        // this.props.getData&&this.props.getData().then(payload => {
        //     console.log(payload)
        //     this.setState({
        //         loading:payload.loading,
        //         title:payload.title
        //     })
        // })
        const { option } = this.props
        this.setState({
            loading:option.loading,
            option:option.option
        })
        console.log(this.props.option)
        // this.setState({
        //     loading:this.props.getData.loading
        // })

    }
    componentWillReceiveProps(nextProps) {
        console.log(nextProps.option)
        const { option } = nextProps
        this.setState({
            loading:option.loading,
            option:option.option
        })
    }
    render() {
        const { option } = this.state
        return (
            this.state.loading ?
                <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)' }}>
                    <Spin indicator={<Icon type="loading" style={{ fontSize: 60 }} spin />}></Spin>
                </div>
                :
                <ReactEcharts
                    ref={(e) => { this.echarts_react = e; }}
                    option={option}
                    notMerge={true}
                    style={{ width: '100%', height: '100%' }}
                />
            
        )
    }
}