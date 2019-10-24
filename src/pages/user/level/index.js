import React from 'react';
import { FormattedMessage } from 'react-intl';
import { connect } from 'react-redux';
import { fetchLevelInfo } from '../../../redux/modules/level'
import Leveltip from './levelTip.js';
import Levelrule from './levelRule.js';
import Obtainintegral from './obtainIntegral.js';
import IntegralList from './integralList.js';

class Level extends React.Component{
    render(){
        return (
            <div className="cont-row">
                <div className="bk-top">
                    <h2><span><FormattedMessage id="manage.text3" /></span></h2>
                </div>
                <Leveltip />
                <Levelrule />
                <Obtainintegral />
                <IntegralList  /> 
            </div>
        )
    }
}
const mapStateToProps = (state, ownProps) => {
    return {
        integralLogs: state.level.integralLogs,
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchLevelInfo: () => {
            dispatch(fetchLevelInfo())
        }
    };
};
export default   connect(mapStateToProps,mapDispatchToProps)(Level);