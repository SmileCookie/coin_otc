import React from 'react';
import { connect } from 'react-redux';
import { FormattedMessage,FormattedHTMLMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import axios from 'axios';
const BigNumber = require('big.js');
import Repoitem from './repoItem';
import Repoitemred from './repoItemRed';
import Earthitem from './earthItem';
import './repo.css';
import {DOMAIN_VIP} from '../../conf';
class Repo extends React.Component {
    constructor(props){
        super(props);
        this.dispatchBlue = this.dispatchBlue.bind(this);
        this.state = {
            blueInst:<p><FormattedHTMLMessage id="repostati-a-info"/></p>,
            
            balance:0,//平台ABCDE余额
            blueloaded:false,
            blue:{
                frequency:0, //回购频率，单位秒
                lastBackCapital:0, //最近一次回购量
                countDown:0, //倒计时，单位秒
                avgSpeed:0, //回购平均速度，单位 ABCDE/M
            },
            yellowloaded:false,
            yellow:{
                totalBalance:0, //累积回购ABCDE量
                frequency:0, //转出频率，单位秒
                countDown:0, //倒计时，单位秒
                lastWithdraw:0 //最近一次转出量
            }
        }
    }
    componentDidMount() {
        let _this = this;
        this.dispatchBlue();
    }
    dispatchBlue(){
        BigNumber.RM = 0;
        axios.get(DOMAIN_VIP+"/backcapital/dividend/bcCountDown")
            .then(res=>{
                if(eval(res['data']).isSuc){
                    let data = eval(res['data']).datas;
                    // console.log(data);
                    this.dispatchYellow(()=>{
                        this.setState({
                            blueloaded:true,
                            blue:{
                                frequency:data.frequency, 
                                lastBackCapital:new BigNumber(data.lastBackCapitalNumber).toFixed(2),
                                countDown:data.countDown,
                                avgSpeed:new BigNumber(data.avgSpeed).toFixed(2),
                            }
                        })
                    })
                }
            })
    }
    dispatchYellow(callback){
        BigNumber.RM = 0;
        axios.get(DOMAIN_VIP+"/backcapital/dividend/withdrawCountDown")
            .then(res=>{
                let data = eval(res['data']).datas;
                // console.log(data);
                callback?callback():"";
                this.setState({
                    balance:new BigNumber(data.balance).toFixed(2),
                    yellowloaded:true,
                    yellow:{
                        totalBalance:new BigNumber(data.totalBalance).toFixed(2), 
                        frequency:data.frequency,  
                        countDown:data.countDown, 
                        lastWithdraw:new BigNumber(data.lastWithdraw).toFixed(2),
                    }
                })
            })
    }
    componentWillReceiveProps(nextProps){
    }
    componentWillUnmount() {
        clearInterval(this.interval)
    }
    render() {
        let {intl} = this.props;
        let blueInfobottom = <p><FormattedMessage id="repostati-a-p1"/>: {this.state.blue.frequency} <FormattedMessage id="seconds-per-time"/></p>;
        let blueTimeTitle = <h4><FormattedMessage id="repostati-a-h3"/></h4>;
        let blueTimebottom = <p><FormattedMessage id="repostati-a-p2"/>: {this.state.blue.lastBackCapital} ABCDE</p>;

        let yellowInfobottom = <p><FormattedMessage id="repostati-b-p1"/>: {this.state.yellow.totalBalance} ABCDE</p>;
        let yellowTimeTitle = <h4><FormattedMessage id="repostati-b-h3"/></h4>;
        let yellowTimebottom = <p><FormattedMessage id="repostati-b-p2"/>: {this.state.yellow.lastWithdraw} ABCDE</p>;
        return (
            <div className="wrap repo-wrap clearfix">
                <div className="repo-main">
                    <div className="introduction">
                        <h1><FormattedMessage id="repostati-h1"/> </h1>
                        <p><FormattedMessage id="repostati-info" /></p>
                    </div>
                    <Repoitem type="blue" 
                              name={intl.formatMessage({id: 'repostati-a-h2'})}
                              inst={this.state.blueInst}
                              num={this.state.blue.avgSpeed}
                              infoBottom={blueInfobottom}
                              timeTitle={blueTimeTitle}
                              frequency = {this.state.blue.frequency}
                              countDown = {this.state.blue.countDown}
                              timeBottom = {blueTimebottom}
                              dispatchFun = {this.dispatchBlue}
                              loaded = {this.state.blueloaded}
                              />
                    <div className="stepcoin"><i></i></div>
                    <Repoitem type="yellow" 
                              name={intl.formatMessage({id: 'repostati-b-h2'})}
                              num={this.state.balance}
                              infoBottom={yellowInfobottom}
                              timeTitle={yellowTimeTitle}
                              frequency = {this.state.yellow.frequency}
                              countDown = {this.state.yellow.countDown}
                              timeBottom = {yellowTimebottom}
                              dispatchFun = {this.dispatchBlue}
                              loaded = {this.state.yellowloaded}
                              />
                    <div className="stepcoin"><i></i></div>
                    <Repoitemred  />
                    <div className="stepcoin"><i></i></div>
                    <Earthitem/>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
       
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(Repo));