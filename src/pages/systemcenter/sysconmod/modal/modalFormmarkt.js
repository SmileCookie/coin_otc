import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import ModalChangeMarket from './modalChangeMarket'
import { DOMAIN_VIP } from '../../../../conf'
import { Input,Modal,Button ,Radio,message} from 'antd'
const RadioGroup = Radio.Group;
export default class ModalFormmarkt extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            key:'',
            momarket:'',
            allmarket:'',
            adBuyFee:'',
            adSellFee:'',
            adValidTime:'',
            adOrderNumMax:'',
            transInvalidTime:'',
            userAdNumMax:'',
            userOrderNumMax:'',
            userCancleNum:'',
            coinBixDian:'',
            legalBixDian:'',
            buyMaxNum:'',
            buyMinNum:'',
            feeRate:'',
            marketType:'',
            market:'',
            sellMaxNum:'',
            sellMinNum:'',
            enable:'', 

            moallmarket:'',
            moadBuyFee:'',
            moadSellFee:'',
            moadValidTime:'',
            moadOrderNumMax:'',
            motransInvalidTime:'',
            mouserAdNumMax:'',
            mouserOrderNumMax:'',
            mouserCancleNum:'',
            mocoinBixDian:'',
            molegalBixDian:'',
            mobuyMaxNum:'',
            mobuyMinNum:'',
            mofeeRate:'',
            momarketType:'',
            momarket:'',
            mosellMaxNum:'',
            mosellMinNum:'',
            moenable:'', 
            remark:'',
            id:'',
            disable:true,
            btndisabl:true,
            visible:false,
            modalHtml:'',
            width:'',
            title:'',
            orderOverTime:'',
            limitBtn:[],
            loading:false
        }
        this.handleInputChange= this.handleInputChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.onChange = this.onChange.bind(this)
        this.onSaveModify = this.onSaveModify.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onInsert = this.onInsert.bind(this)
        this.deleteItem = this.deleteItem.bind(this)
    }
    componentDidMount(){
        const {id,momarket,allmarket,limitBtn} = this.props
        this.setState({
            id,
            momarket,
            allmarket,
            limitBtn
        },()=>this.requestTable())
    }
    componentWillReceiveProps(nextProps){
        const {id,momarket,allmarket,limitBtn} = nextProps
        this.setState({
            id,
            momarket,
            allmarket,
            limitBtn
        },()=>this.requestTable())
    }
    requestTable(){
        const {momarket} = this.state
        axios.get(DOMAIN_VIP+'/sys/market/config', { params: {
        market:momarket,
        }}).then(res => {
            const result = res.data;
            if(result.code == 0){
                const {adBuyFee,orderOverTime,adSellFee,adValidTime,marketType,adOrderNumMax,transInvalidTime,userAdNumMax,userOrderNumMax,userCancleNum,coinBixDian,legalBixDian,buyMaxNum,buyMinNum,feeRate,market,sellMaxNum,sellMinNum,enable}= result.config
                this.setState({
                    adBuyFee,
                    adSellFee,
                    adValidTime,
                    adOrderNumMax,
                    transInvalidTime,
                    userAdNumMax,
                    userOrderNumMax,
                    userCancleNum,
                    coinBixDian,
                    legalBixDian,
                    buyMaxNum,
                    buyMinNum,
                    feeRate,
                    marketType,
                    market,
                    sellMaxNum,
                    sellMinNum,
                    enable,
                    orderOverTime
                })
            }   
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
    }
    onChange(){
       const {allmarket,orderOverTime,adBuyFee,adSellFee,adValidTime,adOrderNumMax,marketType,transInvalidTime,userAdNumMax,userOrderNumMax,userCancleNum,coinBixDian,legalBixDian,buyMaxNum,buyMinNum,feeRate,market,sellMaxNum,sellMinNum,enable} = this.state
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.onSaveModify('mod')}>
                保存修改
            </Button>,
        ]
        this.setState({
            visible:true,
            title:'修改',
            width:'1200px',
            modalHtml:<ModalChangeMarket {...this.state} handleInputChange={this.handleInputChange}/>,
            moallmarket:allmarket||'',
            moadBuyFee:adBuyFee==0?adBuyFee:adBuyFee||'',
            moadSellFee:adSellFee==0?adSellFee:adSellFee||'',
            moadValidTime:adValidTime==0?adValidTime:adValidTime||'',
            moadOrderNumMax:adOrderNumMax==0?adOrderNumMax:adOrderNumMax||'',
            momarketType:marketType==0?marketType:marketType||0,
            motransInvalidTime:transInvalidTime==0?transInvalidTime:transInvalidTime||0,
            mouserAdNumMax:userAdNumMax==0?userAdNumMax:userAdNumMax||'',
            mouserOrderNumMax:userOrderNumMax==0?userOrderNumMax:userOrderNumMax||'',
            mouserCancleNum:userCancleNum==0?userCancleNum:userCancleNum||'',
            mocoinBixDian:coinBixDian==0?coinBixDian:coinBixDian||'',
            molegalBixDian:legalBixDian==0?legalBixDian:legalBixDian||'',
            mobuyMaxNum:buyMaxNum==0?buyMaxNum:buyMaxNum||'',
            mobuyMinNum:buyMinNum==0?buyMinNum:buyMinNum||'',
            mofeeRate:feeRate==0?feeRate:feeRate||'',
            momarket:market||'',
            mosellMaxNum:sellMaxNum==0?sellMaxNum:sellMaxNum||'',
            mosellMinNum:sellMinNum==0?sellMinNum:sellMinNum||'',
            moenable:enable||true,
            orderOverTime:orderOverTime==0?orderOverTime:orderOverTime||''
        })
    }
    onInsert(){
       
         this.footer = [
             <Button key="back" onClick={this.handleCancel}>取消</Button>,
             <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.onSaveModify('add')}>
                 保存修改
             </Button>,
         ]
         this.setState({
             visible:true,
             title:'新增',
             width:'1200px',
             modalHtml:<ModalChangeMarket type={2}  handleInputChange={this.handleInputChange}/>,
             moallmarket:'',
             moadBuyFee:'',
             moadSellFee:'',
             moadValidTime:'',
             moadOrderNumMax:'',
             momarketType:0,
             motransInvalidTime:0,
             mouserAdNumMax:'',
             mouserOrderNumMax:'',
             mouserCancleNum:'',
             mocoinBixDian:'',
             molegalBixDian:'',
             mobuyMaxNum:'',
             mobuyMinNum:'',
             mofeeRate:'',
             momarket:'',
             mosellMaxNum:'',
             mosellMinNum:'',
             moenable:true,
             remark:''
         })
     }
    handleCancel(){
        this.setState({
            visible:false,
            loading:false
        })
    }
    onSaveModify(type){
        this.setState({loading:true})
        console.log(0==='')
        const {moallmarket,moadBuyFee,moadSellFee,moadValidTime,moadOrderNumMax,momarketType,motransInvalidTime,mouserAdNumMax,mouserOrderNumMax,mouserCancleNum,orderOverTime,mocoinBixDian,molegalBixDian,mobuyMaxNum,mobuyMinNum,mofeeRate,momarket,mosellMaxNum,mosellMinNum,moenable,remark}= this.state
        console.log(moallmarket,moadBuyFee,moadSellFee,moadValidTime,moadOrderNumMax,momarketType,motransInvalidTime,mouserAdNumMax,mouserOrderNumMax,mouserCancleNum,orderOverTime,mocoinBixDian,molegalBixDian,mobuyMaxNum,mobuyMinNum,mofeeRate,momarket,mosellMaxNum,mosellMinNum,moenable,remark)
        if(moallmarket===''||moadBuyFee===''||moadSellFee===''||moadValidTime===''||moadOrderNumMax===''||motransInvalidTime===''||mouserAdNumMax===''||mouserOrderNumMax===''||mouserCancleNum===''||orderOverTime===''||mocoinBixDian===''||molegalBixDian===''||mobuyMaxNum===''||mobuyMinNum===''||mofeeRate===''||momarket===''||mosellMaxNum===''||mosellMinNum===''){
            message.warning('必填项不能为空！')
            return false
        }
        let url = type == 'add'?
        axios.post(DOMAIN_VIP+'/sys/market/insert',qs.stringify({
            key:moallmarket,adBuyFee:moadBuyFee,adSellFee:moadSellFee,adValidTime:moadValidTime,adOrderNumMax:moadOrderNumMax,
            marketType:momarketType,transInvalidTime:motransInvalidTime,userAdNumMax:mouserAdNumMax,userOrderNumMax:mouserOrderNumMax,userCancleNum:mouserCancleNum,coinBixDian:mocoinBixDian,legalBixDian:molegalBixDian,
            buyMaxNum:mobuyMaxNum,buyMinNum:mobuyMinNum,feeRate:mofeeRate,market:momarket,sellMaxNum:mosellMaxNum,sellMinNum:mosellMinNum,enable:moenable,remark,orderOverTime
         })).then(res => {
            const result = res.data;
            if(result.code == 0){
              message.success(result.msg)
              this.setState({
                  visible:false,
                  loading:false
              })
              
              this.props.requestTable(momarket)
              this.props.resetActive()
              this.requestTable()
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        }):
        axios.post(DOMAIN_VIP+'/sys/market/update',qs.stringify({
            key:moallmarket,adBuyFee:moadBuyFee,adSellFee:moadSellFee,adValidTime:moadValidTime,adOrderNumMax:moadOrderNumMax,
            marketType:momarketType,transInvalidTime:motransInvalidTime,userAdNumMax:mouserAdNumMax,userOrderNumMax:mouserOrderNumMax,userCancleNum:mouserCancleNum,coinBixDian:mocoinBixDian,legalBixDian:molegalBixDian,
            buyMaxNum:mobuyMaxNum,buyMinNum:mobuyMinNum,feeRate:mofeeRate,market:momarket,sellMaxNum:mosellMaxNum,sellMinNum:mosellMinNum,enable:moenable,orderOverTime
         })).then(res => {
            const result = res.data;
            if(result.code == 0){
              message.success(result.msg)
              this.setState({
                  visible:false,
                  loading:false
              })
              console.log(this.state.momarket)
              this.props.requestTable(momarket)
              this.props.resetActive()
              this.requestTable()
              console.log(this.state.marketType)
            }else{
                message.warning(result.msg)
                this.setState({
                    loading:false
                })
            }
        })
    }
     //删除
     deleteItem(){
         const {id} = this.state
        let self = this;
        Modal.confirm({
            title: "确定删除本项吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+"/otcConfig/delete",qs.stringify({
                        id
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            message.success(result.msg)
                            self.props.requestTable()
                            self.requestTable()
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                }).catch(() => console.log('Oops errors!'));
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
        render(){
            const {btndisabl,allmarket,adBuyFee,adSellFee,orderOverTime,adValidTime,adOrderNumMax,marketType,transInvalidTime,userAdNumMax,userOrderNumMax,userCancleNum,coinBixDian,legalBixDian,buyMaxNum,buyMinNum,feeRate,market,sellMaxNum,sellMinNum,enable,disable,visible,modalHtml,width,title,limitBtn}= this.state
            return(
                <div className="col-md-12 col-sm-12 col-xs-12 marbot">
                <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">市场全称：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="allmarket" value={allmarket||''} disabled={disable}/>
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">购买方广告费率：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="adBuyFee" value={adBuyFee==0?adBuyFee:adBuyFee||''}  disabled={disable} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label"> 出售方广告费率：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="adSellFee" value={adSellFee==0?adSellFee:adSellFee||''} disabled={disable}/>
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label"> 广告有效期(天)</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="adValidTime" value={adValidTime||''} disabled={disable}/>
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label"> 广告最多执行订单数量：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="adOrderNumMax" value={adOrderNumMax||''} disabled={disable}/>
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">用户最多持有广告数量：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="userAdNumMax" value={userAdNumMax||''} disabled={disable}/>
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">用户最多执行订单数量：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="userOrderNumMax" value={userOrderNumMax||''} disabled={disable}  />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">用户取消次数：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="userCancleNum" value={userCancleNum==0?userCancleNum:userCancleNum||''} disabled={disable} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">虚拟币小数点位数：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="coinBixDian" value={coinBixDian==0?coinBixDian:coinBixDian||''} disabled={disable} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">法币小数点位数：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="legalBixDian" value={legalBixDian==0?legalBixDian:legalBixDian||''} disabled={disable} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">买入最大挂单限额：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="buyMaxNum" value={buyMaxNum==0?buyMaxNum:buyMaxNum||''} disabled={disable}  />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">买入最小挂单限额：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="buyMinNum" value={buyMinNum==0?buyMinNum:buyMinNum||''} disabled={disable} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">交易手续费：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="feeRate" value={feeRate==0?feeRate:feeRate||''} disabled={disable} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">市场：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="market" value={market||''} disabled={disable} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">市场类型：</label>
                         <div className="col-sm-8">
                         <RadioGroup onChange={this.handleInputChange} name="marketType" value={marketType} disabled={disable}>
                                <Radio value={0}>币法交易</Radio>
                                <Radio value={1}>币币交易</Radio>
                            </RadioGroup>
                             {/* <input type="text" className="form-control"  name="marketType" value={marketType} disabled={disable}/> */}
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">售出最大挂限额：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="sellMaxNum" value={sellMaxNum==0?sellMaxNum:sellMaxNum||''} disabled={disable} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">售出最小挂限额：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="sellMinNum" value={sellMinNum==0?sellMinNum:sellMinNum||''} disabled={disable} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">订单超时时间：</label>
                         <div className="col-sm-8">
                             <input type="text" className="form-control"  name="orderOverTime" value={orderOverTime == '0'?'0':orderOverTime||''} disabled={disable} />
                         </div>
                    </div>
                 </div>
                 <div className="col-md-6 col-sm-6 col-xs-6">
                     <div className="form-group">
                         <label className="col-sm-3 control-label">是否开启：</label>
                         <div className="col-sm-8">
                         <RadioGroup onChange={this.handleInputChange} name="enable" value={enable}  disabled={disable}>
                                <Radio value={true}>开启</Radio>
                                <Radio value={false}>关闭</Radio>
                            </RadioGroup>
                             {/* <input type="text" className="form-control"  name="enable" value={enable?'开启':'关闭'} disabled={disable}/> */}
                         </div>
                    </div>
                 </div>{
                     btndisabl&&
                 <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={this.onInsert}>增加</Button>:''}
                                        {limitBtn.indexOf('update')>-1?<Button type="primary" onClick={this.onChange}>修改</Button>:''}
                                        <Button type="primary" onClick={this.deleteItem}>删除</Button>
                                    </div>
                            </div>}
                            
                            <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    style={{marginTop:'-80px'}}
                    >
                    {modalHtml}            
                </Modal>
             </div>

        )
    }
}