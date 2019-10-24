import React from 'react';
import axios from 'axios'
import qs from 'qs'
import Select from 'react-select';
import SelectEntrust from '../../../components/form/selectEntrust';
import {DOMAIN_VIP} from '../../../conf'
import { connect } from 'react-redux';
import { optPop,isFloat } from '../../../utils';
import Form from '../../../decorator/form';
import { FormattedMessage, injectIntl } from 'react-intl';
import {fetchManageInfo} from '../../../redux/modules/account'
import { fail } from 'assert';
const BigNumber = require('big.js')
import { fetchAssetsDetail}from '../../../redux/modules/assets'
import cookie from 'js-cookie';
import ReactModal from '../../../components/popBox';
 
@Form
class Transfer extends React.Component{
    constructor(props){
        super(props)
        this.state = {
           transAmount:'',
           coin:'',
           transFrom:1,
           fromList:[],
           toList:[],
           transTo:2,
           drawList:{},
           transList:{},
           ckFlg: 0,
           fundsType:'',
           fromPayUser:'',
           toPayUser:'',
           fundsTypeName:'',
           fundsOtcList:[],
           coinList:[],
           dialog:null,
        }
        this.options = [
            { value: 1, label:<FormattedMessage id="我的钱包"/>},
            { value: 2, label:<FormattedMessage id="币币账户"/>},
            { value: 3, label:<FormattedMessage id="法币账户"/>},
            { value: 5, label:<FormattedMessage id="理财账户"/>},
            /*{ value: 4, label:<FormattedMessage id="f合约账户"/>}*/,
        ]
        this.togetCode = this.togetCode.bind(this)
        this.fromgetCode = this.fromgetCode.bind(this)
        this.transOK = this.transOK.bind(this)
        this.checkNumber = this.checkNumber.bind(this)
        this.setCk = this.setCk.bind(this)
        this.requestData = this.requestData.bind(this)
        this.maxDraw = this.maxDraw.bind(this)
        this.dataProcess = this.dataProcess.bind(this)
        this.getFundsType = this.getFundsType.bind(this)
        this.setFromToDefault = this.setFromToDefault.bind(this)
        this.jj = this.jj.bind(this);
        // this.changeIcon = this.changeIcon.bind(this)

        this.skin = window.location.href.includes("trade") ? cookie.get('skin') : '';

        this.confirm = this.confirm.bind(this);
    }
    // 废弃
    confirm(){
        // 点击立即划转,先请求接口判断是不是应该弹窗，如果弹了走下面逻辑，不弹直接走transOK.
        
        // 先验证下
        this.setCk();
        if(!this.hasError(['transAmount'])){
            this.setState({
                dialog: (
                    <div className="my_tip_wp">
                        <h2><FormattedMessage id="提示" /></h2>
                        <div className="fmt" dangerouslySetInnerHTML={{__html: '<p>xxxxx<span class="f">cc</span>vv</p>'}}></div>
                        <div className="modal-btn">
                            <div className="modal-foot">
                                <a className="btn ml10" onClick={() => this.modal.closeModal()}><FormattedMessage id="cancel" /></a>
                                <a className="btn ml10" onClick={() => this.transOK()}><FormattedMessage id="确定" /></a>
                            </div>
                        </div>
                    </div>
                )
            }, () => {
                this.modal.openModal();
            })
        }
    }
    componentDidMount(){
        localStorage.setItem('isintransfer', 1);
        this.dataProcess(this.props.fromtype,this.props.fundsType,this.props.totype)
        this.props.fetchAssetsDetail();
        // this.confirm();
    }

