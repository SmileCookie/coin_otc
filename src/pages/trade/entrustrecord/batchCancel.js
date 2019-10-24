import React from 'react';
import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
const BigNumber = require('big.js'); 
class BatchCancel extends React.Component {
    constructor(props) {
        super(props);
        this.setBatchCancelDataTypes = this.setBatchCancelDataTypes.bind(this);
        this.submitResult = this.submitResult.bind(this);
        this.state = {
            types:this.props.types,
            minPrice:this.props.minPrice,
            maxPrice:this.props.maxPrice,
            whetherFill:this.props.whetherFill,
        }
    }
    componentDidMount() {
        
    }
    componentWillUnmount() {
        const{ BatchCancelDataTypes,setWhetherFill,BatchCancelDataMinPrice,BatchCancelDataMaxPrice }=this.props;
        BatchCancelDataTypes(0);
        setWhetherFill(false);
        BatchCancelDataMinPrice("");
        BatchCancelDataMaxPrice("");
    }
    setBatchCancelDataTypes(n){
        const{ BatchCancelDataTypes }=this.props;
        this.setState({
            types:n
        },()=>{
            BatchCancelDataTypes(n)
        })
    }
    setWhetherFill(){
        const{ setWhetherFill }=this.props;
        this.setState({
            whetherFill:!this.state.whetherFill,
            minPrice:"",
            maxPrice:"",
        },()=>{
            this.cancelText.innerHTML ="";
            this.maxPrice.style.borderColor="#D7D7D7";
            setWhetherFill(this.state.whetherFill);
        })
    }
    setBatchCancelDataMinPrice(event){
        try{
            const{ BatchCancelDataMinPrice,marketsConf,currentMarket}=this.props;
            let exchangeBixDian = marketsConf[currentMarket].exchangeBixDian;
            let numberBixDian = marketsConf[currentMarket].numberBixDian;
            console.log(event);
            let val = this.checkNumber(event.target.value,exchangeBixDian)
            this.setState({
                minPrice:val
            },()=>{
                this.cancelText.innerHTML ="";
                this.maxPrice.style.borderColor="#D7D7D7";
                BatchCancelDataMinPrice(val)
            })
        } catch(e){

        }
    }
    setBatchCancelDataMaxPrice(event){
        try{
            const{ BatchCancelDataMaxPrice,marketsConf,currentMarket}=this.props;
            let exchangeBixDian = marketsConf[currentMarket].exchangeBixDian;
            let numberBixDian = marketsConf[currentMarket].numberBixDian;
            let val = this.checkNumber(event.target.value,exchangeBixDian)
            this.setState({
                maxPrice:val
            },()=>{
                this.cancelText.innerHTML ="";
                this.maxPrice.style.borderColor="#D7D7D7";
                BatchCancelDataMaxPrice(val)
            })
        } catch(e){

        }
    }
    // 输入框规则校验
    checkNumber (value,unit) {
        if (value != "") {
            if (this.isNumber(value)) {
                var valueStr = value + "";
                if (valueStr.indexOf(".") != -1) {
                    var newStr,
                        intStr = valueStr.split(".")[0] + "",
                        floatStr = valueStr.split(".")[1] + "";
                    if (floatStr.split("").length > unit) {
                        newStr = intStr + "." + floatStr.substr(0, unit);
                        value = newStr;
                    }
                }
            }else{
                value = ''
            }
        }
        return value
    }
    submitResult(val,focus){
        if(val){
            this.cancelText.innerHTML = val;
        }
        if(focus){
            this.maxPrice.focus();
            this.maxPrice.style.borderColor="#E46161";
        }
    }
    // 判断字符串是否为数字
    isNumber(val) {
        　　 var re = /^[0-9]+\.?[0-9]*$/  //判断正整数 /^[1-9]+\.?[0-9]*]*$/  
        　　 if (!re.test(val)) {
        　　　　 return false
        　　 }
            return true
        }
    render() {
        const {marketsConf,currentMarket} = this.props;
        let exchangeBixDian = 0;
        try{
            exchangeBixDian = marketsConf[currentMarket].exchangeBixDian
        }catch(e){
            
        }
        let types = this.state.types;
        let minPrice = this.checkNumber(this.state.minPrice,exchangeBixDian);
        let maxPrice = this.checkNumber(this.state.maxPrice,exchangeBixDian);
        let whetherFill = this.state.whetherFill;
        return (
            <div>
                <div className="formbox">
                    <div className="btn-group bk-btn-group clearfix">
                            <em className={types==0?'active':''} onClick={()=>{this.setBatchCancelDataTypes(0)}}><FormattedMessage id="RevokeAll"/></em>
                            <em className={types==1?'active':''} onClick={()=>{this.setBatchCancelDataTypes(1)}}><FormattedMessage id="RevokeBuy"/></em>
                            <em className={types==2?'active':''} onClick={()=>{this.setBatchCancelDataTypes(2)}}><FormattedMessage id="RevokeSell"/></em>
                    </div>
                    <div className='form-group'>
                        <label htmlFor='whetherFill' className='control-label'></label>
                        <input type='checkbox' id='whetherFill' checked={whetherFill?"checked":""} onClick={()=>{this.setWhetherFill()}}/><FormattedMessage id="CancelAll"/>
                    </div>
                    <div className='form-group'>
                        <label htmlFor='minPrice' className='control-label'><FormattedMessage id="Above"/>：</label>
                        <input type='text' className={whetherFill?"disabled":""} disabled={whetherFill?"disabled":""} value={whetherFill?"":minPrice} id='minPrice' name='minPrice' ref={minPrice => this.minPrice = minPrice} onChange={this.setBatchCancelDataMinPrice.bind(this)}/>
                    </div>
                    <div className='form-group'>
                        <label htmlFor='maxPrice' className='control-label'><FormattedMessage id="Below"/>：</label>
                        <input type='text' className={whetherFill?"disabled":""} disabled={whetherFill?"disabled":""} value={whetherFill?"":maxPrice} id='maxPrice' name='maxPrice' ref={maxPrice => this.maxPrice = maxPrice} onChange={this.setBatchCancelDataMaxPrice.bind(this)}/>
                    </div>
                </div>
                <p ref={(text) => {this.cancelText = text;}}></p>
                <div className="modal-btn">
                    <div className="modal-foot">
                        <a className="btn ml10" onClick={()=>{this.props.closeModal()}}><FormattedMessage id="cancel" /></a>
                        <a className="btn ml10" onClick={() =>{this.props.submiModal(this.submitResult)}}><FormattedMessage id="sure" /></a>
                    </div>
                </div>
            </div>
        );
    }
}

export default BatchCancel;