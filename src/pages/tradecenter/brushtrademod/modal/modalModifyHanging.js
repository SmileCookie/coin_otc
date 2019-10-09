import React from 'react'

export default class ModalModifyHanging extends React.Component{
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

    render(){
        const { item } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12 gbcmodal">

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">市场：</label>
                        <div className="col-sm-7">
                            <input type="text" className="form-control"  name="market" value={item.market||''}  readOnly/>
                        </div> 
                    </div>
                </div> 
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> 原先值。无法修改，仅传参 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">自挂撤账号：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control"  name="supCancelUserId" value={item.supCancelUserId||''}  onChange={this.handleInputChange}/>
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
                            <input type="number" className="form-control"  name="pollingTime" value={item.pollingTime||''} onKeyUp={this.checkNum} onChange={this.handleInputChange}/>
                        </div> 
                    </div>
                </div> 
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34">	>=1 数字 </p>
                </div>
                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">挂单数量比例区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="supCancelPlaceNumRateStart" value={item.supCancelPlaceNumRateStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="supCancelPlaceNumRateEnd" value={item.supCancelPlaceNumRateEnd||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34">（0,1）大于0小于1  数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">每秒挂单笔数区间：</label>
                        <div className="col-sm-7">
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="supCancelCountStart" value={item.supCancelCountStart||''} onChange={this.handleInputChange}/>
                            </div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box">
                                <input type="number" className="form-control" name="supCancelCountEnd" value={item.supCancelCountEnd||''} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >=1 整数数字 </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">撤单比例：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control"  name="supCancelRate" value={item.supCancelRate||''}  onChange={this.handleInputChange}/>
                        </div> 
                    </div>
                </div> 
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> （0,1）大于0小于1   数字  </p>
                </div>

                <div className="col-md-8 col-sm-8 col-xs-8">
                    <div className="form-group">
                        <label className="col-sm-5 control-label-large">挂撤单操作范围的深度：</label>
                        <div className="col-sm-7">
                            <input type="number" className="form-control" name="supCancelDepth" value={item.supCancelDepth||''} onChange={this.handleInputChange}/>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 col-sm-4 col-xs-4">
                    <p className="line34"> >1 整数数字 </p>
                </div>
                
            </div>
        )
    }
}












































