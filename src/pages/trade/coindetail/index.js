import React from 'react'
import { connect } from 'react-redux'
import ScrollArea from 'react-scrollbar'
import { FormattedMessage, injectIntl } from 'react-intl';
import ReactModal from '../../../components/popBox';
import { fetchCoinDetail } from '../../../redux/modules/trade'
import { DEFAULT_MARKETCOIN_TYPE ,ERRORCONFIG} from '../../../conf'
import cookie from 'js-cookie'
import './index.less'

class CoinDetail extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            Mstr:'',
            coinFullName:'',
            coinName:'',
            introduction:'',
            coinContent:'',
            dataError:false
        }
        this.splitNum = this.splitCoinDetail();
        this.coinDetailCon = this.coinDetailCon.bind(this)
        this.splitCoinDetail = this.splitCoinDetail.bind(this)
    }

    componentDidMount(){
        const currentMarket = this.props.currentMarket || DEFAULT_MARKETCOIN_TYPE
        const coin = currentMarket.split('_')[0].toUpperCase()
        const lang = cookie.get("zlan")
        this.props.fetchCoinDetail(coin,lang)
    }
    
    componentWillReceiveProps(nextProps){
        if(this.props.currentMarket&&(nextProps.currentMarket != this.props.currentMarket)){
            const coin = nextProps.currentMarket.split('_')[0].toUpperCase()
            const lang = cookie.get("zlan")
            this.props.fetchCoinDetail(coin,lang)
        }
    }
    //格式化数据
    formatStr = (str="") => {
        let newStr = '';
        for(let i=0;i<str.length;i++){
            if(str[i]=="\\"){
                newStr += "";
                continue;
            }
            newStr += str[i];
        }
        return newStr;
    }
    //截取文字数目
    splitCoinDetail(){
        const bodyHei = document.body.clientHeight;
        if(this.props.lang == "en"){
            if(bodyHei<=870){
                return 35;
            }else if(bodyHei<=940){
                return 85
            }else{
                return 120
            }
        }else{
            if(bodyHei<=870){
                return 25;
            }else if(bodyHei<=940){
                return 45
            }else{
                return 65
            }
        }
    }
    
    coinDetailCon(){
        const { coinNameJson,coinFullNameJson,coinContentJson } = this.props.coinIntro
        const Mstr = <div className="coin-detail-modal">
                        <div className="coin-detail-modal-header">
                            <h3>{coinFullNameJson}</h3>
                            <i className="iconfont icon-guanbi-moren" onClick={() => this.modal.closeModal()}></i>
                        </div>
                        <div className="coin-detail-modal-con" >
                            <ScrollArea className="scrollarea trade-scrollarea scrollarea-content">
                                <div className="coin-detail-modal-con-pad" dangerouslySetInnerHTML={{__html:this.formatStr(coinContentJson)}}>
                                </div>
                            </ScrollArea>
                        </div>
                     </div>
        this.setState({
            Mstr
        })
        this.modal.openModal();
    }
    componentDidCatch(){
        if(window.ERRORCONFIG){
            this.setState({
                dataError:true
            })
            // console.log(err,infor)
        }else{
            // console.log(err,infor)
        }
    }
    render(){
        //throw new Error(2222)
        if(this.state.dataError){
            return (
                <div className="coin-detail">
                    <div className="iconfont icon-jiazai new-loading"></div>
                </div>)
        } 
        const { coinIntro } = this.props
        const { Mstr } = this.state
        // let _p = `<P>打款收款就是打卡机大打卡机的卡的安静打了卡巨大的大家看到了觉得可垃圾的肯德基的金坷垃京东卡上的按揭贷款啦打卡机打开暗示经典款拉沙京东卡时间段卡速度快阿达暗示打手机打卡时间到卡上打算大卡司的卡萨丁暗示的阿达可视对讲卡仕达的就开始打的</P>`
        //  let _p = `<P>As an important part of the planning system of Xiongan New Area, the planning, led by the Research Center of Ecological Environment of the Chinese Academy of Sciences, provides scientific support for the ecological restoration and environmental protection of Baiyangdian Lake, and also lays the ecological foundation for the sustainable development of Xiongan New Area.</P>`

        return(
            // <div className="coin-detail space-margin-bot4">
            <div className="coin-detail">
            {
                false
                &&
                <h3><FormattedMessage id="币种介绍" /></h3>
            }
                { coinIntro&&
                    <React.Fragment>
                        <div className="coin-detail-name">
                            <h4><span className="up-cnj">{coinIntro.coinNameJson}</span><span className="up-cnjed">{coinIntro.coinFullNameJson?' ('+coinIntro.coinFullNameJson+')':''}</span></h4>
                        </div>
                        <div className="coin-detail-con">
                            <div className="word" dangerouslySetInnerHTML={{__html:this.formatStr(coinIntro.introductionJson)}}></div>
                            {/* <div className="word" dangerouslySetInnerHTML={{__html:_p}}></div> */}
                            <a href="javascript:void(0)" onClick={this.coinDetailCon}><FormattedMessage id="查看更多" /> ></a>
                        </div>
                    </React.Fragment>
                }
                <ReactModal ref={modal => this.modal = modal}>
                    { Mstr }
                </ReactModal>
            </div>
        )
    }
}

const mapStateToProps = (state,ownProps) => {
    return {
        currentMarket:state.marketinfo.currentMarket,
        coinIntro:state.trade.coinIntro,
        lang:state.language.locale
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        fetchCoinDetail:(coin,lang) => {
            dispatch(fetchCoinDetail(coin,lang))
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CoinDetail)

