import React from 'react';
import axios from 'axios';
import qs from 'qs';
import { connect } from 'react-redux'
import Form from '../../../decorator/form';
import Sms from '../../../components/user/sms';
import cookie from 'js-cookie';
import { optPop } from '../../../utils';
import ReactModal from '../../../components/popBox'
import { COIN_KEEP_POINT,DISMISS_TIME,DOMAIN_VIP,COUNT_DOWN_ONE_MINUTE,COOKIE_UNAME,USDTARGLIST} from '../../../conf'
import { fetchModifyAddrss } from '../../../redux/modules/withdraw'
import { FormattedMessage,FormattedHTMLMessage, injectIntl ,FormattedDate} from 'react-intl';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend,notifClear,notifDismiss } = notifActions;

const CODETYPE = 8;
@Form
class EditorAdress extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            memo:'',
            ckFlg:'',
            address:'',
            addressTag:'',
            isEosType:false,
            id:'',
            editorBox:'',
            focusBlu:'',
            usdtArgList:[],
            argIndex: 0
        }
        this.submitStatus = 1
        this.onSubmitAddress = this.onSubmitAddress.bind(this)
        this.toUpperCase = this.toUpperCase.bind(this)
        this.setCk = this.setCk.bind(this)
        this.chooseSureAddressBtn = this.chooseSureAddressBtn.bind(this)
        this.setFoucs = this.setFoucs.bind(this)
    }

    componentDidMount(){
        const {address,id,memo,addressTag,isEosType,agree} = this.props
        let {argIndex} = this.state;
        console.log('agreement =======' + agree);
        agree == 102 ? argIndex = 1 : argIndex = 0;
        this.setState({
            address,id,memo,addressTag,isEosType,argIndex
        })
        this.setState({
            usdtArgList: USDTARGLIST
        })
    }
    // componentWillReceiveProps(nextProps){
    //     const {address,id,memo} = nextProps
    //     this.setState({
    //         address,id,memo
    //     })
    // }

    setFoucs(name){
        this.setState({
            focusBlu:name
        })
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
    chooseSureAddressBtn(id){
        const { memo } = this.state,
        { formatMessage } = this.intl;
        this.setCk();
           if(!this.hasError(['memo'])){
               this.props.fetchModifyAddrss({
                id,
                memo:memo.trim(),
                coint:this.props.curCoin
            },(res)=>{
                const result = res.data
                this.props.closeModal()
                if(result.isSuc){
                    optPop(() => {},result.des);
                    this.props.fetchHisAddress()
                }else{
                    optPop(() => {},result.des);
                }
            })

            }


    }

    onSubmitAddress(){
        const { address,memo,emailcode,addressType } = this.state,
             { formatMessage } = this.intl;
             let alldata = []
             if(addressType ==2){
                 alldata = ["memo","address","emailcode"]
             }else{
                alldata = ["memo","address"]
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
                    address:address,
                    memo:memo,
                    emailcode:emailcode,
                    agreement:this.state.argIndex,
                },(res) => {
                    const result = res.data
                    this.submitStatus = 1
                    if(result.isSuc){
                        this.props.closeModal()
                        optPop(() => {},result.des);
                        this.props.HisAddressReload()
                        // browserHistory.push (`/bw/manage/account/download?coint=${this.props.curCoin}`)
                    }else{

                        optPop(() => {},result.des);
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
                   {fIn, bOut,setEmailcode,codeType,setGetCodes,setmemo,setaddress} = this,
                   {errors,ckFlg,memo,address,id,focusBlu,addressTag,isEosType,usdtArgList,argIndex} = this.state,
                   {emailcode:eemailcode = [],memo:ememo = [],address:eaddress=[]} = errors;
            const userName = cookie.get(COOKIE_UNAME);
            const path = window.location.href.toUpperCase();
            let showFlag = path.includes('USDT');

        return (
            // <ReactModal ref={modal => this.modal = modal}>
                <div className="alertBox_body alertBox_body_withdraw">
                         <div className="head ">
                            <h3>{formatMessage({id: "编辑XXX提现地址"}).replace('XXX', cueCoin)}</h3>
                            <a className="right iconfont icon-guanbi-moren" onClick={() => this.props.closeModal()}></a>
                        </div>
                        {
                            isEosType&&
                            <div className="item mb10 tiltes" style={{'paddingTop':'20px'}}>
                                <h5 className="alert_infor">
                                    <span className="bbyitem mb10hspan">
                                        <FormattedMessage  id="yybh地址标签" />
                                        <span>{addressTag}</span>
                                    </span>
                                    {/* <i className="iconfont ts-show icon-l5 icon-tongchang-tishi bbyh-hover-model" style={{'color':'#3E85A2','fontSize':'14px','cursor':'pointer','paddingLeft':'5px'}}>
                                            <div className="bbyh-caveat-modal" style={{top: "-99px"}}>
                                                <FormattedMessage id="bbyh如您被要求填写地址标签（Destination Tag）或数字ID或备注，请在此处填写；反之则不需要填写"  />
                                            </div>
                                    </i>
                                    <span className="tip_infor" style={{fontSize:'14px','color':'#764B33'}}>
                                        {formatMessage({id:'bbyh（填写错误可能导致资产损失，请仔细核对）'})}
                                    </span> */}
                                </h5>
                            </div>

                        }
                        <div className="tiltes mb20 mt30" style={{'fontSize':'14px'}}>{formatMessage({id: "withdraw.text15"})}{address}</div>
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
                                                <span key={i} className={`${argIndex  == v.value && 'active'}`} style={{cursor:"not-allowed"}}>{formatMessage({id:v.name})}</span>
                                            )
                                        }
                                    )
                                }
                            </div>
                            {/*<span className="ew">{ckFlg ? ememo[0] : null}</span>*/}
                        </div>}
                        <div className="item mb10 tiltes">
                            <h5 className="alert_infor">
                                <FormattedMessage  id="yybh地址备注" />
                                {/* <i className="iconfont ts-show icon-l5 icon-tongchang-tishi bbyh-hover-model" style={{'color':'#3E85A2','fontSize':'14px','cursor':'pointer','paddingLeft':'5px'}}>
                                        <div className="bbyh-caveat-modal" style={{top: "-99px"}}>
                                            <FormattedMessage id="bbyh如您被要求填写地址标签（Destination Tag）或数字ID或备注，请在此处填写；反之则不需要填写"  />
                                        </div>
                                </i> */}
                                {/* <span className="tip_infor" style={{fontSize:'14px','color':'#764B33'}}>
                                    {formatMessage({id:'bbyh（填写错误可能导致资产损失，请仔细核对）'})}
                                </span> */}
                            </h5>
                            <div className={`${ckFlg && ememo[0] && 'err'} input_warp`} style={{borderColor:focusBlu=='memo'?'#3E85A2':''}}>
                                <input type="text" name="memo" maxLength="20" className="input_item" onChange={setmemo} value={memo} onFocus={(e)=>{this.setCk();this.setFoucs('memo');this.fIn(e)}} onBlur={bOut}  placeholder={formatMessage({id:'bbyh请输入地址备注'})}/>
                            </div>
                            <span className="ew">{ckFlg ? ememo[0] : null}</span>
                        </div>
                        <div className="btns_div mb40">
                            <span className="btn close_alertBox" onClick={() => this.props.closeModal()}><FormattedMessage  id="暂不设置" /></span>
                            <span className="btn submit" onClick={() => this.chooseSureAddressBtn(id)}><FormattedMessage  id="设置标签" /></span>
                        </div>
                    </div>
                // </ReactModal>
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
        fetchModifyAddrss:(params,cb)=>{
            dispatch(fetchModifyAddrss(params)).then(cb)
        }
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(EditorAdress))





























