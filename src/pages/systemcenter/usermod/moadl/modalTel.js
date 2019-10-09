// 修改手机号弹窗
import Decorator from '../../../decorator'
import { Button, Select, message, Input, Upload, Icon, Divider, Modal } from 'antd'
const { TextArea } = Input;
const { Option } = Select;

@Decorator()
export default class ModalTel extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            userId: '',
            modifyMobile: '',
            remark: '',
            videoUrl: '',
            urls: [],
            detailUser: null,
            pageTabs: true,
            fileList: [],
            videoList: [],
            videoData: {},
            imgData: [],
            tphone: '+86',
            preUserImg: '',
            visible: false,
        }
        this.goofn = () => new Map([
            ['changeTel', () => this.changeTel()], //修改手机号
        ])

    }

    componentDidMount() {
        // let oInput = this.refs.focusInput;
        // oInput.focus();
    }

    // 验证用户编号
    inputDelay = 0
    changeValue = async event => {
        const _this = this;
        this.inputDelay += 1;
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value,
        })
        setTimeout(async () => {
            _this.inputDelay -= 1;
            if (_this.inputDelay === 0) {
                const { userId } = this.state
                const params = { userId }
                if (!userId) {
                    message.warning("用户编号不能为空！")
                    return false
                }
                const result = await this.request({ url: '/userInfo/queryUser', type: 'post', }, params)
                this.setState({
                    mobile: result.list.length ? result.list[0].mobile : '不存在'
                })
            }
        }, 800);
    }




    // 验证手机号码
    inputPhone = 0
    changePhone = async event => {
        const _this = this;
        this.inputPhone += 1;
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value,
        })
        setTimeout(async () => {
            _this.inputPhone -= 1;
            if (_this.inputPhone === 0) {

                const { tphone, modifyMobile } = this.state
                const params = { mobile: tphone + '=' + modifyMobile }
                if (tphone == '+86' || tphone == '+22') {
                    if (!(/^1[3456789]\d{9}$/.test(modifyMobile))) {
                        message.warning("手机号码有误，请重填");
                        return false;
                    }
                    if (!modifyMobile) {
                        message.warning("手机号码不能为空！")
                        return false
                    }
                    const msg = '手机号验证成功'
                    this.request({ url: "/userInfo/queryUserByMobile", type: 'post', msg, isP: true }, params)
                } else {
                    message.warning("请选择正确的国际区号码！");
                    return false;
                }
            }
        }, 800);
    }
    // 确认修改手机号按钮
    changetelBtn = () => {
        const { userId, mobile, modifyMobile, remark, videoUrl, urls } = this.state
        if (!(userId && modifyMobile && remark && videoUrl && urls.length)) {
            message.warning("所有选项不能为空！")
            return false
        }
        this.modalGoogleCode(null, 'changeTel')

    }
    // 修改手机号
    changeTel = async () => {
        const { videoData, userId, mobile, modifyMobile, remark, videoUrl, urls, tphone } = this.state
        const params = {
            userId, mobile, remark,
            modifyMobile: tphone + modifyMobile,
            videoUrl,
            urls: urls.join(",").trim(),
        }
        await this.request({ url: '/userInfo/modifyCustomerMobile', type: 'post', isP: true }, params)
        this.props.toIssue(null, 1)
        this.props.requestTable()
    }


    // 图片上传状态 
    handleChange = (file, _key) => {
        const { fileList } = file;
        const size = fileList.length
        if (size) {
            if (fileList[size - 1].size / 1024 > 300) {
                return false
            }
            fileList[size - 1].status = 'done'
            this.setState({
                [_key]: fileList,
            })

        } else {
            this.setState({
                [_key]: []
            })
        }
    }
    // 视频上传状态 
    handleVideo = (file, _key) => {
        const { fileList } = file;
        const size = fileList.length
        if (size) {
            if (fileList[size - 1].size / 1024 > 3072) {
                return false
            }
            fileList[size - 1].status = 'done'
            this.setState({
                [_key]: fileList,
            })
        }
    }

    // 上传图片回调

    uploadImageCoscb = ({ _key, url }) => {
        const { fileList, urls } = this.state
        const Arr = []
        for (let i = 0; i < fileList.length; i++) {
            Arr.push({ [fileList[i].uid]: url })
        }
        urls.push(url)
        this.setState({
            imgData: Arr,
            urls
        })

    }

    // 上传视频回调
    uploadVideoCoscb = ({ _key, url }) => {
        this.setState({
            videoData: Object.assign({}, this.state.videoData, { [_key]: url })
        }, () => {
            const { videoData } = this.state
            this.setState({
                videoUrl: videoData.videoList || ''
            })
        })
    }
    // 上传样式
    uploadButton = p => (
        <div>
            <Icon type="plus" />
            <div className="ant-upload-text">{p}</div>
        </div>
    )

    
    
    onRemove = (info, _key) => {
        let urlArr = []
        const { imgData } = this.state
        
        const filterArr = imgData.filter((item) => {
            for (let i in item) {
                return i != info.uid
            }
        })
        this.setState({
            imgData:filterArr
        })
        if (filterArr.length) {
            filterArr.forEach((item) => {
                for (let index in item) {
                    urlArr.push(item[index])
                }
            })
            this.setState({
                urls: urlArr
            })
        } else {
            this.setState({
                urls: []
            })
        }
    }
    onRemoveVideo = (info, _key) => {
        this.setState({
            [_key]: [],
            videoData: {},
        })
    }



    //图片上传预览
    handlePreview = async (file, _key) => {
        this.setState({
            preUserImg: file.url || file.thumbUrl,
            // preUserImg: file.url || file.preview,
            visible: true,
        })
    }
    // 弹框取消
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }
    seeUserInfo = () => {
        const { userId } = this.state
        if (userId) {
            this.props.toIssue({ userId, number: 2 }, 3)
        } else {
            message.warning("用户编号为空，不能请求！")
        }
    }

    render() {
        const { preUserImg, visible, userId, mobile, modifyMobile, remark, fileList, videoUrl, videoList, urls, tphone } = this.state
        return (
            <div className="x_panel">
                <div className="x_content">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="row">
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">用户编号：</label>
                                    <div className="col-sm-3" style={{ width: 230 }}>
                                        <input type="text" className="form-control" name="userId" value={userId}
                                            // onChange={this.handleInputChange} 
                                            onChange={this.changeValue}
                                            onBlur={this.requestTable}
                                        // ref='focusInput'
                                        />
                                    </div>
                                    <div className="col-sm-3">
                                        <Button type="primary" onClick={this.seeUserInfo}>查看用户信息</Button>
                                    </div>

                                </div>
                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-4 control-label">当前手机号：</label>
                                    <div className="col-sm-3" style={{ lineHeight: 2.5 }}>
                                        {mobile}
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-4 control-label">修改手机号：</label>
                                    <div className="col-sm-3" style={{ width: 120, marginRight: 20, float: 'left' }}>
                                        <Select value={tphone} style={{ width: 120 }} onChange={(v) => this.onSelectChoose(v, 'tphone')}>
                                            <Option value="">请选择国际码</Option>
                                            <Option value="+86">+86</Option>
                                            <Option value="+22">+22</Option>
                                        </Select>
                                    </div>
                                    <div className="col-sm-3">
                                        <input ref='phoneValue' type="text" className="form-control" name="modifyMobile" value={modifyMobile} onChange={this.changePhone} />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-4  control-label" style={{ width: 160 }}>用户身份认证资料上传：</label>
                                    <div className="col-sm-8">
                                        <Upload
                                            fileList={fileList}
                                            listType="picture-card"
                                            onRemove={(v) => this.onRemove(v, 'fileList')}
                                            onPreview={(v) => this.handlePreview(v, 'fileList')}
                                            onChange={(v) => this.handleChange(v, 'fileList')}
                                            customRequest={(e) => {
                                                this.uploadImageCos(e.file, this.uploadImageCoscb, 'fileList')
                                            }}
                                        >
                                            {this.uploadButton('上传图片')}
                                        </Upload>
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-4 control-label " style={{ width: 160 }}>用户认证视频资料上传：</label>
                                    <div className="col-sm-8">
                                        <Upload
                                            fileList={videoList}
                                            listType="picture-card"
                                            onRemove={(v) => this.onRemoveVideo(v, 'videoList')}
                                            onPreview={(v) => this.handlePreview(v, 'videoList')}
                                            onChange={(v) => this.handleVideo(v, 'videoList')}
                                            customRequest={(e) => {
                                                this.uploadImageCos(e.file, this.uploadVideoCoscb, 'videoList', 3072)
                                            }}
                                        >
                                            {videoList.length >= 1 ? null : this.uploadButton('上传视频')}
                                        </Upload>
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-4 control-label">备注：</label>
                                    <div className="col-sm-6">
                                        <TextArea name="remark" value={remark} onChange={this.handleInputChange} placeholder="请输入内容" autosize={{ minRows: 5, maxRows: 8 }} />
                                    </div>
                                </div>
                            </div>
                            <Divider style={{ float: 'left', marginTop: 15, marginBottom: 15 }} />
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <Button key="back" type="more" onClick={() => this.props.toIssue(null, 1)}>取消</Button>
                                    <Button key="submit" type="more" onClick={this.changetelBtn}>确定修改</Button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal visible={visible} footer={null} onCancel={this.handleCancel}>
                    <img alt="example" style={{ width: '100%' }} src={preUserImg} />
                </Modal>
            </div>

        )
    }
}




