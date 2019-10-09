package com.match.entrust;

import com.world.model.Market;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/6/15上午9:11
 */
public class MemEntrustSwitchService {
    private final static Logger logger = LoggerFactory.getLogger(MemEntrustSwitchService.class);

    private static Map<String, Boolean> MATCH_SWITCH_FLAG = new HashMap<>();

    static {
        for (Map.Entry<String, Market> marketEntry : Market.markets.entrySet()) {
            Market market = marketEntry.getValue();
            if (market.listenerOpen) {
                MATCH_SWITCH_FLAG.put(market.market, true);
            }
        }
    }

    public static void openMatchSwitch(String market) {
        MATCH_SWITCH_FLAG.put(market, true);
    }

    public static void closeMatchSwitch(String market) {
        MATCH_SWITCH_FLAG.put(market, false);
    }

    public static boolean getMatchSwitch(String market) {
        return MATCH_SWITCH_FLAG.get(market);
    }
}
