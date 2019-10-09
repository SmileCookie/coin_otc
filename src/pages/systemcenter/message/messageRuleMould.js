import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalMatch from './modal/modalMatch'
import ModalModify from './modal/modalModify'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,TIMEFORMAT } from '../../../conf'
import { Button,Select,DatePicker,Pagination,Modal,message } from 'antd'
import { pageLimit } from '../../../utils/index'
const Option = Select.Option;
const { MonthPicker, RangePicker, WeekPicker } = DatePicker;

export default class MessageRuleMould extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            tableList:[],
            time:[],
            pageTotal:0,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            templatename:'',
            templatedesc:'',
            creuser:'',
            credateS:'',
            credateE:'',
            sendmodecode:'',
            sendareacode:'',
            modalHtml:'',
            visible:false,
            width:'',
            title:'',
            limitBtn: [],
            loading:false
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onAddMould = this.onAddMould.bind(this)
        this.deleteMould = this.deleteMould.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changeSendMode = this.changeSendMode.bind(this)
        this.changeSendArea = this.changeSendArea.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('msgTemplateRule', this.props.permissList)
        })
    }

    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
    }

    //请求列表
    requestTable(currIndex,currSize){
        const { pageIndex,pageSize,templatename,templatedesc,creuser,credateS,credateE,sendmodecode,sendareacode } = this.state
        axios.post(DOMAIN_VIP + "/msgTemplateRule/queryList",qs.stringify({
            templatename,templatedesc,creuser,credateS,credateE,sendmodecode,sendareacode,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount,                    
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    //点击分页
    changPageNum(page,pageSize){
        this.requestTable(page,pageSize)
        this.setState({
            pageIndex:page
        })
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
        if(name == 'Mrulesql'){
            let val = value.replace('>','&gt;')
                val = val.replace("<","&lt;")
            this.setState({
                [name]: val
                })
        }else{
            this.setState({
                [name]: value
            });
        }
    }

    //时间
    onChangeTime(date, dateString){
        console.log(date, dateString);
        this.setState({
            credateS:dateString[0],
            credateE:dateString[1],
            time:date
        })
    }

    
    //重置状态
    onResetState(){
        this.setState({
            templatename:'',
            templatedesc:'',
            creuser:'',
            credateS:'',
            credateE:'',
            sendmodecode:'',
            sendareacode:'',
            time:[],
        })
    }

    //弹窗关闭
    handleCancel(){
        this.setState({
            visible:false,
            loadng:false
        })
    }

    //匹配预览
    matchMouldView(item){
        const { sendmodecode,id } = item
        let title = sendmodecode==1?"手机匹配预览":"邮箱匹配预览"
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
           
        ]

        this.setState({
            visible:true,
            modalHtml:<ModalMatch  sendmodecode={sendmodecode} id={id} />,
            width:"900px",
            title:title,
        })
    }
    //新增规则模版
    onAddMould(item){
        let title = item ? "修改规则模版":"新增规则模版";
        let itemInfo = item ? item : {};        
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.onAddMouldBtn(item)}>
                确定
            </Button>,
        ]
        this.setState({
            visible:true,
            title:title,
            width:"900px",
            modalHtml:<ModalModify item={itemInfo} handleInputChange={this.handleInputChange} />,
            Mid:itemInfo.id||'',
            Mtemplatename:itemInfo.templatename||'',
            Mruledatabase:itemInfo.ruledatabase||'',
            Msendmodecode:itemInfo.sendmodecode||'',
            Msendareacode:itemInfo.sendareacode||'',
            Mtemplatestatus:itemInfo.templatestatus||'',
            Mrulesql:itemInfo.rulesql||'',
            Mtemplatedesc:itemInfo.templatedesc||''
        })
    }

    //新增规则模版按钮
    onAddMouldBtn(item){
        let url = item?"/msgTemplateRule/upd":"/msgTemplateRule/add";
        const { Mid,Mtemplatename,Mruledatabase,Msendmodecode,Msendareacode,Mtemplatestatus,Mrulesql,Mtemplatedesc } = this.state
        if(!Mtemplatename){
            message.warning("请输入模版名称!");
            return false;
        }
        if(!Mruledatabase){
            message.warning("请输入规则数据库!");
            return false;
        }
        if(!Mrulesql){
            message.warning("请输入规则SQL!");
            return false;
        }
        if(!Mtemplatedesc){
            message.warning("请输入模版描述!");
            return false;
        }
        this.setState({
            loading:true
        })
        axios.post(DOMAIN_VIP+url,qs.stringify({
            id:Mid,
            templatename:Mtemplatename,
            ruledatabase:Mruledatabase,
            sendmodecode:Msendmodecode,
            sendareacode:Msendareacode,
            templatestatus:Mtemplatestatus,
            rulesql:Mrulesql,
            templatedesc:Mtemplatedesc
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false,
                    loading:false
                })
                this.requestTable()
                message.success(result.msg)
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        })

    }
    //删除弹窗
    deleteMould(id){
        let self = this
        Modal.confirm({
            title: '您确定要删除此规则模版?',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/msgTemplateRule/del',qs.stringify({
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
                console.log('OK');
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    //发送渠道 select
    changeSendMode(val){
        this.setState({
            sendmodecode:val
        })
    }

    //发送区域
    changeSendArea(val){
        this.setState({
            sendareacode:val
        })
    }

    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }

    render(){
        const { showHide,templatename,templatedesc,sendareacode,sendmodecode,creuser,time,pageTotal,pageIndex,pageSize,tableList,modalHtml,visible,width,title,limitBtn } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 消息管理 > 消息规则模版
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">模版名称：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="templatename" value={templatename} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">模版描述：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="templatedesc" value={templatedesc} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发送渠道：</label>
                                        <div className="col-sm-8">
                                            <Select value={sendmodecode} style={{ width: SELECTWIDTH }} onChange={this.changeSendMode}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">短信</Option>
                                                <Option value="2">邮箱</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发送区域：</label>
                                        <div className="col-sm-8">
                                            <Select value={sendareacode} style={{ width: SELECTWIDTH }} onChange={this.changeSendArea}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">国内</Option>
                                                <Option value="2">国际</Option>
                                                <Option value="3">全球</Option>                                                
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">创建人：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="creuser" value={creuser} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">创建时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChangeTime} value={time} />
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>    
                                        {limitBtn.indexOf('add')>-1?<Button type="primary" onClick={() => this.onAddMould()}>新增</Button>:''}                                                                                                                   
                                    </div>
                                </div>
                            </div>
                        </div>}

                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title min_69px">序号</th>
                                                <th className="column-title">模版名称</th>
                                                <th className="column-title min_116px">模版描述</th>
                                                <th className="column-title">规则数据库</th>
                                                <th className="column-title min_153px">规则SQL</th>
                                                <th className="column-title">发送渠道</th> 
                                                <th className="column-title">发送区域</th>                       
                                                <th className="column-title">模版状态</th>                       
                                                <th className="column-title">创建人</th>                       
                                                <th className="column-title">创建时间</th>                       
                                                <th className="column-title min_116px">操作</th>         
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index) => {
                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.templatename}</td>
                                                            <td>{item.templatedesc}</td>
                                                            <td>{item.ruledatabase}</td>
                                                            <td>{item.rulesql}</td>
                                                            <td>{item.sendmodecode==1?"短信":"邮件"}</td>
                                                            <td>{item.sendareacode==1?"国内":item.sendareacode==2?"国际":"全球"}</td>
                                                            <td>{item.templatestatus==1?"正常":"停用"}</td>
                                                            <td>{item.creusername}</td>
                                                            <td>{moment(item.credate).format(TIMEFORMAT)}</td>
                                                            <td>
                                                                {limitBtn.indexOf('matchPreviewInfo')>-1?<a href="javascript:void(0)" className="mar20" onClick={() => this.matchMouldView(item)}>匹配预览</a>:''}
                                                                {limitBtn.indexOf('upd')>-1?<a href="javascript:void(0)" className="mar20" onClick={() => this.onAddMould(item)}>修改</a>:''}
                                                                {limitBtn.indexOf('del')>-1?<a href="javascript:void(0)" onClick={() => this.deleteMould(item.id)}>删除</a>:''}                                                                
                                                            </td>
                                                        </tr>
                                                    )
                                                })   
                                                :<tr className="no-record"><td colSpan="12">暂无数据</td></tr>
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
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                { modalHtml }
              </Modal>
            </div>
        )
    }

}







































