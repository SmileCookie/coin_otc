import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,} from '../../../conf'
import { Button,Upload,Icon,message } from 'antd'
import CurrencyTextRelease from './currencyTextRelease'

export default class DistributeCurrency extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            coinFullNameJson:'',//币种全称
            coinNameJson:'',//币种名称
            coinContentJson:'',//内容
            introductionJson:'',//简介
            //coinImg:'http://up.qiniu.com',
            fileList: [],
            qiniu_host:"https://o4we6sxpt.qnssl.com/",
            coinUrl:'',
            showHide:true,
        }
        this.show_click = this.show_click.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.handlePreview = this.handlePreview.bind(this)
        // this.getQiniuToken = this.getQiniuToken.bind(this)
        this.clickHide = this.clickHide.bind(this)
        this.uploadImageCos = this.uploadImageCos.bind(this)
        this.getAuthorization = this.getAuthorization.bind(this)
    }
    componentDidMount(){
        // console.log(this.props)
        //this.getQiniuToken()
        if(this.props.activityId){
            const {coinFullNameJson,coinNameJson,coinContentJson,introductionJson,img} = this.props.activityId
            const {fileList} = this.state
            //添加币种图片
            // fileList[0].url = img || true
            this.setState({
                coinFullNameJson:coinFullNameJson||'',
                coinNameJson:coinNameJson||'',
                coinContentJson:coinContentJson||'',
                introductionJson:introductionJson||'',
                fileList,
                // coinUrl:img
            })
        }
        

    }
    componentWillReceiveProps(nextProps){
        if(nextProps.activityId){
           
            const {coinFullNameJson,coinNameJson,coinContentJson,introductionJson,img} = nextProps.activityId
            const {fileList} = this.state
            // fileList[0].url = img
            this.setState({
                coinFullNameJson:coinFullNameJson||'',
                coinNameJson:coinNameJson||'',
                coinContentJson:coinContentJson||'',
                introductionJson:introductionJson||'',
                fileList
            })
        }
        
    }
    //修改状态
    show_click(index) {
        this.props.showHideClick(index);
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }
    // //获取七牛的 token
    // getQiniuToken(){
    //     axios.post(DOMAIN_VIP +"/news/getQiNiuToken").then(res => {
    //         const result = res.data
    //         if (result.code == 0){
    //             console.log(result.token)
    //             this.setState({
    //                 token: result.token
    //             })
    //         }
    //     })
    // }
    //限制上传图片大小
    limitUpImgSize = (size) => {
        // let isLt300k = size/1024 < 300;
        return size / 1024 < 300;
    }
    //上传 腾讯云
    getAuthorization(callback){
        axios.post(DOMAIN_VIP+"/news/getTencentToken").then(res => {
            const result = res.data
           // console.log(result)
            this.setState({
                coinUrl:result.data.host+result.data.key
            })
            callback({
                url:result.data.host,
                key:result.data.key,
                XCosSecurityToken: result.data.token
            });
        })
    }
    uploadImageCos(file){
        if (!this.limitUpImgSize(file.size)) {
            message.warning('上传图片大小不能超过300k！')
            return false
        }
        this.getAuthorization((info) => {
           // console.log(info,file)
            file.status ='done'
            let fd = new FormData();
            fd.append('key', info.key);
            fd.append('Signature', info.XCosSecurityToken);
            fd.append('Content-Type', '');
            fd.append('file',file);
            // let config = {
            //     headers: {'Content-Type': 'multipart/form-data'}
            //   }
              const xmlhttp = new XMLHttpRequest();
              xmlhttp.open('post', info.url, true);
              xmlhttp.send(fd);
              this.setState({
                  tencent_host:info.url,
                  tencent_key:info.key,
                  coinUrl:info.url+info.key
              },()=>console.log(this.state))
        })
    }
    //图片上传预览
    handlePreview(file){
        this.setState({
            uploadUrl:file.url||file.thumbUrl,
            previewVisible:true,
        })
    }
    //图片上传状态
    handleChange({fileList}){
        console.log(fileList)
        const size = fileList.length
        if(!this.limitUpImgSize(fileList[size-1].size)) return false;
        // fileList.map((item,index)=>{
        //     fileList[index].status = 'done'
        // })
        fileList[size-1].status = 'done'
        this.setState({ fileList })
    }
    render(){
        const {fileList,token,showHide} = this.state
        const uploadButton = (
            <div>
                <Icon type="plus" />
                <div className="ant-upload-text">Upload</div>
            </div>
        );
        // let uploadUrl;
        // if (window.location.protocol === 'https:') {
        //     uploadUrl = 'https://up.qbox.me';
        // } else {
        //     uploadUrl = 'http://up.qiniu.com';
        // }
        let imgName = new Date().getTime()
        imgName = "bitglobal/newsupload/" + imgName;
        let nfileList = fileList.slice(-1)
        // const uploadBannerProps = {
        //     listType:"picture-card",
        //     className:"avatar-uploader",
        //     action:coinImg,
        //     fileList:fileList,
        //     onChange:this.handleChange,
        //     onPreview:this.handlePreview,
        //     //multiple:true,
        // }
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 活动管理 > 币种新增
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div className="x_panel">
                                <div className="col-md-3 col-sm-3 col-xs-3 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => { this.show_click(0) }} >返回上一级</Button>
                                    </div>
                                </div>
                        </div>}
                        <div className="x_panel">
                            {this.props.showHideImg == 1&&<div className="form-group">
                                <label className="col-sm-3 control-label">币种图片：<i>*</i></label>
                                <div className="col-sm-5">
                                    <Upload 
                                        //action={uploadUrl}
                                        listType="picture-card"
                                        fileList={nfileList}
                                        onPreview={this.handlePreview}
                                        onChange={this.handleChange}
                                        //data={{ token: token,key: imgName}}
                                        showUploadList={{
                                            showPreviewIcon:true,
                                            showRemoveIcon:false
                                        }}                                          
                                        customRequest={(e) => {
                                            //console.log(e)
                                            //this.handlePreview(e.file)
                                            this.uploadImageCos(e.file)
                                        }}
                                    >    
                                        {uploadButton}                                    
                                    </Upload>
                                </div>
                            </div>}     
                            <div className="col-md-12 col-sm-12 col-xs-12">
                                <CurrencyTextRelease show_click={this.show_click} {...this.props} {...this.state} show_click={this.show_click}/>
                            </div>                                                  
                        </div>
                    </div>                    
                </div>
            </div>
        )
    }
}