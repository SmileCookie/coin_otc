import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../conf'
import ModalMarket from './modal/modalMarket'
import { Input,Button ,Checkbox,message,Tabs,Radio} from 'antd'
import { pageLimit} from '../../../utils'
const TabPane = Tabs.TabPane;
const RadioGroup = Radio.Group;

export default class MarketList extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            markList:[],
            marketone:'',
            markettwo:'',
            market:'',
            marketname:'',
            limitBtn:[]
        }
        this.requestData = this.requestData.bind(this)
        this.chengeTable = this.chengeTable.bind(this)
        
        
    }
    componentDidMount(){
        this.requestData()
        this.setState({
            limitBtn: pageLimit('market:coins',this.props.permissList)
        })
    }
    requestData(){
        axios.post(DOMAIN_VIP+'/otcConfig/market/coins')
        .then(res => {
             const result = res.data;
             if(result.code == 0){
                 this.setState({
                    markList:result.data,
                    marketone:result.data[0].coinId,
                    markettwo:result.data[0].legalId,
                    marketname:result.data[0].name,
                 })
             }
         })
       
    }
    chengeTable(activeKey){
        const{markList} = this.state
        let marketone = ''
        let markettwo =''
        let market= ''
        markList.map((item,index)=>{
            if(index == activeKey){
                marketone=markList[index].coinId
                markettwo=markList[index].legalId
               market = markList[index].name
            }
        })
        this.setState({
            marketone,markettwo,
            marketname:market
        })
    }
     

    render(){
        const {markList,conList,limitBtn} = this.state
        return(
        <div className="x_panel" style={{ marginTop: '40px' }}>
        
             <div className="x_content">
             {/*<div className="page-title">*/}
                    {/*当前位置：系统中心 > 配置中心 > 市场列表*/}
                {/*</div>*/}
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="col-md-2 col-sm-2 col-xs-2">
                    <Tabs
                        defaultActiveKey="0"
                        tabPosition={'left'}
                        onChange={this.chengeTable}
                        > 
                {
                    markList.length>0?
                    markList.map((item,index)=>{
                        return (
                        <TabPane tab={item.name} key={index}></TabPane>
                        )}):''
                    }
                </Tabs>
                    </div>
                    <div className="col-md-10 col-sm-10 col-xs-10">
                        <ModalMarket {...this.state} requestData={this.requestData}/>
                    </div>
                </div>
             </div>
        </div>)
    }
}