import React from 'react'
import { Link } from "react-router"
import { formatURL } from "../../utils"

import Form from '../../decorator/form';

@Form
class AuthenOne extends React.Component{
    constructor(props){
        super(props)
    }

    render(){
        const { formatMessage } = this.intl;

        return (
            <div className="content">
                <div className="authen">
                    <div className="authen-one">
                        <ul>
                            <li><p style={{display:'none'}}></p><p style={{marginLeft:0,fontSize:'16px'}}>{formatMessage({id: "声明"})}:</p></li>
                            <li><p></p><p>{formatMessage({id: "此认证过程旨在确认您的身份，保护您的身份免于被盗用。"})}</p></li>
                            <li><p></p><p>{formatMessage({id: "认证信息一经验证不能修改，请务必如实填写；"})}</p></li>
                            <li><p></p><p>{formatMessage({id: "平台仅支持满18周岁的用户进行交易。"})}</p></li>
                            <li><p></p><p>{formatMessage({id: "因提供虚假信息所造成的一切后果将由您本人承担。"})}</p></li>
                            <li><p></p><p>{formatMessage({id: "身份认证通过后，您的每日提现总额将提升至50 BTC"})}</p></li>
                            <li><p></p><p>{formatMessage({id: "认证审核时间：24小时内。"})}</p></li>
                        </ul>
                        <Link className="next-btn" style={{margin: '60px auto'}} to={formatURL('authenTwo')}>{formatMessage({id: "开始验证"})}</Link>
                    </div>
                </div>
            </div>        
        )
    }
}
export default AuthenOne