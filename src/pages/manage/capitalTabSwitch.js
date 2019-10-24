import React from 'react'
import { FormattedMessage, injectIntl } from 'react-intl';
import { PAGEINDEX, PAGESIZETHIRTY, LOGINVIEWPORT, COIN_KEEP_POINT } from '../../conf';
import { connect } from 'react-redux';
import { capitalList } from '../../components/tab/tabdata';
import { fetchManageCoinRecord } from '../../redux/modules/deposit'
import { fetchRecord, fetchCancel } from '../../redux/modules/withdraw'
import { fetchCoinList } from '../../redux/modules/coinsList'
import { transferRecord } from '../../redux/modules/transfer'
import '../../assets/css/manegestyle/capitalTabSwitch.less';
import Nav from '../../components/navigator/nav';
import {MONEYMANAGEMENT} from '../../conf';
import ErrorComponent from '../common/ErrorComponent'

class capitalTabSwitch extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZETHIRTY,
            selectedCode: '0',
            optionVal: '',
            selectOption: [],
            dataError:false
        }
        this.tabConfig = capitalList(props.intl);
        this.setSelected = this.setSelected.bind(this)
    }
    componentDidMount() {
        const { pageIndex, pageSize } = this.state
        let conf = {
            pageIndex: pageIndex,
            pageSize: pageSize,
            coint: this.props.curCoin
        }
    }

    setSelected(flg) {
        this.setState({
            selectedCode: flg
        });
        const { pageIndex, pageSize, optionVal } = this.state
        let conf = {
            pageIndex: pageIndex,
            pageSize: pageSize,
            coint: optionVal
        }
        if (flg == 0) {
            this.props.fetchRecord(conf);
        } else if (flg == 1) {
            this.props.fetchManageCoinRecord(conf)
        } else {

        }
 

    }
    // componentDidCatch(err,infor){
    //     if(window.ERRORCONFIG){
    //         this.setState({
    //             dataError:true
    //         })
    //         console.log(err,infor)
    //     }else{
    //         console.log(err,infor)
    //     }
    // }

 
    render() {
        // if(this.state.dataError){
        //     return (
        //         <div className="bk-assets capitalTabSwitch">
        //             <div className="iconfont icon-jiazai new-loading" style={{top:'216px'}} ></div>
        //         </div>)
        // } 
        return (
            <ErrorComponent classNames="bk-assets capitalTabSwitch" styles={{top:'216px'}}>
                 <div className="bk-assets capitalTabSwitch">
                    <div className="history-box">
                        <div className="htb_sy0 mb30 capitalTab">
                            {/* <Nav path={this.props.location.pathname} ay={MONEYMANAGEMENT} /> */}
                        </div>
                        {this.props.children}
                    </div>
                </div>
            </ErrorComponent>
                   
        )
    }
}
const mapStateToProps = (state, ownProps) => {
    return {
        record: state.deposit.record,
        downRecord: state.withdraw.hisRecord,
        language: state.language.locale,
        coinList: state.coinList,
        transferData: state.transferData.record,
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
        fetchManageCoinRecord: (params) => {
            dispatch(fetchManageCoinRecord(params))
        },
        fetchRecord: (params) => {
            dispatch(fetchRecord(params))
        },
        fetchCancel: (params, cb) => {
            dispatch(fetchCancel(params)).then(cb)
        },
        transferRecord: (params) => {
            dispatch(transferRecord(params))
        },
        fetchCoinList: () => {
            dispatch(fetchCoinList())
        }
    }
}


export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(capitalTabSwitch))