    componentDidUpdate(){
        try{
        if(!this.in){
            document.getElementsByClassName("bbyh-sq")[0].onclick = () => {
                clearTimeout(this.l);
                this.l = setTimeout(() => {
                    let items = Array.from(document.getElementsByClassName("Select-menu-outer"));
                    //if(this.isAppend){
                    items.forEach(i => {
                        i.setAttribute("style","display:none")
                        axios.get(DOMAIN_VIP + '/manage/getAllCoinList?'+qs.stringify({transFrom: this.state.transFrom, transTo: this.state.transTo})).then(r => {
                            try{
                                r = eval(r.data) || {};
                            } catch(e){
                                r = {}
                            }
                            // Array.from(i.getElementsByClassName("Select-option")).forEach(j => {
                            //     let currentText = j.innerText,
                            //         c = r[currentText];

                            //     if(c){
                            //         c.balances = new BigNumber(c.balance).toFixed(8)
                            //         let span = document.createElement("span");
                            //         span.setAttribute("style","float:right;font-size:14px");
                            //         span.innerText = c.balances == 0 ? '-' : c.balances;

                            //         j.appendChild(span);
                            //     }
                            // })
                            let coinList =  [];
                            let coinAssetList = r;
                            for(let i in coinAssetList){
                                let item = {};
                                item.label = coinAssetList[i].propTag;
                                item.value = coinAssetList[i].fundsType;
                                item.balances = coinAssetList[i].balance > 0 ? new BigNumber(coinAssetList[i].balance).toFixed(8) : '-';
                                coinAssetList[i].display && coinList.push(item);
                            }
                            if(coinList.length){
                                this.setState({fromList: coinList}, ()=>{
                                    Array.from(i.getElementsByClassName("Select-option")).forEach((j,k) => {
                                        let itm = coinList[k];
                                        // j.innerText = itm.label;
                                        // j.setAttribute('aria-label', itm.label);
                                        // j.setAttribute('id', 'react-select-6--option-'+itm.value);
                                        let span = document.createElement("span");
                                        span.setAttribute("style","float:right;font-size:14px");
                                        span.innerText = itm.balances;
                                        j.appendChild(span);
                                    });
                                })
                            }
                            i.setAttribute("style","display:block")
                        });
                    })
                    this.isAppend = 0;
                    //}
                    // if(!items.length){
                    //     this.isAppend = 1;
                    // }
                },50)
            }
        }
        this.in = 1;
        }catch(e){

        }
    }

    // componentWillReceiveProps(Nextprops){
    //     console.log(Nextprops.fundsType)
    //     this.dataProcess(Nextprops.fromtype,Nextprops.fundsType,Nextprops.totype)
    // }
    dataProcess(transFrom,fundType,transTo){
        this.setState({
            transFrom:transFrom,
            transTo,
            fundsTypeName:fundType,
        })
        let coinList = []
        let coinAssetList = this.props.assetsDetail
        for(let i in coinAssetList){
            let item = {}
            item.label = coinAssetList[i].propTag
            item.value = coinAssetList[i].fundsType
            item.balances = new BigNumber(coinAssetList[i].balance).toFixed(8)
            
            coinAssetList[i].display && coinList.push(item)
        }
        // console.log(coinList);
        let fundsCoin = 0
        fundsCoin = coinAssetList[fundType].fundsType
        coinList = transTo == 4? [{label: "BTC", value: 2}] :coinList;
        this.setState({
            coinList:coinList,
            fromList:coinList,
            fundsType:fundsCoin,
        })
        this.requestData(transFrom,fundsCoin,transTo);
        this._fundsType = fundsCoin;
    }

