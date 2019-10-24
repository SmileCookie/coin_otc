import React from 'react'
import { connect } from 'react-redux';
import { Link } from 'react-router'
import { FormattedMessage, FormattedTime } from 'react-intl';
import ScrollArea from 'react-scrollbar'
import { PAGESIZEFIVE,PAGEINDEX } from '../../../conf'
import { fetchNews } from '../../../redux/modules/news'
import Pages  from '../../../components/pages'
import '../../../assets/css/news.less'

class News extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:15,
            newsData:null,
            total:0,
        }
        this.currentPageClick = this.currentPageClick.bind(this)
    }

    componentDidMount() {
        this.props.fetchNews()
    }

    componentWillReceiveProps(nextProps){
        const nextNewsList = nextProps.newsList
        if(nextNewsList.isloaded&&!nextNewsList.isloading){
            this.setState({
                pageIndex:nextNewsList.list.pageIndex,
                newsData:nextNewsList.list.datalist,
                total:nextNewsList.list.total
            })
        }
    }

    currentPageClick(index){
        this.props.fetchNews(index,() => {
            this.scrollbar.scrollArea.scrollTop(0)
        })
    }

    render() {
        const { pageIndex,newsData,pageSize,total } = this.state
        const { isloaded,isloading } = this.props.newsList
        if(!isloaded&&isloading){
            return <div className="new-box">
                        <div className="iconfont icon-jiazai new-loading"></div>
                   </div> 
        }

        return ( 
            <div className="new-box">
                <div className = "new-title">
                    <div className="new-title-box" >
                        <h3 style={{fontSize:'14px'}}><FormattedMessage id="新闻" /></h3>
                    </div> 
                </div>
                <ScrollArea ref={(scrollbar) => this.scrollbar=scrollbar} className="trade-scrollarea" >
                <div className = "new-con">
                    {newsData ? 
                        <div>
                            {
                                newsData.map((item,index)=>{
                                    return (
                                        <div className= "news-warp"  key={item.id}> 
                                            <div className= "news-left-img" >
                                                <Link to= {`${this.props.location.pathname}/newsdetail?id=${item.id}`} > 
                                                    <img alt={item.title} src={item.photo} /> 
                                                </Link> 
                                            </div> 
                                            <div className="news-warp-right">
                                                <div className = "news-title">
                                                    <span style={{color:'#3E85A2',fontSize:'16px'}}>{item.topInfo}</span>
                                                    <Link to = {`${this.props.location.pathname}/newsdetail?id=${item.id}`} >{item.title}</Link>
                                                </div> 
                                                <div className = "news-content" >{item.digest}</div>
                                                <div className="news-time">
                                                    <span className="time-img"></span> 
                                                    {item.pubTimeStr}
                                                </div>
                                                <div className="news-right">{item.source}</div> 
                                            </div>
                                        </div>
                                    )
                                })
                            }
                        </div>
                        :<div className="no-news-list"><i className="iconfont icon-tongchang-tishi"></i><FormattedMessage id="bbyh当前没有新闻" /></div>
                    }
                    {total>pageSize?<div className="historyEntrustList-page tablist" >
                        <Pages 
                            pageIndex={pageIndex}
                            pagesize={pageSize}
                            total={total}
                            currentPageClick={this.currentPageClick}
                        />
                    </div>:''}
                </div>   
                </ScrollArea>
            </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        newsList:state.news

    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchNews:(index,cb) => {
            dispatch(fetchNews(index)).then(cb)
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(News)