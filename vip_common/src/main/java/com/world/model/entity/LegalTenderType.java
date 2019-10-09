package com.world.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suxinjie on 2017/7/7.
 *
 * 系统支持的法币类型
 */
public enum LegalTenderType {

    CNY("CNY","¥"),
    USD("USD","$"),
    EUR("EUR","€"),
    GBP("GBP","￡"),
    AUD("AUD","A$")
    ;

    LegalTenderType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    private String key;
    private String value;

    public static boolean existKey(String key) {
        if (key == null) {
            return false;
        } else {
            for (LegalTenderType t : LegalTenderType.values()) {
                if (t.getKey().equals(key)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static List<String> getkeys() {
        List<String> result = new ArrayList<>();
        for (LegalTenderType t : LegalTenderType.values()) {
            result.add(t.getKey());
        }

        return result;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}