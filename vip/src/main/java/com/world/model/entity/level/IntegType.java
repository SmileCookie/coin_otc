package com.world.model.entity.level;

import com.world.model.entity.SysEnum;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by xie on 2017/6/24.
 */
public enum IntegType implements SysEnum {

    once(1,"一次性"),
    period(2,"周期性"),
    repeat(3,"重复性");

    private int key;
    private String value;

    private IntegType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    /**
     * 获取该枚举类转成Map后集合
     * @return
     */
    public static Map<Integer, String> getIntegTypeMap(){
        TreeMap<Integer,String> map = new TreeMap<>();
        for(IntegType type : IntegType.values()){
            map.put(type.getKey(),type.getValue());
        }
        return map;
    }
}
