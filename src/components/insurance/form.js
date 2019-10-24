/**
 * 保险的表单主体
 */
import React from 'react';
import { injectIntl,FormattedMessage } from 'react-intl';
import Form from '../../decorator/form';
import SelectList from '../../components/selectList'
import {DOMAIN_VIP} from '../../conf'
import axios from 'axios';
import qs from 'qs';
import cookie from 'js-cookie';

const BigNumber = require('big.js')

@Form
class Fm extends React.Component{
    constructor(props){
        super(props);

        this.getCode = this.getCode.bind(this);

        this.tb = this.tb.bind(this);

        this.dic = {
            fMoney: '',
            fPic: '',
            // pInvitationCode: '',
            // userVID: '',
        }

        this.state = {
            ...this.dic,
            num: 0,
            fs: 0,
            amountFromSys: 0,
        }

        this.dictionaries = Object.keys(this.dic);

        this.doData = this.doData.bind(this);

        this.errorDic = this.errorDic.bind(this);

        this.doRsMsg = this.doRsMsg.bind(this);
    }
    doRsMsg(msgObj = {}){
         // 通知外面
         try{
            this.props.cb(msgObj);
        }catch(e){

        }
    }
    doData(){
       // 中间层处理发送数据

       // 表单数据体
       const { num, amountFromSys, fPic, fMoney, pInvitationCode, userVID, } = this.state;

       // 字典映射
       const rs = {
           insureInvestAmount: 0, // 数量
           amountFromSys, // 账户类型(钱包 1，币币 2，法币 3，理财5)
           triggerPrice: 0, // 触发投保价格
           insureInvestNum: 0, // 投保份额
           pInvitationCode, // 邀请码
           userVID, // VID
       }

       // 类型字段映射
       const types = {
           '0': 1,
           '1': 2,
           '2': 3,
           '3': 5,
       }

       // 处理发送数据
       try{
        rs.insureInvestAmount = new BigNumber(num).div(188).toFixed(0);
       } catch(e){

       }
       
       rs.amountFromSys = types[amountFromSys];
       rs.triggerPrice = fPic;
       rs.insureInvestNum = fMoney;
       // 改版保单必须放这个。
       rs.insureInvestAmount = fMoney;

       return rs;
    }
    tb(){
        // 走验证
        if(!this.hasError(this.dictionaries)){
            // 提交获取格式化数据
            const sendData = this.doData();
            // 阿波罗计划没有购买提上来，下面那个暂时作废
            if(!+localStorage.getItem(cookie.get('zuid')+'ispay')){
                this.doRsMsg({
                    isSuc: false,
                    dealFlg: true,
                })
                return;
            }

            // console.log(sendData, '@@@@@@');
            axios.post(DOMAIN_VIP + '/manage/financial/insurance/userSaveInsure', qs.stringify(sendData)).then((res)=>{
                res = res.data;
                
                if(res.isSuc){
                   // 成功消费前端数据
                   const { fMoney, fPic, } = this.state;
                   
                   this.doRsMsg({
                        v:  fMoney,
                        f: +(new BigNumber(fMoney).div(2).toFixed(2)),
                        p: fPic,
                        isSuc: true,
                        msg: '',
                    })

                } else {
                    // res.datas.pInvitationCodeCR = '2233';
                    // isSuc false 错误消息分两类，外部消费，本部消费

                    // 如果是没参与阿波罗直接弹出去参与
                    if(false){
                        this.doRsMsg({
                            isSuc: false,
                            dealFlg: true,
                        })
                    }
                    else if(2 === JSON.stringify(res.datas).length){
                        // 非表单及错误
                        this.doRsMsg({
                            isSuc: false,
                            msg: res.des,
                        })
                    } else {
                        // 表单及错误
                        // console.log(res.datas);
                        // res.datas.insureInvestNumCR = '2233';
                        if(res.datas.insureInvestAmountCR){
                            this.doRsMsg({
                                isSuc: false,
                                msg: res.datas.insureInvestAmountCR,
                            })
                        } else {
                            this.errorDic(res.datas);
                        }
                    }
                }
            },()=>{
                this.doRsMsg({
                    isSuc: false,
                    msg: 'error',
                })
            });
        }
    }
    errorDic(transfer = {
        insureInvestNumCR: '',
        triggerPriceCR: '',
        pInvitationCodeCR: '',
        userVIDCR: '',
    }){
        // console.log(this.cbShowAllErrors)
        // 根据返回错误，转换为视图错误。
        let rs = {};
        
        ({insureInvestNumCR:rs.fMoney, triggerPriceCR:rs.fPic, pInvitationCodeCR:rs.pInvitationCode, userVIDCR:rs.userVID}=transfer);

        for(let i in rs){
            !rs[i] && delete rs[i];
        }
        
        this.cbShowAllErrors(rs);
    }
    getCode(e){
        // 策略模式处理vds余额
        this.getPms(e).then(res=>{
            let vds = 0;
            try{
                res = eval('('+res.data+')');
                vds = +res['VDS'].balance === 0 ? 0 : res['VDS'].balance;
            }catch(e){

            } finally{
                this.setState({
                    num: vds,
                    amountFromSys: e,
                })
            }
        })
    }
    getPms(e){
        const ay = [
            '/manage/getWalletDetail',
            '/manage/getAssetsDetail',
            '/manage/getAssetsOtcDetail',
            '/manage/getFinancialDetail',
        ];
        return axios.get(DOMAIN_VIP + ay[e]);
    }
    componentDidMount(){
        // 初始化余额默认我的钱包。
        this.getCode(0);

        // this.errorDic();
    }
    render(){
        const { formatMessage } = this.props.intl;
        const { tb, setFMoney, fIn, bOut, setFPic, setPInvitationCode, setUserVID, } = this;
        const { fs, num, fMoney, errors, fPic, pInvitationCode, userVID, } = this.state;
        const { fMoney:efMoney = [], fPic:efPic = [], pInvitationCode:epInvitationCode = [], userVID:euserVID = [], } = errors;
        let maxs = 0;
        try{
          // maxs = new BigNumber(num).div(188).toFixed(0);
          maxs = num;
        }catch(e){
            
        }
        return (
            <div className="my_tip_wp bx">
                <h2><i onClick={this.props.close.closeModal} className="right iconfont icon-guanbi-moren"></i><FormattedMessage id="投保" /></h2>
                <ul className="wlist">
                    <li>
                        <h3><FormattedMessage id="账户类型" /></h3>
                        <SelectList 
                            options={[{key:<FormattedMessage id="t-我的钱包"/>,val:0},{key:<FormattedMessage id="t-币币账号"/>,val:1},{key:<FormattedMessage id="法币账户"/>,val:2},{key:<FormattedMessage id="t-理财账号"/>,val:3}]}
                            Cb={this.getCode}
                        />
                    </li>
                    <li>
                        <h3><FormattedMessage id="数量" /></h3>
                        {
                        false
                        &&
                        <input readOnly={true} type="text" className="itxt" value={`${num} Vollar ≈ ${maxs}${formatMessage({id:"份"})}`} />
                        }
                        <input readOnly={true} type="text" className="itxt" value={`${num} Vollar`} />
                    </li>
                    <li>
                        <h3><FormattedMessage id="投保数量" /></h3>
                        <div className="pubPops">
                            {efMoney[0] ? <span class="ew">{efMoney[0]}</span> : null}
                            <input type="text" maxs={maxs} onChange={setFMoney} value={fMoney} name="fMoney" className="itxt" placeholder={formatMessage({id:"tvl"})} onFocus={fIn} onBlur={bOut} />
                        </div>
                    </li>
                    <li className="plv">
                        <h3><FormattedMessage id="触发投保价格" /></h3>
                        <div className="pubPops">
                            {efPic[0] ? <span class="ew">{efPic[0]}</span> : null}
                            <input type="text" className="itxt" onChange={setFPic} value={fPic} onFocus={fIn} onBlur={bOut} name="fPic" placeholder={formatMessage({id:"请输入触发投保价格"})} />
                        </div>
                        <span className="unit">
                        USDT
                        </span>
                    </li>
                    {
                    false
                    &&
                    <li>
                        <h3><FormattedMessage id="t-邀请码" /></h3>
                        <div className="pubPops">
                            {epInvitationCode[0] ? <span class="ew">{epInvitationCode[0]}</span> : null}
                            <input type="text" className="itxt" onChange={setPInvitationCode} value={pInvitationCode} name="pInvitationCode" onFocus={fIn} onBlur={bOut} placeholder={formatMessage({id:"t-请输入邀请码"})} />
                        </div>
                    </li>
                    }
                    {
                    false
                    &&
                    <li>
                        <h3><FormattedMessage id="t-VID地址" /></h3>
                        <div className="pubPops">
                            {euserVID[0] ? <span class="ew">{euserVID[0]}</span> : null}
                            <input type="text" className="itxt" onChange={setUserVID} value={userVID} name="userVID" onFocus={fIn} onBlur={bOut} placeholder={formatMessage({id:"t-请输入VID地址"})} />
                        </div>
                    </li>
                    }
                    <li className="lst">
                        <span onClick={this.props.close.closeModal} className="cel">{formatMessage({id:"cancel"})}</span>
                        <span onClick={tb} className="sv">{formatMessage({id:"投保"})}</span>
                    </li>
                </ul>
            </div>
        )
    }
}

export default injectIntl(Fm);
