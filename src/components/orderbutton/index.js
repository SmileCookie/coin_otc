import React from 'react';
import { withRouter } from 'react-router-dom';
import { FormattedMessage, injectIntl } from 'react-intl';
import {timeChange,fetchPublicKey,optPop,separator} from '../../utils'
import './orderButton.less'
import {SECRET} from '../../conf'
import ReactModal from '../popBox';
import {get,post} from '../../net/index'
import confs from '../../conf'
import axios from 'axios'
import { resolve } from 'path';
const BigNumber = require('big.js')
//import throttle from 'lodash/throttle';


class OrderButton extends React.Component{
    constructor(props){
            super(props);
            this.state = {
                    Mstr:'',
                    orderCancle:false,
                    giveAppeal:false,
                    confrimPay:false,
                    payRemark:'',
                    timeInterval:'',
                    sellCoin:'',
                    sellCoinBox:false,
                    leavNum:0,
                    isCofrim:false,
                    isLockPwd:false,
                    canPay:true,
                }
            this.configTime  = this.configTime.bind(this) 
            this.setModel    = this.setModel.bind(this) 
            this.orderCancle = this.orderCancle.bind(this)
            this.confrimPay  = this.confrimPay.bind(this)
            this.giveAppeal  = this.giveAppeal.bind(this)
            this.checkBoxChange = this.checkBoxChange.bind(this)
            this.setInput = this.setInput.bind(this)
            this.setTime  = this.setTime.bind(this) 
            this.sellCoin = this.sellCoin.bind(this)
            this.remind   = this.remind.bind(this)
            
            //this.makeSureApi = throttle(this.makeSureApi,1000)
        }
       
    componentWillMount(){
        
    }

    componentDidMount(){
        if(this.props.dealInforList.leaveTimes > 0){
            this.configTime(this.props.dealInforList.leaveTimes)
        }

        // post('/web/common/resetPayPwdAsh').then(res =>{
        //     console.log(res)
        // })
    }
    componentWillReceiveProps(nextProps){
       if(nextProps.dealInforList.leaveTimes !== this.props.dealInforList.leaveTimes && nextProps.dealInforList.leaveTimes>0){
            clearInterval(this.setTimeInterval)    
            this.configTime(nextProps.dealInforList.leaveTimes)
       }
    }

    //倒计时
    configTime(time){
        //初始时间
        this.setTime(time)
        //debugger
        let _time = time
        this.setTimeInterval = setInterval(() =>{
            if(_time == 0){
                clearInterval(this.setTimeInterval)
                this.props.getOrderDetailEvent(false)
            }else{
                _time--
                this.setTime(_time)
            }
        },1000)
    }

      //计算时间
      setTime(leaveTimes){
            let {min,sec,hour} = timeChange(leaveTimes);
            let timeInterval =  <span>
                                <span className="coFont4">{min}<FormattedMessage id="分"/></span>
                                <span className="coFont4">{sec}<FormattedMessage id="秒"/></span>
                            </span>
            this.setState({
                timeInterval
            })  
    }

     //Mstr
     changeStateMstr(template){
        this.setState({Mstr:template})
    }

    //各种状态弹窗
    setModel(type){
        switch(type){
            case 'orderCancle'://取消弹
                this.orderCancle()
            break;
            case 'confrimPay'://确定弹
                this.confrimPay()
            break;
            case 'giveAppeal'://放弃申述弹
                this.giveAppeal()
            break;
            case 'sellCoin'://释放货币弹
                this.sellCoin()
            break;
        }
    }

