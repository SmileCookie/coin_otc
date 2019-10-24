import React from 'react'
import { FormattedMessage, injectIntl} from 'react-intl';
import { PAGEINDEX,PAGESIZETHIRTY,LOGINVIEWPORT,COIN_KEEP_POINT } from '../../conf';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import { connect } from 'react-redux';
import HTab from '../../components/tab/htab';
import { historyListSp as historyList } from '../../components/tab/tabdata';
import { fetchManageCoinRecord } from '../../redux/modules/deposit'
import { fetchRecord,fetchCancel} from '../../redux/modules/withdraw'
import { fetchCoinList } from '../../redux/modules/coinsList'
import {transferRecord} from '../../redux/modules/transfer'
import ChargeRecord  from './deposits/record'
import DownRecord from './withdraw/historyList'
import DistriButionHistory from './distriButionHistory'
import TransferHistory from './transfer/transferHistory'
import '../../assets/css/select.less';
import '../../assets/css/manegestyle/chargeDownHistory.less'

const BigNumber = require('big.js')

class ChargeDownHistory extends React.Component{

    constructor(props){
        super(props)
        this.state={
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZETHIRTY,
            selectedCode: '0',
            optionVal:'',
            selectOption:[]
        }
        this.tabConfig = historyList(props.intl);
        this.handleChange = this.handleChange.bind(this)
        this.setSelected = this.setSelected.bind(this)
        // this.getCode = this.getCode.bind(this)
    }
    componentDidMount() {
        const {pageIndex,pageSize} = this.state 
        let conf = {
            pageIndex:pageIndex,
            pageSize:pageSize,
            coint:this.props.curCoin
        }
        this.props.fetchRecord(conf);
        this.props.fetchManageCoinRecord(conf)
        this.props.fetchCoinList()
        this.props.transferRecord(conf)
    }

    componentWillReceiveProps(nextProps){
        if(nextProps.coinList.isloaded){
            let selectOption = [{val:"",key:<FormattedMessage id="全部"/>}]
            for(let i=0;i < nextProps.coinList.data.length; i++ ){
                let item = {}
                item.val = nextProps.coinList.data[i]
                item.key = nextProps.coinList.data[i]
                selectOption.push(item)
            }
            this.setState({
                selectOption
            })
        }
    }
    //select 改变时 
    handleChange(selectedOption){
        this.setState({
            optionVal:selectedOption.value
        },() => {
            const {pageIndex,pageSize,optionVal} = this.state 
            let conf = {
                pageIndex:pageIndex,
                pageSize:pageSize,
                coint:optionVal
            }
            this.props.fetchRecord(conf);
            this.props.fetchManageCoinRecord(conf)
        })
    }

    setSelected(flg){
        this.setState({
            selectedCode: flg
        });
        const {pageIndex,pageSize,optionVal} = this.state 
        let conf = {
            pageIndex:pageIndex,
            pageSize:pageSize,
            coint:optionVal
        }
        if(flg==0){
            this.props.fetchRecord(conf);
        }else if(flg == 1){
            this.props.fetchManageCoinRecord(conf)
        }else{

        }
        
        
    }
  
    
    render(){
        const { optionVal,selectOption,selectedCode } = this.state
        const {transferData} = this.props
        const {tabConfig,setSelected } = this
        return (
                <div className="history-box">
                  <div className="htb_sy0 mb30">
                        <HTab list={tabConfig} currentFlg={selectedCode} setSelected={setSelected}></HTab>
                    </div>
                    <div className="record-head tit-space">
                    </div>
                    {selectedCode == 0&&<ChargeRecord
                        currentCoin = {optionVal}
                        pageSize={PAGESIZETHIRTY}
                        type={2}
                        optionVal={optionVal}
                        selectOption= {selectOption}
                        // getCode = {getCode}
                        fetchManageCoinRecord={this.props.fetchManageCoinRecord} 
                        record={this.props.record}  
                        formatDate={this.props.intl.formatDate}/>}
                    {selectedCode == 1&&<DownRecord
                        curCoin = {optionVal}
                        pageSize={PAGESIZETHIRTY}
                        type={2}
                        selectOption= {selectOption}
                        fetchRecord={this.props.fetchRecord}
                        fetchCancel={this.props.fetchCancel}  
                        hisRecord={this.props.downRecord}
                        formatDate={this.props.intl.formatDate}
                    />}
                    {selectedCode == 2&&<DistriButionHistory type={2}/>}
                </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        record: state.deposit.record,
        downRecord:state.withdraw.hisRecord,
        language:state.language.locale,
        coinList:state.coinList,
        transferData:state.transferData.record,
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        notifSend: (msg) => {
            dispatch(notifSend(msg));
        },
        notifClear: () => {
            dispatch(notifClear());
        },
        notifDismiss: (msg) => {
            dispatch(notifClear(msg));
        },
        fetchManageCoinRecord:(params) => {
            dispatch(fetchManageCoinRecord(params))
        },
        fetchRecord:(params) => {
            dispatch(fetchRecord(params))
        },
        fetchCancel: (params,cb) =>{
            dispatch(fetchCancel(params)).then(cb)
        },
        transferRecord:(params)=>{
            dispatch(transferRecord(params))
        },
        fetchCoinList:() => {
            dispatch(fetchCoinList())
        }
    }
}


export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(ChargeDownHistory))





















































