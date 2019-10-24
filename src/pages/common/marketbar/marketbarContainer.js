import React from 'react';
import { connect } from 'react-redux';

import Marketbar from './marketbar';
import { fetchMarketsData } from '../../../redux/modules/markets'

const mapStateToProps = (state) => {
    return {
        marketsConf: state.marketsConf,
        markets: state.markets,
    };
}

const mapDispatchToProps = (dispatch, ownProps) => {
    return {
        fetchMarketsData: () => {
            dispatch(fetchMarketsData());
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(Marketbar);