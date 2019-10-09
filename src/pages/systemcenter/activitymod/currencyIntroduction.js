import React from 'react'
import CurrencyIndex from './currencyIndex'
import DistributeCurrency from './distributeCurrency'
import ViewResultCurrency from './viewResultCurrency'

export default class CurrencyManage extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:0,//0 首页    1：新增 2：修改 3：查看结果 4：币种详情
            activityId:{},  
            fundsList:[]               
        }
        this.showHideClick = this.showHideClick.bind(this)
    }
    componentDidMount(){
        
    }
    showHideClick(index,activityId,fundsList){
        this.setState({
            showHide:index,
            activityId,
            fundsList,
            
        })
    }

    render(){

        return (
            <div>
                {
                    (() => {
                        switch(this.state.showHide){
                            case 0:
                                return <CurrencyIndex showHideClick={this.showHideClick}  />
                                break;
                            case 1:
                                return <DistributeCurrency showHideClick={this.showHideClick} showHideImg={this.state.showHide} fundsList={this.state.fundsList} />
                                break;
                            case 2:
                                return <DistributeCurrency showHideClick={this.showHideClick} activityId={this.state.activityId} />
                                break;
                            case 3:
                                return <ViewResultCurrency showHideClick={this.showHideClick} />
                                break;
                        }
                    })()
                }
            </div>
        )
    }
}