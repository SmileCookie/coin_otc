import React from 'react';

import { FETCH_TRADE_INTERVAL } from '../../../conf';
import { formatMarkets } from '../../../utils';

class Marketbar extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            marketsData: null
        };
    }
    componentDidMount() {
        this.timer = setInterval(()=>{
            this.props.fetchMarketsData();
        }, FETCH_TRADE_INTERVAL);
    }
    componentWillReceiveProps(nextProps) {
        if(nextProps.markets.isLoaded) {
            let marketsData = formatMarkets(nextProps.markets.marketsData, this.props.markets.marketsData);
            this.setState({
                marketsData: marketsData
            })
        }
    }
    componentWillUnmount() {
        clearInterval(this.timer);
    }
    render() {
        const marketsData = this.state.marketsData;
        return (
            <div className="marketbar clearfix">
                {
                    marketsData?(
                        marketsData.map(
                            (item, index) => {

                                return (
                                    <div key={item["key"]} className="marketbar-item">
                                        <span>{item["symbol"]}&nbsp;:&nbsp;{item["price"]}</span>
                                        <span className="span-range"><em className={item["rangeOf24h"]>0?"up":item["rangeOf24h"]<0?"down":""}>{item["rangeOf24h"]}</em></span>
                                    </div>
                                );
                            }
                        )
                    ):(
                        <div>Loading</div>
                    )
                }
            </div>
        )
    }
}

export default Marketbar;