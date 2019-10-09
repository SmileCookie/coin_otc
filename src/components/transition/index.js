import React from 'react';
import ThemeA from './theme/dataa';
import ThemeB from './theme/fullCover';


/**
 * 主题工厂
 */
class ThemeFactory {};

const Theme = {
    ThemeA,
    ThemeB//全局loading
};

/**
 * 获取实例
 * @param {Styles} theme 
 */
ThemeFactory.getThemeInstance = (theme = "") => {
    const Component = Theme[theme];
    return (
        Component
        ?
            <Component />
        :
            null
    );
};

/**
 * Styles
 * @property [String] ThemeA 主题风格
 */
const Styles = {
    ThemeA: "ThemeA",
    ThemeB:'ThemeB'
}

export { ThemeFactory, Styles }