    //确认统一处理
    makeSureBtn = (type) =>{
        let {id,orderCode,sellUserMoblie,buyUserMoblie,sellUserId,buyUserId,adUserId,remindTimes} = this.props.dealInforList;
        let {payRemark, orderCancle,giveAppeal,confrimPay,sellCoin,isCofrim,canPay} = this.state;
        let  adNoMoblie = adUserId == buyUserId ? buyUserMoblie : sellUserMoblie;
        if(type == 'orderCancle'){
            let _obj = {
                recordId:id,
                //cancelUserId:1,
                recordNo:orderCode,
                adNoMoblie,
            }
            
            if(orderCancle){
                this.modal.closeModal()
                post('/web/v1/trade/canelTrade',_obj).then((res) =>{
                    if(res.code == '200'){
                        optPop(() =>{this.props.getOrderDetailEvent(false)},res.msg)   
                   }else{
                        this.props.getOrderDetailEvent(false)
                        optPop(() =>{},res.msg)
                   }
                })
            }
        }
        if(type == 'confrimPay'){
            //console.log(this.state.payRemark);
            let _obj = {
                id:id,
                recordNo:orderCode,
                adNoMoblie,
                payRemark
            }
            if(confrimPay){
                this.modal.closeModal()
                post('/web/v1/trade/commit',_obj).then((res) =>{
                    //console.log(res);
                    if(res.code == '200'){
                        optPop(() =>{ this.props.getOrderDetailEvent(false) },res.msg)
                         
                   }else{
                        this.props.getOrderDetailEvent(false)
                        optPop(() =>{},res.msg)
                   }
                   
                })
            }
           
        }
        if(type == 'giveAppeal'){
            if(giveAppeal){
                this.modal.closeModal()
                post('/web/v1/order/giveUpComplain',{recordId:id}).then(res =>{
                    
                   
                    if(res.code == '200'){
                        optPop(() =>{this.props.getOrderDetailEvent(false)},res.msg)
                         
                   }else{
                        this.props.getOrderDetailEvent(false)
                        optPop(() =>{},res.msg)
                   }
                })
            }
        }
        if(type == 'sellCoin'){
            let key = fetchPublicKey()
            let _obj = {
                recordId:id,
                transPwd:isCofrim ? '' : key.encrypt(sellCoin),
                adNoMoblie,
            }
            this.modal.closeModal()
            post('/web/v1/trade/releaseCoin',_obj).then(res =>{
                    if(res.code == '200'){
                        optPop(() =>{this.props.getOrderDetailEvent(false) },res.msg)
                          
                    }else{
                        this.setState({
                            sellCoin:'',
                            sellCoinBox:false
                        })
                        this.props.getOrderDetailEvent(false)
                        optPop(() =>{},res.msg)
                    }
            })
            
        }
    }

    //确认请求接口
    makeSureApi = (url,_obj) =>{
        post(url,_obj).then((res) =>{
            this.modal.closeModal()
            if(res.code == '200'){
                optPop(() =>{this.props.getOrderDetailEvent(false)},res.msg)   
           }else{
                this.props.getOrderDetailEvent(false)
                optPop(() =>{},res.msg)
           }
        })
    }

    checkBoxChange(checkType){        
        this.setState({
            [checkType]:!this.state[checkType],
        },
        () => {
            if(checkType !== 'sellCoinBox'){
                this[checkType]()
            }else{
                this.sellCoin()
            }
        }
    )};

    // //放币单独复选框
    // sellCheckChange(checkType){        
    //     this.setState({
    //         [checkType]:!this.state[checkType],
    //     },() => this()
    // )};

