
import React from 'react';
import { Link } from 'react-router-dom';
import cookie from 'js-cookie'
import {withRouter} from 'react-router'
import { connect } from 'react-redux'
import { FormattedMessage,injectIntl } from 'react-intl';
import Laydate from 'layui-laydate';
import '../../assets/style/vendor/laydata/laydate.css'
import Tab from '../../components/tab/tab_order';
import '../../assets/style/order/index.less'
import {getOrederList,getOrederNum}  from './model'
import {getFindCoinName} from '../advertisement/index.model'
import Pages from '../../components/page'
import UserCenter from '../../components/user/userCenter'
import ReactModal from '../../components/popBox';
import {pageClick} from '../../redux/module/session';
import moment from 'moment';
import {DATA_TIME_FORMAT,USERID,DATA_TIEM_EN} from 'conf'
import {ThemeFactory, Styles} from '../../components/transition';
import Cookie from 'js-cookie'
import {separator,getCoinNum,GetRequest} from '../../utils'
const BigNumber = require('big.js')
//import throttle from 'lodash/throttle';


import {post,get} from 'nets'

//coinList: state.account.detail,
@connect(
    state => ({
        userInfor:state.session.userInfor,
        coinData:state.session.coinData,
        countSys:state.chart.countSys
    }),
    {
        pageClick
    }
)
class Order extends React.Component {
    constructor(props) {
        super(props); 
        this.state = {
            tabIndex: GetRequest().appeal || 0,
            tabIndex2:0,
            //tabList: ['全部订单','待付款{num}','待放币{num}','交易完成{num}','异常/申诉{num}'],
            tabList:[],
            curencyList: [
                {
                    fundsType: '',
                    coinName: '',
                    value:<FormattedMessage id ='全部'/>
                }
            ],
            tradeTypeList: [
                {
                    key: '2',
                    value: '全部'
                },
                {
                    key: '0',
                    value: '购买'
                },
                {
                    key: '1',
                    value: '出售'
                },
            ],
            curency:'',
            tadeType:2,
            datePicker:'',
            orderCode:'',
            tableList: [],
            bordeBlue: false,
            filterVal: '',
            btnStus: 0,
            pageIndex:1,
            pageSize:15,
            count:0,
            baseMark:'/CNY',
            startTime:'',
            endTime:'',
            cancletrade: 0, //取消交易
            complain: 0, //申诉中
            errororder: 0, //异常订单
            overpay: 0,//已付款
            overtrade: 0,//交易完成
            waitpay: 0, //等待付款
            loading:true,
            totalPage:0,
            searchTime:Date.now()
            
        };
        this.showUserCenter = this.showUserCenter.bind(this)
        // this.handleSearch = throttle(this.clearPageChoose, 500);
    }

