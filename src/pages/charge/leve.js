import React from 'react';
import Form from '../../decorator/form';
import Pages from '../../components/pages';
import {fetchIntegral } from '../../redux/modules/level'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../conf'
import '../../assets/css/table.less';
import axios from 'axios';
import cookie from 'js-cookie'
import qs from 'qs';
import { connect } from 'react-redux';
import { separator,formatDate} from '../../utils';
import thunk from '../../../node_modules/redux-thunk';
const BigNumber = require('big.js');
import '../../assets/css/chargeList.less'


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
class Leve extends React.Component{
    constructor(props){
        super(props)

        this.state = {
            tableList:[],
            isLogin:cookie.get('zuid')
        }
        this.requestTable = this.requestTable.bind(this)
    }
    componentDidMount(){
        if(this.state.isLogin){
            this.props.fetchIntegral();
        }
        this.requestTable();
    }

    requestTable(){
        axios.get(DOMAIN_VIP+"/getLevel").then(res => {
            const result = res.data
            if(result.isSuc){
                this.setState({
                    tableList:result.datas.userVipList
                })
            }
        })
        
    }
   
    render(){
        const {tableList,isLogin} = this.state;
        const { formatMessage } = this.intl;
        const integral = this.props.integral;
        let occupy = "";
        let value = separator(integral.value);
        let levelBeginPoint = separator(integral.levelBeginPoint);
        let nextLevelBeginPoint = separator(integral.nextLevelBeginPoint-integral.value>0?integral.nextLevelBeginPoint-integral.value:0);
        if(isLogin){
            if(integral.value>=integral.nextLevelBeginPoint){
                occupy = "100%";
            }else{
                //big.js 
                 occupy = new BigNumber((integral.value-integral.levelBeginPoint)).div((integral.nextLevelBeginPoint-integral.levelBeginPoint)).times(100).toString()+"%";
            }
        }
       
        return(
             <div className="tableContent">
                    {
                        isLogin &&
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
                    }
                     
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
             </div>
        )
    }
}
export default Leve