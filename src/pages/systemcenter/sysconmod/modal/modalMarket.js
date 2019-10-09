import React from 'react'
import axios from '../../../../utils/fetch'
import qs from 'qs'
import ModalAddmarket from './modalAddmarket'
import { DOMAIN_VIP } from '../../../../conf'
import { Input,Modal,Button ,Radio,message} from 'antd'
import { resolve } from 'url';
const RadioGroup = Radio.Group;
export default class ModalMarket extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            marketone:'',
            markettwo:'',
            market:'',
            name:'',
            coinId:'',
            legalId:'',
            conList:[],
            modalHtml:'',
            visible:false,
            width:'',
            title:'',
            loading:false,
            limitBtn:[]
        }
        this.requestList = this.requestList.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onInsert = this.onInsert.bind(this)
        this.onChangename = this.onChangename.bind(this)
        this.onSaveModify = this.onSaveModify.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.deleteItem = this.deleteItem.bind(this)
    }
    componentDidMount(){
        const{marketname,marketone,markettwo,limitBtn} = this.props
        // console.log(marketone)
        this.setState({
            marketone,markettwo,
            market: marketname||'',
            limitBtn
        })
        this.requestList()
    }
    componentWillReceiveProps(nextProps){
        const{marketname,marketone,markettwo,limitBtn} = nextProps
        this.setState({
            marketone,markettwo,
            market: marketname||'',
            limitBtn
        })
    }
    requestList(){
        axios.get(DOMAIN_VIP+'/common/coinAll')
        .then(res => {
             const result = res.data;
             if(result.code == 0){
                 this.setState({
                    conList:result.data
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
    onInsert(){
        const {conList} = this.state
        this.setState({
            visible:true,
            title:'新增',
            width:'800px',
            name:'',
            coinId:'',
            legalId:'',
            modalHtml:<ModalAddmarket handleInputChange={this.handleInputChange} onChangename={this.onChangename} conList={conList}/>,
        })
    }
    onChangename(value){
        this.setState({
           name:value 
        })
    }
    handleCancel(){
        this.setState(
            {
                visible:false,
                loading:false,
            }
        )
    }
    //删除
    deleteItem(){
        const {market} = this.state
       let self = this;
       Modal.confirm({
           title: "确定删除本项吗？",
           okText: '确定',
           okType: 'more',
           cancelText: '取消',
           onOk(){
               return new Promise((resolve, reject) => {
                   axios.post(DOMAIN_VIP+"/otcConfig/market/coins/delete",qs.stringify({
                       name:market
                   })).then(res => {
                       const result = res.data;
                       if(result.code == 0){
                           message.success(result.msg)
                           self.props.requestData()
                           resolve(result.msg)
                       }else{
                           message.warning(result.msg)
                       }
                   }).then(error => {
                       reject(error)
                   })
               }).catch(() => console.log('Oops errors!'));
           },
           onCancel() {
               console.log('Cancel');
           },
       });
   }
    onSaveModify(){
        this.setState({loading:true})
        const {legalId,coinId,name,} = this.state
        if(legalId == ''){
            message.warning('请选择虚拟币id')
            this.setState({
                loading:false
            })
        }else if(coinId == ''){
            message.warning('法币id')
            this.setState({
                loading:false
            })
        }else{
            axios.post(DOMAIN_VIP+'/otcConfig/market/coins/insert',qs.stringify({
                coinId,
                legalId,
                name,
            }))
            .then(res => {
                const result = res.data;
                if(result.code == 0){
                    message.success(result.msg)
                    this.setState({
                        visible:false,
                        loading:false
                    })
                    this.props.requestData()
                }else{
                    message.error(result.msg)
                    this.setState({
                        loading:false
                    })
                    
                }
            })
        }
        
    }
    render(){
        const {conList,marketone,markettwo,market,modalHtml,visible,width,title,limitBtn} = this.state
        return(
            <div  className ='col-md-8 col-sm-8 col-xs-8'>
            <div className="x_panel">
                    <div className="x_content">
                        <div  className ='col-md-4 col-sm-4 col-xs-4 mar20'>
                            <div className="x_panel">
                                <div className="x_content">
                                <RadioGroup name="marketone" value={marketone} disabled>
                                {
                                    conList.length>0?
                                    conList.map((item,index)=>{
                                        return (
                                            <Radio  key={index} value={item.id}>{item.coinName}</Radio>
                                        )}):''
                                    
                                }
                                </RadioGroup>
                                
                                </div>
                            </div>
                        </div>
                        <div  className ='col-md-4 col-sm-4 col-xs-4'>
                            <div className="x_panel">
                                <div className="x_content">
                                <RadioGroup  name="markettwo" value={markettwo} disabled>
                                {
                                    conList.length>0?
                                    conList.map((item,index)=>{
                                        return (
                                            <Radio  key={index} value={item.id}>{item.coinName}</Radio>
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
                                    <input type="text" className="form-control"  name="market" value={market} readOnly/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6 right">
                                    <div className="right">
                                        {limitBtn.indexOf('insert')>-1?<Button type="primary" onClick={this.onInsert}>增加</Button>:''}
                                        {limitBtn.indexOf('delete')>-1?<Button type="primary" onClick={this.deleteItem}>删除</Button>:''}
                                </div>
                        </div>
                        <Modal
                        visible={visible}
                        title={title}
                        width={width}
                        onCancel={this.handleCancel}
                        footer={[
                            <Button key="back" onClick={this.handleCancel}>取消</Button>,
                            <Button key="submit" type="more" loading={this.state.loading} onClick={() => this.onSaveModify()}>
                                保存修改
                            </Button>,
                        ]}
                        style={{marginTop:'-80px'}}
                        >
                        {modalHtml}            
                </Modal>
                </div>
                </div>
        )
    }
}