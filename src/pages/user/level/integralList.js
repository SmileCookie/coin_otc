import React from 'react';
import axios from 'axios'
import qs from 'qs'
import { COIN_KEEP_POINT,DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../../conf'
import { FormattedMessage, injectIntl ,FormattedDate} from 'react-intl';
import Pages from '../../../components/pages'

class IntegralList extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            data:[],
            isFetching:false,
            totalCount:0,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE
        }

        this.dateFormat = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        };

        // date split
        this.sp = '-';

        this.requestTable = this.requestTable.bind(this)
        this.handlePageChanged = this.handlePageChanged.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        //console.log(this.props.integralLogs);
    }

    requestTable(){
        const { pageIndex } = this.state
        axios.post(DOMAIN_VIP+"/manage/level/ajaxJson",qs.stringify({
            page:pageIndex
        })).then(res => {
            const result = res.data
            if(result.isSuc){
                this.setState({
                    data:result.datas.info,
                    totalCount:result.datas.total
                })
            }
        })
    }
    
    //page change
    handlePageChanged(val){
        this.setState({
            pageIndex:val
        },() => this.requestTable())
    }


    render(){
        const  { data,isFetching,totalCount,pageIndex,pageSize }  = this.state;
        // const logsList = this.formatFundsDetail(data);
        if(isFetching){
            return (
                <div>...LOADING</div>
            )
        }
        return(
            <div className="level-box">
                <h4 className="sub-tit mt30"><FormattedMessage id="level.text1" /></h4>
                <table className="table table-striped table-bordered text-left table-level" id="">
                    <thead>
                        <tr>
                            <th><FormattedMessage id="level.text2" /></th>
                            <th><FormattedMessage id="level.text3" /></th>
                            <th><FormattedMessage id="level.text4" /></th>
                            <th><FormattedMessage id="level.text5" /></th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            data.map((item,index)=>{
                                    return(
                                        <tr key={index}>
                                            <td>{this.props.intl.formatDate(new Date(item.addTime), this.dateFormat).replace(',', '').replace(/\//g, this.sp)}</td>
                                            <td>{item.typeShowNew}</td>
                                            <td>{item.ioType == 0? "+": "-"}{item.jifen}</td>
                                            <td>{item.memo}</td>
                                        </tr>
                                    )
                                }
                            )
                        }
                    </tbody>
                </table>
                <div className="bk-pageNav">
                  <div className={totalCount <= pageSize ? "tablist hide" : "tablist"}>
                    <Pages
                        total={totalCount}
                        pageIndex={pageIndex}
                        pagesize={pageSize}
                        currentPageClick = { this.handlePageChanged }
                    />
                  </div>
                </div>
            </div>
        )
    }
}

export default  injectIntl(IntegralList)