import React from 'react';
import axios from 'axios';
import { FormattedMessage,FormattedHTMLMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import {DOMAIN_VIP} from '../../conf';
const BigNumber = require('big.js');

class Repoitemred extends React.Component {
    constructor(props){
        super(props)

        this.state = {
            instShow:false,
            time:"",
            width:0,
            loaded:false,
            red:{
                balance:0, //分红地址ABCDE余额
                address:"0x00000 ......", //分红地址
                webUrl:"####", //查看链接
                number:0, //分红次数
                frequency:0, //转出频率，单位秒
                countDown:0 //倒计时，单位分钟，如果小于1，显示剩余1分钟
            }
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
        axios.get(DOMAIN_VIP+"/backcapital/dividend/getDividendInfo")
            .then(res=>{
                let data = eval(res['data']).datas;
                // console.log(data);
                let time = "";
                let timeO = data.countDown
                if(timeO<0){timeO=data.frequency}
                
                let tian=Math.floor(timeO/(60*24)),shi=Math.floor(timeO/60),fen = timeO<=1?1:timeO;                
                let percent = new BigNumber(data.frequency).minus(timeO).div(data.frequency).times(100);
                if(tian>0){
                    time="≈"+tian+" DAYS";
                }else{
                    time="≈1 DAYS";
                } 
                // else if(shi>0){
                //     time="≈"+shi+" HOURS";
                // }else{
                //     time="≈"+fen+" MINUTES";
                // }
                this.setState({
                    width:percent+"%",
                    time:time,
                    loaded:true,
                    red:{
                        balance:new BigNumber(data.balance).toFixed(2), //分红地址ABCDE余额
                        address:data.address, //分红地址
                        webUrl:data.webUrl, //查看链接
                        number:data.number, //分红次数
                        frequency:data.frequency, //转出频率，单位秒
                        countDown:data.countDown, //倒计时，单位分钟，如果小于1，显示剩余1分钟
                    }
                })
            })
    }

    render() {
        let icon=<i className="iconfont">&#xe672;</i>;
        if(!this.state.loaded){
            return  <div className={"repo-item red"}>
                        <div className="clearfix">
                            <div className="icon">
                            {icon}
                            </div>
                            <div className="repo_info">
                                <h2>Loading</h2>
                            </div>
                        </div>
                    </div>
        }

        let infoBottom = <p><FormattedMessage id="repostati-c-p1"/>: {this.state.red.address} &nbsp;<a href={this.state.red.webUrl} target="new"><FormattedMessage id="repostati-c-p2"/></a></p>;
        let timeTitle = <h4><FormattedMessage id="repostati-c-h3" values={{num:this.state.red.number}}/></h4>;
        let insthtml = <div className="inst_detail">
                        <p><FormattedHTMLMessage id="repostati-c-info" values={{url:this.state.red.webUrl}}/></p>
                        <botton onClick={()=>{this.setInstShow(0)}}><FormattedMessage id="repostati-button"/></botton>
                       </div>;
        return (
            <div className={"repo-item red"}>
                <div className="clearfix">
                    <div className="icon">
                    {icon}
                    </div>
                    <div className="repo_info">
                        <h3><FormattedHTMLMessage id="repostati-c-h2"/><em><i className="iconfont" onClick={()=>{this.setInstShow(1)}}>&#xe657;</i>{this.state.instShow?insthtml:""}</em></h3>
                        <h2>{this.state.red.balance}<span>ABCDE</span></h2>
                        <div className="infobottom">{infoBottom}</div>
                    </div>
                    <div className="time_box">
                        {timeTitle}
                        <div className="dead_time">
                            {this.state.time}
                        </div>
                        <div className="dead_line">
                            <span style={{width:this.state.width}}></span>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}


export default Repoitemred;