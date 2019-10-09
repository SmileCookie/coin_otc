import React from 'react'
import ReactDOM from 'react-dom'
import { connect } from 'react-redux'
import cookie from 'js-cookie'
import { injectIntl } from 'react-intl';
import { requsetWebsocket } from "../../utils";

import './index.less'

/**
 * 全局提示组件 Tips
 */

class Tips extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            tipsList: [], // 消息提示列表
            tipStr:[],
            type:"coin"
        }
        this.closeTradeTip = this.closeTradeTip.bind(this)
        this.createRequset = this.createRequset.bind(this)
    }

    componentDidMount(){
        if(this.props.user){
           this.createRequset()
        }
        ReactDOM.findDOMNode(this).addEventListener("webkitAnimationEnd",this.closeTradeTip);
    }

    componentWillUnmount(){
        ReactDOM.findDOMNode(this).removeEventListener("webkitAnimationEnd", this.closeTradeTip)
        this.ws.close()
    }

    componentDidUpdate(prevProps, prevState){
        if(prevState.type != this.state.type){
            this.ws.close()
            this.createRequset()
        }
    }

    //创建webSocket 链接
    createRequset(){
        let sessionId = cookie.get("zsessionId")
        const url = "market.order.deal";
        requsetWebsocket(
            { "event": "sub","channel": url,"params":{"token":sessionId}},
            (res) => {
                if(res.code == '0000'){
                    this.asyncCode = true;
                }
                this.pushTipToList(res.datas)
            },
            this
        )
    }
    //关闭弹窗
    closeTradeTip(){
        this.setState({
            tipStr:null
        })
    }
    // 消息入栈
    pushTipToList(item){
        this.setState(
            {
                tipsList: this.state.tipsList.push(item)
                })
    }
    // 消息出栈
    popTipFromList(item){
        if (this.state.tipsList.length > 0){
            this.setState(
                {
                    tipsList: this.state.tipsList.pop(item)
                })
        }
    }
    render(){
        const { tipsList } = this.state
        let  { formatMessage } = this.props.intl
        return(
            <div className={`trade-tip trade-tip-${this.props.skin}`} >
                <ul>
                    {
                        tipsList.map((item,index)=>{
                        return <li>
                                <p className="">
                                {
                                formatMessage({id:item.id})
                                }
                                </p>
                             </li>
                        })
                    }
                </ul>
            </div>
        )
    }
}

const mapStateToProps = (state,ownProps) => {
    return {
        user: state.session.user,
        skin:state.trade.skin,
        lang:state.language.locale
    };
}

const mapDispatchToProps = (dispatch) => {
    return {

    }
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(Tips))













































