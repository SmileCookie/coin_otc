import React from 'react';
import { Link } from 'react-router-dom';
import cookie from 'js-cookie'
import {withRouter} from 'react-router'
import { connect } from 'react-redux'
import { FormattedMessage,injectIntl } from 'react-intl';
import DealStatus from '../../components/orderStatu'
import OrderUserinfor from '../../components/orderUserInfor'
import TalkRoom from '../../components/talkRoom.js'
import DealButton from '../../components/orderbutton'
import {ThemeFactory, Styles} from '../../components/transition';
import {post,get} from '../../net'
import moment from 'moment'
import Cookie from 'js-cookie'
import{DATA_TIEM_EN,DATA_TIME_FORMAT} from '../../conf'
import{getOrderDetail} from './model'
import {pageClick} from '../../redux/module/session'
import {isGetTalkHistoryEvent} from '../../redux/module/chart'
import '../../assets/style/order/index.less'


@connect(
    state => ({
        userInfor:state.session.userInfor.data,
        coinData:state.session.coinData,
        chatApi:state.chart.chatApi,
        emojiList:state.chart.emojiList,
        count:state.chart.count,
        text:state.chart.text,
        isConnet:state.chart.isConnet,
        isGetTalkHistory:state.chart.isGetTalkHistory,
        locale:state.language.locale,
        sysText:state.chart.sysText,
        countSys:state.chart.countSys,


    }),
   {
        pageClick,
        isGetTalkHistoryEvent
   }
)
class OrderDetail extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dealInforList:{ //暂时
                // id:1,//订单id
                // orderListInfor:{//订单详情列表
                //     orderMoney:'100CNY',
                //     orderNum:'0.200000BTC',
                //     price:'50,888CNY',
                //     charge:'0.00001BTC',
                //     realMoney:'0.200000BTC',
                //     time:'2018-02-12 12:00:00',
                //     marker:'我已经付款，支付宝号18599876654，金额8000.00'
                // },
                // //用户信息
                // userInfor:{
                //     callName:'中本聪',
                //     userImgUrl:'http://img2.imgtn.bdimg.com/it/u=1091628847,41930541&fm=214&gp=0.jpg',
                //     payMethod:[
                //             {   type:'bank',
                //                 name:'日本聪',
                //                 payName:'中国农牧银行',
                //                 payAdress:'山东省青岛市市北区支行',
                //                 payCode:'877452244233'
                //             },
                //             {   type:'alipay',
                //                 name:'日本聪',
                //                 alipayPayCode:'13176878013',
                //                 alipayPayImgUrl:'http://img2.imgtn.bdimg.com/it/u=1091628847,41930541&fm=214&gp=0.jpg'
                //             }
                //         ]

