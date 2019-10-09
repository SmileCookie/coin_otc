package com.world.data.database;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/9/12下午5:08
 */
public class FormatUtils {

    /**
     * 针对交易平台提示占位符格式化，兼容两个项目
     * 比如 24小时内最多可再次提现%%%%。格式化成 24小时内最多可再次提现12btc。
     *
     * @param f
     * @param param
     * @return
     */
    public static String formatForVip(String f, Object[] param) {
        if (null != param) {
            for (int i = 0; i < param.length; i++) {
                f = f.replaceFirst("%%", param[i].toString());
                f = f.replace("%" + (i + 1) + "%", param[i].toString());
            }
        }
        return f;
    }
}
