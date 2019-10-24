import React, { Component } from 'react';
import {DOMAIN_VIP} from "../../../conf";
import { optPop} from "../../../utils";
import { FormattedMessage, injectIntl } from 'react-intl';
import axios from "axios";
import { Link } from 'react-router';
import "./index.less";
import { JSEncrypt } from 'jsencrypt';
const encrypt = new JSEncrypt();

class index extends Component {

    constructor(props){
        super(props);
        this.state = {

            //isSetpwd:false,   //资金密码是否设置  这个在当前页面请求判断即可
            chooseBtnStatus:this.props.chooseBtnStatus||0, //当前所在的状态
            value:'',
            type:'text'
        }

        this.changeVerifyStatus = this.changeVerifyStatus.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.changeType = this.changeType.bind(this);
        this.commit = this.commit.bind(this);
        this.cancel = this.cancel.bind(this);
    }

    handleChange(event){
        //改变密码值
        this.setState({value: event.target.value});
    }

    changeVerifyStatus(statu){
        //改变当前状态
        this.setState({
            chooseBtnStatus:statu
        })
        //在这里先调用后台接口，改变后台成功后再改变父级的状态
        //this.props.changState(statu)
    }

    changeType(){
        this.setState({ type: 'password' });
    }

    //确定按钮 提交事件
    commit(){
        //加密密码
        if(this.state.value){
            axios.get(DOMAIN_VIP + "/login/getPubTag?t=" + new Date().getTime()).then((res)=>{
                encrypt.setPublicKey(res.data.datas.pubTag);

                axios.post(DOMAIN_VIP + '/manage/useOrCloseSafePwd?payPass=' + encrypt.encrypt(this.state.value)+'&closeStatu='+this.state.chooseBtnStatus).then((res) => {

                  
                        if(res.data.isSuc){
                            //资金密码输入正确，切换模式成功，关闭弹框
                            this.props.changState(0)//刷新数据关闭弹窗
                            optPop(() =>{},res.data.des,{timer: 1500},true)
                        }else{
                             //错误第五次的时候关闭弹窗
                            if(res.data.des.indexOf('_5')!= -1){
                                let newdes = res.data.des.slice(0,-2);
                                optPop(() =>{},newdes,{timer: 1500},true);
                                this.props.changState(0)//刷新数据关闭弹窗
                            }else{
                                optPop(() =>{},res.data.des,{timer: 1500},true)
                                this.props.changState(1) //刷新数据，不关闭弹窗
                            }
                        }
                   
                    
                })

            })
        }else{
            optPop(() =>{},this.props.intl.formatMessage({id:'security.text22'}),{timer: 1500},true)
        }

    }
    //取消按钮，关闭弹窗
    cancel(){
        //还是传不变的参数
        this.props.changState(0)
    }
    render() {
        const { formatMessage } = this.props.intl;
        const {chooseBtnStatus} = this.state;
        const isSetpwd = this.props.hasSafe; //从父级拿到的是否设置资金密码的状态
        const ppwLock = this.props.ppwlock;
        return (
            <div>
                {/* 交易验证弹框的内容 */}
                <div className="fund-box">
                    <p className="fund-notes">{formatMessage({id:'影响的功能范围'})}</p>
                    <div className="fund-topic">{formatMessage({id:'交易验证'})}</div>
                    <div className="fund-choose">
                        <div className={`chooseBtn ${chooseBtnStatus==0?"active":''}`} onClick={this.changeVerifyStatus.bind(this,0)}>{formatMessage({id:'始终开启'})}</div>
                        <div className={`chooseBtn ${chooseBtnStatus==6?"active":''}`} onClick={this.changeVerifyStatus.bind(this,6)}>{formatMessage({id:'sellbuy.p5'})}</div>
                        <div className={`chooseBtn ${chooseBtnStatus==1?"active":''}`} onClick={this.changeVerifyStatus.bind(this,1)}>{formatMessage({id:'始终关闭'})}</div>
                        <div style={{clear:"both"}}></div>  
                    </div>
                    <div className="fund-topic">{formatMessage({id:'user.text6'})}</div>
                    <div className="pwd-Box">

                        {!isSetpwd?
                        <div className="setPwd"><Link to={`/bw/mg/setPayPwd`}>{formatMessage({id:'deposit.text19'})}</Link></div>
                        :
                        <div className="inputPwd">
                            <input type={this.state.type} value={this.state.value} onChange={this.handleChange} onFocus={this.changeType} />
                            {ppwLock?
                                <a onClick={(e)=>e.preventDefault()} style={{color:'#737A8D'}}>{formatMessage({id:'重置资金密码'})}？</a>
                            :
                            <Link to={`/bw/mg/resetPayPwd`}>{formatMessage({id:'重置资金密码'})}？</Link>
                            }
                            
                            <div style={{clear:"both"}}></div>  
                        </div>
                        }

                        
                        
                    </div>
                    <div className="btn-box">
                        <div className="btn-content">
                            <div className="cancel fund-btn" onClick={this.cancel}>{formatMessage({id:'cancel'})}</div>
                            <div className="ensure fund-btn" onClick={this.commit}>{formatMessage({id:'sure'})}</div>
                            <div style={{clear:"both"}}></div> 
                        </div>
                        <div style={{clear:"both"}}></div> 
                    </div>
                </div>


            </div>
        )
    }
}


export default injectIntl(index);
