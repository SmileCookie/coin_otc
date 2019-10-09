import React from 'react'
import axios from './../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { toThousands } from './../../../../utils'
import { PAGEINDEX, PAGESIZE, DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT } from '../../../../conf'
import { Pagination, Button, message, Modal, Tabs, Icon, Input } from 'antd'
import GoogleCode from '../../../common/modal/googleCode'
import { Player } from 'video-react';
//import { Throttle } from 'lodash-decorators'
import throttle from 'lodash.throttle';
import { combineReducers } from 'redux';
const ButtonGroup = Button.Group;
const { TextArea } = Input;
const TabPane = Tabs.TabPane;


export default class ModalAppealdetail extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            id: '',
            orderNo: '',
            tableList: [],
            buyMemo: '',
            buyComplainVo: [],
            buyUserId: '',
            recordNo: '',
            sellMemo: '',
            sellComplainVo: [],
            sellUserId: '',
            checkDesc: '',
            sellQualification: '',
            buyQualification: '',
            checkResultName: '',
            statusName: '',
            buyUserName: '',
            buyRealName: '',
            buyMobile: '',
            sellUserName: '',
            sellRealName: '',
            sellMobile: '',
            bankNumber: '',
            bankOpeningBank: '',
            buyemail: '',
            sellemail: '',
            alipayAccount: '',
            buyPicture: '',
            buyVideoList: '',
            sellPicture: '',
            sellVideoList: '',
            status: '',
            acceptbtn: false,
            previewImage: '',
            imgWidth: '',
            visible: false,
            modalHtml: '',
            loading: false,
            complainId: '',
            tovisible: false,
            totitle: '',
            towidth: '',
            setcheckDesc: '',
            wintype: '',
            bankNumber: '',
            limitBtn: []

        }
        this.changeStarus = this.changeStarus.bind(this)
        this.onImgEnlarge = this.onImgEnlarge.bind(this)
        this.imgSize = this.imgSize.bind(this)
        this.handleImgCancel = this.handleImgCancel.bind(this)
        this.winDealWith = this.winDealWith.bind(this)
        this.beforewinDealWith = this.beforewinDealWith.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.tohandleCancel = this.tohandleCancel.bind(this)
        // this.newOpen = this.newOpen.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.handleGoogleCancel = this.handleGoogleCancel.bind(this)

    }
    componentDidMount() {
        this.setState(
            {
                id: '',
                orderNo: '',
                tableList: [],
                buyMemo: '',
                buyComplainVo: [],
                buyUserId: '',
                recordNo: '',
                sellMemo: '',
                sellComplainVo: [],
                sellUserId: '',
                checkDesc: '',
                checkResultName: '',
                statusName: '',
                buyUserName: '',
                buyRealName: '',
                buyMobile: '',
                sellUserName: '',
                sellRealName: '',
                sellMobile: '',
                bankNumber: '',
                bankOpeningBank: '',
                buyemail: '',
                sellemail: '',
                alipayAccount: '',
                buyPicture: '',
                buyVideoList: '',
                sellPicture: '',
                sellVideoList: '',
                status: '',
                bankNumber: '',
                complainId: this.props.complainId,
                orderNo: this.props.id,
                limitBtn: this.props.limitBtn
            }, () => {
                this.requestTable()
                this.requestDetail()
                


            }
        )

    }




    componentWillReceiveProps(nextProps) {
        this.setState(
            {
                id: '',
                orderNo: '',
                tableList: [],
                buyMemo: '',
                buyComplainVo: [],
                buyUserId: '',
                recordNo: '',
                sellMemo: '',
                sellComplainVo: [],
                sellUserId: '',
                checkDesc: '',
                checkResultName: '',
                statusName: '',
                buyUserName: '',
                buyRealName: '',
                buyMobile: '',
                sellUserName: '',
                sellRealName: '',
                sellMobile: '',
                bankNumber: '',
                bankOpeningBank: '',
                buyemail: '',
                sellemail: '',
                alipayAccount: '',
                buyPicture: '',
                buyVideoList: '',
                sellPicture: '',
                sellVideoList: '',
                status: '',
                bankNumber: '',
                complainId: nextProps.complainId,
                orderNo: nextProps.id,
                limitBtn: nextProps.limitBtn
            }, () => {
                this.requestTable()
                this.requestDetail()
                // console.log(this.state.limitBtn)
            }
        )
    }
    changeStarus() {
        const { id } = this.state
        this.setState({
            loading: true
        })
        axios.get(DOMAIN_VIP + '/otcComplain/dealWith', {
            params: {
                id
            }
        }).then(res => {
            const result = res.data;

            if (result.code == 0) {
                message.success(result.msg);
                this.setState({
                    acceptbtn: true,
                    loading: false
                }, () => {
                    this.requestTable()
                    this.requestDetail()
                })
            }
        })

    }
    //图片放大缩小
    imgSize(type) {
        const { imgWidth } = this.state
        if (type == 1) {
            if (imgWidth == 1600) {
                message.warning('已放至最大')
            } else {
                this.setState({
                    imgWidth: imgWidth + 100
                })
            }
        } else {
            if (imgWidth == 300) {
                message.warning('已缩放至最小')

            } else {
                this.setState({
                    imgWidth: imgWidth - 100
                })
            }
        }
    }
    //图片放大
    onImgEnlarge(type, src) {

        let changeWidth = type == 'pic' ? 600 : 500
        this.setState({
            imgWidth: changeWidth,
            visible: true,
            previewImage: src,

        }, () => {
            let showHtml = type == 'pic' ? <img alt="example" style={{ width: '100%' }} src={this.state.previewImage} /> : <Player
                playsInline
                poster={this.state.previewImage + '?vframe/jpg/offset/0'}
                src={this.state.previewImage} />


            this.setState({
                modalHtml: showHtml
            })
           
        }
        )
    }
    //图片弹窗关闭
    handleImgCancel() {
        this.setState({
            visible: false
        })
    }
    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        if (name === 'setcheckDesc') {
            if (value.length >= 200) {
                message.warning('输入文字过多！')
            }
        }
        this.setState({
            [name]: value
        });
    }
    //胜诉弹窗关闭
    tohandleCancel() {
        this.setState({
            tovisible: false
        })
    }
    //胜诉判定弹窗
    beforewinDealWith(winUserId, type) {
        let wintype = type == 'buywin' ? '买方胜' : '卖方胜'
        this.footer = [
            <Button key="back" onClick={this.tohandleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.modalGoogleCode(winUserId, 'sure')}>确定</Button>
        ]

        this.setState({
            tovisible: true,
            totitle: "申诉判定",
            towidth: '600px',
            wintype,
            setcheckDesc: ''
        })
    }

    //胜诉判定确定
    winDealWith(winUserId) {
        const { id, buyMobile, sellMobile, setcheckDesc } = this.state
        axios.get(DOMAIN_VIP + '/otcComplain/winDealWith', {
            params: {
                id, winUserId, buyMobile, sellMobile, checkDesc: setcheckDesc
            }
        }).then(res => {
            const result = res.data;

            if (result.code == 0) {

                setTimeout(()=>{
                    this.setState({
                        acceptbtn: false,
                        tovisible: false
                    })
                    this.requestDetail()
                    this.requestTable()
                    this.props.handleCancel()
                    this.props.update()
                },300)
                message.success(result.msg);
                

            }else{
                message.error(result.msg)
            }
        })
    }
    requestTable() {
        const { orderNo } = this.state
        axios.post(DOMAIN_VIP + '/otcComplain/orderList', qs.stringify({
            recordNo: orderNo
        })).then(res => {
            const result = res.data;

            if (result.code == 0) {
                let tableList = []
                if (result.data) {
                    tableList.push(result.data)
                }
                this.setState({
                    tableList
                })
            }
        })
    }

