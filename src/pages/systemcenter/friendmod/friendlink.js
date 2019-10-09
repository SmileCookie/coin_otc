import React from 'react'
import axios from 'axios'
import { DOMAIN_VIP } from '../../../conf/'
import GoogleCode from '../../common/modal/googleCode'
import qs from 'qs'
import { message, Button, Modal } from 'antd';
export default class FriendLink extends React.Component{
    constructor(props){
        super(props);

        
        this.model = {
            name: '',
            link: '',
            icon: '',
            desc: 0
        };

        this.footer = [];

        this.state = {
            model: {
                name: '',
                link: ''
            },
            opt: {
                showHide:1,
                visible:false,
                title:'',
                tmp: 0
            },
            res: [],
            save: this.model,
            id: '',
            google:{
                code:''
            },
            check:'',
            googVisibal:false,
            item:'',
            type:'',
            showHid:true
        }

        this.clickHide = this.clickHide.bind(this)
        this.clickHid = this.clickHid.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.inquireBtn = this.inquireBtn.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.addRuleItem = this.addRuleItem.bind(this)
        this.handleOk = this.handleOk.bind(this)
        this.handleCancel = this.handleCancel.bind(this)
        this.up = this.up.bind(this)
        
        this.modalGoogleCode = this.modalGoogleCode.bind(this)

        this.handleCreate = this.handleCreate.bind(this)
        this.saveFormRef = this.saveFormRef.bind(this)
        this.onhandleCancel = this.onhandleCancel.bind(this)
    }

    componentDidMount(){
        this.inquireBtn();
    }

    modalGoogleCode(){
        this.setState({
            googVisibal:true,
        })
       
    }
    modalGoogleCodeBtn(value){
        const {googleCode}=value
        axios.post(DOMAIN_VIP+"/common/checkGoogleCode",qs.stringify({
            googleCode
        })).then((res)=>{
            if(res.data.code == 0){
                this.handleOk();
                this.setState({
                    googVisibal:false
                })
            }else{
                message.warning(res.data.msg);
            }
        })
    }

