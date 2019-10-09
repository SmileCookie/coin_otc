import Decorator from '../../decorator'
import { TIMEFORMAT, TIMEFORMAT_ss, SELECTWIDTH, SHOW_TIME_DEFAULT, TIME_PLACEHOLDER, TIMEFORMAT_DAYS_ss } from '../../../conf'
import { Button, Select, DatePicker, Input, Tabs, Icon, Upload, message, Modal,Radio } from 'antd'
import E from 'wangeditor'
import ModalPrevs from './modal/prevs'
import SourceCode from './modal/sourceCode'
const { RangePicker } = DatePicker;
const Option = Select.Option
const RadioGroup = Radio.Group
const { TextArea } = Input;
const TabPane = Tabs.TabPane;
const _langs = [{ cn: '中文' }, { en: '英文' }, { hk: '繁文' }, { jp: '日文' }, { kr: '韩文' },]
const _isTops = {
    false: '否',
    true: '是'
}
const objToArr = o => Object.keys(o).map(v => ({ k: v, c: o[v] }))

const SeOp = ({ title, value, onSelectChoose, ops }) => (
    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
        <div className="form-group">
            <label className="col-sm-3 control-label" dangerouslySetInnerHTML={{ __html: title }}></label>
            <div className="col-sm-9">
                <Select defaultValue='' value={value} style={{ width: SELECTWIDTH }} onChange={onSelectChoose} >
                    {[<Option key='' value=''>请选择</Option>, ...ops.map(op => <Option key={op.k} value={op.k}>{op.c}</Option>)]}
                </Select>
            </div>
        </div>
    </div>
)

const DF = {
    type: '',
    noticeType: '',
    pubTime: moment(),
    isTop: false,
    digest: '',
    source: '',
    sourceLink: '',
    id: '',
    cn: {
        fileList: []
    },
    en: {
        fileList: []
    },
    hk: {
        fileList: []
    },
    jp: {
        fileList: []
    },
    kr: {
        fileList: []
    },
}
// let dd = ['cn', 'en', 'hk'].reduce((res, key) => {
//     res[key] = {
//         title: '',
//         keyword: '',
//         digest: '',
//         content: '',
//         photo: '',
//         fileList: []
//     }
//     return res
// }, {})


