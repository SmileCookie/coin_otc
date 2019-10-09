package com.world.model.service;

import com.api.config.ApiConfig;
import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.world.config.GlobalConfig;
import com.world.data.database.DatabasesUtil;
import com.world.model.entity.coin.CoinProps;
import com.world.util.request.HttpUtil;
import me.chanjar.weixin.common.util.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * 充值服务推送相关参数
 * <p>
 * Created by renfei on 17/4/17.
 */
public enum RechargeParamBuild {
    INSTANCE;

    protected static Logger log = Logger.getLogger(RechargeParamBuild.class);

    private static volatile Boolean isDown = Boolean.FALSE;


    synchronized public void threadStart() {

        String className = RechargeParamBuild.class.getSimpleName();
        String open = GlobalConfig.getValue(className);

        Boolean isOpen = Boolean.valueOf(open);

        log.info("[推送币种配置] 推送配置是否开启：" + isOpen);
        if (isOpen) {

            new Thread(new Runnable() {
                private boolean pushRechargeParamToTradingCenter() {

                    boolean result = Boolean.FALSE;

                    List<Map<String, Object>> paramList = Lists.newArrayList();

                    Map<String, CoinProps> coinMap = DatabasesUtil.getNewCoinPropMaps();
                    for (Map.Entry<String, CoinProps> entry : coinMap.entrySet()) {
                        CoinProps coin = entry.getValue();

                        Map<String, Object> m = Maps.newLinkedHashMap();
                        m.put("coinType", coin.getPropTag());
                        m.put("confirm", coin.getInConfirmTimes());

                        m.put("dayCash", coin.getDayCash());
                        m.put("timesCash", coin.getTimesCash());
                        m.put("minFees", coin.getMinFees());
                        m.put("outConfirm", coin.getOutConfirmTimes());
                        m.put("minCash", coin.getMinCash());

                        paramList.add(m);
                    }

                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonStr = null;

                    try {
                        log.info("[推送币种配置] 开始推送---");
                        jsonStr = objectMapper.writeValueAsString(paramList);
                        log.info("[推送币种配置] 开始推送---："+jsonStr);

                        String ctype = "application/json;charset=UTF-8";    //类型
                        String url = ApiConfig.getInstance().getValue("tradingcenter.url") + "/openapi/tradingcenter/withdrawal/recharge/param/init";//请求地址,暂时
                        String json = HttpUtil.doPostv2(url, ctype, jsonStr, 3000, 5000);
                        log.info("[推送币种配置] 推送结束---" + json);
                        if (StringUtils.isNotBlank(json)) {
                            result = Boolean.TRUE;
                        }

                    } catch (Exception e) {
                        log.error("format error.", e);
                    }


                    return result;


                }

                @Override
                public void run() {

                    while (!isDown) {
                        try {

                            boolean result = pushRechargeParamToTradingCenter();

                            if (result) {

                                isDown = Boolean.TRUE;
                            } else {

                                Thread.sleep(DateUtils.MILLIS_PER_SECOND);
                            }


                        } catch (InterruptedException e) {
                            log.error("thread sleep error.", e);
                        }
                    }
                }
            }).start();
        }

    }

}
