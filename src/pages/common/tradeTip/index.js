import React from 'react'
import ReactDOM from 'react-dom'
import { requsetWebsocket } from '../../../utils'
import { connect } from 'react-redux'
import cookie from 'js-cookie'
import { FormattedHTMLMessage} from 'react-intl';
import './index.less'

class TradeTip extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            tipStr:[],
            type:"coin"
        }
        this.ts = 0
        this.setTredeSucNum = this.setTredeSucNum.bind(this)
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
    
    //创建
    createRequset(){
        //future_tarde_tip
        let sessionId = cookie.get("zsessionId")
        const url = "market.order.deal";
        requsetWebsocket(
            { "event": "sub","channel": url,"params":{"token":sessionId}},
            (res) => {
                if(res.code == '0000'){
                    this.asyncCode = true;
                }

                if(this.asyncCode && res.datas){
                    this.setTredeSucNum(res.datas)
                }
            },
            this
        )
    }
    //关闭弹窗
    closeTradeTip(){
        // console.log("webkitAnimationEnd")
        // const nowTime = new Date().getTime();
        this.setState({
            tipStr:null
        }) 
        // if(nowTime - this.ts >= 4000){
        //     this.setState({
        //         tipStr:null
        //     }) 
        // }
        
    }

    setTredeSucNum(res){
        
        const {tipStr} = this.state
        const tiparr = tipStr?tipStr:[]
        if(res&&res.ts&&this.state.ts != res.ts){
            const tipStr = [...tiparr,res]
            this.ts = new Date().getTime();
            this.setState({
                tipStr
            })
        }
    }

    render(){
        const { tipStr } = this.state
        return(
            <div className={`trade-tip trade-tip-${this.props.skin}`} >
                <div id="r-pop">
                    <svg className="ep" aria-hidden="true"><use xlinkHref="#icon-zhucewenanicon"></use></svg><div id="up-r-pop-msg"></div>
                </div>
                <div style={{clear:'both'}}></div>
                <div style={{position:'relative'}}>
                {tipStr&&tipStr.length>0?tipStr.map((item,index) => {
                    return(
                        <div className={`trade-tip-item trade-tip-fadeIn ${this.props.lang=='en'?'trade-tip-item-big':''}`} key={`${item.ts}-${index}`}>
                            <div className="trade-tip-item-place">
                                <svg className="icon" aria-hidden="true">
                                    <use xlinkHref="#icon-tijiaorenzheng"></use>
                                </svg>
                                {
                                    item.countPlace == 1?
                                    <FormattedHTMLMessage id="您的1笔委托单已成交" />
                                    :
                                    <FormattedHTMLMessage id="您的N笔委托单已成交" values={{num:item.countPlace}}/>
                                }
                            </div>
                            <div className="trade-tip-item-deals">
                                <FormattedHTMLMessage id="成交笔数N笔" values={{num:item.countDeals}}/>
                            </div>
                            <i onClick={this.closeTradeTip} className="iconfont icon-guanbi-yiru closedICoin"></i>
                        </div>
                    )
                    
                }):"" }
                </div>

                
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


export default connect(mapStateToProps, mapDispatchToProps)(TradeTip)













































