import React from 'react'
import moment from 'moment'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import ModalAuthena from './modalAuthena'
import { TIMEFORMAT ,DOMAIN_VIP,PAGEINDEX,PAGESIZE} from '../../../../conf'
import { Modal,Radio,Select ,Button,message,Card} from 'antd'
import {dataURItoBlob} from '../../../../utils/index'
import { Player } from 'video-react'
const ButtonGroup = Button.Group;
const RadioGroup = Radio.Group;
const Option = Select.Option;

export default class ModalAuthen extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            userId:'',
            value:'1',
            resVal:"",
            visible:false,
            previewImage:"",
            userName:"",
            authTimes:"",
            areaName:"",
            cardTypeName:"",
            cardId:"",
            realName:"",
            submitTime:"",
            statusName:"",
            loadImg:"",
            frontalImg:"",
            backImg:"",
            endDate:"",
            startDate:"",
            countryName:"",
            isok:'0',
            tableList:[],
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            idCardPhoto:'',
            imgWidth:1000,
            movisible:false,
            mowidth:'',
            modalHtml:'',
            reason:'',
            motitle:'',
            cardType:'',
            tencent_host:"https://idopy-1253901570.cos.ap-beijing.myqcloud.com/",
            token:'',
            loading:true,
            readRandom: '',//随机数
            videoImg: null,//视频

        }
        this.onChangeRadio = this.onChangeRadio.bind(this)  
        this.onImgEnlarge = this.onImgEnlarge.bind(this)
        this.handleChange = this.handleChange.bind(this)  
        this.handleCancel = this.handleCancel.bind(this)
        this.handleImgCancel = this.handleImgCancel.bind(this)
        this.validityCheck = this.validityCheck.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.imgSize = this.imgSize.bind(this)
        this.mohandleCancel = this.mohandleCancel.bind(this)
        this.pubt64 = this.pubt64.bind(this)
        this.onAuditInfoto= this.onAuditInfoto.bind(this)
    }
    componentDidMount(){
        console.log(this.props)
        const { userId,userName,authTimes,countryName,startDate,endDate,areaName,cardTypeName,cardId,realName,submitTime,statusName,loadImg,frontalImg,backImg,cardType,readRandom, videoImg } = this.props.item
        this.setState({
            userId,
            userName,
            authTimes,
            areaName,
            cardTypeName,
            cardId,
            realName,
            submitTime,
            statusName,
            loadImg,
            frontalImg,
            backImg,
            endDate,
            startDate,
            countryName,
            cardType,
            value:'1',
            resVal:"",
            isok:'0',
            idCardPhoto:'',
            readRandom, videoImg
        },()=> {
            this.requestTable()
        })        
    }
    componentWillReceiveProps(nextProps){
        //console.log(nextProps.tab)
        //console.log(nextProps)
        const { userId,userName,authTimes,countryName,areaName,startDate,endDate,cardTypeName,cardId,realName,submitTime,statusName,loadImg,frontalImg,backImg,cardType,readRandom, videoImg } = nextProps.item        
        this.setState({
            userId,
            userName,
            authTimes,
            areaName,
            cardTypeName,
            cardId,
            realName,
            submitTime,
            statusName,
            loadImg,
            frontalImg,
            backImg,
            startDate,
            endDate,
            countryName,
            cardType,
            value:'1',
            resVal:"",
            isok:'0',
            idCardPhoto:'',
            readRandom, videoImg
        },()=> {
            this.requestTable()
        })  
    }
    requestTable(currIndex,currSize){
        const { userId,pageIndex,pageSize} = this.state;
        //console.log(userId)
        axios.post(DOMAIN_VIP+'/authentication/auHistory',qs.stringify({
            userId,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.props.setReason(result.page.list)
                this.setState({
                    pageTotal:result.page.totalCount,
                    tableList:result.page.list
                })
            }
        })
        axios.post(DOMAIN_VIP +"/news/getTencentToken").then(res => {
            const result = res.data
            if (result.code == 0){
                this.setState({
                    url:result.data.host,
                    key:result.data.key,
                    token: result.data.token,
                    loading:false
                })
            }
        })
    }
    //图片放大缩小
    imgSize(type){
        const{imgWidth}= this.state
        //console.log(type)
        if(type==1){
                if(imgWidth == 1600){
                    message.warning('已放至最大')
            }else{
                this.setState({
                    imgWidth:imgWidth+100 
                })
            }
        }else{
            if(imgWidth == 300){
                message.warning('已缩放至最小')
                
            }else{
                this.setState({
                    imgWidth:imgWidth-100 
                })
            }
        }
    }
    //有效性检查
    validityCheck(){
        const { realName,cardId } = this.state
                    axios.get(DOMAIN_VIP+'/common/checkAvaliable',{
                        params:{
                            cardNo:cardId,realName
                        }
                    }).then(res => {
                        const result = res.data;
                        //console.log(result)
                        if(result.code == 0){
                        if(result.data.res=='1'){
                            this.setState({
                                isok:'1',
                                idCardPhoto:result.data.photo
                            // },()=>this.pubt64())
                            },()=>this.props.setImage(result.data.description))
                            console.log("查询成功")
                            message.success(result.msg);
                            }else{
                                this.setState({
                                    isok:'2'
                                })
                                console.log("查询失败")
                                message.error(result.data)
                            }  
                        }else{
                            message.warning(result.msg);
                        }
                        
                    })
        
    }
    pubt64(){
        //图片的base64位
        // const{token,idCardPhoto}=this.state
        // if(token&&idCardPhoto){
        //     axios({
        //         method: 'post',
        //         url:"http://up.qiniu.com/putb64/-1",
        //         data:idCardPhoto,
        //         headers: {'Content-Type': 'application/octet-stream','Authorization':"UpToken "+token}
        //     }) .then(res =>{
        //         let result = res.data
        //         if(result.hash){
        //             this.props.setImage(result.hash)
        //         }
        //   });
        // }
        const{idCardPhoto}=this.state
        var blob = dataURItoBlob("data:image/png;base64,"+idCardPhoto); // 上一步中的函数
        var canvas = document.createElement('canvas');
        var dataURL = canvas.toDataURL('image/jpeg', 0.5);
        var fd = new FormData(document.forms[0]);
        fd.append("mf", blob, 'image.png');
            axios({
                method: 'post',
                url:DOMAIN_VIP+'/common/upload',
                data:fd,
                headers:{'Content-Type':'multipart/form-data'}
            }) .then(res =>{
                let result = res.data
                if(result.data){
                    this.props.setImage(result.data)
                }
          });
        
    }
    onChangeRadio(e){
        const{authTimes}= this.state
        this.setState({
            value: e.target.value,
        });
            this.props.onChangeRadio(e.target.value)
    }
    handleChange(value){
        this.setState({
            resVal:value
        },()=>this.props.onChangeReason(this.state.resVal))
        
    }
    //图片放大
    onImgEnlarge(src){
        this.setState({
            imgWidth:1000,
            visible:true,
            previewImage:src
        })
    }
    mohandleCancel(){
        this.setState({
            movisible: false ,
            modalHtml:''
        })
    }
    handleCancel(){
        this.setState({
            visible: false ,
            loadImg:'',
            frontalImg:''
        })
    }
    handleImgCancel(){
        this.setState({ 
            visible: false
        })
    }
    //查看历史记录
    onAuditInfoto(item){
        this.footert = [
            <Button key="back" onClick={this.mohandleCancel}>取消</Button>
        ]
        this.setState({
            movisible:true,
            motitle:'历史记录',
            mowidth:'1000px',
            modalHtml:<ModalAuthena tab={7} item={item} degree={2}/>
            
        })
    }

    render(){
        const { userName,authTimes,tableList,imgWidth,resVal,isok,ip,idCardPhoto,loading,cardTypeName,cardId,startDate,endDate,countryName,realName,submitTime,statusName,loadImg,frontalImg,pageIndex,pageSize,backImg, movisible,mowidth,modalHtml,motitle ,cardType,
            readRandom,videoImg} = this.state
       const ipCon = this.props.item.ip
       
        // let uploadUrl;
        // if (window.location.protocol === 'https:') {
        //     uploadUrl = 'https://up.qbox.me';
        // } else {
        //     uploadUrl = 'http://up.qiniu.com';
        // }
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
             <Card>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">用户名：</label>
                        <div className="col-sm-9 lineHeight_34">
                            {userName||""}
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">认证次数：</label>
                        <div className="col-sm-9 lineHeight_34">
                            {authTimes}
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">IP：</label>
                        <div className="col-sm-9 lineHeight_34">
                            {ipCon}
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">认证国家：</label>
                        <div className="col-sm-9 lineHeight_34">
                            {countryName}
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">认证类型：</label>
                        <div className="col-sm-9 lineHeight_34">
                            {cardTypeName}
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">证件号码：</label>
                        <div className="col-sm-9">
                        <div className="lineHeight_34">
                           {cardId}
                        </div>
                        {this.props.tab ==5&&cardType==1&&
                        <div className="col-sm-2 left">
                        <Button type="primary" onClick={this.validityCheck} loading={loading}>有效性检查</Button>
                        </div>}
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">真实姓名：</label>
                        <div className="col-sm-9 lineHeight_34">
                            {realName}
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">认证时间：</label>
                        <div className="col-sm-9 lineHeight_34">
                            {submitTime?moment(submitTime).format(TIMEFORMAT):''}
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">证件有效开始：</label>
                        <div className="col-sm-9 lineHeight_34">
                            {startDate}
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">证件有效截止：</label>
                        <div className="col-sm-9 lineHeight_34">
                            {endDate}
                        </div>
                    </div>
                </div>
                {this.props.tab ==5&&
                <div className="col-md-6 col-sm-6 col-xs-6 color_128fdc">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">检查结果：</label>
                        <div className="col-sm-9 lineHeight_34">
                          {isok=="0"?"":isok=="1"?"证件号匹配":"证件号不匹配"}
                        </div>
                    </div>
                </div>
                }</Card>
                
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-md-12 col-sm-12 col-xs-12 control-label" style={{ width: '100%' }}>用户上传图片：</label>
                        <div className="col-md-3 col-sm-3 col-xs-3">
                            <img alt="example" style={{ width: '100%',height:'150px' }} src={frontalImg} onClick={() => this.onImgEnlarge(frontalImg)} />
                        </div>
                        {cardType=='1'?
                        <div className="col-md-3 col-sm-3 col-xs-3">
                            <img alt="example" style={{ width: '100%',height:'150px' }} src={backImg} onClick={() => this.onImgEnlarge(backImg)} />
                        </div>:''
                        }
                       
                        <div className="col-md-3 col-sm-3 col-xs-3">
                            <img alt="example" style={{ width: '100%',height:'150px' }} src={loadImg} onClick={() => this.onImgEnlarge(loadImg)} />
                        </div>
                        {
                        //     idCardPhoto ? <div className="col-md-3 col-sm-3 col-xs-3">
                        //     <img alt="example" style={{ width: '100%',height:'150px' }} src={"data:image/png;base64,"+idCardPhoto} onClick={() => this.onImgEnlarge("data:image/png;base64,"+idCardPhoto)} />
                        // </div>:''
                        }
                       
                       
                    </div>
                </div>
                {/* 
                    视频播放
                */}
                {/* <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-md-12 col-sm-12 col-xs-12 control-label" style={{ width: '100%' }}>用户上传视频：</label>
                        <div className='col-md-4 col-sm-4 col-xs-4'>
                            <Player
                            fluid={false}
                                src={videoImg}
                                playsInline={true}
                                aspectRatio='auto'
                                width={360}
                                height={170}
                            />
                        </div>
                    </div>
                </div> */}
                {this.props.tab ==5&&<div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">审核：</label>
                        <div className="col-sm-9">
                             <RadioGroup onChange={this.onChangeRadio} value={this.state.value}>
                                <Radio className='green' value={'1'}>通过</Radio>
                                <Radio className='red' value={'2'}>不通过</Radio>
                             </RadioGroup>
                        </div>
                    </div>
                </div>}
                {this.props.tab ==5&&this.state.value==2&&<div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">原因：</label>
                        <div className="col-sm-9 text-box">
                            <Select value={resVal}  onChange={this.handleChange} style={{width:'300px'}}>
                                <Option value="">请选择不通过的原因</Option>
                                <Option value="8">1、图像经过处理</Option>
                                <Option value="9">2、图像不清晰</Option>
                                <Option value="10">3、证件图像类型不符</Option> 
                                <Option value="11">4、平台仅支持满16周岁的用户进行交易</Option>                                
                                {/* <Option value="12">5、证件号码与真实姓名不匹配</Option>                                 */}
                            </Select>
                        </div>
                    </div>
                </div>}
                {
                    this.props.tab !=5&&<div className="col-md-6 col-sm-6 col-xs-6">
                                            <div className="form-group">
                                                <label className="col-sm-3 control-label">审核状态：</label>
                                                <div className="col-sm-9">
                                                    <input type="text" className="form-control"  value={statusName} readOnly />
                                                </div>
                                            </div>
                                        </div>
                }

                           {this.props.tab ==5&& <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                                        <thead>
                                            <tr className="headings">
                                                <th className="column-title">序号</th>
                                                <th className="column-title">申请时间</th>
                                                <th className="column-title">审核时间</th>
                                                <th className="column-title">审核人</th>
                                                <th className="column-title">IP</th>
                                                <th className="column-title">证件类型</th>  
                                                <th className="column-title">身份证/护照</th>
                                                <th className="column-title">状态</th>
                                                <th className="column-title">操作</th>                       
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                tableList.length>1?
                                                tableList.map((item,index)=>{
                                                    return (
                                                        index == 0?'':<tr key={index}>
                                                            <td>{(pageIndex-1)*pageSize+index}</td>
                                                            <td>{moment(item.submitTime).format('YYYY-MM-DD HH:mm:ss')}</td>
                                                            <td>{item.checkTime?moment(item.checkTime).format('YYYY-MM-DD HH:mm:ss'):''}</td>
                                                            <td>{item.adminUserName}</td>
                                                            <td>{item.areaName}</td>
                                                            <td>{item.cardTypeName}</td>
                                                            <td>{item.areaName}</td>
                                                            <td>{item.statusName}</td>
                                                            <td>
                                                                <a href="javascript:void(0)" onClick={() => this.onAuditInfoto(item)}>查看</a>                                                  
                                                            </td>
                                                        </tr>
                                                    )
                                                })
                                                :<tr className="no-record"><td colSpan="9">暂无数据</td></tr>
                                            }
                                        </tbody>
                                    </table>
                                </div>}

                <Modal visible={this.state.visible} footer={
                <ButtonGroup>
                <Button icon="plus" onClick={()=>this.imgSize(1)}></Button>
                <Button icon="minus" onClick={()=>this.imgSize(2)}></Button>
              </ButtonGroup>
                } wrapClassName="img-box" width={imgWidth+'px'} onCancel={this.handleImgCancel} maskClosable={false}>
                    <img alt="example" style={{ width: '100%' }} src={this.state.previewImage} />
                </Modal>
                <Modal
                    visible={movisible}
                    width={mowidth}
                    title={motitle}
                    maskClosable={true}
                    onCancel={this.mohandleCancel}
                    footer={this.footert}
                    >
                    {modalHtml}            
                </Modal>
            </div>
        )
    }

}