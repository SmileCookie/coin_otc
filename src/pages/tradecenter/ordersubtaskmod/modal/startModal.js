import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Select, Button } from 'antd'
const Option = Select.Option;

export default class ModalTask extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            mdelayTime:''
        }

    }

    componentDidMount() {
        // const { item} = this.props

        this.setState({
            mdelayTime:''
        })
    }

    componentWillReceiveProps(nextProps) {
        // const { item } = nextProps

        this.setState({
            mdelayTime:''
        })
    }

    //输入时 input 设置到 satte
    handleInputChange = (event) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }


    render() {
        const {mdelayTime } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">延时时间(分钟)：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control" name="mdelayTime" value={mdelayTime} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}




