    requestData(fromPayId,fundsType,toPayId){
       // console.log(fundsType, '====--->');
       
       return new Promise((rs) => {
            axios.post(DOMAIN_VIP+"/manage/account/transfer/transferOtc").then(res => {
            let data = res.data.datas
            let fundsOtcList =[]
            try{
                data.forEach(element => {
                    let otcList = {}
                    otcList.label = element.coinName
                    otcList.value = element.id
                    otcList.balances = new BigNumber(element.balance ? element.balance : 0).toFixed(8)
                    fundsOtcList.push(otcList)
                });

            }catch(e){

            }
            this.setState({
                fundsOtcList,
            }, () => {
                axios.post(DOMAIN_VIP+"/manage/account/transfer/transferView",qs.stringify({
                    toPayId:toPayId,
                    fromPayId:fromPayId,
                    fundsType:fundsType,
                })).then(res => {
                    const data = res.data.datas
                    this.setState({
                        fromPayUser:new BigNumber(data[0].fromPayUser).toFixed(8),
                        toPayUser:new BigNumber(data[1].toPayUser).toFixed(8)
                    }, () => {
                        rs(fundsOtcList);
                    })
                })

            })
            })
        })
    }
    setCk(){
        !this.ckFlg &&
        this.setState({
            ckFlg: true
        });
    }
    setFromToDefault(list = []){
        const selected = list.find(v => v.value === this._fundsType)
        if(selected && selected.value){
            this.getFundsType({
                value: selected.value,
                label: selected.label,
            })
        }
        return !!selected;
    }
    fromgetCode(item = {}){
        const {transTo} = this.state
        //console.log(transTo)
        //console.log(item)

        if(item.value==transTo){
            let transToN = 1
            if(item.value == 1){
                transToN = 2
            }
            // else if(item.value == 2){
            //     transToN = 3
            // }
            this.setState({
                transTo:transToN
            })
        }
        this.setState({
            transFrom: item.value
        },() => {
            const {transFrom,fundsType,transTo,fundsOtcList,coinList}=this.state
            let changeFuns = coinList[0].value
            if(transFrom == 3|| transTo ==3){
                this.requestData(transFrom,changeFuns,transTo).then(r => {
                    let fundsOtcList = r;
                    changeFuns = fundsOtcList[0] ? fundsOtcList[0].value : '';
                    this.setState({
                        fromList:fundsOtcList,
                        fundsType:changeFuns,
                        fundsTypeName:fundsOtcList[0] ? fundsOtcList[0].label : ''
                    }, ()=>{
                        this.setFromToDefault(fundsOtcList)
                        this.jj(transFrom, transTo);
                    })
                })
            }else{
                changeFuns = coinList[0].value
                this.setState({
                    fromList:coinList,
                    fundsType:changeFuns,
                    fundsTypeName:coinList[0].label
                }, () => {
                    if(!this.setFromToDefault(coinList)){
                        this.requestData(transFrom,changeFuns,transTo)
                    }
                    this.jj(transFrom, transTo);
                })
                }
            })
    }
    // 获取交集
    jj(transFrom, transTo){
        axios.get(DOMAIN_VIP + '/manage/getAllCoinList?'+qs.stringify({transFrom: this.state.transFrom, transTo: this.state.transTo}))
        .then((r)=>{
            try{
                r = eval(r.data) || {};
            } catch(e){
                r = {}
            }
            try{
                let coinList =  [];
                let coinAssetList = r;
                for(let i in coinAssetList){
                    let item = {};
                    item.label = coinAssetList[i].propTag;
                    item.value = coinAssetList[i].fundsType;
                    item.balances = coinAssetList[i].balance > 0 ? new BigNumber(coinAssetList[i].balance).toFixed(8) : '-';
                    
                    coinAssetList[i].display && coinList.push(item);
                }

                const changeFuns = coinList[0].value
                //console.log(coinList);
                //console.log(changeFuns, '===>');
                this.setState({
                    fromList:coinList,
                    fundsType:changeFuns,
                    fundsTypeName:coinList[0].label
                }, () => {
                    //if(!this.setFromToDefault(coinList)){
                        this.requestData(transFrom,changeFuns,transTo)
                    //}
                })

                // this.requestData(transFrom ,r[Object.keys(r)[0]].fundsType, transTo);
            }catch(e){
                
            }
            //           
        })
    }

