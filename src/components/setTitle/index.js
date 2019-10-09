import React, {Component} from 'react'
import {withRouter} from 'react-router'
import {injectIntl,} from 'react-intl';
import {type} from 'os';

class index extends React.Component {

    shouldComponentUpdate(nextProps, nextState) {
        if (nextProps.titleval == this.props.titleval && nextProps.titlemoney == this.props.titlemoney) {
            return false;
        } else {
            return true;
        }
    }

    setTitle(val) {
        document.title = val;
    }
    render() {
        const {formatMessage} = this.props.intl;
        //接受多国翻译接受的id
        let titleval = this.props.titleval || "默认首页";

        //接受动态的数据
        let titlemoney = this.props.titlemoney || '';

        const newtitleval = formatMessage({
            id: titleval
        });
        let Fcorin = formatMessage({
            id: '法币交易'
        });

        if (titleval == "购买" || titleval == "出售") {
            //带动态参数的
            if (titlemoney != '') {
                this.setTitle(`${titlemoney}-${newtitleval} ${Fcorin}-Btcwinex`)
            } else {
                this.setTitle(`${newtitleval} ${Fcorin}-Btcwinex`)
            }

        } else {
            //不带动态参数的
            this.setTitle(`${newtitleval}-Btcwinex`)
        }

        return (
            null
        )
    }
}

export default withRouter(injectIntl(index));
