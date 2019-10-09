import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP,SELECTWIDTH } from '../../../../conf'
import { Input,Modal,Button,Select,Radio,Icon,Upload,message } from 'antd'
import { MarketConfigInput } from '../../../common/components'
const Option = Select.Option
const RadioGroup = Radio.Group;

export default class ModalAddCurrencyOTC extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            coinType: '',
            coinName:'' ,
            coinFullName: '',
            coinTag: '',
            coinFees: '',
            coinCanWithdraw: '',
            coinCanCharge: '',
            coinConfirmTimes: '',
            coinMinWithdraw: '',
            coinBixDian: '',
            coinWithdrawTimes: '',
            coinDayCash: '',
            coinMaxWithdraw: '',
            autoDownloadLimit: '',
            minimumCharge: '',
            coinUrl: '',
            token:'',
            selectList:[<Option value={0} key={0}>请选择</Option>]

        }
        
        this.handleInputChange= this.handleInputChange.bind(this)
        this.fileChange = this.fileChange.bind(this)
        this.consoData = this.consoData.bind(this)
        this.handleSelectcoinFull = this.handleSelectcoinFull.bind(this)
        this.requestSelect = this.requestSelect.bind(this)
    
    }
    componentDidMount(){
         this.requestSelect()
        const {coinType,coinName,coinFullName,coinTag,coinCanWithdraw,coinFees,coinCanCharge,
            coinConfirmTimes,coinMaxWithdraw,coinBixDian,coinWithdrawTimes,coinDayCash,coinMinWithdraw,coinUrl,
            autoDownloadLimit,minimumCharge,fundsType} = this.props.item
        this.setState({
            coinType:coinType||1,
            coinName:coinName||0,
            coinFullName:coinFullName||'',
            coinTag: coinTag||'',
            coinCanWithdraw: coinCanWithdraw||0,
            coinFees: coinFees||'',
            coinCanCharge: coinCanCharge||0,
            coinConfirmTimes: coinConfirmTimes||'',
            coinMinWithdraw: coinMinWithdraw||'',
            coinBixDian: coinBixDian||'',
            coinWithdrawTimes: coinWithdrawTimes||'',
            coinDayCash: coinDayCash||'',
            coinMaxWithdraw: coinMaxWithdraw||'',
            autoDownloadLimit: autoDownloadLimit||'',
            minimumCharge: minimumCharge||'',
            coinUrl: coinUrl || '',
            fundsType: fundsType || 0
        })
    }
    componentWillReceiveProps(nextProps){
        const {coinType,coinName,coinFullName,coinTag,coinCanWithdraw,coinFees,coinCanCharge,
            coinConfirmTimes,coinMaxWithdraw,coinBixDian,coinWithdrawTimes,coinDayCash,coinMinWithdraw,coinUrl,
            autoDownloadLimit,minimumCharge,fundsType} = nextProps.item
        this.setState({
            coinType:coinType||1,
            coinName:coinName||0,
            coinFullName:coinFullName||'',
            coinTag: coinTag||'',
            coinCanWithdraw: coinCanWithdraw||0,
            coinFees: coinFees||'',
            coinCanCharge: coinCanCharge||0,
            coinConfirmTimes: coinConfirmTimes||'',
            coinMinWithdraw: coinMinWithdraw||'',
            coinBixDian: coinBixDian||'',
            coinWithdrawTimes: coinWithdrawTimes||'',
            coinDayCash: coinDayCash||'',
            coinMaxWithdraw: coinMaxWithdraw||'',
            autoDownloadLimit: autoDownloadLimit||'',
            minimumCharge: minimumCharge||'',
            coinUrl : coinUrl || '',
            fundsType: fundsType || 0
        })
    }
    //输入时 input 设置到 satte
    handleInputChange(event){
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event)
    }
    handleSelectcoinFull(val){
        const { selectList, selectListOrigin } = this.state
        let arr = selectListOrigin.filter((item,index)=>{
            if(item.fundsType==val){
                return item
            }
        })
        let obj = arr[0]
        let stateObj = {
            coinFullName: obj.propEnName,
            fundsType: obj.fundsType,
            coinName: obj.propTag,
            coinTag: obj.unitTag,
            coinUrl: obj.imgUrl
        }
        this.setState(stateObj)
        this.props.handleSelectChange(stateObj)
    }
     fileChange(info){
        // console.log(info)
        if (info.file.status !== 'uploading') {
            console.log(info.file, info.fileList);
          }
          if (info.file.response.code == '0') {
            message.success(`${info.file.response.msg}`);
            this.setState({
                coinUrl:info.file.response.data
            })
            this.props.setcoinUrl(info.file.response.data)
          } else{
            message.error(`${info.file.response.msg}`);
          }
     }
    consoData(object){
         var formdata = new FormData();
         formdata.append('mf', object);
         return formdata;
    }
    requestSelect(){
        const { selectList } = this.state
        axios.get(DOMAIN_VIP+'/otcCointype/queryAttr')
        .then(res =>{
            const result = res.data
            for(let i in result.data){
                selectList.push(<Option value={result.data[i].fundsType} key={result.data[i].fundsType}>{result.data[i].propTag}</Option>)
            }
            this.setState({
                selectList,
                selectListOrigin: result.data
            })
        })
    }
    render(){
        const {coinType,coinName,coinFullName,coinTag,coinCanWithdraw,coinFees,coinCanCharge,
            coinConfirmTimes,coinMaxWithdraw,coinBixDian,coinWithdrawTimes,coinDayCash,coinMinWithdraw,coinUrl,
            autoDownloadLimit,minimumCharge,selectList,fundsType }= this.state
            const headerTxt ={'Content-Type':'multipart/form-data'}
        return(
            <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label"> 币种类型:<i>*</i></label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.handleInputChange} name="coinType" value={coinType}>
                                <Radio value={1}>虚拟货币</Radio>
                                {/*<Radio value={2}>法币</Radio>*/}
                            </RadioGroup>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">币种姓名:<i>*</i></label>
                        <div className="col-sm-8">
                            <Select showSearch  value={coinName} filterOption={(input,option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0} style={{width:SELECTWIDTH}} onChange={(val)=>this.handleSelectcoinFull(val)}>
                                {selectList}
                            </Select>
                        </div>
                    </div>
                </div>
                <MarketConfigInput content="币种全称:<i>*</i>" inputType="text" name="coinFullName" value={coinFullName || ''} disabled />
                <MarketConfigInput content="币种单位:<i>*</i>" inputType="text" name="coinTag" value={coinTag || ''} disabled />
                <MarketConfigInput content="图标地址:<i>*</i>" inputType="text" name="coinUrl" value={coinUrl || ''} disabled />
                
                {/*
                    // 2019.07.03 要求注释掉下面输入项
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">提币网络手续费率:<i>*</i></label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control"  name="coinFees" value={coinFees||''} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div> 
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">是否支持提现:<i>*</i></label>
                        <div className="col-sm-8">
                            <RadioGroup onChange={this.handleInputChange} name="coinCanWithdraw" value={coinCanWithdraw}>
                                <Radio value={1}>是</Radio>
                                <Radio value={0}>否</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">是否支持充值:<i>*</i></label>
                         <div className="col-sm-8">
                             <RadioGroup onChange={this.handleInputChange} name="coinCanCharge" value={coinCanCharge}>
                                <Radio value={1}>是</Radio>
                                <Radio value={0}>否</Radio>
                            </RadioGroup>
                         </div>
                     </div>
                </div> 
                <div className="col-md-6 col-sm-6 col-xs-6">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">充值到账确认次数:<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="coinConfirmTimes" value={coinConfirmTimes||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                </div> 
                <div className="col-md-6 col-sm-6 col-xs-6">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">最小提现额度:<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="coinMinWithdraw" value={coinMinWithdraw||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                </div> 
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">图标地址:</label>
                        <div className="col-sm-5">
                            <input type="url" className="form-control"  name="coinUrl" value={coinUrl||''} onChange={this.handleInputChange}/>
                        </div>
                        <div className="col-sm-3">
                            <Upload name='mf' showUploadList={false} accept='.png, .jpg, .jpeg' data={this.consoData} action='common/upload' onChange={this.fileChange} listType='picture' className='upload-list-inline'>
                                <Button>
                                <Icon type="upload" /> 上传图片
                                </Button>
                            </Upload>
                        </div>
                    </div>
                </div> 
                <div className="col-md-6 col-sm-6 col-xs-6">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">小数点位数:<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="coinBixDian" value={coinBixDian||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                </div> 
                <div className="col-md-6 col-sm-6 col-xs-6">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">提现到账确认次数:<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="coinWithdrawTimes" value={coinWithdrawTimes||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                </div> 
                <div className="col-md-6 col-sm-6 col-xs-6">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">每日每个账号上限:<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="coinDayCash" value={coinDayCash||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">最大提现额度:<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="coinMaxWithdraw" value={coinMaxWithdraw||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">自动打币限额:<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="autoDownloadLimit" value={autoDownloadLimit||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                   <div className="form-group">
                         <label className="col-sm-3 control-label">最低收费:<i>*</i></label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="minimumCharge" value={minimumCharge||''} onChange={this.handleInputChange} />
                         </div>
                     </div>
                </div>  
                */}
            </div>
        )
    }
}