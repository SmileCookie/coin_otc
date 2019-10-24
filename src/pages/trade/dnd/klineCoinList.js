import React from 'react'
import { connect } from 'react-redux'
import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import './dustbin.less'
import ScrollArea from 'react-scrollbar'

class KlineCoinList extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            coinSearch:'',
            btnStus:0,
        }

        this.clearFilterVal = this.clearFilterVal.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.addKlineBtn = this.addKlineBtn.bind(this)

        this.createCoinList = this.createCoinList.bind(this)
    }

    // 创建搜索币种列表
    createCoinList(marketsDataList = {}, coinSearch = '', filter = ''){
        return Object.keys(marketsDataList).filter((currentValue,index,arr) => {
            const filterCoin = currentValue.split("_")[0]+"/"+currentValue.split("_")[1]
            if(coinSearch){
                return filterCoin.includes(coinSearch.toLocaleLowerCase()) && (filter ? !currentValue.split("_")[1].includes(filter) : true)
            }
            return true;
            
        }).map((item,index) => {
            const coin = item.split("_")
            const dropItem = {
                coinName:coin[0].toUpperCase(),
                markName:coin[1].toUpperCase()
            }
            return <li key={item} onClick={() => this.addKlineBtn(dropItem)}>{coin[0].toUpperCase()}/{coin[1].toUpperCase()}</li>
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        if(name == 'coinSearch'&&value){
            this.setState(preState => {
                if(preState.btnStus != 1){
                    return {btnStus:1}
                }
            })
        }else{
            this.setState(preState => {
                if(preState.btnStus != 0){
                    return {btnStus:0}
                }
            })
        }
    }

    //清除弹窗
    clearFilterVal(){
        this.setState({
            coinSearch:'',
            btnStus:0
        })
    }
    //添加 k线图
    addKlineBtn(item){
        const { handleDrop,curIndex,addKlineModal } = this.props
        handleDrop(curIndex,item);
        addKlineModal(false);
    }

    render(){
        const { createCoinList } = this
        const { coinSearch,btnStus } = this.state
        const { isLoading, isLoaded, marketsData } = this.props.markets
        let marketsDataList = {}
        if(isLoaded){
            for(let key in marketsData){
                if(key != 'FAV'){
                    Object.assign(marketsDataList,marketsData[key])
                }
            }
        }

        return(
            <div className="dustbin-modal">
                <div className="dustbin-modal-header">
                    <div className="search-form">
                        <input type="text" placeholder={this.props.intl.formatMessage({id: '搜索'})} name="coinSearch" value={coinSearch} onChange={this.handleInputChange} />
                        <button onClick={btnStus==1?this.clearFilterVal:null} className={btnStus==0?"iconfont icon-search-bizhong":"iconfont icon-shanchu-moren"}></button>
                    </div>
                    <i className="iconfont icon-guanbi-moren" onClick={() => this.props.addKlineModal(false)} ></i>
                </div>
                <div className="dustbin-modal-con scr-wp">
                    <ScrollArea className="sa-wp">
                        <div className="dustbin-modal-tabPane">
                            <div className="gp-wp clearfix">
                                <div className="m">
                                    <h3 className="tith">USDT</h3>
                                    <ul className="dustbin-modal-tabPane-ul">
                                        {
                                            createCoinList(marketsDataList, coinSearch ? coinSearch : 'usdt', 'btc')
                                        }
                                    </ul>
                                </div>

                                <div className="m">
                                    <h3 className="tith">BTC</h3>
                                    <ul className="dustbin-modal-tabPane-ul">
                                        {
                                            createCoinList(marketsDataList, coinSearch ? coinSearch : 'btc', 'usdt')
                                        }
                                    </ul>
                                </div>
                                
                            </div>
                             
                        </div>
                    </ScrollArea>
                </div>
            </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        markets:state.markets
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(KlineCoinList)) ;










































