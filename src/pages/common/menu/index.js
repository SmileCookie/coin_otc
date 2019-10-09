
import React from 'react'
import cookie from 'js-cookie'
import axios from '../../../utils/fetch'
import history from '../../../utils/history'
import qs from 'qs'
import moment from 'moment'
import { DOMAIN_VIP } from '../../../conf'
import { SlideDown } from 'react-slidedown'
import { Link } from 'react-router-dom'
import 'react-slidedown/lib/slidedown.css'
import Img from '../../../assets/images/img.png'
import Menulogo from '../../../assets/images/menulogo.svg'
import Logo  from '../../../assets/images/logo.svg'
import { Modal,Upload,Icon,Button,message } from 'antd'
import classNames from 'classnames'

export default class Menu extends React.Component{
    constructor(props){
        super(props)
        this.state={
            menuShow:'',
            username:'',
            visible:false,
            previewImage: '',
            fileList: [{
              uid: -1,
              name: 'xxx.png',
              status: 'done',
              url:''
            }],
            password:'',
            newPassword:'',
            snewPassword:'',
            time:'',
            token:'',
            icon:'',
            qiniu_host:"https://o4we6sxpt.qnssl.com/",
            height:document.body.clientHeight-170,
            collapsed:false
        }
        this.silderDown = this.silderDown.bind(this)
        this.stopProps = this.stopProps.bind(this)
        this.userLogout = this.userLogout.bind(this)
        this.getUserName = this.getUserName.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.resetPassword = this.resetPassword.bind(this)
        this.resetPasswordBtn = this.resetPasswordBtn.bind(this)
        this.handlePreview = this.handlePreview.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.chinaTime = this.chinaTime.bind(this)
        // this.getQiniuToken = this.getQiniuToken.bind(this)
        this.onResizeWindow = this.onResizeWindow.bind(this)
        this.toggle = this.toggle.bind(this)
        this.uploadImageCos = this.uploadImageCos.bind(this)
        this.getAuthorization = this.getAuthorization.bind(this)
    }

    componentDidMount(){
        this.getUserName()
        // this.getQiniuToken()
        this.chinaTime()
        // this.silderDown(0)
        this.onResizeWindow()
        window.addEventListener('resize', this.onResizeWindow)
    }
    toggle ()  {
        this.setState({
          collapsed: !this.state.collapsed,
        });
        this.props.toggle()
      }
    
    onResizeWindow(){
        this.setState({
            height:document.body.clientHeight-170,
        })
    }

    componentWillUnmount() {
        clearInterval(this.interval);
        window.removeEventListener('resize', this.onResizeWindow)
    }

    // //获取七牛的 token
    // getQiniuToken(){
    //     axios.post(DOMAIN_VIP +"/news/getTencentToken").then(res => {
            
    //         const result = res.data
    //         console.log(result)
    //         if (result.code == 0){
    //             this.setState({
    //                 uploadUrl:result.data.host,
    //                 token: result.data.token,
    //                 key:result.data.key,
    //             })
    //         }
    //     })
    // }
    //上传 腾讯云
    getAuthorization(callback){
        axios.post(DOMAIN_VIP+"/news/getTencentToken").then(res => {
            const result = res.data
            console.log(result)
            callback({
                url:result.data.host,
                key:result.data.key,
                XCosSecurityToken: result.data.token
            });
        })
    }

    uploadImageCos(file){
        this.getAuthorization((info) => {
            console.log(info,file)
            file.status ='done'
            let fd = new FormData();
            fd.append('key', info.key);
            fd.append('Signature', info.XCosSecurityToken);
            fd.append('Content-Type', '');
            fd.append('file',file);
            // let config = {
            //     headers: {'Content-Type': 'multipart/form-data'}
            //   }
              const xmlhttp = new XMLHttpRequest();
              xmlhttp.open('post', info.url, true);
              xmlhttp.send(fd);
              this.setState({
                  tencent_host:info.url,
                  tencent_key:info.key,
              },()=>this.uploadSysImg())
        })
    }
    //时间
    chinaTime(){
        this.interval = setInterval(() => this.setState({
            time:moment().format('LLL')
        }), 1000);
    }
    
