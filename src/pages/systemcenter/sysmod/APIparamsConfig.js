import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import MoadlAPIparamsConfig from './modal/moadlAPIparamsConfig'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE,SELECTWIDTH,PAGRSIZE_OPTIONS20 } from '../../../conf'
import {message,Modal,Table,Button } from 'antd'
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
        this.deleteItem = this.deleteItem.bind(this)
    }
    componentDidMount(){
        this.requestOnline()
    }
    requestOnline(){
        axios.get(DOMAIN_VIP+"/apiConfig/list").then((res) => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].key = tableSource[i].id
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
    onlineDetail(list,set){
        this.setState({
            modalHtml:<MoadlAPIparamsConfig 
                        messageNotice={this.messageNotice} 
                        list={list} 
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
    //删除按钮
    deleteItem(id){
        const that = this
        Modal.confirm({
            title:"确定删除本项吗？",
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP+"/apiConfig/del",qs.stringify({
                    id
                })).then((res) => {
                    const result = res.data
                    if(result.code == 0){
                        that.messageNotice(result.msg)
                        that.requestOnline()
                    }
                })
              },
              onCancel() {
                console.log('Cancel');
              },
        })
    }   
    
    render(){
        const { tableSource,visible,modalHtml } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：系统中心 > 参数设置 > API开关配置
                </div>
                <div className="clearfix"></div>
                <div className="x_panel">                 
                    <div className="x_content">
                        <div className="col-md-12 col-sm-12 col-xs-12 marbot10">
                            <Button className="ant-btn right ant-btn-primary" onClick={() => this.onlineDetail()}>添加</Button>
                        </div>
                        <div className="table-responsive table-checkbox">
                            <Table dataSource={tableSource} locale={{emptyText:'暂无数据'}} bordered pagination={false}>
                                <Column title='序号' dataIndex='id' render={(text,record,index)=>(
                                    <span>{index+1}</span>
                                )}/>
                                <Column title='接口名称' dataIndex='apiName' />
                                <Column title='接口编码' dataIndex='apiCode' />
                                <Column title='接口url' dataIndex='url'/>
                                <Column title='是否开启' dataIndex='open'  render={(open) => {
                                    return open==0?"关闭":"开启"
                                }} />
                                <Column title='接口备注' dataIndex='remark'/>
                                <Column title='操作' dataIndex='configTypeclick' render={(type,record,index) => {
                                    const list = tableSource[index]
                                    return   <span>
                                                <a href="javascript:void(0);" className="mar10" onClick={() => this.onlineDetail(list,0)}>详情</a>
                                                <a href="javascript:void(0);" className="mar10" onClick={() => this.onlineDetail(list,1)} >修改</a>
                                                <a href="javascript:void(0);" onClick={() => this.deleteItem(list.id)}>删除</a>
                                            </span>
                                }} />
                            </Table>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title="添加API开关配置"
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



































