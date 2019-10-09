import React from 'react';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import './orderStatu.less'

class DealStatus extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            //订单完成状态
            doneStatueFont:'',
            doneStatueStyle:'',
        }

    this.checkDoneStatues = this.checkDoneStatues.bind(this)

    }
    componentWillMount(){
        this.props.data.dealProcess == 'done' && this.checkDoneStatues();
    }

    componentDidMount(){
       
    }
    componentWillReceiveProps(nextProps){
        nextProps.data !== this.props.data && this.checkDoneStatues;
    }

    //判断订单完成状态
    checkDoneStatues(){
        let {dealStatue,appealStatue}  = this.props.data;
        let _obj = {
            doneStatueFont:'',
            doneStatueStyle:'',
        }
        //完成
        if(dealStatue == 'pass'){
            
            Object.assign(_obj,{
                doneStatueFont:'交易完成',
                doneStatueStyle:'icon-ico_carryout-'
            })
        }
        //取消
        if(dealStatue == 'cancel'){
           
            Object.assign(_obj,{
                doneStatueFont:'交易取消',
                doneStatueStyle:'icon-ico_cancel-'
            })
        }

         //异常
         if(dealStatue == 'error'){
            
            Object.assign(_obj,{
                doneStatueFont:'异常订单',
                doneStatueStyle:'icon-ico_abnormal-'
            })
        }
         //申述
         if(dealStatue == 'appeal'){
            Object.assign(_obj,{
                doneStatueFont:'申诉中',
                doneStatueStyle:'icon-ico_appeal-'
            })
        }

        return _obj;
    }
    
    render(){
        let {dealProcess,dealStatue,appealStatue} = this.props.data;
        //流程图提示语
        let {formatMessage} = this.props.intl;
        //判断订单完成状态
        let doneInfor;
        if(dealProcess !== 'doing'){
            doneInfor = this.checkDoneStatues();
        }        
        return(
            <div className="dealStatus">
                {/*订单未结果流程图*/}
                {   dealProcess == 'doing' &&
                    <div className="status_pic flex-r coFont15">
                        <div className={`pics_1 ${(dealStatue == 'hasOrder' || dealStatue == 'hasPay')?'statusActive':''}`}>
                            <div className="flex-r">
                                <div className='pic_content ac'>
                                    <p><FormattedMessage id="已下单"/></p>
                                    <i className="iconfont icon-ico_order_h f-30"></i>
                                </div>
                                <div className="status_line">
                                    <span className="oct-center"></span>
                                </div>
                            </div>
                        </div>
                        <div className={`pics_2 ${dealStatue == 'hasPay'?'statusActive':''}`}>
                            <div className="flex-r">
                                <div className="status_line">
                                    <span className="oct-center"></span>
                                </div>
                                <div className='pic_content ac'>
                                    <p ><FormattedMessage id="待付款"/></p>
                                    <i className="iconfont icon-ico_nopayment f-30 "></i>
                                </div>
                                <div className="status_line">
                                    <span className="oct-center"></span>
                                </div>
                            </div>
                        </div>
                        <div className="pics_3" >
                            <div className="flex-r">
                                <div className="status_line">
                                    <span className="oct-center"></span>
                                </div>
                                <div className='pic_content ac'>
                                    <p ><FormattedMessage id="待释放货币"/></p>
                                    <i className="iconfont icon-ico_currency  f-30"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                }
                {   dealProcess == 'doing'&&
                    <p className="doing_p"><FormattedMessage id="当前订单状态："/>{dealStatue == 'hasOrder'?formatMessage({id:'已下单，等待买家付款'}):formatMessage({id:'买家已付款，等待卖家释放货币'})}</p>
                }
                
                {/*订单状态结果状态*/}
                {
                    dealProcess == 'done' &&
                    <div className="status_pic">
                        <div className="statue_done">
                            <i className={`iconfont ${doneInfor.doneStatueStyle} ${dealStatue}`}></i>
                            <span className="f-18">{formatMessage({id:doneInfor.doneStatueFont})}</span>
                        </div>
                    </div>
                }
            </div>
        )
    }
}

export default injectIntl(DealStatus);