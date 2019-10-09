import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../conf'
import ModalFormmarkt from './modal/modalFormmarkt'
import {message,Input,Modal,Button,Tabs} from 'antd'
import Loading from '../../loading'
import { pageLimit} from '../../../utils'
import { format } from 'path';
const TabPane = Tabs.TabPane;

export default class MarketConfig extends React.Component{
    constructor(props){
    super(props)
        this.state = {
            id:'',
            treeData:[],
            allmarket:'',
            momarket:'',
            adBuyFee:'',
            adSellFee:'',
            adValidTime:'',
            adOrderNumMax:'',
            transInvalidTime:'',
            userAdNumMax:'',
            userOrderNumMax:'',
            userCancleNum:'',
            coinBixDian:'',
            legalBixDian:'',
            buyMaxNum:'',
            buyMinNum:'',
            feeRate:'',
            market:'',
            sellMaxNum:'',
            sellMinNum:'',
            enable:'' ,
            loading:false,
            activeKey:'0',
            limitBtn:[]
    }
    this.requestTable = this.requestTable.bind(this)
    this.chengeTable = this.chengeTable.bind(this)
    this.resetActive = this.resetActive.bind(this)
}
    componentDidMount(){
        this.requestTable()
        this.setState({
            limitBtn: pageLimit('market',this.props.permissList)
        })
    }
    requestTable(remarket){
        const {reremark,rekey} = this.state
        axios.post(DOMAIN_VIP+'/otcConfig/query',qs.stringify({
            type:1,status:0
         })).then(res => {
            const result = res.data;
            console.log(result)
            if(result.code == 0){
               let reqlist = []
               let treeData =[]
               reqlist = result.data
               for(let i=0;i<reqlist.length;i++){
                   let title =reqlist[i].key
                   let arr = title.split('_')
                   treeData.push({title:arr[0]+ '_'+arr[1],content:reqlist[i].key,id:reqlist[i].id,key:i})
               }
               
               this.setState({
                treeData,
                id:treeData[0].id,
                momarket:treeData[0].title,
                allmarket:treeData[0].content,
                loading:false
               })
            }else{
                message.warning(result.msg)
            }
        })
    }

    chengeTable(activeKey){
        let market = ''
        let allmarket = ''
        let id =''
        const {treeData} = this.state
        treeData.map((item,index)=>{
            if(index == activeKey){
                market = treeData[index].title
                allmarket = treeData[index].content
                id = treeData[index].id
            }
        })
        this.setState({
            loading:true,
            momarket:market,
            allmarket,
            activeKey,
            id,
        })
      
    }
    resetActive(){
        
        const {treeData} = this.state
        console.log(treeData[0].key)
        this.setState({
            activeKey:String(treeData[0].key)
        })
    }
  
  render() {
   const {id,treeData,adBuyFee,adSellFee,activeKey,loading,adValidTime,adOrderNumMax,transInvalidTime,userAdNumMax,userOrderNumMax,userCancleNum,coinBixDian,legalBixDian,buyMaxNum,buyMinNum,feeRate,market,sellMaxNum,sellMinNum,enable,limitBtn}= this.state
    return (
           <div>
            <div className="page-title marl20">
                {/*当前位置：系统中心 > 配置中心 > 市场配置*/}
            </div>
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-2 col-sm-2 col-xs-2">
               <Tabs
                defaultActiveKey="0"
                tabPosition={'left'}
                activeKey = {activeKey}
                onChange={this.chengeTable}
                > 
                {
                    treeData.length>0?
                    treeData.map((item,index)=>{
                        return (
                        <TabPane tab={item.title} id={String(item.id)} key={item.key}></TabPane>
                        )}):''
                    }
                </Tabs>
             </div>
             <div className="col-md-10 col-sm-10 col-xs-10">
                 
             <ModalFormmarkt {...this.state} requestTable={this.requestTable} resetActive={this.resetActive}/>
             </div>
             </div>
           </div>                             
                                   
    )
  }
}
