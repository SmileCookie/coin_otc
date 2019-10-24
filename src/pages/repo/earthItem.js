import React from 'react';
import axios from 'axios';
import { FormattedMessage,FormattedHTMLMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import {DOMAIN_VIP} from '../../conf';
const BigNumber = require('big.js');
class Earthitem extends React.Component {
    constructor(props){
        super(props)
        // this.runTime = this.runTime.bind(this);
        this.state = {
            instShow:false,
            width:"0%",
            burnedAmount:0, //全球已燃烧
            shareCount:0, //已转化股份
            lastPerShareAmount:0, //最近一次分红每股获得ABCDE数量
            lastDividendAmount:0, //最近一次分红量
            totalDividendAmount:0, //累计分红量
            privateKeyCoords:[] //私钥分布坐标，待确认
        }
    }
    componentDidMount() {
        let _this = this;
        _this.dispatchRed();
        _this.interval = setInterval(function(){
            // console.log("dis:red")
            _this.dispatchRed();
        },60000)
    }
    componentWillReceiveProps(nextProps){
            
    }
    componentWillUnmount() {
        clearInterval(this.interval)
    }
    setInstShow(n){
        if(n==0){
            this.setState(
               {instShow:false}
            )
        }else{
            this.setState(
                {instShow:true}
            )
        }
    }
    dispatchRed(){
        BigNumber.RM = 0;
        axios.get(DOMAIN_VIP+"/backcapital/dividend/getDividendHistory")
            .then(res=>{
                let data = eval(res['data']).datas;
                // console.log(data);
                let frequency = 10000000;
                                
                let percent = new BigNumber(data.burnedAmount).div(frequency).times(100);
                
                this.setState({
                    width:percent+"%",
                    burnedAmount:Math.floor(data.burnedAmount), //全球已燃烧
                    shareCount:Math.floor(data.shareCount), //已转化股份
                    lastPerShareAmount:new BigNumber(data.lastPerShareAmount).toFixed(2), //最近一次分红每股获得ABCDE数量
                    lastDividendAmount:new BigNumber(data.lastDividendAmount).toFixed(2), //最近一次分红量
                    totalDividendAmount:new BigNumber(data.totalDividendAmount).toFixed(2), //累计分红量
                    privateKeyCoords:data.privateKeyCoords //私钥分布坐标，待确认
                
                })
            })
    }
    render() {
        let insthtml = <div className="inst_detail">
                            <p><FormattedHTMLMessage id="repostati-d-info"/></p>
                            <botton onClick={()=>{this.setInstShow(0)}}><FormattedMessage id="repostati-button"/></botton>
                        </div>;
        return (
            <div className="repo-item earth_box">
                <div className="clearfix">
                    <div className="mapbox">
                        <div className="map_bg">
                        {this.state.privateKeyCoords.map((data,index)=>{
                            let dat = data.split(",");
                            return <i key={index} style={{left:dat[0]+"px",top:dat[1]+"px"}}></i>
                        })}
                        </div>
                        <p><FormattedMessage id="repostati-d-h"/></p>
                    </div>
                    <div className="mapdetail">
                        <h3><i className="iconfont">&#xe66c;</i> <FormattedMessage id="repostati-d-h1"/> <em><i className="iconfont" onClick={()=>{this.setInstShow(1)}}>&#xe657;</i>{this.state.instShow?insthtml:""}</em></h3>
                        <div className="line_box sp">
                            <div className="line"><span className="lineto" style={{width:this.state.width}}><em>{this.state.burnedAmount}</em></span></div>
                            <div className="clearfix"> <i>0 ABCDE</i> <i>10,000,000 ABCDE</i> </div>
                        </div>
                        <h3><i className="iconfont green">&#xe66f;</i> <FormattedMessage id="repostati-d-h2"/></h3>
                        <b className="gunum">{this.state.shareCount}</b>
                        <ul>
                            <li>
                                <p><FormattedMessage id="repostati-d-l1"/></p>
                                <em>{this.state.lastPerShareAmount} ABCDE</em>
                            </li>
                            <li>
                                <p><FormattedMessage id="repostati-d-l2"/></p>
                                <em>{this.state.lastDividendAmount} ABCDE</em>
                            </li>
                            <li>
                                <p><FormattedMessage id="repostati-d-l3"/></p>
                                <em>{this.state.totalDividendAmount} ABCDE</em>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        )
    }
}

export default Earthitem;