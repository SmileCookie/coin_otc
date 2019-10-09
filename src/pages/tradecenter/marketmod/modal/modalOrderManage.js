import React from 'react'
import { Select,Button,Radio,Input} from 'antd' 
import { SELECTWIDTH } from '../../../../conf'
import MarketList from '../../select/marketList'
const Option = Select.Option
const RadioGroup = Radio.Group
export default class ModalOrderManage extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            //visible:false,
            market: '',
            type: 0,
            maxprice:'',
            minprice:'',
            totalamount:'',
            totalcount: '',
            userid: '',
            status:0
        }
        //this.handleChangeType = this.handleChangeType.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChangeMarket = this.handleChangeMarket.bind(this)
    }
    componentDidMount(){
        this.setState({
            market: this.props.item.market||'etc_btc',
            type: this.props.item.type||0,
            maxprice:this.props.item.maxprice||'',
            minprice:this.props.item.minprice||'',
            totalamount:this.props.item.totalamount||'',
            totalcount: this.props.item.totalcount||'',
            userid: this.props.item.userid||'',
            status: this.props.item.status||0
        })
    }
    componentWillReceiveProps(nextProps){
        this.setState({
            market: nextProps.item.market||'',
            type: nextProps.item.type,
            maxprice:nextProps.item.maxprice,
            minprice:nextProps.item.minprice,
            totalamount:nextProps.item.totalamount,
            totalcount: nextProps.item.totalcount,
            userid: nextProps.item.userid,
            status: nextProps.item.status
        })
    }
    componentWillUnmount(){

    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }
    //市场 select
    handleChangeMarket(val){
        this.setState({
            market:val
        })
        this.props.handleChangeMarket(val)
    }
    render(){
        const { market,userid,type,minprice,maxprice,totalamount,totalcount,status } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">    
                    <MarketList market={market} col='3' handleChange={this.handleChangeMarket}/>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">用户编号：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control"  name="userid" value={userid||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">委托类型：</label>
                    <div className="col-sm-9">
                        <RadioGroup onChange={this.handleInputChange} name="type" value={type}>
                            <Radio value={1}>买盘</Radio>
                            <Radio value={0}>卖盘</Radio>
                        </RadioGroup>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">状态：</label>
                    <div className="col-sm-9">
                        <RadioGroup onChange={this.handleInputChange} name="status" value={status}>
                            <Radio value={1}>开</Radio>
                            <Radio value={0}>关</Radio>
                        </RadioGroup>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">下单价格：</label>
                    <div className="col-sm-8 ">
                        <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="minprice" value={minprice||''} onChange={this.handleInputChange} /></div>
                        <div className="left line34">-</div>
                        <div className="left col-sm-5 sm-box"><input type="text" className="form-control" name="maxprice" value={maxprice||''} onChange={this.handleInputChange} /></div>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">委托数量：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control"  name="totalamount" value={totalamount||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">下单笔数：</label>
                    <div className="col-sm-9">
                        <input type="text" className="form-control"  name="totalcount" value={totalcount||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
            </div>
        )
    }
}