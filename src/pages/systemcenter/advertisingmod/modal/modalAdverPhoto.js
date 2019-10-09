import React from 'react'
import { Input, Radio, Upload, Button, Icon, Tabs, Modal, message } from 'antd'
import { DOMAIN_VIP } from '../../../../conf'
import axios from '../../../../utils/fetch'
const RadioGroup = Radio.Group;
const { TextArea } = Input;
const TabPane = Tabs.TabPane;

export default class ModalAdverPhoto extends React.Component {
    constructor(props) {
        super(props)
        this.default = {
            bannerUrlCN: '',
            bannerUrlEN: '',
            bannerUrlHK: '',
            bannerUrlKR: '',
            bannerUrlJP: '',
            linkUrlCN: '',
            linkUrlEN: '',
            linkUrlHK: '',
            linkUrlKR: '',
            linkUrlJP: '',
            //uploadUrl:`http://up.qiniu.com`,
            
        }
        this.state = {
            status: 0,
            remark: '',
            loading: false,
            previewVisible: false,
            previewImage: '',
            bannerName: '',
            linkType: 0,
            qiniu_host: "https://o4we6sxpt.qnssl.com/",
            ...this.default,
            fileListCN: [{
                uid: -1,
                name: 'xxx.png',
                status: 'done',
                url: ''
            }],
            fileListHK: [{
                uid: -1,
                name: 'xxx.png',
                status: 'done',
                url: ''
            }],
            fileListEN: [{
                uid: -1,
                name: 'xxx.png',
                status: 'done',
                url: ''
            }],
            fileListKR: [{
                uid: -1,
                name: 'xxx.png',
                status: 'done',
                url: ''
            }],
            fileListJP: [{
                uid: -1,
                name: 'xxx.png',
                status: 'done',
                url: ''
            }],
        }
        this.handleChangeCN = this.handleChangeCN.bind(this)
        this.handleChangeHK = this.handleChangeHK.bind(this)
        this.handleChangeEN = this.handleChangeEN.bind(this)
        this.handlePreview = this.handlePreview.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        //this.getQiniuToken = this.getQiniuToken.bind(this)

    }
    componentDidMount() {
        //this.getQiniuToken()
        console.log(this.props.item)
        if (this.props.item) {
            const { bannerUrl, linkUrl, status, bannerName,linkType } = this.props.item;
            let { fileListCN, fileListEN, fileListHK, fileListJP, fileListKR } = this.state;
            fileListCN[0].url = JSON.parse(bannerUrl).cn;
            fileListEN[0].url = JSON.parse(bannerUrl).en;
            fileListHK[0].url = JSON.parse(bannerUrl).hk;

            fileListKR[0].url = JSON.parse(bannerUrl).kr;
            fileListJP[0].url = JSON.parse(bannerUrl).jp;

            // let url = null
            // if(linkUrl.slice(0,3)==='htt'){
            //     url = linkUrl
            //     }else{
            //       url = linkUrl.replace(linkUrl,'http://'+linkUrl);
            //     }
            // if(!this.checkUrl(url)){
            //  message.warning('请输入正确的网址！')
            //  return false
            // }
            // console.log(url)
            this.setState({
                bannerUrlCN: JSON.parse(bannerUrl).cn,
                bannerUrlEn: JSON.parse(bannerUrl).en,
                bannerUrlHK: JSON.parse(bannerUrl).hk,

                bannerUrlKR: JSON.parse(bannerUrl).kr,
                bannerUrlJP: JSON.parse(bannerUrl).jp,

                linkUrlCN: JSON.parse(linkUrl).cn,
                linkUrlEN: JSON.parse(linkUrl).en,
                linkUrlHK: JSON.parse(linkUrl).hk,
                linkUrlKR: JSON.parse(linkUrl).kr,
                linkUrlJP: JSON.parse(linkUrl).jp,
                status: status,
                linkType,
                fileListCN,
                fileListEN,
                fileListHK,
                fileListJP,
                fileListKR,
                bannerName,
            })
        } else {

            this.setState({
                bannerUrlCN: '',
                bannerUrlEN: '',
                bannerUrlHK: '',
                bannerUrlKR: '',
                bannerUrlJP: '',
                linkUrlCN: '',
                linkUrlEN: '',
                linkUrlHK: '',
                linkUrlKR: '',
                linkUrlJP: '',

                status: 0,
                linkType: 0,
                bannerName: "",
                fileListCN: [{
                    uid: -1,
                    name: 'xxx.png',
                    status: 'done',
                    url: ''
                }],
                fileListHK: [{
                    uid: -1,
                    name: 'xxx.png',
                    status: 'done',
                    url: ''
                }],
                fileListEN: [{
                    uid: -1,
                    name: 'xxx.png',
                    status: 'done',
                    url: ''
                }],
                fileListKR: [{
                    uid: -1,
                    name: 'xxx.png',
                    status: 'done',
                    url: ''
                }],
                fileListJP: [{
                    uid: -1,
                    name: 'xxx.png',
                    status: 'done',
                    url: ''
                }],
                // ...this.default
            })
        }


    }
    componentWillReceiveProps(nextProps) {
        console.log(nextProps.item)

        if (nextProps.item) {
            const { bannerUrl, linkUrl, status, bannerName,linkType } = nextProps.item
            let { fileListCN, fileListEN, fileListHK, fileListJP, fileListKR } = this.state
            fileListCN[0].url = JSON.parse(bannerUrl).cn;
            fileListEN[0].url = JSON.parse(bannerUrl).en;
            fileListHK[0].url = JSON.parse(bannerUrl).hk;

            fileListKR[0].url = JSON.parse(bannerUrl).kr;
            fileListJP[0].url = JSON.parse(bannerUrl).jp;

            this.setState({
                bannerUrlCN: JSON.parse(bannerUrl).cn,
                bannerUrlEN: JSON.parse(bannerUrl).en,
                bannerUrlHK: JSON.parse(bannerUrl).hk,

                bannerUrlKR: JSON.parse(bannerUrl).kr,
                bannerUrlJP: JSON.parse(bannerUrl).jp,

                linkUrlCN: JSON.parse(linkUrl).cn,
                linkUrlEN: JSON.parse(linkUrl).en,
                linkUrlHK: JSON.parse(linkUrl).hk,
                linkUrlKR: JSON.parse(linkUrl).kr,
                linkUrlJP: JSON.parse(linkUrl).jp,

                status: status,
                linkType,
                fileListCN,
                fileListEN,
                fileListHK,
                fileListJP,
                fileListKR,
                bannerName
            })
        } else {
            this.setState({
                bannerUrlCN: '',
                bannerUrlEN: '',
                bannerUrlHK: '',
                bannerUrlKR: '',
                bannerUrlJP: '',
                linkUrlCN: '',
                linkUrlEN: '',
                linkUrlHK: '',
                linkUrlKR: '',
                linkUrlJP: '',
                status: 0,
                linkType: 0,
                bannerName: "",
                fileListCN: [{
                    uid: -1,
                    name: 'xxx.png',
                    status: 'done',
                    url: ''
                }],
                fileListHK: [{
                    uid: -1,
                    name: 'xxx.png',
                    status: 'done',
                    url: ''
                }],
                fileListEN: [{
                    uid: -1,
                    name: 'xxx.png',
                    status: 'done',
                    url: ''
                }],
                fileListKR: [{
                    uid: -1,
                    name: 'xxx.png',
                    status: 'done',
                    url: ''
                }],
                fileListJP: [{
                    uid: -1,
                    name: 'xxx.png',
                    status: 'done',
                    url: ''
                }],
                // ...this.default
            })
        }
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        })
        this.props.handleInputChange(event)
    }
    // //获取七牛的 token
    // getQiniuToken(){
    //     axios.post(DOMAIN_VIP +"/news/getQiNiuToken").then(res => {
    //         const result = res.data
    //         if (result.code == 0){
    //             console.log(result.token)
    //             this.setState({
    //                 token: result.token
    //             })
    //         }
    //     })
    // }
    //上传 腾讯云
    getAuthorization(callback) {
        axios.post(DOMAIN_VIP + "/news/getTencentToken").then(res => {
            const result = res.data
            console.log(result)
            callback({
                url: result.data.host,
                key: result.data.key,
                XCosSecurityToken: result.data.token
            });
        })
    }

    uploadImageCos(file, type) {
        if (!this.limitUpImgSize(file.size)) {
            message.warning('上传图片大小不能超过300k！')
            return false
        }
        this.getAuthorization((info) => {
            file.status = 'done'
            let fd = new FormData();
            fd.append('key', info.key);
            fd.append('Signature', info.XCosSecurityToken);
            fd.append('Content-Type', '');
            fd.append('file', file);
            // let config = {
            //     headers: {'Content-Type': 'multipart/form-data'}
            //   }
            const xmlhttp = new XMLHttpRequest();
            xmlhttp.open('post', info.url, true);
            xmlhttp.send(fd);
            this.setState({
                tencent_host: info.url,
                tencent_key: info.key,
            }, () => {
                // if (type == "CN") {
                //     this.props.handleChangeBannerUrl(this.state.tencent_host + this.state.tencent_key, 'CN')
                // } else if (type == 'HK') {
                //     this.props.handleChangeBannerUrl(this.state.tencent_host + this.state.tencent_key, 'HK')
                // } else if (type == "EN") {
                //     this.props.handleChangeBannerUrl(this.state.tencent_host + this.state.tencent_key, 'EN')
                // }
                this.props.handleChangeBannerUrl(info.url + info.key, type)
            })
        })
    }
    //关闭弹窗
    handleCancel() {
        this.setState({
            previewVisible: false
        })
    }
    //图片上传预览
    handlePreview(file) {
        this.setState({
            previewImage: file.url || file.thumbUrl,
            previewVisible: true,
        })
    }
    //图片上传状态 简体
    handleChangeCN({ fileList }) {
        const size = fileList.length
        if (!this.limitUpImgSize(fileList[size - 1].size)) return false;
        fileList[size - 1].status = 'done'
        this.setState({
            fileListCN: fileList
        }, () => {
            if (fileList[size - 1].status == 'done') {
                const { fileListCN, qiniu_host, tencent_host, tencent_key } = this.state

                //console.log(qiniu_host+fileListCN[0].response.key)
                //this.props.handleChangeBannerUrl(tencent_host+tencent_key,'CN')
            }
        })
    }
    //图片上传状态 繁体
    handleChangeHK({ fileList }) {
        //console.log(fileList)
        const size = fileList.length
        if (!this.limitUpImgSize(fileList[size - 1].size)) return false;
        fileList[size - 1].status = 'done'
        this.setState({
            fileListHK: fileList
        }, () => {
            if (fileList[size - 1].status == 'done') {
                const { fileListCN, qiniu_host, tencent_host, tencent_key } = this.state
                //console.log(qiniu_host+fileListCN[0].response.key)
                //this.props.handleChangeBannerUrl(tencent_host+tencent_key,'HK')
            }
        })
    }
    //图片上传状态 英文
    handleChangeEN({ fileList }) {
        //console.log(fileList)
        const size = fileList.length
        if (!this.limitUpImgSize(fileList[size - 1].size)) return false;
        fileList[size - 1].status = 'done'
        this.setState({
            fileListEN: fileList
        }, () => {
            if (fileList[size - 1].status == 'done') {
                const { fileListCN, qiniu_host, tencent_host, tencent_key } = this.state
                //console.log(qiniu_host+fileListCN[0].response.key)
                //this.props.handleChangeBannerUrl(tencent_host+tencent_key,'EN')
            }
        })
    }
    //图片上传状态 日文
    handleChangeJA = ({ fileList }) => {
        //console.log(fileList)
        const size = fileList.length
        if (!this.limitUpImgSize(fileList[size - 1].size)) return false;
        fileList[size - 1].status = 'done'
        this.setState({
            fileListJP: fileList
        }, () => {
            if (fileList[size - 1].status == 'done') {
                const { fileListCN, qiniu_host, tencent_host, tencent_key } = this.state
                //console.log(qiniu_host+fileListCN[0].response.key)
                //this.props.handleChangeBannerUrl(tencent_host+tencent_key,'EN')
            }
        })
    }
    //图片上传状态 韩文
    handleChangeKO = ({ fileList }) => {
        //console.log(fileList)
        const size = fileList.length
        if (!this.limitUpImgSize(fileList[size - 1].size)) return false;
        fileList[size - 1].status = 'done'
        this.setState({
            fileListKR: fileList
        }, () => {
            if (fileList[size - 1].status == 'done') {
                const { fileListCN, qiniu_host, tencent_host, tencent_key } = this.state
                //console.log(qiniu_host+fileListCN[0].response.key)
                //this.props.handleChangeBannerUrl(tencent_host+tencent_key,'EN')
            }
        })
    }
    //限制上传图片大小
    limitUpImgSize = (size) => {
        // let isLt300k = size/1024 < 300;
        return size / 1024 < 300;
    }
    tabCallback = tabKey => {
        this.setState({
            tabKey
        })
        this.props.tabCallback(tabKey)
    }
    render() {
        const { remark, status, linkUrlCN, linkUrlEN, linkUrlHK, bannerUrlCN, bannerUrlEN, bannerUrlHK, previewImage, previewVisible, fileList, bannerName, token, fileListCN, fileListHK, fileListEN,
            linkUrlJP, linkUrlKR, fileListJP, fileListKR,linkType } = this.state

        const uploadButton = (
            <div>
                <Icon type="plus" />
                <div className="ant-upload-text">Upload</div>
            </div>
        );
        let uploadUrl;
        if (window.location.protocol === 'https:') {
            uploadUrl = 'https://up.qbox.me';
        } else {
            uploadUrl = 'http://up.qiniu.com';
        }
        let imgName = new Date().getTime()
        imgName = "bitglobal/newsupload/" + imgName;
        let nfileListCN = fileListCN.slice(-1);
        let nfileListHK = fileListHK.slice(-1);
        let nfileListEN = fileListEN.slice(-1);
        let nfileListKR = fileListKR.slice(-1);
        let nfileListJP = fileListJP.slice(-1);
        // const tokens= {
        //     token:token
        // }
        // const uploadBannerProps = {
        //     listType:"picture-card",
        //     className:"avatar-uploader",
        //     action:uploadUrl,
        //     data:tokens,
        //     fileList:nfileList,
        //     onChange:this.handleChange,
        //     onPreview:this.handlePreview,
        // }
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-2 control-label">状态:</label>
                    <div className="col-sm-8">
                        <RadioGroup onChange={this.handleInputChange} name="status" value={status}>
                            <Radio value={0}>关闭</Radio>
                            <Radio value={1}>开启</Radio>
                        </RadioGroup>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">跳转方式:</label>
                    <div className="col-sm-8">
                        <RadioGroup onChange={this.handleInputChange} name="linkType" value={linkType}>
                            <Radio value={0}>当前页签</Radio>
                            <Radio value={1}>新页签</Radio>
                        </RadioGroup>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-2 control-label">图片名称:</label>
                    <div className="col-sm-8">
                        <input className="form-control" name="bannerName" value={bannerName} onChange={this.handleInputChange} />
                    </div>
                </div>
                <Tabs defaultActiveKey='en' onChange={this.tabCallback}>
                    <TabPane tab='简体' key="cn">
                        <div className="form-group">
                            <label className="col-sm-2 control-label">banner图片:</label>
                            <div className="col-sm-8">
                                <Upload
                                    //{...uploadBannerProps}
                                    listType="picture-card"
                                    className="avatar-uploader"
                                    //action={uploadUrl}
                                    //data={{token:token,key:imgName}}
                                    fileList={nfileListCN}
                                    onChange={this.handleChangeCN}
                                    onPreview={this.handlePreview}
                                    showUploadList={{
                                        //showPreviewIcon:false,
                                        showRemoveIcon: false
                                    }}
                                    customRequest={(e) => {
                                        //console.log(e)
                                        //this.handlePreview(e.file)
                                        this.uploadImageCos(e.file, "CN")
                                    }}

                                >
                                    {uploadButton}
                                </Upload>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-sm-2 control-label">跳转链接:</label>
                            <div className="col-sm-8">
                                <input className="form-control" name="linkUrlCN" value={linkUrlCN} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </TabPane>
                    <TabPane tab='繁体' key="hk">
                        <div className="form-group">
                            <label className="col-sm-2 control-label">banner图片:</label>
                            <div className="col-sm-8">
                                <Upload
                                    //{...uploadBannerProps}
                                    listType="picture-card"
                                    className="avatar-uploader"
                                    //action={uploadUrl}
                                    //data={{token:token,key:imgName}}
                                    fileList={nfileListHK}
                                    onChange={this.handleChangeHK}
                                    onPreview={this.handlePreview}
                                    showUploadList={{
                                        //showPreviewIcon:false,
                                        showRemoveIcon: false
                                    }}
                                    customRequest={(e) => {
                                        //console.log(e)
                                        //this.handlePreview(e.file)
                                        this.uploadImageCos(e.file, "HK")
                                    }}
                                >
                                    {uploadButton}
                                </Upload>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-sm-2 control-label">跳转链接:</label>
                            <div className="col-sm-8">
                                <input className="form-control" name="linkUrlHK" value={linkUrlHK} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </TabPane>
                    <TabPane tab='英文' key="en">
                        <div className="form-group">
                            <label className="col-sm-2 control-label">banner图片:</label>
                            <div className="col-sm-8">
                                <Upload
                                    //{...uploadBannerProps}
                                    listType="picture-card"
                                    className="avatar-uploader"
                                    //action={uploadUrl}
                                    //data={{token:token,key:imgName}}
                                    fileList={nfileListEN}
                                    onChange={this.handleChangeEN}
                                    onPreview={this.handlePreview}
                                    showUploadList={{
                                        //showPreviewIcon:false,
                                        showRemoveIcon: false
                                    }}
                                    customRequest={(e) => {
                                        //console.log(e)
                                        //this.handlePreview(e.file)
                                        this.uploadImageCos(e.file, "EN")
                                    }}
                                >
                                    {uploadButton}
                                </Upload>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-sm-2 control-label">跳转链接:</label>
                            <div className="col-sm-8">
                                <input className="form-control" name="linkUrlEN" value={linkUrlEN} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </TabPane>
                    <TabPane tab='日文' key="jp">
                        <div className="form-group">
                            <label className="col-sm-2 control-label">banner图片:</label>
                            <div className="col-sm-8">
                                <Upload
                                    //{...uploadBannerProps}
                                    listType="picture-card"
                                    className="avatar-uploader"
                                    //action={uploadUrl}
                                    //data={{token:token,key:imgName}}
                                    fileList={nfileListJP}
                                    onChange={this.handleChangeJA}
                                    onPreview={this.handlePreview}
                                    showUploadList={{
                                        //showPreviewIcon:false,
                                        showRemoveIcon: false
                                    }}
                                    customRequest={(e) => {
                                        //console.log(e)
                                        //this.handlePreview(e.file)
                                        this.uploadImageCos(e.file, "JP")
                                    }}
                                >
                                    {uploadButton}
                                </Upload>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-sm-2 control-label">跳转链接:</label>
                            <div className="col-sm-8">
                                <input className="form-control" name="linkUrlJP" value={linkUrlJP} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </TabPane>
                    <TabPane tab='韩文' key="kr">
                        <div className="form-group">
                            <label className="col-sm-2 control-label">banner图片:</label>
                            <div className="col-sm-8">
                                <Upload
                                    //{...uploadBannerProps}
                                    listType="picture-card"
                                    className="avatar-uploader"
                                    //action={uploadUrl}
                                    //data={{token:token,key:imgName}}
                                    fileList={nfileListKR}
                                    onChange={this.handleChangeKO}
                                    onPreview={this.handlePreview}
                                    showUploadList={{
                                        //showPreviewIcon:false,
                                        showRemoveIcon: false
                                    }}
                                    customRequest={(e) => {
                                        //console.log(e)
                                        //this.handlePreview(e.file)
                                        this.uploadImageCos(e.file, "KR")
                                    }}
                                >
                                    {uploadButton}
                                </Upload>
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-sm-2 control-label">跳转链接:</label>
                            <div className="col-sm-8">
                                <input className="form-control" name="linkUrlKR" value={linkUrlKR} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </TabPane>
                </Tabs>
                <Modal visible={previewVisible} footer={null} onCancel={this.handleCancel}>
                    <img alt="example" style={{ width: '100%' }} src={previewImage} />
                </Modal>
            </div>
        )
    }
}