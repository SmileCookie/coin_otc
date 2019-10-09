import Decorator from 'DTPath'
import { SeOp } from '../../../../components/select/asyncSelect'
import { PAGEINDEX, PAGESIZE, SELECTWIDTH, PAGRSIZE_OPTIONS20, TIME_PLACEHOLDER, TIMEFORMAT_ss, SHOW_TIME_DEFAULT } from 'Conf'
import { Radio, Select, Checkbox, Button, DatePicker, Upload, Icon, Input, message, Modal } from 'antd'
const { TextArea } = Input
const RadioGroup = Radio.Group;
const { MonthPicker, RangePicker, } = DatePicker;
const _limitVideo = 50000

let _cardTypes = {
    1:'身份证',
    2:'护照'
}
@Decorator()
export default class ModalManuallyModify extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            frontP: [],
            negP: [],
            hanP: [],
            video: [],
            time: [],
            user: {},
            realName: '',
            cardId: '',
            cardType: '',
            changeDec: '',
            fileList: [],
            loadImg: '',
            frontalImg: '',
            backImg: '',
            addVideo: '',
            userImg: {},
            countryName: '',
            preUserImg: ''
        }
        this.state = {
            ...this.defaultState,
            previewVisible: false
        }
    }
    componentDidMount() {
        this.setProps(this.props)
    }
    componentWillReceiveProps(nextProps) {
        this.setProps(nextProps)
    }
    setProps = (user) => {
        this.setState({ ...this.defaultState })
        const { realName, cardId, cardType, countryName, startDate, endDate, changeDec, authId } = user.rsu;
        const { id, userId } = user.detailUser
        this.setState({ user, realName, cardId, cardType, countryName, time: startDate || endDate ? [moment(startDate), moment(endDate)] : [], changeDec, id, authId, userId })
    }
    uploadImageCoscb = ({ _key, url }) => {
        this.setState({
            userImg: Object.assign({}, this.state.userImg, { [_key]: url })
        })
    }
    // 图片上传状态 
    handleChange = (file, _key) => {
        const { fileList } = file;
        const size = fileList.length
        let _limit = _key == 'video' ? _limitVideo : void 0;
        if (!this.limitUpSize(fileList[size - 1].size, _limit)) return false;
        fileList[size - 1].status = 'done'
        this.setState({
            [_key]: fileList,
        })
    }
    onRemove = (info, _key) => {
        this.setState({
            [_key]: []
        })
    }
    //图片上传预览
    handlePreview(file, _key) {
        this.setState({
            preUserImg: file.url || file.thumbUrl,
            previewVisible: true,
        })
    }
    uploadButton = p => (
        <div>
            <Icon type="plus" />
            <div className="ant-upload-text">{p}</div>
        </div>
    );
    onSave = async () => {
        const { realName, cardId, cardType, countryName, startDate, endDate, changeDec, frontP, negP, hanP, video, time, userImg, id, authId, userId } = this.state
        if (!frontP.length) {
            message.warning(_cardTypes[cardType] + '正面照片不能为空！')
            return false
        }
        if (cardType == 1 && !negP.length) {
            message.warning(_cardTypes[cardType] + '反面照片不能为空！')
            return false
        }
        if (!hanP.length) {
            message.warning(_cardTypes[cardType] + '手持照片不能为空！')
            return false
        }
        if (!video.length) {
            message.warning('视频不能为空！')
            return false
        }
        await this.request({ url: '/storeAuth/updAuth', type: 'post', isP: true }, {
            startDate: moment(time[0]).format(TIMEFORMAT_ss),
            endDate: moment(time[1]).format(TIMEFORMAT_ss),
            realName, cardId, cardType, countryName, changeDec, id: authId, userId,
            frontalImg: userImg.frontP || '',
            backImg: userImg.negP || '',
            loadImg: userImg.hanP || '',
            addVideo: userImg.video || ''
        })
        this.props.handleCancel()
        this.props.getUser()

    }
    handleCancel = () => { this.setState({ previewVisible: false }) }
    render() {
        const { frontP, negP, hanP, video, time, user, rsu, realName, cardId, cardType, countryName, changeDec, previewVisible, preUserImg } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label ">用户编号：</label>
                        <div className="col-sm-9">
                            {rsu.userId || '--'}
                            {/* <input type="text" className="form-control" name="market" value={''} onChange={this.inputChange} /> */}
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">姓名：<i>*</i></label>
                        <div className="col-sm-8 ">
                            <input type="text" className="form-control" name="realName" value={realName || ''} onChange={this.handleInputChange} />
                            {/* <div className="left col-sm-5 sm-box"><input type="text" placeholder='姓氏' className="form-control" name="firstName" value={firstName} onChange={this.handleInputChange} /></div>
                            <div className="left line34">-</div>
                            <div className="left col-sm-5 sm-box"><input type="text"placeholder='名字' className="form-control" name="lastName" value={lastName} onChange={this.handleInputChange} /></div> */}
                        </div>
                    </div>
                </div>
                <SeOp title='国家' value={countryName} ops={this.props._userType || {}} onSelectChoose={v => this.onSelectChoose(v, 'countryName')} colmg={true} required={true} />
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label ">证件类型:<i>*</i></label>
                        <div className="col-sm-9">
                            <RadioGroup name='cardType' defaultValue='1' onChange={this.handleInputChange} value={cardType}>
                                <Radio value={'1'}>身份证</Radio>
                                <Radio value={'2'}>护照</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label ">证件号码：<i>*</i></label>
                        <div className="col-sm-9">
                            <input type="text" className="form-control" name="cardId" value={cardId} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">有效期：</label>
                        <div className="col-sm-8">
                            <RangePicker
                                showTime={{
                                    defaultValue: SHOW_TIME_DEFAULT
                                }}
                                format={TIMEFORMAT_ss}
                                placeholder={TIME_PLACEHOLDER}
                                onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'time')}
                                value={time}
                            />
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group form-group-heighAuto">
                        <label className="col-sm-1 control-label">证件照片：<i>*</i></label>
                        <div className="col-sm-3">
                            <Upload
                                listType="picture-card"
                                fileList={frontP.slice(-1)}
                                onRemove={(v) => this.onRemove(v, 'frontP')}
                                onPreview={(v) => this.handlePreview(v, 'frontP')}
                                onChange={(v) => this.handleChange(v, 'frontP')}
                                customRequest={(e) => {
                                    this.uploadImageCos(e.file, this.uploadImageCoscb, 'frontP')
                                }}
                            >
                                {frontP.length >= 1 ? null : this.uploadButton(_cardTypes[cardType] + '正面照片')}
                            </Upload>

                        </div>
                        {cardType == '1'&&<div className="col-sm-3">
                            <Upload
                                listType="picture-card"
                                fileList={negP.slice(-1)}
                                onRemove={(v) => this.onRemove(v, 'negP')}
                                onPreview={(v) => this.handlePreview(v, 'negP')}
                                onChange={(v) => this.handleChange(v, 'negP')}
                                customRequest={(e) => {
                                    this.uploadImageCos(e.file, this.uploadImageCoscb, 'negP')
                                }}
                            >
                                {negP.length >= 1 ? null : this.uploadButton(_cardTypes[cardType] + '反面照片')}
                            </Upload>

                        </div>}
                        <div className="col-sm-3">
                            <Upload
                                listType="picture-card"
                                fileList={hanP.slice(-1)}
                                onRemove={(v) => this.onRemove(v, 'hanP')}
                                onPreview={(v) => this.handlePreview(v, 'hanP')}
                                onChange={(v) => this.handleChange(v, 'hanP')}
                                customRequest={(e) => {
                                    this.uploadImageCos(e.file, this.uploadImageCoscb, 'hanP')
                                }}
                            >
                                {hanP.length >= 1 ? null : this.uploadButton(_cardTypes[cardType] + '手持照片')}
                            </Upload>

                        </div>

                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group form-group-heighAuto">
                        <label className="col-sm-1 control-label">证件视频：<i>*</i></label>
                        <div className="col-sm-8">
                            <Upload
                                // listType="picture-card"
                                fileList={video.slice(-1)}
                                onRemove={(v) => this.onRemove(v, 'video')}
                                // onPreview={(v) => this.handlePreview(v, 'video')}
                                onChange={(v) => this.handleChange(v, 'video')}
                                customRequest={(e) => {
                                    this.uploadImageCos(e.file, this.uploadImageCoscb, 'video', _limitVideo)
                                }}
                            >
                                {video.length >= 1 ? null : <Button><Icon type="upload" /> 上传视频</Button>}
                            </Upload>

                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label ">变更说明：<i>*</i></label>
                        <div className="col-sm-9">
                            <TextArea name="changeDec" rows={4} value={changeDec} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className='col-md-12 col-sm-12 col-xs-12 line marbot10'></div>
                <div className="col-md-4 col-sm-4 col-xs-4 right">
                    <div className="right">
                        <Button key="back" onClick={this.props.handleCancel}>取消</Button>
                        <Button key="submit" type="more" onClick={this.onSave} >保存修改</Button>
                    </div>
                </div>
                <Modal visible={previewVisible} footer={null} onCancel={this.handleCancel}>
                    <img alt="example" style={{ width: '100%' }} src={preUserImg} />
                </Modal>
            </div>
        )
    }
}