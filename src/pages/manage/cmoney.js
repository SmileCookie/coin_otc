import React from 'react';
import {FormattedMessage, injectIntl, FormattedDate, } from 'react-intl';
import Nav from "../../components/navigator/nav";
import {browserHistory} from 'react-router'
import {Link} from "react-router";
import {optPop, formatDate} from "../../utils";
import {getUserBaseInfo} from "../../redux/modules/session";
import {connect} from "react-redux";
import Pages from "../../components/pages";
import {CopyToClipboard} from "react-copy-to-clipboard";
import {Dialog} from '../money/dialog'
import EntrustModal from "../../components/entrustBox";
import Transfer from "./transfer";
import {userFinCenInfo, centerDetailsList, tableList, centerInfo} from '../money/index.model'
import '../../assets/css/money.less'
import axios from "axios";
import {DOMAIN_VIP,MONEYMANAGEMENTBASE} from "../../conf";
import ReactModal from '../../components/popBox';
import qs from 'qs';
import Hexagon from '../../components/shapes/hexagon';
import HTab from '../../components/tab/htab';
import { Mc } from '../../components/tab/tabdata';
import { JSEncrypt } from 'jsencrypt';
import Confirm from '../../components/msg/confirm';
import ModelMoney from '../../components/popBox/modelMoney'

const encrypt = new JSEncrypt();

const BigNumber = require('big.js')

// 释放弹出
const SF = (props) => {
    const {svollar, setSvollar, formatMessage, VD, errors, ckVD, safePwd, setSafePwd, safeErrors, ckSafePwd, cp, IAP, DP, clearErrors, clearSafeErrors, } = props;
    const FV = svollar ? svollar : 0;
    const Z = new BigNumber(FV).times(0.2).plus(DP).minus(new BigNumber(cp).div(1.5).div(IAP));
    let MA = cp > 0 ? new BigNumber(cp).div(IAP).div(1.5).minus(DP).times(5) : 0;
    MA = MA > 0 ? MA : 0;
    // console.log(svollar, '=======>>>>>>>');
    return (
        <React.Fragment>
            <div className="clearfix">
                <h3 class="mth" style={{margin:0,float:'left'}}>{formatMessage({id:"释放数量"})}</h3>
                <p className="alt" style={{marginBottom:'10px',fontSize:'12px',float:'left'}}><FormattedMessage id="mbs" values={{m:<span style={{fontSize:'12px',backgroundSize:'11px',paddingRight:'13px'}} className="my_vds_wp">{MA.toFixed(4)}</span>}} /></p>
            </div>
            <div className="pubPops">
                {
                errors
                    ?
                        <span className="ew">{errors}</span>
                    :
                        null
                }
                <input className="lj" type="text" />
                <input value={svollar} maxLength="12" onChange={setSvollar} onBlur={ckVD} onFocus={clearErrors}  type="text" className="my_txt" placeholder={formatMessage({id:"请输入释放数量"})} />
                <input className="lj" type="text" />
                <p className="alt"><FormattedMessage id="说明：释放数量需大于零小于等于可划转数量。" /></p>
            </div>
            <h3 class="mth">{formatMessage({id:"本次交易需要资金密码验证"})}</h3>
            <div className="pubPops">
                {
                    safeErrors
                    ?
                    <span className="ew">{safeErrors}</span>
                    :
                    null
                }
                <input className="lj" type="password" />
                <input maxLength="20" onBlur={ckSafePwd} onFocus={clearSafeErrors} value={safePwd} onChange={setSafePwd} type="password" className="my_txt" placeholder={formatMessage({id:"请输入资金密码"})} />
                <input className="lj" type="password" />
                <p className="alt" style={{margin:0}}>&nbsp;</p>
            </div>

            <ul class="my_titp_wp">
                <li>
                    <FormattedMessage id="您释放" values={{p: <span class='mpic my_vds_wp'>{FV}</span>}} />
                </li>
                <li>
                    <FormattedMessage id="流失贡献为" values={{p: <span class='mpic my_vds_wp'>{new BigNumber(FV).times(0.2).plus(0).toFixed(4)}</span>}} />
                </li>
                <li>
                    <FormattedMessage id="其中 XXX vollar 用作复投" values={{p: <span class='mpic my_vds_wp'>{new BigNumber(cp).div(1.5).div(IAP).toFixed(4)}</span>}} />
                </li>
                <li>
                    <FormattedMessage id="XXX vollar 贡献给VIP静态分红" values={{p: <span class='mpic my_vds_wp'>{(0 < Z ? Z : 0).toFixed(4)}</span>}} />
                </li>
                <li>
                    <FormattedMessage id="备注：释放将扣除20%流失贡献用作复投，多余部分奖励给VIP静态分红。" />
                </li>
            </ul>
        </React.Fragment>
    )
}

/**
 * 理财中心组件
 */

