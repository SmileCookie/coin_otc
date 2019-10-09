import React from 'react'

export default class ModalModifyHanging extends React.Component {
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
                        <label className="col-sm-5 control-label-large">自成交账号：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="userId" value={item.userId || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> 	>=1 整数数字 </p>
                </div>

                {/* <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">主程序等待时间(秒)：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control"  name="pollingTime" value={item.pollingTime||''} onKeyUp={this.checkNum} onChange={this.handleInputChange}/>
                        </div> 
                    </div>
                </div> 
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34">	>=1 数字 </p>
                </div> */}

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">主程序调用频次：</label>
                        <div className="col-sm-7">
                            <div className='left line34'>每</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="pollingTime" value={item.pollingTime || ''} onChange={this.handleInputChange} />
                            </div>

                            <div className="left line34">秒，调用</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="pollingFrequency" value={item.pollingFrequency || ''} onChange={this.handleInputChange} />
                            </div>
                            <div className='left line34'>次</div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">自成交挂单总数量区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="numberStart" value={item.numberStart || ''} onChange={this.handleInputChange} />
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="numberEnd" value={item.numberEnd || ''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">每次成交笔数：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="count" value={item.count || ''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >0  数字  </p>
                </div>

            </div>
        )
    }
}












































