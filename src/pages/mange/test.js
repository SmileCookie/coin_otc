import React from 'react';
import Menu from '../common/menu/menu.js';
import { FormattedMessage, injectIntl } from 'react-intl';

class Test extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            menu:[
                {
                    linkTo:"/bw/manage/user",
                    activeClassName:"active1",
                    text:<FormattedMessage id="manage.text1" />
                },
                {
                    linkTo:"/bw/manage/level",
                    activeClassName:"active1",
                    text:<FormattedMessage id="manage.text3" />
                },
                {
                    linkTo:"/bw/manage/toUserLoginHistroy",
                    activeClassName:"active1",
                    text:<FormattedMessage id="manage.text4" />
                }
            ]
        }
    }
    render(){
        return (
            <div className="mainer2 account-wrap">
                <div className="container">
                    <div className="user-panel">
                        <Menu menu={this.state.menu}/>
                        <div className="content">
                            {this.props.children}
                        </div>
                    </div>
                </div>     
            </div>
            
        )
    }
}

export default Test;