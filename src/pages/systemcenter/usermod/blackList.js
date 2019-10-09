import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { PAGESIZE,PAGEINDEX,DOMAIN_VIP,TIMEFORMAT,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button,Tabs,Select,Pagination,Modal,message,Input } from 'antd'
import GoogleCode from '../../common/modal/googleCode'
import moment from 'moment'
import ModalBlack from './moadl/modalBlack'
import { pageLimit,tableScroll } from '../../../utils'
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { TextArea } = Input;


export default class BlackList extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            visible:false,
            userId:'',
            userName:'',
            state:'',
            showHide:true,
            tableList:[],
            pageTotal:0,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            width:'',
            title:'',
            modalHtml:'',
            changeState:'',
            memo:'',
            movisible:false,
            mowidth:'',
            motitle:'',
            customerType:'0',
            limitBtn: [],
            googVisibal:false,
            googleSpace:'',
            type:'',
            height:0,
            tableScroll:{
                tableId:'BCKLT',
                x_panelId:'BCKLTX',
                defaultHeight:500,
            }
        }

        this.clickHide = this.clickHide.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.modalModify = this.modalModify.bind(this)
        this.viewMemo = this.viewMemo.bind(this)
        this.handleType = this.handleType.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changeType = this.changeType.bind(this)
        this.saveModify = this.saveModify.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleNoteCancel = this.handleNoteCancel.bind(this)
        this.onResetState= this.onResetState.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('blacklist', this.props.permissList)
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
    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        
        const value = target.type === 'checkbox' ? target.checked : target.value;
        console.log(value)
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    handleType(value){
        this.setState({
            state:value
        })
    }
    changeType(value){
        this.setState({
            changeState:value
        })
    }
    //关闭修改备注弹窗
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //重置
    onResetState(){
        this.setState({
        userId:'',
        userName:'',
        state:''
    })
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
    handleNoteCancel(){
        this.setState({
            movisible:false,
        })
    }
    //修改弹窗
    modalModify(item){
        const {changeState,memo} =this.state
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(item)}>
                保存修改
            </Button>,
        ]

        this.setState({
            memo:item.memo?item.memo:'',
            changeState:item.state?item.state:0,
            visible:true,
            title:'修改黑名单用户状态',
            width:'800px'
        })
    }

    //保存修改
    saveModify(item){
        const{memo,changeState}= this.state
        axios.post(DOMAIN_VIP+'/blacklist/update',qs.stringify({
            id:item.id,
            memo:memo,
            state:changeState,
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false
                })
                this.requestTable()
            }
        })
    }
   
    //google 验证弹窗
    modalGoogleCode(item,type){
        this.setState({
            googVisibal:true,
            googleSpace:item,
            type,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { googleSpace,type } = this.state
        const { googleCode } = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                },() => this.saveModify(googleSpace))
            }else{
                message.warning(result.msg)
            }
        })
    }

    //查看备注
    viewMemo(item){
        this.footern = [
            <Button key="back" onClick={this.handleNoteCancel}>取消</Button>,
        ]
        this.setState({
            movisible:true,
            modalHtml:<ModalBlack id = {item.id}/>,
            motitle:'修改黑名单用户状态',
            mowidth:'1000px'
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
    //请求数据
    requestTable(currIndex,currSize){
        const { userId,userName,state,pageSize,pageIndex} = this.state;
        console.log(currIndex)
        console.log(currSize)
        axios.post(DOMAIN_VIP+'/blacklist/query',qs.stringify({
            userId,userName,state,
            pageIndex:pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    pageTotal:result.data.totalCount,
                    tableList:result.data.list
                })
            }
        })
    }
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.modalGoogleCodeBtn(values)
        });
      }
      saveFormRef(formRef){
        this.formRef = formRef;
      }
        //谷歌弹窗关闭
    onhandleCancel(){
        this.setState({
            googVisibal: false 
        })
    }

    render(){
        const { userId,userName,customerType,state,tableList,pageTotal,showHide,pageIndex,pageSize,visible,width,title,modalHtml ,changeState,memo,movisible,mowidth,motitle,limitBtn } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>用户管理>黑名单管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&
                            <div id={this.state.tableScroll.x_panelId} className="x_panel">
                                <div className="x_content">
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">用户编号：</label>
                                            <div className="col-sm-8">
                                                <input type="text" className="form-control"  name="userId" value={userId} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">用户名：</label>
                                            <div className="col-sm-8">
                                                <input type="text" className="form-control"  name="userName" value={userName} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">状态：</label>
                                            <div className="col-sm-8">
                                                <Select value={state} style={{ width: SELECTWIDTH }} onChange={this.handleType}>
                                                    <Option value="">请选择</Option>
                                                    <Option value="0">正常</Option>
                                                    <Option value="1">已移除</Option>                                             
                                                </Select>
                                            </div>
                                        </div>
                                    </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={()=>this.requestTable()}>查询</Button>
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
                                                <th className="column-title">用户编号</th>
                                                <th className="column-title">添加人</th>
                                                <th className="column-title">添加时间</th>
                                                <th className="column-title">备注</th>
                                                <th className="column-title">状态</th>
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
                                                            <td>{item.userid}</td>
                                                            <td>{item.createuser}</td>
                                                            <td>{moment(item.createtime).format(TIMEFORMAT)}</td>
                                                            <td>{item.memo}</td>
                                                            <td>{item.stateName}</td>
                                                            <td>
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)"  onClick={()=>this.modalModify(item)}>修改</a>:''}
                                                                {limitBtn.indexOf('queryMemo')>-1? <a href="javascript:void(0)"  style={{marginLeft:'20px'}} onClick={()=>this.viewMemo(item)}>查看备注</a>:''}                                                                
                                                            </td>
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="7">暂无数据</td></tr>
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
                                                showQuickJumper
                                                pageSizeOptions={PAGRSIZE_OPTIONS20}
                                                defaultPageSize={PAGESIZE}
                                                />
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
                    style={{ top: 50 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                   <div className="col-md-12 col-sm-12 col-xs-12"> 
                            <div className="form-group">
                                <label className="col-sm-3 control-label">状态：</label>
                                <div className="col-sm-8">
                                <Select value={changeState} style={{ width: SELECTWIDTH }} onChange={this.changeType}>
                                    <Option value={0}>正常</Option>
                                    <Option value={1}>已移除</Option>                                             
                                </Select>
                                </div>
                            </div>
                            <div className="form-group">
                                <label className="col-sm-3 control-label">备注：</label>
                                <div className="col-sm-8">
                                    <TextArea name="memo" className="widthText" rows={4} value={memo} onChange={this.handleInputChange} />
                                </div>
                            </div>
                        </div>           
                </Modal>
                <Modal
                    visible={movisible}
                    title={motitle}
                    width={mowidth}
                    style={{ top: 50 }}
                    onCancel={this.handleNoteCancel}
                    footer={this.footern}
                >
                   {modalHtml}           
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='BL'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }
}












