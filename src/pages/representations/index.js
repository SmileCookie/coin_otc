import React from 'react';
import {connect} from 'react-redux';
import {withRouter} from 'react-router';
import { FormattedMessage,injectIntl } from 'react-intl';
import ScrollArea from 'react-scrollbar';
import '../../assets/style/representations/index.less';
import FileUpload from '../../components/upload/index'
import {formatDate, formatURL, optPop, RepreFormatDate} from "../../utils";
import {updComplain,otcComplain} from './model'
import ReactModal from "../../components/popBox";
import ReactModals from "../../components/popBoxs";
import UserCenter from '../../components/user/userCenter'
import {post} from "../../net";
import cookie from 'js-cookie'
import {USERID} from "../../conf";


@connect(
    state => ({
        userInfo:state.session.userInfor,
        count: state.chart.count,
        sysText: state.chart.sysText
    }),
    {}
)


class Representations extends React.Component {
    constructor(props) {
        super(props);
        this.vidRef = React.createRef();
        this.imgRef = React.createRef();
        this.state = {
            imgLimit:6,
            imgFlag:false,
            vidLimit:1,
            vidFlag:false,
            descFlag:false,
            loading:true,
            textFlag:false,
            id:'', // 申诉id
            fileList:[],
            identity:'',
            imgList:[],
            videoList:[],
            modalHTML:'',
            userId:cookie.get("zuid"),
            maxHeight: 360,
            lastCont:2,
            RYCount:0, // 融云消息数量
            RYtext:'', // 融云消息数量
            count: 0,
            textAreaVal: '',
            sysText:[],
            homePage:'', //个人主页
            targetId:'',
            uid:'',
            states:true,
            complainDetail: {
                checkDesc:'',
                checkResult:'', //0-未判定；1-买方胜；2-卖方胜
                recordNo: '',
                recordStatus: 0,// 0是异常订单
                statusName: 0, // 0是未受理 1是已受理 2是已完成
                buyUserName:'',
                sellUserName:'',
                commitTimes:'',
                talkDatail: [
                    {
                        talkTime: '',
                        list: [
                            {
                                addTime: '',
                                type: 0, // 0是买家，1是卖家
                                name: '', // 姓名
                                imgType: 1, // 姓名
                                memo: '',
                                url: '',
                                complainId: '',
                            }
                        ]
                    }
                ]
            }
        };
        this.getComplainInfo = this.getComplainInfo.bind(this);
        this.submitComplain = this.submitComplain.bind(this);

    }
    componentWillMount(){
        let userInfo = this.props.userInfo
        if (!userInfo.data){
            this.props.history.push('/bw/login')
        }
        this.setState({
            RYCount : this.props.count,
            RYtext: this.props.text
        })

    }

    componentDidMount() {
        let id = this.props.match.params.id;
        let sysText=this.props.sysText==null?[]:this.props.sysText
            this.setState({
                id:id,
                sysText:sysText
            },() =>{
                this.getComplainInfo();
            })
            var textarea = document.getElementById('textarea');
            this. makeExpandingArea(textarea);
        // console.log(this.props)
    }
     makeExpandingArea(el) {
        var timer = null;
        //由于ie8有溢出堆栈问题，故调整了这里
        var setStyle = function(el, auto) {
            if (auto) el.style.height = 'auto';
            el.style.height = el.scrollHeight + 'px';
        }
        var delayedResize = function(el) {
            if (timer) {
                clearTimeout(timer);
                timer = null;
            }
            timer = setTimeout(function() {
                setStyle(el)
            }, 200);
        }
        if (el.addEventListener) {
            el.addEventListener('input', function() {
                setStyle(el, 1);
            }, false);
            setStyle(el)
        } else if (el.attachEvent) {
            el.attachEvent('onpropertychange', function() {
                setStyle(el)
            })
            setStyle(el)
        }
        if (window.VBArray && window.addEventListener) { //IE9
            el.attachEvent("onkeydown", function() {
                var key = window.event.keyCode;
                if (key == 8 || key == 46) delayedResize(el);

            });
            el.attachEvent("oncut", function() {
                delayedResize(el);
            }); //处理粘贴
        }
    }

