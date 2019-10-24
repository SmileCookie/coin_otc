import React from 'react';
import Form from '../../decorator/form';
import Pages from '../../components/pages';
import {fetchIntegral } from '../../redux/modules/level'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../conf'
import '../../assets/css/table.less';
import axios from 'axios';
import qs from 'qs';
import { connect } from 'react-redux';
import { separator,formatDate} from '../../utils';
import thunk from '../../../node_modules/redux-thunk';
const BigNumber = require('big.js');

@connect(
    state => ({
        lng: state.language.locale,
        integral:state.level.integral,
        userInfo: state.session.baseUserInfo,
    }),
    (dispatch) => {
        return {
            fetchIntegral: () => {
                dispatch(fetchIntegral())
            }
        }
}
)
@Form
class Grade extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            tableList:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            data:[],
            totalCount:0,
            scoreList: {
                mobileState: false,
                googleStatus: false,
                fstStatus: false,
                dayStatus: false,
                coinStatus: false,
                someCoinStatus: false,
                mobileClose: false,
                googleClose: false,
            }
        }
        this.requestTable = this.requestTable.bind(this)
        this.handlePageChanged = this.handlePageChanged.bind(this)

        // this.setSt = this.setSt.bind(this);
        // this.currentPageClick = this.currentPageClick.bind(this);
        // this.getList = this.getList.bind(this);
    }

    componentDidMount(){
        this.props.fetchIntegral();
        this.requestTable();

        // 获取积分的状态
        axios.get(DOMAIN_VIP + '/manage/level/getFuncJumpInfo').then((res) => {
            this.setState({
                scoreList: {...this.state.scoreList, ...res.data.datas}
            })
        });

    }
    handlePageChanged(val){
        this.setState({
            pageIndex:val
        },() => this.requestTable())
    }
    requestTable(){
        axios.get(DOMAIN_VIP+"/manage/level/level").then(res => {
            const result = res.data
            if(result.isSuc){
                this.setState({
                    tableList:result.datas.userVipList
                })
            }
        })
        const { pageIndex } = this.state
        axios.post(DOMAIN_VIP+"/manage/level/ajaxJson",qs.stringify({
            page:pageIndex
        })).then(res => {
            const result = res.data
            if(result.isSuc){
                this.setState({
                    data:result.datas.info,
                    totalCount:result.datas.total
                })
            }
        })
    }

    render(){
        const path = this.props.userInfo.mobileStatus !== 2  ? 'dEbUpMobile' : 'ebMobile';
        const { mobileState, googleStatus, fstStatus, dayStatus, coinStatus, someCoinStatus, mobileClose, googleClose, } = this.state.scoreList;
        const { formatMessage } = this.intl;
        const {tableList,totalCount,pageIndex,pageSize,data} = this.state;
        const integral = this.props.integral;
        let occupy = "";
        let value = separator(integral.value);
        let levelBeginPoint = separator(integral.levelBeginPoint);
        let nextLevelBeginPoint = separator(integral.nextLevelBeginPoint-integral.value>0?integral.nextLevelBeginPoint-integral.value:0);
        if(integral.value>=integral.nextLevelBeginPoint){
            occupy = "100%";
        }else{
            //big.js 
            occupy = new BigNumber((integral.value-integral.levelBeginPoint)).div((integral.nextLevelBeginPoint-integral.levelBeginPoint)).times(100).toString()+"%";
        }
        return(
            <div className="content">
                <div className="grade clearfix">
                    <div className="my-grade">
                        <div className="grade-title">{formatMessage({id: "我的账户等级:"})}VIP-{integral.level}</div>
                        <p className="grade-setion">
                            {formatMessage({id: "我的积分:"})}&nbsp;
                            <span>{value}</span>
                            {integral.nextLevelBeginPoint==integral.levelBeginPoint?"":formatMessage({id: "再获得 XXX,XXX 积分即可升级至 VIP- N"}).replace('XXX,XXX', integral.level<10?nextLevelBeginPoint:null).replace(' N',integral.nextLevel)}
                        </p>
                        <div className="grade-progress">
                            <div className="grade-bar" style={{width:occupy}}></div>
                        </div>
                        <div className="grade-ladder">
                            <span>{separator(integral.levelBeginPoint)}</span>
                            <span>{
                            integral.nextLevelBeginPoint==integral.levelBeginPoint?'':separator(integral.nextLevelBeginPoint)
                        }</span>
                        </div>
                    </div>
                    <div className="grade-effect">
                        <div className="grade-title">{formatMessage({id: "账户等级的作用"})}</div>
                        <div className="grade-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>{formatMessage({id: "账户等级"})}</th>
                                        <th>{formatMessage({id: "所需积分"})}</th>
                                        <th>{formatMessage({id: "手续费收取"})}</th>
                                        <th>{formatMessage({id: "备注"})}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                {
                                    tableList.length>0&&tableList.map((item,index)=>{
                                        return(
                                            <tr key={index}>
                                                <td>VIP-{item.vipRate}</td>
                                                <td>{item.jifen}</td>
                                                <td>{item.discount}%</td>
                                                <td>{item.memo?item.memo:"--"}</td>
                                            </tr>
                                        )
                                    })
                                }
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div className="grade-int">
                        <div className="grade-title">{formatMessage({id: "如何获得积分"})}</div>
                        <div className="grade-table ">
                            <table>
                                <thead>
                                    <tr>
                                        <th>{formatMessage({id: "条件"})}</th>
                                        <th>{formatMessage({id: "bbyh积分"})}</th>
                                        <th>{formatMessage({id: "说明"})}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <tr>
                            <td>{formatMessage({id: "level.obtainIntegral5"})}<em style={{display:'inline-block'}} className="ok"></em></td>
                            <td>1000</td>
                            <td>{formatMessage({id: "level.obtainIntegral6"})}</td>
                        </tr>
                        <tr>
                            <td>{formatMessage({id: "level.obtainIntegral7"})}<em style={{display:'inline-block'}} className="ok"></em></td>
                            <td>50</td>
                            <td>{formatMessage({id: "level.obtainIntegral8"})}</td>
                        </tr>
                        <tr>
                            <td className={mobileState ? 'isSuc' : ''}><a href={mobileClose ? '/bw/mg/closeMobileCk' : `/bw/mg/${path}`}>{formatMessage({id: "level.obtainIntegral11"})}</a><em className="ok"></em></td>
                            <td>500</td>
                            <td>{formatMessage({id: "level.obtainIntegral12"})}</td>
                        </tr>
                        <tr>
                            <td className={googleStatus ? 'isSuc' : ''}><a href={googleClose ? '/bw/mg/closeG' : '/bw/mg/googleOne'}>{formatMessage({id: "level.obtainIntegral13"})}</a><em className="ok"></em></td>
                            <td>500</td>
                            <td>{formatMessage({id: "level.obtainIntegral14"})}</td>
                        </tr>
                        <tr>
                            <td className={fstStatus ? 'isSuc' : ''}><a href={fstStatus ? '/bw/manage/account/charge?coint=BTC' : '/bw/manage/account/charge?coint=BTC'}>{formatMessage({id: "level.obtainIntegral15"})}</a><em className="ok"></em></td>
                            <td>2000</td>
                            <td>{formatMessage({id: "level.obtainIntegral16"})}</td>
                        </tr>
                        <tr>
                            <td className={dayStatus ? 'isSuc' : ''}><a href={dayStatus ? '/bw/manage/account/charge?coint=BTC' : '/bw/manage/account/charge?coint=BTC'}>{formatMessage({id: "level.obtainIntegral17"})}</a><em className="ok"></em></td>
                            <td>10</td>
                            <td>{formatMessage({id: "level.obtainIntegral18"})}</td>
                        </tr>
                        <tr>
                            <td className={coinStatus ? 'isSuc' : ''}><a href={coinStatus ? '/bw/trade' : '/bw/trade'}>{formatMessage({id: "level.obtainIntegral19"})}</a><em className="ok"></em></td>
                            <td>5000</td>
                            <td>{formatMessage({id: "level.obtainIntegral20"})}</td>
                        </tr>
                        <tr>
                            <td className={someCoinStatus ? 'isSuc' : ''}><a href={someCoinStatus ? '/bw/trade' : '/bw/trade'}>{formatMessage({id: "level.obtainIntegral21"})}</a><em className="ok"></em></td>
                            <td>10</td>
                            <td>{formatMessage({id: "level.obtainIntegral22"})}</td>
                        </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div className="grade-int-detail">
                        <div className="grade-title">{formatMessage({id: "积分明细"})}</div>
                        <div className="grade-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>{formatMessage({id: "日期"})}</th>
                                        <th>{formatMessage({id: "类型"})}</th>
                                        <th>{formatMessage({id: "积分"})}</th>
                                        <th>{formatMessage({id: "描述"})}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                {
                                    data.map((item,index)=>{
                                            return(
                                                <tr key={index}>
                                                    <td>{formatDate(new Date(item.addTime)).replace(',', '').replace(/\//g, this.sp)}</td>
                                                    <td>{item.typeShowNew}</td>
                                                    <td>{item.ioType == 0? "+": "-"}{item.jifen}</td>
                                                    <td>{item.memo}</td>
                                                </tr>
                                            )
                                        }
                                    )
                                }
                                </tbody>
                            </table>
                            <div className="bk-pageNav">
                            <div className={totalCount <= pageSize ? "tablist hide" : "tablist"}>
                                <Pages
                                    total={totalCount}
                                    pageIndex={pageIndex}
                                    pagesize={pageSize}
                                    currentPageClick = { this.handlePageChanged }
                                />
                  </div>
                </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}
export default Grade