package com.world.util.ip;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.world.util.ip.impl.IPLocation;
import com.world.util.ip.impl.IPSeeker;
import com.world.util.ip.impl.Util;
import com.world.util.request.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IpUtil {
    protected static Logger log = Logger.getLogger(IpUtil.class.getName());

    /***
     * 获取ip所在的省份
     * @param ip
     * @return
     */
    public static String getProvince(String ip) {
        return getLocation(ip).getProvince();
    }

    /****
     * 获取ip所在的市
     * @param ip
     * @return
     */
    public static String getCity(String ip) {
        String city = getCityBySina(ip);

        if (city == null) {
            IPLocation location = getLocation(ip);
            city = location.getCity();//getLocation(ip).getCity();
            if (city == null) {
                city = location.getProvince();
            }

            if (city == null) {
                city = location.getCountry();
            }
        }

        return city;
    }

    /****
     * 获取ip所在的市
     * @param ip
     * @return
     */
    public static String getCityByTaobao(String ip) throws UnsupportedEncodingException {
        /******Start by gkl 20190524 过滤ipv6 **********************/
        String city = "";
        if (Util.ipCheck(ip)) {
            city = getAddresses("ip=" + ip, "utf-8");
        } else {
            city = "ipv6";
        }
        /******End***/
        if (city == null) {
            IPLocation location = getLocation(ip);
            city = location.getCity();//getLocation(ip).getCity();
            if (city == null) {
                city = location.getProvince();
            }

            if (city == null) {
                city = location.getCountry();
            }
        }

        return city;
    }


    /****
     * 获取ip所在的市
     * @param ip
     * @return
     */
    public static String getCityByIpApi(String ip,String key) throws UnsupportedEncodingException {
        /******Start by Elysion 20190905 更换ip解析api **********************/
        String city = getProvinceByIpApi(ip, key);
        /******End***/
        if (StringUtils.isEmpty(city)) {
            IPLocation location = getLocation(ip);
            city = location.getCity();//getLocation(ip).getCity();
            if (city == null) {
                city = location.getProvince();
            }

            if (city == null) {
                city = location.getCountry();
            }
        }

        return city;
    }


    /**
     * 通过新浪查询ip归属地
     *
     * @param ip
     * @return
     */
    public static String getCityBySina(String ip) {
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip;

        try {
            String resp = HttpUtil.doGet(httpUrl, null);
            JSONObject jsonObject = JSON.parseObject(resp);
            if (jsonObject.getInteger("ret") == 1) {

                if (StringUtils.isNotBlank(jsonObject.getString("city"))) {
                    return new String(jsonObject.getString("city"));
                }

                if (StringUtils.isNotBlank(jsonObject.getString("province"))) {
                    return new String(jsonObject.getString("province"));
                }

                if (StringUtils.isNotBlank(jsonObject.getString("country"))) {
                    return new String(jsonObject.getString("country"));
                }
            }
        } catch (Exception e) {
        }

        return null;
    }


    /****
     * 获取ip所在的详细区域
     * @param ip
     * @return
     */
    public static String getArea(String ip) {
        return getLocation(ip).getArea();
    }

    private static IPLocation getLocation(String ip) {
        IPSeeker seeker = IPSeekerFactory.getSeeker();
        return seeker.getIPLocation(ip);
    }

    public static String getPre(String ip) {
        log.info(ip + "===1");
        String[] ipList = ip.split(",");
        String strIp = new String();
        for (int index = 0; index < ipList.length; index++) {
            strIp = ipList[index];
            if (!("unknown".equalsIgnoreCase(strIp))) {
                ip = strIp.trim();
                break;
            }
        }
        return ip;
    }

//	public static String getIp(HttpServletRequest request) {
//		String ip = request.getHeader("x-forwarded-for");  
//		log.info("第一步：" + ip);
//		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
//			ip = request.getHeader("Proxy-Client-IP");  
//			log.info("第二步：" + ip);
//		}  
//		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
//			ip = request.getHeader("WL-Proxy-Client-IP");
//			log.info("第三步：" + ip);
//			log.info(ip+"===2");
//			if(ip.contains(",")){
//				return getPre(ip); 
//			}else{
//				return ip;
//			}
//		}  
//		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
//			ip = request.getRemoteAddr();  
//			log.info("第四步：" + ip);
//		}  
//		
//		if(ip.contains(",")){
//			return getPre(ip); 
//		}else{
//			return ip;
//		}
//	} 

    //获取ip的方法
    public static String getIp(HttpServletRequest request) {
//		String ip = request.getHeader("Cdn-Src-Ip");// request.getHeader("X-Real-IP");
        ///使用cdn时候才会有值   使用加速乐时候的情况
        String cdn = request.getHeader("X-Connecting-IP");

        if (cdn != null) {
            return cdn;
        }

        String ip = request.getHeader("x-forwarded-for");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP"); // 获取代理ip
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr(); // 获取真实ip
        }

        if (ip.indexOf(",") >= 0) {
            //log.error("x-forwarded-for:" + ip + ",");
            String[] ips = ip.split(",");
            for (int i = 0; i < ips.length; i++) {
                ip = ips[i];
                if (!"".equals(ip) && !"unknown".equalsIgnoreCase(ip) && !"127.0.0.1".equals(ip))
                    break;
            }
        }

        //log.info("ip:"+ip);
        /*Start by guankaili 20190716 trim */
        if (StringUtils.isNotEmpty(ip)) {
            ip = ip.trim();
        }
        /*End*/
        return ip;
    }

    /***
     * 获取请求者ip
     * @param request
     * @return
     */
    public static String getVisitorCity(HttpServletRequest request) {
        String city = getCity(getIp(request));
        if (city != null) {
            return city;
        } else {
            return "总";
        }
    }


    // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
    public static String getIpAddr(HttpServletRequest request) {
        String strClientIp = request.getHeader("x-forwarded-for");
        if (strClientIp == null || strClientIp.length() == 0 || "unknown".equalsIgnoreCase(strClientIp)) {
            strClientIp = request.getRemoteAddr();
        } else {
            //List<String> ipList = new ArrayList();
//        String[] ipList = strClientIp.split(",");
            //BusiAcceptAction.SplitsString(strClientIp, ',' , ipList); // 拆分字符串，可直接用String.plit方法
//        String strIp = new String();
//        for(int index = 0; index < ipList.length; index ++){
//            strIp = ipList[index];
//            if(!("unknown".equalsIgnoreCase(strIp))){
//                strClientIp = strIp;
//                break;
//            }
//        }
        }

        return strClientIp;
    }

    /**
     * 通过淘宝IP查询接口，获取国家
     *
     * @param ip
     * @return
     */
    public static String getProvinceBySina(String ip) {
        StringBuffer sbf = new StringBuffer();
        String httpUrl = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip;

        try {
            String resp = HttpUtil.doGet(httpUrl, null);

            JSONObject jsonObject = JSON.parseObject(resp);
            if (jsonObject.getInteger("ret") == 1) {
                if (StringUtils.isNotBlank(jsonObject.getString("country"))) {
                    String contry = new String(jsonObject.getString("country"));
                    if (contry.equals("中国")) {
                        String province = "";
                        if (StringUtils.isNotBlank(jsonObject.getString("province"))) {
                            province = jsonObject.getString("province");
                            if (province.equals("香港") || province.equals("澳门") || province.equals("台湾")) {
                                return province;
                            }
                        }
                    }
                    return new String(jsonObject.getString("country"));
                }
            }
        } catch (Exception e) {
            log.info("通过新浪IP查询接口获取结果失败,ip:" + ip, e);
        }

        return null;
    }


    /**
     * 通过淘宝IP查询接口，获取国家
     *
     * @param ip
     * @return
     */
    public static String getProvinceByIpApi(String ip,String key) {
        String httpUrl = "http://pro.ip-api.com/json/" + ip + "?key=" + key + "&fields=status,country,regionName,city,query&lang=zh-CN";
        try {
            Long start = System.currentTimeMillis();
            String resp = HttpUtil.doGet(httpUrl, null);
            Long end = System.currentTimeMillis();
            log.info("调用IP-API接口耗时:" + (end - start));
            JSONObject jsonObject = JSON.parseObject(resp);
            if ("success".equals(jsonObject.getString("status"))) {
                String contry = jsonObject.getString("country");
                String province = jsonObject.getString("regionName");
                String city = jsonObject.getString("city");
                return contry + province + city;
            }
        } catch (Exception e) {
            log.info("通过IP-API查询接口获取结果失败,ip:" + ip, e);
        }

        return null;
    }


    /**
     * 判断是否是国内IP
     *
     * @param ip
     * @return
     */
    public static boolean isPublicIp(String ip) {
        String ALLOWABLE_IP_REGEX = "(127[.]0[.]0[.]1)|" + "(localhost)|" +
                "(10[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3})|" +
                "(172[.]((1[6-9])|(2\\d)|(3[01]))[.]\\d{1,3}[.]\\d{1,3})|" +
                "(192[.]168[.]\\d{1,3}[.]\\d{1,3})|" +
                "(100[.]([6-9][4-9]|1[01]\\d|12[0-7])[.]\\d{1,3}[.]\\d{1,3})";
        Pattern p = Pattern.compile(ALLOWABLE_IP_REGEX);
        Matcher matcher = p.matcher(ip);
        return !matcher.find();
    }

    /**
     * 判断是否是Ipv4
     *
     * @param ip
     * @return
     */
    public static boolean isIpv4(String ip) {
        try {
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

            Pattern p = Pattern.compile(regex);
            Matcher matcher = p.matcher(ip);
            return matcher.find();
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * @param content  请求的参数 格式为：name=xxx&pwd=xxx
     * @param encoding 服务器端请求编码。如GBK,UTF-8等
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getAddresses(String content, String encoding)
            throws UnsupportedEncodingException {
        // 这里调用淘宝API
        String urlStr = "http://ip.taobao.com/service/getIpInfo.php";
        // 从http://whois.pconline.com.cn取得IP所在的省市区信息
        String returnStr = getResult(urlStr, content, encoding);
        if (returnStr != null) {
            // 处理返回的省市区信息
            System.out.println("(1) unicode转换成中文前的returnStr : " + returnStr);
            returnStr = decodeUnicode(returnStr);
            System.out.println("(2) unicode转换成中文后的returnStr : " + returnStr);
            String[] temp = returnStr.split(",");
            if (temp.length < 3) {
                return "0";//无效IP，局域网测试
            }
            JSONObject jsonObject = JSONObject.parseObject(returnStr);
            if (null != jsonObject) {
                String dataStr = jsonObject.getString("data");
                JSONObject jsonObject1 = JSONObject.parseObject(dataStr);
                if (null != jsonObject1) {
                    returnStr = jsonObject1.getString("city");
                }
            }
            return returnStr;
        }
        return null;
    }

    /**
     * @param urlStr   请求的地址
     * @param content  请求的参数 格式为：name=xxx&pwd=xxx
     * @param encoding 服务器端请求编码。如GBK,UTF-8等
     * @return
     */
    private static String getResult(String urlStr, String content, String encoding) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();// 新建连接实例
            connection.setConnectTimeout(2000);// 设置连接超时时间，单位毫秒
            connection.setReadTimeout(2000);// 设置读取数据超时时间，单位毫秒
            connection.setDoOutput(true);// 是否打开输出流 true|false
            connection.setDoInput(true);// 是否打开输入流true|false
            connection.setRequestMethod("POST");// 提交方法POST|GET
            connection.setUseCaches(false);// 是否缓存true|false
            connection.connect();// 打开连接端口
            DataOutputStream out = new DataOutputStream(connection
                    .getOutputStream());// 打开输出流往对端服务器写数据
            out.writeBytes(content);// 写数据,也就是提交你的表单 name=xxx&pwd=xxx
            out.flush();// 刷新
            out.close();// 关闭输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), encoding));// 往对端写完数据对端服务器返回数据
            // ,以BufferedReader流来读取
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            return buffer.toString();
        } catch (IOException e) {
            log.info("通过淘宝IP查询接口获取结果失败");
        } finally {
            if (connection != null) {
                connection.disconnect();// 关闭连接
            }
        }
        return null;
    }

    /**
     * unicode 转换成 中文
     *
     * @param theString
     * @return
     * @author fanhui 2007-3-15
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed      encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    // 测试
    public static void main(String[] args) {

        // 测试ip 219.136.134.157 中国=华南=广东省=广州市=越秀区=电信
        String ip = "222.173.105.146";
        String address = "";
        try {
            address = getCityByIpApi(ip,"nUJXsosjRSlfk7r");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(address);
        // 输出结果为：广东省,广州市,越秀区
    }

}
