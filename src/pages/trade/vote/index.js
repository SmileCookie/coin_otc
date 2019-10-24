import React from 'react'
import { connect } from 'react-redux';
import { Link } from 'react-router'
import {FormattedMessage,FormattedHTMLMessage,injectIntl,FormattedTime,FormattedDate} from 'react-intl';
import { PAGESIZE,PAGEINDEX,BBYH_PAGESIZE} from '../../../conf'
import { fetchvote } from '../../../redux/modules/vote'
import Pages  from '../../../components/pages'
import SelectList from '../../../components/selectList'
import ScrollArea from 'react-scrollbar'
import '../../../assets/css/news.less'

class Vote extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:BBYH_PAGESIZE,
            newsData:null,
            total:0,
            btnStus:0,
            voteSearch:'',
            selectType:null
        }
        this.typeOptions = [{key:<FormattedMessage id="全部"/>,val:0},{key:<FormattedMessage id="新币上线"/>,val:1},
        {key:<FormattedMessage id="系统维护"/>,val:2},
        {key:<FormattedMessage id="最新活动"/>,val:3},
        {key:<FormattedMessage id="平台动态"/>,val:4}]
        this.currentPageClick = this.currentPageClick.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.voteTypeClick = this.voteTypeClick.bind(this)
        this.searchBtn = this.searchBtn.bind(this)
    }

    componentDidMount() {
        this.props.fetchVote()
    }

    componentWillReceiveProps(nextProps){
        const nextVoteList = nextProps.voteList
        if(nextVoteList.isloaded&&!nextVoteList.isloading){
            //  console.log('ok========2>')
            this.setState({
                pageIndex:nextVoteList.list.pageIndex,
                newsData:nextVoteList.list.datalist,
                total:nextVoteList.list.total
            })
        }
    }
    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    currentPageClick(index){
        const { voteSearch,selectType } = this.state
        this.props.fetchVote({pageIndex:index,type:selectType,title:voteSearch},() => {
            this.scrollbar.scrollArea.scrollTop(0)
        })
    }

    //select 回调
    voteTypeClick(val){
        this.setState({
            selectType : val
        })
        
    } 
    //搜索按钮
    searchBtn(){
        if( this.state.total > this.state.pageSize){
            this.refs.pages.resetPage();
        }
        const { voteSearch,selectType } = this.state
        this.props.fetchVote({type:selectType,title:voteSearch})
        

    }

    render() {
        const { pageIndex,newsData,pageSize,total,btnStus,voteSearch } = this.state
        const { isloaded,isloading } = this.props.voteList
        if(!isloaded&&isloading){
            return  <div className="new-box">
                        <div className="iconfont icon-jiazai new-loading"></div> 
                    </div>
        }

        return (
            <div className="new-box">
                <div className = "new-title vote-title">
                    <div className="new-title-box">
                        <h3 style={{fontSize:'14px'}}><FormattedMessage id="公告head"/></h3>
                        <div className="vote-search">
                            <div className="search-form">
                                <input type="text"  name="voteSearch" value={voteSearch} onChange={this.handleInputChange} />
                                <button onClick={this.searchBtn}><FormattedMessage id="搜索"/></button>
                            </div>
                            <div className="vote-type">
                                <SelectList 
                                    options={this.typeOptions}
                                    Cb={this.voteTypeClick}
                                />
                            </div>
                        </div>
                    </div>
                </div> 
                <ScrollArea ref={(scrollbar) => this.scrollbar=scrollbar} className="trade-scrollarea">
                <div className = "new-con">
                    {newsData?
                            newsData.map((item,index)=>{
                                return (
                                    <div className= "vote-warp"  key={item.id} > 
                                        <div className="vote-head" style={{margin:0}}>
                                            <h6 className="vote-page-tit" style={{overflow:'hidden'}}>
                                                <span style={{color:'#3E85A2',fontSize:'14px',lineHeight:'18px',float:'left',fontWeight:'normal'}}>{item.topInfo}</span>
                                                <Link to={`${this.props.location.pathname}/announcementsdetail?id=${item.id}`}>{item.title}</Link>
                                            </h6>
                                            {
                                            false
                                            &&
                                            <span className="vote-time">{item.pubTimeStr}</span>
                                            }
                                        </div>
                                        {
                                        false
                                        &&
                                        <div className="vote-con">
                                            <p>
                                                <Link to={`${this.props.location.pathname}/announcementsdetail?id=${item.id}`}>
                                                    {item.digest}
                                                    <b>[<FormattedMessage id="详情"/>]</b>
                                                </Link>
                                            </p>
                                        </div>
                                        }
                                    </div>
                                )
                            })
                        :<div className="no-news-list"><i className="iconfont icon-tongchang-tishi"></i><FormattedMessage id="当前没有公告"/></div>
                    }
                    {total>pageSize?<div className="historyEntrustList-page tablist" >
                        <Pages 
                            pageIndex={pageIndex}
                            pagesize={pageSize}
                            total={total}
                            currentPageClick={this.currentPageClick}
                            ref="pages"
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
        voteList:state.vote
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchVote:(obj,cb) => {
            dispatch(fetchvote(obj)).then(cb)
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Vote)