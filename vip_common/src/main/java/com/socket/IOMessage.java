package com.socket;

import java.io.InputStream;

/**
 * <p>@Description: 传输对象</p>
 *
 * @author Sue
 * @date 2018/4/20下午6:28
 */
public class IOMessage {

    /**
     * 消息头的长度 4
     */
    public static final int HEADER_LENGHT = 4;

    /**
     * 需要传输的数据 key&&value
     */
    private String value;

    /**
     * 流存储
     */
    private InputStream inputStream = null;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
