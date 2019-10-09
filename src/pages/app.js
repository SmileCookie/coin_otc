import React from 'react'
import ReactDOM from 'react-dom'
import axios from '../utils/fetch'
import Menu from './common/menu'
// import Header from './common/header'
import { ucfirst } from '../utils'
import Home from './home'
import { navWid, DOMAIN_VIP } from '../conf'
import { Tabs, Icon, Popover, Layout } from 'antd'
import ComponentList from './component'
import moment from 'moment'
import DocumentTitle from 'react-document-title';
import ErrorBoundary from './errorBoundary'
import 'moment/locale/zh-cn'
import { jumpItem, jumpItemKeys } from './common/pageurl'
const { Sider } = Layout;
moment.locale('zh-cn')

import '../assets/css/bootstrap.css'
import '../assets/css/custom.css'
import '../assets/css/app.css'
import '../../node_modules/react-grid-layout/css/styles.css'
import '../../node_modules/react-resizable/css/styles.css'
import '../../node_modules/video-react/dist/video-react.css'

const TabPane = Tabs.TabPane


export default class Index extends React.Component {
    constructor(props) {
        super(props)
        this.newTabIndex = 0;
        this.state = {
            height: document.body.clientHeight,
            activeKey: '我的主页',
            panes: [{ title: '我的主页', content: <Home jumpCom={() => this.add(jumpItem[0])} />, key: '我的主页', closable: false }],//<Home jumpCom={() => this.add(jumpItem)} />
            menuId: 1,
            menuList: [], //整个menu树
            FmenuList: [],
            SmenuList: [],
            TmenuListAll: [],
            collapsed: false,
            permissList: [],
            appObj: null,
            windowWidth: 0,
            positionStr: '当前位置：工作台 > 我的待办',
            // tabLenth:0,
            // windowWidth:0,        
        }
        this.onChange = this.onChange.bind(this)
        this.onEdit = this.onEdit.bind(this)
        this.add = this.add.bind(this)
        this.remove = this.remove.bind(this)
        this.setActive = this.setActive.bind(this)
        this.onClickFirstMenu = this.onClickFirstMenu.bind(this)
        this.closeAllTbs = this.closeAllTbs.bind(this)
        this.closeLeft = this.closeLeft.bind(this)
        this.closeRight = this.closeRight.bind(this)
        this.toggle = this.toggle.bind(this)
        this.setAppObj = this.setAppObj.bind(this)
        this.getPosition = this.getPosition.bind(this)
    }
    componentDidMount() {
        this.requestNavlist()
        // this.onResizeWindow()
        // window.addEventListener('resize',this.onResizeWindow); 

    }
    //请求导航栏
    requestNavlist() {
        axios.get(DOMAIN_VIP + "/sys/menu/getRoleMenu").then(res => {

            const result = res.data
            if (result.code == 0) {
                let FmenuList = [],
                    SmenuListAll = [],
                    TmenuListAll = [];
                for (let i = 0; i < result.menuList.length; i++) {
                    let SmenuList = [];
                    FmenuList[i] = {}
                    FmenuList[i].name = result.menuList[i].name
                    FmenuList[i].class = result.menuList[i].icon
                    FmenuList[i].key = result.menuList[i].menuId
                    for (let j = 0; j < result.menuList[i].list.length; j++) {
                        let TmenuList = [];
                        SmenuList[j] = {}
                        SmenuList[j].name = result.menuList[i].list[j].name
                        SmenuList[j].className = result.menuList[i].list[j].icon
                        SmenuList[j].key = result.menuList[i].list[j].menuId
                        SmenuList[j].parentId = result.menuList[i].list[j].parentId
                        SmenuList[j].childMenu = []
                        for (let k = 0; k < result.menuList[i].list[j].list.length; k++) {
                            TmenuList[k] = {}
                            TmenuList[k].name = result.menuList[i].list[j].list[k].name
                            TmenuList[k].key = result.menuList[i].list[j].list[k].menuId
                            TmenuList[k].url = result.menuList[i].list[j].list[k].url
                        }
                        SmenuList[j].childMenu = TmenuList
                        TmenuListAll = TmenuListAll.concat(TmenuList)
                    }
                    SmenuListAll = SmenuListAll.concat(SmenuList)
                }

                //   SmenuListAll[0].childMenu.push({name:'测试',key:6,url:'/test'})
                //   SmenuListAll[0].childMenu.push({name:'充值审核',key:6,url:'/financialcenter/paymentmod/rechargeAudit'})
                // SmenuListAll[0].childMenu.push({name:'用户资料管理',key:6,url:'/systemcenter/usermod/userDataManage'})
                  
                
                this.setState({
                    FmenuList,
                    SmenuList: SmenuListAll,
                    menuId: result.menuList[0].menuId,
                    permissList: result.permissions,
                    menuList: result.menuList,
                    TmenuListAll,
                    panes: [{ title: '我的主页', content: <Home _this={this} jumpCom={() => this.add(jumpItem[0])} changeSendNum={this.changeSendNum} />, key: '我的主页', closable: false }],
                })
            }
        })
    }
    //tab切换时
    onChange(activeKey) {
        let str = this.getPosition(activeKey);
        this.setState({ activeKey, positionStr: str });
    }
    // 获取当前位置
    getPosition(activeKey) {
        document.getElementsByClassName('local_position')[0].style.display = 'block';
        let str = '当前位置：';
        let arr = this.state.menuList;
        if (activeKey == '我的主页') {
            str = '当前位置：工作台 > 我的待办';
        } else {
            for (let i = 0; i < arr.length; i++) {
                for (let j = 0; j < arr[i].list.length; j++) {
                    for (let m = 0; m < arr[i].list[j].list.length; m++) {
                        if (activeKey == arr[i].list[j].list[m].url) {
                            str += arr[i].name + ' > ' + arr[i].list[j].name + ' > ' + arr[i].list[j].list[m].name;
                            document.getElementsByClassName('local_position')[0].style.display = 'block';
                            break;
                        } else if (parseInt(activeKey) && typeof (parseInt(activeKey)) == 'number') {
                            document.getElementsByClassName('local_position')[0].style.display = 'none';
                            break;
                        }
                    }
                }
            }
        }
        return str;
    }
    //tab 编辑时
    onEdit(targetKey, action) {
        this[action](targetKey);
    }
    setActive() {
        this.setState({
            activeKey: '我的主页'
        })
    }
    toggle() {
        this.setState({
            collapsed: !this.state.collapsed,
        });
    }
    //添加 Tabs
    add(item) {
        let actKey = '';
        let widthCheck = '/financialcenter/operataccountmod/operatWithdrawApprove';
        const { panes, activeKey } = this.state;
        console.log(item)
        // const activeKey = `newTab${this.newTabIndex++}`;
        if (item.url == widthCheck) {
            let menuIndex;
            for (let i = 0; i < panes.length; i++) {
                if (panes[i].key == item.key) {
                    menuIndex = i;
                    break;
                }
            }
            let component = ComponentList[ucfirst(item.url).replace(/\s+/g, '')];
            if (item.key == activeKey) {//当前显示tab和点击的tab是同一个
                return false;
            } else if (!menuIndex && menuIndex !== 0) {
                this.setState({
                    panes: panes.concat({ title: item.name, content: component, key: String(item.key), _this: this }),
                    activeKey: String(item.key)
                })
                actKey = String(item.key);
            } else {
                this.setState({
                    activeKey: String(item.key)
                })
                actKey = String(item.key);
            }
        } else {
            let menuIndex;
            for (let i = 0; i < panes.length; i++) {
                if (panes[i].key == item.url) {
                    menuIndex = i;
                    break;
                }
            }
            let component = ComponentList[ucfirst(item.url).replace(/\s+/g, '')];
            if (item.url == activeKey) {//当前显示tab和点击的tab是同一个
                return false;
            } else if (!menuIndex && menuIndex !== 0) {
                this.setState({
                    panes: panes.concat({ title: item.name, content: component, key: item.url, _this: this }),
                    activeKey: item.url
                })
                actKey = item.url;
            } else {
                this.setState({
                    activeKey: item.url
                })
                actKey = item.url;
            }
        }
        // 设置当前位置
        let str = this.getPosition(actKey);
        this.setState({
            positionStr: str
        })
    }
    setAppObj(obj) {
        this.setState({
            appObj: Object.assign({}, obj)
        })
    }
    // onResizeWindow = () => {
    //     this.setState({
    //         windowWidth:document.body.clientWidth
    //     })
    //     // window&&console.log(document.body.clientWidth)
    // }
    //移除 Tabs
    remove(targetKey) {
        let activeKey = this.state.activeKey;
        let lastIndex;
        this.state.panes.forEach((pane, i) => {
            if (pane.key === activeKey) {
                lastIndex = i - 1;
            }
        });
        const panes = this.state.panes.filter(pane => pane.key !== targetKey);
        if (lastIndex >= 0 && activeKey === targetKey) {
            activeKey = panes[lastIndex].key;
        }
        // 获取当前位置
        let str = this.getPosition(activeKey);
        this.setState({ panes, activeKey, positionStr: str });
    }

