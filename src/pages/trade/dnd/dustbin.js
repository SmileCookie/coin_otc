import React from 'react'
import { DropTarget, ConnectDropTarget, DropTargetMonitor } from 'react-dnd'
import ItemTypes from './ItemTypes'
import Kline from '../../../components/originalKline'
import cookie from 'js-cookie'
import { Link } from 'react-router'
import MarketinfoContainer from '../marketinfo/marketinfoContainer';
import KlineCoinList from './klineCoinList'
import { FormattedMessage, injectIntl } from 'react-intl';

const dustbinTarget = {
	drop(props: DustbinProps, monitor: DropTargetMonitor) {
		props.onDrop(monitor.getItem())
	},
}

@DropTarget(
	ItemTypes.FOOD,
	dustbinTarget,
	(connect, monitor) => ({
		connectDropTarget: connect.dropTarget(),
		isOver: monitor.isOver(),
		canDrop: monitor.canDrop(),
	}),
)
export default class Dustbin extends React.Component{

    constructor(props){
		super(props)
		this.state = {
			showBtn:false,
			moadlState:false,
			curIndex:0,
			timeVal:"D",
			styleVal:"1",
			isShow:0,
			klineSymbol:''
		}
		this.showBtnFuc = this.showBtnFuc.bind(this)
		this.hideBtnFuc = this.hideBtnFuc.bind(this)
		this.addKlineModal = this.addKlineModal.bind(this)
		this.getMarketName = this.getMarketName.bind(this)
	}

	componentDidMount() {
		window.addEventListener('click',this.hideBtnFuc)
	}
	
	componentWillUnmount(){
		window.removeEventListener('click',this.hideBtnFuc)
	}

	showBtnFuc(e){
		const { showBtn } = this.state
		this.setState({
			showBtn:!showBtn
		})
		e.nativeEvent.stopImmediatePropagation();
	}

	hideBtnFuc(){
        this.setState({
            showBtn:false
        })
	}
	//添加 K 线图
	addKlineModal(val){
		cookie.get("skin")
		const stateVal = val||!this.state.moadlState
		this.setState({
			moadlState:stateVal
		})
	}

	//去市场  去掉左右空格
	getMarketName(str){
		return str.toLowerCase().replace(/^\s*|\s*$/g,"")
	}
	render() {

		const { accepts,isOver,canDrop,connectDropTarget,lastDroppedItem,index } = this.props
		const { showBtn,moadlState } = this.state
		const isActive = isOver && canDrop
		let klineCoin = '',markOpacity = 1;
		let backgroundColor = 'rgba(47,52,63,1)'
		let lang=cookie.get("zlan") == 'cn' ? 'zh' : cookie.get("zlan")
		let skin=cookie.get("skin")
		if(skin==undefined){
			skin="dark"
		}
		if(lastDroppedItem){
			klineCoin = this.getMarketName(lastDroppedItem.coinName)+"_"+this.getMarketName(lastDroppedItem.markName);
			backgroundColor = 'rgba(47,52,63,0.80)';
		}
		
		// {isActive ? 'Release to drop': ''}
		// {lastDroppedItem && (
		// 	<span>Last dropped: {lastDroppedItem.coinName}-{lastDroppedItem.markName}</span>
		// )}

		return (
			connectDropTarget &&
			connectDropTarget(
				<div className={`dustbin dustbin-${index}`} >
					<div className="dustbin-con">
						{lastDroppedItem==null&&<div className="dustbin-default">
							<p><FormattedMessage id="可以通过从右侧市场中拖拽某一币种，或点击下方按钮添加。" /></p>
							<div className="dustbin-add-btn" onClick={this.addKlineModal}>
								<i className="iconfont icon-iconfontadd"></i>
								<FormattedMessage id="+添加K线图" />
							</div>
						</div>}
						{
							lastDroppedItem&&
							<div className="kline-dust-box plv">
								{canDrop&&<div className="kline-dust-box-mark"></div>}
								<MarketinfoContainer
									currentMarket ={klineCoin}
								/>
								<div className="tab-box">
									<Kline
										currentMarket={klineCoin}
										lang={lang}
										skin={skin}
										index={index}
									/>
								</div>
								<div className="kline-dust-oper bbyh-kdp">
									<Link className="trade-link" to={`/bw/trade/${klineCoin}`}><FormattedMessage id="交易-多屏" /></Link>
									<div className={`kline-dust-edit kline-dust-edit-${index}`}>
										<p onClick={this.showBtnFuc}><FormattedMessage id="编辑" /></p>
										{
											showBtn&&
											<ul>
												<li onClick={() => this.props.deleteDroppedItem(index)}><FormattedMessage id="删除" /></li>
												<li onClick={() => this.addKlineModal(true)}><FormattedMessage id="替换" /></li>
											</ul>
										}
									</div>
								</div>
							</div>
								
						}
						{
							isActive&&<div className="dustbin-market" style={{backgroundColor:backgroundColor}}>
								<b><FormattedMessage id="拖拽到此区域松开鼠标完成添加或替换" /></b>
							</div>	
						}
						{ moadlState&&
							<KlineCoinList
								curIndex = {index}
								handleDrop = {this.props.handleDrop}
								addKlineModal = {this.addKlineModal}
							/>
						}
					</div>
				</div>
			)
		)
	}
}