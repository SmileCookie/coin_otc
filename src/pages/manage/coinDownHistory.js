import React from 'react'
import axios from 'axios'
import { FormattedMessage, injectIntl} from 'react-intl';
import { PAGEINDEX,PAGESIZETHIRTY,LOGINVIEWPORT,COIN_KEEP_POINT } from '../../conf';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
import Pages from '../../components/pages'
import { connect } from 'react-redux';
import HTab from '../../components/tab/htab';
import { cointranList } from '../../components/tab/tabdata';
import { formatDate } from '../../utils'
import DistriButionHistory from './distriButionHistory'
import { fetchTransferList } from '../../redux/modules/transferList'
import {transferRecord} from '../../redux/modules/transfer'
import DownRecord from './withdraw/historyList'
import TransferHistory from './transfer/transferHistory'
// import Select from 'react-select'
import '../../assets/css/select.less';
import '../../assets/css/manegestyle/coinDownHistory.less'

const BigNumber = require('big.js')

class CoinDownHistory extends React.Component{

    constructor(props){
        super(props)
        this.state={
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZETHIRTY,
            selectedCode: '0',
            optionVal:'',
            selectOption:[],
            transferData:[],
           
        }
        this.tabConfig = cointranList(props.intl);
        this.handleChange = this.handleChange.bind(this)
        this.setSelected = this.setSelected.bind(this)
        // this.getCode = this.getCode.bind(this)
    }
    componentDidMount() {
        const {pageIndex,pageSize,from,to,timeTab} = this.state 
        let conf = {
            pageIndex:pageIndex,
            pageSize:pageSize,
            coint:this.props.curCoin
        }
        // let data = {
        //     pageIndex:pageIndex,
        //     pageSize:pageSize,
        //     from:from,
        //     to:to,
        //     timeTab:timeTab,
        // }
        // this.props.fetchTransferList(data)
        this.props.transferRecord(conf)
    }

    componentWillReceiveProps(nextProps){
        // console.log(nextProps)
        // if(nextProps.transferlist.isloaded){
        //     let selectOption = []
        //     for(let i=0;i < nextProps.transferlist.data.length; i++ ){
        //         let item = {}
        //         item.value = nextProps.transferlist.data[i].id
        //         item.label = nextProps.transferlist.data[i].name
        //         selectOption.push(item)
        //     }
        //     console.log(nextProps.transferData)
        //     console.log('--------------------')
        //     this.setState({
        //         selectOption,
        //         transferData:nextProps.transferData,
        //     })
        // }
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
            this.props.transferRecord(conf)
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
            this.props.transferRecord(conf)
        }
        
        
    }
    
    render(){
        const { optionVal,selectOption,selectedCode ,transferData} = this.state
        const {tabConfig,setSelected } = this
        return (
                <div className="history-box">
                  <div className="htb_sy0 mb30">
                        <HTab list={tabConfig} currentFlg={selectedCode} setSelected={setSelected}></HTab>
                    </div>
                    <div className="record-head tit-space">
                  
                    </div>
                    {selectedCode == 0&&<TransferHistory
                       curCoin = {optionVal}
                       pageSize={PAGESIZETHIRTY}
                       type={1}
                    //    selectOption= {selectOption}
                       transferData={transferData}
                       formatDate={this.props.intl.formatDate}/>}
                    {selectedCode == 1&&<DistriButionHistory type={2}/>}
                   
                </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        record: state.deposit.record,
        transferData:state.transferData.record,
        language:state.language.locale,
        // transferlist:state.transferlist
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
        transferRecord:(params)=>{
            dispatch(transferRecord(params))
        },
        fetchTransferList:(params) => {
            dispatch(fetchTransferList(params))
        }
    }
}


export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(CoinDownHistory))





















































