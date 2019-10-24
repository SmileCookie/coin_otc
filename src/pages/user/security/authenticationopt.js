import React from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router'; 
import { FormattedMessage } from 'react-intl';
import { formatDate, formatURL } from '../../../utils';

export default class AuthenticationOpt extends React.Component{
    static propTypes = {
        opt : PropTypes.number.isRequired,
        date: PropTypes.number,
        lng: PropTypes.bool
    }
    static defaultProps = {
        date: 0,
        lng: false
    }
    render(){
        
        let optStr = null;

        switch(+this.props.opt){
            case 0:
                optStr = (<div>
                    <span className="a_authtypeno"><FormattedMessage id="user.text58"/></span>
                    <p className="user_auth_explain"><i className="iconfont mr5">&#xe653;</i><FormattedMessage id="user.text132"/></p>
                </div>);
                break;

            case 1:
                optStr = (<div>
                <span className="a_authtypeno"><FormattedMessage id="user.text58"/></span>
                <p className="user_auth_explain"><i className="iconfont mr5">&#xe653;</i><FormattedMessage id="user.text130"/>{formatDate(this.props.date, this.props.lng ? 'MM-dd, yyyy at hh:mm:ss' : undefined)}<FormattedMessage id="user.text131"/></p>
                </div>);
                break;
            case 2:
                optStr =  (<Link className="a_authtype" to={formatURL('authtype')}><FormattedMessage id="user.text57"/></Link>);break;
            case 3:
                optStr =  (<Link className="a_authtype" to={formatURL('authtype')}><FormattedMessage id="user.text58"/></Link>);break;
        }


        return (
            <div>
                {optStr}
            </div>
        );
    }
} 