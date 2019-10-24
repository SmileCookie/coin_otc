import React from 'react'
import {connect} from 'react-redux';
import { Link } from 'react-router'
import { FormattedMessage } from 'react-intl';
import { fetchVoteDetail } from '../../../redux/modules/vote'
import { DOMAIN_VIP } from '../../../conf/index'
import axios from 'axios'
import ScrollArea from 'react-scrollbar'
import '../../../assets/css/news.less'

class VoteDetail extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            voteItem:null,
            type:1
        }
        // this.getVoteInfor = this.getVoteInfor.bind(this);
    }

    componentDidMount() {
        this.props.fetchVoteDetail(this.props.location.query.id,this.state.type)
        // this.getVoteInfor(this.props.location.query.id,1)
    }

    componentWillReceiveProps(nextProps){
        if(this.props.location.query.id!=nextProps.location.query.id){
            this.props.fetchVoteDetail(nextProps.location.query.id,this.state.type)
        }
    }
    componentWillUnmount(){
        // clear prev detail data.
        this.props.fetchVoteDetail(9999999999,0,1);
    }

    // getVoteInfor(id,type){
    //     axios.get(DOMAIN_VIP+'/msg/newsdetails',{params:{id,type}}).then(res => {
    //         const result = res.data
    //         console.log(result)
    //     })
    // }

    render() {
        const { voteDetail,skin } = this.props;
        let tabelClass = '';
        skin == 'dark'? tabelClass = 'vote-con-details':tabelClass = 'vote-con-details tableLight';
        console.log(voteDetail);
        
        return ( 
            <div className="new-box">
                <div className = "new-title">
                    <div className="new-title-box">
                        <h3>
                            <Link to="/bw/announcements" style={{fontSize:'14px'}}><FormattedMessage id="公告head" /></Link> 
                            <em style={{fontSize: '14px'}}>&gt;</em>
                            <span className="naewspan"><FormattedMessage id="公告详情" /></span>
                        </h3>
                    </div>
                </div>
                <ScrollArea className="trade-scrollarea">
                    {
                        voteDetail&&
                        <div className = "new-con-detail">
                            <h4 className="new-detail-title">{voteDetail.title}</h4>
                            <div className="new-con-time"><i className="iconfont icon-msnui-time"></i>{voteDetail.pubTimeStr}</div>  
                            <div className={tabelClass} dangerouslySetInnerHTML={{__html:voteDetail.content}}></div>
                        </div>
                    }   
                </ScrollArea>
            </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        voteDetail:state.vote.voteDetail,
        skin:state.trade.skin,

    };
}
const mapDispatchToProps = (dispatch) => {
    return {
        fetchVoteDetail:(id,type,flg) => {
            dispatch(fetchVoteDetail(id,type,flg))
        }
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(VoteDetail)