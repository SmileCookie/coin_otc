
import React from "react";
import {injectIntl } from "react-intl";
import {connect} from "react-redux";
import {formatDate, optPop} from "../../utils";
import cookie from 'js-cookie';
import './userCenter.less'
import axios from "axios";
import {DOMAIN_VIP, USERID} from "../../conf";
import {post, get} from 'nets';

/**
 * 个人主页 组件
 *
 *
 *   <ReactModal ref={modal => this.modal = modal}   >
        <UserCenter modal={this.modal}  hoemPage={this.state.homePage} targetId={this.state.targetId} uid={this.state.uid}/>
    </ReactModal>
 */

// @connect(
//     (state)=>({}),
//     {
//         // fetchBlackList
//     }
// )

class UserCenter extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            submitPengding: false,
            flg:false,
            ...this.props
        }

        this.addBlackList = this.addBlackList.bind(this);
        this.removeBlackList = this.removeBlackList.bind(this);
    }
    // 添加黑名单
    async addBlackList(id){
        const {submitPengding} = this.state
        if (submitPengding) {
            return
        }
        let data = new FormData();
        id = parseInt(id)
        this.setState({
            submitPengding: true
        }, async () => {
            await post('/web/blacklist/saveBlacklist', {blackUserId: id}).then((res)=>{
                let msg = res.msg;
                this.props.modal.closeModal()
                if (res.code == 200){
                    optPop(() =>{},msg,{timer: 1500});
                }else{
                    optPop(() =>{},msg,{timer: 1500})
                }
                this.setState({
                    submitPengding: false
                })
            })
        })
    }

    async removeBlackList (id){
        const {submitPengding} = this.state
        if (submitPengding) {
            return
        }
        let data = new FormData();
        this.setState({
            submitPengding: true
        }, async () => {
            await post('/web/blacklist/delBlacklistByUser', {id}).then((res)=>{
                let msg = res.msg;
                this.props.modal.closeModal()
                if (res.code == 200){
                    optPop(() =>{},msg,{timer: 1500});
                }else{
                    optPop(() =>{},msg,{timer: 1500})
                }
                this.setState({
                    submitPengding: false
                })
            })
        })
    }

    render(){
        let {formatMessage} = this.props.intl
        let {hoemPage,uid,targetId, submitPengding }= this.state;
        let {flg} = this.state;
        if (uid == targetId){
            flg = true;
        }
        let on = hoemPage.online == 1 ? true : false
        let lan = cookie.get('zlan')
        return(
            <div className="user-center" style={lan == 'en' ? {width: '600px'} : {}}>
                <div className="user-center-title">
                    <span onClick={() => this.props.modal.closeModal()}>×</span>
                </div>
                <div className="user-center-cont">
                    <div className="center-cont-base">
                        <div className="cont-base-icon" style={hoemPage.color && {backgroundColor: hoemPage.color}}>
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
                            <p><span>{formatMessage({id: '注册时间'})}:</span><span>{formatDate(hoemPage.firstRegTime)}</span></p>
                            <p><span>{formatMessage({id: '首次访问法币时间'})}:</span><span>{hoemPage.firstVisit ? formatDate(hoemPage.firstVisit):''}</span></p>
                        </div>
                    </div>
                    <div className="center-cont-info">
                        <ul className="cont-info-ul">
                            <li>
                                <p>{hoemPage.tradeVolume}</p>
                                <p>{formatMessage({id: '成交笔数'})}</p>
                            </li>
                            <li>
                                <p>{hoemPage.avgPassTime}<span>min</span></p>
                                <p>{formatMessage({id: '平均放行时间'})}</p>
                            </li>
                            <li>
                                <p>{hoemPage.stageVolume}</p>
                                <p>{formatMessage({id: '%%日订单笔数'}).replace('%%', hoemPage.stageDate)}</p>
                            </li>
                            <li>
                                <p>{hoemPage.stageVolumeRate}%</p>
                                <p>{formatMessage({id: '%%日完成率'}).replace('%%', hoemPage.stageDate)}</p>
                            </li>
                        </ul>
                    </div>
                    {   
                        USERID ?
                            !flg ?
                                <div className="user-center-footer">
                                    {
                                        hoemPage.blackListFlg == 0 ?
                                            <p><span className={`btn ${submitPengding?'stop':null}`} onClick={() =>{this.removeBlackList(targetId)}}>{formatMessage({id: '移除黑名单'})}</span></p>
                                            :
                                            <div>
                                                <span className={`btn ${submitPengding?'stop':null}`} onClick={() =>{this.addBlackList(targetId)}}>{formatMessage({id: '移入黑名单'})}</span>
                                                <p>{formatMessage({id: '温馨提示：移入黑名单的用户将无法看到彼此的广告。并且将无法与您私信'})}</p>
                                            </div>

                                    }
                                </div>
                                :null
                            :null
                    }
                </div>

            </div>
        )
    }
}

export default injectIntl(UserCenter)