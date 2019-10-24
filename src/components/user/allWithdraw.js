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

class AllWithdraw extends React.Component{
    static propTypes = {
        mCode: PropTypes.string,
        mobile: PropTypes.string,
        codeType: PropTypes.number.isRequired,
        fn: PropTypes.func,
        sendUrl: PropTypes.string,
        errorKey: PropTypes.string,
        otherData: PropTypes.object,
        isCk: PropTypes.number,
    }
    static defaultProps = {
        fn: () => {

        },
        otherData: {
            
        },
        isCk: 1,
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
        const { mCode, mobile, codeType, fn, errorKey, otherData, isCk } = this.props;
        let send = {};
        mobile ? send = { mCode, mobile, codeType } : send = { codeType };

        send = {...otherData, ...send};

        if(isCk){
            if(mCode && mobile || !mCode && !mobile){

                mobile && !isMobiles(mobile) ? fn('mobile', this.props.intl.formatMessage({id:'nuser95'}))
                :
                axios.post(this.SmsUrl, qs.stringify(send)).then(res => {
                    res = res.data;
                    const KEY = errorKey ? errorKey : 'mobile';
                    if(!res.isSuc){
                        const num = res.des.match(/\d*/)[0];
                        
                        if(num){
                            this.setState({
                                oneMinute: num
                            });
                            this.startSetTime();
                            fn(KEY, '');
                        } else {
                            fn(KEY, res.des);
                        }
                    } else {
                        this.startSetTime();

                        fn(KEY, '');
                    }
                });
            } else {
                fn('mobile', this.props.intl.formatMessage({id:'nuser92'}));
            }
        }
    }
    render(){
        const { sendBtn, oneMinute } = this.state;
        const { send } = this;

        return (
        <div className="ptith">
            {
            !sendBtn ?
            <span onClick={send}>
                {/* <FormattedMessage id="nuser89"  /> */}
                {this.props.textName}
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

export default injectIntl(AllWithdraw);
