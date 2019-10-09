import React from 'react'
import VoteIndex from './voteIndex'
import DistributeVote from './distributeVote'
import ViewResultsVote from './viewResultsVote'
import VoteLog from './voteLog';
import { pageLimit } from '../../../utils'


export default class VoteManage extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            showHide:0,   //0 首页  1:发布 2:修改 3:查看结果 4:投票详情
            activityId:false,
            coin_id:"",
            limitBtn: []
        }
        this.showHideClick = this.showHideClick.bind(this)
    }

    componentDidMount() {
        this.setState({
            limitBtn: pageLimit('voteManage', this.props.permissList)
        })
    }
    showHideClick(index, activityId, coin_id){
        this.setState({
            showHide: index,
            activityId: activityId,
            coin_id: coin_id
        })
    }
    render() {
        return (
            <div>
                {
                    (() => {
                        switch (this.state.showHide) {
                            case 0:
                                return <VoteIndex limitBtn={this.state.limitBtn} showHideClick={this.showHideClick} />
                                break;
                            case 1:
                                return <DistributeVote showHideClick={this.showHideClick}/>
                                break;
                            case 2:
                                return <DistributeVote showHideClick={this.showHideClick} activityId={this.state.activityId} />
                                break;
                            case 3:
                                return <ViewResultsVote showHideClick={this.showHideClick} activityId={this.state.activityId}/>
                                break;
                            case 4:
                                return <VoteLog showHideClick={this.showHideClick} activityId={this.state.activityId} coin_id={this.state.coin_id}/>
                                break;
                            default:
                                break;
                        }
                    })()
                }
                   
            </div>
        )
    }

}





























