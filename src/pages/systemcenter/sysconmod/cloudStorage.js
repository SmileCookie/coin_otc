import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../conf'
import { Input,Radio,Button,Modal,message} from 'antd'
import { pageLimit} from '../../../utils'
const RadioGroup = Radio.Group;


export default class CloudStorage extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            type:1,
            qiniuDomain:'',
            qiniuPrefix:'',
            qiniuAccessKey:'',
            qiniuSecretKey:'',
            qiniuBucketName:'',
            aliyunDomain:'',
            aliyunPrefix:'',
            aliyunEndPoint:'',
            aliyunAccessKeyId:'',
            aliyunAccessKeySecret:'',
            aliyunBucketName:'',
            qcloudDomain:'',
            qcloudPrefix:'',
            qcloudAppId:'',
            qcloudSecretId:'',
            qcloudSecretKey:'',
            qcloudBucketName:'',
            qcloudRegion:'',
            limitBtn:[]
        }
        this.changeRadio = this.changeRadio.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestDetail = this.requestDetail.bind(this)
        this.onInsert = this.onInsert.bind(this)
    }

    componentDidMount(){
        this.requestDetail()
        this.setState({
            limitBtn: pageLimit('oss',this.props.permissList)
        })

    }
    requestDetail(){
        axios.get(DOMAIN_VIP+'/sys/oss/config').then(res => {
                const result = res.data;
                if(result.code == 0){
                    const{type,qiniuDomain,qiniuPrefix,qiniuAccessKey,qiniuSecretKey,qiniuBucketName,aliyunDomain, aliyunPrefix, aliyunEndPoint, aliyunAccessKeyId, aliyunAccessKeySecret, aliyunBucketName, qcloudDomain,qcloudPrefix, qcloudAppId, qcloudSecretId, qcloudSecretKey, qcloudBucketName,qcloudRegion}= result.config
                    this.setState({
                        type,qiniuDomain,qiniuPrefix,qiniuAccessKey,qiniuSecretKey,qiniuBucketName,aliyunDomain, aliyunPrefix, aliyunEndPoint, aliyunAccessKeyId, aliyunAccessKeySecret, aliyunBucketName, qcloudDomain,qcloudPrefix, qcloudAppId, qcloudSecretId, qcloudSecretKey, qcloudBucketName,qcloudRegion
                    })
                }
            })
    }

    //输入时 input 设置到 satte
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    onInsert(){
        const {type,qiniuDomain,qiniuPrefix,qiniuAccessKey,qiniuSecretKey,qiniuBucketName,aliyunDomain, aliyunPrefix, aliyunEndPoint, aliyunAccessKeyId, aliyunAccessKeySecret, aliyunBucketName, qcloudDomain,qcloudPrefix, qcloudAppId, qcloudSecretId, qcloudSecretKey, qcloudBucketName,qcloudRegion} = this.state
        let aRegex = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?/
        if(type==1&&!aRegex.test(qiniuDomain)){
            message.error("七牛域名格式错误")
            return false
            
        }else if(type==2&&!aRegex.test(aliyunDomain)){
            message.error("阿里云域名格式错误")
            return false
           
        }else if(type==3&&!aRegex.test(qcloudDomain)){
            message.error("腾讯云域名格式错误")
            return false
        }else{
            axios.post(DOMAIN_VIP+'/sys/oss/saveConfig',qs.stringify({
                type,qiniuDomain,qiniuPrefix,qiniuAccessKey,qiniuSecretKey,qiniuBucketName,aliyunDomain, aliyunPrefix, aliyunEndPoint, aliyunAccessKeyId, aliyunAccessKeySecret, aliyunBucketName, qcloudDomain,qcloudPrefix, qcloudAppId, qcloudSecretId, qcloudSecretKey, qcloudBucketName,qcloudRegion
             })).then(res => {
                const result = res.data;
                if(result.code == 0){
                  message.success(result.msg)
                  this.requestDetail()
                }else{
                    message.warning(result.msg)
                }
            })
        }
       
        // Regex 
        //  .regex^(?=^.{3,255}$)(http(s)?:\/\/)?(www\.)?[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+(:\d+)*(\/\w+\.\w+)*$)
        
    }

    changeRadio(e){
        const val = e.target.value
        this.setState((prevState) => {
        return {
                type: val,
            }
        });

    }

 
    
    render(){
        const {type,qiniuDomain,qiniuPrefix,qiniuAccessKey,qiniuSecretKey,qiniuBucketName,aliyunDomain, aliyunPrefix, aliyunEndPoint, aliyunAccessKeyId, aliyunAccessKeySecret, aliyunBucketName, qcloudDomain,qcloudPrefix, qcloudAppId, qcloudSecretId, qcloudSecretKey, qcloudBucketName,qcloudRegion,limitBtn} = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12" style={{ marginTop: '40px' }}>
            {/*<div className="page-title marl20">*/}
                    {/*当前位置：系统中心 > 配置中心 > 云存储配置*/}
                {/*</div>*/}
            <div className="x_panel">
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="form-group">
                    <label className="col-sm-4 control-label">存储类型：</label>
                    <div className="col-sm-8">
                        <RadioGroup onChange={this.changeRadio} value={type}>
                            <Radio value={1}>七牛</Radio>
                            <Radio value={2}>阿里云</Radio>
                            <Radio value={3}>腾讯云</Radio>
                        </RadioGroup>
                    </div>
                </div>
                {type=='1'&&<div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">域名：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qiniuDomain" value={qiniuDomain||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">路径前缀：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qiniuPrefix" value={qiniuPrefix||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">ACCESS_KEY：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qiniuAccessKey" value={qiniuAccessKey||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">SECRET_KEY：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qiniuSecretKey" value={qiniuSecretKey||''}  onChange={this.handleInputChange}  />
                        
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">存储空间名：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qiniuBucketName" value={qiniuBucketName||''}  onChange={this.handleInputChange}  />
                        
                    </div>
                </div>
                </div>
                }
                {type=='2'&&<div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">域名：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="aliyunDomain" value={aliyunDomain||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">路径前缀：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="aliyunPrefix" value={aliyunPrefix||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">EndPoint：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="aliyunEndPoint" value={aliyunEndPoint||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">AccessKeyId：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="aliyunAccessKeyId" value={aliyunAccessKeyId||''}  onChange={this.handleInputChange}  />
                        
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">AccessKeySecret：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="aliyunAccessKeySecret" value={aliyunAccessKeySecret||''}  onChange={this.handleInputChange}  />
                        
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">BucketName：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="aliyunBucketName" value={aliyunBucketName||''}  onChange={this.handleInputChange}  />
                        
                    </div>
                </div>
                </div>
                }
                {type=='3'&&<div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">域名：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qcloudDomain" value={qcloudDomain||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">路径前缀：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qcloudPrefix" value={qcloudPrefix||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">AppId：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qcloudAppId" value={qcloudAppId||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">SecretId：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qcloudSecretId" value={qcloudSecretId||''}  onChange={this.handleInputChange} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">SecretKey：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qcloudSecretKey" value={qcloudSecretKey||''}  onChange={this.handleInputChange}/>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">BucketName：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qcloudBucketName" value={qcloudBucketName||''}  onChange={this.handleInputChange}/>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-4 control-label">COS所属地区：</label>
                    <div className="col-sm-8">
                        <input type="text" className="form-control" name="qcloudRegion" value={qcloudRegion||''}  onChange={this.handleInputChange}/>
                    </div>
                </div>
                </div>
                }
                <div className="col-md-6 col-sm-6 col-xs-6 right">
                    {limitBtn.indexOf('save')>-1?<Button type="primary" onClick={this.onInsert}>确认</Button>:''}
                 </div>
            </div>  
            </div>
            </div>
        )
    }
}