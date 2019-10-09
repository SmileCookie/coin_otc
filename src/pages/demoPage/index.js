/**
 * action
 * @description
 * 通过路由直接调此Js。作用action.
 * 所有的视图能抽离的全部置于components 高度抽象 例如 list list-item  或者  form   如果带业务场景的 置于该业务场景下面。 命名 例如 xxx.view.js 如果带业务且被公用置于components/business.
 * 所有的事件，数据获取，置于此层。
 * 获取数据后向下分发，视图组件负责消费。
 * 注释写法也在此 demo
 * 关于提高编辑提示的内容就不要用简写了。
 * @author luchao.ding
 */
import React from 'react';
// import Headers from '../../components/header/header';
// import Footer from '../../components/footer/footer';
import Form from '../../decorator/form'
import { FormattedMessage, injectIntl} from 'react-intl';
import {withRouter} from 'react-router'
import ReactModal from '../../components/popBox';
import '../../assets/style/modal/index.css';
import {optPop,trade_pop} from '../../utils';
import SelectHistory from '../../components/selectHistory'
import Tab from '../../components/tab';
import Confirm from '../../components/confirm';
import FileUploads from '../../components/upload';
import Pages from '../../components/Page';
import Tips from '../../components/globalTip';

// 获取过渡页面样式 注意高频度用的内容不要用简写形式否则编辑无法自动提示！！！
import {ThemeFactory, Styles} from '../../components/transition';
// 获取模型数据
import {DemoPageModel} from './index.model.js';
import {MB} from './index.modelB';
import {Redirect} from 'react-router-dom'
import {connect} from "react-redux";
import {pushGlobalTips,testGlobalTips} from "../../redux/module/tips";


@connect(
    (state)=>({
        msgList:state.tips.globalMsgList
    }),{
        pushGlobalTips,testGlobalTips
    }
)



