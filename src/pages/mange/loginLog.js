import React from 'react';
import Pages from '../../components/pages';
import Form from '../../decorator/form';
import {  injectIntl} from 'react-intl';
import { DOMAIN_VIP,LOGINVIEWPORT,PAGESIZE,PAGEINDEX} from '../../conf';
import {fetchLoginLogs,receiveLoginLogsInfo,requestFailure} from '../../redux/modules/loginlogs';
import '../../assets/css/table.less';
import axios from 'axios';
import qs from 'qs';
import { formatDate } from '../../utils'
import { connect } from 'react-redux';

@connect(
    state => ({
        lng: state.language.locale
    })
)
@Form
class LoginLog extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE
         };

        this.setSt = this.setSt.bind(this);
        this.currentPageClick = this.currentPageClick.bind(this);
        this.getList = this.getList.bind(this);
    }

    getList(cd = {}){
        console.log(cd)
        axios.post(DOMAIN_VIP + "/manage/queryUserLoginHistroy", qs.stringify({
            cd
        })).then(r => {
            r = r.data.datas;
            this.setSt(r);
        });
    }
    componentDidMount() {
        this.props.requestLoginLogs( Object.assign({},this.state) ) 
    }

    setSt(list = {}){
        this.setState({
            ...list,
        });
    }

    currentPageClick (values){
        let {pageIndex} = this.state;
        pageIndex = values;
        this.setState({pageIndex}, () =>{ 
            this.props.requestLoginLogs( Object.assign({},this.state) ); 
        })
    }

    render(){
        const { formatMessage } = this.intl;
        // const { totalCount, pageIndex } = this.state;
        const { setSt, currentPageClick } = this;
        let {list, total, isLoading } = this.props;
        return(
            <div className="content min_h642_d">
                <div className="login-log">
                    <div className="login-log-table">
                        <table>
                            <thead>
                                <tr>
                                    <th>{formatMessage({id: "日期"})}</th>
                                    <th>{formatMessage({id: "类别"})}</th>
                                    <th>{formatMessage({id: "IP地址"})}</th>
                                    <th>{formatMessage({id: "备注"})}</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                
                                list.map(r => {
                                    return (
                                        <tr key={r.id}>
                                            <td>{ formatDate(r.date, this.props.lng === 'en' ? 'MM-dd-yyyy hh:mm:ss' : undefined) }</td>
                                            <td> {r.terminal == LOGINVIEWPORT 
                                ? formatMessage({id: "loginlogs.text6"})
                                : formatMessage({id: "loginlogs.text7"})
                            }</td>
                                            <td>{r.ip}</td>
                                            <td>{formatMessage({id: "loginlogs.text8"})}</td>

                                        </tr>
                                    )
                                })
                                
                                }
                            </tbody>
                        </table>
                    </div>
                    {total > PAGESIZE?<div className="bk-new-tabList-fd tablist">
                  
                        <Pages
                           {...this.props}
                            currentPageClick = { currentPageClick }
                        />
                    </div>:''}
                </div>
            </div>
        )
    }
    
}
const mapStateToProps = (state, ownProps) =>{
    return{
         list:state.loginLogs.datas.list,
         pageIndex:state.loginLogs.datas.pageIndex,
         total:state.loginLogs.datas.totalCount,
         isLoading:state.loginLogs.isLoading,
         language:state.language
    }
     
 }
const mapDispatchToProps = (dispatch) =>{
    return{
        requestLoginLogs:(values) => {
            let pageIndex = values.pageIndex;
            let pageSize = values.pageSize;
            dispatch(fetchLoginLogs({
                pageIndex : pageIndex,
                pageSize : pageSize
            })).then( (res) => {
                if(res.data.isSuc){
                    let data = res["data"]["datas"];
                    dispatch(receiveLoginLogsInfo(data))
                }
                else{
                    dispatch(requestFailure());
                }
            })
        }
    }
}
export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(LoginLog));