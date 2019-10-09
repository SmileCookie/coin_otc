import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import VipRateList from '../../common/select/vipRateList'
import { Button,Pagination,Input,Modal,message } from 'antd'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE_20,PAGRSIZE_OPTIONS20,PAGESIZE } from '../../../conf'
import { pageLimit,tableScroll } from "../../../utils"
const { TextArea } = Input;

export default class IntegralVipRule extends React.Component{

    constructor(props) {
        super(props)
        this.state = {
            showHide:true,
            inpVipRate:'',
            inpJiFen:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE_20,
            tableList:[],
            pageTotal:0,
            visible:false,
            title:'',
            id:'',
            isInquire:false,
            modalHtml:'',
            limitBtn: [],
            googVisibal:false,
            item:'',
            type:'',
            check:'',
            height:0,
            tableScroll:{
                tableId:'ITLVRE',
                x_panelId:'ITLVREX',
                defaultHeight:500,
            }
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleOk = this.handleOk.bind(this)
        this.showModal = this.showModal.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onModifyItem = this.onModifyItem.bind(this)
        this.onModifyBtn = this.onModifyBtn.bind(this)
        this.onDeleteItem = this.onDeleteItem.bind(this)
        this.addVipRule = this.addVipRule.bind(this)
        this.addVipRuleBtn = this.addVipRuleBtn.bind(this)
        this.handleTypeChange = this.handleTypeChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.queryBtnclick = this.queryBtnclick.bind(this)
        this.handleMTypeChange = this.handleMTypeChange.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.deleteItem = this.deleteItem.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.getHeight = this.getHeight.bind(this)
    }
    
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('integralVipRule',this.props.permissList)
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
    //弹窗 ok 
    handleOk(){
        this.setState({ loading: true });
        setTimeout(() => {
          this.setState({ 
              loading: false, 
              visible: false 
          });
        }, 3000);
    }
    //弹窗显示
    showModal(){
        this.setState({
          visible: true,
        });
    }
    //弹窗隐藏
    handleCancel(){
        console.log("handleCancel")
        this.setState({ 
            visible: false,
            vipRate:'',
            jifen:'',
            discount:'',
            memo:''
        });
    }
    //等级 Select
    handleTypeChange(val){
        this.setState({
            inpVipRate:val
        })
    }

    //点击分页
    changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        },()=>this.requestTable(page,pageSize))
        
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
   
    //查询按钮
    inquireBtn(){
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())  
    }

    requestTable(currIndex,currSize,currRate){
        const { inpVipRate,inpJiFen,pageIndex,pageSize } = this.state
        axios.post(DOMAIN_VIP+'/integralVipRule/queryUserVipLevel',qs.stringify({
            vipRate:currRate||inpVipRate,
            jiFen:inpJiFen,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            this.setState({
                tableList:result.page.list,
                pageTotal:result.page.totalCount
            })
        })
    }

    onResetState(){
        this.setState({
            inpVipRate:'',
            inpJiFen:'',
        })
    }
    //修改弹窗
    onModifyItem(item){
        const { vipRate,jifen,discount,memo,id } = item 
        this.footer=[
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(0,3)}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            title:'修改积分等级规则',
            vipRate,
            jifen,
            discount,
            memo,
            id,
            modalHtml:'',
        })
    }
    //修改弹窗按钮
    onModifyBtn(){
        const { vipRate,jifen,discount,memo,id } = this.state
        axios.post(DOMAIN_VIP+'/integralVipRule/update',qs.stringify({
            id,
            vipRate,
            jifen,
            discount,
            memo
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false,
                    inpVipRate:'',
                    pageIndex:PAGEINDEX,
                    pageSize:PAGESIZE_20,
                })
                this.requestTable(PAGEINDEX,PAGESIZE_20,'')
                this.setState({
                    vipRate:'',
                    jifen:'',
                    discount:'',
                    memo:''
                })
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //删除Item
    deleteItem(id){
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP+'/integralVipRule/delete',qs.stringify({
                id
            })).then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    this.requestTable()
                    resolve(result.msg)
                }else{
                    message.warning(result.msg)
                }
            }).then(error => {
                reject(error)
            })
        }).catch(() => console.log('Oops errors!'));
    }
    //删除积分等级弹窗
    onDeleteItem(id,type){
        let self = this;
        Modal.confirm({
            title: '您确定要删除此条记录?',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk(){
                self.modalGoogleCode(id,type)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }

    //google 验证弹窗
    modalGoogleCode(item,type){
        this.setState({
            googVisibal:true,
           item,
           type,
        })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { item,type } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(type == 1){
                    this.setState({
                        googVisibal:false
                    },() => this.addVipRuleBtn())
                }else if(type == 2){
                    this.setState({
                        googVisibal:false
                    },() => this.deleteItem(item))
                }else if(type == 3){
                    this.setState({
                        googVisibal:false
                    },() => this.onModifyBtn())
                }
                
            }else{
                message.warning(result.msg)
            }
        })
    }
    //新增弹窗
    addVipRule(item,type){
        this.footer=[
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.modalGoogleCode(item,type)}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            title:'添加积分等级规则',
            vipRate:'',
            jifen:'',
            discount:'',
            memo:'',
            modalHtml:''
        })
    }
    //新增弹窗按钮
    addVipRuleBtn(){
        const { vipRate,jifen,discount,memo } = this.state
        if(vipRate==" "&&vipRate!=0){
            message.warning("请选择用户等级！");
            return false;
        }
        if(!jifen){
            message.warning("请输入所需积分！");
            return false;
        }
        if(!discount){
            message.warning("请输入费率折扣！");
            return false;
        }
        axios.post(DOMAIN_VIP+'/integralVipRule/save',qs.stringify({
            vipRate,jifen,discount,memo
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    inpVipRate:'',
                    pageIndex:PAGEINDEX,
                    pageSize:PAGESIZE_20,
                    modalHtml:''
                })
                this.requestTable(PAGEINDEX,PAGESIZE_20,'')
            }else{
                message.warning(result.msg)
            }
        })
    }
    //查询点击
    queryBtnclick(val){
        this.setState({
            isInquire:val
        })
    }

    handleMTypeChange(val){
        this.setState({
            vipRate:val
        })
        console.log(val)
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
        const { showHide,tableList,pageTotal,pageIndex,pageSize,visible,isInquire,title,vipRate,jifen,discount,memo,inpVipRate,inpJiFen,modalHtml,limitBtn } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 积分管理 > 积分等级规则
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            <div className="x_content">
                                <div className="col-md-4 col-sm-4 col-xs-4">
                                    <VipRateList col='3' vipType={inpVipRate}  handleChange={this.handleTypeChange} />
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {limitBtn.indexOf('save')>-1?<Button type="primary" onClick={()=>this.addVipRule(0,1)}>添加</Button>:''}                                                                               
                                    </div>
                                </div>
                            </div>
                        </div>}

                        <div className="x_panel">
                            <div className="x_content">
                                <div id={this.state.tableScroll.tableId}  style={{height:`${this.state.tableList.length>10?this.state.tableScroll.defaultHeight+this.state.height+'px':''}`}} className="table-responsive-yAuto">
                                    <table className="table table-striped jambo_table bulk_action table-linehei table_scroll">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">VIP积分等级</th>
                                                <th className="column-title">所需积分</th>
                                                <th className="column-title">手续费折扣</th>
                                                <th className="column-title">备注</th>
                                                <th className="column-title">操作</th>                                                                                                                   
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        <tr key={index}>
                                                            <td>{item.vipRateName}</td>
                                                            <td>{item.jifen}</td>
                                                            <td>{item.discount}</td>
                                                            <td>{item.memo}</td>
                                                            <td>
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" className="mar10" onClick={() => this.onModifyItem(item)}>修改</a>:''}
                                                                {limitBtn.indexOf('delete')>-1?<a href="javascript:void(0)" className="mar10" onClick={() => this.onDeleteItem(item.id,2)}>删除</a>:''}   
                                                            </td>
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>
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
                <Modal
                    visible={visible}
                    title={title}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {modalHtml?modalHtml:<div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="form-group">
                                        <VipRateList col='3' vipType={vipRate}  handleChange={this.handleMTypeChange} />
                                    </div>
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">所需积分：<i>*</i></label>
                                        <div className="col-sm-9">
                                            <input type="text" className="form-control" name="jifen" value={jifen} onChange={this.handleInputChange} />
                                        </div>
                                    </div> 
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">费率折扣：<i>*</i></label>
                                        <div className="col-sm-9">
                                            <input type="text" className="form-control" name="discount" value={discount} onChange={this.handleInputChange} />
                                        </div>
                                    </div> 
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">备注：</label>
                                        <div className="col-sm-9 text-box">
                                            <TextArea  name="memo" value={memo} onChange={this.handleInputChange} />
                                        </div>
                                    </div>         
                                
                                </div>
                    }
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='IVR'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                />    
            </div>
        )
    }

}






























































