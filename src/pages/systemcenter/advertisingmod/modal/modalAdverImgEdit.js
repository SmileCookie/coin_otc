import React from 'react'
import { Button, Pagination,message,Modal,Table } from 'antd'
import axios from '../../../../utils/fetch'
import {PAGESIZE_200,DOMAIN_VIP} from '../../../../conf'
const {Column} = Table

export default class ModalAdverImgEdit extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            tableSource:[],
            selectedRowKeys:[],//选中项的 key 数组 
            selectedRows:[],//选中项的 item 数组
            bannerGroup:'',
            selectedRowsPre:[],
            inputs:{},
            serial:[]
        }

        this.onSelectChangeTable = this.onSelectChangeTable.bind(this)
        this.requstTable = this.requstTable.bind(this)
    }
    componentDidMount(){   
        if(this.props.item){
            this.setState({
                selectedRows:[],
                selectedRowKeys:[],
                selectedRowsPre:[]
            }) 
        }
            
        this.requstTable()
    }
    componentWillReceiveProps(nextProps){
        if(nextProps.item){
            this.setState({
                selectedRows:[],
                selectedRowKeys:[],
                selectedRowsPre:[]
            }) 
        }
        this.requstTable()
    }
    handleInputChange = (event,id) => {
        const { serial} = this.state
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        // for(let i = 0;serial.length;i++ ){

        // }
        serial.map((item,index) => {
            if(item.key == id){
                item.index = Number(value)
            }
        })
        this.setState({
            serial,
        })
        // this.setState({
        //     inputs : Object.assign({},{[name]:value},this.state.inputs)
        // },()=>{console.log(this.state)})
    }
    //table 多选框按钮选中时
    onSelectChangeTable(selectedRowKeys, selectedRows){
        //console.log(selectedRowKeys,selectedRows)
        let arr=[],serial =[]
        //按照勾选的先后顺序为排列顺序
        for(let i=0;i<selectedRowKeys.length;i++){
            for(let j=0;j<selectedRows.length;j++){
                if(selectedRowKeys[i]==selectedRows[j].id){
                    selectedRows[j].index2 = i+1;
                    arr.push(selectedRows[j]);
                    serial.push({key:selectedRows[j].id,index:selectedRows[j].index2})
                }
            }
        }
        // let obj = {}
        // for(let i = 0; i < arr.length;i++){
        //     obj['name'+i] = arr[i].index2
        // }
        // console.log(obj)
        // console.log(serial)
        this.setState({ 
            selectedRowKeys,
            selectedRowsPre:arr,
            // inputs:Object({},obj,this.state.inputs),
            serial:serial
        });
        this.props.onSelectPhotos(selectedRowKeys,selectedRows)
    }
    requstTable(){
        axios.get(DOMAIN_VIP+'/bannerPhoto/queryList', {params: {
            status:1,
            bannerName:'',
            pageIndex:1,
            pageSize:100000
        }}).then(res => {
            const result = res.data;
            if(result.code == 0){
                let tableSource = result.data.list;
                for(let i=0;i<tableSource.length;i++){
                    tableSource[i].index = (result.data.currPage-1)*result.data.pageSize+i+1;
                    tableSource[i].key = tableSource[i].id
                }
                this.setState({
                    tableSource:tableSource,
                })
            }else{
                message.warning(result.msg);
            }
        })
    }

    render(){
        const {tableSource,selectedRowKeys,selectedRows,selectedRowsPre} = this.state
        const rowSelection = {
            selectedRowKeys,
            onChange: this.onSelectChangeTable,
            fixed:true
          };

        return(
            <div className="col-md-12 col-sm-12 col-xs-12 hei400">    
                {this.props.type?'':<div className="col-md-6 col-sm-6 col-xs-6 ">
                    <label>图片选择：</label>
                    <Table rowSelection={rowSelection} dataSource={tableSource} bordered pagination={false} scroll={{ y: 500,x:600 }} locale={{emptyText:'暂无数据'}} >
                        <Column width="70px" title='序号' dataIndex='index' render={(text)=>(
                            <div style={{width:'37px'}}>{text}</div>
                        )}/>
                        <Column title='图片名称' key="bannerName" dataIndex='bannerName' />
                        <Column  title='图片链接列表' key='bannerUrl' dataIndex='bannerUrl' render={(text,record) => (
                            <span>{JSON.parse(record.bannerUrl).cn}</span>
                        )} />
                    </Table>    
                </div>}

                <div className="col-md-6 col-sm-6 col-xs-6 borderLeft">
                    <label>图片预览：</label>
                    <Table dataSource={selectedRowsPre} bordered pagination={false} scroll={{ y: 500 }} locale={{emptyText:'暂无数据'}}>
                        <Column width="70px" title='序号' key="index2" dataIndex='index2' render={(text,record)=>(
                            // <div style={{width:'37px'}}><input style={{width:'37px',textAlign:'center'}} name={'name'+text} defaultValue={text} onChange={(e)=>this.handleInputChange(e,record.id)} /></div>
                            <div style={{width:'37px'}}>{text}</div>
                        )}/>
                        <Column  title='图片' key="bannerUrl" dataIndex='bannerUrl'render={(text,record) => (
                            <div style={{width:"270px",height:"150px"}}><img src={JSON.parse(record.bannerUrl).cn} style={{width:'100%',height:'100%'}} /></div>
                        )} />
                    </Table>
                </div>
            </div>
        )
    }
}