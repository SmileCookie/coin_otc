import { connect } from 'react-redux';
import './practice.css';
import { fetchMarketInfo,recieveMarketinfo,cleanMarket } from '../../../redux/modules/marketinfo';
import Practice from './practice';

const mapStateToProps = (state, ownProps) => {
    return {
        user: state.session.user,
        language: state.language.locale,
        isLoaded: state.marketinfo.isLoaded,
        marketinfo: state.marketinfo,
        marketsConf: state.marketsConf.marketsConfData,
        assetsDetail: state.assets.detail.data,
        moneyLocale:state.money.locale
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchMarketInfo:(market,fullName,depth)=>{
            dispatch(fetchMarketInfo(market,fullName,depth));
        },
        recieveMarketinfo:(data) => {
            dispatch(recieveMarketinfo(data))
        },
        cleanMarket:()=>{
            dispatch(cleanMarket())
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Practice);