    togetCode(item = {}){
        const {transFrom} = this.state
        if(item.value==transFrom){
            let transFrom = 1
            if(item.value == 1){
                transFrom = 2
            }
            this.setState({
                transFrom
            })
        }
        this.setState({
            transTo: item.value
        },() => {
            const {transFrom,fundsType,transTo,fundsOtcList,coinList}=this.state
            
            let changeFuns = coinList[0].value
            if(transFrom == 3||transTo ==3){
                this.requestData(transFrom,changeFuns,transTo).then(r => {
                    let fundsOtcList = r;
                    changeFuns = fundsOtcList[0] ? fundsOtcList[0].value : ''
                    this.setState({
                        fromList:fundsOtcList,
                        fundsType:changeFuns,
                        fundsTypeName:fundsOtcList[0] ? fundsOtcList[0].label : ''
                    }, () => {
                        this.setFromToDefault(fundsOtcList)
                        this.jj(transFrom, transTo);
                    })
                })
            }else{
                changeFuns = coinList[0].value
                this.setState({
                    fromList:coinList,
                    fundsType:changeFuns,
                    fundsTypeName:coinList[0].label
                }, () => {
                    if(!this.setFromToDefault(coinList)){
                        this.requestData(transFrom,changeFuns,transTo)
                    }
                    this.jj(transFrom, transTo);
                })
                }
            }
        );

    }
     //全部提现
     maxDraw(){
        const { fromPayUser } = this.state
        this.setState({
            transAmount:fromPayUser
        })
    }
    getFundsType(item = {}){
        let po = null;
        this.setState((prevState) => {
            const {transFrom,fundsType,transTo} = prevState
            po = prevState;
            this._fundsType = item.value
            return {
                fundsType: item.value,
                fundsTypeName:item.label,
                transAmount:''
            }
        }, () => {
            const {transFrom,fundsType,transTo} = po
            this.requestData(transFrom,item.value,transTo)
        })
    }
    //提币数量限制
    checkNumber(e){
        let value = e.target.value;
        const { fromPayUser } = this.state
        // const coinBalance = this.cutDigits(balance);
        if(value == '00'){
            return false
        }

        let reg = /[^0-9.]/g;;
        if(value < 0||reg.test(value)||!isFloat(value)){
           return false
        }

        else{
            BigNumber.RM = 0;
            let $fromPayUser = Number(fromPayUser);
            let $thatVal = value;
            if($thatVal.toString().split(".")[1]){
                        if($thatVal.toString().split(".")[1].length >8){
                            let _num = new BigNumber($thatVal).toFixed(8);
                            console.log(_num)
                            return false
                        }
             }
            if(Number($thatVal) > Number($fromPayUser)){
                let _nums = new BigNumber($fromPayUser).toFixed(8);
                this.setState({
                    transAmount:_nums
                })
            }
            else{
                this.setState({
                    transAmount: value
                })
            }

        }
        // else{
		// 	BigNumber.RM = 0;
		// 	let $thatVal = value;
        //     let $fromPayUser = Number(fromPayUser);
		// 	if($thatVal > $fromPayUser){
        //         let _nums = new BigNumber($fromPayUser).toFixed(8);
        //         // return false
        //         this.setState({
        //             transAmount:_nums
        //         })
		// 	}
        //     if($thatVal.toString().split(".")[1]&&$thatVal.toString().split(".")[1].length>8){
        //         $thatVal = 	new BigNumber($thatVal).toFixed(8)
        //         this.setState({
        //             transAmount:$thatVal
        //         })
        //     }else{
        //         this.setState({
        //             transAmount:value
        //         })
        //     }

        // }
    }

    // //划转检测
    // changeIcon(){
    //     console.log(1234)
    // }

