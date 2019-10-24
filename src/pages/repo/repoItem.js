import React from 'react';
const BigNumber = require('big.js');
import { FormattedMessage,FormattedHTMLMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';

class Repoitem extends React.Component {
    constructor(props){
        super(props)
        this.runTime = this.runTime.bind(this)
        this.state = {
            instShow:false,
            countDown:23,
            frequency:30,
            time:"",
            width:0,
        }
    }
    componentDidMount() {
        const { countDown , frequency,type,loaded} = this.props;
        loaded?this.setCountDown(countDown,frequency):"";
    }
    componentWillReceiveProps(nextProps){
        const {type,loaded} = nextProps;
            clearInterval(this.interval);
            loaded?this.setCountDown(nextProps.countDown,nextProps.frequency):"";
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
    setCountDown(countDown,frequency,callback){
        this.setState({frequency:frequency,
            countDown:countDown==0?frequency:countDown},
            ()=>{
                this.runTime(countDown);
                if(callback){callback();}
            })
    }
    runTime(countDownTime){
        const {type,dispatchFun} = this.props;
        let _this = this;
        // console.log("state:"+type)
        // console.log(_this.state)
        let frequency = this.state.frequency;
        let countDown = countDownTime;
        let thisTime =  new Date();
        _this.interval = setInterval(function(){
            let timeN = new Date();
            let timeCha = (timeN - thisTime)/1000;
            let timeO = countDown-timeCha;
            if(timeO<=0){
                countDown = frequency;
                clearInterval(_this.interval);
                if(dispatchFun){
                    // console.log("dis:"+type);
                    dispatchFun();
                }
            }
            if(timeO<0){timeO=frequency}
            
            let buling = (num)=>{
                if(num<10){
                    return "0"+num;
                }else{
                    return num;
                }
            }
            let tian=Math.floor(timeO/(3600*24)),shi=Math.floor(timeO/3600),fen = buling(Math.floor((timeO%3600)/60)) ,miao=buling(Math.ceil(timeO%60));
            if(type=="blue"){
                if(shi<=0){
                    _this.setState({time:fen+":"+miao});
                }else{
                    _this.setState({time:buling(shi)+":"+fen+":"+miao});
                }
            }else if(type=="yellow"){
                const _miao = (miao-1)>0?buling(miao-1):'00'
                _this.setState({time:buling(shi)+":"+fen+":"+_miao});
            }else if(type=="red"){
                if(tian>0){
                    _this.setState({time:"≈"+tian+" DAYS"});
                }else if(shi>0){
                    _this.setState({time:"≈"+shi+" HOURS"});
                }else{
                    _this.setState({time:"≈"+fen+" MINUTES"});
                }
            }
            
            let percent = new BigNumber(frequency).minus(timeO).div(frequency).times(100)
            _this.setState({width:percent+"%"})
        },20)
    }

    render() {
        const {type,name,inst,num,infoBottom,timeTitle,timeBottom,loaded} = this.props;
        let icon;
        if(type=="blue"){
            icon=<i className="iconfont">&#xe671;</i>
        }else if(type=="yellow"){
            icon=<i className="iconfont">&#xe670;</i>
        }else if(type=="red"){
            icon=<i className="iconfont">&#xe672;</i>
        }
        if(!loaded){
            return  <div className={"repo-item "+type}>
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
        let insthtml = <div className="inst_detail">
                        {inst?inst:""}
                        <botton onClick={()=>{this.setInstShow(0)}}><FormattedMessage id="repostati-button"/></botton>
                       </div>;
        
        return (
            <div className={"repo-item "+type}>
                <div className="clearfix">
                    <div className="icon">
                    {icon}
                    </div>
                    <div className="repo_info">
                        <h3>{name}{inst?<em><i className="iconfont" onClick={()=>{this.setInstShow(1)}}>&#xe657;</i>{this.state.instShow?insthtml:""}</em>:""}</h3>
                        <h2>{num}<span>{type=="blue"?"ABCDE/M":"ABCDE"}</span></h2>
                        <div className="infobottom">{infoBottom}</div>
                    </div>
                    <div className="time_box">
                        {timeTitle}
                        <div className="dead_time">
                            {this.state.time}
                            {type=="red"?"":<i className="iconfont">&#xe66e;</i>}
                        </div>
                        <div className="dead_line">
                            <span style={{width:this.state.width}}></span>
                        </div>
                        <div className="timeBottom">
                            {timeBottom?timeBottom:""}
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}


export default Repoitem;