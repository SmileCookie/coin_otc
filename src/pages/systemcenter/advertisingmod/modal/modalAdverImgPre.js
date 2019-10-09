import React from 'react'
import { Button, Pagination,message,Modal,Table } from 'antd'
import axios from '../../../../utils/fetch'
import {PAGESIZE_200,DOMAIN_VIP} from '../../../../conf'
const {Column} = Table

export default class ModalAdverImgPre extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            tableSource:[],
            selectedRowKeys:[],//选中项的 key 数组 
            selectedRows:[],//选中项的 item 数组
            bannerGroup:'',
        }
        
    }
    componentDidMount(){
        //this.requstPreTable(this.props.item)
        console.log(this.props.tableSourcePre)
        this.setState({
            tableSource:this.props.tableSourcePre
        })
    }
    componentWillReceiveProps(nextProps){
        this.setState({
            tableSource:nextProps.tableSourcePre
        })
    }
    

    render(){
        const {tableSource} = this.state


        return(
            <div className="col-md-12 col-sm-12 col-xs-12 hei400">    
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <label>图片预览：</label>
                    <Table dataSource={tableSource} bordered pagination={false} scroll={{ y: 500 }} locale={{emptyText:'暂无数据'}}>
                        <Column width="70px" title='序号' key="index" dataIndex='index' render={(text)=>(
                            <span>{text}</span>
                        )}/>
                        <Column  title='图片' key="bannerUrl" dataIndex='bannerUrl'render={(text,record) => (
                            <div style={{width:"200px",height:"200px",overFlow:'auto'}}><img src={JSON.parse(record.bannerUrl).cn} style={{width:'100%',height:'100%'}} /></div>
                        )} />
                    </Table>
                </div>
            </div>
        )
    }
}