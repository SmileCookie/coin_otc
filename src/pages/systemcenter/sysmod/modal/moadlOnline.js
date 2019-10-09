import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import ModalOnlinePeak from './modalOnlinePeak'
import { DOMAIN_VIP,DAYFORMAT } from '../../../../conf'
import { Modal,Table,Button,message } from 'antd'
const { Column } = Table

export default class ModalOnline extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            tableSource:[],
            visible:false,
            modalHtml:"",
            curveRange:"",
            incr:"",
        }

        this.requestOnline = this.requestOnline.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.addIncr = this.addIncr.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.detailBtn = this.detailBtn.bind(this)
        this.messageNotice = this.messageNotice.bind(this)
        this.deleteIncr = this.deleteIncr.bind(this)
        this.deleteItem = this.deleteItem.bind(this)
    }
    componentDidMount(){
        this.requestOnline()
    }
    requestOnline(type){
        axios.get(DOMAIN_VIP+"/onlineNumConfig/peak",{params:{
            configType:type || this.props.configType
        }}).then((res) => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data.peakConfigs
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].curveRange = `${tableSource[i].min}~${tableSource[i].max}`
                    tableSource[i].key = tableSource[i].id
                }
                this.setState({
                    tableSource,
                    curveRange:`${result.data.config.min}-${result.data.config.max}`,
                    incr:result.data.config.incr
                })
            }

        })
    }
    componentWillReceiveProps(nextProps) {
        this.requestOnline(`${nextProps.configType}`)
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
    //弹框隐藏
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //详情弹窗
    addIncr(item){
        this.setState({
            modalHtml:<ModalOnlinePeak 
                        configType={this.props.configType} 
                        incrItem={item} 
                        messageNotice={this.messageNotice}
                        requestOnline={this.requestOnline}
                        handleCancel = {this.handleCancel}
                        />,
            visible:true
        })
    }
    //消息提示
    messageNotice(val){
        message.warning(val)
    }
    //弹窗按钮
    detailBtn(){
        const { configType,set } = this.props
        if(set){
            const { curveRange,incr } = this.state
            const rangArr =  curveRange.split("-")
            axios.post(DOMAIN_VIP+"/onlineNumConfig/",qs.stringify({
                configType:configType,
                max:rangArr[1],
                min:rangArr[0],
                incr
            })).then((res) => {
                const result = res.data
                this.props.requestOnline()
                this.props.messageNotice(result.msg)
            })
        }
        this.props.handleCancel()
    }
    //删除峰值
    deleteIncr(id){
        axios.post(DOMAIN_VIP+"/onlineNumConfig/peak/del",qs.stringify({
            id
        })).then(res => {
            const result = res.data;
            if(result.code == 0){
                this.requestOnline()
                this.messageNotice(result.msg)
            }
        })
    }
    //删除
    deleteItem(id){
        let self = this;
        Modal.confirm({
            title: "确定删除本项吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk(){
                return new Promise((resolve, reject) => {
                    axios.post(DOMAIN_VIP+"/onlineNumConfig/peak/del",qs.stringify({
                        id
                    })).then(res => {
                        const result = res.data;
                        if(result.code == 0){
                            self.requestOnline()
                            self.messageNotice(result.msg)
                            resolve(result.msg)
                        }else{
                            message.warning(result.msg)
                        }
                    }).then(error => {
                        reject(error)
                    })
                })
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    resetState(){
        this.setState({

        })
    }
    
    render(){
        const { tableSource,visible,modalHtml,curveRange,incr } = this.state
        const { configType,set } = this.props
        return(
            <div className="right-con">
                <div className="x_panel">                 
                    <div className="x_content">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">时间范围：</label>
                                <div className="col-sm-8">
                                    <p className="line34">{configType == 0?"周一至周五":"周六周日"}</p>
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">曲线范围值：</label>
                                <div className="col-sm-3">
                                    <input type="text" className="form-control" name="curveRange" value={curveRange} readOnly={!set} onChange={this.handleInputChange} />
                                </div>
                                <div className="col-sm-4">
                                    <p className="line34">(最小值-最大值)</p>
                                </div>
                            </div> 
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">浮动值(±)：</label>
                                <div className="col-sm-8">
                                    <input type="text" className="form-control" name="incr" value={incr} readOnly={!set} onChange={this.handleInputChange} />
                                </div>
                            </div>
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <h4 className="col-sm-4">峰值列表</h4>
                            <div className="col-sm-8">
                                {set==1&&<Button className="right ant-btn-primary" onClick={() => this.addIncr()}>添加峰值</Button>}
                            </div>
                        </div>
                        <div className="table-responsive table-checkbox">
                            <Table dataSource={tableSource} locale={{emptyText:'暂无数据'}} bordered pagination={false}>
                                <Column title='序号' dataIndex='id' render={(text,record,index)=>(
                                    <span>{index+1}</span>
                                )}/>
                                <Column title='时间范围' dataIndex='configType' render={(type) => {
                                    return type == 0?"周一至周五":"周六周日"
                                }} />
                                <Column title='开始时间' dataIndex='startHour' />
                                <Column title='结束时间' dataIndex='endHour' />
                                <Column title='区间数' dataIndex='curveRange' />
                                <Column title='浮动值' dataIndex='incr'/>
                                <Column title='添加时间' dataIndex='createTime' render={(time) => {
                                    return moment(time).format(DAYFORMAT)
                                }} />
                                <Column title='操作' dataIndex='key' render={(time,record,index) => {
                                    const modifyItem = tableSource[index]
                                    return  set? <span>
                                                    <a href="javascript:void(0);" onClick={() => this.addIncr(modifyItem)} className="mar10" >修改</a>
                                                    <a href="javascript:void(0);" onClick={() => this.deleteItem(time)}>删除</a>
                                                </span>
                                                :""
                                }} />
                            </Table>
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <Button className="right ant-btn-primary martop10" onClick={this.detailBtn}>确认</Button>
                            <Button className="right martop10" type="default" onClick={this.props.handleCancel}>取消</Button>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title="添加/修改 峰值设置"
                    width={600}
                    onCancel={this.handleCancel}
                    footer={null}
                    >
                    {modalHtml}  
                </Modal>
            </div>
        )
    }
}







































