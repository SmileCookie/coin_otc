import React from 'react';
import { browserHistory } from 'react-router';
import { formatURL } from '../../utils';
import { injectIntl } from 'react-intl';
import { Link } from 'react-router';
import '../../assets/css/userauth.less';

class FinishedReg extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            timer: 3  
        };

        this.doTime = this.doTime.bind(this);
    }

    doTime(){
        let { timer } = this.state;

        if(timer > 0){
            setTimeout(() => {
                this.setState({
                    timer: --timer
                });
                this.doTime();
            }, 1000);
        } else {
            browserHistory.push(formatURL('login'));
        }
    }
    componentDidMount(){
        this.doTime();
    }


    render(){
        const { formatMessage } = this.props.intl,
              { timer } = this.state;
        return (
            <div className="uauth_wp min_h_d clearfix">
                <div className="l lsp2">
                    <div className="fdwp plv">
                        <h2 className="tith">{formatMessage({id: "激活成功"})}</h2>
                        <em className="iconfont">&#xe6a1;</em>
                    </div>
                    <ul className="list">
                        <li className="lst3x">
                            <h3>
                            {
                                formatMessage({id: "将在%%秒后跳转至登录页面。"}).replace('%%', timer)
                            }
                            </h3>
                        </li>
                    </ul>
                    <div className="subs">
                        <Link className="i3 v" to={formatURL("login")}>{formatMessage({id: "立即跳转"})}</Link>
                    </div>
                </div>
            </div>
        );
    }
}

export default injectIntl(FinishedReg);