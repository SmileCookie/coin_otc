
import React from 'react'
import axios from '../../../../utils/fetch'
import { DOMAIN_VIP } from '../../../../conf'


export default class ModalModify extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            item:{}  
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.checkNum = this.checkNum.bind(this)
    }

    componentDidMount(){
       this.setState({
           item:this.props.item
       })
    }
    componentWillReceiveProps(nextProps){
        this.setState({
            item:nextProps.item
        })
    }

    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState((preState) => {
            preState.item[name] = value
            return {item:preState.item}
        });
    }

    checkNum(e){
        let num = e.target.value
        let name = e.target.name
        if (!(/(^[1-9]\d*$)/.test(num))) { 
            this.setState((preState) => {
                preState.item[name] = num.substr(0,num.length-1)
                return {item:preState.item}
            }) 
　　　　　　  return false; 
　　　　 }
    }

    render(){
        const { item } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12 gbcmodal">

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">市场：</label>
                        <div className="col-sm-7">
                            <input type="text" className="form-control"  name="market" value={item.market||''}  readOnly/>
                        </div> 
                    </div>
                </div> 

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">GBC刷量用户ID：</label>
                        <div className="col-sm-7">
                            <input type="text" className="form-control"  name="qtUserId" value={item.qtUserId||''}  onChange={this.handleInputChange}/>
                        </div> 
                    </div>
                </div> 

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">目标价格上限区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="targetPriceUpperStart" value={item.targetPriceUpperStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="targetPriceUpperEnd" value={item.targetPriceUpperEnd||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">目标价格下限区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="targetPriceLowerStart" value={item.targetPriceLowerStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="targetPriceLowerStart" value={item.targetPriceLowerStart||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">目标价格区间随机生成频率(秒)：</label>
                        <div className="col-sm-7">
                            <input type="text" className="form-control" name="targetPriceChangeTime" value={item.targetPriceChangeTime||''} onKeyUp={this.checkNum} onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">目标价格生命周期(秒)：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="targetPriceLifecycleStart" value={item.targetPriceLifecycleStart||''} onKeyUp={this.checkNum} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="targetPriceLifecycleEnd" value={item.targetPriceLifecycleEnd||''} onKeyUp={this.checkNum} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">预警买一卖一价：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="warnBuyPrice" value={item.warnBuyPrice||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="warnSellPrice" value={item.warnSellPrice||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">委托触发倒计时区间(秒)：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustPollingTimeStart" onKeyUp={this.checkNum} value={item.entrustPollingTimeStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustPollingTimeEnd" onKeyUp={this.checkNum} value={item.entrustPollingTimeEnd||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">委托价格浮动区间(输入小数)：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustPriceStart" value={item.entrustPriceStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustPriceEnd" value={item.entrustPriceEnd||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">委托量区间(GBC)：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustAmountStart" value={item.entrustAmountStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustAmountEnd" value={item.entrustAmountEnd||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">委托单个数区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustOrderNumberStart" value={item.entrustOrderNumberStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustOrderNumberEnd" value={item.entrustOrderNumberEnd||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">回弹触发倒计时区间(秒)：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustConversePollingTimeStart" value={item.entrustConversePollingTimeStart||''} onKeyUp={this.checkNum} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustConversePollingTimeEnd" value={item.entrustConversePollingTimeEnd||''} onKeyUp={this.checkNum} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">回弹价格浮动区间(输入小数)：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustConversePriceStart" value={item.entrustConversePriceStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustConversePriceEnd" value={item.entrustConversePriceEnd||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">回弹量区间(GBC)：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustConverseAmountStart" value={item.entrustConverseAmountStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustConverseAmountEnd" value={item.entrustConverseAmountEnd||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-5 control-label">回弹量单个数区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustConverseOrderNumberStart" value={item.entrustConverseOrderNumberStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="text" className="form-control" name="entrustConverseOrderNumberEnd" value={item.entrustConverseOrderNumberEnd||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        )
    }
}












































