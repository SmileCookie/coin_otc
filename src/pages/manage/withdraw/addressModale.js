import React from 'react';
import axios from 'axios';
import qs from 'qs';
import { connect } from 'react-redux'
// import {browserHistory} from 'react-router';
import Form from '../../../decorator/form';
import Sms from '../../../components/user/sms';
import cookie from 'js-cookie';
import { optPop } from '../../../utils';
import { Link } from 'react-router'
import ReactModal from '../../../components/popBox'
import { COIN_KEEP_POINT,DISMISS_TIME,DOMAIN_VIP,COUNT_DOWN_ONE_MINUTE,COOKIE_UNAME,USDTARGLIST} from '../../../conf'
import { getWithdrawAddressAuthenType,addWithDrawAddress } from '../../../redux/modules/withdraw'
import { FormattedMessage,FormattedHTMLMessage, injectIntl ,FormattedDate} from 'react-intl';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend,notifClear,notifDismiss } = notifActions;

const CODETYPE = 10;
@Form
class AddressModale extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            memo:'',
            bbremark:'',
            address:'',
            sendBtn:true,
            oneMinute:COUNT_DOWN_ONE_MINUTE,
            timeType:0,
            emailcode:'',
            ckFlg: 0,
            modalStatus:0,
            addressType:0,
            addBox:'',
            focusBlu:'',
            props: {
                ln: props.language === 'zh' ? 'cn' : props.language === 'en' ? 'en' : 'hk'

            },
            usdtArgList:[],
            argIndex: 0
        }
        this.submitStatus = 1
        this.onSubmitAddress = this.onSubmitAddress.bind(this)
        this.toUpperCase = this.toUpperCase.bind(this)
        this.setCk = this.setCk.bind(this)
        this.addAddress = this.addAddress.bind(this)
        this.clearError = this.clearError.bind(this)
        this.setFoucs = this.setFoucs.bind(this)

        console.log(props.isEosType)
    }

    componentDidMount(){
        this.setState({addressType:this.props.addressType,usdtArgList: USDTARGLIST})
        this.props.getAddressAuthenType()
        const { formatMessage } = this.intl;
    }
    componentWillReceiveProps(netxProps){
        this.setState({addressType:netxProps.addressType})
    }
    setFoucs(name){
     this.setState({
         focusBlu:name
     })
    }

    clearError(){
        this.setState({errors:[]})
    }
     //新增地址
     addAddress(){
        this.modal.openModal()

    }
     //大写
     toUpperCase(str){
        return str.toUpperCase();
    }

    setCk(){
        !this.ckFlg &&
        this.setState({
            ckFlg: true
        });
    }

    onSubmitAddress(){
        const { address,memo,emailcode,addressType, bbremark} = this.state,
         {isEosType} = this.props,
             { formatMessage } = this.intl;
             let alldata = []
             if(addressType ==2){
                isEosType ? alldata = ["memo","address","emailcode","bbremark"] : alldata = ["memo","address","emailcode"]

             }else{
                isEosType ? alldata = ["memo","address","bbremark"] : alldata = ["memo","address"];

             }
             // usdt ec20 协议时 agreement 赋值
             let path = window.location.href.toUpperCase(),
                 agreement = '';
             if (path.includes("USDT") && this.state.argIndex == 1){
                 agreement = 102
             }

             this.setCk();
                if(!this.hasError(alldata)){
                //添加提现地址
                    if(this.submitStatus==-1){
                            optPop(() => {},formatMessage({id: "withdraw.text81"}));
                        return;
                    }
                this.submitStatus = -1;
                this.props.addWithDrawAddress({
                    address:address.replace(/ /g,''),
                    memo:memo.trim(),
                    addressTag:bbremark,
                    mobileCode:emailcode,
                    agreement: agreement
                    // bbremark:bbremark
                },(res) => {
                    const result = res.data
                    this.submitStatus = 1
                    if(result.isSuc){
                        this.props.closeModal()
                        this.setState({
                            emailcode:''
                        })
                        optPop(() => {},result.des);
                        this.props.HisAddressReload()
                        // browserHistory.push (`/bw/manage/account/download?coint=${this.props.curCoin}`)
                    }else{
                        this.makeResult(result, true);
                    }
                })
            }
    }
    // 切换 usdt 协议
    setUsdtArg = (i) =>{
        this.setState({
            argIndex : i
        })
    }
    render(){
            const  cueCoin = this.props.curCoin,
                     { formatMessage } = this.intl,
                    UpperCurrentCoin = this.toUpperCase(cueCoin),
                   {fIn, bOut,setEmailcode,codeType,setGetCodes,setmemo,setaddress,setbbremark} = this,
                   {emailcode,errors,ckFlg,memo,address,addressType,focusBlu,props:lan_props,usdtArgList,argIndex} = this.state,
                   {emailcode:eemailcode = [],address:eaddress=[],memo:ememo=[],bbremark:ebbremark=[]} = errors;
            const userName = cookie.get(COOKIE_UNAME);
            const {isEosType} = this.props;
            const path = window.location.href.toUpperCase();
            let showFlag = path.includes('USDT');

        return (
            // <div className="add-modal">
            //     {/* <span className="address hover_background"  onClick={() => this.addAddress(UpperCurrentCoin)}><FormattedMessage  id="新增提现地址" /></span> */}
            // {/* <ReactModal ref={modal => this.modal = modal} clearError={this.clearError}>
            //         */}

            <div className="alertBox_body" style={{width:'600px',...(this.props.style ? this.props.style : {})}}>
                         <div className="head ">
                            <h3>{formatMessage({id:'withdraw.text57'}).replace('XXX',UpperCurrentCoin)}</h3>
                            <a className="right iconfont icon-guanbi-moren" onClick={() => this.props.closeModal()}></a>
                        </div>
                        <div className="item mb10">
                            <h5 className="alert_infor">
                                <FormattedMessage  id="yybh地址备注" />
                            </h5>
                            <div className={`${ckFlg && ememo[0]&& 'err'} input_warp`} style={{borderColor:focusBlu=='memo'?'#3E85A2':''}}>
                                <input type="text" name="memo" maxLength="20" className="input_item" placeholder={formatMessage({id:'bbyh请输入地址备注'})} onChange={setmemo} value={memo} onFocus={(e)=>{this.setCk();this.setFoucs('memo');this.fIn(e)}} onBlur={bOut}/>
                            </div>
                            <span className="ew">{ckFlg ? ememo[0] : null}</span>
                        </div>
                        {
                            showFlag &&
                            <div className="item mb10">
                            <h5 className="alert_infor">
                                <FormattedMessage  id="地址协议" />
                            </h5>
                            <div className="coin-agreement">
                                {
                                    usdtArgList.map((v,i) => {
                                            return(
                                                <span key={i} className={`${argIndex  == v.value && 'active'}`} onClick={() => {this.setUsdtArg(v.value)}}>{formatMessage({id:v.name})}</span>
                                            )
                                        }
                                    )
                                }
                            </div>
                            {/*<span className="ew">{ckFlg ? ememo[0] : null}</span>*/}
                        </div>}
                        {
                            isEosType&&
                            <div className="item mb10">
                                <h5 className="alert_infor">
                                    <FormattedMessage  id="yybh地址标签" />
                                    <i className="iconfont ts-show icon-l5 icon-tongchang-tishi bbyh-hover-model" style={{'color':'#3E85A2','fontSize':'14px','cursor':'pointer','paddingLeft':'5px'}}>
                                            <div className="bbyh-caveat-modal" style={{top: "-99px"}}>
                                                <FormattedMessage id="bbyh如您被要求填写地址标签（Destination Tag）或数字ID或备注，请在此处填写；反之则不需要填写"  />
                                            </div>
                                    </i>
                                    <span className="tip_infor" style={{fontSize:'14px','color':'#764B33'}}>
                                        {formatMessage({id:'bbyh（填写错误可能导致资产损失，请仔细核对）'})}
                                    </span>
                                </h5>
                                <div className={`${ckFlg && ebbremark[0]&& 'err'} input_warp`} style={{borderColor:focusBlu=='bbremark'?'#3E85A2':''}}>
                                    <input type="text" name="bbremark" className="input_item" placeholder={formatMessage({id:'请输入地址标签(水印)'})} onChange={setbbremark}  onFocus={(e)=>{this.setCk();this.setFoucs('bbremark');this.fIn(e)}} onBlur={bOut}/>
                                </div>
                                <span className="ew">{ckFlg ? ebbremark[0] : null}</span>
                            </div>
                        }

                    <div className="item mb10">
                        <h5>
                            <FormattedMessage  id="withdraw.text15" />
                        </h5>
                        <div className={`${ckFlg && eaddress[0] && 'err'} input_warp`}  style={{borderColor:focusBlu=='address'?'#3E85A2':''}}>
                            <input type="text" name="address" className="input_item" onChange={setaddress}  onFocus={(e)=>{this.setCk();this.setFoucs('address');this.fIn(e)}} onBlur={bOut} placeholder={formatMessage({id:'请输入您的提现地址'})}/>
                        </div>
                        <span className="ew">{ckFlg ? eaddress[0] : null}</span>
                    </div>
                { addressType==2&&
                    <div className="item mb20">
                    <h5><FormattedMessage  id="邮箱验证码"/></h5>
                        <div className={`${ckFlg && eemailcode[0] && 'err'} input_div_1 input_div_2`}  style={{borderColor:focusBlu=='emailcode'?'#3E85A2':''}}>
                            <input type="text" className="input_1 input_2"  placeholder={formatMessage({id: "请输入邮箱验证码（水印）"})}  autoComplete="off" onChange={setEmailcode} name="emailcode" value={emailcode} onFocus={(e)=>{this.setCk();this.setFoucs('emailcode');this.fIn(e)}} onPaste={setEmailcode}/>
                            <Sms {...{codeType}} fn={(k, v)=>{setGetCodes('emailcode', 1);this.setCk();this.callError(k, v)}} sendUrl="/userSendCode" errorKey="emailcode" otherData={{userName,type:1,currency:UpperCurrentCoin}} codeType={CODETYPE} />
                        </div>
                        <span className="ew">{ckFlg ? eemailcode[0] : null}</span>
                    </div>
                }
                { addressType==2&&
                <p className="prompt">{formatMessage({id: "温馨提示：您当前为“安全模式”，新增提现地址后将被锁定24小时。"})}</p>
                 }

                 <div className="btns_div">
                    <span className="btn close_alertBox" onClick={() => this.props.closeModal()}><FormattedMessage  id="cancel" /></span>
                    <span className="btn submit" onClick={this.onSubmitAddress}><FormattedMessage  id="withdraw.text67" /></span>

                    </div>
                    </div>
                    // {/* </ReactModal> */}
                    // </div>
        )
    }
}

const mapStateToProps = (state,ownProps) => {
    return {
        curCoin:state.withdraw.curCoin,
        addressType:state.withdraw.withdrawAddressAuthenType,
        mobileStatu:state.session.baseUserInfo.mobileStatus
    }
}

const mapDispatchToProps = dispatch => {
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
        getAddressAuthenType:()=>{
            dispatch(getWithdrawAddressAuthenType())
        },
        addWithDrawAddress:(values,cb) => {
            dispatch(addWithDrawAddress(values)).then(cb)
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(AddressModale))
