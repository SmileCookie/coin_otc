import React from 'react'
import { injectIntl } from 'react-intl';
import {connect} from "react-redux";
import axios from "axios";
import { Upload, Icon, Modal } from 'antd';

import './upload.less'
import {optPop} from "../../utils";
import {DOMAIN_VIP} from "../../conf";

function fileToBinaryString(file) {
    return new Promise((resolve, reject) => {
        let re = '',
            read = new FileReader();
        read.readAsArrayBuffer(file)
        read.onload = function () {
            console.log(this.result);
            // console.log(new Blob([this.result]))
            re = this.result;
          return (re);
        }
    }).catch((err) =>{
      return   reject(err);
    })
}


    /**
     * limit : 最大上传张数
     * getImgList（）: 理新父组件 imglist方法
     */
class FileUpload extends React.Component {
    constructor(props){
        super(props);
        this.myfile = React.createRef();
        this.file = {
            uid: '-1',
            name: '',
            status: 'done',
            url: 'https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png',
        }
        this.state = {
            url:'',
            previewVisible: false,
            previewImage: '', // 缩略图
            fileList: [], //  上传文件列表
            tempList: [],  // temp 列表
            uploading: false,
            fileType:''
        };

        this.customUpload = this.customUpload.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.getFileList = this.getFileList.bind(this)
    }


    componentDidMount(){

    }

    handleChange = ({file,fileList}) => {
        this.setState({fileList},
            () =>{
                // console.log(this.state.fileList)
                file.status = "done"
                let {filetype } = this.props
                if (filetype == 'image'){
                    // this.props.ckImgList();
                    setTimeout(() =>{
                        let list = document.querySelectorAll('.anticon-delete');
                        for(let l of list){
                            l.title = '';
                        }
                    },1500)
                } else if (filetype == 'video'){

                    // this.props.ckVidList();
                    setTimeout(() =>{
                        let list = document.querySelectorAll('.anticon-delete');
                        for(let l of list){
                            l.title = '';
                        }
                    },1500)
                }
            })
    };
    handleMove = (file) => {
        let {tempList} = this.state;
        for (let i = 0; i < tempList.length; i++) {
            if (file.uid == tempList[i].uid) {
                tempList.splice(i, 1);
                break;
            }
        }
        this.setState({
            tempList
        }, () => {
            // this.propsToUp(tempList)
            console.log(this.state.tempList)
        })
    }


    notPreview(e){
        e.preventDefault();
        return false;
    }
    // 更新父组件 imglist 或 videolist
    propsToUp = (v) => {
        this.setState({
            tempList: this.state.tempList.concat(v)
        }, () => {
            let {tempList} = this.state;
            this.props.getImgList(tempList)
        })
    }


        // 自定义上传
    customUpload(files){
        console.log('自定义上传')
        console.log(files)
        const {tempList} = this.state
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
                    this.setState({
                        tempList: [...tempList,obj]
                    },() =>{
                        // this.propsToUp(this.state.tempList);
                    })
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

    resetFlieList(){
        this.setState({
            fileList:[],
            tempList:[]
        })
    }
    getFileList(){
        // let {fileList} = this.state;
        // return fileList;
        let {tempList} = this.state;
        return tempList;
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
            let {formatMessage} = this.props;
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
            let {formatMessage} = this.props;
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
    render() {
        let {limit,ckFile,formatMessage}= this.props // 最大图片上传数量
        const { previewVisible, previewImage, fileList, url} = this.state;
        const uploadButton = (
            <div>
                <Icon type="plus" />
                <div className="ant-upload-text">{formatMessage({id:'上传凭证'})}</div>
                <p className="anticon-msg">{this.props.msg}</p>
            </div>
        );
        return (
            <div className="upload-div">
                <Upload
                    ref={this.myfile}
                    action={url}  // 上传地址
                    listType="picture-card"
                    fileList={fileList}
                    onRemove={this.handleMove}
                    onPreview={this.notPreview}
                    onChange={this.handleChange}
                    customRequest={this.customUpload}
                    beforeUpload={this.checkBeforeUpload}
                >
                    {fileList.length >= limit ? null : uploadButton}
                </Upload>
                {/*<Modal visible={previewVisible} footer={null} onCancel={this.handleCancel}>*/}
                {/*    <img alt="example" style={{ width: '100%' }} src={previewImage} />*/}
                {/*</Modal>*/}
            </div>
        );
    }
}

export default FileUpload
