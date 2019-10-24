import React from 'react';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import cookie from 'js-cookie';
import { getUserBaseInfo } from '../../redux/modules/session'; 
import { DOMAIN_COOKIE, COOKIE_EXPIRED_DAYS, COOKIE_FIRST } from '../../conf';
import moneyBorder from '../../assets/images/moneyBorder.png'
import './modelMoney.less'


class ModelMoney extends React.Component{
    constructor(props){
        super(props);
        this.state = {
          

        }

    }

    confrim = () =>{
        this.props.getMoneyModel()
    }

    render(){
        const {formatMessage} = this.props.intl;
        const {count,mum} = this.props
        return (
            <div className="money_center">
                <div className="money_center_main">
                    <div className="money_center_content">
                        <div className="money_center_word">
                            <p><FormattedMessage id="恭喜您收回{num}倍收益" values={{num:mum}}/></p>
                            <h2>{count} USDT</h2>
                            <span onClick={this.confrim} className="money_center_word_sure">{formatMessage({id:"确定"})}</span>
                        </div>
                    </div>
                    {/* <img style={{width:'420px'}} src={moneyBorder} alt=""/> */}
                </div>
            </div>
        )
    }
}


export default injectIntl(ModelMoney);