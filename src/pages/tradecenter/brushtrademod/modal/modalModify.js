
import React from 'react'
import axios from '../../../../utils/fetch'
import { DOMAIN_VIP } from '../../../../conf'
import { message } from 'antd'

export default class ModalModify extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            item: {}
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.checkNum = this.checkNum.bind(this)
    }

    componentDidMount() {
        this.setState({
            item: this.props.item
        })
    }
    componentWillReceiveProps(nextProps) {
        this.setState({
            item: nextProps.item
        })
    }
    checkNum(e) {
        let num = e.target.value
        let name = e.target.name
        if (!(/(^[1-9]\d*$)/.test(num))) {
            this.setState((preState) => {
                preState.item[name] = num.substr(0, num.length - 1)
                return { item: preState.item }
            })
            return false;
        }
    }

    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState((preState) => {
            preState.item[name] = value
            return { item: preState.item }
        });
    }

    render() {
        const { item } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12 gbcmodal">
                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">市场：</label>
                        <div className="col-sm-7">
                            <input type="text" className="form-control" name="market" value={item.market || ''} readOnly />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> 原先值。无法修改，仅传参 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">量化交易账户：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="qtUserId" value={item.qtUserId || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> 	>=1 整数数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">刷单账户：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="qtSuperUserId" value={item.qtSuperUserId || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> 	>=1 整数数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">主程序等待时间(秒)：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="pollingTime" value={item.pollingTime || ''} onKeyUp={this.checkNum} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34">	>=1 数字 </p>
                </div>
                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">盘口厚度高量区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="highNumberStart" value={item.highNumberStart || ''} onChange={this.handleInputChange} />
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="highNumberEnd" value={item.highNumberEnd || ''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">盘口厚度低量区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="lowNumberStart" value={item.lowNumberStart || ''} onChange={this.handleInputChange} />
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="lowNumberEnd" value={item.lowNumberEnd || ''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">盘口高低量比：</label>
                        <div className="col-sm-7">
                            <input type="text" className="form-control" name="numberRatio" value={item.numberRatio || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> 整数数字比例，例如(1:3,2:5),整数:整数 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">盘口密度区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="priceRatioStart" value={item.priceRatioStart || ''} onChange={this.handleInputChange} />
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="priceRatioEnd" value={item.priceRatioEnd || ''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">成交用户单数量区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="dealUserNumberStart" value={item.dealUserNumberStart || ''} onChange={this.handleInputChange} />
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="dealUserNumberEnd" value={item.dealUserNumberEnd || ''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字 </p>
                </div>
                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">外网成交价格浮动比例：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="thirdPlatformPriceBase" value={item.thirdPlatformPriceBase || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字 </p>
                </div>
                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">完全成交需要第三方平台数量是用户单数量的固定倍数：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="thirdPlatformNumberBase" value={item.thirdPlatformNumberBase || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> 	>1 数字 </p>
                </div>
                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">撤单时买一卖一价格安全差值：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="safePriceInterval" value={item.safePriceInterval || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">刷量时撤单前吃单的比例：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="brushForecastCancelDealRate" value={item.brushForecastCancelDealRate || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34">（0,1）大于0小于1   数字</p>
                </div>
                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">铺单时需要批量铺单的价格差比例：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="brushBuildBatchRate" value={item.brushBuildBatchRate || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34">（0,1）大于0小于1   数字 </p>
                </div>


                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">卖盘盘口厚度高量区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="highNumberStartSell" value={item.highNumberStartSell || ''} onChange={this.handleInputChange} />
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="highNumberEndSell" value={item.highNumberEndSell || ''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">卖盘盘口厚度低量区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="lowNumberStartSell" value={item.lowNumberStartSell || ''} onChange={this.handleInputChange} />
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="lowNumberEndSell" value={item.lowNumberEndSell || ''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">卖盘盘口高低量比：</label>
                        <div className="col-sm-7">
                            <input type="text" className="form-control" name="numberRatioSell" value={item.numberRatioSell || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> 整数数字比例，例如(1:3,2:5),整数:整数(为空时只使用上面参数) </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">目标买一浮动比例：</label>
                        <div className="col-sm-7">
                            <input type="text" className="form-control" name="floatPriceBuy" value={item.floatPriceBuy || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> 任何数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">目标买一卖一价差比例：</label>
                        <div className="col-sm-7">
                            <input type="text" className="form-control" name="targetPriceRate" value={item.targetPriceRate || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >=0 数字 </p>
                </div>
            </div>
        )
    }
}












