    setInput(e,type){
        let name = e.target.name
        let value = e.target.value
        let leavNum =  value.length >= 50 ? 50 : value.length
        //console.log(value);
        this.setState({
           [name]: value,
           leavNum,
        },() =>{
            this[type]()
        })
        
    }
    //取消订单
    orderCancle(){
        const {orderCancle} = this.state
        let Mstr =  <div className="Jua-table-inner Jua-table-main ">
                        <div className="head react-safe-box-head">
                            <h3 className="tc f-18 color-fff"><FormattedMessage id="取消交易"/></h3>
                            <p  className="text-p color-fff"><FormattedMessage id="如果您已向卖家付款，为保护自身利益不要取消交易"/></p>
                            <div className="entrust-head-box" onClick={() => this.checkBoxChange('orderCancle')}>
                                <div  className={`${orderCancle?"bg-white":""} checkboxitem`}>
                                    <i className={orderCancle?"iconfont icon-xuanze-yixuan":"iconfont icon-xuanze-weixuan "} ></i>
                                </div>
                                <span><FormattedMessage id="我确认没有付款"/></span>
                            </div>
                        </div>
                        <div className="foot">
                            <a id="JuaBtn_8_2" style={{marginLeft:0}} role="button" className="btn btn-outgray btn-sm" onClick={() => this.modal.closeModal()}><FormattedMessage id="取消"/></a>
                            <a id="JuaBtn_8_1" role="button" className={`btn btn-primary btn-sm ${orderCancle ? null :'stop'}`} onClick={() =>this.makeSureBtn('orderCancle')}><FormattedMessage id="确定"/></a>
                        </div>
                    </div>;

        this.setState({
            Mstr
        },() =>{
            this.modal.openModal();
        })
        
    }
     //确认交易
    confrimPay(){
        const {confrimPay,payRemark,leavNum} = this.state;
        const {formatMessage} = this.props.intl;

        let Mstr =  <div className="Jua-table-inner Jua-table-main ">
                        <div className="head react-safe-box-head">
                            <h3 className="tc f-18 color-fff"><FormattedMessage id="付款确认"/></h3>
                            <p  className="text-p color-fff"><FormattedMessage id="1.一单标记已付款，订单不可取消。"/></p>
                            <p  className="text-p color-fff"><FormattedMessage id="2.请勿在未付款的情况下点击“已付款”按钮，该行为属于恶意点击，当此类订单发生申诉时，系统将冻结买方平台账户。"/></p>
                            <div className="textArea_p">
                                 <p><FormattedMessage id="备注："/></p>
                                <textarea maxLength="50" name="payRemark" id="" cols="30" rows="10" value={payRemark} onChange={(e) =>this.setInput(e,'confrimPay')} placeholder={formatMessage({id:'建议您输入转账时间和转账方式便于卖家账单核对，快速释放货币。'})}></textarea>
                                 <span className="leavNum">{leavNum}/50</span>
                            </div>
                            <div className="entrust-head-box" onClick={() => this.checkBoxChange('confrimPay')}>
                                <div  className={`${confrimPay?"bg-white":""} checkboxitem`}>
                                    <i className={confrimPay?"iconfont icon-xuanze-yixuan":"iconfont icon-xuanze-weixuan "} ></i>
                                </div>
                                <span><FormattedMessage id="我确认已付款"/></span>
                            </div>
                        </div>
                        <div className="foot">
                            <a id="JuaBtn_8_2" style={{marginLeft:0}} role="button" className="btn btn-outgray btn-sm" onClick={() => this.modal.closeModal()}><FormattedMessage id="取消"/></a>
                            <a id="JuaBtn_8_1" role="button" className={`btn btn-primary btn-sm ${confrimPay ? null :'stop'}`} onClick={()=>  this.makeSureBtn('confrimPay')}><FormattedMessage id="确定"/></a>
                        </div>
                    </div>;

        this.setState({
            Mstr
        },() =>{
            this.modal.openModal();
        })
    }
    //放弃申诉
   giveAppeal(){
        const {giveAppeal} = this.state;
        let Mstr =  <div className="Jua-table-inner Jua-table-main ">
                        <div className="head react-safe-box-head">
                            <h3 className="tc f-18 color-fff"><FormattedMessage id="放弃申诉_title"/></h3>
                            <p  className="text-p color-fff"><FormattedMessage id="如果取消申诉，系统会直接判决对方获胜，请您慎重决定"/></p>
                            <div className="entrust-head-box" onClick={() => this.checkBoxChange('giveAppeal')}>
                                <div  className={`${giveAppeal?"bg-white":""} checkboxitem`}>
                                    <i className={giveAppeal?"iconfont icon-xuanze-yixuan":"iconfont icon-xuanze-weixuan "} ></i>
                                </div>
                                <span><FormattedMessage id="我确认要放弃申诉"/></span>
                            </div>
                        </div>
                        <div className="foot">
                            <a id="JuaBtn_8_2" style={{marginLeft:0}} role="button" className="btn btn-outgray btn-sm" onClick={() => this.modal.closeModal()}><FormattedMessage id="取消"/></a>
                            <a id="JuaBtn_8_1" role="button" className={`btn btn-primary btn-sm ${giveAppeal ? null :'stop'}`} onClick={()=> this.makeSureBtn('giveAppeal')}><FormattedMessage id="确定"/></a>
                        </div>
                    </div>;

        this.setState({
            Mstr
        },() =>{
            this.modal.openModal();
        })
    }
    //释放货币
    sellCoin(){
        const {formatMessage} = this.props.intl
        const {icon}  = this.props.dealInforList
        const {sellCoin,sellCoinBox,isCofrim,isLockPwd} = this.state
        let Mstr =  <div className="Jua-table-inner Jua-table-main ">
                        <div className="head react-safe-box-head">
                            <h3 className="tc f-18 color-fff"><FormattedMessage id="确认释放{coin}" values={{coin:icon}}/></h3>
                            {
                                !isCofrim ?
                                <React.Fragment>
                                    <input type="password" className="lj" />
                                    <input type="password" autoComplete="off" maxLength="20" name="sellCoin" onPaste={(e)=> {e.preventDefault()}} className="form_input" onChange={(e) => this.setInput(e,"sellCoin")} value={sellCoin} placeholder={formatMessage({id:'请输入资金密码'})}/>
                                    <input type="password" className="lj" />
                                    <div className="entrust-head-box" onClick={() => this.checkBoxChange('sellCoinBox')}>
                                        <div  className={`${sellCoinBox?"bg-white":""} checkboxitem`}>
                                            <i className={sellCoinBox?"iconfont icon-xuanze-yixuan":"iconfont icon-xuanze-weixuan "} ></i>
                                        </div>
                                        <span><FormattedMessage id="我已收到款项"/></span>
                                    </div>
                                    <div className="ar" style={{paddingTop:'20px'}}>
                                        <a className={`baseColor  ${isLockPwd? 'lockSetPwd' : 'baseCoHover'}`} href={isLockPwd ? `javascript:void(0);` : `/bw/mg/resetPayPwd`} >{formatMessage({id:'重置资金密码'})}</a>
                                    </div>
                                </React.Fragment>
                                :
                                <div className="entrust-head-box" onClick={() => this.checkBoxChange('sellCoinBox')}>
                                    <div  className={`${sellCoinBox?"bg-white":""} checkboxitem`}>
                                        <i className={sellCoinBox?"iconfont icon-xuanze-yixuan":"iconfont icon-xuanze-weixuan "} ></i>
                                    </div>
                                    <span><FormattedMessage id="我已收到款项"/></span>
                                </div>
                            }
                        </div>
                        <div className="foot">
                            <a id="JuaBtn_8_2" style={{marginLeft:0}} role="button" className="btn btn-outgray btn-sm" onClick={() => this.modal.closeModal()}><FormattedMessage id="取消"/></a>
                            {   
                               !isCofrim ? 
                                    (sellCoin && sellCoinBox)?
                                        <a id="JuaBtn_8_1" onClick={()=> this.makeSureBtn('sellCoin')} role="button" className="btn btn-primary btn-sm"><FormattedMessage id="确定"/></a>
                                    :<a id="JuaBtn_8_1" role="button" style={{backgroundColor:'#737A8D',color:'#9199AF',cursor:'default',border:'none'}} className="btn btn-primary btn-sm" ><FormattedMessage id="确定"/></a>
                                :
                                    sellCoinBox?
                                    <a id="JuaBtn_8_1" onClick={()=> this.makeSureBtn('sellCoin',isCofrim)} role="button" className="btn btn-primary btn-sm"><FormattedMessage id="确定"/></a>
                                    :<a id="JuaBtn_8_1" role="button" style={{backgroundColor:'#737A8D',color:'#9199AF',cursor:'default',border:'none'}} className="btn btn-primary btn-sm" ><FormattedMessage id="确定"/></a>

                            }
                           
                        </div>
                    </div>;

        this.setState({
            Mstr
        },() =>{
            this.modal.openModal();
        })
    }
    //判断是否可以免密发送
     checkoutNeedPwd = async () =>{
        let {id,sellUserMoblie,buyUserMoblie,buyUserId,adUserId} = this.props.dealInforList;
        let  adNoMoblie = adUserId == buyUserId ? buyUserMoblie : sellUserMoblie;
        let  isLockPwd = await this.checkoutPwdLock()
        axios.post(confs.BBAPI +  "/manage/isTransSafe").then(res =>{
            let needSafeWord = eval(res["data"]).des
            if(needSafeWord !='false'){
                this.setState({
                    isCofrim:false,
                    isLockPwd
                },() => this.sellCoin()) //不可以免密
                
            }else{
                this.setState({
                    isCofrim:true,
                    isLockPwd
                },() => this.sellCoin()) //可以免密

            }
        })
    }

