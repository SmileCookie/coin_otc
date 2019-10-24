import React from 'react';
import Modal from 'react-modal';
import { FormattedMessage, injectIntl } from 'react-intl';
import { optPop,formatDate } from '../../../utils';
import { COIN_KEEP_POINT,DISMISS_TIME,PAGEINDEX,PAGEBTNNUMS,PAGESIZEFIVE,COOKIE_LAN } from '../../../conf'
import Pages from '../../../components/pages';
import Select from 'react-select';
import SelectHistory from '../../../components/selectHistory'
import FileSaver from 'file-saver';
import cookie from 'js-cookie';
import ExportModal from '../../../components/export';
import ReactModal from '../../../components/popBox'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const BigNumber = require('big.js')

export default class HistoryList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            visiblePage:PAGEBTNNUMS,
            current:PAGEINDEX,
            hisRecord:[],
            totalCount:0,
            pageSize:this.props.pageSize||PAGESIZEFIVE,
            // pageSize:2,
            pageIndex:PAGEINDEX,
            Mstr:"",
            timeType:0,
            optionVal:'',
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
        this.fomartexportData = this.fomartexportData.bind(this)
    }

    componentDidMount(){

    }

    componentWillReceiveProps(nextProps) {
        // console.log(nextProps)
        if(nextProps.hisRecord.isloaded) {
            this.setState({
                hisRecord: nextProps.hisRecord.data.list,
                totalCount:nextProps.hisRecord.data.totalCount,
                pageIndex:nextProps.hisRecord.data.pageIndex,
            })
        }
    }
    fomartexportData(data){
        let chartName = '';
        let languaged = cookie.get(COOKIE_LAN);
        let localData =formatDate(new Date()).replace(/[\|\,|\:|\_|\ ]/g,'').replace(/\//g,'');
        let sdata = []
        if(data !==''){
            data.forEach((item,index)=>{
                    let ddata = []
                    // let date = this.props.formatDate(new Date(item.confirmTime.time), this.dateFormat).replace(',', '').replace(/\//g, this.sp)
                    // BigNumber.RM=0
                    let time = item.confirmTime?item.confirmTime.time:item.submitTime?item.submitTime.time:'--'
                    let date = time=='--'?'--':formatDate(new Date(time)).replace(',', '').replace(/\//g, this.sp)
                    let tradeTypesname = '';
                    let statusname = '';
                    let showStatus = ''
                    if(item.status == 0 || item.status >3){
                        if(item.commandId > 0){
                            showStatus = languaged == 'en'?'Pending':languaged == 'hk'?'打幣中':languaged == 'jp'?'振替中':languaged == 'kr'?'확인 중':'打币中';
                        }else if(item.commandId == 0){
                            showStatus = languaged == 'en'?'Awaiting':languaged == 'hk'?'待處理':languaged == 'jp'?'未処理':languaged == 'kr'?'대기중':'待处理';
                        }
                    }else if(item.status == 1){
                        showStatus = languaged == 'en'?'Fail':languaged == 'hk'?'失敗':languaged == 'jp'?'失敗':languaged == 'kr'?'실패':'失败';
                    }else if(item.status == 2){
                        showStatus = languaged == 'en'?'Success':languaged == 'hk'?'成功':languaged == 'jp'?'成功':languaged == 'kr'?'성공':'成功';

                    }else if(item.status == 3){
                        showStatus = languaged == 'en'?'Canceled':languaged == 'hk'?'已取消':languaged == 'jp'?'キャンセル済':languaged == 'kr'?'취소 완성했습니다':'已取消';
                    }
                    if(languaged == 'cn'){
                        chartName = localData + '提现记录'
                    }else if (languaged == 'en'){
                        chartName = localData + 'WITHDRAWAL HISTORY'
                    }else if(languaged == 'hk'){
                        chartName = localData + '提現記錄'
                    } else if(languaged == 'kr'){
                        chartName = localData + '입금 데이터가 없습니다'
                    }else if(languaged == 'jp'){
                        chartName = localData + '出金記録'
                    }else{
                        chartName = localData + '提现记录'
                    }
                    let txid = item['txId']!=0?' Txid:'+ item['txId']:''
                    ddata.push(showStatus+' '+date)
                    ddata.push(item['fundsTypeName'])
                    ddata.push(new BigNumber(item.amount).toFixed(COIN_KEEP_POINT))
                    ddata.push(item['addressMemo'])
                    if(this.props.isEosType){
                        ddata.push(item['memo'])
                    }
                    ddata.push(item['toAddress']+txid)
                    sdata.push(ddata)
            })



        }
        let str =  '';
        if(!this.props.isEosType){
            switch(languaged) {
                case 'cn':
                    str = '状态,币种,数量,地址备注,提现地址';
                     break;
                case 'en':
                     str = 'Status,Coin,Amount,Note,Withdrawal Address';
                    break;
                case 'hk':
                     str = '狀態,幣種,數量,地址備注,提現地址';
                    break;
                case 'jp':
                    str = 'ステータス,コイン,数量,アドレス付記,出金アドレス';
                   break;
                case 'kr':
                   str = '상태,코인,수량,주소 비고,출금주소';
                  break;
                default:
                     str = '状态,币种,数量,地址备注,提现地址';
            }
        }else{
            switch(languaged) {
                case 'cn':
                    str = '状态,币种,数量,地址备注,地址标签,提现地址';
                     break;
                case 'en':
                     str = 'Status,Coin,Amount,Note,Address Label,Withdrawal Address';
                    break;
                case 'hk':
                     str = '狀態,幣種,數量,地址備注,地址標簽,提現地址';
                    break;
                case 'kr':
                    str = '상태,코인,수량,地址備注,주소 비고,출금주소';
                   break;
                case 'jp':
                    str = 'ステータス,コイン,数量,地址備注,주소 アドレス付記,出金アドレス';
                   break;
                default:
                     str = '状态,币种,数量,地址备注,地址标签,提现地址';
            }
        }

        sdata.forEach((item,index)=>{
            str += '\n'+item+','
        })
        // Excel打开后中文乱码添加如下字符串解决
        let exportContent = "\uFEFF";
        let blob = new Blob([exportContent + str], {
            type: "text/plain;charset=utf-8"
        });
        // const defaultCellStyle = { font: { name: "Verdana", sz: 13, color: "FF00FF88" }, fill: { fgColor: { rgb: "FFFFAA00" } } };
        // let wopts = { bookType: 'xlsx', bookSST: false, type: 'binary', cellStyles: true, defaultCellStyle: defaultCellStyle, showGridLines: false }  //写入的样式
        // let wbout = XLSX.write(wb, wopts)
        // let blob = new Blob([exportContent + str], { type: 'application/octet-stream' })
        // FileSaver.saveAs(blob, `${chartName}.xlsx`);
        FileSaver.saveAs(blob, `${chartName}.csv`);
    }
    handlePageChanged(newPage){
        const {pageSize,timeType} = this.state
        // console.log(this.props)
        let coint = this.props.type ==2?this.state.optionVal:this.props.curCoin;
        let conf = {
            pageIndex:newPage,
            pageSize:pageSize,
            coint,
            timeType,
        }

        this.props.fetchRecord(conf)
    }
      //时间 Tab
      handleChangeTime(id){
        if(this.state.totalCount > this.state.pageSize){
            this.refs.pages.resetPage();
        }
        this.setState({
            timeType:id,
            pageIndex:PAGEINDEX
        }, () =>{
            let coint = this.props.type ==2?this.state.optionVal:this.props.curCoin;
            let obj = {
                pageIndex:this.state.pageIndex,
                pageSize:this.state.pageSize,
                coint,
                timeType:this.state.timeType,
            }
            // console.log(obj)
            this.props.fetchRecord(obj)
        })
    }

    handleCancelOut(id,fundstype){
        const Mstr = <div className="modal-btn">
                        <p><FormattedMessage id="确定要取消吗？" /></p>
                        <div className="modal-foot">
                            <a onClick={this.handleCloseModal} className="btn"><FormattedMessage id="withdraw.text51" /></a>
                            <a className="btn ml10" onClick={() => this.handleConfirmCancel(id,fundstype)}><FormattedMessage id="withdraw.text49" /></a>
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

    handleConfirmCancel(did,fundstype){
       this.props.fetchCancel({did,fundstype},(res)=>{
           let result = res.data
           let state = (/<([^>]+)>([^<>]+)<\/\1>/gm.exec(result))[2]
           let des = (/<([^>]+)>([^<>]+)<\/MainData>/g.exec(result))[2]
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
        if(this.state.totalCount > this.state.pageSize){
            this.refs.pages.resetPage();
        }
        this.setState({
            optionVal: item.val,
            pageIndex:PAGEINDEX
        },() => {
            const {pageIndex,pageSize,optionVal} = this.state
            let conf = {
                pageIndex:pageIndex,
                pageSize:pageSize,
                coint:optionVal,
                timeType:this.state.timeType,
            }
            this.props.fetchRecord(conf)
        });
    }

    render(){
        const {isloading,isloaded} = this.props.hisRecord
        const {timeType,pageSize,pageIndex,totalCount,optionVal} = this.state
        const {type,selectOption} = this.props
        const propTag = this.props.propTag
        return (
            <div className="bk-new-tabList select-new">
               {type!==2&& <h2 className="bk-tabList-hd"><FormattedMessage id="withdraw.text21" /></h2>}
                <div className="entrust-head">
                {/* {type==2&&selectOption.length>0&& <div className="left">
                    <span className="select-tit"><FormattedMessage id="币种" /></span><SelectEntrust list={selectOption} currentCode={optionVal} getCode={this.getCode}></SelectEntrust>
                </div>} */}
                {type==2&&<div className="left">
                        <div className="entrust-leftt">
                            <div className="entrust-head-market left">
                                <h5 className="left padl10"><FormattedMessage id="币种" /></h5>
                                <div className="record-head entrust-selcet">

                                {/* <Select
                                    value={optionVal}
                                    clearable={false}
                                    searchable={false}
                                    onChange={this.getCode}
                                    options={selectOption}
                                    /> */}
                                     <SelectHistory
                                        defaultValue={optionVal}
                                        options={selectOption}
                                        class="sm left marginleft5"
                                        Cb={this.getCode}
                                    />
                                </div>

                            </div>
                        </div>
                        </div>}
                    <div className={`entrust-time left ${type==2?'padl20':''}`}>
                        <h5 className="padl10"><FormattedMessage id="时间（资产管理）" /></h5>
                        <ul className="tab-time">
                        <li>

                            <label  className={timeType== 0?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'}  onClick={() => this.handleChangeTime(0)}></label>
                            <span> <FormattedMessage id="全部" /></span>
                        </li>
                        <li>
                                <label  className={timeType==1?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(1)}></label>
                                <span>  <FormattedMessage id="7天内" /></span>
                        </li>
                        <li>
                                <label  className={timeType==2?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(2)}></label>
                                <span>  <FormattedMessage id="15天内" /></span>
                        </li>
                        <li>
                                <label  className={timeType==3?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(3)}></label>
                                <span>  <FormattedMessage id="30天内" /></span>
                        </li>
                        </ul>
                    </div>
                    {this.state.hisRecord.length > 0?<ExportModal boxname={<FormattedMessage id="导出历史提现记录" />} fomartexportData={()=>this.fomartexportData(this.state.hisRecord)}></ExportModal>:<div className="export-dis right"><FormattedMessage id="导出历史提现记录" /></div>}
                </div>
                <div className="bk-new-tabList-bd">
                    <table className="table-history" width="100%">
                        <thead>
                            <tr>
                                <th><FormattedMessage  id="deposit.text10" /></th>
                                <th className="text-center"><FormattedMessage  id="deposit.text11" /></th>
                                <th className="text-center"><FormattedMessage  id="deposit.text12" /></th>
                                <th className="text-center" style={{padding:'12px 18px'}}><FormattedMessage  id="bbyh地址备注" /></th>
                                {
                                    this.props.isEosType &&
                                    <th className="text-center"><FormattedMessage  id="bbyh地址标签" /></th>
                                }
                                <th className="text-left borright"><FormattedMessage  id="bbyh提现地址" /></th>
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
                                            let time = item.confirmTime?item.confirmTime.time:item.submitTime?item.submitTime.time:'--'
                                            let date = time=='--'?'--':formatDate(new Date(time)).replace(',', '').replace(/\//g, this.sp)
                                            return (
                                                <tr key={index} className="bk_payInOut_tr">
                                                    <td>
                                                        <p className="confirm-detail clearfix">
                                                            {(item.status == 0 || item.status >3)?
                                                                item.commandId > 0?<FormattedMessage  id="withdraw.text31" />:
                                                                    item.commandId == 0?<FormattedMessage  id="withdraw.text32" />:
                                                                    "":""
                                                                }
                                                            {item.status == 1&&<FormattedMessage  id="withdraw.text35" />}
                                                            {item.status == 2&&<FormattedMessage  id="withdraw.text33" />}
                                                            {item.status == 3&& <FormattedMessage  id="withdraw.text34" />}
                                                            {(item.status <= 0 && item.commandId <=0)?('VDS生态回馈提现'.includes(item.remark) ? null : <a className="btn-cancel" onClick={() => this.handleCancelOut(item.id,item.fundsTypeName)} href="javascript:void(0)"><FormattedMessage  id="withdraw.text36" /></a>):''}
                                                        </p>
                                                        {date}
                                                    </td>
                                                    <td className="text-center">{item.fundsTypeName}</td>
                                                    <td className="text-center">{new BigNumber(item.amount).toFixed(COIN_KEEP_POINT)}</td>
                                                    <td className="text-center width120">{item.addressMemo?item.addressMemo:'--'}</td>
                                                    {   this.props.isEosType &&
                                                         <td className="text-center width120">{item.memo}</td>
                                                    }

                                                    <td>
                                                        <div className="address_span">{item.toAddress}</div>
                                                        {item.txId!=0&&
                                                        // <div className="inline_block">
                                                        <p className="txid-detail">
                                                            <span className="txid">Txid:</span>
                                                            <span className='urlid'>{item.txId}</span>

                                                        </p>}
                                                        {item.txId!=0&&<a href={item.webUrl} className="btn-check2" title={item.txId} target="_blank"><FormattedMessage  id="检查"/></a>}
                                                         {/* </div> */}

                                                    </td>
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
                                                    <FormattedMessage id="当前没有提现记录。"/>
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
                <div style={{height:'20px'}}></div>
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
                                ref="pages"
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




