    //移除右侧 Tabs
    closeRight() {
        let activeKey = this.state.activeKey;
        let activeIndex;
        let panes = [];
        this.state.panes.forEach((pane, i) => {
            if (pane.key === activeKey) {
                activeIndex = i
            }

        });
        this.state.panes.forEach((pane, i) => {
            if (i <= activeIndex) {
                panes.push(pane)
            }
        });
        this.setState({ panes });
    }
    //移除左侧 Tabs
    closeLeft() {
        let activeKey = this.state.activeKey;
        let activeIndex;
        let panes = [{ title: '我的主页', content: <Home jumpCom={() => this.add(jumpItem)} />, key: '我的主页', closable: false }];
        this.state.panes.forEach((pane, i) => {
            if (pane.key === activeKey) {
                activeIndex = i
                // panes.push(pane);
            }

        });
        this.state.panes.forEach((pane, i) => {
            if (i >= activeIndex) {
                panes.push(pane);

            }
        });
        if (!(this.state.panes.length == 1 && this.state.panes[0].key == '我的主页')) {

            this.setState({ panes });
        }

    }

    //一级导航点击时
    onClickFirstMenu(key) {
        this.setState({
            menuId: key,
        })
    }
    //全部关闭
    closeAllTbs() {
        this.setState({
            positionStr: '当前位置：工作台 > 我的待办',
            activeKey: '我的主页',
            panes: [{ title: '我的主页', content: <Home jumpCom={() => this.add(jumpItem)} />, key: '我的主页', closable: false }]
        });
    }
    setDt = (positionStr) => {
        let arr = positionStr.split('>');
        return '后台管理系统-' + arr[arr.length - 1].trim()
    }
    render() {
        const { menuId, FmenuList, SmenuList, height, permissList, chengeSend, windowWidth, activeKey, TmenuListAll, positionStr, } = this.state
        return (
            <DocumentTitle title={this.setDt(positionStr)}>
                <div className="container body">
                    <div className="main_container">
                        <Layout>
                            <Sider
                                width={230}
                                collapsible
                                collapsed={this.state.collapsed}
                            >
                                <Menu addNavTab={this.add} SmenuList={SmenuList} menuId={menuId} setActive={this.setActive} toggle={this.toggle} />
                            </Sider>
                            <Layout>
                                <div className="top_nav">
                                    <div className="nav_menu">
                                        <nav>
                                            <ul className="nav navbar-nav navbar-left">
                                                {
                                                    FmenuList.map((item, index) => {
                                                        return (
                                                            <li key={index} id={item.key} onClick={() => this.onClickFirstMenu(item.key)} className={item.key == menuId ? 'active' : ''}>
                                                                <a href="javascript:void(0)">
                                                                    <i className={"pos-left iconfont " + item.class}></i>
                                                                    {item.name}
                                                                </a>
                                                            </li>
                                                        )
                                                    })
                                                }
                                            </ul>
                                        </nav>
                                    </div>
                                </div>
                                <div className="right_col" style={{ height: height - navWid }}>
                                    <div className="topNav">
                                        <Tabs
                                            hideAdd
                                            onChange={this.onChange}
                                            activeKey={this.state.activeKey}
                                            type="editable-card"
                                            onEdit={this.onEdit}
                                        >
                                            {this.state.panes.map(pane =>

                                                <TabPane _this={pane._this} tab={pane.title} key={pane.key} closable={pane.closable} activeKey={pane.activeKey}>
                                                    <ErrorBoundary>

                                                        {
                                                            pane.key == '我的主页' ? pane.content :
                                                                <pane.content
                                                                    appObj={this.state.appObj}
                                                                    _this={pane._this}
                                                                    permissList={permissList}
                                                                    windowWidth={windowWidth}
                                                                    appActiveKey={activeKey}
                                                                    TmenuListAll={TmenuListAll}
                                                                />
                                                        }
                                                    </ErrorBoundary>

                                                </TabPane>)}
                                        </Tabs>
                                        <div className="local_position">{positionStr}</div>
                                        <div className="close-all">
                                            <Popover placement="leftTop"
                                                content={<div>
                                                    <p style={{ cursor: "pointer" }} onClick={this.closeLeft}>关闭左侧</p>
                                                    <p style={{ cursor: "pointer" }} onClick={this.closeRight}>关闭右侧</p>
                                                    <p style={{ cursor: "pointer" }} onClick={this.closeAllTbs}>关闭全部</p>
                                                </div>} arrowPointAtCenter>
                                                <Icon type="bars" />
                                            </Popover></div>
                                        {/* <Button type="more" className="close-all" onClick={this.closeAllTbs}>X</Button> */}

                                    </div>
                                </div>
                            </Layout>
                        </Layout>

                    </div>
                </div>
            </DocumentTitle>
        )
    }
}
































