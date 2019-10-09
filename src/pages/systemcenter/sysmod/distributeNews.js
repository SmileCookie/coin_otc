import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, TIMEFORMAT_DAYS_ss, SELECTWIDTH, TIMEFORMAT_ss } from '../../../conf'
import { Button, Select, DatePicker, Modal, message, Radio, Input, Upload, Icon } from 'antd'
import moment from 'moment'
import E from 'wangeditor'
import Qiniu from '../../../utils/upload/qiniu'
const plupload = require("../../../utils/upload/plupload.full.min.js")
import ModalPrev from './modal/prev'
const Option = Select.Option;
const RadioGroup = Radio.Group;
const { TextArea } = Input;

export default class DistributeNews extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            type: "",
            langs: "",
            source: "",
            sourceLink: "",
            noticeType: "",
            isTop: false,
            title: "",
            keyword: "",
            digest: "",
            editorContent: "",
            previewVisible: false,
            previewImage: '',
            fileList: [],
            pubTime: "",
            time: [],
            token: "",
            visible: false,
            motitle: '',
            width: '',
            qiniu_host: "https://o4we6sxpt.qnssl.com/",
        }
        this.handleInputChange = this.handleInputChange.bind(this)
        this.type_handleChange = this.type_handleChange.bind(this)
        this.langs_handleChange = this.langs_handleChange.bind(this)
        this.noticeType_handleChange = this.noticeType_handleChange.bind(this)
        this.radioOnChange = this.radioOnChange.bind(this)
        this.handleInputTextAreaChange = this.handleInputTextAreaChange.bind(this)
        this.time_onChange = this.time_onChange.bind(this)
        this.editorConfig = this.editorConfig.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handlePreview = this.handlePreview.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.resetNews = this.resetNews.bind(this)
        this.typeStatus = this.typeStatus.bind(this)
        // this.uploadInit = this.uploadInit.bind(this)
        this.printLog = this.printLog.bind(this)
        this.show_click = this.show_click.bind(this)
        this.sourceCode = this.sourceCode.bind(this)
        this.vahandleCancel = this.vahandleCancel.bind(this)
        this.cancelValue = this.cancelValue.bind(this)
        this.textareaChange = this.textareaChange.bind(this)
        // this.business_handleChange = this.business_handleChange.bind(this)
        this.uploadImageCos = this.uploadImageCos.bind(this)
        this.getAuthorization = this.getAuthorization.bind(this)
        this.handleChoice = this.handleChoice.bind(this)
    }
    //修改状态
    show_click(index) {
        this.props.showHideClick(index);
    }
    handleChoice() {
        this.setState({
            previewVisible: false
        })
    }
    componentDidMount() {
        // axios.post(DOMAIN_VIP +"/news/getQiNiuToken").then(res => {
        //     const result = res.data
        //     if (result.code == 0){
        //         this.setState({
        //             token: result.token
        //         })
        if (!!this.props.newsId) {
            axios.post(DOMAIN_VIP + "/news/editItemOfNewById", qs.stringify({
                id: this.props.newsId
            })).then(res => {
                const result = res.data
                console.log(result)
                if (result.code == 0) {
                    let noticeType = result.status.noticeType ? result.status.noticeType.toString() : "";
                    this.setState({
                        type: result.status.type.toString(),
                        langs: result.status.language,
                        source: result.status.source == null ? "" : result.status.source,
                        sourceLink: result.status.sourceLink == null ? "" : result.status.sourceLink,
                        noticeType: noticeType,
                        isTop: result.status.top,
                        title: result.status.title,
                        keyword: result.status.keyword,
                        digest: result.status.digest,
                        editorContent: result.status.content,
                        pubTime: moment(result.status.pubTime).format(TIMEFORMAT_ss),
                        previewImage: result.status.photo,
                        fileList: result.status.photo ? [{
                            uid: -1,
                            name: 'xxx.png',
                            status: 'done',
                            url: result.status.photo,
                        }] : [],
                    })
                    this.editorConfig(result.status.content);//富文本编译器
                }
            })

        }
        else {
            this.editorConfig(false);//富文本编译器
        }
        // }
        // })

    }

    //富文本编译器
    editorConfig(propstate) {
        const elem = this.refs.editorElem
        const editor = new E(elem)
        // editor.customConfig.pasteFilterStyle = true
        editor.customConfig.pasteIgnoreImg = false
        editor.customConfig.uploadImgShowBase64 = true   // 使用 base64 保存图片
        // editor.customConfig.showLinkImg = true // 隐藏“网络图片”tab
        editor.customConfig.zIndex = 1;
        editor.customConfig.uploadImgMaxSize = 300 * 1024 ;
        

        editor.customConfig.customUploadImg = (files, insert)=>{
            // files 是 input 中选中的文件列表
            // insert 是获取图片 url 后，插入到编辑器的方法
            console.log(files)
            let file = files[0]
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
                console.log(info)
                // this.setState({
                //     tencent_host: info.url,
                //     tencent_key: info.key,
                // })
                xmlhttp.onreadystatechange = function(){
                    if(xmlhttp.readyState == 4){
                        console.log(xmlhttp)
                        // if(xmlhttp.status == 200 || xmlhttp.status == 204){

                            insert(info.url+info.key)
                        // }
                    }
                }
            })
            // 上传代码返回结果之后，将图片插入到编辑器中
        }


        editor.customConfig.customAlert = info => {
            console.log(info)
            message.warning('上传图片大小不能超过300k！')
        }
        editor.customConfig.linkImgCallback = url => {
            console.log(url)
        }
        editor.customConfig.linkImgCheck = function (src) {
            console.log(src) // 图片的链接
        
            return true // 返回 true 表示校验成功
            // return '验证失败' // 返回字符串，即校验失败的提示信息
        }
        editor.customConfig.colors = [
            '#9199AF',
            '#ffffff',
            '#000000',
            '#1c487f',
            '#4d80bf',
            '#c24f4a',
            '#8baa4a',
            '#7b5ba1',
            '#46acc8',
            '#f9963b',
            '#333333'
        ];
        editor.customConfig.menus = [
            // 'head',  // 标题
            'bold',  // 粗体
            'fontSize',  // 字号
            'fontName',  // 字体
            'italic',  // 斜体
            'underline',  // 下划线
            'strikeThrough',  // 删除线
            'foreColor',  // 文字颜色
            'backColor',  // 背景颜色
            'link',  // 插入链接
            // 'list',  // 列表
            'justify',  // 对齐方式
            // 'quote',  // 引用
            // 'emoticon',  // 表情
            'image',  // 插入图片
            'table',  // 表格
            // 'video',  // 插入视频
            // 'code',  // 插入代码
            'undo',  // 撤销
            'redo'  // 重复
        ]
        editor.customConfig.linkCheck = function (text, link) {
            console.log(text,link)
            if(/^http:\/\//.test(link)||/^https:\/\//.test(link)){
                console.log(link)
            }else{
                link = 'http://' + link;
            }
        
            return true // 返回 true 表示校验成功
            // return '验证失败' // 返回字符串，即校验失败的提示信息
        }
        editor.customConfig.onchange = html => {
            console.log(/href="/ig.test(html))
            if(/href="/ig.test(html)&&!/href=http"/ig.test(html)){

            }
            if(/href="http/ig.test(html)||/href="https/ig.test(html)){

            }else{
                html = html.replace(/href="/ig,'href="http://')

            }
            this.setState({
                editorContent: html.replace(/<p>&nbsp;&nbsp;/ig, '<&&&&&&&>').replace(/target="_blank" style="background-color: rgb\(255, 255, 255\);"/ig,"target='_blank'").replace(/&nbsp;/ig, ' ').replace(/<&&&&&&&>/ig, '<p>&nbsp;&nbsp;')
            })
            // console.log(html)
        }
        // editor.customConfig.pasteFilterStyle = false
        //     console.log
        //     let eve = e.originalEvent;//所有js的原生事件都被保存到originalEvent中
        //     let cp = eve.clipboardData;//从originalEvent取出剪切板的事件
        //     let clipboardData = window.clipboardData||e.originalEvent.clipboardData; //兼容ie||chrome
        //    console.log(clipboardData.getData('Text'))
        // editor.customConfig.qiniu = true  // 允许上传到七牛云存储

        editor.customConfig.pasteTextHandle = content => {
            let filterContent = content.replace(/<head[^>]*?>[\s\S]*head>/gi, '');//过滤head标签中
            filterContent = filterContent.replace(/<script[^>]*?>[\\s\\S]*script>/gi, '');//过滤js
            // // filterContent = filterContent.replace(/\<br[^>]/g,'*')
            // filterContent = filterContent.replace(/<\/p>|<\/div>|<\/section>|<\/article>|<\/h1>|<\/h2>|<\/h3>|<\/h4>|<\/h5>|<\/h6>|<\/abbr>/g, '</br>')
            // filterContent = filterContent.replace(/<p[^>]*?>|<div[^>]*?>|<section[^>]*?>|<article[^>]*?>|<h1[^>]*?>|<h2[^>]*?>|<h3[^>]*?>|<h4[^>]*?>|<h5[^>]*?>|<h6[^>]*?>|<abbr[^>]*?>/g, '')
            // // filterContent = filterContent.replace(/<\/a>|<a[^>]*?>|<\/b>|<b>|<\/span>|<span[^>]*?>|<\/strong>|<strong[^>]*?>|<\/em>|<em[^>]*?>|<\/font>|<font[^>]*?>|<\/i>|<i[^>]*?>|<img[^>]*?>/g,'')
            // filterContent = filterContent.replace(/<!(img|p)\/?>/g, '');//过滤标签
            // filterContent = filterContent.replace(/\*/g, '<br/>');
            // filterContent = filterContent.replace(/\\s*|\t|\r|\n|&nbsp;/ig, "")//过滤空格，换行
            // filterContent = filterContent.replace(/(?!<[^>]*br[^>]*>)<[^>]+>/g, '')
            return filterContent
        }

        editor.customConfig.debug = location.href.indexOf('wangeditor_debug_mode=1') > 0
        editor.create()
        // this.uploadInit(editor)   // 初始化七牛上传
        if (propstate) {
            editor.txt.html(propstate)
        }
    }


    sourceCode() {
        const { editorContent } = this.state
        // const elem = this.refs.editorElem
        let setValue = this.state.editorContent
        //let contentTxt = editor.txt.innerHTML
        this.footer = [
            <Button key="back" onClick={() => this.cancelValue(setValue)}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.vahandleCancel()}>
                确认
        </Button>,
        ]
        this.setState({
            visible: true,
            width: "800px",
            motitle: '显示源码',
            modalHtml: <TextArea rows={24} value={this.state.editorContent} onChange={this.textareaChange}></TextArea>
        })
    }
    cancelValue(setValue) {
        this.setState({
            editorContent: setValue,
            visible: false
        })
    }
    preview = () => {
        this.footer = [
            <Button key="back" onClick={this.vahandleCancel}>关闭</Button>,
        ]
        this.setState({
            visible: true,
            width: '1000px',
            height: '780px',
            motitle: '预览',
            modalHtml: <ModalPrev content={this.state.editorContent} />
        })
    }
    //弹窗隐藏
    vahandleCancel() {
        this.setState({
            visible: false
        });
    }
    // 初始化七牛上传的方法
    // uploadInit(editor) {
    //     var btnId = editor.imgMenuId;
    //     var containerId = editor.toolbarElemId;
    //     var textElemId = editor.textElemId;
    //     const { qiniu_host,token} = this.state
    //     const that = this
    //     // 创建上传对象
    //     var uploader = Qiniu.uploader({
    //         runtimes: 'html5,flash,html4',    //上传模式,依次退化
    //         browse_button: btnId,       //上传选择的点选按钮，**必需**
    //         uptoken: token,//若未指定uptoken_url,则必须指定 uptoken ,uptoken由其他程序生成
    //         domain: qiniu_host, //bucket 域名，下载资源时用到，**必需**
    //         container: containerId,           //上传区域DOM ID，默认是browser_button的父元素，
    //         max_file_size: '10mb',           //最大文件体积限制
    //         flash_swf_url: "../../../utils/upload/Moxie.swf", 
    //         filters: {
    //             mime_types: [
    //                 //只允许上传图片文件 （注意，extensions中，逗号后面不要加空格）
    //                 { title: "图片文件", extensions: "jpg,gif,png,bmp" }
    //             ]
    //         },
    //         max_retries: 3,                   //上传失败最大重试次数
    //         drop_element: textElemId,        //拖曳上传区域元素的ID，拖曳文件或文件夹后可触发上传
    //         chunk_size: '1mb',                //分块上传时，每片的体积
    //         auto_start: true,                 //选择文件后自动上传，若关闭需要自己绑定事件触发上传
    //         init: {
    //             'FilesAdded': function (up, files) {
    //                 plupload.each(files, function (file) {
    //                     // 文件添加进队列后,处理相关的事情
    //                 });
    //             },
    //             'BeforeUpload': function (up, file) {
    //                 // 每个文件上传前,处理相关的事情
    //             },
    //             'UploadProgress': function (up, file) {
    //                 // 显示进度
    //             },
    //             'FileUploaded': function (up, file, info) {
    //                 // 每个文件上传成功后,处理相关的事情
    //                 // 其中 info 是文件上传成功后，服务端返回的json
    //                 that.printLog(info);
    //                 var domain = up.getOption('domain');
    //                 console.log(info);
    //                 var res = JSON.parse(info.response)
    //                 console.log(res);
    //                 var sourceLink = domain + res.key; //获取上传成功后的文件的Url
    //                 // 插入图片到editor
    //                 editor.cmd.do('insertHtml', '<img src="' + sourceLink + '" style="max-width:100%;"/>')
    //             },
    //             'Error': function (up, err, errTip) {
    //                 //上传出错时,处理相关的事情
    //             },
    //             'UploadComplete': function () {
    //                 //队列文件处理完毕后,处理相关的事情
    //             }
    //         // Key 函数如果有需要自行配置，无特殊需要请注释
    //         //,
    //         // 'Key': function(up, file) {
    //         //     // 若想在前端对每个文件的key进行个性化处理，可以配置该函数
    //         //     // 该配置必须要在 unique_names: false , save_key: false 时才生效
    //         //     var key = "";
    //         //     // do something with key here
    //         //     return key
    //         // }
    //         }
    //     });
    // }

    // 封装 console.log 函数
    printLog(title, info) {
        window.console && console.log(title, info);
    }

    resetNews() {
        const { type, title, langs, isTop, keyword, source, sourceLink, digest, noticeType, pubTime, fileList, editorContent, previewImage, tencent_host, tencent_key } = this.state

        if (!this.typeStatus()) return;
        axios.post(DOMAIN_VIP + "/news/newsManagerForEdit", qs.stringify({
            title: title,
            type: type,
            language: langs,
            top: isTop,
            keyword: keyword,
            digest: digest,
            noticeType: noticeType,
            pubTime: pubTime,
            content: editorContent,
            // photo: fileList.length > 0 ? fileList[0].thumbUrl : "",
            photo:previewImage,
            source: source,
            sourceLink: sourceLink,
            id: this.props.newsId,
        })).then(res => {
            const result = res.data
            if (result.status == 1) {
                message.success('发布成功');
                this.props.showHideClick(0);
            }
        })
    }
    typeStatus() {
        const { type, title, langs, isTop, source, sourceLink, keyword, digest, noticeType, pubTime, fileList, editorContent, previewImage } = this.state
        if (type == "") {
            message.error("请选择发布类型！")
            return false
        }
        if (langs == "") {
            message.error("请选择发布语言")
            return false
        }
        if (type == 1 && noticeType == "") {
            message.error("请选择公告类型")
            return false
        }
        if (type == 0 && source == "") {
            message.error("请输入来源")
            return false
        }
        if (type == 0 && sourceLink == "") {
            message.error("请输入来源链接")
            return false
        }
        if (title == "") {
            message.error("请输入标题")
            return false
        }
        if (keyword == "") {
            message.error("请输入关键字")
            return false
        }
        if (digest == "") {
            message.error("请输入摘要")
            return false
        }
        if (editorContent == "") {
            message.error("请输入正文")
            return false
        }
        if (pubTime == "") {
            message.error("请选择发布时间")
            return false
        }
        return true
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //输入时 TextArea 设置到 state
    handleInputTextAreaChange(event) {
        const target = event.target;
        const value = target.value;
        this.setState({
            digest: value
        });
    }

    //类型
    type_handleChange(value) {
        this.setState({
            type: value
        })
    }
    // //交易类型
    // business_handleChange(value){
    //     this.setState({
    //         dealType:value
    //     })
    // }
    //语言
    langs_handleChange(value) {
        this.setState({
            langs: value
        })
    }
    //公告类型
    noticeType_handleChange(value) {
        this.setState({
            noticeType: value
        })
    }
    //是否置顶
    radioOnChange(e) {
        this.setState({
            isTop: e.target.value,
        });
    }
    //时间选择框
    time_onChange(value, dateString) {
        this.setState({
            pubTime: dateString,
            time: value
        })
    }
    textareaChange(event) {
        const target = event.target;
        const value = target.value;
        this.setState({
            editorContent: value
        })
    }
    //图片上传
    handleCancel() {
        this.setState({ previewVisible: false })
    }
    //图片预览
    handlePreview = (file) => {
        console.log(file)
        const { previewImage} = this.state
        this.setState({
            previewVisible: true,
        })
        console.log(previewImage)
        // this.setState(()=>({
        //     // previewImage:file.thumbUrl,
        //     previewVisible: true,
        // }));
        // console.log(this.state.previewVisible)
    }
    //限制上传图片大小
    limitUpImgSize = (size) => {
        // let isLt300k = size/1024 < 300;
        return size / 1024 < 300;
    }
    handleChange(type) {
        if (!this.limitUpImgSize(type.file.size)) return false;
        if (type.fileList.length > 0) {
            const fileList = type.fileList
            const size = fileList.length
            // console.log('1>>>>>>>>>>'+fileList)
            fileList[size - 1].status = 'done'
            this.setState({
                fileList: [...fileList]
            })
        } else {
            // console.log('2>>>>>>>>>>'+fileList)
            const fileList = type.fileList
            this.setState({
                fileList: [...fileList]
            })
        }

        this.setState({ fileList: [...type.fileList] })
    }

    //上传 腾讯云
    getAuthorization(callback) {
        axios.post(DOMAIN_VIP + "/news/getTencentToken").then(res => {
            const result = res.data
            callback({
                url: result.data.host,
                key: result.data.key,
                XCosSecurityToken: result.data.token
            });
        })
    }

    uploadImageCos(file) {
        if (!this.limitUpImgSize(file.size)) {
            message.warning('上传图片大小不能超过300k！')
            return false
        }
        let self = this;
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
            xmlhttp.onreadystatechange = function(){
                if(xmlhttp.readyState == 4){
                   message.success('上传成功')
                   console.log(file)
                    self.setState({
                        tencent_host: info.url,
                        tencent_key: info.key,
                        previewImage:info.url + info.key
                    },()=> console.log(self.state.previewImage))
                }
            }
        })
    }
    //删除上传图片
    onRemove = info => {
        this.setState({
            fileList: [],
            previewImage:''
        })
    }
    render() {
        const { type, langs, source, visible, width, motitle, sourceLink, noticeType, pubTime, time, isTop, token, title, keyword, digest, previewVisible, previewImage, fileList, modalHtml } = this.state
        console.log(previewImage)
        let uploadUrl;
        let nfileList = fileList.slice(-1)
        console.log(nfileList)
        if (window.location.protocol === 'https:') {
            uploadUrl = 'https://up.qbox.me';
        } else {
            uploadUrl = 'http://up.qiniu.com';
        }
        let imgName = new Date().getTime()
        imgName = "bitglobal/newsupload/" + imgName;
        const uploadButton = (
            <div>
                <Icon type="plus" />
                <div className="ant-upload-text">上传图片</div>
            </div>
        );
        return (
            <div className={previewVisible?"right-con ":'right-con'}>

                <div className="page-title">
                    {/*当前位置：系统中心>系统管理>新闻发布*/}
                </div>
                <div className="clearfix"></div>
                <div className={previewVisible ? 'mask' : ''}></div>
                
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <Button style={{ float: 'right' }} type="primary" onClick={() => { this.show_click(0) }}>返回上一级</Button>
                    </div>
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">类型：</label>
                                        <div className="col-sm-8">
                                            <Select value={type} style={{ width: SELECTWIDTH }} onChange={this.type_handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="1">公告</Option>
                                                <Option value="2">新闻</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>
                                {/* <div className="col-md-3 col-sm-3 col-xs-3">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易类型：</label>
                                        <div className="col-sm-8">
                                            <Select name="dealType" value={dealType} style={{width:SELECTWIDTH}}  onChange={this.business_handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="0">币币</Option>
                                                <Option value="1">OTC</Option>                                            
                                            </Select>
                                        </div>
                                    </div>
                                </div> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">语言：</label>
                                        <div className="col-sm-8">
                                            <Select value={langs} style={{ width: SELECTWIDTH }} onChange={this.langs_handleChange}>
                                                <Option value="">请选择</Option>
                                                <Option value="cn">简体中文</Option>
                                                <Option value="hk">繁体中文</Option>
                                                <Option value="en">ENGLISH</Option>
                                                <Option value="jp">日语</Option>
                                                <Option value="kr">韩语</Option>
                                            </Select>
                                        </div>
                                    </div>
                                </div>

                                {
                                    type == 2 && <div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">来源：<i>*</i></label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="source" value={source} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">来源链接：<i>*</i></label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="sourceLink" value={sourceLink} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>    
                                        </div>
                                    }
                                        {type ==1&&<div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">公告类型：</label>
                                                <div className="col-sm-8">
                                                    <Select value={noticeType} style={{ width: SELECTWIDTH }} onChange={this.noticeType_handleChange}>
                                                        <Option value="">请选择</Option>
                                                        <Option value="1">新币上线</Option>
                                                        <Option value="2">系统维护</Option>
                                                        <Option value="3">最新活动</Option>
                                                        <Option value="4">平台动态</Option>
                                                    </Select>
                                                </div>
                                            </div>
                                        </div>}
                                


                                <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="form-group">
                                        <label className="col-sm-2 control-label">是否置顶：</label>
                                        <div className="col-sm-4">
                                            <RadioGroup onChange={this.radioOnChange} value={isTop}>
                                                <Radio value={false}>否</Radio>
                                                <Radio value={true}>是</Radio>
                                            </RadioGroup>
                                            <span className="color_999">是否置于新闻顶部</span>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">标题：<i>*</i></label>
                                        <div className="col-sm-5">
                                            {
                                                langs == 'cn' || langs == 'hk'
                                                ? 
                                                <input type="text" maxLength={40} className="form-control input_MaxWidth" name="title" value={title} onChange={this.handleInputChange} placeholder='请输入有效标题(40个字以内)' />
                                               : 
                                                <input type="text" className="form-control input_MaxWidth" name="title" value={title} onChange={this.handleInputChange} />
                                            }
                                        </div>
                                        {/* <div className="col-sm-3">
                                            <span className="color_999">请输入有效标题</span>
                                        </div> */}
                                    </div>
                                </div>

                                <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">关键字：<i>*</i></label>
                                        <div className="col-sm-5">
                                            <input type="text" className="form-control input_MaxWidth" name="keyword" value={keyword} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="form-group form-group-heighAuto">
                                        <label className="col-sm-3 control-label">摘要：<i>*</i></label>
                                        <div className="col-sm-5">
                                            <TextArea rows={6} onChange={this.handleInputTextAreaChange} value={digest} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="form-group form-group-heighAuto">
                                        <label className="col-sm-3 control-label">正文：<i>*</i></label>

                                        <div className="col-md-8 col-sm-8 col-xs-8">
                                            {/* 将生成编辑器 */}
                                            <div ref="editorElem" style={{ textAlign: 'left' }}>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="form-group">
                                        <label className="col-sm-1 control-label">发布时间：<i>*</i></label>
                                        <div className={`col-sm-3`}>
                                            {
                                                pubTime == "" ?
                                                    <DatePicker
                                                        showTime={{ format: TIMEFORMAT_DAYS_ss }}
                                                        format={TIMEFORMAT_ss}
                                                        onChange={this.time_onChange}
                                                    />
                                                    : <DatePicker
                                                        showTime={{ format: TIMEFORMAT_DAYS_ss }}
                                                        format={TIMEFORMAT_ss}
                                                        onChange={this.time_onChange}
                                                        value={moment(pubTime, TIMEFORMAT_ss)}
                                                    />
                                            }
                                        </div>
                                    </div>
                                </div>
                                {
                                    type != 1 && <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group form-group-heighAuto">
                                        <div className={previewVisible ? 'showDiv' : 'concealDiv'} >
                                                   
                                                    <div style={{position: 'fixed',left: 0,right:0,top: '150px',margin: 'auto',width: '500px', height: '600px' ,zIndex:2222}}>
                                                    <div className='choice'><span style={{  color: '#fff', cursor: "pointer", }} onClick={this.handleChoice}>X</span></div>
                                                                <div style={{maxWidth:'500px',maxHeight:'400px'}}><img style={{width:'100%'}} src={previewImage} alt="" /></div>
                                                    </div>
                                                </div>
                                            <label className="col-sm-1 control-label">封面图片：</label>
                                            <div className="col-sm-8">
                                                <Upload
                                                    // action={uploadUrl}
                                                    listType="picture-card"
                                                    fileList={nfileList}
                                                    onRemove={this.onRemove}
                                                    onPreview={this.handlePreview}
                                                    onChange={this.handleChange}
                                                    //data={{ token: token, key: imgName}}
                                                    customRequest={(e) => {
                                                        //console.log(e)
                                                        //this.handlePreview(e.file)
                                                        this.uploadImageCos(e.file)
                                                    }}

                                                >
                                                    {fileList.length >= 1 ? null : uploadButton}

                                                </Upload>

                                                
                                            </div>
                                        </div>
                                    </div>
                                }

                                <div className="col-md-6 col-sm-6 col-xs-6">
                                    <div className="form-group form-group-heighAuto">
                                        <label className="col-sm-1 control-label"></label>
                                        <div style={{ float: 'right' }}>
                                            <Button size="large" type="primary" onClick={this.resetNews}>发布</Button>
                                            <Button size="large" type="primary" onClick={this.preview} >预览</Button>
                                            <Button size="large" type="primary" onClick={this.sourceCode}>显示源码</Button>
                                        </div>

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={motitle}
                    width={width}
                    maskClosable={false}
                    footer={this.footer}
                    onCancel={this.vahandleCancel}
                >
                    {modalHtml}
                </Modal>
            </div>

        )
    }

}