    componentWillReceiveProps(nextProps, nextContext) {
        if (nextProps.sysText&&nextProps.sysText.objectName == 'OTC:ComplainMsg'&&nextProps.sysText.content.message.content.extra==this.state.complainDetail.recordNo){
            let id = this.state.id;
            let that = this;
            otcComplain(id).then((res) =>{
              console.log(res);

              this.setState({
                  complainDetail:res,
              }, ()=>{
                  setTimeout(()=>{
                      that.initScrollBar();
                  }, 100)
              })
            })
        }
    }
    // 更新图片列表
    getImgList = (v) =>{
        this.setState({
            imgList: [...v]
        })
    }
    // 更新视频列表
    getVideoList = (v) =>{
        this.setState({
            videoList: [...v]
        })
    }
    // 获取申诉详情
    getComplainInfo =() =>{
        let id = this.state.id;
        let that = this;
        otcComplain(id).then((res) =>{
        //   console.log(res);
          let identity=res.buyUserId==this.state.userId?0:1
          this.setState({
              complainDetail:res,
              identity:identity
          }, ()=>{
              setTimeout(()=>{
                  that.initScrollBar();
              }, 100)
          })
        })
    }
    // 滚动条在最新的消息处
    initScrollBar = () => {
        let childH = document.getElementsByClassName('scrollarea-content')[1].clientHeight;
        let margin = parseInt(childH - 360);
        this.refs.scrollerBar.scrollArea.scrollYTo(margin);
    };
    submitComplain =() => {
        // this.getUploadFiles();
        if(this.checkAllDate()){
            //开关预防多次提交
            if(this.state.states==true){
                this.setState({
                    states:false,
                    loading:false
                })
                // this.getUploadFiles();
                this.getUploadFilesV1();
            }

        }
    }
    // 图片放大
    openModalInfo=(url)=>{
        //confirm demo
        let str = <div className="image-dialog">
            <div className="dialog-title">
                <span onClick={() => this.dialog.closeModal()}>×</span>
            </div>
            <div className="dialog-cont">
                <img src={url} alt=""/>
            </div>
        </div>;

        this.setState({modalHTML:str},()=>{
            this.dialog.openModal();
        });
    };
      // 视频
      openModalInfos=(url)=>{
        //confirm demo
        let str = <div className="image-dialog">
        <div className="dialog-title">
            <span onClick={() => this.dialogs.closeModal()}>×</span>
        </div>
        <div className="dialog-cont">
        <video  className="video" type="video/mp4" controls="controls" autoplay="autoplay">
            <source src={url}  type="video/mp4"></source>
            <source src={url}  type="video/ogg"></source>
            您的浏览器不支持 HTML5 video 标签。 </video>
        </div>
    </div>;

    this.setState({modalHTML:str},()=>{
        this.dialogs.openModal();
    });
    };
    dswa = () =>{
        this.setState({
            imgFlag: false
        })
        return false
    }
    // 图片list校验
    ckImgList = () =>{
        let flag = true,
            imgs = this.imgRef.current.getFileList()
        if (imgs.length < 1){
            this.setState({
                imgFlag: true
            })
            flag = false
        }else{
            this.setState({
                imgFlag: false
            })
        }
        return flag
    }

    // 视频list 校验
    ckVidList = () =>{
        // let flag = true,
        //     videos = this.vidRef.current.getFileList()
        // if (videos.length < 1){
        //     this.setState({
        //         vidFlag: true
        //     })
        //     flag = false
        // }else{
        //     this.setState({
        //         vidFlag: false
        //     })
        // }
        // return flag
    }
    // 申诉内容校验
    ckDesc = () => {
        let flag = true,
            desc = this.state.textAreaVal;
        if (desc.length <= 0){
            this.setState({
                descFlag: true
            })
            flag = false
        }else{
            this.setState({
                descFlag: false
            })
        }
        return flag;
    }


    // 校验全部
    checkAllDate = () =>{
        let flag = true
        if(!this.ckDesc()){
            flag = false
        }
        // if(!this.ckVidList()){
        //     flag = false;
        // }
        if(!this.ckImgList()){
            flag = false;
        }
        return flag;
    }



    // 获取上传图片及视频
    getUploadFiles = () =>{
        let imgs,videos,imgList=[],vidList=[];
        imgs = this.imgRef.current.getFileList()
        videos = this.vidRef.current.getFileList()
        // console.log(imgs,videos);
        for(let item of imgs){
            imgList.push(item.originFileObj)
        }
        for(let item of videos){
            vidList.push(item.originFileObj)
        }

        let id = this.state.id,
            desc = this.state.textAreaVal,
            imgDelFlg = 0,
            videoDelFlg = 0,
            files = imgList.concat(vidList),

            repeatFlg = 0;
        let d = new FormData();
        d.append('id',id);
        d.append('desc',desc);
        d.append('imgDelFlg',imgDelFlg);
        d.append('videoDelFlg',videoDelFlg);
        for(let item of files){
            d.append('files',item)
        }
        d.append('repeatFlg',repeatFlg);
        updComplain(d).then((res) =>{
                console.log(res);
                if (res.code == 200){
                    optPop(() =>{this.getComplainInfo(),this.clearUploadFiles()},res.msg,{timer:1500})
                    this.setState({
                        loading:true
                    })
                    //optPop({},res.msg,{timer:1500})

                }else{
                    this.setState({
                        loading:true
                    })
                    optPop({},res.msg,{timer:1500})
                }
                this.setState({
                    states:true
                })
            })
    }