// 判断买卖方资质
    
qualification(){
    const buyQ=this.props.item.buyQualification;
    const sellQ=this.props.item.sellQualification;
    

    if(!buyQ){
        this.setState({
            buyQualification:'未激活'
        })
    }else if( buyQ==0){
        this.setState({
            buyQualification:'未激活'
        })
    }else if (buyQ==1){
        this.setState({
            buyQualification:'普通用户'
        })
    }else if(buyQ==2){
        this.setState({
            buyQualification:'实名认证'
        })
    }else{
        this.setState({
            buyQualification:'商家'
        })
    }

    if(!sellQ){
        this.setState({
            sellQualification:'未激活'
        })
    }else if( sellQ==0 ){
        this.setState({
            sellQualification:'未激活'
        })
    }else if (sellQ==1){
        this.setState({
            sellQualification:'普通用户'
        })
    }else if(sellQ==2){
        this.setState({
            sellQualification:'实名认证'
        })
    }else{
        this.setState({
            sellQualification:'商家'
        })
    }





}


    requestDetail() {
        const { orderNo, complainId } = this.state
        axios.post(DOMAIN_VIP + '/otcComplain/complainList', qs.stringify({
            recordNo: orderNo,
            complainId,

        })).then(res => {
            const result = res.data;
            //console.log(result);
            if (result.code == 0) {
                const { id, buyMemo, buyComplainVo, buyUserId, status, recordNo, sellMemo, sellComplainVo, sellUserId, checkDesc, checkResultName, statusName, buyUserName, buyRealName, buyMobile, sellUserName, sellRealName, sellMobile, bankNumber, bankOpeningBank, buyemail, sellemail, alipayAccount, buyPicture, buyVideoList, sellPicture, sellVideoList } = result.data
               
                this.qualification()
                this.setState({
                    id, buyMemo, buyComplainVo, buyUserId, recordNo, status, sellMemo, sellComplainVo, sellUserId, checkDesc, checkResultName, statusName, buyUserName, buyRealName, buyMobile, sellUserName, sellRealName, sellMobile, bankNumber, bankOpeningBank, buyemail, sellemail, alipayAccount, buyPicture, buyVideoList, sellPicture, sellVideoList
                })
                if (status == 1) {
                    this.setState({
                        acceptbtn: true
                    })
                } else {
                    this.setState({
                        acceptbtn: false
                    })
                }
            }
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
                if (googleType == 'sure') {
                    this.winDealWith(googleSpace)
                }
               
            } else {
                message.error(result.msg)
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
        const { tableList, buyMemo, buyUserId, sellQualification, buyQualification, status, recordNo, sellMemo, acceptbtn, sellUserId, checkDesc, checkResultName, statusName, buyUserName, buyRealName, buyMobile, sellUserName, sellRealName, sellMobile, bankNumber, bankOpeningBank, buyemail, sellemail, alipayAccount, buyPicture, buyVideoList, sellPicture, sellVideoList, imgWidth, visible, previewImage, modalHtml, tovisible, totitle, towidth, setcheckDesc, wintype, limitBtn } = this.state
        return (
            <div className="row">
                <div className="x_panel">
                    <div className="su_title">订单信息</div>
                    <div className="x_content">
                        <div className="table-responsive">
                            <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                                <thead>
                                    <tr className="headings">
                                        <th className="column-title">序号</th>
                                        <th className="column-title must_153px">订单编号</th>
                                        <th className="column-title">创建时间</th>
                                        <th className="column-title">货币类型</th>
                                        <th className="column-title">订单状态</th>
                                        <th className="column-title">交易类型</th>
                                        <th className="column-title">买方ID</th>
                                        <th className="column-title">卖方ID</th>
                                        <th className="column-title">单价(CNY)</th>
                                        <th className="column-title">交易数量</th>
                                        <th className="column-title">交易金额(CNY)</th>
                                        <th className="column-title">手续费</th>
                                        <th className="column-title">广告编号</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {
                                        tableList.length > 0 ?
                                            tableList.map((item, index) => {
                                                return (
                                                    <tr key={index}>
                                                        <td>{index + 1}</td>
                                                        <td> <a href="javascript:void(0)" onClick={() => this.props.newOpen(item.recordNo,'recordNo', {name: "订单管理", url: "/tradecenter/ordermod/orderManage", key: 700100010062})}>{item.recordNo}</a></td>
                                                        <td>{moment(item.coinTime).format(TIMEFORMAT)}</td>
                                                        <td>{item.coinTypeName}</td>
                                                        <td>{item.statusName}</td>
                                                        <td>{item.typeName}</td>
                                                        <td>{item.buyUserId}</td>
                                                        <td>{item.sellUserId}</td>
                                                        <td>{toThousands(item.coinPrice)}</td>
                                                        <td>{item.coinNumber}</td>
                                                        <td>{toThousands(item.sumAmount)}</td>
                                                        <td>{item.freeAmount ? toThousands(item.freeAmount) : ''}</td>
                                                        <td><a href="javascript:void(0)" onClick={() => this.props.newOpen(item.orderNo,'orderNo',{key: 200300030000,name: "广告管理",url: "/tradecenter/advertmod/advertManage"})}>{item.orderNo}</a></td>
                                                    </tr>
                                                )
                                            })
                                            : <tr className="no-record"><td colSpan="13">暂无数据</td></tr>
                                    }
                                </tbody>
                            </table>
                        </div>

                    </div>
                </div>


                {/* <div className="x_panel">
                    申诉结果
                <div className="x_content">
                        <div className="col-mg-12 col-lg-12">
                            申诉状态：{statusName}{status == '0' && <Button type="primary" className="marl20" onClick={this.changeStarus} >受理</Button>}
                        </div>
                        <div className="col-mg-12 col-lg-12">
                            申诉结果：{checkResultName}
                        </div>
                        <div className="col-mg-12 col-lg-12">
                            判定说明：{checkDesc}
                        </div>
                    </div>
                    <Modal visible={this.state.visible} footer={
                        <ButtonGroup>
                            <Button icon="plus" onClick={() => this.imgSize(1)}></Button>
                            <Button icon="minus" onClick={() => this.imgSize(2)}></Button>
                        </ButtonGroup>
                    } wrapClassName="img-box" width={imgWidth + 'px'} onCancel={this.handleImgCancel} maskClosable={false}>
                        {modalHtml}
                    </Modal>
                </div> */}

                <Modal visible={this.state.visible} footer={
                    <ButtonGroup>
                        <Button icon="plus" onClick={() => this.imgSize(1)}></Button>
                        <Button icon="minus" onClick={() => this.imgSize(2)}></Button>
                    </ButtonGroup>
                } wrapClassName="img-box" width={imgWidth + 'px'} onCancel={this.handleImgCancel} maskClosable={false}>
                    {modalHtml}
                </Modal>




                <div className="col-md-12 col-sm-12 col-xs-12 pad_zone">
                    <div className="x_panel">
                        <div className="su_title">卖方申诉</div>
                        <div className="x_content">
                            <div className="x_panel su_left">
                                <div className="x_content modal_show">
                                    <div className="col-mg-12 col-lg-12">
                                        <span>用户编号：</span>{sellUserId}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>用户昵称：</span>{sellUserName}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>用户资质：</span>{sellQualification}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>真实姓名：</span>{sellRealName}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>联系电话：</span>{sellMobile}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>电子邮箱：</span>{sellemail}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>银行账户：</span>{bankNumber}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>支付宝：</span>{alipayAccount}
                                    </div>
                                </div>
                                {limitBtn.indexOf('winDealWith') > -1 ? (acceptbtn && <Button type="primary" className="marcen" onClick={() => this.beforewinDealWith(sellUserId, 'sellwin')}>胜诉</Button>) : ''}

                            </div>
                            <div className="su_right">

                                <Tabs>

                                    {this.state.sellComplainVo.length > 0 ?
                                        this.state.sellComplainVo.map((value, index) => {
                                            return (
                                                <TabPane tab={index == 0 ? '申诉资料' : `补充${index}`} key={index}>
                                                    <div className="su_tab">
                                                        <div className="su_title">申诉说明</div>
                                                        <div className="x_panel b_none">
                                                            <div className="x_content">
                                                                <div className="col-mg-12 col-lg-12">
                                                                    {value.sellMemo}
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div className="su_title">申诉截图</div>
                                                        <div className="x_panel b_none">
                                                            <div className="x_content">
                                                                <div className="col-mg-12 col-lg-12">
                                                                    {value.sellPicture.length > 0 ?
                                                                        value.sellPicture.map((data, index) => {
                                                                            return (
                                                                                <div className="col-md-3 col-sm-3 col-xs-3 marTop" key={index}>
                                                                                    <img alt="example" style={{ width: '100%', height: '150px' }} src={data} onClick={() => this.onImgEnlarge('pic', data)} />
                                                                                </div>
                                                                            )

                                                                        }) : ''
                                                                    }
                                                                </div>

                                                            </div>
                                                        </div>
                                                        <div className="su_title">申诉视频</div>
                                                        <div className="x_panel b_none">
                                                            <div className="x_content">
                                                                <div className="col-mg-12 col-lg-12">
                                                                    {
                                                                        value.sellVideoList.length > 0 ?
                                                                            value.sellVideoList.map((d, index) => {
                                                                                return (
                                                                                    <div className="col-md-3 col-sm-3 col-xs-3 marTop" key={index}>
                                                                                        <div>
                                                                                            <Icon type="caret-right" onClick={() => this.onImgEnlarge('mov', d)} />
                                                                                            <img alt="example" style={{ width: '100%', height: '150px' }} src={d + '?vframe/jpg/offset/0'}
                                                                                                onClick={() => this.onImgEnlarge('mov', d)} />
                                                                                        </div>
                                                                                    </div>
                                                                                )
                                                                            }) : ''

                                                                    }
                                                                </div>
                                                            </div>
                                                        </div>

                                                    </div>
                                                </TabPane>
                                            )
                                        })
                                        :
                                        <TabPane tab={'申诉资料'} key={0}>
                                            <div className="su_tab">
                                                <div className="su_title">申诉说明</div>
                                                <div className="x_panel b_none">
                                                    <div className="x_content">
                                                        <div className="col-mg-12 col-lg-12">

                                                        </div>
                                                    </div>
                                                </div>
                                                <div className="su_title">申诉截图</div>
                                                <div className="x_panel b_none">
                                                    <div className="x_content">
                                                        <div className="col-mg-12 col-lg-12">
                                                        </div>

                                                    </div>
                                                </div>
                                                <div className="su_title">申诉视频</div>
                                                <div className="x_panel b_none">
                                                    <div className="x_content">
                                                        <div className="col-mg-12 col-lg-12">
                                                        </div>
                                                    </div>
                                                </div>

                                            </div>
                                        </TabPane>
                                    }

                                </Tabs>

                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12 pad_zone">
                    <div className="x_panel">
                        <div className="su_title">买方申诉</div>
                        <div className="x_content">
                            <div className="x_panel su_left">
                                <div className="x_content modal_show">
                                    <div className="col-mg-12 col-lg-12">
                                        <span>用户编号：</span>{buyUserId}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>用户昵称：</span>{buyUserName}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>用户资质：</span>{buyQualification}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>真实姓名：</span>{buyRealName}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>联系电话：</span>{buyMobile}
                                    </div>
                                    <div className="col-mg-12 col-lg-12">
                                        <span>电子邮箱：</span>{buyemail}
                                    </div>

                                </div>
                                {limitBtn.indexOf('winDealWith') > -1 ? (acceptbtn && <Button type="primary" className="marcen" onClick={() => this.beforewinDealWith(buyUserId, 'buywin')} >胜诉</Button>) : ''}

                            </div>
                            <div className="su_right">
                                <Tabs>
                                    {
                                        this.state.buyComplainVo.length > 0 ?
                                            this.state.buyComplainVo.map((value, index) => {
                                                return (
                                                    <TabPane tab={index == 0 ? '申诉资料' : `补充${index}`} key={index}>
                                                        <div className="su_tab">
                                                            <div className="su_title">申诉说明</div>
                                                            <div className="x_panel b_none">
                                                                <div className="x_content">
                                                                    <div className="col-mg-12 col-lg-12">
                                                                        {value.buyMemo}
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div className="su_title">申诉截图</div>
                                                            <div className="x_panel b_none">
                                                                <div className="x_content">
                                                                    <div className="col-mg-12 col-lg-12">
                                                                        {
                                                                            value.buyPicture.length > 0 ?
                                                                                value.buyPicture.map((data, i) => {
                                                                                    return (
                                                                                        <div className="col-md-3 col-sm-3 col-xs-3 marTop" key={i}>
                                                                                            <img alt="example" style={{ width: '100%', height: '150px' }} src={data} onClick={() => this.onImgEnlarge('pic', data)} />
                                                                                        </div>
                                                                                    )

                                                                                }) : ''

                                                                        }
                                                                    </div>

                                                                </div>
                                                            </div>
                                                            <div className="su_title">申诉视频</div>
                                                            <div className="x_panel b_none">
                                                                <div className="x_content">
                                                                    <div className="col-mg-12 col-lg-12">
                                                                        {
                                                                            value.buyVideoList.length > 0 ?
                                                                                value.buyVideoList.map((x, i) => {

                                                                                    return (
                                                                                        <div className="col-md-3 col-sm-3 col-xs-3 marTop" key={i}>
                                                                                            <div>
                                                                                                <Icon type="caret-right" onClick={() => this.onImgEnlarge('mov', x)} />
                                                                                                <img alt="example" style={{ width: '100%', height: '150px' }}
                                                                                                    src={x + '?vframe/jpg/offset/0'} onClick={() => this.onImgEnlarge('mov', x)} />
                                                                                            </div>
                                                                                        </div>
                                                                                    )

                                                                                }) : ''


                                                                        }
                                                                    </div>
                                                                </div>
                                                            </div>

                                                        </div>
                                                    </TabPane>
                                                )
                                            }) :
                                            <TabPane tab={'申诉资料'} key={0}>
                                                <div className="su_tab">
                                                    <div className="su_title">申诉说明</div>
                                                    <div className="x_panel b_none">
                                                        <div className="x_content">
                                                            <div className="col-mg-12 col-lg-12">

                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div className="su_title">申诉截图</div>
                                                    <div className="x_panel b_none">
                                                        <div className="x_content">
                                                            <div className="col-mg-12 col-lg-12">
                                                            </div>

                                                        </div>
                                                    </div>
                                                    <div className="su_title">申诉视频</div>
                                                    <div className="x_panel b_none">
                                                        <div className="x_content">
                                                            <div className="col-mg-12 col-lg-12">
                                                            </div>
                                                        </div>
                                                    </div>

                                                </div>
                                            </TabPane>
                                    }
                                </Tabs>




                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={tovisible}
                    title={totitle}
                    width={towidth}
                    style={{ top: 60 }}
                    onCancel={this.tohandleCancel}
                    footer={this.footer}
                >
                    <div className='col-md-12 col-sm-12 col-xs-12'>
                        <div className='col-md-12 col-sm-12 col-xs-12'>
                            判定结果：{wintype}
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">判定说明：</label>
                                <div className="col-sm-8">
                                    <TextArea maxLength='200' rows={4} name="setcheckDesc" value={setcheckDesc} onChange={this.handleInputChange} />
                                    <span>最多可输入200个汉字</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </Modal>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.check}
                    handleInputChange={this.handleInputChange}
                    mid='SDFGDERTH'
                    visible={this.state.googVisibal}
                    onCancel={this.handleGoogleCancel}
                    onCreate={this.handleCreate}
                />
            </div >
        )

    }
}