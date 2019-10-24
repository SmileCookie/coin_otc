import React from "react";
import { Link } from 'react-router';
import { formatURL, optPop} from "../../utils"
import { FormattedMessage, injectIntl } from 'react-intl';
import Form from '../../decorator/form';
import { checkForm, isPassport } from '../../utils/index';
import { DISMISS_TIME, CARDTYPES, UPLOAD_PATH, DOMAIN_VIP } from '../../conf';
import { getUploadToken, saveUserAuth } from '../../redux/modules/userInfo';
import { connect } from 'react-redux';
import UploadImage from 'react-upload-images';
import axios from '../../utils/axios';
import Laydate from 'layui-laydate';
import '../../assets/css/vendor/laydata/laydate.css';
import cookie from 'js-cookie'
import X from '../../assets/img/handPassCardx.png';
import Y from '../../assets/img/passCardAll.png';

@connect(
    state => ({
        language : state.language,
        userInfo: state.userInfo,
    }),
    {
        getUploadToken,
        saveUserAuth,
    }
)
@Form
class AuthenThree extends React.Component{
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
            frontalImg: intl.formatMessage({id:"user.text79"}),
            loadImg: intl.formatMessage({id:"user.text81"}),
        }

        this.base = {
            countryCode: sessionStorage.getItem('countryCode'),
            countName: sessionStorage.getItem('countName'),
            lastName: '',
            firstName: '',
            cardId: '',
            frontalImg: '',
            loadImg: '',
            startDate: '',
            endDate: '',
            cardType: CARDTYPES[1],
        }

        this.ps = Promise.resolve(0);
        this.noUp = true;

        this.state = {
            ...this.base,
            loading_file_img_1:false,
            loading_file_img_2:false,
            class_ul:"",
            success_file_img_1:false,
            success_file_img_2:false,
            showInfor_file_img_1:false,
            showInfor_file_img_2:false,
        }

        this.checkAction = {
            cardId: [isPassport]
        }

        this.handleInputChange = this.handleInputChange.bind(this);
        this.submission = this.submission.bind(this);
        // this.uploadInfor = this.uploadInfor.bind(this);

        this.dic = [...Object.keys(this.base)];
    }

    handleInputChange(e){
        this.setState({
            [e.target.getAttribute('data-name')] : e.target.value
        });
    }

    submission(){
        // if(checkForm(this.state, this.formDictionaries, this.props.language.locale, this.checkAction, undefined, {
        //     empty: this.intl.formatMessage({id:"user.text89"}),
        //     error: this.intl.formatMessage({id:"user.text90"})
        // })){
        //     // commit then back to authentication
        //     this.props.saveUserAuth(this.state).then(res => {
        //         res = res.data;
        //         if(res.isSuc){
        //             this.props.router.push(formatURL('authening'));
        //         }else{
        //             optPop(() => {}, res.des);
        //         }
        //     });
        // }
        let{success_file_img_1,success_file_img_2} = this.state;

        let _picSucsee = success_file_img_1 && success_file_img_2;
        console.log(_picSucsee)

        if(!this.hasError(this.dic) && _picSucsee){
            this.props.saveUserAuth(this.getState(this.dic)).then(res => {
                res = res.data;
                // console.log(res);
                
                if(res.isSuc){
                    let redirectUrl = localStorage.getItem('redirectUrl'); 
                    if (!!redirectUrl){
                        localStorage.removeItem('redirectUrl');
                        window.location.href =  redirectUrl
                        return false;
                    }else{
                        console.log(formatURL('authening'))
                        this.props.router.push(formatURL('authening'));
                    }
                }else{
                    // console.log(res);
                    // this.cbShowAllErrors([{
                    //     key: res.datas,
                    //     msg: res.des,
                    // },]);
                    this.makeResult(res);
                }
            });
        }

    }

    upload(files, ref, key){
        axios.post(DOMAIN_VIP+"/manage/auth/uploadToken").then(res => {
        this.imgInfo[key] = files;
        // this.refs['df_'+ref].style.display = "none";
        this.refs[ref].innerHTML = "<img src='" + files.base64 + "' />";
        const { datas:info } = res.data;
        this.uploadInfor(ref,1)
        console.log(info);
        const fd = new FormData();
        fd.append('key', info.key);
        fd.append('Signature', info.token);
        fd.append('file',files.file);
        this.clearsError([key]);
        if(!this.hasError([key], 0)){
            axios.post(info.host, fd).then(r => {
                this.uploadInfor(ref,2)
                this.uploadSuccress({key: info.host + info.key}, key);
                this.ps = Promise.resolve(1);
            }).catch(err =>{
                this.uploadInfor(ref,3);
            });
        } else{
            this.uploadInfor(ref,4);
        }
        
        this.noUp = false;
        });
    }
    forceUpdatz (res,amew){
        this.clearsError([res]);
        document.getElementsByClassName("input-file")[amew].click()  
    }
    uploadSuccress(res, ref){
        this.clearsError([ref]);
        this.setState({
            [ref]: res.key,
        });
    }
    //上传状态
    // uploadInfor(key,type){
    //     // type:1 上传中，2 上传成功 3 上传失败
    //     if(type == 1){
    //         this.setState({
    //             ['loading_' + key] : true,
    //             ['success_' + key] : false,
    //             ['showInfor_' + key] : false,
    //         })
    //     }
    //     if(type == 2){
    //         this.setState({
    //             ['loading_' + key] : false,
    //             ['success_' + key] : true,
    //             ['showInfor_' + key] : true,
    //         })
    //     }
    //     if(type == 3){
    //         this.setState({
    //             ['loading_' + key] : false,
    //             ['success_' + key] : false,
    //             ['showInfor_' + key] : false,
    //         })
    //     }

    // }

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
                self.clearsError(['startDate']);
            }
        });
        if(cookie.get("zlan")=="en"){
            this.setState({
                class_ul:"photo-ul em_ul"
                })
        }else if(cookie.get("zlan")=="jp"){
            this.setState({
                class_ul:"photo-ul em_ul"
                })
        }else if(cookie.get("zlan")=="kr"){
            this.setState({
                class_ul:"photo-ul em_ul"
                })
        }else{
            this.setState({
                class_ul:"photo-ul"
                })
        }
        Laydate.render({
            elem: '#layDate_2',
            lang: lang,
            done(value){
                self.setState({
                    endDate: value
                });
                self.clearsError(['endDate']);
            }
        }); 

        // get user upload token
        this.props.getUploadToken().catch((e)=>{
            // when error will jump to prev.
            this.props.router.push(formatURL('authenTwo'));
        });
    }

    render(){
        const { formatMessage } = this.props.intl;
        const { errors ,loading_file_img_1,loading_file_img_2,success_file_img_1,success_file_img_2,showInfor_file_img_1,showInfor_file_img_2} = this.state;
        const { lastName:elastName = [], firstName:efirstName = [], cardId:ecardId = [], frontalImg:efrontalImg = [], backImg:ebackImg = [], loadImg:eloadImg = [], startDate:estartDate = [], endDate:eendDate = [] } = errors;
        const { fIn, bOut } = this;
        const showCheckErro_1 = loading_file_img_1 || success_file_img_1 || showInfor_file_img_1;
        const showCheckErro_2 = loading_file_img_2 || success_file_img_2 || showInfor_file_img_2;
        return(
            <div className="mfwp" style={{paddingBottom: '140px'}}>
                <div className="uauth_wp">
                    <div className="pgoback">
                        <Link className="ptext-btn" to={formatURL('authenTwo')}>
                            <i className="iconfont icon-fanhui-moren"></i>
                            {formatMessage({id: "返回上一步"})}
                        </Link>
                    </div>
                    <div className="pinformation clearfix">
                        <div className="pinforma-title">{formatMessage({id: "基本信息"})}</div>
                        <div className="pinfoma-con clearfix">
                            <div className={`con-input ${elastName[0] && 'err'}`}>
                                <div className="input-lable">{formatMessage({id: "姓氏"})}</div>
                                <div className="input-con">
                                    <input onFocus={fIn} onBlur={bOut} name="lastName" type="text" autoComplete="off" data-name="lastName" value={this.state.lastName} onChange={this.handleInputChange} placeholder={formatMessage({id: "请输入姓氏（水印）"})}  />
                                </div>
                                <p className="ealt">{elastName[0]}</p>
                            </div>
                            <div className={`con-input ${efirstName[0] && 'err'}`}>
                                <div className="input-lable">{formatMessage({id: "名字"})}</div>
                                <div className="input-con">
                                    <input onFocus={fIn} onBlur={bOut} name="firstName" type="text" autoComplete="off" data-name="firstName" value={this.state.firstName} onChange={this.handleInputChange} placeholder={formatMessage({id: "请输入名字（水印）"})} />
                                </div>
                                <p className="ealt">{efirstName[0]}</p>
                            </div>
                        </div>
                        <div className="pinfoma-con">
                            <div className={`con-input marb20 ${ecardId[0] && 'err'}`}>
                                <div className="input-lable">{formatMessage({id: "护照号码"})}</div>
                                <div className="input-con">
                                    <input onFocus={fIn} onBlur={bOut} name="cardId" type="text" autoComplete="off" data-name="cardId" value={this.state.cardId} onChange={this.handleInputChange} placeholder={formatMessage({id: "请输入护照号码（水印）"})} />
                                </div>
                                <p className="ealt">{ecardId[0]}</p>
                            </div>
                        </div>

                        <div className="pinfoma-con">
                            <div className={`con-input marb20 sp`}>
                                <div className="input-lable">{formatMessage({id: '有效期'})}</div>
                                <span className={`q ${(estartDate[0] || eendDate[0]) && 'err'}`}>
                                <div className={`input-con sp`}>
                                    <input  readOnly="true" type="text" className="laydate" id="layDate_1" data-name="startDate" value={this.state.startDate} placeholder={formatMessage({id: "请选择"})} />
                                </div></span><span className="fg" style={{color:'#5f6575'}}>-</span><span className={`q ${(estartDate[0] || eendDate[0]) && 'err'}`}><div className={`input-con sp`}><input readOnly="true" type="text" className="laydate" id="layDate_2" data-name="endDate" value={this.state.endDate} placeholder={formatMessage({id: "请选择"})} />                                 
                                </div></span>
                                <p className="ealt">{estartDate[0] ? estartDate[0] : eendDate[0]}</p>
                            </div>
                        </div>

                    </div>
                    <div className="pinformation clearfix">
                        <div className="pinforma-title">{formatMessage({id: "上传护照照片"})}</div>
                        <div className="p-photo clearfix">
                            <div className="pimg bh-specilImg">
                                <div className="wp"  ref="file_img_1">
                                    <img src={Y} />
                                </div>
                            </div>
                            <div className="photo-ul clearfix">
                                <Link className="pupload-btn plv"  onClick={(res)=>{this.forceUpdatz(res, '0')}} >
                                    { 
                                        loading_file_img_1?
                                        <em style={{top:'1%',left:'41%',margin:'0',display:'block'}} className="iconfont ld">&#xe6ca;</em>
                                        :
                                        formatMessage({id: "上传"})
                                    }
                                    {/* <em style={{top:'1%',left:'41%',margin:'0'}} className="iconfont ld">&#xe6ca;</em>
                                    {formatMessage({id: "上传"})} */}
                                    {
                                        this.props.userInfo.uploadToken.isSuc && !loading_file_img_1? <UploadImage accept="image/png, image/jpeg, image/jpg" name="file" uploadLink="" className="file_img_1" onChange={(e)=>{this.upload(e, 'file_img_1', 'frontalImg')}} uploadSuccress={(res)=>{this.uploadSuccress(res, 'frontalImg')}} /> : null
                                    }
                                </Link>
                            </div>
                            {
                                showInfor_file_img_1&&
                                <p className="picUpInfor">
                                    <svg className="icon" aria-hidden="true">
                                        <use xlinkHref={success_file_img_1?"#icon-yanzhengfangshixuanze":"#icon-renzhengshibai"}></use>
                                    </svg>
                                    <FormattedMessage id={success_file_img_1?"bbyh_上传成功":"bbyh_上传失败"}/>
                                </p>
                            }
                            
                        </div>
                        <p className="ealt" style={{display:showCheckErro_1 ? 'none':'block'}}>{efrontalImg[0]}</p>
                    </div>
                    <div className="pinformation clearfix">
                        <div className="pinforma-title">{formatMessage({id: "上传手持护照照片"})}</div>
                        <div className="p-photo clearfix p-photo1055">
                            <div className="pimg" style={{padding:'22px 20px'}}>
                                <div className="wp" style={{width:'257px'}} ref="file_img_2">
                                    <img src={X} />
                                </div>
                            </div>
                            <div className={this.state.class_ul}>
                                <ul>
                                    <li>{formatMessage({id: "1.护照上的所有信息清晰可见，必须能看清证件号和姓名。"})}</li>
                                    <li>{formatMessage({id: "2.照片需免冠，手持证件人的五官清晰可见。"})}</li>
                                    <li>{formatMessage({id: "3.护照信息不允许任何修改或遮挡。"})}</li>
                                    <li>{formatMessage({id: "4.照片内容真实有效，不得做任何修改。"})}</li>
                                    <li>{formatMessage({id: "5.支持.jpg .jpeg .png格式照片，大小不超过5M"})}</li>
                                </ul>
                                <Link className="martop30 pupload-btn plv" onClick={(res)=>{this.forceUpdatz(res, '1')}}>
                                    {
                                        loading_file_img_2?
                                        <em style={{top:'1%',left:'41%',margin:'0',display:'block'}} className="iconfont ld">&#xe6ca;</em>
                                        :
                                        formatMessage({id: "上传"})
                                    }
                                    {
                                        this.props.userInfo.uploadToken.isSuc && !loading_file_img_2? <UploadImage accept="image/png, image/jpeg, image/jpg" name="file" uploadLink="" className="file_img_1" onChange={(e)=>{this.upload(e, 'file_img_2', 'loadImg')}} uploadSuccress={(res)=>{this.uploadSuccress(res, 'loadImg')}} /> : null
                                    }
                                </Link>
                            </div>
                            {
                                showInfor_file_img_2&&
                                <p className="picUpInfor">
                                    <svg className="icon" aria-hidden="true">
                                        <use xlinkHref={success_file_img_2?"#icon-yanzhengfangshixuanze":"#icon-renzhengshibai"}></use>
                                    </svg>
                                    <FormattedMessage id={success_file_img_2?"bbyh_上传成功":"bbyh_上传失败"}/>
                                </p>
                            }
                        </div>
                        <p className="ealt" style={{display:showCheckErro_2 ? 'none':'block'}}>{eloadImg[0]}</p>
                    </div>
                    <Link className="pnext-btn bbyh_pnext-btn" onClick={this.submission}>{formatMessage({id: "提交申请"})}</Link>
                    
                </div>
            </div>
        )
    }
}
export default injectIntl(AuthenThree);