package com.world.data.database;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2018/4/25上午8:46
 */
public class FormatUtil {
    /**
     * 整数格式化 + 截取n位小数（不四舍五入）
     * @param math
     * @param bit
     * @return
     */
    public static BigDecimal cutBigDecimal(BigDecimal math, int bit) {
        if (math == null) {
            return new BigDecimal(0);
        }
        BigDecimal bigDecimal = null;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(100);
        nf.setMaximumIntegerDigits(100);
        String mathProxy = nf.format(math);
        BigDecimal bg = new BigDecimal(mathProxy);
        String num;
        String mathProxy2 = "";
        if (mathProxy.indexOf(".") > 0) {
            num = mathProxy.substring(mathProxy.indexOf(".") + 1);
            mathProxy2 = mathProxy.substring(0, mathProxy.indexOf(".") + 1);
            if (bit == 4 && num.length() >= 4) {
                num = num.substring(0, bit);
                mathProxy2 = mathProxy2.concat(num);
            } else if (bit == 2 && num.length() >= 2) {
                num = num.substring(0, bit);
                mathProxy2 = mathProxy2.concat(num);
            } else if (bit == 3 && num.length() >= 3) {
                num = num.substring(0, bit);
                mathProxy2 = mathProxy2.concat(num);
            } else if (bit == 8 && num.length() >= 8) {
                num = num.substring(0, bit);
                mathProxy2 = mathProxy2.concat(num);
            } else {
                return bg;
            }
        } else {
            mathProxy2 = nf.format(math);
        }
        bigDecimal = new BigDecimal(mathProxy2);
        return bigDecimal;
    }

    public static void main(String[] args) {
        BigDecimal c = new BigDecimal("0.000000004", new MathContext(3,
                RoundingMode.HALF_UP));// 构造BigDecimal时指定有效精度
        System.out.println(c.toEngineeringString());
    }
}
