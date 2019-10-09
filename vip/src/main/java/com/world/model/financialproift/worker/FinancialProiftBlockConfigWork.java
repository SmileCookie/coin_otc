package com.world.model.financialproift.worker;

import java.io.IOException;
import java.util.Date;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.model.dao.financialproift.FinancialProiftBlockConfigDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.ProfitBlockConfig;

/**
 * @author yeqing
 */
public class FinancialProiftBlockConfigWork extends Worker {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(FinancialProiftBlockConfigWork.class);
    private static final Integer TIMEOUT_MILLS = 10000;
    private static final String CARACTERSET = "UTF-8";
    /**
     * 区块类型 1当前区块，只能有1个。2分红区块，维护添加配置，可以N个。
     */
    private static final int BLOCK_TYPE_CURRENT = 1;
    private static final int BLOCK_TYPE_PROFIT = 2;
    private static final String FINANCIAL_CURRENT_BLOCK = "financial_currentBlock";
    private static final String FINANCIAL_PROFIT_BLOCK = "financial_profitBlock";

    FinancialProiftBlockConfigDao financialProiftBlockConfigDao = new FinancialProiftBlockConfigDao();

    public FinancialProiftBlockConfigWork(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        /*记录核算开始时间*/
        long startTime = System.currentTimeMillis();
        /*现在时间获取*/
        log.info("理财报警AINFO:【同步当前区块和分红区块】开始, startTime={}", new Date());
        try {
            //1、爬虫获取当前VDS区块高度
            Object current = getCurrentHeightFromVdsCool();
            Integer currentHeight = (Integer) current;
            if (currentHeight == null) {
                log.info("[同步当前区块和分红区块] currentHeight == null");
                return;
            }
            
            //2、获取当前数据库存储的最新区块高度
            Long blockHeight = financialProiftBlockConfigDao.selectMaxBlockHeight(BLOCK_TYPE_CURRENT);

            //3、如果数据库无最新高度信息，则新增一条当前区块高度信息
            if (blockHeight == null) {
                financialProiftBlockConfigDao.insertBlockHeight(BLOCK_TYPE_CURRENT, currentHeight, FINANCIAL_CURRENT_BLOCK);
            }

            //4、如果爬虫高度大于数据库最新高度，则更新当前区块高度
            if (currentHeight > blockHeight) {
                financialProiftBlockConfigDao.updateCurrentBlockHeight(BLOCK_TYPE_CURRENT, currentHeight, FINANCIAL_CURRENT_BLOCK);
            }

            //5、查询分红区块高度
            ProfitBlockConfig profitBlockConfig = financialProiftBlockConfigDao.selectMinProfitHeight(BLOCK_TYPE_PROFIT, BLOCK_TYPE_CURRENT);
            if (profitBlockConfig != null && profitBlockConfig.getBlockHeight() > 0) {
                //6、更新分红区块高度
                financialProiftBlockConfigDao.updateProfitBlockHeight(profitBlockConfig, FINANCIAL_PROFIT_BLOCK);
            }
            long endTime = System.currentTimeMillis();
            log.info("理财报警AINFO:【同步当前区块和分红区块】结束!核算耗时【" + (endTime - startTime) + "】当前区块 = " + currentHeight);
        } catch (Exception e) {
        	log.info("理财报警ERROR:InviTotalNumWork", e);
        }
    }

    /**
     * 从VDS COOL 获取当前VDS区块高度
     *
     * @return
     */
    private static Object getCurrentHeightFromVdsCool() throws IOException {
        try {
//            HttpClient client = new HttpClient();
            String urlPattern = "https://www.vds.cool/api/BTC/livenet/block";
            GetMethod request = new GetMethod(urlPattern);
            String result = doHttp(request, TIMEOUT_MILLS, CARACTERSET);
            if (StringUtils.isNotEmpty(result)) {
                JSONArray list = JSONArray.parseArray(result);
                if (list != null && list.size() > 0) {
                    JSONObject currentHeightModel = (JSONObject) list.get(0);
                    return currentHeightModel.get("height");
                } else {
                    log.info("从 vds cool 返回的高度列表为空.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("[同步当前区块和分红区块] 爬虫获取当前区块高度异常", e);
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
            log.info("vds cool HttpException：" + e);
            e.printStackTrace();
        } catch (IOException e) {
            log.info("vds cool IOException：" + e);
            e.printStackTrace();
        } catch (Exception e) {
            log.info("vds cool Exception：" + e);
            e.printStackTrace();
        } finally {
            request.releaseConnection();
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        /**
         * 后续
         * 1、在main.properties里配置定时任务开关
         * task0 FinancialProiftBlockConfigWork=true
         * task1 FinancialProiftBlockConfigWork=false
         * 2、在messi\vip_conf\src\main\resources\mysql.json 配置vip_main的连接
         */
        FinancialProiftBlockConfigWork financialProiftBlockConfigWork = new FinancialProiftBlockConfigWork("FinancialProiftBlockConfigWork", "【同步当前区块和分红区块】");
        financialProiftBlockConfigWork.run();
    }
}
