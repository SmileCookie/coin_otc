import React from 'react';
import { formatURL } from '../../../utils/index';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Link } from 'react-router'; 
import { connect } from 'react-redux';
import { getUploadToken, saveUserAuth } from '../../../redux/modules/userInfo';
import { DISMISS_TIME, CARDTYPES, UPLOAD_PATH } from '../../../conf';
import { reducer as notifReducer, actions as notifActions, Notifs } from 'redux-notifications';
const { notifSend } = notifActions;
import { checkForm, isIdCardNo } from '../../../utils/index';

import Laydate from 'layui-laydate';
import UploadImage from 'react-upload-images';

// img
import frontalImg from '../../../assets/img/user_is_7.png';
import backImg from '../../../assets/img/user_id_4.png';
import loadImg from '../../../assets/img/user_id_5.png';

class IdCardAuth extends React.Component{

    constructor(props){
        super(props);

        // source data
        this.state = {
            countryCode: sessionStorage.getItem('countryCode'),
            countName: sessionStorage.getItem('countName'),
            lastName: '',
            firstName: '',
            cardId: '',
            startDate: '',
            endDate: '',
            frontalImg: '',
            backImg: '',
            loadImg: '',
            cardType: CARDTYPES[0]
        }

        // form dictionaries
        const intl = this.intl = this.props.intl;
        this.formDictionaries = {
            countryCode: intl.formatMessage({id:"user.text67"}),
            countName: intl.formatMessage({id:"user.text67"}),
            lastName: intl.formatMessage({id:"user.text74"}),
            firstName: intl.formatMessage({id:"user.text75"}),
            cardId: intl.formatMessage({id:"user.text93"}),
            _cardId: intl.formatMessage({id:"user.text93"}),
            startDate: intl.formatMessage({id:"user.text91"}),
            endDate: intl.formatMessage({id:"user.text91"}),
            frontalImg: intl.formatMessage({id:"user.text95"}),
            backImg: intl.formatMessage({id:"user.text96"}),
            loadImg: intl.formatMessage({id:"user.text97"}),
            startDateEndDate: intl.formatMessage({id:"user.text92"})
        }

        // check action
        this.checkAction = {
            cardId: [isIdCardNo]
        }


        // event bind
        this.handleInputChange = this.handleInputChange.bind(this);
        this.submission = this.submission.bind(this);
    }

    
    handleInputChange(e){
        this.setState({
            [e.target.getAttribute('data-name')] : e.target.value
        })
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
        });

    }

    uploadSuccress(res, ref){
        this.setState({
            [ref]: res.key
        })
    }
    upload(files, ref){
        this.refs[ref].innerHTML = "<img src='" + files.base64 + "' />";
        this.refs['df_'+ref].style.display = "none";
    }
    submission(){
        // console.log(this.props.router.push, formatURL('authentication'));
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

    render(){
        const locale = this.props.language.locale;
        return (
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
                                <span className={"name_text name_text_2 " + locale}><FormattedMessage id="user.text93" /></span>
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
                            <h3 className="h3_file"><FormattedMessage id="user.text94" /></h3>
                            <div className="file_item clearfix">
                                <div className="file_img_item">
                                    <img className="id_imgs" src={frontalImg} />
                                </div>
                            
                                <div className="file_jiantou">
                                    <span className="user_id_you"></span>
                                    <FormattedMessage id="user.text80" />
                                </div>

                                <div className="file_img_item">
                                    <span className="id_user_id"></span>
                                    <div className="file_text_1" ref="df_file_img_1"><FormattedMessage id="user.text95" /></div>
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

                            <div className="file_item clearfix">
                                <div className="file_img_item">
                                    <img className="id_imgs" src={backImg} />
                                </div>
                                <div className="file_jiantou">
                                    <span className="user_id_you"></span>
                                    <FormattedMessage id="user.text80" />
                                </div>
                                <div className="file_img_item">
                                    <span className="id_user_id"></span>
                                    <div className="file_text_2" ref="df_file_img_2"><FormattedMessage id="user.text96" /></div>
                                    <div className="file_img" id="file_img_2" ref="file_img_2"></div>
                                </div>
                                <div className="file_text">
                                    <div className="files">
                                        {
                                            this.props.userInfo.uploadToken.isSuc ? <UploadImage name="file" uploadLink={UPLOAD_PATH+'?token='+this.props.userInfo.uploadToken.datas.token} className="file_img_2" onChange={(e)=>{this.upload(e, 'file_img_2')}} uploadSuccress={(res)=>{this.uploadSuccress(res, 'backImg')}} /> : null
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

                            <div className="file_item clearfix">
                                <div className="file_img_item">
                                    <img className="id_imgs" src={loadImg} />
                                </div>
                                <div className="file_jiantou">
                                    <span className="user_id_you"></span>
                                    <FormattedMessage id="user.text80" />
                                </div>
                                <div className="file_img_item">
                                    <span className="id_user_id"></span>
                                    <div className="file_text_2" ref="df_file_img_3"><FormattedMessage id="user.text96" /></div>
                                    <div className="file_img" id="file_img_3" ref="file_img_3"></div>
                                </div>
                                <div className="file_text">
                                    <div className="files">
                                        {
                                            this.props.userInfo.uploadToken.isSuc ? <UploadImage name="file" uploadLink={UPLOAD_PATH+'?token='+this.props.userInfo.uploadToken.datas.token} className="file_img_2" onChange={(e)=>{this.upload(e, 'file_img_3')}} uploadSuccress={(res)=>{this.uploadSuccress(res, 'loadImg')}} /> : null
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
        )
    }
}

const mapStateToProps = (state) => {
    return {
        language : state.language,
        userInfo: state.userInfo
    }
};

const mapDispatchToProps = { getUploadToken, notifSend: (msg) => (dispatch) => {
    dispatch(notifSend(msg));
}, saveUserAuth };

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(IdCardAuth));