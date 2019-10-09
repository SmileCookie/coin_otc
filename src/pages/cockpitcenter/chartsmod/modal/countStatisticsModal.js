import React, { Component } from 'react';
import echarts from 'echarts'
import { Icon, Statistic, Tooltip } from 'antd'
import { toThousands } from '../../../../utils'

export default class CountStatisticsModal extends Component {
    constructor(props) {
        super(props)
        this.state = {
            type: props.type,
            list: [1, 2, 3, 4],
            // currencyType: props.currencyType
        }

    }
    // static get defaultProps() {
    //     return {
    //         type: false,
    //         currencyType: 'USDT'
    //     }
    // }
    componentDidMount() {
        this.setState({
            list: this.props.list
        })
    }
    componentWillReceiveProps(nextProps) {
        this.setState({
            list: nextProps.list
        })
    }

    render() {
        const { type } = this.state;
        const styleObjFirst = {
            border: '1px solid #EAEEF2',
            marginLeft: '0px',
            borderRadius: '5px',
            height: '100px',
            backgroundColor: '#fff',
            boxShadow: '0 2px 4px 0 rgba(234,236,239,0.70)',
            padding: '10px 0',
            boxSizing: 'content-box',
            flex: 1
        };
        const styleObj = {
            border: '1px solid #EAEEF2',
            borderRadius: '5px',
            marginLeft: '10px',
            height: '100px',
            backgroundColor: '#fff',
            boxShadow: '0 2px 4px 0 rgba(234,236,239,0.70)',
            padding: '10px 0',
            boxSizing: 'content-box',
            flex: 1
        };
        let obj = JSON.parse(JSON.stringify(styleObjFirst));
        let obj2 = JSON.parse(JSON.stringify(styleObj));
        const projectObjFirst = Object.assign(obj, { height: '80px' });
        const projectObj = Object.assign(obj2, { height: '80px' });
        const colorDown = 'rgba(239, 6, 70, 1)'
        const colorUp = 'rgba(36,204,184,1)'
        return (
            <div>
                {!type ? (
                    <div className="row" style={{ display: 'flex', margin: 0 }}>
                        {
                            this.state.list.map((item, index) => {
                                return <div className="form-group" style={index == 0 ? styleObjFirst : styleObj} key={item.id || index} >
                                    <label className="col-sm-12" style={{ marginTop: '5px' }}>{item.startTitle}</label>
                                    {
                                        item.error ?
                                            <div className="center red">{item.error}</div>
                                            :
                                            <div>
                                                <div className="col-sm-12">今日<span className='fontWeight500'>{toThousands(item.todayup || 0)}</span> {item.unit}</div>
                                                <div className="col-sm-12">
                                                    环比<Tooltip placement="top" title={`对比${item.yesterdaytime}(${item.yesterday || ''})${item.sequential > 0 ? '上升' : '下降'}${item.sequential || 0}%`}>
                                                        <span style={{ color: item.sequential > 0 ? colorUp : colorDown }}><Icon type={item.sequential > 0 ? "caret-up" : "caret-down"} />{item.sequential || 0}%</span>
                                                    </Tooltip>
                                                    同比<Tooltip placement="top" title={`对比${item.yesterdaytime}(${item.yesterday || ''})${item.same > 0 ? '上升' : '下降'}${item.same || 0}%`}>
                                                        <span style={{ color: item.same > 0 ? colorUp : colorDown }}><Icon type={item.same > 0 ? "caret-up" : "caret-down"} ></Icon>{item.same || 0}%</span>
                                                    </Tooltip>
                                                </div>
                                                <div className="col-sm-12">{item.endTitle} {toThousands(item.todaydown || 0)} {item.unit}</div>
                                            </div>
                                    }
                                </div>
                            })
                        }

                        {/* <div className="form-group" style={styleObj}>
                            <label className="col-sm-12" style={{ marginTop: '5px' }}>新增会员</label>
                            <div className="col-sm-12">今日<span className='fontWeight500'>{toThousands(2222222)}</span>次</div>
                            <div className="col-sm-12">环比<span style={{ color: 'rgba(239, 6, 70, 1)' }}><Icon type='caret-up' ></Icon>33.33%</span>同比<Tooltip placement="topLeft" title="33.33%"><span style={{ color: 'rgba(239, 6, 70, 1)' }}><Icon type='caret-up' ></Icon>33.33%</span></Tooltip></div>
                            <div className="col-sm-12">总会员{toThousands(2222222)}人</div>
                        </div>
                        <div className="form-group" style={styleObj}>
                            <label className="col-sm-12" style={{ marginTop: '5px' }}>交易量</label>
                            <div className="col-sm-12">今日<span className='fontWeight500'>{toThousands(2222222)}</span>次</div>
                            <div className="col-sm-12">环比<span style={{ color: 'rgba(239, 6, 70, 1)' }}><Icon type='caret-up' ></Icon>33.33%</span>同比<Tooltip placement="topLeft" title="33.33%"><span style={{ color: 'rgba(239, 6, 70, 1)' }}><Icon type='caret-up' ></Icon>33.33%</span></Tooltip></div>
                            <div className="col-sm-12">总交易量{toThousands(2222222)}人</div>
                        </div>
                        <div className="form-group" style={styleObj}>
                            <label className="col-sm-12" style={{ marginTop: '5px' }}>收入</label>
                            <div className="col-sm-12">今日<span className='fontWeight500'>{toThousands(2222222)}</span>次</div>
                            <div className="col-sm-12">环比<span style={{ color: 'rgba(239, 6, 70, 1)' }}><Icon type='caret-up' ></Icon>33.33%</span>同比<Tooltip placement="topLeft" title="33.33%"><span style={{ color: 'rgba(239, 6, 70, 1)' }}><Icon type='caret-up' ></Icon>33.33%</span></Tooltip></div>
                            <div className="col-sm-12">总收入{toThousands(2222222)}人</div>
                        </div>
                        <div className="form-group" style={styleObj}>
                            <label className="col-sm-12" style={{ marginTop: '5px' }}>提现</label>
                            <div className="col-sm-12">今日<span className='fontWeight500'>{toThousands(2222222)}</span>次</div>
                            <div className="col-sm-12">环比<span style={{ color: 'rgba(239, 6, 70, 1)' }}><Icon type='caret-up' ></Icon>33.33%</span>同比<Tooltip placement="topLeft" title="33.33%"><span style={{ color: 'rgba(239, 6, 70, 1)' }}><Icon type='caret-up' ></Icon>33.33%</span></Tooltip></div>
                            <div className="col-sm-12">充提差率{toThousands(2222222)}人</div>
                        </div> */}
                    </div>
                ) : (
                        <div className="row" style={{ display: 'flex', margin: 0 }}>
                            <div className="form-group" style={projectObjFirst} >
                                <label className="col-sm-12" style={{ marginTop: '5px' }}>新增资方用户</label>
                                <div className="col-sm-12">本周<span className='fontWeight500'>{toThousands(2222222)}</span>人</div>
                                <div className="col-sm-12">今日{toThousands(10000000)}人</div>
                            </div>
                            <div className="form-group" style={projectObj}>
                                <label className="col-sm-12" style={{ marginTop: '5px' }}>收入贡献</label>
                                <div className="col-sm-12">今日<span className='fontWeight500'>{toThousands(2222222)}</span>USD</div>
                                <div className="col-sm-12">总会员{toThousands(10000000)}USD</div>
                            </div>
                            <div className="form-group" style={projectObj}>
                                <label className="col-sm-12" style={{ marginTop: '5px' }}>交易量</label>
                                <div className="col-sm-12">今日<span className='fontWeight500'>{toThousands(2222222)}</span>USD</div>
                                <div className="col-sm-12">总交易量{toThousands(10000000)}USD</div>
                            </div>
                            <div className="form-group" style={projectObj}>
                                <label className="col-sm-12" style={{ marginTop: '5px' }}>资方用户抛售</label>
                                <div className="col-sm-12">今日<span className='fontWeight500'>{toThousands(2222222)}</span>USD</div>
                                <div className="col-sm-12">总收入{toThousands(10000000)}USD</div>
                            </div>
                            <div className="form-group" style={projectObj}>
                                <label className="col-sm-12" style={{ marginTop: '5px' }}>普通用户购入</label>
                                <div className="col-sm-12">今日<span className='fontWeight500'>{toThousands(2222222)}</span>USD</div>
                                <div className="col-sm-12">充提差率{toThousands(10000000)}USD</div>
                            </div>
                        </div>
                    )}
            </div>

        )
    }
}