@Decorator()
export default class NewNewsDistribute extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            type: '',
            noticeType: '',
            isTop: false,
            digest: '',
        }
        this.state = {
            // ...this.defaultState,
            tk: 'cn',
            // ...JSON.parse(JSON.stringify(dd)),
            fileList: [],
            // source: '',
            // sourceLink: '',
            // id: '',
            // cn: {
            //     fileList: []
            // },
            // en: {
            //     fileList: []
            // },
            // hk: {
            //     fileList: []
            // },
            // jp: {
            //     fileList: []
            // },
            // kr: {
            //     fileList: []
            // },
            ...JSON.parse(JSON.stringify(DF))
        }
        this.handlePreview = this.handlePreview.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handleChoice = this.handleChoice.bind(this)
        this.preview = this.preview.bind(this)
        this.vahandleCancel = this.vahandleCancel.bind(this)
        this.sourceCode = this.sourceCode.bind(this)
        this.textareaChange = this.textareaChange.bind(this)
        this.ckUid = this.ckUid.bind(this)
    }
    async componentDidMount() {
        await this.setState({
            ...JSON.parse(JSON.stringify(DF))
        })
        this.requestDetail(this.props.id)
    }

    tcb = tk => {
        this.setState({ tk })
    }

    //限制上传图片大小
    limitUpImgSize = (size) => {
        return size / 1024 < 300;
    }
    uploadImageCoscb = ({_key,url}) => {
        this.setState({
            [_key]: Object.assign({}, this.state[_key], { photo: url }),
            previewImage: url
        })
    }
    // 图片上传状态 
    handleChange = ({ fileList }, _key) => {
        const size = fileList.length
        if (!this.limitUpImgSize(fileList[size - 1].size)) return false;
        fileList[size - 1].status = 'done'
        this.setState({
            [_key]: Object.assign({}, this.state[_key], { fileList: fileList,  })
        })

    }
    onRemove = (info, _key) => {
        this.setState({
            [_key]: Object.assign({}, this.state[_key], { fileList: [],photo: '' })
        })
    }
    _handleInputChange = (event, _key) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [_key]: Object.assign({}, this.state[_key], { [name]: value })
        })
    }
    requestDetail = async (id) => {
        if (id) {
            const result = await this.request({ url: '/news/v2/detail' }, { id })
            const { type, noticeType, remark, top, pubTime, source, sourceLink, adminName } = result
            let en = {}, cn = {}, hk = {}, jp = {}; let kr = {};
            Object.keys(id ? result : {}).forEach(v => {
                let _f = v.split('_')[0], _l = v.split('_')[1]
                switch (_l) {
                    case 'en':
                        en[_f] = JSON.parse(JSON.stringify(result))[v] || ''
                        break;
                    case 'cn':
                        cn[_f] = JSON.parse(JSON.stringify(result))[v] || ''
                        break;
                    case 'hk':
                        hk[_f] = JSON.parse(JSON.stringify(result))[v] || ''
                        break;
                    case 'jp':
                        jp[_f] = JSON.parse(JSON.stringify(result))[v] || ''
                        break;
                    case 'kr':
                        kr[_f] = JSON.parse(JSON.stringify(result))[v] || ''
                        break;
                    default:
                        break
                }
            })
            this.setState({
                en: Object.assign({}, this.state.en, en),
                cn: Object.assign({}, this.state.cn, cn),
                hk: Object.assign({}, this.state.hk, hk),
                jp: Object.assign({}, this.state.jp, jp),
                kr: Object.assign({}, this.state.kr, kr),
                id: result.id,
                type, noticeType, remark, isTop:top, pubTime, source, sourceLink,
            }, () => {
                ['en', 'cn', 'hk', 'jp', 'kr'].forEach(v => {
                    this.editorConfig(this.refs[`content_${v}`], `content_${v}`, id ? this.state[v].content || '' : false, v)
                    if (this.state[v].photo) {
                        const tk_con = this.state[v]
                        if (tk_con.fileList.length === 0) {
                            tk_con.fileList.push({ url: this.state[v].photo, key: '-1', uid: '-1', })
                        } else {
                            tk_con.fileList[0].url = this.state[v].photo
                        }
                        this.setState({
                            [v]: tk_con
                        })
                    }
                })
            })
        } else {
            let en = {}, cn = {}, hk = {}, jp = {}; let kr = {};
            this.setState({
                en: Object.assign({}, this.state.en, en),
                cn: Object.assign({}, this.state.cn, cn),
                hk: Object.assign({}, this.state.hk, hk),
                jp: Object.assign({}, this.state.jp, jp),
                kr: Object.assign({}, this.state.kr, kr),
            }, () => {
                ['en', 'cn', 'hk', 'jp', 'kr'].forEach(v => {
                    this.editorConfig(this.refs[`content_${v}`], `content_${v}`, id ? this.state[v].content || '' : false, v)
                })
            })
        }
    }
    //富文本编译器
    editorConfig = (elem, _key, propstate, _v) => {
        // const elem = this.refs.editorElem
        const editor = new E(elem)
        // editor.customConfig.pasteFilterStyle = true
        editor.customConfig.pasteIgnoreImg = false
        editor.customConfig.uploadImgShowBase64 = true   // 使用 base64 保存图片
        // editor.customConfig.showLinkImg = true // 隐藏“网络图片”tab
        editor.customConfig.zIndex = 1;
        editor.customConfig.uploadImgMaxSize = 300 * 1024;


        editor.customConfig.customUploadImg = (files, insert) => {
            // files 是 input 中选中的文件列表
            // insert 是获取图片 url 后，插入到编辑器的方法
            let file = files[0]
            this.getAuthorization((info) => {
                file.status = 'done'
                let fd = new FormData();
                fd.append('key', info.key);
                fd.append('Signature', info.XCosSecurityToken);
                fd.append('Content-Type', '');
                fd.append('file', file);

                const xmlhttp = new XMLHttpRequest();
                xmlhttp.open('post', info.url, true);
                xmlhttp.send(fd);

                xmlhttp.onreadystatechange = function () {
                    if (xmlhttp.readyState == 4) {
                        insert(info.url + info.key)
                    }
                }
            })
            // 上传代码返回结果之后，将图片插入到编辑器中
        }


        editor.customConfig.customAlert = info => {
            message.warning('上传图片大小不能超过300k！')
        }
        editor.customConfig.linkImgCallback = url => {
        }
        editor.customConfig.linkImgCheck = function (src) {
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
            if (/^http:\/\//.test(link) || /^https:\/\//.test(link)) {
            } else {
                link = 'http://' + link;
            }
            return true // 返回 true 表示校验成功
            // return '验证失败' // 返回字符串，即校验失败的提示信息
        }
        editor.customConfig.onchange = html => {
            if (/href="/ig.test(html) && !/href=http"/ig.test(html)) {

            }
            if (/href="http/ig.test(html) || /href="https/ig.test(html)) {

            } else {
                html = html.replace(/href="/ig, 'href="http://')

            }
            html = html.replace(/<p>&nbsp;&nbsp;/ig, '<&&&&&&&>').replace(/target="_blank" style="background-color: rgb\(255, 255, 255\);"/ig, "target='_blank'").replace(/&nbsp;/ig, ' ').replace(/<&&&&&&&>/ig, '<p>&nbsp;&nbsp;')
            if(html == '<p><br></p>' || /^<p>\s+<\/p>$/.test(html)){
                html = '';
            }

            this.setState({
                [_v]: Object.assign({}, this.state[_v], { content: html })
            })
        }

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


    handlePreview = (file) => {
        let { previewImage } = this.state
        if (!previewImage) {
            previewImage = this.state[this.state.tk].fileList[0].url
        }
        this.setState({
            previewImage,
            previewVisible: true,
        })
    };
    handleCancel = () => {
        this.setState({ previewVisible: false });
    }
    handleChoice() {
        this.setState({
            previewVisible: false
        })
    }
    preview = () => {
        const { en, cn, hk, jp, kr } = this.state
        const contentArr = [
            { title: '中文', con: cn, key: 'cn' },
            { title: '英文', con: en, key: 'en' },
            { title: '繁文', con: hk, key: 'hk' },
            { title: '日文', con: jp, key: 'jp' },
            { title: '韩文', con: kr, key: 'kr' },
        ]
        this.footer = [
            <Button key="back" onClick={this.vahandleCancel}>关闭</Button>,
        ]
        this.setState({
            visible: true,
            width: '1000px',
            height: '780px',
            motitle: '预览',
            modalHtml: <ModalPrevs contentArr={contentArr} activitykey={this.state.tk} />
        })
    }
    //弹窗隐藏
    vahandleCancel() {
        this.setState({
            visible: false
        });
    }
    sourceCode() {
        const { en, cn, hk, jp, kr } = this.state
        const contentArr = [
            { title: '中文', con: cn, key: 'cn' },
            { title: '英文', con: en, key: 'en' },
            { title: '繁文', con: hk, key: 'hk' },
            { title: '日文', con: jp, key: 'jp' },
            { title: '韩文', con: kr, key: 'kr' },
        ]
        this.footer = [
            <Button key="back" onClick={() => this.vahandleCancel(contentArr)}>取消</Button>,
            <Button key="submit" type="more" onClick={() => this.vahandleCancel()}>
                确认
            </Button>,
        ]
        this.setState({
            visible: true,
            width: "800px",
            motitle: '显示源码',
            modalHtml: <SourceCode contentArr={contentArr} activitykey={this.state.tk} textareaChange={this.textareaChange} />
        })
    }

    textareaChange(event) {
        const target = event.target;
        const value = target.value;
        this.setState({
            editorContent: value
        })
    }
    issueNews = async () => {
        const { id, source, sourceLink, type, isTop, noticeType, pubTime, cn, en, hk, jp, kr, digest, remark, tk } = this.state

        if(!type){
            message.warning('请选择类型')
            return
        }else if(type == 1 && !noticeType){
            message.warning('请选择公告类型！')
            return 
        }
        //可以为空 oliver
        // else if(type == 2){
        //     if (!source) {
        //         message.warning('来源不能为空！')
        //         return false
        //     }
        //     if (!sourceLink) {
        //         message.warning('来源链接不能为空！')
        //         return false
        //     }
        // }
        if(!remark){
            message.warning('说明不能为空！')
            return 
        }
        if (!pubTime) {
            message.warning('请选择发布时间！')
            return 
        }
        let ckp = {
            title: '标题不能为空！',
            keyword: '关键字不能为空！',
            digest: '摘要不能为空！',
            content: '正文不能为空！',
            // photo: '请上传封面图片！',
        }
        let warning = Object.keys(ckp).reduce((p, _k) => {
            if(p && this.state[tk][_k] == '<p><br></p>'){
                message.warning(_langs.find(v => Object.keys(v)[0] === tk)[tk] + ckp[_k])
                return false
            }
            if (p && !this.state[tk][_k]) {
                message.warning(_langs.find(v => Object.keys(v)[0] === tk)[tk] + ckp[_k])
                return false
            }
            return p
        }, true)
        if (!warning)
            return
        let params = {
            baseId: id || '',
            source, sourceLink,
            type: Number(type),
            noticeType: Number(noticeType),
            pubTime: moment(pubTime).format(TIMEFORMAT_ss),
            top: isTop,
            remark: remark,

            id_cn: cn.id || '',
            title_cn: cn.title || '',
            keyword_cn: cn.keyword || '',
            digest_cn: cn.digest || '',
            content_cn: cn.content || '',
            photo_cn: cn.photo || '',

            id_en: en.id || '',
            title_en: en.title || '',
            keyword_en: en.keyword || '',
            digest_en: en.digest || '',
            content_en: en.content || '',
            photo_en: en.photo || '',

            id_hk: hk.id || '',
            title_hk: hk.title || '',
            keyword_hk: hk.keyword || '',
            digest_hk: hk.digest || '',
            content_hk: hk.content || '',
            photo_hk: hk.photo || '',

            id_jp: jp.id || '',
            title_jp: jp.title || '',
            keyword_jp: jp.keyword || '',
            digest_jp: jp.digest || '',
            content_jp: jp.content || '',
            photo_jp: jp.photo || '',

            id_kr: kr.id || '',
            title_kr: kr.title || '',
            keyword_kr: kr.keyword || '',
            digest_kr: kr.digest || '',
            content_kr: kr.content || '',
            photo_kr: kr.photo || '',

        }

        let res = await this.request({ url: '/news/v2/push', type: 'post', isP: true }, params)
        await this.setState({
            ...JSON.parse(JSON.stringify(DF))
        })
        this.props.toIssue()
        this.props.requestTable(1)

    }
    ckUid = async () => {
        const tk = this.state.tk
        const tk_con = this.state[tk]
        const result = await this.request({ url: '/news/editItemOfNewById', type: 'post',isP:true }, { id: tk_con.id })
        if (result) {
            tk_con.title = result.title
            tk_con.keyword = result.keyword
            tk_con.digest = result.digest
            tk_con.content = result.content
            this.setState({
                [tk]: tk_con
            }, () => {
                this.editorConfig(this.refs[`content_${tk}`], `content_${tk}`, tk_con.content, tk)
            })
        } else {
            message.warning('未检索到匹配编号')
            tk_con.id = ''
            this.setState({
                [tk]: tk_con
            })
        }
    }

    render() {
        const { id, type, noticeType, pubTime, isTop, digest, tk, fileList, cn, en, hk, jp, kr, previewVisible, previewImage, visible, motitle, width, modalHtml, sourceLink, source, remark } = this.state
        const { _types, _placard } = this.props.SeOp
        const uploadButton = (
            <div>
                <Icon type="plus" />
                <div className="ant-upload-text">上传图片</div>
            </div>
        );
        return (
            <div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <Button style={{ float: 'right' }} type="primary" onClick={() => { this.props.toIssue() }}>返回上一级</Button>
                    </div>
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">基础ID：</label>
                                        <div className="col-sm-8" style={{ marginTop: '7px' }}>
                                            {id}
                                        </div>
                                    </div>
                                </div>
                                <SeOp title='类型：<i>*</i>' value={String(type)} onSelectChoose={v => this.onSelectChoose(v, 'type')} ops={objToArr(_types)} />
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">是否置顶：</label>
                                        <div className="col-sm-8">
                                            <RadioGroup name='isTop' onChange={this.handleInputChange} value={isTop}>
                                                <Radio value={false}>否</Radio>
                                                <Radio value={true}>是</Radio>
                                            </RadioGroup>
                                            <span className="color_999">是否置于新闻顶部</span>
                                        </div>
                                    </div>
                                </div>
                                {/* <SeOp title='是否置顶：' value={String(isTop)} onSelectChoose={v => this.onSelectChoose(v, 'isTop')} ops={objToArr(_isTops)} /> */}
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">发布时间：<i>*</i></label>
                                        <div className="col-sm-8">
                                            <DatePicker
                                                showTime={{ format: TIMEFORMAT_DAYS_ss }}
                                                format={TIMEFORMAT_ss}
                                                onChange={(date, dateString) => this.onChangeCheckTime(date, dateString, 'pubTime')}
                                                value={pubTime ? moment(pubTime) : ''}
                                            />
                                        </div>
                                    </div>
                                </div>
                                {type == 1 && <SeOp title='公告类型：<i>*</i>' value={String(noticeType)} onSelectChoose={v => this.onSelectChoose(v, 'noticeType')} ops={objToArr(_placard)} />}
                                {
                                    type == 2 && <div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">来源：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="source" value={source} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">来源链接：</label>
                                                <div className="col-sm-8">
                                                    <input type="text" className="form-control" name="sourceLink" value={sourceLink} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                }

                                <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="form-group form-group-heighAuto">
                                        <label className="col-sm-3 control-label">说明：<i>*</i></label>
                                        <div className="col-sm-5">
                                            <TextArea rows={6} name='remark' onChange={this.handleInputChange} value={remark} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                                    <Tabs onChange={this.tcb} defaultActiveKey="cn" activeKey={tk}>
                                        {
                                            [{ cn: '中文' }, { en: '英文' }, { hk: '繁文' }, { jp: '日文' }, { kr: '韩文' },].map((item) => {
                                                const _key = Object.keys(item)[0], _value = Object.values(item)[0];
                                                let _obj = this.state[_key];
                                                return <TabPane tab={_value} key={_key} forceRender={true}>
                                                    <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                                                        <div className="form-group">
                                                            <label className="col-sm-3 control-label">编号：</label>
                                                            <div className="col-sm-8">
                                                                <input type="text" className="form-control" name="id" value={_obj.id || ''} onChange={e => this._handleInputChange(e, _key)} />
                                                                <Button style={{ margin: '5px 0 0 20px' }} type="primary" size='small' onClick={() => { this.ckUid() }}>检索</Button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                                        <div className="form-group">
                                                            <label className="col-sm-3 control-label">标题：<i>*</i></label>
                                                            <div className="col-sm-5">
                                                                {/* <input type="text" maxLength={40} className="form-control input_MaxWidth" name="title" value={_obj.title || ''} onChange={e => this._handleInputChange(e, _key)} placeholder='请输入有效标题(40个字以内)'  /> */}
                                                                <input type="text" maxLength={_key == 'cn' && 40} className="form-control input_MaxWidth" name="title" value={_obj.title || ''} onChange={e => this._handleInputChange(e, _key)} placeholder={`${_key == 'cn' ? '请输入有效标题(40个字以内)' :''}`} />
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                                        <div className="form-group">
                                                            <label className="col-sm-3 control-label">关键字：<i>*</i></label>
                                                            <div className="col-sm-5">
                                                                <input type="text" className="form-control input_MaxWidth" name="keyword" value={_obj.keyword || ''} onChange={e => this._handleInputChange(e, _key)} />
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                                        <div className="form-group form-group-heighAuto">
                                                            <label className="col-sm-3 control-label">摘要：<i>*</i></label>
                                                            <div className="col-sm-5">
                                                                <TextArea rows={6} name='digest' onChange={e => this._handleInputChange(e, _key)} value={_obj.digest || ''} />
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                                        <div className="form-group form-group-heighAuto">
                                                            <label className="col-sm-3 control-label">正文：<i>*</i></label>

                                                            <div className="col-md-8 col-sm-8 col-xs-8">
                                                                {/* 将生成编辑器 */}
                                                                <div ref={`content_${_key}`} style={{ textAlign: 'left' }}>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                                        <div className="form-group form-group-heighAuto">
                                                            <label className="col-sm-1 control-label">封面图片：</label>
                                                            <div className="col-sm-8">
                                                                <Upload
                                                                    listType="picture-card"
                                                                    fileList={_obj.fileList.slice(-1)}
                                                                    onRemove={(v) => this.onRemove(v, _key)}
                                                                    onPreview={(v) => this.handlePreview(v, _key)}
                                                                    onChange={(v) => this.handleChange(v, _key)}
                                                                    customRequest={(e) => {
                                                                        this.uploadImageCos(e.file,this.uploadImageCoscb, _key)
                                                                    }}
                                                                >
                                                                    {_obj.fileList.length >= 1 ? null : uploadButton}
                                                                </Upload>

                                                            </div>
                                                        </div>
                                                    </div>
                                                </TabPane>
                                            })
                                        }
                                    </Tabs>
                                </div>
                            </div>
                            <div className="col-md-6 col-sm-6 col-xs-6">
                                <div className="form-group form-group-heighAuto">
                                    <label className="col-sm-1 control-label"></label>
                                    <div style={{ float: 'right' }}>
                                        <Button size="large" type="primary" onClick={this.issueNews}>发布</Button>
                                        <Button size="large" type="primary" onClick={this.preview} >预览</Button>
                                        <Button size="large" type="primary" onClick={this.sourceCode}>显示源码</Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal visible={previewVisible} footer={null} onCancel={this.handleChoice} className="news_distribute">
                    <img alt="example" style={{ width: '100%', height: '100%' }} src={previewImage} />
                </Modal>
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