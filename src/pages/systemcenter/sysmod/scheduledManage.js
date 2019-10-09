import React from 'react'
import ScheduledLog from './scheduledLog';
import ScheduledTtask from './scheduledTtask';
import { pageLimit } from '../../../utils'

export default class ScheduledManage extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: 0,   //0 首页  1:日志 
            newsId:"" ,    //id
        }
        this.showHideClick = this.showHideClick.bind(this)
    }

    componentDidMount() {
                
    }

    showHideClick(index,ids) {
        this.setState({
            showHide: index,
            newsId: ids
        })
    }
    render() {
        const { limitBtn } = this.state
        return (
            <div>
                {
                    (() => {
                        switch (this.state.showHide) {
                            case 0:
                                return <ScheduledTtask  showHideClick={this.showHideClick} />
                                break;
                            case 1:
                                return <ScheduledLog showHideClick={this.showHideClick} newsId={this.state.newsId}/>
                                break
                            default:
                                break;
                        }
                    })()
                }

            </div>
        )
    }
}