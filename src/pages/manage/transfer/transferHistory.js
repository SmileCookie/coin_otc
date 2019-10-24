import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { optPop,formatDate } from '../../../utils';
import { connect } from 'react-redux';
import { COIN_KEEP_POINT,DISMISS_TIME,PAGEINDEX,PAGEBTNNUMS,PAGESIZEFIVE,COOKIE_LAN } from '../../../conf'
import Pages from '../../../components/pages'
import ReactModal from '../../../components/popBox'
import FileSaver from 'file-saver';
import cookie from 'js-cookie';
import ExportModal from '../../../components/export';
import { fetchTransferList } from '../../../redux/modules/transferList'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import Select from 'react-select';
const BigNumber = require('big.js')

@connect(
    state => ({transferlist:state.transferlist}),
    (dispatch) => {
        return {
            fetchTransferList:(params) => {
                dispatch(fetchTransferList(params))
            }
        }
}
        )
class TransferHistory extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            visiblePage:PAGEBTNNUMS,
            current:PAGEINDEX,
            hisRecord:[],
            totalCount:0,
            pageSize:this.props.pageSize||PAGESIZEFIVE,
            pageIndex:PAGEINDEX,
            Mstr:"",
            fromVal:1,
            toVal:3,
            timeTab:0,
        }
        this.dateFormat = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        };

        // date split
        this.sp = '-';
        this.handlePageChanged = this.handlePageChanged.bind(this)
        this.handleCancelOut = this.handleCancelOut.bind(this)
        this.handleConfirmCancel = this.handleConfirmCancel.bind(this)
        this.handleCloseModal = this.handleCloseModal.bind(this)
        this.handleChangeTime = this.handleChangeTime.bind(this)
        this.getCode = this.getCode.bind(this)
        this.toCode = this.toCode.bind(this)
    }

    componentDidMount(){
         if(this.props.type == 2){
            this.setState({
                fromVal:-1,
                toVal:-1,
            })
        }else{
            this.setState({
                fromVal:-1,
                toVal:-1,
            }) 
        }
        setTimeout(() => {
            const {pageIndex,pageSize,fromVal,toVal,timeTab} = this.state 
            let data = {
                pageIndex:pageIndex,
                pageSize:pageSize,
                from:fromVal,
                to:toVal,
                timeTab:timeTab,
            }
            this.props.fetchTransferList(data) 
        }, 0);
        
       
    }

    componentWillReceiveProps(nextProps) {
        if(nextProps.transferlist.record.isloaded) {
            this.setState({
                hisRecord: nextProps.transferlist.record.data.data.list,
                totalCount:nextProps.transferlist.record.data.data.totalCount,
                pageIndex:nextProps.transferlist.record.data.data.pageIndex,
            })
        }
    }

    handlePageChanged(newPage){
        const {pageSize,fromVal,toVal} = this.state 
        // console.log(this.props)
        let coint = this.props.type ==2?this.state.optionVal:this.props.curCoin;
        let conf = {
            pageIndex:newPage,
            pageSize:pageSize,
            from:fromVal,
            to:toVal,
        }
       
        this.props.fetchTransferList(conf)
    }
      //时间 Tab
      handleChangeTime(id){
        this.setState({
            timeTab:id,
            pageIndex:PAGEINDEX
        }, () =>{ 
            const {pageIndex,pageSize,toVal,fromVal,timeTab} = this.state 
            let data = {
                pageIndex:pageIndex,
                pageSize:pageSize,
                from:fromVal,
                to:toVal,
                timeTab:timeTab,
            }
        this.props.fetchTransferList(data)
        })
    }  

    handleCancelOut(id){
        const Mstr = <div className="modal-btn">
                        <p><FormattedMessage id="withdraw.text52" /></p>
                        <div className="modal-foot">
                            <a onClick={this.handleCloseModal} className="btn"><FormattedMessage id="withdraw.text51" /></a>
                            <a className="btn ml10" onClick={() => this.handleConfirmCancel(id)}><FormattedMessage id="withdraw.text49" /></a>
                        </div>
                     </div>;
        this.setState({
            Mstr
        })
        this.modal.openModal();
    }

    handleCloseModal(){
        this.modal.closeModal()
    }
    fomartexportData(data){
        let chartName = '';
        let languaged = cookie.get(COOKIE_LAN);
        let en = {'币币账户':'Spot Account','f合约账户':'Futures Account','我的钱包':'My Wallet',"法币账户":"Fiat Account","理财账户":"Financing Account"}
        let jp = {'币币账户':'通貨口座','f合约账户':'Futures Account','我的钱包':'ウォレット',"法币账户":"法貨口座","理财账户":"理財口座"}
        let kr = {'币币账户':'코인계정','f合约账户':'Futures Account','我的钱包':'내 지갑',"法币账户":"법정화폐계정","理财账户":"재테크 계정"}
        //let hk = {'币币账户':'幣幣帳戶','f合约账户':'期貨帳戶','我的钱包':'我的錢包'}
        let sdata = []
        // let localData=(new Date()).toLocaleString().replace(/\//g,'')
        let localData =formatDate(new Date()).replace(/[\|\,|\:|\_|\ ]/g,'').replace(/\//g,'');
        if(data !==''){
            data.forEach((item,index)=>{
                    let ddata = []
                    let time = item.time?item.time.time:'--'
                    let date = time=='--'?'--':formatDate(new Date(time)).replace(',', '').replace(/\//g, this.sp)
                    let showStatus = languaged == 'en'?'Success':languaged == 'kr'?'성공':'成功'
                    let srcName = '';
                    let dstName = '';
                    if(languaged == 'cn'){
                        chartName = localData+'划转记录';
                        srcName = item['srcName'];
                        dstName =item['dstName'];
                    }else if (languaged == 'en'){
                        chartName = localData+'TRANSFER HISTORY';
                        srcName = en[item['srcName']];
                        dstName =en[item['dstName']];
                    }else if(languaged == 'hk'){
                        chartName = localData+'劃轉記錄';
                        srcName = hk[item['srcName']];
                        dstName = hk[item['dstName']];
                    }else if(languaged == 'jp'){
                        chartName = localData+'トランスファー記録';
                        srcName = jp[item['srcName']];
                        dstName = jp[item['dstName']];
                    }else if(languaged == 'kr'){
                        chartName = localData+'이체 내역';
                        srcName = kr[item['srcName']];
                        dstName = kr[item['dstName']];
                    } else{
                        chartName = localData+'划转记录';
                        srcName = item['srcName'];
                        dstName =item['dstName'];
                    }
                    
                    ddata.push(showStatus+' '+date)
                    ddata.push(item['fundTypeName'])
                    // ddata.push(item['srcName'])
                    ddata.push(srcName)
                    ddata.push(dstName)
                    ddata.push(+new BigNumber(item.amount).toFixed(COIN_KEEP_POINT))
                    sdata.push(ddata)
            }) 
        }
        let str =  '';
        switch(languaged) {
            case 'cn':
                str = '状态,币种,划出账户,划入账户,数量';
                 break;
            case 'en':
                 str = 'Status,Coin,Transfer Out,Transfer In,Amount';
                break;
            case 'hk':
                 str = '狀態,幣種,劃出帳戶,劃入帳戶,數量';
                break;
            case 'kr':
                 str = '상태,코인,전출계정,전입계정,수량';
                break;
            case 'jp':
                str = 'ステータス,コイン,トランスファーアウト,トランスファーイン,数量';
               break;
            default:
                 str = '状态,币种,划出账户,划入账户,数量';
        }
        sdata.forEach((item,index)=>{
            str += '\n'+item+','
        })
        // Excel打开后中文乱码添加如下字符串解决
        let exportContent = "\uFEFF";
        let blob = new Blob([exportContent + str], {
            type: "text/plain;charset=utf-8"
        });
        FileSaver.saveAs(blob, `${chartName}.csv`);
    }
    handleConfirmCancel(did){
       this.props.fetchCancel(did,(res)=>{
           let result = res.data
           let state = (/<([^>]+)>([^<>]+)<\/\1>/gm.exec(result))[2]
           let des = (/<([^>]+)>([^<>]+)<\/MainData>/g.exec(result))[2]  
        //    this.props.notifSend({
        //       message: des,
        //       kind: 'info',
        //       dismissAfter: DISMISS_TIME
        //    });
        optPop(() => {},des);
           if(state == "true"){
            let coint = this.props.type ==2?this.state.optionVal:this.props.curCoin;
               let obj = {
                    pageIndex:this.state.pageIndex,
                    pageSize:this.state.pageSize,
                    coint:coint,
               }
               this.props.fetchRecord(obj)
           }
           this.modal.closeModal()
       })
    }
    getCode(item = {}){
        const {toVal} = this.state
        
        if(item.value==toVal){
            let toVal = 1
            if(item.value == 1){
                toVal = 2
            }else if(item.value == 2){
                toVal = 3
            }
            // this.setState({
            //     toVal
            // })
        }
        this.setState({
            fromVal: item.value,
            pageIndex:PAGEINDEX
        },() => {
            const {pageIndex,pageSize,toVal,timeTab} = this.state 
            let data = {
                pageIndex:pageIndex,
                pageSize:pageSize,
                from:item.value,
                to:toVal,
                timeTab:timeTab,
            }
            this.props.fetchTransferList(data)
            // this.props.fetchRecord(conf)
        });
        
    }
    toCode(item = {}){
        const {fromVal} = this.state
       
        if(item.value==fromVal){
            let fromVal = 1
            if(item.value == 1){
                fromVal = 2
            }else if(item.value == 2){
                fromVal = 3
            }
            // this.setState({
            //     fromVal
            // })
        }
        this.setState({
            toVal: item.value,
            pageIndex:PAGEINDEX
        },() => {
            const {pageIndex,pageSize,fromVal,timeTab} = this.state 
            let data = {
                pageIndex:pageIndex,
                pageSize:pageSize,
                from:fromVal,
                to:item.value,
                timeTab:timeTab,
            }
            this.props.fetchTransferList(data)
            }
        );
    }

    render(){
        const {isloading,isloaded} = this.props.transferlist.record
        const {timeTab,pageSize,pageIndex,totalCount,fromVal,toVal} = this.state
        const {type,selectOption} = this.props
        const propTag = this.props.propTag
        return (
            <div className="bk-new-tabList select-new">
             
                <div className="entrust-head">
                <div className="entrust-head-market left">
                <h5 className="left padl10"><FormattedMessage id="划出账户" /></h5>
                    <div className="record-head entrust-selcet" style={{width:'124px'}}>
                    <Select
                        value={fromVal}
                        clearable={false}
                        searchable={false}
                        onChange={this.getCode}
                        
                        options={[{ value: -1, label: <FormattedMessage id='全部' /> },{ value: 1, label: <FormattedMessage id='我的钱包' /> },{ value: 2, label:  <FormattedMessage id='币币账户'  />},{ value: 3, label:  <FormattedMessage id='法币账户' /> },{ value: 5, label:  <FormattedMessage id='理财账户' /> }]}
                        />
                    </div>
                </div>
              
               <div className="entrust-head-market left padl20">
                <h5 className="left padl10"><FormattedMessage id="划入账户" /></h5>
                <div className="record-head entrust-selcet" style={{width:'124px'}}>
                 <Select
                    value={toVal}
                    clearable={false}
                    searchable={false}
                    onChange={this.toCode}
                    
                    options={[{ value: -1, label: <FormattedMessage id='全部' /> },{ value: 1, label: <FormattedMessage id='我的钱包' /> },{ value: 2, label:  <FormattedMessage id='币币账户'  /> },{ value: 3, label: <FormattedMessage id='法币账户' />},{ value: 5, label:  <FormattedMessage id='理财账户' /> }]}
                    />
                    </div>
                  </div>
                    <div className="entrust-time left padl20">
                        <h5 className="padl10"><FormattedMessage id="时间（资产管理）" /></h5>
                        <ul className="tab-time">
                        <li>
                            
                            <label  className={timeTab== 0?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'}  onClick={() => this.handleChangeTime(0)}></label>
                            <span> <FormattedMessage id="全部" /></span>
                        </li>   
                        <li>                              
                                <label  className={timeTab==1?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(1)}></label>
                                <span>  <FormattedMessage id="7天内" /></span>
                        </li>  
                        <li>                              
                                <label  className={timeTab==2?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(2)}></label>
                                <span>  <FormattedMessage id="15天内" /></span>
                        </li>  
                        <li>                              
                                <label  className={timeTab==3?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(3)}></label>
                                <span>  <FormattedMessage id="30天内" /></span>
                        </li>  
                        </ul>
                    </div>
                    {!isloading&&isloaded&&this.state.totalCount >0&&this.state.hisRecord.length > 0?<ExportModal boxname={<FormattedMessage id="导出历史划转记录" />} fomartexportData={()=>this.fomartexportData(this.state.hisRecord)}></ExportModal>:<div className="export-dis right"><FormattedMessage id="导出历史划转记录" /></div>}
                </div>
                <div className="bk-new-tabList-bd">
                    <table className="table-history" width="100%">
                        <thead>
                            <tr>
                                <th><FormattedMessage  id="状态" /></th>
                                <th className="text-center"><FormattedMessage  id="deposit.text11" /></th>
                                <th className="text-center"><FormattedMessage  id="划出账户" /></th>
                                <th className="text-center"><FormattedMessage  id="划入账户" /></th>
                                <th className="text-right"><FormattedMessage  id="deposit.text12" /></th>
                                <th className="wid183 border_right"></th>
                            </tr>
                        </thead>
                        <tbody className="tbody-load">
                            {
                                isloading&&!isloaded?(
                                    <tr>
                                       <td colSpan="10">
                                        <div className="iconfont icon-jiazai new-loading">
                                        </div>
                                       </td>
                                    </tr>
                                ) : (
                                    this.state.totalCount >0? (
                                        this.state.hisRecord.map((item,index) => {
                                            BigNumber.RM=0
                                            let time = item.time?item.time.time:'--'
                                            let date = time=='--'?'--':formatDate(new Date(time)).replace(',', '').replace(/\//g, this.sp)                                            
                                            return (  
                                                <tr key={index} className="bk_payInOut_tr border_right">
                                                    <td>
                                                        <p className="confirm-detail clearfix">
                                                            <FormattedMessage  id="withdraw.text33" />
                                                        </p>
                                                        {date}
                                                    </td>
                                                    <td className="text-center">{item.fundTypeName}</td>
                                                    <td className="text-center"><FormattedMessage  id={item.srcName} /></td>
                                                    <td className="text-center width120xsp"><FormattedMessage  id={item.dstName} /></td>
                                                    <td className="text-right">
                                                    {new BigNumber(item.amount).toFixed(COIN_KEEP_POINT)}
                                                    						
                                                    </td>
                                                    <td className="wid183"></td>
                                                </tr>
                                            )
                                        })

                                    ) : (
                                        <tr className="tr-norecord">
                                            <td className="billDetail_no_list" colSpan="10">
                                                <p className="entrust-norecord"> 
                                                    <svg className="icon" aria-hidden="true">
                                                        <use xlinkHref="#icon-tongchang-tishi"></use>
                                                    </svg>
                                                    <FormattedMessage id="当前没有划转记录。"/>
                                                </p>
                                            </td>
                                        </tr>
                                    )
                                )
                            }
                        </tbody>
                    </table>
                </div>
                <div className="bk-pageNav">
                  {/* <div className={this.state.totalCount <= PAGEBTNNUMS ? "tablist hide" : "tablist"}>
                    <Pages
                        total={this.state.totalCount}
                        pageIndex={this.state.pageIndex}
                        pagesize={this.state.pageSize}
                        currentPageClick = { this.handlePageChanged }
                    />
                  </div> */}
                  <div className="tablist deposits" style={{display:totalCount > pageSize?'block':'none'}}>
                            <Pages 
                                pageIndex={pageIndex}
                                pagesize={pageSize}
                                total={totalCount}
                                currentPageClick = { this.handlePageChanged }
                            />
                        </div>
                </div>
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.Mstr}
                </ReactModal>
            </div>
        )
    }
}
export default TransferHistory

 


























