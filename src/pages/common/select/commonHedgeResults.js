/* 保值交易市场 */
import { Select,message } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../conf'
const Option = Select.Option

export const JudgeHedgeResults =  (list = []) => {
    return new Promise((resolve,reject) => {
        axios.get(DOMAIN_VIP + '/brush/common/hedgeResults').then(res => {
            const result = res.data;
            let hedgeResults = {}
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    list.push(<Option key={i + 1} value={result.data[i].type}>{result.data[i].message}</Option>)
                    hedgeResults[result.data[i].type] = result.data[i].message
                }
                resolve({
                    hedgeResults,
                    list
                })
            }else{
                reject(result.msg)
                message.error(result.msg)
            }
        }).catch(error => message.error(error))
    })
}

export class CommonHedgeResults  extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            resultsList:this.props.paymod?[]:[<Option key='0' value=''>请选择</Option>],
        }
    }

    componentDidMount(){
        JudgeHedgeResults(this.state.resultsList).then(({list}) => {
            this.setState({
                resultsList:list
            })
        })
    }

    render(){
        const { resultsList } = this.state
        
        return (
            <div className="form-group">
                <label className={this.props.col? 'col-sm-'+this.props.col+' control-label' :'col-sm-2 control-label'} >{this.props.title ? this.props.title: "保值状态："}</label>
                <div className={this.props.col?'col-sm-'+(12-this.props.col) :"col-sm-10"}>
                    <Select value={this.props.status}  style={{ width:SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {resultsList}
                    </Select>
                </div>
            </div>
        )
    }

}