import React from 'react';
import { COIN_KEEP_POINT } from '../../../conf'
import { FormattedMessage} from 'react-intl';

export default class obtainIntegral extends React.Component{
    render(){
        return(
            <div className="level-box">
                <h4 className="sub-tit mt30"><FormattedMessage id="level.obtainIntegral1" /></h4>
                <table className="table table-striped table-bordered text-left table-level">
                    <thead>
                        <tr>
                            <th><FormattedMessage id="level.obtainIntegral2" /></th>
                            <th><FormattedMessage id="level.obtainIntegral3" /></th>
                            <th><FormattedMessage id="level.obtainIntegral4" /></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><FormattedMessage id="level.obtainIntegral5" /></td>
                            <td>1000</td>
                            <td><FormattedMessage id="level.obtainIntegral6" /></td>
                        </tr>
                        <tr>
                            <td><FormattedMessage id="level.obtainIntegral7" /></td>
                            <td>10</td>
                            <td><FormattedMessage id="level.obtainIntegral8" /></td>
                        </tr>
                        <tr>
                            <td><FormattedMessage id="level.obtainIntegral9" /></td>
                            <td>100</td>
                            <td><FormattedMessage id="level.obtainIntegral10" /></td>
                        </tr>
                        <tr>
                            <td><FormattedMessage id="level.obtainIntegral11" /></td>
                            <td>100</td>
                            <td><FormattedMessage id="level.obtainIntegral12" /></td>
                        </tr>
                        <tr>
                            <td><FormattedMessage id="level.obtainIntegral13" /></td>
                            <td>100</td>
                            <td><FormattedMessage id="level.obtainIntegral14" /></td>
                        </tr>
                        <tr>
                            <td><FormattedMessage id="level.obtainIntegral15" /></td>
                            <td>2000</td>
                            <td><FormattedMessage id="level.obtainIntegral16" /></td>
                        </tr>
                        <tr>
                            <td><FormattedMessage id="level.obtainIntegral17" /></td>
                            <td>25</td>
                            <td><FormattedMessage id="level.obtainIntegral18" /></td>
                        </tr>
                        <tr>
                            <td><FormattedMessage id="level.obtainIntegral19" /></td>
                            <td>5000</td>
                            <td><FormattedMessage id="level.obtainIntegral20" /></td>
                        </tr>
                        <tr>
                            <td><FormattedMessage id="level.obtainIntegral21" /></td>
                            <td>25</td>
                            <td><FormattedMessage id="level.obtainIntegral22" /></td>
                        </tr>
                    </tbody>
                </table>
            </div>    
        )
    }
}