import React from 'react';
import { FormattedMessage, injectIntl} from 'react-intl';
import { COOKIE_LAN,PAGEINDEX,PAGESIZEFIVE,LOGINVIEWPORT,COIN_KEEP_POINT } from '../../../conf';
import cookie from 'js-cookie';
import { formatDate } from '../../../utils';
import Pages from '../../../components/pages';
import Select from 'react-select';
import SelectHistory from '../../../components/selectHistory'
import FileSaver from 'file-saver';
import ExportModal from '../../../components/export';
const BigNumber = require('big.js')
import '../balances/balances.less'

class Record extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:this.props.pageSize||PAGESIZEFIVE,
            billDetail:[],
            totalCount:0,
            timeType:0,
            optionVal:'',
        };
        this.dateFormat = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false,
        };

        // date split
        this.sp = '-';
        this.currentPageClick = this.currentPageClick.bind(this);
        this.handleChangeTime = this.handleChangeTime.bind(this);
        this.fomartexportData = this.fomartexportData.bind(this);
        this.getCode = this.getCode.bind(this);

    }
    componentDidMount(){

    }
    componentWillReceiveProps(nextProps){
        if(nextProps.record.isloaded){
            // console.log('ok=======2')
            // console.log(nextProps)
            this.setState({
                billDetail:nextProps.record.data.list,
                totalCount:nextProps.record.data.totalCount
            })
        }
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
            let coint = this.props.type ==2?this.state.optionVal:this.props.currentCoin;
            let obj = {
                pageIndex:this.state.pageIndex,
                pageSize:this.state.pageSize,
                coint,
                timeType:this.state.timeType,
            }

            this.props.fetchManageCoinRecord(obj)
            // console.log('ok=======1')
        })
    }
    fomartexportData(data){
        try{
            let {formatMessage} = this.props.intl;
            let chartName = '';
            let languaged = cookie.get(COOKIE_LAN);
            let sdata = []
            let localData =formatDate(new Date()).replace(/[\|\,|\:|\_|\ ]/g,'').replace(/\//g,'');
            if(data !==''){
                data.forEach((item,index)=>{

                    let ddata = []
                    let date = formatDate(new Date(item.configTime.time)).replace(',', '').replace(/\//g, this.sp)
                    let tradeTypesname = '';
                    let statusname = '';
                    if(languaged == 'cn'){
                        chartName = localData + '充值记录'
                    }else if (languaged == 'en'){
                        chartName = localData + 'deposit records'
                    }else if(languaged == 'hk'){
                        chartName = localData + '充值記錄'
                    }else if(languaged == 'jp'){
                        chartName = localData + '入金記録'
                    }else if(languaged == 'kr'){
                        chartName = localData + '입금 내역'
                    }
                    else{
                        chartName =  localData + '充值记录'
                    }

                    let txid = item['addHash']?' Txid:'+ item['addHash']:''

                    let frimtimes =  item['status'] == 0?item.confirmTimes/item.totalConfirmTimes:'';

                    let status = item['status'] == 1 ? formatMessage({id:'失败'}) : item['status'] == 2 ? formatMessage({id:'已成功'}) : formatMessage({id:'确认中'})

                    ddata.push(status +'  '+ frimtimes+ '  '+'Height:  '+item['blockHeight'])
                    ddata.push(date)
                    ddata.push(item['fundsTypeName'])
                    ddata.push(new BigNumber(item.amount).toFixed(COIN_KEEP_POINT))
                    ddata.push(item['toAddr']+ txid)
                    sdata.push(ddata)
                })



            }
            let str =  '';
            switch(languaged) {
                case 'cn':
                    str = '状态,更新时间,币种,数量,地址';
                    break;
                case 'en':
                    str = 'Status,Update time,Coin,Amount,Address';
                    break;
                case 'hk':
                    str = '狀態,更新时间,幣種,數量,地址';
                    break;
                case 'jp':
                    str = 'ステータス,更新時間,トークン,数量,アドレス';
                    break;
                case 'kr':
                    str = '상태,업데이트 시간,코인,수량,주소';
                    break;
                default:
                    str = '状态,更新时间,币种,数量,地址';
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
        }catch(e){

        }
    }
    currentPageClick (values){
        let {pageIndex} = this.state;
        pageIndex = values;
        let coint = this.props.type ==2?this.state.optionVal:this.props.currentCoin;
        this.setState({pageIndex}, () =>{
            let obj = {
                pageIndex:this.state.pageIndex,
                pageSize:this.state.pageSize,
                coint,
                timeType:this.state.timeType,
            }
            this.props.fetchManageCoinRecord(obj);
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
            this.props.fetchManageCoinRecord(conf)
        });
    }
    render(){
        const {timeType,totalCount,billDetail,pageIndex,pageSize,optionVal,resetPage} = this.state
        const {type,selectOption} = this.props
        return (
            <div className="bk-new-tabList select-new">
                {type!==2&& <h2 className="bk-new-tabList-bd"><FormattedMessage  id="deposit.text9"  /></h2>}
                <div className="entrust-head">
                    {/* {type==2&&selectOption.length>0&& <div className="left">
                        <span className="select-tit"><FormattedMessage id="币种" /></span>
                        <SelectEntrust list={selectOption} currentCode={optionVal} getCode={this.getCode}></SelectEntrust>

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
                                <span> <FormattedMessage id="7天内" /></span>
                            </li>
                            <li>
                                <label  className={timeType==2?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(2)}></label>
                                <span> <FormattedMessage id="15天内" /></span>
                            </li>
                            <li>
                                <label  className={timeType==3?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(3)}></label>
                                <span>  <FormattedMessage id="30天内" /></span>
                            </li>
                        </ul>
                    </div>
                    {billDetail.length > 0?<ExportModal boxname={<FormattedMessage id="导出历史充值记录" />} fomartexportData={()=>this.fomartexportData(billDetail)}></ExportModal>: <div className="export-dis right"><FormattedMessage id="导出历史充值记录" /></div>}
                </div>
                <div className="bk-tabList-bd">
                    <table className="table-history">
                        <thead>
                        <tr>
                            <th width="15%"><FormattedMessage  id="状态(确认)"/></th>
                            <th width="15%" className="text-center"><FormattedMessage  id="更新时间"/></th>
                            {/*<th width="10%"><FormattedMessage  id="deposit.text10"/></th>*/}
                            <th width="10%" className="text-center"><FormattedMessage  id="deposit.text11"  /></th>
                            <th width="10%" className="text-right"><FormattedMessage  id="deposit.text12"  /></th>
                            <th width="50%" className="text-left borright"><FormattedMessage  id="deposit.text13"  /></th>
                        </tr>
                        </thead>
                        <tbody>
                        {
                            billDetail.length > 0 ? (
                                billDetail.map((item,index) => {
                                    BigNumber.RM=0;
                                    let date = formatDate(new Date(item.configTime.time)).replace(',', '').replace(/\//g, this.sp)
                                    return (
                                        <tr key={index} className="deposits_tr">
                                            <td>
                                                {item.status==1?<FormattedMessage  id="失败"/>:item.status==2?<FormattedMessage  id="已成功"/>:<FormattedMessage  id="确认中"/>}
                                                {item.status == 0&&<span>({item.confirmTimes}/{item.totalConfirmTimes})</span>}
                                                <p className="block-height">
                                                    <span>Height: </span><span>{item.blockHeight}</span>
                                                </p>
                                            </td>

                                            <td className="text-center">{date}</td>
                                            {/*<td>*/}

                                            {/*        {item.status==1?<FormattedMessage  id="失败"/>:item.status==2?<FormattedMessage  id="已成功"/>:<FormattedMessage  id="确认中"/>}*/}
                                            {/*        {item.status == 0&&<span>({item.confirmTimes}/{item.totalConfirmTimes})</span>}*/}

                                            {/*    <p className="confirm-detail">*/}
                                            {/*    {date}*/}
                                            {/*    </p>*/}
                                            {/*</td>*/}
                                            <td className="text-center">{item.fundsTypeName}</td>
                                            <td className="text-right">{new BigNumber(item.amount).toFixed(COIN_KEEP_POINT)}</td>
                                            <td>

                                                <div className="txid-detail">
                                                    <div>{item.toAddr}</div>
                                                    <span className="txid"> Txid:</span>
                                                    <span className='urlid'>{item.addHash}</span>
                                                </div>
                                                {item.addHash?<a href={item.webUrl} className="btn-check" title={item.addHash} target="_blank"><FormattedMessage  id="检查"  /></a>:''}
                                            </td>
                                        </tr>
                                    )
                                })
                            ) : (
                                <tr>
                                    <td className="billDetail_no_list" colSpan="5">
                                        <p className="entrust-norecord">
                                            <svg className="icon" aria-hidden="true">
                                                <use xlinkHref="#icon-tongchang-tishi"></use>
                                            </svg>
                                            <FormattedMessage id="当前没有充值记录。"/>
                                        </p>
                                    </td>
                                </tr>
                            )
                        }
                        </tbody>
                    </table>
                </div>
                <div style={{height:'20px'}}></div>
                <div className="tablist deposits" style={{display:totalCount > pageSize?'block':'none'}}>
                    <Pages
                        pageIndex={pageIndex}
                        pagesize={pageSize}
                        total={totalCount}
                        currentPageClick = { this.currentPageClick }
                        ref="pages"
                    />
                </div>
            </div>
        )
    }
}
export default injectIntl(Record);




























