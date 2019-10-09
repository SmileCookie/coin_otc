import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import ModalMemoList from './modalMemoList'
import ModalLogin from './modalLogin'
import ModalIdentification from './modalIdentification'
import ModalCapitalList from '../../../financialcenter/capitalmod/modal/modalCapitalList'
import ModalRechargeAddress from '../../../tradecenter/paymentmod/modal/modalRechargeAddress'
import ModalWithdrawAddress from '../../../tradecenter/paymentmod/modal/modalWithdrawAddress'
import ModalRechargeRecord from '../../../tradecenter/paymentmod/modal/modalRechargeRecord'
import ModalWithdrawRecord from '../../../tradecenter/paymentmod/modal/modalWithdrawRecord'
import ModalBillDetail from '../../../financialcenter/capitalmod/modal/modalBillDetail'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../../../conf'
import { Tabs } from 'antd'
const TabPane = Tabs.TabPane


export default class ModalSafe extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            id:null,
            key:"1",
            conKey:"4"
        }

        this.changeKey = this.changeKey.bind(this)
        this.changeConKey =this.changeConKey.bind(this)
    }

    componentDidMount(){
        this.setState({
            id:this.props.id
        })
    }

    componentWillReceiveProps(nextProps){
        this.setState({
            key:"1",
            conKey:"4"
        })
    }
    changeKey(key){
        this.setState({
            key
        })
    }
    changeConKey(key){
        this.setState({
            conKey:key
        })
    }

    render(){
        const { id,key,conKey } = this.state
        return(
            <Tabs activeKey={key} onChange={this.changeKey}>
                <TabPane tab="安全看板" key="1">
                    <Tabs activeKey={conKey} onChange={this.changeConKey}>
                        <TabPane tab="备注信息" key="4"><ModalMemoList id={this.props.id} /></TabPane>
                        <TabPane tab="登录信息" key="5"><ModalLogin id={this.props.id} isInquire modal /></TabPane>
                        <TabPane tab="认证信息" key="6"><ModalIdentification id={this.props.id} isInquire /></TabPane>
                    </Tabs>
                </TabPane>
                <TabPane tab="资金看板" key="2">
                    <Tabs>
                        <TabPane tab="用户资金" key="7"><ModalCapitalList id={this.props.id} isreLoad /></TabPane>
                        <TabPane tab="充值信息" key="8"><ModalRechargeRecord id={this.props.id} isreLoad display={true}/></TabPane>
                        <TabPane tab="提现信息" key="9"><ModalWithdrawRecord id={this.props.id} isreLoad display={true}/></TabPane>
                        <TabPane tab="充值地址" key="10"><ModalRechargeAddress id={this.props.id} modal isreLoad /></TabPane>
                        <TabPane tab="提现地址" key="11"><ModalWithdrawAddress id={this.props.id} modal isreLoad /></TabPane>
                        <TabPane tab="账单明细" key="12"><ModalBillDetail curId={this.props.id} isreLoad /></TabPane>
                    </Tabs>
                </TabPane>
                <TabPane tab="其他看板" key="3">待定</TabPane>
            </Tabs>
        )
    }

}

































