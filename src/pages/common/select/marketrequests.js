import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP, SELECTWIDTH } from '../../../conf'
const Option = Select.Option

export default class MarketList extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            typeList: this.props.paymod ? [] : [<Option key='0' value=''>请选择</Option>],
            market: 'etc_btc'
        }
    }

    componentDidMount() {
        axios.get(DOMAIN_VIP + '/common/queryMarket').then(res => {
            const result = res.data;
            let accountTypeArr = [], _accountTypeArr = [];
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    accountTypeArr.push(<Option key={i} value={result.data[i].toUpperCase().replace(/_/g, '')}>{(result.data[i]).toUpperCase()}</Option>)
                }
                for (let i = 0; i < result.data.length; i++) {
                    _accountTypeArr.push(<Option key={i} value={result.data[i]}>{(result.data[i]).toUpperCase()}</Option>)
                }
                if (this.props.underLine) {
                    this.setState({
                        typeList: [...this.state.typeList, ..._accountTypeArr]
                    })
                } else {
                    this.setState({
                        typeList: [...this.state.typeList, ...accountTypeArr]
                    })
                }
            }
        })
    }

    render() {
        const { market, typeList } = this.state
        const _col = this.props.col || 3;
        return (
            <div className="form-group">
                <label className={_col ? 'col-sm-' + _col + ' control-label' : 'col-sm-2 control-label'} >{this.props.title ? this.props.title : "交易市场："}</label>
                <div className={_col ? 'col-sm-' + (11 - _col) : "col-sm-10"}>
                    <Select value={this.props.market} style={this.props.changeStyle ? { width: '100%' } : { width: SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {typeList}
                    </Select>
                </div>
            </div>
        )
    }

}