import React from 'react'
import { Tabs, Button, message, Radio, Select } from 'antd';
import E from 'wangeditor'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP, SELECTWIDTH } from '../../../conf'
import FundsTypeList from '../select/fundsType'
const TabPane = Tabs.TabPane;
const RadioGroup = Radio.Group;
const Option = Select.Option
export default class CurrencyTextRelease extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            coinName_cn: '',
            coinName_en: '',
            coinName_hk: '',
            coinName_jp: '',
            coinName_kr: '',
            coinFullName_cn: '',
            coinFullName_en: '',
            coinFullName_hk: '',
            coinFullName_jp: '',
            coinFullName_kr: '',
            urlJsonCN: '',
            urlJsonEN: '',
            urlJsonHK: '',
            coinImg: '',
            editorContent_cn_1: "",//1简介
            editorContent_cn_2: "",//2描述
            editorContent_hk_1: "",
            editorContent_hk_2: "",
            editorContent_en_1: "",
            editorContent_en_2: "",

            editorContent_jp_1: "",
            editorContent_jp_2: "",
            editorContent_kr_1: "",
            editorContent_kr_2: "",
            now_modify: false,
            modify_update: false,
            status: 1,
            coinId: '',
            coinName: '',
            tableList: [],
            selectValue: [],
            tableListHk: [],
            fundsType: '请选择',
            tabKey: 'en',
            key: '0'
        }
        this.btnStart = false;
        this.callback = this.callback.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.callback = this.callback.bind(this)
        this.editorConfig = this.editorConfig.bind(this)
        this.onSave = this.onSave.bind(this)
        this.onSaveAndSubmit = this.onSaveAndSubmit.bind(this)
        this.onVerify = this.onVerify.bind(this)
        this.onEdit = this.onEdit.bind(this)
        this.handleSelectChange = this.handleSelectChange.bind(this)
    }

    componentDidMount() {
        console.log(this.props)
        let { editorContent_cn_1,
            editorContent_hk_1,
            editorContent_en_1,
            editorContent_cn_2,
            editorContent_hk_2,
            editorContent_en_2,
            editorContent_jp_1,
            editorContent_jp_2,
            editorContent_kr_1,
            editorContent_kr_2
        } = this.state

        this.setState({
            modify_update: true
        })
        if (!this.state.modify_update) {
            this.editorConfig(this.refs.editorElem_cn_1, "cn_1", editorContent_cn_1);//富文本编译器
            this.editorConfig(this.refs.editorElem_cn_2, "cn_2", editorContent_cn_2);//富文本编译器
            this.editorConfig(this.refs.editorElem_hk_1, "hk_1", editorContent_hk_1);//富文本编译器
            this.editorConfig(this.refs.editorElem_hk_2, "hk_2", editorContent_hk_2);//富文本编译器
            this.editorConfig(this.refs.editorElem_en_1, "en_1", this.formatStrToEditor(editorContent_en_1));//富文本编译器
            this.editorConfig(this.refs.editorElem_en_2, "en_2", this.formatStrToEditor(editorContent_en_2));//富文本编译器

            this.editorConfig(this.refs.editorElem_jp_1, "jp_1", editorContent_jp_1);//富文本编译器
            this.editorConfig(this.refs.editorElem_jp_2, "jp_2", editorContent_jp_2);//富文本编译器
            this.editorConfig(this.refs.editorElem_kr_1, "kr_1", editorContent_kr_1);//富文本编译器
            this.editorConfig(this.refs.editorElem_kr_2, "kr_2", editorContent_kr_2);//富文本编译器
        }

    }
    componentWillReceiveProps(nextProps) {
        let { editorContent_cn_1,
            editorContent_hk_1,
            editorContent_en_1,
            editorContent_cn_2,
            editorContent_hk_2,
            editorContent_en_2,
            editorContent_jp_1,
            editorContent_jp_2,
            editorContent_kr_1,
            editorContent_kr_2
        } = this.state
        //console.log(nextProps.activityId)
        if (nextProps.coinUrl) {
            // console.log(nextProps.coinUrl)
            this.setState({
                coinImg: nextProps.coinUrl
            })
        }
        if (nextProps.activityId) {
            console.log(nextProps.activityId.coinContentJson);
            // console.log(JSON.parse(nextProps.activityId.coinContentJson.replace(/(^\")|(\"$)/,"\'")));
            editorContent_cn_2 = JSON.parse(nextProps.activityId.coinContentJson).cn;// 简体规则
            editorContent_hk_2 = JSON.parse(nextProps.activityId.coinContentJson).hk;//繁体规则
            editorContent_en_2 = JSON.parse(nextProps.activityId.coinContentJson).en;// 英文规则
            editorContent_jp_2 = JSON.parse(nextProps.activityId.coinContentJson).jp;// 英文规则
            editorContent_kr_2 = JSON.parse(nextProps.activityId.coinContentJson).kr;// 英文规则

            editorContent_cn_1 = JSON.parse(nextProps.activityId.introductionJson).cn;// 
            editorContent_hk_1 = JSON.parse(nextProps.activityId.introductionJson).hk;//
            editorContent_en_1 = JSON.parse(nextProps.activityId.introductionJson).en;// 
            editorContent_jp_1 = JSON.parse(nextProps.activityId.introductionJson).jp;// 
            editorContent_kr_1 = JSON.parse(nextProps.activityId.introductionJson).kr;// 

            this.setState({
                coinName_cn: JSON.parse(nextProps.activityId.coinNameJson).cn,
                coinName_en: JSON.parse(nextProps.activityId.coinNameJson).en,
                coinName_hk: JSON.parse(nextProps.activityId.coinNameJson).hk,
                coinName_jp: JSON.parse(nextProps.activityId.coinNameJson).jp,
                coinName_kr: JSON.parse(nextProps.activityId.coinNameJson).kr,
                coinFullName_cn: JSON.parse(nextProps.activityId.coinFullNameJson).cn,
                coinFullName_en: JSON.parse(nextProps.activityId.coinFullNameJson).en,
                coinFullName_hk: JSON.parse(nextProps.activityId.coinFullNameJson).hk,
                coinFullName_jp: JSON.parse(nextProps.activityId.coinFullNameJson).jp,
                coinFullName_kr: JSON.parse(nextProps.activityId.coinFullNameJson).kr,
                urlJsonCN: JSON.parse(nextProps.activityId.urlJson).cn,
                urlJsonEN: JSON.parse(nextProps.activityId.urlJson).en,
                urlJsonHK: JSON.parse(nextProps.activityId.urlJson).hk,

                editorContent_cn_1: editorContent_cn_1,
                editorContent_cn_2: editorContent_cn_2,
                editorContent_hk_1: editorContent_hk_1,
                editorContent_hk_2: editorContent_hk_2,
                editorContent_en_1: this.formatStrToEditor(editorContent_en_1),
                editorContent_en_2: this.formatStrToEditor(editorContent_en_2),
                editorContent_jp_1: editorContent_jp_1,
                editorContent_jp_2: editorContent_jp_2,
                editorContent_kr_1: editorContent_kr_1,
                editorContent_kr_2: editorContent_kr_2,
                now_modify: true,
                activityId: nextProps.activityId,
                status: nextProps.activityId.status,
                coinId: nextProps.activityId.coinId
            })
        }
        this.setState({
            modify_update: true
        })
        if (!this.state.modify_update) {
            this.editorConfig(this.refs.editorElem_cn_1, "cn_1", editorContent_cn_1);//富文本编译器
            this.editorConfig(this.refs.editorElem_cn_2, "cn_2", editorContent_cn_2);//富文本编译器
            this.editorConfig(this.refs.editorElem_hk_1, "hk_1", editorContent_hk_1);//富文本编译器
            this.editorConfig(this.refs.editorElem_hk_2, "hk_2", editorContent_hk_2);//富文本编译器
            this.editorConfig(this.refs.editorElem_en_1, "en_1", this.formatStrToEditor(editorContent_en_1));//富文本编译器
            this.editorConfig(this.refs.editorElem_en_2, "en_2", this.formatStrToEditor(editorContent_en_2));//富文本编译器

            this.editorConfig(this.refs.editorElem_jp_1, "jp_1", editorContent_jp_1);//富文本编译器
            this.editorConfig(this.refs.editorElem_jp_2, "jp_2", editorContent_jp_2);//富文本编译器
            this.editorConfig(this.refs.editorElem_kr_1, "kr_1", editorContent_kr_1);//富文本编译器
            this.editorConfig(this.refs.editorElem_kr_2, "kr_2", editorContent_kr_2);//富文本编译器
        }

    }

    //富文本编译器
    editorConfig(elem, index, propstate) {
        const editor = new E(elem)
        // 下面两个配置，使用其中一个即可显示“上传图片”的tab。但是两者不要同时使用！！！
        // editor.customConfig.uploadImgShowBase64 = true   // 使用 base64 保存图片
        // editor.customConfig.uploadImgServer = '/upload'  // 上传图片到服务器
        // 使用 onchange 函数监听内容的变化，并实时更新到 state 中

        editor.customConfig.zIndex = 1
        editor.customConfig.pasteIgnoreImg = false
        editor.customConfig.uploadImgShowBase64 = true   // 使用 base64 保存图片
        editor.customConfig.showLinkImg = true // 隐藏“网络图片”tab
        editor.customConfig.uploadImgMaxSize = 300 * 1024;

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
            // 'image',  // 插入图片
            // 'table',  // 表格
            // 'video',  // 插入视频
            // 'code',  // 插入代码
            'undo',  // 撤销
            'redo'  // 重复
        ]
        editor.customConfig.linkCheck = function (text, link) {


            return true // 返回 true 表示校验成功
            // return '验证失败' // 返回字符串，即校验失败的提示信息
        }

        editor.customConfig.onchange = html => {
            //    if(html =='<p><br></p>'){
            //        html=''
            //    }
            html = html.replace(/target="_blank" style="background-color: rgb\(255, 255, 255\);"/ig, "target='_blank'")
            if (index == "cn_1") {
                this.setState({
                    editorContent_cn_1: html
                })
            }
            if (index == "cn_2") {
                this.setState({
                    editorContent_cn_2: html
                })
            }
            if (index == "hk_1") {
                this.setState({
                    editorContent_hk_1: html
                })
            }
            if (index == "hk_2") {
                this.setState({
                    editorContent_hk_2: html
                })
            }
            if (index == "en_1") {
                this.setState({
                    editorContent_en_1: html
                })
            }
            if (index == "en_2") {
                this.setState({
                    editorContent_en_2: html
                })
            }

            if (index == "jp_1") {
                this.setState({
                    editorContent_jp_1: html
                })
            }
            if (index == "jp_2") {
                this.setState({
                    editorContent_jp_2: html
                })
            }

            if (index == "kr_1") {
                this.setState({
                    editorContent_kr_1: html
                })
            }
            if (index == "kr_2") {
                this.setState({
                    editorContent_kr_2: html
                })
            }
        }

        editor.customConfig.pasteTextHandle = content => {
            // console.log(content)
            let filterContent = content.replace(/<head[^>]*?>[\s\S]*head>/gi, '');//过滤head标签中
            filterContent = filterContent.replace(/<script[^>]*?>[\\s\\S]*script>/gi, '');//过滤js
            // filterContent = content.replace(/<[^>]+>/g, '');//过滤标签
            // filterContent = content.replace(/\\s*|\t|\r|\n|&nbsp;/g,"")//过滤空格，换行
            // console.log(this.formatStr(filterContent))
            // console.log(filterContent)
            return filterContent
        }

        editor.create()
        editor.txt.html(propstate)
    }
    callback(key) {
        this.setState({
            tabKey: key
        })
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
    handleChange = (value) => {
        const oldValue = value.label
        // console.log(this.props.fundsList)
        if (this.props.fundsList.includes(oldValue)) {
            message.warning('该币种已经存在，请选择其他币种。')
            this.setState({
                fundsType: '请选择',
                key: '0'
            })
        } else {
            this.setState({
                fundsType: value.label,
                key: value.key
                // [selectValue]:value
            })
        }
    }
    handleSelectChange = (value, option) => {
        // console.log(value,option)
    }
    formatStrToEditor = (str = "") => {
        let newStr = '';
        for (let i = 0; i < str.length; i++) {
            if (str[i] == "\\") {
                newStr += "";
                continue;
            }
            newStr += str[i];
        }
        console.log(newStr)
        return newStr;
    }
    formatStr = (str) => {
        let newStr = "", preStr = "";

        for (let i = 0; i < str.length; i++) {

            if (str[i] == "<") {
                this.btnStart = true
            } else if (str[i] == ">") {
                this.btnStart = false
            }

            if (str[i] == "\"" && preStr != "\\") {
                newStr = newStr + "\\\\\\" + str[i];
                preStr = str[i];
                continue;
            }
            preStr = str[i];
            newStr = newStr + str[i];
        }
        return newStr;
    }
    //保存
    onSave() {
        const { coinName_cn, coinName_en, coinName_hk, coinFullName_cn, coinFullName_en, coinFullName_hk, editorContent_cn_1, editorContent_cn_2, editorContent_hk_1, editorContent_hk_2, editorContent_en_1, editorContent_en_2, urlJsonCN, urlJsonEN, urlJsonHK, coinImg, status, fundsType,
            coinFullName_jp, coinFullName_kr, editorContent_jp_1, editorContent_kr_1,
            editorContent_jp_2, editorContent_kr_2,
            //  urlJsonJP, urlJsonKR 
        } = this.state
        if (this.onVerify()) {
            axios.post(DOMAIN_VIP + '/coin/insert', qs.stringify({
                coinCN: fundsType,
                coinEN: fundsType,
                coinHK: fundsType,
                coinJP: fundsType,
                coinKR: fundsType,
                coinFullNameJsonCN: coinFullName_cn,
                coinFullNameJsonEN: coinFullName_en,
                coinFullNameJsonHK: coinFullName_hk,
                coinFullNameJsonJP: coinFullName_jp,
                coinFullNameJsonKR: coinFullName_kr,
                introductionJsonCN: this.formatStr(editorContent_cn_1),
                introductionJsonEN: this.formatStr(editorContent_en_1),
                introductionJsonHK: this.formatStr(editorContent_hk_1),
                introductionJsonJP: this.formatStr(editorContent_jp_1),
                introductionJsonKR: this.formatStr(editorContent_kr_1),
                coinContentJsonCN: this.formatStr(editorContent_cn_2),
                coinContentJsonEN: this.formatStr(editorContent_en_2),
                coinContentJsonHK: this.formatStr(editorContent_hk_2),
                coinContentJsonJP: this.formatStr(editorContent_jp_2),
                coinContentJsonKR: this.formatStr(editorContent_kr_2),
                urlJsonCN: urlJsonCN,
                urlJsonEN: urlJsonEN,
                urlJsonHK: urlJsonHK,
                // urlJsonJP: urlJsonJP,
                // urlJsonKR: urlJsonKR,
                img: coinImg,
                // status:status,
            })).then(res => {
                const result = res.data;
                if (result.code == 0) {
                    message.success(result.msg)
                    this.props.show_click(0)
                } else {
                    message.warning(result.msg);
                }
            })
        }

    }
    //保存并发布
    onSaveAndSubmit() {

    }
    //修改
    onEdit() {
        const { coinName_cn, coinName_en, coinName_hk, coinFullName_cn, coinFullName_en, coinFullName_hk, editorContent_cn_1, editorContent_cn_2, editorContent_hk_1, editorContent_hk_2, editorContent_en_1, editorContent_en_2, urlJsonCN, urlJsonEN, urlJsonHK, coinImg, status, coinId,
            coinFullName_jp, coinFullName_kr, editorContent_jp_1, editorContent_kr_1,
            editorContent_jp_2, editorContent_kr_2,fundsType,
            coinName_jp,coinName_kr
            // urlJsonJP, urlJsonKR 
        } = this.state
        console.log(this.formatStr(editorContent_en_2))
        axios.post(DOMAIN_VIP + '/coin/update', qs.stringify({
            coinCN: coinName_cn,
            coinEN: coinName_en,
            coinHK: coinName_hk,
            coinJP: coinName_jp,
            coinKR: coinName_kr,
            coinFullNameJsonCN: coinFullName_cn,
            coinFullNameJsonEN: coinFullName_en,
            coinFullNameJsonHK: coinFullName_hk,
            coinFullNameJsonJP: coinFullName_jp,
            coinFullNameJsonKR: coinFullName_kr,
            introductionJsonCN: this.formatStr(editorContent_cn_1),
            introductionJsonEN: this.formatStr(editorContent_en_1),
            introductionJsonHK: this.formatStr(editorContent_hk_1),
            introductionJsonJP: this.formatStr(editorContent_jp_1),
            introductionJsonKR: this.formatStr(editorContent_kr_1),
            coinContentJsonCN: this.formatStr(editorContent_cn_2),
            coinContentJsonEN: this.formatStr(editorContent_en_2),
            coinContentJsonHK: this.formatStr(editorContent_hk_2),
            coinContentJsonJP: this.formatStr(editorContent_jp_2),
            coinContentJsonKR: this.formatStr(editorContent_kr_2),
            urlJsonCN: urlJsonCN,
            urlJsonEN: urlJsonEN,
            urlJsonHK: urlJsonHK,
            // urlJsonJP: urlJsonJP,
            // urlJsonKR: urlJsonKR,
            coinId,
            // img:coinImg,
            // status:status,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                message.success(result.msg)
                this.props.show_click(0)
            } else {
                message.warning(result.msg);
            }
        })
    }
    //验证输入框
    onVerify() {
        const { coinName_cn, coinName_en, coinName_hk, coinFullName_cn, coinFullName_en, coinFullName_hk, editorContent_cn_1, editorContent_cn_2, editorContent_hk_1, editorContent_hk_2, editorContent_en_1, editorContent_en_2,
            urlJsonCN, urlJsonEN, urlJsonHK, coinImg, fundsType, tabKey,
            coinFullName_jp, editorContent_jp_1, editorContent_jp_2,
            coinFullName_kr, editorContent_kr_1, editorContent_kr_2 } = this.state
            // console.log(tabKey)
            // console.log(fundsType)
        switch (tabKey) {
            case 'cn':
                if (fundsType == '请选择') {
                    message.warning('简体币种名称不能为空！')
                    return false;
                }
                if (coinFullName_cn == '') {
                    message.warning('简体币种全称能为空！')
                    return false;
                }
                if (editorContent_cn_1 == '') {
                    message.warning('简体简介不能为空！')
                    return false;
                }
                if (editorContent_cn_2 == '') {
                    message.warning('简体内容不能为空！')
                    return false;
                }
                // if(urlJsonCN==''){
                //     message.warning('简体币种链接不能为空！')
                //     return false;
                // }
                break;
            case 'hk':
                if (fundsType == '请选择') {
                    message.warning('繁体币种名称不能为空！')
                    return false;
                }
                if (coinFullName_hk == '') {
                    message.warning('繁体币种全称能为空！')
                    return false;
                }
                if (editorContent_hk_1 == '') {
                    message.warning('繁体简介不能为空！')
                    return false;
                }
                if (editorContent_hk_2 == '') {
                    message.warning('繁体内容不能为空！')
                    return false;
                }
                // if(urlJsonHK==''){
                //     message.warning('繁体币种链接不能为空！')
                //     return false;
                // }
                break;
            case 'en':
                if (fundsType == '请选择') {
                    message.warning('英文币种名称不能为空！')
                    return false;
                }

                if (coinFullName_en == '') {
                    message.warning('英文币种全称能为空！')
                    return false;
                }
                // if(urlJsonEN==''){
                //     message.warning('英文币种链接不能为空！')
                //     return false;
                // }
                if (editorContent_en_1 == '') {
                    message.warning('英文简介不能为空！')
                    return false;
                }
                if (editorContent_en_2 == '') {
                    message.warning('英文内容不能为空！')
                    return false;
                }
                break;
            case 'jp':
                if (fundsType == '请选择') {
                    message.warning('日语币种名称不能为空！')
                    return false;
                }

                if (coinFullName_jp == '') {
                    message.warning('日语币种全称能为空！')
                    return false;
                }
                // if(urlJsonEN==''){
                //     message.warning('日语币种链接不能为空！')
                //     return false;
                // }
                if (editorContent_jp_1 == '') {
                    message.warning('日语简介不能为空！')
                    return false;
                }
                if (editorContent_jp_2 == '') {
                    message.warning('日语内容不能为空！')
                    return false;
                }
                break
            case 'kr':
                if (fundsType == '请选择') {
                    message.warning('韩语币种名称不能为空！')
                    return false;
                }

                if (coinFullName_kr == '') {
                    message.warning('韩语币种全称能为空！')
                    return false;
                }
                // if(urlJsonEN==''){
                //     message.warning('韩语币种链接不能为空！')
                //     return false;
                // }
                if (editorContent_kr_1 == '') {
                    message.warning('韩语简介不能为空！')
                    return false;
                }
                if (editorContent_kr_2 == '') {
                    message.warning('韩语内容不能为空！')
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    render() {
        const { coinName_cn, coinName_en, coinName_hk, coinFullName_cn, coinFullName_en, coinFullName_hk, activityId, urlJsonCN, urlJsonEN, urlJsonHK, coinImg, status, tableList, tableListHk, tableListEn, fundsType, key,
            coinName_jp,coinFullName_jp,coinName_kr,coinFullName_kr } = this.state
        return (
            <div>
                {/* <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">    
                        <label className="col-sm-2 control-label">状态:</label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.handleInputChange} name="status" value={status}>
                                <Radio value={0}>关闭</Radio>
                                <Radio value={1}>开启</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                </div> */}
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <Tabs defaultActiveKey="en" onChange={this.callback}>
                        <TabPane tab="简体" key="cn" forceRender={true}>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">


                                    {/* <input type="text" className="form-control" name="coinName_cn" value={coinName_cn} onChange={this.handleInputChange} /> */}
                                    {coinName_cn ?
                                        // <Select value={"1"}>
                                        //      <Option value={"1"}>1</Option>
                                        // </Select>
                                        <div>
                                            <label className="col-sm-3 control-label">币种名称：<i>*</i></label>
                                            <div className='col-sm-5'>
                                                <Select value={coinName_cn} style={{ width: SELECTWIDTH }} disabled={true}>
                                                    <Option value={coinName_cn}>{coinName_cn}</Option>
                                                </Select>
                                            </div>

                                        </div>

                                        :
                                        <FundsTypeList fundsType={fundsType} handleChange={this.handleChange} title={'币种名称'} value={key} />
                                    }

                                </div>
                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">全称：<i>*</i></label>
                                    <div className="col-sm-5">
                                        <input type="text" className="form-control" name="coinFullName_cn" value={coinFullName_cn} onChange={this.handleInputChange} />
                                    </div>
                                </div>
                            </div>
                            {/* <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">币种链接：<i>*</i></label>
                                    <div className="col-sm-5">
                                        <input type="text" className="form-control" name="urlJsonCN" value={urlJsonCN} onChange={this.handleInputChange} />
                                    </div>
                                </div>
                            </div> */}
                            <label className="col-sm-3 mb10">简介：<i>*</i></label>
                            <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                {/* 将生成编辑器 */}
                                <div ref="editorElem_cn_1" style={{ textAlign: 'left' }}>
                                </div>
                            </div>
                            <label className="col-sm-3 mb10">内容：<i>*</i></label>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                {/* 将生成编辑器 */}
                                <div ref="editorElem_cn_2" style={{ textAlign: 'left' }}>
                                </div>
                            </div>
                        </TabPane>
                        <TabPane tab="繁体" key="hk" forceRender={true}>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">

                                    {coinName_hk ?
                                        // <Select value={"1"}>
                                        //      <Option value={"1"}>1</Option>
                                        // </Select>
                                        <div >
                                            <label className="col-sm-3 control-label">币种名称：<i>*</i></label>
                                            <div className='col-sm-5'>
                                                <Select value={coinName_hk} style={{ width: SELECTWIDTH }} disabled={true}>
                                                    <Option value={coinName_hk}>{coinName_hk}</Option>
                                                </Select>
                                            </div>

                                        </div>

                                        :
                                        <FundsTypeList fundsType={fundsType} handleChange={this.handleChange} title={'币种名称'} handleSelectChange={this.handleSelectChange} />
                                    }
                                </div>

                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">全称：<i>*</i></label>
                                    <div className="col-sm-5">
                                        <input type="text" className="form-control" name="coinFullName_hk" value={coinFullName_hk} onChange={this.handleInputChange} />
                                    </div>
                                </div>
                            </div>
                            {/* <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">币种链接：<i>*</i></label>
                                    <div className="col-sm-5">
                                        <input type="text" className="form-control" name="urlJsonHK" value={urlJsonHK} onChange={this.handleInputChange} />
                                    </div>
                                </div>
                            </div> */}
                            <label className="col-sm-3 mb10">简介：<i>*</i></label>
                            <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                {/* 将生成编辑器 */}
                                <div ref="editorElem_hk_1" style={{ textAlign: 'left' }}>
                                </div>
                            </div>
                            <label className="col-sm-3 mb10">内容：<i>*</i></label>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                {/* 将生成编辑器 */}
                                <div ref="editorElem_hk_2" style={{ textAlign: 'left' }}>
                                </div>
                            </div>
                        </TabPane>
                        <TabPane tab="英文" key="en" forceRender={true}>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">

                                    {/* <input type="text" className="form-control" name="coinName_en" value={coinName_en} onChange={this.handleInputChange} /> */}
                                    {coinName_en ?
                                        // <Select value={"1"}>
                                        //      <Option value={"1"}>1</Option>
                                        // </Select>
                                        <div >
                                            <label className="col-sm-3 control-label">币种名称：<i>*</i></label>
                                            <div className='col-sm-5'>
                                                <Select value={coinName_en} style={{ width: SELECTWIDTH }} disabled={true}>
                                                    <Option value={coinName_en}>{coinName_en}</Option>
                                                </Select>
                                            </div>

                                        </div>

                                        :
                                        <FundsTypeList fundsType={fundsType} handleChange={this.handleChange} title={'币种名称'} handleSelectChange={this.handleSelectChange} />
                                    }
                                </div>

                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">全称：<i>*</i></label>
                                    <div className="col-sm-5">
                                        <input type="text" className="form-control" name="coinFullName_en" value={coinFullName_en} onChange={this.handleInputChange} />

                                    </div>
                                </div>
                            </div>
                            {/* <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">币种链接：<i>*</i></label>
                                    <div className="col-sm-5">
                                        <input type="text" className="form-control" name="urlJsonEN" value={urlJsonEN} onChange={this.handleInputChange} />
                                    </div>
                                </div>
                            </div> */}
                            <label className="col-sm-3 mb10">简介：<i>*</i></label>
                            <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                {/* 将生成编辑器 */}
                                <div ref="editorElem_en_1" style={{ textAlign: 'left' }}>
                                </div>
                            </div>
                            <label className="col-sm-3 mb10">内容：<i>*</i></label>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                {/* 将生成编辑器 */}
                                <div ref="editorElem_en_2" style={{ textAlign: 'left' }}>
                                </div>
                            </div>
                        </TabPane>

                        <TabPane tab="日语" key="jp" forceRender={true}>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">

                                    {/* <input type="text" className="form-control" name="coinName_en" value={coinName_en} onChange={this.handleInputChange} /> */}
                                    {coinName_en ?
                                        // <Select value={"1"}>
                                        //      <Option value={"1"}>1</Option>
                                        // </Select>
                                        <div >
                                            <label className="col-sm-3 control-label">币种名称：<i>*</i></label>
                                            <div className='col-sm-5'>
                                                <Select value={coinName_en} style={{ width: SELECTWIDTH }} disabled={true}>
                                                    <Option value={coinName_en}>{coinName_en}</Option>
                                                </Select>
                                            </div>

                                        </div>

                                        :
                                        <FundsTypeList fundsType={fundsType} handleChange={this.handleChange} title={'币种名称'} handleSelectChange={this.handleSelectChange} />
                                    }
                                </div>

                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">全称：<i>*</i></label>
                                    <div className="col-sm-5">
                                        <input type="text" className="form-control" name="coinFullName_jp" value={coinFullName_jp} onChange={this.handleInputChange} />

                                    </div>
                                </div>
                            </div>
                            {/* <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">币种链接：<i>*</i></label>
                                    <div className="col-sm-5">
                                        <input type="text" className="form-control" name="urlJsonEN" value={urlJsonEN} onChange={this.handleInputChange} />
                                    </div>
                                </div>
                            </div> */}
                            <label className="col-sm-3 mb10">简介：<i>*</i></label>
                            <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                {/* 将生成编辑器 */}
                                <div ref="editorElem_jp_1" style={{ textAlign: 'left' }}>
                                </div>
                            </div>
                            <label className="col-sm-3 mb10">内容：<i>*</i></label>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                {/* 将生成编辑器 */}
                                <div ref="editorElem_jp_2" style={{ textAlign: 'left' }}>
                                </div>
                            </div>
                        </TabPane>

                        <TabPane tab="韩语" key="kr" forceRender={true}>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">

                                    {/* <input type="text" className="form-control" name="coinName_en" value={coinName_en} onChange={this.handleInputChange} /> */}
                                    {coinName_en ?
                                        // <Select value={"1"}>
                                        //      <Option value={"1"}>1</Option>
                                        // </Select>
                                        <div >
                                            <label className="col-sm-3 control-label">币种名称：<i>*</i></label>
                                            <div className='col-sm-5'>
                                                <Select value={coinName_en} style={{ width: SELECTWIDTH }} disabled={true}>
                                                    <Option value={coinName_en}>{coinName_en}</Option>
                                                </Select>
                                            </div>

                                        </div>

                                        :
                                        <FundsTypeList fundsType={fundsType} handleChange={this.handleChange} title={'币种名称'} handleSelectChange={this.handleSelectChange} />
                                    }
                                </div>

                            </div>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">全称：<i>*</i></label>
                                    <div className="col-sm-5">
                                        <input type="text" className="form-control" name="coinFullName_kr" value={coinFullName_kr} onChange={this.handleInputChange} />

                                    </div>
                                </div>
                            </div>
                            {/* <div className="col-md-12 col-sm-12 col-xs-12">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">币种链接：<i>*</i></label>
                                    <div className="col-sm-5">
                                        <input type="text" className="form-control" name="urlJsonEN" value={urlJsonEN} onChange={this.handleInputChange} />
                                    </div>
                                </div>
                            </div> */}
                            <label className="col-sm-3 mb10">简介：<i>*</i></label>
                            <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                {/* 将生成编辑器 */}
                                <div ref="editorElem_kr_1" style={{ textAlign: 'left' }}>
                                </div>
                            </div>
                            <label className="col-sm-3 mb10">内容：<i>*</i></label>
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                {/* 将生成编辑器 */}
                                <div ref="editorElem_kr_2" style={{ textAlign: 'left' }}>
                                </div>
                            </div>
                        </TabPane>

                    </Tabs>
                    <div className="col-md-12 col-sm-12 col-xs-12 mb30">
                    </div>
                    <div className="col-md-12 col-sm-12 col-xs-12 mb30">
                        {activityId ? <Button type="primary" onClick={this.onEdit} size="large">保存修改</Button> : <div className="form-group center mb10 form-group-heighAuto">
                            <Button type="primary" onClick={this.onSave} size="large">保存</Button>
                            {/* <Button type="primary" onClick={this.onSaveAndSubmit} size="large">保存并发布</Button> */}
                        </div>}
                    </div>
                </div>
            </div>
        )
    }
}