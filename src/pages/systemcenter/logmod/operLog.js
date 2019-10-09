import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalDetail from './modal/modalDetail'
import ModalOper from './modal/modalOper'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,TIMEFORMAT,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Select,Button,DatePicker,Pagination,Modal,message } from 'antd'
import { pageLimit,tableScroll } from '../../../utils/index'
const { RangePicker } = DatePicker;
const Option = Select.Option;

export default class OperLog extends React.Component{

    constructor(props){
        super(props)
        this.state={
            tableList:[],
            showHide:true,
            pageTotal:0,
            operusername:'',
            logtypeid:'0',
            opertimeS:'',
            opertimeE:'',
            operbeforelog:'',
            operafterlog:'',
            operchangelog:'',
            operip:'',
            time:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            limitBtn: [],
            height:0,
            tableScroll:{
                tableId:'OLGS',
                x_panelId:'OLGSX',
                defaultHeight:500,
            }
        }

        this.requestTable = this.requestTable.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onChangeTime = this.onChangeTime.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.inqueryBtn = this.inqueryBtn.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.detailInfo = this.detailInfo.bind(this)
        this.handleChangeType = this.handleChangeType.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('log', this.props.permissList)
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

    inqueryBtn(){
        this.setState({
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE
        },()=>this.requestTable())
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
    requestTable(){
        const {operusername,logtypeid,opertimeS,opertimeE,operbeforelog,operafterlog,operchangelog,operip,pageIndex,pageSize} = this.state
        axios.post(DOMAIN_VIP+"/operInfo/queryList",qs.stringify({
            operusername,logtypeid,opertimeS,opertimeE,operbeforelog,operafterlog,operchangelog,operip,pageIndex,pageSize
        })).then(res => {
            const result = res.data
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

    //弹窗隐藏
    handleCancel(){
        this.setState({ 
            visible: false
        });
    }
    //详细信息
    detailInfo(id){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="primary" loading={this.state.loading} onClick={this.handleCancel}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            title:"详细信息",
            width:"800px",
            modalHtml:<ModalDetail id={id} />
        })
        
    }

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestTable())
        
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.setState({
            pageIndex:current,
            pageSize:size
        }, () => this.requestTable())
    }

    //时间改变
    onChangeTime(date, dateString) {
        this.setState({
            opertimeS:dateString[0],
            opertimeE:dateString[1],
            time:date
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

    //日志类型 select
    handleChangeType(val){
        console.log(val)
        this.setState({
            logtypeid:val
        })
    }

    //重置状态
    onResetState(){
        this.setState({
            operusername:'',
            logtypeid:'0',
            opertimeS:'',
            opertimeE:'',
            operbeforelog:'',
            operafterlog:'',
            operchangelog:'',
            operip:'',
            time:[]
        })
    }

    render(){
        const { showHide,operusername,logtypeid,opertimeS,opertimeE,operbeforelog,operafterlog,operchangelog,operip,time,tableList,pageTotal,pageIndex,pageSize,visible,width,title,modalHtml } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>日志管理>操作日志
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                           
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">登录名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="operusername" value={operusername}  onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <ModalOper logtypeid={logtypeid} col='3' handleChangeType={this.handleChangeType} />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">操作时间：</label>
                                        <div className="col-sm-8">
                                            <RangePicker onChange={this.onChangeTime} value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">操作前信息：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="operbeforelog" value={operbeforelog}  onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">操作后信息：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="operafterlog" value={operafterlog}  onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">操作变更信息：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="operchangelog" value={operchangelog}  onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">操作IP：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="operip" value={operip} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right martop4">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inqueryBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}

                        <div className="x_panel">
                            <div className="x_content">
                                <div  id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto table-responsive-fixed">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title min_69px">序号</th>
                                                <th className="column-title min_82px">登录名</th>
                                                <th className="column-title min_68px">真实姓名</th>
                                                <th className="column-title">操作时间</th>
                                                <th className="column-title">日志类型</th>
                                                <th className="column-title">对应业务ID</th>
                                                <th className="column-title wid300 min_153px">操作后信息</th>  
                                                <th className="column-title wid300 min_153px">操作变更信息</th>
                                                <th className="column-title min_68px">操作IP</th>
                                                <th className="column-title min_68px">操作</th>                       
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return(
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{item.operusername}</td>
                                                            <td>{item.realName}</td>                                         
                                                            <td>{moment(item.opertime).format(TIMEFORMAT)}</td>
                                                            <td>{item.logtypename}</td>
                                                            <td>{item.busiid}</td>
                                                            <td>{item.operafterlog}</td>
                                                            <td>{item.operchangelog}</td>
                                                            <td>{<a href={`http://www.ip138.com/ips138.asp?ip=${item.operip}&action=2`} target="_blank">{item.operip}</a>}</td>
                                                            <td><a href="javascript:void(0)" onClick={() => this.detailInfo(item.id)}>详细信息</a></td>
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
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    { modalHtml }
                </Modal>  
            </div>
        )
    }

}


























