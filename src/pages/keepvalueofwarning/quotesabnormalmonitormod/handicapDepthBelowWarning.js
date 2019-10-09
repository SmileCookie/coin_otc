
/**盘口深度低于预警值报警 */
import React,{Component} from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import { Button,DatePicker,Tabs,Pagination,Select,message,Table,Modal } from 'antd'
import { DOMAIN_VIP,SELECTWIDTH,PAGEINDEX,PAGESIZE,DEFAULTVALUE,TIMEFORMAT_ss,TIMEFORMAT,PAGRSIZE_OPTIONS20,DAYFORMAT} from '../../../conf'
import  MarketRequests  from '../../common/select/marketrequests'
import {toThousands,pageLimit} from '../../../utils'

// const TabPane = Tabs.TabPane;
const Option = Select.Option;
const {Column} = Table
class HandicapDepthBelowWarning extends Component{
    constructor(props){
        super(props);
        this.state ={
            visible:false,
            isreLoad:false,
            showHide:true,
            pageIndex:PAGEINDEX,
            pageSize:PAGESIZE,
            pageTotal:DEFAULTVALUE,
            width:'',
            pagination:{
                showQuickJumper:true,
                showSizeChanger:true,
                showTotal:total=>`总共${total}条`,
                size:'small',
                // hideOnSinglePage:true,
                // total:0,
                pageSizeOptions:PAGRSIZE_OPTIONS20,
                defaultPageSize:PAGESIZE
            },
            tableSource:[],
            market:'',
            solveValue:'0',
            RowKeys: [],
            limitBtn: [],
        }
        this.clickHide = this.clickHide.bind(this)
        
        // this.queryClickBtn = this.queryClickBtn.bind(this)
    }
    render(){
        const {time,pageSize,showHide,tableSource,pagination,pageIndex,market ,solveValue,RowKeys,limitBtn} = this.state
        return (
            <div className='right-con '>
                <div className="page-title">
                    当前位置：风控管理 > 保值异常 > 盘口深度低于预警值报警
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    {showHide &&<div   className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                 <MarketRequests market={market}  handleChange={this.handleChangeSelect} col = '3' />
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">解决状态：</label>
                                        <div className="col-sm-8">
                                                <Select value={solveValue} style={{width:SELECTWIDTH}}
                                                    onChange={this.handleChangeSolve}
                                                >
                                                    <Option value = ''>全部</Option>
                                                    <Option value = '1'>已解决</Option>
                                                    <Option value = '0'>未解决</Option>
                                                </Select>
                                        </div>
                                    </div> 
                                </div>
                                <div className="col-md-4 col-sm-4 col-xs-4 right">
                                            <div className="form-group right">
                                                <Button type="primary" onClick={this.inquiry}>查询</Button>
                                                <Button type="primary" onClick={this.resetState}>重置</Button>
                                                {limitBtn.indexOf('updateall')>-1 && <Button type="primary" disabled={!RowKeys.length} onClick={this.batchMarking}>批量标记</Button>}
                                            </div>
                                    </div>
                                    
                            </div>
                        </div>}
                        <div className="x_panel">
                                    <div className="x_content">
                                        <div className="table-responsive">
                                            <Table dataSource={tableSource} bordered pagination={{...pagination,current:pageIndex}} locale={{emptyText:'暂无数据'}}
                                            onChange={this.sorter}
                                                   rowSelection={{
                                                       selectedRowKeys: RowKeys,
                                                       onChange: this.onSelectChange,
                                                   }}
                                                >
                                                <Column title='序号' dataIndex='index' key='index' />
                                                <Column title='交易市场' dataIndex='entrustmarket' key='entrustmarket' render={(parameter)=>{
                                                    return this.insert_flg(parameter,'_',3).toUpperCase()
                                                }}/>
                                                <Column title='买单委托' dataIndex='buy' key='buy' />
                                                <Column title='买单委托预警值' dataIndex='buywarning' key='buywarning' />
                                                <Column title='卖单委托' dataIndex='sell' key='sell' />
                                                <Column title='卖单委托预警值' dataIndex='sellwarning' key='sellwarning' />
                                                <Column title='解决状态' dataIndex='states' key='states' render={text=>text==1?'已解决':'未解决'}/>
                                                <Column title='报警时间' dataIndex='datetime' key='datetime' render = {(parameter)=>{
                                                    return parameter?moment(parameter).format(TIMEFORMAT):'--'
                                                }}/>
                                                <Column title='操作' dataIndex='id' key='id' render = {(parameter,record)=>{
                                                   return(<div>
                                                        {record.states==0 && (limitBtn.indexOf('update')>-1)?<a href='javascript:void(0);' onClick={(e)=>{e.preventDefault;this.requestState(parameter,1)} }>标记</a>:''}
                                                   </div>       
                                                   )
                                                   }}/>

                                            </Table>
                                        </div>
                                </div>
                            </div>
                    </div>
        
            </div>
            
       </div>
        )
    }
    componentDidMount(){
        this.requestTable();
        this.setState({
            limitBtn: pageLimit('coinQtDishlowwarning', this.props.permissList)
        })
    }
    componentDidUpdate(){

    }
    
