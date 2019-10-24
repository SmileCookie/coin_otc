import React from 'react';
import Menu from '../common/menu/menu.js';
import { FormattedMessage, injectIntl } from 'react-intl';
// import Menu from './menu.js';
import Marketbar from '../common/marketbar/marketbarContainer';
import cookie from 'js-cookie';
import { browserHistory } from 'react-router';
import ErrorComponent from '../common/ErrorComponent'

class Manage extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            menu:[
                {
                    linkTo:"/bw/manage/account",
                    activeClassName:"active1",
                    text:<FormattedMessage id="account.text1" />,
                    onlyActiveOnIndex:true
                },
                {
                    linkTo:"/bw/manage/account/charge",
                    activeClassName:"active1",
                    text:<FormattedMessage id="account.text2" />
                },
                {
                    linkTo:"/bw/manage/account/download",
                    activeClassName:"active1",
                    text:<FormattedMessage id="account.text3" />
                }
            ],
            isMoneyControl:false
        }

    }
    componentDidCatch(){
        //setTimeout(() => {
            if(!cookie.get('zuid')){
                window.location.href = '/bw/login';
            }
        //}, 100)
    }
    componentDidMount(){
        if(this.props.location.query.coint){
            this.setState({
                isMoneyControl:true
            })
        }
    }
    render() {
        let {isMoneyControl} = this.state;
        return (
            <ErrorComponent classNames="mainer" style={{}}>
                <div className="mainer">
                    <div className="container2" style={{'overflow':isMoneyControl?'inherit':'hidden','position': 'static'}}>
                        <div className="content">
                            {this.props.children}
                        </div>
                    </div>
                 </div>
            </ErrorComponent>

        )
    }
}

export default Manage;