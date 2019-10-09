import React from 'react'
import { Button, Tabs, Select, Pagination, Modal, message, Input } from 'antd'
import { PAGESIZE, PAGEINDEX, DOMAIN_VIP, TIMEFORMAT, SELECTWIDTH, PAGRSIZE_OPTIONS20 } from '../../../../conf'
const Option = Select.Option;
import { SeOp } from '../../../../components/select/asyncSelect'

export default class UserType extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            modalCustomer: ''
        }
    }
    componentDidMount() {
        const { modalCustomer } = this.props
        this.setState({
            modalCustomer
        })
    }
    componentWillReceiveProps(nextProps) {
        const { modalCustomer } = nextProps
        this.setState({
            modalCustomer
        })
    }
    //用户类型弹窗 select
    handleModalCustomer = (val) => {
        this.setState({
            modalCustomer: val
        })
        this.props.handleModalCustomer(val)
    }
    render() {
        return (

            <SeOp title='账户类型' value={this.state.modalCustomer} colmg={true} onSelectChoose={v => this.handleModalCustomer(v)} ops={this.props._customerType} pleaseC={false} />

        )
    }
}