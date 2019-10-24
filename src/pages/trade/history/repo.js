import React from 'react';
import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
const BigNumber = require('big.js');
import ScrollArea from 'react-scrollbar';
import { formatDate } from '../../../utils';
import {FETCH_REPO_INFO,COOKIE_LAN} from '../../../conf';
import Repolist from './repoList';
import cookie from 'js-cookie';
class repobox extends React.Component {
    constructor(props) {
        super(props);
        // console.log("marketinfo")
       this.setAboutMe = this.setAboutMe.bind(this);
       this.setLastId = this.setLastId.bind(this);
       this.svgRun = this.svgRun.bind(this);
        this.state = {
            frequency: 0,
            countDown:0,
            time:"00:00",
            strokeDasharray:"",
            capitalsList:[],
            aboutMe:false,
            lastBackCapital:"",
            lastId:0,
            repoList:[],
            repoListMe:[],
            url : "https://btcwinex.zendesk.com/hc/zh-cn/articles/115003128032-%E5%85%B3%E4%BA%8EABCDE%E5%9B%9E%E8%B4%AD"
        }
    }
    componentWillReceiveProps(){
        
    }
    componentDidMount() {
        
        // console.log("props Up")
        this.props.fetchCountDown(0,(data)=>{
            this.setCountDown(data)
        });
        let localeCookie = cookie.get(COOKIE_LAN);
        let localeUrl;
        switch(localeCookie) {
            case 'cn':
                localeUrl = "https://btcwinex.zendesk.com/hc/zh-cn/articles/115003128032-%E5%85%B3%E4%BA%8EABCDE%E5%9B%9E%E8%B4%AD";
                break;
            case 'en':
                localeUrl = "https://btcwinex.zendesk.com/hc/en-us/articles/115003128032-About-ABCDE-Repo-Mechanism";
                break;
            case 'hk':
                localeUrl = "https://btcwinex.zendesk.com/hc/zh-tw/articles/115003128032-%E9%97%9C%E6%96%BCABCDE%E5%9B%9E%E8%B3%BC";
                break;
            default:
                localeUrl = "https://btcwinex.zendesk.com/hc/en-us/articles/115003128032-About-ABCDE-Repo-Mechanism";
        }
        this.setState({
            url:localeUrl
        })
        // this.interval = setInterval(()=>{
        //     // this.props.fetchCountDown();
        //     console.log(this.props.data)
        // },20)
    }
    componentWillUnmount() {
        clearInterval(this.interval)
    }
    fetchCountDown(){
        console.log("lastId:"+this.state.lastId);
        this.props.fetchCountDown(this.state.lastId,(data)=>{
            this.setCountDown(data)
        });
    }
    formatListData(capitals){
        const {marketsConfData,currentMarket} = this.props;
        let exchangeBixDian = 0;
        try{
            exchangeBixDian = marketsConfData[currentMarket].exchangeBixDian
        } catch(e){

        }
        let numberBixDian = marketsConfData[currentMarket].numberBixDian
        let capitalsData = [];
        let maxTrade = 0;
        for(let i = capitals.length-1;i>=0;i--){
            if(capitals[i]>maxTrade){
                maxTrade = capitals[i];
            }
        }
        // console.log(maxTrade);
        for(let i = capitals.length-1;i>=0;i--){
            let height = capitals[i]!=0?capitals[i]/maxTrade*100:0;
            let arryCapitalsOpen = String(capitals[i]).split(".");
            // console.log(arryCapitalsOpen);
            let textCapitals = arryCapitalsOpen[1]&&arryCapitalsOpen[1].length>=exchangeBixDian?new BigNumber(capitals[i]).toFixed(exchangeBixDian):capitals[i];
            capitalsData.push({height:height,textCapitals:textCapitals})
        }
        // console.log(capitalsData);
        return capitalsData
    }
    setLastId(id){
        this.setState({lastId:id});
    }
    setCountDown(data){
        try{
        const {marketsConfData,currentMarket} = this.props;
        let _this = this;
        let capitalsList = this.formatListData(data.capitals);
        let exchangeBixDian = marketsConfData[currentMarket].exchangeBixDian;
        _this.setState({capitalsList:capitalsList});
        if(data.entrusts.length>0){
            let nextRepoListData = [];
            let nextRepoListMe = [];
            for(let em = 0;em < data.entrusts.length;em++){
                nextRepoListData.push(data.entrusts[em])
                if(data.entrusts[em].ratio>0){
                    nextRepoListMe.push(data.entrusts[em])
                }
            }
            let repoListData = this.state.repoList.concat(nextRepoListData);
            let repoListMe = this.state.repoListMe.concat(nextRepoListMe);
            _this.setState({lastId:data.entrusts[data.entrusts.length-1].entrustId});
            _this.setState({repoList:repoListData});
            _this.setState({repoListMe:repoListMe});
        }
        _this.setState({lastBackCapital:new BigNumber(data.lastBackCapital).toFixed(exchangeBixDian)});
        _this.setState({frequency: data.frequency,
            countDown: data.countDown==0?data.frequency:data.countDown},()=>{this.svgRun(data.countDown)})
        }catch(e){
            
        }
    }
    svgRun(countDownTime){
        let _this = this;
         console.log(_this.state)
         let frequency = this.state.frequency;
         let countDown = countDownTime;
         let thisTime =  new Date();
         _this.interval = setInterval(function(){
             let timeN = new Date();
             let timeCha = (timeN - thisTime)/1000;
             // thisTime = timeN;
             let timeO = countDown-timeCha;
             // countDown = timeO;
             if(timeO<=0){
                 countDown = frequency;
                 clearInterval(_this.interval);
                 _this.fetchCountDown();
             }
             if(timeO<0){timeO=frequency}
             let percent = timeO / frequency;
             let fen = Math.floor(timeO/60) ,miao=Math.ceil(timeO%60);
             if(miao<10){
                _this.setState({time:fen+":0"+miao});
             }else{
                _this.setState({time:fen+":"+miao});
             }
             let perimeter = Math.PI * 2 * 28;
             _this.setState({strokeDasharray:perimeter * percent + " " + perimeter * (1- percent)})
         },20)
    }
    setAboutMe(){
        const {user} = this.props;
        if(user){
            this.setState({
                aboutMe:!this.state.aboutMe
            })
        }else{
            this.props.notifSend("请登录后再尝试",'warning')
        }
    }
    render() {
        const { isLoaded } = this.props;
        if(!isLoaded){
            // console.log("Loading!")
            return(
                <div className="repobox">
                    Loading...
                </div>
            )
        }
        // console.log(data);
        
        return(
            <div className="repobox">
                <div className="repo_top">
                    <div className="repo_top_right">
                        <div className="circle_box">
                            <svg width="62" height="62" viewBox="0 0 62 62">
                                <circle cx="31" cy="31" r="28" strokeWidth="5" stroke="#EFEFEF" fill="none"></circle>
                                <circle cx="31" cy="31" r="28" strokeWidth="5" stroke="#57C4A7" fill="none" transform="rotate(-90,31 31)" strokeDasharray={this.state.strokeDasharray}></circle>
                            </svg>
                            <em>{this.state.time}</em>
                        </div>
                        <div className="histogram_box clearfix">
                        {this.state.capitalsList.map((item,index)=>{
                            return <div className="pillar" key={index}>
                                        <span style={{height:item.height+"%"}}>
                                        <em>{item.textCapitals}</em>
                                        </span>
                                    </div>
                        })
                        }
                        </div>
                    </div>
                    <p><FormattedMessage id="repo-frequency"/></p>
                    <h4><b id="repo-rate">{this.state.frequency==0?" ":this.state.frequency}</b> <FormattedMessage id="seconds-per-time"/></h4>
                    <p><FormattedMessage id="Amount-for-the-Latest"/></p>
                    <h4><b id="last-repo">{this.state.lastBackCapital}</b>USDT</h4>
                </div>
                <div className="repo_body">
                    <div className="repo_list_box">
                        <table>
                            <thead>
                                <tr>
                                    <th><FormattedMessage id="repoDate"/></th>
                                    <th><FormattedMessage id="Proportion"/></th>
                                    <th><FormattedMessage id="Repo-Used"/></th>
                                    <th>=></th>
                                    <th><FormattedMessage id="Repo-Amount"/></th>
                                </tr>
                            </thead>
                        </table>
                        <div className="list_cover">
                            <ScrollArea className="example" horizontal={false} smoothScrolling={true} >
                                <Repolist 
                                    type={this.state.aboutMe?"mine":"all"} 
                                    fetchRepoListData={this.state.aboutMe?this.props.fetchRepoListMineData:this.props.fetchRepoListData} 
                                    data={this.state.aboutMe?this.props.repolistmine:this.props.repolist}
                                    loading={this.state.aboutMe?this.props.repolistmine.isloading:this.props.repolist.isloading}
                                    repoListAf={this.state.aboutMe?this.state.repoListMe:this.state.repoList}
                                    marketsConfData={this.props.marketsConfData}
                                    currentMarket={this.props.currentMarket}
                                    setLastId={(id)=>{this.setLastId(id)}}
                                    url = {this.state.url}
                                />
                            </ScrollArea>
                        </div>
                    </div>
                </div>
                <div className="repo_footer">
                    <a target="new"  href={this.state.url}><FormattedMessage id="What-is-the-Repo"/></a>
                    <span className={this.state.aboutMe?"changetype active":"changetype"} onClick={this.setAboutMe}><FormattedMessage id="Repo-related-to-me"/></span>
                </div>
            </div>
        )
    }
}
export default repobox;