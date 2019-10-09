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

const { RangePicker } = DatePicker
const { Column } = Table

let defaultEditor = (type) => {
    //充值
    let recharge = {
        descriptCN: '<p>•请勿向上述地址充值任何非##coin##资产，否则资产将不可找回。</p><p>•往该地址充值，等待网络自动确认（##count2##个确认）后系统自动到账。</p><p>•为了快速到账，充值时可以适当提高网络手续费。</p><p>•充值完成后，可以进入充值记录页面跟踪进度。</p>',
        descriptEN: '<p>•Please don’t deposit any other digital assets except ##coin## to the above address. Otherwise, you may lose your assets permanently.</p><p>•Deposit to the above address and wait for automatic network confirmation (##count2## confirmations) before the system automatically arrives.</p><p>•In order to quicken deposit, it can be appropriate to improve the network fee.</p><p>•After deposit completed, you can enter the WITHDRAWAL HISTORY to track the progress.</p>',
        descriptHK: '<p>•請勿向上述地址充值任何非##coin##資産，否則資産將不可找回。</p><p>•您充值至上述地址後，需要整個網絡節點的確認，##count1##次網絡確認後到賬，##count2##次網絡確認後可提幣。</p>',
        descriptJP: '<p>•上述のアドレスにいかなる非##coin##資産を入金しないでください。資産を失う可能性があります。</p><p>•このアドレスにチャージして、ネットワークが自動的に確認した後（##count2## 回確認）、自動的に到着します。</p><p>•早めに振り込みできるために、チャージする時にネット手数料を適当に高めます.</p><p>•チャージが完了したら、チャージの歴史記録ページに入って進行状況を追跡できる.</p>',
        descriptKR: '<p>•위의 주소에 비 ##coin## 자산을 충전하지 마세요. 아니면 되찾지 못합니다.</p><p>•이 주소에 충전하고 네트워크가 자동으로 확인(##count2## 개의 확인)한 후에 시스템이 자동으로 입금됩니다. </p><p>•더 빨리 성공적으로 입금되기 위해 적당하게 수수료를 높일 수 있습니다.</p><p>•충전이 완료되면, 입금 내역  페이지로 들어가 진도를 추적할 수 있습니다.</p>'
    }
    //提现
    let withdraw = {
        descriptCN: '<p>•##coin##单笔提现，最小数量##minimum##，最大限额##maximum##（##maximum##个以上需要平台审核）。</p><p>•##coin##提现手续费为0，网络收取矿工费##miner-fee##/笔，由平台代扣。</p><p>•##coin##充值经过区块链网络的##count##个确认后，才允许提现。</p>',
        descriptEN: '<p>•For single withdrawal of ##coin##, the minimum amount is ##minimum## and the maximum amount is ##maximum## (more than ##maximum## need platform review).</p><p>•##coin## withdrawal fee is 0, internet collects the miner fee for ##miner-fee## which will be withhold by platform.</p><p>•##coin## withdrawals will be allowed after the deposits makes ##count## confirmations in blockchain network.</p>',
        descriptHK: '<p>•##coin##单笔提现，最小数量##minimum##，最大限额##maximum##（##maximum##个以上需要平台审核）。</p><p>•##coin##提现手续费为0，网络收取矿工费##miner-fee##/笔，由平台代扣。</p><p>•##coin##充值经过区块链网络的##count##个确认后，才允许提现。</p>',//未翻译
        descriptJP: '<p>•##coin##一回の送金なら、最小数は##minimum##で、最大限度額は##maximum##です（##maximum##以上はプラットフォーム監査が必要です）。</p><p>•##coin##一回の送金なら手数料は無料であり、ネットワークは鉱夫の費用として##miner-fee## /回を徴収しますが、これはプラットフォームによって差し引かれます。</p><p>•##coin##入金なら、ブロックチェーンネットワークから##count##回の確認後に引き出すことが可能です。</p>',
        descriptKR: '<p>•##coin##는 한번 인출의 최소 수량은 ##minimum##, 최대 한도액은 ##maximum## 입니다. ##maximum##개 이상을 넘으면 플랫폼에서 심사가 필요합니다).</p><p>•##coin## 현금 인출 수수료는 0이고,네트워크는 광부료를 ##miner-fee##/회 받고 플랫폼에서 대신 공제합니다. </p><p>•##coin## 충전은 블록체인 네트워크의 ##count##개 확인을 거친 후에야 인출이 허용됩니다.</p>'
    }
    return type ? withdraw : recharge
}

