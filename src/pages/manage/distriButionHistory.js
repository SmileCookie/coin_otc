import React from 'react'
import { FormattedMessage, injectIntl} from 'react-intl';
import { PAGEINDEX,PAGESIZETHIRTY,LOGINVIEWPORT,COIN_KEEP_POINT,COOKIE_LAN } from '../../conf';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import Pages from '../../components/pages'
import {formatDate } from '../../utils';
import Select from 'react-select';
import cookie from 'js-cookie';
import { connect } from 'react-redux'
import ExportModal from '../../components/export';
import FileSaver from 'file-saver';
import { fetchDistriHistory } from '../../redux/modules/distriHistory'
import SelectHistory from '../../components/selectHistory'
const BigNumber = require('big.js')

class DistriButionHistory extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZETHIRTY,
            totalCount:0,
            tableList:[],
            type:'',
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

        this.currentPageClick = this.currentPageClick.bind(this)
        this.getCode = this.getCode.bind(this)
    }

    componentDidMount() {
        const{pageIndex,pageSize,type}=this.state
        let conf = {
            pageIndex,
            pageSize,
            type,
        }
        this.props.fetchDistriHistory(conf)
    }

    //分页
    currentPageClick (values){
        this.setState({
            pageIndex:values
        },() =>{
            const{pageIndex,pageSize,type}=this.state
            let conf = {
                pageIndex,
                pageSize,
                type,
            }
        this.props.fetchDistriHistory(conf)
        })
    }
    fomartexportData(data){
        let chartName = '';
        let languaged = cookie.get(COOKIE_LAN);
        let localData =formatDate(new Date()).replace(/[\|\,|\:|\_|\ ]/g,'').replace(/\//g,'');
        let sdata = []
        if(data !==''){
            data.forEach((item,index)=>{
                    let ddata = []
                    let tradeTypesname = '';
                    let date = formatDate(new Date(item['sendTime'])).replace(',', '').replace(/\//g, this.sp)
                    let showStatus = languaged == 'en'?'Success':languaged == 'hk'?'成功':'成功'
                    let srcName = '';
                    let dstName = '';
                    if(languaged == 'cn'){
                        chartName = localData + '分发记录';
                    }else if (languaged == 'en'){
                        chartName = localData + 'distribution history records';
                    }else if(languaged == 'hk'){
                        chartName = localData+  '分發記錄';
                    }else if(languaged == 'jp'){
                        chartName = localData+  '配分記録';
                    }else if(languaged == 'kr'){
                        chartName = localData+  '나누어 준 내역';
                    }
                    else{
                        chartName =localData + '分发记录';
                    }
                    
                    ddata.push(date)
                    ddata.push(item['typeView'])
                    // ddata.push(item['srcName'])
                    ddata.push(item['coinView'])
                    ddata.push(new BigNumber(item.amount).toFixed(COIN_KEEP_POINT))
                    ddata.push(item['sourceRemark'])
                    sdata.push(ddata)
            }) 
            console.log(sdata)
        }
        let str =  '';
        switch(languaged) {
            case 'cn':
                str = '日期,类型,币种,数量,备注';
                 break;
            case 'en':
                 str =  'Date,Type,Coin,Amount,Note';
                break;
            case 'hk':
                 str = '日期,類型,幣種,數量,備注';
                break;
            case 'jp':
                str = '日付,類型,幣種,數量,付記';
               break;
            case 'kr':
               str = '날짜,유형,코인,수량,비고';
              break;
            default:
                 str = '日期,类型,币种,数量,备注';
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

     getCode(item = {}){
        const {data} = this.props.distriList;
        const {pageSize} = this.state;
        if(data.totalCount > pageSize){
            this.refs.pages.resetPage();
        }
        this.setState({
            type: item.val,
            pageIndex:PAGEINDEX,
        },() => {
            const {pageIndex,pageSize,type} = this.state 
            let conf = {
                pageIndex:pageIndex,
                pageSize:pageSize,
                type:type
            }
            this.props.fetchDistriHistory(conf)
        });
    }
    render(){
        const { data,isloaded,isloading } = this.props.distriList
        const { pageIndex,pageSize ,type} = this.state
        let ttt = <FormattedMessage id="划出账户" />
        return (
            <div className="history-box select-new"> 
                
                 <div className="entrust-head">
                    <div className="entrust-head-market left">
                        <div className="record-head entrust-selcet" style={{width:'168px'}}>
                            

                            <SelectHistory 
                                        defaultValue = {type}
                                        options={[
                                            { val:'', key: <FormattedMessage id="全部" /> },
                                            { val:'201' , key: <FormattedMessage id="系统分发" /> },
                                            { val:'221,222', key: <FormattedMessage id="活动奖励" /> },                                    
                                        ]}
                                        class="sm2 left marginleft5"
                                        Cb={this.getCode}
                                    />
                        </div>
                        </div>
                        { isloaded&&data.list.length>0?<ExportModal boxname={<FormattedMessage id="导出历史分发记录" />} fomartexportData={()=>this.fomartexportData(data.list)}></ExportModal>:<div className="export-dis right"><FormattedMessage id="导出历史分发记录" /></div>}
                </div>
                <div className="record-box" style={{minHeight:'390px'}}>
                    <table className="table-history table-history-fixed">
                        <thead>
                            <tr>
                                <th><FormattedMessage  id="withdraw.text22" /></th>
                                <th style={{textAlign:"center"}}><FormattedMessage  id="account.text11" /></th>
                                <th><FormattedMessage  id="币种" /></th>
                                <th style={{textAlign:"right",paddingRight:"60px"}}><FormattedMessage  id="deposit.text12" /></th>
                                <th  className="borright"><FormattedMessage  id="withdraw.text70" /></th>
                            </tr>
                        </thead>
                        <tbody id="butionHistory">
                            {
                                isloaded&&data.list.length>0? data.list.map((item,index) => {
                                    BigNumber.RM = 0;
                                    let date = formatDate(new Date(item.sendTime)).replace(',', '').replace(/\//g, this.sp)
                                    return (
                                        <tr key={index}>
                                            <td>{date}</td>
                                            <td style={{textAlign:'center'}}>{item.typeView}</td>
                                            <td>{item.coinView}</td>
                                            <td  style={{textAlign:"right",paddingRight:"60px"}}>
                                                { new BigNumber(item.amount).toFixed(COIN_KEEP_POINT)}	
                                            </td>
                                            <td>{item.sourceRemark}</td>
                                        </tr>
                                    )
                                })
                                :
                                (
                                    <tr>
                                        <td className="billDetail_no_list" colSpan="10">
                                            <p className="entrust-norecord"> 
                                                <svg className="icon" aria-hidden="true">
                                                    <use xlinkHref="#icon-tongchang-tishi"></use>
                                                </svg>
                                                <FormattedMessage id="当前没有分发记录"/>
                                            </p>
                                        </td>
                                    </tr>
                                )
                            }
                        </tbody>
                    </table>
                    {
                        data.totalCount > pageSize?
                            <div className="pageCon" id="butionHistory_Page">
                                <Pages 
                                    pageIndex={pageIndex} 
                                    pagesize={pageSize} 
                                    total={data.totalCount}
                                    ref="pages"
                                    currentPageClick = { this.currentPageClick }
                                />
                            </div>:""
                    }
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state,ownProps) => {
    return {
        distriList: state.distriHistory,
    }   
}

const mapDispatchToProps = (dispatch) => {
    return {
        fetchDistriHistory:(params) => {
            dispatch(fetchDistriHistory(params))
        }
    }
}

export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(DistriButionHistory))



































