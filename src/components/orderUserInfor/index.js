import React from 'react';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import './orderUserinfor.less'
import {getFirstStr} from '../../utils'
import {USERID} from '../../conf'
import UserCenter from '../../components/user/userCenter'
import ReactModal from '../../components/popBox';
import {post} from '../../net'

class UserInfor extends React.Component{
    constructor(props){
        super(props);
        this.state = {
                 tipMsg:'<p>--</p>',
              }
        }
       
    componentWillMount(){
        
    }

    componentDidMount(){

        post('/web/otcIntroduction/getMsg',{type:'adTips_order'}).then(res =>{
            if(res.code == 200){
                this.setState({
                    tipMsg: res.data[0].descript || '<p>--</p>'
                })
               
            }
            
        })
        // const element =  document.querySelector('.aliImg')
        // element.classList.add('animated', 'zoomIn')
        // element.addEventListener('animationend', function() { })
    }
    componentWillReceiveProps(nextProps){
       
    }
    ImgMouseOn = (e) =>{
        e.stopPropagation();
        const element =  document.querySelector('.aliImg')
        element.style.display = 'block';
        element.classList.add('zoomIn');
    }
    ImgMouseOut = (e) => {
        const element =  document.querySelector('.aliImg')
        element.classList.remove('zoomIn')
        element.style.display = 'none';

    }
    async showUserCenter(id) {
        let userId = USERID || "";
        let res = await post('/web/common/getAvgPassTime', {targetUserId: id, userId})
        if (res.code == 200){
            this.setState({
                targetId:id,
                uid: USERID,
                homePage: res.data,
            },() =>{
                this.modalUser.openModal()
            })
        }else{
            optPop(() =>{},res.data.msg,{timer: 1500})
        }
    }
   
    
    render(){
        const {dealType,userInfor,orderListInfor,sellUserColor,buyUserColor,backUseId,backNickname,backCardname,selfCardname} = this.props.dealInforList;
        const {formatMessage} = this.props.intl
        const {cardName} = this.props
        const {tipMsg}  = this.state
        let realName = null
        //console.log('++++++++++++++++++++++++'+ cardName)
        if(cardName){
           realName = cardName.split('');
           realName.splice(0,1,'*');
           realName = realName.join('')
        }
        return(
            <div className="userInfor">
               <h2>
                   <FormattedMessage id={dealType == 'buy' ? '付款方式' : '买家信息'}/>
                  {
                      dealType == 'buy' &&
                      <span className="realName">
                          {/* {formatMessage({id:'(您必须用实名为：{name} 的账户向以下账户转账)',values: {value:'dsds'} }) } */}
                          <FormattedMessage id='(您必须用实名为：{name} 的账户向以下账户转账)' values={{name: <span style={{color:'#3E85A2'}}>{realName} </span>}}/>
                      </span>
                  }
                </h2>
               <div className="infors">
                   <div className="pic">
                       <span onClick={() =>this.showUserCenter(backUseId)} className="photo"  style={{backgroundColor:dealType  == 'buy' ? sellUserColor: buyUserColor}}>{getFirstStr(backNickname)}</span>
                       <span onClick={() =>this.showUserCenter(backUseId)}>{backNickname}{dealType == 'sell' ?  backCardname ? `(${backCardname})` : ''  : ''}</span>
                       { orderListInfor.marker && dealType == 'sell'  && <p className="other">{formatMessage({id:'备注：'}) + orderListInfor.marker}</p>}
                   </div>
                   {
                    dealType == 'buy' && 
                        <React.Fragment>
                            <div className="detail flex-r">
                                {
                                    userInfor.payMethod.map((item,index) =>{
                                        return(
                                            item.type == 'bank' ?
                                            <div className="payInfor" key={'bank'}>
                                                <div className="list flex-r">
                                                    <h3><FormattedMessage id="收款方式"/></h3>
                                                    <div>
                                                        <svg className="icon" aria-hidden="true" style={{width:'82px',height:'20px'}}>
                                                            <use xlinkHref="#icon-shoukuanfangshiyinlian"></use>
                                                        </svg>
                                                    </div>
                                                </div>
                                                <div className="list flex-r">
                                                    <h3><FormattedMessage id="收款人"/></h3>
                                                    <div>
                                                        {item.name}
                                                    </div>
                                                </div>
                                                <div className="list flex-r">
                                                    <h3><FormattedMessage id="开户行"/></h3>
                                                    <div>
                                                        {item.payName}
                                                    </div>
                                                </div>
                                                <div className="list flex-r">
                                                    <h3><FormattedMessage id="开户支行"/></h3>
                                                    <div>
                                                        {item.payAdress}
                                                    </div>
                                                </div>
                                                <div className="list flex-r">
                                                    <h3><FormattedMessage id="银行卡号"/></h3>
                                                    <div>
                                                        {item.payCode}
                                                    </div>
                                                </div>
                                            </div> 
                                            :
                                            <div className="payInfor" key={'alipay'} style={{borderLeft: index == 1? '1px solid #414654':'none', paddingLeft: index == 1? '100px':'0'}}>
                                                <div className="list flex-r">
                                                    <h3><FormattedMessage id="收款方式"/></h3>
                                                    <div>
                                                        <svg className="icon" aria-hidden="true" style={{width:'60px',height:'20px'}}>
                                                            <use xlinkHref="#icon-shoukuanfangshizhifubao"></use>
                                                        </svg>
                                                    </div>
                                                </div>
                                                <div className="list flex-r">
                                                    <h3><FormattedMessage id="收款人"/></h3>
                                                    <div>
                                                        {item.name}
                                                    </div>
                                                </div>
                                                <div className="list flex-r">
                                                    <h3><FormattedMessage id="支付宝号"/></h3>
                                                    <div>
                                                        {item.alipayPayCode}
                                                    </div>
                                                </div>
                                                <div className="list flex-r">
                                                    <h3><FormattedMessage id="收款码"/></h3>
                                                    <div>
                                                        {
                                                            item.alipayPayImgUrl?
                                                            <span className="iconfont icon-zhifubaoshoukuanmas" onMouseEnter={(e) =>this.ImgMouseOn(e)} onMouseLeave={(e) => this.ImgMouseOut(e)}>
                                                                <div className="hoverMsg">({formatMessage({id:'鼠标移入查看'})})</div>
                                                                <div className="aliImg">
                                                                    <img src={item.alipayPayImgUrl} alt=""/>
                                                                </div>
                                                            </span>
                                                            :
                                                            <span><FormattedMessage id="未上传"/></span>

                                                        }
                                                        
                                                    </div>
                                                </div>
                                            </div>
                                            
                                        )
                                    })
                                }
                            </div>
                            <div className="tips flex-r">
                                <h3><FormattedMessage id="温馨提示："/></h3>
                                <p dangerouslySetInnerHTML={{__html:tipMsg}}></p>
                            </div>
                       </React.Fragment>
                   }
                  
               </div>
                 <ReactModal ref={modal => this.modalUser = modal}   >
                    <UserCenter modal={this.modalUser}  hoemPage={this.state.homePage} targetId={this.state.targetId} uid={this.state.uid}/>
                </ReactModal>
            </div> 
        )
    }
}

export default injectIntl(UserInfor);