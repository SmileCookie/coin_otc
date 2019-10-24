import React from 'react'
import Select from 'react-select'
import cookie from 'js-cookie';
import axios from 'axios';
import { DOMAIN_VIP,DEFAULT_LOCALE, COOKIE_LAN, DOMAIN_COOKIE, COOKIE_EXPIRED_DAYS } from '../../conf/index'
import './index.less'

export default class LanguageToggle extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedOption:'zh'
        }
        this.fieldInput = React.createRef()
        this.chooseLanguage = this.chooseLanguage.bind(this)

    }
    componentWillMount(){
        let localeCookie = cookie.get(COOKIE_LAN);
        let locale;
        switch(localeCookie) {
            case 'cn':
                locale = "zh";
                break;
            case 'en':
                locale = "en";
                break;
            case 'hk':
                locale = "zh-hant-hk";
                break;
            case 'jp':
                locale = "jp";
                break;
            case 'kr':
                locale = "kr";
                break;
            default:
                locale = DEFAULT_LOCALE;
        }
        this.chooseLanguage(locale)
    }
    chooseLanguage(selectedOption){
        this.setState({
            selectedOption,
        })
        axios.get(DOMAIN_VIP+"/setLan?lan="+selectedOption);
        cookie.set(COOKIE_LAN, selectedOption, {
            expires: COOKIE_EXPIRED_DAYS,
            domain: DOMAIN_COOKIE,
            path: '/'
        })
         this.props.chooseLanguage(selectedOption)
    }
    showSelect(event) {
        this.fieldInput.current.closeMenu();
        this.fieldInput.current.handleMouseDown(event);
    }
    hideSelect(event) {
        this.fieldInput.current.closeMenu();
    }

    renderOption(option) {
        let cls = "icon-lang icon-lang-" + option.value;
        return (
            <span>
                <i className={cls} />
                {option.label}
            </span>
        )
    }
    renderValue(option) {
        let cls = "icon-lang icon-lang-" + option.value;
        return (     
            <span>
                <i className={cls} />
                <em>{option.label}</em>
            </span>
        );
    }
    render() {
        const { selectedOption } = this.state;
        const options = [
            {
                value: "en",
                label: "English"
            },
            // {
            //     value: "zh-hant-hk",
            //     label: "繁體中文"
            // },
            {
                value: "zh",
                label: "简体中文"
            }
        ];
        let cls = "language-toggle " + this.props.theme + "-language-toggle";
        return (
            <div className="chooseLanguage">
                <Select
                    className={cls}
                    simpleValue 
                    options={options}
                    searchable={false}
                    clearable={false}
                    name="selected-state"
                    onChange={this.chooseLanguage}
                    value={selectedOption}
                    optionRenderer={this.renderOption}
                    valueRenderer={this.renderValue}
                    ref={this.fieldInput}
                />
            </div>
        );
    }
}