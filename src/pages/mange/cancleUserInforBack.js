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
            storeReason:''
        };


    }

    componentWillMount(){
        // if(this.props.location.query.statu !== 2){
        //     browserHistory.push('/bw/mg/account')
        // }
    }

    componentDidMount(){
       this.checkUserInfors()

    }

    // 获取用户是否商家认证拒绝原因
    checkUserInfors = () =>{
        axios(DOMAIN_VIP + '/manage/auth/authenticationJson').then(res =>{
            if(res.status == 200){
                let {storeStatus,storeReason} = res.data.datas
                this.setState({
                    storeReason,

                })
            }

        })
    }

    render(){
        const { formatMessage } = this.props.intl;
        const{storeReason} = this.state;
        return (
            <div className="cancelUserInfor" style={{paddingBottom: '60px',padding: "80px 0 60px 200px"}}>
                    <div className="checking">
                        <span className="imgs">
                            <img style={{width:'24px'}} src={shibai} alt=""/>
                        </span>
                        <span className="textInfor">
                            <FormattedMessage id="您的取消认证申请审核失败，请重新尝试"/>
                        </span>
                        <div className="cancleInfor">
                            <p>
                                <FormattedMessage id="失败原因"/>:<span>{storeReason}</span>
                            </p>
                        </div>
                        <div className="sendBtn">
                            <input type="button" onClick={() =>{browserHistory.push(`/bw/mg/cancleUserInfor`)}} value={formatMessage({id:'重新提交'})}/>
                        </div>
                    </div>
            </div>
        )
    }
}

export default withRouter(injectIntl(CancelUserInfor));
