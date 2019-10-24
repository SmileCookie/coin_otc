import React from 'react';
import { Link } from 'react-router';
import { connect } from 'react-redux';

import Form from '../../decorator/form';
import Select from '../../components/form/select';
import { formatURL } from '../../utils';
import { fetchCoinList, fetchCoinCm } from '../../redux/modules/session';
import '../../assets/css/userauth.less';

@connect(
    state => ({}),
    {
        fetchCoinList,
        fetchCoinCm,
    }
)
@Form
class AuthOne extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            payAddress: '',
            selectCode: 2, // default btc
            token: localStorage.getItem("token"),
        };

        this.state = {
            ...this.base,
            selectItem: {},
            coinList : [],
            email: localStorage.getItem("email"),
        };

        this.dictionaries = [...Object.keys(this.base)];
        this.cm = this.cm.bind(this);
        this.getCode = this.getCode.bind(this);
    }
    cm(){
        if(!this.hasError(this.dictionaries)){
            const { payAddress, email, selectItem } = this.state;

            this.props.fetchCoinCm({...this.getState(this.dictionaries), address: payAddress, userName: email, propTag: selectItem.name}, () => {return this}, 'payAddress', this.props.intl.formatMessage);
        }
    }
    getCode(item = {}){
        this.fIn(this.refs.ad);

        this.setState({
            selectCode: item.code,
            selectItem: item,
        });
    }
    componentDidMount(){
        this.props.fetchCoinList().then(res => {
            this.setState({
                coinList: res
            });
        });
    }
    render(){
        const { formatMessage } = this.intl;
        const { selectItem, coinList:list } = this.state;

        // default select btc
        const c = 2;

        const { getCode, setPayAddress, fIn, bOut, cm } = this;
        const { payAddress, errors } = this.state;
        const { payAddress:epayAddress = [] } = errors;
        
        return (
            <form className="uauth_wp min_h_d clearfix">
                <div className="l">
                    <div className="plv">
                        <Link  to={formatURL('notGCode')} className="iconfont bk">&#xe6a3;</Link>
                        <h2 className="tith">{formatMessage({id: "身份验证"})}</h2>
                    </div>
                    <p className="alt2 mb10">{formatMessage({id: "请输入您在XXXX进行过充值操作的地址，以便核实您的身份"}).replace('XXXX','btcwinex')}</p>
                    <ul className="list">
                        <li className={`lst3x ${epayAddress[0] && 'err'}`}>
                            <div className="plv">
                                {list.length ? <Select list={list} currentCode={c} getCode={getCode}></Select>:null}
                                <input name="payAddress" ref="ad" onFocus={fIn} onBlur={bOut} onChange={setPayAddress} value={payAddress} style={{paddingLeft: '80px'}} type="text" autoComplete="off" className="i1" placeholder={formatMessage({id: "请输入充值地址（水印）"})} />
                            </div>
                            <span className="ew">{epayAddress[0]?epayAddress[0].replace('XXXX', selectItem.name):null}</span>
                        </li>
                    </ul>
                    <div className="subs mb20">
                        <input onClick={cm} type="button" value={formatMessage({id: "确定"})} className="i3 v" />
                    </div>
                    <p className="alt">{formatMessage({id: "如何查找充值地址？"})}</p>
                    <p className="alt">{formatMessage({id: "您可以通过其他平台的提现记录或钱包的转账记录查询到您在XXX的充值地址。"}).replace('XXX','btcwinex')}</p>
                </div>
            </form>
        );
    }
}

export default AuthOne;