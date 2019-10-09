import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, SELECTWIDTH } from 'Conf'
import { Tabs, Button, Modal, Select } from 'antd'
import { toThousands, dateToFormat } from 'Utils'
import { AsyncSelect, SeOp } from "../../../components/select/asyncSelect";
import ModalAddEditMarket from './modal/modalAddEditMarket'
import GoogleCode from "../../common/modal/googleCode";
import React from "react";


const { TabPane } = Tabs;
const Option = Select.Option
// const _userType = {
//     1: '普通用户',
//     2: '广告商家'
// }
const _dataType =['_btc','_usdt','_cny']

@Decorator()
export default class MarketConfig extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            market: '',
            type: ''
        }
        this.state = {
            tk: '1',
            userType:'',
            userTypeList:[], // 用户资质列表
            modalHtml: '',
            marketsList: [],
            modalTitle: '',
            ...this.defaultState,
        }
        this.goofn = () => new Map([
            ['freezeOrNo', v => this.submitData(v)],    //是否冻结账户
        ])
    }
    componentDidMount() {
        // let arr = [<Option key='0' value=''>请选择</Option>]
        // const rs = await this.request({ url: '/sys/market/getMarketName', type: 'post' })
        // for (let i = 0; i < rs.length; i++) {
        //     arr.push(<Option key={i + 1} value={rs[i]}>{rs[i].toUpperCase()}</Option>)
        // }
        // await this.setState({
        //     marketsList: arr
        // })
        this.getUserType()
        this.requestTable()
    }
    // 获取用户资质列表
   async getUserType () {
        let arr = [<Option key='0' value=''>请选择</Option>]
        const rs = await this.request({ url: '/common/getUserQualificationList', type: 'post' })
        console.log(rs);
        for (let i = 0; i < rs.length; i++) {
            arr.push(<Option key={i + 1} value={rs[i].key}>{rs[i].value}</Option>)
        }
        await this.setState({
            userTypeList: [...arr]
        })
    }
    // 获取币种市场列表
    async getMarketList() {
        let arr = [<Option key='0' value=''>请选择</Option>]
        const rs = await this.request({ url: '/common/getUserQualificationList', type: 'post' })
        console.log(rs);
    }

    requestTable = async (currIndex, currSize) => {
        const { pageIndex, pageSize, market, tk, type } = this.state
        let url = {
            1: '/userConfig/query',
            2: '/marketConfig/query'
        }
        let t = '';
        if (type == ''){
            t = -1
        }else if (type == 0){
            t = 0
        }else{
            t = Number(type)
        }
        let params = {
            type: t,
            pageIndex: currIndex || pageIndex,
            pageSize: currSize || pageSize
        }
        const result = await this.request({ url: url[tk], type: 'post' }, params)
        this.setState({
            dataSource: result.list || [],
            pageTotal: result.totalCount,
            pageSize: currSize || pageSize,
            pageIndex: currIndex || pageIndex
        })
    }
    updStatus = ({id,enable}) => {
        const {tk} = this.state
        let urls = {
            1:'/userConfig/stop',
            2:'/marketConfig/stop'
        }
        Modal.confirm({
            title:`你确定要${enable == 1 ?'停用':'开启'}吗？`,
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk:async () => {
                this.modalGoogleCode(id, 'freezeOrNo', 'check')
            },
            onCancel(){
                console.log('Cancel')
            }
        })
    }

    submitData = (id) =>{
        this.request({url:'/userConfig/stop',type:'post'},{id})
        this.requestTable()
    }
    createColumns = (pageIndex, pageSize) => {
        if (this.state.tk == 1) {
            return [
                { title: '序号', dataIndex: 'index', width:'100px', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 } ,
                { title: '用户资质', dataIndex: 'typeName' },
                { title: '用户数量', dataIndex: 'userCount', render: text => toThousands(text, true) },
                { title: '保证金', dataIndex: 'depositNum', render: text => toThousands(text, true) },
                { title: '状态', dataIndex: 'enable', render: text => text == 1 ? '开启' : '停用' },
                {
                    title: '操作', render: (record) => <span>
                        <a href="javascript:void(0)" className="mar10" onClick={() => this.addEdit(record.type, record)}>修改</a>
                        <a href="javascript:void(0)" className="mar10" onClick={() => this.updStatus(record)}>{record.enable == 1 ? '停用' : '开启'}</a>
                    </span>
                },
            ]
        }
    }
    tcb = async tk => {
        await this.setState({ tk })
        this.requestTable()
    }
    addEdit = (type, item = null) => {
        const { marketsList } = this.state
        this.setState({
            modalTitle: `参数配置`,
            modalVisible: true,
            modalHtml: <ModalAddEditMarket _tk={this.state.tk} requestTable={this.requestTable} item={item} marketsList={marketsList} _type={type} _userType={this.state.userTypeList} handleCancel={this.handleCancel} />
        })
    }
    handleCancel = () => {
        this.setState({ modalVisible: false })
    }
    render() {
        const { showHide, pageTotal, pageIndex, pageSize, dataSource, tk, modalHtml, modalVisible, marketsList, market, type ,userTypeList} = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户资质：</label>
                                        <div className="col-sm-9">
                                            <Select value={type} style={{ width: SELECTWIDTH }} onChange={v => this.onSelectChoose(v, 'type')} >
                                                {userTypeList}
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                {/*<SeOp title='用户类型' value={type} onSelectChoose={v => this.onSelectChoose(v, 'type')} ops={_userType} pleaseC={true} />*/}
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable(pageIndex)}>查询</Button>
                                        <Button type="primary" onClick={this.addEdit}>新增</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">

                                    <CommonTable
                                        dataSource={dataSource}
                                        pagination={
                                            {
                                                total: pageTotal,
                                                pageSize: pageSize,
                                                current: pageIndex

                                            }
                                        }
                                        columns={this.createColumns(pageIndex, pageSize)}
                                        requestTable={this.requestTable}
                                    // scroll={this.islessBodyWidth() ? { x: 1800 } : {}}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={modalVisible}
                    title={this.state.modalTitle}
                    width={1100}
                    onCancel={this.handleCancel}
                    footer={null}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}