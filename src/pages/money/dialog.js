import React from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import {doSave, doPay} from './index.model';
import {optPop} from '../../utils';
import From from '../../decorator/form';
import { browserHistory, Link } from 'react-router';
import cookie from 'js-cookie';
import CheckBox from '../../components/form/checkbox';

@From
class Dialog extends React.Component{
    constructor(props){
        super(props)
        const State = props.states;
        const {userVID, pInvitationCode, userFinVdsAmount} = props.transferInfo;

        this.base = {
            userVID: userVID,
            pInvitationCode: pInvitationCode,
        };

        this.state ={
            ...this.base,
            isPay: (1 == State || (this.props.step && State > 0)),
            isSave: 0 == State,
            isTransfer: false,
            isOpen: false,
            regAgreement: false,
        }

        this.save = this.save.bind(this);

        this.dictionaries = Object.keys(this.base);

        this.pay = this.pay.bind(this);

        this.setCk = this.setCk.bind(this);
        
        this.mz = this.mz.bind(this);

        this.step = this.props.step;

        // 当支付的时候加支付锁防止重复点击。
        this.isPayLock = false;

        this.money = [
            0,
            2,
            8,
            18,
            38,
            88,
            188,
        ];
        // this.money = [
        //     0,
        //     2,
        //     6,
        //     10,
        //     20,
        //     50,
        //     100,
        // ];
    }

    mz(){
        this.props.mz();
    }

    setCk(state){
        this.setState({
            regAgreement: state,
        });
    }

    pay(){
        if(!this.isPayLock && this.state.isPay && this.state.regAgreement){
            // 加锁
            this.isPayLock = true;

            doPay(this.step).then((res)=>{
                if(res.suc){
                    localStorage.setItem(cookie.get('zuid')+'ispay', 1);

                    //optPop(()=>{browserHistory.push('/bw/manage/account/cmoney');}, res.des, {timer: 5000}, !res.suc);

                    optPop(()=>{browserHistory.push('/bw/manage/account/cmoney');}, `
                    <h2 style="margin-bottom: 10px;" class="bok"><span>${this.props.intl.formatMessage({id: "购买成功！"})}</span></h2>
                        <p>${res.des}</p>
                        <div class="boklst"><pre style="margin-bottom:0"><span style="vertical-align:middle">${this.props.intl.formatMessage({id: "5秒后，自动跳转"})}</span> <a style="vertical-align:middle" href="/bw/manage/account/cmoney">&gt;</a></pre></div>
                    
                    `, {timer: 5000, innerHTML:1}, true);

                } else {
                    optPop(()=>{}, res.des, undefined, !res.suc);
                }

                this.isPayLock = false;
            })
        }
    }

    save(){
        if(!this.hasError(this.dictionaries)){
            // saveObj
            const saveObj = this.dictionaries.reduce((saveObj, key)=>{
                saveObj[key] = this.state[key];
                return saveObj;
            }, {});

            doSave(saveObj).then((res)=>{
                let showSuccess = 1;
                if(res.suc){
                    this.setState({
                        isPay: true,
                        isSave: false,
                    })
                    showSuccess = 0;
                }
                // 消息处理
                if(res.ekeys.length){
                    const errorRS = {};
                    res.errors.uvidCR && (errorRS['userVID'] = res.errors.uvidCR);
                    res.errors.picCR && (errorRS['pInvitationCode'] = res.errors.picCR);
                    this.cbShowAllErrors(errorRS);
                } else{
                    optPop(()=>{}, res.des, undefined, showSuccess);
                }
            })
        }
        // this.props.closeModal();
    }

    render(){
        
        // Form的元素
        const {setUserVID,setPInvitationCode,fIn,bOut,save,pay, setCk, money, step, } = this;

        let { formatMessage } = this.props.intl
        const {isPay, isSave, userVID, pInvitationCode, errors, regAgreement, } = this.state;
        const {userVID:euserVID = [], pInvitationCode:epInvitationCode = []} = errors;

        return (
            <div className="money-dialog">

                <div className="head "><h3></h3><a className="right iconfont icon-guanbi-moren" onClick={() => this.props.closeModal()}></a></div>

                <div className="body">
                    <div className="title">
        <p className="fl-lt"><span>{formatMessage({id: "投资数量"})}</span><span>{money[step]}Vollar</span></p> <p className="fl-rt"><span>{isSave && <a className="btn btn-border" onClick={save}>{formatMessage({id: "保存"})}</a>}</span></p>
                    </div>
                    <div className="bk-balances pubPop" >
                        <div className="bk-bottom">
                            <div className="trans-number">{formatMessage({id: "我的VID地址:"})}</div>
                            <div className={euserVID[0] && 'errx'}>
                                <input disabled={isSave?false:true} className="trans-input" onFocus={fIn} onBlur={bOut} placeholder={formatMessage({id: "请输入VID地址"})}  autoComplete="off"  name="userVID" value={userVID} onChange={setUserVID} />
                                <span className="ew">{euserVID[0]}</span>
                            </div>

                        </div>

                        <div className="bk-bottom">
                            <div className="trans-number">{formatMessage({id: "推荐人邀请码:"})}</div>
                            <div className={epInvitationCode[0] && 'errx'}>
                                <input disabled={isSave?false:true} className="trans-input" onFocus={fIn} onBlur={bOut} placeholder={formatMessage({id: "请输入邀请码"})}  autoComplete="off"  name="pInvitationCode" value={pInvitationCode} onChange={setPInvitationCode} />
                                <span className="ew">{epInvitationCode[0]}</span>
                            </div>

                        </div>
                        <div className="bk-prompt clearfix"><p className="fl-lt">{formatMessage({id: "理财账户可用数量:"})}<span>{this.props.transferInfo.userFinVdsAmount} Vollar</span></p><p className="fl-rt"><a target="_blank" href="/bw/manage/account/charge?coint=VDS">{formatMessage({id: "充值"})}</a><a onClick={this.props.transfer}>{formatMessage({id: "划转"})}</a></p></div>
                        {
                        !isSave
                        ?
                        <div className="bk-bottom">
                            <CheckBox setCk={setCk} isCk={regAgreement} key={regAgreement+""} />
                            <span>
                                <span style={{paddingLeft:'25px',color:"#fff"}}>
                                    <FormattedMessage id="我已阅读并同意-1" values={{p:(<a target="_blank" onClick={this.mz}>
                                    {formatMessage({id: "《免责声明》"})}
                                </a>)}} />
                                </span>
                            </span>
                        </div>
                        :null
                        }
                    </div>
                </div>
                <div className="modal-btn">
                    <div className="modal-foot">
                        <a className={`btn ml10 ${!(isPay && regAgreement) && 'dis'}`} onClick={pay}>{formatMessage({id: "支付"})}</a>
                    </div>
                </div>
            </div>
        )
    }
}

export default injectIntl(Dialog)