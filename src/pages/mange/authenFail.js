import React from 'react';
import { Link } from 'react-router'
import { formatURL } from "../../utils"
import axios from 'axios';
import qs from 'qs';
import Form from '../../decorator/form';
import { DOMAIN_VIP } from '../../conf';

@Form
class AuthenFail extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            detail: ''
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP+'/manage/user', qs.stringify({})).then(r => {
            this.setState({
                detail: r.data.datas.reason
            })
        })
    }
    
    render(){
        const { formatMessage } = this.intl;
        return (
            <div className="content">
                <div className="authen">
                    <div className="authen-fail" >
                        <p className="con-title" style={{textAlign:'center'}}>
                            <svg className="icon icon24" aria-hidden="true"><use xlinkHref="#icon-renzhengshibai"></use></svg>
                            {formatMessage({id: '认证失败，请尝试重新认证。'})}
                        </p>
                        <p className="con-reason">{formatMessage({id: '详细信息：'})}<br />{formatMessage({id: '失败原因：'})}{this.state.detail}</p>
                        <Link className="next-btn" style={{margin:'0 auto'}} to={formatURL('authenTwo?go=1')}>{formatMessage({id: '重新认证'})}</Link>
                    </div>
                </div> 
            </div>        
        )
    }
}

export default AuthenFail