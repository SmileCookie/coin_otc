import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import {message,Input,Modal,Button,Select,Pagination } from 'antd'
import ModalAddCurrencyOTC from './modal/modalAddCurrencyOTC'
import Decorator from '../../decorator'
import CommonTable from '../../common/table/commonTable'
import { pageLimit, islessBodyWidth} from '../../../utils'
const Option = Select.Option

/** 
 * 该组件2019.07.04重写，删除无用字段，采用commonTable 写法
 * 若恢复原有字段，将备份文件 currencyManageOTC_bak.js 改为 currencyManageOTC.js即可
 */

@Decorator()
export default class CurrencyManageOTC extends React.Component{
    constructor(props){
        super(props)
        this.defaultState = {
            visible:false,
            title:'',
            width:'',
            modalHtml:'',
            status:'',
            value:'',
            type:'',
            showHide:true,
            coinTypeId: '',
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal:0,
            coinType: '',
            coinName:'' ,
            coinFullName: '',
            coinTag: '',
            coinUrl : '',
            recoinType: '',
            loading:false,
            item:'',
        }
        this.state = {
            limitBtn:[],
            tableList:[],
            ...this.defaultState,
        }
        this.requestTable = this.requestTable.bind(this)
        this.changeDetail = this.changeDetail.bind(this)
        this.handCancel = this.handCancel.bind(this)
        this.onAuditInfoBtn = this.onAuditInfoBtn.bind(this)
        this.deleteItem = this.deleteItem.bind(this)
        this.handleFundChange =  this.handleFundChange.bind(this)
        this.setcoinUrl= this.setcoinUrl.bind(this)
        this.handleSelectChange = this.handleSelectChange.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('otcCointype',this.props.permissList)
        })
    }
    requestTable = async (currentIndex,currentSize) =>{
        const { name,recoinType,pageIndex,pageSize } = this.state
        const params = {
            coinType:recoinType,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        }
        const result = await this.request({ url: '/otcCointype/query', type: 'post'}, params)
        this.setState({
            tableList:result.list,
            pageTotal:result.totalCount,
            pageIndex:result.currPage
        })
    }

    // Modal下拉选择，设置到state
    handleSelectChange(stateObj) {
        this.setState(stateObj)
    }

    //资金类型
    handleFundChange(val){
        this.setState({
            recoinType:val
        })
    }
    setcoinUrl(coinUrl){
        this.setState({
            coinUrl
        })
    }
    changeDetail(title, item){
        this.setState({
            title,
            item,
            visible:true,
            width:'1200px',
            modalHtml:<ModalAddCurrencyOTC setcoinUrl={this.setcoinUrl} item={item} handleInputChange ={this.handleInputChange} handleSelectChange={this.handleSelectChange}/>,
            coinType:item.coinType|| 1,
            coinName:item.coinName||'',
            coinFullName:item.coinFullName||'',
            coinTag:item.coinTag||'',
            coinUrl:item.coinUrl||'',
            fundsType:item.fundsType||0
        })
    }
    onAuditInfoBtn(){
        this.setState({
            loading:true
        })
        const {coinType,coinName,coinFullName,coinTag,coinUrl,item,fundsType}=this.state
        if (coinType===''||coinName===''||coinFullName===''||coinTag==='') {
            message.warning('必填项不能为空！')
            return false
        }
        let params = {
            coinType, coinName, coinFullName, coinTag, coinUrl, fundsType
        }
        let url = ''
        if (item.id) {
            params.id = item.id
            url = '/otcCointype/update'
        }else {
            url = '/otcCointype/insert'
        }
        axios.post(DOMAIN_VIP+url, qs.stringify(params)).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState(
                    {
                        visible:false,
                        loading:false
                    }
                )
                this.requestTable()
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        })
    }
    //关闭弹窗
    handCancel(){
        this.setState({
            visible:false,
            loading:false
        })
    }
    //删除
    deleteItem(id){
        let self = this;
        Modal.confirm({
            title: "确定删除本项吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+"/otcCointype/delete",qs.stringify({
                        id
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.requestTable()
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    createColumns = (pageIndex, pageSize) => {
        const { hedgeResults } = this.state
        return [
            { title: '序号', dataIndex: 'index', render: (text, record, index) => (pageIndex - 1) * pageSize + index + 1 },
            { title: '币种姓名', dataIndex: 'coinName' },
            { title: '币种类型', dataIndex: 'coinType', render: text => text==1?'虚拟货币':'法币'},
            { title: '币种全称', dataIndex: 'coinFullName'},
            { title: '图标地址', dataIndex: 'coinUrl', width: '50%', render: (text) => <div style={{ wordWrap: 'break-word', wordBreak: 'break-all', whiteSpace: 'normal' }}>
                                                                                                    {text}
                                                                                                </div>},
            { title: '操作', render: (record) =>  <div><a href="javascript:void(0)" className="mar10" onClick={()=>this.changeDetail('修改信息', record)}>修改</a> <a href="javascript:void(0)" className="mar10" onClick={()=>this.deleteItem(record.id)}>删除</a></div> },
        ]
    }

    render(){
        const {tableList,visible,title,width,modalHtml,showHide,recoinType,pageTotal,pageIndex,pageSize,limitBtn} = this.state
        return(
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
                                        <label className="col-sm-3 control-label">币种类型:</label>
                                        <div className="col-sm-8">
                                            <Select value={recoinType} style={{ width: SELECTWIDTH }} onChange={this.handleFundChange}>
                                                <Option value="">请选择</Option>
                                                <Option value={1}>虚拟货币</Option>
                                                {/*<Option value={2}>法币</Option>*/}
                                            </Select>
                                        </div>
                                    </div> 
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => {this.requestTable()}}>查询</Button>
                                        <Button type="primary" onClick={() => {this.resetState()}}>重置</Button>
                                        {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={() => {this.changeDetail('新增', '')}}>新增</Button>:''}
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive center">    
                                    <CommonTable
                                        dataSource={tableList}
                                        pagination={
                                            {
                                                total: pageTotal,
                                                pageSize: pageSize,
                                                current: pageIndex
                                            }
                                        }
                                        columns={this.createColumns(pageIndex, pageSize)}
                                        requestTable={this.requestTable}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handCancel}
                    footer={[
                        <Button key="back" onClick={this.handCancel}>取消</Button>,
                        <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.onAuditInfoBtn()}>
                            保存修改
                        </Button>,
                    ]}
                    >
                    {modalHtml}            
                </Modal>
            </div>
        )
    }
}