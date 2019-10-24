import React from 'react';
import {DOMAIN_VIP,MONEYMANAGEMENTBASE,PAGESIZETHIRTY,PAGEINDEX} from "../../conf";
import Nav from "../../components/navigator/nav";
import Pages from '../../components/pages';
import Crd from './crd';
import ReactModal from '../../components/popBox';
import Confirm from '../../components/msg/confirm';
import { FormattedMessage, injectIntl} from 'react-intl';
import { optPop } from '../../utils/index';
import axios from 'axios';
import qs from 'qs';
import cookie from 'js-cookie';

class Cmonerd extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            list: [],
            isLoading: true,
            totalCount: 0,
            pagesize:PAGESIZETHIRTY,
            pageIndex:PAGEINDEX,
            showProp: 0,
            cbData: null,
        }
        this.handlePageChanged = this.handlePageChanged.bind(this);

        this.getRes = this.getRes.bind(this);

        this.cb = this.cb.bind(this);

        this.cbBussiness = this.cbBussiness.bind(this);
        this.cbBussinessok = this.cbBussinessok.bind(this);

        this.optProcessOne = this.optProcessOne.bind(this);
        this.optProcessTwo = this.optProcessTwo.bind(this);
        this.optProcessThree = this.optProcessThree.bind(this);

        this.processGroups = [
            this.optProcessOne,
            this.optProcessTwo,
            this.optProcessThree,
        ];
    }

    doRef(flg = false){
        flg && window.location.reload(true);
    }
    
    cbBussinessok(flg = 0){
        if(flg){
            const {id:insureId} = this.state.cbData;
            axios.get(DOMAIN_VIP + '/manage/financial/insurance/userConfirmExecuteInsure?'+qs.stringify({insureId})).then(res=>{
                res = res.data;
                this.setState({
                    showProp: false,
                }, () => {
                    optPop(()=>{
                        this.doRef(res.isSuc);
                    }, res.des, undefined, !res.isSuc);
                })
            },()=>{})  
        }else{
            this.setState({
                showProp: false,
            })
        }
    }
    // 撤销
    optProcessOne(cbData){
        this.setState({
            showProp: true,
            cbData,
        })
    }
    // 执行
    optProcessTwo(cbData){
        this.setState({
            showProp: true,
            cbData,
        })
    }
    // 72小时直接撸接口
    optProcessThree(cbData){
        const {id:insureId} = cbData;

        axios.get(DOMAIN_VIP + '/manage/financial/insurance/userRollBackInsure?'+qs.stringify({insureId})).then(res=>{
            res = res.data;
            
            optPop(()=>{
                this.doRef(res.isSuc);
            }, res.des, undefined, !res.isSuc);
        },()=>{})
    }
    cbBussiness(flg = 0){
        if(flg){
            const {id:insureId} = this.state.cbData;
            axios.get(DOMAIN_VIP + '/manage/financial/insurance/userCancelInsure?'+qs.stringify({insureId})).then(res=>{
                res = res.data;
                this.setState({
                    showProp: false,
                }, () => {
                    optPop(()=>{
                        this.doRef(res.isSuc);
                    }, res.des, undefined, !res.isSuc);
                })
            },()=>{})  
        }else{
            this.setState({
                showProp: false,
            })
        }
        
    }
    cb(cbData = {}){
        const { opt } = cbData;
        // console.log(cbData, '====>>');
        // 根据操作不同的任务分发  1.撤销保险 2执行 3限时撤销
        this.processGroups[opt-1](cbData);
    }
    handlePageChanged(pageIdx = 1){
        this.setState({
            pageIndex: pageIdx
        }, () => {
            this.getRes();
        });
    }

    getRes(){
        const { pageIndex, pagesize:pageSize } = this.state;
        this.setState({
            isLoading: true,
        }, ()=>{
            axios.get(DOMAIN_VIP + '/manage/financial/insurance/queryUserInsure?' + qs.stringify({pageIndex, pageSize})).then(res => {
                res = res.data.datas;
                const { totalCount, pageIndex, list, } = res;
                this.setState({
                    totalCount,
                    pageIndex,
                    list,
                    isLoading: false,
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

    render(){
        
        const URL = [{name: '理财中心', link: MONEYMANAGEMENTBASE +  'cmoney'}, {name: '保险中心', link: MONEYMANAGEMENTBASE + 'cmonerd'}, {name: '投保记录', link: MONEYMANAGEMENTBASE + 'cmrd'}];
        const { list, isLoading, totalCount, pagesize, pageIndex, showProp, cbData, } = this.state;
        const { formatMessage } = this.props.intl;
        const _lan = cookie.get('zlan')
        return (
            <div className="mwp mwp_d cmoney spploading">
                <Nav path={this.props.location.pathname} ay={URL}/>
                <Crd cb={this.cb} list={list} isLoading={isLoading} />
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
                {
                showProp && cbData
                ?
                <div className="confirm_sy1">
                    <Confirm cb={cbData.opt === 1 ? this.cbBussiness : this.cbBussinessok} cancel={formatMessage({id: "取消"})} ok={formatMessage({id: "确定f"})} msg={cbData.opt === 1 ? formatMessage({id: "是否确认撤销保险？"}) : <div>
                        <p style={{
                            fontSize: '18px',
                            color: '#fff',
                            marginBottom:'30px'
                        }}>{formatMessage({id:"执行保险"})}</p>
                        <table className="phz">
                            <tr>
                                <th><FormattedMessage id="tx-投保数量" /></th>
                                <td>{['en','kr'].includes(_lan) && <span>&nbsp;</span>}<span className="my_vds_wp">{cbData.insureInvestAmount}</span></td>
                            </tr>
                            <tr>
                                <th><FormattedMessage id="tx-触发价格" /></th>
                                <td>{['en','kr'].includes(_lan) && <span>&nbsp;</span>}{cbData.triggerPrice} USDT</td>
                            </tr>
                            <tr>
                                <th><FormattedMessage id="tx-理论收益" /></th>
                                <td>{['en','kr'].includes(_lan) && <span>&nbsp;</span>}{cbData.theoryPrice} USDT</td>
                            </tr>
                        </table>
                    </div>} />
                </div>
                :null
                }
            </div>
        )
    }
}

export default injectIntl(Cmonerd);