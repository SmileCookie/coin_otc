import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import Pages from '../../components/pages';
import axios from 'axios';
import qs from 'qs';
import SelectList from '../../components/selectList'
import {DOMAIN_VIP,PAGESIZETHIRTY} from '../../conf'
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
            userName: '',
        }
        
        this.getCode = this.getCode.bind(this);
        this.getList = this.getList.bind(this);
        this.acpx = this.acpx.bind(this);
        this.fy = this.fy.bind(this);
        this.getCodesNodeBelType = this.getCodesNodeBelType.bind(this);
        this.setKeys = this.setKeys.bind(this);
        this.shTimer = null;
        this.ck = this.ck.bind(this);
    }
    ck(e){
        this.setState({
            pageIndex: 1,
        },()=>{
            const {userName} = this.state;
            this.getList({userName});
        })
    }
    setKeys(e){
        this.setState({
            userName: e.target.value,
            //pageIndex: 1,
        }, () => {
            clearTimeout(this.shTimer);
            this.shTimer = setTimeout(()=>{
                const {userName} = this.state;
                // 傻逼改需求
                // this.getList({userName});
            }, 100)
        })
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
        
        const { pageIndex, sort, sNodeType, sNodeBelType, userName, } = this.state;
        const cp = {pageIndex, sort, sNodeType, sNodeBelType, pageSize:PAGESIZETHIRTY, userName, };
        const c = qs.stringify({...cp, ...cd});
       
        axios.get(DOMAIN_VIP+'/manage/financial/returnUserOrderWork?'+c).then((res) => {
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
        const {sort, pageIndex, total, list, sNodeBelType, userName, } = this.state;
        const { setKeys, ck } = this;

        return (
            <div className="smn_wp">
                <div className="inx">
                <h2 className="smn_tith"><FormattedMessage id="回本加成排名" /></h2>
                <p className="plv" style={{width:'1400px',margin:'0 auto'}}>
                    <input type="text" onChange={setKeys} value={userName} className="cpsh_wp" placeholder={this.props.intl.formatMessage({id:"输入用户名"})} />
                    <i onClick={ck} style={{
                        position: 'absolute',
                        left: '218px',
                        top: '-37px',
                        width:'20px',
                        height:'20px',
                        background:'transparent',
                        cursor:'pointer',
                    }}></i>
                </p>
                <p className="smn_lk" style={{display:'none'}}><Link to="/bw/smnlist" ><FormattedMessage id="静态贡献" /></Link></p>
                <div style={{minHeight:'550px'}}>
                <table className="smn_tb">
                    <tr>
                        <th width="150" className="mtr"><FormattedMessage id="序号" /></th>
                        <th width="300" className="md">
                            <span className="mr5 k">
                                <FormattedMessage id="激活日期" />
                            </span>
                        </th>

                        <th width="250" className="mtc"><FormattedMessage id="用户" /></th>

                        <th width="250" className="mtr"><span className="span"><FormattedMessage id="t-理论收益" /></span></th>

                        <th width="300" className="mtc ctb_selwp">
                            <FormattedMessage id="状态" />
                            <SelectList 
                            defaultValue={sNodeBelType}
                            options={[{key:<FormattedMessage id="全部"/>,val:0},{key:<FormattedMessage id="已回本"/>,val:1},{key:<FormattedMessage id="未回本"/>,val:2}]}
                            Cb={this.getCodesNodeBelType}
                            />
                        </th>
                        <th className="mtc"><FormattedMessage id="复投" /></th>
                    </tr>
                    {
                list.length ?
                    <tbody>
                    {
                    list.map((item, i)=>{
                        return(
                    <tr key={item.id}>
                        <td className="mtr">{item.seqNo}</td>
                        <td>{formatDate(item.resetProfitTime)}</td>
                        <td className="mtc">{item.userName}</td>
                        <td className="tb" className="mtr">
                            {item.profit ? `${item.profit} USDT` : ''}
                        </td>
                        <td className="mtc">{item.dealFlagDESC}</td>
                        <td className="mtc">
                             {item.returnTypeDESC}
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
                </div>
                {total > PAGESIZETHIRTY ?
                    <div className="tablist deposits" style={{paddingBottom:'60px'}}>
                        <Pages 
                            pageIndex={pageIndex}
                            pagesize={PAGESIZETHIRTY}
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

export default injectIntl(Smn);