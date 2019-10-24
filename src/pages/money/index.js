import React from 'react';
import '../../assets/css/money.less';
import { FormattedMessage, injectIntl } from 'react-intl';
import EntrustModal from '../../components/entrustBox';
import Dialog from "./dialog"
import td1 from '../../assets/img/money/td1.png'
import td2 from '../../assets/img/money/td2.png'
import td3 from '../../assets/img/money/td3.png'
import '../manage/balances/balances.less'
import {init,userFinancialInfo,productSuperNode} from './index.model';
import {connect} from 'react-redux';
import cookie from 'js-cookie'
import Transfer from '../manage/transfer';
import { Link } from 'react-router';
import ReactSwipe from 'react-swipe';

import ReactModal from '../../components/popBox';
import ScrollArea from 'react-scrollbar'

const Mz = (props) => {
    return(
        <div className="modal trade-notice-modal mz_by">
            <div className="modal-body mmbs">
                <div className="notice-dialog-close mz_ms">
                    <i onClick={()=>props.modals.closeModal()} className="iconfont icon-guanbi-yiru"></i>
                </div>
                <h4><FormattedMessage id="免责说明-tith" /></h4>
                <ScrollArea className="trade-scrollarea" style={{width:'100%'}}>
                    <div className="mz_wp" >
                        <h5><FormattedMessage id="免责说明" /></h5>
                        <div className="my_indent" dangerouslySetInnerHTML={{__html: props.intl.formatMessage({id:"mz"})}}>
                        
                        </div>
                    </div>
                </ScrollArea>
            </div>
        </div>
    )
}

