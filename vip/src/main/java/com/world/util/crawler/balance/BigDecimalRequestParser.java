package com.world.util.crawler.balance;


import java.math.BigDecimal;

/**
 * @ClassName BigDecimalRequestParser
 * @Author hunter
 * @Date 2019-05-27 09:55
 * @Version v1.0.0
 * @Description
 */
public abstract class BigDecimalRequestParser implements RequestPraser<BigDecimal> {

    /**
     * 单位转换
     * @param balanceStr 余额
     * @param unit 换算单位量值
     * @param filter 过滤参数
     * @return
     */
    public BigDecimal unitConvert(String balanceStr, BigDecimal unit, String filter) {
        if (balanceStr != null) {
            String filtedRs = balanceStr.trim();
            if (filter != null && !"".equalsIgnoreCase(filter)) {
                filtedRs = balanceStr.replaceAll(filter, "");
                String[] filterStrArray = filter.split(",");
                for (String filterStr : filterStrArray) {
                    filtedRs = filtedRs.replaceAll(filterStr, "").trim();
                }
            }
            if (unit == null || unit.intValue() == 0) {
                unit = new BigDecimal(1);
            }
            BigDecimal balance = new BigDecimal(filtedRs).divide(unit);
            return balance;
        }
        return null;
    }

}
