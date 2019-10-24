import React from 'react';
import Modal from 'react-modal';
import axios from 'axios'
import qs from 'qs'
import cookie from 'js-cookie'
import AddressModale from './addressModale';
import { FormattedMessage,FormattedHTMLMessage, injectIntl } from 'react-intl';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import Pages from '../../../components/pages'
import ScrollArea from 'react-scrollbar'
import { fetchDownCoin,
    chooseDownCoin,
    fetchRecord,
    fetchCancel,
    fetchHisAddress,
    receiveRecord,
    modifyCoinType,
    fetchModifyAddrss,
    getWithdrawAddressAuthenType} from '../../../redux/modules/withdraw'
import { fetchWalletInfo } from '../../../redux/modules/wallet'
import { COIN_KEEP_POINT,DISMISS_TIME,COUNTDOWN_INTERVAL,DOMAIN_VIP,COOKIE_UNAME} from '../../../conf'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import { isFloat,cutDigits,checkEosIcoin } from '../../../utils'
import ReactModal from '../../../components/popBox'
import { optPop } from '../../../utils';
import HistoryList from './historyList'
// import AddressModal from './addressModal'
// import WithdrawForm from './withdrawForm'
import EditorAdress from './editorAdress'
const { notifSend,notifClear,notifDismiss } = notifActions;
const BigNumber = require('big.js')
import './withdraw.less'
import '../../../assets/css/table.less'
import MoneyOpt from '../../../components/msg/moneyOpt'
import CT from '../../../components/context/index'

import SetTitle from "../../common/titleSet";

