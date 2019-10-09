import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../../conf'
import ModalLogin from './moadl/modalLogin'
import { Button } from 'antd';


export default class LoginInfo extends React.Component{

    constructor(props) {
        super(props)
        this.state = {
            showHide:true,
            userId:'',
            userName:'',
            loginIp:'',
            city:'',
            loginMode:'',
            isInquire:false,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            height:0,
            tableScroll:{
                tableId:'LONIO',
                x_panelId:'LONIOX',
                defaultHeight:500,
            }
        }

        this.handleInputChange = this.handleInputChange.bind(this);
        this.queryBtnclick = this.queryBtnclick.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.clickHide = this.clickHide.bind(this)
    }
    componentDidMount(){
        var height  =document.querySelector(`#${this.state.tableScroll.x_panelId}`).offsetHeight
        this.setState({
            xheight:height
        })
    }

    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //查询点击
    queryBtnclick(val){
        this.setState({
            isInquire:val
        })
    }

    //重置
    onResetState(){
        this.setState({
            userId:'',
            userName:'',
            loginIp:'',
            city:'',
            loginMode:''
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    
    render(){
        const { showHide,userId,userName,loginIp,city,loginMode } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心>用户管理>登录信息
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="userId" value={userId}  onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="userName" value={userName}  onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">登录IP：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="loginIp" value={loginIp}  onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">城市：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="city" value={city}  onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-4 control-label">登录方式：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control"  name="loginMode" value={loginMode} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.queryBtnclick(true)}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                    </div>
                                </div>
                            </div>
                        </div>}
                        <ModalLogin { ...this.state } queryBtnclick={this.queryBtnclick}  />
                    </div>
                </div>
            </div>
        )
    }

}






























































