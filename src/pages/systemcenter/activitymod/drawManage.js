import React from 'react'
import DrawIndex from './drawIndex'
import DrawPush from './drawPush'
import ViewResultsDraw from './viewResultsDraw'
import DrawShowUser from './drawShowUser'
import { pageLimit } from '../../../utils'

export default class DrawManage extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            showHide: 0,   //0 首页   1：新增/修改  2:查看结果 3:查看用户抽奖结果明细
            activityId: false,
            userId:"",
            luckyId:"",
            limitBtn: []
        }
        this.showHideClick = this.showHideClick.bind(this)
    }

    componentDidMount() {
        this.setState({
            limitBtn: pageLimit('drawManage', this.props.permissList)
        })
    }
    showHideClick(index, activityId, luckyId, userId) {
        this.setState({
            showHide: index,
            activityId: activityId,
            luckyId: luckyId,
            userId: userId
        })
    }
    render() {
        return (
            <div>
                {
                    (() => {
                        switch (this.state.showHide) {
                            case 0:
                                return <DrawIndex limitBtn={this.state.limitBtn} showHideClick={this.showHideClick} />
                                break;
                            case 1:
                                return <DrawPush showHideClick={this.showHideClick} activityId={this.state.activityId}/>
                                break;
                            case 2:
                                return <ViewResultsDraw showHideClick={this.showHideClick} activityId={this.state.activityId} />
                            case 3:
                                return <DrawShowUser showHideClick={this.showHideClick} {...this.state} />
                            default:
                                break;
                        }
                    })()
                }

            </div>
        )
    }

}





























