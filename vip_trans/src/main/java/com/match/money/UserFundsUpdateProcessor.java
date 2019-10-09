package com.match.money;

import com.world.model.daos.world.FundsUserDao;
import com.world.model.service.BrushAccountService;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>@Description: 用户资金更新定时任务 500毫秒执行一次，用户去重，减少重复执行次数</p>
 *
 * @author buxianguan
 * @date 2018/11/17 4:00 PM
 */
public class UserFundsUpdateProcessor {
    public static Logger log = Logger.getLogger(UserFundsUpdateProcessor.class);

    private static FundsUserDao fundsUserDao = new FundsUserDao();

    private static BrushAccountService brushAccountService = new BrushAccountService();

    /**
     * 用户去重map
     */
    private static ConcurrentHashMap<Integer, Integer> userIdMap = new ConcurrentHashMap<>();

    private static ExecutorService pool = Executors.newFixedThreadPool(50);

    public static int userFundsUpdateSize() {
        return ((ThreadPoolExecutor)pool).getQueue().size();
    }

    private static void asyncUpdate(int userId) {
        pool.execute(() -> {
            fundsUserDao.updateFundsByChange(userId);
            userIdMap.remove(userId);
        });
    }

    public static void add(int userId) {
        //刷量账号直接删除缓存
        if (brushAccountService.isBrushAccountCache(String.valueOf(userId))) {
            clearUser(userId);
        } else {
            if (null == userIdMap.putIfAbsent(userId, 1)) {
                asyncUpdate(userId);
            }
        }
    }

    public static void clearUser(int userId) {
        fundsUserDao.clearUserFundsCache(userId);
    }
}