    //请求数据
    requestTable = (currentIndex, currentSize) => {       
        const { pageIndex,pageSize,types,pagination,accountid,market,platformValue,solveValue} = this.state
        axios.post(DOMAIN_VIP+'/coinQtDishlowwarning/list',qs.stringify({
            market,states:solveValue,
            pageIndex:currentIndex||pageIndex,
            pageSize:currentSize||pageSize,
    
        })).then(res => {
            const result = res.data;
            // console.log(result)
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id
                }
                pagination.total = result.data.totalCount;
                pagination.onChange = this.onChangePageNum;
                pagination.onShowSizeChange = this.onShowSizeChange
                this.setState({
                    tableSource:tableSource,
                    pagination,
                })
            }else{
                message.warning(result.msg);
            }
        })
    }
    requestState(parameter,el){
        let id = parameter,self = this;
        Modal.confirm({
            title:'你确定要标记吗？',
            okText:'确定',
            okType:'more',
            cancelText:'取消',
            onOk(){
                axios.post(DOMAIN_VIP+'/coinQtDishlowwarning/update ',qs.stringify({id,state:el})).then(res => {
                    const result = res.data;
                    if(result.code == 0){
                        self.requestTable()
                    } 
                })
            },
            onCancel(){
                console.log('Cancel')
            } 
        })
    }
    //交易市场下拉菜单
    handleChangeSelect = value=> {
        this.setState({
            market:value,
        })
    }
    
    onChangePageNum = (pageIndex,pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        })
        this.requestTable(pageIndex,pageSize)
    }
    handleChangeSolve=(value)=>{
        this.setState({
            solveValue:value
        })
    }
     //查询按钮
     inquiry = () => {
        this.setState({
            pageIndex:PAGEINDEX,
        },()=>this.requestTable())       
    }
    //重置按钮
    resetState = () => {
        this.setState(()=>({
            market:'',
            solveValue:'0',
    }),()=>{
        this.requestTable()
    })
    
    }
   
    //点击收起
    clickHide() {
        let { showHide } = this.state;
            
            this.setState({
                showHide: !showHide,
            })
    }
    //格式化交易市场
    insert_flg(str,flg,sn){
          var newstr="";
          for(var i=0;i<str.length;i+=sn){
              var tmp=str.substring(i, i+sn);
              newstr+=tmp+flg+str.substring( i+sn,str.length);
            if(newstr.length >= 1) {
                return newstr
            }
        }
    }
    onSelectChange = (selectedRowKeys) => {
        this.setState({
            RowKeys: selectedRowKeys
        });
    };
    batchMarking =()=> {
        let self = this;
        const {RowKeys} = this.state;
        Modal.confirm({
            title: '你确定要批量标记选中的数据吗？',
            okText: '确定',
            okType: 'more',
            cancelText: '取消',
            onOk() {
                axios.post(DOMAIN_VIP + '/coinQtDishlowwarning/updateall', qs.stringify({idlist: RowKeys.toString()})).then(res => {
                    const result = res.data;
                    if (result.code == 0) {
                        self.setState({
                            RowKeys: []
                        }, ()=> {
                            self.requestTable();
                        });
                    }
                })
            },
            onCancel() {
            }
        })
    }
    
}
export default HandicapDepthBelowWarning





