package com.world.model.entity.autodownload;

import com.world.model.entity.SysEnum;

/**
 * Created by xie on 2017/10/18.
 * 待打币记录状态
 */
public enum AutoDownloadRecordStatus implements SysEnum {

    COMMIT(1 , "初始状态"),
    SUCCESS(2 , "成功"),
    FAIL(3 , "失败");

    private AutoDownloadRecordStatus(int key, String value) {
        this.key = key;
        this.value = value;
    }

    private int key;
    private String value;

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
