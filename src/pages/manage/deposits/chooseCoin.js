import React from 'react';
import { Link ,withRouter} from 'react-router';
import { optPop } from '../../../utils';
import { FormattedMessage, injectIntl } from 'react-intl';
import Form from '../../../decorator/form';
import { COIN_KEEP_POINT, DISMISS_TIME,DOMAIN_VIP,USDTARGLIST } from '../../../conf';
import axios from 'axios'
import qs from 'qs'
import copy from 'copy-to-clipboard';
//import {withRouter} from 'react-router'
const BigNumber = require('big.js')
import ScrollArea from 'react-scrollbar'
import { checkEosIcoin } from '../../../utils'

@Form
class ChooseCoin extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            checkcoin:'',
            showSearch:false,
            currentAddress:'',
            confirmTimes:0,
            addressTag:'',
            btnStus:0,
            bordeBlue:false,
            isEosType:props.isEosType,
            descript:'',
            inConfirmTimes:'',
            outConfirmTimes:'',
            props: {
                ln: props.language === 'zh' ? 'cn' : props.language === 'en' ? 'en' : 'hk'

            },
            usdtArgList: [],
            argIndex : 0,
            usdtList: [], // usdt 币种列表
            usdtDesc: [] // usdt 说明

        }
        this.copyUrl = this.copyUrl.bind(this);
        this.copyLabel = this.copyLabel.bind(this);
        this.searchAccount = this.searchAccount.bind(this)
        this.hideSearchAccount = this.hideSearchAccount.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.clearFilterVal = this.clearFilterVal.bind(this)
        this.changeFocus = this.changeFocus.bind(this)
        this.changeBlur = this.changeBlur.bind(this)
        // this.otherInfor = this.otherInfor.bind(this)
        this.getTipInfor = this.getTipInfor.bind(this)
        this.getTipText = this.getTipText.bind(this)
        this.getAdress = this.getAdress.bind(this)
        this.setUsdtArg = this.setUsdtArg.bind(this)
    }

    componentWillMount(){
         //this.props.location.query.coint;
        //this.props.fetchManageCoinInfo()
    }

    componentDidMount(){
        //  const coinType = this.props.iconType
        // let isEosType = checkEosIcoin(coinType.toUpperCase());
        // this.setState({
        //     isEosType
        // })
        //this.props.fetchManageCoinInfo()
        if(!this.props.accountList.isloaded){
            this.props.fetchManageInfo()
        }
        document.addEventListener('click',this.hideSearchAccount)
        this.getTipInfor()
        this.setState({
            usdtArgList : [...USDTARGLIST]
        },() =>{
            this.getAdress()
        })
       // console.log("bgin =======>" +new BigNumber('0E-9').toFixed(8))

    }
    componentWillReceiveProps(nextProps){
        // if(nextProps.coinList.isloaded){
        //     for(let i = 0; i < nextProps.coinList.data.length;i++){
        //         if(nextProps.coinList.data[i]['coinName'] == this.props.location.query.coint.toLowerCase()){
        //
        //             //console.log(this.props.currentCoin.toLowerCase())
        //             //console.log( '+++++++++++' ,nextProps.coinList.data)
        //             this.setState({
        //                 currentAddress: nextProps.coinList.data[i]['address'],
        //                 confirmTimes: nextProps.coinList.data[i]['confirmTimes'],
        //                 addressTag :nextProps.coinList.data[i]['addressTag'],
        //             })
        //         }
        //     }
        // }
        // if(nextProps.currentCoin !== this.props.currentCoin){
        //     //console.log( '+++++++++++' + nextProps.currentCoin)
        //     //console.log('+++++++++++' + this.props.currentCoin)
        //     this.setState({
        //         isEosType:checkEosIcoin(nextProps.currentCoin.toUpperCase()),
        //         showSearch:false,
        //     })
        // }
    }

    componentWillUnmount() {
        document.removeEventListener('click',this.hideSearchAccount)
    }

    copyUrl(){
        const { formatMessage } = this.intl;
        copy(this.state.currentAddress,{
            debug: true
        });
        optPop(() => {}, formatMessage({id: "复制成功"}));
    }

    copyLabel(){
        const { formatMessage } = this.intl;
        copy(this.state.addressTag,{
            debug: true
        });
        optPop(() => {}, formatMessage({id: "复制成功"}));
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

    //搜索查看币种
    searchAccount(e){
        this.setState({
            showSearch:true
        })
        e.nativeEvent.stopImmediatePropagation();
    }
    hideSearchAccount(e){
        const {showSearch} = this.state
        if(showSearch){
            this.setState({
                showSearch:false
            })
        }
    }

    clearFilterVal(){
        this.setState({
            checkcoin:'',
            btnStus:0
        })
    }

    getTipInfor(inConfirmTimes = '',outConfirmTimes = ''){
        let {iconType}  = this.props
        let fundsType = null
        axios.get(DOMAIN_VIP + "/manage/getAssetsDetail").then(res =>{
            if(res.status == '200'){
                //console.log(res);

                let data = eval(res["data"]);
                for(let i in data){
                    if(i == iconType){
                        fundsType = data[i].fundsType;
                        break;
                    }
                }

                this.getTipText(fundsType,inConfirmTimes,outConfirmTimes)

            }
        })
    }

    getTipText(fundsType,inConfirmTimes,outConfirmTimes){
        axios.post(DOMAIN_VIP + "/descript",qs.stringify({fundsType})).then(res =>{
             let data = res["data"];
             if(data.isSuc){
                if(data.datas){
                    let coin = this.props.location.query.coint.toUpperCase();
                    if(coin.includes("USDT")){
                        let list = data.datas;
                        for (let l of list){
                            l.descript = l.descript.replace(new RegExp('##coin##','g'),coin);
                            l.descript = l.descript.replace(new RegExp('##count1##','g'),inConfirmTimes)
                            l.descript = l.descript.replace(new RegExp('##count2##','g'),outConfirmTimes)
                        }
                        console.log(list);
                        this.setState({
                            descript:list[0].descript,
                            usdtDesc:[...list]
                        })
                    }else{
                        let descript = data.datas.descript;
                        //let descript = 'jdksjkdakdljjkjdkaldsdsd,##count1##,djakjdakjdlk##coin##,dsadad##count2##'
                        // let {inConfirmTimes,outConfirmTimes} = this.state;

                        descript = descript.replace(new RegExp('##coin##','g'),coin);
                        descript = descript.replace(new RegExp('##count1##','g'),inConfirmTimes)
                        descript = descript.replace(new RegExp('##count2##','g'),outConfirmTimes)
                        // let regs= /(\w*)##coin##(.*)##count1##(.*)##count2##(.*)/g;
                        // descript = descript.replace(regs,`$1${coin}$2${inConfirmTimes}$3${outConfirmTimes}$4`)
                        this.setState({
                            descript
                        })
                    }
                 }
             }

        })
    }

    getAdress = () =>{
        axios.get(DOMAIN_VIP + "/manage/account/charge/rechargecoininfo").then(res =>{
           let data = res.data;
           let coin = this.props.location.query.coint.toUpperCase();
           // console.log("============" + res)

          if(data.isSuc){
              // usdt 协议初化
              if (coin.includes('USDT')){
                let list = data.datas.usdtlist;
                let {usdtArgList} = this.state;
                let tempList = [];
                let {argIndex} = this.state;
                  for (let i = 0; i < usdtArgList.length; i++) {
                      tempList[i] = {...usdtArgList[i],...list[i]};
                  }
                  // console.log("======>>>><<<<<<" +tempList)
                  this.setState({
                      usdtArgList: [...tempList]
                  },() =>{
                      // let {argIndex} = this.state;
                      // console.log("======>>>><<<<<<" +this.state.usdtArgList)
                      list.forEach((v,i) =>{
                        !v.canCharge ? (argIndex += 1):argIndex
                      })
                      this.setState({
                          argIndex
                      })
                  })
                this.setState({
                    usdtList:[...list],
                    addressTag:list[argIndex].addressTag,
                    currentAddress:list[argIndex].address,
                },() =>{
                    // this.setUsdtArg(list[argIndex]);
                    console.log(this.state.usdtList);
                })
                  this.getTipInfor(list[0].inConfirmTimes,list[0].outConfirmTimes)
              }else{
                  // 其他币种
                  let list = data.datas.list;
                  //console.log(list);
                  for(let i =0 ; i<list.length;i++){
                      if(list[i].coinName == this.props.location.query.coint.toLowerCase()){
                          this.setState({
                              addressTag:list[i].addressTag,
                              currentAddress:list[i].address,
                          })
                          this.getTipInfor(list[i].inConfirmTimes,list[i].outConfirmTimes)
                      }
                  }

              }
          }

        })

    }
    // 切换 usdt 协议
    setUsdtArg = (v) =>{
        let i;
        if (v.canCharge){
            i = v.value;
            this.setState({
                argIndex : i,
                addressTag:this.state.usdtList[i].addressTag,
                currentAddress:this.state.usdtList[i].address,
                descript:this.state.usdtDesc[i].descript,
            })
        } else {
            return false;
        }
    }

    render(){
        const { data,isloading,isloaded } = this.props.coinList;
        let { bordeBlue,checkcoin,showSearch,currentAddress,addressTag,confirmTimes,btnStus ,isEosType,props:lan_props,descript,usdtArgList,argIndex} = this.state
        const upperCoinName = this.props.currentCoin.toUpperCase()
        const allowWithdraw = this.props.assets.isloaded&&this.props.assets.data[upperCoinName].canWithdraw
        const { formatMessage } = this.props.intl;
        const path = this.props.location.query.coint.toUpperCase();
        let showFlag = path.includes('USDT');
        // usdtArgList.forEach((v,i)=>{
        //     !v.canCharge ? (argIndex = argIndex+  1): argIndex;
        // })
        // this.setState({
        //     argIndex
        // })
        // console.log('==========' + showFlag);
        // let _descript = this.descriptCoin(descript)

        return (
                <div className="bk-assets bk_pay_asset" style={{'paddingBottom':'40px'}}>
                    <h2 className="assets-title assets-title-search">
                        <div className="search-box">
                            <span className="search"><FormattedMessage id="balance.text15" /> {upperCoinName}{showSearch?<i className="iconfont icon-xialajiantou-yiru-copy" onClick={(e)=>this.hideSearchAccount(e)}></i>:<i className="iconfont icon-xialajiantou-moren"  onClick={(e)=>this.searchAccount(e)}></i>}</span>
                            <div id="search_warp"  style={{height:'auto'}} onClick={(e) => this.searchAccount(e)} className={`${showSearch?'show':'hide'} ${this.props.language}_cong`}>
                                <div className={bordeBlue?"input_warp borde-blue":'input_warp'}>
                                    <input type="text" className="search_input" value={checkcoin} name="checkcoin" onChange={this.handleInputChange}  onFocus={this.changeFocus} onBlur={this.changeBlur}/>
                                    <button onClick={btnStus==1&&this.clearFilterVal} className={btnStus==0?"iconfont icon-search-bizhong":"iconfont icon-shanchu-moren"}></button>
                                </div>
                                <section  id="btc_list">
                                <ScrollArea stopScrollPropagation={true}  className="btc_list scrollarea trade-scrollarea scrollarea-content">

                                   { this.props.accountList.isloaded?
                                    Object.keys(this.props.accountList.data).filter((currentValue,index) => {
                                        if(checkcoin){
                                            return currentValue.indexOf(checkcoin.toUpperCase()) != -1
                                        }else{
                                            return true;
                                        }
                                    })
                                    // .sort((a,b) => {
                                    //     return this.props.accountList.data[b].propTag < this.props.accountList.data[a].propTag?1:-1;
                                    // //     return this.props.accountList.data[b].balance-this.props.accountList.data[a].balance
                                    // })
                                    .map((key,index) => {
                                            BigNumber.RM=0;
                                            if(this.props.accountList.data[key].canCharge){
                                                return (
                                                    <a key={index}  href={"/bw/manage/account/charge?coint="+this.props.accountList.data[key].propTag} className="item clearfix">
                                                        <span className="left color_font">{this.props.accountList.data[key].propTag}</span>
                                                        <span className="right">{new BigNumber(this.props.accountList.data[key].balance).toFixed(COIN_KEEP_POINT)}</span>
                                                    </a>
                                                )
                                            }

                                    }):""


                                }
                                 </ScrollArea>
                                </section>
                            </div>
                        </div>
                    </h2>
                    {allowWithdraw&&<ul className="tab-link">
                     <a className="tab-a" href={`/bw/manage/account/download?coint=${upperCoinName}`}><span className="iconfont icon-qiehuanchongzhitixian-moren"></span><FormattedMessage id="提现" /></a>
                        {/* <li className="active"><Link to="javascript:void(0);"><FormattedMessage id="balance.text15" /></Link></li>
                        <li><Link to={`/bw/manage/account/download?coint=${upperCoinName}`}><FormattedMessage id="balance.text16" /></Link></li> */}
                    </ul>}
                    <section className={`${!this.props.isSafePwd&&'tablist-tips-warp'} clearfix`}>
                        <div className="bk-tabList-bd">
                                <div className="deposit-box">
                                {
                                    !this.props.isSafePwd ? (
                                        <div className="key_wrap wid60">
                                            <div className="shimingpoper">
                                                <div className="safepwd-tip">
                                                    <FormattedMessage id="deposit.text18" />
                                                    <p className="mt10 mb0">
                                                        <Link to={`/bw/mg/setPayPwd?router=charge&coint=${upperCoinName?upperCoinName:'BTC'}`} className="btn btn-set mr15" target="_self"><FormattedMessage id="deposit.text19" /></Link>
                                                    </p>
                                                </div>
                                            </div>
                                        </div>
                                    ):(
                                        <div className="key_wrafalse clearfixp wid60">
                                            <h4 className="assets-sub-title">
                                                <FormattedMessage id="deposit.text3"  values = {{coinName : this.props.currentCoin.toLocaleUpperCase()}}  />
                                            </h4>
                                            {
                                                showFlag ?
                                                    <div className="coin-agreement">
                                                        {
                                                            usdtArgList.map((v,i) => {
                                                                let style = v.canCharge ? "pointer" : "not-allowed",
                                                                    bgc = !v.canCharge ? '#737A8D' : ''
                                                                // !v.canCharge ?  (argIndex = argIndex + 1) : argIndex;
                                                                return(
                                                                      <span key={i} className={`${argIndex  == v.value && 'active'}`}  style={{cursor:style,backgroundColor:bgc}} onClick={() => {this.setUsdtArg(v)}}>{formatMessage({id:v.name})}</span>
                                                                    )
                                                                }
                                                            )
                                                        }
                                                    </div>
                                                    : null
                                            }
                                            <div className="key-box clearfix" style={{paddingTop:'30px'}}>
                                                <div className="keyPreImg">
                                                    <img src={'/ac/qrcode?code='+currentAddress+'&width=115&height=115'} alt="" className="qrcode"/>
                                                    {/* <img src={Imgs} alt="" className="qrcode"/> */}
                                                    <span className="bbyhKeyTip">
                                                        <span className="bbyhKeyText"><FormattedMessage id="bbyh充币地址"/></span>
                                                        <i className="iconfont ts-show icon-l5 icon-tongchang-tishi bbyh-hover-modelRight" style={{'color':'#3E85A2','fontSize':'14px','cursor':'pointer'}}>
                                                            <div className="bbyh-caveat-modal" style={{top: lan_props.ln == 'en'?'-63px':'-42px'}}>
                                                                <p>
                                                                    <FormattedMessage id="bbyh什么是充币地址？"  />
                                                                </p>
                                                                <FormattedMessage id="bbyh您的充币地址相当于您的银行账号，如果您想进行数字资产的转移，您只需提供该币种在充币目标的充币地址即可。"  />
                                                            </div>
                                                        </i>
                                                    </span>
                                                </div>
                                                <div className="keyPreCopy">
                                                     <span className="text-done" ref="qrcode">{currentAddress}</span>
                                                    {/* <span className="text-done" ref="qrcode">dksdlsakkdladsadskdsddklfjjj</span> */}
                                                    <a href="javascript:void(0);" className="btns btn-skip copy" onClick={this.copyUrl} ><FormattedMessage id="deposit.text17"  /></a>
                                                </div>
                                                <p className="ft14"><FormattedMessage id="deposit.text4"  /></p>
                                            </div>
                                            {
                                                isEosType&&
                                                <div className="key-box clearfix" style={{marginTop:'70px',paddingBottom:"40px"}}>
                                                    <div className="keyPreImg" style={{paddingTop:"-20px"}}>
                                                        <img src={'/ac/qrcode?code='+addressTag+'&width=115&height=115'} alt="" className="qrcode"/>
                                                        {/* <img src={Imgs} alt="" className="qrcode"/> */}
                                                        <span className="bbyhKeyTip">
                                                            <span className="bbyhKeyText"><FormattedMessage id="bbyh地址标签"/></span>
                                                            <i className="iconfont ts-show icon-l5 icon-tongchang-tishi bbyh-hover-modelRight" style={{'color':'#3E85A2','fontSize':'14px','cursor':'pointer'}}>
                                                                <div className="bbyh-caveat-modal" style={{top: lan_props.ln == 'en'?'-123px': '-71px'}}>
                                                                    <p>
                                                                        <FormattedMessage id="bbyh关于地址标签"  />
                                                                    </p>
                                                                    <span>{formatMessage({id:'bbyhXXX充币同时需要一个充值地址和地址标签（又名Tag/memo等），地址标签是一种保证您的充币地址唯一性的数字串，与充币地址成对出现一一对应。'}).replace(/%%/g,this.props.currentCoin.toUpperCase())}</span>
                                                                    <span style={{'display':'block'}}>{formatMessage({id:'bbyh请您务必遵守正确的XXX充值步骤，在提币时输入完整信息，否则将面临丢失币的风险！'}).replace(/%%/g,this.props.currentCoin.toUpperCase())}</span>
                                                                    {/* <FormattedMessage id="bbyhXXX充币同时需要一个充值地址和地址标签（又名Tag/memo等），地址标签是一种保证您的充币地址唯一性的数字串，与充币地址成对出现一一对应。"/> */}
                                                                </div>
                                                            </i>
                                                        </span>
                                                    </div>
                                                    <div className="keyPreCopy">
                                                        <span className="text-done" ref="qrcode">{addressTag}</span>
                                                        {/* <span className="text-done" ref="qrcode">dksdlsakkdladsadskdsddklfjjj</span> */}
                                                        <a href="javascript:void(0);" className="btn btn-skip copy" onClick={this.copyLabel} ><FormattedMessage id="deposit.text17"  /></a>
                                                    </div>
                                                    <p className="ft14"><FormattedMessage id="bbyh由于浏览器兼容性问题，可能会复制失败，请手动复制地址标签或扫码充值。"  /></p>
                                                </div>
                                            }

                                        </div>

                                        )
                                    }
                                    <div className="pay-tip wid40">
                                        <h4><FormattedMessage  id="deposit.text5"  /></h4>
                                        <p dangerouslySetInnerHTML={{ __html: descript }}></p>
                                        {/* <h4><FormattedMessage  id="deposit.text5"  /></h4>
                                        <p><FormattedMessage id="deposit.text6"  values={{coinName : this.props.currentCoin.toLocaleUpperCase()}}/></p>
                                        <p><FormattedMessage id="deposit.text7"  values={{nums : confirmTimes, s: confirmTimes>1?'s':''}}/></p>
                                        <p><FormattedMessage id="deposit.text8" /></p>
                                        {
                                            isEosType&&
                                            <p><span>{formatMessage({id: 'bbyh4.充值%%同时需要一个充值地址和地址标签。警告：如果未遵守正确的%%的充值步骤，币会丢失！'}).replace(/%%/g,this.props.currentCoin.toUpperCase())}</span></p>
                                        } */}
                                    </div>
                                </div>

                      </div>
                    </section>
                </div>

            )
    }
}
export default withRouter(injectIntl(ChooseCoin))