    // 获取上传图片及视频
    getUploadFilesV1 = () =>{
        let imgs,videos,imgList=[],vidList=[];
        imgs = this.imgRef.current.getFileList()
        videos = this.vidRef.current.getFileList()
        // console.log(imgs,videos);
        for(let item of imgs){
            imgList.push(item.url)
        }
        for(let item of videos){
            vidList.push(item.url)
        }

        let id = this.state.id,
            desc = this.state.textAreaVal,
            imgDelFlg = 0,
            videoDelFlg = 0,
            files = imgList.concat(vidList),

            repeatFlg = 0;
        let d = new FormData();
        d.append('id',id);
        d.append('desc',desc);
        d.append('imgDelFlg',imgDelFlg);
        d.append('videoDelFlg',videoDelFlg);
        d.append('repeatFlg',repeatFlg);
        d.append('images',imgList.join(','));
        d.append('video',vidList.join(','));
        updComplain(d).then((res) =>{
            console.log(res);
            if (res.code == 200){
                optPop(() =>{this.getComplainInfo(),this.clearUploadFiles()},res.msg,{timer:1500})
                this.setState({
                    loading:true
                })
                //optPop({},res.msg,{timer:1500})

            }else{
                this.setState({
                    loading:true
                })
                optPop({},res.msg,{timer:1500})
            }
            this.setState({
                states:true
            })
        })
    }
    // 清空上传 from
    clearUploadFiles = () =>{
        this.setState({
            textAreaVal:'',
            count: 0
        });
        this.imgRef.current.resetFlieList();
        this.vidRef.current.resetFlieList();
    }
    // 申诉说明 文本watch
    onTextChange = e => {
        let val = e.target.value
        this.setState({
            textAreaVal: val,
            count: val.length
        },() =>{
            if(this.state.count >= 200){
                this.setState({
                    textFlag: false
                })
            }else{
                this.setState({
                    textFlag: false
                })
            }
        })
        // this.ckDesc()
    };
    setDescFlag = () =>{
        this.setState({
            descFlag:false
        })
    }
    // 显示个人主页
    showHomePage = (id) =>{
        let data = new FormData(),
            targetId = parseInt(id);
            let ueid=cookie.get("zuid")
        let userId=parseInt(this.state.userId)
        // data.append('targetUserId',targetId)
        post( '/web/common/getAvgPassTime', {targetUserId: targetId,ueid }).then((res)=>{
            console.log(res);
            let msg = res.msg;
            if (res.code == 200){
                this.setState({
                    targetId:targetId,
                    uid:USERID,
                    homePage: res.data
                },() =>{
                    this.modal.openModal();
                })
            }else{
                optPop(() =>{
                },msg,{timer: 1500})
            }
        })
    }

