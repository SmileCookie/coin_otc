import React from 'react';
import { Table,Modal,message } from 'antd'
import moment from 'moment'
import { toThousands } from '../../../../utils'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,TIMEFORMAT,PAGEINDEX,PAGESIZE,DEFAULTVALUE, TIMEFORMAT_ss,PAGRSIZE_OPTIONS20 } from '../../../../conf'
import GoogleCode from '../../../common/modal/googleCode'
const {Column} = Table
const confirm = Modal.confirm


export default class ModalOperatWithdrawApprove extends React.Component{
    constructor(props){
        super(props)
        this.state= {
            tableSource:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            googVisibal:false,
            check:'',
            item:{},

        }
    }
    componentDidMount(){
        console.log(this.props)
        const { type } = this.props
        this.setState({
            tableSource:[]
        },()=>{
            if(type==1){
                this.requestTable()
            }else if(type==2){
                this.requestTableCheck()
            }
        })
    }
    componentWillUnmount(){

    }
    componentWillReceiveProps(nextProps){
        // console.log(nextProps)
        const { type } = nextProps
        this.setState({
            tableSource:[]
        },()=>{
            if(type==1){
                this.requestTable()
    
            }else if(type==2){
                this.requestTableCheck()
            }
        })
    }
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name] : value
        })
    }
    //请求数据--记账选择
    requestTable = (currentIndex, currentSize) => {
        const {pageIndex,pageSize,pagination,id} = this.state
        const {fundstype,downloadamountS,downloadamountE,createtimeS,createtimeE} = this.props
        axios.post(DOMAIN_VIP+'/feeAccountCheck/choice',qs.stringify({
            fundstype:'0',amountS:downloadamountS,amountE:downloadamountE,configtimeS:createtimeS,configtimeE:createtimeE,type:1,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.data.totalCount;
                pagination.onChange = this.onChangePageNum;
                pagination.onShowSizeChange = this.onShowSizeChange
                this.setState({
                    tableSource:tableSource,
                    pagination,
                })
            }else{
                message.warning(result.msg);
            }
        })
    }
    //记账查看
    requestTableCheck = () => {
        axios.post(DOMAIN_VIP+'/feeAccountCheck/view',qs.stringify({
            checkId:this.props.item.id
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource= [];
                tableSource.push(result.data)
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = i+1;
                    tableSource[i].key = tableSource[i].id
                }
                this.setState({
                    tableSource:tableSource,
                })
            }else{
                message.warning(result.msg);
            }
        })
    }
    onChangePageNum = (pageIndex,pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    //匹配检查
    checkMatch = (nowObj,prevObj) => {
        console.log(nowObj, prevObj)
        let fundstypename = nowObj.fundstypename == prevObj.fundstypename;
        let txamount = toThousands(nowObj.txamount,true) == toThousands(prevObj.downloadamount,true);
        let receivewallet = nowObj.receivewallet == prevObj.downloadaddress;
        if(!fundstypename){
            message.warning('资金类型不匹配！')
            return false;
        }else if(!txamount){
            message.warning('交易金额不匹配！')
            return false;
        // }else if(!receivewallet){
        //     // message.warning('提现地址不匹配！')
        //     return false;
        }else{
           return true; 
        }                
    }
    //选择匹配弹框
    selectMatch = (item, type) => {
        let self = this
        Modal.confirm({
            title: '确认匹配此笔记账信息？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                if(!self.checkMatch(item, self.props.item)) return;
                self.modalGoogleCode(item)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //选择匹配按钮
    selectMatchBtn = item => {
        axios.post(DOMAIN_VIP+'/feeAccountCheck/check2',qs.stringify({
            checkId:this.props.item.id,choiceId:item.id,operationType:2
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.props.requestTable()
                this.props.handleCancel()
            }else{
                message.warning(result.msg);
            }
        })
    }
    //google验证弹窗
    modalGoogleCode = (item) => {
        this.setState({
            googVisibal:true,
            item,
        })
    }
    //google 按钮
    modalGoogleCodeBtn = value => {
        const { item,googletype } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                })
                this.selectMatchBtn(item)
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCreate = () =>{
        const form = this.formRef.props.form;
        form.validateFields((err, valus) => {
            if(err){
                return;
            }
            form.resetFields();
            this.modalGoogleCodeBtn(valus)
        })
    }
    saveFormRef = formRef => {
        this.formRef = formRef
    }
    //google弹窗关闭
    onhandleCancel = () =>{
        this.setState({
            googVisibal:false
        })
    }
    render() {
        const { tableSource,pagination,googVisibal,check } = this.state
        return (
            <div className='col-md-12 col-sm-12 col-xs-12'>
                <div className="table-responsive">
                    <Table dataSource={tableSource} pagination={pagination} bordered locale={{emptyText:'暂无数据'}} scroll={tableSource.length>0?this.props.type==1?{x:2000}:{x:1410}:{}}>
                        <Column title='序号'  dataIndex='index'  key='index' fixed={tableSource.length>0?true:false}/>
                        <Column title='交易流水号'  dataIndex='txid'  key='txid' />
                        <Column title='资金类型'  dataIndex='fundstypename'  key='fundstypename' />
                        <Column title='发送方'  dataIndex='sendwallet'  key='sendwallet' />
                        <Column title='接收方'  dataIndex='receivewallet'  key='receivewallet' />
                        <Column className='moneyGreen' title='交易金额'  dataIndex='txamount'  key='txamount' render={text=>toThousands(text,true)} />
                        <Column className='moneyGreen' title='网络费'  dataIndex='fee'  key='fee' />
                        <Column title='交易类型'  dataIndex='dealtype'  key='dealtype' render={text=>{
                            switch (text){
                                case 1 :
                                    return '充值';
                                case 2 :
                                    return '提现(热提)';
                                case 3 :
                                    return '冷钱包到热提钱包转账';
                                case 4 :
                                    return '其他到热提';
                                case 5 :
                                    return '其他到热提';
                                case 6 :
                                    return '其他到冷';
                                case 7 :
                                    return '冷到其他';
                                case 8 :
                                    return '热提到其他';
                                default :
                                    return '--';
                            }
                        }} />
                        <Column title='区块高度'  dataIndex='blockheight'  key='blockheight' />
                        <Column title='确认时间'  dataIndex='configtime'  key='configtime' render={text=>text?moment(text).format(TIMEFORMAT_ss):'--'} />
                        {this.props.type==1&&this.props.limitBtn.includes('check2')&&<Column title='操作'  dataIndex='action'  key='action' render={(text,record)=><a href='javascript:void(0);' onClick={()=>this.selectMatch(record)}>选择匹配</a>} />}
                    </Table>
                </div>
                <GoogleCode 
                wrappedComponentRef={this.saveFormRef}
                check={check}
                handleInputChange={this.handleInputChange}
                mid='MOWA'
                visible={googVisibal}
                onCancel={this.onhandleCancel}
                onCreate={this.handleCreate}/>
            </div>
        )
    }

}