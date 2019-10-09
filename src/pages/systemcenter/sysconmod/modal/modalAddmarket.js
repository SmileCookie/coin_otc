import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../../conf'
import { Input,Button ,Radio} from 'antd'
const RadioGroup = Radio.Group;

export default class ModalAddmarket extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            name:'',
            coinId:'',
            legalId:'',
            conList:[],
        }
        this.handleInputChange= this.handleInputChange.bind(this)
        this.marketChange = this.marketChange.bind(this)
    }
    componentDidMount(){
        const {conList} = this.props
        this.setState({
            conList
        })
    }
    componentWillReceiveProps(nextProps){
        this.setState({
            name:'',
            coinId:'',
            legalId:''
        })
    }
    //输入时 input 设置到 satte
    handleInputChange(event){
        const {conList} = this.state
        console.log(event.target)
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value, 
        },()=>this.marketChange());

        this.props.handleInputChange(event)
    }
    marketChange(){
        const {coinId,legalId,conList} = this.state
        console.log(coinId)
        console.log(legalId)
        let marktname = ''
        let conname= ''
        for(let i=0;i<conList.length;i++){
            if(conList[i].id == coinId){
                console.log(1)
                marktname = conList[i].coinName
            }
            if(conList[i].id == legalId){
                conname = conList[i].coinName
            }
        }
        let name = `${marktname}/${conname}`.toLowerCase()
        this.setState({
            name
        })
        this.props.onChangename(name)
    }
    render(){
        const {conList,name,coinId,legalId}= this.state
        return(
            <div>
            <div  className ='col-md-3 col-sm-3 col-xs-3 mar20'>
                            <div className="x_panel">
                                <div className="x_content">
                                <RadioGroup onChange={this.handleInputChange} name="coinId" value={coinId} >
                                {
                                    conList.length>0?
                                    conList.map((item,index)=>{
                                        return (
                                            <Radio key={index} value={item.id} >{item.coinName}</Radio>
                                        )}):''
                                    
                                }
                                </RadioGroup>
                                
                                </div>
                            </div>
                        </div>
                        <div  className ='col-md-3 col-sm-3 col-xs-3'>
                            <div className="x_panel">
                                <div className="x_content">
                                <RadioGroup onChange={this.handleInputChange}  name="legalId" value={legalId}>
                                {
                                    conList.length>0?
                                    conList.map((item,index)=>{
                                        return (
                                            <Radio key={index} value={item.id}>{item.coinName}</Radio>
                                        )}):''
                                    
                                }
                                </RadioGroup>
                                
                                </div>
                            </div>
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label">市场名称：</label>
                                <div className="col-sm-8">
                                    <input type="text" className="form-control"  name="name" value={name} readOnly/>
                                </div>
                            </div>
                        </div>
                        </div>
        )
    }
}