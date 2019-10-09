import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,DEFAULTVALUE,PAGRSIZE_OPTIONS20 } from '../../../conf'
import { Button, Pagination,message,Modal } from 'antd'
import { tableScroll } from '../../../utils'
const Big = require('big.js')
const confirm = Modal.confirm;

export default class CurrencyIndex extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            tableList:[],
            coinName:'',
            coinImg:'',
            visible:false,
            height:0,
            tableScroll:{
                tableId:'CURID',
                x_panelId:'CURIDX',
                defaultHeight:500,
            },
            fundsList:[]
        }
        this.requestTable = this.requestTable.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.clickInquireState = this.clickInquireState.bind(this)
        this.show_click = this.show_click.bind(this)
        this.onShowCoinImg = this.onShowCoinImg.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onDelete = this.onDelete.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    componentDidMount(){
        this.requestTable()
        this.requestTable(1,10000)
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
    //输入时 input 设置到 state
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]:value
        })
    }
    //点击收起
    clickHide(){
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
    //点击分页
    changPageNum(pageIndex,pageSize){        
        this.setState({
            pageIndex,
            pageSize,
        },()=>this.requestTable(pageIndex,pageSize))
    }
    //分页pageSize改变时
    onShowSizeChange(current,size){
        this.setState({
            pageIndex:current,
            pageSize:size
        },()=>this.requestTable(current,size))
    }
    //查询按钮
    clickInquireState(){
        this.requestTable()
    }
    //重置按钮
    onResetState(){
        this.setState({
            coinName:'',
        })
    }
    //关闭弹窗
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //修改状态
    show_click(index,activityId,fundsList){
        // console.log(activityId)
        this.props.showHideClick(index,activityId,fundsList)
    }
    //查看币种图片
    onShowCoinImg(item){       
        this.setState({
            visible:true,
            coinImg:item.img
        })
    }
    //请求列表
    requestTable(currIndex,currSize){
        const {pageIndex,pageSize,coinName} = this.state
        axios.get(DOMAIN_VIP+'/coin/query', {params:{
            coinNameJson:coinName,
            pageIndex:currIndex || pageIndex,
            pageSize:currSize || pageSize,
        }}).then(res => {
            const result = res.data;
            if(result.code == 0){
                if(currSize > 200){
                    let fundsList = result.data.list.map((item, index) =>JSON.parse(item.coinNameJson).cn )
                    this.setState({
                        fundsList
                    })
                }else{
                    this.setState({
                        tableList:result.data.list,
                        pageTotal:result.data.totalCount
                    })
                }
            }else{
                message.warning(result.msg);
            }
        })
    }
    //删除
    onDelete(coinId){
        let self = this;
        Modal.confirm({
            title:'确定删除本项吗？',
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/coin/delete', qs.stringify({
                        coinId
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.requestTable()
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => reject(error))
                }).catch(() => console.log('Oops errors!'))
            },
            onCancel() {
                console.log('Cancel');
            },
        })
    }
    render(){
        const { showHide,pageIndex,pageSize,pageTotal,tableList,coinName,visible,coinImg } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 活动管理 > 币种介绍
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">币种名称</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="coinName" value={coinName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.clickInquireState} >查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        <Button type="primary" onClick={ () => {this.show_click(1,{},this.state.fundsList)} } >新增</Button>
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
                                                <th className="column-title">币种名称</th>
                                                <th className="column-title">全称</th>
                                                <th className="column-title wid300 ">币种图片</th>
                                                {/* <th className="column-title wid300">币种链接</th> */}
                                                {/* <th className="column-title">状态</th> */}
                                                <th className="column-title">操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length > 0 ? tableList.map((item, index) => {

                                                    return (
                                                        <tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index+1}</td>
                                                            <td>{JSON.parse(item.coinNameJson).en}</td>                                    
                                                            <td>{JSON.parse(item.coinFullNameJson).en}</td>  
                                                            <td><a className="mar10" href="javascript:void(0)" onClick={()=>this.onShowCoinImg(item)}>{item.img}</a></td>
                                                            {/* <td>{JSON.parse(item.urlJson).cn!='null'?<a className="mar10" target="_blank" href={JSON.parse(item.urlJson).cn} >{JSON.parse(item.urlJson).cn}</a>:''}</td>                                           */}
                                                            {/* <td>{item.status == 0 ? '关闭':'启用'}</td> */}
                                                            <td>
                                                                <a className="mar10" href="javascript:void(0)" onClick={() => this.show_click(2,item)}>修改</a>
                                                                <a className="mar10" href="javascript:void(0)" onClick={() => this.onDelete(item.coinId)}>删除</a>                                                                
                                                            </td>
                                                        </tr>
                                                    )
                                                }) : <tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
                                <div className="pagation-box">
                                    {
                                        pageTotal > 0 && <Pagination
                                            size="small"
                                            current={pageIndex}
                                            total={pageTotal}
                                            onChange={this.changPageNum}
                                            showTotal={total => `总共 ${total} 条`}
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
                <Modal visible={visible} footer={null} onCancel={this.handleCancel}>
                    <img alt="example" style={{ width: '100%' }} src={coinImg} />
                </Modal>
            </div>
        )
    }
}