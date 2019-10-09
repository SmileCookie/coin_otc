import React, { Component } from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import E from 'wangeditor'
import { Button, Select, message, DatePicker, Table, Modal, Radio, Tabs } from "antd"
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, PAGRSIZE_OPTIONS20 } from '../../../conf'
import { toThousands } from '../../../utils'
import FundsTypeList from '../../common/select/fundsTypeList'
const RadioGroup = Radio.Group;
const TabPane = Tabs.TabPane;
const Option = Select.Option
const { Column } = Table

let defaultEditor = {}

export default class RechargeInstructions extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            //  pageIndex: PAGEINDEX,
            // pageSize: PAGESIZE,
            tableSource: [],
            pageTabs: true,
            ...JSON.parse(JSON.stringify(defaultEditor)),
            record: {},
            key: 'cn',
            accountType: [<Option key='0' value=''>请选择</Option>],
            selectName: '',
            desc:'',
            descriptCN:'', 
            descriptEN:'', 
            descriptHK:'', 
            descriptJP:'', 
            descriptKR:'',
           
           

        }
    }

    componentDidMount() {
        this.requestTable()
        this.selectData()

    }
    clickHide = () => {
        this.setState({
            showHide: !this.state.showHide
        })
    }

    // 查询
    inquiry = () => {
         this.requestTable()
    };
   
    



    //请求数据
    requestTable = () => {
        const { selectName } = this.state;

        axios.post(DOMAIN_VIP + '/otcIntroduction/getListByType', qs.stringify({
            type: selectName,
            // pageIndex: currIndex || pageIndex,
            // pageSize: currSize || pageSize
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data;
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].key = i;
                }
                this.setState({
                    tableSource: tableSource,
                    //pageTotal: result.data.totalCount

                })
            } else {
                message.warning(result.msg);
            }
        })
    };

    // 重置
    resetState = () => {
        
        this.setState(()=>({
            selectName: ''
        }),()=>{
            this.requestTable()
        })
        

        
    };


