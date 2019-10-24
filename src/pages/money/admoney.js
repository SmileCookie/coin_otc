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

@connect(
    state=>({user: state.session.user})
)
class Money extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            butStatus: 0, // 0 去登录 1 弹窗 2 去支付中心
        };
        this.openDialog = this.openDialog.bind(this)
        this.openDialogIn = this.openDialogIn.bind(this)
        // 基础信息
        this.baseInfo = {};

        // 点击购入或者已购入跳转或者弹窗
        this.jmp = this.jmp.bind(this);

        this.showDig = this.showDig.bind(this);

        // 是否首次打开dialog
        this.notFstDialog = false;

        // 弹窗防抖动指针
        this.dialogTimer = null;

        // 同步消息轮训
        this.hdTmer = null;
        
    }

    jmp(e){
        const data = this.state.butStatus;
        let url = '/bw/login';

        if(2 === data){
            url = '/bw/manage/account/cmoney';
        } else if(0 !== data) {
            url = '';
        }

        if(url){
            // 跳转
            this.props.router.push(url);
        } else {
            // 弹窗
            this.openDialog(this.baseInfo.authPayFlag);
        }

    }

    componentDidMount(){

        // header 基本信息伦旭获取
        this.hdTmer = setInterval(()=>{
            productSuperNode().then(res => {
                this.baseInfo = Object.assign({}, this.baseInfo, res);
                this.forceUpdate();
            })
        },1000*30);
        productSuperNode().then(res => {
            this.baseInfo = Object.assign({}, this.baseInfo, res);
            this.forceUpdate();
        })
        //this.openDialog()
        // init().then((res)=>{
        //     this.baseInfo = res;

        //     // 中间按钮的点击去向
        //     const IsLoginUser = this.props.user && cookie.get("zloginStatus")!=4;
        //     const { authPayFlag } = res;

        //     if(IsLoginUser){
        //         if(2 === +authPayFlag){
        //             this.setState({
        //                 butStatus: 2,
        //             })
        //         }else{
        //             this.setState({
        //                 butStatus: 1
        //             })
        //         }
        //     } else{
        //         this.forceUpdate();
        //     }
        // });
    }

    componentWillMount(){
        clearInterval(this.hdTmer);
    }

    openDialog(state = 0){
        state = +state;
        clearTimeout(this.dialogTimer);
        this.dialogTimer = setTimeout(()=>{
            // 重新获取是否保存过 状态
            // 以及表单的基础信息
            if(true || !state && this.notFstDialog){
                // 如果没保存过在打开弹窗前从新验证下是否保存过。
                userFinancialInfo().then((res)=>{
                    this.showDig(res, res.authPayFlag);
                });
            }else{
                // console.log(this.baseInfo, state);
                this.showDig(this.baseInfo, state);
            }

            this.notFstDialog = true;
        },100)
    }
    showDig(obj = {}, state){
        this.modal.closeModal();
        this.setState({
            dialog: <Dialog transferInfo={obj} states={state} transfer={this.openDialogIn} closeModal={this.modal.closeModal} />
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

        let {butStatus, diaShow} = this.state
        let {jmp} = this;
        const {proTotalAmount, proTotalUser, profitWeight, sNodeNum, sNodeTotalProfit, currentBlock, profitBlock, authPayFlag, profitWeightTotal, surplusHour, } = this.baseInfo
        return (
            <div className={`money_wp_out ${diaShow && 'isShow'}`}>
                <div className="money_wp">
                    <div className="mc">
                        {/*  nav */}
                        <ul className="mnav">
                            <li className="mi0">
                                <div className="lt">
                                    <span className="icon icon1"></span>
                                </div>
                                <div className="rt">
                                    <p>
                                        <span className="mpic">0</span>
                                        <span className="mutil">vollar</span>
                                    </p>
                                    <p className="msub">
                                        <FormattedMessage id="投资总额" />
                                    </p>
                                </div>
                            </li>
                            <li className="mi0">
                                <div className="lt">
                                    <span className="icon icon2"></span>
                                </div>
                                <div className="rt">
                                    <p>
                                        <span className="mpic">0</span>
                                    </p>
                                    <p className="msub">
                                        <FormattedMessage id="投资人数" />
                                    </p>
                                </div>
                            </li>
                            <li className="mi0">
                                <div className="lt">
                                    <span className="icon icon3"></span>
                                </div>
                                <div className="rt">
                                    <p>
                                    <span className="mpic">0</span>
                                    </p>
                                    <p className="msub">
                                    <FormattedMessage id="总分红权重" />
                                    </p>
                                </div>
                            </li>
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
                                            <FormattedMessage id="超级主节点数量" />
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
                                        <span className="mpic">0</span>
                                    </p>
                                    <p className="msub">
                                        <FormattedMessage id="产出总量" />
                                    </p>
                                </div>
                            </li>
                            <li className="mi0">
                                <div className="lt">
                                    <span className="icon icon6"></span>
                                </div>
                                <div className="rt">
                                    <p>
                                        <span className="mpic">0/0</span>
                                    </p>
                                    <p className="msub">
                                        <FormattedMessage id="分红时间" />
                                    </p>
                                </div>
                            </li>
                        </ul>
                        {/* content */}
                        <div className="in">
                        <div className="money_content">
                            <p className="l"></p>
                            <div className="sx_wp">
                                {/* <h2><FormattedMessage id="投资数量" /></h2> */}
                                <h3>188 Vollar</h3>
                                <ul style={{width:this.props.intl.locale === 'en' ? '290px':'210px',margin:'0 auto',textAlign:'left'}} className="sx_wp_ul">
                                    <li><i className="money-icon"></i><FormattedMessage id="本金1.5倍收益" /></li>
                                    <li><i className="money-icon"></i><FormattedMessage id="动态收益" /></li>
                                    <li><i className="money-icon"></i><FormattedMessage id="VIP分红权100" /></li>
                                    <li><i className="money-icon"></i><FormattedMessage id="顶级分红权重" /></li>
                                    <li style={{display:'none'}}><i className="money-icon"></i><FormattedMessage id="240小时改为：限时返利：剩余xx小时，xx总数216h,随着时间变化而减少" values={{h:surplusHour}} /></li>
                                    <li style={{display:'none'}}><i className="money-icon"></i><FormattedMessage id="立返" values={{t:profitWeight+'%'}} /> </li>
                                </ul>
                                  <div style={{width:this.props.intl.locale === 'en' ? '290px':'260px',margin:'0 auto',textAlign:'left'}}>
                                    <div style={{marginBottom:"20px"}}><FormattedMessage id="ad1" /></div>
                                    <div style={{marginBottom:"20px"}}><FormattedMessage id="ad2" /></div>
                                    <div><FormattedMessage id="ad3" /></div>
                                  </div>
                                
                                <p style={{display:'none'}} className="six-p"><span className="six six-1"></span><span className="six six-2"></span><span className="six six-2"></span><span className="six six-2"></span></p>
                            </div>
                            <p className="r"></p>
                        </div>
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
            </div>
        )
    }
}

export default injectIntl(Money);