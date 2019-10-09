import React from 'react';
import { connect } from 'react-redux';
import Select from 'react-select';
import { setLang } from '../../redux/module/language'
import "./index.less";
@connect(
    state => ({
        language: state.language
    }),
   {
        setLang
   }
)
class LanguageToggle extends React.Component {
    constructor(props) {
        super(props);
        this.fieldInput = React.createRef();
        this.initLng = this.initLng.bind(this)
    }
    componentDidMount(){
       this.initLng()
    }
    showSelect(event) {
        this.fieldInput.current.closeMenu();
        this.fieldInput.current.handleMouseDown(event);
    }
    hideSelect(event) {
        this.fieldInput.current.closeMenu();
    }
    setLang(lang) {
        this.props.setLang(lang);
        this.initLng()
    }
    //全局title
    initLng(){
        // (function(){
             const LANG = document.cookie;
             const title = document.getElementById("title"),
                   keywords = document.getElementById("keywords"),
                   description = document.getElementById("description");
 
             const ct = {
                 title: {
                     'zh': 'Btcwinex-全球最专业,安全的数字资产服务平台,专注于交易BTC,VDS,VOLLAR,EOS,LTC,ETH等多种虚拟货币.',
                     'en': "Btcwinex-the world's most professional and secure digital asset service platform.Focus on trading BTC,VDS,VOLLAR,EOS,LTC,ETH and other digital currency.",
                     'zh-hant-hk': 'Btcwinex-全球最專業,安全的數位資產服務平臺,專注於交易BTC,VDS,VOLLAR,EOS,LTC,ETH等多種虛擬貨幣.'
                 },
                 keywords: {
                     'zh': 'VDS,VOLLAR,比特币交易平台,虚拟货币,数字货币',
                     'en': 'VDS,VOLLAR,Bitcoin trading platform,Virtual currency,Digital currency',
                     'zh-hant-hk': 'VDS,VOLLAR,比特幣交易平臺,虛擬貨幣,數位貨幣'
                 },
                 description: {
                     'zh': 'btcwinex交易所是全球最专业的比特币交易平台,拥有最安全的技术,最专业的撮合引擎,为用户提供BTC,ETH,VDS,VOLLAR,EOS,LTC等多种虚拟货币交易,btcwinex是一个集合新闻,交易,行情的综合类的数字资产交易平台.',
                     'en': "Btcwinex is the most professional bitcoin trading platform in the world,have the safest technology and the most professional matching engine.Provide users with various virtual currency trading such as BTC,ETH,VDS,VOLLAR,EOS,LTC.Provides BTC market and BTC news in real time,Btcwinex is a comprehensive digital asset trading platform.",
                     'zh-hant-hk': 'btcwinex交易所是全球最專業的比特幣交易平臺,擁有最安全的科技,最專業的撮合引擎,為用戶提供BTC,ETH,VDS,VOLLAR,EOS,LTC等多種虛擬貨幣交易,btcwinex是一個集合新聞,交易,行情的綜合類的數位資產交易平臺.'
                 }
             }
 
             let key = "";
             if(LANG.includes("cn")){
                 key = "zh";
             }else {
                 key = "en";
             }
 
             //title.innerHTML = ct.title[key];
             keywords.innerHTML = ct.keywords[key];
             description.innerHTML = ct.description[key];
 
        // })();
     }

    renderOption(option) {
        let cls = "icon-lang icon-lang-" + option.value ;
        return (
            <span>
                <i className={cls} />
                {option.label}
            </span>
        )
    }
    renderValue(option) {
        let cls = "icon-lang icon-lang-" + option.value +" marright0";
        return (     
            <span>
                <i className={cls} />
                <em>{option.label}</em>
            </span>
        );
    }
    render() {
        const { language } = this.props;
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
            },
           
            {
                value: "ja",
                label: "日本語"
            },
            {
                value: "ko",
                label: "한국어"
            }

        ];
        let cls = "language-toggle " + this.props.theme + "-language-toggle";
        return (
            <div className="language-toggle-wrap" onMouseEnter={this.showSelect.bind(this)} onMouseLeave={this.hideSelect.bind(this)}>
            {/* 暂时隐藏其他语言 */}
            
                <Select
                    className={cls}
                    onChange={this.setLang.bind(this)}
                    simpleValue 
                    options={options}
                    searchable={false}
                    clearable={false}
                    name="selected-state"
                    value={language.locale}
                    optionRenderer={this.renderOption}
                    valueRenderer={this.renderValue}
                    ref={this.fieldInput}
                />
            </div>
        );
    }
}


export default LanguageToggle;