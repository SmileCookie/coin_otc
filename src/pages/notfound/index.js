import React from 'react';
import '../../assets/style/base/notfound.css'
import { FormattedMessage } from 'react-intl';


class NotFound extends React.Component {
    constructor(props){
        super(props)
        this.state = {

        }
    }
    render() {
        return (
            <div className="wrap">
                <div className="notcon">
                    <i className="icon-notcon"></i>
                    <p><FormattedMessage id="出错啦！您访问的网页不存在。" /></p>
                    <a href="/"><FormattedMessage id="访问首页" /></a>
                </div>
            </div>
        )
    }
}


export default NotFound;