    render() {
        const {formatMessage} = this.props.intl;
        const { complainDetail,maxHeight,count,textAreaVal,imgLimit,vidLimit,descFlag,imgFlag,vidFlag,textFlag,userId,RYcount,identity,loading} = this.state;
        // console.log(identity)
        return (
            <div className="representations">
                 {
                    loading===false&&<div><div className="toum"></div><div className="iconfont icon-jiazai new-loading" style={{"position": "fixed"}}></div></div>
                }
                <div className="page_title"><FormattedMessage id="申诉详情"/></div>
                <div className="representations_content">
                    <div className="top_line">
                        <div className="top_line_item">
                            <FormattedMessage id="订单编号："/>
                            <span className="color_white">{complainDetail.recordNo}</span>
                        </div>
                        <div className="top_line_item margin_l80">
                            <FormattedMessage id="订单状态："/>
                            <span className="orderStatus">
                                {complainDetail.recordStatus === 3 ? formatMessage({id: '交易完成'}): complainDetail.recordStatus === 4 ? formatMessage({id: '交易取消'}): complainDetail.recordStatus === 5 ? formatMessage({id: '异常订单'}):complainDetail.recordStatus === 6 ? formatMessage({id: '申诉中'}):formatMessage({id: '待放币'})}
                            </span>
                        </div>
                        <div className="top_line_item margin_l80">
                            <FormattedMessage id="申诉状态："/>
                            <span className="color_white">{formatMessage({id: `${complainDetail.statusName}`})}</span>
                        </div>
                    </div>
                    {
                        complainDetail.checkResult != 0 ?
                            <div className="complain-result">
                                <p><span>{formatMessage({id:'申诉结果'})}:</span>{complainDetail.checkResult == 1  ? formatMessage({id: '买方胜'}) :  formatMessage({id: '卖方胜'}) }</p>
                                <p><span>{formatMessage({id:'判定说明'})}:</span>{complainDetail.checkDesc=="买方放弃申诉"?formatMessage({id: '买方放弃申诉'}):complainDetail.checkDesc=="卖方放弃申诉"?formatMessage({id: '卖方放弃申诉'}):complainDetail.checkDesc}</p>
                            </div>
                            :
                            null
                    }

                    <ScrollArea ref="scrollerBar" style={{maxHeight:maxHeight}}>
                        <div className="represent_details">

                            {
                                complainDetail.talkDatail.length > 0 && complainDetail.talkDatail[0].talkTime
                                    ?
                                    complainDetail.talkDatail.map((item, i)=> {
                                            let style = item.list[0].type == identity ?"right":"left",
                                                color = item.list[0].type == 0 ?complainDetail.buyUserColor: complainDetail.sellUserColor,
                                                name = item.list[0].type == 0 ? complainDetail.buyUserName:complainDetail.sellUserName,
                                                id = item.list[0].type == 0 ? complainDetail.buyUserId:complainDetail.sellUserId
                                        return (
                                            <div key={i} className="item_info clearfix">
                                                {i==0?<p className="date">{formatDate(item.talkTime,'yyyy-MM-dd')}</p>:formatDate(complainDetail.talkDatail[i-1].list[0].addTime,'yyyy-MM-dd')==formatDate(item.talkTime,'yyyy-MM-dd')?"":<p className="date">{formatDate(item.talkTime,'yyyy-MM-dd')}</p>}

                                                {
                                                     item.list[0].type==2?
                                                        <div  className="item_info clearfix">
                                                            <div className={`details left`}>
                                                                <div className={`name left service`}></div>
                                                                <div className={`content left`}>
                                                                    <p className="time" style={{ textAlign: 'left' }} >{formatDate(item.list[0].addTime,'hh:mm:ss')}</p>
                                                                    <div className="content_box servicemsg">
                                                                        <p className="" style={{"padding-bottom":"0px"}}>{formatMessage({id:item.list[0].memo})}</p>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>:
                                                    // let id = item.type == 0 ? complainDetail.buyUserId : complainDetail.sellUserId
                                                    <div className={`details ${style}`}>
                                                        <div className={`name ${style} `} style={{background:color}} onClick={() =>{this.showHomePage(id)}}>{name.substring(0,1).toLocaleUpperCase()}</div>
                                                        <div className={`content ${style}`}>
                                                            <p className="time" style={{ textAlign: style }} >{formatDate(item.list[0].addTime,'hh:mm:ss')}</p>
                                                            <div className="content_box">
                                                                <p >{item.list[0].memo}</p>
                                                                <div className="img_video">

                                                                    {
                                                                        item.list.map((img, key)=> {
                                                                            return(
                                                                                img.imgType == 1 ?
                                                                                <img key={key} src={img.url} alt="" onClick={() =>{this.openModalInfo(img.url)}}/>
                                                                                :
                                                                                <div className="s_video" onClick={() =>{this.openModalInfos(img.url)}}>
                                                                                    <div className="bo"></div>
                                                                                    <video  className="video" type="video/mp4" >
                                                                                    <source src={img.url}  type="video/mp4"></source>
                                                                                    <source src={img.url}  type="video/ogg"></source>
                                                                                    {formatMessage({id:"浏览器暂不支持"})} </video>
                                                                                </div>

                                                                            )
                                                                        })
                                                                    }

                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                }

                                            </div>
                                        )

                                    })
                                    :
                                    <div className="no_data">
                                        <FormattedMessage id="暂无申诉"/>
                                    </div>

                                    // {/* todo  客服消息*/}
                                    // {
                                    //     sysText &&  sysText.objectName == 'OTC:ComplainMsg' ?
                                    //     <div className={`details left`}>
                                    //         <div className={`name left service`}>{'客服'}</div>
                                    //         <div className={`content left`}>
                                    //             <p className="time" style={{ textAlign: 'left' }} >{formatDate(sysText.sentTime,'hh:mm:ss')}</p>
                                    //             <div className="content_box servicemsg">
                                    //                 <p className="" >{sysText.content.message.content}</p>
                                    //             </div>
                                    //         </div>
                                    //     </div>
                                    //     :null
                                    // }



                            }
                                {/* {
                                      sysText.length>0?sysText.map((item, i)=> {
                                        return (
                                        <div  className="item_info clearfix">
                                                <div className={`details left`}>
                                                    <div className={`name left service`}>{'客服'}</div>
                                                    <div className={`content left`}>
                                                        <p className="time" style={{ textAlign: 'left' }} >{formatDate(item.sentTime,'hh:mm:ss')}</p>
                                                        <div className="content_box servicemsg">
                                                            <p className="" >{item.content.message.content.content}</p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        )
                                      })
                                      :null


                                    } */}
                        </div>
                    </ScrollArea>
                    {
                        complainDetail.checkResult == 0
                            ?
                            <div>
                                <div className="form_box">
                                    <label><FormattedMessage id="申诉说明："/></label>
                                    <div className="textArea_content">
                                        <ScrollArea style={{"width":"900px","height":"140px"}}>
                                            <textarea maxLength="200" name="" id="textarea" rows="5"  placeholder={formatMessage({id:'请输入申诉说明'})} onChange={this.onTextChange} onBlur={this.ckDesc} onFocus={this.setDescFlag} value={textAreaVal} style={{"outline":"none"}} className={`${textFlag && 'text-error'}`}> </textarea>
                                        </ScrollArea>

                                        <span>{count}/200</span>

                                    </div>
                                    <p className={`error ${descFlag && 'active'}` }>{formatMessage({id:'请输入申诉说明'})}</p>
                                    <p className={`error ${textFlag && 'active'}` }>{formatMessage({id:'申诉说明最多可输入200个符号'})}</p>
                                </div>
                                <div className="form_box">
                                    <label><FormattedMessage id="申诉截图："/></label>
                                    <div className="upload_img" onClick={() =>{this.dswa()}}>
                                        <div className="upload-div  ddd">
                                            <FileUpload  ref={this.imgRef} limit={imgLimit}  msg={formatMessage({id:'最多XXX张'}).replace('XXX',imgLimit)} filetype="image" formatMessage={formatMessage} getImgList={this.getImgList}/>
                                        </div>
                                    </div>
                                    <p className={`error ${imgFlag && 'active'}`}>{formatMessage({id:'至少上传一张申诉截图'})}</p>
                                </div>
                                <div className="form_box">
                                    <label><FormattedMessage id="申诉视频："/></label>
                                    <div className="upload_img">
                                        <div className="upload-div">
                                            <FileUpload ref={this.vidRef} limit={vidLimit}  msg={formatMessage({id:'最多XXX组'}).replace('XXX',vidLimit)} filetype="video" formatMessage={formatMessage} getVideoList={this.getVideoList}/>
                                        </div>
                                    </div>
                                    {/*<p className={`error ${vidFlag && 'active'}`}>{formatMessage({id:'至少上传一组申诉视频'})}</p>*/}
                                </div>
                            </div>
                            :
                            null
                    }

                    <div className="representations-footer">
                        {
                            complainDetail.checkResult == 0
                                ?
                                <button disabled={complainDetail.commitTimes == 0} className={`btn submit ${complainDetail.commitTimes == 0 && 'btn-disabled'}`} onClick={this.submitComplain}>{complainDetail.firstFlag ? formatMessage({id:'提交申诉'}) : formatMessage({id:'补充申诉（N）'}).replace('N',complainDetail.commitTimes)}</button>
                                :
                                <button className="btn submit" onClick={() =>{this.props.history.go(-1)}}>{formatMessage({id:'返回'})}</button>
                        }
                    </div>
                </div>
                <ReactModal ref={el => this.dialog = el}>
                    {this.state.modalHTML}
                </ReactModal>

                <ReactModal ref={modal => this.modal = modal}   >
                    <UserCenter modal={this.modal}  hoemPage={this.state.homePage} targetId={this.state.targetId} uid={this.state.uid}/>
                </ReactModal>

                <ReactModals ref={el => this.dialogs = el}>
                        {this.state.modalHTML}
                    </ReactModals>

                    <ReactModals ref={modal => this.modals = modal}   >
                        <UserCenter modal={this.modal}  hoemPage={this.state.homePage} targetId={this.state.targetId} uid={this.state.uid}/>
                    </ReactModals>
            </div>
        )
    }
}

export default withRouter(injectIntl(Representations));
