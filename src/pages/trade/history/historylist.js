import React from 'react';
import { FormattedMessage, FormattedTime } from 'react-intl';
import ScrollArea from 'react-scrollbar'
import cookie from 'js-cookie'
const BigNumber = require('big.js');

class Historylist extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            preTime:0,
            dataList:[],
            lastTime:0,
            diffLength:0,
            tid:''
        }
        this.flashBtn = false
    }
    fmDate(d = 0){
        const dt = new Date(d);
        return this.addZero(dt.getHours()) + ':' + this.addZero(dt.getMinutes()) + ':' + this.addZero(dt.getSeconds());
    }
    addZero(d = 0){
        return d < 10 ? '0' + d : d;
    }
    componentWillReceiveProps(nextProps){
        if(this.props.data&&this.props.data.length>0&&nextProps.data&&nextProps.data.length>0){
            if(!this.state.tid){
                this.setState({
                    tid:this.props.data[0].tid
                })
            }else{
                //console.log(nextProps.data, '=====----->');
                const diffLength = nextProps.data.findIndex((item) => {return item.tid == this.state.tid})
                const diffTid = nextProps.data.findIndex((item) => {return item.tid == this.state.tid})
                this.setState({
                    diffLength,
                    tid:diffTid.tid
                })
            }
        }
    }
    shouldComponentUpdate(nextProps, nextState){
        if(nextProps.selectPK){
            return true;
        } else if(this.props.wsIsLoading||nextProps.wsIsLoading){
            return false
        }else if(!this.props.wsIsLoading&&!nextProps.wsIsLoading){
            return true
        }
    }

    render() {
        const { currentMarket,marketsConfData,data,mkIsLoading,wsIsLoading } = this.props;
        const { diffLength } = this.state
        const lan = cookie.get("zlan")
       
        return(
            <ScrollArea style={{position:'absolute',left:0,right:0,top:'52px',bottom:0}} className="trade-scrollarea">
                <table>
                    <tbody>
                        
                        {  mkIsLoading || this.props.selectPK ?
                            <tr><td><div className="iconfont icon-jiazai new-loading"></div></td></tr>
                            :
                            data == null || data.length == 0 ?(
                            <tr>
                                <td colSpan='4'>
                                    {
                                    <div className="alert_under_table">
                                        <i className="iconfont icon-tongchang-tishi norecord"></i>
                                        <FormattedMessage id="No.record"/>
                                    </div>  
                                    }
                                </td>
                            </tr>
                            ):(
                                data.map((item,index)=>{
                                    BigNumber.RM = 0;
                                    let exchangeBixDian = 0;
                                    try{
                                        exchangeBixDian = marketsConfData[currentMarket].exchangeBixDian
                                    } catch(e){
                                        
                                    }
                                    let numberBixDian = marketsConfData[currentMarket].numberBixDian
                                    let priceBig  = new BigNumber(item.price).toFixed(exchangeBixDian);
                                    let amountBig = new BigNumber(item.amount).toFixed(numberBixDian);
                                    const colorType = item.type=="buy"?'green':'red';
                                    return(
                                        <tr className={index<diffLength?`new_data_${colorType}`:''} key={item.tid} >
                                            <td width="30%">
                                            {
                                                'ko' === lan || 'kr' === lan || window.ieFlag ?
                                                this.fmDate(item.date*1000)
                                                :
                                                <FormattedTime 
                                                    value={new Date(item.date*1000)}
                                                    hour='numeric'
                                                    minute='numeric'
                                                    second='numeric'
                                                    hour12={false} />
                                                    

                                            }
                                            </td>
                                            <td width="35%">
                                                <span className={colorType}>{priceBig}</span>
                                            </td>
                                            <td width="35%">{amountBig}</td>
                                        </tr>
                                    )
                                }
                                )
                            )
                        }
                    </tbody>
                </table>
            </ScrollArea>
        )
    }
}
export default Historylist;