    componentWillMount(){
       if(!this.props.userInfor.data){
           window.location.href = '/bw/login'
       }
       console.log(this.props.countSys);
       
    }
    componentWillReceiveProps(nextProps){
        if(nextProps.countSys !== this.props.countSys){
            
            this.getOrderListEvent()
            this.getOrederNumEvent()
        }
    }
     //输入时 input 设置到 satte
     handleInputChange=(event) =>{
        let _this = this;
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value,
        },() => {
            //this.handleSearch()
            //this.debounce(_this.clearPageChoose,500);
            this.clearPageChoose();
        });
        if (name == 'filterVal' && value) {
            this.setState(preState => {
                if (preState.btnStus != 1) {
                    return { btnStus: 1}
                }
            })
        } else {
            this.setState(preState => {
                if (preState.btnStus != 0) {
                    return { btnStus: 0}
                }
            })
        }
    };
     //搜索框边框
     changeFocus = () =>{
        this.setState({
            bordeBlue: true
        })
    };
      //搜索框边框
      changeFocus = () =>{
        this.setState({
            bordeBlue: true
        })
    };
    changeBlur = () => {
        this.setState({
            bordeBlue: false
        })
    };
    clearFilterVal=()=> {
        this.setState({
            filterVal: '',
            btnStus: 0
        },() => this.clearPageChoose())
    }

    //筛选后清除page信息
    clearPageChoose = () =>{
        this.setState({
            pageIndex:1
        },() => this.getOrderListEvent())
    }

    debounce = (fun, delay) => {
            // debugger
            // console.log();
            
            return function (args) {
                let that = this
                let _args = args
                clearTimeout(fun.id)
                fun.id = setTimeout(function () {
                    fun.call(that, _args)
                }, delay)
            }
      }
      

    componentDidMount() {
       let self = this;
       const lang    = cookie.get('zlan')  == 'cn' ? 'cn' : 'en';
       //时间控件
       Laydate.render({
            elem: '#layDate',
            lang: lang,
            type: 'datetime',
            range: '-',
            format: 'yyyy-M-d H:m:s',
            done(value, dates, endDate){
                console.log(dates);
                console.log(endDate);
                self.setState({
                    datePicker: value,
                    startTime:JSON.stringify(dates) !== "{}" ? `${dates.year}-${dates.month}-${dates.date} ${dates.hours}:${dates.minutes}:${dates.seconds}` : '',
                    endTime:JSON.stringify(endDate) !== "{}" ? `${endDate.year}-${endDate.month}-${endDate.date} ${endDate.hours}:${endDate.minutes}:${endDate.seconds}` : '',
                }, ()=> self.clearPageChoose());
            }
        });

        setTimeout(() =>{
            this.props.pageClick(1)
           // console.log(784);
            
        },100)

      
        //初始化数据
      
       this.getOrederNumEvent()
       this.getOrderListEvent()
       this.getFindCoinName()
       //轮训
       this.time = setInterval(() =>{
            //this.getOrderListEvent()
            this.getOrederNumEvent()
            //this.getOrderListEvent()
        },3000)

        //console.log(GetRequest());
         
    }

    componentWillReceiveProps() {  
        
    }
    //切换tab
    tabChange =(val)=> {
        this.setState({
            tabIndex: val
        },() =>{
            this.clearPageChoose();
        })
    };

    //筛选
    chooseItem =(val, type)=> {
        if(type) {
            this.setState({
                curency: val
            }, () => this.clearPageChoose())
        } else {
            this.setState({
                tadeType: val
            }, () => this.clearPageChoose())
        }
    };
    //获取列表
    getOrderListEvent = () =>{
        let { pageSize,pageIndex,tabIndex ,tadeType,filterVal, curency,baseMark,datePicker,startTime,endTime} = this.state;
         console.log(datePicker);
        //转化订单状态
        if(tabIndex == 0){
            tabIndex = ''
        }else if(tabIndex == 4){
            tabIndex = '5,6'
        }
        
        // switch(tabIndex){
        //     case 0:
        //         tabIndex = '';
        //         break;
        //     case 4:
        //         tabIndex = '5,6';
        //         break;
        // }
        //datePicker
        curency === '' ? curency = '' : curency = curency + baseMark;//币种市场        
        getOrederList(pageSize,pageIndex,tabIndex ,tadeType,filterVal, curency,startTime,endTime,this).then(res =>{
            let tableList = [];
            res.list.map((item,index) =>{
                let market =  item['market'];
                let _obj = getCoinNum(this.props.coinData,market)
                //console.log(_obj);
                let _item = Object.assign({},item,_obj)
                tableList.push(_item)
            })
            console.log(tableList);
            
            this.setState({
                 tableList,
                 count:res.totalCount,
                 pageIndex:res.currPage,
                 totalPage:res.totalPage
            })
            
        })
    }
    //获取订单数量
    getOrederNumEvent = () =>{
        getOrederNum().then(res =>{
            let {cancletrade, complain, errororder , overpay,overtrade, waitpay}  =  res;
            let list = [{
                value: '全部订单',
                num:0
            },
            {
                value: '待付款{num}',
                num:waitpay,
                key:'waitpay'
            },
            {
                value: '待放币{num}',
                num:overpay,
                key:'overpay'
            },
            {
                value: '交易完成{num}',
                num:overtrade,
                key:'overtrade'
            },
            {
                value: '异常/申诉{num}',
                num:complain + errororder,
                key:'complain' //异常/申诉合起来
            }]
            //console.log(errororder);
            this.setState({
                tabList:list
            })
            
            // this.setState({
            //     cancletrade,
            //     complain,
            //     errororder,
            //     overpay,
            //     overtrade,
            //     waitpay
            // })
        })
    }
    //分页
    currentPageClick= (values)=>{
        this.props.pageClick(0);
        this.setState({
            pageIndex:values
        },() => {
            this.getOrderListEvent();
        });
    };
    //获取币种列表
    getFindCoinName = () =>{
        const {curencyList} = this.state;
        getFindCoinName().then(res =>{
           res.forEach((item) =>{
                item.value = item.coinName
           })
           let _curencyList = curencyList.concat(res)
           //console.log(curencyList);
           this.setState({
                curencyList:_curencyList
           }) 
        })
    }
   
    async showUserCenter(item) {
        let targetId = item.buyUserId == USERID ? item.sellUserId: item.buyUserId
        let userId = USERID || "";
        let res = await post('/web/common/getAvgPassTime', {targetUserId: targetId, userId})
        if (res.code == 200){
            this.setState({
                targetId,
                uid: USERID,
                homePage: res.data,
            },() =>{
                this.modalUser.openModal()
            })
        }else{
            optPop(() =>{},res.data.msg,{timer: 1500})
        }
    }
    render() {
        const {tabList,tabIndex,curencyList,tadeType,curency,tradeTypeList,datePicker,orderCode,tableList,bordeBlue,filterVal,btnStus, pageIndex,pageSize, count,loading,totalPage} = this.state; 
        const {formatMessage} = this.props.intl
        //const userId = Cookie.get('zuid')
        const LAN = Cookie.get('zlan')
        
        return (
           <div className="OrderMain">
               <div className="page_title"><FormattedMessage id="订单列表"/></div>
               <Tab list={tabList} index={tabIndex} onChange={(val)=>this.tabChange(val)}/>
               <div className="chooseItem clearfix" >
                    <div className={bordeBlue ? "input-box borde-blue" : 'input-box'}>
                            <input style={{width:'195px'}} type="text" name="filterVal" value={filterVal}  placeholder={formatMessage({id:'输入订单号'})} onChange={this.handleInputChange} onFocus={this.changeFocus} onBlur={this.changeBlur} />
                            <button onClick={btnStus == 1 ? this.clearFilterVal : null} className={btnStus == 0 ? "iconfont icon-search-bizhong" : "iconfont icon-shanchu-moren"}></button>
                    </div>
                    <div className="entrust-head-type left">
                            <h5 className="left padd5"><FormattedMessage id="币种："/></h5>
                            {
                                curencyList.map((item,index) => {
                                    return (
                                        <span key={index} className={`currency_label ${curency == item.coinName ? 'curency_choose': ''}`} onClick={()=>this.chooseItem(item.coinName, 1)}>{item.value}</span>
                                    )
                                })
                            }
                        </div>
                    <div className="entrust-head-type left">
                            <h5 className="left padd5"><FormattedMessage id="交易类型："/></h5>
                            {
                                tradeTypeList.map((item,index) => {
                                    return (
                                        <span key={index} className={`currency_label ${tadeType == item.key ? 'curency_choose': ''}`} onClick={()=>this.chooseItem(item.key)}>{formatMessage({id:item.value})}</span>
                                    )
                                })
                            }
                        </div>
                    <div className="entrust-head-type right sp">
                        <h5 className="left padd5"><FormattedMessage id="日期："/></h5>
                        <div className="input-box">
                            <input type="text" readOnly={true} data-name="datePicker"  value={datePicker} id="layDate" name="filterVal" placeholder={formatMessage({id:'请选择日期'})}/>
                            <button className="iconfont icon-ico_time"></button>
                        </div>
                    </div>
               </div>
               <div className="bk-entrust">
                    <table className="table-entrust bk_table">
                        <thead>
                        <tr>
                            <th className="text-center"><FormattedMessage id="交易币种"/></th>
                            <th className="text-center"><FormattedMessage id="交易类型"/></th>
                            <th className="text-center"><FormattedMessage id="交易数量"/></th>
                            <th className="text-center"><FormattedMessage id="单价"/></th>
                            <th className="text-center"><FormattedMessage id="交易金额"/></th>
                            <th className="text-center"><FormattedMessage id="手续费"/></th>
                            <th className="text-center"><FormattedMessage id="订单状态"/></th>
                        </tr>
                        </thead>
                        <tbody id="historyEntrustList">
                            {   !loading?
                                tableList.length > 0? tableList.map((item,index) => {
                                    let backCardname = ''
                                    console.log(!item.sellerCardName);

                                   // debugger
                                    if( item.buyUserId == USERID){

                                       backCardname = item.sellerCardName ? `(${item.sellerCardName})` : ''  

                                    }else{

                                        backCardname = item.buyerCardName ? `(${item.buyerCardName})` : ''
                                    }
                                    //console.log(backCardname);
                                    
                                    return (
                                        <React.Fragment key={index}>
                                            <tr className="first_tr">
                                                <td className="text-center">{formatMessage({id:"订单编号"})} <span className="white">{item.recordNo}</span></td>
                                                <td className="text-center"></td>
                                                <td className="text-center">{formatMessage({id:"交易时间："})}<span className="white">{moment(item.coinTime).format(LAN == 'en' ? DATA_TIEM_EN : DATA_TIME_FORMAT)} </span> </td>
                                                <td className="text-center">
                                                    <span >{formatMessage({id: item.buyUserId == USERID? "卖家：" :"买家："})}</span> 
                                                    <a className="blue" onClick={() => this.showUserCenter(item)} style={{cursor: 'pointer'}}>{item.buyUserId == USERID ? item.sellUserName +  backCardname : item.buyUserName +  backCardname }</a>
                                                    {/* <span className="charts iconfont icon-Shape-message">
                                                        <i>22</i>
                                                    </span> */}
                                                </td>
                                                <td></td>
                                                <td></td>
                                                <td></td>
                                            </tr>
                                            <tr className="second_tr">
                                                <td className="text-center white">{item.market}</td>
                                                <td className="text-center">{ USERID == item.buyUserId ? formatMessage({id:"购买"}) :formatMessage({id:"出售"})}</td>
                                                <td className="text-center white">{new BigNumber(item.coinNumber).toFixed(item.marketL)} {item.market}</td>
                                                <td className="text-center white">{separator(new BigNumber(item.coinPrice).toFixed(item.payL))} CNY</td>
                                                <td className="text-center white">{separator(new BigNumber(item.myMoney).toFixed(item.payL))} CNY</td>
                                                <td className="text-center white">{item.buyUserId == USERID ? item.buyUserFeeStr: item.sellUserFeeStr } {item.market}</td>
                                                <td className="text-center control">
                                                    <div>
                                                        <span className="white">
                                                            {formatMessage({id:item.statusName})}
                                                        </span>
                                                    </div>
                                                    <div>
                                                        <a  href={`/otc/orderDetail/${item.id}`}>
                                                            {formatMessage({id:"查看详情"})}
                                                        </a>
                                                    </div>     
                                                </td>
                                            </tr>
                                        </React.Fragment>
                                        
                                    )
                                }):(
                                    <tr className="nodata">
                                        <td className="billDetail_no_list" colSpan="15">
                                            <p className="entrust-norecord">
                                                <svg className="icon" aria-hidden="true">
                                                    <use xlinkHref="#icon-tongchang-tishi"></use>
                                                </svg>
                                                <span><FormattedMessage id="没有相关的订单记录"/></span>
                                            </p>
                                        </td>
                                    </tr>
                                )
                                :
                                <tr className="spl_tr" style={{height:'500px'}}>
                                    <td className="billDetail_no_list" colSpan="15">
                                        {ThemeFactory.getThemeInstance(Styles.ThemeA)}
                                    </td>
                                </tr>

                            }
                        </tbody>
                    </table>
                    {
                       totalPage > 1 &&
                        <div className="tablist">
                                <Pages
                                    pageIndex={pageIndex}
                                    pagesize={pageSize}
                                    total={count}
                                    ref="pages"
                                    currentPageClick = { this.currentPageClick }
                                />
                        </div>
                    }
                </div>
                <ReactModal ref={modal => this.modalUser = modal}   >
                    <UserCenter modal={this.modalUser}  hoemPage={this.state.homePage} targetId={this.state.targetId} uid={this.state.uid}/>
                </ReactModal>
           </div>
        )
    }
}

export default withRouter(injectIntl(Order));