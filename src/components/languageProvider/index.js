import React from 'react';
import { connect } from 'react-redux';
import { IntlProvider } from 'react-intl';

class LanguageProvider extends React.PureComponent {
    
    render() {
        
        return (
            <IntlProvider locale={this.props.locale} key={this.props.locale} messages={this.props.messages[this.props.locale]}>
                {this.props.children}
            </IntlProvider>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        locale: state.language.locale
    }
}

export default connect(mapStateToProps)(LanguageProvider);