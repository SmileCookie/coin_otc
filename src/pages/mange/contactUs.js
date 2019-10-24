import React from 'react';
import { Link } from 'react-router';
import { formatURL } from "../../utils"

import Form from '../../decorator/form';

@Form
class ContactUs extends React.Component{
    constructor(props){
        super(props)
    }

    render(){
        const { formatMessage } = this.intl;

        return (
            <div className="content">
                <div className="contack-us">
                    <div className="con-section">
                        <div className="sec-title">{formatMessage({id: "一般支持:"})}</div>
                        <div className="sec-content">{formatMessage({id: "您在Hitman遇到任何疑问或异常，都可以通过以下邮箱联系我们。"})}</div>
                        {/* <Link className="sec-email text-btn" to={formatURL('')}>support@btcwinex.com</Link> */}
                        <span>support@btcwinex.com</span>
                    </div>
                    <div className="con-section">
                        <div className="sec-title">{formatMessage({id: "意见建议:"})}</div>
                        <div className="sec-content">{formatMessage({id: "您对Hitman有任何意见或建议，都可以通过以下邮箱联系我们。"})}</div>
                        {/* <Link className="sec-email text-btn" to={formatURL('')}>support@btcwinex.com</Link> */}
                        <span>support@btcwinex.com</span>
                    </div>
                </div>
            </div>                        
        )
    }
}
export default ContactUs