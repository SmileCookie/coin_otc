import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import ModalRule from './modal/modalRule'
import GoogleCode from '../../common/modal/googleCode'
import {DOMAIN_VIP, PAGEINDEX, PAGESIZE, PAGRSIZE_OPTIONS20, SELECTWIDTH} from '../../../conf'
import {Button, Pagination, Modal, message, Select, Switch} from 'antd';
import RuleTypeList from '../../common/select/ruleTypeList';
import { pageLimit } from '../../../utils'
const Option = Select.Option;

export default class IntergralRule extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            showHide:true,
            scoreName:0, //名称选择
            scoreType:0, //类型选择
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],
            pageTotal:0,
            visible:false,
            loading: false,
            modalHtml:'',
            title:'',
            scoreItem: {
                typeCode: '1',
                status: 1, // 状态
                integType: 1, //积分类型
                ruleSelect: 1, //活动积分的规则选择
                punishType: 1,// 惩罚积分的规则的的下拉框
            }, // 每条积分规则的详情
            limitBtn: [],
            checkGoogle:'',
            googVisibal:false,
            googleSpace:'',
            googleType:'',
            check:'',
        };
    }

    componentDidMount() {
        this.requestTable();
        this.setState({
            limitBtn: pageLimit('integralRule', this.props.permissList)
        })
    }
    //点击分页
    changPageNum =(page,pageSize)=>{
        this.setState({
            pageIndex:page
        },() => this.requestTable(page,pageSize))
    }
    //分页的 pagesize 改变时
    onShowSizeChange =(current,size)=>{
        this.requestTable(current,size);
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }

    //输入时 input 设置到 satte
    handleInputChange=event=>{
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        let item = this.state.scoreItem;
        item[name] = value;
        this.setState({
            scoreItem: item
        });
    };
    //名称的筛选
    handleChange =(val)=> {
        this.setState({
            scoreName: val
        })
    };
    //查询按钮
    inquireBtn=()=>{
        this.setState({
            pageIndex:PAGEINDEX
        },() => this.requestTable())
        
    }

    requestTable = (currIndex,currSize)=>{
        const { scoreName,scoreType,pageIndex,pageSize } = this.state;
        axios.post(DOMAIN_VIP+'/integralRule/queryIntegralRule',qs.stringify({
            seqNo:scoreName,
            typeCode:scoreType,
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
    //弹窗 ok 
    handleOk =()=>{
        this.setState({ loading: true });
        setTimeout(() => {
          this.setState({ 
              loading: false, 
              visible: false 
          });
        }, 3000);
    }
    //弹窗显示
    showModal=()=>{
        this.setState({
          visible: true,
        });
    }
    //弹窗隐藏
    handleCancel =()=>{
        console.log("handleCancel")
        this.setState({ 
            visible: false,
        });
    };
    //重置按钮
    onResetState =()=>{
        this.setState({
            scoreName:0,
            scoreType:0,
            pageIndex:PAGEINDEX
        },()=>this.requestTable())
    };
    //删除Item
    deleteItem =(id)=>{
        return new Promise((resolve, reject) => {
                axios.post(DOMAIN_VIP+'/integralRule/delete',qs.stringify({
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
    //删除弹窗
    onDeleteItem =(id,type)=>{
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

    //修改积分规则
    modifyRuleItem =(item)=>{
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.validate(item.id,2)}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            modalHtml:
                <ModalRule
                    item={item}
                    handleInputChange={this.handleInputChange}
                    onChooseChange = {this.onChooseChange}/>,
            title:'修改积分规则',
            scoreItem: item
        })
    }
    onChooseChange = (val, name)=>{
        let item = this.state.scoreItem;
        item[name] = val
        this.setState({
            scoreItem:item
        })
    };
    //修改按钮
    modifyRuleBtn=(id)=>{
        this.setState({
            loading:true
        });
        let {seqNo,typeCode,score,integType,rule,memo,status, unit, daylimit,punishType } = this.state.scoreItem;
        status = status == true ? 1 : 0;
        let punishTypeName = punishType == 1 ? '登录': punishType == 2? '交易':'充值';
        if(typeCode == "2") {
            rule = '用户单次充值/交易金额（折算成USDC）每满足'+ unit + '获得' + score + '积分，单日积分获取上限' + daylimit + '总计';
        } else if(typeCode == "4") {
            rule = '用户连续'+ unit + '小时未' + punishTypeName + '惩罚' + score + '积分';
        }
        axios.post(DOMAIN_VIP+'/integralRule/update',qs.stringify({
            seqNo: seqNo,
            typeCode,
            score,
            integType,
            rule,
            status,
            unit,
            daylimit,
            punishType,
            memo,
            id
        })).then(res => {
            const result = res.data;
            console.log(result)
            if(result.code == 0){
                this.setState({
                    visible:false,
                    loading:false
                });
                this.requestTable();
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //google 弹窗添加 回撤 事件
    modalGoogleCodekeyPress(e){
        const { visible } = this.state
        if(visible){
            if(e.keyCode == 13){
                if(this.input&&this.input.value){
                    this.googleBtn.props.onClick()
                }
            }
        }
    }
    //google 验证弹窗
    modalGoogleCode = (item,type)=>{
        this.setState({
            googVisibal:true,
            googleSpace:item,
            googleType:type,
        })
    }

    //google 按钮
    modalGoogleCodeBtn =(value)=>{
        const { googleSpace,googleType } = this.state
        const {googleCode} = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                if(googleType == 1){
                    this.setState({
                        googVisibal:false
                    },() => this.addRuleBtn())
                }else if(googleType == 2){
                    this.setState({
                        googVisibal:false
                    },() => this.modifyRuleBtn(googleSpace))
                }else if(googleType == 3){
                    this.setState({
                        googVisibal:false
                    },() => this.deleteItem(googleSpace))
                }
                
            }else{
                message.warning(result.msg)
            }
        })
        
    }

    //添加类型
    addRuleItem =()=>{
        this.resetItem(1);
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.validate(0, 1)}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            modalHtml:
                <ModalRule
                    item={{}}
                    resetItem={()=>this.resetItem(1)}
                    handleInputChange={this.handleInputChange}
                    onChooseChange = {this.onChooseChange}/>,
            title:'添加积分规则',
        })
    };
    validate = (id, type)=> {
        let item = this.state.scoreItem;
        if(!item.seqNo){
            message.warning("请选择名称！");
            return false
        }
        if(item.typeCode == '1') {
            if(!item.rule || item.rule == ''){
                message.warning("请输入积分规则！");
                return false
            }
            if(!item.score){
                message.warning("请输入积分！");
                return false
            }
        } else if(item.typeCode == '2') {
            if(!item.unit || (!item.score) || (!item.daylimit) ){
                message.warning("请输入完整的积分规则！");
                return false
            }
        } else if(item.typeCode == '4') {
            if(!item.unit || (!item.score)){
                message.warning("请输入完整的积分规则！");
                return false
            }
        }
        this.modalGoogleCode(id,type);
    };
    addRuleBtn=()=>{
        let {seqNo,typeCode,score,integType,rule,memo,status, unit, daylimit,punishType } = this.state.scoreItem;
        status = status == true ? 1 : 0;
        let punishTypeName = punishType == 1? '登录':punishType == 2? '交易':'充值';
        if(typeCode == "2") {
            rule = '用户单次充值/交易金额（折算成USDC）每满足'+ unit + '获得' + score + '积分，单日积分获取上限' + daylimit + '总计';
        } else if(typeCode == "4") {
            rule = '用户连续'+ unit + '小时未' + punishTypeName + '惩罚' + score + '积分';
        }
        axios.post(DOMAIN_VIP+'/integralRule/save',qs.stringify({
            seqNo: seqNo,
            typeCode,
            score,
            integType,
            rule,
            status,
            unit: unit == ""? 0:unit,
            daylimit: daylimit == ""? 0: daylimit,
            punishType,
            memo
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false
                })
                this.requestTable()
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //点击收起
    clickHide = () =>{
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    };
    handleCreate = ()=>{
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.modalGoogleCodeBtn(values)
        });
      };
      saveFormRef=(formRef)=>{
        this.formRef = formRef;
      }
        //谷歌弹窗关闭
    onhandleCancel=()=>{
        this.setState({
            googVisibal: false 
        })
    };
    onSelectChoose = (val, name)=> {
        this.setState({
            [name]: val
        })
    };
    //新增的时候类型切换的时候清空输入的数据
    resetItem = (tag)=> {
        let scoreItem = {
            status: 1, // 状态
            integType: 1, //积分类型
            unit: '',
            daylimit: '',
            score: '',
            memo: '',
            ruleSelect: 1, //活动积分的规则选择
            punishType: 1,// 惩罚积分的规则的的下拉框
        };
        if(tag) {
            scoreItem.typeCode = '1';
        }
        this.setState({
            scoreItem
        })
    };
    render(){
        const { showHide,tableList,pageTotal,pageIndex,pageSize,visible,title,modalHtml,scoreName,scoreType,limitBtn } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > VIP管理 > 积分规则
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div className="x_panel">
                           
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <RuleTypeList col="3" scoreName={scoreName} handleChange={this.handleChange} />
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">类型：</label>
                                        <div className="col-sm-9">
                                            <Select value={scoreType} style={{ width: SELECTWIDTH }} onChange={val=>this.onSelectChoose(val, 'scoreType')}>
                                                <Option value={0}>请选择</Option>
                                                <Option value="1">普通积分</Option>
                                                <Option value="2">累加积分</Option>
                                                {/*<Option value="3">活动积分</Option>*/}
                                                <Option value="4">惩罚积分</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        {limitBtn.indexOf('save')>-1?<Button type="primary" onClick={this.addRuleItem}>添加</Button>:''}
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
                                                <th className="column-title min_69px">序号</th>
                                                <th className="column-title min_116px ">名称</th>
                                                <th className="column-title min_116px">类型</th>
                                                <th className="column-title min_116px">积分</th>
                                                <th className="column-title min_116px">积分类型</th>
                                                <th className="column-title min_394px">规则</th>
                                                <th className="column-title min_68px">状态</th>
                                                <th className="column-title min_394px">备注</th>
                                                <th className="column-title">操作</th>                                                                                                                     
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>0? 
                                                tableList.map((item,index)=>{
                                                    return (
                                                        <tr key={index}>
                                                            <td>{item.seqNo}</td>
                                                            <td>{item.type}</td>
                                                            <td>{item.typeCode == "1" ? '普通积分': item.typeCode == "2" ? '累加积分': item.typeCode == "3"? '活动积分':'惩罚积分'}</td>
                                                            <td>{item.score}</td>
                                                            <td>{item.integTypeName}</td>
                                                            <td>{item.rule}</td>
                                                            <td><Switch checked={item.status == 1} disabled /></td>
                                                            <td>{item.memo}</td>
                                                            <td>
                                                                {limitBtn.indexOf('update')>-1?<a href="javascript:void(0)" className="mar10" onClick={() => this.modifyRuleItem(item)}>修改</a>:''}
                                                                {limitBtn.indexOf('delete')>-1?<a href="javascript:void(0)" className="mar10" onClick={() => this.onDeleteItem(item.id,3)}>删除</a>:''}                                                                
                                                            </td>
                                                        </tr>
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
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {modalHtml}
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='IR'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                />    
            </div>
        )
    }

}






























