    //判断密码是否锁定24h
    checkoutPwdLock = () => {
        return new Promise((resolve,reject) =>{
            post('/web/common/resetPayPwdAsh').then(res =>{
                if(res.code == 200){
                    resolve(res.ppwLock)
                }else{
                    resolve(false)
                }
            })
        })
        
    }

    //提醒卖家
    remind(){
        let {id} = this.props.dealInforList;
        post('/web/v1/trade/remind',{id}).then(res =>{
            optPop(() =>{},res.msg)
            if(res.code == '200'){
                this.props.getOrderDetailEvent(false)   
            }
        })
    }
    
    render(){
       const {dealStatue,dealType,appealStatue,leaveTimes,icon,complainId,remindTimes,appealId} = this.props.dealInforList
       const {formatMessage} = this.props.intl
       const {timeInterval} = this.state;
        return(
            <div className="orderButton">
                {/* 待付款 */}
                {
                    dealStatue == 'hasOrder' &&
                    <div className="orderBtnInfor">
                        {  dealType =="buy" ?
                            <React.Fragment>
                                <p className="ar">
                                    <FormattedMessage  id="交易剩余时间："/>
                                    {timeInterval}
                                    <FormattedMessage  id="逾期将自动取消，请及时付款并点击"/>
                                    <span className="coFont4">
                                        <FormattedMessage  id="我已付款"/> 
                                    </span>
                                    <FormattedMessage  id="按钮"/>       
                                </p>
                                <div className="controlBtn ar">
                                    <input type="button" className="btn cancel" onClick={() => this.setModel('orderCancle')} value={formatMessage({id:"取消订单"})} />
                                    <input type="button" className="btn submit" onClick={() => this.setModel('confrimPay')} value={formatMessage({id:"我已付款_btn"})} />    
                                </div>  
                            </React.Fragment>
                            :
                            <React.Fragment>
                                <p className="ar">
                                    <FormattedMessage  id="交易剩余时间："/>
                                    {timeInterval}
                                    <FormattedMessage  id="请等待对方付款，未收到付款不可释放货币"  values={{icon: icon}} />
                                   
                                </p>
                                <div className="controlBtn ar">
                                    <input type="button" style={{backgroundColor:'#737A8D',color:'#9199AF',cursor:'default'}} className="btn submit" value={formatMessage({id:"释放{coin}"}).replace('xx', icon)} />
                                </div>  
                            </React.Fragment>
                            
                        }      
                    </div>

                }
                 {/* 待放币 */}
                {
                    dealStatue == 'hasPay' &&
                    <div className="orderBtnInfor">
                        {  dealType =="buy" ?
                            <React.Fragment>
                                <p className="ar">
                                    <FormattedMessage  id="交易剩余时间："/>
                                    {timeInterval},
                                    <FormattedMessage  id="逾期将自动取消，请及时付款并点击"/>
                                    <span className="coFont4">
                                        <FormattedMessage  id="我已付款"/> 
                                    </span>
                                    <FormattedMessage  id="按钮"/>       
                                </p>
                                <div className="controlBtn ar">
                                    {
                                        remindTimes > 2 ?
                                         <input type="button" style={{backgroundColor:'#737A8D',color:'#9199AF',cursor:'default'}} className="btn submit" value={formatMessage({id:"提醒卖家"})} />
                                        :<input type="button" onClick={this.remind} className="btn submit" value={formatMessage({id:"提醒卖家"})} />    
                                    }
                                </div>  
                            </React.Fragment>
                            :
                            <React.Fragment>
                                <p className="ar">
                                    <FormattedMessage  id="交易剩余时间："/>
                                    {timeInterval},
                                    <FormattedMessage  id="请等待对方付款，未收到付款不可释放货币" values={{icon: icon}}/>
                                   
                                </p>
                                <div className="controlBtn ar">
                                    <input type="button" className="btn submit" onClick={this.checkoutNeedPwd} value={formatMessage({id:"释放{coin}"}).replace('xx', icon)}/>    
                                </div>  
                            </React.Fragment>
                            
                        }      
                    </div>

                }
                {/* 完成、取消 */}
                 {
                    (dealStatue == 'pass' || dealStatue == 'cancel') && 
                    <div className="orderBtnInfor">
                        <div className="controlBtn ar">
                            {appealId !== 0 && <a style={{display:'inline-block'}} href={`/otc/representations/${complainId}`}><input type="button"  className="btn submit" value={formatMessage({id:"申诉详情"})} /></a> }   
                        </div>  
                    </div>
                }
                {/* 异常，申诉 */}
                 {
                    (dealStatue == 'error' || dealStatue == 'appeal') && 
                    <div className="orderBtnInfor">
                        <div className="controlBtn ar">
                            {dealStatue == 'error' &&
                                <p className="ar">
                                    <FormattedMessage  id="规定的时间内未达成交易，您可将问题进行申诉，我们会尽快协调双方进行最终交易"/>
                                </p>
                            }
                            <input type="button" className="btn cancel giveUp" onClick={() => this.setModel('giveAppeal')} value={formatMessage({id:"放弃申诉"})} />
                            { dealStatue == 'error'  &&   <a style={{display:'inline-block'}} href={`/otc/representations/${complainId}`}><input type="button" className="btn submit" value={formatMessage({id:"申诉"})} /></a> } 
                            { dealStatue == 'appeal' &&   <a style={{display:'inline-block'}} href={`/otc/representations/${complainId}`}><input type="button" className="btn submit" value={formatMessage({id:"申诉详情"})} /></a>}   
                        </div>  
                    </div>
                }
                 <ReactModal ref={modal => this.modal = modal}>
                        {this.state.Mstr}
                 </ReactModal>
            </div> 
        )
    }
}

export default withRouter(injectIntl(OrderButton));