class CMoney extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            centerInfo: {},
            detailsList: '',
            dialogtos: '',
            svollar: "",
            errors: "",
            selectedCode: 0,
            safePwd: "",
            safeErrors: "",
            vid: "",
            vidErrors: "",
            cbVID: "",
            dpFlg: false,
            issw: false,
            modelMoney:false
        }
        this.test = {    //分页测试json
            total: '23',
            pagesize: '5',
            pageIndex: '1'
        }
        this.openDialogIn = this.openDialogIn.bind(this);
        // this.currentPageClick = this.currentPageClick.bind(this);
        this.getInfo = this.getInfo.bind(this);
        this.getTableList = this.getTableList.bind(this);
        this.getVollar = this.getVollar.bind(this);

        this.modaltos = null;

        this.confirm = this.confirm.bind(this);
        this.transOK = this.transOK.bind(this);

        this.setSvollar = this.setSvollar.bind(this);

        this.ckVD = this.ckVD.bind(this);
        this.lc = this.lc.bind(this);

        this.confirmProxy = this.confirmProxy.bind(this);

        this.tabConfig = Mc(props.intl);

        this.setSelected = this.setSelected.bind(this);

        this.setSafePwd = this.setSafePwd.bind(this);

        this.ckSafePwd = this.ckSafePwd.bind(this);

        this.jmp = this.jmp.bind(this);

        this.upVid = this.upVid.bind(this);

        this.setVid = this.setVid.bind(this);

        this.ckVid = this.ckVid.bind(this);

        this.chVidx = this.chVidx.bind(this);

        this.clearUpVid = this.clearUpVid.bind(this);

        this.clearErrors = this.clearErrors.bind(this);

        this.clearSafeErrors = this.clearSafeErrors.bind(this);

        this.clearSimpleErrors = this.clearSimpleErrors.bind(this);

        this.ckdpFlg = this.ckdpFlg.bind(this);
        
        this.cb = this.cb.bind(this);

        this.ckSw = this.ckSw.bind(this);
    }
    ckSw(){
        this.setState({
            issw: true
        })
    }
    cb(flg = 0){
        this.props.router.push('/bw/pm')
    }
    ckdpFlg(){
        this.setState({
            dpFlg: !this.state.dpFlg,
        })
    }
    clearErrors(){
        this.clearSimpleErrors('errors');
    }
    clearSafeErrors(){
        this.clearSimpleErrors('safeErrors');
    }
    clearSimpleErrors(name = ''){
        this.setState({
            [name]: '',
        }, () => {
            this.confirm();
        })
    }
    chVidx(){
        this.ckVid(()=>{
            if(!this.state.vidErrors){
                axios.post(DOMAIN_VIP + '/manage/financial/userVidUpdate', qs.stringify({userVID: this.state.vid})).then((res)=>{
                    res = res.data;
                    this.setState({
                        cbVID: res.isSuc ? this.state.vid : '',
                    }, () => {
                        this.modaltos.closeModal();
                        optPop(() => {}, res.datas.uvidCR ? res.datas.uvidCR : res.des, undefined, !res.isSuc);
                    })
                    
                })

                // this.modaltos.closeModal();
                //         optPop(() => {
                //         }, res.des, undefined, !res.isSuc);
            }
        })
    }
    ckVid(cb = () => {}){
        const {vid} = this.state;
        //if(0 < vid.length && 35 !== vid.length){
            this.setState({
                vidErrors: 35 !== vid.length ? this.props.intl.formatMessage({id: "地址输入错误"}) : ''
            }, () => {
                try{
                    cb();
                } catch(e){

                }
                this.upVid();
            })
        //}
    }
    setVid(e){
        this.setState({
            vid: e.target.value
        }, () => {
            this.upVid();
        })
    }
    clearUpVid(){
        this.setState({
            vidErrors: '',
            vid: '',
        }, () => {
            this.upVid();
        })
    }
    upVid(){
        const {formatMessage} = this.props.intl;
        this.setState({
            dialogtos: (
            <div className="my_tip_wp">
                <h2><FormattedMessage id="修改" /></h2>
                
                <div className="fmt my_titp_wp">
                    <h3 class="mth">{formatMessage({id:"请输入VID地址"})}</h3>
                    <div className="pubPops">
                    {
                        this.state.vidErrors
                        ?
                        <em className="ew">{this.state.vidErrors}</em>
                        :
                        null
                    }
                        <input onBlur={this.ckVid} onChange={this.setVid} value={this.state.vid} type="text" className="my_txt" placeholder={formatMessage({id:"请输入VID地址"})} />
                    </div>
                </div>

                <div className="modal-btn">
                    <div className="modal-foot">
                        <a className="btn ml10" onClick={() => this.modaltos.closeModal()}><FormattedMessage id="cancel" /></a>
                        <a className="btn ml10" onClick={this.chVidx}><FormattedMessage  id="确定" /></a>
                    </div>
                </div>

            </div>)
            ,
        }, () => {
            this.modaltos.openModal();
        })
    }
    jmp(e){
        this.props.router.push(this.getLink(e));
    }
    getLink(e){
        return e.target.getAttribute("link");
    }
    setSelected(flg){
        this.setState({
            selectedCode: flg
        });
    }
    confirmProxy(){
        this.setState({
            svollar: '',
            errors: '',
            safeErrors: '',
            vidErrors: '',
            safePwd: '',
            vid: '',

        }, () => {
            
            this.confirm()
        });
    }
    lc(){
        axios.get(DOMAIN_VIP+'/manage/financial/userFinVdsBalance').then((data)=>{
            const datas = data.data.datas;
            // console.log(datas, '=====>');
            try{
                this.setState({
                    centerInfo:{
                        ...this.state.centerInfo,
                        ...datas,
                    }
                })
            } catch(e){

            }
        });
    }
    setSvollar(e){
        if(/^((\d+\.{0,1}\d{0,2})|\B)$/.test(e.target.value)){
            this.setState({
                svollar: e.target.value
            }, () => {
                this.confirm();
            })
        }
    }

    setSafePwd(e){
        this.setState({
            safePwd: e.target.value,
        }, () => {
            this.confirm();
        })
    }


    transOK(){
        // capitalPwd
        // console.log(this.state.safePwd, '#####')
        this.ckVD(this.ckSafePwd(()=>{
            //console.log(this.state.errors, this.state.svollar, '====---->');
            if(!this.state.errors && !this.state.safeErrors){
                // 加密资金密码传递
                axios.get(DOMAIN_VIP + "/login/getPubTag?t=" + new Date().getTime()).then((res)=>{
                    encrypt.setPublicKey(res.data.datas.pubTag);
                    // encrypt.encrypt(this.state.safePwd)
                

                    // 提交释放金额
                    axios.post(DOMAIN_VIP + '/manage/financial/userAvaTransferAmount?' + qs.stringify({avaTransferAmount: this.state.svollar,capitalPwd: encrypt.encrypt(this.state.safePwd) })).then((res) => {
                        res = res.data;

                        this.modaltos.closeModal();
                        optPop(() => {
                        }, res.des, undefined, !res.isSuc);
                        
                    })
                })
            }
        }))
    }

    ckVD(cb = () => {}){
        
        const UD = this.state.centerInfo.avaTransferAmount ? this.state.centerInfo.avaTransferAmount : 0;// 接口获取的上限值
        //console.log(UD, 'vvvvvvvvvv', this.state.svollar, '=====---->',this.state.svollar > 0 && this.state.svollar <= UD);
        const {formatMessage} = this.props.intl;
        // 验证输入的是否在svollar范围内。
        // console.log(2233);
        // 判断非空
        // if(!this.state.svollar){
            this.setState({
                errors: !this.state.svollar ? formatMessage({id: '请输入释放数量'}) : (this.state.svollar > 0 && this.state.svollar <= +UD ? '' : formatMessage({id: '请输入正确的释放数量。'}) )
            }, () => {
                this.confirm();
                try{
                    cb();
                }catch(e){

                }
            })
        //}

        // 判断范围
    }

    ckSafePwd(cb = () => {}){
        const {formatMessage} = this.props.intl;
        this.setState({
            safeErrors: !this.state.safePwd ? formatMessage({id: '请输入资金密码'}) : '',
        }, () => {
            this.confirm();
            try{
                cb();
            }catch(e){

            }  
        })
    }

    confirm(){
        // 从列表拿到待划转的金额
        // 这里可以直接从接口get到
        // 暂时写死对接口的时候在搞
        const VD  =  this.state.centerInfo.vdsUsdtPrice ? this.state.centerInfo.vdsUsdtPrice : 0;
        const CP  =  this.state.centerInfo.curstaticProfitUsdt ? this.state.centerInfo.curstaticProfitUsdt : 0;
        const IAP =  this.state.centerInfo.investAvergPrice > 0 ? this.state.centerInfo.investAvergPrice : 1;
        const DP  =  this.state.centerInfo.douProfitAmount ? this.state.centerInfo.douProfitAmount : 0;

        const {formatMessage} = this.props.intl;
        const { setSvollar,ckVD,setSafePwd,ckSafePwd, clearErrors, clearSafeErrors, } = this;
        const {svollar,errors,safePwd,safeErrors,} = this.state;
        this.setState({
            dialogtos: (
                <div className="my_tip_wp">
                    <h2><FormattedMessage id="释放" /></h2>
                    
                    <div className="fmt my_titp_wp">
                        <SF clearErrors={clearErrors} clearSafeErrors={clearSafeErrors} DP={DP} IAP={IAP} cp={CP} ckSafePwd={ckSafePwd} safeErrors={safeErrors} errors={errors} ckVD={ckVD}  VD={VD} svollar={svollar} setSvollar={setSvollar} formatMessage={formatMessage} setSafePwd={setSafePwd} safePwd={safePwd}  />
                    </div>
                    <div className="modal-btn">
                        <div className="modal-foot">
                            <a className="btn ml10" onClick={() => this.modaltos.closeModal()}><FormattedMessage id="cancel" /></a>
                            <a className="btn ml10" onClick={() => this.transOK()}><FormattedMessage id="确定" /></a>
                        </div>
                    </div>
                </div>
            )
        }, () => {
            this.modaltos.openModal();
        });
    }

    componentWillMount() {
        // localStorage.setItem('1003614ispay',1)
    }

    componentDidMount() {
        // 数据get
        this.getTableList();
        this.getInfo();
        //setInterval(()=>{
            //this.getInfo();
        //},3000)
        // setInterval(()=>{
        //     this.getVollar();
        // },2000)

        
        setInterval(()=>{
            this.lc();
        },3000)

    }

    getVollar(){
        axios.get(DOMAIN_VIP+'/manage/financial/userFinancialInfo').then((data)=>{
            let vollar = data.data.datas
            this.setState({
                centerInfo:centerInfo
            })        
        })
    }
    // 翻页
    // currentPageClick(index) {
    //     console.log(index);
    // }

    // 获取 用户信息渲染数据或跳转
    getInfo() {
        this.lc();
        userFinCenInfo().then((res) => {
            this.setState({
                centerInfo: {
                    ...this.state.centerInfo,
                    ...res,
                }
            })
        })
    }

    // 获取收益详情
    getTableList() {
        centerDetailsList().then((res) => {
            this.setState({
                detailsList: res
            })
        })
    }

    // 资金划转内部弹窗用 - open
    openDialogIn() {
        this.setState({dialog: <Transfer closeModal={this.modal.closeModal} fromtype={5} totype={1} fundsType='VDS'/>});
        this.modal.openModal();
    }

    getMoneyModel = () =>{
        this.setState({
            modelMoney:false
        })
    }

    render() {
        const URL = [{name: '理财中心', link: MONEYMANAGEMENTBASE +  'cmoney'}, {name: '保险中心', link: MONEYMANAGEMENTBASE + 'cmonerd'}, {name: '投保记录', link: MONEYMANAGEMENTBASE + 'cmrd'}];
        const userInfo = this.props.baseUserInfo;
        const {formatMessage} = this.props.intl;
        const { tabConfig,setSelected, jmp, clearUpVid, ckdpFlg, } = this;
        let {centerInfo, detailsList,selectedCode, cbVID, dpFlg, issw, modelMoney } = this.state;
        //centerInfo.avaTransferAmount = 100;
        //console.log(centerInfo);
        // detailsList = []
        // console.log(this.props.location.pathname);
        return (
            <div className="mwp mwp_d cmoney">
                <Nav path={this.props.location.pathname} ay={URL}/>
                <div className="min_wp min_h527_d">
                    <div className="account clearfix">
                        {/*  用户信息 */}
                        <div className="top clearfix plv">
                            <div className="account-left">
                                <svg className="icon" aria-hidden="true">
                                    <use xlinkHref="#icon-zhanghu-yonghutouxiang"></use>
                                </svg>
                            </div>
                            <div className="account-right">
                                <p><span>UID: {userInfo.userName}</span></p>
                                <p className="clearMarPa"><span>VID: </span><span>{cbVID ? cbVID : centerInfo.userVID}</span><span onClick={clearUpVid} className="my_up_wp"></span></p>
                                <p>
                                    {
                                    false
                                    &&
                                    <Link to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=7">
                                    </Link>
                                    }
                                    <div style={{position:'absolute',right:'20px'}}>
                                        <span className="c_abdrop" onClick={ckdpFlg}>
                                            <i className="per-icon-jiao"></i>
                                            <Hexagon txt={`${formatMessage({id: 'VIP加速器'})} <span class="my_vds_wp jsc">${centerInfo.platNewVipWeekNotPayAmount ? centerInfo.platNewVipWeekNotPayAmount : '--'}</span>`} />
                                            {
                                            dpFlg
                                            &&
                                            <ul>
                                                <li>
                                                    <Link to="/bw/smn/2"><FormattedMessage id="超级主节点收益" /></Link>
                                                </li>
                                                <li>
                                                    <Link to="/bw/pm"><FormattedMessage id="回本加成排名" /></Link>
                                                </li>
                                            </ul>
                                            }
                                        </span>
                                        <Link to="/bw/smnlist">
                                            <Hexagon kz="pz" txt={`${formatMessage({id: '本周释放贡献'})} <span class="my_vds_wp jsc">${centerInfo.platReleaseNotPayAmount ? centerInfo.platReleaseNotPayAmount : '--'}</span>`} />
                                        </Link>
                                    </div>
                                    <span style={{display:'none'}} className="my_hwp">
                                        
                                            <span className="plv">
                                                <Hexagon txt={formatMessage({id: '新VIP红包'})} />
                                                <span className="my_ait"></span>
                                            </span>
                                        <Link to="/bw/money">
                                        </Link>    

                                        <span className="i">
                                            <FormattedMessage id="本周新VIP加成:" />
                                        </span>
                                        {
                                        false
                                        &&
                                        <span className="my_vds_wp">{centerInfo.platNewVipWeekAmount}</span>
                                        }
                                        <span className="">{formatMessage({id: "暂未开启"})}</span>
                                    </span>
                                    
                                    <span>{formatMessage({id: "可用余额"})}: </span><span className="my_vds_wp">{centerInfo.userFinVdsAmount}</span><span
                                     className="money-btn mar-lt20"
                                    onClick={this.openDialogIn}>{formatMessage({id: "划转"})}</span>

                                    <Link to="/bw/manage/account/fbchargeDownHistory?type=2&fl=0" style={{marginRight:'20px',width:'auto',minWidth:'100px',padding:'0 10px',color:'#FFF'}} className="money-btn mar-lt20">{formatMessage({id: "划转记录-1"})}</Link>
                                    {
                                    false
                                    &&
                                    <React.Fragment>
                                        <span className="cyrs">{formatMessage({id: "参与人数"})}:</span>
                                        <span style={{color:"#fff",marginRight:'20px'}}>{centerInfo.proTotalUser}</span>
                                    </React.Fragment>
                                    }

                                    <Link to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=6" className="my_txt_cz">{formatMessage({id: "获得回馈:"})} 
                                    <span className="my_vds_hd">{centerInfo.ecologySystemAmount}</span></Link>
                                    <span>{formatMessage({id: "回馈总额:"})}</span>
                                    <span className="my_vds_hd">{centerInfo.platEcologySystemAmount}</span>

                                </p>
                            </div>
                        </div>
                        <div className="account-center"></div>
                        {/*  理财中心 cont  */}
                        <div className="my_t_wp">
                            <HTab list={tabConfig} currentFlg={selectedCode} setSelected={setSelected}></HTab>
                        </div>
                        <div className="money-cont clearfix">
                        {
                            false
                            &&
                            <ul className="money-ul fl-lt">
                                
                                <li><p><span>{formatMessage({id: "邀请人数"})}</span><span
                                    className="font-fff">{centerInfo.directInvitationNum}</span></p></li>

                                <li><Link style={{color:'#9199AF'}} to="/bw/manage/account/cmoneytree"><p><span>{formatMessage({id: "团队邀请总数"})}</span><span
                                    className="font-fff" style={{color:'#09A5C5'}}>{/*centerInfo.invitationTotalNum*/}{formatMessage({id: "请点击查看"})}</span></p></Link></li>

                                <li><p><span>{formatMessage({id: "推荐人用户名"})}</span><span
                                    className="font-fff">{centerInfo.pInvitationUserName}</span></p></li>

                                <li><p><span>{formatMessage({id: "收益估值:"})}</span><span
                                    className="font-fff">{centerInfo.investUsdtAmount} USDT</span></p></li>
                                    

                                <li>
                                    {
                                    false
                                    &&
                                    <p><span>{formatMessage({id: "已获收益"})}</span><span style={{position:'relative'}} 
                                        className="font-fff">{centerInfo.avaTotalProfitAmount} Vollar<em style={{display:'none'}} className="lock"></em></span>
                                    </p>
                                    }
                                    <Link style={{color:'#9199AF'}} to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2"><p><span>{formatMessage({id: "已获收益"})}</span><span
                                    className="font-fff" style={{color:'#09A5C5'}}>{centerInfo.avaTotalProfitAmount} Vollar</span></p></Link>
                                    
                                </li>

                                <li><p><span>{formatMessage({id: "投资日期:"})}</span><span
                                    className="font-fff">
                                    {
                                    centerInfo.profitTime
                                    ?
                                    formatDate(new Date(+centerInfo.profitTime)).replace(',', '').replace(/\//g, '-')
                                    :
                                    null
                                    }
                                    </span></p></li>
                                

                                
                                <li><p  style={{display:'inline-block'}}><span>{formatMessage({id: "我的邀请码:"})}</span><span
                                    className="font-fff" style={{marginRight:'10px'}}>{centerInfo.invitationCode}</span></p>
                                    <CopyToClipboard onCopy={() => {
                                        optPop(() => {
                                        }, formatMessage({id: '复制成功'}), {timer: 1500})
                                    }} text={ `${centerInfo.invitationCode ? centerInfo.invitationCode : '--'}` }>
                                        <span className="money-btn">{formatMessage({id: "复制"})}</span>
                                    </CopyToClipboard>
                                </li>

                                
                                
                                

                                <li style={{height:'auto',paddingTop:'40px'}}><p><span>{formatMessage({id: "层级建点奖励:"})}({formatMessage({id: "层级"}, {p:centerInfo.hierarchyBuildFloor})}):</span><span
                                    className="font-fff">{centerInfo.hierarchyBuildAmount} vollar</span></p></li>
                                <li><p><span>{formatMessage({id: "级别晋升奖励:"})}({centerInfo.levelPromotionRatio}%):</span><span
                                    className="font-fff">{centerInfo.levelPromotionAmount} vollar</span></p></li>
                                <li><p><span>{formatMessage({id: "VIP分红奖励:"})}</span><span
                                    className="">{formatMessage({id: "暂未开启"})}</span></p></li>
                                <li><p><span>{formatMessage({id: "VIP分红权重"})}</span><span
                                    className="font-fff">{centerInfo.superNodeWeight}</span></p></li>
                                
                                

                                <li><p><span>{formatMessage({id: "VIP点位加成奖励"})}</span><span
                                    className="">{formatMessage({id: "暂未开启"})}</span></p></li>

                                {
                                false
                                &&
                                <li><p>
                                    <span>{formatMessage({id: "推荐奖励:"})}</span><span>{formatMessage({id: "暂未开启"})}</span>
                                </p></li>
                                }
                                
                            </ul>
                        }
                        {/*信息总览*/}
                        {
                        0 === +selectedCode
                        &&
                        <React.Fragment>
                        <table className="my_c_gwp">
                            <tbody>
                                <tr>
                                    <td width="235">{formatMessage({id: "参与人数"})}:</td>
                                    <td className="p" width="350">{centerInfo.proTotalUser}</td>
                                    <td width="235">{formatMessage({id: "总额"})}:</td>
                                    <td className="p">{centerInfo.sumInvestUsdtAmount} USDT</td>
                                </tr>
                                <tr>
                                    <td width="235">{false && <Link className="my_txt_cz" to=""></Link>}<Link className="my_txt_cz" to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=5">{formatMessage({id: "超级主节点累计分配:"})}</Link></td>
                                    <td className="" width="350"><span
                                    className=""><Link className="my_txt_cz" to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=5"><span className="my_vds_wp">{centerInfo.platSuperNodePayAmount}</span></Link></span></td>
                                    <td width="235">{formatMessage({id: "超级主节点待分配:"})}</td>
                                    <td className=""><span className="my_vds_wp">{centerInfo.platSuperNodeNotPayAmount}</span></td>
                                </tr>
                            </tbody>
                        </table>
                        <div className="my_jg_wp"></div>
                        <table className="my_c_gwp">
                            <tbody>
                                <tr>
                                    <td width="235"><FormattedMessage id="邀请人:" /></td>
                                    <td className="p" width="350">{centerInfo.pInvitationUserName}</td>
                                    <td width="235"><FormattedMessage id="直属关系:" /></td>
                                    <td className="p">{centerInfo.physicsSupName}</td>
                                </tr>
                                <tr>
                                    <td width="235"><FormattedMessage id="成功邀请:" /></td>
                                    <td className="p" width="350">{centerInfo.directInvitationSucNum}</td>
                                    <td width="235"><Link className="my_txt_cz" to=""><FormattedMessage id="团队邀请:" /></Link></td>
                                    <td className="p"><Link className="my_txt_cz font-fff" to="/bw/manage/account/cmoneyTree">{centerInfo.invitationTotalNum}</Link></td>
                                </tr>
                                <tr>
                                    <td width="235"><FormattedMessage id="我的邀请码:" /></td>
                                    <td className="p" width="350"> 
                                        <span
                                        className="font-fff" style={{marginRight:'10px'}}>{centerInfo.invitationCode}</span>
                                        <CopyToClipboard onCopy={() => {
                                            optPop(() => {
                                            }, formatMessage({id: '复制成功'}), {timer: 1500})
                                        }} text={ `${centerInfo.invitationCode ? centerInfo.invitationCode : '--'}` }>
                                            <span className="money-btn">{formatMessage({id: "复制"})}</span>
                                        </CopyToClipboard>
                                    </td>
                                    <td width="235"><FormattedMessage id="邀请链接:" /></td>
                                    <td className="p">
                                        <span className="font-fff" style={{marginRight: '20px'}}>{centerInfo.invitationLinks}</span>
                                        <CopyToClipboard onCopy={() => {
                                        optPop(() => {
                                        }, formatMessage({id: '复制成功'}), {timer: 1500})
                                    }} text={ centerInfo.invitationLinks }>
                                        <span className="money-btn">{formatMessage({id: "复制"})}</span>
                                    </CopyToClipboard>
                                    </td>
                                </tr>
                                {
                                    false &&
                                    <tr>
                                        <td width="235"><FormattedMessage id="回本激活:" /></td>
                                        <td className="p" width="350"><span>{centerInfo.directInvitationSucNum}/3</span></td>
                                        <td colSpan="2"></td>
                                    </tr>
                                }
                                
                                
                            </tbody>
                        </table>
                        </React.Fragment>
                        }
                        {
                        1 === +selectedCode
                        &&
                        <React.Fragment>
                        <table className="my_c_gwp">
                            <tbody>
                                <tr>
                                    <td width="235"><Link className="my_txt_cz" to="/bw/manage/account/fbchargeDownHistory?type=2&fl=1">{formatMessage({id: "投资基数:"})}</Link></td>
                                    <td className="p" width="350"><Link className="my_txt_cz font-fff" to="/bw/manage/account/fbchargeDownHistory?type=2&fl=1">{centerInfo.investUsdtAmount} USDT</Link></td>
                                    <td width="235">{formatMessage({id: "理论收益:"})}</td>
                                    <td className="p">{centerInfo.expectProfitUsdt} USDT</td>
                                </tr>

                                <tr>
                                    <td width="235">{formatMessage({id: "我的权重:"})}</td>
                                    <td className="p" width="350">{centerInfo.superNodeWeight}</td>
                                    <td width="235"><Link className="my_txt_cz" to=""><FormattedMessage id="完成额度(5%):" values={{p:(+new BigNumber(centerInfo.staticProfitSumUsdt ? centerInfo.staticProfitSumUsdt : 0).div(centerInfo.expectProfitUsdt > 0 ? centerInfo.expectProfitUsdt : 1).times(100).toFixed(3)).toFixed(2) }} /></Link></td>
                                    <td className="p"><Link className="my_txt_cz font-fff" to="">{centerInfo.staticProfitSumUsdt} USDT</Link></td>
                                </tr>

                                <tr>
                                    <td width="235">{formatMessage({id: "可提额度:"})}</td>
                                    <td className="p" width="350"><span style={{marginRight: '10px'}}>{centerInfo.userFinUsdtAmount} USDT</span> <span className="money-btn" onClick={this.openDialogIn}>{formatMessage({id: "提取"})}</span></td>
                                    <td width="235"><Link className="my_txt_cz" to=""><FormattedMessage id="预备复投资金:" values={{p:5}} /></Link></td>
                                    <td className="p"><Link className="my_txt_cz" to=""><span className="plv"><span className="my_vds_wp">{centerInfo.douProfitAmount}</span><em className="lock"></em></span></Link></td>
                                </tr>

                            </tbody>
                        </table>
                        </React.Fragment>
                        }
                        {
                        2 === +selectedCode
                        &&
                        <React.Fragment>
                            <table className="my_c_gwp">
                                <tbody>
                                    <tr>
                                        <td width="235"><Link className="my_txt_cz" to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=1"><FormattedMessage id="建点奖励(1层级):" values={{p:centerInfo.hierarchyBuildFloor}} /></Link></td>
                                        <td className="p" width="350">
                                        <Link className="my_txt_cz" to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=1">
                                            <span className="my_vds_wp">
                                                {
                                                    centerInfo.hierarchyBuildAmount
                                                }
                                            </span>
                                            <span className="my_pth_wp">
                                                 (
                                                 {formatMessage({id:'冻结'})}
                                                 <span className="my_vds_wp jsb plv">
                                                     {centerInfo.hierarchyBuildInsureAmount}
                                                     <i className="my_pi_alt">{formatMessage({id: "mnx"})}</i>
                                                 </span>
                                                 )
                                            </span>
                                        </Link>
                                        </td>
                                        <td width="235"><Link className="my_txt_cz" to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=2"><FormattedMessage id="指导奖励(1级):" values={{p: centerInfo.pushGuidanceRatio + '%'}} /></Link></td>
                                        <td className="p">
                                            <Link className="my_txt_cz  font-fff" to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=2">
                                                <span className="my_vds_wp">{centerInfo.pushGuidanceAmount}</span>
                                                <span className="my_pth_wp">
                                                    (
                                                        {formatMessage({id:'冻结'})}
                                                        <span className="my_vds_wp jsb plv">
                                                            {centerInfo.pushGuidanceInsureAmount}
                                                            <i className="my_pi_alt">{formatMessage({id: "mnx"})}</i>
                                                        </span>
                                                    )
                                            </span>
                                            </Link>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td width="235"><Link className="my_txt_cz" to=""><FormattedMessage id="全球领袖分红池:" /></Link></td>
                                        <td width="350">
                                        <Link className="my_txt_cz" to="">
                                        <span className="my_vds_wp">
                                                {
                                                    centerInfo.platLeaderBonusAmount
                                                }
                                            </span>
                                            </Link>
                                        </td>
                                        <td width="235"><FormattedMessage id="全球领袖分红奖(3%):" values={{p: centerInfo.leaderBonusRatio}} /></td>
                                        <td>
                                        <span className="my_vds_wp">
                                               {
                                                   centerInfo.leaderBonusAmount
                                               }
                                            </span>
                                            <span className="my_pth_wp">
                                                 (
                                                 {formatMessage({id:'冻结'})}
                                                 <span className="my_vds_wp jsb plv">
                                                    {centerInfo.leaderBonusInsureAmount}
                                                    <i className="my_pi_alt">{formatMessage({id: "mnx"})}</i>
                                                 </span>
                                                 )
                                            </span>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td width="235"><Link className="my_txt_cz" to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=3"><FormattedMessage id="晋升奖励(10%):" values={{p:centerInfo.levelPromotionRatio}} /></Link></td>
                                        <td className="p" width="350">
                                        <Link className="my_txt_cz" to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=3">
                                            <span className="my_vds_wp">
                                                {
                                                    centerInfo.levelPromotionAmount
                                                }
                                            </span>
                                            <span className="my_pth_wp">
                                                 (
                                                 {formatMessage({id:'冻结'})}
                                                 <span className="my_vds_wp jsb plv">
                                                    {centerInfo.levelPromotionInsureAmount}
                                                    <i className="my_pi_alt">{formatMessage({id: "mnx"})}</i>
                                                 </span>
                                                 )
                                            </span>
                                        </Link>
                                        </td>
                                        <td width="235"><FormattedMessage id="可释放:"  /></td>
                                        <td className="p"><span className="my_vds_wp" style={{marginRight:'10px'}}>{centerInfo.avaTransferAmount}</span>
                                            <span className={`money-btn ${!(centerInfo.avaTransferAmount > 0) && 'dis'}`} onClick={centerInfo.avaTransferAmount > 0 && this.confirmProxy}>
                                                {formatMessage({id: "释放"})}
                                            </span>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </React.Fragment>
                        }
                        {
                            false
                            &&
                            <ul className="money-ul fl-lt">
                                
                                <li><p><span>{formatMessage({id: "直属上级"})}</span><span
                                    className="font-fff">{centerInfo.physicsSupName}</span></p></li>

                                <li><p><span>{formatMessage({id: "成功邀请人数"})}</span><span
                                    className="font-fff">{centerInfo.directInvitationSucNum}</span></p></li>

                                <li>
                                    {
                                    false
                                    &&
                                    <p><span>{formatMessage({id: "投资数量:"})}</span><span
                                    className="font-fff">{centerInfo.investAmount} Vollar</span></p>
                                    }
                                    <Link to="/bw/manage/account/fbchargeDownHistory?type=2&fl=1" style={{color:'#9199AF'}}><p><span>{formatMessage({id: "投资数量:"})}</span><span
                                    className="font-fff" style={{color:'#09A5C5'}}>{centerInfo.investAmount} Vollar</span></p></Link>
                                
                                </li>


                                
                                <li><p><span>{formatMessage({id: "投资估值:"})}</span><span
                                    className="font-fff">{centerInfo.expectProfitUsdt} USDT</span></p></li>

                                <li>
                                    <p>
                                        <span>
                                            {formatMessage({id: "待划转："})}
                                        </span>
                                        <span className="font-fff" style={{marginRight:'10px'}}>
                                            {centerInfo.avaTransferAmount} Vollar
                                        </span>
                                        <span className="money-btn" onClick={this.confirmProxy}>
                                            {formatMessage({id: "释放"})}
                                        </span>
                                    </p>
                                </li>    


                                <li><p><span>{formatMessage({id: "复投资金："})}</span><span style={{position:'relative'}}
                                    className="font-fff">{centerInfo.douProfitAmount} vollar <em className="lock"></em></span></p></li>


                                <li>
                                    <p style={{display:'inline-block',marginRight:'10px'}}>
                                        <span>{formatMessage({id: "邀请链接:"})}</span>

                                        <span className="font-fff">{centerInfo.invitationLinks}</span>
                                    </p>
                                    <CopyToClipboard onCopy={() => {
                                        optPop(() => {
                                        }, formatMessage({id: '复制成功'}), {timer: 1500})
                                    }} text={ this.props.intl.formatMessage({id: 'VDS阿波罗投资理财计划'}, {p:centerInfo.invitationLinks,c:centerInfo.invitationCode}) }>
                                        <span className="money-btn">{formatMessage({id: "复制"})}</span>
                                    </CopyToClipboard>
                                </li>

                                

                                <li style={{height:'auto',paddingTop:'40px'}}><p><span>{formatMessage({id: "直推执导奖励:"})}({centerInfo.pushGuidanceRatio}%):</span><span
                                    className="font-fff">{centerInfo.pushGuidanceAmount} vollar</span></p></li>
                                
                                <li><p><span>{formatMessage({id: "全球领袖分红奖励:"})}</span><span
                                    className="">{formatMessage({id: "暂未开启"})}</span></p></li>
                                <li><p><span>{formatMessage({id: "全球领袖分红权重"})}:</span><span
                                    className="font-fff">{centerInfo.leaderBonusWeight}</span></p></li>
                                

                                
                                {
                                false
                                &&
                                <li><p><span>{formatMessage({id: "生态体系参与奖励:"})}</span><span
                                    className="font-fff">{centerInfo.ecologySystemAmount} vollar</span></p></li>
                                }
                                <li><p><span>{formatMessage({id: "生态体系参与奖励:"})}</span><span
                                    className="">{formatMessage({id: "暂未开启"})}</span></p></li>
                                {   
                                false
                                &&
                                <li><p><span>{formatMessage({id: "分红权重"})}:</span><span
                                    className="">{formatMessage({id: "暂未开启"})}</span></p></li>
                                }
                                {
                                false
                                &&
                                <li><p>
                                    <span>{formatMessage({id: "累积分红:"})}</span><span>{formatMessage({id: "暂未开启"})}</span>
                                </p></li>
                                }

                            </ul>
                        }
                        </div>
                        
                        {/* 收益详情 */}
                        <div style={{display:'none'}} className="clearboth money-table">
                            <h2>{formatMessage({id: "收益详情"})}</h2>
                            <div className="money-table-content">
                                <table width="100%" className="norm-table table-account">
                                    <thead id="tableSort">
                                    <tr id="money-tr">
                                        <th>{formatMessage({id: "时间"})}</th>
                                        <th>{formatMessage({id: "类型"})}</th>
                                        <th>{formatMessage({id: "币种"})}</th>
                                        <th>{formatMessage({id: "数量"})}</th>
                                        <th>{formatMessage({id: "备注"})}</th>
                                    </tr>
                                    </thead>
                                    <tbody id="fundsDetail" className="bgtbody">
                                    {
                                        !!detailsList && detailsList.length > 0 ?
                                            detailsList.map((item, index) => {
                                                return (
                                                    <tr key={index}
                                                        className="border_left border_right border_bottom">
                                                        <td className="text-left">{formatDate(+(item.createTime))}</td>
                                                        <td className="text-left">{item.typeName}</td>
                                                        <td className="text-left">Vollar</td>
                                                        <td className="text-left">{item.amount}</td>
                                                        <td className="text-left">{item.remark}</td>
                                                    </tr>
                                                )
                                            }) : null

                                    }

                                    </tbody>
                                </table>
                                {/* 无记录提示 */}
                                {
                                    !!detailsList && detailsList.length < 1 ?
                                        <div className="alert_under_table"><i
                                            className="iconfont icon-tongchang-tishi norecord"></i>{formatMessage({id: "No.record"})}
                                        </div>
                                        : null
                                }
                            </div>
                        </div>
                    </div>
                </div>
                {/*分页*/}
                {/*<div className="tablist">*/}
                {/*    <Pages*/}
                {/*        {...this.test}*/}
                {/*        currentPageClick={this.currentPageClick}*/}
                {/*    />*/}
                {/*</div>*/}
                {/* 对话框 */}
                <EntrustModal ref={modal => this.modal = modal}>
                    {this.state.dialog}
                </EntrustModal>

                <ReactModal ref={modal => this.modaltos = modal}>
                    {this.state.dialogtos}
                </ReactModal>
                {
                    modelMoney&&

                    <ModelMoney getMoneyModel={this.getMoneyModel} count={100} mum={1.5}/> //count：钱数，num : 倍数

                }                
                {
                issw
                &&
                <div className="confirm_sy1">
                    <Confirm msg={<FormattedMessage id="mcpy" values={{n:1,m:12}} />} ok={formatMessage({id: "查看排名"})} isNotCancel={true} cb={this.cb} />
                </div>
                }
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        language: state.language,
        baseUserInfo: state.session.baseUserInfo
    }
};
const mapDispatchToProps = {
    getUserBaseInfo
};


export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(CMoney));