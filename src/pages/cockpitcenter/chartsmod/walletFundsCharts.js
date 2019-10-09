import React, { Component } from 'react';
import echarts from 'echarts'

export default class WalletFundsCharts extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: false
        }

    }
    componentDidMount() {
        console.log(this.refs.WalletFundsCharts)
        let myChart = echarts.init(this.refs.WalletFundsCharts)
        myChart.setOption({
            title: {
                text: '钱包资金分布',
                // backgroundColor:'gray'
                right:20,
                top:20,
            },
            tooltip: {},
            legend: {
                bottom: 10,
                left: 'center',
                data: [{ name: '热充钱包', icon: 'circle' }, { name: '冷钱包', icon: 'circle' }, { name: '热提钱包', icon: 'circle' }]
            },
            series: [{
                selectedMode: 'single',
                label:{
                    formatter:(params) => {
                        console.log(params)
                        
                    }
                },
                animation: false,
                name: '销量',
                type: 'pie',
                radius: '55%',
                data: [
                    { value: 235, name: '热充钱包' },
                    { value: 274, name: '冷钱包' },
                    { value: 310, name: '热提钱包' },
                ]
            }]
        });
    }
    componentWillReceiveProps() {

    }
    render() {
        const { showHide, } = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 财务中心 > 期货流水明细 > 杠杆调整记录
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6" style={{border:'1px solid gray',padding:'0 0 0 0'}}>
                                <div  style={{backgroundColor:'gray',lineHeight:'40px',width:'100%'}}>总计&nbsp;100USDT</div>
                                    <div ref='WalletFundsCharts' style={{ minHeight: '500px' }}>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}