export default class RechargeInstructions extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: true,
            // pageIndex: PAGEINDEX,
            // pageSize: PAGESIZE,
            // pageTotal: DEFAULTVALUE,
            oldData: [],
            tableSource: [],
            pageTabs: true,
            type: 0,
            ...JSON.parse(JSON.stringify(defaultEditor(0))),
            fundsType: '0',
            record: {},
            key: 'cn'
        }
    }

    componentDidMount() {
        this.requestTable()

    }
    clickHide = () => {
        this.setState({
            showHide: !this.state.showHide
        })
    }
    inquiry = () => {
        if (this.state.fundsType == '0') {
            this.requestTable();
            return;
        }
        let arr = this.state.oldData.filter(item => {
            if (item.fundsType == this.state.fundsType) {
                return item;
            }
        });
        this.setState({
            tableSource: arr
        })
    };
    resetState = () => {
        this.requestTable();
        this.setState({
            fundsType: '0'
        })
    };
    // onChangePageNum = (pageIndex, pageSize) => {
    //     this.setState({
    //         pageIndex,
    //         pageSize
    //     })
    //     this.requestTable(pageIndex, pageSize)
    // }
    // onShowSizeChange = (pageIndex, pageSize) => {
    //     this.setState({
    //         pageIndex,
    //         pageSize
    //     })
    //     this.requestTable(pageIndex, pageSize)
    // }
    //输入时 input 设置到 state
    // handleInputChange = (event) => {
    //     const target = event.target;
    //     const value = target.type === 'checkbox' ? target.checked : target.value;
    //     const name = target.name;
    //     this.setState({
    //         [name]: value
    //     });
    // }
    //请求数据
    requestTable = () => {
        axios.get(DOMAIN_VIP + '/introductionManagement/queryAttr').then(res => {
            const result = res.data;
            if (result.code == 0) {
                let tableSource = result.data;
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = i + 1;
                    tableSource[i].key = i;
                }
                this.setState({
                    tableSource: tableSource,
                    oldData: tableSource,
                })
            } else {
                message.warning(result.msg);
            }
        })
    };
    onEdit = (record) => {
        let { descriptCN, descriptEN, descriptHK, descriptJP, descriptKR } = JSON.parse(JSON.stringify(defaultEditor(this.state.type)));
        let { type } = this.state;
        //替换是三个对应的字段
        // descriptCN = descriptCN.replace('##coin##', record.propTag).replace('##count1##', record.inConfirmTimes).replace('##count2##',record.outConfirmTimes);
        // descriptEN = descriptEN.replace('##coin##', record.propTag).replace('##count1##', record.inConfirmTimes).replace('##count2##',record.outConfirmTimes);
        // descriptHK = descriptHK.replace('##coin##', record.propTag).replace('##count1##', record.inConfirmTimes).replace('##count2##',record.outConfirmTimes);
        // descriptJP = descriptJP.replace('##coin##', record.propTag).replace('##count1##', record.inConfirmTimes).replace('##count2##',record.outConfirmTimes);
        // descriptKR = descriptKR.replace('##coin##', record.propTag).replace('##count1##', record.inConfirmTimes).replace('##count2##',record.outConfirmTimes);
        //发请求获取富文本框的内容
        axios.post(DOMAIN_VIP + '/introductionManagement/getListById', qs.stringify({ fundstype: record.fundsType, type })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                descriptCN = result.data ? result.data.descriptcn : descriptCN;
                descriptEN = result.data ? result.data.descripten : descriptEN;
                descriptHK = result.data ? result.data.descripthk : descriptHK;
                descriptJP = result.data ? result.data.descriptjp : descriptJP;
                descriptKR = result.data ? result.data.descriptkr : descriptKR;
                this.setState({ pageTabs: false, record, descriptCN, descriptEN, descriptHK, descriptJP, descriptKR, recoedFundsType: record.fundsType, key: 'cn' }, () => {
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
        let defaultData = JSON.parse(JSON.stringify(defaultEditor(this.state.type)));
        this.setState({ pageTabs: true, ...defaultData, type: 0 })
    }
    //富文本编译器
    editorConfig(elem, index, propstate) {
        // console.log(elem)
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
    /**
     * @param type
     * @description 0-充值    1-提现
     */
    createDesc = (type) => {
        let desc = [
            '请务必以##coin##代表文案中的币种。以##count1##代表到账网络确认次数，以##count2##代表可提币网络确认次数每句开头请加入：•',
            '请务必以##coin##代表文案中的币种。以##minimum##代表最小数量，以##maximum##代表最大限额，以##miner-fee##代表矿工手续费，以##count##代表网络确认次数每句开头请加入：•'
        ]
        return (
            <div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <label className="col-sm-1 control-label">币种:</label>
                    <div className="col-sm-8">{this.state.record.propTag}</div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <label className="col-sm-1 control-label">说明:<i>*</i></label>
                    <div className="col-sm-11" style={{ color: '#FF0033' }}>{desc[type]}</div>
                </div>
            </div>
        )
    }
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
        const { descriptCN, descriptEN, descriptHK, descriptJP, descriptKR, recoedFundsType, type } = this.state;
        let list = [descriptCN, descriptEN, descriptHK, descriptJP, descriptKR];
        for (let j = 0; j < list.length; j++) {
            let results = this.findall(list[j], "<p>");
            for (let i = 0; i < results.length; i++) {
                let str = list[j].substr(results[i] + 3, 1);
                if (str != '•') {
                    message.warning('每句开头请加入：•');
                    return;
                }
            }
        }
        axios.post(DOMAIN_VIP + '/introductionManagement/update', qs.stringify({
            descriptcn: descriptCN, descripten: descriptEN, descripthk: descriptHK, descriptjp: descriptJP, descriptkr: descriptKR, fundstype: recoedFundsType, type
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                message.success('保存成功');
                let defaultData = JSON.parse(JSON.stringify(defaultEditor(type)));
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
    render() {
        const { showHide, pageIndex, pageSize, pageTotal, tableSource, pageTabs, type, fundsType, key } = this.state;
        return (
            <div className="right-con">
                {pageTabs ?
                    <div>
                        <div className='page-title'>
                            {/* 当前位置：系统管理 >  系统管理 > 充值说明  */}
                            <i className={showHide ? 'iconfont cur_poi icon-shouqi right' : 'iconfont cur_poi icon-zhankai right'} onClick={this.clickHide}></i>
                        </div>
                        <div className='clearfix'></div>
                        <div className="row">
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                {showHide && <div className="x_panel">
                                    <div className='x_content'>
                                        <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                            {/* <div className="form-group">
                                                <label className="col-sm-3 control-label">币种:</label>
                                                <div className="col-sm-8">

                                                </div>
                                            </div> */}
                                            <FundsTypeList title='币种' fundsType={fundsType} handleChange={this.selectFundsType} />
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
                                            <Table dataSource={tableSource}
                                                rowKey="key"
                                                bordered
                                                // pagination={{
                                                //     showQuickJumper: true,
                                                //     showSizeChanger: true,
                                                //     showTotal: total => `总共${total}条`,
                                                //     size: 'small',
                                                //     total: pageTotal,
                                                //     pageSize: pageSize,
                                                //     current: pageIndex,
                                                //     pageSizeOptions: PAGRSIZE_OPTIONS20,
                                                //     defaultPageSize: PAGESIZE,
                                                //     onShowSizeChange: this.onShowSizeChange,
                                                //     onChange: this.onChangePageNum
                                                // }}
                                                pagination={false}
                                                locale={{ emptyText: '暂无数据' }}>
                                                <Column title='序号' dataIndex='index' key='index' />
                                                <Column title='币种' dataIndex='propTag' key='propTag' />
                                                <Column title='操作' dataIndex='op' key='op' render={(text, record) => <a href='javascript:void(0);' className="mar10" onClick={() => this.onEdit(record)} >编辑</a>} />
                                            </Table>
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
                                        <div className="form-group">
                                            <label className="col-sm-2 control-label">状态:</label>
                                            <div className="col-sm-8">
                                                <RadioGroup name="type" value={type} onChange={this.selectType}>
                                                    <Radio value={0}>充值</Radio>
                                                    <Radio value={1}>提现</Radio>
                                                </RadioGroup>
                                            </div>
                                        </div>

                                        <Tabs onChange={this.callback} defaultActiveKey="cn" activeKey={key}>
                                            <TabPane tab="简体" key="cn" forceRender={true}>
                                                {this.createDesc(type)}
                                                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                                    <div ref="editorElem_cn" style={{ textAlign: 'left' }}>
                                                    </div>
                                                </div>
                                            </TabPane>
                                            <TabPane tab="英文" key="en" forceRender={true}>
                                                {this.createDesc(type)}
                                                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                                    <div ref="editorElem_en" style={{ textAlign: 'left' }}>
                                                    </div>
                                                </div>
                                            </TabPane>
                                            <TabPane tab="繁体" key="hk" forceRender={true}>
                                                {this.createDesc(type)}
                                                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                                    <div ref="editorElem_hk" style={{ textAlign: 'left' }}>
                                                    </div>
                                                </div>
                                            </TabPane>
                                            <TabPane tab="韩文" key="kr" forceRender={true}>
                                                {this.createDesc(type)}
                                                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                                    <div ref="editorElem_kr" style={{ textAlign: 'left' }}>
                                                    </div>
                                                </div>
                                            </TabPane>
                                            <TabPane tab="日文" key="jp" forceRender={true}>
                                                {this.createDesc(type)}
                                                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                                                    <div ref="editorElem_jp" style={{ textAlign: 'left' }}>
                                                    </div>
                                                </div>
                                            </TabPane>
                                        </Tabs>
                                    </div>
                                    <Button type="primary" onClick={this.onSave}>保存修改</Button>
                                </div>
                            </div>
                        </div>
                    </div>}
            </div>

        )
    }
}