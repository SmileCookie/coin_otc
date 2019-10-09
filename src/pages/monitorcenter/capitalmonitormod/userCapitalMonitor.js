import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import cookie from 'js-cookie'
import moment from 'moment'
import MoadlCapital from './modal/moadlCapital'
import ModalMemo from './modal/modalMemo'
import {pageLimit,tableScroll} from '../../../utils/index'
import SelectAType from '../../financialcenter/select/selectAType'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,TIMEFORMAT, SELECTWIDTH ,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Select,Modal,Pagination,Button,message,DatePicker,Input } from 'antd'
const { RangePicker } = DatePicker;
const Option = Select.Option;
const { TextArea } = Input;


export default class UserCapitalMonitor extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            tableList:[],
            pageTotal:0,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            PAGRSIZE_OPTIONS20:PAGRSIZE_OPTIONS20,
            fundsType:'0',
            time:[],
            startTimeMon:'',
            endTimeMon:'',
            visible:false,
            width:'',
            title:'',
            modalHtml:'',
            checkResult:'',
            memo:'',
            limitBtn:[],
            loading:true ,
            height:0,
            tableScroll:{
                tableId:'UCPTMTOR',
                x_panelId:'UCPTMTORX',
                defaultHeight:500,
                height:0,
            }
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.handleFundChange =  this.handleFundChange.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.addModalMemo = this.addModalMemo.bind(this)
        this.addModalMemoBtn = this.addModalMemoBtn.bind(this)
        this.handleChangeResult = this.handleChangeResult.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.viewModalMemo = this.viewModalMemo.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState ({
            limitBtn : pageLimit('capitalMonitor',this.props.permissList)
        })
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillReceiveProps(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
        tableScroll(`#${this.state.tableScroll.tableId}`,'add',`#${this.state.tableScroll.x_panelId}`,this.getHeight)
    }
    componentWillUnmount(){
        tableScroll(`#${this.state.tableScroll.tableId}`)
    }
    getHeight(xheight){
        this.setState({
            xheight
        })
    }
    //列表
    requestTable(currentIndex, currentSize){
        const { fundsType,startTimeMon,checkResult,endTimeMon,pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+"/capitalMonitor/getCapitalMonitorInfo",qs.stringify({
            fundsType,startTimeMon,endTimeMon,checkResult,
            pageIndex:currentIndex||pageIndex,
            pageSize: currentSize || pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.date.list?result.date.list:[],
                    pageTotal:result.date.totalCount
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        })
        this.requestTable(page,pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

     //点击收起
     clickHide() {
        let { showHide,xheight,pageSize } = this.state;
        if(showHide&&pageSize>10){
            this.setState({
                showHide: !showHide,
                height:xheight,
            })
        }else{
            this.setState({
                showHide: !showHide,
                height:0
            })
        }
        // this.setState({
        //     showHide: !showHide,
        // })
    }

    //资金类型
    handleFundChange(val){
        this.setState({
            fundsType:val
        })
    }
    //检查结果
    handleChange(val){
        this.setState({
            type:val
        })
    }

    onChangeTime(date, dateString){
        this.setState({
            startTimeMon:dateString[0],
            endTimeMon:dateString[1],
            time:date
        })
    }
    //关闭修改备注弹窗
    handleCancel(){
        this.setState({
            visible:false,
            modalHtml:'',
            loading:false
        })
    }
    //添加备注
    addModalMemo(ucmId){
        this.footer= [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.addModalMemoBtn(ucmId)}>
                确定
            </Button>,
        ]
        
        this.setState({
            visible:true,
            title:"新增备注",
            width:"700px",
            memo:'',
            modalHtml:<div className="form-group">
                          <label className="col-sm-3 control-label">备注：</label>
                          <div className="col-sm-8">
                              <TextArea rows={4} name="memo"  onChange={this.handleInputChange}/>
                          </div>
                      </div>
        })
    }
    //异常用户
    addMoadlCapital(ucmId){
        this.footer= [
            <Button key="back" onClick={this.handleCancel}>返回</Button>,
        ]
        this.setState({
            visible:true,
            title:"监控错误详情",
            width:"1000px",
            modalHtml:<MoadlCapital ucmId={ucmId}/>
        })
    }
    //添加备注按钮
    addModalMemoBtn(ucmId){
        // let dealUserId = cookie.get('userId');
        const { memo } = this.state
        if(memo == ''){
            message.warning('备注不能为空！')
        }else{
            this.setState({
                loading:true
            })
            axios.post(DOMAIN_VIP+"/capitalMonitor/addRemark",qs.stringify({
                id:ucmId,
                memo
            })).then(res => {
                const result = res.data
                if(result.code == 0){
                    message.success(result.msg)
                    this.setState({
                        visible:false,
                        loading:false
                    })
                    this.requestTable()
                }else{
                    message.warning(result.msg)
                    this.setState({
                        loading:false
                    })
                }
            })
        }
    }
    //查看备注
    viewModalMemo(ucmId){
        this.footer= [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
        ]
        this.setState({
            visible:true,
            title:"查看备注",
            width:"800px",
            modalHtml:<ModalMemo ucmId={ucmId} />
        })
    }

    //检查结果 result
    handleChangeResult(val){
        this.setState({
            checkResult:val
        })
    }

    //重置状态
    onResetState(){
        this.setState({
            fundsType:'0',
            time:[],
            startTimeMon:'',
            endTimeMon:'',
            checkResult:''
        })
    }

    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },()=>this.requestTable())
    }

    render(){
        const { checkResult,showHide,tableList,pageIndex,limitBtn,pageSize,pageTotal,fundsType,time,visible,width,title,modalHtml } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：监控中心>资金监控>用户资金监控
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&
                            <div id={this.state.tableScroll.x_panelId} className="x_panel">
                                <div className="x_content">
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <SelectAType findsType={fundsType} col='3' handleChange={this.handleFundChange}></SelectAType>
                                    </div>

                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">检查结果：</label>
                                            <div className="col-sm-8">
                                                <Select value={checkResult} style={{width:SELECTWIDTH}}  onChange={this.handleChangeResult}>
                                                    <Option value="">请选择</Option>
                                                    <Option value="1">正常</Option>
                                                    <Option value="2">异常</Option>                                            
                                                </Select>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">监控时间：</label>
                                            <div className="col-sm-8">
                                                <RangePicker onChange={this.onChangeTime} value={time} />
                                            </div>
                                        </div>
                                    </div>

                                    <div className="col-md-4 col-sm-4 col-xs-4 right">
                                        <div className="right">
                                            <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                            <Button type="primary" onClick={this.onResetState}>重置</Button>                                            
                                        </div>
                                    </div>

                                </div>
                            </div>
                        }

                        <div className="x_panel">
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">资金类型</th>
                                                <th className="column-title">监控时间</th>
                                                <th className="column-title">检查用户数</th>
                                                <th className="column-title">正常用户数</th>
                                                <th className="column-title">异常用户数</th>                   
                                                <th className="column-title">检查结果</th>  
                                                <th className="column-title">备注</th>
                                                <th className="column-title">操作</th>                                                 
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.fundsTypeName}</td>
                                                            <td>{moment(item.monTime).format(TIMEFORMAT)}</td>
                                                            <td>{item.checkUserNum}</td>
                                                            <td>{item.correctUserNum}</td>
                                                            <td>{
                                                            item.errorUuserNum==0?item.errorUuserNum:
                                                                <a href="javascript:void(0)" style={{color:'#FF0000'}} onClick={() => this.addMoadlCapital(item.ucmId)}>{item.errorUuserNum}</a>
                                                                }</td>
                                                            <td>{item.checkResult==1?"正常":"异常"}</td>
                                                            <td>{item.memo}</td>                                                            
                                                            <td>
                                                               {limitBtn.indexOf('addRemark')>-1?<a href="javascript:void(0)" className="mar20" onClick={() => this.addModalMemo(item.id)}>添加备注</a>:''}
                                                            {limitBtn.indexOf('checkRemark')>-1?<a href="javascript:void(0)" onClick={() => this.viewModalMemo(item.id)}>查看备注</a>:'' }                                                               
                                                            </td>
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="15">暂无数据</td></tr>
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
                                                    onChange={this.changPageNum}
                                                    onShowSizeChange={this.onShowSizeChange}
                                                    pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                    defaultPageSize={PAGESIZE}
                                                    showSizeChanger
                                                    showQuickJumper />
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 60 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}            
                </Modal>
            </div>
        )
    }
}































