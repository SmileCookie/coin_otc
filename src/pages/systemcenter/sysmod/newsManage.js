import React from 'react'
import NewsIndex from './newsIndex';
import DistributeNews from './distributeNews';
import { pageLimit } from '../../../utils'

export default class NewsManage extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: 0,   //0 首页  1:发布 2:修改
            newsId:"" ,    //id
            limitBtn: []
        }
        this.showHideClick = this.showHideClick.bind(this)
    }

    componentDidMount() {
        this.setState({
            limitBtn: pageLimit('news',this.props.permissList)
        })        
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
                                return <NewsIndex limitBtn={limitBtn} showHideClick={this.showHideClick} />
                                break;
                            case 1:
                                return <DistributeNews showHideClick={this.showHideClick} newsId={this.state.newsId}/>
                                break;
                            case 2:
                                return <div></div>
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