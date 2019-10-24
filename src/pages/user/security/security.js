import React from 'react';
import Modal from 'react-modal';
import ReactDOM from 'react-dom';
import axios from 'axios'
import qs from 'qs'
import { FormattedMessage,injectIntl } from 'react-intl';
import { connect } from 'react-redux';
import { fetchSecurityInfo, saveChange, userSendCode } from '../../../redux/modules/security';
import SafeSelect from './safeSelect.js';
import ReactModal from '../../../components/popBox'
import ModalCode from './modalCode'
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend } = notifActions;
import { DISMISS_TIME,DOMAIN_VIP } from '../../../conf';


class Security extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            modalId:1,
            modalHTML:'',
            spandArr:[],
            sendBtn:true,
            oneMinute:60,
        }
        this.data  = {
            list:[
                {
                    Title:<FormattedMessage id='security.text1' />,
                    Info:<FormattedMessage id='security.text2' />,
                    url:"",
                    choose:this.chooseLogin,
                    Ways:[
                        {name:<FormattedMessage id='登录密码' />,needGoogle:false,type:1,id:1},
                        {name:<FormattedMessage id='登录密码+异地登录验证（短信/邮件）' />,needGoogle:false,type:3,id:2},
                        {name:<FormattedMessage id='登录密码+Google验证码' />,needGoogle:true,type:2,id:3},
                        {name:<FormattedMessage id='登录密码+Google验证码+异地登录验证（短信/邮件）' />,needGoogle:true,type:4,id:4},
                    ]
                },
                {
                    Title:<FormattedMessage id='security.text7' />,
                    Info:<FormattedMessage id='security.text8' />,
                    url:"",
                    choose:this.chooseTrade,
                    Ways:[
                        {name:<FormattedMessage id='永不输入资金密码' />,needGoogle:false,type:1,id:5},
                        {name:<FormattedMessage id='6小时内免输资金密码' />,needGoogle:false,type:2,id:6},
                        {name:<FormattedMessage id='每次交易均验证资金密码' />,needGoogle:false,type:3,id:7},
                    ]
                },
                {
                    Title:<FormattedMessage id='security.text12' />,
                    Info:<FormattedMessage id='security.text13' />,
                    url:"",
                    choose:this.chooseWithdraw,
                    Ways:[
                        {name:<FormattedMessage id='资金密码+短信/邮件验证码' />,needGoogle:false,type:1,id:8},
                        {name:<FormattedMessage id='资金密码+Google验证码' />,needGoogle:true,type:2,id:9},
                        {name:<FormattedMessage id='资金密码+短信/邮件验证码+Google验证码' />,needGoogle:true,type:3,id:10},
                    ]
                },
                {
                    Title:<FormattedMessage id='security.text17' />,
                    Info:<FormattedMessage id='security.text18' />,
                    url:"",
                    choose:this.chooseWithdrawAddress,
                    Ways:[
                        {name:<FormattedMessage id='security.text19' />,needGoogle:false,type:1,id:11},
                        {name:<FormattedMessage id='security.text20' />,needGoogle:false,type:2,id:12},
                    ]
                }
            ],
            isGoogleAuth:false
        }

        this.processing = this.processing.bind(this);
        this.chooseLogin = this.chooseLogin.bind(this);
        this.chooseTrade = this.chooseTrade.bind(this);
        this.chooseWithdraw = this.chooseWithdraw.bind(this);
        this.chooseOne = this.chooseOne.bind(this);
        this.submit = this.submit.bind(this)
        this.addSpandList = this.addSpandList.bind(this)
        this.openAddressAuthen = this.openAddressAuthen.bind(this)
        this.addressAuthenBtn = this.addressAuthenBtn.bind(this)
        this.getModalHTML = this.getModalHTML.bind(this)
    }

    componentDidMount(){
        this.props.fetchSecurityInfo();
        if(this.props.location.query.address == "address_withdraw"){
            this.setState(preState => {
                let newArr = preState.spandArr.concat([3])
                return {spandArr:newArr}
            })
        }    
    }

    processing(data){
        if(data){
            this.data.list.map(
                (item,index)=>{
                    item.Ways = data.list[index];
                }
            )
            this.data.isGoogleAuth = data.isGoogleAuth

        }
    }
    chooseLogin(ide){
        console.log("chooseLogin:"+ide);
    }
    chooseTrade(ide){
        console.log("chooseTrade:"+ide);
    }
    chooseWithdraw(ide){
        console.log("chooseWithdraw:"+ide);
    }
    // 选择了某一项的回调
    chooseOne(id){
        let modalHTML = this.getModalHTML(id)
        this.setState({
            modalId:id,
            modalHTML
        })
        
        this.modal.openModal()
    }
    
    // 提交修改
    submit(){
        let input1 = document.querySelector('#input1')
        let input2 = document.querySelector('#input2')
        let input3 = document.querySelector('#input3')
       
        if(!this.verify(input1,input3)) return

        let obj = {}
        let {modal,category,type} = this.getValueById(this.state.modalId)
        obj.category = category
        obj.type = type
        if(input1 && input1.value != ''){
            obj.mobileCode = input1.value
        }
        if(input2 && input2.value != ''){
            obj.googleCode = input2.value
        }
        if(input3 && input3.value != ''){
            obj.safePwd = input3.value
        }
       
        this.props.saveChange(obj)
    }

    // 提交验证
    verify(input1,input3){
    
        if(input1){
            let v = /^[0-9A-Za-z]{6}$/
            let s = v.test(input1.value)
            if(!s){
                this.props.notifSend(<FormattedMessage id='security.text21' />)
                return false
            }
            
        }
        if(input3 && input3.value == ''){
            this.props.notifSend(<FormattedMessage id='security.text22' />)
            return false
        }
        return true
    }


    /**
     * 获取显示的弹出层样式
     */ 
    getModalHTML(id){
        let {modal} = this.getValueById(id);
        let dis = this.props.listState.data.hasGoogleAtuh?'block':'none'
        const { intl } = this.props
        let modalList = [
            <div className="modal-form">
                <div className="top">
                    <ModalCode userSendCode={this.props.userSendCode} notifSend={this.props.notifSend} />
                    <input className="modal-googleCode" id="input2" style={{'display':dis}} placeholder={intl.formatMessage({id:'security.text24'})}/>
                </div>
                <button className="subs" onClick={this.submit}><FormattedMessage id='security.text23' /></button>
            </div>,
            <div className="modal-form">
                <div className="top">
                    <input type="password" className="modal-googleCode2" id="input3" placeholder = {intl.formatMessage({id:'security.text28'})}/>
                </div>
                <button className="subs" onClick={this.submit}><FormattedMessage id='security.text23' /></button>
            </div>
        ]
        return modalList[modal]
    }

    // 根据id返回对应的提交参数category type modal:弹窗的类型
    getValueById(id){
        let obj;
        switch (id) {
            case 1:
                obj = {modal:0,category:1,type:1}
                break;
            case 2:
                obj = {modal:0,category:1,type:3}
                break;

            case 3:
                obj = {modal:0,category:1,type:2}
                break;
            case 4:
                obj = {modal:0,category:1,type:4}
                break;
            case 5:
                obj = {modal:1,category:2,type:1}
                break;
            case 6:
                obj = {modal:1,category:2,type:2}
                break;
            case 7:
                obj = {modal:1,category:2,type:3}
                break;
            case 8:
                obj = {modal:0,category:3,type:1}
                break;
            case 9:
                obj = {modal:0,category:3,type:2}
                break;
            case 10:
                obj = {modal:0,category:3,type:3}
                break;
            case 11:
                obj = {modal:3,category:4,type:1}
                break;
            case 12:
                obj = {modal:3,category:4,type:2}
                break;
            default:
                obj = null
                break;
        }

        return obj;
    }

    //address authen list
    openAddressAuthen(type){
        let nums = this.props.listState.data.withdrawAddressAuthenType,title='';
        if(type==2&&nums==0){
            title = <FormattedMessage id='security.text25' />
        }else if(type==2&&nums==1){
            title = <FormattedMessage id='security.text26' />
        }else{
            title = <FormattedMessage id='security.text27' />
        }
        let modalHTML = <div className="modal-body-sm">
                            <div className="bk-page-tableCell">{title}</div>
                            <div className="foot">
                                <a className="btn btn-outgray btn-sm" onClick={this.modal.closeModal}><FormattedMessage id='security.text29' /></a>
                                <a className="btn btn-primary btn-sm" onClick={()=>this.addressAuthenBtn(type)}><FormattedMessage id='security.text30' /></a>
                            </div>
                        </div> 

        this.setState({
            modalHTML
        })
        this.modal.openModal()
    }
    //
    addressAuthenBtn(type){
        axios.post(DOMAIN_VIP+"/manage/auth/changeAuth",qs.stringify({
            category: 4,
            type
        })).then(res => {
            const result = res.data
            if(result.isSuc){
                this.props.fetchSecurityInfo()
            }else{
                this.props.notifSend(result.des)
            }
        })
    }

    //add spand item
    addSpandList(id,showStatus){
        this.setState(preState => {
            let newSpand
            if(!preState.spandArr.includes(id)){
                newSpand = preState.spandArr.concat([id])
            }else{
                newSpand = preState.spandArr.filter((currentValue,index) =>{ return currentValue!=id})
            }
            return {spandArr:newSpand}    
        })
    }

    render(){
        const { data,isFetching }  = this.props.listState;
        const { spandArr,oneMinute } = this.state
        if(isFetching){
            return (
                <div>...LOADING</div>
            )
        }
        return (
            <div className="cont-row">
                <ReactModal ref={modal => this.modal = modal}>
                    {this.state.modalHTML}
                </ReactModal>

                <div className="bk-top">
                    <h2><span><FormattedMessage id="manage.text2" /></span></h2>
                </div>
                <ul className = "security_list">
                {this.data.list.map(
                    (item,index)=>{
                        return (
                            <SafeSelect 
                                item={item} 
                                selectId={index} 
                                selectInfo={data} 
                                key={index} 
                                chooseOne={this.chooseOne}
                                fetchSecurityInfo={this.props.fetchSecurityInfo}
                                notifSend={this.props.notifSend}
                                spandArr={spandArr}
                                addSpandList={this.addSpandList}
                                openAddressAuthen={this.openAddressAuthen}
                                lang={this.props.lang}
                                />
                        )
                    }
                )}
                </ul>
            </div>
        )
    }
}

const mapStateToProps = (state, ownProps) => {
    return {
        listState:state.security.security,
        lang:state.language.locale
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        fetchSecurityInfo:()=>{
            dispatch(fetchSecurityInfo())
        },
        saveChange: (data)=>{
            dispatch(saveChange(data)).then(res => {
                console.log(res)
                let o = {
                    message: res.data.des,
                    kind: 'info',
                    dismissAfter: DISMISS_TIME
                };
                dispatch(notifSend(o))

                if(res.data.isSuc){
                    dispatch(fetchSecurityInfo())
                }
            })
        },
        userSendCode: (cb)=>{
            dispatch(userSendCode()).then(cb)
        },
        notifSend:(msg)=>{
            dispatch(notifSend({
                message: msg,
                kind: 'info',
                dismissAfter: DISMISS_TIME
            }));
        }
    };
};
export default connect(mapStateToProps,mapDispatchToProps)(injectIntl(Security));