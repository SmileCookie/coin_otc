import { connect } from 'react-redux';
import './depthhighcharts.css';

import Depthhighchart from './depthhighchart';
import {fetchMarketDepthChartData} from '../../../redux/modules/marketdepthchartdata';
import {injectIntl} from 'react-intl';
const mapStateToProps = (state, ownProps) => {
    return {
        isLoaded: state.marketdepthchartdata.isLoaded,
        data:state.marketdepthchartdata.data,
        currentMarket: state.marketinfo.currentMarket,
        marketsConf: state.marketsConf.marketsConfData,
        skin:state.trade.skin
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchMarketDepthChartData:(market)=>{
            dispatch(fetchMarketDepthChartData(market));
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(Depthhighchart));