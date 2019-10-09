import React from 'react'
import axios from 'axios'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Modal,message,Tabs } from 'antd'
const { TabPane } = Tabs

export default class ModalSafeManage extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            diffAreaLoginNoCheck:false,//异地登陆
            diffAreaLoginNoCheckDec:'',//异地登陆状态描述
            lockSafePwd:false,//资金密码
            lockSafePwdDec:'',//解锁资金密描述
            lockGoogle:false,//谷歌验证锁定
            isLockGoogleDec:'',//谷歌验证锁定描述
            userId:'',
            loginGoogleAuthDec:'',//登录谷歌认证
            googleAuDec:'',//谷歌认证
            safePwdModifyTimesDec:'',
            featuresLockList:[]
        }

        this.areaLogCheck = this.areaLogCheck.bind(this)
        this.lockGoogle = this.lockGoogle.bind(this)
        this.safePwd = this.safePwd.bind(this)
        this.lockPhoneCode = this.lockPhoneCode.bind(this)
        this.loginGoogleInden = this.loginGoogleInden.bind(this)
        this.googleInden = this.googleInden.bind(this)
        this.safePwdtwo = this.safePwdtwo.bind(this)
        this.featuresLock= this.featuresLock.bind(this)
        this.featuresOnLock = this.featuresOnLock.bind(this)
    }

    componentDidMount(){  
        const {id,diffAreaLoginNoCheck,diffAreaLoginNoCheckDec,lockSafePwd,lockSafePwdDec,lockGoogle,isLockGoogleDec,googleAuDec,loginGoogleAuthDec,safePwdModifyTimesDec,safePwdModifyTimes,userContact,loginGoogleAuth} = this.props.item
        this.setState({
            diffAreaLoginNoCheck,
            diffAreaLoginNoCheckDec,
            lockSafePwd,
            lockSafePwdDec,
            lockGoogle,
            isLockGoogleDec,
            userId:id,
            googleAuDec,
            loginGoogleAuthDec,
            safePwdModifyTimesDec,
            safePwdModifyTimes,
            googleAu:userContact.googleAu,
            loginGoogleAuth
        },()=>{
            this.featuresLock()
        })
    }

    componentWillReceiveProps(nextProps){
        const {id,diffAreaLoginNoCheck,diffAreaLoginNoCheckDec,safePwdModifyTimesDec,lockSafePwd,lockSafePwdDec,lockGoogle,isLockGoogleDec,googleAuDec,loginGoogleAuthDec,safePwdModifyTimesDe,safePwdModifyTimes,userContact,loginGoogleAuth} = nextProps.item
        this.setState({
            diffAreaLoginNoCheck,
            diffAreaLoginNoCheckDec,
            lockSafePwd,
            lockSafePwdDec,
            lockGoogle,
            isLockGoogleDec,
            userId:id,
            googleAuDec,
            loginGoogleAuthDec,
            safePwdModifyTimesDec,
            safePwdModifyTimes,
            googleAu:userContact.googleAu,
            loginGoogleAuth
        },()=>{
            this.featuresLock()
        })
    }

    //异地登陆限制
    areaLogCheck(){
        let self = this;
        const { userId,diffAreaLoginNoCheck } = this.state
        Modal.confirm({
            title: diffAreaLoginNoCheck?'开启异地登陆限制':'关闭异地登陆限制',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/userInfo/loginCheck',qs.stringify({
                        userId
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.setState(prevState => ({
                                diffAreaLoginNoCheck: !prevState.diffAreaLoginNoCheck
                              }));
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //资金密码
    safePwd(){
        let self = this;
        const { userId,lockSafePwd } = this.state
        Modal.confirm({
            title:'解锁资金密码',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/userInfo/clearsafelock',qs.stringify({
                        userId
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.setState(prevState => ({
                                lockSafePwd: !prevState.lockSafePwd
                              }));
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //资金密码2
    safePwdtwo(){
        let self = this;
        const { userId,lockSafePwd } = this.state
        Modal.confirm({
            title:'解锁资金密码',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/userInfo/unlockSafePwd',qs.stringify({
                        userId
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.setState(prevState => ({
                                safePwdModifyTimes: 0
                              }));
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //解除当天短信锁定
    lockPhoneCode(){
        let self = this;
        const { userId } = this.state
        Modal.confirm({
            title: '解除当天短信锁定',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/userInfo/unlockPhoneCode',qs.stringify({
                        userId
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //谷歌验证锁定
    lockGoogle(){
        let self = this;
        const { userId,isLockGoogle } = this.state
        Modal.confirm({
            title: '解除谷歌验证锁定',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/userInfo/clearGoogle',qs.stringify({
                        userId
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.setState(prevState => ({
                                isLockGoogle: !prevState.isLockGoogle
                              }));
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //谷歌认证
    googleInden(){
        let self = this;
        const { userId,isLockGoogle } = this.state
        Modal.confirm({
            title: '解除谷歌验证锁定',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/userInfo/cancelGoogle',qs.stringify({
                        userId
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.setState(prevState => ({
                                googleAu: 0
                              }));
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //登录谷歌认证
    loginGoogleInden(){
        let self = this;
        const { userId } = this.state
        Modal.confirm({
            title: '取消登录谷歌认证',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/userInfo/releaseLoginGoogle',qs.stringify({
                        userId
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.setState(prevState => ({
                                loginGoogleAuth: !prevState.loginGoogleAuth
                              }))
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    //功能锁
    featuresLock(){
        let self = this;
        const { userId } = this.state
            return new Promise((resolve, reject) => {
                axios.post(DOMAIN_VIP+'/deblocking/query',qs.stringify({
                    userid:userId,
                })).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        self.setState(prevState => ({
                            featuresLockList:result.page
                          }))
                        resolve(result.msg)
                    }else{
                        message.warning(result.msg)
                    }
                }).then(error => {
                    reject(error)
                })
            }).catch(() => console.log('Oops errors!'));
    }
    featuresOnLock(item){
        let self = this;
        const { userId } = this.state
        Modal.confirm({
            title: '确定解锁本项吗?',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+'/deblocking/clearlock',qs.stringify({
                        userId,key:item.keyName
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            self.featuresLock()
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    render(){
        const {diffAreaLoginNoCheck,diffAreaLoginNoCheckDec,lockSafePwd,lockSafePwdDec,lockGoogle,isLockGoogleDec,googleAuDec,loginGoogleAuthDec,safePwdModifyTimesDec,safePwdModifyTimes,googleAu,loginGoogleAuth,featuresLockList} = this.state        
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <Tabs defaultActiveKey='1'>
                    <TabPane key='1' tab='安全验证'>
                        <div className="table-responsive">
                            <table className="table table-striped jambo_table bulk_action table-linehei table-border">
                                <thead>
                                    <tr className="headings">
                                        <th className="column-title">序号</th>
                                        <th className="column-title">安全事项</th>
                                        <th className="column-title">当前状态</th>
                                        <th className="column-title">操作</th>
                                        <th className="column-title">事项备注</th>                                              
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>1</td>
                                        <td>异地登陆限制</td>
                                        <td>{diffAreaLoginNoCheckDec}</td>
                                        <td><a href="javascript:void(0)" onClick={this.areaLogCheck}>{diffAreaLoginNoCheck?'开启':'关闭'}</a></td>
                                        <td>开启或关闭</td>
                                    </tr>
                                    <tr>
                                        <td>2</td>
                                        <td>资金密码</td>
                                        <td>{lockSafePwdDec}</td>
                                        <td>{lockSafePwd?<a href="javascript:void(0)" onClick={this.safePwd}>解锁</a>:''}</td>
                                        <td>
                                            <p>修改资金密码时，原始资金密码输错次数超过6次会锁定；</p>
                                            <p>修改资金密码时，谷歌验证码输错次数超过6次会锁定；</p>
                                            <p>修改手机时，资金密码输入次数超过6次会锁定；</p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>3</td>
                                        <td>资金密码</td>
                                        <td>{safePwdModifyTimesDec}</td>
                                        <td>{safePwdModifyTimes==2?<a href="javascript:void(0)" onClick={this.safePwdtwo}>解锁</a>:''}</td>
                                        <td>为了用户资金安全，修改资金密码后将锁定24小时</td>
                                    </tr>
                                    <tr>
                                        <td>4</td>
                                        <td>当天短信锁定</td>
                                        <td></td>
                                        <td><a href="javascript:void(0)" onClick={this.lockPhoneCode}>解除</a></td>
                                        <td>解除当天短信锁定</td>
                                    </tr>
                                    <tr>
                                        <td>5</td>
                                        <td>谷歌验证锁定</td>
                                        <td>{isLockGoogleDec}</td>
                                        <td>{lockGoogle?<a href="javascript:void(0)" onClick={this.lockGoogle}>解锁</a>:""}</td>
                                        <td>谷歌验证输错次数超过6次锁定</td>
                                    </tr>
                                    <tr>
                                        <td>6</td>
                                        <td>谷歌认证</td>
                                        <td>{googleAuDec}</td>
                                        <td>{googleAu==2?<a href="javascript:void(0)" onClick={this.googleInden}>关闭</a>:""}</td>
                                        <td>取消谷歌验证</td>
                                    </tr>
                                    <tr>
                                        <td>7</td>
                                        <td>登录谷歌认证</td>
                                        <td>{loginGoogleAuthDec}</td>
                                        <td><a href="javascript:void(0)" onClick={this.loginGoogleInden}>{loginGoogleAuth?"关闭":"开启"}</a></td>
                                        <td>登录时是否启用谷歌验证</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </TabPane>
                    <TabPane key='2' tab='功能锁'>
                        <div className="table-responsive">
                            <table  className="table table-striped jambo_table bulk_action table-linehei">
                                <thead>
                                    <tr className="headings">
                                        <th className="column-title">序号</th>
                                        <th className="column-title">key</th>
                                        <th className="column-title">功能</th>
                                        <th className="column-title">上限次数</th>
                                        <th className="column-title">状态</th>
                                        <th className="column-title">操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {    
                                        featuresLockList.length>0?featuresLockList.map((item,index) => {
                                            return (
                                                <tr key={index} >
                                                    {/* <td>{(pageIndex-1)*pageSize+index+1}</td> */}
                                                    <td>{index+1}</td>
                                                    <td>{item.keyName}</td>
                                                    <td>{item.remark}</td>
                                                    <td>{item.limitForbid}</td>
                                                    <td>{item.lock==='false'?'未锁定':'已锁定'}</td>
                                                    <td>
                                                        {item.lock==='false'?
                                                            '解锁'
                                                            :<a href="javascript:void(0)" onClick={()=>this.featuresOnLock(item,'unlock')}>解锁</a>
                                                        }                       
                                                    </td>                                           
                                                </tr>
                                            )
                                        }):<tr className="no-record"><td colSpan="10">暂无数据</td></tr>
                                    }
                                </tbody>
                            </table>
                        </div>
                    </TabPane>
                </Tabs>
                
            </div>
        )
    }

}