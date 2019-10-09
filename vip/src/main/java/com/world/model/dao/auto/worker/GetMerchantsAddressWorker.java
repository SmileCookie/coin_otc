package com.world.model.dao.auto.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.pay.KeyDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.util.MerchantsUtil;

@SuppressWarnings("serial")
public class GetMerchantsAddressWorker extends Worker{

    public GetMerchantsAddressWorker(String name, String des) {
        super(name, des);
    }
    KeyDao keyDao = new KeyDao();

    @Override
    public void run() {
        super.run();
        log.info("从商户平台获取新地址");
        try {
            Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
            for(Entry<String, CoinProps> entry : coinMap.entrySet()){
                CoinProps coin = entry.getValue();
                if(coin.isCoin()){
                    getAddress(coin, 50);
                }
            }
        } catch (Exception e) {
            log.error("从商户平台获取新地址出错：", e);
        }
    }

    public void getAddress(CoinProps coint, int size) {
        keyDao.setCoint(coint);
        long count = keyDao.getNoUseCount();
        if (count >= size) {
            log.info(coint.getPropTag()+"地址还充足，不用重新生成。");
            return;
        }

        String currency = coint.getStag();
        List<OneSql> sqls = new ArrayList<OneSql>();
        JSONObject json = MerchantsUtil.getNewAddress(currency, size);
        String tableName = currency + "Key";
        if (json.containsKey("data")) {
            JSONObject data = json.getJSONObject("data");
            if (data.containsKey("address")) {
                JSONArray addressList = data.getJSONArray("address");
                String orderNo = data.getString("orderNo");
                if (null != addressList && addressList.size() > 0) {
                    for (int i=0; i< addressList.size(); i++) {
                        JSONObject aObj = addressList.getJSONObject(i);
                        if (aObj.containsKey("address") && aObj.containsKey("wallet")) {
                            String address = aObj.getString("address");

                            String countSql = "select count(*) from " + tableName + " where keyPre=?";
                            List<Long> one = (List<Long>)Data.GetOne(countSql, new Object[]{address});
                            if(one !=null && one.size() > 0 && one.get(0) > 0){
                                continue;
                            }

                            sqls.add(new OneSql("insert into "+tableName+"(keyPre,wallet,createTime,merchantOrderNo) values (?,?,now(),?)", 1,
                                    new Object[]{address, aObj.getString("wallet"), orderNo}));
                        }
                    }
                }
            } else if (data.containsKey("keySize")) {
                int keySize = data.getIntValue("keySize");
                if (keySize > 0) {
                    getAddress(coint, keySize);
                }
                return;
            }
        }

        if (sqls.size() > 0) {
            Data.doTrans(sqls);
        }
    }
}
