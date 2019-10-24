import React from 'react'
import { FormattedMessage, injectIntl} from 'react-intl';
import Laydate from 'layui-laydate';
import '../../assets/css/vendor/laydata/laydate.css';
import {formatDate} from '../../utils';
import Pages from '../../components/pages';
import { PAGEINDEX, PAGESIZETHIRTY, DOMAIN_VIP } from '../../conf';
import axios from 'axios';
import qs from 'qs';
const BigNumber = require('big.js')
/**
 * 投资记录
 */
class InvestmentR extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            totalCount: 0,
            pagesize:PAGESIZETHIRTY,
            pageIndex:PAGEINDEX,
            startDate: '',
            endDate: '',
            list: [],
            isLoading: true,
        }

        this.handlePageChanged = this.handlePageChanged.bind(this);
        this.getRes = this.getRes.bind(this);
    }
    handlePageChanged(){

    }

    getRes(cd = {}){
        
        // axios.get(DOMAIN_VIP + '/manage/financial/userFinProfitBill?'+qs.stringify(cd)).then((res)=>{
        //     console.log(res);
        // }, () => {

        // })
        axios.get(DOMAIN_VIP + '/manage/financial/userFinProfitBill').then((res)=>{
            
            this.setState({
              list: res.data.datas,
              isLoading: false,
            })
        }, () => {
              this.setState({
                  isLoading: false,
              })
        })
    }

    componentDidMount(){
        //获取列表数据
        this.getRes();

        const lang = this.props.locale == 'zh' ? 'cn' : 'en';
        const self = this;
        
        Laydate.render({
            elem: '#layDate_1',
            lang: lang,
            done(value){
                self.setState({
                    startDate: value
                });
            }
        });


        Laydate.render({
            elem: '#layDate_2',
            lang: lang,
            done(value){
                self.setState({
                    endDate: value
                });
            }
        });
    }

    render(){
        const { startDate, endDate, pageIndex, pagesize, totalCount, list, isLoading, } = this.state;
        const stats = [
            <FormattedMessage id="冻结" />,
            <FormattedMessage id="释放" />
        ];
        const { formatMessage } = this.props.intl;

        return(
            <div className="bk-new-tabList select-new my_select_wp">
                <div className="entrust-head my_k" style={{display:'none'}}>
                    <div className="entrust-head-market left p_date_groups">
                        <div className="opt_wp">
                            <span className="i"><FormattedMessage id="搜索" /></span>
                            <span className="i"><FormattedMessage id="重置" /></span>
                        </div>
                        <h5 className="left padl10"><FormattedMessage id="投资日期:" /></h5>
                        <span className="laydate">
                            <input readOnly="true" type="text" id="layDate_1" data-name="startDate" value={startDate} placeholder={formatMessage({id: "请选择"})} />
                            <span className="sp">~</span>
                            <input readOnly="true" type="text" id="layDate_2" data-name="endDate" value={endDate} placeholder={formatMessage({id: "请选择"})} />
                        </span>
                    </div>
                </div>
                <div className="bk-new-tabList-bd">
                    <table className="table-history" width="100%">
                        <thead>
                            <tr>
                                <th><FormattedMessage id="日期" /></th>
                                <th><FormattedMessage id="资金数量" /></th>
                                <th><FormattedMessage id="协议价" /></th>
                                <th><FormattedMessage id="投资基数" /></th>
                                <th><FormattedMessage id="预期估值" /></th>
                                <th><FormattedMessage id="分红权重" /></th>
                                <th><FormattedMessage id="类型" /></th>
                            </tr>
                        </thead>
                        <tbody>
                            {   list.length
                            ?
                                list.map((item, i)=>{
                                    return(
                                        <tr key={i}>
                                        <td>{formatDate(new Date(+item.createTime)).replace(',', '').replace(/\//g, '-')}</td>
                                        <td><span className="my_vds_wp">{item.amount}</span></td>
                                        <td>{new BigNumber(item.vdsUsdtPrice ? item.vdsUsdtPrice : 0).toFixed(4)} USDT</td>
                                        <td>{new BigNumber(item.usdtAmount ? item.usdtAmount : 0).toFixed(4)} USDT</td>
                                        <td>{item.expectProfitUsdt} USDT</td>
                                        <td>{item.vipWeight}</td>
                                        <td>{item.profitType}</td>
                                    </tr>
                                    )
                                })
                            :
                            
                            <tr className="nohover">
                                <td colSpan="7" style={{height:'400px',color:'#fff', textAlign:'center',lineHeight:'400px'}}>
                                <div>
                                {isLoading ? <div className="ploading">
                <img src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+Cjxzdmcgd2lkdGg9IjQwcHgiIGhlaWdodD0iNDBweCIgdmlld0JveD0iMCAwIDQwIDQwIiB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPgogICAgPCEtLSBHZW5lcmF0b3I6IFNrZXRjaCA1MC4yICg1NTA0NykgLSBodHRwOi8vd3d3LmJvaGVtaWFuY29kaW5nLmNvbS9za2V0Y2ggLS0+CiAgICA8dGl0bGU+5Yqg6L29PC90aXRsZT4KICAgIDxkZXNjPkNyZWF0ZWQgd2l0aCBTa2V0Y2guPC9kZXNjPgogICAgPGRlZnM+PC9kZWZzPgogICAgPGcgaWQ9IuS4gOacn+S8mOWMliIgc3Ryb2tlPSJub25lIiBzdHJva2Utd2lkdGg9IjEiIGZpbGw9Im5vbmUiIGZpbGwtcnVsZT0iZXZlbm9kZCI+CiAgICAgICAgPGcgaWQ9IuWFqOWxgOWKoOi9vS1jb3B5LTIiIHRyYW5zZm9ybT0idHJhbnNsYXRlKC05NDAuMDAwMDAwLCAtMzc3LjAwMDAwMCkiIGZpbGw9IiM3MzdBOEQiIGZpbGwtcnVsZT0ibm9uemVybyI+CiAgICAgICAgICAgIDxnIGlkPSLliqDovb0iIHRyYW5zZm9ybT0idHJhbnNsYXRlKDk0MC4wMDAwMDAsIDM3Ny4wMDAwMDApIj4KICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0zMC41NTk4NDgyLDM1LjIxNjc3ODMgQzMwLjk4NjIyODUsMzUuOTM2OTcxNiAzMC43NDYwMzU1LDM2Ljg5NzIxNDIgMzAuMDAxMzc3NiwzNy4zMDIyODg3IEMyOS4yNjg3MzYyLDM3LjcyODQxMzggMjguMzQwOTk4LDM3LjQ3NjM1NTIgMjcuOTI2NjM0MiwzNi43NDQxNTI2IEMyNy40NzkyMzY0LDM2LjAyMzk1OTMgMjcuNzQwNDkyNiwzNS4wODQ3MjE1IDI4LjQ2MTExNzQsMzQuNjU4NjQyMiBDMjkuMjE3NzQ2MSwzNC4yMzU1NzY2IDMwLjEzMzUxMzUsMzQuNDk2NjMwNiAzMC41NTk4NDgyLDM1LjIxNjc3ODMgWiBNMjEuNTQ2MjM1LDM4LjQ3MjYyNTcgQzIxLjU0NjIzNSwzOS4zMDM4MjUzIDIwLjg1ODY0NDEsNDAgMTkuOTkzOTU4Myw0MCBDMTkuMTYyMjE1LDQwIDE4LjQ2NTY2ODksMzkuMzIxODE2NCAxOC40NjU2Njg5LDM4LjQ3MjYyNTcgTDE4LjQ2NTY2ODksMzcuNDAxMzc3IEMxOC40NjU2Njg5LDM2LjU0OTE3MjUgMTkuMTY1MjMwNiwzNS44NzA5ODkgMTkuOTkzOTU4MywzNS44NzA5ODkgQzIwLjg0NjYyNzYsMzUuODcwOTg5IDIxLjU0NjIzNSwzNi41NDkxMjY5IDIxLjU0NjIzNSwzNy40MDEzNzcgTDIxLjU0NjIzNSwzOC40NzI2MjU3IFogTTEyLjEwNjI0MTUsMzYuNzQ3MTY2NCBDMTEuNjcwODYwMiwzNy41MDAzMjgyIDEwLjczMTEwNTQsMzcuNzQwNDIzMSAxMC4wMDc0NjUxLDM3LjMxNDI5ODEgQzkuMjY1ODY4NDMsMzYuODk3MTY4NiA5LjAwMTU5NjY4LDM1Ljk2MDk5MDMgOS40NDAwMzkyMywzNS4yMTY3Nzg0IEwxMC41Nzc5NTIxLDMzLjIyNzI5NjggQzExLjAxNjM0OSwzMi40OTUwOTQxIDExLjk1NjE0OTQsMzIuMjQzMDM1NiAxMi42OTc3OTE3LDMyLjY2MDE2NTEgQzEzLjQxODQxNjUsMzMuMDg2MjQ0NCAxMy42ODI1OTY5LDM0LjAzNzUzNzEgMTMuMjQ0MjQ1NywzNC43NTc2ODQ4IEwxMi4xMDYyNDE1LDM2Ljc0NzE2NjQgWiBNNC43ODMwMjYzLDMwLjU2NTY1OTEgQzQuMDQxNDI5NjgsMzAuOTc5Nzc0OCAzLjA4OTY1ODQ2LDMwLjczMDY4NDQgMi42ODQyOTU1NCwyOS45OTg1Mjc0IEMyLjI0ODkxNDIyLDI5LjI3ODMzNDEgMi40OTgxMDgyMiwyOC4zMjcxMzI3IDMuMjMwNzQ5NTgsMjcuOTAxMDUzMyBMNi4yOTAzNDM5LDI2LjEzMDYxNjEgQzcuMDIyOTg1MjUsMjUuNzI1NDk1OSA3Ljk3MTc4NjYxLDI1Ljk1MzU4MTUgOC4zODkxMjAzMywyNi42OTc3MDIyIEM4LjgwMzQzODUxLDI3LjQxNzg5NTUgOC41NTEyMjg5MiwyOC4zNjkxNDI1IDcuODIxNjQ4ODMsMjguNzk1MjIxOSBMNC43ODMwMjYzLDMwLjU2NTY1OTEgWiBNMS41MjgyODkzOSwyMS41MzMzNzg5IEMwLjY3NTU3NDM0NiwyMS41MzMzNzg5IDAsMjAuODQ2MTk5OCAwLDIwLjAwNjAwNDYgQzAsMTkuMTUzODQ1OSAwLjY3NTU3NDM0NiwxOC40NjY2NjY3IDEuNTI4Mjg5MzksMTguNDY2NjY2NyBMNi4yOTMzMTM3NiwxOC40NjY2NjY3IEM3LjE0NjAyODgxLDE4LjQ2NjY2NjcgNy44MjE2MDMxNSwxOS4xNTM4NDU5IDcuODIxNjAzMTUsMTkuOTk0MDQxIEM3LjgyMTYwMzE1LDIwLjg0NjE5OTggNy4xNDYwMjg4MSwyMS41MzMzNzg5IDYuMjkzMzEzNzYsMjEuNTMzMzc4OSBMMS41MjgyODkzOSwyMS41MzMzNzg5IFogTTMuMjMzNzE5NDgsMTIuMDg3MDc0NCBDMi40ODkxMDczMywxMS42NzI5NTg2IDIuMjQ4OTE0MjYsMTAuNzMzNjc1MyAyLjY4NzI2NTQ1LDEwLjAwMTU2NCBDMy4wOTI2MjgzNiw5LjI2MDM2NTc1IDQuMDQxMzg0MDUsOS4wMDgyNjE1MyA0Ljc4NjA0MTg5LDkuNDM0Mzg2NiBMOS45NjIzNjg5LDEyLjQzODEyOTggQzEwLjY5NTAxMDIsMTIuODU1MjU5MyAxMC45MzUyMDMzLDEzLjc5MTQzNzYgMTAuNTI5ODQwNCwxNC41MTQ2NDQ2IEMxMC4wOTQ0NTkxLDE1LjI1NTg0MjggOS4xNTQ3MDQzMywxNS40OTU5Mzc3IDguNDM0MDc5NSwxNS4wODE3NzYzIEwzLjIzMzcxOTQ4LDEyLjA4NzA3NDQgWiBNOS40Mzk5OTM1NSw0Ljc3MTIxMjM1IEwxMy4wNDYwNDE5LDExLjAyNzc4OTIgQzEzLjQ4NDM5MzEsMTEuNzU5OTkxOSAxNC40MjQyMzkyLDEyLjAyMTAwMDMgMTUuMTUzODE5MywxMS41ODU5MjU0IEMxNS44ODY0NjA3LDExLjE3MTg1NTMgMTYuMTI2NjUzNywxMC4yMjA1NjI2IDE1LjcwMDMxOSw5LjQ4ODQwNTY1IEwxMi4wODIyNTQyLDMuMjQzODgzNzQgQzExLjY2Nzg5MDMsMi41MjA3MjIzNCAxMC43MjgwNDQyLDIuMjU5NjY4MjEgMTAuMDA3NDY1MSwyLjY4NTc0NzU5IEM5LjI2Mjg1Mjg5LDMuMTExNzgxMzYgOS4wMjI2NTk4Myw0LjA1MTAxOTAzIDkuNDM5OTkzNTUsNC43NzEyMTIzNSBaIE0xOC40NjU2Njg5LDEuNTM5NDI5MjQgQzE4LjQ2NTY2ODksMC43MDgxODQwMzMgMTkuMTY1MjMwNiwwIDE5Ljk5Mzk1ODMsMCBDMjAuODQ2NjI3NiwwIDIxLjU0NjIzNSwwLjY5MDE5Mjg4MiAyMS41NDYyMzUsMS41Mzk0MjkyNCBMMjEuNTQ2MjM1LDguNzQ3MjA3NDUgQzIxLjU0NjIzNSw5LjU5OTQxMTg5IDIwLjg1ODY0NDEsMTAuMjg2NjM2NyAxOS45OTM5NTgzLDEwLjI5ODU1NDcgQzE5LjE2MjIxNSwxMC4yOTg1NTQ3IDE4LjQ2NTY2ODksOS42MjMzODQ4NiAxOC40NjU2Njg5LDguNzQ3MjA3NDUgTDE4LjQ2NTY2ODksMS41Mzk0MjkyNCBaIE0yNy45MDU2NjI0LDMuMjQzODM4MDkgQzI4LjM0NDAxMzYsMi41MDI2ODU1NCAyOS4yNzE4NDMyLDIuMjU5NjIyNTYgMzAuMDA0MzkzMiwyLjY3NjcwNjM5IEMzMC43NDYwMzU1LDMuMDkwODIyMTMgMzEuMDEwMjYxNSw0LjAzMDEwNTQ1IDMwLjU3MTg2NDcsNC43NzQyMjYwOSBMMjYuOTU2NzY5NywxMS4wMzA4MDI5IEMyNi41NDg0MzY5LDExLjc1OTk5MTkgMjUuNTg3NjE5LDEyLjAyMTA0NTkgMjQuODU4MDM4OSwxMS41OTc5MzQ2IEMyNC4xMjUzOTc2LDExLjE3MTg1NTMgMjMuODg1MjA0NSwxMC4yMjA1NjI2IDI0LjI5OTUyMjcsOS40ODg0MDU2MSBMMjcuOTA1NjYyNCwzLjI0MzgzODA5IFogTTM1LjI0OTg5NSw5LjQzNDM4NjYgTDI4Ljk3NzU1MywxMy4wNDczMTY5IEMyOC4yNDQ5MTE3LDEzLjQ2MTM4NyAyNy45OTI3MDIxLDE0LjQwMDYyNDcgMjguNDEwMDgxNSwxNS4xNDQ3OTEgQzI4Ljg0NTQ2MjgsMTUuODY0OTg0MyAyOS43ODUyNjMzLDE2LjEwNTAzMzUgMzAuNTA4ODU4LDE1LjcwMjk3MjggTDM2Ljc4MTIsMTIuMDkwMDQyNSBDMzcuNTAxODI0OCwxMS42NzU5MjY3IDM3Ljc2NjAwNTIsMTAuNzM2NzM0NyAzNy4zMjc2NTQsMTAuMDA0NTMyIEMzNi45MTAyNzQ2LDkuMjYwMzIwMSAzNS45Njc1MDQzLDguOTk2Mjk3ODkgMzUuMjQ5ODk1LDkuNDM0Mzg2NiBaIE0zOC40NzE1NTIzLDE4LjQ2NjYyMTEgQzM5LjMzMzMxMzksMTguNDY2NjIxMSA0MC4wMTE5MDM5LDE5LjE1MzgwMDIgMzkuOTk5ODQxNywyMC4wMDU5NTkgQzM5Ljk5OTg0MTcsMjAuODQ2MTU0MSAzOS4zMzMzMTM5LDIxLjUzMzMzMzMgMzguNDcxNTUyMywyMS41MzMzMzMzIEwzMS4yNTA0NTQ2LDIxLjUzMzMzMzMgQzMwLjQxODc1NywyMS41MzMzMzMzIDI5LjcxOTE0OTYsMjAuODQ2MTU0MSAyOS43MTkxNDk2LDE5Ljk5Mzk5NTQgQzI5LjcxOTE0OTYsMTkuMTUzODAwMiAzMC40MTg3NTcsMTguNDY2NjIxMSAzMS4yNTA0NTQ2LDE4LjQ2NjYyMTEgTDM4LjQ3MTU1MjMsMTguNDY2NjIxMSBaIiBpZD0iU2hhcGUiPjwvcGF0aD4KICAgICAgICAgICAgPC9nPgogICAgICAgIDwvZz4KICAgIDwvZz4KPC9zdmc+" />
            </div> : <FormattedMessage id="No.record" />}
                            </div></td></tr>
                            
                            }
                        </tbody>
                    </table>
                </div>

                <div className="bk-pageNav">
                    <div className="tablist deposits" style={{display:'none'}}>
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

export default injectIntl(InvestmentR);