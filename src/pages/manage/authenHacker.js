import React from 'react';
import { Link } from 'react-router'
import { formatURL, formatDate } from "../../utils"
import { FormattedMessage } from 'react-intl';
import Form from '../../decorator/form';
import axios from 'axios';
import qs from 'qs';
import { DOMAIN_VIP } from '../../conf';
import { connect } from 'react-redux';

@connect(
    state => ({
        lng: state.language.locale
    })
)
@Form
class AuthenHacker extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            date: '',
            detail: '',
            isBlack: '',
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+'/manage/user', qs.stringify({})).then(r => {
            const {reason, lockTime, isBlack} = r.data.datas;
            this.setState({
                date: lockTime,
                detail: reason,
                isBlack,
            })
        })
    }
    
    render(){
        const { formatMessage } = this.intl;
        const { isBlack } = this.state;
        return (
            <div className="content">
                <div className="authen">
                    <div className="authen-fail">
                        <p className="con-title" style={{textAlign:'center'}}>
                            <svg className="icon icon24" aria-hidden="true"><use xlinkHref="#icon-renzhengshibai"></use></svg>
                            {formatMessage({id: '认证失败，请尝试重新认证。'})}
                        </p>
                        <p className="con-reason">{formatMessage({id: '详细信息：'})}<br />{formatMessage({id: '失败原因：'})}{this.state.detail}</p>
                        <Link className="next-btn dn" style={{margin:'0 auto'}}>{formatMessage({id: '重新认证'})}</Link>
                        <p className="user_auth_explain" style={{margin:0,width:'auto',textAlign:'center'}}>
                        {
                            !isBlack
                            ?
                            <span>
                            <FormattedMessage id="user.text130"/>{formatDate(this.state.date, this.props.lng === 'en' ? 'MM-dd, yyyy at hh:mm:ss' : undefined)}<FormattedMessage id="user.text131"/>
                            </span>
                            :
                            <FormattedMessage id="user.text132"/>
                        }
                        </p>
                    </div>
                </div> 
            </div>        
        )
    }
}

export default AuthenHacker;