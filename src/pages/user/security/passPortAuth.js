import React from 'react';
import { formatURL } from '../../../utils/index';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Link } from 'react-router'; 
import { connect } from 'react-redux';
import Laydate from 'layui-laydate';
import UploadImage from 'react-upload-images';
import { getUploadToken, saveUserAuth } from '../../../redux/modules/userInfo';
import { checkForm, isPassport } from '../../../utils/index';
import { DISMISS_TIME, CARDTYPES, UPLOAD_PATH } from '../../../conf';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend } = notifActions;
// get base image
import PassportImg from '../../../assets/img/user_id_6.png';
import IdCard from '../../../assets/img/user_id_2.png';

class PassPortAuth extends React.Component{
    constructor(props){
        super(props);

        // form dictionaries
        const intl = this.intl = this.props.intl;
        this.formDictionaries = {
            countryCode: intl.formatMessage({id:"user.text67"}),
            countName: intl.formatMessage({id:"user.text67"}),
            lastName: intl.formatMessage({id:"user.text74"}),
            firstName: intl.formatMessage({id:"user.text75"}),
            cardId: intl.formatMessage({id:"user.text76"}),
            _cardId: intl.formatMessage({id:"user.text76"}),
            startDate: intl.formatMessage({id:"user.text91"}),
            endDate: intl.formatMessage({id:"user.text91"}),
            frontalImg: intl.formatMessage({id:"user.text79"}),
            loadImg: intl.formatMessage({id:"user.text81"}),
            startDateEndDate: intl.formatMessage({id:"user.text92"})
        }
        
        this.state = {
            countryCode: sessionStorage.getItem('countryCode'),
            countName: sessionStorage.getItem('countName'),
            lastName: '',
            firstName: '',
            cardId: '',
            startDate: '',
            endDate: '',
            frontalImg: '',
            loadImg: '',
            cardType: CARDTYPES[1]
        }

        this.checkAction = {
            cardId: [isPassport]
        }

        this.handleInputChange = this.handleInputChange.bind(this);
        this.submission = this.submission.bind(this);
    }
    handleInputChange(e){
        this.setState({
            [e.target.getAttribute('data-name')] : e.target.value
        })
    }
    upload(files, ref){
        this.refs[ref].innerHTML = "<img src='" + files.base64 + "' />";
        this.refs['df_'+ref].style.display = "none";
    }
    uploadSuccress(res, ref){
        this.setState({
            [ref]: res.key
        })
    }
    submission(){
        // verification
        if(checkForm(this.state, this.formDictionaries, this.props.language.locale, this.checkAction, this.props.notifSend, {
            empty: this.intl.formatMessage({id:"user.text89"}),
            error: this.intl.formatMessage({id:"user.text90"}),
            timer: DISMISS_TIME
        })){
            // commit then back to authentication
            this.props.saveUserAuth(this.state).then(res => {
                res = res.data;
                if(res.isSuc){
                    this.props.router.push(formatURL('authentication'));
                }else{
                    this.props.notifSend({
                        message: res.des,
                        kind: 'info',
                        dismissAfter: DISMISS_TIME
                    })
                }
            })
        }
    }
    componentDidMount(){
        const lang = this.props.language.locale == 'zh' ? 'cn' : 'en';
        const self = this;

        Laydate.render({
            elem: '#layDate_1',
            lang: lang,
            done(value){
                self.setState({
                    startDate: value
                });
            }
        });
        
        Laydate.render({
            elem: '#layDate_2',
            lang: lang,
            done(value){
                self.setState({
                    endDate: value
                });
            }
        }); 

        // get user upload token
        this.props.getUploadToken().catch((e)=>{
            // when error will jump to prev.
            this.props.router.push(formatURL('authtype'));
        })
    }
    render(){
        const locale = this.props.language.locale;
        return(
            <div className="auth_wp">
                <h2 className="tith"><FormattedMessage id="user.text49" /></h2>
                <div className="from_prev">
                    <Link to={formatURL('authtype')}>
                        <span> &lt; </span>
                        <FormattedMessage id="nav.text1" />
                    </Link>
                </div>
                <div className="user_auth_content">
                    <div className="user_auth_id_text">
                        <h4><FormattedMessage id="user.text73" /></h4>
                        <div className="id_text_wrap clearfix">
                            <div className="id_item">
                                <span className={"name_text name_text_2 " + locale}><FormattedMessage id="user.text74" /></span>
                                <div className="id_text_input_wrap">
                                    <input className="id_text_input name_toUpCase" type="text" autoComplete="off" data-name="lastName" value={this.state.lastName} onChange={this.handleInputChange} />
                                </div>
                            </div>

                            <div className={"id_item marr_100 marr_" + locale + " clearfix"}>
                                <span className={"name_text name_text_1 " + locale + " left"}><FormattedMessage id="user.text75" /></span>
                                <div className="id_text_input_wrap left">
                                    <input className="id_text_input name_toUpCase" type="text" autoComplete="off" data-name="firstName" value={this.state.firstName} onChange={this.handleInputChange} />
                                </div>
                            </div>

                            <div className="id_item cl">
                                <span className={"name_text name_text_2 " + locale}><FormattedMessage id="user.text76" /></span>
                                <div className="id_text_input_wrap">
                                    <input className="id_text_input" type="text" autoComplete="off" data-name="cardId" value={this.state.cardId} onChange={this.handleInputChange} />
                                </div>
                            </div>

                            <div className={"id_item marr_100 marr_" + locale + " clearfix"}>
                                <span className={"name_text name_text_1 " + locale + " left"}><FormattedMessage id="user.text77" /></span>
                                <div className="id_text_input_wrap clearfix">
                                    <div className="laydate_warp">
                                        <input readOnly="true" type="text" className="laydate" id="layDate_1" data-name="startDate" value={this.state.startDate} />
                                    </div>
                                    <div className="laydate_jiantou">
                                        -
                                    </div>
                                    <div className="laydate_warp">
                                        <input readOnly="true" type="text" className="laydate" id="layDate_2" data-name="endDate" value={this.state.endDate} />
                                    </div>
                                </div>
                            </div>

                        </div>

                        <div className="id_imgfile_warp">
                            <h3 className="h3_file"><FormattedMessage id="user.text78" /></h3>
                            <div className="file_item clearfix" id="qiniu_1">
                                <div className="file_img_item">
                                    <img className="id_imgs" src={IdCard} />
                                </div>
                            
                                <div className="file_jiantou">
                                    <span className="user_id_you"></span>
                                    <FormattedMessage id="user.text80" />
                                </div>

                                <div className="file_img_item">
                                    <span className="id_user_id"></span>
                                    <div className="file_text_1" ref="df_file_img_1"><FormattedMessage id="user.text79" /></div>
                                    <div className="file_img" id="file_img_1" ref="file_img_1"></div>
                                </div>

                                <div className="file_text">
                                    <div className="files">
                                        {
                                            this.props.userInfo.uploadToken.isSuc ? <UploadImage name="file" uploadLink={UPLOAD_PATH+'?token='+this.props.userInfo.uploadToken.datas.token} className="file_img_1" onChange={(e)=>{this.upload(e, 'file_img_1')}} uploadSuccress={(res)=>{this.uploadSuccress(res, 'frontalImg')}} /> : null
                                        }
                                        <div className="file_warp">
                                            <FormattedMessage id="user.text88" />
                                        </div>
                                    </div>
                                    <div className="files_text_foot">
                                        <FormattedMessage id="user.text82" />
                                    </div>
                                </div>
                            </div>

                            <div className="file_item clearfix" id="qiniu_2">
                                <div className="file_img_item">
                                    <img className="id_imgs" src={PassportImg} />
                                </div>
                                <div className="file_jiantou">
                                    <span className="user_id_you"></span>
                                    <FormattedMessage id="user.text80" />
                                </div>
                                <div className="file_img_item">
                                    <span className="id_user_id"></span>
                                    <div className="file_text_2" ref="df_file_img_2"><FormattedMessage id="user.text81" /></div>
                                    <div className="file_img" id="file_img_2" ref="file_img_2"></div>
                                </div>
                                <div className="file_text">
                                    <div className="files">
                                        {
                                            this.props.userInfo.uploadToken.isSuc ? <UploadImage name="file" uploadLink={UPLOAD_PATH+'?token='+this.props.userInfo.uploadToken.datas.token} className="file_img_2" onChange={(e)=>{this.upload(e, 'file_img_2')}} uploadSuccress={(res)=>{this.uploadSuccress(res, 'loadImg')}} /> : null
                                        }
                                        <div className="file_warp">
                                            <FormattedMessage id="user.text88" />
                                        </div>
                                    </div>
                                    <div className="files_text_foot">
                                        <FormattedMessage id="user.text82" />
                                    </div>
                                </div>
                            </div>
                        </div>
                        <section className="foot_user_text">
                            <p>1. <FormattedMessage id="user.text83" /></p>
                            <p>2. <FormattedMessage id="user.text84" /></p>
                            <p>3. <FormattedMessage id="user.text85" /></p>
                            <p>4. <FormattedMessage id="user.text86" /></p>
                        </section>
                        <section className="user_auth_submit" onClick={this.submission}>
                            <FormattedMessage id="user.text87" />
                        </section>
                    </div>
                </div>
            </div>
        );
    }
}

const mapStateToProps = (state) =>{
    return {
        language : state.language,
        userInfo: state.userInfo
    }
}

const mapDispatchToProps = { getUploadToken, notifSend: (msg) => (dispatch) => {
    dispatch(notifSend(msg));
}, saveUserAuth };

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(PassPortAuth));