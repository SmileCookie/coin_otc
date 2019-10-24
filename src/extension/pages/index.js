import React from 'react';
import {Link ,browserHistory} from 'react-router'
import LanguageToggle from '../components/languageToggle'
import './market/market.less'
import { formatURL} from '../utils'
import LanguageProvider from '../components/languageProvider'
import { IntlProvider } from 'react-intl'
import enLang from '../locale/en'
import cnLang from '../locale/cn'
import hkLang from '../locale/hk'


class Market extends React.Component{
    constructor(props){
        super(props);
        this.state= {
            locale:'en',
            messages:cnLang
        }
        this.chooseLanguage = this.chooseLanguage.bind(this)
    }
    chooseLanguage(selectedOption){
        switch(selectedOption){
            case 'zh':
                this.setState({
                    //locale:selectedOption,
                    messages:cnLang,
                    
                })
                break;
            case 'zh-hant-hk':
                this.setState({
                    //locale:selectedOption,
                    messages:hkLang,
                    
                })
                break;
            case 'en':
                this.setState({
                    //locale:selectedOption,
                    messages:enLang,
                })
                break;
            default:
                 break;
        }
    }
    render(){
        const {locale,messages} = this.state
        return (
            <IntlProvider locale={locale} messages={messages} >
            <div className="market-main tgp">
            <Link onClick={()=>{
                browserHistory.push(formatURL(`walletCooperation?p=1&all=${all}`))
            }}></Link>
                <LanguageToggle chooseLanguage={this.chooseLanguage} />
                {this.props.children}
            </div>
            </IntlProvider>
        );
    }
}

export default Market;