    transOK(){
        const {transFrom,transTo,transAmount,fundsType} = this.state
        //console.log(transFrom, transTo, fundsType, '@@@@@')
        this.setCk();
        if(!this.hasError(['transAmount'])){
            axios.post(DOMAIN_VIP+"/manage/account/transfer/transfer",qs.stringify({
                from:transFrom,
                to:transTo,
                amount:transAmount,
                coinTypeId:fundsType
            })).then(res => {
                optPop(()=>{},res.data.des)
                if(res.data.isSuc == true){
                    this.props.closeModal();
                }
            })
        }
    }
    render(){

        const { formatMessage } = this.intl;
        const {transAmount,coin,fundsType,toPayUser,fromPayUser,transFrom,fromList,transTo,fundsTypeName,transList,errors,ckFlg}= this.state;
        const {fIn, bOut,settransAmount}= this;
        const { transAmount:etransAmount = []} = errors;
        return(
            <div className={`bk-moadlDetail bk-ogz  width640 ${this.skin}vs`} >
                    <div className="head gz"><h3>{formatMessage({id: "资金互转"})}</h3><a className="right iconfont icon-guanbi-moren" onClick={() => this.props.closeModal()}></a></div>
                    <div className="bk-balances">
                    <div className="bk-transfer select-new">
                        <div className="trans-from" style={{width:'260px'}}>
                            <div className="record-head entrust-selcet" style={{width:'260px'}}>
                            <Select
                                value={transFrom}
                                clearable={false}
                                searchable={false}
                                onChange={this.fromgetCode}
                                options={this.options}
                                />
                            </div>
                            <span className="chart"><i>{formatMessage({id: "可用余额"})}：</i><b>{fromPayUser} {fundsTypeName}</b></span>
                        </div>
                        <div className="trans-txt">{formatMessage({id: "转至"})}</div>
                        <div className="trans-from" style={{width:'260px'}}>
                        <div className="record-head entrust-selcet" style={{width:'260px'}}>
                            <Select
                                value={transTo}
                                clearable={false}
                                searchable={false}
                                onChange={this.togetCode}
                                options={this.options}
                                />
                        </div>
                            <span className="chart"><i>{formatMessage({id: "可用余额"})}：</i><b>{toPayUser} {fundsTypeName}</b></span>
                        </div>
                    </div>
                    <div className="bk-center ">
                    {fromList.length>0&& <div className="entrust-head-market select-new" style={{width:'100%',clear:'both'}}>
                    <h5 className="left padl10"><FormattedMessage id="币种" /></h5>
                        <div className="record-head entrust-selcet bbyh-sq">
                        <Select ref="cc"
                            value={fundsType}
                            clearable={false}
                            searchable={false}
                            onChange={this.getFundsType}
                            options={fromList}
                        />
                        </div>
                    </div>}

                    </div>
                    <div className="bk-bottom">
                            <div className="trans-number">{formatMessage({id: "划转数量"})}</div>
                            <div className="trans-innumber">{formatMessage({id: "可划转数量"})}：<i>{fromPayUser} {fundsTypeName}</i></div>
                        
                        <div className={`${ckFlg && etransAmount[0] && 'bbyh-err'} bk-trans`}>
                            <input className="trans-input" placeholder={formatMessage({id: "请输入划转数量"})}  autoComplete="off" ref={(inp) => this.cashAmountInput = inp}  name="transAmount" onChange={this.checkNumber} value={transAmount} onFocus={(e)=>{this.setCk();this.fIn(e)}} onBlur={bOut}/>
                            <label  className="trans-lable" onClick={this.maxDraw}>{formatMessage({id: "全部划转"})}</label>
                            <span className="ew">{ckFlg ? etransAmount[0] : null}</span>
                        </div>
                        
                    </div>
                    <div className="bk-prompt">{formatMessage({id: "说明：资金互转无需任何手续费。"})}</div>
                    </div>

                    <div className="modal-btn">
                        <div className="modal-foot">
                            <a className="btn ml10" onClick={() => this.props.closeModal()}><FormattedMessage id="cancel" /></a>
                            <a className="btn ml10" onClick={() => this.transOK()}><FormattedMessage id="立即划转" /></a>
                        </div>
                    </div>

                    <ReactModal ref={modal => this.modal = modal}>
                        {this.state.dialog}
                    </ReactModal>
                </div>
        )
    }
}
const mapStateToProps = (state, ownProps) => {
    return {
        assetsDetail: state.assets.detail.data,
    };
};
const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        fetchAssetsDetail: () => {
            dispatch(fetchAssetsDetail());
        }
    };
}

// const mapDispatchToProps = (dispatch) => {
//     return {
//         fetchManageInfo: (cb) => {
//             dispatch(fetchManageInfo()).then(cb)
//         }
//     };
// };
export default connect(mapStateToProps,mapDispatchToProps)(Transfer);