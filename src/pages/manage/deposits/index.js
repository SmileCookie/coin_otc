import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import axios from 'axios'
import qs from 'qs'
import { fetchManageCoinInfo,fetchManageCoinRecord,setCoinListCurrentCoin,modifyDepositCoin } from '../../../redux/modules/deposit'
import { fetchWalletInfo } from '../../../redux/modules/wallet'
import { getUserBaseInfo } from '../../../redux/modules/session';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend } = notifActions;
import { connect } from 'react-redux'
import ChooseCoin from './chooseCoin'
import Record from './record'
import { browserHistory } from 'react-router';
import MoneyOpt from '../../../components/msg/moneyOpt'

import { PAGEINDEX,PAGESIZEFIVE,DOMAIN_VIP} from '../../../conf';
import { checkEosIcoin } from '../../../utils'

import SetTitle from "../../common/titleSet";

class Deposits extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZEFIVE,
            timeType:0,
            closeOP:true,
            canCharge:true,
            closeChargePop:true,
            props: {
                ln: props.language.locale === 'zh' ? 'cn' : props.language.locale === 'en' ? 'en' : 'hk'
            },
            isEosType:checkEosIcoin(props.location.query.coint.toUpperCase()),
            coinType:''

        }
        this.changeCurrentCoin = this.changeCurrentCoin.bind(this)
        this.startPageData = this.startPageData.bind(this)
        this.close = this.close.bind(this);
        this.iscanCharge = this.iscanCharge.bind(this);
        this.closeCharge = this.closeCharge.bind(this);
    }


    componentDidMount() {
        const coinType = this.props.location.query.coint

        // let isEosType = checkEosIcoin(coinType.toUpperCase());
        this.setState({
            coinType
        })
        this.startPageData(coinType)
        this.props.getUserBaseInfo()
        this.iscanCharge(coinType)


    }
    componentWillReceiveProps(nextProps){
        const nextCoint = nextProps.location.query.coint
        const thisCoint = this.props.location.query.coint

        if(nextCoint!=thisCoint){
            let isEosType = checkEosIcoin(nextCoint.toUpperCase());
            this.setState({
                isEosType,
                coinType:nextCoint
            })
            this.startPageData(nextCoint)
            this.iscanCharge(nextCoint)
        }
    }

    componentWillUnmount(){
        this.props.modifyDepositCoin()
    }


    //判断币种是否可充值
    iscanCharge(coinType){
        try{
            //判断币种是否可充值
            axios.get(DOMAIN_VIP+'/manage/isCanOper?' + qs.stringify({coinName:coinType}))
                .then(res => {
                    let _data = res.data.datas;
                    let {canCharge} = _data;
                    if(!canCharge){
                        this.setState({
                            closeChargePop:false
                        })
                    }
                    this.setState({
                        canCharge
                    },() =>{
                        console.log(this.state.canCharge)
                    })
                });
        }catch(e){

        }
    }
    //启动页面 获取数据
    startPageData(coinType){
        new Promise((resolve, reject) => {
            this.props.setCoinListCurrentCoin(coinType)
            resolve(coinType)
        }).then((value) => {
            var obj = {
                pageIndex:this.state.pageIndex,
                pageSize:this.state.pageSize,
                coint:this.props.currentCoin,
                timeType:this.state.timeType,
            }
            this.props.fetchManageCoinRecord(obj)
        })
    }

    changeCurrentCoin(currentCoin){
        var obj = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            coint:currentCoin
        }
        this.props.setCoinListCurrentCoin(currentCoin)
        this.props.fetchManageCoinRecord(obj)
    }
    close(){
        this.setState({
            isEosType:false
        })
    }
    closeCharge(){
        this.setState({
            canCharge:true
        },()=>{
            setTimeout(() =>{
                this.setState({
                    closeChargePop:true
                })
            },500)
        })

    }
    render(){
        const {props:sprops,isEosType,closeOP,coinType,canCharge,closeChargePop} = this.state;
        const { formatMessage } = this.props.intl;
        const { close,closeCharge} = this;
        return(
            <div className="bk-payInOut">
                <div className="cont-row">
                    <ChooseCoin
                        changeCurrentCoin={this.changeCurrentCoin}
                        currentCoin ={this.props.currentCoin}
                        coinList = {this.props.coinList}
                        fetchManageCoinInfo={this.props.fetchManageCoinInfo}
                        notifSend={this.props.notifSend}
                        accountList={this.props.accountList}
                        fetchManageInfo={this.props.fetchWalletInfo}
                        language={this.props.language}
                        isSafePwd={this.props.isSafePwd}
                        assets={this.props.assets}
                        isEosType = {isEosType}
                        iconType = {this.props.location.query.coint}

                    />
                    <Record
                        currentCoin ={this.props.currentCoin}
                        fetchManageCoinRecord={this.props.fetchManageCoinRecord}
                        record={this.props.record}
                        formatDate={this.props.intl.formatDate}
                        formatMessage={formatMessage}
                    />
                    <SetTitle titlemoney={`${this.props.location.query.coint}`} titleval={'充值'}/>
                </div>
                {
                    (isEosType && this.props.isSafePwd && closeChargePop) &&
                    <div className={`sigup_tips_one ${sprops.ln === 'en' ? 'kc' : ''}`}>
                        <div className="tips_bg"></div>
                        <MoneyOpt type="0" closeCb={close} msg={`${formatMessage({id: 'bbyh充值%%同时需要一个充值地址和地址标签。警告：如果未遵守正确的%%的充值步骤，币会丢失！'}).replace(/%%/g,coinType.toUpperCase())}`} />
                    </div>
                }
                {
                    (this.props.isSafePwd  && !canCharge) &&
                    <div className={`sigup_tips_one ${sprops.ln === 'en' ? 'kc' : ''}`}>
                        <div className="tips_bg"></div>
                        <MoneyOpt type="1" closeCb={closeCharge} msg={`${formatMessage({id: 'bbyh%%币种已暂停充值服务'}).replace(/%%/g,coinType.toUpperCase())}`} />
                    </div>
                }

            </div>
        )
    }
}


const mapStateToProps = (state, ownProps) => {
    return {
        coinList: state.deposit.coinList,
        record: state.deposit.record,
        currentCoin: state.deposit.currentCoin,
        accountList:state.assets.detail,
        language:state.language.locale,
        isSafePwd:state.session.baseUserInfo.hasSafe,
        assets:state.assets.detail
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchManageCoinInfo: () => {
            dispatch(fetchManageCoinInfo())
        },
        fetchManageCoinRecord:(params) => {
            dispatch(fetchManageCoinRecord(params))
        },
        setCoinListCurrentCoin:(coin)=>{
            dispatch(setCoinListCurrentCoin(coin))
        },
        modifyDepositCoin:()=>{
            dispatch(modifyDepositCoin())
        },
        fetchWalletInfo:()=>{
            dispatch(fetchWalletInfo())
        },
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        getUserBaseInfo:()=>{
            dispatch(getUserBaseInfo())
        }

    };
};

export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(Deposits));






























