import React from 'react';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl,FormattedTime ,FormattedDate} from 'react-intl';
import {fetchLoginLogs,receiveLoginLogsInfo,requestFailure} from '../../../redux/modules/loginlogs';
import { PAGEINDEX,PAGESIZE,LOGINVIEWPORT } from '../../../conf';
import Pages from '../../../components/pages';

class LoginLogs extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
           pageIndex:PAGEINDEX,
           pageSize:PAGESIZE
        };
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

        this.currentPageClick = this.currentPageClick.bind(this);
    }
    componentDidMount() {
        this.props.requestLoginLogs( Object.assign({},this.state) ); 
    }

    currentPageClick (values){
        let {pageIndex} = this.state;
        pageIndex = values;
        this.setState({pageIndex}, () =>{ 
            this.props.requestLoginLogs( Object.assign({},this.state) ); 
        })
    }
    
    render() {
        let {list, total, isLoading } = this.props;
        let trhTML = null;
        
        if( isLoading ){
            trhTML = <tr>
                         <td>loading....</td>
                      </tr>
        }
        else{
            trhTML =list.map((item ,i) => {
                let date =  this.props.intl.formatDate(new Date(item.date), this.dateFormat).replace(',', '').replace(/\//g, this.sp)
                return( 
                    <tr key={i}>
                        <td>{date}</td>
                        <td>
                            {   item.terminal == LOGINVIEWPORT 
                                ? <FormattedMessage  id="loginlogs.text6"/> 
                                : <FormattedMessage  id="loginlogs.text7"/> 
                            }
                        </td>
                        <td>{item.ip}</td>
                        <td><FormattedMessage  id="loginlogs.text8"/></td>
                    </tr>
                )
            })
        }
        return (
            <div className="cont-row">
                <div className="bk-top">
                    <h2><span><FormattedMessage id="manage.text4" /></span></h2>
                </div>
                <div className="logslist-table">
                    <table className="table table-striped table-bordered text-left mt30">
                        <thead>
                            <tr>
                                <th><FormattedMessage  id="loginlogs.text2"/></th>
                                <th><FormattedMessage  id="loginlogs.text3"/></th>
                                <th><FormattedMessage  id="loginlogs.text4"/></th>
                                <th><FormattedMessage  id="loginlogs.text5"/></th>
                            </tr>
                        </thead>  
                        <tbody>{trhTML}</tbody> 
                    </table>
                </div>
                <div className="tablist">
                    <Pages
                            {...this.props}
                            currentPageClick = { this.currentPageClick }
                        />
                </div>
            </div>
        );
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
export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(LoginLogs));