class Withdraw extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            isWithdraw:false,
            sendBtn : true,
            oneMinute : 60,
            checkAddress:"",
            receiveAddress : "",
            Mstr:"",
            totalCount:0,
            hisAddressList:[],
            pageIndex:1,
            memo:'',
            address:'',
            modifyMemo:'',
            showSearch:false,
            accountList:[],
            checkcoin:'',
            withdrawAddressAuthenType:0,
            btnStus:0,
            disable:false,
            editormodal:'',
            editorBox:false,
            addBox:false,
            widthBox:'56px',
            errorsStatus:true,
            bordeBlue:false,
            isEosType:false,
            isCanWithdraw:true,
            coinType:props.location.query.coint|| props.curCoin,
            props: {
                ln: props.language.locale === 'zh' ? 'cn' : props.language.locale === 'en' ? 'en' : 'hk'
            },
            isLinkBtn:true,

        }
        this.toUpperCase = this.toUpperCase.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.currentPageClick = this.currentPageClick.bind(this)
        this.withdrawCoin = this.withdrawCoin.bind(this)
        this.chooseSureAddress = this.chooseSureAddress.bind(this)
        this.delAddress = this.delAddress.bind(this)
        this.delAddressBtn = this.delAddressBtn.bind(this)
        this.searchAccount = this.searchAccount.bind(this)
        this.hideSearchAccount = this.hideSearchAccount.bind(this)
        this.startPageData = this.startPageData.bind(this)
        this.clearFilterVal = this.clearFilterVal.bind(this)
        this.setCk = this.setCk.bind(this)
        this.addAdress = this.addAdress.bind(this)
        this.changeFocus = this.changeFocus.bind(this)
        this.changeBlur = this.changeBlur.bind(this)
        this.iscanWithdraw = this.iscanWithdraw.bind(this)
        this.closeWithdraw = this.closeWithdraw.bind(this)
    }

    componentDidMount(){
        this.setState({
            widthBox:cookie.get("zlan")=='en'?'84px':'56px'
        })
        const cointType = this.props.location.query.coint||this.props.curCoin;
        let isEosType = checkEosIcoin(cointType.toUpperCase());
        this.setState({
            isEosType,
        })
        this.startPageData(cointType)
        this.props.getAddressAuthenType()
        this.iscanWithdraw(cointType)
        // console.log( ' ===============>   scrollTop     <===============')
        // setTimeout(() =>{
        //     document.querySelector(".scrollarea-content").style.marginTop = 0;
        // },500);
    }
    componentWillUnmount(){
        document.removeEventListener('click',this.hideSearchAccount)
    }
    componentWillReceiveProps(nextProps){
        const nextCoint = nextProps.location.query.coint
        const thisCoint = this.props.location.query.coint
        if(nextCoint!=thisCoint){

            this.startPageData(nextCoint)
            let isEosType = checkEosIcoin(nextCoint.toUpperCase());
            this.setState({
                isEosType,
                showSearch:false,
            },() =>console.log(this.state.isEosType))
            this.iscanWithdraw(nextCoint)
        }

        if(nextProps.hisAddress.isloaded){
            this.setState({
                totalCount:nextProps.hisAddress.data.totalCount,
                pageIndex:nextProps.hisAddress.data.pageIndex
            })
        }
    }
    setCk(){
        !this.ckFlg &&
        this.setState({
            ckFlg: true
        });
    }
    //搜索框边框
    changeFocus(){
        this.setState({
            bordeBlue:true
        })
    }
    changeBlur(){
        this.setState({
            bordeBlue:false
        })
    }
    //页面开始时 数据请求
    startPageData(cointType){
        new Promise((resolve, reject) => {
            this.props.chooseDownCoin(cointType)
            resolve()
        }).then(() => {
            const { pageIndex,pageSize,timeType } = this.state
            let conf = {
                pageIndex:pageIndex,
                pageSize:pageSize,
                coint:this.props.curCoin,
                timeType:timeType,
            }
            this.props.fetchRecord(conf)
            this.props.fetchDownCoin()
            this.props.fetchHisAddress()
            if(!this.props.accountList.data){
                this.props.fetchWalletInfo()
            }
            document.addEventListener('click',this.hideSearchAccount)
        })

    }
    //大写
    toUpperCase(str){
        return str.toUpperCase();
    }
    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        if(name == 'checkcoin'&&value){
            this.setState(preState => {
                if(preState.btnStus != 1){
                    return {btnStus:1}
                }
            })
        }else{
            this.setState(preState => {
                if(preState.btnStus != 0){
                    return {btnStus:0}
                }
            })
        }
    }
    //分页切换
    currentPageClick(pageIndex){
        this.props.fetchHisAddress(pageIndex)
    }
    //币种提现
    withdrawCoin(item){
        this.setState({
            memo:item.memo,
            address:item.address,
            isWithdraw:true,
        })
    }
    //编辑地址
    chooseSureAddress(item){
        const {editorBox,isEosType} = this.state
        const {intl} = this.props
        this.setState({
            Mstr:<EditorAdress editorBox={editorBox} fetchHisAddress={this.props.fetchHisAddress} closeModal={this.modal.closeModal}  address={item.address} memo={item.memo} id={item.id} addressTag={item.addressTag} isEosType={isEosType} agree={item.agreement}/>
        })
        this.modal.openModal()
    }
    //设置标签按钮
    // chooseSureAddressBtn(id){
    //     const { modifyMemo } = this.state
    //     this.props.fetchModifyAddrss({
    //         id,
    //         memo:modifyMemo,
    //         coint:this.props.curCoin
    //     },(res)=>{
    //         const result = res.data
    //         this.modal.closeModal()
    //         if(result.isSuc){
    //             this.props.notifSend({
    //                 message: result.des,
    //                 kind: 'info',
    //                 dismissAfter: DISMISS_TIME
    //             });
    //             this.props.fetchHisAddress()
    //         }else{
    //             this.props.notifSend({
    //                 message: result.des,
    //                 kind: 'warning',
    //                 dismissAfter: DISMISS_TIME
    //             });
    //         }
    //     })
    // }
    addAdress(){
        this.props.getAddressAuthenType().then(r => {
            if(r.data.isSuc){
                const {addBox,addressType,isEosType} = this.state
                this.setState({
                    Mstr: <AddressModale style={{height: '100%',borderRadius:0}} isEosType = {isEosType} addBox={addBox} HisAddressReload = {this.props.fetchHisAddress} closeModal={this.modal.closeModal} fetchHisAddress={this.props.fetchHisAddress} addressType={addressType}></AddressModale>
                })
                this.modal.openModal()
            } else {
                optPop(()=>{}, r.data.des,undefined,true)
            }
        });
    }
    //删除提现地址弹窗
    delAddress(item){
        const Mstr= <div className="alertBox_body">
            <div className="tiltes_center mb20 mt35"><FormattedMessage  id="是否删除该地址" /></div>
            <div className="alertBox_text">
                <div className="mb10"><FormattedMessage  id="yybh地址备注" />{item.memo?item.memo:'--'}</div>
                {
                    this.state.isEosType&&
                    <div className="mb10"><FormattedMessage  id="yybh地址标签" />{item.addressTag}</div>
                }
                <div><FormattedMessage  id="yybh提现地址" />{item.address}</div>
            </div>
            <div className="btns_div">
                <span className="btn close_alertBox" onClick={() => this.modal.closeModal()}><FormattedMessage  id="cancel" /></span>
                <span className="btn submit" onClick={() => this.delAddressBtn(item.id)}><FormattedMessage  id="withdraw.text41" /></span>
            </div>
        </div>
        this.setState({
            Mstr
        })
        this.modal.openModal()
    }
    //删除提现地址按钮
    delAddressBtn(id){
        axios.post(DOMAIN_VIP+"/manage/account/download/doDel",qs.stringify({
            receiveId:id,
            coint:this.props.curCoin
        })).then(res => {
            const result = res.data
            this.modal.closeModal()
            if(result.isSuc){
                // this.props.notifSend({
                //     message: result.des,
                //     kind: 'info',
                //     dismissAfter: DISMISS_TIME
                // });
                optPop(() => {}, result.des);
                this.props.fetchHisAddress()
            }else{
                optPop(() => {}, result.des);
                // this.props.notifSend({
                //     message: result.des,
                //     kind: 'warning',
                //     dismissAfter: DISMISS_TIME
                // });
            }
        })
    }
    //判断币种是否可提现
    iscanWithdraw(coinType){
        try{
            //判断币种是否可充值
            axios.get(DOMAIN_VIP+'/manage/isCanOper?' + qs.stringify({coinName:coinType}))
                .then(res => {
                    let _data = res.data.datas;
                    console.log(_data)
                    let {canWithdraw} = _data;
                    this.setState({
                        isCanWithdraw:canWithdraw,
                        isLinkBtn:canWithdraw
                    },() =>{
                        console.log(this.state.isCanWithdraw)
                    })
                });
        }catch(e){
            return ;
        }
    }

    //搜索查看币种
    searchAccount(e){
        this.setState({
            showSearch:true
        })
        e.nativeEvent.stopImmediatePropagation();
    }
    closeWithdraw(){
        this.setState({
            isCanWithdraw:true
        })
    }
    hideSearchAccount(e){
        const {showSearch} = this.state
        if(showSearch){
            this.setState({
                showSearch:false,
            })
        }
    }

    clearFilterVal(){
        this.setState({
            checkcoin:'',
            btnStus:0
        })
    }

    render(){
        const {coinMap,addressType} = this.props;
        // console.log(this.props.accountList)
        const { formatMessage } = this.props.intl;
        const {balance,dayCash,everyTimeCash,fees,minD,todayCash,canWithdraw,propTag,stag,payGoogleAuth,payMobileAuth,payEmailAuth} = this.props.drawList.datas;
        const cueCoin = this.props.curCoin;
        const { totalCount,pageIndex,showSearch,checkcoin,btnStus,editormodal,widthBox,bordeBlue,isEosType,isCanWithdraw,props:sprops,coinType,isLinkBtn} = this.state
        const UpperCurrentCoin = this.toUpperCase(cueCoin);
        const allowCharge = this.props.assets.isloaded&&this.props.assets.data[UpperCurrentCoin].canCharge
        let cm = CT();
        return (
            <cm.Consumer>
                {
                    (gotoTop) => {

                        return (
                            <div className="bk-payInOut">
                                <div className="content">
                                    <div className="bk-assets bk_pay_asset">
                                        <h2 className="assets-title assets-title-search">
                                            <div className="search-box">
        <span className="search"><FormattedMessage id="balance.text16"/>
            {UpperCurrentCoin}{showSearch ?
                <i className="iconfont icon-xialajiantou-yiru-copy" onClick={(e) => this.hideSearchAccount(e)}></i> :
                <i className="iconfont icon-xialajiantou-moren" onClick={(e) => this.searchAccount(e)}></i>}</span>
                                                <div id="search_warp" style={{height: 'auto'}}
                                                     className={`${showSearch ? 'show' : 'hide'} ${this.props.language}`}>
                                                    <div className={`input_warp ${bordeBlue ? 'borde-blue' : ''}`}
                                                         onClick={(e) => this.searchAccount(e)}>
                                                        <input type="text" className="search_input" name="checkcoin"
                                                               value={checkcoin} onChange={this.handleInputChange}
                                                               onFocus={this.changeFocus} onBlur={this.changeBlur}/>
                                                        <button onClick={this.clearFilterVal}
                                                                className={btnStus == 0 ? "iconfont icon-search-bizhong" : "iconfont icon-shanchu-yiru"}></button>
                                                    </div>
                                                    <ScrollArea stopScrollPropagation={true}
                                                                className="btc_list scrollarea trade-scrollarea scrollarea-content"
                                                                id="btc_list">
                                                        {
                                                            this.props.accountList.isloaded ?
                                                                Object.keys(this.props.accountList.data).filter((currentValue, index) => {
                                                                    if (checkcoin) {
                                                                        return currentValue.indexOf(checkcoin.toUpperCase()) != -1
                                                                    } else {
                                                                        return true;
                                                                    }
                                                                }).map((key, index) => {
                                                                    BigNumber.RM = 0;
                                                                    if (this.props.accountList.data[key].canWithdraw) {
                                                                        return (
                                                                            <Link key={index}
                                                                                  to={"/bw/manage/account/download?coint=" + this.props.accountList.data[key].propTag}
                                                                                  className="item clearfix">
                                                                            <span
                                                                                className="left color_font">{this.props.accountList.data[key].propTag}</span>
                                                                                <span
                                                                                    className="right">{new BigNumber(this.props.accountList.data[key].balance).toFixed(COIN_KEEP_POINT)}</span>
                                                                            </Link>
                                                                        )
                                                                    }
                                                                }) : ""
                                                        }
                                                    </ScrollArea>
                                                </div>
                                            </div>
                                        </h2>
                                        {allowCharge && <ul className="tab-link">
                                            <a className="tab-a fz18p"
                                               href={`/bw/manage/account/charge?coint=${UpperCurrentCoin}`}><span
                                                className="iconfont icon-qiehuanchongzhitixian-moren"></span><FormattedMessage
                                                id="balance.text15"/></a>

                                        </ul>}
                                        {/* {
                        allowCharge&&<ul className="tab-link">
                            <li><Link to={`/bw/manage/account/charge?coint=${UpperCurrentCoin}`}><FormattedMessage  id="balance.text15" /></Link></li>
                            <li className="active"><Link to="javascript:void(0);"><FormattedMessage  id="balance.text16" /></Link></li>
                        </ul>
                    } */}
                                    </div>
                                    <article className="bk_assets_with_tips">
                                        <em className="iconfont icon-denglu-tishi"></em>
                                        <span className="mb0">
                   <FormattedMessage id="withdraw.text55"/></span>
                                    </article>
                                    <section className="asset_address">
                                        <div className="clearifx">
                                            <h2><FormattedMessage id="withdraw.text56"/></h2>
                                            <span className="mid-title"></span>
                                            <span className="address hover_background"
                                                  onClick={() => this.addAdress(UpperCurrentCoin)}><FormattedMessage
                                                id="新增提现地址"/></span>
                                            {/* <Link className="address hover_background" to={"/bw/manage/account/download/address?coint="+UpperCurrentCoin}><FormattedMessage  id="withdraw.text57" /></Link> */}
                                            {/* <AddressModale HisAddressReload = {this.props.fetchHisAddress} fetchHisAddress={this.props.fetchHisAddress} addressType={addressType}></AddressModale> */}
                                            <SetTitle titlemoney={`${this.props.location.query.coint}`} titleval={'提现'}/>
                                        </div>

                                        <div className="address_list">
                                            <div className="address_list_title clearfix">
                                                <div className="item width182"><FormattedMessage id="bbyh地址备注"/></div>
                                                {isEosType &&
                                                <div className="item width182"><FormattedMessage id="bbyh地址标签"/></div>

                                                }
                                                <div className="item width585"><FormattedMessage id="bbyh提现地址"/></div>
                                                <div className="item width430 text-center"><FormattedMessage
                                                    id="withdraw.text60"/></div>
                                            </div>
                                            <section className="address_list_body" id="address_list_body">
                                                {
                                                    this.props.hisAddress.isloaded && this.props.hisAddress.data.totalCount > 0 ?
                                                        this.props.hisAddress.data.list.map((item, index) => {
                                                            return (
                                                                <div key={index} className="item clearfix">
                                                                    <div className="item_1 width182">
                                                                        {item.memo == "" ? "--" : item.memo}
                                                                    </div>
                                                                    {
                                                                        isEosType &&
                                                                        <div className="item_1 width182">
                                                                            {item.addressTag}
                                                                        </div>

                                                                    }
                                                                    <div className="item_1 item_1_2 width585">
                                                                        {item.address == "" ? "--" : item.address}
                                                                    </div>
                                                                    <div
                                                                        className="item_1 item_1_3 width430 text-center"
                                                                        data-id="item.id" data-memo="item.memo"
                                                                        data-address="item.address">
                                                                        {item.lockStatus == 1 ?
                                                                            <span>

                                                        <span className="withdraw_href security_a"
                                                              style={{width: widthBox}}><FormattedMessage
                                                            id="withdraw.text83"/></span>
                                                        <strong className="hover_text iconfont icon-denglu-tishi"
                                                                style={{left: widthBox == '56px' ? '186px' : '200px'}}>
                                                            <div className="text_div tag ${lan}"><FormattedMessage
                                                                id="withdraw.text63"/></div>
                                                        </strong>
                                                    </span>
                                                                            :
                                                                            <Link className="withdraw_href" style={{
                                                                                width: widthBox,
                                                                                backgroundColor: !isLinkBtn ? '#737A8D' : ''
                                                                            }}
                                                                                  to={isLinkBtn ? "/bw/manage/account/download/downloadDetails?coint=" + this.props.curCoin + "&addressId=" + item.id + "&agree=" + item.agreement: null}><FormattedMessage
                                                                                id="balance.text16"/></Link>
                                                                        }
                                                                        <span className="withdraw_a address_memo"
                                                                              onClick={() => this.chooseSureAddress(item)}><FormattedMessage
                                                                            id="withdraw.text61"/></span>
                                                                        <span className="withdraw_a address_delete"
                                                                              onClick={() => this.delAddress(item)}><FormattedMessage
                                                                            id="withdraw.text62"/></span>

                                                                    </div>
                                                                </div>
                                                            )
                                                        }) :
                                                        <div className="address_none">
                                                            <i className="iconfont icon-denglu-tishi">
                                                            </i>
                                                            <FormattedMessage id="withdraw.text72"
                                                                              values={{coint: cueCoin}}/>
                                                        </div>
                                                }
                                            </section>
                                            {totalCount > 5 && <div className="bk-new-tabList-fd tablist">
                                                <Pages
                                                    total={totalCount}
                                                    pagesize="5"
                                                    pageIndex={pageIndex}
                                                    currentPageClick={this.currentPageClick}
                                                />
                                            </div>}
                                        </div>
                                    </section>
                                    <HistoryList
                                        cancelData={this.props.cancelData}
                                        notifDismiss={this.props.notifDismiss}
                                        notifSend={this.props.notifSend}
                                        fetchCancel={this.props.fetchCancel}
                                        notifClear={this.props.notifClear}
                                        curCoin={this.props.curCoin}
                                        propTag={UpperCurrentCoin}
                                        fetchRecord={this.props.fetchRecord}
                                        hisRecord={this.props.hisRecord}
                                        modal={this.modal}
                                        changeStateMstr={this.changeStateMstr}
                                        fetchDownCoin={this.props.fetchDownCoin}
                                        formatDate={this.props.intl.formatDate}
                                        isEosType={isEosType}
                                    />
                                </div>
                                {
                                    !isCanWithdraw &&
                                    <div className={`sigup_tips_one ${sprops.ln === 'en' ? 'kc' : ''}`}>
                                        <div className="tips_bg"></div>
                                        <MoneyOpt type="1" closeCb={this.closeWithdraw}
                                                  msg={`${formatMessage({id: 'bbyh%%币种已暂停提现服务'}).replace(/%%/g, coinType.toUpperCase())}`}/>
                                    </div>
                                }
                                <ReactModal style={{'overflow': 'inherit'}} ref={modal => this.modal = modal}>
                                    {this.state.Mstr}
                                </ReactModal>
                                {editormodal}
                                {
                                    ((func)=> {
                                        console.log('===============>   gotoTop     <===============')
                                        func();
                                    })(gotoTop)
                                }
                            </div>

                        )
                    }
                }

            </cm.Consumer>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    // console.log(state.withdraw)
    return {
        drawList:state.withdraw.drawList,
        coinMap:state.withdraw.coinMap,
        curCoin:state.withdraw.curCoin,
        hisRecord:state.withdraw.hisRecord,
        hisAddress:state.withdraw.hisAddress,
        language:state.language.locale,
        accountList:state.wallet.detail,
        addressType:state.withdraw.withdrawAddressAuthenType,
        assets:state.assets.detail
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        notifClear: () => {
            dispatch(notifClear());
        },
        notifDismiss: (msg) => {
            dispatch(notifClear(msg));
        },
        fetchDownCoin:() => {
            dispatch(fetchDownCoin())
        },
        chooseDownCoin: (params) => {
            dispatch(chooseDownCoin(params))
        },
        fetchRecord: (params) =>{
            dispatch(fetchRecord(params))
        },
        fetchCancel: (params,cb) =>{
            dispatch(fetchCancel(params)).then(cb)
        },
        fetchHisAddress:(index,cb) =>{
            dispatch(fetchHisAddress(index)).then(cb)
        },
        modifyCoinType:()=>{
            dispatch(modifyCoinType())
        },
        fetchWalletInfo:()=>{
            dispatch(fetchWalletInfo())
        },
        getAddressAuthenType:()=>{
            return dispatch(getWithdrawAddressAuthenType())
        },
        fetchModifyAddrss:(params,cb)=>{
            dispatch(fetchModifyAddrss(params)).then(cb)
        },
        dispatch2:(params)=>{
            dispatch(params)
        }
    };
};

export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(Withdraw));



