// 编辑
    onEdit = (record) => {
        let { descriptCN, descriptEN, descriptHK, descriptJP, descriptKR } = this.state;
      
        axios.post(DOMAIN_VIP + '/otcIntroduction/getListByType', qs.stringify({ type: record.type })).then(res => {
            const result = res.data;
            console.log(result)
            if (result.code == 0) {
                descriptCN = result.data[0].descriptcn || '';
                descriptEN = result.data[0].descripten || '';
                descriptHK = result.data[0]. descripthk || '';
                descriptJP = result.data[0].descriptjp || '';
                descriptKR = result.data[0].descriptkr || '';
                this.setState({ pageTabs: false, record, descriptCN, descriptEN, descriptHK, descriptJP, descriptKR, key: 'cn' }, () => {
                    this.editorConfig(this.refs.editorElem_cn, "cn", descriptCN);//富文本编译器
                    this.editorConfig(this.refs.editorElem_en, "en", descriptEN);//富文本编译器
                    this.editorConfig(this.refs.editorElem_hk, "hk", descriptHK);//富文本编译器
                    this.editorConfig(this.refs.editorElem_kr, "kr", descriptKR);//富文本编译器
                    this.editorConfig(this.refs.editorElem_jp, "jp", descriptJP);//富文本编译器
                })
            } else {
                message.warning(result.msg);
            }
        })
    }


    backToPrevious = () => {
        let defaultData = JSON.parse(JSON.stringify(defaultEditor));
        this.setState({ pageTabs: true, ...defaultData, type: 0 })
    }
    //富文本编译器
    editorConfig(elem, index, propstate) {
        console.log(elem)
        const editor = new E(elem)

        editor.customConfig.zIndex = 1
        editor.customConfig.pasteIgnoreImg = false
        editor.customConfig.uploadImgShowBase64 = true   // 使用 base64 保存图片
        editor.customConfig.showLinkImg = true // 隐藏“网络图片”tab
        editor.customConfig.uploadImgMaxSize = 300 * 1024;
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
            // 'bold',  // 粗体
            // 'fontSize',  // 字号
            // 'fontName',  // 字体
            // 'italic',  // 斜体
            // 'underline',  // 下划线
            // 'strikeThrough',  // 删除线
            // 'foreColor',  // 文字颜色
            // 'backColor',  // 背景颜色
            // 'link',  // 插入链接
            // 'list',  // 列表
            // 'justify',  // 对齐方式
            // 'quote',  // 引用
            // 'emoticon',  // 表情
            // 'image',  // 插入图片
            // 'table',  // 表格
            // 'video',  // 插入视频
            // 'code',  // 插入代码
            'undo',  // 撤销
            'redo'  // 重复
        ]

        editor.customConfig.onchange = html => {
            html = html.replace(/&nbsp;/ig, ' ')
            switch (index) {
                case 'cn':
                    this.setState({ descriptCN: html });
                    break;
                case 'en':
                    this.setState({ descriptEN: html })
                    break;
                case 'hk':
                    this.setState({ descriptHK: html })
                    break;
                case 'kr':
                    this.setState({ descriptKR: html })
                    break;
                case 'jp':
                    this.setState({ descriptJP: html })
                    break;
            }
        };

        editor.customConfig.pasteTextHandle = content => {
            // console.log(content)
            let filterContent = content.replace(/<head[^>]*?>[\s\S]*head>/gi, '');//过滤head标签中
            filterContent = filterContent.replace(/<script[^>]*?>[\\s\\S]*script>/gi, '');//过滤js
            filterContent = content.replace(/<[^>]+>/g, '');//过滤标签
            filterContent = content.replace(/\\s*|\t|\r|\n|&nbsp;/g, "")//过滤空格，换行
            return filterContent
        }

        editor.create()
        editor.txt.html(propstate)
    }
    findall = (html, searchStr) => {
        let results = [],
            len = html.length,
            pos = 0;
        while (pos < len) {
            pos = html.indexOf(searchStr, pos);
            if (pos === -1) {//未找到就退出循环完成搜索
                break;
            }
            results.push(pos);//找到就存储索引
            pos += 1;//并从下个位置开始搜索
        }
        return results;
    }
    createDesc = () => (
        <div>
            <div className="col-md-12 col-sm-12 col-xs-12">
                <label className="col-sm-1 control-label">提示说明:</label>
                <div className="col-sm-8">{this.state.record.title}</div>
            </div>
            {/* <div className="col-md-12 col-sm-12 col-xs-12">
                <label className="col-sm-1 control-label">说明:<i>*</i></label>
                <div className="col-sm-8" style={{ color: '#FF0033' }}>{this.state.desc}</div>
            </div> */}
        </div>
    )
    selectFundsType = v => {
        this.setState({
            fundsType: v,
        })
    }
    selectType = e => {
        this.setState({ type: e.target.value, key: 'cn' }, () => {
            this.onEdit(this.state.record);
        });
    };
    onSave = () => {
        const { record,descriptCN, descriptEN, descriptHK, descriptJP, descriptKR} = this.state;
      
        // let list = [descriptCN, descriptEN, descriptHK, descriptJP, descriptKR];
        // for (let j = 0; j < list.length; j++) {
        //     let results = this.findall(list[j], "<p>");
        //     for (let i = 0; i < results.length; i++) {
        //         let str = list[j].substr(results[i] + 3, 1);
        //         if (str != '•') {
        //             message.warning('每句开头请加入：•');
        //             return;
        //         }
        //     }
        // }
        axios.post(DOMAIN_VIP + '/otcIntroduction/update', qs.stringify({
           type:record.type, market:record.market || '', title:record.title,
           descriptcn: descriptCN, descripten: descriptEN, descripthk: descriptHK, descriptjp: descriptJP, descriptkr: descriptKR,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                message.success('保存成功');
                let defaultData = JSON.parse(JSON.stringify(defaultEditor));
                this.setState({ pageTabs: true, ...defaultData, type: 0, fundsType: '0' }, () => this.requestTable());
            } else {
                message.warning(result.msg);
            }
        })
    };
    callback = (e) => {
        this.setState({
            key: e
        })
    };

    // 下拉框数据

    selectData = () => {
        axios.get(DOMAIN_VIP + '/otcIntroduction/queryType').then(res => {
            const result = res.data;
            let selectList = [];
            if (result.code == 0) {
                for (let i = 0; i < result.data.length; i++) {
                    selectList.push(<Option key={i + 1} value={result.data[i].type}>{result.data[i].title}</Option>)
                }
                this.setState({
                    accountType: [<Option key='0' value=''>请选择</Option>, ...selectList]
                });
            }
        })
    }

    selectChange = (value) => {
        this.setState({
            selectName: value
        })
    }


    render() {
        const { selectName, accountType, showHide, tableSource, pageTabs, key, } = this.state;
        return (
            <div className="right-con">
                {pageTabs ?
                    <div>
                        <div className='page-title'>
                            {/* 当前位置：文案配置  */}
                            <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                        </div>
                        <div className='clearfix'></div>
                        <div className="row">
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                {showHide && <div className="x_panel">
                                    <div className='x_content'>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">提示说明:</label>
                                                <div className="col-sm-8">
                                                    <Select
                                                        showSearch
                                                        value={selectName}
                                                        style={{ width: SELECTWIDTH }}
                                                        onChange={this.selectChange}
                                                    >
                                                        {accountType}
                                                    </Select>
                                                </div>
                                            </div>

                                        </div>
                                        <div className="right">
                                            <div className="form-group right">
                                                <Button type="primary" onClick={this.inquiry}>查询</Button>
                                                <Button type="primary" onClick={this.resetState}>重置</Button>
                                            </div>
                                        </div>
                                    </div>
                                </div>}
                                <div className="x_panel">
                                    <div className="x_content">
                                        <div className="table-responsive">
                                            <div className="x_panel">
                                                <div className="x_content">
                                                    <div className="table-responsive">
                                                        <Table
                                                         rowKey="key"
                                                        dataSource={tableSource}
                                                         bordered
                                                           pagination={false}
                                                           locale={{ emptyText: '暂无数据' }} >
                                                           <Column title='序号' dataIndex='index' key='index' render={(text, record,index) =>index+1}  />
                                                           <Column title='提示说明' dataIndex='title' key='title' />
                                                           <Column title='操作' dataIndex='op' key="op" render={(text, record,index) => <a href='javascript:void(0);' className="mar10" onClick={() => this.onEdit(record)} >编辑</a>} />
                                                            
                                                        
                                                        </Table>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    :
                    <div>
                        <div className='clearfix'></div>
                        <div className="row">
                            <div className="x_panel">
                                <div className="x_content">
                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group right">
                                            <Button type="primary" onClick={this.backToPrevious}>返回上一级</Button>
                                        </div>
                                        {/* <div className="form-group">
                                            <label className="col-sm-2 control-label">提示类型:</label>
                                            <div className="col-sm-8">
                                             <div style={{fontSize:14,lineHeight:2.1,fontWeight:'bold',color:'#000000a6'}}>{this.state.record.type}</div>
                                            </div>
                                        </div> */}

                                        <Tabs onChange={this.callback} defaultActiveKey="cn" activeKey={key}>
                                            <TabPane tab="简体" key="cn" forceRender={true}>
                                                {this.createDesc()}
                                                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                                    <div ref="editorElem_cn" style={{ textAlign: 'left' }}>
                                                    </div>
                                                </div>
                                            </TabPane>
                                            <TabPane tab="英文" key="en" forceRender={true}>
                                                {this.createDesc()}
                                                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                                    <div ref="editorElem_en" style={{ textAlign: 'left' }}>
                                                    </div>
                                                </div>
                                            </TabPane>
                                            <TabPane tab="繁体" key="hk" forceRender={true}>
                                                {this.createDesc()}
                                                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                                    <div ref="editorElem_hk" style={{ textAlign: 'left' }}>
                                                    </div>
                                                </div>
                                            </TabPane>
                                            <TabPane tab="韩文" key="kr" forceRender={true}>
                                                {this.createDesc()}
                                                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                                    <div ref="editorElem_kr" style={{ textAlign: 'left' }}>
                                                    </div>
                                                </div>
                                            </TabPane>
                                            <TabPane tab="日文" key="jp" forceRender={true}>
                                                {this.createDesc()}
                                                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                                    <div ref="editorElem_jp" style={{ textAlign: 'left' }}>
                                                    </div>
                                                </div>
                                            </TabPane>
                                        </Tabs>
                                    </div>
                                    <Button type="primary" onClick={()=>this.onSave()}>保存修改</Button>
                                </div>
                            </div>
                        </div>
                    </div>}
            </div>

        )
    }
}