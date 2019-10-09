package com.world.model.service;

import com.api.config.ApiConfig;
import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.world.config.GlobalConfig;
import com.world.data.database.DatabasesUtil;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.usercap.dao.CommAttrDao;
import com.world.model.entity.usercap.entity.SpecialAddress;
import com.world.util.request.HttpUtil;
import me.chanjar.weixin.common.util.StringUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 特殊地址推送
 * Created by xie on 2017/9/4.
 */
public enum SpecialAddressBuild {
    INSTANCE;
    protected static Logger log = Logger.getLogger(RechargeParamBuild.class);

    private static volatile Boolean isDown = Boolean.FALSE;

    synchronized public void threadStart(){

        String className = SpecialAddressBuild.class.getSimpleName();
        String open = GlobalConfig.getValue(className);

        Boolean isOpen = Boolean.valueOf(open);
        if(isOpen){

            new Thread(new Runnable() {

                @Override
                public void run() {

                    while (!isDown) {
                        try {

                            boolean result = pushSpecialAddress();

                            if(result){

                                isDown = Boolean.TRUE;
                            }else{

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

    public boolean pushSpecialAddress(){

        boolean result = Boolean.FALSE;
        List<SpecialAddress> specialAddressList = new CommAttrDao().querySpecialAddressByAttrType(10000004);//获取冷到其他地址\

        List<Map<String,String>> list = new ArrayList<>();
        Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
        for(Map.Entry<String, CoinProps> entry : coinMap.entrySet()){
            Map<String, String> tmpAddress = new HashedMap();
            String coinType = entry.getKey();
            String addressStr = "";
            for(SpecialAddress specialAddress : specialAddressList){
                    if(specialAddress.getCoinType().equalsIgnoreCase(coinType) && org.apache.commons.lang.StringUtils.isNotBlank(specialAddress.getAddress())){
                    addressStr += specialAddress.getAddress() + ",";
                }
            }

            if(addressStr.length() > 0){
                tmpAddress.put("coinType",coinType.toUpperCase());
                tmpAddress.put("address",addressStr.substring(0,addressStr.length()-1));
                list.add(tmpAddress);
            }

        }

        if(null != list && list.size() > 0){
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonStr = null;
            try {
                jsonStr = objectMapper.writeValueAsString(list);
                String ctype = "application/json;charset=UTF-8";	//类型
                String url = ApiConfig.getInstance().getValue("tradingcenter.url") + "/openapi/tradingcenter/coldToOtherAddress/init";//请求地址,暂时
                String json = HttpUtil.doPostv2(url, ctype, jsonStr, 3000, 5000);
                if(StringUtils.isNotBlank(json)){
                    result = Boolean.TRUE;
                }
            } catch (Exception e) {
                log.error("format error.", e);
            }
        }
        return result;
    }
}
