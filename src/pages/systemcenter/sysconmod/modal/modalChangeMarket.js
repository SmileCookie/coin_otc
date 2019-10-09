import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, SELECTWIDTH } from '../../../../conf'
import { Input,Modal,Button ,Radio, Select} from 'antd'
import { MarketConfigInput } from '../../../common/components'
import { toThousands } from '../../../../utils'
const RadioGroup = Radio.Group;
const Option = Select.Option

export default class ModalChangeMarket extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            moallmarket:'',
            moadBuyFee:'',
            moadSellFee:'',
            moadValidTime:'',
            moadOrderNumMax:'',
            motransInvalidTime:'',
            mouserAdNumMax:'',
            mouserOrderNumMax:'',
            mouserCancleNum:'',
            mocoinBixDian:'',
            molegalBixDian:'',
            mobuyMaxNum:'',
            mobuyMinNum:'',
            mofeeRate:'',
            momarketType:0,
            momarket:'',
            mosellMaxNum:'',
            mosellMinNum:'',
            moenable: true,
            remark:'',
            orderOverTime:'',
            disable:false,
            mocoinid: 0,
            molegalid: '',
        }
        this.handleInputChange= this.handleInputChange.bind(this)
        this.requestCoinIdSelect = this.requestCoinIdSelect.bind(this)
        this.handleSelectCoinId = this.handleSelectCoinId.bind(this)
    }
    componentDidMount(){
        this.requestCoinIdSelect()
        const { allmarket,adBuyFee,orderOverTime,adSellFee,adValidTime,adOrderNumMax,marketType,transInvalidTime,userAdNumMax,userOrderNumMax,userCancleNum,coinBixDian,legalBixDian,buyMaxNum,buyMinNum,feeRate,market,sellMaxNum,sellMinNum,enable, coinid, legalid } = this.props
        this.setState({
            moallmarket:market||'',
            moadBuyFee:adBuyFee==0?toThousands(adBuyFee, true):toThousands(adBuyFee, true)||'',
            moadSellFee:adSellFee==0?toThousands(adSellFee, true):toThousands(adSellFee, true)||'',
            moadValidTime:adValidTime==0?adValidTime:adValidTime||'',
            moadOrderNumMax:adOrderNumMax==0?adOrderNumMax:adOrderNumMax||'',
            momarketType:marketType||0,
            motransInvalidTime:transInvalidTime?transInvalidTime:transInvalidTime||'',
            mouserAdNumMax:userAdNumMax?userAdNumMax:userAdNumMax||'',
            mouserOrderNumMax:userOrderNumMax==0?userOrderNumMax:userOrderNumMax||'',
            mouserCancleNum:userCancleNum==0?userCancleNum:userCancleNum||'',
            mocoinBixDian:coinBixDian==0?coinBixDian:coinBixDian||'',
            molegalBixDian:legalBixDian==0?legalBixDian:legalBixDian||'',
            mobuyMaxNum:buyMaxNum==0?toThousands(buyMaxNum, true):toThousands(buyMaxNum, true)||'',
            mobuyMinNum:buyMinNum==0?toThousands(buyMinNum, true):toThousands(buyMinNum, true)||'',
            mofeeRate:feeRate==0?toThousands(feeRate, true):toThousands(feeRate, true)||'',
            momarket:market||'',
            mosellMaxNum:sellMaxNum==0?toThousands(sellMaxNum, true):toThousands(sellMaxNum, true)||'',
            mosellMinNum:sellMinNum==0?toThousands(sellMinNum, true):toThousands(sellMinNum, true)||'',
            moenable: (enable === undefined? true : enable),
            orderOverTime:orderOverTime==0?orderOverTime:orderOverTime||'',
            disable:allmarket?true:false,
            remark:'',
            mocoinid:coinid==0?coinid:coinid||0,
            molegalid:legalid==0?legalid:legalid||'',
        })  
    }
    componentWillReceiveProps(nextProps){
        const {allmarket,orderOverTime,adBuyFee,adSellFee,adValidTime,adOrderNumMax,marketType,transInvalidTime,userAdNumMax,userOrderNumMax,userCancleNum,coinBixDian,legalBixDian,buyMaxNum,buyMinNum,feeRate,market,sellMaxNum,sellMinNum,enable, coinid, legalid} = nextProps
        this.setState({
            moallmarket:market||'',
            moadBuyFee:adBuyFee==0?toThousands(adBuyFee,true):toThousands(adBuyFee,true)||'',
            moadSellFee:adSellFee==0?toThousands(adSellFee, true):toThousands(adSellFee, true)||'',
            moadValidTime:adValidTime==0?adValidTime:adValidTime||'',
            moadOrderNumMax:adOrderNumMax==0?adOrderNumMax:adOrderNumMax||'',
            momarketType:marketType||0,
            motransInvalidTime:transInvalidTime?transInvalidTime:transInvalidTime||'',
            mouserAdNumMax:userAdNumMax?userAdNumMax:userAdNumMax||'',
            mouserOrderNumMax:userOrderNumMax==0?userOrderNumMax:userOrderNumMax||'',
            mouserCancleNum:userCancleNum==0?userCancleNum:userCancleNum||'',
            mocoinBixDian:coinBixDian==0?coinBixDian:coinBixDian||'',
            molegalBixDian:legalBixDian==0?legalBixDian:legalBixDian||'',
            mobuyMaxNum:buyMaxNum==0?toThousands(buyMaxNum, true):toThousands(buyMaxNum, true)||'',
            mobuyMinNum:buyMinNum==0?toThousands(buyMinNum, true):toThousands(buyMinNum, true)||'',
            mofeeRate:feeRate==0?toThousands(feeRate, true):toThousands(feeRate, true)||'',
            momarket:market||'',
            mosellMaxNum:sellMaxNum==0?toThousands(sellMaxNum, true):toThousands(sellMaxNum, true)||'',
            mosellMinNum:sellMinNum==0?toThousands(sellMinNum, true):toThousands(sellMinNum, true)||'',
            moenable: (enable === undefined? true : enable),
            orderOverTime:orderOverTime==0?orderOverTime:orderOverTime||'',
            disable:allmarket?true:false,
            remark:'',
            mocoinid:coinid==0?coinid:coinid||0,
            molegalid:legalid==0?legalid:legalid||'',
        })
    }
    // 数字货币ID 下拉框
    requestCoinIdSelect(){
        axios.get(DOMAIN_VIP+'/otcCointype/queryAttr')
        .then(res =>{
            const result = res.data
            let coinIdSelectList = [<Option value={0} key={0}>请选择</Option>]
            for(let i in result.data){
                coinIdSelectList.push(<Option value={result.data[i].fundsType} key={result.data[i].fundsType}>{result.data[i].propCnName}</Option>)
            }
            this.setState({
                coinIdSelectList
            })
        })
    }
    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }

    handleSelectCoinId(val) {
        this.setState({
            mocoinid: val
        })
        this.props.handleSelectCoinId(val)
    }
    render(){
        const {remark,moallmarket,moadBuyFee,moadSellFee,moadValidTime,moadOrderNumMax,momarketType,motransInvalidTime,disable,mouserAdNumMax,mouserOrderNumMax,mouserCancleNum,mocoinBixDian,molegalBixDian,mobuyMaxNum,mobuyMinNum,mofeeRate,momarket,mosellMaxNum,mosellMinNum,moenable,orderOverTime, mocoinid, molegalid, coinIdSelectList}= this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12 marbot market_config">
                <MarketConfigInput content="市场全称：<i>*</i>" inputType="text" name="moallmarket" value={moallmarket} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="购买方广告费：<i>*</i>" inputType="number" name="moadBuyFee" value={moadBuyFee} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="出售方广告费：<i>*</i>" inputType="number" name="moadSellFee" value={moadSellFee} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="广告有效期(天)：<i>*</i>" inputType="number" name="moadValidTime" value={moadValidTime} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="广告最多执行订单数量：<i>*</i>" inputType="number" name="moadOrderNumMax" value={moadOrderNumMax} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="订单失效时间(分钟)：<i>*</i>" inputType="number" name="motransInvalidTime" value={motransInvalidTime} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="用户最多持有广告数量：<i>*</i>" inputType="number" name="mouserAdNumMax" value={mouserAdNumMax} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="用户最多执行订单数量：<i>*</i>" inputType="number" name="mouserOrderNumMax" value={mouserOrderNumMax} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="用户取消次数：<i>*</i>" inputType="number" name="mouserCancleNum" value={mouserCancleNum} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="虚拟币小数点位数：<i>*</i>" inputType="number" name="mocoinBixDian" value={mocoinBixDian} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="法币小数点位数：<i>*</i>" inputType="number" name="molegalBixDian" value={molegalBixDian} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="买入最大挂单限额：<i>*</i>" inputType="number" name="mobuyMaxNum" value={mobuyMaxNum} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="买入最小挂单限额：<i>*</i>" inputType="number" name="mobuyMinNum" value={mobuyMinNum} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="交易手续费：<i>*</i>" inputType="number" name="mofeeRate" value={mofeeRate} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="售出最大挂单限额：<i>*</i>" inputType="number" name="mosellMaxNum" value={mosellMaxNum} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="售出最小挂单限额：<i>*</i>" inputType="number" name="mosellMinNum" value={mosellMinNum} handleInputChange={this.handleInputChange}/>
                <MarketConfigInput content="订单超时时间：<i>*</i>" inputType="number" name="orderOverTime" value={orderOverTime} handleInputChange={this.handleInputChange}/>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">是否开启：</label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.handleInputChange} name="moenable" value={moenable} >
                                <Radio value={true}>开启</Radio>
                                <Radio value={false}>关闭</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                </div>
                {/*
                    <MarketConfigInput content="数字货币ID：<i>*</i>" inputType="number" name="mocoinid" value={mocoinid} handleInputChange={this.handleInputChange}/>
                    <MarketConfigInput content="法币ID：<i>*</i>" inputType="number" name="molegalid" value={molegalid} handleInputChange={this.handleInputChange}/>
                */}
                <div className="col-md-6 col-sm-6 col-xs-6">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">数字货币ID：<i>*</i></label>
                         <div className="col-sm-8">
                             <Select showSearch  value={mocoinid} filterOption={(input,option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0} style={{width:SELECTWIDTH}} onChange={(val)=>this.handleSelectCoinId(val)}>
                                 {coinIdSelectList}
                             </Select>
                         </div>
                     </div>
                 </div>
                {this.props.type=='2'&&<MarketConfigInput content="备注：" inputType="text" name="remark" value={remark} handleInputChange={this.handleInputChange}/>}
            </div>
        )
    }
}