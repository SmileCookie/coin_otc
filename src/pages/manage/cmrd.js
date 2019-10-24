import React from 'react';
import {DOMAIN_VIP,MONEYMANAGEMENTBASE,PAGESIZETHIRTY,PAGEINDEX} from "../../conf";
import Nav from "../../components/navigator/nav";
import Pages from '../../components/pages';
import { FormattedMessage, injectIntl} from 'react-intl';
import axios from 'axios';
import qs from 'qs';
import Crds from './crds';

class Cmrd extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            list: [],
            isLoading: true,
            totalCount: 0,
            pagesize:PAGESIZETHIRTY,
            pageIndex:PAGEINDEX,
        }

        this.handlePageChanged = this.handlePageChanged.bind(this);

        this.getRes = this.getRes.bind(this);
    }

    getRes(){
        const { pageIndex, pagesize:pageSize } = this.state;
        this.setState({
            isLoading: true,
        }, ()=>{
            axios.get(DOMAIN_VIP + '/manage/financial/insurance/queryInsuranceRecord?' + qs.stringify({pageIndex, pageSize})).then(res => {
                res = res.data.datas;
                const { totalCount, pageIndex, list, frozen, thaw, ensure, } = res;
                this.setState({
                    totalCount,
                    pageIndex,
                    list,
                    isLoading: false,
                    frozen,
                    thaw,
                    ensure,
                })
            }, () => {
                this.setState({
                    isLoading: false,
                })
            })
            // 获取投保记录数据
            // this.setState({
            //     totalCount:100,
            //     pageIndex,
            //     list:[{
            //         id:'1',
            //         bonus_time:1111111111111,
            //         a:1,
            //         b:1,
            //         c:1,
            //         d:1,
            //         e:1,
            //         f:1,
            //     }],
            //     isLoading: false,
            // })
        });
    }

    componentDidMount(){
        this.getRes();
    }

    handlePageChanged(pageIdx = 1){
        this.setState({
            pageIndex: pageIdx
        }, () => {
            this.getRes();
        });
    }

    render(){
        const URL = [{name: '理财中心', link: MONEYMANAGEMENTBASE +  'cmoney'}, {name: '保险中心', link: MONEYMANAGEMENTBASE + 'cmonerd'}, {name: '投保记录', link: MONEYMANAGEMENTBASE + 'cmrd'}];
        const { list, isLoading, totalCount, pagesize, pageIndex, frozen, thaw, ensure, } = this.state;

        return(
            <div className="mwp mwp_d cmoney spploading">
                <Nav path={this.props.location.pathname} ay={URL} />
                <table className="myppic">
                    <td><FormattedMessage id="累计冻结" />: <span className="my_vds_wp jsbig">{frozen}</span></td>
                    <td><FormattedMessage id="累计解冻" />: <span className="my_vds_wp jsbig">{thaw}</span></td>
                    <td><FormattedMessage id="保益节点" />: {ensure}</td>
                </table>
                <Crds list={list} isLoading={isLoading} />
                <div className="bk-pageNav">
                    <div className="tablist deposits" style={{display: totalCount > pagesize ? 'block' : 'none'  }}>
                        <Pages 
                            pageIndex={pageIndex}
                            pagesize={pagesize}
                            total={totalCount}
                            currentPageClick = { this.handlePageChanged }
                        />
                    </div>
                </div>
            </div>
        )
    }
}

export default injectIntl(Cmrd);