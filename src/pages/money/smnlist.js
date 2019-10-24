import React from 'react';
import { FormattedMessage } from 'react-intl';
import Pages from '../../components/pages';
import axios from 'axios';
import qs from 'qs';
import SelectList from '../../components/selectList'
import {DOMAIN_VIP} from '../../conf'
import { formatDate} from '../../utils';
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
            platReleasePayAmount: 0,
        }
        this.getCode = this.getCode.bind(this);
        this.getList = this.getList.bind(this);
        this.acpx = this.acpx.bind(this);
        this.fy = this.fy.bind(this);
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
        const {pageIndex, sort, sNodeType} = this.state;
        const cp = {pageIndex, sort, sNodeType};
        const c = qs.stringify({...cp, ...cd});
       
        axios.get(DOMAIN_VIP+'/manage/financial/doubleThrowToSuperNode?'+c).then((res) => {
            const data = res.data.datas;
            
            this.setState({
                platReleasePayAmount:data.platReleasePayAmount,
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
        const {sort, pageIndex, total, list,platReleasePayAmount} = this.state;
     
        return (
            <div className="smn_wp">
                <div className="inx">
                <h2 className="smn_tith" style={{display:'none'}}><em className="vds">2018.2-现在 <FormattedMessage id="静态贡献" /> 111</em></h2>
                <h2 className="smn_tith"><FormattedMessage id="静态贡献" /></h2>
                <p className="vdstith"><FormattedMessage id="已发放" />: <span className="my_vds_wp">{platReleasePayAmount}</span></p>
                <table className="smn_tb">
                    <tr>
                        <th width="240" className="mtr"><FormattedMessage id="释放时间" /></th>
                        <th><FormattedMessage id="用户x" /></th>
                        <th><FormattedMessage id="释放数量x" /></th>
                        <th><FormattedMessage id="预设复投20%" /></th>
                        <th><FormattedMessage id="实际复投" /></th>
                        <th><FormattedMessage id="静态贡献" /></th>
                        <th><FormattedMessage id="状态x" /></th>
                    </tr>
                    {
                list.length ?
                    <tbody>
                    {
                    list.map((item, i)=>{
                        return(
                    <tr key={item.id}>
                        <td className="mtr">{formatDate(new Date(+item.creatTime)).replace(',', '').replace(/\//g, '-')}</td>
                        <td><span className="">{item.userName}</span></td>
                        <td><span className="my_vds_wp">{item.releaseAmount}</span></td>
                        <td><span className="my_vds_wp">{item.lossAmount}</span></td>
                        <td><span className="my_vds_wp">{item.doubleThrowAmount}</span></td>
                        <td><span className="my_vds_wp">{item.toSuperNodeAmount}</span></td>
                        <td>{item.dealflagName}</td>
                    </tr>)
                    })
                    }
                    </tbody>
                    :(
                    <tr>
                        <td colSpan="7" style={{height:'400px',color:'#fff', textAlign:'center',lineHeight:'400px'}}>
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