import React from 'react';
import { IntlProvider } from 'react-intl';

class LanguageProvider extends React.Component {
    constructor(props){
        super(props)
    }
    // chooseLocal(){
        
    // }
    
    render() {
        console.log(this.props.messages.zh)
        return (
            <IntlProvider locale={this.props.locale} key={this.props.locale} messages={this.props.messages[this.props.locale]}>
                {this.props.children}
            </IntlProvider>
        );
    }
}



export default LanguageProvider;