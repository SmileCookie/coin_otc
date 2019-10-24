
import React from "react";
import {injectIntl } from "react-intl";
import {connect} from "react-redux";
import {formatDate, optPop} from "../../utils";

import './userCenter.less'
import {fetchBlackList, fetchUesrHomePage} from "../../redux/modules/account";
import axios from "axios";
import {DOMAIN_VIP,OTC,OTC_UIR} from "../../conf";

/**
 * 个人主页 组件
 */

@connect(
    (state)=>({
        homePage:state.account.homePage
    }),
    {
        fetchBlackList,fetchUesrHomePage

    }
)

class UserCenter extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            flg:false,
            ...this.props
        }

        this.addBlackList = this.addBlackList.bind(this);
        this.removeBlackList = this.removeBlackList.bind(this);
    }
    // 添加黑名单
    addBlackList =(id) =>{
        let data = new FormData();
        id = parseInt(id)
        data.append("blackUserId",id);
        axios.post(OTC_UIR + OTC + '/web/blacklist/saveBlacklist', data).then((res)=>{
            // console.log(res);
            let msg = res.data.msg;
            this.props.modal.closeModal()
            if (res.data.code == 200){
                optPop(() =>{
                    this.props.fetchBlackList();
                    // this.props.fetchUesrHomePage(id);
                },msg,{timer: 1500});
            }else{
                optPop(() =>{
                },msg,{timer: 1500})
            }
        })
    }

    removeBlackList = (id) =>{
        let data = new FormData();
        data.append("id",id);
        axios.post(OTC_UIR + OTC + '/web/blacklist/delBlacklistByUser', data).then((res)=>{
            // console.log(res);
            let msg = res.data.msg;
            this.props.modal.closeModal()
            if (res.data.code == 200){
                optPop(() =>{
                    this.props.fetchBlackList();
                    // this.props.fetchUesrHomePage(id);
                },msg,{timer: 1500});
            }else{
                optPop(() =>{
                },msg,{timer: 1500})
            }
        })
    }

    render(){
        let {formatMessage} = this.props.intl
        let {hoemPage,uid,targetId }= this.state;
        let {flg} = this.state;
        if (uid == targetId){
            flg = true;
        }
        let on = hoemPage.online == 1 ? true : false
        return(

            <div className="user-center" style={{width:"600px"}}>
                <div className="user-center-title">
                    <span onClick={() => this.props.modal.closeModal()}>×</span>
                </div>
                <div className="user-center-cont">
                    <div className="center-cont-base">
                        <div className="cont-base-icon" style={{background:hoemPage.color}}>
                            {hoemPage.nickname.substr(0,1).toLocaleUpperCase()}
                            <i className={`${on ? 'online' : 'offline'}`}></i>
                        </div>
                        <div className="cont-base-info">
                            <p className="base-info-name"><span>{hoemPage.nickname}</span>
                                {
                                    hoemPage.cardStatus != 6?
                                    <span className="not">{formatMessage({id: '未认证'})}</span>
                                        :
                                    <span className="done">{formatMessage({id: '已认证'})}</span>
                                }
                                </p>
                            <p><span>{formatMessage({id: '注册时间'})}:</span><span>{!!hoemPage.firstRegTime && formatDate(hoemPage.firstRegTime)}</span></p>
                            <p><span>{formatMessage({id: '首次访问法币时间'})}:</span>
                            {
                                hoemPage.firstVisit==null?<span></span>:<span>{!!hoemPage.firstVisit && formatDate(hoemPage.firstVisit)}</span>
                            }
                            </p>
                            {/* <p><span>{formatMessage({id: '首次访问法币时间'})}:</span><span>{!!hoemPage.firstVisit && formatDate(hoemPage.firstVisit)}</span></p> */}
                        </div>
                    </div>
                    <div className="center-cont-info">
                        <ul className="cont-info-ul">
                            <li>
                                <p>{hoemPage.tradeVolume}</p>
                                <p>{formatMessage({id:'成交笔数'})}</p>
                            </li>
                            <li>
                                <p>{hoemPage.avgPassTime}<span>min</span></p>
                                <p>{formatMessage({id: '平均放行时间'})}</p>
                            </li>
                            <li>
                                <p>{hoemPage.stageVolume}</p>
                                <p>{formatMessage({id: '%%日订单笔数'}).replace('%%',hoemPage.stageDate)}</p>
                            </li>
                            <li>
                                <p>{hoemPage.stageVolumeRate}%</p>
                                <p>{formatMessage({id: '%%日完成率'}).replace('%%',hoemPage.stageDate)}</p>
                            </li>
                        </ul>
                    </div>
                    {
                        !flg ?
                        <div className="user-center-footer">
                            {
                                hoemPage.blackListFlg == 0 ?
                                <p><span className="btnc" onClick={() =>{this.removeBlackList(targetId)}}>{formatMessage({id: '移除黑名单'})}</span></p>
                                :
                                        <p>
                                            <span className="btnc" onClick={() =>{this.addBlackList(targetId)}}>{formatMessage({id: '移入黑名单'})}</span>
                                            <p>{formatMessage({id: '温馨提示：移入黑名单的用户将无法看到彼此的广告。并且将无法与您私信'})}</p>
                                        </p>

                            }

                        </div>
                            :null
                    }
                </div>

            </div>
        )
    }
}

export default injectIntl(UserCenter)