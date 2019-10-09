import React from 'react'
import axios from '../../../utils/fetch'
import qs from 'qs'
import ModalCapitalList from './modal/modalCapitalListOTC'
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../../conf'
import SelectAType from '../select/selectAType'
import { Button,Pagination } from 'antd'
const BigNumber = require('big.js')

export default class CapitalCountOTC extends React.Component{

    constructor(props) {
        super(props)
        this.state = {
            showHide:true,
            fundsType:'0',
            userId:'',
            moneyMin:'',
            moneyMax:'',
            userName:'',
            borrowMax:'',
            freezMoneyMin:'',
            freezMoneyMax:'',
            frozenWithdrawMin:'',
            frozenWithdrawMax:'',
            lendingMin:'',
            lendingMax:'',
            frozenTradeMin:'',
            frozenTradeMax:'',
            isreLoad:false,
            totalMoneyMin:'',
            totalMoneyMax:'',
            frozenFeeMin:'',
            frozenFeeMax:'',
            tableScroll:{
                tableId:'CAPTLCTOTC',
                x_panelId:'CAPTLCTOTCXX',
                defaultHeight:500,
            }
        }   
        this.handleInputChange = this.handleInputChange.bind(this)
        this.onResetState = this.onResetState.bind(this)
        this.handleChange = this.handleChange.bind(this)
        this.queryClickBtn = this.queryClickBtn.bind(this)
        this.clickHide = this.clickHide.bind(this)
    }

    componentDidMount(){
        var height  =document.querySelector(`#${this.state.tableScroll.x_panelId}`).offsetHeight
        this.setState({
            xheight:height
        })
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

    //资金类型
    handleChange(val){
        this.setState({
            fundsType:val
        })
    }

    //重置按钮
    onResetState(){
         this.setState({
            fundsType:'0',
            userId:'',
            moneyMin:'',
            moneyMax:'',
            userName:'',
            borrowMin:'',
            borrowMax:'',
            freezMoneyMin:'',
            freezMoneyMax:'',
            frozenWithdrawMin:'',
            frozenWithdrawMax:'',
            lendingMin:'',
            lendingMax:'',
            frozenTradeMin:'',
            frozenTradeMax:'',
            totalMoneyMin:'',
            totalMoneyMax:'',
            frozenFeeMin:'',
            frozenFeeMax:'',
         })
    }
    //查询按钮
    queryClickBtn(val){
        this.setState({
            isreLoad:val
        })
    }
    //点击收起
    clickHide() {
        let { showHide } = this.state;
        this.setState({
            showHide: !showHide
        })
    }

    render(){
        const { showHide,fundsType,totalMoneyMin,totalMoneyMax,pageTotal,tableList,pageIndex,pageSize,isreLoad,userId,userName,moneyMin,moneyMax,borrowMin,borrowMax,freezMoneyMin,freezMoneyMax,frozenWithdrawMin,frozenWithdrawMax,lendingMin,lendingMax,frozenTradeMin,frozenTradeMax,frozenFeeMax,frozenFeeMin } = this.state
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心 > 资金统计 > 法币资金统计
                    <i className={showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={this.clickHide}></i>
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        {showHide&&<div id={this.state.tableScroll.x_panelId} className="x_panel">
                            
                            <div className="x_content">
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <SelectAType findsType={fundsType} col='3' handleChange={this.handleChange}></SelectAType>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户编号：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userId" value={userId} onChange={this.handleInputChange} />
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">用户名：</label>
                                        <div className="col-sm-8">
                                            <input type="text" className="form-control" name="userName" value={userName} onChange={this.handleInputChange} />
                                            <b className="icon-fuzzy">%</b>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">可用金额：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="moneyMin" value={moneyMin} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="moneyMax" value={moneyMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">总金额：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="totalMoneyMin" value={totalMoneyMin} onChange={this.handleInputChange} />
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="totalMoneyMax" value={totalMoneyMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4 hide">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">借用金额：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="borrowMin" value={borrowMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="borrowMax" value={borrowMax} onChange={this.handleInputChange} />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">冻结金额：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="freezMoneyMin" value={freezMoneyMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="freezMoneyMax" value={freezMoneyMax} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">提现冻结金额：</label>
                                        <div className="col-sm-8">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="frozenWithdrawMin" value={frozenWithdrawMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="frozenWithdrawMax" value={frozenWithdrawMax} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4 hide">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">放贷冻结金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="lendingMin" value={lendingMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="lendingMax" value={lendingMax} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">广告冻结金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="frozenFeeMin" value={frozenFeeMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="frozenFeeMax" value={frozenFeeMax} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                                    <div className="form-group">
                                        <label className="col-sm-3 control-label">交易冻结金额：</label>
                                        <div className="col-sm-8 ">
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="frozenTradeMin" value={frozenTradeMin} onChange={this.handleInputChange}/>
                                            </div>
                                            <div className="left line34">-</div>
                                            <div className="left col-sm-5 sm-box">
                                                <input type="text" className="form-control" name="frozenTradeMax" value={frozenTradeMax} onChange={this.handleInputChange}/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-3 col-sm-3 col-xs-3 right">
                                    <div className="right">
                                        <Button type="primary" onClick={() => this.queryClickBtn(true)}>查询</Button> 
                                        <Button type="primary" onClick={this.onResetState}>重置</Button>  
                                    </div>
                                </div>
                            </div>
                        </div>}
                        
                        <ModalCapitalList permissList= {this.props.permissList} {...this.state} queryClickBtn={this.queryClickBtn}/>
                    </div>
                </div>
            </div>
        )
    }
}





























































