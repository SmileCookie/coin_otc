import React from 'react';
import { FormattedMessage } from 'react-intl';
const BigNumber = require('big.js');
import ReactHighcharts from 'react-highcharts';
import { DepthConfig } from './depathConfing'
import {FETCH_MARKETS_DEPTH} from '../../../conf';

class Practice extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            chartHeight: document.body.clientHeight * 0.75 - 122,
            stel:0
        }

        this.chart = React.createRef()

    }

    componentDidMount() {
        // console.log(document.body.clientHeight)
        this.interval=setInterval(()=>{
            const {currentMarket} = this.props
            this.props.fetchMarketDepthChartData(currentMarket)
        },FETCH_MARKETS_DEPTH)
       
        this.deepHeight = setInterval(() =>{
            let offsetHeight = document.getElementsByClassName('trade-depth-content')[0].offsetHeight;
            if(offsetHeight !== this.state.chartHeight){
                // console.log('ok--------------->')
                this.setState({
                    chartHeight:offsetHeight
               })
            }
            
        },1000) 
        setTimeout(() =>{
            this.setState({
                stel:1
           })
        },1000)
    }
    
    componentWillUnmount() { 
        clearInterval(this.interval)
        clearInterval(this.deepHeight)
    }

    afterRender(){

    }

    render() {
         const { isLoaded } = this.props;
         const {chartHeight} = this.state;
        
         if(!isLoaded){
            
            return (
                <div className="trade-item trade-depth">
                    <div className="trade-item-title" >
                        <h4><FormattedMessage id="depthhighcharts.text1" /></h4>
                    </div>
                    <div className="trade-content">loading</div>
                </div>
            )
        }
        // const chartHeight = document.body.clientHeight * 0.75 - 122;
        // const chartHeight = document.getElementsByClassName('trade-depth-content')
        const config = DepthConfig(this.props,chartHeight)
        return (
            <div className={this.state.stel==0?"trade-depth-content opic":"trade-depth-content"}>
            <div className={this.state.stel==1?"":"iconfont icon-jiazai new-loading"}></div>
               
                <ReactHighcharts config={config} ref={ this.chart } domProps = {{id: 'chartId'}} callback = {this.afterRender}></ReactHighcharts>
            </div>
        );
    }
}

export default Practice;










