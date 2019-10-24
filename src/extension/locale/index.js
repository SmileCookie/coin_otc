import { addLocaleData } from 'react-intl'
import enLocaleData from 'react-intl/locale-data/en';
import cnLocaleData from 'react-intl/locale-data/zh'

import cnLang from './cn'
import enLang from './en'
import hkLang from './hk'

addLocaleData([...enLocaleData,...cnLocaleData]);


const translationMessages = {
    'en':enLang,
    'zh':cnLang,
    'zh-hant-hk':hkLang
}

export default translationMessages;