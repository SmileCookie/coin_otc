import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE, DEFAULTVALUE, PAGRSIZE_OPTIONS20, TIMEFORMAT_ss } from '../../../conf'
import { Button, Pagination, message, Modal, Table, DatePicker,Radio } from 'antd'
import ModalAdverPhoto from './modal/modalAdverPhoto'
import GoogleCode from '../../common/modal/googleCode'
import ModalLinkView from './modal/modalLinkView'
const confirm = Modal.confirm;
const { Column } = Table
const { RangePicker } = DatePicker;
const RadioGroup = Radio.Group;

export default class AdvertisingPhotoManage extends React.Component {
    constructor(props) {
        super(props)
        this.default = {
            bannerUrlCN: '',
            bannerUrlEN: '',
            bannerUrlHK: '',
            bannerUrlJP: '',
            bannerUrlKR: '',
            linkUrlCN: '',
            linkUrlEN: '',
            linkUrlHK: '',
            linkUrlKR: '',
            linkUrlJP: '',
            bannerName: '',
        }
        this.state = {
            showHide: true,
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            tableList: [],
            modalHtml: '',
            title: '',
            visible: false,
            loading: false,
            groupId: '',
            tableSource: [],
            pagination: {
                showQuickJumper: true,
                showSizeChanger: true,
                showTotal: total => `总共${total}条`,
                size: 'small',
                // total:0,
                pageSizeOptions: PAGRSIZE_OPTIONS20,
                defaultPageSize: PAGESIZE
            },
            selectedRowKeys: [],//选中项的 key 数组
            selectedRows: [],
            ...this.default,
            googleCode: '',
            googleSpace: '',
            googleType: '',
            tabKey: 'en',
            createtimeS: '',
            createtimeE: '',
            time: [],
            rebannerName:''

        }
        this.requstTable = this.requstTable.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
        this.clickInquireState = this.clickInquireState.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onAddEditAdverPhoto = this.onAddEditAdverPhoto.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.onSelectChangeTable = this.onSelectChangeTable.bind(this)
        this.onDelete = this.onDelete.bind(this)
        this.onDeleteBtn = this.onDeleteBtn.bind(this)
        this.handleChangeBannerUrl = this.handleChangeBannerUrl.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.handleGoogleCancel = this.handleGoogleCancel.bind(this)
    }
    componentDidMount() {
        this.requstTable()
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        })
    }
    //时间控件
    onChangeCheckTime = (date, dateString) => {
        this.setState({
            createtimeS: dateString[0] ? moment(dateString[0]).format(TIMEFORMAT_ss) : '',
            createtimeE: dateString[1] ? moment(dateString[1]).format(TIMEFORMAT_ss) : '',
            time: date
        })
    }
    //点击收起
    clickHide() {
        const { showHide } = this.state
        this.setState({
            showHide: !showHide
        })
    }
    //点击分页
    changPageNum(pageIndex, pageSize) {
        this.setState({
            pageIndex,
            pageSize,
        }, () => this.requstTable(pageIndex, pageSize))
    }
    //分页pageSize改变时
    onShowSizeChange(current, size) {
        this.setState({
            pageIndex: current,
            pageSize: size
        }, () => this.requstTable(current, size))
    }
    //banner组ID选择
    handleChange(val) {
        this.setState({
            groupId: val
        })
    }
    //查询按钮
    clickInquireState() {
        this.requstTable()
    }
    //重置按钮
    onResetState() {
        this.setState({
            //groupId:''
            rebannerName: '',
            createtimeE: '',
            createtimeS: '',
            time: []
        }, () => {
            this.requstTable()
        })
    }
    //关闭弹窗
    handleCancel() {
        this.setState({
            visible: false,
            loading: false,
            // bannerUrlCN: '',
            // bannerUrlEN: '',
            // bannerUrlHK: '',
            // linkUrlCN: '',
            // linkUrlEN: '',
            // linkUrlHK: '',
            // bannerName: '',
            ...this.default
        })
    }
    //多选框按钮选中时
    onSelectChangeTable(selectedRowKeys, selectedRows) {
        this.setState({ selectedRowKeys, selectedRows });
        console.log(selectedRowKeys, selectedRows)
    }
    //banner图片链接设置
    handleChangeBannerUrl(url, type) {
        console.log(url + type)
        switch (type) {
            case 'CN':
                this.setState({
                    bannerUrlCN: url
                })
                break;
            case 'EN':
                this.setState({
                    bannerUrlEN: url
                })
                break;
            case 'HK':
                this.setState({
                    bannerUrlHK: url
                })
                break;
            case 'KR':
                this.setState({
                    bannerUrlKR: url
                })
                break;
            case 'JP':
                this.setState({
                    bannerUrlJP: url
                })
                break;
            default:
                break;
        }
    }
    //新增、修改广告图片管理弹窗
    onAddEditAdverPhoto(item, type) {
        // console.log(JSON.parse(item&&item.bannerUrl))
        let mtitle = item ? '修改广告图片' : '新增广告图片'
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.modalGoogleCode(item, type)}>保存修改</Button>
        ]
        this.setState({
            visible: true,
            title: mtitle,
            width: '700px',
            modalHtml: <ModalAdverPhoto item={item} handleChangeBannerUrl={this.handleChangeBannerUrl} handleInputChange={this.handleInputChange} tabCallback={this.tabCallback} />,
        })
        if (item) {
            this.setState({
                bannerUrlCN: JSON.parse(item.bannerUrl).cn || '',
                bannerUrlEN: JSON.parse(item.bannerUrl).en || '',
                bannerUrlHK: JSON.parse(item.bannerUrl).hk || '',
                bannerUrlKR: JSON.parse(item.bannerUrl).kr || '',
                bannerUrlJP: JSON.parse(item.bannerUrl).jp || '',
                linkUrlCN: JSON.parse(item.linkUrl).cn || '',
                linkUrlEN: JSON.parse(item.linkUrl).en || '',
                linkUrlHK: JSON.parse(item.linkUrl).hk || '',
                linkUrlKR: JSON.parse(item.linkUrl).kr || '',
                linkUrlJP: JSON.parse(item.linkUrl).jp || '',
                status: item.status || 0,
                linkType: item.linkType || 0,
                bannerName: item.bannerName
            })
        }
    }
    //修改广告图片管理弹窗
    onEditAdverPhotoBtn(item) {
        const { bannerUrlCN, bannerUrlEN, bannerUrlHK, status, bannerName, tabKey, bannerUrlJP, bannerUrlKR,linkType } = this.state
        let { linkUrlCN, linkUrlEN, linkUrlHK, linkUrlJP, linkUrlKR } = this.state
        console.log(this.state)
        if (!bannerName) {
            message.warning('图片名称不能为空！')
            return false
        }
        // if((bannerUrlCN&&linkUrlCN)||(bannerUrlEN&&linkUrlEN)||(bannerUrlHK&&linkUrlHK)){
        // console.log('不根据tab标签来判断中英繁只要有一个为true，就可以保存')
        // }else{
        //     return false
        // }
        switch (tabKey) {
            case 'cn':
                if (!bannerUrlCN) {
                    message.warning('简体图片不能为空！')
                    return false
                }
                if (!linkUrlCN) {
                    message.warning('简体跳转链接不能为空！')
                    return false
                }
                break;
            case 'en':
                if (!bannerUrlEN) {
                    message.warning('英文图片不能为空！')
                    return false
                }
                if (!linkUrlEN) {
                    message.warning('英文跳转链接不能为空！')
                    return false
                }
                break;
            case 'hk':
                if (!bannerUrlHK) {
                    message.warning('繁体图片不能为空！')
                    return false
                }
                if (!linkUrlHK) {
                    message.warning('繁体跳转链接不能为空！')
                    return false
                }
                break;
            case 'kr':
                if (!bannerUrlKR) {
                    message.warning('韩语图片不能为空！')
                    return false
                }
                if (!linkUrlKR) {
                    message.warning('韩语跳转链接不能为空！')
                    return false
                }
                break;
            case 'jp':
                if (!bannerUrlJP) {
                    message.warning('日语图片不能为空！')
                    return false
                }
                if (!linkUrlJP) {
                    message.warning('日语跳转链接不能为空！')
                    return false
                }
                break;
            default:
                break;
        }

        let linkCNurl;
        let linkENurl;
        let linkHKurl;
        let linkKRurl;
        let linkJPurl;
        if (linkUrlCN && linkUrlCN.slice(0, 3) !== 'htt') {
            linkCNurl = linkUrlCN.replace(linkUrlCN, 'http://' + linkUrlCN)
        } else {
            linkCNurl = linkUrlCN
        }
        if (linkUrlEN && linkUrlEN.slice(0, 3) !== 'htt') {
            linkENurl = linkUrlEN.replace(linkUrlEN, 'http://' + linkUrlEN)
        } else {
            linkENurl = linkUrlEN
        }
        if (linkUrlHK && linkUrlHK.slice(0, 3) !== 'htt') {
            linkHKurl = linkUrlHK.replace(linkUrlHK, 'http://' + linkUrlHK)
        } else {
            linkHKurl = linkUrlHK
        }

        if (linkUrlJP && linkUrlJP.slice(0, 3) !== 'htt') {
            linkJPurl = linkUrlJP.replace(linkUrlJP, 'http://' + linkUrlJP)
        } else {
            linkJPurl = linkUrlJP
        }

        if (linkUrlKR && linkUrlKR.slice(0, 3) !== 'htt') {
            linkKRurl = linkUrlKR.replace(linkUrlKR, 'http://' + linkUrlKR)
        } else {
            linkKRurl = linkUrlKR
        }
        // if(linkUrlCN.slice(0,3)==='htt'){
        //     linkCNurl = linkUrlCN
        // }else{
        //     linkCNurl = linkUrlCN.replace(linkUrlCN,'http://'+linkUrlCN)
        // }
        // if(linkUrlCN.slice(0,3)==='htt'){
        //     linkENurl = linkUrlEN
        // }else{
        //     linkENurl = linkUrlEN.replace(linkUrlEN,'http://'+linkUrlEN)
        // }
        // if(linkUrlCN.slice(0,3)==='htt'){
        //     linkHKurl = linkUrlHK
        // }else{
        //     linkHKurl = linkUrlHK.replace(linkUrlHK,'http://'+linkUrlHK)
        // }
        item ?

            axios.post(DOMAIN_VIP + '/bannerPhoto/update', qs.stringify({//修改
                id: item.id,
                status, bannerName,
                linkType: linkType,
                bannerUrlCN, bannerUrlEN, bannerUrlHK,bannerUrlJP,bannerUrlKR,
                linkUrlCN: linkCNurl, 
                linkUrlEN: linkENurl,
                linkUrlHK: linkHKurl,
                linkUrlJP:linkJPurl,
                linkUrlKR:linkKRurl
            })).then(res => {
                const result = res.data;
                if (result.code == 0) {
                    message.success(result.msg)
                    this.setState({
                        visible: false,
                        // bannerUrlCN: '',
                        // bannerUrlEN: '',
                        // bannerUrlHK: '',
                        // linkUrlCN: '',
                        // linkUrlEN: '',
                        // linkUrlHK: '',
                        // bannerName: '',
                        ...this.default
                    })
                    this.requstTable()
                } else {
                    message.warning(result.msg)
                }
            })
            :

            axios.post(DOMAIN_VIP + '/bannerPhoto/update', qs.stringify({//新增
                status, bannerName,
                linkType:linkType,
                bannerUrlCN, bannerUrlEN, bannerUrlHK,bannerUrlJP,bannerUrlKR,
                linkUrlCN: linkCNurl, 
                linkUrlEN: linkENurl,
                linkUrlHK: linkHKurl,
                linkUrlJP:linkJPurl,
                linkUrlKR:linkKRurl
            })).then(res => {
                const result = res.data;
                if (result.code == 0) {
                    message.success(result.msg)
                    this.setState({
                        visible: false,
                        // bannerUrlCN: '',
                        // bannerUrlEN: '',
                        // bannerUrlHK: '',
                        // linkUrlCN: '',
                        // linkUrlEN: '',
                        // linkUrlHK: '',
                        bannerName: '',
                        tabKey: 'en',
                        ...this.default
                    })
                    this.requstTable()
                } else {
                    message.warning(result.msg)
                }
            })

    }
    //请求列表
    requstTable(currIndex, currSize) {

        const { pageIndex, pageSize, groupId, pagination, rebannerName, createtimeS, createtimeE } = this.state
        let self = this
        console.log(createtimeS)
        axios.get(DOMAIN_VIP + '/bannerPhoto/queryList', {
            params: {
                bannerName: rebannerName,
                createtimeS, createtimeE,
                pageIndex: currIndex || pageIndex,
                pageSize: currSize || pageSize,
            }
        }).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data.list;
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.data.totalCount;
                pagination.onChange = self.changPageNum;
                pagination.onShowSizeChange = self.onShowSizeChange
                self.setState({
                    tableSource: tableSource,
                    pagination,
                })
            } else {
                message.warning(result.msg);
            }
        })
    }
    //删除弹窗
    onDelete(type, record) {
        let self = this;
        const id = record.id
        // const { selectedRowKeys } = this.state
        let selects = record.id;//转换成string并去除空格
        Modal.confirm({
            title: '确定删除本项吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.modalGoogleCode(selects, type)
            },
            onCancel() {
                console.log('Cancel');
            },
        })
    }
    //删除按钮
    onDeleteBtn(selects) {
        if (!selects) {
            message.warning('请至少选择一项删除！')
            return false;
        }
        return new Promise((resolve, reject) => {
            axios.post(DOMAIN_VIP + '/bannerPhoto/delete', qs.stringify({
                ids: selects
            })).then(res => {
                const result = res.data;
                if (result.code == 0) {
                    message.success(result.msg)
                    this.requstTable()
                    resolve(result.msg)
                } else {
                    message.warning(result.msg)
                }
            }).then(error => reject(error))
        }).catch(() => console.log('Oops errors!'))
    }
    //查看链接
    onViewLink = (item, type) => {
        let mtitle = type == 1 ? 'banner图链接查看' : '跳转链接查看'
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>关闭</Button>,
            // <Button key="submit" type="more" onClick={()=>this.modalGoogleCode(item,type)}>保存修改</Button>
        ]
        this.setState({
            title: mtitle,
            visible: true,
            modalHtml: <ModalLinkView links={item} type={type} />
        })

    }
    tabCallback = tabKey => {
        this.setState({
            tabKey
        })
    }
    //google弹窗
    modalGoogleCode(item, type) {
        this.setState({
            googVisibal: true,
            googleSpace: item,
            googleType: type
        })
    }
    //google 按钮
    modalGoogleCodeBtn(values) {
        const { googleSpace, googleType } = this.state
        const { googleCode } = values
        axios.post(DOMAIN_VIP + "/common/checkGoogleCode", qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                this.setState({
                    googVisibal: false
                })
                if (googleType == 'edit' || googleType == 'add') {
                    this.onEditAdverPhotoBtn(googleSpace)
                } else if (googleType == 'del') {
                    this.onDeleteBtn(googleSpace)
                } else if (googleType == 'photoEdit') {
                    this.onSelectPhotosBtn(googleSpace)
                }
            } else {
                message.warning(result.msg)
            }
        })
    }
    //google 校验并获取一组输入框的值
    handleCreate() {
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
            if (err) return;
            //重置输入框的值
            form.resetFields();
            this.modalGoogleCodeBtn(values)
        })
    }
    saveFormRef(formRef) {
        this.formRef = formRef
    }
    //google 弹窗关闭
    handleGoogleCancel() {
        this.setState({
            googVisibal: false
        })
    }
    render() {
        const { showHide, pageIndex, pageSize, pageTotal, tableList, visible, title, width, groupId, tableSource, pagination, selectedRowKeys, rebannerName, time } = this.state
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChangeTable,
            fixed: true
        };
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 广告管理 > 广告图片管理
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide && <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">图片名称：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="rebannerName" value={rebannerName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">上传时间:</label>
                                        <div className="col-sm-8">
                                            <RangePicker
                                                showTime={{
                                                    defaultValue: [moment('00:00:00', 'HH:mm,ss'), moment('23:59:59', 'HH:mm,ss')]
                                                }}
                                                format={TIMEFORMAT_ss}
                                                onChange={this.onChangeCheckTime}
                                                value={time} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="form-group right">
                                        <Button type="primary" onClick={this.clickInquireState} >查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        <Button type="primary" onClick={() => this.onAddEditAdverPhoto('', 'add')} >新增</Button>
                                        {/* <Button type="primary" onClick={()=>this.onDelete('del')}>删除</Button> */}
                                    </div>
                                </div>
                            </div>
                        </div>
                        }
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive ">
                                    <Table dataSource={tableSource} bordered pagination={pagination} locale={{ emptyText: '暂无数据' }}>
                                        <Column title='序号' dataIndex='index' render={(text) => (
                                            <span>{text}</span>
                                        )} />
                                        <Column title='图片名称(alt SEO)' dataIndex='bannerName' key='bannerName' />
                                        {/* <Column  title='banner图链接' dataIndex='bannerUrl' key='bannerUrl' render={(text,record) => (
                                            <a href={JSON.parse(record.bannerUrl).cn} target='_blank'>{JSON.parse(record.bannerUrl).cn}</a>
                                        )} /> */}
                                        <Column title='banner图链接' dataIndex='bannerUrl' key='bannerUrl' render={(text, record) => (
                                            <a href='javascript:void(0);' onClick={() => this.onViewLink(JSON.parse(record.bannerUrl), 1)} >查看链接</a>
                                        )} />
                                        {/* <Column  title='跳转链接' dataIndex='linkUrl' key='linkUrl' render={(text,record) => 
                                            <a href={JSON.parse(record.linkUrl).cn} target='_blank'>{JSON.parse(record.linkUrl).cn}</a>
                                        } />  */}
                                        <Column title='跳转链接' dataIndex='linkUrl' key='linkUrl' render={(text, record) => (
                                            <a href='javascript:void(0);' onClick={() => this.onViewLink(JSON.parse(record.linkUrl), 2)} >查看链接</a>
                                        )} />
                                        <Column title='状态' dataIndex='status' render={(text, record) => (
                                            (() => {
                                                return text == 0 ? <span>关闭</span> : <span>开启</span>
                                            })()
                                        )} />
                                        <Column title='上传时间' dataIndex='addTime' render={(text) => {
                                            return text ? moment(text).format(TIMEFORMAT_ss) : '--'
                                        }} />
                                        <Column title='跳转方式' dataIndex='linkType' render={(text) => {
                                            let str = <RadioGroup value={text} disabled>
                                                <Radio value={0}>当前页签</Radio>
                                                <Radio value={1}>新页签</Radio>
                                            </RadioGroup>
                                            return str;
                                        }} />
                                        <Column title='操作' dataIndex='op' key='op' render={(text, record) => (
                                            (<span>
                                                <Button size="small" type="primary" onClick={() => this.onAddEditAdverPhoto(record, 'edit')}>修改</Button>
                                                <Button size="small" type="primary" onClick={() => this.onDelete('del', record)}>删除</Button>
                                            </span>)
                                        )} />
                                    </Table>
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
                    width={width}
                >
                    {this.state.modalHtml}
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange={this.handleInputChange}
                    mid='ADSPM'
                    visible={this.state.googVisibal}
                    onCancel={this.handleGoogleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )
    }
}