    inquireBtn(){
        axios.post('/friendUrl/queryList', qs.stringify(this.state.model)).then((result) => {
            const res = result.data.page.list;
            
            this.setState({
                res
            })
        })
    }
    onResetState(){
        this.setState({
            model: Object.assign({}, this.state.model, this.model)
        })
        setTimeout(()=>{
            this.inquireBtn();
        },0)
 
    }
    addRuleItem(){
        // this.modalGoogleCode();
        console.log(this.state.opt)
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={this.modalGoogleCode}>
                保存
            </Button>,
        ];
        this.setState({
            opt:Object.assign({}, this.state.opt, {tmp:0,visible:true,title:'新增'})
        })
    }
   
    clickHid(){
        const {showHid} = this.state;
        this.setState(()=>({
            showHid:!showHid
        }))
    }
    clickHide() {
        let { showHide } = this.state.opt;
        this.setState({
            opt: {
                showHide: !showHide
            }
        })
    }

    handleInputChange(e){
        let md = e.target.getAttribute('md');
        md = md ? md : 'model';
        this.setState({
            [md]: Object.assign({}, this.state[md], {
                [e.target.getAttribute('data-name')]: e.target.value
            })
        },)
    }
    del(id){
        axios.post('/friendUrl/delete', qs.stringify({id})).then((res)=>{
            if(res.data.code == 0){
                this.inquireBtn();
                message.success(res.data.msg);
            }else{
                message.warning(res.data.msg);
            }
        })
        
    }

    handleOk(){
       if(!this.state.id){
           const{name,icon,desc} = this.state.save
           let {link} = this.state.save
           if(!name ){
            message.warning('合作伙伴名称不能为空！')
            return false
           }
           if(!link ){
            message.warning('合作伙伴链接不能为空！')
            return false
           }
           if(!this.checkUrl(link)){
            message.warning('请输入正确的网址！')
            return false
           }
           let url = null;
            if(link.slice(0,3)==='htt'){
               url = link
               }else{
                 url = link.replace(link,'http://'+link);
               }
               if(!this.checkUrl(url)){
                message.warning('请输入正确的网址！')
                return false
               }
            axios.post('/friendUrl/add', qs.stringify({name,icon,desc,link:url})).then((res)=>{
                if(res.data.code == 0){
                    this.inquireBtn();
                    this.clickHide();
                    message.success(res.data.msg);
                }else{
                    message.warning(res.data.msg);
                }
                

            })
        }else{
            // up date
            let update = {};
            const {name, link} = this.state.save;
            const id = this.state.id;
            let url = null;
            if(link.slice(0,3)==='htt'){
               url = link
               }else{
                 url = link.replace(link,'http://'+link);
               }
               if(!this.checkUrl(url)){
                message.warning('请输入正确的网址！')
                return false
               }
            axios.post('/friendUrl/update', qs.stringify({id, name, url})).then((res)=>{
                if(res.data.code == 0){
                    this.setState({id:''});
                    this.inquireBtn();
                    this.clickHide();
                    message.success(res.data.msg);
                }else{
                    message.warning(res.data.msg);
                }
               
            })
        }
        this.clearSave();
    }
    checkUrl(urlString){
        if(urlString!=""){
        var reg=/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
        if(!reg.test(urlString)){
                    return false
                }else{
                    return true
                }

            }
        }
    clearSave(){
        this.setState({
            save: Object.assign({}, this.state.save, {name:'', link:''})
        })
    }
    handleCancel(){
        this.setState({
            opt:Object.assign({}, this.state.opt, {visible:false})
        })
        this.clearSave()
    }
    up(id, name, link){
        this.footer = [
            <Button key="back" onClick={this.handleCancel}>取消</Button>,
            <Button key="submit" type="more" onClick={this.handleOk}>
                修改
            </Button>,
        ];
       this.setState({id});
       this.setState({
           save: Object.assign({}, this.state.save, {name, link})
       })
       this.setState({
            opt:Object.assign({}, this.state.opt, {tmp:0,visible:true,title:'修改'})
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

    render(){
        const {showHide,visible,title, tmp} = this.state.opt;
        const {name, link} = this.state.model;
        const { res ,showHid} = this.state;
        const { name : sname, link : slink } = this.state.save;
        const { code : googleCode } = this.state.google;
        this.dialogHtml = [<div className="x_content">
        <div className="col-mg-24 col-lg-24 col-md-24 col-sm-24 col-xs-24">
            <div className="form-group sp">
                <label className="col-sm-3 control-label">合作伙伴名称：<i>*</i></label>
                <input type="text"  className="col-sm-21 form-control"  name="type" data-name="name" md="save" value={sname} onChange={this.handleInputChange} />
            </div>
        </div>
        
        <div className="col-mg-24 col-lg-24 col-md-24 col-sm-24 col-xs-24">
            <div className="form-group sp">
                <label className="col-sm-3 control-label">合作伙伴链接：<i>*</i></label>
                <input  type="text" className="col-sm-21 form-control"  name="rule" data-name="link" md="save" value={slink} onChange={this.handleInputChange} />
            </div>
        </div>
    </div>];
         return (
            <div className="right-con">
                    <div className="page-title">
                        当前位置：系统中心 > 合作伙伴
                        <i className={showHid ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHid}></i>
                    </div>

                    <div className="clearfix"></div>
                    <div className="row">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            {showHid&&<div className="x_panel">
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">合作伙伴名称：</label>
                                        <div className="col-sm-9">
                                            <input type="text" className="form-control"  name="type" data-name="name" value={name} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">合作伙伴链接：</label>
                                        <div className="col-sm-9">
                                            <input type="text" className="form-control"  name="rule" data-name="link" value={link} onChange={this.handleInputChange} />                                    
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>

                                <div className="col-md-4 col-sm-4 col-xs-4 right" style={{clear:'left'}}>
                                    <div className="right">
                                        <Button type="primary" onClick={this.inquireBtn}>查询</Button>
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>
                                        <Button type="primary" onClick={this.addRuleItem}>新增</Button>
                                    </div>
                                </div>

                            </div>
                        </div>}
                        <div className="x_panel">
                            
                            <div className="x_content">
                                <div className="table-responsive">
                                    <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
                                        <thead>
                                            <tr>
                                                <th>合作伙伴名称</th>
                                                <th>合作伙伴链接</th>
                                                <th>操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {
                                                res.map((item, k) => {
                                                    return (
                                                        <tr key={item.id}>
                                                            <td>{item.name}</td>
                                                            <td>{item.url}</td>
                                                            <td>
                                                                <a href="javascript:void(0)" className="mar10" onClick={() => this.del(item.id)}>删除</a>
                                                                <a href="javascript:void(0)" className="mar10" onClick={() => this.up(item.id, item.name, item.url)}>修改</a>
                                                            </td>
                                                        </tr>
                                                    );
                                                })
                                            }
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <Modal
                    visible={visible}
                    title={title}
                    onOk={this.handleOk}
                    onCancel={this.handleCancel}
                    footer={this.footer}
                    >
                    {
                        this.dialogHtml[tmp]
                    }
                </Modal>
                <GoogleCode
                 wrappedComponentRef={this.saveFormRef}
                 check={this.state.check}
                 handleInputChange = {this.handleInputChange}
                 mid='FD'
                visible={this.state.googVisibal}
                 onCancel={this.onhandleCancel}
                 onCreate={this.handleCreate}
                /> 
             </div>
         )
    }
}