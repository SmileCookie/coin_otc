import React from 'react';
import { connect } from 'react-redux';
import { withRouter,Link } from 'react-router'
import { browserHistory } from 'react-router';
import axios from 'axios'
import { FormattedMessage, injectIntl } from 'react-intl';
import duigou  from '../../assets/images/duigou.png'
import shibai  from '../../assets/images/shibai.png'
import { JSEncrypt } from 'jsencrypt';
import {DOMAIN_VIP,SECRET} from '../../conf'
import {optPop} from '../../utils/index'
const BigNumber = require('big.js')

import qs from 'qs'
const encrypt = new JSEncrypt();

class CancelUserInfor extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            pwd:'',
            errInfor:'',
            storeFreez:'',
            statu :null,
            storeReason:'',
            isSendMsg:false,
        };
        this.pwdRef = React.createRef();

    }

    componentWillMount(){
        // if(this.props.location.query.statu !== 1){
        //     browserHistory.push('/bw/mg/account')
        // }
        this.checkUserInfors()
    }

    componentDidMount(){
       this.getFrezMoney()
       //this.checkUserInfors()

    }

    // 获取用户是否商家认证状态
    checkUserInfors = async () =>{
       let data =  await axios(DOMAIN_VIP + '/manage/auth/authenticationJson').then(res =>{
            if(res.status == 200){
                return res.data.datas
            }
            else{
                return false
            }
        })
        if(data){
            let {storeStatus,storeType} = data
            if(storeStatus !== 2 && storeType == 2){
                browserHistory.push('/bw/mg/cancleUserInforIng')
            }else if(storeStatus !== 1 && storeType == 1){
                browserHistory.push('/bw/mg/account')
            }
        }
    }

    checkOut = (e,type = true)=>{
        const { formatMessage } = this.props.intl;
        let v = '';
        if(type){
            v = e.target.value;
        }else{
            v = e.current.value;
        }
        if(!v.trim()){
            this.setState({
                errInfor:formatMessage({id:'请输入资金密码'})
            })
            return false
        }else{
            return true
        }
    }

    changePwd = (e) =>{
        let pwd = e.target.value.trim();
        this.setState({
            pwd
        })
    }

    clearCheck = () =>{
        this.setState({
            errInfor:''
        })
    }

     //获取保证金
     getFrezMoney = () =>{
        axios.get(DOMAIN_VIP + '/manage/getAssetsOtcDetail').then((res) =>{
            if(res.status == 200){
                let data = eval(res["data"])
                //console.log(data.USDT);
                let {storeFreez}  = data.USDT;
                  this.setState({
                    storeFreez,
                  })

            }

        })
    }


    sendPwd = () =>{
        const {pwd,errInfor} = this.state
        let _this = this
        // console.log(this.pwdRef);

        if(!this.checkOut(this.pwdRef,false)){
            return false
        }
        this.setState({
            isSendMsg:true
        })
        encrypt.setPublicKey(SECRET);
        let _pwd = encrypt.encrypt(pwd)
        axios.post(DOMAIN_VIP + '/otcweb/web/v1/store/cancel',qs.stringify({safePwd:_pwd})).then(res =>{
                        if(res.data.code == 200){
                            this.setState({
                                isSendMsg:false
                            })
                            browserHistory.push('/bw/mg/cancleUserInforIng')
                            //optPop(() =>{},res.data.msg,{timer:1500})

                        }else{
                            _this.setState({
                                errInfor:res.data.msg,
                                isSendMsg:false
                            })
                        }
                    })

        // axios.get(DOMAIN_VIP + "/login/getPubTag?t=" + new Date().getTime()).then(rp =>{
        //     if(rp && rp.data.isSuc){
        //         encrypt.setPublicKey(rp.data.datas.pubTag);
        //         let _pwd = encrypt.encrypt(pwd)
        //         axios.post(DOMAIN_VIP + '/otcweb/web/v1/store/cancel',qs.stringify({safePwd:_pwd})).then(res =>{
        //             if(res.data.datas.code == 200){
        //                 browserHistory.push('/bw/mg/account')
        //             }else{
        //                 _this.setState({
        //                     errInfor:res.data.datas.msg
        //                 })
        //             }
        //         })
        //     }
        // })

    }
    render(){
        const { formatMessage } = this.props.intl;
        const {pwd,errInfor,storeFreez,statu,storeReason,isSendMsg} = this.state
        return (
            <div className="cancelUserInfor" style={{paddingBottom: '60px',padding: "80px 0 60px 200px"}}>
                    <div className="infors uauth_wp" style={{padding:'0'}}>
                        <p><FormattedMessage id="取消商家认证后，保证金会退还至法币账户余额中，当前保证金余额为" />: {storeFreez ? new BigNumber(storeFreez).toFixed(8) : 0.00000000} USDT</p>
                        <div className="inputMoney err">
                            <p><FormattedMessage id="资金密码" /></p>
                            <input ref={this.pwdRef} type="password" maxLength="20" placeholder={formatMessage({id:'请输入资金密码'})} value={pwd} onChange={(e) =>this.changePwd(e)} onFocus={(e) => this.clearCheck(e)} onBlur={(e) =>this.checkOut(e)} />
                        {
                            errInfor &&
                            <span className="ew" style={{bottom: '85%'}}>{errInfor}</span>
                        }
                            <div className="sendBtn">
                                {
                                    !isSendMsg ?
                                    <input type="button" onClick={this.sendPwd} value={formatMessage({id:'提交'})}/>
                                    :
                                    <div className="loadingSend">
                                        <em  className="iconfont ld">&#xe6ca;</em>
                                    </div>
                                }


                            </div>
                        </div>
                    </div>
            </div>
        )
    }
}

export default withRouter(injectIntl(CancelUserInfor));
