import React from 'react'
import { FormattedMessage, injectIntl} from 'react-intl';
import { PAGEINDEX,PAGESIZETHIRTY,LOGINVIEWPORT,COIN_KEEP_POINT } from '../../conf';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import { connect } from 'react-redux';
import HTab from '../../components/tab/htab';
import { historyList,fbHistoryList,MoneyList } from '../../components/tab/tabdata';
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
import InvestmentR from './investmentR';
import ProfitR from './profitR';

const BigNumber = require('big.js')

class ChargeDownHistory extends React.Component{

    constructor(props){
        super(props)

        // 获取是由 币币 还是法币过来的  币币0 法币1 理财2
        this.historyType = props.location.query.type;
        // 理财指针区分理财下的模块--即理财的哪个selectCode
        this.fl = props.location.query.fl;


        this.state={
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZETHIRTY,
            selectedCode: 2 != this.historyType ? '0' : this.fl,
            optionVal:'',
            selectOption:[]
        }
        this.tabConfig = (2 == this.historyType ? MoneyList(props.intl) : fbHistoryList(props.intl));
        this.handleChange = this.handleChange.bind(this)
        this.setSelected = this.setSelected.bind(this)
        // this.getCode = this.getCode.bind(this)

        this.lcGroups = [
            null,
            <InvestmentR />,
            <ProfitR query={props.location.query}/>
        ]
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
                [0, 1, 2].includes(+this.historyType) &&
                <div className="history-box">
                  <div className="htb_sy0 mb30">
                  {
                      0 == this.historyType || 2 == this.historyType?
                        <HTab list={tabConfig} currentFlg={selectedCode} setSelected={setSelected} sp={2 == this.historyType ? 'fl=' : ''}></HTab>
                      :(
                        <ul className="tabwp clearfix">
                            <li className="ac" style={{border:0}}>
                                <a>
                                    <FormattedMessage id="划转记录" />
                                </a>
                            </li>
                        </ul>
                      )
                  }
                    </div>
                    <div className="record-head tit-space">
                    </div>
                 
                    {2!=this.historyType && selectedCode == 0&&<TransferHistory
                        curCoin = {optionVal}
                        pageSize={PAGESIZETHIRTY}
                        type={2}
                        selectOption= {selectOption}
                        fetchRecord={this.props.fetchRecord}
                        hisRecord={this.props.downRecord}
                        formatDate={this.props.intl.formatDate}/>}
                    {2!=this.historyType && selectedCode == 1&&<DistriButionHistory type={2}/>}

                    {
                        2==this.historyType && (
                            0 == selectedCode ?
                            <TransferHistory
                                curCoin = {optionVal}
                                pageSize={PAGESIZETHIRTY}
                                type={2}
                                selectOption= {selectOption}
                                fetchRecord={this.props.fetchRecord}
                                hisRecord={this.props.downRecord}
                                formatDate={this.props.intl.formatDate}/>
                            :
                            this.lcGroups[selectedCode]
                        )
                    }

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





















































