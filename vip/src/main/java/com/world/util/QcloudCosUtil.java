package com.world.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.auth.COSSigner;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import com.tencent.cloud.CosStsClient;
import com.world.util.jpush.PropertiesUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 腾讯云COS 工具类
 *
 * @author Jack
 * @since 2018-09-06
 */
public class QcloudCosUtil {
    private static COSCredentials cred;
    private static String host;
    private static String staticHost;
    private static String uploadDir;
    private static String regionName;
    private static String bucketName;
    private static Long expiredMills;
    private static String appID;
    private static String tmpSecretId;
    private static String tmpSecretKey;
    private static final String SLASH = "/";
    protected static Logger log = Logger.getLogger(QcloudCosUtil.class.getName());

    static {
        init();
    }

    /**
     * 初始化配置参数
     */
    private static void init() {
        Properties qcloudPro = PropertiesUtils.getProperties("qcloud.properties");
        cred = new BasicCOSCredentials(qcloudPro.getProperty("qcloud.access.key"), qcloudPro.getProperty("qcloud.secret.key"));
        host = qcloudPro.getProperty("qcloud.host");
        staticHost = qcloudPro.getProperty("qcloud.host.static");
        regionName = qcloudPro.getProperty("qcloud.upload.regionName");
        bucketName = qcloudPro.getProperty("qcloud.upload.bucketName");
        expiredMills = Long.valueOf(qcloudPro.getProperty("qcloud.upload.token.expired.mills"));
        appID = qcloudPro.getProperty("qcloud.upload.appId");
        tmpSecretId = qcloudPro.getProperty("qcloud.access.key");
        tmpSecretKey = qcloudPro.getProperty("qcloud.secret.key");
        setHost(qcloudPro.getProperty("qcloud.host"));
        setUploadDir(qcloudPro.getProperty("qcloud.upload.dir"));
    }

    private static void setHost(String property) {
        host = property;
        if (!host.endsWith(SLASH)) {
            host += SLASH;
        }
    }

    /**
     * 设置上传路径
     *
     * @param dir
     */
    private static void setUploadDir(String dir) {
        uploadDir = dir;
        if (uploadDir.startsWith(SLASH)) {
            uploadDir = uploadDir.substring(1);
        }
        if (!uploadDir.endsWith(SLASH)) {
            uploadDir += SLASH;
        }
    }

    public static String getTmpSecretId() {
        return tmpSecretId;
    }

    public static void setTmpSecretId(String tmpSecretId) {
        QcloudCosUtil.tmpSecretId = tmpSecretId;
    }

    public static String getTmpSecretKey() {
        return tmpSecretKey;
    }

    public static void setTmpSecretKey(String tmpSecretKey) {
        QcloudCosUtil.tmpSecretKey = tmpSecretKey;
    }

    /**
     * 获取默认的有效期（毫秒值）
     *
     * @return
     */
    public static Long getDefaultExpiredMills() {
        return expiredMills;
    }

    /**
     * 获取默认的上传路径
     *
     * @return
     */
    public static String getUploadDir() {
        return uploadDir;
    }

    /**
     * 获取POST上传Token
     *
     * @param expiredMills
     * @return
     */
    public static String getPostToken(Long expiredMills) {
        return getPostToken(SLASH, expiredMills);
    }

    /**
     * 获取POST上传Token
     *
     * @param filename     上传文件名称
     * @param expiredMills Token有效期（毫秒值）
     * @return token
     */
    private static String getPostToken(String filename, Long expiredMills) {
        return getToken(filename, HttpMethodName.POST, expiredMills);
    }

    /**
     * 获取上传用Token
     *
     * @param filename     上传文件名称
     * @param methodName   上传方式
     * @param expiredMills Token有效期（毫秒值）
     * @return token
     */
    public static String getToken(String filename, HttpMethodName methodName, Long expiredMills) {
        COSSigner signer = new COSSigner();
        Date expiredTime = new Date(System.currentTimeMillis() + expiredMills);
        return signer.buildAuthorizationStr(methodName, filename, cred, expiredTime);
    }

    /**
     * 获取上传链接
     *
     * @return
     */
    public static String getHost() {
        return host;
    }

    public static String getRegionName() {
        return regionName;
    }

    public static String getAppID() {
        return appID;
    }

    public static void setAppID(String appID) {
        QcloudCosUtil.appID = appID;
    }

    public static void setRegionName(String regionName) {
        QcloudCosUtil.regionName = regionName;
    }

    public static String getBucketName() {
        return bucketName;
    }

    public static void setBucketName(String bucketName) {
        QcloudCosUtil.bucketName = bucketName;
    }

    public static String getStaticHost() {
        return staticHost;
    }

    public static void setStaticHost(String staticHost) {
        QcloudCosUtil.staticHost = staticHost;
    }

    public static String uploadSuffix(MultipartFile file) {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        return upload(file, getPath(uploadDir, suffix));
    }

