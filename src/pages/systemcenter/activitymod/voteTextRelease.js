import React from 'react'
import { Tabs, Button, message } from 'antd';
import E from 'wangeditor'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../conf'
const TabPane = Tabs.TabPane;

export default class TextReleaseBox extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            title_cn: "",
            title_en: "",
            title_hk: "",
            editorContent_cn_1: "",
            editorContent_cn_2: "",
            editorContent_hk_1: "",
            editorContent_hk_2: "",
            editorContent_en_1: "",
            editorContent_en_2: "",
            now_modify: false,
            modify_update: false,
            activityId: "",
        }
        this.callback = this.callback.bind(this)
        this.editorConfig = this.editorConfig.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.submit = this.submit.bind(this)
        this.resetAxios = this.resetAxios.bind(this)
        this.typeState = this.typeState.bind(this)
    }
    componentDidMount() {
    }
    componentWillReceiveProps(nextProps) {
        console.log(nextProps.modify)
        let { editorContent_cn_1, editorContent_cn_2,
            editorContent_hk_1, editorContent_hk_2,
            editorContent_en_1, editorContent_en_2
        } = this.state
        if (nextProps.modify != "") {
            editorContent_cn_2 = nextProps.modify.activityRuleSimple;// 简体规则
            editorContent_hk_2 = nextProps.modify.activityRuleTraditional;//繁体规则
            editorContent_en_2 = nextProps.modify.activityRuleEnglish;// 英文规则

            editorContent_cn_1 = nextProps.modify.activityContentSimple;// 
            editorContent_hk_1 = nextProps.modify.activityContentTraditional;//
            editorContent_en_1 = nextProps.modify.activityContentEnglish;// 

            this.setState({
                title_cn: nextProps.modify.activityNameSimple,
                title_hk: nextProps.modify.activityNameTraditional,
                title_en: nextProps.modify.activityNameEnglish,
                editorContent_cn_1: editorContent_cn_1,
                editorContent_cn_2: editorContent_cn_2,
                editorContent_hk_1: editorContent_hk_1,
                editorContent_hk_2: editorContent_hk_2,
                editorContent_en_1: editorContent_en_1,
                editorContent_en_2: editorContent_en_2,
                now_modify: true,
                activityId: nextProps.activityId
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
            this.editorConfig(this.refs.editorElem_en_1, "en_1", editorContent_en_1);//富文本编译器
            this.editorConfig(this.refs.editorElem_en_2, "en_2", editorContent_en_2);//富文本编译器
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
        
        editor.customConfig.onchange = html => {
            console.log(html)
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

        }
        
        editor.customConfig.pasteTextHandle=  content => {
            console.log(content)
            let filterContent = content.replace(/<head[^>]*?>[\s\S]*head>/gi, '');//过滤head标签中
                filterContent = filterContent.replace(/<script[^>]*?>[\\s\\S]*script>/gi,'');//过滤js
                filterContent = filterContent.replace(/<[^>]+>/g, '');//过滤标签
                filterContent = filterContent.replace(/\\s*|\t|\r|\n|&nbsp;/g,"")//过滤空格，换行
                console.log(filterContent)
            return filterContent
        }
        
        editor.create()
        editor.txt.html(propstate)
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
    callback(key) {
    }
    //新建活动请求接口
    resetAxios() {
        const { title_cn, title_en, title_hk,
            editorContent_cn_1, editorContent_cn_2,
            editorContent_hk_1, editorContent_hk_2,
            editorContent_en_1, editorContent_en_2
        } = this.state
        const { selectCount, beginTime, endTime, status } = this.props
        if (!this.typeState()) return;
        axios.post(DOMAIN_VIP + "/voteManage/insert", qs.stringify({
            activityNameSimple: title_cn,
            activityNameTraditional: title_hk,
            activityNameEnglish: title_en,
            activityContentSimple: editorContent_cn_1,
            activityContentTraditional: editorContent_hk_1,
            activityContentEnglish: editorContent_en_1,
            activityRuleSimple: editorContent_cn_2,
            activityRuleTraditional: editorContent_hk_2,
            activityRuleEnglish: editorContent_en_2,
            selectCount: selectCount,
            beginTime: beginTime,
            endTime: endTime,
            activityLimit: status
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                message.success("创建成功");
                this.props.show_click(0);
            } else {
                message.error(result.msg);
            }
        })
    }
    //修改活动请求接口
    resetModifyAxios() {
        const { title_cn, title_en, title_hk, editorContent_cn_1, editorContent_cn_2,
            editorContent_hk_1, editorContent_hk_2,
            editorContent_en_1, editorContent_en_2, activityId } = this.state
        const { selectCount, beginTime, endTime, status } = this.props
        if (!this.typeState()) return;
        axios.post(DOMAIN_VIP + "/voteManage/update", qs.stringify({
            activityNameSimple: title_cn,
            activityNameTraditional: title_hk,
            activityNameEnglish: title_en,
            activityContentSimple: editorContent_cn_1,
            activityContentTraditional: editorContent_hk_1,
            activityContentEnglish: editorContent_en_1,
            activityRuleSimple: editorContent_cn_2,
            activityRuleTraditional: editorContent_hk_2,
            activityRuleEnglish: editorContent_en_2,
            selectCount: selectCount,
            beginTime: beginTime,
            endTime: endTime,
            activityLimit: status,
            activityId: activityId
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                message.success("修改活动成功");
                this.props.show_click(0);
            } else {
                message.error(result.msg);
            }
        })
    }
    typeState() {
        const { title_cn, title_en, title_hk,
            editorContent_cn_1, editorContent_cn_2,
            editorContent_hk_1, editorContent_hk_2,
            editorContent_en_1, editorContent_en_2
        } = this.state
        const { selectCount, beginTime, endTime, status } = this.props
        if (beginTime == "" || endTime == "") {
            message.error('请选择活动时间')
            return false;
        }
        if (status == "") {
            message.error('请选择投票方式')
            return false;
        }
        if (selectCount == "") {
            message.error('请选择票数限制')
            return false;
        }
        if (title_cn == "") {
            message.error('请输入简体标题')
            return false;
        }
        if (editorContent_cn_1 == "") {
            message.error('请输入简体活动内容')
            return false;
        }
        if (editorContent_cn_2 == "") {
            message.error('请输入简体活动规则')
            return false;
        }
        if (title_hk == "") {
            message.error('请输入繁体字标题')
            return false;
        }
        if (editorContent_hk_1 == "") {
            message.error('请输入繁体活动内容')
            return false;
        }
        if (editorContent_hk_2 == "") {
            message.error('请输入繁体活动规则')
            return false;
        }
        if (title_en == "") {
            message.error('请输入英文标题')
            return false;
        }
        if (editorContent_en_1 == "") {
            message.error('请输入英文活动内容')
            return false;
        }
        if (editorContent_en_2 == "") {
            message.error('请输入英文活动规则')
            return false;
        }
        return true;

    }
    //发布活动
    submit() {
        let { now_modify } = this.state
        if (now_modify) {
            this.resetModifyAxios();
        } else {
            this.resetAxios();
        }

    }
    render() {
        const { title_cn, title_en, title_hk } = this.state

        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <Tabs defaultActiveKey="cn" onChange={this.callback}>
                    <TabPane tab="简体" key="cn" forceRender={true} >
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">标题：<i>*</i></label>
                                <div className="col-sm-5">
                                    <input type="text" className="form-control input_MaxWidth" name="title_cn" value={title_cn} onChange={this.handleInputChange} />
                                </div>
                            </div>
                        </div>
                        <label className="col-sm-3 mb10">活动描述简体中文：<i>*</i></label>
                        <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                            {/* 将生成编辑器 */}
                            <div ref="editorElem_cn_1" style={{ textAlign: 'left' }}>
                            </div>
                        </div>
                        <label className="col-sm-3 mb10">活动规则描述简体中文：<i>*</i></label>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            {/* 将生成编辑器 */}
                            <div ref="editorElem_cn_2" style={{ textAlign: 'left' }}>
                            </div>
                        </div>
                    </TabPane>
                    <TabPane tab="繁体" key="hk" forceRender={true} >
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">标题：<i>*</i></label>
                                <div className="col-sm-5">
                                    <input type="text" className="form-control input_MaxWidth" name="title_hk" value={title_hk} onChange={this.handleInputChange} />
                                </div>
                            </div>
                        </div>
                        <label className="col-sm-3 mb10">活动描述繁体中文：<i>*</i></label>
                        <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                            {/* 将生成编辑器 */}
                            <div ref="editorElem_hk_1" style={{ textAlign: 'left' }}>
                            </div>
                        </div>
                        <label className="col-sm-3 mb10">活动规则描述繁体中文：<i>*</i></label>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            {/* 将生成编辑器 */}
                            <div ref="editorElem_hk_2" style={{ textAlign: 'left' }}>
                            </div>
                        </div>
                    </TabPane>
                    <TabPane tab="英文" key="en" forceRender={true} >
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">标题：<i>*</i></label>
                                <div className="col-sm-5">
                                    <input type="text" className="form-control input_MaxWidth" name="title_en" value={title_en} onChange={this.handleInputChange} />
                                </div>
                            </div>
                        </div>
                        <label className="col-sm-3 mb10">活动描述ENGLISH：<i>*</i></label>
                        <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                            {/* 将生成编辑器 */}
                            <div ref="editorElem_en_1" style={{ textAlign: 'left' }}>
                            </div>
                        </div>
                        <label className="col-sm-3 mb10">活动规则描述ENGLISH：<i>*</i></label>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            {/* 将生成编辑器 */}
                            <div ref="editorElem_en_2" style={{ textAlign: 'left' }}>
                            </div>
                        </div>
                    </TabPane>
                </Tabs>
                <div className="col-md-12 col-sm-12 col-xs-12 mb30">
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12 mb30">
                    <div className="form-group center mb10 form-group-heighAuto">
                        <Button type="primary btn_max_width" onClick={this.submit} size="large">发布活动</Button>
                    </div>
                </div>
            </div>
        )
    }

}