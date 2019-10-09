import React from 'react';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import cookie from 'js-cookie'
import { Button,Select,Pagination,message,DatePicker,Table,Modal } from "antd"
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20,PAGRSIZE_OPTIONS,PAGESIZE_50} from '../../../conf'
import ModalPlatformWithdraw from './modal/modalPlatformWithdraw'
import FundsTypeList from '../../common/select/fundsTypeList'
import FeeTypeList from '../../common/select/feeTypeList'
import FeeDirectionList from '../../common/select/feeDirectionList'
import AccountNameList from '../../common/select/accountNameList'
import { toThousands,pageLimit } from '../../../utils'
const { RangePicker} = DatePicker
const {Column} = Table

export default class PlatformFeeWithdrawal extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:100,
            pageTotal:DEFAULTVALUE,
            visible:false,
            modalHtml:'',
            title:'',
            width:'',
            tableSource:[],
            accname:'',
            fundstype:'0',
            feetype:'1',

            userId:cookie.get('userId'),
            userName:cookie.get('userName'),
            mcostdirection:1,
            mdownloadamount:'',
            mdownloadaddress:'',
            refeetype:'',
            tmp:'',
            refundstype:'',
            refundstypeName:'',
            limitAvailableAmount:0,//提现金额的最大的值,
            limitBtns:[],
        }
    }
    componentDidMount(){        
        this.requestTable()
        this.setState({
            limitBtns:pageLimit('feeAccountCheck',this.props.permissList)
        },()=>console.log(this.state.limitBtns))
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
        },()=>{
            this.requestTable()
            this.forceUpdate()
        })       
    }
    resetState = () => {
        this.setState({
            accname:'',
            fundstype:'0',
            feetype:'1'
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
    //资金类型
    selectFundsType = v => {
        this.setState({
            fundstype:v
        })
    }
    //费用类型
    selectFeeType = v => {
        this.setState({
            feetype:v
        })
    }
    //账户名称
    selectAccname = v => {
        this.setState({
            accname:v
        })
    }
    selectReFeeType = v => {
        this.setState({
            refeetype:v
        })
    }
    selectFeeDirect = v => {
        this.setState({
            mcostdirection:v
        })
    }
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
        const { pageIndex,pageSize,pagination,accname,fundstype,feetype,} = this.state
        axios.post(DOMAIN_VIP+'/feeAccountDetails/list',qs.stringify({
            accname,fundstype,feetype,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableSource:result.data.list,
                    pageTotal:result.data.totalCount,
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
        // var a  ={} var b={} var c ={}  function A(){this}.bind(b)   A.call(a) 
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    onSave = (type) => {
        const { maccname,id,mcostdirection,mdownloadaddress,mdownloadamount,tmp,refeetype,refundstype,userId,userName,refundstypeName } = this.state
        if(!this.judge()) return ; //judge返回false，则return false

        let url = type === 1 ? '/feeAccountCheck/applySubmit' : '/feeAccountCheck/applyInsert'
        axios.post(DOMAIN_VIP+url,qs.stringify({
            accname:maccname,costdirection:mcostdirection,downloadaddress:mdownloadaddress,downloadamount:mdownloadamount,tmp,feetype:refeetype,fundstype:refundstype,
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
        const { limitBtns} = this.state
        this.footer= [
            limitBtns.includes('applySubmit')&&<Button key="submit" type="more" onClick={()=>this.onSave(1)}>提交</Button>,
            limitBtns.includes('applyInsert')&&<Button key="save" type="more" onClick={()=>this.onSave(0)}>保存</Button>,
            <Button key="back" onClick={this.handleCancel}>取消</Button>
        ]
        this.setState({
            visible:true,
            title:"提现",
            width:'700px',
            refundstype:item.fundstype||'',
            modalHtml:<ModalPlatformWithdraw selectReFeeType={this.selectReFeeType} selectFeeDirect={this.selectFeeDirect} userName={this.state.userName} item={item} handleInputChange={this.handleInputChange} />,
            mcostdirection:1,
            mdownloadamount:'',
            mdownloadaddress:'',
            limitAvailableAmount:item.availableAmount||0,
            refeetype:item.feetype||'',
            tmp:'',
            maccname:item.accname||'',
            refundstypeName:item.fundstypename||''
        })
    }
    render(){
        const { showHide,tableSource,pageTotal,pageIndex,pageSize,visible,modalHtml,width,title,accname,fundstype,feetype,limitBtns  } = this.state
        return(
            <div className="right-con">
                <div className='page-title'>
                    当前位置： 财务中心 > 运营账户管理 > 平台手续费提现明细 
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
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <FeeTypeList title='费用类型' showOption='平台手续费' feeType={feetype} handleChange={this.selectFeeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <AccountNameList title='账户名称' accName={accname} handleChange={this.selectAccname} />
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
                                    <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">资金类型</th>
                                                <th className="column-title">费用类型</th>
                                                <th className="column-title">账户名称</th>
                                                <th className="column-title">昨日发生额(累计值)</th>
                                                <th className="column-title">当前累计金额</th>
                                                <th className="column-title">时间</th>                                             
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableSource.length>0?tableSource.map((item,index)=> {
                                                    return (
                                                        [<tr key={item.accid}>
                                                            <td colSpan='10'>
                                                                <span className="pad-sapce">{item.fundstypename}</span>
                                                                <span className="pad-sapce">手续费累计总数({item.fundstypename})：<span className='moneyGreen'>{toThousands(item.currentTotalAmount,true)}</span></span>
                                                                <span className="pad-sapce">已提现金额({item.fundstypename})：<span className='moneyGreen'>{toThousands(item.downloadAmount,true)}</span></span>
                                                                <span className="pad-sapce">当前可提现金额({item.fundstypename})：<span className='moneyGreen'>{toThousands(item.availableAmount,true)}</span></span>
                                                                <span className="pad-sapce">冻结金额({item.fundstypename})：<span className='moneyGreen'>{toThousands(item.freezeamount,true)}</span></span>
                                                                <span className="pad-sapce" style={{float:'right'}}>{limitBtns.includes('list')&&<a href='javascript:void(0);' onClick={()=>this.onWithdraw(item)} >提现</a>}</span>                                                                
                                                            </td>
                                                        </tr>,
                                                        <tr key={item.id}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.fundstypename}</td>
                                                            <td>{
                                                                (()=>{
                                                                    switch(item.feetype){
                                                                    case 1:
                                                                        return '平台手续费'
                                                                        break;
                                                                    case 2:
                                                                        return '平台运营'
                                                                        break;
                                                                    case 3:
                                                                        return '保险基金'
                                                                        break;
                                                                    default:
                                                                        return '--'
                                                                        break;
                                                                    }
                                                                })()
                                                            }</td>
                                                            <td>{item.accname}</td>
                                                            <td className='moneyGreen'>{toThousands(item.lastdayamount,true)}</td>
                                                            <td className='moneyGreen'>{toThousands(item.currentamount,true)}</td>
                                                            <td>{item.updatetime?moment(item.updatetime).format(TIMEFORMAT_ss):'--'}</td>
                                                        </tr>]
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                <div className="pagation-box">
                                {
                                    pageTotal>0 && <Pagination
                                                size="small"
                                                current={pageIndex}
                                                total={pageTotal}
                                                showTotal={total => `总共 ${total} 条`}
                                                onChange={this.onChangePageNum}
                                                onShowSizeChange={this.onShowSizeChange}
                                                pageSizeOptions={PAGRSIZE_OPTIONS}
                                                showSizeChanger
                                                showQuickJumper
                                                defaultPageSize={100}/>
                                }
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