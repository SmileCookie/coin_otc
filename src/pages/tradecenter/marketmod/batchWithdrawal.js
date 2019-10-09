import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import MarketList from '../select/marketList'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP } from '../../../conf'
import { Button,Radio,message,Modal } from 'antd'
import { pageLimit } from '../../../utils'
const RadioGroup = Radio.Group;

export default class BatchEntrust extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            market:'etc_btc',
            type:'',
            mode:'',
            minPrice:'',
            maxPrice:'',
            userId:'',
            visible:false,
            title:'',
            width:'',
            modalHtml:'',
            limitBtn: [],
            googVisibal:false,
            check:'',
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.handleChangeMarket = this.handleChangeMarket.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.requestTableBtn = this.requestTableBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }
    componentDidMount(){
        this.setState({
            limitBtn: pageLimit('batchEntrust', this.props.permissList)
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

    //市场 select
    handleChangeMarket(val){
        this.setState({
            market:val
        })
    }
    //批量下单按钮
    requestTableBtn(){
        const { market,type,userId,mode,minPrice,maxPrice } = this.state
        axios.post(DOMAIN_VIP +"/batchEntrust/batchCancel",qs.stringify({
            market,type,userId,
            mode,
            minPrice:mode?minPrice:'',
            maxPrice:mode?maxPrice:''
        })).then(res => {
            const result = res.data
            message.warning(result.msg)
        })
    }
    //批量下单弹窗
    requestTable(){
        let self = this
        Modal.confirm({
            title: '确定要批量撤单吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                self.modalGoogleCode()
            },
            onCancel(){
                console.log('Cancel');
            }
        })
    }
  
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //google 验证弹窗
    modalGoogleCode(){
       this.setState({
           googVisibal:true
       })
    }

    //google 按钮
    modalGoogleCodeBtn(value){
        const { googleCode } = value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    googVisibal:false
                })
                this.requestTableBtn()
            }else{
                message.warning(result.msg)
            }
        })
    }
    handleCreate(){
        const form = this.formRef.props.form;
        form.validateFields((err, values) => {
          if (err) {
            return;
          }
          form.resetFields();
          this.modalGoogleCodeBtn(values)
        });
      }
      saveFormRef(formRef){
        this.formRef = formRef;
      }
        //谷歌弹窗关闭
    onhandleCancel(){
        this.setState({
            googVisibal: false 
        })
    }

    render(){
        const { market,type,userId,visible,width,title,modalHtml,limitBtn,mode,minPrice,maxPrice } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 盘口管理 > 批量撤单
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_title">
                                <h4>批量撤单</h4>
                            </div>
                            <div className="x_content">
                                <div className="batch-box">
                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                       <MarketList market={market} col='3' handleChange={this.handleChangeMarket}/>
                                    </div>

                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group">
                                            <label className="col-sm-3 control-label">用户编号：</label>
                                            <div className="col-sm-8">
                                                <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>

                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">盘口市场:</label>
                                            <div className="col-sm-8">
                                                <RadioGroup onChange={this.handleInputChange} name="type" value={type}>
                                                    <Radio value={1}><span className="font_green">买盘</span></Radio>
                                                    <Radio value={0}><span className="font_red">卖盘</span></Radio>
                                                </RadioGroup>
                                            </div>
                                        </div> 
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">撤单方式:</label>
                                            <div className="col-sm-8">
                                                <RadioGroup onChange={this.handleInputChange} name="mode" value={mode}>
                                                    <Radio value={0}><span className="font_green">撤销全部</span></Radio>
                                                    <Radio value={1}><span className="font_red">按照价格区间撤销</span></Radio>
                                                </RadioGroup>
                                            </div>
                                            <label className="col-sm-2 control-label"></label>
                                            {mode===1&&<div className="col-sm-10">
                                                <div className="col-sm-5">
                                                    <input type="text" className="form-control input-smb" name="minPrice" value={minPrice} onChange={this.handleInputChange} />
                                                </div>
                                                <div className="col-sm-2 line34">至</div>
                                                <div className="col-sm-5">
                                                    <input type="text" className="form-control input-smb" name="maxPrice" value={maxPrice} onChange={this.handleInputChange} />
                                                </div>
                                            </div>}
                                        </div> 
                                    </div>

                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        {limitBtn.indexOf('doBatchEntrust')>-1?<Button className="marl50" type="more" size="large" onClick={this.requestTable}>批量撤单</Button>:''}
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    { modalHtml }
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='BETW'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}



































