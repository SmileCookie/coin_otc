import { connect } from 'react-redux';
import './sidebar.css';
import { fetchMarketsData } from '../../../redux/modules/markets';
import Sidebar from './sidebar'; 
import { recieveMarket } from '../../../redux/modules/marketinfo';

const mapStateToProps = (state, ownProps) => {
    return {
        user: state.session.user,
        isLoaded: state.markets.isLoaded,
        markets: state.markets.marketsData,
        currentMarket:state.marketinfo.currentMarket,
        marketsConfLoaded:state.marketsConf.isLoaded,
        marketsConfData:state.marketsConf.marketsConfData,
        money:state.money,
        dropMenu:ownProps.dropMenu,
        pathName:ownProps.pathName
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchMarketsData:()=>{
            dispatch(fetchMarketsData());
        },
        recieveMarket:(market) => {
            dispatch(recieveMarket(market));
        }
    };
}
export default connect(mapStateToProps, mapDispatchToProps)(Sidebar);