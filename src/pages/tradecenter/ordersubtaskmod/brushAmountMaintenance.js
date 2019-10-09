import React from 'react';
import moment from 'moment';
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, SELECTWIDTH, PAGRSIZE_OPTIONS20,DEFAULTVALUE } from '../../../conf'
import FundsTypeList from '../../common/select/fundsTypeList'
import BrushModal from './modal/brushModal'
import GoogleCode from '../../common/modal/googleCode'
import { message, Modal, Table, Button } from 'antd'
import BrushAccount from "../../common/select/brushAccount";
const { Column } = Table

export default class BrushAmountMaintenance extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            time: [],
            visible: false,
            modalHtml: '',
            title: '',
            width: '',
            tableSource: [],
            googVisibal: false,
            googleSpace: null,
            googletype: '',
            userid: '',
            fundsType: '0',
            ftype: '0',
            virtualMoney:'',
            accountType: '',
        }
    }
    componentDidMount() {
        this.requestTable();
    }
    clickHide = () => {
        this.setState({
            showHide: !this.state.showHide
        })
    }
    inquiry = () => {
        this.setState({
            pageIndex: PAGEINDEX,
        }, () => this.requestTable());
    }
    resetState = () => {
        this.setState({
            userid: '',
            fundsType: '0'
        },() => this.requestTable());
    }
    requestTable = (currIndex,currSize) => {
        const { fundsType, userid, pageIndex,pageSize } = this.state;
        axios.post(DOMAIN_VIP + '/brushamount/queryList', qs.stringify({
            fundstype:fundsType != '0'? fundsType: '',
            userid:userid,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data.list;
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].updatetime = moment(tableSource[i].updatetime).format('YYYY-MM-DD');
                    tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id
                }
                this.setState({
                    tableSource: tableSource,
                    pageTotal: result.data.totalCount
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    //输入时 input 设置到 satte
    handleInputChange = (event) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    };
    //弹框隐藏
    handleCancel = () => {
        this.setState({
            visible: false,
            ftype: '0',
            virtualMoney:'',
            accountType: ''
        })
    };
    onAddEdit = (record,type) => {
        let ftype = '0';
        let virtualMoney = '';
        let accountType = '';
        if(type == 'edit') {
            accountType = record.userid;
            virtualMoney = record.amount;
            ftype = record.fundstype;
        }
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={()=>this.modalGoogleCode(record.id, type)}>保存修改</Button>,
        ];
        this.setState({
            visible: true,
            width:'500px',
            title:'新增/修改',
            modalHtml: < BrushModal item={{
                ftype: ftype,
                virtualMoney:virtualMoney,
                accountType: accountType,
                type
            }}
            handleMTypeChange={this.selectAccountType}
            selectFundsType={this.selectfType}
            handleInputChange={this.handleInputChange}
            />,
            ftype: ftype,
            virtualMoney:virtualMoney,
            accountType: accountType,
        })
    }
    selectFundsType = value => {
        this.setState({
            fundsType:value
        })
    };
    selectAccountType = value => {
        this.setState({
            accountType:value
        })
    }
    selectfType = value => {
        this.setState({
            ftype:value
        })
    };
    onDelete = (record, type) => {
        let self = this;
        Modal.confirm({
            title: '您确定要删除此条记录?',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk(){
                self.modalGoogleCode(record.id,type)
            },
            onCancel() {},
        });
    };
    onDeleteBtn = () => {
        const {googleSpace} = this.state;
        axios.post(DOMAIN_VIP+'/brushamount/delete',qs.stringify({
            id: googleSpace,
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.setState({
                    visible:false
                })
                this.requestTable();
                message.success(result.msg)
            }else{
                message.warning(result.msg)
            }
        })
    };
    onChangePageNum = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex, pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        });
        this.requestTable(pageIndex, pageSize)
    }
    //google验证弹窗
    modalGoogleCode = (item, googletype) => {
        this.setState({
            googVisibal: true,
            googleSpace: item,
            googletype,
        })
    }
    //google 按钮
    modalGoogleCodeBtn = value => {
        const { googletype } = this.state;
        const { googleCode } = value
        axios.post(DOMAIN_VIP + "/common/checkGoogleCode", qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                switch (googletype) {
                    case 'add':
                        this.setState({
                            googVisibal: false
                        },()=>this.addRuleBtn());
                        break;
                    case 'edit':
                        this.setState({
                            googVisibal: false
                        },()=>this.addRuleBtn());
                        break;
                    case 'delete':
                        this.setState({
                            googVisibal: false
                        },()=>this.onDeleteBtn());
                        break;
                    default:
                        break;
                }
            } else {
                message.warning(result.msg)
            }
        })
    };
    addRuleBtn = () =>{
        const {ftype, virtualMoney, accountType, googleSpace} = this.state;
        if(!accountType){
            message.warning("请选择刷量账号！");
            return false
        }
        if(ftype == '' || ftype == '0'){
            message.warning("请选择资金类型！");
            return false
        }
        if(!virtualMoney){
            message.warning("请输入虚拟资金！");
            return false
        }
        axios.post(DOMAIN_VIP+'/brushamount/insertOrUpdate',qs.stringify({
            id: googleSpace,
            fundstype: ftype,
            userid: accountType,
            amount: virtualMoney
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
    handleCreate = () => {
        const form = this.formRef.props.form;
        form.validateFields((err, valus) => {
            if (err) {
                return;
            }
            form.resetFields();
            this.modalGoogleCodeBtn(valus)
        })
    }
    saveFormRef = formRef => {
        this.formRef = formRef
    }
    //google弹窗关闭
    onhandleCancel = () => {
        this.setState({
            googVisibal: false
        })
    };
    handleMTypeChange = (val)=>{
        this.setState({
            userid:val
        })
    }
    render() {
        const { tableSource,showHide, visible, modalHtml, title, width, pageIndex, pageSize, pageTotal,fundsType,userid } = this.state
        return (
            <div className="right-con">
                <div className='page-title'>
                    当前位置：
                    <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                </div>
                <div className='clearfix'></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className='x_content'>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <BrushAccount accountType={userid}  handleChange={this.handleMTypeChange} />
                                    </div>
                                </div>
                                <div className='col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4'>
                                    <FundsTypeList title='资金类型' fundsType={fundsType} handleChange={this.selectFundsType} />
                                </div>
                                <div className="right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.inquiry}>查询</Button>
                                        <Button type="primary" onClick={this.resetState}>重置</Button>
                                        <Button type="primary" onClick={() => this.onAddEdit({}, 'add')}>新增</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table
                                        dataSource={tableSource}
                                        bordered
                                        pagination={{
                                            size: "small",
                                            pageSize: pageSize,
                                            current: pageIndex,
                                            total: pageTotal,
                                            onChange: this.onChangePageNum,
                                            showTotal: total => `总共 ${total} 条`,
                                            onShowSizeChange: this.onShowSizeChange,
                                            pageSizeOptions: PAGRSIZE_OPTIONS20,
                                            defaultPageSize: PAGESIZE,
                                            showSizeChanger: true,
                                            showQuickJumper: true
                                        }}
                                        locale={{ emptyText: '暂无数据' }}
                                    >
                                        <Column title='序号' dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )} />
                                        <Column title='刷量账号' dataIndex='userid' key='userid' />
                                        <Column title='资金类型' dataIndex='fundstypeName' key='fundstypeName' />
                                        <Column title='虚拟资金' dataIndex='amount' key='amount' />
                                        <Column title='更新时间' dataIndex='updatetime' key='updatetime' />
                                        <Column title='操作' dataIndex='' key='' render={(text, record) => {
                                            return <span>
                                                <a href='javascript:void(0);' className="mar10" onClick={() => this.onAddEdit(record, 'edit')}>修改</a>
                                                <a href='javascript:void(0);' className="mar10" onClick={() => this.onDelete(record, 'delete')}>删除</a>
                                            </span>
                                        }} />
                                    </Table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    width={width}
                    title={title}
                    footer={this.footer}
                    onCancel={this.handleCancel}
                >
                    {modalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange={this.handleInputChange}
                    mid='BRUAM'
                    visible={this.state.googVisibal}
                    onCancel={this.onhandleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )
    }
}







