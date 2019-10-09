import { addLocaleData } from 'react-intl';
import enLocaleData from 'react-intl/locale-data/en';
import cnLocaleData from 'react-intl/locale-data/zh';
import jaLocaleData from "react-intl/locale-data/ja";
import koLocaleData from "react-intl/locale-data/ko";

import cnLang from './cn';
import enLang from './en';
import hkLang from './hk';
import krLang from './ko_KR'
import jpLang from './ja_JP'

addLocaleData([...enLocaleData , ...cnLocaleData , ...jaLocaleData, ...koLocaleData ])

const translationMessages = {
    "en": enLang,
    "zh": cnLang,
    "zh-hant-hk": hkLang,
    "ko":krLang,
    "ja":jpLang
}
export default translationMessages;