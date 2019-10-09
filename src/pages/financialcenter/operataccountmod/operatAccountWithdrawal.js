import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import cookie from 'js-cookie'
import moment from 'moment'
import { Button,Select,Pagination,message,DatePicker,Table,Modal } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import ModalPlatformWithdraw from './modal/modalPlatformWithdraw'
import FundsTypeList from '../../common/select/fundsTypeList'
import { toThousands,pageLimit } from '../../../utils'
const Big = require('big.js')
const { RangePicker} = DatePicker
const {Column} = Table

export default class OperatAccountWithdrawal extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            visible:false,
            modalHtml:'',
            title:'',
            width:'',
            tableSource:[],
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共 ${total} 条`,
                size:'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            fundstype:'0',
            amountS:'',
            amountE:'',

            userId:cookie.get('userId'),
            userName:cookie.get('userName'),
            mcostdirection:1,
            mdownloadamount:'',
            mdownloadaddress:'',
            refeetype:'',
            tmp:'',
            refundstype:'',
            refundstypeName:'',
            limitAvailableAmount:0,//提现金额的最大的值
            limitBtn:[]
        }
    }
    componentDidMount(){        
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('feeAccountCheck', this.props.permissList)
        },()=>console.log(this.state.limitBtn))
    }
    componentWillUnmount(){

    }
    clickHide = () => {
        this.setState({
            showHide:!this.state.showHide
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    resetState = () => {
        this.setState({
            accname:'',
            fundstype:'0',
            feetype:'',
            amountS:'',
            amountE:''
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
    handleCancel = () => {
        this.setState({
            visible:false
        })
    }
    selectFundsType = v => {
        this.setState({
            fundstype:v
        })
    }
    selectReFeeType = v => {
        this.setState({
            refeetype:v
        })
    }
    // 弹框 费用方向
    selectFeeDirect = v => {
        this.setState({
            mcostdirection:v
        })
    }
    // //时间控件
    // onChangeCheckTime = (date, dateString) => {
    //     this.setState({
    //         createtimeS:dateString[0]?moment(dateString[0]).format('x'):'',
    //         createtimeE:dateString[1]?moment(dateString[1]).format('x'):'',
    //         time:date
    //     })
    // }
    //判断输入框
    judge = () => {
        const {mcostdirection,mdownloadaddress,mdownloadamount,tmp,limitAvailableAmount} = this.state
        // console.log(limitAvailableAmount)
        if(!mcostdirection){
            message.warning('请选择费用方向！')
            return false
        }else if(!mdownloadamount||mdownloadamount==0){
            message.warning('提现金额不能为空！')
            return false
        }else if(mdownloadamount-limitAvailableAmount>0){
            message.warning('提现金额不可以大于可用金额！')
            return false
        }else if(!/^[0-9]+([.]{1}[0-9]+){0,1}$/ig.test(mdownloadamount)){
            message.warning('提现金额只能是数字！')
            return false
        }else if(!mdownloadaddress){
            message.warning('提现地址不能为空！')
            return false
        }else if(!tmp){
            message.warning('备注不能为空！')
            return false
        }else{
            return true
        }
    }
    //请求数据
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex,pageSize,pagination,fundstype,amountS,amountE} = this.state
        axios.post(DOMAIN_VIP+'/feeAccountDetails/operation',qs.stringify({
            fundstype,amountS,amountE,
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
                    tableSource,
                    pagination,
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
    onSave = (type) => {
        const { reaccname,id,mcostdirection,mdownloadaddress,mdownloadamount,tmp,refeetype,refundstype,userId,userName,refundstypeName } = this.state
        if(!this.judge()) return ; //judge返回false，则return false
        let url = type === 1 ? '/feeAccountCheck/applySubmit' : '/feeAccountCheck/applyInsert'
        axios.post(DOMAIN_VIP+url,qs.stringify({
            accname:reaccname,costdirection:mcostdirection,downloadaddress:mdownloadaddress,downloadamount:mdownloadamount,tmp,feetype:refeetype,fundstype:refundstype,
            sponsorid:userId,sponsorname:userName,operationType:type,fundstypename:refundstypeName
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false
                })
                this.requestTable()
            }else{
                message.warning(result.msg);
            }
        })
    }
    onWithdraw = (item) => {
        Big.RM = 0;
        // console.log(item)
        const { limitBtn } = this.state
        this.footer= [
            limitBtn.includes('applySubmit')&&<Button key="submit" type="more" onClick={()=>this.onSave(1)}>提交</Button>,
            limitBtn.includes('applyInsert')&&<Button key="save" type="more" onClick={()=>this.onSave(0)}>保存</Button>,
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]
        item.availableAmount = new Big(item.currentamount).minus(item.freezeamount)
        // axios.post(DOMAIN_VIP+'/feeAccountCheck/balance',qs.stringify({
            
        //     type:item.fundstype
        // })).then(res => {
        //     const result = res.data;
        //     if(result.code == 0){
        //         item.availableAmount = result.data.availableamount
                this.setState({
                    visible:true,
                    title:"提现",
                    width:'700px',
                    refundstype:item.fundstype,
                    modalHtml:<ModalPlatformWithdraw selectReFeeType={this.selectReFeeType} selectFeeDirect={this.selectFeeDirect} userName={this.state.userName} item={item} handleInputChange={this.handleInputChange} />,
                    mcostdirection:item.costdirection||1,
                    mdownloadamount:item.downloadamount||'',
                    mdownloadaddress:item.downloadaddress||'',
                    limitAvailableAmount:item.availableAmount||0,
                    refeetype:item.feetype||'',
                    tmp:item.tmp||'',
                    reaccname:item.accname||'',
                    refundstypeName:item.fundstypename||''
                })
        //     }else{
        //         message.warning(result.msg);
        //     }
        // })
        
    }
    render(){
        const { showHide,tableSource,pagination,visible,modalHtml,width,title,fundstype,amountS,amountE,limitBtn  } = this.state
        return(
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 财务中心 > 运营账户管理 > 运营账户提现
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <FundsTypeList title='资金类型' fundsType={fundstype} handleChange={this.selectFundsType} />
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">余额:</label>
                                        <div className="col-sm-8">
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='amountS' value={amountS} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="col-sm-4 left sm-box">
                                                <input type="text" className="form-control" name='amountE' value={amountE} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table dataSource={tableSource} bordered pagination={pagination} locale={{emptyText:'暂无数据'}} >
                                        <Column title='序号' dataIndex='index' key='index' />
                                        <Column title='资金类型' dataIndex='fundstypename' key='fundstypename' />
                                        <Column title='费用类型' dataIndex='feetype' key='feetype' render={(text)=>{
                                            switch(text){
                                                case 1:
                                                    return '平台手续费';
                                                case 2:
                                                    return '平台运营';
                                                case 3:
                                                    return '保险基金';
                                                default:
                                                    // return '--'
                                                    break;
                                            }
                                        }} />
                                        <Column title='账户名称' dataIndex='accname' key='accname' />
                                        <Column className='moneyGreen' title='余额' dataIndex='currentamount' key='currentamount' render={(text)=>toThousands(text,true)} />
                                        <Column className='moneyGreen' title='冻结金额' dataIndex='freezeamount' key='freezeamount' render={(text)=>toThousands(text,true)} />
                                        <Column title='操作' dataIndex='action' key='action' render={(text,record)=>
                                            limitBtn.includes('operation')&&<a href='javascript:void(0);' onClick={()=>this.onWithdraw(record)}>提现</a>
                                        } />
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    width={width}
                    title={title}
                    onOk={this.handleOk}
                    footer={this.footer}
                    onCancel={this.handleCancel}
                >
                    {modalHtml}
                </Modal>
            </div>
        )
    }
}