import React from "react";
import {connect} from 'react-redux';
import { FormattedMessage,injectIntl } from 'react-intl';
import {pushGlobalTips,popGlobalTips} from "../../redux/module/tips";
import Tips from '../../assets/image/otcTip.png' 

import './globalTip.less';
import {Link} from "react-router-dom";

/**
 * 全局消息提示
 * pushGlobalTips 推送消息 params String
 * popGlobalTips  删除消息
 */

@connect(
    (state) => ({
        msgList: state.tips.globalMsgList
    }),
    {pushGlobalTips,popGlobalTips}
)
class GlobalTip extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            msgList:[]

        }
    }

    componentWillMount() {

    }

    componentDidMount() {
        this.setState({
            msgList:[...this.props.msgList]
        })
    }

    componentDidUpdate() {
        this.popMsg()
    }

    componentWillReceiveProps(nextProps, nextContext) {
        this.setState({
            msgList:[...nextProps.msgList]
        })
    }

    popMsg = () => {
        let {msgList} = this.props,
            t;
        console.log('pop msg')
        if (msgList.length > 0){
         t = setTimeout(() =>{
                this.props.popGlobalTips()
            },4100)
        }else{
            clearTimeout(t);
        }

    }

    render() {
        let {msgList}= this.state;
        // let {msgList}= this.props;
        // console.log("render",msgList)
        const {formatMessage} = this.props.intl;
        return (
            <div className="otc-tip">
                <ul className="tip-ul">
                    {
                        msgList.length > 0 ?
                            msgList.map((v, index) => {
                                return (
                                    <li key={index} id={index + new Date().getTime()}>
                                        <a className="tipsLinks" href={`/otc/orderDetail/${v.extra}`}> <span> <img style={{width:'18px',paddingBottom: "3px"}} src={Tips} alt=""/> </span>{formatMessage({id:v.content})}</a>
                                    </li>
                                )
                            })
                    :
                    null
                    }
                </ul>
            </div>
        )
    }
}

export default injectIntl(GlobalTip)