    /**
     * 文件路径
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    public static String getPath(String prefix, String suffix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = DateUtil.format(new Date(), "yyyyMMdd") + "/" + "COINDOUAPP/" + uuid;

        if (StrUtil.isNotBlank(prefix)) {
            path = prefix + path;
        }

        return path + suffix;
    }

    public static String upload(MultipartFile file, String path) {
        String key = path;

        //初始化客户端配置
        ClientConfig clientConfig = new ClientConfig(new Region(regionName));
        //设置bucket所在的区域，华南：gz 华北：tj 华东：sh

        COSClient client = new COSClient(cred, clientConfig);
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
            objectMetadata.setContentLength(file.getSize());
            // 默认下载时根据cos路径key的后缀返回响应的contenttype, 上传时设置contenttype会覆盖默认值
            objectMetadata.setContentType(file.getContentType());
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            PutObjectResult putObjectResult = client.putObject(putObjectRequest);
            // putobjectResult会返回文件的etag
            String etag = putObjectResult.getETag();

            String url = staticHost + "/" + key;
            return url;
        } catch (CosServiceException e) {
            log.error("内部异常", e);
//            json(SystemCode.code_1002, L("上传文件失败"));
        } catch (CosClientException e) {
            log.error("内部异常", e);
//            json(SystemCode.code_1002, L("上传文件失败"));
        } catch (IOException e) {
            log.error("内部异常", e);
//            json(SystemCode.code_1002, L("上传文件失败"));
        } finally {
            // 关闭客户端
            client.shutdown();
        }
        return null;
    }


    //根据昵称生成头像上传
    public static String upload(File file, String path) {
        String key = path;

        //初始化客户端配置
        ClientConfig clientConfig = new ClientConfig(new Region(regionName));
        //设置bucket所在的区域，华南：gz 华北：tj 华东：sh

        COSClient client = new COSClient(cred, clientConfig);
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
            objectMetadata.setContentLength(file.length());
            // 默认下载时根据cos路径key的后缀返回响应的contenttype, 上传时设置contenttype会覆盖默认值
            objectMetadata.setContentType("image/jpeg");
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, new FileInputStream(file), objectMetadata);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            PutObjectResult putObjectResult = client.putObject(putObjectRequest);
            // putobjectResult会返回文件的etag
            String etag = putObjectResult.getETag();

            String url = staticHost + "/" + key;
            return url;
        } catch (CosServiceException e) {
            log.error("内部异常", e);
//            json(SystemCode.code_1002, L("上传文件失败"));
        } catch (CosClientException e) {
            log.error("内部异常", e);
//            json(SystemCode.code_1002, L("上传文件失败"));
        } catch (IOException e) {
            log.error("内部异常", e);
//            json(SystemCode.code_1002, L("上传文件失败"));
        } finally {
            // 关闭客户端
            client.shutdown();
        }
        return null;
    }

    public static JSONObject qCloud(String allowPrefix) {
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        JSONObject json= new JSONObject();

        try {
            // 替换为您的 SecretId
            config.put("SecretId", tmpSecretId);
            // 替换为您的 SecretKey
            config.put("SecretKey", tmpSecretKey);

            // 临时密钥有效时长，单位是秒
            config.put("durationSeconds", 1800);

            // 换成您的 bucket
            config.put("bucket", bucketName);
            // 换成 bucket 所在地区
            config.put("region", regionName);

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的目录，例子：* 或者 doc/* 或者 picture.jpg
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String str=sdf.format(new Date());
            config.put("allowPrefix", allowPrefix+"*");

            // 密钥的权限列表。简单上传、表单上传和分片上传需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[]{
                    // 简单上传
                    "name/cos:PutObject",
                    // 表单上传、小程序上传
                    "name/cos:PostObject",
                    // 分片上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);

            JSONObject credential = CosStsClient.getCredential(config);
            //成功返回临时密钥信息，如下打印密钥信息
            System.out.println(credential);
            return credential;
        } catch (Exception e) {
            //失败抛出异常
            throw new IllegalArgumentException("no valid secret !");
        }
    }

    public static void main(String[] args) {
        TreeMap<String, Object> config = new TreeMap<String, Object>();

        try {
            // 替换为您的 SecretId
            config.put("SecretId", tmpSecretId);
            // 替换为您的 SecretKey
            config.put("SecretKey", tmpSecretKey);

            // 临时密钥有效时长，单位是秒
            config.put("durationSeconds", 1800);

            // 换成您的 bucket
            config.put("bucket", bucketName);
            // 换成 bucket 所在地区
            config.put("region", regionName);

            // 这里改成允许的路径前缀，可以根据自己网站的用户登录态判断允许上传的目录，例子：* 或者 doc/* 或者 picture.jpg
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String str=sdf.format(new Date());
            config.put("allowPrefix", "*");

            // 密钥的权限列表。简单上传、表单上传和分片上传需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[]{
                    // 简单上传
                    "name/cos:PutObject",
                    // 表单上传、小程序上传
                    "name/cos:PostObject",
                    // 分片上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);

            JSONObject credential = CosStsClient.getCredential(config);
            //成功返回临时密钥信息，如下打印密钥信息
            System.out.println(credential);
        } catch (Exception e) {
            //失败抛出异常
            throw new IllegalArgumentException("no valid secret !");
        }
    }
}
