package com.world.controller.backcapital;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.model.backcapital.constant.BackCapitalConst;
import com.world.model.backcapital.dao.EntrustRecordDao;
import com.world.model.backcapital.service.BackCapitalService;
import com.world.model.dao.trace.EntrustDao;
import com.world.model.entity.backcapital.BackCapitalConfig;
import com.world.model.entity.backcapital.BcEntrustTransRecord;
import com.world.model.entity.record.TransRecord;
import com.world.model.entity.trace.Entrust;
import com.world.model.quanttrade.dao.TransRecordDao;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by buxianguan on 17/8/18.
 */
public class Index extends BaseAction {
    private final static Logger log = Logger.getLogger(Index.class);

    private EntrustDao entrustDao = new EntrustDao();
    private EntrustRecordDao entrustRecordDao = new EntrustRecordDao();
    private TransRecordDao transRecordDao = new TransRecordDao();
    private BackCapitalService backCapitalService = new BackCapitalService();

    /**
     * 获取倒计时
     */
    @Page(Viewer = JSON)
    public void countDown() {
        try {
            JSONObject result = new JSONObject();

            BackCapitalConfig config = backCapitalService.getConfig();
            if (null == config) {
                json("出现异常", false, "", true);
                return;
            }

            //回购频率
            int frequency = config.getBcFrequency();
            result.put("frequency", frequency);

            //倒计时
            long countDown = backCapitalService.getCountDown(frequency, BackCapitalConst.BACK_CAPITAL_TIME_CACHE_KEY);
            result.put("countDown", countDown);

            //20条委托资金
            BigDecimal[] capitals = get20Capitals();
            result.put("capitals", capitals);

            //最近一次回购资金
            BigDecimal lastBackCapital = backCapitalService.getLastBackCapital(String.valueOf(config.getBcUserId()));
            result.put("lastBackCapital", lastBackCapital);

            //委托记录
            long lastEntrustId = longParam("lastEntrustId");
            JSONArray entrusts = new JSONArray();
            //没有传过来上次最大委托id，则不获取委托记录，适用于第一次打开页面请求接口
            if (lastEntrustId > 0) {
                entrusts = getEntrustsLessId(lastEntrustId);
            }
            result.put("entrusts", entrusts);

            json("success", true, result.toJSONString(), true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    /**
     * 获取回购委托记录，翻页
     */
    @Page(Viewer = JSON)
    public void getEntrusts() {
        try {
            JSONObject result = new JSONObject();

            BackCapitalConfig config = backCapitalService.getConfig();
            if (null == config) {
                json("系统异常", false, "", true);
                return;
            }
            //回购账户
            String bcUserId = String.valueOf(config.getBcUserId());

            //委托记录
            JSONArray entrusts = new JSONArray();
            long lastEntrustId = longParam("lastEntrustId");
            int pageSize = intParam("pageSize");
            if (pageSize == 0) {
                pageSize = 20;
            }
            result.put("hasMore", 0);
            if (lastEntrustId <= 0) {
                List<Entrust> entrustCache = backCapitalService.getEntrustsFromCacheOrder();
                if (CollectionUtils.isEmpty(entrustCache)) {
                    entrustCache = entrustDao.getEntrustListByUser(bcUserId, 1, pageSize + 1, BackCapitalConst.BACK_CAPITAL_DB_NAME);
                }
                if (entrustCache.size() > pageSize) {
                    result.put("hasMore", 1);
                }
                int i = 0;
                for (Entrust entrust : entrustCache) {
                    if (i < pageSize) {
                        entrusts.add(entrustToJson(entrust));
                        i++;
                    } else {
                        break;
                    }
                }
            } else {
                //获取小于lastEntrustId的记录
                List<Entrust> allEntrusts = new ArrayList<>();
                //多读取一条，用于显示是否还有下一页
                List<Entrust> entrustList = entrustDao.getEntrustListByUserAndId(bcUserId, pageSize + 1, lastEntrustId, BackCapitalConst.BACK_CAPITAL_DB_NAME);
                allEntrusts.addAll(entrustList);
                if (entrustList.size() < pageSize) {
                    int newPageSize = pageSize - entrustList.size();
                    List<Entrust> entrustAllList = entrustDao.getEntrustAllListByUserAndId(bcUserId, lastEntrustId, newPageSize + 1, BackCapitalConst.BACK_CAPITAL_DB_NAME);
                    allEntrusts.addAll(entrustAllList);
                }
                if (allEntrusts.size() > pageSize) {
                    result.put("hasMore", 1);
                }
                int i = 0;
                for (Entrust entrust : allEntrusts) {
                    if (i < pageSize) {
                        entrusts.add(entrustToJson(entrust));
                        i++;
                    } else {
                        break;
                    }
                }
            }
            result.put("entrusts", entrusts);

            json("success", true, result.toJSONString(), true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    /**
     * 获取与我相关回购委托记录，翻页
     */
    @Page(Viewer = JSON)
    public void getEntrustsRelatedMe() {
        try {
            if (!IsLogin()) {
                json("请登陆后再操作", false, "", true);
                return;
            }

            int userId = userId();
            JSONObject result = new JSONObject();

            //委托记录
            JSONArray entrusts = new JSONArray();
            long lastEntrustId = longParam("lastEntrustId");
            int pageSize = intParam("pageSize");
            if (pageSize == 0) {
                pageSize = 20;
            }
            result.put("hasMore", 0);
            if (lastEntrustId <= 0) {
                String cacheKey = String.format(BackCapitalConst.BACK_CAPITAL_USER_ENTRUST_CACHE_KEY, userId);
                List<BcEntrustTransRecord> userEntrustCache = (List<BcEntrustTransRecord>) Cache.GetObj(cacheKey);
                if (CollectionUtils.isEmpty(userEntrustCache)) {
                    userEntrustCache = entrustRecordDao.getUserEntrusts(userId, pageSize + 1);
                    Cache.SetObj(cacheKey, userEntrustCache, 2 * 60 * 60);
                }
                if (userEntrustCache.size() > pageSize) {
                    result.put("hasMore", 1);
                }
                int i = 0;
                for (BcEntrustTransRecord entrustRecord : userEntrustCache) {
                    if (i < pageSize) {
                        Entrust entrustFromMap = backCapitalService.getEntrustFromCache(entrustRecord.getEntrustId());
                        if (null != entrustFromMap) {
                            entrustRecord.setCompleteTotalMoney(entrustFromMap.getCompleteTotalMoney());
                            entrustRecord.setCompleteNumber(entrustFromMap.getCompleteNumber());
                        }
                        entrusts.add(entrustRecordToJson(entrustRecord));
                        i++;
                    } else {
                        break;
                    }
                }
            } else {
                //获取小于lastEntrustId的记录
                List<BcEntrustTransRecord> entrustRecords = entrustRecordDao.getUserEntrustsByEntrustId(userId, lastEntrustId, pageSize + 1);
                if (entrustRecords.size() > pageSize) {
                    result.put("hasMore", 1);
                }
                int i = 0;
                for (BcEntrustTransRecord entrustRecord : entrustRecords) {
                    if (i < pageSize) {
                        entrusts.add(entrustRecordToJson(entrustRecord));
                        i++;
                    } else {
                        break;
                    }
                }
            }

            result.put("entrusts", entrusts);

            json("success", true, result.toJSONString(), true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    /**
     * 获取回购委托详情
     */
    @Page(Viewer = JSON)
    public void getEntrustById() {
        try {
            JSONObject result = new JSONObject();

            long entrustId = longParam("entrustId");
            if (entrustId < 0) {
                json("参数错误", false, "", true);
                return;
            }

            result.put("entrustId", entrustId);

            Entrust entrust = entrustDao.getEntrustById(entrustId, BackCapitalConst.BACK_CAPITAL_DB_NAME);
            if (null == entrust) {
                entrust = entrustDao.getEntrustAllById(entrustId, BackCapitalConst.BACK_CAPITAL_DB_NAME);
            }
            if (null == entrust) {
                json("委托不存在", false, "", true);
                return;
            }

            result.put("date", entrust.getSubmitTime());
            result.put("totalMoney", entrust.getCompleteTotalMoney());
            result.put("amount", entrust.getCompleteNumber());

            //获取成交记录
            JSONArray records = new JSONArray();
            List<TransRecord> transRecords = transRecordDao.getTransRecordByEntrust(entrustId, BackCapitalConst.BACK_CAPITAL_DB_NAME);
            for (TransRecord transRecord : transRecords) {
                JSONObject json = new JSONObject();
                json.put("date", transRecord.getTimes());
                json.put("price", transRecord.getUnitPrice());
                json.put("amount", transRecord.getNumbers());
                json.put("totalMoney", transRecord.getTotalPrice());
                records.add(json);
            }
            result.put("transRecords", records);

            //获取回购资金构成
            JSONArray capitals = new JSONArray();
            List<BcEntrustTransRecord> entrustRecords = entrustRecordDao.getEntrustRecordsByEntrustId(entrustId);
            for (BcEntrustTransRecord entrustRecord : entrustRecords) {
                JSONObject json = new JSONObject();
                json.put("date", entrustRecord.getTransRecordTime());
                json.put("ratio", entrustRecord.getFeeRatio());
                json.put("transRecordId", entrustRecord.getTransRecordId());
                json.put("market", entrustRecord.getMarket());
                json.put("fee", entrustRecord.getAmount());
                int userId = userId();
                if (userId > 0 && entrustRecord.getUserId() == userId) {
                    json.put("relatedMe", 1);
                } else {
                    json.put("relatedMe", 0);
                }
                capitals.add(json);
            }
            result.put("capitals", capitals);

            json("success", true, result.toJSONString(), true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    private BigDecimal[] get20Capitals() {
        BigDecimal[] capitals = new BigDecimal[20];
        List<BcEntrustTransRecord> entrustRecords = (List<BcEntrustTransRecord>) Cache.GetObj(BackCapitalConst.BACK_CAPITAL_TWENTY_CAPITAL_CACHE_KEY);
        if (CollectionUtils.isEmpty(entrustRecords)) {
            entrustRecords = entrustRecordDao.getEntrustPrice();
        }
        int i = 0;
        for (BcEntrustTransRecord entrustRecord : entrustRecords) {
            if (i < 20) {
                if (entrustRecord.getEntrustId() > 0) {
                    Entrust entrust = backCapitalService.getEntrustFromCache(entrustRecord.getEntrustId());
                    if (null != entrust) {
                        capitals[i] = entrust.getCompleteTotalMoney();
                    } else {
                        capitals[i] = entrustRecord.getCompleteTotalMoney();
                    }
                } else {
                    capitals[i] = entrustRecord.getCompleteTotalMoney();
                }
                i++;
            }
        }
        for (; i < capitals.length; i++) {
            capitals[i] = BigDecimal.ZERO;
        }
        return capitals;
    }

    private JSONArray getEntrustsLessId(long lastEntrustId) {
        JSONArray entrusts = new JSONArray();

        List<Entrust> entrustCache = backCapitalService.getEntrustsFromCacheOrder();
        if (CollectionUtils.isNotEmpty(entrustCache)) {
            //获取当前登录用户相关委托
            int userId = userId();
            Set<Long> userEntrustIds = new HashSet<>();
            if (userId > 0) {
                String cacheKey = String.format(BackCapitalConst.BACK_CAPITAL_USER_ENTRUST_CACHE_KEY, userId);
                List<BcEntrustTransRecord> userEntrustCache = (List<BcEntrustTransRecord>) Cache.GetObj(cacheKey);
                if (CollectionUtils.isNotEmpty(userEntrustCache)) {
                    for (BcEntrustTransRecord entrustRecord : userEntrustCache) {
                        userEntrustIds.add(entrustRecord.getEntrustId());
                    }
                }
            }

            for (Entrust entrust : entrustCache) {
                if (entrust.getEntrustId() > lastEntrustId) {
                    JSONObject json = entrustToJson(entrust);
                    //是否与我相关
                    if (userEntrustIds.contains(entrust.getEntrustId())) {
                        //用大于0的数值表示与我相关，如果以后要显示个人占比，沿用此字段即可
                        json.put("ratio", 1);
                    } else {
                        json.put("ratio", 0);
                    }
                    entrusts.add(json);
                }
            }
        }
        return entrusts;
    }

    private JSONObject entrustToJson(Entrust entrust) {
        JSONObject json = new JSONObject();
        json.put("entrustId", entrust.getEntrustId());
        json.put("date", entrust.getSubmitTime());
        json.put("totalMoney", entrust.getCompleteTotalMoney());
        json.put("amount", entrust.getCompleteNumber());
        return json;
    }

    private JSONObject entrustRecordToJson(BcEntrustTransRecord entrustRecord) {
        JSONObject json = new JSONObject();
        json.put("entrustId", entrustRecord.getEntrustId());
        json.put("date", entrustRecord.getEntrustTime());
        json.put("totalMoney", entrustRecord.getCompleteTotalMoney());
        json.put("amount", entrustRecord.getCompleteNumber());
        return json;
    }
}