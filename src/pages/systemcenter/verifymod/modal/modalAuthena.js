import React from 'react'
import moment from 'moment'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { TIMEFORMAT, DOMAIN_VIP, PAGEINDEX, PAGESIZE } from '../../../../conf'
import { Modal, Radio, Select, Button, message, Card } from 'antd'
import { Player } from 'video-react'
const ButtonGroup = Button.Group;
const RadioGroup = Radio.Group;
const Option = Select.Option;

export default class ModalAuthena extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            userId: '',
            value: '1',
            resVal: "",
            visible: false,
            previewImage: "",
            userName: "",
            authTimes: "",
            areaName: "",
            cardTypeName: "",
            cardId: "",
            realName: "",
            submitTime: "",
            status: '',
            statusName: "",
            loadImg: "",
            frontalImg: "",
            backImg: "",
            endDate: "",
            startDate: "",
            countryName: "",
            isok: '0',
            tableList: [],
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            idCardPhoto: '',
            imgWidth: 1000,
            reason: '',
            cardType: '',
            checkImg: '',
            checkResult: '',
            readRandom: '',//随机数
            videoImg: null,//视频


        }
        this.onImgEnlarge = this.onImgEnlarge.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handleImgCancel = this.handleImgCancel.bind(this)
        this.imgSize = this.imgSize.bind(this)
    }
    componentDidMount() {
        console.log(1)
        const { userId, userName, authTimes, countryName, startDate, endDate, areaName, cardTypeName, cardId, realName, submitTime, statusName, loadImg, frontalImg, backImg, cardType, reason, status, checkImg, checkResult
            , readRandom, videoImg } = this.props.item
        this.setState({
            userId,
            userName,
            authTimes,
            areaName,
            cardTypeName,
            cardId,
            realName,
            submitTime,
            statusName,
            loadImg,
            frontalImg,
            backImg,
            endDate,
            startDate,
            countryName,
            cardType,
            reason,
            status,
            checkImg,
            checkResult,
            readRandom,//随机数
            videoImg,//视频

        })
    }
    componentWillReceiveProps(nextProps) {
        console.log(nextProps)
        const { userId, userName, authTimes, countryName, areaName, startDate, endDate, cardTypeName, cardId, realName, submitTime, statusName, loadImg, frontalImg, backImg, cardType, status, reason, checkImg, checkResult,
            readRandom, videoImg } = nextProps.item
        this.setState({
            userId,
            userName,
            authTimes,
            areaName,
            cardTypeName,
            cardId,
            realName,
            submitTime,
            statusName,
            loadImg,
            frontalImg,
            backImg,
            startDate,
            endDate,
            countryName,
            cardType,
            reason,
            status,
            checkImg,
            checkResult,
            readRandom,//随机数
            videoImg,//视频
        })
    }

    //图片放大缩小
    imgSize(type) {
        const { imgWidth } = this.state
        console.log(type)
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


    handleChange(value) {
        this.setState({
            resVal: value
        })
        this.props.onChangeReason(value)
    }
    //图片放大
    onImgEnlarge(src) {
        this.setState({
            imgWidth: 1000,
            visible: true,
            previewImage: src
        })
    }
    handleCancel() {
        this.setState({
            visible: false,
            loadImg: '',
            frontalImg: ''
        })
    }
    handleImgCancel() {
        this.setState({
            visible: false
        })
    }

    render() {
        const { userName, authTimes, tableList, imgWidth, resVal, isok, areaName, idCardPhoto, cardTypeName, cardId, startDate, endDate, countryName, realName, submitTime, statusName, loadImg, frontalImg,
             pageIndex, pageSize, backImg, cardType, reason, status, checkImg, checkResult,readRandom,videoImg } = this.state
        return (

            <div className="col-md-12 col-sm-12 col-xs-12">
                <Card>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">用户名：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {userName || ""}
                            </div>
                        </div>
                    </div>
                    {this.props.degree !== 2 && <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">认证次数：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {authTimes}
                            </div>
                        </div>
                    </div>}
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">认证地区：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {areaName}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">认证国家：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {countryName}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">认证类型：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {cardTypeName}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">证件号码：</label>
                            <div className="col-sm-9">
                                <div className="lineHeight_34">
                                    {cardId}
                                </div>
                                {this.props.tab == 5 &&
                                    <div className="col-sm-2 left">
                                        <Button type="primary" onClick={this.validityCheck}>有效性检查</Button>
                                    </div>}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">真实姓名：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {realName}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">认证时间：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {submitTime ? moment(submitTime).format(TIMEFORMAT) : ''}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">证件有效开始：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {startDate}
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">证件有效截止：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {endDate}
                            </div>
                        </div>
                    </div>
                    {checkResult &&
                        <div className="col-md-6 col-sm-6 col-xs-6 color_128fdc">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">检查结果：</label>
                                <div className="col-sm-9 lineHeight_34">
                                    {checkResult == "1" ? "证件号匹配" : "证件号不匹配"}
                                </div>
                            </div>
                        </div>
                    }
                    {/* <div className="col-md-6 col-sm-6 col-xs-6 color_128fdc">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">随机数：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {readRandom}
                            </div>
                        </div>
                    </div> */}
                </Card>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-md-12 col-sm-12 col-xs-12 control-label" style={{ width: '100%' }}>用户上传图片：</label>
                        <div className="col-md-3 col-sm-3 col-xs-3">
                            <img alt="example" style={{ width: '100%', height: '150px' }} src={frontalImg} onClick={() => this.onImgEnlarge(frontalImg)} />
                        </div>
                        {
                            cardType == '1' ?
                                <div className="col-md-3 col-sm-3 col-xs-3">
                                    <img alt="example" style={{ width: '100%', height: '150px' }} src={backImg} onClick={() => this.onImgEnlarge(backImg)} />
                                </div> : ''
                        }

                        <div className="col-md-3 col-sm-3 col-xs-3">
                            <img alt="example" style={{ width: '100%', height: '150px' }} src={loadImg} onClick={() => this.onImgEnlarge(loadImg)} />
                        </div>
                        {
                            idCardPhoto ? <div className="col-md-3 col-sm-3 col-xs-3">
                                <img alt="example" style={{ width: '100%', height: '150px' }} src={"data:image/png;base64," + idCardPhoto} onClick={() => this.onImgEnlarge(loadImg)} />
                            </div> : ''
                        }
                        {
                            checkImg ? <div className="col-md-3 col-sm-3 col-xs-3">
                                <img alt="example" style={{ width: '100%', height: '150px' }} src={checkImg} onClick={() => this.onImgEnlarge(checkImg)} />
                            </div> : ''
                        }

                    </div>
                </div>
                {/* 
                    视频播放
                */}
                {/* <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-md-12 col-sm-12 col-xs-12 control-label" style={{ width: '100%' }}>用户上传视频：</label>
                        <div className='col-md-4 col-sm-4 col-xs-4'>
                            <Player
                            fluid={false}
                                src={videoImg}
                                playsInline={true}
                                aspectRatio='auto'
                                width={360}
                                height={170}
                            />
                        </div>
                    </div>
                </div> */}
                {
                    this.props.tab !== 5 && <div className="col-md-6 col-sm-6 col-xs-6 marTop">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">审核状态：</label>
                            <div className="col-sm-9">
                                <input type="text" className="form-control" value={statusName} readOnly />
                            </div>
                        </div>
                    </div>
                }
                {
                    status == '7' && <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">原因：</label>
                            <div className="col-sm-9 lineHeight_34">
                                {(() => {
                                    switch (reason) {
                                        case '8':
                                            return "图像经过处理"
                                            break;
                                        case '9':
                                            return '图像不清晰'
                                            break;
                                        case '10':
                                            return '证件图像类型不符'
                                            break;
                                        case '11':
                                            return '平台仅支持满16周岁的用户进行交易'
                                            break;
                                        default:
                                            break;
                                    }
                                })()}
                            </div>
                        </div>
                    </div>
                }

                <Modal
                    visible={this.state.visible}
                    footer={
                        <ButtonGroup>
                            <Button icon="plus" onClick={() => this.imgSize(1)}></Button>
                            <Button icon="minus" onClick={() => this.imgSize(2)}></Button>
                        </ButtonGroup>
                    }
                    wrapClassName="img-box"
                    width={imgWidth + 'px'}
                    onCancel={this.handleImgCancel}
                    maskClosable={false}
                >
                    <img alt="example" style={{ width: '100%' }} src={this.state.previewImage} />
                </Modal>
            </div>
        )
    }

}




























