package com.world.util.qiniu;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.world.util.jpush.PropertiesUtils;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.util.Properties;

/**
 * 七牛上传工具类
 *
 * Created by suxinjie on 2017/5/17.
 */
public class QiNiuUtil {

    private final static Logger log = Logger.getLogger(QiNiuUtil.class);

    private static String accessKey;
    private static String secretKey;
    private static String bucketname;
    private static String host;

    static {
        Properties qiniuProperties = PropertiesUtils.getProperties("qiniu.properties");
        accessKey = qiniuProperties.getProperty("qiniu.access.key");
        secretKey = qiniuProperties.getProperty("qiniu.secret.key");
        bucketname = qiniuProperties.getProperty("qiniu.bucketname");
        host = qiniuProperties.getProperty("qiniu.host");
    }

    public static String getHost(){
        return host;
    }

    private static final Auth auth = Auth.create(accessKey, secretKey);

    // 创建上传对象
    private static final UploadManager uploadManager = new UploadManager();

    // 简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public static String getUpToken() {
        return auth.uploadToken(bucketname);
    }

    public static String uploadFile(byte[] data, String key, String token) {
        String path = "";
        try {
            // 调用put方法上传
            Response res = uploadManager.put(data, key, token);

            if(res != null){
                JSONObject result = JSONObject.parseObject(res.bodyString());
                // 打印返回的信息
                if(result.containsKey("key")){
                    path = String.format(host, result.getString("key"));
                }
            }
        } catch (QiniuException e) {
            log.error(e.toString(), e);
            //TODO
        }

        return path;
    }

    /**
     * 获取七牛上传token
     * @return
     */
    public static String getToken(){
        String token = "";
        try{
            Auth auth = Auth.create(accessKey, secretKey);
            token = auth.uploadToken(bucketname);
        }catch (Exception e){
            log.error(e.toString(), e);
        }
        return token;
    }






    public static void main(String[] args) throws Exception {

        log.info(getUpToken());

       String s = "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";

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b = new byte[0];
        try {
            b = decoder.decodeBuffer(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 处理数据
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                b[i] += 256;
            }
        }

       String path =  uploadFile(b,"test.jpg",QiNiuUtil.getUpToken());
       System.out.println(path);
    }

}
