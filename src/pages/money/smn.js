import React from 'react';
import { FormattedMessage } from 'react-intl';
import Pages from '../../components/pages';
import axios from 'axios';
import qs from 'qs';
import SelectList from '../../components/selectList'
import {DOMAIN_VIP} from '../../conf'
import { formatDate} from '../../utils';
import { Link } from 'react-router';
const BigNumber = require('big.js')

class Smn extends React.Component{
    constructor(props){
        super(props)

        this.state = {
            sort: 0,
            px: 0,
            list: [],
            pageIndex: 1,
            total: 0,
            sNodeType:0,
            sNodeBelType:props.routeParams.type || 0,
        }
        
        this.getCode = this.getCode.bind(this);
        this.getList = this.getList.bind(this);
        this.acpx = this.acpx.bind(this);
        this.fy = this.fy.bind(this);
        this.getCodesNodeBelType = this.getCodesNodeBelType.bind(this);

        // 整合零散数据
        this.otherData = {
            homeMadeNodeShowNum: '',
            homeMadeNodeTotalProfit: '',
            homeMadeNodePayProfit: '',
            fixedMadeNodeShowNum: '',
            fixedMadeNodeTotalProfit: '',
            fixedMadeNodePayProfit: '',
            trendsMadeNodeShowNum: '',
            trendsMadeNodeTotalProfit: '',
            trendsMadeNodePayProfit: '',
            homeMadeNodeTips: '',
            fixedMadeNodeTips: '',
            trendsMadeNodeTips: '',
        }
    }

    getCodesNodeBelType(code = 0){
        this.setState({
            pageIndex: 1,
            sNodeBelType: code,
        }, () => {
            this.getList({sNodeBelType: code});
        });
    }

    fy(c){
        this.getList({pageIndex: c});
    }

    acpx(){
        
        this.setState({
            sort: this.state.sort === 0 ? 1 : 0,
            pageIndex:1,
        }, () => {
            this.getList({sort: this.state.sort});
        })
    }

    getList(cd = {}){
        const { pageIndex, sort, sNodeType, sNodeBelType, } = this.state;
        const cp = {pageIndex, sort, sNodeType, sNodeBelType, };
        const c = qs.stringify({...cp, ...cd});
       
        axios.get(DOMAIN_VIP+'/getSuperNodeInfo?'+c).then((res) => {
            const data = res.data.datas;
            
            this.setState({
                pageIndex: data.pageIndex,
                total: data.totalCount,
                list: data.list,
            },()=>{
                console.log(this.state.list);
            })
            
        })
    }

    componentDidMount(){
        this.getList();

        // 获取乱七八糟的数据，比如头部，下拉，气泡
        axios.get(DOMAIN_VIP + '/superNodeProduceInfo').then(res=>{
            this.otherData = res.data.datas;
            // console.log(res,'====>>>');
            this.forceUpdate();
        })
    }

