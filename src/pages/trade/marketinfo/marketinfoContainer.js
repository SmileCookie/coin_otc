import { connect } from 'react-redux';
import './marketinfo.css';
import Marketinfo from './marketinfo';
// import { fetchMarketInfo } from '../../../redux/modules/marketinfo';

const mapStateToProps = (state, ownProps) => {
    return {
        marketHistorydata:state.marketHistoryData.data,
        marketinfo: state.marketinfo,
        marketsConf: state.marketsConf.marketsConfData,
        money:state.money,
        markets: state.markets,
        klineCoin:ownProps.klineCoin,
        moneyLocale:state.money.locale,
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        // fetchMarketInfo:(market,fullName)=>{
        //     dispatch(fetchMarketInfo(market,fullName))
        // }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Marketinfo);