@connect(
    state=>({user: state.session.user})
)
class Money extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            butStatus: 0, // 0 去登录 1 弹窗 2 去支付中心
            swipeClassName: 0,
            ablFlg: 0,
            ablStepFlg: 0,
        };
        this.openDialog = this.openDialog.bind(this)
        this.openDialogIn = this.openDialogIn.bind(this)
        // 基础信息
        this.baseInfo = {};

        // 点击购入或者已购入跳转或者弹窗
        this.jmp = this.jmp.bind(this);

        this.showDig = this.showDig.bind(this);

        this.swipeJump = this.swipeJump.bind(this);

        // 是否首次打开dialog
        this.notFstDialog = false;

        // 弹窗防抖动指针
        this.dialogTimer = null;

        // 同步消息轮训
        this.hdTmer = null;

        // 轮播
        this.reactSwipeEl = null;

        // 下一个
        this.swNext = this.swNext.bind(this);
        // 上一个
        this.swPrev = this.swPrev.bind(this);

        this.mz = this.mz.bind(this);

        // 遮罩关闭
        this.cloaseAblPop = this.cloaseAblPop.bind(this);
    }

    cloaseAblPop(){
        this.setState({
            ablFlg: 0,
        })
    }

    swNext(){
        this.reactSwipeEl.next();
        //console.log(this.reactSwipeEl.getPos());
        this.setState({
            swipeClassName: this.reactSwipeEl.getPos(),
        })
    }

    swPrev(){
        this.reactSwipeEl.prev();
        //console.log(this.reactSwipeEl.getPos());
        this.setState({
            swipeClassName: this.reactSwipeEl.getPos(),
        })
    }

    mz(){
        this.modal.closeModal();
        this.setState({
            modalHTML: <Mz  intl={this.props.intl} modals={this.modals} />
        })
        this.modals.openModal();
    }

    jmp(e, key = 0){
        // console.log(e.target.getAttribute("keyName"), '=====>')
        const data = this.state.butStatus;
        let url = '/bw/login';

        if(2 === data && !key){
            url = '/bw/manage/account/cmoney';
        } else if(0 !== data) {
            url = '';
        }

        if(url){
            // 跳转
            this.props.router.push(url);
        } else {
            // 弹窗
            this.openDialog(this.baseInfo.authPayFlag, key);
        }

    }

    componentDidMount(){
        //this.mz();
        // header 基本信息伦旭获取
        this.hdTmer = setInterval(()=>{
            productSuperNode().then(res => {
                this.baseInfo = Object.assign({}, this.baseInfo, res);
                this.forceUpdate();
            })
        },1000*30);
        //this.openDialog()
        init().then((res)=>{
            this.baseInfo = res;

            // 中间按钮的点击去向
            const IsLoginUser = this.props.user && cookie.get("zloginStatus")!=4;
            const { authPayFlag } = res;

            if(IsLoginUser){
                if(2 === +authPayFlag){
                    this.setState({
                        butStatus: 2,
                    })
                }else{
                    this.setState({
                        butStatus: 1
                    })
                }
            } else{
                this.forceUpdate();
            }
        });

        // 等边六边形初始标注点 -- 等接口返回
        this.swipeJump(5, 1);
    }

    componentWillMount(){
        clearInterval(this.hdTmer);
    }

    openDialog(state = 0, step = 0){
        
        state = +state;
        clearTimeout(this.dialogTimer);
        this.dialogTimer = setTimeout(()=>{
            // 重新获取是否保存过 状态
            // 以及表单的基础信息
            if(true || !state && this.notFstDialog){
                // 如果没保存过在打开弹窗前从新验证下是否保存过。
                userFinancialInfo().then((res)=>{
                    this.showDig(res, res.authPayFlag, step);
                });
            }else{
                // console.log(this.baseInfo, state);
                this.showDig(this.baseInfo, state);
            }

            this.notFstDialog = true;
        },100)
    }
    swipeJump(flg, timer){
        this.reactSwipeEl.slide(flg, timer);
        this.setState({
            swipeClassName: flg,
        })
    }
    showDig(obj = {}, state, step = 0){
        this.modal.closeModal();
        this.setState({
            dialog: <Dialog step={step} transferInfo={obj} states={state} transfer={this.openDialogIn} closeModal={this.modal.closeModal} mz={this.mz} />
        })
        this.modal.openModal();
    }

    // 资金划转内部弹窗用 - open
    openDialogIn(){
        this.modal.closeModal();
        this.setState({ dialog: <Transfer closeModal={this.modal.closeModal} fromtype={5} totype={1} fundsType='VDS' /> });
        this.modal.openModal();
    }

    render(){

        let {butStatus, diaShow, swipeClassName, ablFlg, ablStepFlg, } = this.state
        let {jmp, cloaseAblPop} = this;
        const {proTotalAmount, proTotalUser, profitWeight, sNodeNum, sNodeTotalProfit, currentBlock, profitBlock, authPayFlag, profitWeightTotal, surplusHour, bonusSurplusTime, vdsEcologyBack, matrixLevel, } = this.baseInfo
        // console.log(matrixLevel, '===>>');
        // console.log(authPayFlag, '====>>>');
        return (
            <div className={`money_wp_out ${diaShow && 'isShow'}`}>
                <div className="money_wp">
                    <div className="mc">
                        {/*  nav */}
                        <ul className="mnav">
                            <li className="mi0">
                                <div className="lt">
                                    <span className="icon icon4"></span>
                                </div>
                                <div className="rt">
                                    <p>
                                        <span className="mpic">{sNodeNum}</span>
                                    </p>
                                    <p className="msub">
                                        <Link to="/bw/smn">
                                            <FormattedMessage id="已运行超级主节点数量" />
                                        </Link>
                                    </p>
                                </div>
                            </li>

                            <li className="mi0">
                                <div className="lt">
                                    <span className="icon icon5"></span>
                                </div>
                                <div className="rt">
                                    <p>
                                        <span className="mpic king">{sNodeTotalProfit}</span>
                                    </p>
                                    <p className="msub">
                                        <FormattedMessage id="累计产出" />
                                    </p>
                                </div>
                            </li>

                            <li className="mi0">
                                <div className="lt">
                                    <span className="icon icon3"></span>
                                </div>
                                <div className="rt">
                                    <p>
                                    <span className="mpic">{profitWeightTotal}</span>
                                    </p>
                                    <p className="msub">
                                    <FormattedMessage id="累计分红权重" />
                                    </p>
                                </div>
                            </li>

                            {
                            false
                            &&
                            <li className="mi0">
                                <div className="lt">
                                    <span className="icon icon1"></span>
                                </div>
                                <div className="rt">
                                    <p>
                                        <span className="mpic">{proTotalAmount}</span>
                                        <span className="mutil"></span>
                                    </p>
                                    <p className="msub">
                                        <FormattedMessage id="预计1权重分红: (USDT)" />
                                    </p>
                                </div>
                            </li>
                            }
                            <li className="mi0">
                                <div className="lt">
                                    <span className="icon icon6"></span>
                                </div>
                                <div className="rt">
                                    <p>
                                        <span className="mpic"><FormattedMessage id="约" />{bonusSurplusTime}</span>
                                    </p>
                                    <p className="msub">
                                        <FormattedMessage id="分红倒计时" />
                                    </p>
                                </div>
                            </li>

                            <li className="mi0">
                                <div className="lt">
                                    <span className="icon icon2"></span>
                                </div>
                                <div className="rt">
                                    <p>
                                        <span className="mpic king">{vdsEcologyBack}</span>
                                    </p>
                                    <p className="msub">
                                        <FormattedMessage id="VDS生态回馈" />
                                    </p>
                                </div>
                            </li>
                        </ul>
                        {/* content */}
                        <div className="mgd_wp">
                        <p className="l" onClick={this.swPrev}></p>
                        <ReactSwipe
                            className="carousel"
                            swipeOptions={{ continuous: false }}
                            ref={el => (this.reactSwipeEl = el)}
                        >
                            
                        <div className="in">
                            <div className="money_content">
                                <div className="sx_wp">
                                    <h3>2 Vollar</h3>
                                    <ul style={{width:this.props.intl.locale === 'en' ? '340px':'220px',margin:'0 auto',textAlign:'left'}} className="sx_wp_ul">
                                        <li style={{display:'none'}}><i className="money-icon"></i><FormattedMessage id="动态收益" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权100" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权" values={{q:1}} /></li>
                                        <li style={{whiteSpace:'nowrap'}}><i className="money-icon"></i><FormattedMessage id="价格达到" values={{q:360}} /></li>
                                    </ul>
                                    {
                                        false
                                        ?
                                       ( authPayFlag !== void 0 ? ( 2 !== butStatus ?  <p className="sx_wp_btn" keyName="1" onClick={(e)=>{jmp(e, 1)}}><FormattedMessage id="购入" /></p> : <p className="sx_wp_btn" onClick={jmp}><FormattedMessage id="已购入" /></p>) : (<div style={{height:'104px'}}></div>))
                                       :
                                       <p className="sx_wp_btn ds"><FormattedMessage id="暂未开启" /></p>
                                    }
                                </div>
                            </div>
                        </div>
                        <div className="in">
                            <div className="money_content">
                                <div className="sx_wp">
                                    <h3>8 Vollar</h3>
                                    <ul style={{width:this.props.intl.locale === 'en' ? '340px':'220px',margin:'0 auto',textAlign:'left'}} className="sx_wp_ul">
                                        <li style={{display:'none'}}><i className="money-icon"></i><FormattedMessage id="动态收益" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权100" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权" values={{q:4.1}} /></li>
                                        <li style={{whiteSpace:'nowrap'}}><i className="money-icon"></i><FormattedMessage id="价格达到" values={{q:180}} /></li>
                                    </ul>
                                    {
                                        false
                                        ?(
                                        authPayFlag !== void 0 ? ( (2 !== butStatus || (2 === butStatus && 2 > matrixLevel)) ?  <p keyName="2" className="sx_wp_btn" onClick={(e)=>{jmp(e, 2)}}><FormattedMessage id="购入" /></p> : <p className="sx_wp_btn" onClick={jmp}><FormattedMessage id="已购入" /></p>) : (<div style={{height:'104px'}}></div>))
                                        :
                                        <p className="sx_wp_btn ds"><FormattedMessage id="暂未开启" /></p>
                                    }
                                </div>
                            </div>
                        </div>
                        <div className="in">
                            <div className="money_content">
                                <div className="sx_wp">
                                    <h3>18 Vollar</h3>
                                    <ul style={{width:this.props.intl.locale === 'en' ? '340px':'220px',margin:'0 auto',textAlign:'left'}} className="sx_wp_ul">
                                        <li style={{display:'none'}}><i className="money-icon"></i><FormattedMessage id="动态收益" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权100" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权" values={{q:9.3}} /></li>
                                        <li style={{whiteSpace:'nowrap'}}><i className="money-icon"></i><FormattedMessage id="价格达到" values={{q:90}} /></li>
                                    </ul>
                                    {
                                        false
                                        ?
                                        (
                                        authPayFlag !== void 0 ? ( (2 !== butStatus || (2 === butStatus && 3 > matrixLevel)) ?  <p keyName="3" className="sx_wp_btn" onClick={(e)=>{jmp(e, 3)}}><FormattedMessage id="购入" /></p> : <p className="sx_wp_btn" onClick={jmp}><FormattedMessage id="已购入" /></p>) : (<div style={{height:'104px'}}></div>))
                                        :
                                        <p className="sx_wp_btn ds"><FormattedMessage id="暂未开启" /></p>
                                    }
                                </div>
                            </div>
                        </div>
                        <div className="in">
                            <div className="money_content">
                                <div className="sx_wp">
                                    <h3>38 Vollar</h3>
                                    <ul style={{width:this.props.intl.locale === 'en' ? '340px':'220px',margin:'0 auto',textAlign:'left'}} className="sx_wp_ul">
                                        <li style={{display:'none'}}><i className="money-icon"></i><FormattedMessage id="动态收益" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权100" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权" values={{q:19.8}} /></li>
                                        <li style={{whiteSpace:'nowrap'}}><i className="money-icon"></i><FormattedMessage id="价格达到" values={{q:40}} /></li>
                                    </ul>
                                    {
                                        false
                                        ?(
                                        authPayFlag !== void 0 ? ( (2 !== butStatus || (2 === butStatus && 4 > matrixLevel)) ?  <p keyName="4" className="sx_wp_btn" onClick={(e)=>{jmp(e, 4)}}><FormattedMessage id="购入" /></p> : <p className="sx_wp_btn" onClick={jmp}><FormattedMessage id="已购入" /></p>) : (<div style={{height:'104px'}}></div>))
                                        :
                                        <p className="sx_wp_btn ds"><FormattedMessage id="暂未开启" /></p>
                                    }
                                </div>
                            </div>
                        </div>
                        <div className="in">
                            <div className="money_content">
                                <div className="sx_wp">
                                    <h3>88 Vollar</h3>
                                    <ul style={{width:this.props.intl.locale === 'en' ? '340px':'220px',margin:'0 auto',textAlign:'left'}} className="sx_wp_ul">
                                        <li style={{display:'none'}}><i className="money-icon"></i><FormattedMessage id="动态收益" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权100" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权" values={{q:46}} /></li>
                                        <li style={{whiteSpace:'nowrap'}}><i className="money-icon"></i><FormattedMessage id="价格达到" values={{q:20}} /></li>
                                    </ul>
                                    {
                                        false
                                        ?(
                                        authPayFlag !== void 0 ? ( (2 !== butStatus || (2 === butStatus && 5 > matrixLevel)) ?  <p keyName="5" className="sx_wp_btn" onClick={(e)=>{jmp(e, 5)}}><FormattedMessage id="购入" /></p> : <p className="sx_wp_btn" onClick={jmp}><FormattedMessage id="已购入" /></p>) : (<div style={{height:'104px'}}></div>))
                                        :
                                        <p className="sx_wp_btn ds"><FormattedMessage id="暂未开启" /></p>
                                    }
                                </div>
                            </div>
                        </div>
                        <div className="in">
                            <div className="money_content">
                                
                                <div className="sx_wp">
                                    {/* <h2><FormattedMessage id="投资数量" /></h2> */}
                                    <h3>188 Vollar</h3>
                                    <ul style={{width:this.props.intl.locale === 'en' ? '290px':'210px',margin:'0 auto',textAlign:'left'}} className="sx_wp_ul">
                                        <li style={{display:'none'}}><i className="money-icon"></i><FormattedMessage id="本金1.5倍收益" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="动态收益" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权100" /></li>
                                        <li><i className="money-icon"></i><FormattedMessage id="VIP分红权" values={{q:100}} /></li>
                                        <li style={{display:'none'}}><i className="money-icon"></i><FormattedMessage id="240小时改为：限时返利：剩余xx小时，xx总数216h,随着时间变化而减少" values={{h:surplusHour}} /></li>
                                        <li style={{display:'none'}}><i className="money-icon"></i><FormattedMessage id="立返" values={{t:profitWeight+'%'}} /> </li>
                                    </ul>
                                    {
                                    authPayFlag !== void 0 ? ( (2 !== butStatus || (2 === butStatus && 6 > matrixLevel)) ?  <p keyName="6" className="sx_wp_btn" onClick={(e)=>{jmp(e, 6)}}><FormattedMessage id={3 === +authPayFlag ? "复投" : "购入"} /></p> : <p className="sx_wp_btn" onClick={jmp}><FormattedMessage id="已购入" /></p>) : (<div style={{height:'104px'}}></div>)
                                    }
                                    
                                </div>
                                
                            </div>
                        </div>
                        
                        
                    </ReactSwipe>
                    {
                    ablFlg
                    ?
                    <div className="myjl_wp">
                        <div className="shadow" onClick={this.cloaseAblPop}></div>
                        {/*理财奖励弹出*/}
                        <div className="in">
                            <div className="close" onClick={this.cloaseAblPop}></div>
                            <div className="money_content">
                                <div className="sx_wp">
                                    <h3 className="tith"><FormattedMessage id="阿波罗计划" /></h3>
                                    <p className="p0"><FormattedMessage id="新增VIP点位加成奖励" /></p>
                                    {
                                        0 === ablStepFlg?
                                        <React.Fragment>
                                            <p className="p1"><FormattedMessage id="恭喜您获得" /></p>
                                            <p className="p2"><span className="my_vds_wp jc">0.56</span></p>
                                            <p className="p1"><FormattedMessage id="提前回收" /></p>
                                            <p className="p2">0.56 USDT</p>
                                            <p className="p3"><FormattedMessage id="奖励已提现至理财中心" /></p>
                                            <Link to="/bw/manage/account/fbchargeDownHistory?type=2&fl=2&reward=7" className="sx_wp_btn"><FormattedMessage id="查看" /></Link>
                                        </React.Fragment>
                                        :
                                        <React.Fragment>
                                            <p className="tb"></p>
                                            <Link className="sx_wp_btn"><FormattedMessage id="打开" /></Link>
                                        </React.Fragment>
                                    }
                                </div>
                            </div>
                        </div>

                    </div>
                    :null
                    }
                    <p className="six-p"><span className={`six ${swipeClassName === 0 ? 'six-1' : 'six-2'}`} onClick={()=>this.swipeJump(0)}></span><span className={`six ${swipeClassName === 1 ? 'six-1' : 'six-2'}`} onClick={()=>this.swipeJump(1)}></span><span className={`six ${swipeClassName === 2 ? 'six-1' : 'six-2'}`} onClick={()=>this.swipeJump(2)}></span><span className={`six ${swipeClassName === 3 ? 'six-1' : 'six-2'}`} onClick={()=>this.swipeJump(3)}></span><span className={`six ${swipeClassName === 4 ? 'six-1' : 'six-2'}`} onClick={()=>this.swipeJump(4)}></span><span className={`six ${swipeClassName === 5 ? 'six-1' : 'six-2'}`} onClick={()=>this.swipeJump(5)}></span></p>
                    <p className="r" onClick={this.swNext}></p>
                    </div>

                    <div className="money-footer_wp">
                        <div className="money-footer">
                            <ul>
                                <li className="footer-li">
                                    <div className="footer-li-img">
                                        <img src={td1} alt="" />
                                    </div>
                                    <div className="footer-li-cont">
                                        <h3><FormattedMessage id="安全透明" /></h3>
                                        <p><FormattedMessage id="安全透明内容" /></p>
                                    </div>
                                </li>
                                <li className="footer-li">
                                    <div className="footer-li-img">
                                        <img src={td2} alt="" />
                                    </div>
                                    <div className="footer-li-cont">
                                        <h3><FormattedMessage id="稳健收益" /></h3>
                                        <p><FormattedMessage id="稳健收益内容" /></p>
                                    </div>
                                </li>
                                <li className="footer-li">
                                    <div className="footer-li-img">
                                        <img src={td3} alt="" />
                                    </div>
                                    <div className="footer-li-cont">
                                        <h3><FormattedMessage id="合作共赢" /></h3>
                                        <p><FormattedMessage id="合作共赢内容" /></p>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                </div>
                {/* 对话框 */}
                <EntrustModal ref={modal => this.modal = modal}>
                    {this.state.dialog}
                </EntrustModal>

                <ReactModal ref={modal => this.modals = modal}>
                        {this.state.modalHTML}
                </ReactModal>
            </div>
        )
    }
}



export default injectIntl(Money);