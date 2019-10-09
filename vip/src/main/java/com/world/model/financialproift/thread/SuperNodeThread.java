package com.world.model.financialproift.thread;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Data;
import com.world.model.dao.financialproift.FinancialSuperNodeDao;
import com.world.model.entity.financialproift.SuperNode;
import com.world.util.crawler.balance.regex.StringRegexUtil;

/**
 * @author yeqing
 */
public class SuperNodeThread extends Thread {

    private static Logger log = Logger.getLogger(SuperNodeThread.class.getName());
    private static final Integer TIMEOUT_MILLS = 30000;
    private static final String CARACTERSET = "UTF-8";
    public static BigDecimal DIVIDE_DEGREE = new BigDecimal(1E8);
//    private static String lateMiningTime;
//    private static String lateMiningAmount;
    private static String sizeSelector = "data>totalCount";
    private static String Selector = "data>list[{i}]>transaction";
    private static String urlPattern = "https://vdsblock.io/index.php/api/tx-address-list/{address}?page={pageIndex}";
    private static int pageSize = 10;


    private FinancialSuperNodeDao financialSuperNodeDao = new FinancialSuperNodeDao();
    private SuperNode superNode;
    private CountDownLatch countDownLatch;

    public SuperNodeThread() {
    }

    public SuperNodeThread(SuperNode superNode, CountDownLatch countDownLatch) {
        this.superNode = superNode;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            refreshData(superNode);
        } catch (Exception e) {
            log.error("理财报警ERROR:SuperNodeThread", e);
        } finally {
            countDownLatch.countDown();
        }

    }


    public void refreshData(SuperNode superNode) {

        //1、获取超级节点余额
        Object balance = getCurrentBalance(superNode);
        BigDecimal currentBalance = BigDecimal.ZERO;
        if (balance != null) {
            currentBalance = new BigDecimal(balance.toString());
        }
        currentBalance = currentBalance.divide(DIVIDE_DEGREE);
        log.info("从 vds cool 返回的余额为:" + currentBalance);

        //2、若余额大于0，则更新该地址余额
        if (currentBalance.compareTo(BigDecimal.ZERO) == 1) {
            financialSuperNodeDao.updateBalance(superNode, currentBalance);
        }

        //3 更新挖矿最近产出数量及时间
        ConcurrentMap<String, String> map = getLateMiningAmountAndTime(superNode, 1);
        if (map == null || map.isEmpty()) {
            log.info("从vds cool 超级节点地址:" + superNode.getsNodeAddr() + " 未发现挖矿信息");
        } else {
            String amount = map.get("amount");
            String createtime = map.get("createtime");
            log.info("从vds cool 超级节点地址:" + superNode.getsNodeAddr() + " 返回的挖矿最近产出数量为:" + amount + ",挖矿最近产出时间为:" + createtime);
            financialSuperNodeDao.updateLateTimeAmount(superNode, amount, createtime);
        }

        //4、记录超级节点控矿收益明细
        long lastHeight = getLastHeight(superNode);
        //记录超级节点控矿收益明细
        saveMiningDetail(superNode, lastHeight, 1);

    }

    /**
     * 获取节点最后解析高度
     * @param superNode
     * @return
     */
    @SuppressWarnings("unchecked")
	private long getLastHeight(SuperNode superNode) {
        String sql = "select ifnull(max(height),0) from fin_supernode_mining_detail where sNodeId = " + superNode.getId();
        List<Long> heightList = (List<Long>) Data.GetOne("vip_financial", sql, null);
        if (heightList != null && heightList.size() > 0) {
            long height = heightList.get(0);
            log.info("超级节点:" + superNode.getsNodeName() + ",地址:" + superNode.getsNodeAddr() + ",最后解析区块高度:" + height);
            return height;
        }
        return 0;
    }

    /**
     * 记录超级节点控矿收益明细
     *
     * @param superNode
     * @param lastHeight
     */
    private void saveMiningDetail(SuperNode superNode, long lastHeight, int page) {
        try {
            String uri = urlPattern.replace("{address}", superNode.getsNodeAddr()).replace("{pageIndex}", String.valueOf(page));
            GetMethod request = new GetMethod(uri);
            String result = doHttp(request, TIMEOUT_MILLS, CARACTERSET);
            //总条目数
            JSONObject json = JSON.parseObject(result);
            int code = json.getIntValue("code");
            //获取结果失败
            if (code != 1) {
                return;
            }
            JSONObject data = json.getJSONObject("data");
            //总条目数
            int total = data.getIntValue("totalCount");

            //总页数
            int pages = (int) Math.ceil((float) total / pageSize);
            JSONArray data_list = data.getJSONArray("list");

            int size = data_list.size();
            String sql = "";
            for (int i = 0; i < size; i++) {
                JSONObject data_list_i = JSON.parseObject(data_list.get(i).toString());
                JSONObject transaction = data_list_i.getJSONObject("transaction");
                int is_coinbase = transaction.getIntValue("is_coinbase");
                if (is_coinbase == 1) {
                    long tid = transaction.getLongValue("tid");
                    String tx_hash = transaction.getString("tx_hash");
                    long height = transaction.getLongValue("height");
                    String create_time = transaction.getString("create_time");
                    if (height == lastHeight && lastHeight != 0) {
                        //当前区块高度与上次解析区块高度相同
                        break;
                    }
                    JSONArray outputArray = transaction.getJSONArray("output");
                    if (outputArray == null) {
                        continue;
                    }
                    for (int j = 0; j < outputArray.size(); j++) {
                        JSONObject outputObj = outputArray.getJSONObject(j);
                        String out_address = outputObj.getString("address");
                        String miningAcmount = outputObj.getString("amount");
                        if (superNode.getsNodeAddr().equals(out_address)) {
                            sql = "insert into fin_supernode_mining_detail " 
                            	+ "(sNodeId,sNodeName, sNodeAddr, MiningAmount, createTime, height, tid, tx_hash, "
                            	+ "sNodeState, sNodeType, sNodeBelType, sNodeShowFlag) "
                            	+ "values (" + superNode.getId() + ",'" + superNode.getsNodeName() + "', '" + superNode.getsNodeAddr() + "', "
                            	+ "" + miningAcmount + ", '" + create_time + "', " + height + ", " + tid + ", '" + tx_hash + "', "
                            	+ "" + superNode.getsNodeState() + ", " + superNode.getsNodeType() + ", "
                            	+ "" + superNode.getsNodeBelType() + ", " + superNode.getsNodeShowFlag() + " )";
                            log.info("SuperNodeThread sql = " + sql);
                            Data.Insert("vip_financial", sql, null);
                        }
                    }
                }
            }
            if (pages > page) {
                page++;
                saveMiningDetail(superNode, lastHeight, page);
            }

        } catch (Exception e) {
            log.error("理财报警:记录超级节点挖矿收益记录时发生异常", e);
        }
    }


    private static Object getCurrentBalance(SuperNode sn) {
        try {
            String urlPattern = "https://www.vds.cool/api/BTC/livenet/address/" + sn.getsNodeAddr() + "/balance";
            GetMethod request = new GetMethod(urlPattern);
            String result = doHttp(request, TIMEOUT_MILLS, CARACTERSET);
            if (StringUtils.isNotEmpty(result)) {
                JSONObject model = JSONObject.parseObject(result);
                if (model != null) {
                    return model.get("balance");
                } else {
                    log.info("从 vds cool 返回的余额为空.");
                }
            }

        } catch (Exception e) {
            log.error("理财报警ERROR:同步超级节点信息,爬虫获取当前区块高度异常", e);
        }
        return null;
    }


    /**
     * https://www.cnblogs.com/hwaggLee/p/5210889.html
     *
     * @param request
     * @param timeout
     * @param charset
     * @return
     */
    public static String doHttp(HttpMethod request, int timeout, String charset) {
        HttpClient client = new HttpClient();
        try {

            HttpConnectionManagerParams managerParams = client.getHttpConnectionManager().getParams();
            //连接超时
            managerParams.setConnectionTimeout(timeout);
            //等待结果超时
            managerParams.setSoTimeout(timeout);
            client.getHttpConnectionManager().setParams(managerParams);

            request.setRequestHeader(HttpHeaders.ACCEPT, "application/json");
            request.setRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");

            int code = client.executeMethod(request);
            if (code == 200) {
                String result = request.getResponseBodyAsString();
                log.info("vds cool <<< " + result);
                return result;
            } else {
                log.info("vds cool code " + code);
            }

        } catch (HttpException e) {
            log.error("理财报警ERROR:vds cool HttpException：", e);
        } catch (IOException e) {
            try {
                log.error("理财报警ERROR: 接口url=" + request.getURI() + "连接超时 IOException：", e);
            } catch (URIException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            log.error("理财报警ERROR:vds cool Exception：", e);
        } finally {
            request.releaseConnection();
        }
        return null;
    }

    /**
     * 获取超级节点挖矿最近产出数量、时间
     *
     * @param superNode
     * @return
     */
    private static ConcurrentMap<String, String> getLateMiningAmountAndTime(SuperNode superNode, int page) {
        ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

        try {
            String uri = urlPattern.replace("{address}", superNode.getsNodeAddr()).replace("{pageIndex}", String.valueOf(page));
            log.info("获取超级节点挖矿最近产出数量、时间uri=" + uri);
            GetMethod request = new GetMethod(uri);
            String result = doHttp(request, TIMEOUT_MILLS, CARACTERSET);
            //总条目数
            String sum = jsonRequestParser(result, sizeSelector);
            if (StringUtils.isEmpty(sum)) {
                return null;
            }
            int total = Integer.parseInt(sum);
            //总页数
            int pages = (int) Math.ceil((float) total / pageSize);
            //判断
            String transSelector = Selector.replace("{i}", String.valueOf(page - 1));
            JSONObject transObj = JSONObject.parseObject(jsonRequestParser(result, transSelector));
            if (isCoinBase(transObj)) {
                String create_time = transObj.getString("create_time");
                JSONArray outputArray = transObj.getJSONArray("output");
                for (int j = 0; j < outputArray.size(); j++) {
                    JSONObject outputObj = outputArray.getJSONObject(j);
                    String out_address = outputObj.getString("address");
                    String out_amount = outputObj.getString("amount");
                    if (superNode.getsNodeAddr().equals(out_address)) {
                        map.put("createtime", create_time);
                        map.put("amount", out_amount);
                        return map;
                    }
                }
            }
            if (pages > page) {
                page++;
                getLateMiningAmountAndTime(superNode, page);
            }
        } catch (Exception e) {
            log.error("理财报警ERROR：com.world.model.financialproift.thread.SuperNodeThread#getLateMiningAmountAndTime 获取超级节点挖矿最近产出数量、时间异常", e);
            return null;
        }
        return null;
    }

    /**
     * 是否coinbase交易
     *
     * @param jsonObject
     * @return
     */
    private static boolean isCoinBase(JSONObject jsonObject) {
        if (jsonObject != null && "1".equals(jsonObject.getString("is_coinbase"))) {
            return true;
        }
        return false;
    }

    private static String jsonRequestParser(String jsonRes, String selector) {
        if (StringUtils.isEmpty(jsonRes)) {
            return null;
        }
        //最后字段
        String unFilterRs = null;
        JSON json = JSONObject.parseObject(jsonRes);
        //获取字段选择器
        String[] selectorArray = selector.split("\\>");

        if (selectorArray != null) {
            int length = selectorArray.length;
            for (int i = 0; i < selectorArray.length; i++) {
                //jsonobject or jsonarray key
                String key = selectorArray[i];
                //进行正则匹配，是否符合 [x]
                Pattern pattern = Pattern.compile("\\[\\d+\\]");
                Matcher matcher = pattern.matcher(key);
                Boolean arrayFlag = matcher.find();
                if (arrayFlag) {
                    //获得[index]
                    String subSeqStr = matcher.group();
                    //获得JsonArray的name
                    String arrayName = key.replaceAll(StringRegexUtil.str2Regex(subSeqStr), "").trim();
                    //获得JsonArray的index
                    Integer index = Integer.valueOf(subSeqStr.replaceAll("\\[", "").replaceAll("\\]", "").trim());
                    //获得key=${arrayName}的jsonarray
                    JSONArray jsonArray = ((JSONObject) json).getJSONArray(arrayName);
                    //为下级解析准备json
                    json = jsonArray.getJSONObject(index);
                } else {
                    //普通对象或者字段
                    if ((i + 1) == length) {
                        //最后一个是字段
                        unFilterRs = ((JSONObject) json).getString(key.trim());
                        return unFilterRs;
                    } else {
                        //是对象
                        json = ((JSONObject) json).getJSONObject(key.trim());
                    }
                }
            }
        }
        return null;
    }

}
