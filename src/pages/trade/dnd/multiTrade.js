import React from 'react'
import { connect } from 'react-redux'
import { fetchMultiTrade,receiveMutili } from '../../../redux/modules/trade'
import Dustbin from './dustbin'
import ItemTypes from './ItemTypes'
const update = require('immutability-helper')
import cookie from 'js-cookie'
import './dustbin.less'

class Container extends React.Component{
	constructor(props) {
		super(props)
		this.state = {
			dustbins: [
				{ accepts: [ItemTypes.FOOD], lastDroppedItem: null },
				{ accepts: [ItemTypes.FOOD], lastDroppedItem: null },
				{ accepts: [ItemTypes.FOOD], lastDroppedItem: null },
				{ accepts: [ItemTypes.FOOD], lastDroppedItem: null },
			],
			boxes: [
				{ name: 'Bottle', type: ItemTypes.GLASS },
				{ name: 'Banana', type: ItemTypes.FOOD },
				{ name: 'Magazine', type: ItemTypes.PAPER },
			],
			showMenu:true
		}
		this.handleDrop = this.handleDrop.bind(this)
		this.deleteDroppedItem = this.deleteDroppedItem.bind(this)
		this.cookieStore = this.cookieStore.bind(this)
	}

	handleDrop(index, item){
		//2.新增  4.替换
		const oldItem = this.state.dustbins[index].lastDroppedItem
		const opeType = oldItem?4:2;
		this.setState(
			update(this.state, {
				dustbins: {
					[index]: {
						lastDroppedItem: {
							$set: item,
						},
					},
				}
			})
		)
		if(this.props.user){
			const oldScreen = oldItem?`${oldItem.coinName}/${oldItem.markName}`:''
			this.props.fetchMultiTrade({
				multiScreen:`${item.coinName}/${item.markName}`,
				type:opeType,
				multiScreenOld:oldScreen,
				group:4-index
			})
		}else{
			this.cookieStore(item,opeType,index)
		}

	}

	//在 新增2 和 替换4 时候 进行 cookie 操作如果是游客那么就操作localStorage
	cookieStore(item,opeType,index){
		if(cookie.get("zuid")==undefined){
			let cookieStr = localStorage.getItem("multiTrade")||'-'.repeat(3)
			let cookieArr = cookieStr.split("-")
			const newCookieArr =  cookieArr.map((element,itemIndex)=> {
				if(itemIndex == (3-index)){
					return element = `${item.coinName}/${item.markName}`
				}
				return element
			})
			localStorage.setItem("multiTrade",newCookieArr.join("-"))
		}else{
			let cookieStr = cookie.get("multiTrade")||'-'.repeat(3)
			let cookieArr = cookieStr.split("-")
			const newCookieArr =  cookieArr.map((element,itemIndex)=> {
				if(itemIndex == (3-index)){
					return element = `${item.coinName}/${item.markName}`
				}
				return element
			})
			cookie.set("multiTrade",newCookieArr.join("-"))
		}
	}

	//删除 k 线图标
	deleteDroppedItem(index){
		const coinMarket = this.state.dustbins[index].lastDroppedItem
		this.setState(
			update(this.state, {
				dustbins: {
					[index]: {
						lastDroppedItem: {
							$set: null,
						},
					},
				}
			})
		)
		//3.删除
		if(this.props.user){
			this.props.fetchMultiTrade({
				multiScreen:`${coinMarket.coinName}/${coinMarket.markName}`,
				type:3,
				group:4-index
			})
			//在删除的时候保存一个cook证明当前index被用户删除过并且把当前的index内的框架清除
			var nis=cookie.get("zindex")
			var niss=(nis==undefined)?"":nis
			var zw=niss+"original_kline"+index
			cookie.set("zindex",zw)
		}else{
			//当前是游客身份，那么存储localStorage
			localStorage.getItem('zindex')
			const cookieArr = localStorage.getItem('multiTrade').split("-")
			//在删除的时候保存一个cook证明当前index被用户删除过并且把当前的index内的框架清除
			var nis=localStorage.getItem('zindex')
			var niss=(nis==undefined)?"":nis
			var zw=niss+"original_kline"+index
			localStorage.setItem("zindex",zw)
			const newCookieArr =  cookieArr.map((element,itemIndex)=> {
				if(itemIndex == (3-index)){
					return element = ""
				}
				return element
			})
			localStorage.setItem("multiTrade",newCookieArr.join("-"))
		}
	}

	componentDidMount() {
		if(this.props.user){
			this.props.fetchMultiTrade()
		}else{
			const multiArr = localStorage.getItem('multiTrade')&&localStorage.getItem('multiTrade').split("-")
			if(multiArr&&multiArr.length){
				this.props.receiveMutili(multiArr.reverse())
			}
		}
	}

	componentWillReceiveProps(nextProps){
		this.updateStateDustbins(nextProps.mutli)
	}

	//更新 state 中的 dustbins
	updateStateDustbins(data){
		if(data.length > 0){
			for(let i=0;i < data.length;i++){
				const dataItem = data[i].replace(/^\s*|\s*$/g,"")
				if(!dataItem){
					continue
				}
				const coinMarket = dataItem.split("/")
				const item = {coinName: coinMarket[0], markName: coinMarket[1]}
				this.setState(pre => {
					const nowState = update(pre.dustbins, {
						[i]: {
							lastDroppedItem: {
								$set: item,
							},
						}
					})
					return {dustbins:nowState}
				})
			}
		}
	}


	render() {
		//throw new Error(456)
		const { dustbins } = this.state
		return (
			<div className='dustbin-box' style={{ overflow: 'hidden', clear: 'both' }}>
				{dustbins.map(({ accepts, lastDroppedItem }, index) => (
					<Dustbin
						accepts={accepts}
						lastDroppedItem={lastDroppedItem}
						// tslint:disable-next-line jsx-no-lambda
						onDrop={item => this.handleDrop(index, item)}
						handleDrop = {this.handleDrop}
						key={index}
						index={index}
						deleteDroppedItem = {this.deleteDroppedItem}
					/>
				))}
			</div>
		)
	}
}

const mapStateToProps = (state,ownProps) => ({
	mutli:state.trade.mutli,
	user:state.session.user
})

const mapDispatchToProps = (dispatch) => {
	return {
		fetchMultiTrade:(params) => {
			dispatch(fetchMultiTrade(params))
		},
		receiveMutili:(data) => {
			dispatch(receiveMutili(data))
		}
	}
}

export default connect(mapStateToProps,mapDispatchToProps)(Container)