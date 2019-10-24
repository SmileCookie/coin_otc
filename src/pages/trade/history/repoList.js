import React from 'react';
import axios from 'axios';
import { DOMAIN_BASE, DOMAIN_VIP, DOMAIN_TRANS } from '../../../conf';
import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
const BigNumber = require('big.js');
import ReactModal from '../../../components/popBox';
import { formatDate } from '../../../utils';


class repolist extends React.Component {
    constructor(props) {
        super(props);
        // console.log("marketinfo")
        this.setListState = this.setListState.bind(this);
        this.fetchData = this.fetchData.bind(this);
        this.addListState = this.addListState.bind(this);
        this.state = {
            lastId:0,
            list:[],
            hasMore:1,
            modalHTML:""
        }
    }
    componentWillReceiveProps(nextProps){
        if(nextProps.type!=this.props.type){
            nextProps.fetchRepoListData(0,(data)=>{
                console.log(data)
                this.setListState(data)
            })
        }
    }
    componentDidMount() {
        this.props.fetchRepoListData(0,(data)=>{
            this.setListState(data);
            let lastId = data.entrusts[0].entrustId;
            this.props.setLastId(lastId);
        })
    }
    componentWillUnmount() {
        clearInterval(this.interval)
    }
    fetchData(){
        this.props.fetchRepoListData(this.state.lastId,(data)=>{
            this.addListState(data)
        })
    }
    setListState(data){
        if(data.entrusts.length>0){
            let lastId = data.entrusts[data.entrusts.length-1].entrustId;
            // console.log(lastId);
            this.setState({hasMore:data.hasMore,list:data.entrusts,lastId:lastId})
        }else{
            let lastId = 0;
            // console.log(lastId);
            this.setState({hasMore:data.hasMore,list:data.entrusts,lastId:lastId})
        }
        
    }
    addListState(data){
        let oldData = this.state.list; 
        let thisData = oldData.concat(data.entrusts);
        let lastId = data.entrusts[data.entrusts.length-1].entrustId;
        // console.log(lastId);
        this.setState({hasMore:data.hasMore,list:thisData,lastId:lastId});
    }
    formatListData(){
        const {marketsConfData,currentMarket} = this.props;
        let oldData = this.props.repoListAf.concat(this.state.list);
        oldData.sort((x,y)=>{return (x.date < y.date) ? 1 : -1 })
        let Lister = [];
        let fullTotalMoney=10;
        let exchangeBixDian = 0;
        try{
            exchangeBixDian = marketsConfData[currentMarket].exchangeBixDian
        }catch(e){

        }
        let numberBixDian = marketsConfData[currentMarket].numberBixDian
        let maxBuyNumber = 0;
        BigNumber.RM = 0;
        for(let em =0;em<oldData.length;em++){
            let teList = {};
            teList.entrustId = oldData[em].entrustId;
            teList.persent = (oldData[em].totalMoney/fullTotalMoney)*100;
            teList.date = oldData[em].date;
            teList.totalMoney = new BigNumber(oldData[em].totalMoney).toFixed(exchangeBixDian);
            teList.amount = new BigNumber(oldData[em].amount).toFixed(numberBixDian);
            Lister.push(teList);
        }
        return Lister
    }
    // this.returnPiebuySvg(iten.ratio,3)
    returnPiebuySvg(ratio,r){
        let ru = r?r:3;
        let perimeter = Math.PI * 2 * ru;
        BigNumber.RM = 0;
        let persentString = new BigNumber(ratio).times(100);
        if(persentString<0.1){
            persentString = "<0.1";
        }else{
            persentString = new BigNumber(persentString).toFixed(1);
        }
        let svgH = <div className="svg_box">
                        <svg width="20" height="20" viewBox="0 0 20 20">
                            <circle cx="10" cy="10" r="3" strokeWidth="6" stroke="#EAEAEA" fill="none"></circle>
                            <circle cx="10" cy="10" r="3" strokeWidth="6" stroke="#0084D3" fill="none" transform="rotate(-90,10 10)" strokeDasharray={(ratio*perimeter)+' '+((1-ratio)*perimeter)}></circle>
                        </svg>
                        <em>{persentString}%</em>
                    </div>
        return svgH
    };
    getDetail(id){
        try{
        const {marketsConfData,currentMarket,url} = this.props;
        let exchangeBixDian = marketsConfData[currentMarket].exchangeBixDian;
        let numberBixDian = marketsConfData[currentMarket].numberBixDian;
        axios.get(DOMAIN_VIP + "/backcapital/getEntrustById?callback=&entrustId="+id)
        .then(res => {
            let data = eval(res['data']).datas;
            console.log(data);
            let str  =  <div className="bk-repoDetail">
                            <div className="head "><h3><FormattedMessage id="Repo-Records"/></h3></div>
                            <div className="bk-repo">
                                <h3><FormattedMessage id="repo-fee-repurchase"/> <a target="new" href={url}><FormattedMessage id="For-more-details"/></a></h3>
                                <div className="bk-repo-info clearfix">
                                    <div className="tit"><FormattedMessage id="Repo-Success"/></div>
                                    <div className="repo-id"><FormattedMessage id="Repo"/>ID:<b>{data.entrustId}</b></div>
                                    <div className="repo-date">
                                        <p><em>{formatDate(data.date,'hh:mm:ss')}</em></p>
                                        <p><b>{formatDate(data.date,'yyyy-MM-dd')}</b></p>
                                    </div>
                                    <div className="content">
                                        <p><span><FormattedMessage id="Repo-Used"/></span><i><FormattedMessage id="sellbuy.BUY"/></i><span><FormattedMessage id="Repo-Amount"/></span></p>
                                        <p>
                                            <b className="repo-total-money">{new BigNumber(data.totalMoney).toFixed(exchangeBixDian)} USDT</b>
                                            <i className="iconfont">&#xe633;</i>
                                            <b className="repo-amount">{new BigNumber(data.amount).toFixed(numberBixDian)} ABCDE</b>
                                        </p>
                                    </div>
                                </div>
                                <div className="bk-repo-inscape">
                                    <h4><FormattedMessage id="Repo-Transaction-Records"/></h4>
                                    <div className="table-cover">
                                        <table>
                                            <thead>
                                                <tr>
                                                    <th><FormattedMessage id="repoDate"/></th>
                                                    <th><FormattedMessage id="Price"/></th>
                                                    <th><FormattedMessage id="Amount"/></th>
                                                    <th><FormattedMessage id="TotalBalance"/></th>
                                                </tr>
                                            </thead>
                                            <tbody id="repoRecordInfo">
                                                {data.transRecords.map((item,index)=>{
                                                    return (
                                                        <tr key={item.date +""+ index}>
                                                            <td>{formatDate(item.date, "yyyy-MM-dd hh:mm:ss")}</td>
                                                            <td>{new BigNumber(item.price).toFixed(exchangeBixDian)} USDT</td>
                                                            <td>{new BigNumber(item.amount).toFixed(numberBixDian)} ABCDE</td>
                                                            <td>{new BigNumber(item.totalMoney).toFixed(exchangeBixDian)} USDT</td>
                                                        </tr>
                                                    )
                                                })}
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <div className="bk-repo-detail">
                                    <h4><FormattedMessage id="Repo-Funds-Composition"/></h4>
                                    <div className="table-cover">
                                        <table>
                                            <thead>
                                                <tr>
                                                    <th><FormattedMessage id="repoDate"/></th>
                                                    <th><FormattedMessage id="Occupied"/></th>
                                                    <th><FormattedMessage id="Trade-ID"/></th>
                                                    <th><FormattedMessage id="Market"/></th>
                                                    <th><FormattedMessage id="The-Fee-Estimated"/></th>
                                                </tr>
                                            </thead>
                                            <tbody id="repoRecordDetail">
                                                {data.capitals.map((item,index)=>{
                                                    return (
                                                        <tr key={item.date+item.transRecordId +""+ index}  className={item.relatedMe==1?"blue":""}>
                                                            <td>{formatDate(item.date, "yyyy-MM-dd hh:mm:ss")}</td>
                                                            <td>{this.returnPiebuySvg(item.ratio,3)}</td>
                                                            <td>{item.transRecordId}</td>
                                                            <td>{item.market}</td>
                                                            <td>{new BigNumber(item.fee).toFixed(exchangeBixDian)} USDT</td>
                                                        </tr>
                                                    )
                                                })}
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>;
            this.setState({modalHTML:str},()=>{
                this.modal.openModal();
            })
        })
    }catch(e){}
    }
    render() {
        const { loading } = this.props;
        // console.log(data);
        let data = this.formatListData();
        return(<div>
                <table>
                    <tbody id="repoList">
                        {
                            data.length == 0 ?(
                            <tr>
                                <td colSpan='4'>
                                    <div className="alert_under_table">
                                        <i className="iconfont icon-tongchang-tishi norecord"></i>
                                        <FormattedMessage id="No.record" tagName = 'b' />
                                    </div>
                                </td>
                            </tr>
                            ):(
                            data.map((item,index)=>{
                                return (
                                    <tr key={item.entrustId} className={item.ratio>0?"ratio":""} onClick={()=>{this.getDetail(item.entrustId)}}>
                                        <td>{this.props.type=="all"?formatDate(item.date,'hh:mm:ss'):formatDate(item.date,'yyyy-MM-dd')}</td>
                                        <td><div><span style={{width:item.persent+'%'}}></span></div></td>
                                        <td>{item.totalMoney} <i>USDT</i></td>
                                        <td><i className="iconfont"> &#xe634; </i></td>
                                        <td>{item.amount} <i>ABCDE</i></td>
                                    </tr>
                                )
                            })
                        )}
                    </tbody>
                </table>
                {loading?<p>Loading ~ ~ </p>:""}
                <p className="moretwenty">{this.state.hasMore==1?<b onClick={()=>{this.fetchData()}}><FormattedMessage id="repo-more20"/> <i>&#xe635;</i></b>:""}</p>
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.modalHTML}
                </ReactModal>
            </div>
        )
    }
}
export default repolist;