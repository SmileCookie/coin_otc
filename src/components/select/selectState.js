import { Select } from 'antd'
import { SELECTWIDTH } from 'Conf'
const { Option } = Select

export default class SelectState extends React.Component {
    constructor(props) {
        super(props)
       
    }
    
    render() {
        return (
            <div className="form-group">
                <label className="col-sm-3 control-label">状态：</label>
                <div className="col-sm-8">
                    <Select value={this.props.state} style={{ width: SELECTWIDTH }} onChange={(v) => this.props.changeState(v)}>
                        <Option value="">请选择</Option>
                        <Option value="0">正常</Option>
                        <Option value="1">异常</Option>
                    </Select>
                </div>
            </div>
        )
    }
}


















