import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import GoogleCode from '../../common/modal/googleCode'
import { DOMAIN_VIP, TIMEFORMAT, SELECTWIDTH } from '../../../conf'
import { Button , message, Modal ,Table } from 'antd'
import { toThousands,pageLimit } from '../../../utils'
import ModalModify from './modal/modalModifyOldSelf'
const { Column } = Table

export default class BrushParameterManage extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            tableList: [],
            tableRow:[],
            visible:false,
            width:'',
            title:'',
            limitBtn: [],
            googVisibal:false,
            type:'',
            check:'',
        }

        this.handleInputChange = this.handleInputChange.bind(this)
        this.modalModify = this.modalModify.bind(this)
        this.modalModifyBtn = this.modalModifyBtn.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.modalGoogleCode = this.modalGoogleCode.bind(this)
        this.modalGoogleCodeBtn = this.modalGoogleCodeBtn.bind(this)
        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
        this.expandedRowRender = this.expandedRowRender.bind(this)
    }

    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('config', this.props.permissList)
        })
    }
    requestTable(){
        axios.get(DOMAIN_VIP + "/brush/config/dealList").then(res => {
            const result = res.data
            for( let i = 0; i<result.data.length ; i++ ){
                result.data[i].key = result.data[i].market
                result.data[i].dealLevel = result.data[i].dealLevel || {};
            }
            if(result.code == 0){
                this.setState({
                    tableList:result.data,
                })
            }else{
                message.warning(result.msg)
            }
        })
    }
    //弹窗隐藏
    handleCancel(){
        this.setState({ 
            visible: false,
        });
        this.requestTable()
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }

    //google 验证弹窗
    modalGoogleCode(){
        this.setState({
            googVisibal:true,
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
                this.modalModifyBtn()
            }else{
                message.warning(result.msg)
            }
        })
    }

    //修改弹窗
    modalModify(item){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.modalGoogleCode()}>
                手别抖,没问题再提交
            </Button>,
        ]
        this.setState({
            visible:true,
            width:"1000px",
            title:"自成交配置参数修改",
            modalHtml:<ModalModify item={item} handleInputChange={this.handleInputChange} />,
            item:item
        }) 
    }

    modalModifyBtn(){
        const { item } = this.state
        if(!Number.isInteger(item.supDealUserId-0)||item.supDealUserId < 1){
            message.warning("挂撤单账号规则：>=1 整数数字");
            return false;
        }
        if(item.pollingTime < 1){
            message.warning("主程序等待时间规则：>=1 数字");
            return false;
        }
        const dealLevel = Object.values(item.dealLevel)
        if(item.dealLevel&&dealLevel.length){
            for(let i=0;i<dealLevel.length;i++){
                if(!dealLevel[i].numberStart&&!dealLevel[i].numberEnd&&!dealLevel[i].count&&!dealLevel[i].volatility){
                    delete item.dealLevel[i];
                    continue;
                }
                if(dealLevel[i].numberStart <= 0 || dealLevel[i].numberStart == ""){
                    message.warning(`级别${i}-自成交挂单总数量起始规则：>0  数字`);
                    return false;
                }
                if(dealLevel[i].numberEnd <= 0 || dealLevel[i].numberEnd == ""){
                    message.warning(`级别${i}-自成交挂单总数量结束规则：>0  数字`);
                    return false;
                }
                if(dealLevel[i].count < 0 || dealLevel[i].count == ""){
                    message.warning(`级别${i}-每次成交笔数：>=0  数字`);
                    return false;
                }
                if(dealLevel[i].volatility == ""){
                    message.warning(`级别${i}-波动幅度规则：数字`);
                    return false;
                }
                if(item.dealLevel[i]){
                    delete item.dealLevel[i]["key"]
                }
            }
        }
        delete item.key;
        axios.post(DOMAIN_VIP+"/brush/config/updateDeal",item).then(res => {
            const result = res.data
            if(result.code == 0 ){
                message.success(result.msg)
                this.setState({
                    visible:false
                },()=>this.requestTable())
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

    expandedRowRender(tableRow,index){
        const tableSource = Object.values(tableRow.dealLevel);
        for(let i = 0; i<tableSource.length ; i++){
            tableSource[i].key = tableSource[i].levelType
        }
        const columns = [
            { title: '市场', dataIndex: 'market', key: 'market' },
            { title: '级别', dataIndex: 'levelType', key: 'levelType' },
            { title: '自成交交挂单总数量区间', key: 'numberStart', render: (item,record,index) => {
               return  `${item.numberStart}-${item.numberEnd}`
            }},   
            { title: '每次成交笔数', dataIndex: 'count', key: 'count' },
            { title: '波动幅度', dataIndex: 'volatility', key: 'volatility' },
        ]

        return (
            <Table
                className="components-table-demo-nested"
                columns={columns}
                dataSource={tableSource}
                pagination={false}
            />
        );
    }

    render() {
        const { tableList,visible,width,title,modalHtml,limitBtn } = this.state
        return (
            <div className="right-con">
                <div className="page-title">
                    当前位置：数据中心 > 量化交易管理 > 新自成交配置管理
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">
                                <div className="table-responsive">
                                    <Table 
                                        dataSource={tableList} 
                                        expandedRowRender={this.expandedRowRender}
                                        locale={{emptyText:'暂无数据'}} 
                                        bordered 
                                        pagination={false}
                                        >
                                            <Column title='市场' dataIndex='market' />
                                            <Column title='主程序轮询时间（秒）' dataIndex='pollingTime' />
                                            <Column title='自成交账号' dataIndex='supDealUserId' />
                                            <Column title='等级' dataIndex='dealLevel' render={(dealLevel) => {
                                                return dealLevel?Object.keys(dealLevel).length:0
                                            }}/>
                                            <Column title='操作' dataIndex='configTypeclick' render={(text, record, index) => {
                                            return   <span>
                                                        <a href="javascript:void(0);" onClick={() => this.modalModify(tableList[index])} className="mar10" >修改</a>
                                                    </span>
                                            }} />
                                    </Table>
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
                 mid='BPMSEL'
                 visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
            </div>
        )
    }

}





























