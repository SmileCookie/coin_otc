/**
 * Sms
 * @author luchao.ding
 */
import React from 'react';
import PropTypes from 'prop-types';
import { FormattedMessage, injectIntl } from 'react-intl';
import axios from 'axios';
import qs from 'qs';

import { DOMAIN_VIP } from '../../conf';
import { isMobiles } from '../../utils';

class Sms extends React.Component{
    static propTypes = {
        mCode: PropTypes.string,
        mobile: PropTypes.string,
        codeType: PropTypes.number.isRequired,
        fn: PropTypes.func,
        sendUrl: PropTypes.string,
        errorKey: PropTypes.string,
        otherData: PropTypes.object,
        isCk: PropTypes.number,
        getCkFn: PropTypes.func,
        clearFn: PropTypes.func,
    }
    static defaultProps = {
        clearFn: () => {

        },
        fn: () => {

        },
        otherData: {

        },
        isCk: 1,
        getCkFn: () => {
            return true;
        },
    }
    constructor(props){
        super(props);
        this.SmsUrl = DOMAIN_VIP + (props.mobile ? '/manage/auth/authMobileSendCode' : '/userSendCode');
        props.sendUrl && (this.SmsUrl = DOMAIN_VIP + props.sendUrl);

        this.state = {
            sendBtn: false,
            oneMinute: 60,
        }

        this.send = this.send.bind(this);
    }
    componentWillUnmount(){
        this.setState = (state,callback)=>{
            return;
        };
    }
    startSetTime(){

        this.setState({
            sendBtn: true,
        });
        this.c = setInterval(() => {
            this.setState((prev) => {
                const num = prev.oneMinute - 1;
                let rt = {
                    oneMinute: num
                };

                if(num === 0){
                    clearInterval(this.c);
                    this.props.clearFn();
                    rt  = {
                        sendBtn: false,
                        oneMinute: 60
                    };
                }
                return {
                    ...rt
                }
            })
        }, 1000);
    }
    send(){
        if(this.props.getCkFn()){
            const { mCode, mobile, codeType, fn, errorKey, otherData, isCk } = this.props;

            let that = this;
            let send = {};
            const openshow = this.props.openshow || null
            mobile ? send = { mCode, mobile, codeType } : send = { codeType };

            send = {...otherData, ...send};

            if(isCk){
                if(mCode && mobile || !mCode && !mobile){

                    mobile && !isMobiles(mobile) ? fn('mobile', this.props.intl.formatMessage({id:'nuser95'}))
                        :
                        axios.post(this.SmsUrl, qs.stringify(send)).then(res => {
                            res = res.data;

                            console.log(res)
                            //判断邮箱是否激活
                            if(codeType == 16){  //限制在忘记密码的那一块
                                if(res.datas.hasOwnProperty('isConfirm')){ //没有激活的返回的数据里面包含这个字段
                                    //未激活 显示未激活弹窗
                                    that.props.openshow()

                                }
                            }
                            //************************************ */


                            const KEY = errorKey ? errorKey : 'mobile';
                            if(!res.isSuc){
                                let num = '';

                                try{
                                    num = res.des.match(/\d+\s*(秒|seconds|초)/)[0];
                                } catch(e){
                                    num = '';
                                }


                                if(num){
                                    this.setState({
                                        oneMinute: num.match(/\d+/)[0],
                                    });
                                    this.startSetTime();

                                }
                                fn(KEY, res.des, res);
                                if(res.datas){
                                    try{
                                        for(let i in res.datas){
                                            fn(i, res.datas[i], res);
                                        }
                                    }catch(e){

                                    }
                                }
                            } else {
                                this.startSetTime();
                                fn(KEY, '', res);
                            }
                        });
                } else {
                    fn('mobile', this.props.intl.formatMessage({id:'nuser92'}));
                }
            }
        }
    }
    render(){
        const { sendBtn, oneMinute, } = this.state;
        console.log('======>>' + sendBtn)
        const { send } = this;
        let {cln} = this.props;
        return (
            <div className={`ptith siq ${cln} ${sendBtn && 'code-loading'}`}>
                {
                    !sendBtn ?
                        <span onClick={send}>
                <FormattedMessage id="nuser89"  />
            </span>
                        :
                        <div>
                            {this.props.intl.formatMessage({id:'user.text129'}).replace('[$1]', oneMinute)}
                        </div>
                }
            </div>
        );
    }
}

export default injectIntl(Sms);
