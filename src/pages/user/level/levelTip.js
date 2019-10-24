import React from 'react';
import { connect } from 'react-redux';
import { COIN_KEEP_POINT } from '../../../conf';
import { fetchLevelInfo,fetchIntegral } from '../../../redux/modules/level'
import { FormattedMessage, injectIntl ,FormattedDate} from 'react-intl';
import { separator } from '../../../utils';
const BigNumber = require('big.js');
class levelTip extends React.Component{
    componentDidMount(){
        this.props.fetchIntegral();
    }
    
    render(){
        const integral = this.props.integral;
        let occupy = "";
        let value = separator(integral.value);
        let levelBeginPoint = separator(integral.levelBeginPoint);
        let nextLevelBeginPoint = separator(integral.nextLevelBeginPoint);
        if(integral.value>=integral.nextLevelBeginPoint){
            occupy = "100%";
        }else{
            //big.js
            occupy = new BigNumber((integral.value-integral.levelBeginPoint)).div((integral.nextLevelBeginPoint-integral.levelBeginPoint)).times(100).toString()+"%";
        }
        if(this.props.mini){
            return(
                <div className="top-vipbox">
                    <div className="top-vipgrade clearfix">
                        <span className="left">VIP {integral.level}</span>
                        <div className="right">
                            <FormattedMessage id="level.tips3" />：
                            {value}
                        </div>
                    </div>
                    <div className="vip-rang clearfix">
                        <em className="left">{levelBeginPoint}</em>
                        <span className="full-rang" style={{width:occupy}}></span>
                        {
                            integral.level<10?(
                                <em className="right">{nextLevelBeginPoint}</em>
                            ):(
                                <em className="right"></em>
                            )
                        }
                    </div>
                </div>
            )
        }
        return (
            <div className="level-box">
                <h4 className="sub-tit mt30"><FormattedMessage id="level.tips1" />：VIP-{integral.level}</h4>
                <div className="vip-tip vip-tip2">
                    <dl className="ft16 clearfix">
                        <dt>VIP - {integral.level}</dt>
                            {
                                integral.level<10?(
                                    integral.value>=integral.nextLevelBeginPoint?(
                                        <dd className="reentry-jifen right">
                                        <FormattedMessage id="level.tips4" /> VIP-{integral.nextLevel}, <FormattedMessage id="level.tips5" />
                                        </dd>
                                    ):(
                                        <dd className="reentry-jifen right">
                                        <FormattedMessage id="level.tips6" /> <i>{separator(integral.nextLevelBeginPoint-integral.value)}</i> <FormattedMessage id="level.tips7" /> VIP- {integral.nextLevel}
                                        </dd>
                                    )
                                ):(
                                    <dd className="reentry-jifen right">
                                    </dd>
                                )
                            }
                    </dl> 
                    <div className="vip-rang">
                        {
                            integral.level<10?(
                                <div>
                                    <em className="figure-left">{levelBeginPoint}</em>
                                    <em className="figure-right">{nextLevelBeginPoint}</em>
                                </div>
                            ):(
                                <em className="figure-left">{levelBeginPoint}</em>
                            )
                        }
                        <span style={{width:occupy}}></span>
                    </div>
                    <div className="total-jifen bot12">
                    <FormattedMessage id="level.tips3" />：
                    <em className="figure-right">{value}</em>
                    </div>
                </div>
            </div>
        )
    }
}
const mapStateToProps = (state, ownProps) => {
    return {
        integral:state.level.integral
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchIntegral: () => {
            dispatch(fetchIntegral())
        }
    };
};
export default   connect(mapStateToProps,mapDispatchToProps)(levelTip);