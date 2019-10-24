import React from 'react';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';
import { DEFAULT_MARKETCOIN_TYPE,TIMER } from '../../conf'
import * as TradingView  from './charting_library.min.js'
import KlineConfig from './kline.config'
import KlineSelect from './klineSelect'
import './index.css';

class Kline extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      curIndex:0,
      timeVal:localStorage.getItem("tradingview.interval")||"15",
      styleVal:"1",
      isShow:0,
      klineSymbol:''
    }
    this.setKlineoptions = this.setKlineoptions.bind(this)
    this.timeOption = [
      {name:<FormattedMessage id="1分钟" />,val:1},
      {name:<FormattedMessage id="5分钟" />,val:5},
      {name:<FormattedMessage id="15分钟" />,val:15},
      {name:<FormattedMessage id="30分钟" />,val:30},
      {name:<FormattedMessage id="1小时" />,val:60},
      {name:<FormattedMessage id="4小时" />,val:240},
      {name:<FormattedMessage id="1天" />,val:"D"},
      {name:<FormattedMessage id="5天" />,val:"5D"},
      {name:<FormattedMessage id="1周" />,val:"W"},
      {name:<FormattedMessage id="1月" />,val:"M"}
    ]
    this.klineStyle = [
      {name:<FormattedMessage id="美国线" />,val:0,ele:<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26" width="26" height="26"><path d="M16 4v7h-3v2h3v7h2v-2h3v-2h-3V4h-2zM7 6v12H4v2h3v2h2V10h3V8H9V6H7z"></path></svg>},
      {name:<FormattedMessage id="k线图" />,val:1,ele:<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26" width="26" height="26"><path d="M16 3v3h-2v12h2v5h1v-5h2V6h-2V3h-1zM9 4v5H7v11h2v3h1v-3h2V9h-2V4H9zm-1 6h3v9H8v-9z"></path></svg>},
      {name:<FormattedMessage id="空心k线图" />,val:9,ele:<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26" width="26" height="26"><path d="M16 3v3h-2v12h2v5h1v-5h2V6h-2V3h-1zM9 4v5H7v11h2v3h1v-3h2V9h-2V4H9zm6 3h3v10h-3V7zm-7 3h3v9H8v-9z"></path></svg>},
      {name:<FormattedMessage id="平均k线图" />,val:8,ele:<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26" width="26" height="26"><path d="M16 3v3h-2v12h2v5h1v-5h2V6h-2V3h-1zM9 4v5H7v11h2v3h1v-3h2V9h-2V4H9z"></path></svg>},
      {name:<FormattedMessage id="线形图" />,val:2,ele:<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 595.3 841.9" enableBackground="new 0 0 595.3 841.9" width="26" height="26"><path d="M142.5 447.4c-102.74 102.74-83.44 83.44 0 0m399-186.2l-70.9 94.6H370.1l-136 159.6-91.6-112.3-38.5 41.4L6.5 542l41.4 41.4 94.6-94.6 91.6 112.3 162.6-186.2h103.5l88.7-118.2z"></path></svg>},
      {name:<FormattedMessage id="面积图" />,val:3,ele:<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 595.3 841.9" enableBackground="new 0 0 595.3 841.9" width="26" height="26"><path d="M453.8 360.2l-92.1 6L234 517.7l-86.2-86.1L5.2 514.7v118.9h585.3V268.1z" opacity=".3"></path><path d="M234 508.8c-156 222.067-78 111.033 0 0zm309-276.3l-71.3 95.1h-101L236.9 488l-92.1-112.9-38.6 41.6-101 98.1 41.6 41.6 95.1-95.1L234 574.2 397.4 387h104l89.1-118.9z"></path></svg>},
      {name:<FormattedMessage id="基准线" />,val:10,ele:<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 28 28" width="26" height="26"><g fill="none" stroke="currentColor"><path strokeDasharray="1,1" d="M4 14.5h22"></path><path strokeLinecap="round" strokeLinejoin="round" d="M7.5 12.5l2-4 1 2 2-4 3 6"></path><path strokeLinecap="round" d="M5.5 16.5l-1 2"></path><path strokeLinecap="round" strokeLinejoin="round" d="M17.5 16.5l2 4 2-4m2-4l1-2-1 2z"></path></g></svg>},
    ]
    
    this.klineBox = React.createRef()
  }
  componentDidMount() {
    const { scrIndex,klineCoin } = this.props
    if(scrIndex){
       this.setState({
         curIndex:scrIndex,
      },() => this.setKlineoptions(klineCoin))
    }else{
      this.setKlineoptions(klineCoin)
    }
  }

  setKlineoptions(coin){
      const { curIndex } = this.state
      const klineSymbol = coin || this.props.pathName || DEFAULT_MARKETCOIN_TYPE
      if(klineSymbol){
        let widget = window[`tvWidget_${curIndex}`] = new TradingView.widget(KlineConfig(klineSymbol,curIndex,this.props.skin));
        widget.onChartReady(function() {
          widget.chart().createStudy('Moving Average', false, false, [5], null, {'Plot.linewidth': 1,'Plot.color':'#5BC59A'});
          widget.chart().createStudy('Moving Average', false, false, [10], null, {'Plot.linewidth': 1,'Plot.color':'#B7664E'});
          widget.chart().createStudy('Moving Average', false, false, [30], null, {'Plot.linewidth': 1,'Plot.color':'#B5B550'});
          widget.chart().createStudy('Moving Average', false, false, [60], null, {'Plot.linewidth': 1,'Plot.color':'#4675CA'});
        })
        this.setState({
          klineSymbol
        })
      }
  }
  
  componentWillReceiveProps(nextProps){
    if(this.props.klineCoin != nextProps.klineCoin){
      this.setKlineoptions(nextProps.klineCoin)
    }
    if(this.props.marketinfo.currentMarket != nextProps.marketinfo.currentMarket){
        if(Object.keys(TIMER).length&&TIMER[this.props.marketinfo.currentMarket]){
          const prveTime = Object.values(TIMER[this.props.marketinfo.currentMarket]);
          for(let val of prveTime){
           clearInterval(val)
          }
        }
        this.setKlineoptions(nextProps.marketinfo.currentMarket)
    }
    const { curIndex } = this.state
    if(this.props.skin != nextProps.skin){
      if(nextProps.skin == 'light'){
        window[`tvWidget_${curIndex}`].addCustomCSSFile('light.css')
        window[`tvWidget_${curIndex}`].applyOverrides({
          "paneProperties.background": "#FFFFFF",
          "paneProperties.horzGridProperties.color": "#f9f9fc",
        })
      }else{
        window[`tvWidget_${curIndex}`].addCustomCSSFile('night.css')
        window[`tvWidget_${curIndex}`].applyOverrides({
          "paneProperties.background": "#121418",
          "paneProperties.horzGridProperties.color": "#3D4454",
        })
      }
    }
     
  }

  componentWillUnmount(){
      const { curIndex,klineSymbol } = this.state
      window[`tvWidget_${curIndex}`] = null;
      const prveTime = Object.values(TIMER[klineSymbol]);
      for(let k of prveTime){
          clearInterval(k)
      }
  }
  //设置 K线图周期
  setResolution = (val) => {
    const { curIndex } = this.state
    const klineSymbol = this.props.klineCoin || this.props.pathName || DEFAULT_MARKETCOIN_TYPE
    window[`tvWidget_${curIndex}`].setSymbol(klineSymbol,val, () => {
      this.setState({
        timeVal:val
      })
      localStorage.setItem("tradingview.interval",val)
    })
  }
  //setChartType设置 K线图周期
  setChartType = (val) => {
    const { curIndex } = this.state
    window[`tvWidget_${curIndex}`].chart().setChartType(val)
    this.setState({
      styleVal:val
    })
  }
  //指标
  setIndicator = () => {
    const { curIndex } = this.state
    window[`tvWidget_${curIndex}`].chart().executeActionById("insertIndicator")
  }
  //设置
  setInstall = () => {
    const { curIndex } = this.state
    window[`tvWidget_${curIndex}`].chart().executeActionById("chartProperties")
  }
  //全屏
  setFullscren = (e) => {
    const { curIndex } = this.state
    let el=document.getElementById(`kline-${curIndex}`);
    let isFullscreen=document.fullScreen||document.mozFullScreen||document.webkitIsFullScreen;
    if(!isFullscreen){//进入全屏,多重短路表达式
      (el.requestFullscreen&&el.requestFullscreen())||
      (el.mozRequestFullScreen&&el.mozRequestFullScreen())||
      (el.webkitRequestFullscreen&&el.webkitRequestFullscreen())||(el.msRequestFullscreen&&el.msRequestFullscreen());
    }else{//退出全屏,三目运算符
      document.exitFullscreen?document.exitFullscreen():
      document.mozCancelFullScreen?document.mozCancelFullScreen():
      document.webkitExitFullscreen?document.webkitExitFullscreen():'';
    }
  }
  
  render() {
    const { curIndex, timeVal, styleVal, isShow } = this.state
    const { styleClass } = this.props
    // console.log(this.state)
    return (
      <div className ={`kline ${styleClass?styleClass:''}`} id={`kline-${curIndex}`} ref={this.klineBox}>
        <div className="kline_top">
          <KlineSelect 
            options = {this.timeOption} 
            selectVal = {timeVal}
            Cb = {this.setResolution}
            className="kline-select-type-bor"
            />
          <KlineSelect 
            options = {this.klineStyle} 
            selectVal = {styleVal}
            Cb = {this.setChartType}
            className="kline-select-type-style"
            />
          <span onClick={this.setIndicator} className="time-button iconfont icon-zhibiao indicator"></span>
          <span onClick={this.setInstall} className="time-button iconfont icon-shezhi1 set"></span>
          <span onClick={this.setFullscren} className="time-button iconfont icon-quanping1 fullscreen" ></span>
        </div>
        <div id = {`kline-wrap-${curIndex}`} className="kline-wrap"></div>
      </div>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
    return {
        marketinfo: state.marketinfo,
        locale: state.language.locale,
        skin: state.trade.skin
    };
}

const mapDispatchToProps = (dispatch) => {
    return {
      
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Kline);