    getCode(code = 0){
        this.setState({
            sNodeType: code,
            pageIndex: 1,
        }, () => {
            this.getList({sNodeType: code});
        })
    }
    render(){
        const {sort, pageIndex, total, list, sNodeBelType, } = this.state;
        const {
            homeMadeNodeShowNum,
            homeMadeNodeTotalProfit,
            homeMadeNodePayProfit,
            fixedMadeNodeShowNum,
            fixedMadeNodeTotalProfit,
            fixedMadeNodePayProfit,
            trendsMadeNodeShowNum,
            trendsMadeNodeTotalProfit,
            trendsMadeNodePayProfit,
            homeMadeNodeTips,
            fixedMadeNodeTips,
            trendsMadeNodeTips,
            insureMadeNodeTotalProfit,
            insureMadeNodePayProfit,
            insureMadeNodeShowNum,
            insureMadeNodeTips,
        } = this.otherData;
        return (
            <div className="smn_wp">
                <div className="inx">
                <h2 className="smn_tith"><FormattedMessage id="BTCWINEX超级主节点地址" /></h2>
                <div className="msuperhd_wp">
                    <table>
                        <thead>
                            <tr>
                                <th>
                                    <span className="msth">
                                        <FormattedMessage id="初创" />:
                                    </span>
                                    <span className="msnum">{homeMadeNodeShowNum}</span>
                                    <span className="plv msalt">
                                        <i>{homeMadeNodeTips}</i>
                                    </span>
                                </th>
                                <th>
                                    <span className="msth">
                                        <FormattedMessage id="固定" />:
                                    </span>
                                    <span className="msnum">{fixedMadeNodeShowNum}</span>
                                    <span className="plv msalt">
                                        <i>{fixedMadeNodeTips}</i>
                                    </span>
                                </th>
                                <th>
                                    <span className="msth">
                                        <FormattedMessage id="动态" />:
                                    </span>
                                    <span className="msnum">{trendsMadeNodeShowNum}</span>
                                    <span className="plv msalt">
                                        <i>{trendsMadeNodeTips}</i>
                                    </span>
                                </th>

                                <th>
                                    <span className="msth">
                                        <FormattedMessage id="保益" />:
                                    </span>
                                    <span className="msnum">{insureMadeNodeShowNum}</span>
                                    <span className="plv msalt">
                                        <i>{insureMadeNodeTips}</i>
                                    </span>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>
                                    <FormattedMessage id="累计收益" />: <span className="my_vds_wp">{homeMadeNodeTotalProfit}</span>
                                    <FormattedMessage id="已发放" />: <span className="my_vds_wp">{homeMadeNodePayProfit}</span>
                                </td>
                                <td>
                                    <FormattedMessage id="累计收益" />: <span className="my_vds_wp">{fixedMadeNodeTotalProfit}</span>
                                    <FormattedMessage id="已发放" />: <span className="my_vds_wp">{fixedMadeNodePayProfit}</span>
                                </td>
                                <td>
                                    <FormattedMessage id="累计收益" />: <span className="my_vds_wp">{trendsMadeNodeTotalProfit}</span>
                                    <FormattedMessage id="已发放" />: <span className="my_vds_wp">{trendsMadeNodePayProfit}</span>
                                </td>
                                <td>
                                    <FormattedMessage id="累计收益" />: <span className="my_vds_wp">{insureMadeNodeTotalProfit}</span>
                                    <FormattedMessage id="已发放" />: <span className="my_vds_wp">{insureMadeNodePayProfit}</span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <p className="smn_lk" style={{display:'none'}}><Link to="/bw/smnlist" ><FormattedMessage id="静态贡献" /></Link></p>
                <table className="smn_tb">
                    <tr>
                        <th width="120" className="mtr"><FormattedMessage id="序号" /></th>
                        <th width="360" className="md">
                            <span className="mr5 k">
                                <FormattedMessage id="超级主节点地址" />
                            </span>
                           
                            <SelectList 
                                    options={[{key:<FormattedMessage id="全部"/>,val:0},{key:<span><FormattedMessage id="初创"/>({homeMadeNodeShowNum})</span>,val:1},{key:<span><FormattedMessage id="固定"/>({fixedMadeNodeShowNum})</span>,val:2},{key:<span><FormattedMessage id="动态"/>({trendsMadeNodeShowNum})</span>,val:3},{key:<span><FormattedMessage id="保益"/>({insureMadeNodeShowNum})</span>,val:4}]}
                                    Cb={this.getCode}
                                />
                            
                           
                        </th>
                        <th className="mtr"><FormattedMessage id="余额" /></th>
                        <th width="290"><span className="span"><FormattedMessage id="最新产出数量" /></span><em onClick={this.acpx} className={`px ${sort?'ac':''}`}></em></th>
                        <th width="220" className="mtr"><FormattedMessage id="可分红额度(98%)" /></th>
                        <th className="slsp md">
                            <SelectList 
                            defaultValue={sNodeBelType}
                            options={[{key:<FormattedMessage id="全部"/>,val:0},{key:<FormattedMessage id="回本加成"/>,val:2},{key:<FormattedMessage id="VIP分红"/>,val:1}]}
                            Cb={this.getCodesNodeBelType}
                            />
                            <span className="mr5 k">
                                <FormattedMessage id="分配类型" />
                            </span>
                            
                        </th>
                        <th width="120" className="mtc"><FormattedMessage id="操作" /></th>
                    </tr>
                    {
                list.length ?
                    <tbody>
                    {
                    list.map((item, i)=>{
                        return(
                    <tr key={item.sNodeQueryLink}>
                        <td className="mtr">{i+1}</td>
                        <td>{item.sNodeAddr}</td>
                        <td className="mtr">{new BigNumber(item.sNodeBalance).toFixed(5)}</td>
                        <td className="tb">
                            <span className="nr">{new BigNumber(item.lateMiningAmount).toFixed(5)}</span>
                            <span className="b"></span>
                            <span className="t">{+item.lateMiningTime ? formatDate(new Date(+item.lateMiningTime)).replace(',', '').replace(/\//g, '-') : ''}</span>
                            <span className="dg"></span>
                        </td>
                        <td className="mtr">{new BigNumber(item.bonusAmount).toFixed(5)}</td>
                        <td className="mytr">
                            {
                                item.sNodeBelName
                            }
                        </td>
                        <td className="mtc">
                            <a href={item.sNodeQueryLink} target="_blank">
                                <FormattedMessage id="查看详情" />
                            </a>
                        </td>
                    </tr>)
                    })
                    }
                    </tbody>
                    :(
                    <tr>
                        <td colSpan="6" style={{height:'400px',color:'#fff', textAlign:'center',lineHeight:'400px'}}>
                        <div>
                        <FormattedMessage id="No.record" />
                    </div></td></tr>)
                }
                </table>
                
                {total > 50 ?
                    <div className="tablist deposits" style={{paddingBottom:'60px'}}>
                        <Pages 
                            pageIndex={pageIndex}
                            pagesize={50}
                            total={total}
                            currentPageClick = {this.fy}
                        />
                    </div>
                    :
                    null
                }
                </div>
            </div>
        )
    }
}

export default Smn;