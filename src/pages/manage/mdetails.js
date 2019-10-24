import React from 'react';
import { FormattedMessage, injectIntl, } from 'react-intl';
import { connect } from 'react-redux';

class MDetails extends React.Component{
    constructor(props){
        super(props);
    }

    render(){
        return (
            <div>
                
            </div>
        )
    }
}

export default connect(state => ({}), {})(injectIntl(MDetails));