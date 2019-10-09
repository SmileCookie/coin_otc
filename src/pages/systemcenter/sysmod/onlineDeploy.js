import React from 'react'
import axios from '../../../utils/fetch'
import ModalOnline from './modal/moadlOnline'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import {message,Modal,Table } from 'antd'
const { Column } = Table

export default class BankManage extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            tableSource:[],
            visible:false,
            modalHtml:""
        }

        this.requestOnline = this.requestOnline.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.onlineDetail = this.onlineDetail.bind(this)
        this.messageNotice = this.messageNotice.bind(this)
    }
    componentDidMount(){
        this.requestOnline()
    }
    requestOnline(){
        axios.get(DOMAIN_VIP+"/onlineNumConfig/").then((res) => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].curveRange = `${tableSource[i].min}~${tableSource[i].max}`
                    tableSource[i].key = tableSource[i].id
                    tableSource[i].configTypeclick = tableSource[i].configType
                }
                this.setState({
                    tableSource
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
    //弹框隐藏
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //详情弹窗
    onlineDetail(type,set){
        this.setState({
            modalHtml:<ModalOnline 
                        messageNotice={this.messageNotice} 
                        configType={type} 
                        set={set} 
                        handleCancel={this.handleCancel}
                        requestOnline={this.requestOnline}
                        />,
            visible:true
        })
    }
    //消息提醒
    messageNotice(val){
        message.warning(val)
    }
    
    render(){
        const { tableSource,visible,modalHtml } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 参数设置 > 在线人数配置
                </div>
                <div className="clearfix"></div>
                <div className="x_panel">                 
                    <div className="x_content">
                        <div className="table-responsive table-checkbox">
                            <Table dataSource={tableSource} locale={{emptyText:'暂无数据'}} bordered pagination={false}>
                                <Column title='序号' dataIndex='id' render={(text,record,index)=>(
                                    <span>{index+1}</span>
                                )}/>
                                <Column title='时间范围' dataIndex='configType' render={(type) => {
                                    return type == 0?"周一至周五":"周六周日"
                                }} />
                                <Column title='曲线范围' dataIndex='curveRange' />
                                <Column title='浮动值' dataIndex='incr'/>
                                <Column title='峰值' dataIndex='peakNum' />
                                <Column title='操作' dataIndex='configTypeclick' render={(type) => {
                                   return   <span>
                                                <a href="javascript:void(0);" onClick={() => this.onlineDetail(type,0)} className="mar10" >详情</a>
                                                <a href="javascript:void(0);" onClick={() => this.onlineDetail(type,1)}>修改</a>
                                            </span>
                                }} />
                            </Table>
                        </div>
                    </div>
                </div>
                
                <Modal
                    visible={visible}
                    title="在线人数配置详情"
                    width={1100}
                    onCancel={this.handleCancel}
                    footer={null}
                    >
                    {modalHtml}            
                </Modal>
            </div>
        )
    }
}







