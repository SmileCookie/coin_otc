import React from 'react'
import {injectIntl} from 'react-intl';
import {Upload, Icon, Modal} from 'antd';
import {connect} from "react-redux";
import {getUploadToken} from "../../redux/modules/userInfo";
import {fetchFrontalImg} from "../../redux/modules/session";
import {DOMAIN_VIP, UPLOAD_PATH} from "../../conf";
import axios from "axios";
import {optPop} from "../../utils";

import './upload.less'

function getBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });
}

function fileToBinaryString(file) {
    return new Promise((resolve, reject) => {
        let re = '',
            read = new FileReader();
        read.readAsArrayBuffer(file)
        read.onload = function () {
            console.log(this.result);
            // console.log(new Blob([this.result]))
            re = this.result;
            return resolve(re);
        }
    }).catch((err) =>{
        return   reject(err);
    })
}


@connect(
    state => ({userInfo: state.userInfo}), // 用户信息
    {getUploadToken, fetchFrontalImg}
)
    /**
     * limit : 最大上传张数
     * getImgList（）: 理新父组件 imglist方法
     */
class FileUpload extends React.Component {
    constructor(props) {
        super(props);
        this.file = {
            uid: '-1',
            name: '',
            status: 'done',
            url: 'https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png',
        }
        this.state = {
            url: '',
            previewVisible: false,
            previewImage: '', // 缩略图
            fileList: [], //  上传文件列表
            imgList: [], // 上传用imlist
        };

        this.customUpload = this.customUpload.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.propsToUp = this.propsToUp.bind(this)
    }

    componentDidMount() {

    }

    handleCancel = () => this.setState({previewVisible: false});

    handlePreview = async file => {
        if (!file.url && !file.preview) {
            file.preview = await getBase64(file.originFileObj);
        }

        this.setState({
            previewImage: file.url || file.preview,
            previewVisible: true,
        });
    };

    handleChange = ({file,fileList}) => {
        this.setState({fileList},
            () => {
                file.status = "done";
                setTimeout(() =>{
                    let list = document.querySelectorAll('.anticon-delete');
                    for(let l of list){
                        l.title = '';
                    }
                },1500)
            })
    };
    handleMove = (file) => {
        let {imgList} = this.state;
        for (let i = 0; i < imgList.length; i++) {
            if (file.uid == imgList[i].uid) {
                imgList.splice(i, 1);
                break;
            }
        }
        this.setState({
            imgList
        }, () => {
            this.propsToUp(imgList)
        })
        console.log(this.state.imgList)
    }

    // 更新父组件 imglist
    propsToUp(v) {
        this.setState({
            imgList: this.state.imgList.concat(v)
        }, () => {
            let {imgList} = this.state;
            this.props.getImgList(imgList)
        })
    }

    notPreview(e) {
        e.preventDefault();
        return false;
    }
    checkBeforeUpload = (file) =>{
        return new Promise((resolve, reject) =>{
            // let {formatMessage} = this.props.intl;
            let {filetype} = this.props;
            console.log(filetype)
            if (filetype && filetype == 'image'){
                if(!this.checkUploadImg(file)){
                    return reject(false);
                }
            } else {
                if(!this.checkUploadVideo(file)){
                    return reject(false);
                }
            }
            return resolve(true)
        })
    }
    checkUploadVideo = (file) =>{
        let {formatMessage} = this.props.intl;
        let {type, size} = file,
            str = 'video';
        if(type.includes(str)){
            const isLt30M = size /1024 /1024 < 50;
            if (!isLt30M) {
                optPop({},formatMessage({id:'大小不可超过30M'}),{timer:1500})
                return false
            }
        }else{
            optPop({},formatMessage({id:'只支持主流视格式文件'}),{timer:1500})
            return false
        }
        return true

    }

    checkUploadImg = (file) =>{
        let {formatMessage} = this.props.intl;
        let {type, size} = file,
            reg = /jpeg|jpg|bmp|png/,
            img = "image";
        if (reg.test(type)) {
            const isLt5M = size / 1024 / 1024 < 5;
            if (!isLt5M) {
                optPop({}, formatMessage({id: '申诉截图大小不可超过5M'}), {timer: 1500})
                return false
            }
        } else {
            optPop({}, formatMessage({id: '只支持bmp、jpg、png和jpeg格式'}), {timer: 1500})
            return false
        }
        return true
    }
    // 自定义上传
    customUpload(files) {
        console.log('自定义上传')
        // fileToBinaryString(files.file).then((res) => {
        // let res
        // this.propsToUp(files.file);
        // let e = files.file
        // files.onSuccess((e) => {
        //     console.log(e)
        // })
        axios.post(DOMAIN_VIP + "/manage/auth/uploadToken").then(res => { // 获取token
            console.log(res);
            const {datas: info} = res.data;
            console.log(info);
            const fd = new FormData();
            fd.append('key', info.key);
            fd.append('Signature', info.token);
            fd.append('file', files.file);
            axios.post(info.host, fd).then((r) => {
                console.log("上传图片========", r);
                if (r.status == 204) {
                    let k = info.host + info.key
                    let obj = Object.assign({}, {
                        url: k,
                        type: files.file.type,
                        uid: files.file.uid
                    })
                    //更新父组件 imglist
                    this.propsToUp([obj])
                    //图片回显
                    files.onSuccess((e) => {
                        console.log(e);
                    })
                } else {
                    let {formatMessage} = this.props.intl
                    optPop(() => {
                    }, formatMessage({id: "上传失败"}), {timer: 1500})
                }
            })
        })
    }

    render() {
        let {limit: limit = 5} = this.props // 最大图片上传数量
        const {previewVisible, previewImage, fileList, url} = this.state;
        let {formatMessage} = this.props.intl
        const uploadButton = (
            <div>
                <Icon type="plus"/>
                {/*<div className="ant-upload-text">{formatMessage({id:'上传凭证'})}</div>*/}
            </div>
        );
        return (
            <div className="clearfix">
                <Upload
                    action={url}  // 上传地址
                    listType="picture-card"
                    fileList={fileList}
                    onPreview={this.notPreview}
                    onChange={this.handleChange}
                    customRequest={this.customUpload}
                    onRemove={this.handleMove}
                    beforeUpload={this.checkBeforeUpload}
                >
                    {fileList.length >= limit ? null : uploadButton}
                </Upload>
                {/*<Modal visible={previewVisible} footer={null} onCancel={this.handleCancel}>*/}
                {/*    <img alt="example" style={{width: '100%'}} src={previewImage}/>*/}
                {/*</Modal>*/}
            </div>
        );
    }
}

export default injectIntl(FileUpload)
