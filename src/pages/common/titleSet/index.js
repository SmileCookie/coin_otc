import React, {
    Component
} from 'react'
import {
    withRouter
} from 'react-router'

import {
    injectIntl,
} from 'react-intl';


class index extends React.Component {

    shouldComponentUpdate(nextProps, nextState) {
        
        if (nextProps.titleval == this.props.titleval&& nextProps.titlemoney == this.props.titlemoney) {
            return false;
        } else {
            return true;
        }
    }


    setTitle(val) {
        document.title = val;
    }
    render() {
       
        const {
            formatMessage
        } = this.props.intl;

            //解析在躲过翻译中配置的id项
            let titleval = this.props.titleval || "默认首页";
            //部分页面中的动态数据
            let titlemoney = this.props.titlemoney||"";
           
            const newtitleval = formatMessage({
                id: titleval
            });
            //展现格式不一单独处理
            if(titleval == '充值'||titleval == '提现'){
                this.setTitle(`${newtitleval}${titlemoney}-Btcwinex`);
            }else if(titleval == '默认首页'){
                this.setTitle(`${newtitleval}`);
            }else{
                this.setTitle(`${titlemoney}${newtitleval}-Btcwinex`);
            }
                  
        return (
            null
        )
    }
}


export default withRouter(injectIntl(index));