@Form
class DemoPage extends React.Component{
    constructor(props){
        super(props);

        // 跟控制视图相关的内容（和业务有关的），form表单的放置于此， 业务逻辑处理过程中需要的标志位禁止放在state中。
        this.state = {
            // form 相关的
            // 用户名称
            username: '',
            // 用户年龄
            age: '',
            // 控制相关的
            // 是否显示列表
            showMsgList: false,
            bordeBlue: false,
            filterVal: '',
            btnStus: 0,
            type: "0",
            tab: "-1",
            tableList: [{},{}],
            pageIndex:1,
            pageSize:15,//PAGESIZETHIRTY
            // modalHTML: ''
            count: 60,
            timeType: 0,
            loading: true,
            tabList: [],
            bigTabList: [],
            tabIndex: 0,
            tabIndex2: 0,
            showIconConfirm: false,
            noCancelConfirm: false,
            changeTextConfirm: false,
            includeCancel: false, // 复选框
            curencyList: [
                {
                    key: '',
                    value: '全部'
                },
                {
                    key: 'BTC',
                    value: 'BTC'
                },
                {
                    key: 'VDS',
                    value: 'VDS'
                },
                {
                    key: "USDT",
                    value: 'USDT'
                }
            ],
            tradeTypeList: [
                {
                    key: '',
                    value: '全部'
                },
                {
                    key: 1,
                    value: '购买'
                },
                {
                    key: 2,
                    value: '出售'
                },
            ],
            curency: '',
            tadeType: ''
        };

        // A业务
        this.A = null;
        // B业务
        this.B = null;
    }
    componentDidMount(){
        console.log(123, this.props);
        let that = this;
        setTimeout(function () {
            that.setState({
                loading: false
            })
        },1000);
        // 负责获取数据
        DemoPageModel().then((res)=>{
            that.setState({
                tabList: ['全部订单','待付款(3)','待放币','交易完成(2)','异常/申诉(1)'],
                bigTabList: ['购买','出售']
            })
        });

        // 模型B
        MB().then(res => {
            this.B = res;
        })
    }
    //搜索框边框
    changeFocus = () =>{
        this.setState({
            bordeBlue: true
        })
    };
    changeBlur = () => {
        this.setState({
            bordeBlue: false
        })
    };
    clearFilterVal=()=> {
        this.setState({
            filterVal: '',
            btnStus: 0
        })
    }
    testMsg = () =>{
        this.props.testGlobalTips();
    }
    //输入时 input 设置到 satte
    handleInputChange=(event) =>{
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value,
        },() => {
            console.info('搜索啦')
        });
        if (name == 'filterVal' && value) {
            this.setState(preState => {
                if (preState.btnStus != 1) {
                    return { btnStus: 1}
                }
            })
        } else {
            this.setState(preState => {
                if (preState.btnStus != 0) {
                    return { btnStus: 0}
                }
            })
        }
    };
    // 下拉框的选择
    getCodeType =(item = {})=>{
        this.setState({
            type: item.val,
        },() => console.info('搜索啦'));
    }
    getChangeType=(item = {})=>{
        this.setState({
            tab:item.val,
        },() => console.info('搜索啦'));
    }
    //分页
    currentPageClick= (values)=>{
        this.setState({
            pageIndex:values
        },() => console.info('加载数据'));
    }
    testInfo = () =>{
        trade_pop({},"hahahah",0,{timer:3000});
    }
    openModalInfo=()=>{
        //confirm demo
        let str = <div className="Jua-table-inner Jua-table-main ">
            <div className="head react-safe-box-head">
                <h3 className="tc">交易前请先登录</h3>
            </div>

            <div className="foot">
                <a id="JuaBtn_8_2" role="button" className="btn btn-outgray btn-sm" onClick={() => this.modal.closeModal()}><FormattedMessage id="取消"/></a>
                <a id="JuaBtn_8_1" role="button" className="btn btn-primary btn-sm" onClick={()=>{this.modal.closeModal();window.location.href = formatURL('/login')}}><FormattedMessage id="去登录"/></a>
            </div>
            <div className="zoom"></div>
        </div>;

        this.setState({modalHTML:str},()=>{
            this.modal.openModal();
        });
    };
    openToast = ()=> {
        // toast 提示
        optPop(()=> {
            console.info('测试');
        },'交易成功');
    };
    handleChangeTime = (val)=> {
        this.setState({
            timeType: val
        });
    };
    goUrl = ()=> {
        this.props.history.push('/otc/*');
        console.info(this.props)
    };
    tabChange =(val, tag)=> {
        if(tag) {
            this.setState({
                tabIndex: val
            })
        } else {
            this.setState({
                tabIndex2: val
            })
        }
    };
    showIconConfirm = ()=> {
        this.setState({
            showIconConfirm: true
        })
    };
    noCancelConfirm = ()=> {
        this.setState({
            noCancelConfirm: true
        })
    };
    changeTextConfirm = ()=> {
        this.setState({
            changeTextConfirm: true
        })
    }
    checkBoxChange = () => {
        this.setState({
            includeCancel:!this.state.includeCancel,
        })
    };
    chooseItem =(val, type)=> {
        if(type) {
            this.setState({
                curency: val
            })
        } else {
            this.setState({
                tadeType: val
            })
        }
    };
    render(){
        const {formatMessage} = this.props.intl;
        const {tadeType,tradeTypeList,curency,curencyList,username,errors, bordeBlue, filterVal, btnStus,tab,type,tableList,pageIndex,pageSize, count,timeType,loading,tabList,bigTabList,tabIndex,tabIndex2,showIconConfirm,noCancelConfirm,changeTextConfirm,includeCancel } = this.state;
        const { setUserName,fIn, bOut} = this; // 从form中 解构
        const { username:eusername = [] } = errors;
        return (
            <div style={{ paddingBottom: '40px' }}>
                {/*<Headers />*/}
                <div style={{ overflow: 'hidden' }}>
                    <h6 style={{ color: 'red',marginTop: '20px' }}>输入框的样式</h6><br/>
                    <input className="form_input" placeholder="请输入资金密码"/>
                    <h6 style={{ color: 'red',marginTop: '20px' }}>输入搜索框</h6><br/>
                    <div className="entrust-head">
                        <div className={bordeBlue ? "input-box borde-blue" : 'input-box'}>
                            <input type="text" name="filterVal" value={filterVal} placeholder="输入订单号" onChange={this.handleInputChange} onFocus={this.changeFocus} onBlur={this.changeBlur} />
                            <button onClick={btnStus == 1 ? this.clearFilterVal : null} className={btnStus == 0 ? "iconfont icon-search-bizhong" : "iconfont icon-shanchu-moren"}></button>
                        </div>
                        <div className="entrust-head-type left">
                            <h5 className="left padd5">币种：</h5>
                            {
                                curencyList.map((item,index) => {
                                    return (
                                        <span key={index} className={`currency_label ${curency == item.key ? 'curency_choose': ''}`} onClick={()=>this.chooseItem(item.key, 1)}>{item.value}</span>
                                    )
                                })
                            }
                        </div>
                        <div className="entrust-head-type left">
                            <h5 className="left padd5">交易类型：</h5>
                            {
                                tradeTypeList.map((item,index) => {
                                    return (
                                        <span key={index} className={`currency_label ${tadeType == item.key ? 'curency_choose': ''}`} onClick={()=>this.chooseItem(item.key)}>{item.value}</span>
                                    )
                                })
                            }
                        </div>
                    </div>
                    <h6 style={{ color: 'red',marginTop: '50px' }}>下拉选择框和radio</h6><br/>
                    <div className="entrust-head">
                        <div className="left">
                            <h5 className="left padd5">类型：</h5>
                            <div className="record-head entrust-selcet">
                                <SelectHistory
                                    options = {[
                                        { val:"1", key: "计划" },
                                        { val:"0" , key: "限价" },
                                    ]}
                                    Cb={this.getChangeType}
                                />
                            </div>
                        </div>
                        <div className="entrust-head-type left">
                            <h5 className="left padd5">交易：</h5>
                            <div className="record-head entrust-selcet">
                                <SelectHistory
                                    options = {[
                                        { val:"-1", key: "不限" },
                                        { val:"1" , key: "买入" },
                                        { val:"0" , key: "卖出" },
                                        { val:"2" , key: "卖出" },
                                        { val:"3" , key: "卖出" },
                                        { val:"4" , key: "卖出" },
                                        { val:"5" , key: "卖出" },
                                        { val:"6" , key: "卖出" },
                                        { val:"7" , key: "卖出" },
                                        { val:"8" , key: "卖出" },
                                    ]}
                                    Cb={this.getCodeType}
                                />
                            </div>
                        </div>
                        <div className="entrust-head-type entrust-time left">
                            <h5 className="padl40 padl10">时间：</h5>
                            <ul className="tab-time">
                                <li>
                                    <label className={timeType==0?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'}  onClick={() => this.handleChangeTime(0)}></label>
                                    <span> 24H</span>
                                </li>
                                <li>
                                    <label className={timeType==1?"iconfont icon-danxuan-yixuan":'iconfont icon-danxuan-moren'} onClick={() => this.handleChangeTime(1)}></label>
                                    <span> History</span>
                                </li>
                            </ul>
                        </div>
                        <div className="entrust-head-box left">
                            <div  className={`${includeCancel?"bg-white":""} checkboxitem`}>
                                <i className={includeCancel?"iconfont icon-xuanze-yixuan":"iconfont icon-xuanze-weixuan "} onClick={this.checkBoxChange}></i>
                            </div>
                            <span>checkBox例子</span>
                        </div>
                    </div>
                    <h6 style={{ color: 'red',marginTop: '40px' }}>取消，确定按钮，操作列按钮</h6><br/>
                    <div>
                        <input type="button" className="btn cancel" value={formatMessage({id:"取消"})} />
                        <input type="button" className="btn submit" value={formatMessage({id:"确定"})} />
                        <input type="button" className="btn small_button" value="上架" />
                        <input type="button" className="btn cancel add_btn" value="发布广告" />
                    </div>
                    <h6 style={{ color: 'red',marginTop: '40px' }}>confirm</h6><br/>
                    <div>
                        <input type="button" className="btn cancel" value="带图标的" onClick={this.showIconConfirm}/>
                        {showIconConfirm &&  <Confirm safeIcon={true} content="为了您的资金安全，进行交易前请完成身份认证!" cb={type=> {this.setState({
                            showIconConfirm: false
                        })}}/>}
                        <input type="button" className="btn submit" value="不带取消按钮的" onClick={this.noCancelConfirm}/>
                        {noCancelConfirm &&  <Confirm safeIcon={true} isNotCancel={true} content="请设置昵称、手机号、资金密码。" cb={type=> {this.setState({
                            noCancelConfirm: false
                        })}}/>}
                        <input type="button" className="btn submit" value="取消确定按钮的文本修改" onClick={this.changeTextConfirm} />
                        {changeTextConfirm &&  <Confirm title="下架确认" cancelText="不下架" okText="知道啦" content="广告一旦下架将不可复原，您确认要下架广告吗？" cb={type=> {this.setState({
                            changeTextConfirm: false
                        })}}/>}
                    </div>
                    <h6 style={{ color: 'red' }}>toast</h6><br/>
                    <input type="button" className="btn submit" value="toast测试" onClick={this.openToast} />
                    <h6 style={{ color: 'red' }}>全局信息</h6><br/>
                    <input type="button" className="btn submit" value="全局信息提示" onClick={this.testMsg} />
                    <Tips />
                    <h6 style={{ color: 'red' }}>modal</h6><br/>
                    <input type="button" className="btn submit" value="modal测试" onClick={this.openModalInfo} />
                    <ReactModal ref={modal => this.modal = modal}>
                        {this.state.modalHTML}
                    </ReactModal>
                    <h6 style={{ color: 'red' }}>404页面的跳转</h6><br/>
                    <input type="button" className="btn submit" value="跳转404" onClick={this.goUrl} />
                    <h6 style={{ color: 'red' }}>字体为18的tab</h6><br/>
                    <div>
                        <Tab list={tabList} index={tabIndex} onChange={val=>this.tabChange(val, 1)}/>
                    </div>
                    <h6 style={{ color: 'red',marginTop: '40px' }}>字体为24的tab</h6><br/>
                    <div>
                        <Tab list={bigTabList} index={tabIndex2} className="big_tab" onChange={this.tabChange}/>
                    </div>
                    <h6 style={{ color: 'red',marginTop: '20px' }}>页面的标题此标题的margin-bottom是30px</h6><br/>
                    <div className="page_title">订单详情</div>
                    <h6 style={{ color: 'red' }}>页面的标题此标题的margin-bottom是20px</h6><br/>
                    <div className="page_title margin_b20">购买BTC， 出售OTC</div>
                    <h6 style={{ color: 'red' }}>table的样式和分页以及loading效果</h6><br/>
                    <div className="table_box">
                        <table className="table_content">
                            <thead>
                            <tr>
                                <th width="10%" className="text-left">交易</th>
                                <th width="10%" className="text-left">类型</th>
                                <th width="10%" className="text-left">价格</th>
                                <th width="12%" className="text-left">委托数量/已成交</th>
                                <th width="8%" className="text-left">日期</th>
                                <th width="10%" className="text-left">日期</th>
                                <th width="10%" className="text-left">日期</th>
                                <th width="10%" className="text-left">日期</th>
                                <th width="10%" className="text-left">日期</th>
                                <th width="10%" className="borright text-center">操作</th>
                            </tr>
                            </thead>
                            <tbody id="historyEntrustList">
                            {
                                tableList.length > 0? tableList.map((item,index) => {
                                    return (
                                        <tr key={index + 1}>
                                            <td className="text-left">测试</td>
                                            <td className="text-left">计划</td>
                                            <td className="text-left">100</td>
                                            <td className="text-left">100</td>
                                            <td className="text-left">测试</td>
                                            <td className="text-left">测试</td>
                                            <td className="text-left">测试</td>
                                            <td className="text-left">测试</td>
                                            <td  className="text-left">2017-10-11</td>
                                            <td className="text-center"><a href="javascript:void(0)"> 撤销</a></td>
                                        </tr>
                                    )
                                }):(
                                    <tr className="nodata">
                                        <td className="billDetail_no_list" colSpan="15">
                                            <p className="entrust-norecord">
                                                <svg className="icon" aria-hidden="true">
                                                    <use xlinkHref="#icon-tongchang-tishi"></use>
                                                </svg>
                                                <span>当前没有委托记录数据</span>
                                            </p>
                                        </td>
                                    </tr>
                                )
                            }
                            </tbody>
                        </table>
                    </div>
                    <div className="historyEntrustList-page tablist">
                        <Pages
                            pageIndex={pageIndex}
                            pagesize={pageSize}
                            total={count}
                            ref="pages"
                            currentPageClick = { this.currentPageClick }
                        />
                    </div>
                </div>
                <div className="test-form">
                    <h3>Form 校验</h3>
                    <div className={eusername && eusername[0] && 'err'}>
                        <h3>{formatMessage({id: "电子邮件"})}</h3>
                        <input type="text" className="lj" />
                        <input autoComplete="off" onFocus={fIn} onBlur={bOut} name='username' type="text" onChange={setUserName} value={username} className="i1" placeholder={formatMessage({id: "请输入电子邮件（水印）"})} tabIndex="1" />
                        <input type="text" className="lj" />
                        <span className="ew">{eusername && eusername[0]}</span>
                    </div>
                </div>
                <div>
                    <FileUploads formatMessage={formatMessage} limit='2' msg={formatMessage({id:'最多XXX张'}).replace('XXX',2)} filetype="image" ckImgList={() =>{}} />
                </div>
                {
                    loading && ThemeFactory.getThemeInstance(Styles.ThemeA)
                }
                {/* <Footer /> */}
                {/*123*/}
                {/*{*/}
                {/*!this.A ? */}
                {/*ThemeFactory.getThemeInstance(Styles.ThemeA)*/}
                {/*:*/}
                {/*<div>*/}
                {/*name: {this.A.name}*/}
                {/*id: {this.A.id}*/}
                {/*</div>*/}
                {/*}*/}
                {/*{*/}
                {/*!this.B ? */}
                {/*ThemeFactory.getThemeInstance(Styles.ThemeA)*/}
                {/*:*/}
                {/*console.log(this.B)*/}
                {/*}*/}
            </div>
        )
    }
}

export default withRouter(injectIntl(DemoPage));