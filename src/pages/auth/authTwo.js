import React from 'react';
import UploadImage from 'react-upload-images';
import { Link } from 'react-router';
import { connect } from 'react-redux';
import { browserHistory } from 'react-router';
import axios from '../../utils/axios';
import { getUploadToken } from '../../redux/modules/userInfo';
import { fetchFrontalImg } from '../../redux/modules/session';
import Form from '../../decorator/form';
import hand from '../../assets/img/s.png';
import ihand from '../../assets/img/si.png';
import aimg from '../../assets/img/a.png';
import loadImg from '../../assets/img/licenseLoding.png'
import { formatURL } from '../../utils';
import { UPLOAD_PATH, DOMAIN_VIP } from '../../conf';
import '../../assets/css/userauth.less';
import { FormattedMessage, injectIntl } from 'react-intl';

@connect(
    state => ({userInfo: state.userInfo}),
    { getUploadToken, fetchFrontalImg }
)
@Form
class AuthTwo extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            frontalImg: '',
        };

        this.sk = '请上传手持证件照。';

        this.state = {
            imgHover: 0,
            ckImg: 0,
            handImg: hand,
            email: localStorage.getItem("email"),
            token: localStorage.getItem('token'),
            loading_file_img_1:false,
            success_file_img_1:false,
            showInfor_file_img_1:false,
        };

        this.min = this.min.bind(this);
        this.mout = this.mout.bind(this);
        this.upload = this.upload.bind(this);
        this.uploadSuccress = this.uploadSuccress.bind(this);
        this.cm = this.cm.bind(this);
        this.dictionaries = [...Object.keys(this.base)];

        this.p = Promise.resolve(0);
        this.ps = Promise.resolve(0);
        this.noUp = true;
    }
    cm(){
        let {success_file_img_1} = this.state;
        this.ps.then((r)=>{
            
            if(r || this.noUp){
                if(!this.hasError(this.dictionaries) && success_file_img_1){
                    const send = this.getState(this.dictionaries);
                    const { email, frontalImg, token, } = this.state;
                    send.userName = email;
                    send.imgUrl = frontalImg;
                    send.token = token;
                    this.props.fetchFrontalImg(send, this.callError, 'frontalImg', this.props.intl.formatMessage,this);
                }
            }
        },()=>{})
    }
    upload(files, k, ref){
        axios.post(DOMAIN_VIP+"/manage/auth/uploadToken").then(res => {
        this.setState({
            handImg: files.base64,
            ckImg: 1,
        });
        this.clearsError([k]);
        this.imgInfo.frontalImg = files;
        this.imgInfo.fg = 1;
        
        const { datas:info } = res.data;
        
        this.uploadInfor(k,1)
        const fd = new FormData();
        fd.append('key', info.key);
        fd.append('Signature', info.token);
        fd.append('file',files.file);
        this.clearsError([ref]);
        if(!this.hasError([ref], 0)){
            axios.post(info.host, fd).then(r => {
                this.uploadInfor(k,2)
                this.uploadSuccress({key: info.host + info.key}, ref);
                this.ps = Promise.resolve(1);
            }).catch(err =>{
                this.uploadInfor(k,3)
            });
        } else{
            this.uploadInfor(k,4);
        }

        this.noUp = false;
    });
    }
    forceUpdatz (res,amew){
        this.clearsError([res]);
        document.getElementsByClassName("input-file")[amew].click()  
    }
    uploadSuccress(res, ref){
        this.p = this.clearsError([ref]);
        this.setState({
            [ref]: res.key,
        });
        this.base[ref] = res.key;
    }
    min(){
        this.setState({
            imgHover: 1
        });
    }
    mout(){
        this.setState({
            imgHover: 0
        });
    }
    componentDidMount(){
        // get user upload token
        this.props.getUploadToken().catch((e)=>{
            browserHistory.push(formatURL('notGCode'));
        });
    }
    componentWillReceiveProps(props){
        
    }
    render(){
        const { formatMessage } = this.intl;
        const { imgHover, ckImg, handImg, errors ,showInfor_file_img_1,success_file_img_1,loading_file_img_1} = this.state;
        const { min, mout, cm } = this;
        const { uploadToken } = this.props.userInfo;
        const { isSuc, datas } = uploadToken;
        const { frontalImg:efrontalImg = [] } = errors;
        // console.log(isSuc)

        return (
            <form className="uauth_wp min_h_d clearfix">
                <div className="l lsp">
                    <div className="plv">
                        <Link to={formatURL('notGCode')} className="iconfont bk">&#xe6a3;</Link>
                        <h2 className="tith">{formatMessage({id: "bbyh身份验证"})}</h2>
                    </div>
                    <ul className="list">
                        <li className="lst3x">
                            <h3 className="mb20">{formatMessage({id: "手持证件照片"})}</h3>
                            <div className="upgwp clearfix">
                                <div className="add left plv" onClick={(res)=>{this.forceUpdatz(res, '0')}}>
                                    {
                                        isSuc && !loading_file_img_1? <div onMouseOver={min} onMouseOut={mout}><UploadImage accept="image/png, image/jpeg, image/jpg" name="file" title=" "  uploadLink='' className="file_img_1" onChange={(e)=>{this.upload(e, 'file_img_1', 'frontalImg')}} uploadSuccress={(res)=>{this.uploadSuccress(res, 'frontalImg')}} /></div> : null
                                    }
                                    {
                                        !ckImg
                                        ?
                                        <img src={imgHover ? ihand : hand} onMouseOver={min} onMouseOut={mout} />
                                        :(
                                        <div>
                                            <img src={handImg} />
                                            <img src={aimg} className="addimg" />
                                        </div>
                                        )
                                    }
                                    {/* loading */}
                                    {   loading_file_img_1 &&
                                        <div className="loadingImg">
                                            <img src={loadImg} alt=""/>
                                            <em style={{top:'40%',left:'43%',margin:'0',display:'block',color:'#888A92',fontSize:'40px'}} className="iconfont ld">&#xe6ca;</em>
                                        </div>
                                    }
                                    
                                </div>
                                {   showInfor_file_img_1&&
                                        <p className="picUpInfor">
                                            <svg className="icon" aria-hidden="true">
                                                <use xlinkHref={success_file_img_1?"#icon-yanzhengfangshixuanze":"#icon-renzhengshibai"}></use>
                                            </svg>
                                            <FormattedMessage id={success_file_img_1?"bbyh_上传成功":"bbyh_上传失败"}/>
                                        </p>
                                } 
                                <ol className="art right">
                                    <li>{formatMessage({id: "1.证件照上的所有信息清晰可见，必须能看清证件号和姓名。"})}</li>
                                    <li>{formatMessage({id: "2.照片需免冠，手持证件人的五官清晰可见。"})}</li>
                                    <li>{formatMessage({id: "3.证件信息不允许任何修改或遮挡。"})}</li>
                                    <li>{formatMessage({id: "4.照片内容真实有效，不得做任何修改。"})}</li>
                                    <li>{formatMessage({id: "5.支持.jpg .jpeg .png格式照片，大小不超过5M。"})}</li>
                                </ol>
                            </div>
                        </li>
                    </ul>
                    <div className="plv">
                        <p className="rerr">{efrontalImg[0]}</p>
                    </div>

                    <div className="subs mb20">
                        {/* <em style={{top:'1%',left:'41%',margin:'0',display:'block',color:'#888A92',fontSize:'40px'}} className="iconfont ld">&#xe6ca;</em> */}
                        <input onClick={cm} type="button" value={formatMessage({id: "确定"})} className="i3 v" />
                    </div>

                    <p className="alt">{formatMessage({id: "此项验证需客服人员人工审核，请您耐心等待。"})}</p>
                </div>
            </form>
        );
    }
}

export default AuthTwo;