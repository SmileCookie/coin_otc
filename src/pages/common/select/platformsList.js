
/**保值平台 */

import React from 'react'
import { Select } from 'antd'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP, SELECTWIDTH } from '../../../conf'
const Option = Select.Option

export default class PlatformsList extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            arrayList: [<Option key='' value=''>请选择</Option>],
            jifenType: ''
        }
    }

    componentDidMount() {
        axios.get(DOMAIN_VIP + '/brush/common/platforms').then(res => {
            const result = res.data;
            let accountTypeArr = this.state.arrayList;
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    accountTypeArr.push(<Option key={i + 1} value={result.data[i]}>{result.data[i]}</Option>)
                }
                this.setState({
                    arrayList: accountTypeArr
                })
            } else {
                message.warning(result.msg)
            }
        })
    }

    render() {
        const { jifenType, arrayList } = this.state
        const col = this.props.col || 3;
        return (
            <div className="form-group">
                <label className={col ? 'col-sm-' + col + ' control-label' : 'col-sm-2 control-label'} >{this.props.title || '第三方平台'}：</label>
                <div className={col ? 'col-sm-' + (11 - col) : "col-sm-10"}>
                    <Select value={this.props.platform} style={{ width: SELECTWIDTH }} onChange={(val) => this.props.handleChange(val)}>
                        {arrayList}
                    </Select>
                </div>
            </div>
        )
    }

}





























