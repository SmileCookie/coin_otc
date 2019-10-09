import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import MarketList from '../select/marketList'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP } from '../../../conf'
import { Button, Radio, message, Modal,Switch } from 'antd'
import { pageLimit } from '../../../utils'
const RadioGroup = Radio.Group;

export default class BatchEntrust extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            id:'',
            qtUserId: '',
            priceMin: '',
            priceMax: '',
            numMultipleStart: '',
            numMultipleEnd: '',
            priceSubMax: '',
            priceBuildMin: '',
            priceBuildMax: '',
            taskFlag:'',
            market: '',
            visible: false,
            title:'',
            width:'',
            modalHtml:'',
            swit:''
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.requestTable = this.requestTable.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.onChangeSwitch = this.onChangeSwitch.bind(this)
        this.requestData = this.requestData.bind(this)
        this.saveData = this.saveData.bind(this)

        
    }
    componentDidMount() {
        this.requestData()
        this.onChangeSwitch()
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

   
    //保存弹窗
    requestTable() {
        let self = this
        Modal.confirm({
            title: '确定要保存吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                self.saveData()
            }
            
        })
    }

    handleCancel() {
        this.setState({
            visible: false
        })
    }
// 开关
     onChangeSwitch(checked) {
       
       checked==false? this.setState({
           taskFlag:1
       }) : this.setState({
            taskFlag:0
       })
       console.log(this.state.taskFlag)
      }


    //弹窗关闭
    onhandleCancel() {
        this.setState({
            googVisibal: false
        })
    }
// 保存数据
    saveData=()=>{
        const {id,qtUserId,priceMin,priceMax,numMultipleStart,numMultipleEnd,priceSubMax,priceBuildMin,priceBuildMax,taskFlag,market}=this.state;
        axios.post(DOMAIN_VIP + "/brush/config/modify/boss",qs.stringify({
            id,qtUserId,priceMin,priceMax,numMultipleStart,numMultipleEnd,priceSubMax,priceBuildMin,priceBuildMax,taskFlag,market
        })).then(res=>{
            const result = res.data;
            if(result.code==0){
                message.success("保存成功")
            }else{
                message.error("保存失败")
            }
        })
    }
    // 请求数据
    requestData() {
        axios.get(DOMAIN_VIP + "/brush/config/boss").then(res => {
            const result = res.data;
            
            if (result.code == 0) {
              
                const {id,qtUserId,priceMin,priceMax,numMultipleStart,numMultipleEnd,priceSubMax,priceBuildMin,priceBuildMax,taskFlag,market}=result.data;
                this.setState({
                    id,qtUserId,priceMin,priceMax,numMultipleStart,numMultipleEnd,priceSubMax,priceBuildMin,priceBuildMax,taskFlag,market
                })
                
            } else {
                message.warning(result.msg)
            }
        })
    }




    render() {
        const {modalHtml,title,width, swit,visible,qtUserId,priceMin,priceMax,numMultipleStart,numMultipleEnd,priceSubMax,priceBuildMin,priceBuildMax,taskFlag,market } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 盘口管理 > 新页面
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="batch-box">
                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">市场:</label>
                                            <div className="col-sm-8">
                                                <div style={{ lineHeight: 2.6 }}>{market}</div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">刷量用户ID:</label>
                                            <div className="col-sm-8">
                                                <input type="text" className="form-control" name="qtUserId" value={qtUserId} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                   
                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">价格区间:</label>
                                            <div className="col-sm-8">
                                                <div className="left col-sm-5 sm-box">
                                                    <input type="text" className="form-control" name="priceMin" value={priceMin} onChange={this.handleInputChange} />
                                                </div>
                                                <div className="left line34">-</div>
                                                <div className="left col-sm-5 sm-box">
                                                    <input type="text" className="form-control" name="priceMax" value={priceMax} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">下单倍数:</label>
                                            <div className="col-sm-8">
                                                <div className="left col-sm-5 sm-box">
                                                    <input type="text" className="form-control" name="numMultipleStart" value={numMultipleStart} onChange={this.handleInputChange} />
                                                </div>
                                                <div className="left line34">-</div>
                                                <div className="left col-sm-5 sm-box">
                                                    <input type="text" className="form-control" name="numMultipleEnd" value={numMultipleEnd} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">补单价格:</label>
                                            <div className="col-sm-8">
                                                <div className="left col-sm-5 sm-box">
                                                    <input type="text" className="form-control" name="priceBuildMin" value={priceBuildMin} onChange={this.handleInputChange} />
                                                </div>
                                                <div className="left line34">-</div>
                                                <div className="left col-sm-5 sm-box">
                                                    <input type="text" className="form-control" name="priceBuildMax" value={priceBuildMax} onChange={this.handleInputChange} />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">补单最大价差:</label>
                                            <div className="col-sm-8">
                                                <input type="text" className="form-control" name="priceSubMax" value={priceSubMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                        <div className="form-group">
                                            <label className="col-sm-4 control-label">任务开关:</label>
                                            <div className="col-sm-8">
                                            <Switch name="taskFlag" checked={!taskFlag} checkedChildren="开" unCheckedChildren="关" onChange={this.onChangeSwitch} />
                                            </div>
                                        </div>
                                    </div>


                                    <div className="col-md-12 col-sm-12 col-xs-12">
                                       <Button className="marl50" type="more" size="large" onClick={this.requestTable}>保存</Button> 
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
                    {modalHtml}
                </Modal>
               
            </div>
        )
    }

}



