                // },
                // dealType:'buy',//buy买币订单&sell卖币订单
                // icon:'BTC',//币种
                // orderCode:2018545451154454,//订单编号
                // dealProcess:'doing',//订单完成状态      doing正在进行&&done已经完成
                // dealStatue:'hasOrder',//订单进程状态    doing:[hasOrder(已经下单或者正要下单),hasPay(待付款或者已经下单)]
                //                       //              done:[pass(交易完成),cancel(交易取消),error(异常订单),appeal(申述)]
                // appealStatue:'hasAppeal',//订单是否有申述   doneAppeal:订单已经完成申述 hasAppeal:申述中 noAccept:未受理
                // leaveTimes:'123'//交易剩余时间 && none:没有时间
            },
            loading:true,
            erroPage:false
            
        };
        this.getOrderDetailEvent = this.getOrderDetailEvent.bind(this)
        //this.myRef=React.createRef();

    }

    componentWillMount(){

    }
    componentWillMount(){

        //未登录跳转
         if(!this.props.userInfor){
             window.location.href = '/bw/login'
         }
         
    }
    componentDidMount() {

        this.getOrderDetailEvent()
       
    }

    componentWillReceiveProps(nextProps) {
        
    }

    //获取用户信息
    getOrderDetailEvent(needLoading = true){//是否需要刷新页面
        let {id} = this.props.match.params
        if(needLoading){
            this.setState({
                loading:true
            },() =>{
                this.props.pageClick(0); 
                getOrderDetail(id,this).then(res =>{
                    console.log(res);
                    this.setState({
                        dealInforList:Object.assign({},this.state.dealInforList,{
                            ...res
                        }),
                        loading:false
                    })
                })
            })
        }else{

            this.setState({
                loading:false
            },() =>{
                getOrderDetail(id,this).then(res =>{
                    console.log(res);
                    this.setState({
                        dealInforList:Object.assign({},this.state.dealInforList,{
                            ...res
                        })
                    })
                })
            })

        }


    }


    render() {
        const {formatMessage} = this.props.intl
        const {dealInforList,loading,erroPage} = this.state;
        const {orderListInfor} = this.state.dealInforList;
        const LAN = Cookie.get('zlan')
        return (
           <div className="OrderMain">
           {
               loading?
               ThemeFactory.getThemeInstance(Styles.ThemeB)
               : !erroPage ?
               <React.Fragment>
                    <div className="page_title"><FormattedMessage id="订单详情"/></div>
                    <div className="detail flex-r">
                        <div className="infor">
                            <div className="top clearfix">
                                <span className="first"><FormattedMessage id={dealInforList.dealType == 'buy'?'购买':'出售'}/>{dealInforList.icon}</span>
                                <span className="second"><FormattedMessage id="订单编号"/>{dealInforList.orderCode}</span>
                                <span className="third fr"><FormattedMessage id={dealInforList.statusName}/></span>
                            </div>
                            {/* 订单状态 */}
                            <DealStatus data={dealInforList}/>
                            <div className="userInforCon">
                              {/* 用户信息 */}
                            <OrderUserinfor dealInforList={dealInforList} cardName = {this.props.userInfor.cardName}  />
                            <div className="inforTable">
                                <h2><FormattedMessage id="交易信息"/></h2>
                                <table className="listTable">
                                    <thead>
                                        <tr className="coFont2">
                                            <th><FormattedMessage  id={dealInforList.dealType == "buy" ? '付款金额':'收款金额'}/></th>
                                            <th><FormattedMessage  id={dealInforList.dealType == 'buy'?'购买数量':'出售数量'}/></th>
                                            <th><FormattedMessage  id="单价"/></th>
                                            <th><FormattedMessage  id="手续费"/></th>
                                            {dealInforList.dealType == 'buy' && <th><FormattedMessage  id="实际到账"/></th>}
                                            <th><FormattedMessage  id="交易时间"/></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr className="coFont1">
                                            {/* dealInforList.map  */}
                                            <td>{orderListInfor.orderMoney}CNY</td>
                                            <td>{orderListInfor.orderNum + dealInforList.icon}</td>
                                            <td>{orderListInfor.price}CNY</td>
                                            <td>{orderListInfor.charge + dealInforList.icon}</td>
                                            {dealInforList.dealType == 'buy' && <td>{orderListInfor.realMoney + dealInforList.icon}</td>}
                                            <td>{moment(orderListInfor.time).format(LAN == 'en' ? DATA_TIEM_EN : DATA_TIME_FORMAT)}</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            {(orderListInfor.marker && dealInforList.dealType == 'buy') && <p className="marker"><FormattedMessage id="我的备注："/>{orderListInfor.marker}</p>}
                            {/* 操作按钮 */}
                            <DealButton  dealInforList={dealInforList} getOrderDetailEvent={this.getOrderDetailEvent} />
                         </div>
                        </div>
                        <div className="talk">
                            <div className="title f-16"><FormattedMessage  id="在线聊天"/></div>
                            <TalkRoom isConnet={this.props.isConnet} 
                            RongIMLib={this.props.chatApi} 
                            dealInforList={dealInforList} 
                            text={this.props.text}  
                            count={this.props.count}
                            sysText = {this.props.sysText} 
                            countSys = {this.props.countSys}
                            emojiList={this.props.emojiList} 
                            nickname={this.props.userInfor.nickname}  
                            getOrderDetailEvent={this.getOrderDetailEvent}
                            isGetTalkHistory = {this.props.isGetTalkHistory}
                            isGetTalkHistoryEvent = {this.props.isGetTalkHistoryEvent}/>
                        </div>
                    </div>
               </React.Fragment>
               :
               <div className="erroPage">
                   <h3><span style={{color:'#3E85A2',paddingRight:'10px'}} className="iconfont icon-gonggao-moren"></span><FormattedMessage id="无效订单"/></h3>
               </div>
           }
           </div>
        )
    }
}

export default withRouter(injectIntl(OrderDetail));