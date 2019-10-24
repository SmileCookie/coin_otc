import React from 'react';
import { injectIntl } from 'react-intl';

class StepOne extends React.Component{
    constructor(props){
        super(props);
    }

    render(){
        return <div>StepOne</div>
    }
}

export default injectIntl(StepOne);
