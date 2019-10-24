import React from 'react';
import { connect } from 'react-redux'
import { FormattedMessage,injectIntl } from 'react-intl';
import { userSendCode } from '../../../redux/modules/security';

class ModalCode extends React.Component{
    constructor(props){
        super(props)
        this.state={
            sendBtn:true,
            oneMinute:60,
        }

        this.getCode = this.getCode.bind(this)
    }

    // 获取验证码
    getCode(){
        console.log('getcode')
        this.props.userSendCode((res) => {
            console.log(res)
            let result = res.data;
            this.props.notifSend(result.des);
            if(result.isSuc){
                if(this.state.sendBtn){
                    this.setState({sendBtn:false})
                    let timer = setInterval(() => {
                        this.setState((prevState) => ({
                            oneMinute: prevState.oneMinute - 1
                          }))
                        if(this.state.oneMinute == 0){
                            clearInterval(timer);
                            this.setState({sendBtn : true,oneMinute : 60 });
                        }
                    },1000)
                }
            } 
        });
    }

    render(){
        const { sendBtn,oneMinute } = this.state
        const {intl} = this.props
        return(
            <div className="top">
                <input placeholder={intl.formatMessage({id:'security.text31'})} maxLength="6" name="input1" id="input1" />
                <button className="code" disabled={sendBtn?"":"disabled"} onClick={this.getCode}>{sendBtn ? <FormattedMessage id='withdraw.text8' /> : <FormattedMessage id='withdraw.text42' values={{time:oneMinute}} /> }</button>
            </div>
        )
    }

}

export default injectIntl(ModalCode)
















