import React from 'react'
import { connect } from 'react-redux';
import { Link,browserHistory } from 'react-router'
import { FormattedMessage } from 'react-intl';
import { fetchNewsDetail } from '../../../redux/modules/news'
import ScrollArea from 'react-scrollbar'
import '../../../assets/css/news.less'
import axios from 'axios';
import { DOMAIN_VIP } from '../../../conf'

// const newType = 2;
class NewsDetail extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            hotNews: [],
            newsdetial:[],
            //新闻列表类型
            type:2,
            screenW:document.body.clientWidth
        }
        this.getHotNews = this.getHotNews.bind(this);
        this.limitLen = this.limitLen.bind(this);
    }
    componentDidUpdate(){
        setTimeout(()=>{
            if(this.isScroll){
                this.isScroll = 0;
                document.getElementsByClassName("new-box")[0].getElementsByClassName("scrollarea-content")[0].removeAttribute("style");
            }
        })
    }
    componentDidMount() {
        // this.props.fetchNewsDetail(this.props.location.query.id,this.state.type);
        // 获取热门新闻
        //监控浏览器大小
        window.onresize = () => {
            this.setState({
                screenW:document.body.clientWidth
            })
        }

    }
    componentWillMount(){
        this.getHotNews(this.props.location.query.id,this.state.type);

    }

    componentWillReceiveProps(nextProps){
        if(this.props.location.query.id != nextProps.location.query.id){
            this.getHotNews(nextProps.location.query.id,this.state.type)
        }
        this.scrollbar.scrollArea.scrollTop(0)

        
        
    }
    getHotNews(id,type){
        console.log(type)
        axios.get(DOMAIN_VIP+'/msg/newsdetails',{params:{id,type}}).then(res => {
            const hotNews = res.data.datas.datalist;
            const newsdetial = res.data.datas.newsdetial
            this.setState({
                hotNews:hotNews,
                newsdetial:newsdetial
            })
        })
    }
    limitLen (str,sub_length){
        // let _data = data;
        // if(_data.length > 80){
        //     _data = _data.substring(0,80);
        //     _data = _data + '...';

        // }
        // return _data
        // console.log(document.body.clientWidth)
        if (str == "" || str == null){
            return "";
        }   
        var temp1 = str.replace(/[^\x00-\xff]/g, "**"); //精髓   
        var temp2 = temp1.substring(0, sub_length);
        //找出有多少个*   
        var x_length = temp2.split("\*").length - 1;
        var hanzi_num = x_length / 2;
        sub_length = sub_length - hanzi_num; //实际需要sub的长度是总长度-汉字长度   
        var res = str.substring(0, sub_length);
        if (sub_length < str.length) {
            var end = res + "…";
        } else {
            var end = res;
        }
        return end;        
    }

    render() {
        const { newsdetial,hotNews,screenW } = this.state
        const { skin } = this.props;
        console.log(hotNews)
        let tabelClass = '';
        skin == 'dark'? tabelClass = 'new-con-tit':tabelClass = 'new-con-tit tableLight';
        return ( 
            <div className="new-box">
                <div className = "new-title">
                    <div className="new-title-box">
                        <h3>
                            <Link to="/bw/news" style={{fontSize: '14px'}}><FormattedMessage id="新闻" /></Link> 
                            <em style={{fontSize: '14px'}}>&gt;</em>
                            <span className="naewspan"><FormattedMessage  id="新闻详情" /></span> 
                        </h3> 
                    </div>
                </div>
                <ScrollArea onScroll={()=>{this.isScroll = 1}} ref={(scrollbar) => this.scrollbar=scrollbar} className="trade-scrollarea">
                    {
                        newsdetial&&<div className = "new-con-detail">
                            <h4 className="new-detail-title">{newsdetial.title}</h4>
                            <div className="new-con-time"><i className="iconfont icon-msnui-time"></i>{newsdetial.pubTimeStr}</div>  
                            <div className="new-con-img">
                                <img src={newsdetial.photo} alt={newsdetial.title} />
                            </div>
                            <div className={tabelClass} dangerouslySetInnerHTML={{__html:newsdetial.content}}></div>
                            <div className="new-con-source">
                                <p className="new-con-link">
                                    <FormattedMessage id="文章来源" />{newsdetial.source}
                                </p> 
                                <p className="new-con-keyword">
                                    <FormattedMessage id="文章关键字" />{newsdetial.keyword}
                                </p>
                            </div>
                            <div className="new-con-anouce"><FormattedMessage id="版权声明：作者保留权利。文章为作者独立观点，不代表btcwinex立场" /></div>
                            <div className="bbyh-newLine"></div>
                            <div className="bbyh-hot-new">
                                <p className="bhtith"><FormattedMessage id="bbyh推荐新闻" /></p>
                                <ul>
                                    {
                                        hotNews&&hotNews.map((item,index) =>{
                                            // if(screenW>1450){
                                            //     _text = this.limitLen(item.digest,140)
                                            // }
                                            let _text = screenW < 1450 ? this.limitLen(item.digest,140):this.limitLen(item.digest,170)
                                            return(
                                                <li key={item.id}>
                                                    <div className="bh-imgwp">
                                                        <img src={item.photo} alt=""/>
                                                    </div>
                                                    <div className="bh-text">
                                                        <p className="bh-tith2" onClick={()=>{window.location.href = `/bw/news/newsdetail?id=${item.id}`}}>{item.title}</p>
                                                        <p className="bh-p">{_text}</p>
                                                        <div className="bh-lst">
                                                            <div className="date">
                                                                <i className="iconfont icon-msnui-time"></i>
                                                                <span className="times">{item.pubTimeStr}</span>
                                                            </div>
                                                            <div className="lk">{item.source}</div>
                                                        </div>
                                                    </div>
                                                </li>
                                            )
                                        })
                                    }
                                    
                                </ul>
                            </div>
                        </div> 
                    }
                </ScrollArea>
            </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        skin:state.trade.skin,
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchNewsDetail:(id,type) => {
            dispatch(fetchNewsDetail(id,type))
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(NewsDetail)
