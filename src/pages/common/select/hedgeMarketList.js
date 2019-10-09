/* 保值交易市场 */

import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf'
const Option = Select.Option

export default class HedgeMarketList  extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            marketsList:this.props.paymod?[]:[<Option key='0' value=''>请选择</Option>],
        }
    }

    componentDidMount(){
        axios.get(DOMAIN_VIP + '/brush/common/markets').then(res => {
            const result = res.data;
            let marketsList = this.state.marketsList;
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    marketsList.push(<Option key={i + 1} value={result.data[i]}>{result.data[i]}</Option>)
                }
                this.setState({
                    marketsList: marketsList
                })
            }
        })
    }

    render(){
        const { market,marketsList } = this.state
        
        return (
            <div className="form-group">
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-2 control-label'} >{this.props.title ? this.props.title: "交易市场："}</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-10"}>
                    <Select value={this.props.market}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {marketsList}
                    </Select>
                </div>
            </div>
        )
    }

}