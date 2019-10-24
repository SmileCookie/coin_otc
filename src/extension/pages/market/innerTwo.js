import React from 'react';
import { FormattedMessage,injectIntl} from 'react-intl'
import successImg from './images/success.png'
import cookie from 'js-cookie';
import { DOMAIN_VIP,URL_IMG_CODE ,COOKIE_LAN} from '../../conf'



class InnerTwo extends React.Component{
    constructor(props){
        super(props);
        this.state={
            userEmail:''
        }
        this.ConfrimText = this.ConfrimText.bind(this);
        this.getScrollTop = this.getScrollTop.bind(this);
    }

    //判断类型
    ConfrimText(props){
        let id = props.id;
        let {userEmail} = this.state;
        console.log(id)
        if(id == 1){//注册成功
            return (
                <div className="bottom-adver">
                        <p><FormattedMessage id= "恭喜您获得创世用户大礼包！"/></p>
                        <p><FormattedMessage id= "平台上线后将发送到您的账户中"/></p>
                </div>
            )
        }
        if(id == 2){//提交成功
            return (
                <div className="bottom-adver">
                    <p className="sendSuccess">
                        <img  src={successImg} alt=""/>
                        <span>
                            <FormattedMessage id= "提交成功"/>
                        </span>
                    </p>
                </div>
                )
        }
        if(id == 3){//注册完发验证码确认
            return (
                <div className="bottom-adver">
                     <p style={{fontSize:'30px',color:'#FFFFFF'}}><FormattedMessage id= "账号激活"/></p>
                     <p style={{fontSize:'16px',color:'#B4BFD7'}}>
                        <FormattedMessage id= "我们已发送邮件至"/>
                        <span style={{color:'#78A3E4'}}>{userEmail}</span>,
                        <FormattedMessage id= "登录您的邮箱查收并点击链接来激活账号。"/>
                     </p>
                </div>
                )
        }
        
    }

    componentDidMount(){
        this.getScrollTop();
    }

    componentWillMount(){
        let _userEmail = window.localStorage.getItem('userEmail');
        this.setState({
            userEmail:_userEmail
        })
    }
    getScrollTop(){
        var scrollTop=0;
        if(document.documentElement&&document.documentElement.scrollTop){
            setTimeout (() =>{
                document.documentElement.scrollTop = 0;
            },10)
            
            console.log(scrollTop)
        }else if(document.body){

            scrollTop=document.body.scrollTop;
            
        }
            return scrollTop;
    }
    
    render(){
        let {id} = this.props.params
        let {ConfrimText} = this;
        // console.log(ConfrimText)
        let locale = cookie.get(COOKIE_LAN);
        return (
            <div className="market-content inner-bg">
                <div className="inner-top">
                    <div className={locale == 'en' ? 'inner-title-s big-en' : 'inner-title-s'}>
                        {/* <span style={{position:'relative'}}>
                            <FormattedMessage id="由{%%}领投," values={{"%%":'xxx'}}/>
                            {
                                locale !== 'en' &&
                                <span className="text_line1"></span>
                            }
                        </span>
                        <span style={{position:'relative'}}>
                            <FormattedMessage id="{%%}跟投" values={{"%%":'xxx'}}/>
                            {
                                locale !== 'en' &&
                                <span className="text_line2"></span>
                            }
                        </span> */}
                    </div>
                    <div className={locale == 'en'?'inner-title-big big-en':'inner-title-big'}><FormattedMessage id="引领数字交易 成就万千梦想"/></div>
                </div>
                <div className="inner-bottom">
                    <ConfrimText id={id}/>
                </div>
            </div>
        )
    }
}

export default InnerTwo;