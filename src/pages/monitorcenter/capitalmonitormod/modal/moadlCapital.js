import React from 'react'
import axios from '../../../../utils/fetch'
import moment from 'moment'
import qs from 'qs'
import { message,Input,Modal,Button,Pagination} from 'antd'
import { DOMAIN_VIP,TIMEFORMAT,PAGEINDEX ,PAGESIZE} from '../../../../conf'
const { TextArea } = Input;


export default class MoadlCapital extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            ucmId:'',
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            tableList:[],
            pageTotal:0,
            memo:'',
            visible:false,
            title:'',
            width:'',
            modalHtml:'',
            loading:false

        }
        this.requestTable = this.requestTable.bind(this)
        this.onAuditInfoto = this.onAuditInfoto.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResave = this.onResave.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.changPageNum = this.changPageNum.bind(this)
        this.onShowSizeChange = this.onShowSizeChange.bind(this)
    }

    componentDidMount(){
        this.setState({
            ucmId : this.props.ucmId
        },()=>this.requestTable())
        // this.requestTable(this.props.ucmId)
    }
     //点击分页
     changPageNum(page,pageSize){
        this.setState({
            pageIndex:page
        })
        this.requestTable(page,pageSize)
    }
    //分页的 pagesize 改变时
    onShowSizeChange(current,size){
        this.requestTable(current,size)
        this.setState({
            pageIndex:current,
            pageSize:size
        })
    }

    componentWillReceiveProps(nextProps){
        this.setState({
            ucmId : nextProps.ucmId
        },()=>this.requestTable())
        // this.requestTable(nextProps.ucmId)
    }
    onAuditInfoto(item){
        const{memo}= this.state
        this.footer= [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" loading={this.state.loading} onClick={()=>this.onResave(item)}>
                确定
            </Button>,
        ]
        
        this.setState({
            visible:true,
            title:"新增备注",
            width:"700px",
            memo:'',
            modalHtml:<div className="form-group">
                          <label className="col-sm-3 control-label">备注：</label>
                          <div className="col-sm-8">
                              <TextArea rows={4} name="memo"  onChange={this.handleInputChange}/>
                          </div>
                      </div>
        })
    }
    //关闭修改备注弹窗
    handleCancel(){
        this.setState({
            visible:false,
            modalHtml:'',
            loading:false
        })
    }
    onResave(item){
        const {memo} = this.state
        if(memo == ''){
            message.warning('备注不能为空！')
        }else{  
                this.setState({
                    loading:true
                })
                axios.post(DOMAIN_VIP+"/capitalMonitor/dealRemarkInfo",qs.stringify({
                id:item.id,
                memo
            })).then(res => {
                const result = res.data
                if(result.code == 0){
                    message.success(result.msg) 
                    this.setState({
                        visible:false,
                        modalHtml:'',
                        loading:false
                    })
                    this.requestTable()  
                }else{
                    message.warning(result.msg)
                    this.setState({
                        loading:false
                    })
                }
            }) 
        }
       
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


    requestTable(currIndex,currSize){
        const {ucmId,pageIndex,pageSize} = this.state
        axios.post(DOMAIN_VIP+"/capitalMonitor/errorUserInfo",qs.stringify({
            ucmId,
            pageIndex:currIndex||pageIndex,
            pageSize:currSize||pageSize
        })).then(res => {
            const result = res.data
            if(result.code == 0){
                this.setState({
                    tableList:result.List.list,
                    pageTotal:result.List.totalCount,
                })    
            }else{
                message.warning(result.msg)
            }
        })
    }

    render(){
        const { tableList,pageIndex,pageSize,memo,visible,modalHtml,title,width,pageTotal } = this.state
        return(
            <div className="x_content">
            <div className="table-responsive">
                <table border='1' className="table table-striped jambo_table bulk_action table-memo">
                    <thead>
                        <tr className="headings">
                            <th className="column-title">序号</th>
                            <th className="column-title">资金类型</th>
                            <th className="column-title">检查时点余额</th>
                            <th className="column-title">流水合计金额</th>
                            <th className="column-title">账单号</th>
                            <th className="column-title">处理备注</th>
                            <th className="column-title">操作</th>                                           
                        </tr>
                    </thead>
                    <tbody>
                        {
                            tableList.length>0?
                            tableList.map((item,index) => {
                                return (
                                    <tr key={index}>
                                        <td>{(pageIndex-1)*pageSize+index+1}</td>
                                        <td>{item.fundsTypeName}</td>
                                        <td>{item.checkBillAmount}</td>
                                        <td>{item.billTotalAmount}</td>
                                        <td>{item.billId}</td>
                                        <td>{item.dealRemark}</td>
                                        <td><a href="javascript:void(0)" onClick={() => this.onAuditInfoto(item)}>处理</a>
                                        </td>
                                    </tr>
                                )
                            })
                            :<tr className="no-record"><td colSpan="15">暂无数据</td></tr>
                        }
                    </tbody>
                </table>
                
                </div>
                <div className="pagation-box">
                    {
                        pageTotal>0 && <Pagination
                                    size="small"
                                    current={pageIndex}
                                    total={pageTotal}
                                    showTotal={total => `总共 ${total} 条`}
                                    onChange={this.changPageNum}
                                    onShowSizeChange={this.onShowSizeChange}
                                    showSizeChanger
                                    showQuickJumper />
                    }
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    width={width}
                    style={{ top: 60 }}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                >
                    {modalHtml}            
                </Modal>
               
            </div>
           
        )
    }
}
