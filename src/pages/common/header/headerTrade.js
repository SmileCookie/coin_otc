import React from 'react'
import { connect } from 'react-redux' 
import { fetchTotalTrade,modifyTradeBg,fetchOnlinePer } from '../../../redux/modules/trade'
import { FormattedMessage } from 'react-intl';
import { exChangeTradeper, numFm, } from '../../../utils'
import cookie from 'js-cookie';
import './header.less'
const BigNumber = require('big.js'); 
import FlipNumbers from "react-flip-numbers-os";

class HeaderTrade extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            btnName:'icon-dengguang-kaiguan-dark',
        }
        
        this.changePageSkin = this.changePageSkin.bind(this)
        this.mouseEnterorOver = this.mouseEnterorOver.bind(this)
        
    }
    
    componentDidMount(){
        if(this.props.skin!='dark'){
            this.setState({
                btnName:`icon-dengguang-kaiguan-${this.props.skin}`
            })
        }
        this.props.fetchTotalTrade()
        this.props.fetchOnlinePer()

        this.interval = setInterval(() => {
            //this.props.fetchTotalTrade()
            this.props.fetchOnlinePer()
        },3000)
    }

    componentWillReceiveProps(nextProps){
        if(this.props.skin != nextProps.skin){
            this.setState({
                btnName:`icon-dengguang-kaiguan-${nextProps.skin}`
            })
        }
    }

    componentWillUnmount(){
        clearInterval(this.interval)
    }

    //换肤
    changePageSkin(skinag = ''){
        const stateSkin = skinag?skinag:(this.props.skin =='dark'?'light':'dark');
        this.props.modifyTradeBg(stateSkin);

        // 清除换肤后body的样式
        // console.log('=======>', window.clearSkin);
        window.clearSkin();
    }
    
    // 移除 亮灯
    mouseEnterorOver(type){
        const { skin } = this.props
        if('light' === skin){
            return
        }
        if(type=="enter"){
            const stateSkin = skin =='dark'?'light':'dark';
            this.setState({
                btnName:`icon-dengguang-kaiguan-${stateSkin}`
            }) 
        }else{
            this.setState({
                btnName: `icon-dengguang-kaiguan-${skin}`
            })
        }
    }
    // 判断IE 切换 在线人数组件
    renderOnlineNums(){
        if (!window.ieFlag) {
            return (
                <FlipNumbers
                    height={13}
                    width={9}
                    color="#ffffff"
                    background="#121418"
                    play
                    perspective={0}
                    numbers={this.props.online + ''}
                    numberStyle={{width: '9px', fontSize: '14px'}}
                />
            );
        } else {
            return (
                <span>{this.props.online}</span>
            );
        }
    }

    render(){
        const volumeStr = numFm(this.props.volume)
        //const onlineUser = numFm(this.props.online,1)
        const onlineUser = {
            num: this.props.online,
            unit: '',
        };
        const { btnName } = this.state
        const { locale,rate } = this.props.money
        let legalMonNum = '0.00'
        if(rate.exchangeRateUSD){
            try{
            BigNumber.RM = 0;
            const curRate = rate['exchangeRateUSD'][locale.name] 
            legalMonNum = new BigNumber(volumeStr.num).times(curRate).toFixed(2)
            }
            catch(e){
                
            }
        }

        return(
            <div className="header-trade">
                <div className="header-trade-person">
                    <FormattedMessage id="在线人数"/>:&nbsp;
                    {
                        this.renderOnlineNums()
                    }
                </div>
                <div className="header-trade-num">
                <FormattedMessage id="当前交易量" />:&nbsp;<b>{locale.logo}&nbsp;{legalMonNum}</b>{volumeStr.unit}
                </div>
                <i 
                    className={`iconfont ${btnName}`} 
                    onMouseEnter={() => this.mouseEnterorOver('enter')}
                    onMouseLeave={() => this.mouseEnterorOver('leave')}
                    onClick={()=>{this.changePageSkin('light')}}
                ></i>

                <em className={`up-lg ${this.props.skin !== 'light' ? 'ac' : ''}`} onClick={()=>{this.changePageSkin('dark')}}></em>
            </div>
        )
    }   

}

const mapStateToProps = (state,ownProps) => ({
    volume:state.trade.volume,
    skin:state.trade.skin,
    online:state.trade.online,
    money:state.money,
})

const mapDispatchToProps = (dispatch) => {
    return {
        fetchTotalTrade:() => {
            dispatch(fetchTotalTrade())
        },
        fetchOnlinePer:() => {
            dispatch(fetchOnlinePer())
        },
        modifyTradeBg:(type) => {
            cookie.set("skin",type,{expires: 7})
            dispatch(modifyTradeBg(type))
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(HeaderTrade)























