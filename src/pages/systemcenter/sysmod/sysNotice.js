import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { toThousands,pageLimit,tableScroll } from '../../../utils'
import ModalNotice from './modal/modalNotice'
import { PAGEINDEX,PAGESIZE,DOMAIN_VIP,SELECTWIDTH,TIMEFORMAT,PAGRSIZE_OPTIONS20} from '../../../conf'
import { Button,Tabs,Pagination ,Modal,message,DatePicker} from 'antd'
const { RangePicker } = DatePicker;
const TabPane = Tabs.TabPane;

export default class SysNotice extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide: true,
            msgTitle:'',
            userName:'',
            beginTime:'',
            endTime:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:0,
            tableList:[],
            visible:false,
            modalHtml:'',
            title:'',
            width:'',
            time:[],
            endOpen:true,
            id:'',
            msgPublishAuto:'',
            msgContent:'',
            momsgTitle:'',
            msgType:'',
            msgPublishTimeStr:'',
            channel:'',
            loading:false,
            limitBtn:[],
            height:0,
            tableScroll:{
                tableId:'sNTE',
                x_panelId:'sNTEX',
                defaultHeight:500,
            }
        }
        this.clickHide = this.clickHide.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.showDetail = this.showDetail.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.modalSave = this.modalSave.bind(this)
        this.onChange = this.onChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.deleteRole = this.deleteRole.bind(this)
        this.stickTop = this.stickTop.bind(this)
        this.getHeight = this.getHeight.bind(this)

    }
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn:pageLimit('msg',this.props.permissList)
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
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    deleteRole(id){
        let self = this;
        Modal.confirm({
            title: '您确定要删除此条数据?',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/msg/delete',qs.stringify({
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
    stickTop(id){
        let self = this;
        Modal.confirm({
            title: '您确定要置顶此条数据?',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/msg/up',qs.stringify({
                        id,seq:1
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
    showDetail(item){
        const {loading} = this.state
       if(item.id) {
           this.setState({
              id:item.id
           })
       }else{
        this.setState({
            id:''
         })
       }
        
       
        let title = item.id ? '修改系统公告':'新增系统公告'
        this.setState({
            visible:true,
            title:title,
            width:'800px',
            modalHtml:<ModalNotice item={item} handleInputChange={this.handleInputChange} onChange={()=>this.onChange}/>,
            msgPublishAuto:item.msgPublishAuto||false,
            msgContent:item.msgContent||'',
            momsgTitle:item.msgTitle||'',
            msgType:item.msgType||0,
            msgPublishTimeStr:item.msgPublishTimeStr||moment().format("YYYY-MM-DD HH:mm:ss"),
            channel:item.channel||0
        })
    }
    modalSave(){
        this.setState({
            loading:true
        })
        const {id,msgPublishAuto,msgContent,momsgTitle,msgType,msgPublishTimeStr,channel}= this.state
        id?axios.post(DOMAIN_VIP+'/msg/update',qs.stringify({
            id,msgPublishAuto,msgContent,msgTitle:momsgTitle,msgType,msgPublishTimeStr,channel
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
               this.setState({
                   visible:false,
                   loading:false
               },()=>this.requestTable())
               
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        }):axios.post(DOMAIN_VIP+'/msg/insert',qs.stringify({
            msgPublishAuto,msgContent,msgTitle:momsgTitle,msgType,msgPublishTimeStr,channel
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
               this.setState({
                   visible:false,
                   loading:false
               },()=>this.requestTable())
            }else{
                this.setState({
                    loading:false
                })
                message.warning(result.msg)
            }
        })
        
    }
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())    
    }
    handleCancel(){
        this.setState({
            visible:false,
            loading:false,
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
    requestTable(currIndex,currSize){
        const { msgTitle,userName,beginTime,endTime,pageIndex,pageSize} = this.state
        axios.post(DOMAIN_VIP+'/msg/query',qs.stringify({
            msgTitle,userName,beginTime,endTime,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    tableList:result.data.list,
                    pageTotal:result.data.totalCount
                })
            }   
        })
    }
    onResetState(){
        this.setState({
            msgTitle:'',
            userName:'',
            beginTime:'',
            endTime:'',
            time:[]
        })
    }
    onChange(dateString){
        this.setState({
            msgPublishTimeStr:dateString
        })
    }
    onChangeTime(date, dateString){
        this.setState({
            beginTime:dateString[0],
            endTime:dateString[1],
            time:date
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

    render(){
        const {showHide,msgTitle,userName,pageIndex,loading,pageSize,time,pageTotal,tableList,visible,title,modalHtml,width,limitBtn}=this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 系统管理 > OTC公告管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide &&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">标题：</label>
                                        <div className="col-sm-8 ">
                                         <input type="text" className="form-control" name="msgTitle" value={msgTitle} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                              
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">发布人：</label>
                                        <div className="col-sm-8 ">
                                         <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发布时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                            style={{width:SELECTWIDTH}}
                                            showTime={{
                                                defaultValue:[moment('00:00:00', 'HH:mm:ss'), moment('23:59:59', 'HH:mm:ss')]
                                            }}
                                            format="YYYY-MM-DD HH:mm:ss"
                                            placeholder={['Start Time', 'End Time']}
                                        onChange={this.onChangeTime }
                                        value={time}
                                        />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.inquireBtn()}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={this.showDetail}>新增</Button>:''}                             
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
                                                    <th className="column-title must_153px">公告编号</th>
                                                    <th className="column-title">标题</th>
                                                    <th className="column-title">发布时间</th>  
                                                    <th className="column-title">发布人</th>
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
                                                            <td>{item.id}</td>
                                                            <td>{item.msgTitle}</td>
                                                            <td>{moment(item.createTime).format(TIMEFORMAT)}</td>
                                                            <td>{item.userName}</td>
                                                            <td>
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" className='mar20' onClick={()=>this.showDetail(item)}>修改</a>:''}
                                                                {limitBtn.indexOf('delete')>-1?<a href="javascript:void(0)" className='mar20' onClick={()=>this.deleteRole(item.id)}>删除</a>:''}
                                                                {limitBtn.indexOf('up')>-1?<a href="javascript:void(0)" onClick={()=>this.stickTop(item.id)}>置顶</a>:''}                                                                                                                                                                                                 
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
                    // onOk={this.modalSave}
                    width={width}
                    style={{ top: 60 }}
                    onCancel={this.handleCancel}
                    footer={[
                        <Button key="back" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" type='more' loading={loading} onClick={this.modalSave}>保存</Button>
                    ]}
                    // confirmLoading={confirmLoading}
                >
                    {modalHtml}            
                </Modal>
            </div>
        )
        
    }
}