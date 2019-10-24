import React from 'react';
import Menu from '../common/menu/menu.js';
import { FormattedMessage, injectIntl } from 'react-intl';

import Marketbar from '../common/marketbar/marketbarContainer';

class Mange extends React.Component {
    constructor(){
        super();
        
    }
    render() {
        return (
            <div className="mainer2 account-wrap">
              <div className="container">
                    {this.props.children}
              </div>     
            </div>
        )
    }
}

export default Mange;