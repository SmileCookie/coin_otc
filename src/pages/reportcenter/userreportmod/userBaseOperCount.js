import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalUpdate from './modal/modalUpdate'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,DAYFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,DatePicker,Pagination,Modal,message } from 'antd'
import { pageLimit,tableScroll } from '../../../utils'
const {  RangePicker } = DatePicker;
import GoogleCode from '../../common/modal/googleCode'

export default class UserBaseOperCount extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            beginTime:"",
            endTime:"",
            tableList:"",
            moadlHtml:'',
            time:[],
            visible:false,
            loading: false,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            id:'',
            pvCount:'',
            uvCount:"",
            accessingIp:"",
            googleCode:'',
            limitBtn: [],
            check:'',
            googVisibal:false,
            item:'',
            type:'',
            height:0,
            tableScroll:{
                tableId:'URBEORCT',
                x_panelId:'URBEORCTX',
                defaultHeight:500,
                height:0,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.onChangeCheckTime = this.onChangeCheckTime.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.showDetail = this.showDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modifyUpdate = this.modifyUpdate.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
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
            limitBtn: pageLimit('userBaseOperCount', this.props.permissList)
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
    //时间控件
    onChangeCheckTime(date, dateString) {
        this.setState({
            beginTime:dateString[0]+" 00:00:00",
            endTime:dateString[1]+" 23:59:59",
            time:date
        })
       
    }
    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },this.requestTable(page,pageSize))
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
      handleInputChange(event) {
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
      //修改
      showDetail(item){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode(item)}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            modalHtml:<ModalUpdate
                        id={item.id}
                        pvCount={item.pvCount}
                        uvCount={item.uvCount}
                        accessingIp={item.accessingIp}
                        handleInputChange={this.handleInputChange}/>,
            title:'修改PV,UV',
            id:item.id,
            pvCount:item.pvCount,
            uvCount:item.uvCount,
            accessingIp:item.accessingIp,
        })
    }
       //关闭弹窗
       handleCancel(){
        this.setState({
            visible: false 
        })
    }
    //保存修改
    modifyUpdate(item){
        const { pvCount,uvCount,accessingIp } = this.state
        axios.post(DOMAIN_VIP + '/userBaseOperCount/update',qs.stringify({
            id:item.id,pvCount:pvCount,uvCount:uvCount,accessingIp,registerCount:item.registerCount,
        })).then(res => {
             const result = res.data;
             if(result.code == 0){
                this.setState({
                    visible:false
                })
                message.success(result.msg)
                this.requestTable();
             }else{
                message.warning(result.msg)
             }
        })
    }
 
    requestTable(currIndex,currSize){
        const {beginTime,endTime,pageIndex,pageSize,pageTotal} = this.state
        axios.post(DOMAIN_VIP+'/userBaseOperCount/query',qs.stringify({
            beginTime,endTime,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }else{
                message.warning(result.msg)
            }   
        })
    }
  

    //google 验证弹窗
    modalGoogleCode(item){
        this.setState({
            googVisibal:true,
            item,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { item } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                })
                this.modifyUpdate(item)
            }else{
                message.warning(result.msg)
            }
        })
    }
    onResetState(){
        this.setState({
            beginTime:"",
            endTime:"",
            time:[],
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
        const {showHide,visible,moadlHtml,beginTime,endTime,tableList,time,pageIndex,pageSize,pageTotal,limitBtn }=this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：报表中心 > 用户报表 > 用户登录注册统计
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">统计时间：</label>
                                        <div className="col-sm-8">
                                        <RangePicker 
                                        format="YYYY-MM-DD"
                                        placeholder={['Start Time', 'End Time']}
                                       onChange={this.onChangeCheckTime }
                                       value={time}
                                       />
                                        </div>
                                    </div>
                                </div>
                               
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.requestTable()}>查询</Button>
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
                                                <th className="column-title">报表时间</th>
                                                <th className="column-title min_68px">PV</th>
                                                <th className="column-title min_68px">UV</th>
                                                <th className="column-title ">访问IP</th>   
                                                <th className="column-title">当日登陆IP</th>
                                                <th className="column-title">总注册量</th>
                                                <th className="column-title">当日注册量</th> 
                                                <th className="column-title">每日登陆人数</th>
                                                <th className="column-title">用户登陆率</th> 
                                                <th className="column-title">注册转化率</th> 
                                                <th className="column-title">操作</th>                
                                                </tr>
                                            </thead>
                                            <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        
                                                         <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{moment(item.countDate).format(DAYFORMAT)}</td>
                                                            <td>{item.pvCount}</td>
                                                            <td>{item.uvCount}</td>
                                                            <td>{item.accessingIp}</td>
                                                            <td>{item.ipCount}</td>
                                                            <td>{item.allRegisterCount}</td>
                                                            <td>{item.registerCount}</td>
                                                            <td>{item.loginCount}</td>
                                                            <td>{item.loginRate}</td>
                                                            <td>{item.registrationConversionRate}</td>
                                                            <td>
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" onClick={() => this.showDetail(item)}>修改PV,UV</a>:''}
                                                            </td>
                                                        </tr>
                                                    )
                                                }):
                                                <tr className="no-record"><td colSpan="12">暂无数据</td></tr>
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
                                                onChange={this.changPageNum}
                                                showTotal={total => `总共 ${total} 条`}
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
                    title={this.state.title}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    width="600px"
                >
                    {this.state.modalHtml}
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='UBOC'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
        
    }
}