    stopProps(event,item){
        event.stopPropagation()
        this.props.addNavTab({
            name:item.name,
            url:item.url,
            key:item.key,
        })
    }

    silderDown(index){
        if(this.state.menuShow === index){
            this.setState({
                menuShow:''
            })
        }else{
            this.setState({
                menuShow:index
            })
        }
    }
    //获取用户名
    getUserName(){
        axios.get(DOMAIN_VIP+"/sys/user/info").then(res => {
            const result = res.data            
            if(result.code == 0){
                this.setState({
                    username:result.user.username,
                    userId:result.user.userId,
                    icon:result.user.icon,
                    fileList: [{
                        uid: -1,
                        name: 'xxx.png',
                        status: 'done',
                        url: result.user.icon||Img
                      }],
                })
                cookie.set('userId', result.user.userId, { expires: 7 });
                cookie.set('userName', result.user.username, { expires: 7 });
            }
        })
    }

    //登出
    userLogout(){
        axios.post(DOMAIN_VIP+"/sys/logout").then(res => {
            const result = res.data
            if(result.code == 0){
                // window.location.reload()
                cookie.remove("userId")
                cookie.remove("token")
                history.push('/login')
            }
        })
    }

    //弹窗隐藏
    handleCancel(){
        this.setState({ 
            visible: false,
            password:'',
            newPassword:'',
            snewPassword:''
        });
    }
    //修改密码
    resetPassword(){
        this.footer = [ <Button key="back" onClick={this.handleCancel}>取消</Button>,
                        <Button key="submit" type="more" loading={this.state.loading} onClick={this.resetPasswordBtn}>
                            保存
                        </Button>]
        this.setState({
            visible:true
        })

    }
    //修改密码按钮
    resetPasswordBtn(){
        const { password,newPassword,snewPassword } = this.state
        if(newPassword !== snewPassword){
            message.warning("两次输入密码不一致！")
            return false;
        }
        axios.post(DOMAIN_VIP+"/sys/user/password",qs.stringify({
            password,
            newPassword
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    visible:false,
                    password:'',
                    newPassword:'',
                    snewPassword:''
                })
            }else{
                message.warning(result.msg)
            }
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //头像上传
    handlePreview(file){
        console.log(file)
        this.setState({
            previewImage: file.name || file.thumbUrl,
            previewVisible: true,
        });
    }
    //图片上传改变
    handleChange({ fileList }){
        
        const size = fileList.length
        fileList[size-1].status = 'done'
        this.setState({ fileList },() => {
            console.log(this.state.fileList)
            if(fileList[size-1].status == 'done'){
                //this.uploadSysImg()
            }
        })
    }
    //将 URL 上传自己服务器
    uploadSysImg(){
        const { userId,fileList,qiniu_host,tencent_host,tencent_key } = this.state
        console.log(tencent_host,tencent_key)
        //const icon = qiniu_host+ fileList[0].response.key
        const icon = tencent_host+ tencent_key
        axios.post(DOMAIN_VIP+"/sys/user/updIcon",qs.stringify({
            userId,
            icon
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                message.success(result.msg)
                this.setState({
                    icon 
                })
            }else{
                message.warning(result.msg)
            }
        })
    }


    render(){
        const { menuShow,username,height,token,collapsed,icon,visible,userId,fileList,password,newPassword,snewPassword,time,key,fd } = this.state
        let uploadUrl;
        // if (window.location.protocol === 'https:') {
        //     uploadUrl = 'https://up.qbox.me';
        // } else {
        //     uploadUrl = 'http://up.qiniu.com';
        // }
        uploadUrl = 'https://idopy-1253901570.cos.ap-beijing.myqcloud.com/'
        let imgName = new Date().getTime()
        imgName = "bitglobal/newsupload/" + imgName;
        
        let nfileList = fileList.slice(-1)
        // console.log(11111111111,nfileList)
        const uploadButton = (
            <div>
              <Icon type="plus" />
              <div className="ant-upload-text">Upload</div>
            </div>
          );
        return (            
            <div className="col-md-3 left_col">
            <div className="left_col scroll-view">
            {collapsed?
                <div className="navbar nav_title" style={{border: 0}}>
                    <a to="/" className="site_title">
                        <img src={Logo} alt="menulogo"/>
                    </a>
                 </div>:
                 <div className="navbar nav_title" style={{border: 0}}>
                 <a to="/" className="site_title">
                     <img src={Menulogo} alt="menulogo"/>
                 </a>
             </div>
            }
                

                <div className="profile">
                    <div className={collapsed?'profile_coll':'profile_pic'} onClick={this.resetPassword}>
                        <img src={icon||Img}  className="img-circle profile_img" />
                    </div>
                    
                    <div className={collapsed?'profile_info profile_info_coll':"profile_info"}>
                        <span  onClick={this.resetPassword}>欢迎</span>
                        <h2><b onClick={this.resetPassword}>{username}</b> <a href="javascript:void(0)" className="logout" onClick={this.userLogout}>退出</a></h2>
                    </div>
                   
                </div>

                    <div id="sidebar-menu" style={{maxHeight:height}} className="main_menu_side hidden-print main_menu">
                    <div className="menu_section">
                    {collapsed?'':<h3>{time}</h3>}
                            <ul className={classNames('nav', 'side-menu',{'side-hidden-print': !collapsed})} style={{maxHeight:height-100}}>
                            {
                                this.props.SmenuList.map((item,index) => {
                                    if(item.parentId == this.props.menuId){
                                        let list = <li key={index} onClick={() => this.silderDown(index)} className={menuShow === index?collapsed?'active li_coll':'active':collapsed?'li_coll':''}>
                                                <a><i className={item.className?"iconfont " + item.className:"iconfont icon-tongyonglei"}></i>
                                                <b>{item.name}</b><span className={classNames("iconfont icon-xialaxuanze-",{'xialaxuanzed':menuShow === index})}></span></a>
                                                <SlideDown className='my-dropdown-slidedown'>
                                                    <ul className="nav child_menu" style={{display:menuShow === index?'block':''}}>
                                                        {
                                                            item.childMenu&&item.childMenu.map((list)=>{
                                                                return <li key={list.name}><a to={list.url} id={list.url} onClick={(e) => this.stopProps(e,list)}>{list.name}</a></li>
                                                            })
                                                        }
                                                    </ul>
                                                </SlideDown>   
                                                <div className="hover_aside"><ul  className='hover_ul' style={{display:menuShow === index?'block':''}}>
                                                        {
                                                            item.childMenu&&item.childMenu.map((list)=>{
                                                                return <li key={list.name}><a to={list.url} id={list.url} onClick={(e) => this.stopProps(e,list)}>{list.name}</a></li>
                                                            })
                                                        }
                                                    </ul></div>
                                            </li>
                                     return list;
                                    }
                                })
                            }
                        </ul>
                    </div>

                </div>

                <div className="sidebar-footer hidden-small">
                    <a data-toggle="tooltip" data-placement="top" title="Settings" onClick={this.toggle}>
                    {/* <span className="iconfont icon-shezhi" aria-hidden="true"></span> */}
                    <Icon
                        className="trigger"
                        type={this.state.collapsed ? 'menu-unfold' : 'menu-fold'}
                        
                    />
                    </a>
                    {
                        !collapsed&&
                    <a data-toggle="tooltip" data-placement="top" title="FullScreen">
                        <span className="iconfont icon-caozuo_quanpingfangda" aria-hidden="true"></span>
                    </a> }
                    { !collapsed&&
                     <a data-toggle="tooltip" data-placement="top" title="Lock">
                        <span className="iconfont icon-iconshengyin" aria-hidden="true"></span>
                    </a>   
                    }
                    
                   
                    
                    <a data-toggle="tooltip" data-placement="top" title="Logout" onClick={this.userLogout}>
                        <span className="iconfont icon-guanbi" aria-hidden="true"></span>
                    </a>
                </div>
            </div>
            <Modal
                visible={visible}
                title="个人设置"
                width="600px"
                onCancel={this.handleCancel}
                footer={this.footer}
                >
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="imgbox">
                        <Upload
                            // action={uploadUrl}
                             listType="picture-card"
                            fileList={nfileList}
                             onPreview={this.handlePreview}
                             onChange={this.handleChange}
                            // data={{ Signature: token, key: key,ContentType:''}}
                            showUploadList={{
                                showPreviewIcon:true,
                                showRemoveIcon:false
                            }}
                            multiple={false}
                            // headers={{
                            //     ContentType:'',
                            // }}
                            // name="file"
                            // withCredentials={true}
                            // customRequest={(e) => {
                            //     console.log(e);
                            //     console.log('-----');
                            // }}
                            customRequest={(e) => {
                                this.uploadImageCos(e.file)
                            }}
                            beforeUpload={(file,fileList)=>{
                               console.log(file,fileList)
                            }}
                            >
                            {uploadButton}
                        </Upload>
                    </div>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">原始密码：</label>
                        <div className="col-sm-8">
                            <input type="password" className="form-control"  name="password" value={password} onChange={this.handleInputChange} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">新密码：</label>
                        <div className="col-sm-8">
                            <input type="password" className="form-control" name="newPassword" value={newPassword} onChange={this.handleInputChange} />
                        </div>
                    </div>
                    <div className="form-group">
                        <label className="col-sm-3 control-label">确认新密码：</label>
                        <div className="col-sm-8">
                            <input type="password" className="form-control" name="snewPassword" value={snewPassword} onChange={this.handleInputChange} />
                        </div>
                    </div>       
                </div>
            </Modal>
        </div>
        )
    }

}
Menu.defaultProps = {
    menuList:[
        {
            name:'任务管理',
            className:'icon-iconfonthome0',
            key:1,
            parentId:1,
            childMenu:[
                {
                    name:'任务查询',
                    url:'/deskcenter/opertaskmod/operTask',
                    key:2
                }
            ]
        },
        {
            name:'财务管理',
            className:'icon-caiwuguanli',
            parentId:4,
            childMenu:[
                {
                    name:'F冲提结算',
                    url:'/financialcenter/financialmod/accountManage',
                    key:2
                },
                {
                    name:'F冲提结算查询',
                    url:'/financialcenter/financialmod/settleMent',
                    key:3
                },
                {
                    name:'收支用途',
                    url:'/financialcenter/financialmod/capitalUse',
                    key:3
                },
                {
                    name:'划转结算',
                    url:'/financialcenter/financialmod/transferSettlement',
                    key:4
                },
                {
                    name:'Demo',
                    url:'finance.html',
                    key:4
                },
                
            ]
        },{
            name:'资金报表',
            className:'icon-caiwuguanli',
            parentId:7,
            childMenu:[
                {
                    name:'F平台资金累计',
                    url:'/reportcenter/capitalreportmod/centerCapitalSum',
                    key:1
                },
                {
                    name:'F平台资金日报',
                    url:'/reportcenter/capitalreportmod/centerCapitalDaily',
                    key:2
                },{
                    name:'钱包收支表',
                    url:'/reportcenter/capitalreportmod/walletCapital',
                    key:2
                },{
                    name:'交易平台资金总账',
                    url:'/reportcenter/capitalreportmod/capitalAccount',
                    key:2
                }
                
            ]
        },{
            name:'用户报表',
            className:'icon-yonghubaobiao',
            parentId:7,
            childMenu:[
                {
                    name:'用户冲提交易统计',
                    url:'/reportcenter/userreportmod/userPaymentTradeCount',
                    key:1
                },
                {
                    name:'用户登录注册统计',
                    url:'/reportcenter/userreportmod/userBaseOperCount',
                    key:2
                },{
                    name:'用户持仓统计',
                    url:'/reportcenter/userreportmod/userPositionCount',
                    key:3
                }
                
            ]
        },{
            name:'财务类报表',
            className:'icon-caiwuguanli',
            parentId:7,
            childMenu:[
                {
                    name:'F火星表',
                    url:'/reportcenter/financialmod/mars',
                    key:1
                }
                
            ]
        },
        {
            name:'资金管理',
            className:'icon-zijinguanli',
            parentId:4,
            childMenu:[
                // {
                //     name:'F用户资金',
                //     url:'/financialcenter/capitalmod/userCapital',
                //     key:5
                // },
                // {
                //     name:'F资金统计',
                //     url:'/financialcenter/capitalmod/capitalCount',
                //     key:6
                // },
                // {
                //     name:'F账单明细',
                //     url:'/financialcenter/capitalmod/billDetail',
                //     key:7
                // },
                {
                    name:'手续费收益',
                    url:'/financialcenter/capitalmod/feeProfit',
                    key:8
                }
            ]
        },
        {
            name:'资金统计',
            className:'icon-money',
            parentId:4,
            childMenu:[
                {
                    name:'F币币资金统计',
                    url:'/financialcenter/fundstatistics/capitalCount',
                    key:5
                },
                {
                    name:'F钱包资金统计',
                    url:'/financialcenter/fundstatistics/capitalCountWallet',
                    key:6
                },
                {
                    name:'FOTC资金统计',
                    url:'/financialcenter/fundstatistics/capitalCountOTC',
                    key:7
                },
            ]
        },
        {
            name:'用户资金',
            className:'icon-money',
            parentId:4,
            childMenu:[
                {
                    name:'F币币',
                    url:'/financialcenter/userfundsmod/userCapital',
                    key:5
                },
                {
                    name:'F钱包',
                    url:'/financialcenter/userfundsmod/userCapitalWallet',
                    key:6
                },
                {
                    name:'FOTC',
                    url:'/financialcenter/userfundsmod/userCapitalOTC',
                    key:7
                },
                {
                    name:'F资金汇总',
                    url:'/financialcenter/userfundsmod/userCapitalSummary',
                    key:8
                },
            ]
        },
        {
            name:'账单明细',
            className:'icon-caiwu',
            parentId:4,
            childMenu:[
                {
                    name:'F币币',
                    url:'/financialcenter/billingdetailsmod/billDetail',
                    key:5
                },
                {
                    name:'F钱包',
                    url:'/financialcenter/billingdetailsmod/billDetailWallet',
                    key:6
                },
                {
                    name:'FOTC',
                    url:'/financialcenter/billingdetailsmod/billDetailOTC',
                    key:7
                },
            ]
        },
        {
            name:'权限管理',
            className:'icon-quanxianguanli',
            parentId:2,
            childMenu:[
                {
                    name:'F角色权限',
                    url:'/systemcenter/authoritymod/functionRole',
                    key:8
                },
                {
                    name:'F功能管理',
                    url:'/systemcenter/authoritymod/functionManage',
                    key:9
                },
                {
                    name:'F操作员管理',
                    url:'/systemcenter/authoritymod/operManage',
                    key:10
                }
            ]
        },
        {
            name:'用户管理',
            className:'icon-yonghuguanli',
            parentId:2,
            childMenu:[
                {
                    name:'F用户信息',
                    url:'/systemcenter/usermod/userInfo',
                    key:1-5-1
                },
                {
                    name:'F登录信息',
                    url:'/systemcenter/usermod/loginInfo',
                    key:1-5-2
                },
                {
                    name:'F认证信息',
                    url:'/systemcenter/usermod/identificationInfo',
                    key:1-5-3
                },
                {
                    name:'黑名单管理',
                    url:'/systemcenter/usermod/blackList',
                    key:1-5-3
                }
            ]
        }, 
        {
            name:'保值管理',
            className:'icon-baozhiguanli',
            parentId:3,
            childMenu:[
                {
                    name:'F保值账户财务',
                    url:'/tradecenter/hedgemod/hedgeAccountFinancial',
                    key:1-5-1
                },{
                    name:'F保值账户状态',
                    url:'/tradecenter/hedgemod/hedgeAccountState',
                    key:1-5-2
                },{
                    name:'F对冲交易记录',
                    url:'/tradecenter/hedgemod/hedgingTradeRecord',
                    key:1-5-3
                }
            ]
        },
        {
            name:'期货保值管理',
            className:'icon-baozhiguanli',
            parentId:3,
            childMenu:[
                {
                    name:'F对冲合约交易记录',
                    url:'/tradecenter/futureshedgemod/hedgeContractTransactionRecord',
                    key:1-5-1
                }
            ]
        },
        {
            name: '盘口管理',
            className: 'icon-jiaoyipingtaiduizhang',
            parentId:3,
            childMenu: [
                {
                    name: 'F盘口交易',
                    url: '/tradecenter/markemod/marketTrade',
                    key: 1-5-1
                }, 
                {
                    name: 'F盘口合并交易',
                    url: '/tradecenter/markemod/marketMergeTrade',
                    key: 1-5-2
                },
                {
                    name: 'F成交记录',
                    url: '/tradecenter/markemod/dealRecord',
                    key: 1-5-3
                },
                {
                    name:'F委托记录',
                    url:'/tradecenter/marketmod/entrustRecord',
                    key:1-5-1
                },
                {
                    name:'F计划委托记录',
                    url:'/tradecenter/marketmod/planEntrustRecord',
                    key:1-5-2
                },
                {
                    name:'F批量挂单',
                    url:'/tradecenter/marketmod/batchEntrust',
                    key:1-5-2
                }

            ]
        },
        {
            name:'支付中心对账',
            className:'icon-zhifuzhongxinduizhang',
            parentId:5,
            childMenu:[
                {
                    name:'F充值对账',
                    url:'/balancecenter/paymentcenterbalancemod/rechargeBalance',
                    key:1-5-1
                },{
                    name:'F提现对账',
                    url:'/balancecenter/paymentcenterbalancemod/withdrawBalance',
                    key:1-5-2
                },{
                    name:'F钱包每日对账',
                    url:'/balancecenter/paymentcenterbalancemod/walletBalance',
                    key:1-5-3
                },{
                    name:'F钱包流水',
                    url:'/balancecenter/paymentcenterbalancemod/walletBill',
                    key:1-5-4
                },{
                    name:'F钱包流水明细',
                    url:'/balancecenter/paymentcenterbalancemod/walletBillDetail',
                    key:1-5-5
                }
            ]
        },
        {
            name:'钱包对账',
            className:'icon-qianbaoduizhang',
            parentId:5,
            childMenu:[
                {
                    name:'F钱包VS交易平台总账',
                    url:'/balancecenter/walletbalancemod/walletTradePlatformLedger',
                    key:1-5-1
                }
            ]
        },
        {
            name:'量化交易对账',
            className:'icon-lianghuajiaoyiduizhang',
            parentId:5,
            childMenu:[
                {
                    name:'F保值对账',
                    url:'/balancecenter/brushbalancemod/hedgeBalance',
                    key:1-5-1
                },{
                    name:'F对冲记录对账',
                    url:'/balancecenter/brushbalancemod/hedgeRecordBalance',
                    key:1-5-2
                }
            ]
        },
        {
            name: 'GBC刷量管理',
            className: 'icon-GBCshualiangguanli',
            parentId: 3,
            childMenu: [
                {
                    name: 'FGBC参数管理',
                    url: '/tradecenter/brushmod/brushParameter',
                    key: 1-5-1
                },
                {
                    name: 'FGBC任务管理',
                    url: '/tradecenter/brushmod/brushTask',
                    key: 1-5-2
                }

            ]
        },
        {
            name: '量化交易管理',
            className: 'icon-lianghuajiaoyiguanli',
            parentId: 3,
            childMenu: [
                {
                    name: 'F量化参数管理',
                    url: '/tradecenter/brushtrademod/brushParameterManage',
                    key: 1-5-1
                },
                {
                    name: 'F量化任务管理',
                    url: '/tradecenter/brushtrademod/brushTaskManage',
                    key: 1-5-2
                }

            ]
        },
        {
            name:'VIP管理',
            className:'icon-VIPguanli',
            parentId:2,
            childMenu:[
                {
                    name:'F积分规则',
                    url:'/systemcenter/vipmod/integralRule',
                    key:1-5-1
                },
                {
                    name:'F积分等级规则',
                    url:'/systemcenter/vipmod/integralVipRule',
                    key:1-5-2
                },
                {
                    name:'F积分流水',
                    url:'/systemcenter/vipmod/integralBill',
                    key:1-5-3
                },
                {
                    name:'F用户等级',
                    url:'/systemcenter/vipmod/userVip',
                    key:1-5-3
                }
            ]
        },
        {
            name:'审核管理',
            className:'icon-shenheguanli',
            parentId:2,
            childMenu:[
                {
                    name:'F实名认证',
                    url:'/systemcenter/verifymod/identityAuthentication',
                    key:1-5-1
                },
                {
                    name:'FGoogle审核',
                    url:'/systemcenter/verifymod/googleVerify',
                    key:1-5-2
                },
                {
                    name:'F手机审核',
                    url:'/systemcenter/verifymod/phoneVerify',
                    key:1-5-3
                }
            ]
        },
        {
            name:'日志管理',
            className:'icon-rizhiguanli',
            parentId:2,
            childMenu:[
                {
                    name:'轨迹日志',
                    url:'/systemcenter/logmod/trajectoryLog',
                    key:1-5-1
                },
                {
                    name:'操作日志',
                    url:'/systemcenter/logmod/operLog',
                    key:1-5-2
                }
            ] 
        },{
            name:'GBC回购管理',
            className:'icon-rizhiguanli',
            parentId:2,
            childMenu:[
                {
                    name:'私钥坐标管理',
                    url:'/monitorcenter/backcapitalmod/backCapitalCoords',
                    key:1-5-1
                },
                {
                    name:'GBC回购管理',
                    url:'/monitorcenter/backcapitalmod/backCapital',
                    key:1-5-2
                }
            ] 
        },
        {
            name:'充提管理',
            className:'icon-chongtiguanli',
            parentId:4,
            childMenu:[
                {
                    name:'F充值记录',
                    url:'/systemcenter/paymentMod/rechargeRecord',
                    key:1-5-1
                },
                {
                    name:'F充值地址',
                    url:'/systemcenter/paymentMod/rechargeAddress',
                    key:1-5-2
                },
                {
                    name:'F提现审核',
                    url:'/systemcenter/paymentMod/withdrawApprove',
                    key:1-5-3
                },
                {
                    name:'F提现地址',
                    url:'/systemcenter/paymentMod/withdrawAddress',
                    key:1-5-3
                },
                {
                    name:'F提现查询',
                    url:'/systemcenter/paymentMod/withdrawRecord',
                    key:1-5-3
                }
            ]
        },
        {
            name: '活动管理',
            className: 'icon-huodongguanli',
            parentId: 2,
            childMenu: [
                {
                    name: 'F投票管理',
                    url: '/systemcenter/activitymod/voteManage',
                    key: 1-5-1
                },
                {
                    name: 'F抽奖管理',
                    url: '/systemcenter/activitymod/drawManage',
                    key: 1-5-2
                }

            ]
        },{
            name:'小额自动打币',
            className:'icon-VIPguanli',
            parentId:6,
            childMenu:[
                {
                    name:'小额自动打币记录',
                    url:'/monitorcenter/smallautopaymod/smallAutoPayRecord',
                    key:1-5-1
                },{
                    name:'小额打币定时任务',
                    url:'/monitorcenter/smallautopaymod/smallPayTask',
                    key:1-5-2
                }
            ]
        },
        {
            name: '系统管理',
            className: 'icon-xitongguanli',
            parentId: 2,
            childMenu: [
                {
                    name: 'F新闻管理',
                    url: '/systemcenter/sysmod/newsManage',
                    key: 1-5-1
                },
                {
                    name: '客户端管理',
                    url: '/systemcenter/sysmod/appManage',
                    key: 1-5-3
                },
                {
                    name: '系统字典',
                    url: '/systemcenter/sysmod/sysDictionary',
                    key: 1-5-3
                },
                {
                    name: '部门管理',
                    url: '/systemcenter/sysmod/deptManage',
                    key: 1-5-3
                },
                {
                    name: '功能锁',
                    url: '/systemcenter/sysconmod/featuresLock',
                    key: 1-5-5
                }
            ]
        },
        {
            name: '资金监控',
            className: 'icon-xitongguanli',
            parentId: 6,
            childMenu: [
                {
                    name: '用户资金监控',
                    url: '/monitorcenter/capitalmonitormod/userCapitalMonitor',
                    key: 1-5-1
                }
            ]
        },
        {
            name:'消息管理',
            className:'icon-xiaoxiguanli',
            parentId:2,
            childMenu:[
                {
                    name:'消息查询',
                    url:'/systemcenter/message/messageQuery',
                    key:1-5-1
                },
                {
                    name:'消息发送',
                    url:'/systemcenter/message/messageSend',
                    key:1-5-2
                },
                {
                    name:'消息规则模块',
                    url:'/systemcenter/message/messageRuleMould',
                    key:1-5-3
                },
                {
                    name:'模版发送',
                    url:'/systemcenter/message/mouldSend',
                    key:1-5-3
                }]
        }
    ]
};























