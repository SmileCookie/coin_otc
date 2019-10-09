package com.world.util.request;
 
import java.io.UnsupportedEncodingException;
 
/**
 * 封装HttpClient返回数据
 * <p>
 * @Date     Aug 5, 2014
 *
 *
 * 20170407 renfei 重用此类
 */
public class ResponseContent {
    private String encoding;
 
    private int statusCode;
 
    private String contentType;
 
    private String headerToken;//加密token

    private String content;

    public String getHeaderToken() {
        return headerToken;
    }

    public void setHeaderToken(String headerToken) {
        this.headerToken = headerToken;
    }

    public String getEncoding() {
        return encoding;
    }
 
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
 
    public String getContentType() {
        return this.contentType;
    }
 
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
 
    public int getStatusCode() {
        return statusCode;
    }
 
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}