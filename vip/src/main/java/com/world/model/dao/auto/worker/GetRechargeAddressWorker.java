package com.world.model.dao.auto.worker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.google.common.collect.Maps;
import com.world.cache.Cache;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.pay.KeyDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.util.request.HttpUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.lang.Thread.sleep;

/**
 *  @Param
 *  @Return 
 *  @Description  获取用户充值地址
 **/
@SuppressWarnings("serial")
public class GetRechargeAddressWorker extends Worker {

    public GetRechargeAddressWorker(String name, String des) {
        super(name, des);
    }
    KeyDao keyDao = new KeyDao();

    private static final int SYNC_SIZE = 100;//同步的数据条数
    //地址标签序列号
    private static final String ADDRESS_TAG_SEQUENCE = "address_tag_sequence_";

    @Override
    public void run() {
        super.run();
        log.info("从GAIA平台获取新地址");
        try {
            Map<String, CoinProps> coinMap = DatabasesUtil.getNewCoinPropMaps();
            for (Entry<String, CoinProps> entry : coinMap.entrySet()) {
                CoinProps coin = entry.getValue();
                //是 ERC2.0币种 就不查对应的数据库 跳出此次循环 开始下一次循环
                if (coin.isERC()) {
                    continue;
                }
                getAddress(coin, SYNC_SIZE);

            }
        } catch (Exception e) {
            log.error("从GAIA平台获取新地址出错：", e);
        }
    }

    public void getAddress(CoinProps coint, int size) {
        keyDao.setCoint(coint);
        long count = keyDao.getNoUseCount();
        if (count >= 10000) {
            log.info(coint.getPropTag() + "地址还充足，不用重新生成。");
            return;
        }
        String currency = coint.getStag();
        try {
            Map<String, String> param = Maps.newHashMap();
            param.put("platform_channel", "bitglobal");
            param.put("coin_type", currency);
            param.put("limit", String.valueOf(size));
            String url = ApiConfig.getValue("tradingcenter.url") + "/openapi/tradingcenter/rechargeaddress";
            log.info("支付中心请求链接：" + url);
            String bodyJson = HttpUtil.doGetv2(url, param, 30000, 30000);
            JSONObject json = JSONObject.parseObject(bodyJson);
            String tableName = currency + "Key";
            int status = json.getInteger("status");
            List<OneSql> sqls = new ArrayList<OneSql>();
            JSONArray dataArray = json.getJSONArray("data");
            String addressTag = "";
            if (status == 0 && dataArray.size() > 0) {

                /*start by kinghao 20190110 eos新币:*/
                /*** 验证如果为EOS，则将获取到的数据循环插入300条**/
                if ("eos".equalsIgnoreCase(currency)) {
                    this.saveEosAddress(dataArray, tableName, addressTag);
                    log.info("EOS充值地址获取结束");
                    return;
                }
                /*end*/
                for (int i = 0; i < dataArray.size(); i++) {
                    long now = System.currentTimeMillis();
                    JSONObject o = dataArray.getJSONObject(i);
                    String address = o.getString("recharge_address");
                    String wallet = o.getString("wallet");
                    String countSql = "select count(*) from " + tableName + " where keyPre=?";
                    List<Long> one = (List<Long>) Data.GetOne(countSql, new Object[]{address});
                    if (one != null && one.size() > 0 && one.get(0) > 0) {
                        continue;
                    }
                    sqls.add(new OneSql("insert into " + tableName + "(keyPre,wallet,createTime,merchantOrderNo,addressTag) values (?,?,now(),?,?)", 1,
                            new Object[]{address, wallet, now, addressTag}));// TODO wallet 和 merchantOrderNo 字段需要看一下做什么用
                    log.info("[第" + i + "/" + dataArray.size() + "]从GAIA平台获取" + coint.getPropTag() + "新地址:" + address);
                }
            }


            if (sqls.size() > 0) {
                Data.doTrans(sqls);
            }

        } catch (Exception e) {
            log.info("充值地址获取失败", e);
            try {
                sleep(10000);
            } catch (InterruptedException e1) {
                log.info("调用失败，休息10秒", e);
            }
        }

    }

    private String getAddressTag(String tableName) {
        //地址标签
        String addressTag = "";
        //地址序列缓存key
        String sequenceKey = ADDRESS_TAG_SEQUENCE.concat("eoskey");
        addressTag = Cache.Get(sequenceKey);
        if (StringUtils.isEmpty(addressTag)) {
            String countSql = "select count(1) from " + tableName;
            List countList = (List) Data.GetOne(countSql, new Object[]{});
            if ("0".equals(countList.get(0).toString())) {
                addressTag = "100000";
            } else {
                String addressTagMaxSql = "select max(addressTag) from " + tableName;
                List maxList = (List) Data.GetOne(addressTagMaxSql, new Object[]{});
                addressTag = String.valueOf(Integer.valueOf(maxList.get(0).toString()) + 1);
            }
        } else {
            addressTag = String.valueOf(Integer.valueOf(addressTag) + 1);
        }
        Cache.Set(sequenceKey, addressTag);
        return addressTag;
    }


    /*start by kinghao 20190110 eos新币:*/
    public void saveEosAddress(JSONArray dataArray, String tableName, String addressTag) {
        List<OneSql> sqlsEOS = new ArrayList<>();
        JSONObject o = dataArray.getJSONObject(0);

        for (int i = 0; i < 10000; i++) {
            long now = System.currentTimeMillis();
            String address = o.getString("recharge_address");
            String wallet = o.getString("wallet");
            addressTag = getAddressTag(tableName);
            /*Start by guankaili 20190108 为eos添加地址标签 */

            sqlsEOS.add(new OneSql("insert into " + tableName + "(keyPre,wallet,createTime,merchantOrderNo,addressTag) values (?,?,now(),?,?)", 1,
                    new Object[]{address, wallet, now, addressTag}));// TODO wallet 和 merchantOrderNo 字段需要看一下做什么用
        }
        if (sqlsEOS.size() > 0) {
            Data.doTrans(sqlsEOS);
        }
    }
    /*end*/

    public static void main(String[] args) {
        log.info("从GAIA平台获取新地址");
        try {
            Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
            for (Entry<String, CoinProps> entry : coinMap.entrySet()) {
                CoinProps coin = entry.getValue();
                //是 ERC2.0币种 就不查对应的数据库 跳出此次循环 开始下一次循环
                if (coin.isERC() || !coin.getDatabaseKey().equals("qtum")) {
                    continue;
                }
                GetRechargeAddressWorker worker = new GetRechargeAddressWorker("", "");
                worker.getAddress(coin, SYNC_SIZE);
            }
        } catch (Exception e) {
            log.error("从GAIA平台获取新地址出错：", e);
        }
    }
}
