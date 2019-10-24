import React from 'react'
import { FormattedMessage, injectIntl } from 'react-intl';
import { 
	DragSource,
	ConnectDragSource,
	DragSourceConnector,
	DragSourceMonitor,
} from 'react-dnd'
import ItemTypes from '../dnd/ItemTypes'


const boxSource = {
	beginDrag(props) {
		return {
			coinName: props.coinName,
			markName: props.markName
		}
	},
}

@DragSource(
	ItemTypes.FOOD,
	boxSource,
	(connect: DragSourceConnector, monitor: DragSourceMonitor) => ({
		connectDragSource: connect.dragSource(),
		isDragging: monitor.isDragging(),
	}),
)
export default class Box extends React.Component{
    constructor(props){
        super(props)
        this.state = {

        }
    }

	render() {
		const { isDropped, isDragging, activeClassName, connectDragSource,key,coinKey,fav,coinName,markName,showEstimated,price,tareMoney,rangeHtml,logo,range } = this.props
        const opacity = isDragging ? 0.4 : 1
        const rangColor = range>=0?'green':'red';
		return (
            connectDragSource &&
                connectDragSource(
                            <div key={coinKey} className={`coin sp ${activeClassName}`}>
                                    <div className="sidebar-list-market-box">
                                        <div className="sidebar-list-market">
                                            {fav?
                                                <span className="coin-fav coin-fav-true" onClick={(e)=>{this.props.removeFav(coinKey,e)}}>
                                                    &#xe6ad;
                                                    <span className="tips"><FormattedMessage id="取消自选"/></span>
                                                </span>:
                                                <span className="coin-fav" onClick={(e)=>{this.props.addFav(coinKey,e)}}>
                                                    &#xe6ac;
                                                    <span className="tips"><FormattedMessage id="加入自选"/></span>
                                                </span>
                                            }
                                            <span className="coin-name coin-name-fav">{coinName+"/"+markName}</span>
                                        </div>
                                        <div className="sidebar-list-price">
                                            {showEstimated?
                                                <span className={`coin-current-price coin-current-price-usdt ${rangColor}`}>
                                                    {price}&nbsp;<i>{logo} {tareMoney}</i>
                                                </span>
                                            :
                                                <span className="coin-current-price">
                                                    {price}
                                                </span>
                                            }
                                        </div>
                                            {rangeHtml}
                                    </div>
                                    <div style={{clear: 'both'}}></div>
                            </div>
                )
		)
	}
}