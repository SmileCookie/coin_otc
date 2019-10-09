package com.world.model.dao.auto.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.model.dao.task.Worker;
import com.world.model.entity.LegalTenderType;
import com.world.util.request.HttpUtil;
import com.world.util.string.StringUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SynForeignPriceWorker  extends Worker {

    public static Logger LOGGER = Logger.getLogger(SynForeignPriceWorker.class.getName());

    public SynForeignPriceWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        super.run();
        //从中国人民银行同步法币汇率
        getPriceFromPBOC();
    }
    /**
     * 从中国人民银行获取各个币种对人民币的价格
     */
    private void getPriceFromPBOC() {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization","APPCODE " + GlobalConfig.waihuiAppCode);
            String response = HttpUtil.doGet(GlobalConfig.waihuiUrl, null, headers);
            LOGGER.info("ali-waihui <<< " + response);
            if (StringUtil.exist(response)) {
                JSONObject json = JSONObject.parseObject(response);
                if (json != null && json.getInteger("showapi_res_code").intValue() == 0) {
                    JSONObject body = json.getJSONObject("showapi_res_body");
                    if (body != null) {
                        JSONArray jsonArray = body.getJSONArray("list");
                        if (jsonArray != null && jsonArray.size() > 0) {
                            List<String> legalTenders = LegalTenderType.getkeys();
                            for (Object object : jsonArray) {
                                String code = ((JSONObject) object).getString("code");
                                if(!"cny".equalsIgnoreCase(code) && legalTenders.contains(code.toUpperCase())){

                                    LOGGER.info("从中国人民银行同步 [" + ((JSONObject) object).getString("name").toLowerCase() + " 对 人民币] 价格 : "
                                            + new BigDecimal(((JSONObject) object).getString("zhesuan")).divide(new BigDecimal(100)).toPlainString());

                                    Cache.Set(((JSONObject) object).getString("code").toLowerCase() + "_cny",
                                            new BigDecimal(((JSONObject) object).getString("zhesuan")).divide(new BigDecimal(100)).toPlainString());
                                }
                            }
                        }
                    }
                } else {
                    LOGGER.error("从中国人民银行同步外汇价格失败 : " + response);
                }
            }
        } catch (IOException e) {
            LOGGER.error("获取法币价格异常", e);
        }
    }

    public static void main(String[] args) {
        SynForeignPriceWorker a = new SynForeignPriceWorker("", "");
        a.run();
    }

}
